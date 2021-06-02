package com.roma.android.sihmi.model.response;


import com.roma.android.sihmi.model.database.entity.PengajuanLK1;

import java.util.List;

public class PengajuanLK1Response {
    private String status;
    private String message;
    private List<PengajuanLK1> data;

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

    public List<PengajuanLK1> getData() {
        return data;
    }

    public void setData(List<PengajuanLK1> data) {
        this.data = data;
    }
}
