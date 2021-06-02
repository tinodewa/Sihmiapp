package com.roma.android.sihmi.view.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Agenda;
import com.roma.android.sihmi.model.database.interfaceDao.AgendaDao;
import com.roma.android.sihmi.model.database.interfaceDao.MasterDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.model.network.ApiClient;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.response.GeneralResponse;
import com.roma.android.sihmi.model.response.UploadFileResponse;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Tools;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("NonConstantResourceId")
public class AgendaFormActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {
    public static String IS_NEW = "IS_NEW";
    public static String ID_AGENDA = "ID_AGENDA";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_type)
    EditText etType;
    @BindView(R.id.et_nama_type)
    EditText etNamaType;
    @BindView(R.id.et_title)
    EditText etTitle;
    @BindView(R.id.et_tanggal)
    EditText etTanggal;
    @BindView(R.id.et_jam)
    EditText etJam;
    @BindView(R.id.iv_tanggal)
    ImageView ivTanggal;
    @BindView(R.id.iv_jam)
    ImageView ivJam;
    @BindView(R.id.et_tempat)
    EditText etTempat;
    @BindView(R.id.et_alamat)
    EditText etAlamat;
    @BindView(R.id.et_desc)
    EditText etDesc;
    @BindView(R.id.iv_image)
    ImageView ivImage;
    @BindView(R.id.btn_simpan)
    Button btnSimpan;

    MasterService service;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    SimpleDateFormat dateFormatter;
    String id_agenda="", judul;
    Agenda agenda;
    boolean isNew;
    private Uri uri;
    String urlImage;

    AppDb appDb;
    UserDao userDao;
    AgendaDao agendaDao;
    MasterDao masterDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda_form);
        ButterKnife.bind(this);

        service = ApiClient.getInstance().getApi();
        appDb = AppDb.getInstance(this);
        userDao = appDb.userDao();
        agendaDao = appDb.agendaDao();
        masterDao = appDb.masterDao();

        isNew = getIntent().getBooleanExtra(IS_NEW, false);
        if (isNew) {
            judul = getString(R.string.tambah_agenda);
        } else {
            id_agenda = getIntent().getStringExtra(ID_AGENDA);
            agenda = agendaDao.getAgendaById(id_agenda);
            judul = getString(R.string.perbarui_agenda);
            etTitle.setText(agenda.getNama());
            String jamStr = Tools.getTimeFromMillis(agenda.getDate_expired()) + getString(R.string.sampai_selesai);
            etJam.setText(jamStr);
            etTanggal.setText(Tools.getDateFromMillis(agenda.getDate_expired()));
            etTempat.setText(agenda.getTempat());
            etAlamat.setText(agenda.getLokasi());
            urlImage = agenda.getImage();
            String[] type = agenda.getType().split("-");
            etType.setText(Tools.getStringType(type[0]));
            etNamaType.setText(type[1]);
            if (agenda.getImage() != null && !agenda.getImage().isEmpty())
                Glide.with(AgendaFormActivity.this)
                        .load(Uri.parse(agenda.getImage()))
                        .into(ivImage);
        }

        toolbar.setTitle(judul.toUpperCase());
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

        etJam.setFocusable(false);
        etTanggal.setFocusable(false);
        etType.setFocusable(false);
        etNamaType.setFocusable(false);

        etTanggal.setOnClickListener(v -> showDateDialog());

        etJam.setOnClickListener(v -> showTimeDialog());

        viewAdmin();
    }

    private void viewAdmin(){
        if (!Tools.isSuperAdmin() && !Tools.isSecondAdmin()) {
            if (Tools.isAdmin1()){
                etNamaType.setText(userDao.getUser().getKomisariat());
            } else if (Tools.isAdmin2() || Tools.isLA1()){
                etNamaType.setText(userDao.getUser().getCabang());
            } else if (Tools.isLA2()){
                etNamaType.setText(R.string.pb_hmi_name);
            }
            etType.setVisibility(View.GONE);
            etNamaType.setEnabled(false);
            etNamaType.setBackgroundColor(getResources().getColor(R.color.colorTextLight));
        }
    }

    @OnClick(R.id.iv_image)
    public void upload(){
        selectImage();
    }

    @OnClick(R.id.btn_simpan)
    public void simpan(){
        if (isNew){
            addAgenda();
        } else {
            updateAgenda();
        }
    }

    private void addAgenda(){
        String type;
        if (etNamaType.getText().toString().equals("PB HMI")) {
            type = "0-PB HMI";
        } else {
            type = masterDao.getTypeMasterByValue(etNamaType.getText().toString()) + "-" + etNamaType.getText().toString();
        }
        Log.d("halooo", "addAgenda: "+type+" ---> "+etNamaType.getText().toString());
        if (!etTanggal.getText().toString().isEmpty() && !etJam.getText().toString().isEmpty() && !etTitle.getText().toString().isEmpty() && !etTempat.getText().toString().isEmpty() && !etAlamat.getText().toString().isEmpty()){
            String date = etTanggal.getText().toString()+" "+etJam.getText().toString();
            long longTime = getDateFromString(date).getTime();

            Call<GeneralResponse> call = service.addAgenda(Constant.getToken(), etTitle.getText().toString(), "", urlImage, etTempat.getText().toString(), etAlamat.getText().toString(), longTime, type);

            if (Tools.isOnline(this)) {
                Tools.showProgressDialog(this, getString(R.string.menambah_agenda));
                call.enqueue(new Callback<GeneralResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                        if (response.isSuccessful()) {
                            GeneralResponse body = response.body();
                            if (body != null) {
                                if (body.getStatus().equalsIgnoreCase("ok")) {
                                    Toast.makeText(AgendaFormActivity.this, getString(R.string.berhasil_tambah), Toast.LENGTH_SHORT).show();
                                    setResult(Activity.RESULT_OK);
                                    finish();
                                } else {
                                    Toast.makeText(AgendaFormActivity.this, getString(R.string.gagal_tambah), Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                Toast.makeText(AgendaFormActivity.this, getString(R.string.gagal_tambah), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(AgendaFormActivity.this, getString(R.string.gagal_tambah), Toast.LENGTH_SHORT).show();
                        }
                        Tools.dissmissProgressDialog();
                    }

                    @Override
                    public void onFailure(@NonNull Call<GeneralResponse> call, @NonNull Throwable t) {
                        Toast.makeText(AgendaFormActivity.this, getString(R.string.gagal_tambah), Toast.LENGTH_SHORT).show();
                        Tools.dissmissProgressDialog();
                    }
                });
            }

        } else {
            Toast.makeText(this, getString(R.string.field_mandatory), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateAgenda(){
        String type;
        if (etNamaType.getText().toString().equals("PB HMI")) {
            type = "0-PB HMI";
        } else {
            type = masterDao.getTypeMasterByValue(etNamaType.getText().toString()) + "-" + etNamaType.getText().toString();
        }
        if (!etTanggal.getText().toString().isEmpty() && !etJam.getText().toString().isEmpty() && !etTitle.getText().toString().isEmpty() && !etTempat.getText().toString().isEmpty() && !etAlamat.getText().toString().isEmpty()){
            Call<GeneralResponse> call = service.updateAgenda(Constant.getToken(), id_agenda, etTitle.getText().toString(), "", urlImage, etTempat.getText().toString(), etAlamat.getText().toString(),  type);

            if (Tools.isOnline(this)) {
                Tools.showProgressDialog(this, getString(R.string.update_agenda));
                call.enqueue(new Callback<GeneralResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                        if (response.isSuccessful()) {
                            GeneralResponse body = response.body();
                            if (body != null) {
                                if (body.getStatus().equalsIgnoreCase("ok")) {
                                    Tools.showToast(AgendaFormActivity.this, getString(R.string.berhasil_update));
                                    setResult(RESULT_OK);
                                    finish();
                                } else {
                                    Tools.showToast(AgendaFormActivity.this, getString(R.string.gagal_update));
                                }
                            }
                            else {
                                Tools.showToast(AgendaFormActivity.this, getString(R.string.gagal_update));
                            }
                        } else {
                            Tools.showToast(AgendaFormActivity.this, getString(R.string.gagal_update));
                        }
                        Tools.dissmissProgressDialog();
                    }

                    @Override
                    public void onFailure(@NonNull Call<GeneralResponse> call, @NonNull Throwable t) {
                        Tools.showToast(AgendaFormActivity.this, getString(R.string.gagal_update));
                        Tools.dissmissProgressDialog();
                    }
                });
            } else {
                Toast.makeText(this, getString(R.string.tidak_ada_internet), Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, getString(R.string.tidak_ada_internet), Toast.LENGTH_SHORT).show();
        }
    }

    private void showDateDialog(){

        Calendar newCalendar = Calendar.getInstance();

        datePickerDialog = new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {

            Calendar newDate = Calendar.getInstance();
            newDate.set(year, monthOfYear, dayOfMonth);

            etTanggal.setText(dateFormatter.format(newDate.getTime()));
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void showTimeDialog() {

        Calendar calendar = Calendar.getInstance();

        timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            String min;
            if (minute < 10){
                min = "0"+minute;
            } else {
                min = String.valueOf(minute);
            }
            String jamStr = hourOfDay+":"+min;
            etJam.setText(jamStr);
        },
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),

                DateFormat.is24HourFormat(this));

        timePickerDialog.show();
    }

    public Date getDateFromString(String value) {
        java.text.DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ENGLISH);
        try {
            if (value == null || value.equals(""))
                return new Date();
            return formatter.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    @OnClick({R.id.et_type, R.id.et_nama_type})
    public void click(EditText editText){
        switch (editText.getId()){
            case R.id.et_type:
                if (Tools.isSuperAdmin() || Tools.isSecondAdmin()) {
                    Tools.showDialogType(this, res -> {
                        etType.setText(res);

                        if (res.equalsIgnoreCase("nasional")) {
                            etNamaType.setText(R.string.pb_hmi_name);
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
                    }
                    else if (etType.getText().toString().equalsIgnoreCase("komisariat")) {
                        list = masterDao.getMasterKomisariat();
                        array = new String[list.size()];
                        list.toArray(array);
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

    private void selectImage(){
        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
        openGalleryIntent.setType("image/*");
        startActivityForResult(openGalleryIntent, Constant.REQUEST_GALLERY_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, AgendaFormActivity.this);
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
                startCropImageActivity(imageUri);

            } else {
                EasyPermissions.requestPermissions(this, getString(R.string.akses_penyimpanan), Constant.READ_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        } if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                File compFile = null;
                try {
                    compFile = new Compressor(this).compressToFile(new File(Objects.requireNonNull(result.getUri().getPath())));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Uri comUri = Uri.fromFile(compFile);
                String filePath = getRealPathFromURIPath(comUri, AgendaFormActivity.this);
                File file = new File(filePath);
                int file_size = Integer.parseInt(String.valueOf(file.length()/1024));
                if (file_size < 2000){
                    uploadImage(file);
                } else {
                    Tools.showToast(this, getString(R.string.maks_foto));
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, getString(R.string.gagal_potong) + result.getError(), Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == Constant.REQUEST_GANTI_POTO && resultCode == Activity.RESULT_OK){
            Log.d("formagenda", "onActivityResult: "+urlImage);
        }


    }

    private String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        String str;
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            str = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            str = cursor.getString(idx);
            cursor.close();
        }

        return str;
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if(uri != null){
            String filePath = getRealPathFromURIPath(uri, AgendaFormActivity.this);
            File file = new File(filePath);
            RequestBody.create(MediaType.parse("image/*"), file);
        }
    }
    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(9, 16)
                .setFixAspectRatio(false)
                .setMultiTouchEnabled(true)
                .start(this);
    }

    private void uploadImage(File file) {
        if (Tools.isOnline(this)) {
            Tools.showProgressDialog(AgendaFormActivity.this, getString(R.string.mengunggah_foto));

            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
            Map<String, RequestBody> map = new HashMap<>();
            map.put("files\"; filename=\"" + file.getName() + "\"", requestBody);

            // finally, execute the request
            Call<UploadFileResponse> call = service.uploadFile(Constant.getToken(), map);
            call.enqueue(new Callback<UploadFileResponse>() {
                @Override
                public void onResponse(@NonNull Call<UploadFileResponse> call, @NonNull Response<UploadFileResponse> response) {
                    if (response.isSuccessful()) {
                        UploadFileResponse body = response.body();
                        if (body != null) {
                            if (body.getStatus().equalsIgnoreCase("ok")) {
                                Log.d("uploadFile", "onResponse: ok");
                                Toast.makeText(AgendaFormActivity.this, getString(R.string.sukses_upload_file), Toast.LENGTH_SHORT).show();
                                String url = body.getData().get(0).getUrl();

                                Log.d("tes", "onResponse: formagenda "+url);
                                urlImage = url;
                                Glide.with(AgendaFormActivity.this)
                                        .load(Uri.parse(url))
                                        .into(ivImage);

                            } else {
                                Toast.makeText(AgendaFormActivity.this, "" + body.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(AgendaFormActivity.this, "" + response.message(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(AgendaFormActivity.this, "" + response.message(), Toast.LENGTH_SHORT).show();
                    }
                    Tools.dissmissProgressDialog();
                }

                @Override
                public void onFailure(@NonNull Call<UploadFileResponse> call, @NonNull Throwable t) {
                    Tools.dissmissProgressDialog();
                    Toast.makeText(AgendaFormActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Tidak Ada Internet!", Toast.LENGTH_SHORT).show();
        }
    }
}
