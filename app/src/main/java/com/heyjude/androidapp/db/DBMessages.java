package com.heyjude.androidapp.db;


public class DBMessages {

    public static final String TABLE_MESSAGES = "messages";
    public static final String TABLE_VENDOR = "venodr_list";

    public static final String _ID = "_id"; // auto generated id (optional)
    public static final String MSG_ID = "msg_id"; // id of the message which we are generating from our side.
    public static final String TASK_ID = "taskid"; // id of the task
    public static final String FROM = "_from"; // myid
    public static final String TO = "_to"; // destination id (it will require in one to one chat.)
    public static final String MSG_TYPE = "flag"; // type of messsage (Text/ Image/location etc)
    public static final String SENDER_NAME = "sender_name"; // my name
    public static final String MSG_DATE = "showutcdate"; // date when i click on send
    public static final String NEW_TIMESTAMP = "newtimestamp"; // date when i click on send
    public static final String MSG_TEXT = "chatmsg"; // message or base 64 string
    public static final String VENDOR_IDS = "vendor_ids"; // No of Vendor List Comma Seprated
    public static final String STATUS = "status"; // pending, process, done
    public static final String ISDELIVER = "isDeliver";
    public static final String ISREAD = "isRead";
    public static final String ISPUSH = "isPush";  //it will be helpful in case of push notification. (boolean value)
    public static final String MAP_URL = "imageurl"; //Image URL of Map
    public static final String LATITUDE = "lat";//Latitude of Image
    public static final String LONGITUDE = "lon";//Longitude of Image
    public static final String DESTINATION = "destination";//Longitude of Image
    public static final String AMOUNT = "amt";//Amount sen
    public static final String DETAILS = "details";//Amount sen
    public static final String SUPPLIER = "supplier";//Amount sen
    public static final String REFERENCE = "reference";//Amount sen
    public static final String ORDER_NO = "orderno";//Amount sen


    public static final String VENDOR_ID = "vendor_id";
    public static final String VENDOR_NAME = "vendor_name";
    public static final String VENDOR_CONTENT = "vendor_content";
    public static final String VENDOR_STAR = "vendor_star";
    public static final String VENDOR_ADDRESS = "vendor_address";
    public static final String VENDOR_COMMNETS = "vendor_comments";
    public static final String VENDOR_DISTANCE = "vendor_distance";
    public static final String VENDOR_MOBILE = "mobile";
    public static final String VENDOR_LATITUDE = "lat";
    public static final String VENDOR_LONGITUDE = "lon";
    public static final String JUDE_SAYS = "judesays";


    /**
     * if you don't want to delete database on logout then it will help you next time in order to fetch
     * particular user chat based on user_id;
     * store your own ID in both case (send & receive)
     */
    public static final String USER_ID = "user_id";

    /**
     * Query to create database.
     */
    protected static final String CREATE_TABLE_MESSAGES = "CREATE TABLE " + TABLE_MESSAGES + " ( " +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            MSG_ID + " text, " +
            TASK_ID + " text, " +
            FROM + " text, " +
            TO + " text, " +
            SENDER_NAME + " text, " +
            MSG_TEXT + " text, " +
            VENDOR_IDS + " text, " +
            MAP_URL + "  text, " +
            LATITUDE + " text, " +
            LONGITUDE + " text, " +
            DESTINATION + " text, " +
            AMOUNT + " text, " +
            DETAILS + " text, " +
            SUPPLIER + " text, " +
            REFERENCE + " text, " +
            ORDER_NO + " text, " +
            MSG_TYPE + " text, " +
            MSG_DATE + " text, " +
            NEW_TIMESTAMP + " text, " +
            STATUS + " text, " +
            ISDELIVER + " text, " +
            ISREAD + " text, " +
            USER_ID + " text, " +
            ISPUSH + " text " +
            " ) ";

    protected static final String CREATE_TABLE_VENDOR = "CREATE TABLE " + TABLE_VENDOR + " ( " +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TASK_ID + " text, " +
            VENDOR_ID + " text, " +
            VENDOR_NAME + " text, " +
            VENDOR_CONTENT + " text, " +
            VENDOR_STAR + "  text, " +
            VENDOR_ADDRESS + " text, " +
            VENDOR_COMMNETS + " text, " +
            VENDOR_DISTANCE + " text, " +
            VENDOR_MOBILE + " text, " +
            VENDOR_LATITUDE + " text, " +
            VENDOR_LONGITUDE + " text, " +
            JUDE_SAYS + " text " +
            " ) ";
}