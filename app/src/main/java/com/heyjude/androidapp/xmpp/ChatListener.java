package com.heyjude.androidapp.xmpp;


import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;

/**
 * Created by bhavesh on 13/7/15.
 */
public class ChatListener {

    public void connected() {

    }

    public void authenticated() {

    }

    public void connectionStatusChanged(int mStatus) {

    }

    public void onMessageReceived(Message receivedMessage) {

    }

    public void onMessageDelievred(String receipt) {

    }

    public void onMessageSentSuccessfully(Message sentMessage) {

    }

    public void userStatusChanged() {

    }

    public void disconnected(boolean isLogoutRequested) {
    }

    public void presenceChanged(String userName, Presence.Type mPresence) {

    }
}
