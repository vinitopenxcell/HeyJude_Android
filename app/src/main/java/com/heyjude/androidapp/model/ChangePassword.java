package com.heyjude.androidapp.model;

import com.google.gson.annotations.Expose;

/**
 * Created by dipen on 7/9/15.
 */
public class ChangePassword {

    @Expose
    private Data data;

    /**
     * @return The data
     */
    public Data getData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setData(Data data) {
        this.data = data;
    }
}
