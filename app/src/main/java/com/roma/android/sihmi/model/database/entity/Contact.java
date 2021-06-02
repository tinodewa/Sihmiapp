package com.roma.android.sihmi.model.database.entity;

import com.roma.android.sihmi.utils.Tools;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Contact {
    @PrimaryKey
    @NonNull
    String _id = "";
    String badko, cabang, korkom, komisariat, id_roles, image, nama_depan, nama_belakang, nama_panggilan,
            jenis_kelamin, nomor_hp, alamat, username, tanggal_daftar, tempat_lahir, tanggal_lahir,
            status_perkawinan, keterangan, device_name, last_login, email;
    int status_online;
    int id_level;
    boolean bisukan;
    long dateRole = System.currentTimeMillis();

    String lk1, lk2, lk3, sc, tid;
    String tahun_daftar, tanggal_lk1, tahun_lk1, akun_sosmed, domisili_cabang, pekerjaan, jabatan, alamat_kerja, kontribusi;

    public Contact(@NonNull String _id, String badko, String cabang, String korkom, String komisariat, String id_roles, String image, String nama_depan, String nama_belakang, String nama_panggilan, String jenis_kelamin, String nomor_hp, String alamat, String username, String tanggal_daftar, String tempat_lahir, String tanggal_lahir, String status_perkawinan, String keterangan, String device_name, String last_login, String email, int status_online, int id_level) {
        this._id = _id;
        this.badko = badko;
        this.cabang = cabang;
        this.korkom = korkom;
        this.komisariat = komisariat;
        this.id_roles = id_roles;
        this.image = image;
        this.nama_depan = nama_depan;
        this.nama_belakang = nama_belakang;
        this.nama_panggilan = nama_panggilan;
        this.jenis_kelamin = jenis_kelamin;
        this.nomor_hp = nomor_hp;
        this.alamat = alamat;
        this.username = username;
        this.tanggal_daftar = tanggal_daftar;
        this.tempat_lahir = tempat_lahir;
        this.tanggal_lahir = tanggal_lahir;
        this.status_perkawinan = status_perkawinan;
        this.keterangan = keterangan;
        this.device_name = device_name;
        this.last_login = last_login;
        this.email = email;
        this.status_online = status_online;
        this.id_level = id_level;
        this.bisukan = false;
        this.dateRole = System.currentTimeMillis();
    }

    @Ignore
    public Contact(String nama_depan, String keterangan) {
        this.nama_depan = nama_depan;
        this.keterangan = keterangan;
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

    public String getId_roles() {
        return id_roles;
    }

    public void setId_roles(String id_roles) {
        this.id_roles = id_roles;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNama_depan() {
        return nama_depan;
    }

    public void setNama_depan(String nama_depan) {
        this.nama_depan = nama_depan;
    }

    public String getNama_belakang() {
        return nama_belakang;
    }

    public void setNama_belakang(String nama_belakang) {
        this.nama_belakang = nama_belakang;
    }

    public String getNama_panggilan() {
        return nama_panggilan;
    }

    public void setNama_panggilan(String nama_panggilan) {
        this.nama_panggilan = nama_panggilan;
    }

    public String getJenis_kelamin() {
        return jenis_kelamin;
    }

    public void setJenis_kelamin(String jenis_kelamin) {
        this.jenis_kelamin = jenis_kelamin;
    }

    public String getNomor_hp() {
        return nomor_hp;
    }

    public void setNomor_hp(String nomor_hp) {
        this.nomor_hp = nomor_hp;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTanggal_daftar() {
        return tanggal_daftar;
    }

    public void setTanggal_daftar(String tanggal_daftar) {
        this.tanggal_daftar = tanggal_daftar;
    }

    public String getTempat_lahir() {
        return tempat_lahir;
    }

    public void setTempat_lahir(String tempat_lahir) {
        this.tempat_lahir = tempat_lahir;
    }

    public String getTanggal_lahir() {
        return tanggal_lahir;
    }

    public void setTanggal_lahir(String tanggal_lahir) {
        this.tanggal_lahir = tanggal_lahir;
    }

    public String getStatus_perkawinan() {
        return status_perkawinan;
    }

    public void setStatus_perkawinan(String status_perkawinan) {
        this.status_perkawinan = status_perkawinan;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public String getLast_login() {
        return last_login;
    }

    public int getStatus_online() {
        return status_online;
    }

    public void setStatus_online(int status_online) {
        this.status_online = status_online;
    }

    public int getId_level() {
        return id_level;
    }

    public void setId_level(int id_level) {
        this.id_level = id_level;
    }

    public boolean isBisukan() {
        return bisukan;
    }

    public void setBisukan(boolean bisukan) {
        this.bisukan = bisukan;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getAkun_sosmed() {
        return akun_sosmed;
    }

    public void setAkun_sosmed(String akun_sosmed) {
        this.akun_sosmed = akun_sosmed;
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

    public String getAlamat_kerja() {
        return alamat_kerja;
    }

    public void setAlamat_kerja(String alamat_kerja) {
        this.alamat_kerja = alamat_kerja;
    }

    public String getKontribusi() {
        return kontribusi;
    }

    public void setKontribusi(String kontribusi) {
        this.kontribusi = kontribusi;
    }

    public String getTahun_daftar() {
        return tahun_daftar;
    }

    public void setTahun_daftar(String tahun_daftar) {
        this.tahun_daftar = tahun_daftar;
    }

    public String getGender(){
        String gender;
        if (jenis_kelamin.equals("0")) {
            gender = "Laki - laki";
        } else {
            gender = "Perempuan";
        }
        return gender;
    }

    public String getFullName(){
        return nama_depan+" "+nama_belakang;
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

    public long getDateRole() {
        if (Tools.getDateLaporanFromMillis(this.dateRole).equalsIgnoreCase("01/01/1970")) {
            this.setDateRole(System.currentTimeMillis());
        }
        return dateRole;
    }

    public void setDateRole(long dateRole) {
        this.dateRole = dateRole;
    }
}
