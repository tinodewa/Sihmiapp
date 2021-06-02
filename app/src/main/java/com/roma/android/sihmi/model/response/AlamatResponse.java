package com.roma.android.sihmi.model.response;

import com.roma.android.sihmi.model.database.entity.Alamat;

import java.util.List;

public class AlamatResponse {
    private String status;
    private String message;
    private List<Alamat> data;

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

    public List<Alamat> getData() {
        return data;
    }

    public void setData(List<Alamat> data) {
        this.data = data;
    }
}
