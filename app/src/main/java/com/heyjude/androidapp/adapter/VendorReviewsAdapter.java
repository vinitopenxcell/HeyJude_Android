package com.heyjude.androidapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.heyjude.androidapp.R;
import com.heyjude.androidapp.model.ListData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by dipen on 28/9/15.
 */
public class VendorReviewsAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    ArrayList<ListData> reviewsList = new ArrayList<>();
    private Context mContext;


    public VendorReviewsAdapter(Context context, ArrayList<ListData> reviewList) {
        mContext = context;
        reviewsList = reviewList;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return reviewsList.size();
    }

    @Override
    public Object getItem(int i) {
        return reviewsList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    static class ViewHolder {
        RatingBar vendor_rating;
        TextView vendor_comment, tv_user_name, tv_vendor_city;
        ImageView iv_user;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.row_vendor_reviews, null);


            holder = new ViewHolder();

            holder.vendor_rating = (RatingBar) convertView.findViewById(R.id.vendor_rating);
            holder.vendor_comment = (TextView) convertView.findViewById(R.id.vendor_comment);
            holder.tv_user_name = (TextView) convertView.findViewById(R.id.tv_user_name);
            holder.tv_vendor_city = (TextView) convertView.findViewById(R.id.tv_vendor_city);
            holder.iv_user = (ImageView) convertView.findViewById(R.id.iv_user);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.vendor_rating.setRating(Float.parseFloat(reviewsList.get(position).getReviewStars()));
        holder.vendor_comment.setText(reviewsList.get(position).getReviewComment());
        holder.tv_user_name.setText(reviewsList.get(position).getUserProfileName());
        holder.tv_vendor_city.setText(reviewsList.get(position).getUserCity());

        if (!reviewsList.get(position).getUserProfileImage().isEmpty()) {
            Picasso.with(mContext).load(reviewsList.get(position).getUserProfileImage()).into(holder.iv_user);
        }

        return convertView;
    }
}