package com.heyjude.androidapp.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListPopupWindow;

import com.google.gson.Gson;
import com.heyjude.androidapp.R;
import com.heyjude.androidapp.apirequest.RestClient;
import com.heyjude.androidapp.model.User;
import com.heyjude.androidapp.requestmodel.LoginData;
import com.heyjude.androidapp.utility.Constants;
import com.heyjude.androidapp.utility.Global;
import com.heyjude.androidapp.utility.HeyJudeApp;
import com.heyjude.androidapp.utility.Util;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

       /* For demo purpose only created a Account on Stripe
             Username: android.testapps@gmail.com
             Password: androidandroid*/

public class AddCardActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = "AddCardActivity";

    private EditText etCardNo1, etCardNo2, etCardNo3, etCardNo4, etCvv;
    private Button btnSaveCard, btnMonth, btnYear;
    private int year, amount;
    private SharedPreferences sharedPreferences;
    private LoginData loginData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        amount = Integer.parseInt(getIntent().getStringExtra(Constants.AMOUNT));

        setToolbar(AddCardActivity.this, "Payment");

        Gson gson = new Gson();
        sharedPreferences = getSharedPreferences(Global.PREF_NAME, Context.MODE_PRIVATE);
        loginData = gson.fromJson(sharedPreferences.getString(Global.PREF_RESPONSE_OBJECT, ""), LoginData.class);

        init();
    }

    private void init() {

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        etCardNo1 = (EditText) findViewById(R.id.etCardNo1);
        etCardNo2 = (EditText) findViewById(R.id.etCardNo2);
        etCardNo3 = (EditText) findViewById(R.id.etCardNo3);
        etCardNo4 = (EditText) findViewById(R.id.etCardNo4);

        btnSaveCard = (Button) findViewById(R.id.btnSaveCard);
        btnMonth = (Button) findViewById(R.id.btnMonth);
        btnYear = (Button) findViewById(R.id.btnYear);

        btnYear.setText(String.valueOf(year));

        etCvv = (EditText) findViewById(R.id.etCvv);

        btnSaveCard.setOnClickListener(this);
        btnMonth.setOnClickListener(this);
        btnYear.setOnClickListener(this);

        final StringBuilder sb = new StringBuilder();

        etCardNo1.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (sb.length() == 0 & etCardNo1.length() == 4) {
                    sb.append(s);
                    etCardNo1.clearFocus();
                    etCardNo2.requestFocus();
                    etCardNo2.setCursorVisible(true);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        etCardNo2.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (sb.length() == 4 & etCardNo2.length() == 4) {
                    sb.append(s);
                    etCardNo2.clearFocus();
                    etCardNo3.requestFocus();
                    etCardNo3.setCursorVisible(true);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {


            }
        });

        etCardNo3.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (sb.length() == 8 & etCardNo3.length() == 4) {
                    sb.append(s);
                    etCardNo2.clearFocus();
                    etCardNo4.requestFocus();
                    etCardNo4.setCursorVisible(true);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {

            }
        });

        etCardNo4.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (sb.length() == 12 & etCardNo4.length() == 4) {
                    sb.append(s);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btnMonth:
                MonthSelected(view);
                break;

            case R.id.btnYear:
                YearSelected(view);
                break;

            case R.id.btnSaveCard:

                if (HeyJudeApp.hasNetworkConnection()) {
                    Util.showProgressDailog(AddCardActivity.this);

                    String creditCardNo = etCardNo1.getText().toString() +
                            etCardNo2.getText().toString() +
                            etCardNo3.getText().toString() +
                            etCardNo4.getText().toString();

                    String cvvNo = etCvv.getText().toString();
                    int month = Integer.parseInt(btnMonth.getText().toString());
                    int year = Integer.parseInt(btnYear.getText().toString());

                    /**
                     * Pass you Credit Card No, Expiry Month, Expiry Year and CVV No.
                     */
                    Card card = new Card(creditCardNo, month, year, cvvNo);

                    try {
                        Stripe stripe = new Stripe(Constants.STRIPE_PUBLISHABLE_KEY);

                        stripe.createToken(
                                card,
                                new TokenCallback() {
                                    public void onSuccess(Token token) {

                                        String token_id = token.getId();
                                        Log.e(TAG, token_id);
                                    /*From Here you need to call webservice by passing this TokenID as a parameter*/

                                        RestClient.getInstance().getApiService().stripePayment(
                                                token_id,
                                                loginData.getData().getId(),
                                                amount,
                                                new Callback<User>() {
                                                    @Override
                                                    public void success(User user, Response response) {
                                                        Util.dismissProgressDailog();
                                                        Log.e(TAG, "SUCCESS");

                                                        final AlertDialog.Builder builder = new AlertDialog.Builder(AddCardActivity.this);
                                                        builder.setTitle(user.getStatus());
                                                        builder.setMessage(user.getMessage());
                                                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                finish();
                                                            }
                                                        });
                                                        builder.show();
                                                    }

                                                    @Override
                                                    public void failure(RetrofitError error) {
                                                        Util.dismissProgressDailog();
                                                        Util.showToast(AddCardActivity.this, getString(R.string.lbl_retrofit_error));
                                                        Log.e(TAG, "FAIL " + error.getMessage().toString());
                                                    }
                                                }
                                        );

                                    }

                                    public void onError(Exception error) {

                                        Util.dismissProgressDailog();

                                        final AlertDialog.Builder builder = new AlertDialog.Builder(AddCardActivity.this);
                                        builder.setTitle("Error");
                                        builder.setMessage(error.getMessage());
                                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        });
                                        builder.show();
                                        Log.e("Retrofit Error", getString(R.string.lbl_retrofit_error));
                                    }
                                }
                        );
                    } catch (com.stripe.exception.AuthenticationException e) {
                        Util.showToast(AddCardActivity.this, e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    Util.showAlertDialog(AddCardActivity.this);
                }


                break;
        }
    }

    public void MonthSelected(View anchor) {
        final ListPopupWindow lpw = new ListPopupWindow(this);
        final String[] data = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
        lpw.setAdapter(new ArrayAdapter(
                AddCardActivity.this, R.layout.row_dropdown, data));

        //setting up an anchor view
        lpw.setAnchorView(anchor);

        lpw.setHeight(400);
        lpw.setWidth(anchor.getRight() - anchor.getLeft());

        // Background is needed. You can use your own drawable or make a 9patch.
        // I'v used a custom btn drawable. looks nice.
        lpw.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.spinner_border));

        // Offset between anchor view and popupWindow
        lpw.setVerticalOffset(3);

        lpw.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                btnMonth.setText(data[position]);

                lpw.dismiss();

            }
        });
        lpw.show();

    }

    public void YearSelected(View anchor) {
        final ListPopupWindow lpw = new ListPopupWindow(this);
        final ArrayList<Integer> data = new ArrayList<>();

        int i = 0;
        while (i < 50) {
            data.add(year + i);
            i++;
        }

        lpw.setAdapter(new ArrayAdapter(AddCardActivity.this, R.layout.row_dropdown, data));

        //setting up an anchor view
        lpw.setAnchorView(anchor);

        lpw.setHeight(400);
        lpw.setWidth(anchor.getRight() - anchor.getLeft());

        // Background is needed. You can use your own drawable or make a 9patch.
        // I'v used a custom btn drawable. looks nice.
        lpw.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.spinner_border));

        // Offset between anchor view and popupWindow
        lpw.setVerticalOffset(3);

        lpw.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                btnYear.setText(data.get(position).toString());
                lpw.dismiss();
            }
        });
        lpw.show();
    }
}