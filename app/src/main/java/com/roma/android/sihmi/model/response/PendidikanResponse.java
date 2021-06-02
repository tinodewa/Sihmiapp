package com.roma.android.sihmi.model.response;

import com.roma.android.sihmi.model.database.entity.Pendidikan;

import java.util.List;

public class PendidikanResponse {
    private String status;
    String message;
    List<Pendidikan> data;

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

    public List<Pendidikan> getData() {
        return data;
    }

    public void setData(List<Pendidikan> data) {
        this.data = data;
    }
}
