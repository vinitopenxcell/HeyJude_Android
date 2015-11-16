package com.heyjude.androidapp.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.heyjude.androidapp.R;
import com.heyjude.androidapp.adapter.VendorReviewsAdapter;
import com.heyjude.androidapp.apirequest.RestClient;
import com.heyjude.androidapp.model.ListData;
import com.heyjude.androidapp.model.VendorReviews;
import com.heyjude.androidapp.utility.HeyJudeApp;
import com.heyjude.androidapp.utility.Util;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ShowReviewsActivity extends BaseActivity implements View.OnClickListener {

    private String vendorId, vendorName;
    private String TAG = "ShowReviewsActivity";
    private TextView tvVendorName, tvError, tvLoadMore;
    private ListView lvVendorReviews;
    private ProgressBar progressBar, progressBar1;
    private VendorReviewsAdapter adapter;
    private ArrayList<ListData> reviewList = new ArrayList<>();
    int PAGE_OFFSET = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_reviews);

        setToolbar(this, "Hey Jude");

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            vendorId = bundle.getString("VendorId");
            vendorName = bundle.getString("VendorName");
        }

        Log.e(TAG, "Vendor Id:" + vendorId + " &VendorName:" + vendorName);


        init();

        adapter = new VendorReviewsAdapter(ShowReviewsActivity.this, reviewList);
        lvVendorReviews.setAdapter(adapter);

        getReviewList();
    }


    private void init() {
        tvVendorName = (TextView) findViewById(R.id.tvVendorName);
        tvLoadMore = (TextView) findViewById(R.id.tvLoadMore);
        lvVendorReviews = (ListView) findViewById(R.id.lv_reviews);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
        tvError = (TextView) findViewById(R.id.tvError);

        tvVendorName.setText(vendorName);
        tvLoadMore.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvLoadMore:
                PAGE_OFFSET = PAGE_OFFSET + 1;
                LoadMoreData();
                break;
        }
    }

    private void getReviewList() {

        if (HeyJudeApp.hasNetworkConnection()) {

            progressBar.setVisibility(View.VISIBLE);
            progressBar1.setVisibility(View.GONE);

            RestClient.getInstance().getApiService().getUserReview(
                    vendorId, PAGE_OFFSET, new Callback<VendorReviews>() {
                        @Override
                        public void success(VendorReviews vendorReviews, Response response) {

                            Log.e(TAG, "SUCCESS");
                            progressBar.setVisibility(View.GONE);

                            if (vendorReviews.getData().size() < 10)
                                tvLoadMore.setVisibility(View.GONE);

                            if (vendorReviews.getStatus().equals("Success")) {
                                if (vendorReviews.getData().size() > 0) {

                                    reviewList.clear();
                                    reviewList.addAll(vendorReviews.getData());

                                    adapter = new VendorReviewsAdapter(ShowReviewsActivity.this, reviewList);
                                    lvVendorReviews.setAdapter(adapter);
                                } else {
                                    tvError.setText(vendorReviews.getMessage());
                                    tvError.setVisibility(View.VISIBLE);
                                }
                            } else if (vendorReviews.getStatus().equals("Fail")) {
                                tvError.setText(vendorReviews.getMessage());
                                tvError.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.e(TAG, "FAIL");

                            progressBar.setVisibility(View.GONE);
                            tvError.setVisibility(View.VISIBLE);
                        }
                    }
            );

        } else {
            Util.showAlertDialog(ShowReviewsActivity.this);
        }
    }

    private void LoadMoreData() {

        if (HeyJudeApp.hasNetworkConnection()) {

            progressBar.setVisibility(View.GONE);
            progressBar1.setVisibility(View.VISIBLE);

            RestClient.getInstance().getApiService().getUserReview(
                    vendorId, PAGE_OFFSET, new Callback<VendorReviews>() {
                        @Override
                        public void success(VendorReviews vendorReviews, Response response) {

                            Log.e(TAG, "SUCCESS");
                            progressBar1.setVisibility(View.GONE);

                            if (vendorReviews.getStatus().equals("Success")) {
                                if (vendorReviews.getData().size() > 0) {
                                    reviewList.addAll(vendorReviews.getData());
                                    adapter.notifyDataSetChanged();
                                } else {
                                    tvLoadMore.setVisibility(View.GONE);
                                }
                            } else {
                                tvError.setVisibility(View.VISIBLE);
                            }


                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.e(TAG, "FAIL");

                            progressBar1.setVisibility(View.GONE);
                            tvError.setVisibility(View.VISIBLE);
                            Log.e("TwoForOneDining Failure", "Failure");
                        }
                    }
            );

        } else {
            Util.showAlertDialog(ShowReviewsActivity.this);
        }
    }
}
