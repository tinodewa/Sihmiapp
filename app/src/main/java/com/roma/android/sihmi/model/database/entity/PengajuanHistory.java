package com.roma.android.sihmi.model.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class PengajuanHistory {
    @PrimaryKey
    @NonNull
    String _id;
    String nama;
    String id_roles, file, created_by, approved_by;
    long date_created, date_modified;
    int status, level;
    String tanggal_lk1;



    public PengajuanHistory(@NonNull String _id, String id_roles, String file, String created_by, String approved_by, long date_created, long date_modified, int status, String tanggal_lk1, int level) {
        this._id = _id;
        this.id_roles = id_roles;
        this.file = file;
        this.created_by = created_by;
        this.approved_by = approved_by;
        this.date_created = date_created;
        this.date_modified = date_modified;
        this.status = status;
        this.tanggal_lk1 = tanggal_lk1;
        this.level = level;
    }

    @NonNull
    public String get_id() {
        return _id;
    }

    public void set_id(@NonNull String _id) {
        this._id = _id;
    }

    public String getId_roles() {
        return id_roles;
    }

    public void setId_roles(String id_roles) {
        this.id_roles = id_roles;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getApproved_by() {
        return approved_by;
    }

    public void setApproved_by(String approved_by) {
        this.approved_by = approved_by;
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

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getTanggal_lk1() {
        return tanggal_lk1;
    }

    public void setTanggal_lk1(String tanggal_lk1) {
        this.tanggal_lk1 = tanggal_lk1;
    }
}
