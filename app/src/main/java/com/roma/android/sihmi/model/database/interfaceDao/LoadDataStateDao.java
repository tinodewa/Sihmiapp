package com.roma.android.sihmi.model.database.interfaceDao;

import com.roma.android.sihmi.model.database.entity.LoadDataState;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface LoadDataStateDao {
    @Insert(onConflict = REPLACE)
    void insertLoadDataState(LoadDataState loadDataState);

    @Query("SELECT * FROM LoadDataState ORDER BY timeLoaded DESC LIMIT 1")
    LoadDataState getLoadDataState();

    @Query("SELECT isLoaded FROM LoadDataState ORDER BY timeLoaded DESC LIMIT 1")
    LiveData<Boolean> getIsLoadedLiveData();

    @Query("SELECT isLoaded FROM LoadDataState ORDER BY timeLoaded DESC LIMIT 1")
    boolean getIsLoaded();
}
