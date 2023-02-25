package com.example.sookcheduler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    public interface ClickListener {
        void delete(int position);
    }

    @Override
    public int getItemCount() {
        return List.size();
    }

    public void addItem(Schedules item) {
        List.add(item);
    }

    public Schedules getItem(int position) {
        return List.get(position);
    }

    public void removeItem(int position) {
        List.remove(position);
    }

    public void clear() {
        List.clear();
        notifyDataSetChanged();
    }

    public RecyclerViewAdapter(ClickListener Listner) {
        listener = Listner;
    }

    private ClickListener listener;

    private ArrayList<Schedules> List=new ArrayList<>();

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title1;
        public TextView content1;
        public Button deletebtn1;

        public ViewHolder(View view) {
            super(view);

            title1 = (TextView)view.findViewById(R.id.title);
            content1 = (TextView)view.findViewById(R.id.content);
            deletebtn1 = (Button)view.findViewById(R.id.deletebtn); }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.todolist, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Schedules schedules = List.get(position);

        holder.title1.setText(schedules.title);
        holder.content1.setText(schedules.content);

        holder.deletebtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.delete(position);
                notifyDataSetChanged();
            }
        });
    }
}
