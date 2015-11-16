package com.heyjude.androidapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.heyjude.androidapp.R;
import com.heyjude.androidapp.animation.AnimatorClass;
import com.heyjude.androidapp.apirequest.RestClient;
import com.heyjude.androidapp.requestmodel.LoginData;
import com.heyjude.androidapp.utility.Util;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class ForgetPasswordActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = "ForgetPasswordActivity";
    private EditText editEmailAddress;
    private Button btnGetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        setToolbar(this, getResources().getString(R.string.lbl_forgot_password));

        editEmailAddress = (EditText) findViewById(R.id.editEmailAddress);
        btnGetPassword = (Button) findViewById(R.id.btnGetPassword);
        btnGetPassword.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btnGetPassword:

                String email = editEmailAddress.getText().toString();

                if (isValidData(email)) {

                    Util.showProgressDailog(ForgetPasswordActivity.this);

                    if (getApplicationContext().hasNetworkConnection()) {
                        RestClient.getInstance().getApiService().forgotpassword(email, new Callback<LoginData>() {

                            @Override
                            public void success(LoginData loginData, Response response) {

                                Util.dismissProgressDailog();

                                if (loginData.getData().getStatus().equals(getResources().getString(R.string.response_fail))) {

                                    Util.showToast(ForgetPasswordActivity.this, getResources().getString(R.string.alert_emailid_not_exist));
                                    editEmailAddress.setText("");
                                    editEmailAddress.requestFocus();

                                }

                                if (loginData.getData().getStatus().equals(getResources().getString(R.string.response_success))) {

                                    Util.showToast(ForgetPasswordActivity.this, getResources().getString(R.string.alert_passworsd_sent_successfully));

                                    Util.HideKeyBoard(ForgetPasswordActivity.this);
                                    Intent intent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    AnimatorClass.appearLeftAnimation(ForgetPasswordActivity.this);
                                    finish();
                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {

                                Util.dismissProgressDailog();
                                Util.showToast(ForgetPasswordActivity.this, getString(R.string.lbl_retrofit_error));
                                Log.e(TAG, error.getMessage().toString());
                            }
                        });
                    } else {
                        Util.showAlertDialog(ForgetPasswordActivity.this);
                        Util.dismissProgressDailog();
                    }
                }

                break;
        }

    }

    private boolean isValidData(String email) {
        boolean isValid = true;

        if (TextUtils.isEmpty(email)) {
            editEmailAddress.setError(getResources().getString(R.string.alert_enter_email));
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmailAddress.setError(getResources().getString(R.string.alert_enter_valid_email));
            isValid = false;
        }
        return isValid;
    }
}
