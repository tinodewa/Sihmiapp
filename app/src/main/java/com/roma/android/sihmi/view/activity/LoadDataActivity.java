package com.roma.android.sihmi.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.ViewModel.LoadDataActivityViewModel;
import com.roma.android.sihmi.utils.Constant;

import androidx.lifecycle.ViewModelProvider;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class LoadDataActivity extends BaseActivity {
    private LoadDataActivityViewModel loadDataActivityViewModel;
    @BindView(R.id.tv_error_load_data)
    TextView tvErrorLoadData;
    @BindView(R.id.pb_load_data)
    ProgressBar pbLoadData;
    @BindView(R.id.b_reload)
    Button bReload;
    @BindView(R.id.b_logout)
    Button bLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_data);
        ButterKnife.bind(this);

        loadDataActivityViewModel = new ViewModelProvider(this).get(LoadDataActivityViewModel.class);
        loadDataActivityViewModel.init(this);

        loadDataActivityViewModel.getData();

        loadDataActivityViewModel.getIsLoaded().observe(this, isLoaded -> {
            if (isLoaded != null && isLoaded) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        });

        loadDataActivityViewModel.getIsError().observe(this, isError -> {
            if (isError) {
                tvErrorLoadData.setText(loadDataActivityViewModel.getErrorMessage());

                pbLoadData.setVisibility(View.GONE);
                tvErrorLoadData.setVisibility(View.VISIBLE);
                bReload.setVisibility(View.VISIBLE);
                bLogout.setVisibility(View.VISIBLE);
            }
            else {
                pbLoadData.setVisibility(View.VISIBLE);
                tvErrorLoadData.setVisibility(View.GONE);
                bReload.setVisibility(View.GONE);
                bLogout.setVisibility(View.GONE);
            }
        });

        loadDataActivityViewModel.getIsLoggedOut().observe(this, isLoggedOut -> {
            if (isLoggedOut) {
                if (Constant.getSizeAccount() > 0) {
                    startActivity(new Intent(this, SwitchAccountActivity.class));
                } else {
                    startActivity(new Intent(this, LoginActivity.class));
                }
            }
        });
    }

    @OnClick({R.id.b_reload, R.id.b_logout})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.b_reload:
                loadDataActivityViewModel.getData();
                break;
            case R.id.b_logout:
                loadDataActivityViewModel.logout();
                break;
        }
    }
}
