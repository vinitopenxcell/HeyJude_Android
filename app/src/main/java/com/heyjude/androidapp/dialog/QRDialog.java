package com.heyjude.androidapp.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.heyjude.androidapp.QRCode.Contents;
import com.heyjude.androidapp.QRCode.QRCodeEncoder;
import com.heyjude.androidapp.R;
import com.heyjude.androidapp.customview.CustomDialog;
import com.heyjude.androidapp.model.IssueCouponWiGroup;
import com.heyjude.androidapp.requestmodel.CouponData;
import com.heyjude.androidapp.utility.HeyJudeApp;
import com.heyjude.androidapp.utility.Util;
import com.heyjude.androidapp.wiGroupAPIRequest.WiGroupRestClient;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by dipen on 27/6/15.
 */
public class QRDialog extends CustomDialog {

    private Button btn_cancel;
    private ImageView iv_qr_scan;
    private TextView tv_qrCode;
    Activity mContext;
    String campignId;
    private ProgressBar progressBar;

    public QRDialog(Context context, String campignId) {
        super(context);

        mContext = (Activity) context;
        this.campignId = campignId;
        setContentView(R.layout.dialog_qr_scanner);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        iv_qr_scan = (ImageView) findViewById(R.id.iv_qr_scan);
        tv_qrCode = (TextView) findViewById(R.id.tv_qrcode);

        iv_qr_scan.setVisibility(View.GONE);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        getWiCode();
    }

    private void getWiCode() {

        if (HeyJudeApp.hasNetworkConnection()) {
            WiGroupRestClient.getInstance().getApiService().wiGroupCouponRequest(

                    new IssueCouponWiGroup(campignId, "42343425544234"),
                    new Callback<CouponData>() {
                        @Override
                        public void success(CouponData couponData, Response response) {

                            generateWiCode(couponData.getVouchers().getWiCode());
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.e("Error WiGroup", "Error WiGroup : " + error.toString());
                            Util.showToast(mContext, mContext.getString(R.string.lbl_retrofit_error));

                        }
                    });
        } else {
            Util.showToast(mContext, mContext.getString(R.string.alert_connection));
        }

    }

    public void ButtonClickListner(View.OnClickListener reedemClickListener) {
        btn_cancel.setOnClickListener(reedemClickListener);
    }

    private void generateWiCode(String wiCode) {

        StringBuilder s;
        s = new StringBuilder(wiCode);

        if (wiCode.length() == 9) {

            for (int i = 3; i < s.length(); i += 4) {
                s.insert(i, " ");
            }
            tv_qrCode.setText(s);

        } else if (wiCode.length() == 7) {

            for (int i = 0; i < 9; i++) {
                if (i == 2)
                    s.insert(i, " ");

                if (i == 5)
                    s.insert(i, " ");

            }
            tv_qrCode.setText(s);

        } else {
            tv_qrCode.setText(wiCode);
        }

        //Find screen size
        WindowManager manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        int smallerDimension = width < height ? width : height;
        smallerDimension = smallerDimension * 3 / 4;

        //Encode with a QR Code image
        QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(wiCode, null, Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString(), smallerDimension);
        try {
            Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
            iv_qr_scan.setVisibility(View.VISIBLE);
            iv_qr_scan.setImageBitmap(bitmap);
            progressBar.setVisibility(View.GONE);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    /*public static class QRCodeFormatText implements TextWatcher {

        private static final char space = ' ';

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            // Remove spacing char
            if (s.length() > 0 && (s.length() % 3) == 0) {
                final char c = s.charAt(s.length() - 1);
                if (space == c) {
                    s.delete(s.length() - 1, s.length());
                }
            }
            // Insert char where needed.
            if (s.length() > 0 && (s.length() % 3) == 0) {
                char c = s.charAt(s.length() - 1);
                // Only if its a digit where there should be a space we insert a space
                if (Character.isDigit(c) && TextUtils.split(s.toString(), String.valueOf(space)).length <= 2) {
                    s.insert(s.length() - 1, String.valueOf(space));
                }
            }
        }
    }*/
}