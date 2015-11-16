package com.heyjude.androidapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.heyjude.androidapp.R;
import com.heyjude.androidapp.activity.ShowReviewsActivity;
import com.heyjude.androidapp.dialog.AcceptQuote;
import com.heyjude.androidapp.model.VendorList;
import com.heyjude.androidapp.utility.Constants;
import com.heyjude.androidapp.utility.Util;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by dipen on 13/8/15.
 */
public class ViewPagerIndicator extends PagerAdapter {

    // Declare Variables
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<VendorList> venodr_list = new ArrayList<>();
    private Bundle bundle;
    private AcceptQuote acceptQuote;
    private String taskid;
    private boolean isFromHistory = false;
    private String judeId;


    public ViewPagerIndicator(Bundle b, Activity activity, ArrayList<VendorList> venodr_list, String taskid,
                              boolean isFromHistory,
                              String judeId) {
        this.bundle = b;
        this.context = activity;
        this.venodr_list = venodr_list;
        this.taskid = taskid;
        this.isFromHistory = isFromHistory;
        this.judeId = judeId;
    }

    @Override
    public int getCount() {
        return venodr_list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    public class ViewHolder {
        private TextView tv_vendorTitle, tv_commentCount, tv_vendorDistance, tv_vendorAddress, tv_vendorMobile, tv_vendorContent, tv_accept, tv_direction;
        private RatingBar rating_vendorStar;
        private LinearLayout ll_call, ll_reviews;
        private ImageView ivMap;
    }


    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {

        // Declare Variables
        ViewHolder holder = new ViewHolder();

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.row_chat_deals, null);


        holder.tv_vendorTitle = (TextView) itemView.findViewById(R.id.tv_vendorTitle);
        holder.tv_commentCount = (TextView) itemView.findViewById(R.id.tv_commentCount);
        holder.tv_vendorDistance = (TextView) itemView.findViewById(R.id.tv_vendorDistance);
        holder.tv_vendorAddress = (TextView) itemView.findViewById(R.id.tv_vendorAddress);
        holder.tv_vendorMobile = (TextView) itemView.findViewById(R.id.tv_vendorMobile);
        holder.tv_vendorContent = (TextView) itemView.findViewById(R.id.tv_vendorContent);
        holder.tv_accept = (TextView) itemView.findViewById(R.id.tv_accept);
        holder.ivMap = (ImageView) itemView.findViewById(R.id.ivMap);
        holder.tv_direction = (TextView) itemView.findViewById(R.id.tv_directions);
        holder.ll_call = (LinearLayout) itemView.findViewById(R.id.ll_call);
        holder.ll_reviews = (LinearLayout) itemView.findViewById(R.id.ll_reviews);


        if (isFromHistory) {
            holder.tv_accept.setEnabled(false);
        }

        holder.rating_vendorStar = (RatingBar) itemView.findViewById(R.id.rating_vendorStar);

        holder.ll_reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ShowReviewsActivity.class);
                intent.putExtra("VendorId", venodr_list.get(position).vendor_id);
                intent.putExtra("VendorName", venodr_list.get(position).vendor_name);
                context.startActivity(intent);
            }
        });

        holder.ll_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + Uri.parse(venodr_list.get(position).mobile)));
                context.startActivity(intent);
            }
        });

        holder.tv_direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!TextUtils.isEmpty(venodr_list.get(position).lat) && !TextUtils.isEmpty(venodr_list.get(position).lon)) {
                    String url = "http://maps.google.com/maps?saddr=" + Util.latitude + "," + Util.longitude + "&daddr=" + venodr_list.get(position).lat + "," + venodr_list.get(position).lon + "&mode=driving";
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "Unable to find the Location", Toast.LENGTH_LONG).show();
                }
            }
        });

        holder.tv_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptQuote = new AcceptQuote(context, venodr_list.get(position).vendor_id.toString(), taskid, judeId);
                acceptQuote.show();
            }
        });


        holder.tv_vendorTitle.setText(venodr_list.get(position).vendor_name);
        holder.tv_commentCount.setText(venodr_list.get(position).vendor_comments + " Reviews");
        holder.tv_vendorDistance.setText(venodr_list.get(position).vendor_distance + "Km from your Location");
        holder.tv_vendorAddress.setText(venodr_list.get(position).vendor_address);
        holder.tv_vendorMobile.setText(venodr_list.get(position).mobile);
        holder.tv_vendorContent.setText(venodr_list.get(position).judesays);

        if (!TextUtils.isEmpty(venodr_list.get(position).lat) && !TextUtils.isEmpty(venodr_list.get(position).lon)) {
            Picasso.with(context).load("https://maps.googleapis.com/maps/api/" +
                    "staticmap?center=" + venodr_list.get(position).lat + "," + venodr_list.get(position).lon +
                    "&zoom=17&size=500x250" +
                    "&maptype=roadmap" +
                    "&markers=color:red|label:Vendor|" + venodr_list.get(position).lat + "," + venodr_list.get(position).lon +
                    "&key=" + Constants.GOOGLE_KEY).error(R.drawable.image_map).into(holder.ivMap);
        }

        holder.rating_vendorStar.setRating(Float.parseFloat(String.valueOf(venodr_list.get(position).vendor_star)));


        // Add row_chat_deals.xml to ViewPager
        container.addView(itemView);

        return itemView;

    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        ((ViewPager) container).removeView((View) object);
    }

}
