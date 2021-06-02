package com.roma.android.sihmi.model.database.interfaceDao;

import com.roma.android.sihmi.model.database.entity.Leader;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface LeaderDao {
    //Leader
    @Insert(onConflict =  REPLACE)
    void insertLeader(List<Leader> list);

    @Query("SELECT * FROM Leader")
    List<Leader> getAllLeader();

    @Query("SELECT * FROM Leader WHERE type LIKE '0-PB HMI' ORDER BY periode ASC")
    LiveData<List<Leader>> getLeaderNasional();

    @Query("SELECT * FROM Leader WHERE type LIKE :type ORDER BY periode ASC")
    LiveData<List<Leader>> getLeaderByType(String type);

    @Query("SELECT * FROM Leader WHERE nama LIKE :name")
    List<Leader> getSearchLeader(String name);

    @Query("SELECT * FROM Leader WHERE type LIKE :type AND nama LIKE :name")
    List<Leader> getSearchLeaderNasional(String type, String name);

    @Query("SELECT * FROM Leader WHERE type LIKE :type AND nama LIKE :name OR type LIKE :name")
    List<Leader> getSearchLeaderByType(String type, String name);

    @Query("SELECT * FROM Leader WHERE _id = :id LIMIT 1")
    Leader getLeaderById(String id);

    @Query("DELETE FROM Leader WHERE _id LIKE :id")
    void deleteLeaderById(String id);
}
