package com.roma.android.sihmi.view.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Leader;
import com.roma.android.sihmi.model.database.interfaceDao.LeaderDao;
import com.roma.android.sihmi.model.database.interfaceDao.MasterDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.model.network.ApiClient;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.response.GeneralResponse;
import com.roma.android.sihmi.model.response.UploadFileResponse;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Tools;
import com.roma.android.sihmi.utils.UploadFile;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("NonConstantResourceId")
public class LeaderFormActivity extends BaseActivity {
    public static String IS_NEW = "IS_NEW";
    public static String IS_UPDATE = "IS_NEW";
    public static String ID_LEAD = "ID_LEAD";
    public static String TYPE = "TYPE";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_type)
    EditText etType;
    @BindView(R.id.et_nama_type)
    EditText etNamaType;
    @BindView(R.id.et_nama)
    EditText etNama;
    @BindView(R.id.et_periode)
    EditText etPeriode;
    @BindView(R.id.et_sampai)
    EditText etSampai;
    @BindView(R.id.img_foto)
    ImageView imgFoto;
    @BindView(R.id.btn_simpan)
    Button btnSimpan;
    @BindView(R.id.tv_ket)
    TextView tvKet;
    @BindView(R.id.tv_maks)
    TextView tvMaks;

    int type;
    boolean isNew, isUpdate;
    String idLead, urlImage="";
    Leader leader;

    MasterService service;

    AppDb appDb;
    LeaderDao leaderDao;
    UserDao userDao;
    MasterDao masterDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_form);
        ButterKnife.bind(this);
        appDb = AppDb.getInstance(this);
        leaderDao = appDb.leaderDao();
        userDao = appDb.userDao();
        masterDao = appDb.masterDao();

        service = ApiClient.getInstance().getApi();
        isNew = getIntent().getBooleanExtra(IS_NEW, false);
        isUpdate = getIntent().getBooleanExtra(IS_UPDATE, false);
        idLead = getIntent().getStringExtra(ID_LEAD);
        Log.d("haaaaaaa", "onCreate: "+isNew+" - "+isUpdate);
        String title;
        if (isNew){
            title = getString(R.string.tambah_ketua_umum);
            type = getIntent().getIntExtra(TYPE, 0);
            if (Tools.isSecondAdmin() || Tools.isSuperAdmin()) {
                String tipe;
                if (type == 1) {
                    tipe = "0";
                    Constant.setTypeData(0);
                } else if (type == 2) {
                    tipe = "2";
                    Constant.setTypeData(1);
                } else {
                    tipe = "4";
                    Constant.setTypeData(2);
                }
                etType.setText(Tools.getStringType(tipe));
                if(type != 1) {
                    etNamaType.setText(masterDao.getFirstDataMasterByType(Integer.parseInt(tipe)));
                }
            }
        } else {
            if (idLead != null && !idLead.isEmpty()){
                leader = leaderDao.getLeaderById(idLead);
                urlImage = leader.getImage();
                etNama.setText(leader.getNama());
                etPeriode.setText(leader.getPeriode());
                etSampai.setText(leader.getSampai());
                String[] type = leader.getType().split("-");
                etType.setText(Tools.getStringType(type[0]));
                etNamaType.setText(type[1]);
                if (urlImage != null && !urlImage.isEmpty()){
                    Glide.with(LeaderFormActivity.this)
                            .load(Uri.parse(urlImage))
                            .into(imgFoto);
                }
            }
//            if (!isUpdate){
//                title = leader.getNama();
//                viewLeader();
//            } else {
                title = getString(R.string.perbarui_ketua_umum);
//            }
        }
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        etPeriode.setFocusable(false);
        etSampai.setFocusable(false);
        etType.setFocusable(false);

        viewAdmin();
    }
//
    private void viewAdmin(){
        if (!Tools.isSuperAdmin() && !Tools.isSecondAdmin()) {
            if (Tools.isAdmin1()){
                etNamaType.setText(userDao.getUser().getKomisariat());
            } else if (Tools.isAdmin2() || Tools.isLA1()){
                etNamaType.setText(userDao.getUser().getCabang());
            } else if (Tools.isLA2()){
                etNamaType.setText(getString(R.string.pb_hmi_name));
            }
            etType.setVisibility(View.GONE);
            etNamaType.setEnabled(false);
            etNamaType.setBackgroundColor(getResources().getColor(R.color.colorTextLight));
        }
    }

    @OnClick({R.id.et_periode, R.id.et_sampai, R.id.et_type, R.id.et_nama_type})
    public void click(EditText editText){
        switch (editText.getId()){
            case R.id.et_periode:
                dialogTahun(etPeriode, 0, true);
                break;
            case R.id.et_sampai:
                if (!etPeriode.getText().toString().trim().isEmpty()) {
                    int tahunMulai = Integer.parseInt(etPeriode.getText().toString());
                    dialogTahun(etSampai, tahunMulai, false);
                }
                break;
            case R.id.et_type:
                if (Tools.isSuperAdmin() || Tools.isSecondAdmin()) {
                    Tools.showDialogType(this, res -> {
                        etType.setText(res);

                        if (res.equalsIgnoreCase("nasional")) {
                            etNamaType.setText(getString(R.string.pb_hmi_name));
                        }
                        else if (res.equalsIgnoreCase("cabang")) {
                            List<String> listCabang = masterDao.getMasterCabang();
                            if (listCabang.size() > 0)
                                etNamaType.setText(listCabang.get(0));
                        }
                        else if (res.equalsIgnoreCase("komisariat")) {
                            List<String> listKomisariat = masterDao.getMasterKomisariat();
                            if (listKomisariat.size() > 0)
                                etNamaType.setText(listKomisariat.get(0));
                        }
                        else {
                            etNamaType.setText(null);
                        }
                    });
                }
                break;
            case R.id.et_nama_type:
                if (Tools.isSuperAdmin() || Tools.isSecondAdmin()) {
                    String[] array;
                    List<String> list;
                    if (etType.getText().toString().equalsIgnoreCase("cabang")) {
                        list = masterDao.getMasterCabang();
                        array = new String[list.size()];
                        list.toArray(array);
                        Log.d("LEADER FORM", "LEADER FORM this is cabang and the length of list is " + array.length + " " + masterDao.getMasterCabang());
                    }
                    else if (etType.getText().toString().equalsIgnoreCase("komisariat")) {
                        list = masterDao.getMasterKomisariat();
                        array = new String[list.size()];
                        list.toArray(array);
                        Log.d("LEADER FORM", "LEADER FORM this is komisariat and the length of list is " + array.length + " " + masterDao.getMasterKomisariat().size());
                    }
                    else {
                        array = new String[1];
                        array[0] = "PB HMI";
                    }
                    Tools.showDialogNamaType(this, array, res -> etNamaType.setText(res));
                }
                break;
        }
    }

    @OnClick(R.id.img_foto)
    public void upload(){
        UploadFile.selectImage(LeaderFormActivity.this);
    }

    @OnClick(R.id.btn_simpan)
    public void click(){
        if (!etNama.getText().toString().isEmpty() && !etPeriode.getText().toString().isEmpty() && !etSampai.getText().toString().isEmpty()){
            if (isNew){
                addLeader(etNama.getText().toString(), etPeriode.getText().toString(), etSampai.getText().toString(), urlImage);
            } else {
                updateLeader(leader.get_id(), etNama.getText().toString(), etPeriode.getText().toString(), etSampai.getText().toString(), urlImage);
            }
        } else {
            Tools.showToast(this, getString(R.string.field_mandatory));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, LeaderFormActivity.this);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_GALLERY_CODE && resultCode == Activity.RESULT_OK) {
            if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Uri imageUri = CropImage.getPickImageResultUri(this, data);
                if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                    }
                }
                UploadFile.startCropImageActivity(this, imageUri);

            } else {
                EasyPermissions.requestPermissions(this, getString(R.string.akses_penyimpanan), Constant.READ_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                File compFile = null;
                try {
                    compFile = new Compressor(this).compressToFile(new File(Objects.requireNonNull(result.getUri().getPath())));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Uri comUri = Uri.fromFile(compFile);
                String filePath = UploadFile.getRealPathFromURIPath(comUri, LeaderFormActivity.this);

                if (Tools.isOnline(this)) {
                    Tools.showProgressDialog(LeaderFormActivity.this, getString(R.string.mengunggah_foto));
                    UploadFile.uploadFileToServer(Constant.IMAGE, filePath, new Callback<UploadFileResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<UploadFileResponse> call, @NonNull Response<UploadFileResponse> response) {
                            if (response.isSuccessful()) {
                                UploadFileResponse body = response.body();
                                if (body != null) {
                                    if (body.getStatus().equalsIgnoreCase("ok")) {
                                        if (body.getData().size() > 0) {
                                            urlImage = body.getData().get(0).getUrl();

                                            Glide.with(LeaderFormActivity.this)
                                                    .load(Uri.parse(urlImage))
                                                    .into(imgFoto);
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
                    Tools.showToast(LeaderFormActivity.this, getString(R.string.tidak_ada_internet));
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, getString(R.string.gagal_potong) + result.getError(), Toast.LENGTH_LONG).show();
            }
        }

    }


    private void addLeader(String nama, String periode, String sampai, String image){
        if (Tools.isOnline(this)) {
            Tools.showProgressDialog(this, getString(R.string.menambah_leader));
            String type;
            if (etNamaType.getText().toString().equals("PB HMI")) {
                type = "0-PB HMI";
            } else {
                type = masterDao.getTypeMasterByValue(etNamaType.getText().toString()) + "-" + etNamaType.getText().toString();
            }
            Call<GeneralResponse> call = service.addLeader(Constant.getToken(), nama, periode, sampai, image, type);
            call.enqueue(new Callback<GeneralResponse>() {
                @Override
                public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                    if (response.isSuccessful()) {
                        GeneralResponse body = response.body();
                        if (body != null) {
                            if (body.getStatus().equalsIgnoreCase("ok")) {
                                Tools.showToast(LeaderFormActivity.this,getString(R.string.berhasil_tambah));
                                setResult(Activity.RESULT_OK);
                                finish();
                            } else {
                                Tools.showToast(LeaderFormActivity.this,getString(R.string.gagal_tambah));
                            }
                        }
                        else {
                            Tools.showToast(LeaderFormActivity.this,getString(R.string.gagal_tambah));
                        }
                    } else {
                        Tools.showToast(LeaderFormActivity.this,getString(R.string.gagal_tambah));
                    }
                    Tools.dissmissProgressDialog();
                }

                @Override
                public void onFailure(@NonNull Call<GeneralResponse> call, @NonNull Throwable t) {
                    Tools.showToast(LeaderFormActivity.this,getString(R.string.gagal_tambah));
                    Tools.dissmissProgressDialog();

                }
            });
        } else {
            Tools.showToast(this, getString(R.string.tidak_ada_internet));
        }
    }

    private void updateLeader(String id, String nama, String periode, String sampai, String image){
        if (Tools.isOnline(this)) {
            Tools.showProgressDialog(this, getString(R.string.update_leader));
            String type;
            if (etNamaType.getText().toString().equals("PB HMI")) {
                type = "0-PB HMI";
            } else {
                type = masterDao.getTypeMasterByValue(etNamaType.getText().toString()) + "-" + etNamaType.getText().toString();
            }
            Call<GeneralResponse> call = service.updateLeader(Constant.getToken(), id, nama, periode, sampai, image, type);
            call.enqueue(new Callback<GeneralResponse>() {
                @Override
                public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                    if (response.isSuccessful()) {
                        GeneralResponse body = response.body();
                        if (body != null) {
                            if (body.getStatus().equalsIgnoreCase("ok")) {
                                Tools.showToast(LeaderFormActivity.this,getString(R.string.berhasil_update));
                                setResult(Activity.RESULT_OK);
                                finish();
                            } else {
                                Tools.showToast(LeaderFormActivity.this,getString(R.string.gagal_update));
                            }
                        }
                        else {
                            Tools.showToast(LeaderFormActivity.this,getString(R.string.gagal_update));
                        }
                    } else {
                        Tools.showToast(LeaderFormActivity.this,getString(R.string.gagal_update));
                    }
                    Tools.dissmissProgressDialog();
                }

                @Override
                public void onFailure(@NonNull Call<GeneralResponse> call, @NonNull Throwable t) {
                    Tools.showToast(LeaderFormActivity.this,getString(R.string.gagal_update));
                    Tools.dissmissProgressDialog();

                }
            });
        } else {
            Tools.showToast(this, getString(R.string.tidak_ada_internet));
        }

    }

    private void dialogTahun(EditText editText, int tahunMulai, boolean reset){
        int layoutHeight = (int)(Tools.getScreenHeight() * 0.6);
        int layoutWidth = 250;

        Log.d("LOG DIALOG TAHUN", "LOG DIALOG TAHUN height : " + layoutHeight);

        if (layoutHeight > 800) layoutHeight = 800;

        String[] tahun = listTahun(tahunMulai).toArray(new String[0]);
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.mydialog)
                .setItems(tahun, (dialog1, which) -> {
                    editText.setText(tahun[which]);
                    if (reset) {
                        etSampai.setText(null);
                    }
                    dialog1.dismiss();
                }).create();
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(layoutWidth, layoutHeight);
            window.setGravity(Gravity.CENTER);
        }
    }

    private List<String> listTahun(int tahunMulai){
        int tahun = 1947;
        if (tahunMulai != 0){
            tahun = tahunMulai;
        }
        List<String> list = new ArrayList<>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = tahun; i<= thisYear; i++) {
            list.add(String.valueOf(i));
        }
        return list;
    }
}
