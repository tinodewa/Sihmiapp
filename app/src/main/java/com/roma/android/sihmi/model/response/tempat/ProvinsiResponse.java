package com.roma.android.sihmi.model.response.tempat;

import com.roma.android.sihmi.model.database.entity.tempat.Provinsi;

import java.util.List;

public class ProvinsiResponse {
    private String status;
    private String message;
    private List<Provinsi> data;

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

    public List<Provinsi> getData() {
        return data;
    }

    public void setData(List<Provinsi> data) {
        this.data = data;
    }
}
