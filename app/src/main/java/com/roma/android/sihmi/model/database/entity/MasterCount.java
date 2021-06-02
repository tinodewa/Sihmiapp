package com.roma.android.sihmi.model.database.entity;

public class MasterCount {
    int count;

    public String getCountStr() {
        return String.valueOf(count);
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String name;
}
