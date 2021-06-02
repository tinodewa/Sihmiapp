package com.roma.android.sihmi.model.response;

import com.roma.android.sihmi.model.database.entity.Agenda;

public class AgendaSingleResponse {
    private String status;
    private String message;
    private Agenda data;

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

    public Agenda getData() {
        return data;
    }

    public void setData(Agenda data) {
        this.data = data;
    }
}
