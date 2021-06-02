package com.roma.android.sihmi.model.database.entity.tempat;

public class Kota {
    String _id, id, id_provinsi, nama;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setId_provinsi(String id_provinsi) {
        this.id_provinsi = id_provinsi;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }
}
