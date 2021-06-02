package com.roma.android.sihmi.model.response;

import com.roma.android.sihmi.model.database.entity.Level;

import java.util.List;

public class LevelResponse {
    private String status;
    private String message;
    private List<Level> data;

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

    public List<Level> getData() {
        return data;
    }

    public void setData(List<Level> data) {
        this.data = data;
    }
}
