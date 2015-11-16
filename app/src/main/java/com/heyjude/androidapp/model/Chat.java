package com.heyjude.androidapp.model;

/**
 * Created by dipen on 16/7/15.
 */
public class Chat {

    private String msg;
    private String date;
    private String latitude;
    private String longitude;
    private String mapImageUrl;

    private String flag;

    public Chat(String msg, String date, String flag) {
        this.msg = msg;
        this.flag = flag;
        this.date = date;

    }

    public Chat(String msg, String flag) {
        this.msg = msg;
        this.flag = flag;

    }

    public Chat(String msg, String latitude, String longitude, String mapImageUrl, String date, String flag) {

        this.msg = msg;
        this.flag = flag;
        this.latitude = latitude;
        this.longitude = longitude;
        this.mapImageUrl = mapImageUrl;
        this.date = date;
    }


    public String getMsg() {
        return msg;
    }

    public String getViewType() {
        return flag;
    }

    public String getDate() {
        return date;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getMapImageUrl() {
        return mapImageUrl;
    }
}
