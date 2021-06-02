package com.roma.android.sihmi.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Contact;
import com.roma.android.sihmi.model.database.entity.PengajuanHistory;
import com.roma.android.sihmi.model.database.entity.PengajuanLK1;
import com.roma.android.sihmi.model.database.entity.Training;
import com.roma.android.sihmi.model.database.interfaceDao.ContactDao;
import com.roma.android.sihmi.model.database.interfaceDao.HistoryPengajuanDao;
import com.roma.android.sihmi.model.database.interfaceDao.LevelDao;
import com.roma.android.sihmi.model.database.interfaceDao.PengajuanLK1Dao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.model.network.ApiClient;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.response.GeneralResponse;
import com.roma.android.sihmi.model.response.PendidikanResponse;
import com.roma.android.sihmi.model.response.TrainingResponse;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Tools;
import com.roma.android.sihmi.view.adapter.PelatihanOtherUserAdapter;
import com.roma.android.sihmi.view.adapter.PendidikanAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

@SuppressLint("NonConstantResourceId")
public class ProfileChatActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.iv_photo)
    ImageView ivPhoto;
    @BindView(R.id.iv_initial)
    ImageView ivInitial;
    @BindView(R.id.et_fullname)
    EditText etFullname;
    @BindView(R.id.et_jenis_kelamin)
    EditText etJenisKelamin;
    @BindView(R.id.et_komisariat)
    EditText etKomisariat;
    @BindView(R.id.tv_pelatihan)
    TextView tvPelatihan;
    @BindView(R.id.rv_pelatihan)
    RecyclerView rvPelatihan;
    @BindView(R.id.iv_pdf)
    ImageView ivPdf;
    @BindView(R.id.tv_ket)
    TextView tvKet;
    @BindView(R.id.tv_pendidikan)
    TextView tvPendidikan;
    @BindView(R.id.rv_pendidikan)
    RecyclerView rvPendidikan;

    Contact otherUser;
    MasterService service;

    List<Training> list;
    PelatihanOtherUserAdapter adapter;
    private PendidikanAdapter pendidikanAdapter;
    String idPengajuan = "", filePdf = "";

    AppDb appDb;
    ContactDao contactDao;
    UserDao userDao;
    HistoryPengajuanDao historyPengajuanDao;
    PengajuanLK1Dao pengajuanLK1Dao;
    LevelDao levelDao;
    Boolean requestMode, acceptedMode, isRequestKader;
    public static final String requestKader = "REQUEST KADER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_chat);
        ButterKnife.bind(this);

        service = ApiClient.getInstance().getApi();
        appDb = AppDb.getInstance(this);
        contactDao = appDb.contactDao();
        userDao = appDb.userDao();
        historyPengajuanDao = appDb.historyPengajuanDao();
        levelDao = appDb.levelDao();
        pengajuanLK1Dao = appDb.pengajuanLK1Dao();

        otherUser = contactDao.getContactById(getIntent().getStringExtra("iduser"));
        requestMode = getIntent().getBooleanExtra("MODE_REQUEST", false);
        acceptedMode = getIntent().getBooleanExtra("MODE_ACCEPTED", false);
        idPengajuan = getIntent().getStringExtra("idpengajuan");
        isRequestKader = getIntent().getBooleanExtra(requestKader, false);

        if (idPengajuan != null && !idPengajuan.isEmpty()){
            filePdf = historyPengajuanDao.getFilePengajuanHistory(idPengajuan);
            if (filePdf != null && !filePdf.isEmpty()){
                PengajuanHistory pengajuanHistory = historyPengajuanDao.getPengajuanHistoryById(idPengajuan);
                String pengajuan = levelDao.getNamaLevel(pengajuanHistory.getId_roles());
                ivPdf.setVisibility(View.VISIBLE);
                tvKet.setVisibility(View.VISIBLE);
                String descriptionTxt = pengajuan+" "+getString(R.string.oleh)+" "+otherUser.getUsername();
                tvKet.setText(descriptionTxt);
            } else {
                ivPdf.setVisibility(View.GONE);
                tvKet.setVisibility(View.GONE);
            }
        } else {
            ivPdf.setVisibility(View.GONE);
            tvKet.setVisibility(View.GONE);
        }

        initToolbar();
        initView();
        initAdapter();
        getDataPelatihan();
        getPendidikan(3);

        if (otherUser != null) appDb.pendidikanDao().getPendidikanLiveDataByUserId(otherUser.get_id()).observe(this, listPendidikan -> pendidikanAdapter.updateData(listPendidikan));

        tvPelatihan.setVisibility(View.GONE);
    }

    private void getPendidikan(int retry) {
        Call<PendidikanResponse> call = service.getPendidikan(Constant.getToken(), otherUser.get_id());

        call.enqueue(new Callback<PendidikanResponse>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<PendidikanResponse> call, Response<PendidikanResponse> response) {
                if (response.isSuccessful()) {
                    PendidikanResponse body = response.body();
                    if (body != null) {
                        if (body.getStatus().equalsIgnoreCase("success")) {
                            appDb.pendidikanDao().insertManyPendidikan(body.getData());

                            if (body.getData().size() > 0) {
                                tvPendidikan.setVisibility(View.VISIBLE);
                                rvPendidikan.setVisibility(View.VISIBLE);
                            }
                        }
                        else {
                            // failed
                            if (retry > 1) getPendidikan(retry-1);
                        }
                    }
                    else {
                        // failed
                        if (retry > 1) getPendidikan(retry-1);
                    }
                }
                else {
                    // failed
                    if (retry > 1) getPendidikan(retry-1);
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<PendidikanResponse> call, Throwable t) {
                if (retry > 1) getPendidikan(retry-1);
            }
        });
    }

    @OnClick(R.id.iv_pdf)
    public void clickPdf(){
        startActivity(new Intent(ProfileChatActivity.this, FileDetailActivity.class)
                .putExtra("file", filePdf)
                .putExtra("_id", idPengajuan)
                .putExtra("judul", "SK USER"));
    }

    private void initToolbar(){
        toolbar.setTitle(otherUser.getFullName());
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    private void initView() {
        etFullname.setText(otherUser.getFullName());
        etJenisKelamin.setText(otherUser.getGender());
        if (isRequestKader) {
            PengajuanLK1 pengajuanLK1 = pengajuanLK1Dao.getPengajuanLK1ById(idPengajuan);
            if (pengajuanLK1 != null) {
                etKomisariat.setText(pengajuanLK1.getKomisariat());
            }
        }
        else {
            etKomisariat.setText(otherUser.getKomisariat());
        }
        if (otherUser.getImage() != null && !otherUser.getImage().trim().isEmpty()) {
            ivPhoto.setVisibility(View.VISIBLE);
            ivInitial.setVisibility(View.GONE);
            Glide.with(this).load(otherUser.getImage()).into(ivPhoto);
        } else {
            ivPhoto.setVisibility(View.GONE);
            ivInitial.setVisibility(View.VISIBLE);
            Tools.initial(ivInitial, otherUser.getFullName());
        }
    }

    @OnClick(R.id.iv_photo)
    public void click(){
        startActivity(new Intent(ProfileChatActivity.this, ChatFileDetailActivity.class).putExtra(ChatFileDetailActivity.NAMA_FILE, otherUser.getImage()).putExtra(ChatFileDetailActivity.TYPE_FILE, Constant.IMAGE));
    }

    private void initAdapter(){
        list = new ArrayList<>();
        adapter = new PelatihanOtherUserAdapter(this, list);
        rvPelatihan.setLayoutManager(new LinearLayoutManager(this));
        rvPelatihan.setHasFixedSize(true);
        rvPelatihan.setAdapter(adapter);

        pendidikanAdapter = new PendidikanAdapter(this, new ArrayList<>(), id -> { });
        pendidikanAdapter.setDisplayAdd(false);
        rvPendidikan.setLayoutManager(new LinearLayoutManager(this));
        rvPendidikan.setAdapter(pendidikanAdapter);
    }

    private void getDataPelatihan(){
        Call<TrainingResponse> call = service.getTraining(Constant.getToken(), otherUser.get_id());
        call.enqueue(new Callback<TrainingResponse>() {
            @Override
            public void onResponse(@NonNull Call<TrainingResponse> call, @NonNull Response<TrainingResponse> response) {
                if (response.isSuccessful()) {
                    TrainingResponse body = response.body();
                    if (body != null) {
                        if (body.getStatus().equalsIgnoreCase("success")) {
                            adapter.updateData(body.getData());
                            if (body.getData().size()>0){
                                tvPelatihan.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<TrainingResponse> call, @NonNull Throwable t) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem;
        if (requestMode || acceptedMode) {
            getMenuInflater().inflate(R.menu.change_admin, menu);

            if (requestMode) {
                menuItem = menu.findItem(R.id.action_2);
            }
            else {
                menuItem = menu.findItem(R.id.action_1);
            }
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_1:
                Tools.showDialogCustom(this, "Batalkan Permintaan", "Apakah Anda yakin ingin membatalkan permintaan ini?", "batal", ket -> rejectRequest(otherUser.get_id(), idPengajuan));
                break;
            case R.id.action_2:
                Tools.showDialogCustom(this, "Konfirmasi", getString(R.string.konfirm_akses_ket), Constant.LIHAT, ket -> showDialogPilihan());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDialogPilihan(){
        String[] grpName = {"Admin Komisariat", "Admin BPL", "Admin Alumni", "Admin Cabang", "Admin PBHMI", "Admin Nasional"};
        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this)
                .setSingleChoiceItems(grpName, -1, (dialog1, which) -> {
            int level;
            if (which == 0){
                level = Constant.USER_ADMIN_1;
            } else if (which == 1){
                level = Constant.USER_ADMIN_2;
            } else if (which == 2) {
                level = Constant.USER_ADMIN_3;
            } else if (which == 3) {
                level = Constant.USER_LOW_ADMIN_1;
            } else if (which == 4) {
                level = Constant.USER_LOW_ADMIN_2;
            } else {
                level = Constant.USER_SECOND_ADMIN;
            }
            changeRole(otherUser.get_id(), level, new Callback<GeneralResponse>() {
                @Override
                public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                    if (response.isSuccessful()) {
                        otherUser.setId_level(level);
                        otherUser.setId_roles(levelDao.getIdRoles(level));
                        contactDao.insertContact(otherUser);
                        sendNotif(otherUser.get_id(), "1", level);
                        Tools.showToast(ProfileChatActivity.this, "Berhasil menjadikan admin");
                        finish();
                    }
                    else {
                        Tools.showToast(ProfileChatActivity.this, "Gagal menjadikan admin");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<GeneralResponse> call, @NonNull Throwable t) {
                    Tools.showToast(ProfileChatActivity.this, "Gagal menjadikan admin");
                }
            });
            dialog1.dismiss();
        }).setCancelable(true).create();
        dialog.show();
    }

    private void sendNotif(String user, String status, int newLevel) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("from", userDao.getUser().get_id());
        hashMap.put("to", user);
        hashMap.put("status", status.trim());
        hashMap.put("time", System.currentTimeMillis());
        hashMap.put("isshow", false);
        hashMap.put("newLevel", newLevel);
        hashMap.put("type", "User");

        databaseReference.child("Notification").push().setValue(hashMap);
    }

    private void rejectRequest(String idUser, String id_pengajuan) {
        PengajuanHistory pengajuanHistory = historyPengajuanDao.getPengajuanHistoryById(id_pengajuan);
        Tools.showProgressDialog(ProfileChatActivity.this, getString(R.string.membatalkan_permintaan));
        int level = pengajuanHistory.getLevel();
        int levelBefore = levelDao.getLevel(otherUser.getId_roles());
        Call<GeneralResponse> call;
        if (level == 2) {
            // Pengajuan LK1
            call = service.updatePengajuanLK1(Constant.getToken(), id_pengajuan, "-1");
        }
        else {
            call = service.updatePengajuanAdmin(Constant.getToken(), id_pengajuan, "-1");
        }
        call.enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                Tools.dissmissProgressDialog();
                if (response.isSuccessful()) {
                    changeRole(idUser, levelBefore, new Callback<GeneralResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                            if (response.isSuccessful()) {
                                PengajuanHistory pengajuanHistory = historyPengajuanDao.getPengajuanHistoryById(id_pengajuan);
                                pengajuanHistory.setStatus(-1);
                                historyPengajuanDao.insertPengajuanHistory(pengajuanHistory);
                                sendNotif(idUser, "-1", levelBefore);
                                Tools.showToast(ProfileChatActivity.this, "Berhasil membatalkan");
                                finish();
                            }
                            else {
                                Tools.showToast(ProfileChatActivity.this, getString(R.string.gagal_membatalkan));
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<GeneralResponse> call, @NonNull Throwable t) {
                            Tools.showToast(ProfileChatActivity.this, getString(R.string.gagal_membatalkan));
                        }
                    });
                } else {
                    Log.d("PROFILE REJECT ERROR", "PROFILE REJECT ERROR" + response.message());
                    Tools.showToast(ProfileChatActivity.this, getString(R.string.gagal_membatalkan));
                }
            }

            @Override
            public void onFailure(@NonNull Call<GeneralResponse> call, @NonNull Throwable t) {
                Tools.dissmissProgressDialog();
                Log.d("PROFILE REJECT ERROR", "PROFILE REJECT ERROR");
                Tools.showToast(ProfileChatActivity.this, getString(R.string.gagal_membatalkan));
            }
        });
    }

    private void changeRole(String idUser, int level, Callback<GeneralResponse> callback) {
        Call<GeneralResponse> call = service.updateUserLevel(Constant.getToken(), idUser, level);
        call.enqueue(callback);
    }

}
