package com.roma.android.sihmi.model.database.entity;

import androidx.annotation.NonNull;

public class PengajuanHistoryJoin extends PengajuanHistory {
    String komisariat, cabang;

    public PengajuanHistoryJoin(@NonNull String _id, String id_roles, String file, String created_by, String approved_by, long date_created, long date_modified, int status, String tanggal_lk1, int level) {
        super(_id, id_roles, file, created_by, approved_by, date_created, date_modified, status, tanggal_lk1, level);
    }

    public String getKomisariat() {
        return komisariat;
    }

    public void setKomisariat(String komisariat) {
        this.komisariat = komisariat;
    }

    public String getCabang() {
        return cabang;
    }

    public void setCabang(String cabang) {
        this.cabang = cabang;
    }
}
