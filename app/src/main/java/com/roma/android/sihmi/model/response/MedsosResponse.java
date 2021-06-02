package com.roma.android.sihmi.model.response;

import com.roma.android.sihmi.model.database.entity.Medsos;

import java.util.List;

public class MedsosResponse {
    private String status;
    String message;
    List<Medsos> data;

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

    public List<Medsos> getData() {
        return data;
    }

    public void setData(List<Medsos> data) {
        this.data = data;
    }
}
