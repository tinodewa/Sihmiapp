package com.roma.android.sihmi.utils;

import android.util.Log;

import androidx.sqlite.db.SimpleSQLiteQuery;

public class QueryRoomDao {


    public static SimpleSQLiteQuery getUser(String type, String value, String level, String gender, String year){
        String query = "SELECT * FROM Contact";
        String kondisi;

        if (type.equals(Constant.M_NASIONAL)){
            kondisi = "";
        } else if (type.equals(Constant.M_BADKO)){
            if (value.isEmpty()){
                kondisi = " WHERE badko IS NOT NULL   AND   badko NOT LIKE ''   AND   badko NOT LIKE ' '";
            } else {
                kondisi = " WHERE badko IS NOT NULL   AND   badko NOT LIKE ''   AND   badko NOT LIKE ' ' AND badko = '"+value+"'";
            }
        } else if (type.equals(Constant.M_CABANG)){
            if (value.isEmpty()){
                kondisi = " WHERE cabang IS NOT NULL   AND   cabang NOT LIKE ''   AND   cabang NOT LIKE ' '";
            } else {
                kondisi = " WHERE cabang IS NOT NULL   AND   cabang NOT LIKE ''   AND   cabang NOT LIKE ' ' AND cabang = '"+value+"'";
            }
        } else if (type.equals(Constant.M_KORKOM)){
            if (value.isEmpty()){
                kondisi = " WHERE korkom IS NOT NULL   AND   korkom NOT LIKE ''   AND   korkom NOT LIKE ' '";
            } else {
                kondisi = " WHERE korkom IS NOT NULL   AND   korkom NOT LIKE ''   AND   korkom NOT LIKE ' ' AND korkom = '"+value+"'";
            }
        } else if (type.equals(Constant.M_KOMISARIAT)){
            if (value.isEmpty()){
                kondisi = " WHERE komisariat IS NOT NULL   AND   komisariat NOT LIKE ''   AND   komisariat NOT LIKE ' '";
            } else {
                kondisi = " WHERE komisariat IS NOT NULL   AND   komisariat NOT LIKE ''   AND   komisariat NOT LIKE ' ' AND komisariat = '"+value+"'";
            }
        } else if (type.equals(Constant.M_ALUMNI)){
            if (value.isEmpty()){
                kondisi = " WHERE domisili_cabang IS NOT NULL   AND   domisili_cabang NOT LIKE ''   AND   domisili_cabang NOT LIKE ' '";
            } else {
                kondisi = " WHERE domisili_cabang IS NOT NULL   AND   domisili_cabang NOT LIKE ''   AND   domisili_cabang NOT LIKE ' ' AND domisili_cabang = '"+value+"'";
            }
        } else {
            kondisi = "";
        }

        String kondisilevel;
        if (level.isEmpty()){
            if (kondisi.isEmpty()){
                kondisilevel = " WHERE id_level != '19' AND id_level != '20' AND id_level != 1";
            } else {
                kondisilevel = " AND id_level != '19' AND id_level != '20' AND id_level != 1";
            }
        } else {
            if (kondisi.isEmpty()){
                kondisilevel = " WHERE id_level = '"+level+"'";
            } else {
                if (level.equals("1")){
                    kondisilevel = " AND id_level = '"+level+"'";
                } else {
                    kondisilevel = " AND id_level != '1' AND id_level != '19' AND id_level != '20'";
                }
            }
        }

        String kondisigender;
        if (gender.isEmpty()){
            kondisigender = "";
        } else {
            kondisigender = " AND jenis_kelamin = '"+gender+"'";
        }

        String kondisiyear;
        if (year.isEmpty()){
            kondisiyear = "";
        } else {
            kondisiyear = " AND tahun_daftar = '"+year+"'";
        }

        Log.d("romatest", "getUser: " + query + kondisi + kondisilevel + kondisigender + kondisiyear + ";");
        return new SimpleSQLiteQuery(query+kondisi+kondisilevel+kondisiyear+";");
    }

    public static SimpleSQLiteQuery getTraining(String tipe, String cabang, String gender, String year){
        String  query = "SELECT * FROM Training WHERE id_level != " + Constant.USER_NON_LK + " AND id_level != " + Constant.USER_SUPER_ADMIN + " AND " +
                "(tahun >= 1947) ";

        if (!tipe.isEmpty()){
            query += " AND tipe = '"+tipe+"' ";
        }

        if (!cabang.isEmpty()){
            query += " AND cabang = '"+cabang+"' ";
        }

        if (!gender.isEmpty()){
            query += " AND jenis_kelamin = '+"+gender+"' ";
        }

        if (!year.isEmpty()){
            query += " AND tahun = '+"+year+"' ";
        }
        query += ";";
        return new SimpleSQLiteQuery(query);
    }

}
