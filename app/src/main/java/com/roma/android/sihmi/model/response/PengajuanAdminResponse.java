package com.roma.android.sihmi.model.response;


import com.roma.android.sihmi.model.database.entity.PengajuanAdmin;

import java.util.List;

public class PengajuanAdminResponse {
    private String status;
    private String message;
    private List<PengajuanAdmin> data;

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

    public List<PengajuanAdmin> getData() {
        return data;
    }

    public void setData(List<PengajuanAdmin> data) {
        this.data = data;
    }
}
