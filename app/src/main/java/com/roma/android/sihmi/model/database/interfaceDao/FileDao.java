package com.roma.android.sihmi.model.database.interfaceDao;

import com.roma.android.sihmi.model.database.entity.File;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface FileDao {
    //File
    @Insert(onConflict = REPLACE)
    void insertFile (List<File> files);

    @Query("SELECT * FROM File")
    LiveData<List<File>> getAllFile();
}
