package com.roma.android.sihmi.model.database.interfaceDao;

import com.roma.android.sihmi.model.database.entity.Contact;
import com.roma.android.sihmi.model.database.entity.UserCount;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface ContactDao {
    @RawQuery
    List<Contact> rawQueryContact(SupportSQLiteQuery query);

    @RawQuery
    int countRawQueryContact(SupportSQLiteQuery query);

    @Insert(onConflict = REPLACE)
    void insertContact (List<Contact> contacts);

    @Insert (onConflict = REPLACE)
    void insertContact (Contact contacts);

    @Query("SELECT * FROM Contact WHERE id_level == 1 OR id_level == 2 ORDER BY lower(nama_depan) ASC")
    LiveData<List<Contact>> getAllContactMember();

    @Query("SELECT * FROM Contact ORDER BY lower(nama_depan) ASC")
    List<Contact> getAllListContact();

    @Query("SELECT * FROM Contact WHERE id_level == 1 OR id_level == 2 ORDER BY lower(nama_depan) ASC")
    List<Contact> getAllListNonAdminUser();

    @Query("SELECT * FROM Contact WHERE _id NOT LIKE :id AND id_level != 20 ORDER BY lower(nama_depan) ASC")
    List<Contact> getAllListContact(String id);

    @Query("SELECT * FROM Contact WHERE _id NOT LIKE :id AND id_level != 20 AND nama_depan LIKE :nama OR komisariat LIKE :nama ORDER BY lower(nama_depan) ASC")
    List<Contact> getSearchListContact(String id, String nama);

    @Query("SELECT * FROM Contact WHERE _id LIKE :id")
    Contact getContactById(String id);

    @Query("SELECT * FROM Contact WHERE _id LIKE :id")
    LiveData<Contact> getContactLiveDataById(String id);

    @Query("SELECT _id FROM Contact WHERE nama_depan LIKE :nama")
    String getContactIdByName(String nama);

    @Query("SELECT * FROM Contact WHERE id_level == 5 OR id_level == 8 OR id_level == 11 OR id_level == 13 OR id_level == 16 OR id_level == 19 ORDER BY lower(nama_depan) ASC")
    List<Contact> getListAdmin();

    @Query("SELECT * FROM Contact WHERE id_level == 5 OR id_level == 8 OR id_level == 11 OR id_level == 13 OR id_level == 16 OR id_level == 19 ORDER BY lower(nama_depan) ASC")
    LiveData<List<Contact>> getLiveDataListAdmin();

    @Query("SELECT * FROM Contact WHERE (id_level == 5 OR id_level == 8 OR id_level == 11 OR id_level == 13 OR id_level == 16 OR id_level == 19) AND (nama_depan LIKE :name OR nama_belakang LIKE :name OR komisariat LIKE :name OR cabang LIKE :name)  ORDER BY lower(nama_depan) ASC")
    List<Contact> getSearchListAdmin(String name);

    @Query("SELECT * FROM Contact WHERE (id_level == 5 OR id_level == 8) AND cabang = :cabang ORDER BY lower(nama_depan) ASC")
    List<Contact> getListAdminForLA1(String cabang);

    @Query("SELECT * FROM Contact WHERE (id_level == 5 OR id_level == 8) AND cabang = :cabang ORDER BY lower(nama_depan) ASC")
    LiveData<List<Contact>> getLiveDataListAdminForLA1(String cabang);

    @Query("SELECT * FROM Contact WHERE (id_level == 5 OR id_level == 8) AND (nama_depan LIKE :name OR nama_belakang LIKE :name AND komisariat LIKE :name) AND cabang = :cabang ORDER BY lower(nama_depan) ASC")
    List<Contact> getSearchListAdminForLA1(String cabang, String name);

    @Query("SELECT * FROM Contact WHERE id_level == 13 ORDER BY lower(nama_depan) ASC")
    List<Contact> getListAdminForLA2();

    @Query("SELECT * FROM Contact WHERE id_level == 13 ORDER BY lower(nama_depan) ASC")
    LiveData<List<Contact>> getLiveDataListAdminForLA2();

    @Query("SELECT * FROM Contact WHERE id_level == 13 AND (nama_depan LIKE :name OR nama_belakang LIKE :name OR komisariat LIKE :name OR cabang LIKE :name)  ORDER BY lower(nama_depan) ASC")
    List<Contact> getSearchListAdminForLA2(String name);

    @Query("SELECT * FROM Contact WHERE id_level == 5 OR id_level == 8 OR id_level == 11 OR id_level == 13 OR id_level == 16 OR id_level == 19 ORDER BY lower(nama_depan) ASC")
    List<Contact> getListAdminForSecondAdmin();

    @Query("SELECT * FROM Contact WHERE id_level == 5 OR id_level == 8 OR id_level == 11 OR id_level == 13 OR id_level == 16 OR id_level == 19 ORDER BY lower(nama_depan) ASC")
    LiveData<List<Contact>> getLiveDataListAdminForSecondAdmin();

    @Query("SELECT * FROM Contact WHERE (id_level == 5 OR id_level == 8 OR id_level == 11 OR id_level == 13 OR id_level == 16 OR id_level == 19) AND (nama_depan LIKE :name OR nama_belakang LIKE :name OR komisariat LIKE :name OR cabang LIKE :name)  ORDER BY lower(nama_depan) ASC")
    List<Contact> getSearchListAdminForSecondAdmin(String name);

    @Query("SELECT * FROM Contact WHERE id_level == 2 OR id_level == 1 ORDER BY id_level DESC, lower(nama_depan)")
    LiveData<List<Contact>> getLiveDataListAllAnggota();

    @Query("SELECT * FROM Contact WHERE id_level == 2 AND komisariat = :koms ORDER BY lower(nama_depan) ASC")
    LiveData<List<Contact>> getLiveDataListAnggotaByKomisariat(String koms);

    @Query("SELECT * FROM Contact WHERE (id_level == 2 OR id_level == 1) AND (nama_depan LIKE :name OR nama_belakang LIKE :name OR komisariat LIKE :name OR cabang LIKE :name)  ORDER BY lower(nama_depan) ASC")
    List<Contact> getSearchListAllAnggota(String name);

    @Query("SELECT * FROM Contact WHERE id_level == 2 AND komisariat = :koms AND (nama_depan LIKE :name OR nama_belakang LIKE :name)  ORDER BY lower(nama_depan) ASC")
    List<Contact> getSearchListAnggotaByKomisariat(String koms, String name);

    @Query("SELECT * FROM Contact WHERE id_level == 6 OR id_level == 7 ORDER BY nama_depan ASC")
    List<Contact> getListKomisariat();

    @Query("SELECT * FROM Contact WHERE id_level == 2 OR id_level == 1 ORDER BY lower(nama_depan) ASC")
    List<Contact> getListAllAnggota();

    @Query("SELECT * FROM Contact WHERE id_level == 2 AND komisariat = :koms ORDER BY lower(nama_depan) ASC")
    List<Contact> getListAnggotaByKomisariat(String koms);

    @Query("SELECT * FROM Contact WHERE id_level == 2 AND cabang = :cabang ORDER BY lower(nama_depan) ASC")
    List<Contact> getListAnggotaByCabang(String cabang);

    @Query("SELECT * FROM Contact WHERE id_level == 2 AND cabang = :cabang AND (nama_depan LIKE :name OR nama_belakang LIKE :name OR komisariat LIKE :name) ORDER BY lower(nama_depan) ASC")
    List<Contact> getSearchListAnggotaByCabang(String cabang, String name);

    @Query("SELECT * FROM Contact WHERE id_level == 2 AND cabang = :cabang ORDER BY lower(nama_depan) ASC")
    LiveData<List<Contact>> getListLiveDataAnggotaByCabang(String cabang);

    @Query("SELECT * FROM Contact WHERE id_level == 2 ORDER BY lower(nama_depan) ASC")
    List<Contact> getListAnggota();

    @Query("SELECT * FROM Contact WHERE id_level == 2 AND (nama_depan LIKE :name OR nama_belakang LIKE :name OR komisariat LIKE :name OR cabang LIKE :name) ORDER BY lower(nama_depan) ASC")
    List<Contact> getSearchListAnggota(String name);

    @Query("SELECT * FROM Contact WHERE id_level == 2 ORDER BY lower(nama_depan) ASC")
    LiveData<List<Contact>> getListLiveDataAnggota();

    @Query("SELECT * FROM Contact WHERE id_level == 2 AND domisili_cabang = :domisili_cabang ORDER BY lower(nama_depan) ASC")
    List<Contact> getListAnggotaByDomisiliCabang(String domisili_cabang);

    @Query("SELECT * FROM Contact WHERE id_level == 2 AND domisili_cabang = :domisili_cabang AND (nama_depan LIKE :name OR nama_belakang LIKE :name OR komisariat LIKE :name) ORDER BY lower(nama_depan) ASC")
    List<Contact> getSearchListAnggotaByDomisiliCabang(String domisili_cabang, String name);

    @Query("SELECT * FROM Contact WHERE id_level == 2 AND domisili_cabang = :domisili_cabang ORDER BY lower(nama_depan) ASC")
    LiveData<List<Contact>> getListLiveDataAnggotaByDomisiliCabang(String domisili_cabang);

    @Query("UPDATE Contact SET id_roles= :idRoles, id_level = :id_level WHERE _id = :id")
    void updateRolesUser(String id, String idRoles, int id_level);

    @Query("SELECT cabang AS value, count(cabang) AS userCount FROM Contact WHERE cabang IN (SELECT value FROM MASTER WHERE parentId = :parentId) AND tahun_lk1 = :tahun_lk1 AND id_level != 1 AND id_level != 20 GROUP BY cabang")
    List<UserCount> getCabangUserCount(String parentId, String tahun_lk1);

    @Query("SELECT komisariat AS value, count(komisariat) AS userCount FROM Contact WHERE cabang = :cabang AND tahun_lk1 = :tahun_lk1 AND id_level != 1 AND id_level != 20 GROUP BY komisariat")
    List<UserCount> getKomisariatUserCount(String cabang, String tahun_lk1);
}
