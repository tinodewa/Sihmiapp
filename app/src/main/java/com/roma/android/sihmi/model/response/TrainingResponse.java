package com.roma.android.sihmi.model.response;

import com.roma.android.sihmi.model.database.entity.Training;

import java.util.List;

public class TrainingResponse {
    private String status;
    String message;
    List<Training> data;

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

    public List<Training> getData() {
        return data;
    }

    public void setData(List<Training> data) {
        this.data = data;
    }
}
