package com.roma.android.sihmi.model.database.interfaceDao;

import com.roma.android.sihmi.model.database.entity.PengajuanLK1;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface PengajuanLK1Dao {
    @Insert(onConflict = REPLACE)
    void insertPengajuan(PengajuanLK1 pengajuanLK1);

    @Query("SELECT * FROM PengajuanLK1 WHERE _id = :id")
    PengajuanLK1 getPengajuanLK1ById(String id);
}
