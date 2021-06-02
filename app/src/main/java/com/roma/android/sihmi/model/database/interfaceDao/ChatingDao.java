package com.roma.android.sihmi.model.database.interfaceDao;

import com.roma.android.sihmi.model.database.entity.Chatting;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface ChatingDao {
    // Chating
    @Insert(onConflict = REPLACE)
    void insertChating(Chatting chatting);

    @Query("SELECT * FROM Chatting ORDER BY time_message DESC")
    LiveData<List<Chatting>> getListChating();

    @Query("SELECT * FROM Chatting WHERE _id = :id LIMIT 1")
    Chatting getChatingById(String id);

    @Query("SELECT * FROM Chatting WHERE name LIKE :name ORDER BY time_message DESC")
    List<Chatting> getSearchChating(String name);

}
