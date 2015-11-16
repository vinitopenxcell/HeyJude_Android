package com.heyjude.androidapp.model.chatHistory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.heyjude.androidapp.xmpp.ChatMessage;

public class ChatHistoryData {

    @SerializedName("conversationID")
    @Expose
    private String conversationID;

    @SerializedName("msg_from")
    @Expose
    private String msgFrom;

    @SerializedName("msg_to")
    @Expose
    private String msgTo;

    @SerializedName("fromJIDResource")
    @Expose
    private String fromJIDResource;

    @SerializedName("body")
    @Expose
    private ChatMessage body;

    /**
     * @return The conversationID
     */
    public String getConversationID() {
        return conversationID;
    }

    /**
     * @param conversationID The conversationID
     */
    public void setConversationID(String conversationID) {
        this.conversationID = conversationID;
    }

    /**
     * @return The msgFrom
     */
    public String getMsgFrom() {
        return msgFrom;
    }

    /**
     * @param msgFrom The msg_from
     */
    public void setMsgFrom(String msgFrom) {
        this.msgFrom = msgFrom;
    }

    /**
     * @return The msgTo
     */
    public String getMsgTo() {
        return msgTo;
    }

    /**
     * @param msgTo The msg_to
     */
    public void setMsgTo(String msgTo) {
        this.msgTo = msgTo;
    }

    /**
     * @return The fromJIDResource
     */
    public String getFromJIDResource() {
        return fromJIDResource;
    }

    /**
     * @param fromJIDResource The fromJIDResource
     */
    public void setFromJIDResource(String fromJIDResource) {
        this.fromJIDResource = fromJIDResource;
    }

    public ChatMessage getBody() {
        return body;
    }

    public void setBody(ChatMessage body) {
        this.body = body;
    }
}