package com.roma.android.sihmi.model.database.interfaceDao;

import com.roma.android.sihmi.model.database.entity.PengajuanHistory;
import com.roma.android.sihmi.model.database.entity.PengajuanHistoryJoin;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface HistoryPengajuanDao {
    @Insert(onConflict = REPLACE)
    void insertPengajuanHistory(PengajuanHistory pengajuanHistory);

    @Query("SELECT file FROM PengajuanHistory WHERE _id = :idPengajuan AND (status == 0 OR status == -1) AND level != 2 LIMIT 1")
    String getFilePengajuanHistory(String idPengajuan);

    @Query("UPDATE PengajuanHistory SET id_roles= :idRoles AND level = 1 WHERE _id = :id")
    void updatePengajuanUser(String id, String idRoles);

    @Query("SELECT * FROM PengajuanHistory WHERE _id = :id_pengajuan LIMIT 1")
    PengajuanHistory getPengajuanHistoryById(String id_pengajuan);

    @Query("SELECT * FROM PengajuanHistory WHERE (status == 0) AND level = 2 ORDER BY date_created DESC")
    LiveData<List<PengajuanHistory>> getAllPengajuanAnggota();

    @Query("SELECT level FROM PengajuanHistory WHERE _id = :id LIMIT 1")
    int getLevelByIdPengajuanHistory(String id);

    @Query("UPDATE PengajuanHistory SET status=1, date_modified=JULIANDAY('NOW') WHERE _id = :id")
    void updateApprovePengajuan(String id);

    @Query("SELECT * FROM PengajuanHistory WHERE status = 1 AND created_by = :createBy ORDER BY date_modified DESC LIMIT 1")
    PengajuanHistory getSuccessPengajuan(String createBy);

    @Query("SELECT * FROM PengajuanHistory WHERE status = 0 AND created_by = :createBy ORDER BY date_created DESC LIMIT 1")
    PengajuanHistory getOnProgressPengajuan(String createBy);

    @Query("SELECT ph.*, plk.cabang, plk.komisariat FROM PengajuanHistory AS ph LEFT JOIN PengajuanLK1 AS plk on ph._id = plk._id WHERE ph.status = 0 ORDER BY ph.date_created DESC")
    LiveData<List<PengajuanHistoryJoin>> getAllPengajuanHistory();

    @Query("SELECT ph.*, plk.cabang, plk.komisariat FROM PengajuanHistory AS ph LEFT JOIN PengajuanLK1 AS plk on ph._id = plk._id WHERE ph.status = 0 AND (nama LIKE :nama OR plk.komisariat LIKE :nama OR plk.cabang LIKE :nama) ORDER BY ph.date_created DESC")
    List<PengajuanHistoryJoin> getSearchAllPengajuanHistory(String nama);

    @Query("SELECT ph.*, plk.cabang, plk.komisariat FROM PengajuanHistory AS ph LEFT JOIN PengajuanLK1 AS plk on ph._id = plk._id WHERE ph.status = 0 ORDER BY ph.date_created DESC")
    List<PengajuanHistoryJoin> getAllListPengajuanHistory();

    @Query("SELECT ph.*, plk.cabang, plk.komisariat FROM PengajuanHistory AS ph LEFT JOIN PengajuanLK1 AS plk on ph._id = plk._id WHERE ph.status = 0 AND ph.level != 2 ORDER BY ph.date_created DESC")
    LiveData<List<PengajuanHistoryJoin>> getAllPengajuanHistorySecondAdmin();

    @Query("SELECT ph.*, plk.cabang, plk.komisariat FROM PengajuanHistory AS ph LEFT JOIN PengajuanLK1 AS plk on ph._id = plk._id WHERE ph.status = 0 AND ph.level != 2 AND (nama LIKE :nama OR plk.komisariat LIKE :nama OR plk.cabang LIKE :nama) ORDER BY ph.date_created DESC")
    List<PengajuanHistoryJoin> getSearchAllPengajuanHistorySecondAdmin(String nama);

    @Query("SELECT ph.*, plk.cabang, plk.komisariat FROM PengajuanHistory AS ph LEFT JOIN PengajuanLK1 AS plk on ph._id = plk._id WHERE ph.status = 0 AND ph.level != 2 ORDER BY ph.date_created DESC")
    List<PengajuanHistoryJoin> getAllListPengajuanHistorySecondAdmin();

    @Query("SELECT ph.*, plk.cabang, plk.komisariat FROM PengajuanHistory AS ph LEFT JOIN PengajuanLK1 AS plk on ph._id = plk._id WHERE ph.status = 0 AND ph.level <= :level ORDER BY ph.date_created DESC")
    List<PengajuanHistoryJoin> getAllListPengajuanHistory(int level);

    @Query("SELECT ph.*, plk.cabang, plk.komisariat FROM PengajuanHistory AS ph LEFT JOIN PengajuanLK1 AS plk on ph._id = plk._id WHERE ph.status = 0 AND ph.level <= :level ORDER BY ph.date_created DESC")
    LiveData<List<PengajuanHistoryJoin>> getAllPengajuanHistory(int level);

    @Query("SELECT ph.*, plk.cabang, plk.komisariat FROM PengajuanHistory AS ph LEFT JOIN PengajuanLK1 AS plk on ph._id = plk._id WHERE ph.status = 0 AND ph.level <= :level AND nama LIKE :nama ORDER BY ph.date_created DESC")
    List<PengajuanHistoryJoin> getSearchAllPengajuanHistory(int level, String nama);

    @Query("SELECT ph.*, plk.cabang, plk.komisariat FROM PengajuanHistory AS ph LEFT JOIN PengajuanLK1 AS plk on ph._id = plk._id WHERE ph.status = 0 AND ph.level = 2 AND plk.komisariat = :komisariat ORDER BY ph.date_created DESC")
    LiveData<List<PengajuanHistoryJoin>> getAllPengajuanHistoryAdmin1(String komisariat);

    @Query("SELECT ph.*, plk.cabang, plk.komisariat FROM PengajuanHistory AS ph LEFT JOIN PengajuanLK1 AS plk on ph._id = plk._id WHERE ph.status = 0 AND ph.level = 2 AND plk.komisariat = :komisariat ORDER BY ph.date_created DESC")
    List<PengajuanHistoryJoin> getAllListPengajuanHistoryAdmin1(String komisariat);

    @Query("SELECT ph.*, plk.cabang, plk.komisariat FROM PengajuanHistory AS ph LEFT JOIN PengajuanLK1 AS plk on ph._id = plk._id WHERE ph.status = 0 AND ph.level = 2 AND plk.komisariat = :komisariat AND nama LIKE :nama ORDER BY ph.date_created DESC")
    List<PengajuanHistoryJoin> getSearchAllPengajuanHistoryAdmin1(String komisariat, String nama);

    @Query("SELECT ph.*, plk.cabang, plk.komisariat FROM PengajuanHistory AS ph LEFT JOIN PengajuanLK1 AS plk on ph._id = plk._id WHERE ph.status = 0 AND (ph.level = 3 OR ph.level = 4) AND plk.cabang = :cabang ORDER BY ph.date_created DESC")
    LiveData<List<PengajuanHistoryJoin>> getAllPengajuanHistoryLA1(String cabang);

    @Query("SELECT ph.*, plk.cabang, plk.komisariat FROM PengajuanHistory AS ph LEFT JOIN PengajuanLK1 AS plk on ph._id = plk._id WHERE ph.status = 0 AND (ph.level = 3 OR ph.level = 4) AND plk.cabang = :cabang ORDER BY ph.date_created DESC")
    List<PengajuanHistoryJoin> getAllListPengajuanHistoryLA1(String cabang);

    @Query("SELECT ph.*, plk.cabang, plk.komisariat FROM PengajuanHistory AS ph LEFT JOIN PengajuanLK1 AS plk on ph._id = plk._id WHERE ph.status = 0 AND (ph.level = 3 OR ph.level = 4) AND plk.cabang = :cabang AND (nama LIKE :nama OR plk.komisariat LIKE :nama) ORDER BY ph.date_created DESC")
    List<PengajuanHistoryJoin> getSearchAllPengajuanHistoryLA1(String cabang, String nama);

    @Query("SELECT ph.*, plk.cabang, plk.komisariat FROM PengajuanHistory AS ph LEFT JOIN PengajuanLK1 AS plk on ph._id = plk._id WHERE ph.status = 0 AND ph.level = 6 ORDER BY ph.date_created DESC")
    LiveData<List<PengajuanHistoryJoin>> getAllPengajuanHistoryLA2();

    @Query("SELECT ph.*, plk.cabang, plk.komisariat FROM PengajuanHistory AS ph LEFT JOIN PengajuanLK1 AS plk on ph._id = plk._id WHERE ph.status = 0 AND ph.level = 6 ORDER BY ph.date_created DESC")
    List<PengajuanHistoryJoin> getAllListPengajuanHistoryLA2();

    @Query("SELECT ph.*, plk.cabang, plk.komisariat FROM PengajuanHistory AS ph LEFT JOIN PengajuanLK1 AS plk on ph._id = plk._id WHERE ph.status = 0 AND ph.level = 6 AND (nama LIKE :nama OR plk.komisariat LIKE :nama OR plk.cabang LIKE :nama) ORDER BY ph.date_created DESC")
    List<PengajuanHistoryJoin> getSearchAllPengajuanHistoryLA2(String nama);

    @Query("SELECT ph.*, plk.cabang, plk.komisariat FROM PengajuanHistory AS ph LEFT JOIN PengajuanLK1 AS plk on ph._id = plk._id WHERE ph.status = 0 AND ph.level = 2 AND plk.cabang = :cabang AND ph.tanggal_lk1 LIKE :dateNow ORDER BY ph.date_created DESC")
    LiveData<List<PengajuanHistoryJoin>> getAllPengajuanHistoryAdmin2(String cabang, String dateNow);

    @Query("SELECT ph.*, plk.cabang, plk.komisariat FROM PengajuanHistory AS ph LEFT JOIN PengajuanLK1 AS plk on ph._id = plk._id WHERE ph.status = 0 AND ph.level = 2 AND plk.cabang = :cabang AND ph.tanggal_lk1 LIKE :dateNow ORDER BY ph.date_created DESC")
    List<PengajuanHistoryJoin> getAllListPengajuanHistoryAdmin2(String cabang, String dateNow);

    @Query("SELECT ph.*, plk.cabang, plk.komisariat FROM PengajuanHistory AS ph LEFT JOIN PengajuanLK1 AS plk on ph._id = plk._id WHERE ph.status = 0 AND ph.level = 2 AND plk.cabang = :cabang AND ph.tanggal_lk1 LIKE :dateNow AND (nama LIKE :nama OR plk.komisariat LIKE :nama) ORDER BY ph.date_created DESC")
    List<PengajuanHistoryJoin> getSearchAllPengajuanHistoryAdmin2(String cabang, String dateNow, String nama);

}
