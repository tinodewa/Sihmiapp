package com.roma.android.sihmi.model.response;

import com.roma.android.sihmi.model.database.entity.File;

import java.util.List;

public class FileResponse {
    private String status;
    List<File> message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<File> getMessage() {
        return message;
    }

    public void setMessage(List<File> message) {
        this.message = message;
    }
}
