package com.heyjude.androidapp.apirequest;

import com.heyjude.androidapp.utility.HeyJudeApp;

import retrofit.RequestInterceptor;

/**
 * Created by aalap on 11/5/15.
 */
public class RequestIntercepter implements RequestInterceptor {

    @Override
    public void intercept(RequestFacade request) {

        if (HeyJudeApp.getInstance().getSessionKey() != null) {
            request.addHeader("Cookie", "PHPSESSID=" + HeyJudeApp.getInstance().getSessionKey());
            request.addHeader(Request.SESSION_HEADER, HeyJudeApp.getInstance().getSessionKey());
        }
    }

}
