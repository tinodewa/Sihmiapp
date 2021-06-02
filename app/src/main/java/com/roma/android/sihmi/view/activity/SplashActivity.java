package com.roma.android.sihmi.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.interfaceDao.LoadDataStateDao;
import com.roma.android.sihmi.utils.Constant;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("NonConstantResourceId")
public class SplashActivity extends BaseActivity {
    @BindView(R.id.tv_splash)
    TextView tvSplash;
    private LoadDataStateDao loadDataStateDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        loadDataStateDao = AppDb.getInstance(this).loadDataStateDao();

        tvSplash.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/buka_puasa_bersama_5.ttf"));

        checkLanguage(Constant.getLanguage());

        new Handler().postDelayed(() -> {
            Log.d("Test", "runTOken: "+Constant.getToken());
            if (Constant.isLoggedIn(this)){
                if (!loadDataStateDao.getIsLoaded()) {
                    startActivity(new Intent(SplashActivity.this, LoadDataActivity.class));
                }
                else {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }
            } else {
                if (Constant.getSizeAccount() > 0){
                    startActivity(new Intent(SplashActivity.this, SwitchAccountActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                }
            }
            finish();
        }, 3000);
    }


    private void checkLanguage(String code){
        Constant.setLanguage(code);
        Locale locale = new Locale(code);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config,
                getResources().getDisplayMetrics());
    }
}
