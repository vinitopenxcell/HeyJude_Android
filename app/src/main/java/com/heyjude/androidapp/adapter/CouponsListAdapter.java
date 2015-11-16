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
import com.heyjude.androidapp.model.CouponCampaign;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by dipen on 27/6/15.
 */
public class CouponsListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    ArrayList<CouponCampaign> couponCampaignArrayList = new ArrayList<>();
    Context context;


    public CouponsListAdapter(Context latestFragment, ArrayList<CouponCampaign> couponCampaignArrayList) {
        this.couponCampaignArrayList = couponCampaignArrayList;
        mInflater = LayoutInflater.from(latestFragment);
        context = latestFragment;
    }

    @Override
    public int getCount() {
        return couponCampaignArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return couponCampaignArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    static class ViewHolder {
        private TextView tv_header, tv_tagline, tv_distance;
        private ImageView iv_logo;
        private ProgressBar progressBar;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.row_list_coupons, null);

            holder = new ViewHolder();

            holder.tv_header = (TextView) convertView.findViewById(R.id.tv_list_header);
            holder.tv_tagline = (TextView) convertView.findViewById(R.id.tv_list_tagline);
            holder.tv_distance = (TextView) convertView.findViewById(R.id.tv_list_distance);
            holder.iv_logo = (ImageView) convertView.findViewById(R.id.iv_logo);
            holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_header.setText(couponCampaignArrayList.get(position).getName());
        holder.tv_tagline.setText(couponCampaignArrayList.get(position).getDescription());
        holder.tv_distance.setText(couponCampaignArrayList.get(position).getDistance().toString() + " Km");

        Picasso.with(context).load(couponCampaignArrayList.get(position).getImageUrl()).into(holder.iv_logo);
        holder.progressBar.setVisibility(View.GONE);

        return convertView;
    }
}
