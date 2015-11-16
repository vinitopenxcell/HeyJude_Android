package com.heyjude.androidapp.model.chatHistory;

/**
 * Created by dipen on 12/10/15.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class ChatHistory {

    @SerializedName("data")
    @Expose
    private List<ChatHistoryData> data = new ArrayList<ChatHistoryData>();

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("message")
    @Expose
    private String message;

    /**
     * @return The data
     */
    public List<ChatHistoryData> getData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setData(List<ChatHistoryData> data) {
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
