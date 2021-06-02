package com.roma.android.sihmi.model.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Medsos {
    @PrimaryKey
    int id;
    String _id, id_user;
    String medsos, username;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getMedsos() {
        return medsos;
    }

    public void setMedsos(String medsos) {
        this.medsos = medsos;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
