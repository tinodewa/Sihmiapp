package com.roma.android.sihmi.model.response;

import com.roma.android.sihmi.model.database.entity.AboutUs;

import java.util.List;

public class AboutUsResponse {
    private String status;
    private String message;
    private List<AboutUs> data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<AboutUs> getData() {
        return data;
    }

    public void setData(List<AboutUs> data) {
        this.data = data;
    }
}
