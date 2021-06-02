package com.roma.android.sihmi.model.database.entity;


import com.google.gson.annotations.SerializedName;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Training {
    @PrimaryKey
    @NonNull
    @SerializedName("_id")
    String id = "";
    String id_user;
    int id_level;
    String tipe, tahun;
    @SerializedName("nama")
    String nama_training;
    @SerializedName("lokasi")
    String tempat;

    String cabang;
    String komisariat;
    String domisili_cabang;
    String jenis_kelamin;

    public Training() {
    }

    public int getId_level() {
        return id_level;
    }

    public void setId_level(int id_level) {
        this.id_level = id_level;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getNama_training() {
        return nama_training;
    }

    public void setNama_training(String nama_training) {
        this.nama_training = nama_training;
    }

    public String getTipe() {
        return tipe;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }

    public String getTahun() {
        return tahun;
    }

    public void setTahun(String tahun) {
        this.tahun = tahun;
    }

    public String getNama() {
        return nama_training;
    }

    public void setNama(String nama_training) {
        this.nama_training = nama_training;
    }

    public String getTempat() {
        return tempat;
    }

    public void setTempat(String tempat) {
        this.tempat = tempat;
    }

    public String getCabang() {
        return cabang;
    }

    public void setCabang(String cabang) {
        this.cabang = cabang;
    }

    public String getKomisariat() {
        return komisariat;
    }

    public void setKomisariat(String komisariat) {
        this.komisariat = komisariat;
    }

    public String getDomisili_cabang() {
        return domisili_cabang;
    }

    public void setDomisili_cabang(String domisili_cabang) {
        this.domisili_cabang = domisili_cabang;
    }

    public String getJenis_kelamin() {
        return jenis_kelamin;
    }

    public void setJenis_kelamin(String jenis_kelamin) {
        this.jenis_kelamin = jenis_kelamin;
    }
}
