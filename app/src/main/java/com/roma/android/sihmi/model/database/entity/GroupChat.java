package com.roma.android.sihmi.model.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class GroupChat {
    @PrimaryKey
    @NonNull
    private String nama;
    private String image;
    private String provinsi, kota, fakultas, kampus;
    private long time;
    private String last_msg;
    private long last_seen;
    private int unread;
    private boolean bisukan;

    public GroupChat(@NonNull String nama, String image) {
        this.nama = nama;
        this.image = image;
    }

    public boolean isBisukan() {
        return bisukan;
    }

    public void setBisukan(boolean bisukan) {
        this.bisukan = bisukan;
    }

    @NonNull
    public String getNama() {
        return nama;
    }

    public void setNama(@NonNull String nama) {
        this.nama = nama;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getProvinsi() {
        return provinsi;
    }

    public void setProvinsi(String provinsi) {
        this.provinsi = provinsi;
    }

    public String getKota() {
        return kota;
    }

    public void setKota(String kota) {
        this.kota = kota;
    }

    public String getFakultas() {
        return fakultas;
    }

    public void setFakultas(String fakultas) {
        this.fakultas = fakultas;
    }

    public String getKampus() {
        return kampus;
    }

    public void setKampus(String kampus) {
        this.kampus = kampus;
    }

    public String getLast_msg() {
        return last_msg;
    }

    public void setLast_msg(String last_msg) {
        this.last_msg = last_msg;
    }

    public long getLast_seen() {
        return last_seen;
    }

    public void setLast_seen(long last_seen) {
        this.last_seen = last_seen;
    }

    public int getUnread() {
        return unread;
    }

    public void setUnread(int unread) {
        this.unread = unread;
    }
}
