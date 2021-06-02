package com.roma.android.sihmi.model.database.entity;

public class DataGrafik {
    int tahun, jumlah, nonKader, kader;

    public DataGrafik(int tahun, int jumlah, int nonKader, int kader) {
        this.tahun = tahun;
        this.jumlah = jumlah;
        this.nonKader = nonKader;
        this.kader = kader;
    }

    public int getTahun() {
        return tahun;
    }

    public void setTahun(int tahun) {
        this.tahun = tahun;
    }

    public int getJumlah() {
        return jumlah;
    }

    public int getNonKader() {
        return nonKader;
    }

    public void setNonKader(int nonKader) {
        this.nonKader = nonKader;
    }

    public int getKader() {
        return kader;
    }

    public void setKader(int kader) {
        this.kader = kader;
    }
}
