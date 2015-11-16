package com.heyjude.androidapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.heyjude.androidapp.R;
import com.heyjude.androidapp.model.ListData;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by dipen on 1/9/15.
 */
public class TwoForOneDiningAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ArrayList<ListData> restaurantLists;
    private ArrayList<ListData> orig;
    private Context mContext;

    public TwoForOneDiningAdapter(Context context, ArrayList<ListData> restaurant) {
        mContext = context;
        restaurantLists = restaurant;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return restaurantLists.size();
    }

    @Override
    public ListData getItem(int i) {
        return restaurantLists.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    public class ViewHolder {
        TextView tv_restName;
        ImageView iv_rest;
        ProgressBar progressBar;
    }


    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {

        final ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.row_restaurent, null);

            holder = new ViewHolder();

            holder.tv_restName = (TextView) convertView.findViewById(R.id.tv_restname);
            holder.iv_rest = (ImageView) convertView.findViewById(R.id.iv_restImage);
            holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar_rest);

            holder.progressBar.setVisibility(View.VISIBLE);

            holder.tv_restName.setSelected(true);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_restName.setText(restaurantLists.get(position).getPostTitle().trim());

        if (restaurantLists.get(position).getImage().isEmpty()) {
            Picasso.with(mContext).load(R.drawable.ic_restaurant).fit().into(holder.iv_rest);
            holder.progressBar.setVisibility(View.GONE);
        } else {
            Picasso.with(mContext).load(restaurantLists.get(position).getImage()).fit().into(holder.iv_rest, new Callback() {
                @Override
                public void onSuccess() {
                    holder.progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError() {
                    Picasso.with(mContext).load(R.drawable.ic_restaurant).fit().into(holder.iv_rest);
                    holder.progressBar.setVisibility(View.GONE);
                }
            });
        }
        return convertView;
    }
}
