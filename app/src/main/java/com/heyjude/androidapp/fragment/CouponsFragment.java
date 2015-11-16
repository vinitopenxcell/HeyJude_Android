package com.heyjude.androidapp.fragment;

/**
 * Created by dipen on 26/6/15.
 */

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.heyjude.androidapp.R;
import com.heyjude.androidapp.adapter.CouponsListAdapter;
import com.heyjude.androidapp.dialog.AssistantDialog;
import com.heyjude.androidapp.dialog.QRDialog;
import com.heyjude.androidapp.libclasses.LoadMoreListView;
import com.heyjude.androidapp.model.CouponCampaign;
import com.heyjude.androidapp.model.GetCouponCampaign;
import com.heyjude.androidapp.utility.HeyJudeApp;
import com.heyjude.androidapp.utility.Util;
import com.heyjude.androidapp.wiGroupAPIRequest.WiGroupRestClient;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CouponsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener {

    private LoadMoreListView lv_latest;
    private AssistantDialog assistantDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<CouponCampaign> couponCampaignArrayList = new ArrayList<>();
    private ProgressDialog progressDialog;
    private CouponsListAdapter adapter;
    private String PAGE_OFFSET = "0";
    private String PAGE_SIZE = "20";
    private TextView tv_noVoucher;
    private ProgressBar progressBar;
    private TextView tvError;
    private SearchView searchView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.tab_latest, container, false);
        lv_latest = (LoadMoreListView) v.findViewById(R.id.lv_latest);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        tv_noVoucher = (TextView) v.findViewById(R.id.tv_noVoucher);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        tvError = (TextView) v.findViewById(R.id.tvError);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.blue));

        progressBar.setVisibility(View.VISIBLE);

        getListVoucher();
        lv_latest.setOnItemClickListener(VoucherItemClickListner);

        setHasOptionsMenu(true);

        lv_latest.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {

            @Override
            public void onLoadMore() {

                LoadMoreData();
            }
        });
        return v;
    }


    AdapterView.OnItemClickListener VoucherItemClickListner = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            opennewDialog(
                    couponCampaignArrayList.get(i).getId().toString(),
                    couponCampaignArrayList.get(i).getName(),
                    couponCampaignArrayList.get(i).getDescription(),
                    couponCampaignArrayList.get(i).getImageUrl());
        }
    };

    /**
     * Dialog will open on Click of Coupon.
     *
     * @param campignId
     * @param name
     * @param description
     * @param imageURL
     */
    public void opennewDialog(final String campignId, String name, String description, String imageURL) {

        assistantDialog = new AssistantDialog(getActivity(), name, description, imageURL);
        assistantDialog.ButtonClickListner(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assistantDialog.dismiss();

                final QRDialog qrDialog = new QRDialog(getActivity(), campignId);
                qrDialog.ButtonClickListner(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        qrDialog.dismiss();


                    }
                });
                qrDialog.show();
            }
        });
        assistantDialog.show();

    }

    private void getListVoucher() {

        if (HeyJudeApp.hasNetworkConnection()) {
            swipeRefreshLayout.setRefreshing(true);

            WiGroupRestClient.getInstance().getApiService().getCouponCampaign(
                    Util.latitude,
                    Util.longitude,
                    PAGE_OFFSET,
                    PAGE_SIZE,
                    new Callback<GetCouponCampaign>() {
                        @Override
                        public void success(GetCouponCampaign getCouponCampaign, Response response) {


                            Log.e("Success Latest Fragment", "Success WiGroup");

                            progressBar.setVisibility(View.GONE);

                            couponCampaignArrayList.clear();
                            if (getCouponCampaign.getCouponCampaigns().size() > 0)
                                couponCampaignArrayList.addAll(getCouponCampaign.getCouponCampaigns());
                            if (getCouponCampaign.getCouponCampaigns().size() == 0)
                                tv_noVoucher.setVisibility(View.VISIBLE);

                            adapter = new CouponsListAdapter(getActivity(), couponCampaignArrayList);
                            lv_latest.setAdapter(adapter);

                            swipeRefreshLayout.setRefreshing(false);
                        }

                        @Override
                        public void failure(RetrofitError error) {

                            progressBar.setVisibility(View.GONE);
                            tvError.setText(getString(R.string.lbl_retrofit_error));
                            swipeRefreshLayout.setRefreshing(false);

                            Log.e("Error Latest Fragment", "Error WiGroup : " + error.toString());
                            Util.showToast(getActivity(), getActivity().getString(R.string.lbl_retrofit_error));
                        }
                    });
        } else {
            Util.showToast(getActivity(), getActivity().getString(R.string.alert_connection));
        }

    }

    private void LoadMoreData() {

        if (HeyJudeApp.hasNetworkConnection()) {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }

            progressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.lbl_loading), false, false);

            WiGroupRestClient.getInstance().getApiService().getCouponCampaign(
                    Util.latitude,
                    Util.longitude,
                    PAGE_OFFSET + 1,
                    PAGE_SIZE + 20,
                    new Callback<GetCouponCampaign>() {
                        @Override
                        public void success(GetCouponCampaign getCouponCampaign, Response response) {

                            if (progressDialog.isShowing())
                                progressDialog.dismiss();

                            Log.e("Success Latest Fragment", "Success WiGroup");

                            lv_latest.onLoadMoreComplete();

                            if (getCouponCampaign.getCouponCampaigns().size() > 0)
                                couponCampaignArrayList.addAll(getCouponCampaign.getCouponCampaigns());

                            adapter.notifyDataSetChanged();


                        }

                        @Override
                        public void failure(RetrofitError error) {

                            lv_latest.onLoadMoreComplete();

                            if (progressDialog.isShowing())
                                progressDialog.dismiss();

                            Log.e("Error Latest Fragment", "Error WiGroup : " + error.toString());
                            Util.showToast(getActivity(), getActivity().getString(R.string.lbl_retrofit_error));
                        }
                    });
        } else {
            Util.showToast(getActivity(), getActivity().getString(R.string.alert_connection));
        }
    }

    @Override
    public void onRefresh() {
        getListVoucher();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_display_vouchers, menu);
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

        if (id == R.id.action_search) {
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;

    }

    @Override
    public boolean onQueryTextChange(String newText) {

        String str = newText.toString();
        adapter = new CouponsListAdapter(getActivity(), getFilteredArry(str, couponCampaignArrayList));
        lv_latest.setAdapter(adapter);

        return true;
    }

    private ArrayList<CouponCampaign> getFilteredArry(String str, ArrayList<CouponCampaign> mListItems) {

        ArrayList<CouponCampaign> filtered_ary = new ArrayList<>();
        for (int i = 0; i < mListItems.size(); i++) {

            if (mListItems.get(i).getName().toLowerCase().startsWith(str.toLowerCase())) {
                filtered_ary.add(mListItems.get(i));
            }
        }

        return filtered_ary;
    }
}

