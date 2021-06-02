package com.roma.android.sihmi.model.database.interfaceDao;

import com.roma.android.sihmi.model.database.entity.Pendidikan;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface PendidikanDao {

    @Insert(onConflict = REPLACE)
    void insertManyPendidikan(List<Pendidikan> pendidikan);

    @Query("SELECT * FROM Pendidikan WHERE id_user = :userId ORDER BY tahun")
    List<Pendidikan> getPendidikanByUserId(String userId);

    @Query("SELECT * FROM Pendidikan WHERE id_user = :userId ORDER BY tahun")
    LiveData<List<Pendidikan>> getPendidikanLiveDataByUserId(String userId);
}
