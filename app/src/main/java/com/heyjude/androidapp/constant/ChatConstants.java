package com.heyjude.androidapp.constant;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class ChatConstants {

    public static final int CHAT_TYPE_DATE = 0;
    public static final int CHAT_TYPE_TEXT_JUDE = 1;
    public static final int CHAT_TYPE_PAYMENT = 2;
    public static final int CHAT_TYPE_MAP = 3;
    public static final int CHAT_TYPE_DEALS = 4;
    public static final int CHAT_TYPE_ME_CHAT = 5;
    public static final int CHAT_TYPE_ME_RATING = 6;

    private static final String SERVER_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";


    public static String STATUS_TYPE_PENDING = "pending";
    public static String STATUS_TYPE_PROCESS = "process";
    public static String STATUS_TYPE_READ = "true";
    public static String STATUS_TYPE_SENT = "sent";
    public static String STATUS_TYPE_DELIVER = "deliver";
    public static String STATUS_TYPE_DOWNLOAD_PROCESS = "download_process";

    public static final String ALERT = "alert";
    public static final String BODY = "body";
    public static final String USER_ID = "user_id";
    public static final String FLAG = "flag";

    public static final String FLAG_ASSIGNED_TASK = "0";
    public static final String FLAG_TEXT_MESSAGE = "1";
    public static final String FLAG_PAYMENT_MESSAGE = "2";
    public static final String FLAG_MAP_MESSAGE = "3";
    public static final String FLAG_VENDOR_MESSAGE = "4";

    public static final String TASK_ID = "taskid";
    public static final String CHAT_MESSAGE = "chatmsg";
    public static final String DATE = "showutcdate";
    public static final String TIMESTAMP = "newtimestamp";
    public static final String REFERENCE = "reference";
    public static final String AMOUNT = "amount";
    public static final String ORDER_NO = "orderno";
    public static final String SUPPLIER = "supplier";
    public static final String DETAILS = "details";
    public static final String IMAGE_URL = "imageurl";
    public static final String LATITUDE = "lat";
    public static final String LONGITUDE = "lon";
    public static final String FALSE = "false";
    public static final String VENDOR_LIST = "venodr_list";
    public static final String JUDE_ID = "agentid";

    public static final String VENDOR_COMMENT = "vendor_comments";
    public static final String VENDOR_DISTANCE = "vendor_distance";
    public static final String VENDOR_ID = "vendor_id";
    public static final String VENDOR_STAR = "vendor_star";
    public static final String MOBILE = "mobile";
    public static final String VENDOR_NAME = "vendor_name";
    public static final String JUDESAYS = "judesays";
    public static final String VENDOR_CONTENT = "vendor_content";
    public static final String VENDOR_ADDRESS = "vendor_address";


    public static boolean hasNetworkConnection(Context mContext) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public static String getCurrentUTCDate() {
        try {

            Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            //String dt=DateFormat.format("yyyy-MM-dd hh:mm:ss", c).toString();

			/*SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss z");
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
			Date date = df.parse(dt);
			df.setTimeZone(TimeZone.getDefault());
			String formattedDate = df.format(date);

			return formattedDate;*/
            return DateFormat.format("yyyy-MM-dd kk:mm:ss", c).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    // ======DATE SETTINGS=====
    public static String getFormatedDate(Calendar calendar, String format, boolean isConvertToUTC) {

        if (isConvertToUTC)
            calendar.setTimeZone(TimeZone.getTimeZone("UTC"));

        if (calendar != null) {
            return DateFormat.format(format, calendar).toString();
        }
        return "";
    }

    public static String getDateForDisplay(Calendar calendar) {

        if (calendar != null) {
            return DateFormat.format("dd MMMM yyyy", calendar).toString();
        }
        return "";
    }

    public static String getDateForDisplayInLabel1(Calendar calendar) {

        if (calendar != null) {
            return DateFormat.format("dd MMMM, HH:mm a", calendar).toString();
        }
        return "";
    }

    public static Calendar getDate(String date, boolean isThisTimeUTC, boolean isReturnTimeZoneLocal) {

        try {
            if (!TextUtils.isEmpty(date)) {
                Calendar c;

                if (isThisTimeUTC) {
                    c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                } else {
                    c = Calendar.getInstance(TimeZone.getDefault());
                }

                SimpleDateFormat sdf;
                sdf = new SimpleDateFormat(SERVER_DATETIME_FORMAT);

                if (isThisTimeUTC) {
                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                } else {
                    sdf.setTimeZone(TimeZone.getDefault());
                }

                Date dt = sdf.parse(date);

                if (isReturnTimeZoneLocal) {
                    c.setTimeZone(TimeZone.getDefault());
                    sdf.setTimeZone(TimeZone.getDefault());

                } else {
                    c.setTimeZone(TimeZone.getTimeZone("UTC"));
                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                }
                c.setTime(dt);
                return c;
            }
        } catch (Exception e) {
            Log.e("Date Exception", e.getMessage());
        }
        return null;
    }

    public static String CommonDateConvertor(String datetime, String currentformat, String requireformat) {
        String _StrDT = "";
        try {
            SimpleDateFormat f1 = new SimpleDateFormat(currentformat);
            Date d = f1.parse(datetime);
            SimpleDateFormat f2 = new SimpleDateFormat(requireformat);
            _StrDT = f2.format(d); // "12:18am"
        } catch (Exception e) {
            e.printStackTrace();
        }

        return _StrDT;
    }

}
