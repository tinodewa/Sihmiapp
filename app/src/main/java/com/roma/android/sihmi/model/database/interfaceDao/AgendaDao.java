package com.roma.android.sihmi.model.database.interfaceDao;

import com.roma.android.sihmi.model.database.entity.Agenda;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface AgendaDao {
    @Insert(onConflict = REPLACE)
    void insertAgenda (Agenda agenda);

    @Insert (onConflict = REPLACE)
    void insertAgenda (List<Agenda> agenda);

    @Query("SELECT * FROM Agenda ORDER BY date_created DESC")
    LiveData<List<Agenda>> getAllAgenda();

    @Query("SELECT * FROM Agenda WHERE type LIKE '0-PB HMI' ORDER BY date_created DESC")
    LiveData<List<Agenda>> getAgendaNasional();

    @Query("SELECT * FROM Agenda WHERE type LIKE '0-PB HMI' OR type LIKE :typeCab OR type LIKE :typeKoms ORDER BY date_created DESC")
    LiveData<List<Agenda>> getAgendaNasCabKoms(String typeCab, String typeKoms);

    @Query("SELECT * FROM Agenda WHERE type = :type ORDER BY date_created DESC")
    LiveData<List<Agenda>> getAgendaByType(int type);

    @Query("SELECT * FROM Agenda WHERE _id LIKE :id LIMIT 1")
    Agenda getAgendaById(String id);

    @Query("SELECT * FROM Agenda WHERE nama LIKE :name")
    List<Agenda> getSearchAgenda(String name);

    @Query("SELECT * FROM Agenda WHERE type LIKE '0-PB HMI' AND nama LIKE :name")
    List<Agenda> getSearchAgendaNasional(String name);

    @Query("SELECT * FROM Agenda WHERE (type LIKE '0-PB HMI' OR type LIKE :typeCab OR type LIKE :typeKoms) AND nama LIKE :name")
    List<Agenda> getSearchAgendaNasCabKoms(String typeCab, String typeKoms, String name);

    @Query("UPDATE Agenda SET isReminder=:reminder WHERE _id = :id")
    void updateReminderAgenda(String id, boolean reminder);

    @Query("DELETE FROM Agenda WHERE _id LIKE :id")
    void deleteAgendaById(String id);

    @Query("SELECT * FROM Agenda WHERE date_expired >= :currentDay AND isReminder = 1 ORDER BY date_expired LIMIT 1")
    Agenda getUpcomingAgenda(long currentDay);

    @Query("DELETE FROM Agenda WHERE _id NOT IN (:agendaIds)")
    void deleteUnusedAgenda(List<String> agendaIds);

    @Query("DELETE FROM Agenda")
    void deleteAgenda();
}
