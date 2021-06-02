package com.roma.android.sihmi.model.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Job {
    @PrimaryKey
    int id;
    String _id, id_user;
    String nama_perusahaan, jabatan, alamat, tahun_mulai, tahun_berakhir;

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

    public String getNama_perusahaan() {
        return nama_perusahaan;
    }

    public void setNama_perusahaan(String nama_perusahaan) {
        this.nama_perusahaan = nama_perusahaan;
    }

    public String getJabatan() {
        return jabatan;
    }

    public void setJabatan(String jabatan) {
        this.jabatan = jabatan;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getTahun_mulai() {
        return tahun_mulai;
    }

    public void setTahun_mulai(String tahun_mulai) {
        this.tahun_mulai = tahun_mulai;
    }

    public String getTahun_berakhir() {
        return tahun_berakhir;
    }

    public void setTahun_berakhir(String tahun_berakhir) {
        this.tahun_berakhir = tahun_berakhir;
    }
}
