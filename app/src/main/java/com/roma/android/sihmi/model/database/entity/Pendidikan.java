package com.roma.android.sihmi.model.database.entity;


import com.google.gson.annotations.SerializedName;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Pendidikan {
    @PrimaryKey
    @NonNull
    String _id;
    String id_user;
    String tahun, strata, jurusan;
    @SerializedName("kampus")
    String nama_kampus;

    public Pendidikan(String tahun, String strata, String nama_kampus, String jurusan) {
        this.tahun = tahun;
        this.strata = strata;
        this.nama_kampus = nama_kampus;
        this.jurusan = jurusan;
        this._id = "";
    }

    @NonNull
    public String get_id() {
        return _id;
    }

    public void set_id(@NonNull String _id) {
        this._id = _id;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getNama_kampus() {
        return nama_kampus;
    }

    public String getTahun() {
        return tahun;
    }

    public void setTahun(String tahun) {
        this.tahun = tahun;
    }

    public String getStrata() {
        return strata;
    }

    public String getNama() {
        return nama_kampus;
    }

    public void setNama(String nama_kampus) {
        this.nama_kampus = nama_kampus;
    }

    public String getJurusan() {
        return jurusan;
    }

    public void setJurusan(String jurusan) {
        this.jurusan = jurusan;
    }
}
