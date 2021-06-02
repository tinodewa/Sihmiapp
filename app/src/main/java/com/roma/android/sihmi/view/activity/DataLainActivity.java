package com.roma.android.sihmi.view.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Pendidikan;
import com.roma.android.sihmi.model.database.entity.Training;
import com.roma.android.sihmi.model.database.entity.User;
import com.roma.android.sihmi.model.database.interfaceDao.MasterDao;
import com.roma.android.sihmi.model.database.interfaceDao.TrainingDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.model.network.ApiClient;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.response.GeneralResponse;
import com.roma.android.sihmi.model.response.PendidikanResponse;
import com.roma.android.sihmi.model.response.TrainingResponse;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Tools;
import com.roma.android.sihmi.view.adapter.PendidikanAdapter;
import com.roma.android.sihmi.view.adapter.TrainingAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("NonConstantResourceId")
public class DataLainActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_tempat_lahir)
    EditText etTempatLahir;
    @BindView(R.id.et_tanggal_lahir)
    EditText etTglLahir;
    @BindView(R.id.et_email)
    EditText etEmail;
    @BindView(R.id.et_status)
    EditText etStatus;
    @BindView(R.id.et_akun)
    EditText etAkun;
    @BindView(R.id.rv_pelatihan)
    RecyclerView rvPelatihan;
    @BindView(R.id.rv_pendidikan)
    RecyclerView rvPendidikan;

    MasterService service;
    AppDb appDb;
    UserDao userDao;
    TrainingDao trainingDao;
    MasterDao masterDao;
    User user;

    TrainingAdapter trainingAdapter;
    PendidikanAdapter pendidikanAdapter;

    List<Training> trainings = new ArrayList<>();
    List<Pendidikan> pendidikans = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_lain);
        ButterKnife.bind(this);
        initToolbar();
        initModule();
        initView();
        initAdapter();
    }

    private void initToolbar(){
        toolbar.setTitle(getString(R.string.data_lain_profil));
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
        trainingDao = appDb.trainingDao();
        masterDao = appDb.masterDao();
        userDao = appDb.userDao();
        user = userDao.getUser();

        getDataPendidikan();
        getDataTraining();

        trainingDao.getAllTrainingByUser(user.get_id()).observe(this, list -> trainingAdapter.updateData(list));
    }

    private void initView(){
        setText(etTglLahir, user.getTanggal_lahir());
        setText(etTempatLahir, user.getTempat_lahir());
        setText(etEmail, user.getEmail());
        setText(etStatus, user.getStatus_perkawinan());
        setText(etAkun, user.getAkun_sosmed());
    }

    private void initAdapter(){
        trainings = trainingDao.getListAllTrainingByUser(user.get_id());
        trainingAdapter = new TrainingAdapter(this, trainings, id -> {
            if (id.equalsIgnoreCase(Constant.FAB_ADD)){
                dialogAddDataTraining();
            }
        });
        rvPelatihan.setLayoutManager(new LinearLayoutManager(this));
        rvPelatihan.setAdapter(trainingAdapter);

        pendidikanAdapter = new PendidikanAdapter(this, pendidikans, id -> {
            if (id.equalsIgnoreCase(Constant.FAB_ADD)){
                dialogAddDataPendidikan();
            }
        });
        rvPendidikan.setLayoutManager(new LinearLayoutManager(this));
        rvPendidikan.setAdapter(pendidikanAdapter);

    }

    @OnClick(R.id.btn_simpan)
    public void click(){
        // halaman ini menyimpan tempat lahir, tanggal lahir, email, status perkawinan, Akun Sosmed
        if (!etTempatLahir.getText().toString().trim().isEmpty() && !etTglLahir.getText().toString().trim().isEmpty() &&  !etStatus.getText().toString().trim().isEmpty() && !etAkun.getText().toString().trim().isEmpty()){
            saveProfile(etTempatLahir.getText().toString(), etTglLahir.getText().toString(), etStatus.getText().toString(), etAkun.getText().toString());
        } else {
            Tools.showToast(this, getString(R.string.field_mandatory));
        }

    }

    @OnClick({R.id.et_tanggal_lahir, R.id.et_status})
    public void showDate(EditText editText){
        switch (editText.getId()){
            case R.id.et_tanggal_lahir:
                Tools.showDateDialog(this, etTglLahir);
                break;
            case R.id.et_status:
                Tools.showDialogStatus(this, ket -> etStatus.setText(ket));
                break;
        }
    }

    private void saveProfile(String tempat_lhr, String tgl_lhr, String stts, String akun_sosmed){
        if (Tools.isOnline(this)) {
            Tools.showProgressDialog(this, getString(R.string.menyimpan_profile));

            Call<GeneralResponse> call = service.updateProfile(Constant.getToken(), user.getBadko(), user.getCabang(), user.getKorkom(), user.getKomisariat(), user.getId_roles(), user.getImage(), user.getNama_depan(), user.getNama_belakang(),
                    user.getNama_panggilan(), user.getJenis_kelamin(), user.getNomor_hp(), user.getAlamat(), user.getUsername(),
                    tempat_lhr, tgl_lhr, stts, "", user.getEmail(), akun_sosmed, user.getDomisili_cabang(), user.getPekerjaan(), user.getJabatan(), user.getAlamat_kerja(), user.getKontribusi());
            call.enqueue(new Callback<GeneralResponse>() {
                @Override
                public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                    if (response.isSuccessful()) {
                        GeneralResponse body = response.body();
                        if (body != null) {
                            if (body.getStatus().equalsIgnoreCase("ok")) {
                                Tools.showToast(DataLainActivity.this, getString(R.string.berhasil_update));
                                setResult(Activity.RESULT_OK);
                                finish();
                            } else {
                                Tools.showDialogAlert(DataLainActivity.this, body.getMessage());
                            }
                        }
                        else {
                            Tools.showToast(DataLainActivity.this, getString(R.string.gagal_update));
                        }
                    } else {
                        Tools.showToast(DataLainActivity.this, getString(R.string.gagal_update));
                    }
                    Tools.dissmissProgressDialog();
                }

                @Override
                public void onFailure(@NonNull Call<GeneralResponse> call, @NonNull Throwable t) {
                    Tools.showToast(DataLainActivity.this, getString(R.string.gagal_update));
                    Tools.dissmissProgressDialog();
                }
            });
        } else {
            Tools.showToast(DataLainActivity.this, getString(R.string.tidak_ada_internet));
        }
    }

    private void setText(EditText editText, String text){
        if (text != null && !text.trim().isEmpty()){
            editText.setText(text);
        }
    }

    private void getDataPendidikan(){
        Call<PendidikanResponse> call = service.getPendidikan(Constant.getToken(), user.get_id());
        call.enqueue(new Callback<PendidikanResponse>() {
            @Override
            public void onResponse(@NonNull Call<PendidikanResponse> call, @NonNull Response<PendidikanResponse> response) {
                if (response.isSuccessful()) {
                    PendidikanResponse body = response.body();
                    if (body != null) {
                        if (body.getStatus().equalsIgnoreCase("success")) {
                            pendidikanAdapter.updateData(body.getData());
                        }
                        else {
                            Tools.showToast(DataLainActivity.this, getString(R.string.gagal_load_data));
                        }
                    }
                    else {
                        Tools.showToast(DataLainActivity.this, getString(R.string.gagal_load_data));
                    }
                } else {
                    Tools.showToast(DataLainActivity.this, getString(R.string.gagal_load_data));
                }
            }

            @Override
            public void onFailure(@NonNull Call<PendidikanResponse> call, @NonNull Throwable t) {

            }
        });
    }

    private void dialogAddDataPendidikan(){
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_pendidikan, null);
        final EditText etTahun = dialogView.findViewById(R.id.et_thn_masuk);
        final Spinner spJenjang = dialogView.findViewById(R.id.sp_jenjang);
        final EditText etNama = dialogView.findViewById(R.id.et_nama);
        final EditText etFakultas = dialogView.findViewById(R.id.et_fakultas);

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton(getString(R.string.simpan), (dialog1, which) -> {
                    String tahun = etTahun.getText().toString();
                    int tahunInt = Integer.parseInt(tahun);
                    String curYear = Tools.getYearFromMillis(System.currentTimeMillis());
                    int curYearInt = Integer.parseInt(curYear);

                    if (tahun.length() > 4 || tahunInt < 1947) {
                        Tools.showToast(DataLainActivity.this, getString(R.string.invalid_year_format));
                    }
                    else if (tahunInt > curYearInt) {
                        Tools.showToast(DataLainActivity.this, getString(R.string.tahun_overload));
                    }
                    else if (etNama.getText().toString().trim().isEmpty() || etFakultas.getText().toString().trim().isEmpty()) {
                        Tools.showToast(getApplicationContext(), getString(R.string.field_mandatory));
                    } else {
                        addDataPendidikan(etTahun.getText().toString(), String.valueOf(spJenjang.getSelectedItem()), etNama.getText().toString(), etFakultas.getText().toString());
                    }
                })
                .setNegativeButton(getString(R.string.batal), (dialog12, which) -> dialog12.dismiss())
                .setCancelable(true)
                .create();

        dialog.setOnShowListener(arg -> {
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorTextDark));
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
        });
        dialog.show();
    }

    private void addDataPendidikan(String tahun, String strata, String kampus, String jurusan){
        if (!tahun.isEmpty() && !strata.isEmpty() && !kampus.isEmpty() && !jurusan.isEmpty()) {
            Call<GeneralResponse> call = service.addPendidikan(Constant.getToken(), tahun, strata, kampus, jurusan);
            call.enqueue(new Callback<GeneralResponse>() {
                @Override
                public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                    if (response.isSuccessful()) {
                        GeneralResponse body = response.body();
                        if (body != null) {
                            if (body.getStatus().equalsIgnoreCase("success")) {
                                Tools.showToast(DataLainActivity.this, getString(R.string.berhasil_tambah));
                                getDataPendidikan();
                            } else {
                                Tools.showToast(DataLainActivity.this, body.getMessage());
                            }
                        }
                        else {
                            Tools.showToast(DataLainActivity.this, getString(R.string.gagal_tambah));
                        }
                    } else {
                        Tools.showToast(DataLainActivity.this, getString(R.string.gagal_tambah));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<GeneralResponse> call, @NonNull Throwable t) {
                    Tools.showToast(DataLainActivity.this, getString(R.string.gagal_tambah));
                }
            });
        } else {
            Tools.showToast(DataLainActivity.this, getString(R.string.gagal_tambah));
        }
    }

    private void getDataTraining(){
        Call<TrainingResponse> call = service.getTraining(Constant.getToken(), user.get_id());
        call.enqueue(new Callback<TrainingResponse>() {
            @Override
            public void onResponse(@NonNull Call<TrainingResponse> call, @NonNull Response<TrainingResponse> response) {
                if (response.isSuccessful()) {
                    TrainingResponse body = response.body();
                    if (body != null) {
                        if (body.getStatus().equalsIgnoreCase("success")) {
                            List<Training> trainingList = body.getData();
                            for (int i = 0; i < trainingList.size(); i++) {
                                Training training = trainingList.get(i);
                                training.setId(training.getId());
                                training.setId_user(training.getId_user());
                                training.setCabang(user.getCabang());
                                training.setKomisariat(user.getKomisariat());
                                training.setDomisili_cabang(user.getDomisili_cabang());
                                training.setJenis_kelamin(user.getJenis_kelamin());
                                trainingDao.insertTraining(training);
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

    private void dialogAddDataTraining(){
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_training, null);
        final Spinner spTipe = dialogView.findViewById(R.id.sp_training);
        final EditText etTahun = dialogView.findViewById(R.id.etTahun);
        final EditText etLokasi = dialogView.findViewById(R.id.etLokasi);
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton(getString(R.string.simpan), (dialog1, which) -> {
                    String tahun = etTahun.getText().toString();
                    int tahunInt = Integer.parseInt(tahun);
                    String curYear = Tools.getYearFromMillis(System.currentTimeMillis());
                    int curYearInt = Integer.parseInt(curYear);
                    Training trainingSame = trainingDao.getTrainingUserByType(spTipe.getSelectedItem().toString(), user.get_id());

                    if (tahun.trim().length() > 4 || tahunInt < 1947) {
                        Tools.showToast(DataLainActivity.this, getString(R.string.invalid_year_format));
                    }
                    else if (tahunInt > curYearInt) {
                        Tools.showToast(DataLainActivity.this, getString(R.string.tahun_overload));
                    }
                    else if (trainingSame != null) {
                        Tools.showToast(DataLainActivity.this, getString(R.string.same_training_type));
                    }
                    else if (spTipe.getSelectedItem().toString().trim().isEmpty()
                            || etTahun.getText().toString().trim().isEmpty()
                            || etLokasi.getText().toString().trim().isEmpty()) {
                        Tools.showToast(DataLainActivity.this, getString(R.string.field_mandatory));
                    }
                    else {
                        addDataTraining(String.valueOf(spTipe.getSelectedItem()), etTahun.getText().toString(), spTipe.getSelectedItem().toString(), etLokasi.getText().toString());
                    }
                    dialog1.dismiss();
                })
                .setNegativeButton(getString(R.string.batal), (dialog12, which) -> dialog12.dismiss())
                .setCancelable(true)
                .create();

        dialog.setOnShowListener(arg -> {
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorTextDark));
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
        });
        dialog.show();

        List<String> strings = masterDao.getMasterTraining();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, strings);
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
        spTipe.setAdapter(adapter);

        spTipe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TypedValue typedValue = new TypedValue();
                Resources.Theme theme = DataLainActivity.this.getTheme();
                theme.resolveAttribute(R.attr.textcolor, typedValue, true);
                @ColorInt int textColor = typedValue.data;
                ((TextView) parent.getChildAt(0)).setTextColor(textColor);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void addDataTraining(String tipe, String tahun, String nama, String lokasi){
        if (!tipe.isEmpty() && !tahun.isEmpty() && !nama.isEmpty() && !lokasi.isEmpty()) {
            Call<GeneralResponse> call = service.addTraining(Constant.getToken(), tipe, tahun, nama, lokasi);
            call.enqueue(new Callback<GeneralResponse>() {
                @Override
                public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                    if (response.isSuccessful()) {
                        GeneralResponse body = response.body();
                        if (body != null) {
                            if (body.getStatus().equalsIgnoreCase("success")) {
                                Tools.showToast(DataLainActivity.this, getString(R.string.berhasil_tambah));
                                getDataTraining();
                            } else {
                                Tools.showToast(DataLainActivity.this, body.getMessage());
                            }
                        }
                        else {
                            Tools.showToast(DataLainActivity.this, getString(R.string.gagal_tambah));
                        }
                    } else {
                        Tools.showToast(DataLainActivity.this, getString(R.string.gagal_tambah));

                    }
                }

                @Override
                public void onFailure(@NonNull Call<GeneralResponse> call, @NonNull Throwable t) {
                    Tools.showToast(DataLainActivity.this, getString(R.string.gagal_tambah));

                }
            });
        } else {
            Tools.showToast(DataLainActivity.this, getString(R.string.field_mandatory));
        }
    }
}
