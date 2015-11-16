package com.heyjude.androidapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dipen on 7/9/15.
 */
public class VendorList implements Parcelable {

    public String vendor_id;//Id of Perticular Vendor
    public String vendor_name;// Vendor Name
    public String vendor_content; // Content of Vendor
    public String vendor_star; //Rating for perticular Vendror
    public String vendor_address; //Vendor Address
    public String vendor_comments; // Comments for Vendor
    public String vendor_distance; // Distance of vendor from Current Location
    public String mobile; //Vendor's Mobile Number.
    public String flag; // TYPE OF MESSAGE (TEXT/IMAGE/LOCATION/VENDOR)
    public String showdate; // MESSAGE DATE
    public String lat;
    public String lon;
    public String judesays;

    public VendorList() {

    }

    VendorList(Parcel source) {
        readFromParcel(source);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(vendor_id);
        dest.writeString(vendor_name);
        dest.writeString(vendor_content);
        dest.writeString(vendor_star);
        dest.writeString(vendor_address);
        dest.writeString(vendor_comments);
        dest.writeString(vendor_distance);
        dest.writeString(mobile);
        dest.writeString(flag);
        dest.writeString(showdate);
        dest.writeString(lat);
        dest.writeString(lon);
        dest.writeString(judesays);

    }

    private void readFromParcel(Parcel parcel) {

        vendor_id = parcel.readString();
        vendor_name = parcel.readString();
        vendor_content = parcel.readString();
        vendor_content = parcel.readString();
        vendor_star = parcel.readString();
        vendor_address = parcel.readString();
        vendor_comments = parcel.readString();
        vendor_distance = parcel.readString();
        mobile = parcel.readString();
        flag = parcel.readString();
        showdate = parcel.readString();
        lat = parcel.readString();
        lon = parcel.readString();
        judesays = parcel.readString();

    }

    // Method to recreate a Question from a Parcel
    public static Creator<VendorList> CREATOR = new Creator<VendorList>() {

        @Override
        public VendorList createFromParcel(Parcel source) {
            return new VendorList(source);
        }

        @Override
        public VendorList[] newArray(int size) {
            return new VendorList[size];
        }

    };

    @Override
    public int describeContents() {
        return 0;
    }

}