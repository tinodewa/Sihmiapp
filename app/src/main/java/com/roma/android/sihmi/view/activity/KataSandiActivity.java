package com.roma.android.sihmi.view.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.network.ApiClient;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.response.ProfileResponse;
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
public class KataSandiActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_password)
    EditText etPass;
    @BindView(R.id.et_password_baru)
    EditText etPassNew;
    @BindView(R.id.et_confirm_password)
    EditText etPassConfirm;

    MasterService service;
    ProgressDialog dialog;

    AppDb appDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kata_sandi);
        ButterKnife.bind(this);
        initToolbar();
        initModule();

    }

    private void initToolbar(){
        toolbar.setTitle(getString(R.string.perbarui_kata_sandi));
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
        dialog = new ProgressDialog(this);
    }

    @OnClick(R.id.btn_simpan)
    public void click(){
        String password = etPass.getText().toString();
        String password_baru = etPassNew.getText().toString();
        String confirm_passsword_baru = etPassConfirm.getText().toString();

        if (password_baru.length() >= 6) {
            if (password_baru.equals(confirm_passsword_baru)) {
                changePassword(password, password_baru);
            } else {
                Tools.showToast(KataSandiActivity.this, getString(R.string.pass_tidak_sesuai));
            }
        } else {
            Tools.showToast(KataSandiActivity.this, getString(R.string.pass_min_karakter));
        }
    }

    private void changePassword(String old_pass, String new_pass){
        if (Tools.isOnline(this)) {
            dialog.setMessage(getString(R.string.ganti_password));
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();

            Call<ProfileResponse> call = service.changePassword(Constant.getToken(), new_pass, old_pass);
            call.enqueue(new Callback<ProfileResponse>() {
                @Override
                public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                    dialog.dismiss();
                    if (response.isSuccessful()) {
                        ProfileResponse body = response.body();
                        if (body != null) {
                            if (body.getStatus().equalsIgnoreCase("ok")) {
                                Tools.showToast(KataSandiActivity.this, getString(R.string.berhasil_update));
                                Constant.logout();
                                appDb.clearAllTables();
                                startActivity(new Intent(KataSandiActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                finish();
                            } else {
                                Tools.showToast(KataSandiActivity.this, getString(R.string.gagal_update));
                            }
                        }
                        else {
                            Tools.showToast(KataSandiActivity.this, getString(R.string.gagal_update));
                        }
                    } else {
                        Tools.showToast(KataSandiActivity.this, getString(R.string.gagal_update));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {
                    dialog.dismiss();
                    Tools.showToast(KataSandiActivity.this, getString(R.string.gagal_update));
                    finish();
                }
            });
        } else {
            Tools.showToast(KataSandiActivity.this, getString(R.string.tidak_ada_internet));
        }

    }
}
