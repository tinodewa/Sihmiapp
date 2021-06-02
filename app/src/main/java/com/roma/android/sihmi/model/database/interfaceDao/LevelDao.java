package com.roma.android.sihmi.model.database.interfaceDao;

import com.roma.android.sihmi.model.database.entity.Level;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface LevelDao {
    @Insert(onConflict = REPLACE)
    void insertLevel(Level level);

    @Query("SELECT id FROM Level WHERE idLevel LIKE :id_roles")
    int getPengajuanLevel(String id_roles);

    @Query("SELECT level FROM Level WHERE idLevel LIKE :id_roles")
    int getLevel(String id_roles);

    @Query("SELECT newName FROM Level WHERE idLevel LIKE :id_roles")
    String getNamaLevel(String id_roles);

    @Query("SELECT idLevel FROM level WHERE newName LIKE :nama")
    String getIdRoles(String nama);

    @Query("SELECT idLevel FROM level WHERE level = :level")
    String getIdRoles(int level);

    @Query("SELECT nama FROM Level WHERE level LIKE :level")
    String getNamaLevel(int level);

    @Query("SELECT id FROM Level WHERE idLevel = :id_roles")
    int getIdLevelByIdRole(String id_roles);
}
