package com.roma.android.sihmi.model.response;

import com.roma.android.sihmi.model.database.entity.Job;

import java.util.List;

public class JobResponse {
    private String status;
    String message;
    List<Job> data;

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

    public List<Job> getData() {
        return data;
    }

    public void setData(List<Job> data) {
        this.data = data;
    }
}
