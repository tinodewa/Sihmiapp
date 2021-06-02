package com.roma.android.sihmi.model.database.interfaceDao;

import com.roma.android.sihmi.model.database.entity.Alamat;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface AlamatDao {
    @Insert(onConflict = REPLACE)
    void insertAlamat (Alamat alamat);

    @Insert (onConflict = REPLACE)
    void insertAlamat (List<Alamat> alamats);

    @Query("SELECT * FROM Alamat")
    LiveData<List<Alamat>> getAllAlamat();

    @Query("SELECT * FROM Alamat WHERE _id LIKE :id LIMIT 1")
    Alamat getAlamatById(String id);

    @Query("SELECT * FROM Alamat WHERE nama LIKE :name")
    List<Alamat> getSearchAlamat(String name);

    @Query("DELETE FROM Alamat WHERE _id LIKE :id")
    void deleteAlamatById(String id);

    @Query("DELETE FROM Alamat")
    void deleteAlamat();
}
