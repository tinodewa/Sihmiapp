package com.roma.android.sihmi.model.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Master {
    @PrimaryKey
    @NonNull
    private String _id = "";
    private String type, value, date_created, date_modified, id_user;
    @NonNull
    String parentId = "";
    private boolean availableAddres;

    public Master() {
    }

    @Ignore
    public Master(String type, String value) {
        this.type = type;
        this.value = value;
    }

    @NonNull
    public String getParentId() {
        return parentId;
    }

    public void setParentId(@NonNull String parentId) {
        this.parentId = parentId;
    }

    @NonNull
    public String get_id() {
        return _id;
    }

    public void set_id(@NonNull String _id) {
        this._id = _id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getDate_modified() {
        return date_modified;
    }

    public void setDate_modified(String date_modified) {
        this.date_modified = date_modified;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public boolean isAvailableAddres() {
        return availableAddres;
    }

    public void setAvailableAddres(boolean availableAddres) {
        this.availableAddres = availableAddres;
    }
}
