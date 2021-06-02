package com.roma.android.sihmi.model.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class PengajuanLK1 {
    @PrimaryKey
    @NonNull
    String _id;
    String badko, cabang, korkom, komisariat, tanggal_lk1, tahun_lk1, created_by, modified_by;
    long date_created, date_modified;
    int status;

    public PengajuanLK1(@NonNull String _id, String badko, String cabang, String korkom, String komisariat, String tanggal_lk1, String tahun_lk1, String created_by, String modified_by, long date_created, long date_modified, int status) {
        this._id = _id;
        this.badko = badko;
        this.cabang = cabang;
        this.korkom = korkom;
        this.komisariat = komisariat;
        this.tanggal_lk1 = tanggal_lk1;
        this.tahun_lk1 = tahun_lk1;
        this.created_by = created_by;
        this.modified_by = modified_by;
        this.date_created = date_created;
        this.date_modified = date_modified;
        this.status = status;
    }

    @NonNull
    public String get_id() {
        return _id;
    }

    public void set_id(@NonNull String _id) {
        this._id = _id;
    }

    public String getBadko() {
        return badko;
    }

    public void setBadko(String badko) {
        this.badko = badko;
    }

    public String getCabang() {
        return cabang;
    }

    public void setCabang(String cabang) {
        this.cabang = cabang;
    }

    public String getKorkom() {
        return korkom;
    }

    public void setKorkom(String korkom) {
        this.korkom = korkom;
    }

    public String getKomisariat() {
        return komisariat;
    }

    public void setKomisariat(String komisariat) {
        this.komisariat = komisariat;
    }

    public String getTanggal_lk1() {
        return tanggal_lk1;
    }

    public void setTanggal_lk1(String tanggal_lk1) {
        this.tanggal_lk1 = tanggal_lk1;
    }

    public String getTahun_lk1() {
        return tahun_lk1;
    }

    public void setTahun_lk1(String tahun_lk1) {
        this.tahun_lk1 = tahun_lk1;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getModified_by() {
        return modified_by;
    }

    public void setModified_by(String modified_by) {
        this.modified_by = modified_by;
    }

    public long getDate_created() {
        return date_created;
    }

    public void setDate_created(long date_created) {
        this.date_created = date_created;
    }

    public long getDate_modified() {
        return date_modified;
    }

    public void setDate_modified(long date_modified) {
        this.date_modified = date_modified;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
