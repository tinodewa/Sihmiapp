package com.roma.android.sihmi.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Account;
import com.roma.android.sihmi.model.database.interfaceDao.ContactDao;
import com.roma.android.sihmi.model.database.interfaceDao.GroupChatDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Constant {

    public static String BASE_URL = "http://45.77.247.110:8765/";

    // ID ROLES
    public static String SUPER_ADMIN = "5da699c1a61aed0fe65ae331";
    public static String SECOND_ADMIN = "5da699c1a61aed0fe65ae330";
    public static String LOW_ADMIN_2 = "5da699c1a61aed0fe65ae32d";
    public static String LOW_ADMIN_1 = "5da699c1a61aed0fe65ae32a";
    public static String ADMIN_3 = "5da699c1a61aed0fe65ae328";
    public static String ADMIN_2 = "5da699c1a61aed0fe65ae325";
    public static String ADMIN_1 = "5da699c1a61aed0fe65ae322";
    public static String LK_1 = "5da699c1a61aed0fe65ae31f";
    public static String NON_LK = "5da699c1a61aed0fe65ae31e";

    // ID
    public static String ID_KONSTITUSI = "id_konstitusi";

    public static String FAB_ADD = "fab_add";
    public static String LIHAT = "lihat";
    public static String UBAH = "ubah";
    public static String HAPUS = "hapus";
    public static String MASUK = "masuk";

    //Jenis Kade
    public static String M_NASIONAL = "Nasional";
    public static String M_BADKO = "Badko";
    public static String M_CABANG = "Cabang";
    public static String M_KORKOM = "Korkom";
    public static String M_KOMISARIAT = "Komisariat";
    public static String M_ALUMNI = "Alumni";
    public static String M_TRAINING = "Training";

    // jenis User
    public static int USER_NON_LK = 1;
    public static int USER_LK_1 = 2;
    @SuppressWarnings("unused")
    public static int USER_LK_2 = 3;
    @SuppressWarnings("unused")
    public static int USER_LK_3 = 4;
    public static int USER_ADMIN_1 = 5;
    public static int USER_KETUA_KOMISARIAT = 6;
    public static int USER_SEKUM_KOMISARIAT = 7;
    public static int USER_ADMIN_2 = 8;
    @SuppressWarnings("unused")
    public static int USER_KETUA_BPL = 9;
    @SuppressWarnings("unused")
    public static int USER_SEKUM_BPL = 10;
    public static int USER_ADMIN_3 = 11;
    @SuppressWarnings("unused")
    public static int USER_CO_ALUMNI = 12;
    public static int USER_LOW_ADMIN_1 = 13;
    @SuppressWarnings("unused")
    public static int USER_KETUA_CABANG = 14;
    @SuppressWarnings("unused")
    public static int USER_SEKUM_CABANG = 15;
    public static int USER_LOW_ADMIN_2 = 16;
    @SuppressWarnings("unused")
    public static int USER_KETUA_PBHMI = 17;
    @SuppressWarnings("unused")
    public static int USER_SEKUM_PBHMI = 18;
    public static int USER_SECOND_ADMIN = 19;
    public static int USER_SUPER_ADMIN = 20;

    //REQUEST CODE
    public static int REQUEST_PROFILE = 665;
    public static int REQUEST_KONSTITUSI = 666;
    public static int REQUEST_ALAMAT = 667;
    public static int REQUEST_AGENDA = 668;
    public static int REQUEST_GANTI_POTO = 670;
    public static int REQUEST_TENTANG_KAMI = 671;
    public static int REQUEST_BANTUAN = 672;
    public static int REQUEST_LEADER = 673;

    //Laporan
    public static int LAP_AKTIVITAS_PENGGUNA = 1;
    public static int LAP_MEDIA_PENGGUNA = 2;
    public static int LAP_ADMIN_AKTIF = 3;
    public static int LAP_APPROVE_USER = 4;
    public static int LAP_KONTEN_AGENDA = 5;
    public static int LAP_KONTEN_SEJARAH = 6;
    public static int LAP_KONTEN_KONSTITUSI = 7;
    public static int LAP_KADER = 8;

    public static int READ = 2;
    public static int UPDATE = 3;
    public static int DELETE = 4;

    // Recycler view
    public static int LIST = 0;
    public static int GRID = 1;

    // Upload File
    public static final int REQUEST_GALLERY_CODE = 200;
    public static final int REQUEST_DOC_CODE = 400;
    public static final int READ_REQUEST_CODE = 300;

    // File
    public static final String IMAGE = "Gambar";
    public static final String DOCUMENT = "Dokument";
    public static final String TEXT = "text";
    public static final String STICKER = "stiker";

    // Laporan
    public static final String KADERISASI = "Kaderisasi";
    public static final String PELATIHAN = "Pelatihan";
    public static final String ALUMNI = "Alumni";

    // TRAINING
    public static final String TRAINING_LK1 = "LK1 (Basic Training)";
    public static final String TRAINING_LK2 = "LK2 (Intermediate Training)";
    public static final String TRAINING_LK3 = "LK3 (Advance Training)";
    public static final String TRAINING_SC = "SC (Senior Course)";
    public static final String TRAINING_TID = "TID (Training Instruktur Dasar)";

    private static int typeData = 0;

    private static SharedPreferences pref;
    private static SharedPreferences.Editor editor;
    Context context;

    AppDb appDb;
    UserDao userDao;
    ContactDao contactDao;
    GroupChatDao groupChatDao;

    public UserDao getUserDao() {
        return userDao;
    }

    public ContactDao getContactDao() {
        return contactDao;
    }

    @SuppressLint("CommitPrefEdits")
    public Constant(Context context) {
        this.context = context;
        pref = context.getSharedPreferences("SIHMI", Context.MODE_PRIVATE);
        editor = pref.edit();

        appDb = AppDb.getInstance(context);
        userDao = appDb.userDao();
        contactDao = appDb.contactDao();
        groupChatDao = appDb.groupChatDao();
    }

    public AppDb getAppDb() {
        return appDb;
    }

    public GroupChatDao getGroupChatDao() {
        return groupChatDao;
    }

    public static boolean isLoggedIn(Context context) {
        UserDao userDao = AppDb.getInstance(context).userDao();
        if (userDao.getUser() == null) {
            Constant.logout();
        }
        return pref.getBoolean("IS_LOGIN", false);
    }

    public static void login() {
        editor.putBoolean("IS_LOGIN", true);
        editor.commit();
    }

    public static void logout() {
        editor.putBoolean("IS_LOGIN", false);
        editor.commit();
    }

    public static String getToken() {
        return pref.getString("TOKEN", "");
    }

    public static void setToken(String token) {
        editor.putString("TOKEN", token);
        editor.commit();
    }

    public static String getIdRoles(){
        return pref.getString("ID_ROLES", "");
    }

    public static void setIdRoles(String idRoles){
        editor.putString("ID_ROLES", idRoles);
        editor.commit();
    }

    public static void setListAccount(List<Account> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString("Account", json);
        editor.commit();
    }

    public static List<Account> getLisAccount() {
        List<Account> list;
        Gson gson = new Gson();
        String json = pref.getString("Account", "");
        Type type = new TypeToken<List<Account>>() {}.getType();
        if (gson.fromJson(json, type) == null){
            list = new ArrayList<>();
        } else {
            list = gson.fromJson(json, type);
        }
        return list;
    }

    public static void setLanguage(String language){
        editor.putString("Language", language);
        editor.commit();
    }

    public static String getLanguage(){
        return pref.getString("Language", "id");
    }

    public static void setNightModeState(Boolean state) {
        editor.putBoolean("NightMode",state);
        editor.commit();
    }

    // this method will load the Night Mode State
    public static Boolean loadNightModeState (){
        return pref.getBoolean("NightMode",false);
    }

    public static void setFontName(String fontName) {
        editor.putString("FontName",fontName);
        editor.commit();
    }

    public static String getFontName (){
        return pref.getString("FontName","Default");
    }

    public static void setFontSize(float fontSize) {
        editor.putFloat("FontSize",fontSize);
        editor.commit();
    }

    public static float getFontSize(){
        return pref.getFloat("FontSize",1);
    }

    public static void setFontSizeName(String fontName) {
        editor.putString("FontSizeName",fontName);
        editor.commit();
    }

    public static String getFontSizeName (){
        return pref.getString("FontSizeName","Default");
    }

    private static String name;

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        Constant.name = name;
    }

    public static boolean dataProfile= false;

    public static boolean isDataProfile() {
        return dataProfile;
    }

    public static void setDataProfile(boolean dataProfile) {
        Constant.dataProfile = dataProfile;
    }

    public static void setTypeData(int typeData) {
        Constant.typeData = typeData;
    }

    public static List<Account> getListAccount(){
        return Constant.getLisAccount();
    }
    public static int getSizeAccount(){
        return Constant.getLisAccount().size();
    }
}
