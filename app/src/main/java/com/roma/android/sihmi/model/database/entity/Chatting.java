package com.roma.android.sihmi.model.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Chatting {
    @PrimaryKey
    @NonNull
    String _id;
    String name;
    String last_message;
    long time_message;
    int unread;

    public Chatting(@NonNull String _id, String name, String last_message, long time_message, int unread) {
        this._id = _id;
        this.name = name;
        this.last_message = last_message;
        this.time_message = time_message;
        this.unread = unread;
    }

    @NonNull
    public String get_id() {
        return _id;
    }

    public void set_id(@NonNull String _id) {
        this._id = _id;
    }

    public String getLast_message() {
        return last_message;
    }

    public void setLast_message(String last_message) {
        this.last_message = last_message;
    }

    public long getTime_message() {
        return time_message;
    }

    public void setTime_message(long time_message) {
        this.time_message = time_message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUnread() {
        return unread;
    }

    public void setUnread(int unread) {
        this.unread = unread;
    }
}
