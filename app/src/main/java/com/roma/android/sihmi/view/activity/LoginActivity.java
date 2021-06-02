package com.roma.android.sihmi.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.network.ApiClient;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.utils.LoginProcess;
import com.roma.android.sihmi.utils.Tools;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class LoginActivity extends BaseActivity {
    @BindView(R.id.etUsername)
    EditText etUsername;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.tvRegister)
    TextView tvRegister;
    @BindView(R.id.tvForget)
    TextView tvForget;
    @BindView(R.id.btnLogin)
    Button btnLogin;

    MasterService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        service = ApiClient.getInstance().getApi();

        tvForget.setTypeface(tvForget.getTypeface(), Typeface.BOLD);
        tvRegister.setTypeface(tvRegister.getTypeface(), Typeface.BOLD);
    }

    @OnClick(R.id.tvRegister)
    public void goRegister(){
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    @OnClick(R.id.tvForget)
    public void goReset(){
        startActivity(new Intent(LoginActivity.this, ForgetPassActivity.class));
    }

    @OnClick(R.id.btnLogin)
    public void goLogin(){
        if (!etUsername.getText().toString().isEmpty() && !etPassword.getText().toString().isEmpty()) {
            if (Tools.isOnline(this)) {
                LoginProcess loginProcess = new LoginProcess(this, service);
                loginProcess.login(etUsername.getText().toString(), etPassword.getText().toString());
            } else {
                Tools.showToast(LoginActivity.this, getString(R.string.tidak_ada_internet));
            }
        } else {
            Tools.showToast(LoginActivity.this, getString(R.string.field_mandatory));
        }
    }

}
