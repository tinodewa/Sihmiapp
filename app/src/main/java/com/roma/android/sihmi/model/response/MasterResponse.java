package com.roma.android.sihmi.model.response;

import com.roma.android.sihmi.model.database.entity.Master;

import java.util.List;

public class MasterResponse {
    private String status;
    private String message;
    private List<Master> data;

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

    public List<Master> getData() {
        return data;
    }

    public void setData(List<Master> data) {
        this.data = data;
    }
}
