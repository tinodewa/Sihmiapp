package com.roma.android.sihmi.model.database.entity;

//@Entity
public class Member {
//    @PrimaryKey
    String id_user;
    String badko, cabang, korkom, komisariat, alumni;
    String lk1, lk2, lk3, sc, tid;
    String gender;
    int tahun, tahunTraining;

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
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

    public String getAlumni() {
        return alumni;
    }

    public void setAlumni(String alumni) {
        this.alumni = alumni;
    }

    public String getLk1() {
        return lk1;
    }

    public void setLk1(String lk1) {
        this.lk1 = lk1;
    }

    public String getLk2() {
        return lk2;
    }

    public void setLk2(String lk2) {
        this.lk2 = lk2;
    }

    public String getLk3() {
        return lk3;
    }

    public void setLk3(String lk3) {
        this.lk3 = lk3;
    }

    public String getSc() {
        return sc;
    }

    public void setSc(String sc) {
        this.sc = sc;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getTahun() {
        return tahun;
    }

    public void setTahun(int tahun) {
        this.tahun = tahun;
    }

    public int getTahunTraining() {
        return tahunTraining;
    }

}
