package com.roma.android.sihmi.view.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Konstituisi;
import com.roma.android.sihmi.model.database.entity.Master;
import com.roma.android.sihmi.model.database.interfaceDao.KonstitusiDao;
import com.roma.android.sihmi.model.database.interfaceDao.MasterDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.model.network.ApiClient;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.response.GeneralResponse;
import com.roma.android.sihmi.model.response.UploadFileResponse;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Tools;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("NonConstantResourceId")
public class KonstitusiFormActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_type)
    EditText etType;
    @BindView(R.id.et_nama_type)
    EditText etNamaType;
    @BindView(R.id.et_title)
    EditText etTitle;
    @BindView(R.id.et_unggah)
    EditText etUnggah;
    @BindView(R.id.img_attach)
    ImageView imgAttach;
    @BindView(R.id.iv_image)
    ImageView ivImage;
    @BindView(R.id.btn_simpan)
    Button btnSimpan;


    String id_konstitusi="", judul;
    Konstituisi konstitusi;
    boolean isNew = true;
    String pdfFileName, pdfPath="";
    String urlFile;
    MasterService service;

    AppDb appDb;
    UserDao userDao;
    KonstitusiDao konstitusiDao;
    MasterDao masterDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konstitusi_form);
        ButterKnife.bind(this);
        initModule();
        initToolbar();

    }

    private void initToolbar(){
        toolbar.setTitle(judul);
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
        konstitusiDao = appDb.konstitusiDao();
        masterDao = appDb.masterDao();

        id_konstitusi = getIntent().getStringExtra(Constant.ID_KONSTITUSI);
        if (id_konstitusi != null && !id_konstitusi.isEmpty()){
            judul = getString(R.string.perbarui_konstitusi);
            konstitusi = konstitusiDao.getKonstituisibyId(id_konstitusi);
            urlFile = konstitusi.getFile();
            isNew = false;
            initView();
        } else {
            judul = getString(R.string.tambah_konstitusi);
            adminSpecific();
        }
    }

    private void initView(){
        etTitle.setText(konstitusi.getNama());
        etUnggah.setText(konstitusi.getNama_file());
        etType.setFocusable(false);
        etNamaType.setFocusable(false);
        etUnggah.setFocusable(false);
        String[] type = konstitusi.getType().split("-");
        etType.setText(Tools.getStringType(type[0]));
        etNamaType.setText(type[1]);
        adminSpecific();
    }

    private void adminSpecific(){
        if (Tools.isAdmin1()) {
            etType.setVisibility(View.GONE);
            etNamaType.setEnabled(false);
            etNamaType.setBackgroundColor(getResources().getColor(R.color.colorTextLight));
            etNamaType.setText(userDao.getUser().getKomisariat());
        }
        else if (Tools.isLA1()) {
            etType.setText(getString(R.string.Komisariat));
            etType.setEnabled(false);
            String[] komsArray = getKomisariatByRole();
            if (komsArray.length > 0 && id_konstitusi == null) {
                etNamaType.setText(komsArray[0]);
            }
        }
        else if (Tools.isLA2()) {
            if (id_konstitusi == null) {
                etType.setText(getString(R.string.nasional));
                etType.setEnabled(false);
                etNamaType.setEnabled(false);
            }
        }
    }

    @OnClick(R.id.iv_image)
    public void upload(){

    }

    @OnClick(R.id.btn_simpan)
    public void simpan(){
        if (isNew){
            addKonstitusi();
        } else {
            updateKonstitusi();
        }
    }

    private void addKonstitusi(){
        if (Tools.isOnline(KonstitusiFormActivity.this)) {
            Tools.showProgressDialog(KonstitusiFormActivity.this, getString(R.string.add_konstitusi));
            String type;
            if (etNamaType.getText().toString().equals("PB HMI")) {
                type = "0-PB HMI";
            } else {
                type = masterDao.getTypeMasterByValue(etNamaType.getText().toString()) + "-" +
                        etNamaType.getText().toString() + "-" +
                        userDao.getUser().getCabang();
            }
            Call<GeneralResponse> call = service.addKonstitusi(Constant.getToken(), etTitle.getText().toString(), etUnggah.getText().toString(), "", urlFile , "", type);
            call.enqueue(new Callback<GeneralResponse>() {
                @Override
                public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                    if (response.isSuccessful()) {
                        GeneralResponse body = response.body();
                        if (body != null) {
                            if (body.getStatus().equalsIgnoreCase("ok")) {
                                Tools.showToast(KonstitusiFormActivity.this, getString(R.string.berhasil_tambah));
                            } else {
                                Tools.showToast(KonstitusiFormActivity.this, getString(R.string.gagal_tambah));
                            }
                        }
                        else {
                            Tools.showToast(KonstitusiFormActivity.this, getString(R.string.gagal_tambah));
                        }
                    } else {
                        Tools.showToast(KonstitusiFormActivity.this, getString(R.string.gagal_tambah));
                    }
                    setResult(Activity.RESULT_OK);
                    finish();
                    Tools.dissmissProgressDialog();
                }

                @Override
                public void onFailure(@NonNull Call<GeneralResponse> call, @NonNull Throwable t) {
                    Tools.showToast(KonstitusiFormActivity.this, getString(R.string.gagal_tambah));
                    Tools.dissmissProgressDialog();
                    finish();
                }
            });
        } else {
            Tools.showToast(KonstitusiFormActivity.this, getString(R.string.tidak_ada_internet));
        }
    }

    private void updateKonstitusi(){
        if (Tools.isOnline(this)) {
            Tools.showProgressDialog(KonstitusiFormActivity.this, getString(R.string.update_konstitusi));
            String type;
            if (etNamaType.getText().toString().equals("PB HMI")) {
                type = "0-PB HMI";
            } else {
                type = masterDao.getTypeMasterByValue(etNamaType.getText().toString()) + "-" +
                        etNamaType.getText().toString() + "-" +
                        userDao.getUser().getCabang();
            }
            Call<GeneralResponse> call = service.updateKonstitusi(Constant.getToken(), id_konstitusi, etTitle.getText().toString(), etUnggah.getText().toString(), "", urlFile, "", type);
            call.enqueue(new Callback<GeneralResponse>() {
                @Override
                public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                    if (response.isSuccessful()) {
                        GeneralResponse body = response.body();
                        if (body != null) {
                            if (body.getStatus().equalsIgnoreCase("ok")) {
                                Tools.showToast(KonstitusiFormActivity.this, getString(R.string.berhasil_update));
                            } else {
                                Tools.showToast(KonstitusiFormActivity.this, getString(R.string.gagal_update));
                            }
                        }
                        else {
                            Tools.showToast(KonstitusiFormActivity.this, getString(R.string.gagal_update));
                        }
                    } else {
                        Tools.showToast(KonstitusiFormActivity.this, getString(R.string.gagal_update));
                    }
                    setResult(Activity.RESULT_OK);
                    finish();
                    Tools.dissmissProgressDialog();
                }

                @Override
                public void onFailure(@NonNull Call<GeneralResponse> call, @NonNull Throwable t) {
                    Tools.showToast(KonstitusiFormActivity.this, getString(R.string.gagal_update));
                    Tools.dissmissProgressDialog();
                    finish();
                }
            });
        } else {
            Toast.makeText(this, getString(R.string.tidak_ada_internet), Toast.LENGTH_SHORT).show();
        }
    }

    private String[] getKomisariatByRole() {
        List<String> komisariatList;
        String[] array;
        if (Tools.isLA1()) {
            Master m = masterDao.getMasterByValue(userDao.getUser().getCabang());
            komisariatList = masterDao.getMasterKomisariatByCabangId(m.get_id());
            Log.d("Konstitusi", "Konstitusi master id " + m.get_id() +
                    " size " + komisariatList.size());
        }
        else {
            komisariatList = masterDao.getMasterKomisariat();
        }

        array = new String[komisariatList.size()];
        komisariatList.toArray(array);
        return array;
    }

    @OnClick({R.id.et_type, R.id.et_nama_type, R.id.et_unggah})
    public void click(EditText editText){
        switch (editText.getId()){
            case R.id.et_type:
                if (Tools.isSuperAdmin() || Tools.isSecondAdmin()) {
                    String[] list = {"Nasional", "Komisariat"};
                    Tools.showDialogNamaType(this, list, res -> {
                        etType.setText(res);

                        if (res.equalsIgnoreCase("nasional")) {
                            etNamaType.setText(getString(R.string.pb_hmi_name));
                        }
                        else if (res.equalsIgnoreCase("komisariat")) {
                            String[] komsArray = getKomisariatByRole();
                            if (komsArray.length > 0)
                                etNamaType.setText(komsArray[0]);
                        }
                        else {
                            etNamaType.setText(null);
                        }
                    });
                }
                break;
            case R.id.et_nama_type:
                if (Tools.isSuperAdmin() || Tools.isSecondAdmin() || Tools.isLA1() || Tools.isLA2()) {
                    String[] array;
                    if (etType.getText().toString().equalsIgnoreCase("komisariat")) {
                        array = getKomisariatByRole();
                    }
                    else {
                        array = new String[1];
                        array[0] = "PB HMI";
                    }
                    Tools.showDialogNamaType(this, array, res -> etNamaType.setText(res));
                }
                break;
            case R.id.et_unggah:
                selectFile();
                break;
        }
    }

    private void selectFile(){
        new MaterialFilePicker()
                .withActivity(this)
                .withRequestCode(Constant.REQUEST_DOC_CODE)
                .withHiddenFiles(true)
                .withFilter(Pattern.compile(".*\\.pdf$"))
                .withTitle("Select PDF file")
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data != null) {
            if (requestCode == Constant.REQUEST_DOC_CODE) {
                if (resultCode == RESULT_OK) {
                    String path = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);

                    if (path != null && !path.isEmpty()) {
                        pdfPath = path;
                        uploadFile();
                    }

                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getFileName(Uri uri) {
        String result = null;
        String uriScheme = uri.getScheme();
        if (uriScheme != null) {
            if (uriScheme.equals("content")) {
                try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    private void uploadFile() {
        if (Tools.isOnline(this)) {
            Tools.showProgressDialog(KonstitusiFormActivity.this, getString(R.string.mengunggah_dok));

            File file = new File(pdfPath);
            // Parsing any Media type file
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/pdf"), file);

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
                                Tools.showToast(KonstitusiFormActivity.this, getString(R.string.berhasil_unggah_dokumen));
                                urlFile = body.getData().get(0).getUrl();
                                pdfFileName = body.getData().get(0).getNama_file();
                                String fileName = pdfFileName + ".pdf";
                                etUnggah.setText(fileName);
                            } else {
                                Tools.showToast(KonstitusiFormActivity.this, getString(R.string.gagal_unggah_dokumen));
                            }
                        }
                        else {
                            Tools.showToast(KonstitusiFormActivity.this, getString(R.string.gagal_unggah_dokumen));
                        }
                    } else {
                        Tools.showToast(KonstitusiFormActivity.this, getString(R.string.gagal_unggah_dokumen));
                    }
                    Tools.dissmissProgressDialog();
                }

                @Override
                public void onFailure(@NonNull Call<UploadFileResponse> call, @NonNull Throwable t) {
                    Tools.showToast(KonstitusiFormActivity.this, getString(R.string.gagal_unggah_dokumen));
                    Tools.dissmissProgressDialog();
                }
            });
        } else {
            Tools.showToast(KonstitusiFormActivity.this, getString(R.string.tidak_ada_internet));
        }
    }
}
