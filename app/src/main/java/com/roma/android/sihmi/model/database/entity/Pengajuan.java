package com.roma.android.sihmi.model.database.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Pengajuan {
    @PrimaryKey
    @NonNull
    String _id = "";
    String id_user, bulan_lk, ttl, stts_perkawinan, facebook, instagram, domisili_cabang, pekerjaan, jabatan, alamat_perusahaan;
    @SerializedName("tahun_lk")
    String tahun_lk1;
    @SerializedName("tempat_lk")
    String tempat_lk1;
    int tujuan_pengajuan, status;
    @Ignore
    List<Pendidikan> pendidikanList;
    @Ignore
    List<Training> trainingList;

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

    public String getBulan_lk() {
        return bulan_lk;
    }

    public void setBulan_lk(String bulan_lk) {
        this.bulan_lk = bulan_lk;
    }

    public String getTahun_lk1() {
        return tahun_lk1;
    }

    public void setTahun_lk1(String tahun_lk1) {
        this.tahun_lk1 = tahun_lk1;
    }

    public String getTempat_lk1() {
        return tempat_lk1;
    }

    public void setTempat_lk1(String tempat_lk1) {
        this.tempat_lk1 = tempat_lk1;
    }

    public String getTtl() {
        return ttl;
    }

    public void setTtl(String ttl) {
        this.ttl = ttl;
    }

    public String getStts_perkawinan() {
        return stts_perkawinan;
    }

    public void setStts_perkawinan(String stts_perkawinan) {
        this.stts_perkawinan = stts_perkawinan;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getDomisili_cabang() {
        return domisili_cabang;
    }

    public void setDomisili_cabang(String domisili_cabang) {
        this.domisili_cabang = domisili_cabang;
    }

    public String getPekerjaan() {
        return pekerjaan;
    }

    public void setPekerjaan(String pekerjaan) {
        this.pekerjaan = pekerjaan;
    }

    public String getJabatan() {
        return jabatan;
    }

    public void setJabatan(String jabatan) {
        this.jabatan = jabatan;
    }

    public String getAlamat_perusahaan() {
        return alamat_perusahaan;
    }

    public void setAlamat_perusahaan(String alamat_perusahaan) {
        this.alamat_perusahaan = alamat_perusahaan;
    }

    public List<Pendidikan> getPendidikanList() {
        return pendidikanList;
    }

    public void setPendidikanList(List<Pendidikan> pendidikanList) {
        this.pendidikanList = pendidikanList;
    }

    public List<Training> getTrainingList() {
        return trainingList;
    }

    public void setTrainingList(List<Training> trainingList) {
        this.trainingList = trainingList;
    }

    public int getTujuan_pengajuan() {
        return tujuan_pengajuan;
    }

    public void setTujuan_pengajuan(int tujuan_pengajuan) {
        this.tujuan_pengajuan = tujuan_pengajuan;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
