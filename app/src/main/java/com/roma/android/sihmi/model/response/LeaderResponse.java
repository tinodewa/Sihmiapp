package com.roma.android.sihmi.model.response;

import com.roma.android.sihmi.model.database.entity.Leader;

import java.util.List;

public class LeaderResponse {
    private String status;
    private String message;
    private List<Leader> data;

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

    public List<Leader> getData() {
        return data;
    }

    public void setData(List<Leader> data) {
        this.data = data;
    }
}
