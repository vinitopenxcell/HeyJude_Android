package com.heyjude.androidapp.wiGroupAPIRequest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.heyjude.androidapp.apirequest.ApiService;
import com.heyjude.androidapp.apirequest.Request;
import com.heyjude.androidapp.utility.Constants;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by Dipen on 11/5/15.
 */
public class WiGroupRestClient {

    private static WiGroupRestClient restClientInstance;
    private ApiService apiService;


    public static WiGroupRestClient getInstance() {
        if (restClientInstance == null) {
            restClientInstance = new WiGroupRestClient();
        }
        return restClientInstance;
    }


    private WiGroupRestClient() {

        Gson gson = new GsonBuilder().create();
        Constants.WS_TYPE = "wiGroup";
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(Request.WIGROUP_BASE_URL)
                .setConverter(new GsonConverter(gson))
                .setRequestInterceptor(new RequestIntercepterWiGroup())
                .build();

        apiService = restAdapter.create(ApiService.class);
    }


    public ApiService getApiService() {
        return apiService;
    }
}