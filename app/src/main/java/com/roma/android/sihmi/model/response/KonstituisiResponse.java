package com.roma.android.sihmi.model.response;

import com.roma.android.sihmi.model.database.entity.Konstituisi;

import java.util.List;

public class KonstituisiResponse {
    private String status;
    private String message;
    private List<Konstituisi> data;

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

    public List<Konstituisi> getData() {
        return data;
    }

    public void setData(List<Konstituisi> data) {
        this.data = data;
    }
}
