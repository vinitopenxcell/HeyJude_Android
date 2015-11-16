package com.heyjude.androidapp.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.heyjude.androidapp.R;
import com.heyjude.androidapp.activity.RestaurantDetail;
import com.heyjude.androidapp.adapter.TwoForOneDiningAdapter;
import com.heyjude.androidapp.apirequest.RestClient;
import com.heyjude.androidapp.libclasses.LoadMoreListView;
import com.heyjude.androidapp.model.ListData;
import com.heyjude.androidapp.model.Restaurant;
import com.heyjude.androidapp.utility.Constants;
import com.heyjude.androidapp.utility.HeyJudeApp;
import com.heyjude.androidapp.utility.Util;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TwoForOneDiningFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener, AdapterView.OnItemClickListener {


    private int PAGE_OFFSET = 0;
    private TextView tvError, tvMap;
    private FrameLayout frameContainer;
    private LoadMoreListView lv_resturants;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TwoForOneDiningAdapter adapter;
    private ArrayList<ListData> restaurantLists = new ArrayList<>();
    private SearchView searchView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_two_for_one_dining, container, false);


        lv_resturants = (LoadMoreListView) v.findViewById(R.id.lv_restaurants);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        tvError = (TextView) v.findViewById(R.id.tvError);
        tvMap = (TextView) v.findViewById(R.id.tv_MapView);
        frameContainer = (FrameLayout) v.findViewById(R.id.frameContainer);

        progressBar.setVisibility(View.VISIBLE);


        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.blue));

        adapter = new TwoForOneDiningAdapter(getActivity(), restaurantLists);
        lv_resturants.setAdapter(adapter);
        lv_resturants.setOnItemClickListener(this);

        lv_resturants.setTextFilterEnabled(true);

        getRestaurantList();

        setHasOptionsMenu(true);

        tvMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.e("Map Button Clicked", "Map Button Clicked");

                Fragment fragment = new MapViewFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.commit();
            }
        });


        lv_resturants.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {

            @Override
            public void onLoadMore() {
                PAGE_OFFSET = PAGE_OFFSET + 1;
                LoadMoreData();
            }
        });

        return v;
    }

    private void LoadMoreData() {
        if (HeyJudeApp.hasNetworkConnection()) {

            RestClient.getInstance().getApiService().getRestaurants(
                    Util.latitude,
                    Util.longitude,
                    PAGE_OFFSET,
                    new Callback<Restaurant>() {
                        @Override
                        public void success(Restaurant restaurants, Response response) {
                            Log.e("TwoForOneDining Success", "Success");

                            if (restaurants.getStatus().equalsIgnoreCase("Success")) {
                                lv_resturants.onLoadMoreComplete();

                                if (restaurants.getData().size() > 0) {
                                    restaurantLists.addAll(restaurants.getData());

                                    for (int i = 0; i < restaurants.getData().size(); i++) {
                                        Log.e("Distance:" + i, ":" + restaurantLists.get(i).getDistance());
                                        //  Log.e("" + i, "Lat:" + restaurantLists.get(i).getLat() + "Lon:" + restaurantLists.get(i).getLng() + "\n");
                                    }

                                    adapter.notifyDataSetChanged();
                                }
                            } else {
                                lv_resturants.onLoadMoreComplete();
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            lv_resturants.onLoadMoreComplete();
                            Log.e("TwoForOneDining Failure", "Failure");
                        }
                    });

        } else {
            Util.showToast(getActivity(), getString(R.string.alert_connection));
        }
    }

    private void getRestaurantList() {

        if (HeyJudeApp.hasNetworkConnection()) {
            swipeRefreshLayout.setRefreshing(true);

            RestClient.getInstance().getApiService().getRestaurants(
                    Util.latitude,
                    Util.longitude,
                    PAGE_OFFSET,
                    new Callback<Restaurant>() {
                        @Override
                        public void success(Restaurant restaurants, Response response) {
                            Log.e("TwoForOneDining Success", "Success");

                            progressBar.setVisibility(View.GONE);

                            if (restaurants.getData().size() > 0) {
                                frameContainer.setVisibility(View.VISIBLE);

                                restaurantLists.clear();
                                restaurantLists.addAll(restaurants.getData());

                                adapter = new TwoForOneDiningAdapter(getActivity(), restaurantLists);
                                lv_resturants.setAdapter(adapter);

                                swipeRefreshLayout.setRefreshing(false);
                            } else {
                                frameContainer.setVisibility(View.GONE);
                                tvError.setVisibility(View.VISIBLE);
                                tvError.setText("No Restaurant Found");
                            }

                        }

                        @Override
                        public void failure(RetrofitError error) {
                            frameContainer.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            tvError.setVisibility(View.VISIBLE);
                            tvError.setText(getString(R.string.lbl_retrofit_error));
                            Log.e("TwoForOneDining Failure", "Failure");
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });

        } else {
            Util.showToast(getActivity(), getString(R.string.alert_connection));
        }

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_two_for_one_dining, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint(Html.fromHtml("<font color = #ffffff>" + getResources().getString(R.string.lbl_search) + "</font>"));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRefresh() {
        getRestaurantList();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;

    }

    @Override
    public boolean onQueryTextChange(String newText) {

        String str = newText.toString();
        adapter = new TwoForOneDiningAdapter(getActivity(), getFilteredArry(str, restaurantLists));
        lv_resturants.setAdapter(adapter);

        return true;
    }

    private ArrayList<ListData> getFilteredArry(String str, ArrayList<ListData> mListItems) {

        ArrayList<ListData> filtered_ary = new ArrayList<>();
        for (int i = 0; i < mListItems.size(); i++) {

            if (mListItems.get(i).getPostTitle().toLowerCase().startsWith(str.toLowerCase())) {
                filtered_ary.add(mListItems.get(i));
            }
        }

        return filtered_ary;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
        ListData listData = adapter.getItem(pos);

        Log.e("Selected Restaurant", listData.getID().toString());

        Intent intent = new Intent(new Intent(getActivity(), RestaurantDetail.class));
        intent.putExtra(Constants.RESTID, listData.getID());
        intent.putExtra(Constants.RESTNAME, listData.getPostTitle());
        intent.putExtra(Constants.RESTIMAGE, listData.getImage());

        Pair<View, String> p1 = Pair.create(view.findViewById(R.id.tv_restname), getString(R.string.restaurnat_name_transition));
        Pair<View, String> p2 = Pair.create(view.findViewById(R.id.iv_restImage), getString(R.string.restaurnat_image_transition));
        Pair<View, String> p3 = Pair.create(view.findViewById(R.id.iv_shadow), getString(R.string.restaurnat_image_transition));

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), p1, p2, p3);
        getActivity().startActivity(intent, options.toBundle());
    }
}
