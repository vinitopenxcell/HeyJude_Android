package com.heyjude.androidapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.heyjude.androidapp.R;
import com.heyjude.androidapp.model.ListData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by dipen on 1/10/15.
 */
public class HistoryTaskAdapter extends BaseAdapter {


    private Context context;
    private LayoutInflater mInflater;
    ArrayList<ListData> taskLists = new ArrayList<>();
    Date date;

    public HistoryTaskAdapter(Context context, ArrayList<ListData> list) {
        this.context = context;
        this.taskLists = list;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return taskLists.size();
    }

    @Override
    public ListData getItem(int position) {
        return taskLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        TextView tvMonth, tvDate, tvTitle, tvStage;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHolder holder;

        if (v == null) {
            v = mInflater.inflate(R.layout.row_history, null);

            holder = new ViewHolder();

            holder.tvMonth = (TextView) v.findViewById(R.id.tvMonth);
            holder.tvDate = (TextView) v.findViewById(R.id.tvDate);
            holder.tvTitle = (TextView) v.findViewById(R.id.tvTitle);
            holder.tvStage = (TextView) v.findViewById(R.id.tvStage);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        try {
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            date = format.parse(taskLists.get(position).getTaskDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.tvTitle.setText(taskLists.get(position).getTaskTitle());
        holder.tvStage.setText("Stage: " + taskLists.get(position).getTaskStage());
        holder.tvDate.setText((String) android.text.format.DateFormat.format("dd", date).toString());
        holder.tvMonth.setText((String) android.text.format.DateFormat.format("MMM", date).toString());

        return v;
    }
}

