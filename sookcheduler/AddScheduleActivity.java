package com.example.sookcheduler;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AddScheduleActivity extends AppCompatActivity {
    int year1 = 0;
    int month1 = 0;
    int day1 = 0;
    String sday;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_schedule);

        final EditText date1 = (EditText) findViewById(R.id.date);
        final EditText title1 = (EditText) findViewById(R.id.title);
        final EditText content1 = (EditText) findViewById(R.id.content);

        date1.setOnClickListener(new View.OnClickListener() {   // 일정 날짜 선택 Datepicker
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                year1 = calendar.get(Calendar.YEAR);
                month1 = calendar.get(Calendar.MONTH);
                day1 = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddScheduleActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                month++;
                                year1 = year;
                                month1 = month;
                                day1 = day;
                                date1.setText(year + "년 " + month + "월 " + day + "일");
                            }
                        }, year1, month1, day1);
                datePickerDialog.show();
            }
        });

        Button finish = (Button)findViewById(R.id.finishbtn);
        finish.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if ((year1 ==0||month1==0||day1==0)){
                    Toast.makeText(AddScheduleActivity.this, "날짜를 선택해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                String title = title1.getEditableText().toString();
                String contents = content1.getEditableText().toString();
                sday = day1+"";
                int length = sday.length();
                if (length == 1)
                    sday = "0"+sday;
                else
                    sday = sday+"";
                String date = year1+""+month1+""+sday;
                Intent intent = new Intent(AddScheduleActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("date",date);
                bundle.putString("title",title);
                bundle.putString("contents",contents);
                intent.putExtras(bundle);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }
}
