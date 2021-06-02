package com.roma.android.sihmi.model.database.entity;

public class DataKader {
    private String nama;
    private int total;

    private int tahun, lk1, lk2, lk3, sc, tid;

    public DataKader(String nama, int total) {
        this.nama = nama;
        this.total = total;
    }

    public DataKader(int tahun, int lk1, int lk2, int lk3, int sc, int tid) {
        this.tahun = tahun;
        this.lk1 = lk1;
        this.lk2 = lk2;
        this.lk3 = lk3;
        this.sc = sc;
        this.tid = tid;
        total = lk1+lk2+lk3+sc+tid;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTahun() {
        return tahun;
    }

    public void setTahun(int tahun) {
        this.tahun = tahun;
    }

    public int getLk1() {
        return lk1;
    }

    public void setLk1(int lk1) {
        this.lk1 = lk1;
    }

    public int getLk2() {
        return lk2;
    }

    public void setLk2(int lk2) {
        this.lk2 = lk2;
    }

    public int getLk3() {
        return lk3;
    }

    public void setLk3(int lk3) {
        this.lk3 = lk3;
    }

    public int getSc() {
        return sc;
    }

    public void setSc(int sc) {
        this.sc = sc;
    }

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }
}
