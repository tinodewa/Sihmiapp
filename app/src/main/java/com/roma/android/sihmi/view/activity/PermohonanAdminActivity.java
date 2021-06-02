package com.roma.android.sihmi.view.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.User;
import com.roma.android.sihmi.model.database.interfaceDao.LevelDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.model.network.ApiClient;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.response.GeneralResponse;
import com.roma.android.sihmi.model.response.PengajuanAdminResponse;
import com.roma.android.sihmi.model.response.UploadFileResponse;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Tools;
import com.roma.android.sihmi.utils.UploadFile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("NonConstantResourceId")
public class PermohonanAdminActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_type)
    EditText etType;
    @BindView(R.id.et_hak_akses)
    EditText etHakAkses;
    @BindView(R.id.et_unggah)
    EditText etUnggah;
    @BindView(R.id.img_attach)
    ImageView imgAttach;
    @BindView(R.id.iv_image)
    ImageView ivImage;
    @BindView(R.id.checkbox)
    CheckBox checkBox;
    @BindView(R.id.btn_simpan)
    Button btnSimpan;

    User user;
    String urlFile;
    MasterService service;
    AppDb appDb;
    UserDao userDao;
    LevelDao levelDao;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permohonan_admin);
        ButterKnife.bind(this);
        initToolbar();

        service = ApiClient.getInstance().getApi();
        appDb = AppDb.getInstance(this);
        userDao = appDb.userDao();
        levelDao = appDb.levelDao();
        user = userDao.getUser();
        etType.setText(user.getKomisariat());
        etType.setTextColor(getResources().getColor(R.color.colorBlack));
        etType.setBackground(getResources().getDrawable(R.color.colorTextLight));
        etType.setEnabled(false);

        getPengajuan();
    }

    private void initToolbar(){
        toolbar.setTitle(getString(R.string.permohonan_admin_profil));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    @OnClick(R.id.et_hak_akses)
    public void clickHakAkses(){
        Tools.showDialogAdmin(this, ket -> etHakAkses.setText(ket));
    }

    @OnClick({R.id.img_attach, R.id.iv_image})
    public void click(){
        UploadFile.selectFile(PermohonanAdminActivity.this);
    }

    @OnClick(R.id.btn_simpan)
    public void onBtnSaveClick(){
        if (!etHakAkses.getText().toString().isEmpty() && !etUnggah.getText().toString().trim().isEmpty() && checkBox.isChecked()){
            addPengajuan(levelDao.getIdRoles(etHakAkses.getText().toString()), urlFile);
        } else {
            Toast.makeText(this, getString(R.string.field_kosong), Toast.LENGTH_SHORT).show();
        }
    }

    private void addPengajuan(String idRoles, String file){
        Call<GeneralResponse> call = service.addPengajuanAdmin(Constant.getToken(), idRoles, file);
        call.enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                if (response.isSuccessful()){
                    GeneralResponse body = response.body();
                    if (body != null) {
                        if (body.getStatus().equalsIgnoreCase("ok")){
                            Tools.showToast(PermohonanAdminActivity.this, getString(R.string.pengajuan_berhasil));
                        } else {
                            Tools.showToast(PermohonanAdminActivity.this, body.getMessage());
                        }
                    }
                    else {
                        Tools.showToast(PermohonanAdminActivity.this, getString(R.string.pengajuan_gagal));
                    }
                } else {
                    Tools.showToast(PermohonanAdminActivity.this, getString(R.string.pengajuan_gagal));
                }
                finish();
            }

            @Override
            public void onFailure(@NonNull Call<GeneralResponse> call, @NonNull Throwable t) {
                Tools.showToast(PermohonanAdminActivity.this, getString(R.string.pengajuan_gagal));
                finish();

            }
        });
    }

    private void getPengajuan(){
        Call<PengajuanAdminResponse> call = service.getPengajuanAdmin(Constant.getToken());
        call.enqueue(new Callback<PengajuanAdminResponse>() {
            @Override
            public void onResponse(@NonNull Call<PengajuanAdminResponse> call, @NonNull Response<PengajuanAdminResponse> response) {

            }

            @Override
            public void onFailure(@NonNull Call<PengajuanAdminResponse> call, @NonNull Throwable t) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == Constant.REQUEST_DOC_CODE && resultCode == Activity.RESULT_OK) {
                String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
                sendFile(filePath);
            }
        }
    }

    private void sendFile(String filePath){
        if (Tools.isOnline(this)) {
            String msg = getString(R.string.mengunggah_dok);
            Tools.showProgressDialog(PermohonanAdminActivity.this, msg);
            UploadFile.uploadFileToServer(Constant.DOCUMENT, filePath, new Callback<UploadFileResponse>() {
                @Override
                public void onResponse(@NonNull Call<UploadFileResponse> call, @NonNull Response<UploadFileResponse> response) {
                    if (response.isSuccessful()) {
                        UploadFileResponse body = response.body();
                        if (body != null) {
                            if (body.getStatus().equalsIgnoreCase("ok")) {
                                if (body.getData().size() > 0) {
                                    etUnggah.setText(body.getData().get(0).getNama_file());
                                    urlFile = body.getData().get(0).getUrl();
                                }
                            }
                        }
                    }
                    Tools.dissmissProgressDialog();
                }

                @Override
                public void onFailure(@NonNull Call<UploadFileResponse> call, @NonNull Throwable t) {
                    Tools.dissmissProgressDialog();
                }
            });
        } else {
            Tools.showToast(PermohonanAdminActivity.this, getString(R.string.tidak_ada_internet));
        }
    }
}
