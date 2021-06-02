package com.roma.android.sihmi.model.response;

import com.roma.android.sihmi.model.database.entity.Contact;

public class ContactSingleResponse {private String status;
    private String message;
    @SuppressWarnings("unused")
    private Contact data;


    public Contact getData() {
        return data;
    }

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
}
