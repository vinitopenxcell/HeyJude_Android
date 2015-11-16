package com.heyjude.androidapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.heyjude.androidapp.R;
import com.heyjude.androidapp.db.DBHelper;
import com.heyjude.androidapp.model.ListData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by aalap on 20/5/15.
 */
public class CurrentTaskAdapter extends BaseAdapter {


    private Context context;
    private LayoutInflater mInflater;
    ArrayList<ListData> taskLists;
    Date date;
    private DBHelper dbHelper;

    public CurrentTaskAdapter(Context context, ArrayList<ListData> list) {
        this.context = context;
        this.taskLists = list;
        if (context != null) {
            mInflater = LayoutInflater.from(context);
            dbHelper = DBHelper.getInstance(context);
        }
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
        TextView tvMonth, tvDate, tvTitle, tvStage, tvCounter;
    }

    @Override
    public View getView(final int position, View v, ViewGroup parent) {
        ViewHolder holder;

        if (v == null) {
            v = mInflater.inflate(R.layout.row_task, parent, false);

            holder = new ViewHolder();

            holder.tvMonth = (TextView) v.findViewById(R.id.tvMonth);
            holder.tvDate = (TextView) v.findViewById(R.id.tvDate);
            holder.tvTitle = (TextView) v.findViewById(R.id.tvTitle);
            holder.tvStage = (TextView) v.findViewById(R.id.tvStage);
            holder.tvCounter = (TextView) v.findViewById(R.id.tvCounter);

            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        int counter = Integer.parseInt(dbHelper.getUnReadCount(String.valueOf(taskLists.get(position).getTaskId()), "false"));

        if (counter > 0) {
            holder.tvCounter.setVisibility(View.VISIBLE);
            holder.tvCounter.setText(String.valueOf(counter));
        }

        // 2015-10-28 07:10:57
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
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
