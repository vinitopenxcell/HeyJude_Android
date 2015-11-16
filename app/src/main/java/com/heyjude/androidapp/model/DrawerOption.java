package com.heyjude.androidapp.model;

/**
 * Created by Aalap on 24-04-2015.
 */
public class DrawerOption {

    private String optionName;
    private int rightImageResId;

    public DrawerOption(String optionName, int rightImageResId) {
        this.optionName = optionName;
        this.rightImageResId = rightImageResId;
    }

    public String getOptionName() {
        return optionName;
    }

    public int getRightImageResId() {
        return rightImageResId;
    }

}
