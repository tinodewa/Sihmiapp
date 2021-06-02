package com.roma.android.sihmi.model.response;

import com.roma.android.sihmi.model.database.entity.Contact;

import java.util.List;

public class ContactResponse {
    private String status;
    private String message;
    private List<Contact> data;

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

    public List<Contact> getData() {
        return data;
    }

    public void setData(List<Contact> data) {
        this.data = data;
    }
}
