package com.heyjude.androidapp.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.heyjude.androidapp.R;
import com.heyjude.androidapp.activity.ChatActivity;
import com.heyjude.androidapp.adapter.HistoryTaskAdapter;
import com.heyjude.androidapp.animation.AnimatorClass;
import com.heyjude.androidapp.apirequest.RestClient;
import com.heyjude.androidapp.model.ListData;
import com.heyjude.androidapp.model.Task;
import com.heyjude.androidapp.requestmodel.LoginData;
import com.heyjude.androidapp.utility.Constants;
import com.heyjude.androidapp.utility.Global;
import com.heyjude.androidapp.utility.HeyJudeApp;
import com.heyjude.androidapp.utility.Util;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by aalap on 21/5/15.
 */
public class HistoryTaskFragment extends Fragment implements AdapterView.OnItemClickListener, SearchView.OnQueryTextListener {

    private View viewVerticleLine;
    private ProgressBar progressBar;
    private TextView tvHistoryTask, tv_noTask;
    private ListView listTask;
    private RelativeLayout rrLayout;
    private ArrayList<ListData> taskLists = new ArrayList<>();
    private String TAG = "HistoryTaskFragment";
    private HistoryTaskAdapter adapter;

    private BroadcastReceiver refresh_reciever;
    private IntentFilter filter;
    private SharedPreferences sharedPreferences;
    private LoginData loginData;
    private SearchView searchView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_history, container, false);

        listTask = (ListView) v.findViewById(R.id.listTask);
        tv_noTask = (TextView) v.findViewById(R.id.tv_noTask);
        tvHistoryTask = (TextView) v.findViewById(R.id.tvHistoryTask);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        viewVerticleLine = (View) v.findViewById(R.id.viewVerticleLine);
        rrLayout = (RelativeLayout) v.findViewById(R.id.rrLayout);


        Gson gson = new Gson();
        sharedPreferences = getActivity().getSharedPreferences(Global.PREF_NAME, Context.MODE_PRIVATE);
        loginData = gson.fromJson(sharedPreferences.getString(Global.PREF_RESPONSE_OBJECT, ""), LoginData.class);

        listTask.setOnItemClickListener(this);

        getTaskList(true);
        setHasOptionsMenu(false);

        refresh_reciever = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                Log.i(TAG, "Received message   " + intent.toString());
                if (intent.getAction().equals(Constants.REFRESH_DATA)) {
                    getTaskList(false);
                }

            }
        };


        filter = new IntentFilter();
        filter.addAction(Constants.REFRESH_DATA);

        getActivity().registerReceiver(refresh_reciever, filter);

        return v;
    }

    private void getTaskList(boolean isProgressbar) {

        if (HeyJudeApp.hasNetworkConnection()) {

            if (isProgressbar)
                progressBar.setVisibility(View.VISIBLE);

            RestClient.getInstance().getApiService().getCompletedTask(
                    loginData.getData().getId(),
                    new Callback<Task>() {
                        @Override
                        public void success(Task task, Response response) {
                            Log.e(TAG, "Success");

                            if (task.getStatus().equals("Success")) {
                                if (task.getData().size() > 0) {
                                    progressBar.setVisibility(View.GONE);
                                    rrLayout.setVisibility(View.VISIBLE);
                                    taskLists.clear();
                                    taskLists.addAll(task.getData());

                                    adapter = new HistoryTaskAdapter(getActivity(), taskLists);
                                    listTask.setAdapter(adapter);

                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    rrLayout.setVisibility(View.GONE);
                                    tv_noTask.setVisibility(View.VISIBLE);
                                    tv_noTask.setText(getActivity().getString(R.string.lbl_no_history_found));
                                }
                            } else {
                                progressBar.setVisibility(View.GONE);
                                rrLayout.setVisibility(View.GONE);
                                tv_noTask.setVisibility(View.VISIBLE);
                                tv_noTask.setText(getActivity().getString(R.string.lbl_no_history_found));
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.e(TAG, "Fail");
                            progressBar.setVisibility(View.GONE);
                            rrLayout.setVisibility(View.GONE);
                            tv_noTask.setVisibility(View.VISIBLE);
                            tv_noTask.setText(getActivity().getString(R.string.lbl_retrofit_error));
                        }
                    });
        } else {
            Util.showAlertDialog(getActivity());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
        ListData listData = adapter.getItem(pos);

        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(Constants.REQUEST_ID, String.valueOf(listData.getTaskId()));
        intent.putExtra(Constants.JUDE_ID, listData.getJudeId());
        intent.putExtra(Constants.FROM, Constants.HISTORY_TASK);
        startActivity(intent);
        AnimatorClass.appearLeftAnimation(getActivity());
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
        adapter = new HistoryTaskAdapter(getActivity(), getFilteredArry(str, taskLists));
        listTask.setAdapter(adapter);

        return true;
    }

    private ArrayList<ListData> getFilteredArry(String str, ArrayList<ListData> mListItems) {

        ArrayList<ListData> filtered_ary = new ArrayList<>();
        for (int i = 0; i < mListItems.size(); i++) {

            if (mListItems.get(i).getTaskTitle().toLowerCase().startsWith(str.toLowerCase())) {
                filtered_ary.add(mListItems.get(i));
            }
        }

        return filtered_ary;
    }
}
