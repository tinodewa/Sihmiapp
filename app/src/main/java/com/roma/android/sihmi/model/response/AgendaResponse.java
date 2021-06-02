package com.roma.android.sihmi.model.response;

import com.roma.android.sihmi.model.database.entity.Agenda;

import java.util.List;

public class AgendaResponse {
    private String status;
    private String message;
    private List<Agenda> data;

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

    public List<Agenda> getData() {
        return data;
    }

    public void setData(List<Agenda> data) {
        this.data = data;
    }
}
