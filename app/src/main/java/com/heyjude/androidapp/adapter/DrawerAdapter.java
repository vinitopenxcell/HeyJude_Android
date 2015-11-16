package com.heyjude.androidapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.heyjude.androidapp.R;
import com.heyjude.androidapp.model.DrawerOption;

import java.util.List;

/**
 * Created by Aalap on 24-04-2015.
 */
public class DrawerAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<DrawerOption> drawerOptionList;
    private Context context;

    public DrawerAdapter(Context context, List<DrawerOption> drawerOptionList) {
        this.context = context;
        inflater = inflater.from(context);
        this.drawerOptionList = drawerOptionList;
    }

    @Override
    public int getCount() {
        return drawerOptionList.size();
    }

    @Override
    public Object getItem(int position) {
        return drawerOptionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.row_sliding_drawer, parent, false);
            holder.tvOptionName = (TextView) convertView.findViewById(R.id.tvDrawerOptionName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        DrawerOption option = this.drawerOptionList.get(position);
        holder.tvOptionName.setText(option.getOptionName());

        holder.tvOptionName.setCompoundDrawablesWithIntrinsicBounds(null
                , null
                , context.getResources().getDrawable(option.getRightImageResId())
                , null);

        return convertView;
    }

    class ViewHolder {
        TextView tvOptionName;
    }
}
