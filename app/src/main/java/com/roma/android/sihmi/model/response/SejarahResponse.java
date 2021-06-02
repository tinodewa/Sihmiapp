package com.roma.android.sihmi.model.response;

import com.roma.android.sihmi.model.database.entity.Sejarah;

import java.util.List;

public class SejarahResponse {
    private String status;
    String message;
    List<Sejarah> data;

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

    public List<Sejarah> getData() {
        return data;
    }

    public void setData(List<Sejarah> data) {
        this.data = data;
    }
}
