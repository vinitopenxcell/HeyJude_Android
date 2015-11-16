package com.heyjude.androidapp.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.heyjude.androidapp.R;
import com.heyjude.androidapp.apirequest.RestClient;
import com.heyjude.androidapp.customview.ParallexScrollView;
import com.heyjude.androidapp.fragment.MyMapFragment;
import com.heyjude.androidapp.model.RestaurantData;
import com.heyjude.androidapp.utility.Constants;
import com.heyjude.androidapp.utility.HeyJudeApp;
import com.heyjude.androidapp.utility.Util;
import com.squareup.picasso.Picasso;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RestaurantDetail extends BaseActivity implements View.OnClickListener, ParallexScrollView.OnScrollChangedListener {

    private TextView tvName, tvDescription, tvTitle1, tvTitle2, tvTitle3, tvTitle4, tvOffer, tvExclusions, tvTrendingTimes;
    private ImageView ivRest;
    private Button btnReservation;
    private GoogleMap googleMap;
    private ProgressBar progressBar;
    private LinearLayout llContainer;
    private int id;
    private ParallexScrollView scrollView;
    private FrameLayout imgContainer;
    private String TAG = "RestaurantDetail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // // set an enter transition
            // getWindow().setEnterTransition(new Explode());
            // // set an exit transition
            // getWindow().setExitTransition(new Explode());

            getWindow().setSharedElementEnterTransition(new ChangeBounds());
            getWindow().setSharedElementExitTransition(new ChangeBounds());
        }
        super.onCreate(savedInstanceState);


        if (!Util.isGooglePlayServicesAvailable(RestaurantDetail.this)) {
            finish();
        }

        setContentView(R.layout.activity_restaurant_detail);

        setToolbar(this, "Hey Jude");

        init();
    }

    private void init() {
        tvTitle1 = (TextView) findViewById(R.id.tv_title_1);
        tvTitle2 = (TextView) findViewById(R.id.tv_title_2);
        tvTitle3 = (TextView) findViewById(R.id.tv_title_3);
        tvTitle4 = (TextView) findViewById(R.id.tv_title_4);
        tvName = (TextView) findViewById(R.id.tv_restname);
        imgContainer = (FrameLayout) findViewById(R.id.imgContainer);
        tvDescription = (TextView) findViewById(R.id.tv_descri);
        tvOffer = (TextView) findViewById(R.id.tv_offer);
        tvExclusions = (TextView) findViewById(R.id.tv_exclusions);
        tvTrendingTimes = (TextView) findViewById(R.id.tv_trending_times);
        ivRest = (ImageView) findViewById(R.id.iv_restImage);
        llContainer = (LinearLayout) findViewById(R.id.llContainer);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        tvName.setSelected(true);


        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            id = extra.getInt(Constants.RESTID);
            String title = extra.getString(Constants.RESTNAME);
            String image = extra.getString(Constants.RESTIMAGE);

            if (image.isEmpty())
                Picasso.with(RestaurantDetail.this).load(R.drawable.ic_restaurant).fit().into(ivRest);
            else
                Picasso.with(RestaurantDetail.this).load(image).error(R.drawable.ic_restaurant).fit().into(ivRest);

            tvName.setText(title);
            getResturantDetails(id);
        }

        scrollView = (ParallexScrollView) findViewById(R.id.scrollView);

        googleMap = ((MyMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_Rest)).getMap();

        ((MyMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_Rest)).setListener(new MyMapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                scrollView.requestDisallowInterceptTouchEvent(true);
            }
        });


        btnReservation = (Button) findViewById(R.id.btn_reservation);
        btnReservation.setOnClickListener(this);
        scrollView.setOnScrollChangedListener(this);
    }

    private void getResturantDetails(int id) {

        if (HeyJudeApp.hasNetworkConnection()) {

//            Util.showProgressDailog(RestaurantDetail.this);
            RestClient.getInstance().getApiService().getRestaurantsData(id, new Callback<RestaurantData>() {

                @Override
                public void success(RestaurantData restaurant, Response response) {

                    progressBar.setVisibility(View.GONE);
                    llContainer.setVisibility(View.VISIBLE);

                    Log.e(TAG, "Success");

//                    Util.dismissProgressDailog();

//                    tvName.setText(restaurant.getData().getPostTitle());

                    if (restaurant.getData().getPostContent().isEmpty()) {
                        tvTitle1.setVisibility(View.GONE);
                        tvDescription.setVisibility(View.GONE);
                    } else {
                        tvDescription.setText(restaurant.getData().getPostContent().trim());
                    }

                    if (restaurant.getData().getTrendingItems().isEmpty()) {
                        tvTitle2.setVisibility(View.GONE);
                        tvTrendingTimes.setVisibility(View.GONE);
                    } else {
                        tvTrendingTimes.setText(restaurant.getData().getTrendingItems().trim());
                    }

                    if (restaurant.getData().getWpcfExclusions().isEmpty()) {
                        tvTitle3.setVisibility(View.GONE);
                        tvExclusions.setVisibility(View.GONE);
                    } else {
                        tvExclusions.setText(restaurant.getData().getWpcfExclusions().trim());
                    }

                    if (restaurant.getData().getOfferValid().isEmpty()) {
                        tvTitle4.setVisibility(View.GONE);
                        tvOffer.setVisibility(View.GONE);
                    } else {
                        tvOffer.setText(restaurant.getData().getOfferValid().trim());
                    }


                    if (!TextUtils.isEmpty(restaurant.getData().getLat()) & !TextUtils.isEmpty(restaurant.getData().getLng())) {
                        LatLng latLng = new LatLng(Double.parseDouble(restaurant.getData().getLat()), Double.parseDouble(restaurant.getData().getLng()));
                        googleMap.addMarker(new MarkerOptions().title(restaurant.getData().getPostTitle()).position(latLng));
                        googleMap.setMyLocationEnabled(true);

                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                    }

                    /*LatLng latLng = new LatLng(Double.parseDouble(restaurant.getData().getLat()), Double.parseDouble(restaurant.getData().getLng()));
                    googleMap.setMyLocationEnabled(true);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    googleMap.addMarker(new MarkerOptions().title(name).position(latLng));*/


                    // temp = restaurant.getData().getContactsDetails().split(";");

                }

                @Override
                public void failure(RetrofitError error) {

//                    Util.dismissProgressDailog();

                    Log.e(TAG, "Error");
                    Util.showToast(RestaurantDetail.this, getString(R.string.lbl_retrofit_error));
                }
            });
        } else {
            Util.showAlertDialog(RestaurantDetail.this);
        }
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btn_reservation:

                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + Uri.parse(Constants.JUDE_PHONE_NO)));
                startActivity(intent);
                break;
        }

    }

    @Override
    public void onScrollChanged(int deltaX, int deltaY) {
        int scrollY = scrollView.getScrollY();
        // Add parallax effect
        imgContainer.setTranslationY(scrollY * 0.3f);
    }

}
