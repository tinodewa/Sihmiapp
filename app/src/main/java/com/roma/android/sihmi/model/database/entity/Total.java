package com.roma.android.sihmi.model.database.entity;

import com.google.gson.annotations.SerializedName;

public class Total {
    @SerializedName("Non LK")
    String Non_LK;
    String LK1, LK2, LK3, totalAll;

    @SuppressWarnings("unused")
    public void setNon_LK(String non_LK) {
        Non_LK = non_LK;
    }

    public String getLK1() {
        return LK1;
    }

    public void setLK1(String LK1) {
        this.LK1 = LK1;
    }

    public String getLK2() {
        return LK2;
    }

    public void setLK2(String LK2) {
        this.LK2 = LK2;
    }

    public String getLK3() {
        return LK3;
    }

    public void setLK3(String LK3) {
        this.LK3 = LK3;
    }

    public String getTotalAll() {
        return totalAll;
    }

    public void setTotalAll(String totalAll) {
        this.totalAll = totalAll;
    }
}
