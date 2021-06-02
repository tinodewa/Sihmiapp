package com.roma.android.sihmi.view.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.network.ApiClient;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.response.GeneralResponse;
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
public class ForgetPassActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.etUsername)
    EditText etUsername;
    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.btnReset)
    Button btnReset;

    MasterService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass);
        ButterKnife.bind(this);
        service = ApiClient.getInstance().getApi();
        initToolbar();
    }

    private void initToolbar(){
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.lupa_kata_sandi_title));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    @OnClick(R.id.btnReset)
    public void goReset(){
        if (!etEmail.getText().toString().trim().isEmpty() && !etUsername.getText().toString().trim().isEmpty()){
            if (Tools.isValidEmail(etEmail.getText().toString())){
                if (Tools.isOnline(this)) {
                    resetPass(etUsername.getText().toString(), etEmail.getText().toString());
                } else {
                    Tools.showToast(ForgetPassActivity.this, getString(R.string.tidak_ada_internet));
                }
            } else {
                Tools.showToast(ForgetPassActivity.this, getString(R.string.email_not_valid));
            }
        } else {
            Tools.showToast(ForgetPassActivity.this, getString(R.string.field_mandatory));
        }
    }

    private void  resetPass(String username, String email){
        Tools.showProgressDialog(this, "Tunggu Sebentar...");
        Call<GeneralResponse> call = service.forgotpassword(username, email);
        call.enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                Tools.dissmissProgressDialog();
                if (response.isSuccessful()){
                    GeneralResponse body = response.body();
                    if (body != null) {
                        if (body.getStatus().equalsIgnoreCase("success")){
                            Tools.showToast(ForgetPassActivity.this, getString(R.string.pengajuan_berhasil));
                            finish();
                        } else {
                            Tools.showToast(ForgetPassActivity.this, getString(R.string.pengajuan_gagal));
                        }
                    }
                    else {
                        Tools.showToast(ForgetPassActivity.this, getString(R.string.pengajuan_gagal));
                    }
                } else {
                    Tools.showToast(ForgetPassActivity.this, getString(R.string.pengajuan_gagal));
                }
            }

            @Override
            public void onFailure(@NonNull Call<GeneralResponse> call, @NonNull Throwable t) {
                Tools.dissmissProgressDialog();
                Tools.showToast(ForgetPassActivity.this, getString(R.string.pengajuan_gagal));
            }
        });
    }

}
