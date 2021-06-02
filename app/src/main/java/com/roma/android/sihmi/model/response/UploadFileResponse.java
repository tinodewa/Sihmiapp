package com.roma.android.sihmi.model.response;

import com.roma.android.sihmi.model.database.entity.FileUpload;

import java.util.List;

public class UploadFileResponse {
    private String status;
    String message;
    List<FileUpload> data;

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

    public List<FileUpload> getData() {
        return data;
    }

    public void setData(List<FileUpload> data) {
        this.data = data;
    }
}
