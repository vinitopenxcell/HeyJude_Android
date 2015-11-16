package com.heyjude.androidapp.xmpp;


/**
 * Created by bhavesh on 13/7/15.
 * Wrapper class that will manage all callbacks
 */
public class ChatCallBacks {

    public void onLogin() {
    }

    public void onLogout() {
    }

    public void connectionStatusChanged(int connectionStatus) {
    }

    public void onMessageReceived(ChatMessage chatUserDetail) {
    }


    public void onMessageSentSuccessfully(ChatMessage chatUserDetail) {
    }

    /**
     * for updating unread/read count once it needs to change
     */
    public void updateOnMessageRead() {

    }

}
