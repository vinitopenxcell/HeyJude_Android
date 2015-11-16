package com.heyjude.androidapp.utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.location.LocationManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.heyjude.androidapp.R;

/**
 * Created by aalap on 21/5/15.
 */
public class Util {

    public static String latitude = null;
    public static String longitude = null;

    public static void HideKeyBoard(Activity mActivity) {

        InputMethodManager inputManager = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View view = mActivity.getCurrentFocus();
        if (view == null)
            return;

        inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);


    }

    public static void getCurrentLocation(Activity mActivity) {
        // check if GPS enabled
        GPSTracker gpsTracker = new GPSTracker(mActivity);

        LocationManager locationManager = (LocationManager) mActivity.getSystemService(mActivity.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            latitude = String.valueOf(gpsTracker.latitude);
            longitude = String.valueOf(gpsTracker.longitude);

            Log.e("Location2:", "" + latitude + " " + longitude);
        } else {
            gpsTracker.showSettingsAlert(mActivity);
        }
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    private static ProgressDialog pDialog;

    public static void showProgressDailog(Context context) {
        pDialog = new ProgressDialog(context, R.style.DialogTheme);
        pDialog.setMessage(context.getResources().getString(R.string.lbl_loading));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    public static void dismissProgressDailog() {

        if (pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

    public static void showAlertDialog(final Context context) {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context, R.style.DialogTheme);

        // Setting Dialog Title
        builder.setTitle("No Internet Connection");

        // Setting Dialog Message
        builder.setMessage("You don't have internet connection.");

        // Setting OK Button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        // Showing Alert Message
        builder.show();
    }


    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    /**
     * For Checking whether Google Play Services is On or Not.
     *
     * @return
     */
    public static boolean isGooglePlayServicesAvailable(Activity activity) {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, activity, 0).show();
            return false;
        }
    }
}
