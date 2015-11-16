package com.heyjude.androidapp.apirequest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by aalap on 11/5/15.
 */
public class RestClient {

    private static RestClient restClientInstance;
    private ApiService apiService;


    public static RestClient getInstance() {
        if (restClientInstance == null) {
            restClientInstance = new RestClient();
        }
        return restClientInstance;
    }

    private RestClient() {

        Gson gson = new GsonBuilder().create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(Request.BASE_URL)
                .setConverter(new GsonConverter(gson))
                .setRequestInterceptor(new RequestIntercepter())
                .build();

        apiService = restAdapter.create(ApiService.class);
    }


    public ApiService getApiService() {
        return apiService;
    }
}