package com.roma.android.sihmi.model.response.tempat;

import com.roma.android.sihmi.model.database.entity.tempat.Kota;

import java.util.List;

public class KotaResponse {
    private String status;
    private String message;
    private List<Kota> data;

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

    public List<Kota> getData() {
        return data;
    }

    public void setData(List<Kota> data) {
        this.data = data;
    }
}
