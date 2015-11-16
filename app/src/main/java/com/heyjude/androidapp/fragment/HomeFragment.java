package com.heyjude.androidapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.heyjude.androidapp.R;
import com.heyjude.androidapp.activity.ChatActivity;
import com.heyjude.androidapp.animation.AnimatorClass;
import com.heyjude.androidapp.apirequest.RestClient;
import com.heyjude.androidapp.model.ChangePassword;
import com.heyjude.androidapp.requestmodel.LoginData;
import com.heyjude.androidapp.utility.Constants;
import com.heyjude.androidapp.utility.GPSTracker;
import com.heyjude.androidapp.utility.Global;
import com.heyjude.androidapp.utility.Util;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by aalap on 20/5/15.
 */
public class HomeFragment extends Fragment {

    private String TAG = "HomeFragment";

    private TextView tvUsername;
    private ImageView ivSend;
    private EditText etChatmessage;

    private String city = "", state = "", country = "";

    private SharedPreferences sharedPreferences;
    private LoginData loginData;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home_new, container, false);

        Util.getCurrentLocation(getActivity());

        GPSTracker gpsTracker = new GPSTracker(getActivity());

        Gson gson = new Gson();
        sharedPreferences = getActivity().getSharedPreferences(Global.PREF_NAME, Context.MODE_PRIVATE);
        loginData = gson.fromJson(sharedPreferences.getString(Global.PREF_RESPONSE_OBJECT, ""), LoginData.class);

        if (gpsTracker.getIsGPSTrackingEnabled(getActivity())) {

            country = gpsTracker.getCountryName(getActivity());
            state = gpsTracker.getState(getActivity());
            city = gpsTracker.getCity(getActivity());

            Log.e("Country:", country + " State: " + state + " city: " + city);
            Log.e("Fragment Data:", "" + loginData.getData().getUserName());
        } else {
            gpsTracker.showSettingsAlert(getActivity());
        }

        tvUsername = (TextView) v.findViewById(R.id.tvUsername);
        etChatmessage = (EditText) v.findViewById(R.id.editChatMsg);
        ivSend = (ImageView) v.findViewById(R.id.ivSend);

        if (loginData.getData().getUserName().contains(" ")) {
            String[] splited = loginData.getData().getUserName().split(" ");
            tvUsername.setText("Hi " + splited[0] + ",");
        } else {
            tvUsername.setText("Hi " + loginData.getData().getUserName().trim() + ",");
        }

/*

        if (!user.getImageProfile().isEmpty())
            Picasso.with(getActivity()).load(user.getImageProfile()).placeholder(R.drawable.ic_no_profile_pic).error(R.drawable.ic_no_profile_pic).into(imgSender);
        else
            Picasso.with(getActivity()).load(R.drawable.ic_no_profile_pic).placeholder(R.drawable.ic_no_profile_pic).error(R.drawable.ic_no_profile_pic).into(imgSender);
*/

        ivSend.setOnClickListener(chatClickListener);

        return v;
    }


    View.OnClickListener chatClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            final String newMessage = etChatmessage.getText().toString().trim();

            if (TextUtils.isEmpty(newMessage)) {
                //error Message
                etChatmessage.setError("Please Enter Chat Message");
            } else {

                Util.showProgressDailog(getActivity());
                RestClient.getInstance().getApiService().createTask(
                        newMessage,
                        loginData.getData().getId(),
                        Util.latitude + "," + Util.longitude,
                        "1",
                        country,
                        state,
                        city,
                        new Callback<ChangePassword>() {
                            @Override
                            public void success(ChangePassword changePassword, Response response) {

                                Util.dismissProgressDailog();
                                Log.e(TAG, "Success");

                                Intent intent = new Intent(getActivity(), ChatActivity.class);
                                intent.putExtra(Constants.CHAT_MESSAGE, newMessage);
                                intent.putExtra(Constants.FROM_FRAGMENT, true);
                                intent.putExtra(Constants.REQUEST_ID, changePassword.getData().getRequestId());
                                etChatmessage.setText("");
                                startActivity(intent);
                                AnimatorClass.appearLeftAnimation(getActivity());
                            }

                            @Override
                            public void failure(RetrofitError error) {

                                Util.dismissProgressDailog();
                                Log.e(TAG, error.toString());
                                Util.showToast(getActivity(), getString(R.string.lbl_retrofit_error));
                            }
                        });
            }
        }
    };
}
