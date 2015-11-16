package com.heyjude.androidapp.wiGroupAPIRequest;

import retrofit.RequestInterceptor;

/**
 * Created by dipen on 28/8/15.
 */
public class RequestIntercepterWiGroup implements RequestInterceptor {
    @Override
    public void intercept(RequestFacade request) {
        request.addHeader("apiId", "HeyJudeIssuer");
        request.addHeader("apiPassword", "test");
    }
}
