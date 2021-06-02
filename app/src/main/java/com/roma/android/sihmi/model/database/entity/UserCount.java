package com.roma.android.sihmi.model.database.entity;

public class UserCount {
    public String value;
    public int userCount;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUserCountStr() {
        return String.valueOf(userCount);
    }
}
