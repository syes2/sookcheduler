package com.example.sookcheduler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.ClickListener {

    GridAdapter gridAdapter;
    ArrayList<String> dayList;
    GridView gridView;
    Calendar calendar;
    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    String id,pw;
    ArrayList<String> Title = new ArrayList<>();
    ArrayList<String> Date = new ArrayList<>();
    ArrayList<Schedules> todoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView TitleMonth = (TextView) findViewById(R.id.textView);
        gridView = (GridView) findViewById(R.id.gv);

        /* 해당 월에 맞는 날짜 리스트  */
        long today = System.currentTimeMillis();
        final Date date = new Date(today);

        final SimpleDateFormat curYear = new SimpleDateFormat("yyyy", Locale.KOREA);
        final SimpleDateFormat curMonth = new SimpleDateFormat("MM", Locale.KOREA);

        TitleMonth.setText(curMonth.format(date) + "月");

        dayList = new ArrayList<String>();
        dayList.add(" 일 ");
        dayList.add(" 월 ");
        dayList.add(" 화 ");
        dayList.add(" 수 ");
        dayList.add(" 목 ");
        dayList.add(" 금 ");
        dayList.add(" 토 ");

        calendar = Calendar.getInstance();

        calendar.set(Integer.parseInt(curYear.format(date)), Integer.parseInt(curMonth.format(date)) - 1, 1);
        int dayNum = calendar.get(Calendar.DAY_OF_WEEK);
        for (int i = 0; i < dayNum - 1; i++) {
            dayList.add("");
        }
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
        for (int i = 0; i < calendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            dayList.add("" + (i + 1));
        }

        gridAdapter = new GridAdapter(getApplicationContext(), dayList);
        gridView.setAdapter(gridAdapter);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerViewAdapter = new RecyclerViewAdapter(this);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setAdapter(recyclerViewAdapter);

        Bundle extras = getIntent().getExtras();
        id = extras.getString("id");
        pw = extras.getString("pw");

        todoList.clear();
        JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
        jsoupAsyncTask.execute();

        Button addbtn = (Button) findViewById(R.id.addbtn);
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddScheduleActivity.class);
                startActivityForResult(intent,1);
            }
        });
    }

    /* 일정을 추가하고 메인 화면으로 돌아왔을 때 */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            if(resultCode == Activity.RESULT_OK){
                Schedules schedules = new Schedules();
                schedules.date = (String)data.getExtras().getString("date");
                schedules.title = (String)data.getExtras().getString("title");
                schedules.content = (String)data.getExtras().getString("contents");
                todoList.add(schedules);
            }
        }
    }

    /* 달력 GridView */
    private class GridAdapter extends BaseAdapter {
        private final List<String> list;
        private final LayoutInflater inflater;

        private GridAdapter(Context context, ArrayList<String> list) {
            this.list = list;
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); }

        @Override
        public int getCount() { return list.size(); }

        @Override
        public Object getItem(int position) { return list.get(position); }

        @Override
        public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, final ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.calendar, parent, false);
                holder = new ViewHolder();
                holder.DayGridView = (TextView) convertView.findViewById(R.id.cell);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.DayGridView.setText("" + getItem(position));

            calendar = Calendar.getInstance();
            final int pos = position;
            convertView.setOnClickListener(new View.OnClickListener() {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1;
                String dday = "";

                @Override
                public void onClick(View v) {
                    int length = getItem(pos).toString().length();
                    if (length == 1)
                        dday = "0" + getItem(pos);
                    else
                        dday = getItem(pos) + "";
                    String s = +year + "" + month + "" + dday;
                    recyclerViewAdapter.clear();
                    Schedules schedules = new Schedules();
                    schedules.date = s;
                    ArrayList<Schedules> list = dayselect(s,todoList);
                    for (int i = 0; i < list.size(); i++) {
                        recyclerViewAdapter.addItem(list.get(i));
                    }
                    Toast.makeText(MainActivity.this,dday,Toast.LENGTH_SHORT).show();
                    recyclerViewAdapter.notifyDataSetChanged();
                }
            });

            Integer today = calendar.get(Calendar.DAY_OF_MONTH);
            String sToday = String.valueOf(today);

            if (sToday.equals(getItem(position))) {
                holder.DayGridView.setTextColor(getResources().getColor(R.color.colorPrimary));
            }
            return convertView;
        }
    }

    private class ViewHolder {
        TextView DayGridView;
    }

    /* 일정 삭제 */
    public void delete(int position) {
        Schedules schedules = recyclerViewAdapter.getItem(position);
        for (int i=0;i<todoList.size();i++){
            if (todoList.get(i).date.equals(schedules.date)&&todoList.get(i).title.equals(schedules.title)){
                todoList.remove(i);
            }
        }
        recyclerViewAdapter.removeItem(position);
        recyclerViewAdapter.notifyDataSetChanged();
    }

    /* 일정 보기 */
    public ArrayList<Schedules> dayselect(String s, ArrayList<Schedules> schedulesArrayList) {  // 달력에서 날짜 클릭 시
        ArrayList<Schedules> list = new ArrayList<>();
        for (int i = 0; i < schedulesArrayList.size(); i++) {
            if (schedulesArrayList.get(i).date.equals(s)) {
                Schedules schedules1 = new Schedules();
                schedules1.date = schedulesArrayList.get(i).date;
                schedules1.title = schedulesArrayList.get(i).title;
                schedules1.content = schedulesArrayList.get(i).content;
                list.add(schedules1);
            }
        }
        return list;
    }

    /* 스노보드에서 수업 일정 크롤링 */
    public class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute(); }
        @Override
        protected Void doInBackground(Void... params) {
            try {

                Connection.Response loginFormResponse = Jsoup.connect("http://snowboard.sookmyung.ac.kr/")
                        .method(Connection.Method.POST)
                        .execute();

                FormElement loginForm = (FormElement)loginFormResponse.parse()  // 스노보드 로그인
                        .select("form").first();
                checkElement("Login Form", loginForm);

                Element loginField = loginForm.select("#input-username").first();
                checkElement("Login Field", loginField);
                loginField.val(id);

                Element passwordField = loginForm.select("#input-password").first();
                checkElement("Password Field", passwordField);
                passwordField.val(pw);

                Connection.Response loginActionResponse = loginForm.submit()
                        .cookies(loginFormResponse.cookies())
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36 Edg/86.0.622.69")
                        .execute();

                Map<String,String> cookies = loginActionResponse.cookies();

                Document doc = loginActionResponse.parse();
                Elements link = doc.select("a.course_link");
                for(Element a:link){
                    String url = a.attr("href");
                    Document mPage = Jsoup.connect(url).cookies(cookies).get();
                    Elements title = mPage.select(".total_sections").select(".activity.xncommons.modtype_xncommons .instancename");
                    Elements date = mPage.select(".total_sections").select(".text-ubstrap");

                    for(Element e:title){
                        String se = e.attr("title");
                        Title.add(se);
                    }
                    for(Element e:date){
                        String se = e.text();
                        String mdate = se.split("~")[1];
                        String year = mdate.split("-")[0];
                        String month = mdate.split("-")[1];
                        String day = mdate.split("-")[2];
                        String rdate = year+month+day;
                        Date.add(rdate.split("\\s")[1]);
                    }
                }
                for(int i=0;i<Title.size();i++){
                    Schedules schedules = new Schedules();
                    schedules.date = Date.get(i);
                    schedules.title = Title.get(i);
                    schedules.content = "수업 듣기";
                    todoList.add(schedules);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

        public void checkElement(String name, Element elem) {
            if (elem == null) {
                throw new RuntimeException("Unable to find " + name);
            }
        }
    }
}