package com.roma.android.sihmi.model.database.interfaceDao;

import com.roma.android.sihmi.model.database.entity.User;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface UserDao {
    @Insert(onConflict = REPLACE)
    void insertUser(User user);

    @Query("SELECT * FROM User ORDER BY _id ASC")
    LiveData<List<User>> getAllUser();

    @Query("SELECT * FROM User ORDER BY _id ASC")
    User getUser();

    @Query("SELECT image FROM user WHERE _id = :id")
    LiveData<String> getImageLiveData(String id);

    @Query("UPDATE User SET image=:image WHERE _id = :id")
    void updatePhoto(String id, String image);

    @Query("UPDATE User SET nama_depan=:namadepan, nama_panggilan=:nama, jenis_kelamin=:jenis_kelamin, nomor_hp=:nomor, alamat=:alamat WHERE _id = :id")
    void updateProfile(String id, String namadepan, String nama, String jenis_kelamin, String nomor, String alamat);
}
