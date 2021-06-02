package com.roma.android.sihmi.utils;

public class Query {

    public static String reportTrainingAdmin1(String komisariat) {
        return "SELECT * FROM Training WHERE id_level != " + Constant.USER_NON_LK + " AND id_level != " + Constant.USER_SUPER_ADMIN + " AND komisariat = '" + komisariat + "' AND tahun >= 1947 ";
    }

    public static String ReportKaderAdmin1(String komisariat){
        return "SELECT * FROM Contact WHERE id_level != " + Constant.USER_NON_LK + " AND id_level != " + Constant.USER_SUPER_ADMIN + " AND komisariat = '"+komisariat+"'";
    }

    public static String countReportKaderAdmin1(String komisariat){
        return "SELECT COUNT (*) FROM Training WHERE id_level != " + Constant.USER_NON_LK + " AND id_level != " + Constant.USER_SUPER_ADMIN + " AND komisariat = '"+komisariat+"'";
    }

    public static String countReportKaderAdmin1L(String komisariat){
        return "SELECT COUNT (*) FROM Training WHERE id_level != " + Constant.USER_NON_LK + " AND id_level != " + Constant.USER_SUPER_ADMIN + " AND komisariat = '"+komisariat+"' AND jenis_kelamin = '0'";
    }
    public static String countReportKaderAdmin1P(String komisariat){
        return "SELECT COUNT (*) FROM Training WHERE id_level != " + Constant.USER_NON_LK + " AND id_level != " + Constant.USER_SUPER_ADMIN + " AND komisariat = '"+komisariat+"' AND jenis_kelamin = '1'";
    }

    public static String countPelatihanAdmin1(String komisariat){
        return "SELECT COUNT (*) FROM Training WHERE id_level != " + Constant.USER_NON_LK + " AND id_level != " + Constant.USER_SUPER_ADMIN + " AND komisariat = '"+komisariat+"' AND tahun >= 1947 ";
    }

    // report kader admin 2 ini sama dengan LA1 dan Admin 3(namun di admin3 menggunakan domisili_cabang)
    public static String ReportKaderAdmin2(String cabang){
        return "SELECT * FROM Contact WHERE id_level != " + Constant.USER_NON_LK + " AND id_level != " + Constant.USER_SUPER_ADMIN + " AND cabang = '"+cabang+"'";
    }

    public static String reportTrainingAdmin2(String cabang){
        return "SELECT * FROM Training WHERE id_level != " + Constant.USER_NON_LK + " AND id_level != " + Constant.USER_SUPER_ADMIN + " AND cabang = '"+cabang+"' AND tahun >= 1947 ";
    }

    public static String countReportKaderAdmin2(String cabang){
        return "SELECT COUNT (*) FROM Training WHERE id_level != " + Constant.USER_NON_LK + " AND id_level != " + Constant.USER_SUPER_ADMIN + " AND cabang = '"+cabang+"'";
    }
    public static String countReportKaderAdmin2L(String cabang){
        return "SELECT COUNT (*) FROM Contact WHERE id_level != " + Constant.USER_NON_LK + " AND id_level != " + Constant.USER_SUPER_ADMIN + " AND cabang = '"+cabang+"' AND jenis_kelamin = '0'";
    }
    public static String countReportKaderAdmin2P(String cabang){
        return "SELECT COUNT (*) FROM Contact WHERE id_level != " + Constant.USER_NON_LK + " AND id_level != " + Constant.USER_SUPER_ADMIN + " AND cabang = '"+cabang+"' AND jenis_kelamin = '1'";
    }

    public static String countPelatihanAdmin2(String cabang){
        return "SELECT COUNT (*) FROM Training WHERE id_level != " + Constant.USER_NON_LK + " AND id_level != " + Constant.USER_SUPER_ADMIN + " AND cabang = '"+cabang+"' AND tahun >= 1947 ";
    }

    // report kader la2 ini sama dengan second admin
    public static String ReportKaderLA2(){
        return "SELECT * FROM Contact WHERE id_level != " + Constant.USER_NON_LK + " AND id_level != " + Constant.USER_SUPER_ADMIN;
    }

    public static String reportPelatihanLA2(){
        return "SELECT * FROM Training WHERE id_level != " + Constant.USER_NON_LK + " AND id_level != " + Constant.USER_SUPER_ADMIN + " AND tahun >= 1947 ";
    }

    public static String countReportKaderLA2(){
        return "SELECT COUNT (*) FROM Training WHERE id_level != " + Constant.USER_NON_LK + " AND id_level != " + Constant.USER_SUPER_ADMIN;
    }
    public static String countReportKaderLA2L(){
        return "SELECT COUNT (*) FROM Training WHERE id_level != " + Constant.USER_NON_LK + " AND id_level != " + Constant.USER_SUPER_ADMIN + " AND jenis_kelamin = '0'";
    }
    public static String countReportKaderLA2P(){
        return "SELECT COUNT (*) FROM Training WHERE id_level != " + Constant.USER_NON_LK + " AND id_level != " + Constant.USER_SUPER_ADMIN + " AND jenis_kelamin = '1'";
    }

    public static String countPelatihanLA2(){
        return "SELECT COUNT (*) FROM Training WHERE (id_level != " + Constant.USER_NON_LK + " AND id_level != " + Constant.USER_SUPER_ADMIN + ")  AND tahun >= 1947 ";
    }

    public static String ReportAlumniAdmin3(String domisili_cabang){
        return "SELECT * FROM Contact WHERE id_level != " + Constant.USER_NON_LK + " AND id_level != " + Constant.USER_SUPER_ADMIN + " AND domisili_cabang = '"+domisili_cabang+"'";
    }

    public static String countReportAlumniAdmin3(String domisili_cabang){
        return "SELECT COUNT (*) FROM Contact WHERE id_level != " + Constant.USER_NON_LK + " AND id_level != " + Constant.USER_SUPER_ADMIN + " AND domisili_cabang = '"+domisili_cabang+"'";
    }

    public static String countReportKaderAdmin3(String domisili_cabang){
        return "SELECT COUNT (*) FROM Training WHERE id_level != " + Constant.USER_NON_LK + " AND id_level != " + Constant.USER_SUPER_ADMIN + " AND cabang = '"+domisili_cabang+"'";
    }

    public static String ReportSuperAdmin(int tahun){
        return "SELECT * FROM Contact WHERE id_level != " + Constant.USER_NON_LK + " AND id_level != " + Constant.USER_SUPER_ADMIN + " AND tahun_daftar = '"+tahun+"'";
    }
    public static String ReportSuperAdminNonLK(int tahun){
        return "SELECT * FROM Contact WHERE id_level = 1  AND tahun_daftar = '"+tahun+"'";
    }

    public static String countReportKaderLK1(int tahun, String komisariat) {
        return "SELECT COUNT (*) FROM Contact WHERE id_level != " + Constant.USER_NON_LK +
                " AND id_level != " + Constant.USER_SUPER_ADMIN + " AND tahun_lk1 = '"+tahun+"'" + "AND komisariat = '"+komisariat+"'" ;
    }

    public static String countReportKaderLK1Male(int tahun, String komisariat) {
        return countReportKaderLK1(tahun, komisariat) + " AND jenis_kelamin = '0'";
    }

    public static String countReportKaderLK1Female(int tahun, String komisariat) {
        return countReportKaderLK1(tahun, komisariat) + " AND jenis_kelamin = '1'";
    }

    public static String countReportSuperAdmin(int tahun){
        return "SELECT COUNT (*) FROM Contact WHERE id_level != " + Constant.USER_NON_LK + " AND id_level != " + Constant.USER_SUPER_ADMIN + " AND tahun_daftar = '"+tahun+"'";
    }
    public static String countReportSuperAdminNonLK(int tahun){
        return "SELECT COUNT (*) FROM Contact WHERE id_level = 1  AND tahun_daftar = '"+tahun+"'";
    }

    public static String countReportSuperAdmin(){
        return "SELECT COUNT (*) FROM Contact WHERE id_level != " + Constant.USER_NON_LK + " AND id_level != " + Constant.USER_SUPER_ADMIN + " ";
    }
    public static String countReportSuperAdminNonLK(){
        return "SELECT COUNT (*) FROM Contact WHERE id_level = 1 ";
    }

}
