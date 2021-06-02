package com.roma.android.sihmi.model.response.tempat;

import com.roma.android.sihmi.model.database.entity.tempat.Wilayah;

import java.util.List;

public class WilayahResponse {
    private String status;
    private String message;
    private List<Wilayah> data;

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

    public List<Wilayah> getData() {
        return data;
    }

    public void setData(List<Wilayah> data) {
        this.data = data;
    }
}
