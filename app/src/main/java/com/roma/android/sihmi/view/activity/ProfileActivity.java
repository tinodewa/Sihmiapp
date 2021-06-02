package com.roma.android.sihmi.view.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Contact;
import com.roma.android.sihmi.model.database.entity.User;
import com.roma.android.sihmi.model.database.interfaceDao.ContactDao;
import com.roma.android.sihmi.model.database.interfaceDao.LevelDao;
import com.roma.android.sihmi.model.database.interfaceDao.TrainingDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.model.network.ApiClient;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.response.GeneralResponse;
import com.roma.android.sihmi.model.response.ProfileResponse;
import com.roma.android.sihmi.model.response.UploadFileResponse;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Tools;
import com.roma.android.sihmi.utils.UploadFile;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("NonConstantResourceId")
public class ProfileActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.iv_photo)
    ImageView ivPhoto;
    @BindView(R.id.iv_initial)
    ImageView ivInitial;
    @BindView(R.id.et_fullname)
    EditText etFullname;
    @BindView(R.id.et_panggilan)
    EditText etPanggilan;
    @BindView(R.id.et_jenis_kelamin)
    EditText etJenisKelamin;
    @BindView(R.id.et_nohp)
    EditText etNoHp;
    @BindView(R.id.et_email)
    EditText etEmail;
    @BindView(R.id.et_alamat_domisili)
    EditText etAlamatDomisili;
    @BindView(R.id.cv_perbarui_sandi)
    CardView cvPerbaruiSandi;
    @BindView(R.id.et_perbarui_sandi)
    EditText etPerbaruiSandi;
    @BindView(R.id.iv_perbarui_sandi)
    ImageView ivPerbaruiSandi;
    @BindView(R.id.cv_lk_1)
    CardView cvLk1;
    @BindView(R.id.et_lk_1)
    EditText etLK1;
    @BindView(R.id.iv_lk_1)
    ImageView ivLk1;
    @BindView(R.id.cv_alumni)
    CardView cvAlumni;
    @BindView(R.id.et_alumni)
    EditText etAlumni;
    @BindView(R.id.iv_alumni)
    ImageView ivAlumni;
    @BindView(R.id.cv_data_lain)
    CardView cvDataLain;
    @BindView(R.id.et_data_lain)
    EditText etDataLain;
    @BindView(R.id.iv_data_lain)
    ImageView ivDataLain;
    @BindView(R.id.cv_permohonan_admin)
    CardView cvPermohonanAdmin;
    @BindView(R.id.et_permohonan_admin)
    EditText etPermohonanAdmin;
    @BindView(R.id.iv_permohonan_admin)
    ImageView ivPermohonanAdmin;
    @BindView(R.id.fab_delete)
    FloatingActionButton fabDel;
    @BindView(R.id.fab_edit)
    FloatingActionButton fabEdit;

    User user;
    Uri uri;
    String urlImage, jenis_kelamin;
    MasterService service;

    AppDb appDb;
    UserDao userDao;
    LevelDao levelDao;
    TrainingDao trainingDao;
    ContactDao contactDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        initToolbar();
        initModule();
        initView();
        getData();

        etPerbaruiSandi.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, KataSandiActivity.class)));
    }

    private void initToolbar(){
        toolbar.setTitle(getString(R.string.perbarui_profil));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    private void initModule(){
        service = ApiClient.getInstance().getApi();
        appDb = AppDb.getInstance(this);
        userDao = appDb.userDao();
        levelDao = appDb.levelDao();
        trainingDao = appDb.trainingDao();
        contactDao = appDb.contactDao();
        user = userDao.getUser();
        jenis_kelamin = user.getJenis_kelamin();
    }

    private void initView(){
        if (Tools.isNonLK()){
            cvAlumni.setEnabled(false);
            etAlumni.setTextColor(getResources().getColor(R.color.textColorDisable));
            cvDataLain.setEnabled(false);
            etDataLain.setTextColor(getResources().getColor(R.color.textColorDisable));
            cvPermohonanAdmin.setEnabled(false);
            etPermohonanAdmin.setTextColor(getResources().getColor(R.color.textColorDisable));
        } else if (Tools.isLK()){
            if (user.getTanggal_lk1() != null && !user.getTanggal_lk1().trim().isEmpty()){
                if (getUmurLK1(user.getTanggal_lk1()) < 4){
                    cvAlumni.setEnabled(false);
                    etAlumni.setTextColor(getResources().getColor(R.color.textColorDisable));
                }
            } else {
                cvAlumni.setEnabled(false);
                etAlumni.setTextColor(getResources().getColor(R.color.textColorDisable));
            }
        } else if (Tools.isSecondAdmin() || Tools.isSuperAdmin()){
            cvPermohonanAdmin.setVisibility(View.GONE);
            cvLk1.setVisibility(View.GONE);
            if (Tools.isSuperAdmin()){
                cvAlumni.setVisibility(View.GONE);
                cvDataLain.setVisibility(View.GONE);
            }
        }

        setText(etFullname, user.getFullName());
        setText(etPanggilan, user.getNama_panggilan());
        setText(etJenisKelamin, user.getGender());
        setText(etNoHp, user.getNomor_hp());
        setText(etEmail, user.getEmail());
        setText(etAlamatDomisili, user.getAlamat());

        urlImage = user.getImage();
        Tools.initial(ivInitial, user.getNama_depan());
        if (user.getImage() != null && !user.getImage().isEmpty()  && !user.getImage().equals(" ")){
            Glide.with(ProfileActivity.this)
                    .load(Uri.parse(user.getImage()))
                    .into(ivPhoto);
            ivInitial.setVisibility(View.GONE);
            ivPhoto.setVisibility(View.VISIBLE);
            fabDel.setVisibility(View.VISIBLE);
        } else {
            ivInitial.setVisibility(View.VISIBLE);
            ivPhoto.setVisibility(View.GONE);
            fabDel.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.iv_perbarui_sandi, R.id.iv_lk_1, R.id.iv_alumni, R.id.iv_data_lain, R.id.iv_permohonan_admin, R.id.iv_photo, R.id.iv_initial})
    public void click(ImageView imageView){
        int id = imageView.getId();
        switch (id){
            case R.id.iv_perbarui_sandi:
                startActivity(new Intent(ProfileActivity.this, KataSandiActivity.class));
                break;
            case R.id.iv_lk_1:
                startActivity(new Intent(ProfileActivity.this, LK1Activity.class));
                break;
            case R.id.iv_alumni:
                if (!Tools.isNonLK()) {
                    startActivityForResult(new Intent(ProfileActivity.this, AlumniActivity.class), Constant.REQUEST_PROFILE);
                }
                break;
            case R.id.iv_data_lain:
                if (!Tools.isNonLK()) {
                    startActivityForResult(new Intent(ProfileActivity.this, DataLainActivity.class), Constant.REQUEST_PROFILE);
                }
                break;
            case R.id.iv_permohonan_admin:
                if (!Tools.isNonLK()) {
                    startActivity(new Intent(ProfileActivity.this, PermohonanAdminActivity.class));
                }
                break;
            case R.id.iv_photo:
            case R.id.iv_initial:
                UploadFile.selectImage(this);
                break;
        }
    }

    @OnClick({R.id.et_perbarui_sandi, R.id.et_lk_1, R.id.et_alumni, R.id.et_data_lain, R.id.et_permohonan_admin})
    public void click(EditText editText){
        int id = editText.getId();
        switch (id){
            case R.id.et_perbarui_sandi:
                startActivity(new Intent(ProfileActivity.this, KataSandiActivity.class));
                break;
            case R.id.et_lk_1:
                startActivity(new Intent(ProfileActivity.this, LK1Activity.class));
                break;
            case R.id.et_alumni:
                if (!Tools.isNonLK()) {
                    startActivityForResult(new Intent(ProfileActivity.this, AlumniActivity.class), Constant.REQUEST_PROFILE);
                }
                break;
            case R.id.et_data_lain:
                if (!Tools.isNonLK()) {
                    startActivityForResult(new Intent(ProfileActivity.this, DataLainActivity.class), Constant.REQUEST_PROFILE);
                }
                break;
            case R.id.et_permohonan_admin:
                if (!Tools.isNonLK()) {
                    startActivity(new Intent(ProfileActivity.this, PermohonanAdminActivity.class));
                }
                break;
        }
    }

    @OnClick(R.id.btn_simpan)
    public void save(){
        if (Tools.isOnline(this)) {
            String fullname = etFullname.getText().toString().trim();
            String name = etPanggilan.getText().toString().trim();
            String jk = etJenisKelamin.getText().toString().trim();
            String noHp = etNoHp.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String alamat = etAlamatDomisili.getText().toString().trim();
            if (!fullname.isEmpty() && !name.isEmpty() && !jk.isEmpty() && !noHp.isEmpty() && !alamat.isEmpty() && !email.isEmpty()){
                if (Tools.isValidEmail(email)) {
                    updateProfile(fullname, name, jenis_kelamin, noHp, alamat, email);
                } else {
                    Tools.showToast(ProfileActivity.this, getString(R.string.email_not_valid));
                }
            } else {
                Tools.showToast(ProfileActivity.this, getString(R.string.field_mandatory));
            }
        } else {
            Tools.showToast(ProfileActivity.this, getString(R.string.tidak_ada_internet));
        }
    }
    @OnClick(R.id.et_jenis_kelamin)
    public void goGender(){
        Tools.showDialogGender(ProfileActivity.this, ket -> {
            jenis_kelamin = ket;
            String gender;
            if (ket.equals("0")){
                gender = getString(R.string.laki2);
            } else {
                gender = getString(R.string.perempuan);
            }
            etJenisKelamin.setText(gender);
        });
    }

    @OnClick({R.id.fab_delete, R.id.fab_edit})
    public void fabClick(FloatingActionButton view){
        switch (view.getId()){
            case R.id.fab_delete:
                updatePhoto("");
                break;
            case R.id.fab_edit:
                UploadFile.selectImage(this);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, ProfileActivity.this);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_GALLERY_CODE && resultCode == Activity.RESULT_OK) {
            uri = data.getData();
            if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Uri imageUri = CropImage.getPickImageResultUri(this, data);
                if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                    uri = imageUri;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                    }
                }
                UploadFile.startCropImageActivity(this, imageUri);

            } else {
                EasyPermissions.requestPermissions(this, getString(R.string.akses_penyimpanan), Constant.READ_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        } else if (requestCode == Constant.REQUEST_PROFILE && resultCode == Activity.RESULT_OK){
            getData();
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
                String filePath = UploadFile.getRealPathFromURIPath(comUri, ProfileActivity.this);

                if (Tools.isOnline(this)) {
                    Tools.showProgressDialog(ProfileActivity.this, getString(R.string.mengunggah_foto));
                    UploadFile.uploadFileToServer(Constant.IMAGE, filePath, new Callback<UploadFileResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<UploadFileResponse> call, @NonNull Response<UploadFileResponse> response) {
                            Tools.dissmissProgressDialog();
                            if (response.isSuccessful()) {
                                UploadFileResponse body = response.body();
                                if (body != null) {
                                    if (body.getStatus().equalsIgnoreCase("ok")) {
                                        if (body.getData().size() > 0) {
                                            updatePhoto(body.getData().get(0).getUrl());
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<UploadFileResponse> call, @NonNull Throwable t) {
                            Tools.dissmissProgressDialog();
                        }
                    });
                } else {
                    Tools.showToast(ProfileActivity.this, getString(R.string.tidak_ada_internet));
                }
//                File file = new File(filePath);
//                uploadImage(file);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, getString(R.string.gagal_potong) + result.getError(), Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == Constant.REQUEST_GANTI_POTO && resultCode == Activity.RESULT_OK){
            getData();
        }

    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    private void getData(){
        Call<ProfileResponse> call = service.getProfile(Constant.getToken());
        call.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                if (response.isSuccessful()){
                    ProfileResponse body = response.body();
                    if (body != null) {
                        if (body.getStatus().equalsIgnoreCase("ok")){
                            userDao.insertUser(body.getData());
                            saveProfiletoContact();
                        } else {
                            Toast.makeText(ProfileActivity.this, ""+body.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(ProfileActivity.this, getString(R.string.gagal_load_data), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, getString(R.string.gagal_load_data), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {
                Toast.makeText(ProfileActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProfiletoContact(){
        User user = userDao.getUser();
        int id_level = levelDao.getPengajuanLevel(user.getId_roles());
        Contact contact = new Contact(user.get_id(), user.getBadko(), user.getCabang(), user.getKorkom(), user.getKomisariat(),
                user.getId_roles(), user.getImage(), user.getNama_depan(), user.getNama_belakang(), user.getNama_panggilan(),
                user.getJenis_kelamin(), user.getNomor_hp(), user.getAlamat(), user.getUsername(), user.getTanggal_daftar(),
                user.getTempat_lahir(), user.getTanggal_lahir(), user.getStatus_perkawinan(), user.getKeterangan(), user.getDevice_name(),
                user.getLast_login(), user.getEmail(), user.getStatus_online(), id_level);
        contact.setDomisili_cabang(user.getDomisili_cabang());
        contact.setTanggal_lk1(user.getTanggal_lk1());
        contact.setTahun_daftar(Tools.getYearFromMillis(Long.parseLong(contact.getTanggal_daftar())));

        contactDao.insertContact(contact);
    }

    private void updatePhoto (String url){
        if (Tools.isOnline(this)) {
            Tools.showProgressDialog(ProfileActivity.this, getString(R.string.mengganti_foto_profile));

            User user = userDao.getUser();

            Call<GeneralResponse> call = service.updateProfile(Constant.getToken(), user.getBadko(), user.getCabang(), user.getKorkom(), user.getKomisariat(), user.getId_roles(), url, user.getNama_depan(), user.getNama_belakang(),
                    user.getNama_panggilan(), user.getJenis_kelamin(), user.getNomor_hp(), user.getAlamat(), user.getUsername(),
                    user.getTempat_lahir(), user.getTanggal_lahir(), user.getStatus_perkawinan(), "", user.getEmail(), user.getAkun_sosmed(), user.getDomisili_cabang(), user.getPekerjaan(), user.getJabatan(), user.getAlamat_kerja(), user.getKontribusi());
            call.enqueue(new Callback<GeneralResponse>() {
                @Override
                public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                    Tools.dissmissProgressDialog();
                    if (response.isSuccessful()) {
                        GeneralResponse body = response.body();
                        if (body != null) {
                            if (body.getStatus().equalsIgnoreCase("ok")) {
                                if (url.isEmpty()){
                                    ivInitial.setVisibility(View.VISIBLE);
                                    ivPhoto.setVisibility(View.GONE);
                                    fabDel.setVisibility(View.GONE);
                                } else {
                                    ivInitial.setVisibility(View.GONE);
                                    ivPhoto.setVisibility(View.VISIBLE);
                                    fabDel.setVisibility(View.VISIBLE);
                                    userDao.updatePhoto(user.get_id(), url);
                                    Glide.with(ProfileActivity.this)
                                            .load(Uri.parse(url))
                                            .into(ivPhoto);
                                }
                                user.setImage(url);
                                userDao.updatePhoto(user.get_id(), url);
                                setResult(Activity.RESULT_OK);

                            } else {
                                Toast.makeText(ProfileActivity.this, "" + body.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<GeneralResponse> call, @NonNull Throwable t) {
                    Tools.dissmissProgressDialog();
                    Toast.makeText(ProfileActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, getString(R.string.tidak_ada_internet), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateProfile(String nama, String nama_panggilan, String jenis_kelamin, String no_hp, String alamat, String email) {
        Tools.showProgressDialog(ProfileActivity.this, getString(R.string.perbarui_profil));
        User user = userDao.getUser();

        Call<GeneralResponse> call = service.updateProfile(Constant.getToken(), user.getBadko(), user.getCabang(), user.getKorkom(), user.getKomisariat(), user.getId_roles(), user.getImage(), nama, user.getNama_belakang(),
                nama_panggilan, jenis_kelamin, no_hp, alamat, user.getUsername(),
                user.getTempat_lahir(), user.getTanggal_lahir(), user.getStatus_perkawinan(), "", email, user.getAkun_sosmed(), user.getDomisili_cabang(), user.getPekerjaan(), user.getJabatan(), user.getAlamat_kerja(), user.getKontribusi());
        call.enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                Tools.dissmissProgressDialog();
                if (response.isSuccessful()) {
                    GeneralResponse body = response.body();
                    if (body != null) {
                        if (body.getStatus().equalsIgnoreCase("ok")) {
                            userDao.updateProfile(user.get_id(), nama, nama_panggilan, jenis_kelamin, no_hp, alamat);
                            userDao.updatePhoto(user.get_id(), user.getImage());
                            setResult(Activity.RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(ProfileActivity.this, "" + body.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<GeneralResponse> call, @NonNull Throwable t) {
                Tools.dissmissProgressDialog();
                Toast.makeText(ProfileActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setText(EditText editText, String text){
        if (text != null && !text.trim().isEmpty()){
            editText.setText(text);
        }
    }

    public int getUmurLK1(String tanggal) {
        int umur = 0;
        Date tglLk1 = null;
        long time;
        try {
            time = Objects.requireNonNull(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(tanggal)).getTime();
            tglLk1 = new Date(time);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            umur = getAgeLK1(tglLk1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return umur;

    }

    public int getAgeLK1(Date dateOfLK1) throws Exception {

        Calendar today = Calendar.getInstance();
        Calendar LK1 = Calendar.getInstance();

        int age;

        LK1.setTime(dateOfLK1);
        if (LK1.after(today)) {
            throw new Exception(getString(R.string.tanggal_lk1_tidak_boleh));
        }

        age = today.get(Calendar.YEAR) - LK1.get(Calendar.YEAR);

        if ((LK1.get(Calendar.DAY_OF_YEAR)
                - today.get(Calendar.DAY_OF_YEAR) > 3)
                || (LK1.get(Calendar.MONTH) > today.get(Calendar.MONTH))) {
            age--;
        } else if ((LK1.get(Calendar.MONTH) == today.get(Calendar.MONTH))
                && (LK1.get(Calendar.DAY_OF_MONTH) > today
                .get(Calendar.DAY_OF_MONTH))) {
            age--;
        }

        return age;
    }

}
