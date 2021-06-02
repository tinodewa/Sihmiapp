package com.roma.android.sihmi.view.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Master;
import com.roma.android.sihmi.model.database.entity.PengajuanHistory;
import com.roma.android.sihmi.model.database.entity.PengajuanLK1;
import com.roma.android.sihmi.model.database.entity.User;
import com.roma.android.sihmi.model.database.interfaceDao.HistoryPengajuanDao;
import com.roma.android.sihmi.model.database.interfaceDao.LevelDao;
import com.roma.android.sihmi.model.database.interfaceDao.MasterDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.model.network.ApiClient;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.response.GeneralResponse;
import com.roma.android.sihmi.model.response.PengajuanLK1Response;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Tools;

import java.text.ParseException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("NonConstantResourceId")
public class LK1Activity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_badko)
    EditText etBadko;
    @BindView(R.id.et_cabang)
    EditText etCabang;
    @BindView(R.id.et_korkom)
    EditText etKorkom;
    @BindView(R.id.et_komisariat)
    EditText etKomisariat;
    @BindView(R.id.et_tanggal)
    EditText etTanggal;
    @BindView(R.id.tv_ket)
    TextView tvKet;
    @BindView(R.id.checkbox)
    CheckBox checkBox;
    @BindView(R.id.btn_simpan)
    Button btnSimpan;

    MasterService service;
    User user;

    AppDb appDb;
    MasterDao masterDao;
    UserDao userDao;
    LevelDao levelDao;
    HistoryPengajuanDao historyPengajuanDao;
    Boolean isOnPengajuan = false;

    private String badkoId = "";
    private String cabangId = "";
    private String korkomId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lk1);
        ButterKnife.bind(this);
        initToolbar();
        service = ApiClient.getInstance().getApi();
        appDb = AppDb.getInstance(this);
        masterDao = appDb.masterDao();
        userDao = appDb.userDao();
        user = userDao.getUser();
        levelDao = appDb.levelDao();
        historyPengajuanDao = appDb.historyPengajuanDao();

        btnSimpan.setVisibility(View.GONE);
        checkBox.setVisibility(View.GONE);

        getPermintaanLK1();
        initView();

        Log.d("CHECK TOKEN", "CHECK TOKEN " + Constant.getToken());
    }

    private void initToolbar(){
        toolbar.setTitle(getString(R.string.telah_lk1_profil));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    private void initView(){
        if (isOnPengajuan || !Tools.isNonLK()) {
            Tools.setText(etBadko, user.getBadko());
            Tools.setText(etCabang, user.getCabang());
            Tools.setText(etKorkom, user.getKorkom());
            Tools.setText(etKomisariat, user.getKomisariat());
            Tools.setText(etTanggal, user.getTanggal_lk1());

            etBadko.setEnabled(false);
            etCabang.setEnabled(false);
            etKorkom.setEnabled(false);
            etKomisariat.setEnabled(false);
            etTanggal.setEnabled(false);
            tvKet.setVisibility(View.GONE);
            btnSimpan.setVisibility(View.GONE);
            checkBox.setVisibility(View.GONE);
        } else {
            etBadko.setEnabled(true);
            etCabang.setEnabled(true);
            etKorkom.setEnabled(true);
            etKomisariat.setEnabled(true);
            etTanggal.setEnabled(true);

            tvKet.setVisibility(View.VISIBLE);
            checkBox.setVisibility(View.VISIBLE);
            btnSimpan.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.et_badko, R.id.et_cabang, R.id.et_korkom, R.id.et_komisariat, R.id.et_tanggal})
    public void click(EditText editText){
        switch (editText.getId()){
            case R.id.et_badko:
                String[] badkoArray = masterDao.getMasterNameByType("1").toArray(new String[0]);
                List<Master> badkoMaster = masterDao.getListMasterByType("1");
                Tools.showDialogLK1(this, badkoArray, (res, index) -> {
                    etBadko.setText(res);
                    badkoId = badkoMaster.get(index).get_id();
                    etCabang.setText(null);
                    etKorkom.setText(null);
                    etKomisariat.setText(null);
                    cabangId = "";
                    korkomId = "";
                });
                break;
            case R.id.et_cabang:
                if (!badkoId.isEmpty()) {
                    String[] cabangArray = masterDao.getMasterValueByParentId(badkoId, "2").toArray(new String[0]);
                    List<Master> cabangMaster = masterDao.getMasterByParentId(badkoId, "2");
                    Tools.showDialogLK1(this, cabangArray, (res, index) -> {
                        etCabang.setText(res);
                        cabangId = cabangMaster.get(index).get_id();
                        etKorkom.setText(null);
                        etKomisariat.setText(null);
                        korkomId = "";
                    });
                }
                break;
            case R.id.et_korkom:
                if (!cabangId.isEmpty()) {
                    String[] korkomArray = masterDao.getMasterValueByParentId(cabangId, "3").toArray(new String[0]);
                    List<Master> korkomMaster = masterDao.getMasterByParentId(cabangId, "3");
                    Tools.showDialogLK1(this, korkomArray, (res, index) -> {
                        etKorkom.setText(res);
                        korkomId = korkomMaster.get(index).get_id();
                        etKomisariat.setText(null);
                    });
                }
                break;
            case R.id.et_komisariat:
                if (!korkomId.isEmpty()) {
                    String[] komisariatArray = masterDao.getMasterValueByParentId(korkomId, "4").toArray(new String[0]);
                    Tools.showDialogLK1(this, komisariatArray, ket -> etKomisariat.setText(ket));
                }
                break;
            case R.id.et_tanggal:
                Tools.showDateDialog(this, etTanggal);
                break;
        }
    }

    @OnClick(R.id.btn_simpan)
    public void click(){
        String badko = etBadko.getText().toString().trim();
        String cabang = etCabang.getText().toString().trim();
        String korkom = etKorkom.getText().toString().trim();
        String komisariat = etKomisariat.getText().toString().trim();
        String tanggal = etTanggal.getText().toString().trim();
        if (!badko.isEmpty() && !cabang.isEmpty() && !korkom.isEmpty() && !komisariat.isEmpty() && !tanggal.isEmpty() && checkBox.isChecked()) {
            long dateLong = -1;
            try {
                dateLong = Tools.getMillisFromTimeStr(tanggal, "dd-MM-yyyy");
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (dateLong > System.currentTimeMillis()) {
                Tools.showToast(this, getString(R.string.tanggal_lk1_over));
            }
            else if (Tools.isOnline(this)) {
                pegajuan(badko, cabang, korkom, komisariat, tanggal);
            } else {
                Tools.showToast(this, getString(R.string.tidak_ada_internet));
            }
        } else {
            Tools.showToast(this, getString(R.string.field_mandatory));
        }
    }

    private void pegajuan(String badko, String cabang, String korkom, String komisariat, String tgl){
        Tools.showProgressDialog(this, getString(R.string.pengajuan));
        String tahunLk1 = "";
        String[] tanggalLk1Split = tgl.split("-");
        if (tanggalLk1Split.length == 3) {
            tahunLk1 = tanggalLk1Split[2];
        }
        Call<GeneralResponse> call = service.addPengajuanLK1(Constant.getToken(), badko, cabang, korkom, komisariat, tgl, tahunLk1);
        call.enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                Tools.dissmissProgressDialog();
                if (response.isSuccessful()){
                    GeneralResponse body = response.body();
                    if (body != null) {
                        if (body.getStatus().equalsIgnoreCase("ok")){
                            Tools.showToast(LK1Activity.this, getString(R.string.pengajuan_berhasil));
                            finish();
                        } else {
                            Tools.showToast(LK1Activity.this, getString(R.string.pengajuan_gagal));
                        }
                    }
                    else {
                        Tools.showToast(LK1Activity.this, getString(R.string.pengajuan_gagal));
                    }
                } else {
                    Tools.showToast(LK1Activity.this, getString(R.string.pengajuan_gagal));
                }
            }

            @Override
            public void onFailure(@NonNull Call<GeneralResponse> call, @NonNull Throwable t) {
                Tools.dissmissProgressDialog();
                Tools.showToast(LK1Activity.this, getString(R.string.pengajuan_gagal));
            }
        });
    }

    private void getPermintaanLK1() {
        if (Tools.isOnline(this)) {
            Call<PengajuanLK1Response> call = service.getPengajuanLK1(Constant.getToken());
            call.enqueue(new Callback<PengajuanLK1Response>() {
                @Override
                public void onResponse(@NonNull Call<PengajuanLK1Response> call, @NonNull Response<PengajuanLK1Response> response) {
                    if (response.isSuccessful()) {
                        PengajuanLK1Response body = response.body();
                        if (body != null) {
                            if (body.getStatus().equalsIgnoreCase("ok")) {
                                int size = body.getData().size();
                                if (size > 0) {
                                    for (int i = 0; i < size; i++) {
                                        PengajuanLK1 pengajuanLK1 = body.getData().get(i);
                                        String idPengajuan = pengajuanLK1.get_id();
                                        String idRoles = "5da699c1a61aed0fe65ae31f";
                                        String tgl_lk1 = pengajuanLK1.getTanggal_lk1();
                                        String createdBy = pengajuanLK1.getCreated_by();
                                        String approvedBy = pengajuanLK1.getModified_by();
                                        long dateCreated = pengajuanLK1.getDate_created();
                                        long dateApproved = pengajuanLK1.getDate_modified();
                                        int status = pengajuanLK1.getStatus();

                                        int level = levelDao.getLevel(idRoles);

                                        PengajuanHistory pengajuanHistory = new PengajuanHistory(idPengajuan, idRoles, "", createdBy, approvedBy, dateCreated, dateApproved, status, tgl_lk1, level);
                                        pengajuanHistory.setNama(user.getNama_depan());
                                        historyPengajuanDao.insertPengajuanHistory(pengajuanHistory);
                                    }
                                }
                            }
                        }

                        isOnPengajuan = checkIsOnPengajuan();
                        initView();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<PengajuanLK1Response> call, @NonNull Throwable t) {
                    Tools.showToast(LK1Activity.this, getString(R.string.tidak_ada_internet));
                }
            });
        } else {
            Tools.showToast(this, getString(R.string.tidak_ada_internet));
        }
    }

    private boolean checkIsOnPengajuan() {
        PengajuanHistory pengajuanHistory = historyPengajuanDao.getOnProgressPengajuan(user.get_id());
        return pengajuanHistory != null;
    }
}
