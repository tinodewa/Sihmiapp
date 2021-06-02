package com.roma.android.sihmi.model.database.entity;


import com.google.gson.annotations.SerializedName;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Level {
    @PrimaryKey
    @NonNull
    @SerializedName("_id")
    String idLevel = "";
    int id, level;
    String nama;
    String newName;

    @NonNull
    public String getIdLevel() {
        return idLevel;
    }

    public void setIdLevel(@NonNull String idLevel) {
        this.idLevel = idLevel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }
}
