package com.roma.android.sihmi.model.database.interfaceDao;

import com.roma.android.sihmi.model.database.entity.AboutUs;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface AboutUsDao {
    //About Us / Help
    @Insert(onConflict = REPLACE)
    void insertAboutUs(List<AboutUs> aboutUses);

    @Query("SELECT * FROM AboutUs")
    LiveData<List<AboutUs>> getAllAboutUs();

    @Query("SELECT * FROM AboutUs WHERE _id LIKE :id")
    AboutUs getAboutUsById(String id);

    @Query("DELETE FROM AboutUs WHERE _id LIKE :id")
    void deleteAboutById(String id);

}
