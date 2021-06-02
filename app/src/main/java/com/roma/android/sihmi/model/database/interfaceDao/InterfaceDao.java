package com.roma.android.sihmi.model.database.interfaceDao;


import androidx.room.Dao;
import androidx.room.Query;


@Dao
public interface InterfaceDao {

    //Nasional
    @Query("SELECT COUNT(_id) FROM Contact")
    int getCountAllUser();

    @Query("SELECT COUNT(_id) FROM Contact WHERE badko IS NOT NULL AND badko NOT LIKE '' AND badko NOT LIKE ' ' AND tahun_daftar = :year")
    int getCountAllBadkoByYear (String year);

    @Query("SELECT COUNT(_id) FROM Contact WHERE badko = :badko AND tahun_daftar = :year")
    int getCountBadkoByYear(String badko, String year);

    @Query("SELECT COUNT(_id) FROM Contact WHERE badko IS NOT NULL AND badko NOT LIKE '' AND badko NOT LIKE ' ' AND jenis_kelamin = :gender AND tahun_daftar = :year")
    int getCountAllBadkoByGender(String gender, String year);

    //Cabang
    @Query("SELECT COUNT(_id) FROM Contact WHERE komisariat IS NOT NULL AND komisariat NOT LIKE '' AND komisariat NOT LIKE ' ' ")
    int getCountAllKomisariat();

    @Query("SELECT COUNT(_id) FROM Contact WHERE domisili_cabang IS NOT NULL AND domisili_cabang NOT LIKE '' AND domisili_cabang NOT LIKE ' ' ")
    int getCountAllAlumni();

}
