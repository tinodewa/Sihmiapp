package com.roma.android.sihmi.ViewModel;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.helper.AgendaScheduler;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Chat;
import com.roma.android.sihmi.model.database.entity.Chatting;
import com.roma.android.sihmi.model.database.entity.Contact;
import com.roma.android.sihmi.model.database.entity.GroupChat;
import com.roma.android.sihmi.model.database.entity.GroupChatFirebase;
import com.roma.android.sihmi.model.database.entity.GroupChatSeen;
import com.roma.android.sihmi.model.database.entity.Level;
import com.roma.android.sihmi.model.database.entity.LoadDataState;
import com.roma.android.sihmi.model.database.entity.Training;
import com.roma.android.sihmi.model.database.entity.User;
import com.roma.android.sihmi.model.database.entity.notification.Token;
import com.roma.android.sihmi.model.database.interfaceDao.ChatingDao;
import com.roma.android.sihmi.model.database.interfaceDao.ContactDao;
import com.roma.android.sihmi.model.database.interfaceDao.GroupChatDao;
import com.roma.android.sihmi.model.database.interfaceDao.LevelDao;
import com.roma.android.sihmi.model.database.interfaceDao.LoadDataStateDao;
import com.roma.android.sihmi.model.database.interfaceDao.MasterDao;
import com.roma.android.sihmi.model.database.interfaceDao.TrainingDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.model.network.ApiClient;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.response.AgendaResponse;
import com.roma.android.sihmi.model.response.ContactResponse;
import com.roma.android.sihmi.model.response.GeneralResponse;
import com.roma.android.sihmi.model.response.LevelResponse;
import com.roma.android.sihmi.model.response.MasterResponse;
import com.roma.android.sihmi.model.response.TrainingResponse;
import com.roma.android.sihmi.service.MyFirebaseMessagingService;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Tools;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class LoadDataActivityViewModel extends ViewModel {
    private AppDb appDb;
    private LoadDataStateDao loadDataStateDao;
    private MasterDao masterDao;
    private MasterService service;
    private UserDao userDao;
    private LevelDao levelDao;
    private ContactDao contactDao;
    private TrainingDao trainingDao;
    private GroupChatDao groupChatDao;
    private ChatingDao chatingDao;
    private String token, errorMessage;
    private MutableLiveData<Boolean> loadDataError;
    private MutableLiveData<Boolean> isLoggedOut;
    private Context context;

    public void init(Context context) {
        this.context = context;
        appDb = AppDb.getInstance(context);
        loadDataStateDao = appDb.loadDataStateDao();
        levelDao = appDb.levelDao();
        masterDao = appDb.masterDao();
        userDao = appDb.userDao();
        contactDao = appDb.contactDao();
        trainingDao = appDb.trainingDao();
        groupChatDao = appDb.groupChatDao();
        chatingDao = appDb.chatingDao();

        service = ApiClient.getInstance().getApi();
        loadDataError = new MutableLiveData<>();
        loadDataError.setValue(false);
        isLoggedOut = new MutableLiveData<>();
        isLoggedOut.setValue(false);

        token = Constant.getToken();
    }

    public LiveData<Boolean> getIsLoaded() {
        return loadDataStateDao.getIsLoadedLiveData();
    }

    public LiveData<Boolean> getIsError() {
        return loadDataError;
    }

    public MutableLiveData<Boolean> getIsLoggedOut() {
        return isLoggedOut;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void getData () {
        Log.d("LOAD DATA PROCESS", "LOAD DATA PROCESS " + "on getData");
        loadDataError.setValue(false);

        Call<MasterResponse> call = service.getMaster(token, "0");
        call.enqueue(new Callback<MasterResponse>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<MasterResponse> call, Response<MasterResponse> response) {
                if (response.isSuccessful()){
                    assert response.body() != null;
                    if (response.body().getStatus().equalsIgnoreCase("ok")){
                        masterDao.insertMaster(response.body().getData());
                        getListGroupChat();
                        new Handler().postDelayed(() -> getLevel(), 1000);
                        new Handler().postDelayed(() -> getAgenda(3), 1500);
                    }
                }
                else {
                    errorMessage = context.getString(R.string.gagal_memuat_data);
                    loadDataError.setValue(true);
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<MasterResponse> call, Throwable t) {
                errorMessage = context.getString(R.string.gagal_memuat_data);
                loadDataError.setValue(true);
            }
        });
    }

    private void getLevel(){
        Log.d("LOAD DATA PROCESS", "LOAD DATA PROCESS " + "on getLevel");
        Call<LevelResponse> call = service.getLevelLK();
        call.enqueue(new Callback<LevelResponse>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<LevelResponse> call, Response<LevelResponse> response) {
                if (response.isSuccessful()){
                    assert response.body() != null;
                    if (response.body().getStatus().equalsIgnoreCase("success")){
                        if (response.body().getData().size() > 0){
                            for (int i = 0; i < response.body().getData().size(); i++) {
                                Level level = response.body().getData().get(i);
                                if (level.getId() == 1){
                                    level.setNewName("Non Kader");
                                } else if (level.getId() == 2){
                                    level.setNewName("Kader");
                                } else if (level.getId() == 5){
                                    level.setNewName("Admin Komisariat");
                                } else if (level.getId() == 8){
                                    level.setNewName("Admin BPL");
                                } else if (level.getId() == 11){
                                    level.setNewName("Admin Alumni");
                                } else if (level.getId() == 13){
                                    level.setNewName("Admin Cabang");
                                } else if (level.getId() == 16){
                                    level.setNewName("Admin PBHMI");
                                } else if (level.getId() == 19){
                                    level.setNewName("Admin Nasional");
                                } else if (level.getId() == 20){
                                    level.setNewName("Super Admin");
                                }
                                levelDao.insertLevel(level);
                            }
                        }
                        new Handler().postDelayed(() -> {
                            getContact();
                            saveProfiletoContact();
                        }, 1000);

                    } else {
                        // error
                        errorMessage = response.body().getMessage();
                        loadDataError.setValue(true);
                    }
                } else {
                    // error
                    errorMessage = context.getString(R.string.gagal_memuat_data);
                    loadDataError.setValue(true);
                }

            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<LevelResponse> call, Throwable t) {
                errorMessage = context.getString(R.string.gagal_memuat_data);
                loadDataError.setValue(true);
            }
        });
    }

    private void getContact(){
        Log.d("LOAD DATA PROCESS", "LOAD DATA PROCESS " + "on getContact");
        Call<ContactResponse> call = service.getContact(token);
        call.enqueue(new Callback<ContactResponse>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<ContactResponse> call, Response<ContactResponse> response) {
                if (response.isSuccessful()){
                    assert response.body() != null;
                    if (response.body().getStatus().equalsIgnoreCase("success")){
                        List<Contact> contacts = response.body().getData();
                        for (int i = 0; i < contacts.size() ; i++) {
                            Contact c = contacts.get(i);
                            c.setId_level(levelDao.getPengajuanLevel(c.getId_roles()));
                            c.setTahun_daftar(Tools.getYearFromMillis(Long.parseLong(c.getTanggal_daftar())));

                            String tanggalLk1 = c.getTanggal_lk1();
                            if (tanggalLk1 != null) {
                                String tahunLk1 = tanggalLk1.split("-")[2];
                                c.setTahun_lk1(tahunLk1);
                            }

                            contactDao.insertContact(c);
                        }
                        new Handler().postDelayed(() -> getTraining(), 1000);
                    } else {
                        // error
                        errorMessage = response.body().getMessage();
                        loadDataError.setValue(true);
                    }
                } else {
                    // error
                    errorMessage = context.getString(R.string.gagal_memuat_data);
                    loadDataError.setValue(true);
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<ContactResponse> call, Throwable t) {
                // error
                errorMessage = context.getString(R.string.gagal_memuat_data);
                loadDataError.setValue(true);
            }
        });
    }

    private void saveProfiletoContact(){
        Log.d("LOAD DATA PROCESS", "LOAD DATA PROCESS " + "on saveProfiletoContact");
        User user = userDao.getUser();

        int id_level = levelDao.getPengajuanLevel(user.getId_roles());
        Contact contact = new Contact(user.get_id(), user.getBadko(), user.getCabang(), user.getKorkom(), user.getKomisariat(),
                user.getId_roles(), user.getImage(), user.getNama_depan(), user.getNama_belakang(), user.getNama_panggilan(),
                user.getJenis_kelamin(), user.getNomor_hp(), user.getAlamat(), user.getUsername(), user.getTanggal_daftar(),
                user.getTempat_lahir(), user.getTanggal_lahir(), user.getStatus_perkawinan(), user.getKeterangan(), user.getDevice_name(),
                user.getLast_login(), user.getEmail(), user.getStatus_online(), id_level);
        contact.setDomisili_cabang(user.getDomisili_cabang());
        contact.setTahun_daftar(Tools.getYearFromMillis(Long.parseLong(contact.getTanggal_daftar())));

        contact.setTahun_lk1(user.getTahun_lk1());
        contact.setTanggal_lk1(user.getTanggal_lk1());

        contactDao.insertContact(contact);
    }

    private void initializeFcmToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("Fabric", "Failed to initialize token");
                return;
            }
            String tokenStr = Objects.requireNonNull(task.getResult()).getToken();

            Token token = new Token(tokenStr);
            FirebaseDatabase.getInstance().getReference("Tokens").child(userDao.getUser().get_id()).setValue(token);

            Log.d("Fabric", "Successfully initialize new token " + tokenStr);
        });
    }

    private void getListGroupChat(){
        Log.d("LOAD DATA PROCESS", "LOAD DATA PROCESS " + "on getListGroupChat");

        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("ChatGroup_v2");

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot s : dataSnapshot.getChildren()){
                    GroupChatFirebase groupChatFirebase = s.getValue(GroupChatFirebase.class);
                    assert groupChatFirebase != null;
                    groupChatDao.insertGroupChat(new GroupChat(groupChatFirebase.getNama(), groupChatFirebase.getImage()));
                }

                if (!Tools.isNonLK()) {
                    User user = userDao.getUser();
                    checkGroup(user.getCabang(), user.getKomisariat(), user.getDomisili_cabang());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void subscribeToGroupChat() {
        List<GroupChat> groupChats;
        User user = userDao.getUser();
        if (user != null) {
            if (Tools.isSuperAdmin()) {
                groupChats = groupChatDao.getAllGroupList();
            }
            else {
                groupChats = groupChatDao.getAllGroupListNotSuperAdmin(user.getCabang(), user.getKomisariat(), user.getDomisili_cabang());
            }

            for(GroupChat groupChat : groupChats) {
                String[] groupNameSplit = groupChat.getNama().split(" ");
                String groupName = TextUtils.join("_", groupNameSplit);
                final String topic = MyFirebaseMessagingService.GROUP_TOPIC_PREFIX + groupName;
                FirebaseMessaging.getInstance().subscribeToTopic(topic)
                        .addOnCompleteListener(task -> Log.d("Fabric", "Successfully subscribed to topic " + topic));
            }
        }
    }

    private void checkGroup(String cabang, String komisariat, String alumniCabang) {
        if (cabang != null && !cabang.trim().isEmpty()){
            if (groupChatDao.getGroupChatByName(cabang) == null && masterDao.getMasterCabang().contains(cabang)){
                addGroupChat(cabang);
            }
        }

        if (komisariat != null && !komisariat.trim().isEmpty()){
            if (groupChatDao.getGroupChatByName(komisariat) == null && masterDao.getMasterKomisariat().contains(komisariat)){
                addGroupChat(komisariat);
            }
        }

        if (alumniCabang != null && !alumniCabang.trim().isEmpty()){
            if (groupChatDao.getGroupChatByName("Alumni "+alumniCabang) == null && masterDao.getMasterCabang().contains(alumniCabang)){
                addGroupChat("Alumni "+alumniCabang);
            }
        }
    }

    private void addGroupChat(String name){
        Log.d("LOAD DATA PROCESS", "LOAD DATA PROCESS " + "on addGroupChat");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("nama", name);
        hashMap.put("image", "");

        databaseReference.child("ChatGroup_v2").child(name).setValue(hashMap);

        databaseReference.child("ChatGroup_v2").child(name).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GroupChatFirebase groupChatFirebase = dataSnapshot.getValue(GroupChatFirebase.class);
                assert groupChatFirebase != null;
                groupChatDao.insertGroupChat(new GroupChat(groupChatFirebase.getNama(), groupChatFirebase.getImage()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void deleteTraining(String idTraining) {
        Call<GeneralResponse> call = service.deleteTraining(Constant.getToken(), idTraining);

        call.enqueue(new Callback<GeneralResponse>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        Log.d("DELETE TRAINING", "DELETE TRAINING success " + idTraining);
                    }
                    else {
                        // error
                        Log.d("DELETE TRAINING", "DELETE TRAINING failed " + response.body().getMessage() + " " + idTraining);
                    }
                }
                else {
                    // error
                    assert response.body() != null;
                    Log.d("DELETE TRAINING", "DELETE TRAINING failed " + response.body().getMessage() + " " + idTraining);
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                // error
                Log.d("DELETE TRAINING", "DELETE TRAINING failed " + t.getMessage() + " " + idTraining);
            }
        });
    }

    private void addDataTraining(String tipe, String tahun, String nama, String lokasi, int tryCount){
        Call<GeneralResponse> call = service.addTraining(Constant.getToken(), tipe, tahun, nama, lokasi);
        call.enqueue(new Callback<GeneralResponse>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        Log.d("ADD TRAINING", "ADD TRAINING succeeded");
                    } else {
                        //error
                        if (tryCount < 3) {
                            addDataTraining(tipe, tahun, nama, lokasi, tryCount+1);
                        }
                        else {
                            Log.d("ADD TRAINING", "ADD TRAINING failed " + response.body().getMessage());
                        }
                    }
                } else {
                    //error
                    if (tryCount < 3) {
                        addDataTraining(tipe, tahun, nama, lokasi, tryCount+1);
                    }
                    else {
                        assert response.body() != null;
                        Log.d("ADD TRAINING", "ADD TRAINING failed " + response.body().getMessage());
                    }
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                //error
                if (tryCount < 3) {
                    addDataTraining(tipe, tahun, nama, lokasi, tryCount+1);
                }
                else {
                    Log.d("ADD TRAINING", "ADD TRAINING failed " + t.getMessage() + " ");
                }
            }
        });
    }

    private void getTraining(){
        Log.d("LOAD DATA PROCESS", "LOAD DATA PROCESS " + "on getTraining");
        Call<TrainingResponse> call = service.getTraining(token, "0");
        call.enqueue(new Callback<TrainingResponse>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<TrainingResponse> call, Response<TrainingResponse> response) {
                if (response.isSuccessful()){
                    assert response.body() != null;
                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        int size = response.body().getData().size();
                        Log.d("LOAD DATA PROCESS", "LOAD DATA PROCESS training size " + size);
                        if (size > 0) {
                            boolean foundSame = false;
                            Contact me = contactDao.getContactById(userDao.getUser().get_id());
                            String tanggalLk1 = me.getTanggal_lk1();
                            String[] tanggalLk1Split;
                            String tahun_lk1;

                            for (int i = 0; i < size ; i++) {
                                Training training = response.body().getData().get(i);
                                Contact contact = contactDao.getContactById(training.getId_user());

                                boolean insert = true;
                                if (training.getTipe().contains("LK1")) {
                                    Log.d("LOAD DATA PROCESS", "LOAD DATA PROCESS training detail LK1 " + contact.getFullName() + " iduser " + training.getId_user() + " " + me.get_id());

                                    if (training.getId_user().equals(me.get_id())) {
                                        if (Tools.isNonLK()) {
                                            deleteTraining(training.getId());
                                            insert = false;
                                            foundSame = true;
                                        }
                                        else {
                                            Log.d("LOAD DATA PROCESS", "LOAD DATA PROCESS training detail LK1 FOUND ME " + tanggalLk1);
                                            contact.setLk1(training.getTipe());
                                            if (tanggalLk1 != null && !tanggalLk1.isEmpty()) {
                                                tanggalLk1Split = tanggalLk1.split("-");

                                                if (tanggalLk1Split.length == 3) {
                                                    tahun_lk1 = tanggalLk1Split[2];

                                                    if (!foundSame && training.getTahun().equals(tahun_lk1) && training.getTempat().equalsIgnoreCase(me.getCabang())) {
                                                        foundSame = true;
                                                        Log.d("FOUND SAME", "FOUND SAME " + training.getTahun() + " " + training.getTempat());
                                                    }
                                                    else {
                                                        // delete training
                                                        Log.d("FOUND SAME", "NOT SAME " + training.getTahun() + " " + training.getTempat());
                                                        deleteTraining(training.getId());
                                                        insert = false;
                                                    }
                                                }
                                            }
                                            else {
                                                foundSame = true;
                                            }
                                        }
                                    }
                                    else {
                                        Log.d("LOAD DATA PROCESS", "LOAD DATA PROCESS not me " + contact.getFullName());
                                        contact.setLk1(training.getTipe());
                                        contact.setTahun_lk1(training.getTahun());
                                    }
                                } else if (training.getTipe().contains("LK2")) {
                                    Log.d("LOAD DATA PROCESS", "LOAD DATA PROCESS training detail LK2 " + contact.getFullName() + " iduser " + training.getId_user() + " " + me.get_id());

                                    if (training.getId_user().equals(me.get_id()) && Tools.isNonLK()) {
                                        deleteTraining(training.getId());
                                        insert = false;
                                    }
                                    else {
                                        contact.setLk2(training.getTipe());
                                    }
                                } else if (training.getTipe().contains("LK3")) {
                                    Log.d("LOAD DATA PROCESS", "LOAD DATA PROCESS training detail LK3 " + contact.getFullName() + " iduser " + training.getId_user() + " " + me.get_id());

                                    if (training.getId_user().equals(me.get_id()) && Tools.isNonLK()) {
                                        deleteTraining(training.getId());
                                        insert = false;
                                    }
                                    else {
                                        contact.setLk3(training.getTipe());
                                    }
                                } else if (training.getTipe().contains("SC")) {
                                    Log.d("LOAD DATA PROCESS", "LOAD DATA PROCESS training detail LK3 " + contact.getFullName() + " iduser " + training.getId_user() + " " + me.get_id());

                                    if (training.getId_user().equals(me.get_id()) && Tools.isNonLK()) {
                                        deleteTraining(training.getId());
                                        insert = false;
                                    }
                                    else {
                                        contact.setSc(training.getTipe());
                                    }
                                } else if (training.getTipe().contains("TID")) {
                                    Log.d("LOAD DATA PROCESS", "LOAD DATA PROCESS training detail LK3 " + contact.getFullName() + " iduser " + training.getId_user() + " " + me.get_id());

                                    if (training.getId_user().equals(me.get_id()) && Tools.isNonLK()) {
                                        deleteTraining(training.getId());
                                        insert = false;
                                    }
                                    else {
                                        contact.setTid(training.getNama_training());
                                    }
                                }

                                if (insert) {
                                    contactDao.insertContact(contact);

                                    training.setId(training.getId());
                                    training.setId_user(training.getId_user());
                                    training.setId_level(contact.getId_level());
                                    training.setCabang(contact.getCabang());
                                    training.setKomisariat(contact.getKomisariat());
                                    training.setDomisili_cabang(contact.getDomisili_cabang());
                                    training.setJenis_kelamin(contact.getJenis_kelamin());

                                    if (trainingDao.checkTrainingAvailable(training.getId_user(), training.getTipe(), training.getTahun()) == null){
                                        trainingDao.insertTraining(training);
                                    }
                                }
                            }

                            if (!foundSame && tanggalLk1 != null) {
                                String tahunLk1 = me.getTahun_lk1();
                                if (tahunLk1 == null || !tahunLk1.trim().isEmpty()) {
                                    tanggalLk1Split = tanggalLk1.split("-");
                                    if (tanggalLk1Split.length == 3) {
                                        tahunLk1 = tanggalLk1Split[2];
                                    }
                                }

                                Log.d("ADD TRAINING", "ADD TRAINING");
                                addDataTraining(Constant.TRAINING_LK1, tahunLk1, Constant.TRAINING_LK1, me.getCabang(), 1);
                            }
                        }

                        getPersonalChat();
                    }
                    else {
                        // error
                        errorMessage = context.getString(R.string.gagal_memuat_data);
                        loadDataError.setValue(true);
                    }
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<TrainingResponse> call, Throwable t) {
                // error
                errorMessage = context.getString(R.string.gagal_memuat_data);
                loadDataError.setValue(true);
            }
        });
    }

    public void logout() {
        ApiClient.getInstance().getApi().logout(Constant.getToken())
                .enqueue(new Callback<GeneralResponse>() {
                    @Override
                    @EverythingIsNonNull
                    public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                        Constant.logout();
                        clearData();
                    }

                    @Override
                    @EverythingIsNonNull
                    public void onFailure(Call<GeneralResponse> call, Throwable t) {
                        Constant.logout();
                        clearData();
                    }
                });
    }

    private void unsubscribeFromGroupChat() {
        List<GroupChat> groupChats;
        User user = userDao.getUser();
        if (user != null) {
            if (Tools.isSuperAdmin()) {
                groupChats = groupChatDao.getAllGroupList();
            }
            else {
                groupChats = groupChatDao.getAllGroupListNotSuperAdmin(user.getCabang(), user.getKomisariat(), user.getDomisili_cabang());
            }

            for(GroupChat groupChat : groupChats) {
                String[] groupNameSplit = groupChat.getNama().split(" ");
                String groupName = TextUtils.join("_", groupNameSplit);
                final String topic = MyFirebaseMessagingService.GROUP_TOPIC_PREFIX + groupName;
                FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
                        .addOnCompleteListener(task -> Log.d("Fabric", "Successfully unsubscribed from topic " + topic));
            }
        }
    }

    private void getAgenda(int retry){
        Call<AgendaResponse> call = service.getAgenda(Constant.getToken(), "0");
        call.enqueue(new Callback<AgendaResponse>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<AgendaResponse> call, Response<AgendaResponse> response) {
                if (response.isSuccessful()) {
                    AgendaResponse body = response.body();
                    if (body != null && body.getStatus().equalsIgnoreCase("ok")) {
                        if (body.getData().size() > 0){
                            appDb.agendaDao().insertAgenda(body.getData());

                            AgendaScheduler.setupUpcomingAgendaNotifier(context);
                        }
                    } else {
                        // failed
                        if (retry > 1) getAgenda(retry-1);
                    }
                } else {
                    // failed
                    if (retry > 1) getAgenda(retry-1);
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<AgendaResponse> call, Throwable t) {
                // failed
                if (retry > 1) getAgenda(retry-1);
            }
        });
    }

    private void clearData() {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        reference.child(userDao.getUser().get_id()).removeValue();

        unsubscribeFromGroupChat();
        unsubscribeFromAgenda();

        AgendaScheduler.cancelAgenda(context);

        Constant.logout();
        appDb.clearAllTables();

        isLoggedOut.setValue(true);
    }

    private void getPersonalChat() {
        Log.d("LOAD DATA PROCESS", "LOAD DATA PROCESS " + "on getPersonalChat");
        getListChating();
    }

    private void getListChating() {
        Log.d("LOAD DATA PROCESS", "LOAD DATA PROCESS " + "on getListChating");
        DatabaseReference referenceChats = FirebaseDatabase.getInstance().getReference("Chats_v2");
        referenceChats.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                new getListChatingAsync().execute(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class getListChatingAsync extends AsyncTask<DataSnapshot, Void, Boolean> {

        @Override
        protected Boolean doInBackground(DataSnapshot... dataSnapshots) {
            try {
                String myuserid = userDao.getUser().get_id();
                for (DataSnapshot snapshot : dataSnapshots[0].getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat != null && (chat.getSender().equals(myuserid) || chat.getReceiver().equals(myuserid))) {
                        String userId;
                        if (chat.getSender().equals(myuserid)) {
                            userId = chat.getReceiver();
                        }
                        else {
                            userId = chat.getSender();
                        }

                        Chatting thisChatting = chatingDao.getChatingById(userId);

                        if (thisChatting == null || chat.getTime() > thisChatting.getTime_message()) {
                            int unreadCount = (thisChatting != null) ? thisChatting.getUnread() : 0;
                            if (chat.getSender().equals(userId) && chat.isNotSeen()) {
                                unreadCount++;
                            }
                            String lastMsg;
                            if (chat.getType().equalsIgnoreCase(Constant.IMAGE)) {
                                lastMsg = Constant.IMAGE;
                            } else if (chat.getType().equalsIgnoreCase(Constant.DOCUMENT)) {
                                lastMsg = Constant.DOCUMENT;
                            } else {
                                lastMsg = chat.getMessage();
                            }
                            String name = contactDao.getContactById(userId).getFullName();
                            chatingDao.insertChating(new Chatting(userId, name, chat.getReceiver() + "split100x" + lastMsg, chat.getTime(), unreadCount));
                        }
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
                return true;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            getGroupChatUnRead();
        }
    }

    private void getGroupChatUnRead() {
        Log.d("LOAD DATA PROCESS", "LOAD DATA PROCESS " + "on getGroupChatUnread");
        DatabaseReference referenceUsers = FirebaseDatabase.getInstance().getReference("Users");
        referenceUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                new getGroupChatUnReadAsync().execute(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class getGroupChatUnReadAsync extends AsyncTask <DataSnapshot, Void, Boolean> {

        @Override
        protected Boolean doInBackground(DataSnapshot... dataSnapshots) {
            for (DataSnapshot snapshot : dataSnapshots[0].getChildren()) {
                GroupChatSeen groupChatSeen = snapshot.getValue(GroupChatSeen.class);
                assert groupChatSeen != null;
                if (groupChatSeen.getId_user() != null && groupChatSeen.getId_user().equals(userDao.getUser().get_id())) {
                    groupChatDao.updateLastSeen(groupChatSeen.getNama(), groupChatSeen.getLast_seen());
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            getGroupChat();

            new Handler().postDelayed(LoadDataActivityViewModel.this::initializeFcmToken, 500);
            new Handler().postDelayed(LoadDataActivityViewModel.this::subscribeToGroupChat, 1000);
            new Handler().postDelayed(LoadDataActivityViewModel.this::subscribeToAgenda, 1500);
        }
    }

    private void subscribeToAgenda() {
        FirebaseMessaging.getInstance().subscribeToTopic(MyFirebaseMessagingService.AGENDA_TOPIC)
                .addOnCompleteListener(task -> Log.d("Fabric", "Successfully subscribed to topic " + MyFirebaseMessagingService.AGENDA_TOPIC));
    }

    private void unsubscribeFromAgenda() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(MyFirebaseMessagingService.AGENDA_TOPIC)
                .addOnCompleteListener(task -> Log.d("Fabric", "Successfully unsubscribed from topic " + MyFirebaseMessagingService.AGENDA_TOPIC));
    }

    private void getGroupChat(){
        Log.d("LOAD DATA PROCESS", "LOAD DATA PROCESS " + "on getGroupChat");
        DatabaseReference referenceChats = FirebaseDatabase.getInstance().getReference("Chats_v2");
        referenceChats.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                new getGroupChatAsync().execute(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class getGroupChatAsync extends AsyncTask<DataSnapshot, Void, Boolean> {

        @Override
        protected Boolean doInBackground(DataSnapshot... dataSnapshots) {
            try {
                for (DataSnapshot snapshot : dataSnapshots[0].getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);

                    assert chat != null;
                    GroupChat groupChat = groupChatDao.getGroupChatByName(chat.getReceiver());
                    if (groupChat != null) {
                        if (chat.getTime() >= groupChat.getTime()) {
                            groupChat.setLast_msg(chat.getType() + "split100x" + chat.getMessage());
                            groupChat.setTime(chat.getTime());
                            int unread;
                            if (chat.getTime() > groupChat.getLast_seen() && !chat.getSender().equals(userDao.getUser().get_id())) {
                                unread = groupChat.getUnread() + 1;
                            } else {
                                unread = groupChat.getUnread();
                            }
                            groupChat.setUnread(unread);
                            groupChatDao.insertGroupChat(groupChat);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return true;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            loadDataStateDao.insertLoadDataState(new LoadDataState(true, System.currentTimeMillis()));
        }
    }
}
