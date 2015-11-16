package com.heyjude.androidapp.apirequest;

/**
 * Created by aalap on 5/5/15.
 */
public class Request {

    public static final String SUCCESS = "Success";
    public static final String STATIC_KEY = "static_key";
    public static final String SESSION_HEADER = "session-key";

    public static final String KEY = "1QiFE2ywtnDl4rdXzQ_9OkgLJJFXrCQxAHLS3Sg7LSGk";
    //public static final String BASE_URL = "http://heyjude.openxcelltechnolabs.info";
    public static final String BASE_URL = "http://heyjudeapp.com";
    //http://heyjude.openxcelltechnolabs.info/api/get_session_key.php

    //api Urls
    public static final String GET_KEY = "/api/get_session_key.php";
    public static final String LOGIN_URL = "/api/user_login.php";
    public static final String REGISTRATION_URL = "/api/user_registration.php";
    public static final String FACEBOOK_URL = "/api/user_login_with_fb.php";
    public static final String TWITTER_URL = "/api/user_login_with_tw.php";
    public static final String LOGOUT_URL = "/api/user_logout.php";
    public static final String FORGOTPASS_URL = "/api/forgot_password.php";
    public static final String CHANGE_PASSWORD = "/api/change_password.php";
    public static final String UPDATE_PROFILE = "/api/update_user_detail.php";
    public static final String CREATE_TASK = "/api/add_new_request.php";
    public static final String STRIPE_PAYMENT = "/api/stripe_payment_integration.php";
    public static final String CURRENT_TASK = "/api/getUserCurrentRequests.php";
    public static final String COMPLETED_TASK = "/api/getUserCompletedRequests.php";
    public static final String CHAT_HISTORY_FROM_SERVER = "/api/chatHistory.php";
    public static final String TERMS_AND_CONDITION = "http://heyjudeapp.com/terms-and-conditions/";
    public static final String PRIVACY_POLICY = "http://heyjudeapp.com/privacy-policy/";
    public static final String ACCEPT_QUOTE = "/api/user_accept_quotes.php";

    public static final String FIELD_FACEBOOKID = "fbid";
    public static final String FIELD_USERID = "user_id";
    public static final String FIELD_TWITTERID = "twid";
    public static final String FIELD_TWITTER_SCREEN_NAME = "screen_name";
    public static final String FIELD_USERNAME = "name";
    public static final String FIELD_NAME = "user_name";//For Facebook
    public static final String FIELD_EMAILID = "emaiid";//In Login
    public static final String FIELD_EMAIL = "email";//For Facebook
    public static final String FIELD_USER_EMAIL = "user_email";//In Forgot Password
    public static final String FIELD_PWD = "password";
    public static final String FIELD_IMAGE = "image";
    public static final String FIELD_MOBILE = "phonenumber";
    public static final String FIELD_PLATFORM = "platform";
    public static final String FIELD_DEVICE_TOKEN = "devicetoken";
    public static final String FIELD_LAT = "lat";
    public static final String FIELD_LON = "lon";
    public static final String FIELD_LNG = "lng";
    public static final String FIELD_PAGED = "paged";
    public static final String FIELD_RESTAURANTID = "restaurantId";
    public static final String FIELD_LATITUDE = "latitude";
    public static final String FIELD_LONGITUDE = "longitude";
    public static final String FIELD_PAGE_OFFSET = "pageOffset";
    public static final String FIELD_PAGE_SIZE = "pageSize";
    public static final String FIELD_CURRENT_PASSWORD = "currentpw";
    public static final String FIELD_NEW_PASSWORD = "newpw";
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_LOCATION = "location";
    public static final String FIELD_TELESAVE_ID = "telesave_id";

    public static final String vendorId = "vendorId";
    public static final String vendor_id = "vendor_id";
    public static final String reviewComment = "reviewComment";
    public static final String reviewStars = "reviewStars";
    public static final String userId = "userId";
    public static final String userid = "userid";

    public static final String usercountry = "usercountry";
    public static final String userstate = "userstate";
    public static final String usercity = "usercity";
    public static final String flag = "flag";
    public static final String page = "page";
    public static final String taskid = "taskid";
    public static final String taskId = "taskId";
    public static final String judeId = "judeId";


    public static final String token = "token";
    public static final String current_user = "current_user";
    public static final String amount = "amount";


    //WiGroup urls
    public static final String WIGROUP_BASE_URL = "http://qa.wigroup.co:8080";
    public static final String ISSUE_COUPON_URL = "/cvs-issuer-generic/rest/coupons?issueWiCode=true";
    public static final String GET_COUPON_CAMPAIGN = "/cvs-issuer-generic/rest/couponcampaigns";


    //Get Restaurant
    public static final String GET_RESTAURANTS = "/api/get_restaurants_list.php";
    public static final String GET_RESTAURANTS_DATA = "/api/get_restaurant_data.php";

    //Chat URLs...!!!
    public static final String ADD_USER_REVIEW = "/api/addUserReviews.php";
    public static final String GET_USER_REVIEW = "/api/getUserReviews.php";

}
