package com.heyjude.androidapp.requestmodel;

import com.heyjude.androidapp.model.User;

import java.io.Serializable;

/**
 * Created by dipen on 1/7/15.
 */
public class LoginData implements Serializable {

    User data;

    public User getData() {
        return data;
    }

}
