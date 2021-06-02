package com.roma.android.sihmi.view.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.User;
import com.roma.android.sihmi.model.database.interfaceDao.MasterDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.model.network.ApiClient;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.response.GeneralResponse;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Tools;

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
public class AlumniActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_domisili_cabang)
    EditText etDomisili;
    @BindView(R.id.et_pekerjaan)
    EditText etPekerjaan;
    @BindView(R.id.et_jabatan)
    EditText etJabatan;
    @BindView(R.id.et_alamat)
    EditText etAlamat;
    @BindView(R.id.et_kontribusi)
    EditText etKontribusi;
    @BindView(R.id.checkbox)
    CheckBox checkBox;
    @BindView(R.id.btn_simpan)
    Button btnSimpan;

    MasterService service;
    AppDb appDb;
    UserDao userDao;
    MasterDao masterDao;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alumni);
        ButterKnife.bind(this);

        service = ApiClient.getInstance().getApi();
        appDb = AppDb.getInstance(this);
        masterDao = appDb.masterDao();
        userDao = appDb.userDao();
        user = userDao.getUser();

        initToolbar();
        initView();

    }

    private void initToolbar() {
        toolbar.setTitle(getString(R.string.alumni_profil));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    private void initView(){
        setText(etDomisili, user.getDomisili_cabang());
        setText(etPekerjaan, user.getPekerjaan());
        setText(etJabatan, user.getJabatan());
        setText(etAlamat, user.getAlamat_kerja());
        setText(etKontribusi, user.getKontribusi());
    }

    private void setText(EditText editText, String text){
        if (text != null && !text.trim().isEmpty()){
            editText.setText(text);
        }
    }

    @OnClick(R.id.et_domisili_cabang)
    public void clickDomisili() {
        String[] cabangArray = masterDao.getMasterNameByType("2").toArray(new String[0]);
        Tools.showDialogLK1(this, cabangArray, ket -> etDomisili.setText(ket));
    }

    @OnClick(R.id.btn_simpan)
    public void click() {
        String domisili = etDomisili.getText().toString().trim();
        String pekerjaan = etPekerjaan.getText().toString().trim();
        String jabatan = etJabatan.getText().toString().trim();
        String alamat = etAlamat.getText().toString().trim();
        String kontribusi = etKontribusi.getText().toString().trim();

        if (!domisili.isEmpty() && !pekerjaan.isEmpty() && !jabatan.isEmpty() && !alamat.isEmpty() && !kontribusi.isEmpty() && checkBox.isChecked()) {
            if (Tools.isOnline(this)) {
                simpan(domisili, pekerjaan, jabatan, alamat, kontribusi);
            } else {
                Tools.showToast(this, getString(R.string.tidak_ada_internet));
            }
        } else {
            Tools.showToast(this, getString(R.string.field_mandatory));
        }

    }

    private void simpan(String domisili, String pekerjaan, String jabatan, String alamat, String kontribusi) {
        Tools.showProgressDialog(this, getString(R.string.menyimpan_profile));

        Call<GeneralResponse> call = service.updateProfile(Constant.getToken(), user.getBadko(), user.getCabang(), user.getKorkom(), user.getKomisariat(), user.getId_roles(), user.getImage(), user.getNama_depan(), user.getNama_belakang(),
                user.getNama_panggilan(), user.getJenis_kelamin(), user.getNomor_hp(), user.getAlamat(), user.getUsername(),
                user.getTempat_lahir(), user.getTanggal_lahir(), user.getStatus_perkawinan(), "", user.getEmail(), user.getAkun_sosmed(), domisili, pekerjaan, jabatan, alamat, kontribusi);
        call.enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                if (response.isSuccessful()) {
                    GeneralResponse body = response.body();
                    if (body != null) {
                        if (body.getStatus().equalsIgnoreCase("ok")) {
                            Tools.showToast(AlumniActivity.this, getString(R.string.berhasil_update));
                            setResult(Activity.RESULT_OK);
                            finish();
                        } else {
                            Tools.showDialogAlert(AlumniActivity.this, body.getMessage());
                        }
                    }
                    else {
                        Tools.showDialogAlert(AlumniActivity.this, response.message());
                    }
                } else {
                    Tools.showToast(AlumniActivity.this, getString(R.string.gagal_daftar));
                }
                Tools.dissmissProgressDialog();
            }

            @Override
            public void onFailure(@NonNull Call<GeneralResponse> call, @NonNull Throwable t) {
                Tools.showToast(AlumniActivity.this, getString(R.string.gagal_daftar));
                Tools.dissmissProgressDialog();
            }
        });
    }
}
