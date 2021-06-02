package com.roma.android.sihmi.model.database.interfaceDao;

import com.roma.android.sihmi.model.database.entity.Konstituisi;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface KonstitusiDao {
    @Insert(onConflict = REPLACE)
    void insertKonstituisi(List<Konstituisi> konstituisis);

    @Query("SELECT * FROM Konstituisi ORDER BY date_created DESC")
    LiveData<List<Konstituisi>> getAllKonstitusi();

    @Query("SELECT * FROM Konstituisi WHERE type LIKE '0-PB HMI' ORDER BY date_created DESC")
    LiveData<List<Konstituisi>> getKonstitusiNasional();

    @Query("SELECT * FROM Konstituisi WHERE type LIKE '0-PB HMI' OR type LIKE :type ORDER BY date_created DESC")
    LiveData<List<Konstituisi>> getKonstitusiNasAndKoms(String type);

    @Query("SELECT * FROM Konstituisi ORDER BY date_created DESC")
    List<Konstituisi> getAllKonstituisii();

    @Query("SELECT * FROM Konstituisi WHERE _id LIKE :id")
    Konstituisi getKonstituisibyId(String id);

    @Query("SELECT * FROM Konstituisi WHERE nama LIKE :name OR type LIKE :name ORDER BY date_created DESC")
    List<Konstituisi> getSearchKonstituisi(String name);

    @Query("SELECT * FROM Konstituisi WHERE type LIKE '0-PB HMI' AND nama LIKE :name ORDER BY date_created DESC")
    List<Konstituisi> getSearchKonstituisiNasional(String name);

    @Query("SELECT * FROM Konstituisi WHERE (type LIKE '0-PB HMI' OR type LIKE :type) AND nama LIKE :name OR type LIKE :name ORDER BY date_created DESC")
    List<Konstituisi> getSearchKonstituisiNasAndKoms(String type, String name);

    @Query("DELETE FROM Konstituisi WHERE _id LIKE :id")
    void deleteKonstituisiById(String id);
}
