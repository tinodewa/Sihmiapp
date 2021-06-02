package com.roma.android.sihmi.model.response;

import com.roma.android.sihmi.model.database.entity.Pengajuan;

import java.util.List;

public class PengajuanResponse {
    String status, message;
    List<Pengajuan> data;

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

    public List<Pengajuan> getData() {
        return data;
    }

    public void setData(List<Pengajuan> data) {
        this.data = data;
    }
}
