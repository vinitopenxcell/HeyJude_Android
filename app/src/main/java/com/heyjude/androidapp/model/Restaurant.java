package com.heyjude.androidapp.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class Restaurant {

    @Expose
    private List<ListData> data = new ArrayList<ListData>();
    @Expose
    private String status;
    @Expose
    private String message;

    /**
     * @return The data
     */
    public List<ListData> getData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setData(List<ListData> data) {
        this.data = data;
    }

    /**
     * @return The status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return The message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message The message
     */
    public void setMessage(String message) {
        this.message = message;
    }

}