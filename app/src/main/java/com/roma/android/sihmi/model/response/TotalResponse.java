package com.roma.android.sihmi.model.response;

import com.roma.android.sihmi.model.database.entity.Total;

public class TotalResponse {
    private String status;
    String message;
    Total data;

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

    public Total getData() {
        return data;
    }

    public void setData(Total data) {
        this.data = data;
    }
}
