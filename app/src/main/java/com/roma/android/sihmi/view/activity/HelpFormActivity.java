package com.roma.android.sihmi.view.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.AboutUs;
import com.roma.android.sihmi.model.database.interfaceDao.AboutUsDao;
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
public class HelpFormActivity extends BaseActivity {
    public static String IS_NEW = "IS_NEW";
    public static String ID_HELP = "ID_HELP";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_judul)
    EditText etJudul;
    @BindView(R.id.et_isi)
    EditText etIsi;
    @BindView(R.id.btn_simpan)
    Button btnSimpan;

    boolean isNew = false;
    AboutUs aboutUs;

    AboutUsDao aboutUsDao;
    MasterService service;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_form);
        ButterKnife.bind(this);

        service = ApiClient.getInstance().getApi();
        aboutUsDao = AppDb.getInstance(this).aboutUsDao();

        isNew = getIntent().getBooleanExtra(IS_NEW, false);
        String idHelp = getIntent().getStringExtra(ID_HELP);
        String title;
        Log.d("tes", "onCreate: " + idHelp);

        if (idHelp != null && !idHelp.isEmpty()) {
            aboutUs = aboutUsDao.getAboutUsById(idHelp);
            Log.d("tes", "onCreate: " + aboutUs.getNama());
            etJudul.setText(aboutUs.getNama());
            etIsi.setText(aboutUs.getDeskripsi());
        }

        if (isNew) {
            title = getString(R.string.tambah_bantuan);
        } else {
            title = getString(R.string.perbarui_bantuan);
        }
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        etIsi.setOnTouchListener((v, event) -> {
            if (etIsi.hasFocus()) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_SCROLL) {
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    return true;
                }
            }
            return false;
        });

    }

    @OnClick(R.id.btn_simpan)
    public void go() {
        if (!etJudul.getText().toString().isEmpty() && !etIsi.getText().toString().isEmpty()) {
            if (isNew) {
                addData(etJudul.getText().toString(), etIsi.getText().toString());
            } else {
                updateData(etJudul.getText().toString(), etIsi.getText().toString());
            }
        } else {
            Tools.showToast(this, getString(R.string.field_mandatory));
        }
    }

    private void addData(String judul, String isi) {
        service.addAboutUs(Constant.getToken(), judul, isi)
                .enqueue(new Callback<GeneralResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                        if (response.isSuccessful()) {
                            GeneralResponse body = response.body();
                            if (body != null) {
                                if (body.getStatus().equalsIgnoreCase("ok")) {
                                    Tools.showToast(HelpFormActivity.this, getString(R.string.berhasil_tambah));
                                    setResult(Activity.RESULT_OK);
                                    finish();
                                } else {
                                    Tools.showToast(HelpFormActivity.this, getString(R.string.gagal_tambah));
                                }
                            }
                            else {
                                Tools.showToast(HelpFormActivity.this, getString(R.string.gagal_tambah));
                            }
                        } else {
                            Tools.showToast(HelpFormActivity.this, getString(R.string.gagal_tambah));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<GeneralResponse> call, @NonNull Throwable t) {
                        Tools.showToast(HelpFormActivity.this, getString(R.string.gagal_tambah));
                    }
                });
    }

    private void updateData(String judul, String isi) {
        service.updateAboutUs(Constant.getToken(), aboutUs.get_id(), judul, isi)
                .enqueue(new Callback<GeneralResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                        if (response.isSuccessful()) {
                            GeneralResponse body = response.body();
                            if (body != null) {
                                if (body.getStatus().equalsIgnoreCase("ok")) {
                                    Tools.showToast(HelpFormActivity.this, getString(R.string.berhasil_update));
                                    setResult(Activity.RESULT_OK);
                                    finish();
                                } else {
                                    Tools.showToast(HelpFormActivity.this, getString(R.string.gagal_update));
                                }
                            }
                            else {
                                Tools.showToast(HelpFormActivity.this, getString(R.string.gagal_update));
                            }
                        } else {
                            Tools.showToast(HelpFormActivity.this, getString(R.string.gagal_update));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<GeneralResponse> call, @NonNull Throwable t) {
                        Tools.showToast(HelpFormActivity.this, getString(R.string.gagal_update));
                    }
                });

    }
}
