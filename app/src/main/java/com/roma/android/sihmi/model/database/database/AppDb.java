package com.roma.android.sihmi.model.database.database;


import android.content.Context;

import com.roma.android.sihmi.model.database.entity.AboutUs;
import com.roma.android.sihmi.model.database.entity.Agenda;
import com.roma.android.sihmi.model.database.entity.Alamat;
import com.roma.android.sihmi.model.database.entity.Chatting;
import com.roma.android.sihmi.model.database.entity.Contact;
import com.roma.android.sihmi.model.database.entity.File;
import com.roma.android.sihmi.model.database.entity.GroupChat;
import com.roma.android.sihmi.model.database.entity.Job;
import com.roma.android.sihmi.model.database.entity.Konstituisi;
import com.roma.android.sihmi.model.database.entity.Leader;
import com.roma.android.sihmi.model.database.entity.Level;
import com.roma.android.sihmi.model.database.entity.LoadDataState;
import com.roma.android.sihmi.model.database.entity.Master;
import com.roma.android.sihmi.model.database.entity.Medsos;
import com.roma.android.sihmi.model.database.entity.Pendidikan;
import com.roma.android.sihmi.model.database.entity.Pengajuan;
import com.roma.android.sihmi.model.database.entity.PengajuanHistory;
import com.roma.android.sihmi.model.database.entity.PengajuanLK1;
import com.roma.android.sihmi.model.database.entity.Sejarah;
import com.roma.android.sihmi.model.database.entity.Training;
import com.roma.android.sihmi.model.database.entity.User;
import com.roma.android.sihmi.model.database.interfaceDao.AboutUsDao;
import com.roma.android.sihmi.model.database.interfaceDao.AgendaDao;
import com.roma.android.sihmi.model.database.interfaceDao.AlamatDao;
import com.roma.android.sihmi.model.database.interfaceDao.ChatingDao;
import com.roma.android.sihmi.model.database.interfaceDao.ContactDao;
import com.roma.android.sihmi.model.database.interfaceDao.GroupChatDao;
import com.roma.android.sihmi.model.database.interfaceDao.HistoryPengajuanDao;
import com.roma.android.sihmi.model.database.interfaceDao.InterfaceDao;
import com.roma.android.sihmi.model.database.interfaceDao.KonstitusiDao;
import com.roma.android.sihmi.model.database.interfaceDao.LeaderDao;
import com.roma.android.sihmi.model.database.interfaceDao.LevelDao;
import com.roma.android.sihmi.model.database.interfaceDao.LoadDataStateDao;
import com.roma.android.sihmi.model.database.interfaceDao.MasterDao;
import com.roma.android.sihmi.model.database.interfaceDao.PendidikanDao;
import com.roma.android.sihmi.model.database.interfaceDao.PengajuanLK1Dao;
import com.roma.android.sihmi.model.database.interfaceDao.SejarahDao;
import com.roma.android.sihmi.model.database.interfaceDao.TrainingDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(
        entities = {
            AboutUs.class,
            Agenda.class,
            Alamat.class,
            Chatting.class,
            Contact.class,
            File.class,
            GroupChat.class,
            Job.class,
            Konstituisi.class,
            Leader.class,
            Level.class,
            Master.class,
            Medsos.class,
            Pendidikan.class,
            Pengajuan.class,
            PengajuanHistory.class,
            Sejarah.class,
            Training.class,
            User.class,
            LoadDataState.class,
            PengajuanLK1.class
        },
        version = 28,
        exportSchema = false)
public abstract class AppDb extends RoomDatabase {
    private static volatile AppDb instance = null;

    public abstract AboutUsDao aboutUsDao();
    public abstract AgendaDao agendaDao();
    public abstract AlamatDao alamatDao();
    public abstract ChatingDao chatingDao();
    public abstract ContactDao contactDao();
    public abstract GroupChatDao groupChatDao();
    public abstract HistoryPengajuanDao historyPengajuanDao();
    public abstract InterfaceDao interfaceDao();
    public abstract KonstitusiDao konstitusiDao();
    public abstract LeaderDao leaderDao();
    public abstract LevelDao levelDao();
    public abstract MasterDao masterDao();
    public abstract SejarahDao sejarahDao();
    public abstract TrainingDao trainingDao();
    public abstract UserDao userDao();
    public abstract LoadDataStateDao loadDataStateDao();
    public abstract PengajuanLK1Dao pengajuanLK1Dao();
    public abstract PendidikanDao pendidikanDao();

    public static AppDb getInstance(Context context){
        if (instance == null){
            synchronized (AppDb.class){
                instance = Room.databaseBuilder(context.getApplicationContext(), AppDb.class,"sihmi_database")
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .addMigrations(MIGRATION_22_23, MIGRATION_23_24, MIGRATION_24_25, MIGRATION_25_26, MIGRATION_26_27)
                        .build();
            }
        }
        return instance;
    }

    private static final Migration MIGRATION_22_23 = new Migration(22, 23) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Contact ADD COLUMN dateRole INTEGER NOT NULL DEFAULT 0");
        }
    };

    private static final Migration MIGRATION_23_24 = new Migration(23, 24) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE LoadDataState (" +
                    "id INTEGER PRIMARY KEY NOT NULL," +
                    "isLoaded INTEGER DEFAULT 0 NOT NULL," +
                    "timeLoaded INTEGER DEFAULT 0 NOT NULL" +
                    ")");
        }
    };

    private static final Migration MIGRATION_24_25 = new Migration(24, 25) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE PengajuanLK1 (" +
                    "_id TEXT PRIMARY KEY NOT NULL," +
                    "badko TEXT," +
                    "cabang TEXT," +
                    "korkom TEXT," +
                    "komisariat TEXT," +
                    "tanggal_lk1 TEXT," +
                    "tahun_lk1 TEXT," +
                    "created_by TEXT," +
                    "modified_by TEXT," +
                    "date_created INTEGER NOT NULL DEFAULT 0," +
                    "date_modified INTEGER NOT NULL DEFAULT 0," +
                    "status INTEGER NOT NULL DEFAULT 0" +
                    ")");
        }
    };

    private static final Migration MIGRATION_25_26 = new Migration(25, 26) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE GroupChat " +
                    "ADD bisukan INTEGER NOT NULL DEFAULT 0");

            database.execSQL("ALTER TABLE Master " +
                    "ADD parentId TEXT NOT NULL DEFAULT ''");
        }
    };

    private static final Migration MIGRATION_26_27 = new Migration(26, 27) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.beginTransaction();

            database.execSQL("ALTER TABLE Pendidikan RENAME TO Pendidikan_temp");

            database.execSQL("CREATE TABLE Pendidikan (" +
                    "_id TEXT PRIMARY KEY NOT NULL," +
                    "id_user TEXT," +
                    "tahun TEXT," +
                    "strata TEXT," +
                    "jurusan TEXT," +
                    "nama_kampus TEXT" +
                    ")");

            database.execSQL("INSERT INTO Pendidikan (_id, id_user, tahun, strata, jurusan, nama_kampus) " +
                    "SELECT _id, id_user, tahun, strata, jurusan, nama_kampus " +
                    "FROM Pendidikan_temp");

            database.execSQL("DROP TABLE Pendidikan_temp");

            database.setTransactionSuccessful();
            database.endTransaction();
        }
    };

//    private static final Migration MIGRATION_27_28 = new Migration(27, 28) {
//        @Override
//        public void migrate(@NonNull SupportSQLiteDatabase database) {
//
//        }
//    };
}
