package com.heyjude.androidapp.xmpp;

import android.os.Parcel;
import android.os.Parcelable;

import com.heyjude.androidapp.model.VendorList;

import java.util.ArrayList;

public class ChatMessage implements Parcelable {

    public String _id; //  DEFAULT ID WHICH WILL AUTO GENERATE.
    public String msg_id; // ID OF THE MESSAGE
    public String taskid; // ID OF THE TASK
    public String from;// ID OF THE  PERSON WHO WILL SEND MESSAGE.
    public String sender_name; // NAME OF PERSON WHO SEND MESSAGE
    public String to; //ID OF THE PERSON WHO WILL RECEIVE THE MESSAGE. // IN CASE OF GROUP CHAT RECEIVER ID WILL STORE  USER PROFILE PATH.
    public String chatmsg; // MESSAGE TEXT
    public String vendor_ids; // NO OF VENDOR LIST
    public String imageurl; // MAP IMAGE URL
    public String lat; // Along with Map lat lon is comming with it.
    public String lon; // Along with Map lat lon is comming with it.
    public String destination;
    public String amt; // amt.
    public String details;
    public String supplier;
    public String reference;
    public String orderno;
    public String flag = ""; // TYPE OF MESSAGE (TEXT/IMAGE/LOCATION ETC..)
    public String showutcdate = ""; // MESSAGE DATE
    public String newtimestamp;
    public String status;   // message status (pending, process and sent)
    public String isdeliver; // BOOLEAN TO TRACE WHETHER MESSAGE DELIVER OR NOT
    public String isread; // BOOLEAN TO CHECK WHETHER SENT MESSAGE IS READ OR NOT
    public String user_id; // logged user id
    public String ispush; // true, if it is push notification.

    public ArrayList<VendorList> venodr_list = new ArrayList<>();


    public ChatMessage() {

    }

    ChatMessage(Parcel source) {
        readFromParcel(source);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(_id);
        dest.writeString(msg_id);
        dest.writeString(taskid);
        dest.writeString(from);
        dest.writeString(sender_name);
        dest.writeString(to);
        dest.writeString(chatmsg);
        dest.writeString(vendor_ids);
        dest.writeString(imageurl);
        dest.writeString(lat);
        dest.writeString(lon);
        dest.writeString(destination);
        dest.writeString(amt);
        dest.writeString(details);
        dest.writeString(supplier);
        dest.writeString(reference);
        dest.writeString(orderno);
        dest.writeString(flag);
        dest.writeString(showutcdate);
        dest.writeString(newtimestamp);
        dest.writeString(status);
        dest.writeString(isdeliver);
        dest.writeString(isread);
        dest.writeString(user_id);
        dest.writeString(ispush);

        dest.writeTypedList(venodr_list);

    }

    private void readFromParcel(Parcel parcel) {
        _id = parcel.readString();
        msg_id = parcel.readString();
        taskid = parcel.readString();
        from = parcel.readString();
        sender_name = parcel.readString();
        to = parcel.readString();
        chatmsg = parcel.readString();
        vendor_ids = parcel.readString();
        imageurl = parcel.readString();
        lat = parcel.readString();
        lon = parcel.readString();
        destination = parcel.readString();
        amt = parcel.readString();
        details = parcel.readString();
        supplier = parcel.readString();
        reference = parcel.readString();
        orderno = parcel.readString();
        flag = parcel.readString();
        showutcdate = parcel.readString();
        newtimestamp = parcel.readString();
        status = parcel.readString();
        isdeliver = parcel.readString();
        isread = parcel.readString();
        user_id = parcel.readString();
        ispush = parcel.readString();

        parcel.readTypedList(venodr_list, VendorList.CREATOR);

    }

    // Method to recreate a Question from a Parcel
    public static Creator<ChatMessage> CREATOR = new Creator<ChatMessage>() {

        @Override
        public ChatMessage createFromParcel(Parcel source) {
            return new ChatMessage(source);
        }

        @Override
        public ChatMessage[] newArray(int size) {
            return new ChatMessage[size];
        }

    };

    @Override
    public int describeContents() {
        return 0;
    }

}
