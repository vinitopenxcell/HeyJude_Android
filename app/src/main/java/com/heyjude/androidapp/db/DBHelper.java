package com.heyjude.androidapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.heyjude.androidapp.constant.ChatConstants;
import com.heyjude.androidapp.model.VendorList;
import com.heyjude.androidapp.requestmodel.LoginData;
import com.heyjude.androidapp.utility.Global;
import com.heyjude.androidapp.xmpp.ChatLog;
import com.heyjude.androidapp.xmpp.ChatMessage;

import java.util.ArrayList;


public class DBHelper {

    private static DBHelper dbHelper;
    private SQLiteDatabase db;
    String count;

    private static SharedPreferences sharedPreferences;
    private static LoginData loginData;

    private DBHelper(Context context) {
        db = new SQLHelper(context).getWritableDatabase();
    }

    public static DBHelper getInstance(Context context) {
        if (dbHelper == null)
            dbHelper = new DBHelper(context);

        Gson gson = new Gson();
        sharedPreferences = context.getSharedPreferences(Global.PREF_NAME, Context.MODE_PRIVATE);
        loginData = gson.fromJson(sharedPreferences.getString(Global.PREF_RESPONSE_OBJECT, ""), LoginData.class);

        return dbHelper;
    }

    /**
     * Fetch message history of specific user.
     *
     * @param receiverId
     * @param user_id
     * @return
     */
    // ============MESSAGES============
    public ArrayList<ChatMessage> getAllRecords(String receiverId, String user_id, String task_id) {
        String sql = "";

        sql = "SELECT * FROM messages WHERE " +
                DBMessages.TO + " ='" + receiverId + "' AND " +
                DBMessages.TASK_ID + " ='" + task_id + "' AND " +
                DBMessages.FROM + " ='" + user_id + "' ";
        //sql = "SELECT * FROM messages WHERE " + DBMessages.SENDER_ID + " ='" + user_id + "' ";
        Cursor cur = db.rawQuery(sql, null);
        ArrayList<ChatMessage> listChatMessage = new ArrayList<>();
        if (cur != null && cur.moveToFirst()) {
            do {
                ChatMessage msg = new ChatMessage();
                msg._id = cur.getString(cur.getColumnIndex(DBMessages._ID));
                msg.msg_id = cur.getString(cur.getColumnIndex(DBMessages.MSG_ID));
                msg.taskid = cur.getString(cur.getColumnIndex(DBMessages.TASK_ID));
                msg.from = cur.getString(cur.getColumnIndex(DBMessages.FROM));
                msg.sender_name = cur.getString(cur.getColumnIndex(DBMessages.SENDER_NAME));
                msg.to = cur.getString(cur.getColumnIndex(DBMessages.TO));
                msg.chatmsg = cur.getString(cur.getColumnIndex(DBMessages.MSG_TEXT));
                msg.imageurl = cur.getString(cur.getColumnIndex(DBMessages.MAP_URL));
                msg.lat = cur.getString(cur.getColumnIndex(DBMessages.LATITUDE));
                msg.lon = cur.getString(cur.getColumnIndex(DBMessages.LONGITUDE));
                msg.destination = cur.getString(cur.getColumnIndex(DBMessages.DESTINATION));
                msg.amt = cur.getString(cur.getColumnIndex(DBMessages.AMOUNT));
                msg.details = cur.getString(cur.getColumnIndex(DBMessages.DETAILS));
                msg.supplier = cur.getString(cur.getColumnIndex(DBMessages.SUPPLIER));
                msg.reference = cur.getString(cur.getColumnIndex(DBMessages.REFERENCE));
                msg.orderno = cur.getString(cur.getColumnIndex(DBMessages.ORDER_NO));
                msg.flag = cur.getString(cur.getColumnIndex(DBMessages.MSG_TYPE));
                msg.showutcdate = cur.getString(cur.getColumnIndex(DBMessages.MSG_DATE));
                msg.newtimestamp = cur.getString(cur.getColumnIndex(DBMessages.NEW_TIMESTAMP));
                msg.status = cur.getString(cur.getColumnIndex(DBMessages.STATUS));
                msg.isdeliver = cur.getString(cur.getColumnIndex(DBMessages.ISDELIVER));
                msg.isread = cur.getString(cur.getColumnIndex(DBMessages.ISREAD));
                msg.user_id = cur.getString(cur.getColumnIndex(DBMessages.USER_ID));

                if (cur.getString(cur.getColumnIndex(DBMessages.VENDOR_IDS)) != null) {
                    msg.venodr_list = getVendorList(msg.taskid, cur.getString(cur.getColumnIndex(DBMessages.VENDOR_IDS)));
                }

                listChatMessage.add(msg);
            } while (cur.moveToNext());
        }
        return listChatMessage;
    }


    /**
     * Add entry in database
     *
     * @param msg
     */
    public void add(ChatMessage msg) {
        ContentValues values = new ContentValues();

        values.put(DBMessages.MSG_ID, msg.msg_id);
        values.put(DBMessages.TASK_ID, msg.taskid);
        values.put(DBMessages.FROM, msg.from);
        values.put(DBMessages.SENDER_NAME, msg.sender_name);
        values.put(DBMessages.TO, msg.to);
        values.put(DBMessages.MSG_TEXT, msg.chatmsg);
        values.put(DBMessages.MAP_URL, msg.imageurl);
        values.put(DBMessages.LATITUDE, msg.lat);
        values.put(DBMessages.LONGITUDE, msg.lon);
        values.put(DBMessages.DESTINATION, msg.destination);
        values.put(DBMessages.AMOUNT, msg.amt);
        values.put(DBMessages.DETAILS, msg.details);
        values.put(DBMessages.SUPPLIER, msg.supplier);
        values.put(DBMessages.REFERENCE, msg.reference);
        values.put(DBMessages.ORDER_NO, msg.orderno);
        values.put(DBMessages.MSG_TYPE, msg.flag);
        values.put(DBMessages.MSG_DATE, msg.showutcdate);
        values.put(DBMessages.NEW_TIMESTAMP, msg.newtimestamp);
        values.put(DBMessages.STATUS, msg.status);
        values.put(DBMessages.ISDELIVER, msg.isdeliver);
        values.put(DBMessages.ISREAD, msg.isread);
        values.put(DBMessages.USER_ID, msg.user_id);
        values.put(DBMessages.ISPUSH, msg.ispush);

        /**
         * If Flag is 4(Means Vendor List as Message) Then Add those data in Vendor List Also
         * For Vendor Table Entry
         */
        if (msg.flag.equals("4")) {

            String vendoriDs = insertVendorList(msg.taskid, msg.venodr_list);
            values.put(DBMessages.VENDOR_IDS, vendoriDs);
        }

        long result = db.insert(DBMessages.TABLE_MESSAGES, null, values);
        ChatLog.e("INSERT RESULT:" + result);

    }

    private String insertVendorList(String taskId, ArrayList<VendorList> vendorList) {
        StringBuilder vendorIds = new StringBuilder();
        int i = 0;
        for (VendorList vendor : vendorList) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBMessages.TASK_ID, taskId);
            contentValues.put(DBMessages.VENDOR_ID, vendor.vendor_id);
            contentValues.put(DBMessages.VENDOR_NAME, vendor.vendor_name);
            contentValues.put(DBMessages.VENDOR_CONTENT, vendor.vendor_content);
            contentValues.put(DBMessages.VENDOR_STAR, vendor.vendor_star);
            contentValues.put(DBMessages.VENDOR_ADDRESS, vendor.vendor_address);
            contentValues.put(DBMessages.VENDOR_COMMNETS, vendor.vendor_comments);
            contentValues.put(DBMessages.VENDOR_DISTANCE, vendor.vendor_distance);
            contentValues.put(DBMessages.VENDOR_MOBILE, vendor.mobile);
            contentValues.put(DBMessages.VENDOR_LATITUDE, vendor.lat);
            contentValues.put(DBMessages.VENDOR_LONGITUDE, vendor.lon);
            contentValues.put(DBMessages.JUDE_SAYS, vendor.judesays);

            long result = db.insert(DBMessages.TABLE_VENDOR, null, contentValues);
            if (i == 0)
                vendorIds.append(result);
            else
                vendorIds.append("," + result);
            ++i;
        }
        ChatLog.e("INSERT RESULT:" + vendorIds);
        return vendorIds.toString();
    }

    private ArrayList<VendorList> getVendorList(String taskId, String vendorIds) {
        ArrayList<VendorList> vendorList = null;
        String query = "SELECT * FROM " + DBMessages.TABLE_VENDOR + " WHERE " +
                DBMessages._ID + " IN (" + vendorIds + ") and " + DBMessages.TASK_ID + " = '" + taskId + "'";
        Cursor cur = db.rawQuery(query, null);
        //VendorList vendor = new VendorList();
        if (cur != null && cur.moveToFirst()) {
            vendorList = new ArrayList<>();
            do {
                VendorList vendor = new VendorList();
                vendor.vendor_id = cur.getString(cur.getColumnIndex(DBMessages.VENDOR_ID));
                vendor.vendor_name = cur.getString(cur.getColumnIndex(DBMessages.VENDOR_NAME));
                vendor.vendor_content = cur.getString(cur.getColumnIndex(DBMessages.VENDOR_CONTENT));
                vendor.vendor_star = cur.getString(cur.getColumnIndex(DBMessages.VENDOR_STAR));
                vendor.vendor_address = cur.getString(cur.getColumnIndex(DBMessages.VENDOR_ADDRESS));
                vendor.vendor_comments = cur.getString(cur.getColumnIndex(DBMessages.VENDOR_COMMNETS));
                vendor.vendor_distance = cur.getString(cur.getColumnIndex(DBMessages.VENDOR_DISTANCE));
                vendor.mobile = cur.getString(cur.getColumnIndex(DBMessages.VENDOR_MOBILE));
                vendor.lat = cur.getString(cur.getColumnIndex(DBMessages.VENDOR_LATITUDE));
                vendor.lon = cur.getString(cur.getColumnIndex(DBMessages.VENDOR_LONGITUDE));
                vendor.judesays = cur.getString(cur.getColumnIndex(DBMessages.JUDE_SAYS));

                vendorList.add(vendor);

            } while (cur.moveToNext());

            cur.close();
        }

        return vendorList;

    }


    /**
     * Update specific entry in database.
     *
     * @param msg
     */
    public void update(ChatMessage msg) {

        ContentValues values = new ContentValues();
        values.put(DBMessages.MSG_ID, msg.msg_id);
        values.put(DBMessages.TASK_ID, msg.taskid);
        values.put(DBMessages.FROM, msg.from);
        values.put(DBMessages.SENDER_NAME, loginData.getData().getUserName());
        values.put(DBMessages.TO, msg.to);
        values.put(DBMessages.MSG_TEXT, msg.chatmsg);
        values.put(DBMessages.MAP_URL, msg.imageurl);
        values.put(DBMessages.LATITUDE, msg.lat);
        values.put(DBMessages.LONGITUDE, msg.lon);
        values.put(DBMessages.DESTINATION, msg.destination);
        values.put(DBMessages.AMOUNT, msg.amt);
        values.put(DBMessages.DETAILS, msg.details);
        values.put(DBMessages.SUPPLIER, msg.supplier);
        values.put(DBMessages.REFERENCE, msg.reference);
        values.put(DBMessages.ORDER_NO, msg.orderno);
        values.put(DBMessages.MSG_TYPE, msg.flag);
        values.put(DBMessages.MSG_DATE, msg.showutcdate);
        values.put(DBMessages.NEW_TIMESTAMP, msg.newtimestamp);
        values.put(DBMessages.STATUS, msg.status);
        values.put(DBMessages.ISDELIVER, msg.isdeliver);
        values.put(DBMessages.ISREAD, msg.isread);
        values.put(DBMessages.USER_ID, msg.user_id);
        values.put(DBMessages.ISPUSH, msg.ispush);

        if (!TextUtils.isEmpty(msg._id)) {
            db.update(DBMessages.TABLE_MESSAGES, values, DBMessages._ID + "='" + msg._id + "' COLLATE NOCASE", null);
        } else if (!TextUtils.isEmpty(msg.newtimestamp)) {
            db.update(DBMessages.TABLE_MESSAGES, values, DBMessages.NEW_TIMESTAMP + "='" + msg.newtimestamp + "' COLLATE NOCASE", null);
        } else {
            db.update(DBMessages.TABLE_MESSAGES, values, DBMessages.MSG_ID + "='" + msg.msg_id + "' COLLATE NOCASE", null);
        }


    }


    /**
     * Update the status based on callback of message send (status: pending, process and sent)
     *
     * @param id
     * @param status
     */
    public void updateStatus(String id, String status) {
        ContentValues values = new ContentValues();
        values.put(DBMessages.STATUS, status);
        db.update(DBMessages.TABLE_MESSAGES, values, DBMessages.MSG_ID + "='" + id + "' COLLATE NOCASE", null);
    }

    /**
     * Update the status based on callback of message send (status: pending, process and sent)
     *
     * @param receiver_id
     * @param task_id
     */
    public void updateUnAssignTask(String task_id, String receiver_id) {
        ContentValues values = new ContentValues();
        values.put(DBMessages.TO, receiver_id);
        db.update(DBMessages.TABLE_MESSAGES, values, DBMessages.TASK_ID + "='" + task_id + "' COLLATE NOCASE", null);
    }


    /**
     * get all the pending messages which you send in offline mode (no internet) and send
     * it again when internet is active again.
     *
     * @return
     */
    public ArrayList<ChatMessage> getAllPendingMessage() {

        String sql = "SELECT * FROM " + DBMessages.TABLE_MESSAGES + " WHERE " +
                DBMessages.STATUS + " = '" + ChatConstants.STATUS_TYPE_PENDING + "' COLLATE NOCASE " + " OR " + DBMessages.STATUS + " = '" + ChatConstants.STATUS_TYPE_PROCESS + "' COLLATE NOCASE " + " ORDER BY " + DBMessages._ID;

        Cursor cur = db.rawQuery(sql, null);

        ArrayList<ChatMessage> listChatMessage = new ArrayList<>();
        if (cur != null && cur.moveToFirst()) {

            do {

                ChatMessage msg = new ChatMessage();
                msg._id = cur.getString(cur.getColumnIndex(DBMessages._ID));
                msg.msg_id = cur.getString(cur.getColumnIndex(DBMessages.MSG_ID));
                msg.taskid = cur.getString(cur.getColumnIndex(DBMessages.TASK_ID));
                msg.from = cur.getString(cur.getColumnIndex(DBMessages.FROM));
                msg.sender_name = cur.getString(cur.getColumnIndex(DBMessages.SENDER_NAME));
                msg.to = cur.getString(cur.getColumnIndex(DBMessages.TO));
                msg.chatmsg = cur.getString(cur.getColumnIndex(DBMessages.MSG_TEXT));
                msg.imageurl = cur.getString(cur.getColumnIndex(DBMessages.MAP_URL));
                msg.lat = cur.getString(cur.getColumnIndex(DBMessages.LATITUDE));
                msg.lon = cur.getString(cur.getColumnIndex(DBMessages.LONGITUDE));
                msg.destination = cur.getString(cur.getColumnIndex(DBMessages.DESTINATION));
                msg.amt = cur.getString(cur.getColumnIndex(DBMessages.AMOUNT));
                msg.details = cur.getString(cur.getColumnIndex(DBMessages.DETAILS));
                msg.supplier = cur.getString(cur.getColumnIndex(DBMessages.SUPPLIER));
                msg.reference = cur.getString(cur.getColumnIndex(DBMessages.REFERENCE));
                msg.orderno = cur.getString(cur.getColumnIndex(DBMessages.ORDER_NO));
                msg.flag = cur.getString(cur.getColumnIndex(DBMessages.MSG_TYPE));
                msg.showutcdate = cur.getString(cur.getColumnIndex(DBMessages.MSG_DATE));
                msg.newtimestamp = cur.getString(cur.getColumnIndex(DBMessages.NEW_TIMESTAMP));
                msg.status = cur.getString(cur.getColumnIndex(DBMessages.STATUS));
                msg.isdeliver = cur.getString(cur.getColumnIndex(DBMessages.ISDELIVER));
                msg.isread = cur.getString(cur.getColumnIndex(DBMessages.ISREAD));
                msg.user_id = cur.getString(cur.getColumnIndex(DBMessages.USER_ID));

                listChatMessage.add(msg);
            } while (cur.moveToNext());

        }
        return listChatMessage;
    }


    /**
     * to delete the database, when user logout from the app.
     *
     * @param tableName
     */
    public void deleteDatabase(String tableName) {
        // TODO Auto-generated method stub
        String sql = "DELETE FROM " + tableName;
        db.execSQL(sql);
        Log.e(">>>>>>>>>", "Record Deleted");
    }


    /**
     * Update database when you read the unread messages, set as true(means: read)
     *
     * @param taskid
     * @param status
     */
    public void updateISReadStatus(String taskid, String status) {
        ContentValues values = new ContentValues();
        values.put(DBMessages.ISREAD, status);

        db.update(DBMessages.TABLE_MESSAGES, values, DBMessages.TASK_ID + "='" + taskid + "' COLLATE NOCASE", null);

    }

    public void updateIsDeliever(String id, String status) {
        ContentValues values = new ContentValues();
        values.put(DBMessages.ISDELIVER, status);
        db.update(DBMessages.TABLE_MESSAGES, values, DBMessages.MSG_ID + "='" + id + "' COLLATE NOCASE", null);
    }


    /**
     * It will return you that how many message has been read and unread. (database column ISREAD)
     *
     * @param taskid
     * @param status
     * @return
     */
    public String getUnReadCount(String taskid, String status) {
        String sql = "";
        sql = "SELECT count(*) FROM " + DBMessages.TABLE_MESSAGES +
                " WHERE " + DBMessages.TASK_ID + "='" + taskid + "' AND " + DBMessages.ISREAD + "='" + status + "'";

        //SELECT count(*) FROM messages WHERE taskid=6800 AND isRead="false";


        Cursor cur = db.rawQuery(sql, null);
        if (cur != null && cur.moveToFirst()) {
            count = cur.getString(0);
            return count;
        }
        return "";
    }


    /**
     * It will return counter that how much messages you received as push notification.
     *
     * @param userid
     * @param Ispush
     * @return
     */
    public int getPushCount(String userid, String Ispush) {
        String sql = "";
        sql = "SELECT count(*) FROM " + DBMessages.TABLE_MESSAGES + " WHERE " + DBMessages.USER_ID + "='" + userid + "' AND " + DBMessages.ISPUSH + "='" + Ispush + "' ";
        int count;
        Cursor cur = db.rawQuery(sql, null);
        if (cur != null && cur.moveToFirst()) {
            count = cur.getInt(0);
            return count;
        }
        return 0;
    }


    /**
     * Chexck whether received message is already exists or not.
     *
     * @param newtimestamp
     * @return
     */
    public boolean IsMessageExists(String newtimestamp) {
        String sql = "SELECT * FROM " + DBMessages.TABLE_MESSAGES + " WHERE " + DBMessages.NEW_TIMESTAMP + " ='" + newtimestamp + "' COLLATE NOCASE ";

        Cursor cur = db.rawQuery(sql, null);
        if (cur != null && cur.moveToFirst()) {
            if (cur.getCount() > 0) {
                return true;
            }

        }
        return false;
    }
}

