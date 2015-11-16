package com.heyjude.androidapp.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.heyjude.androidapp.R;
import com.heyjude.androidapp.activity.RestaurantDetail;
import com.heyjude.androidapp.apirequest.RestClient;
import com.heyjude.androidapp.customview.MapWrapperLayout;
import com.heyjude.androidapp.customview.OnInfoWindowElemTouchListener;
import com.heyjude.androidapp.model.ListData;
import com.heyjude.androidapp.model.Restaurant;
import com.heyjude.androidapp.utility.HeyJudeApp;
import com.heyjude.androidapp.utility.Util;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MapViewFragment extends Fragment {

    private GoogleMap googleMap;
    private int PAGE_OFFSET = 1;
    private String TAG = "MapViewFragment";
    private TextView tvList;
    private MapWrapperLayout mapWrapperLayout;
    private HashMap<Marker, ListData> mMarkersHashMap;

    private ViewGroup infoWindow;
    ImageView ivRestImage;
    TextView tvRestName;
    TextView tvRestCall;
    private OnInfoWindowElemTouchListener infoButtonListener, imageListener;
    MarkerOptions mo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, null);

        tvList = (TextView) view.findViewById(R.id.tv_ListView);
        mapWrapperLayout = (MapWrapperLayout) view.findViewById(R.id.map_relative_layout);

        googleMap = ((MyMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
        ((MyMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).setListener(new MyMapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
            }
        });

        mapWrapperLayout.init(googleMap, getPixelsFromDp(getActivity(), 39 + 20));

        this.infoWindow = (ViewGroup) getLayoutInflater(savedInstanceState).inflate(R.layout.infowindow, null);
        this.ivRestImage = (ImageView) infoWindow.findViewById(R.id.ivRestImage);
        this.tvRestName = (TextView) infoWindow.findViewById(R.id.tvRestName);
        this.tvRestCall = (TextView) infoWindow.findViewById(R.id.tvRestCall);

        tvList.setOnClickListener(listClickListener);

        // Setting custom OnTouchListener which deals with the pressed state
        // so it shows up
        this.infoButtonListener = new OnInfoWindowElemTouchListener(tvRestCall) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {

                Log.e("Clicked", "1");

                final ListData listData = mMarkersHashMap.get(marker);
                // Here we can perform some action triggered after clicking the button
                Toast.makeText(getActivity(), listData.getPostTitle() + "'s button clicked!", Toast.LENGTH_SHORT).show();
            }
        };


        this.imageListener = new OnInfoWindowElemTouchListener(tvRestCall) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {

                Log.e("Clicked", "2");
                final ListData listData = mMarkersHashMap.get(marker);

                Intent intent = new Intent(new Intent(getActivity(), RestaurantDetail.class));
                intent.putExtra("RESTID", listData.getID());
                getActivity().startActivity(intent);
            }
        };

        this.tvRestCall.setOnTouchListener(infoButtonListener);
        this.ivRestImage.setOnTouchListener(imageListener);

        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                new LatLng(Double.parseDouble(Util.latitude), Double.parseDouble(Util.longitude))).
                zoom(12).
                build();

        mo = new MarkerOptions().position(
                new LatLng(Double.parseDouble(Util.latitude), Double.parseDouble(Util.longitude)));

        mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location));

        Marker currentMarker = googleMap.addMarker(mo);

        Circle circle = googleMap.addCircle(new CircleOptions()
                .center(new LatLng(Double.parseDouble(Util.latitude), Double.parseDouble(Util.longitude)))
                .radius(1000).strokeColor(getResources().getColor(R.color.map_strokecolor))
                .fillColor(getResources().getColor(R.color.map_fillcolor)));

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                final ListData listData = mMarkersHashMap.get(marker);

                Intent intent = new Intent(new Intent(getActivity(), RestaurantDetail.class));
                intent.putExtra("RESTID", listData.getID());
                getActivity().startActivity(intent);

                /*Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + Uri.parse(listData.getContactphone())));
                startActivity(intent);*/
            }
        });

        getRestaurantList();

        return view;
    }


    private void getRestaurantList() {

        if (HeyJudeApp.hasNetworkConnection()) {

            RestClient.getInstance().getApiService().getRestaurants(
                    Util.latitude,
                    Util.longitude,
                    PAGE_OFFSET,
                    new Callback<Restaurant>() {
                        @Override
                        public void success(Restaurant restaurants, Response response) {
                            Log.e(TAG, "Success");

                            mMarkersHashMap = new HashMap<Marker, ListData>();
                            plotMarkers(restaurants.getData());
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.e(TAG, "Failure");
                        }
                    });

        } else {
            Util.showToast(getActivity(), getString(R.string.alert_connection));
        }
    }

    private void plotMarkers(List<ListData> markers) {


        if (markers.size() > 0) {
            for (ListData listData : markers) {

                // Create user marker with custom icon and other options
                MarkerOptions markerOption = new MarkerOptions().position(new LatLng(Double.parseDouble(listData.getLat()), Double.parseDouble(listData.getLng())));
                markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin));

                Marker cm = googleMap.addMarker(markerOption);
                mMarkersHashMap.put(cm, listData);

                googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker marker) {


                        final ListData listData = mMarkersHashMap.get(marker);

                        if (listData != null) {
                            Picasso.with(getActivity()).load(listData.getImage()).placeholder(R.drawable.ic_restaurant).into(ivRestImage);
                            tvRestName.setText(listData.getPostTitle());

                            infoButtonListener.setMarker(marker);

                            // We must call this to set the current marker and infoWindow references
                            // to the MapWrapperLayout
                            mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
                            return infoWindow;
                        } else {
                            mo.title("Current Location");
                            return null;
                        }
                    }

                    @Override
                    public View getInfoContents(Marker marker) {
                        return null;
                    }
                });
            }
        }
    }

    View.OnClickListener listClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.e("Map Button Clicked", "Map Button Clicked");

            Fragment fragment = new TwoForOneDiningFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }
    };

    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
