package com.roma.android.sihmi.view.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.ViewModel.DataKaderLA2ActivityViewModel;
import com.roma.android.sihmi.model.database.entity.Master;
import com.roma.android.sihmi.model.database.entity.UserCount;
import com.roma.android.sihmi.view.adapter.UserCountAdapter;

import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("NonConstantResourceId")
public class DataKaderLA2Activity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_title_year)
    TextView tvTitleYear;
    @BindView(R.id.ll_data_kader_wrapper)
    LinearLayout llDataKaderWrapper;

    static String YEAR_DATA = "__year_data__";
    private String year;
    private DataKaderLA2ActivityViewModel dataKaderLA2ActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_kader_la2);
        ButterKnife.bind(this);

        year = getIntent().getStringExtra(YEAR_DATA);

        if (year == null) {
            finish();
        }

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle(R.string.data_kader);
        }

        dataKaderLA2ActivityViewModel = new ViewModelProvider(this).get(DataKaderLA2ActivityViewModel.class);
        dataKaderLA2ActivityViewModel.init(this, year);

        initView();
    }

    private void initView() {
        String titleText = tvTitleYear.getText() + year;
        tvTitleYear.setText(titleText);

        List<Master> badkoList = dataKaderLA2ActivityViewModel.getAllBadko();

        for (Master badko : badkoList) {
            @SuppressLint("InflateParams") View v = getLayoutInflater().inflate(R.layout.item_data_kader_badko, null);
            TextView tvBadkoName = v.findViewById(R.id.tv_badko_name);
            RecyclerView rvCabangList = v.findViewById(R.id.rv_cabang_list);
            TextView tvNoData = v.findViewById(R.id.tv_no_data);

            tvBadkoName.setText(badko.getValue());
            List<UserCount> cabangCount = dataKaderLA2ActivityViewModel.getCabangUserFromBadko(badko.get_id());
            if (cabangCount.isEmpty()) {
                tvNoData.setVisibility(View.VISIBLE);
            }
            UserCountAdapter userCountAdapter = new UserCountAdapter(cabangCount, false);
            userCountAdapter.setOnItemClick(cabang -> {
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_komisariat_user_count, null);
                RecyclerView rvKomisariatItem = dialogView.findViewById(R.id.rv_komisariat_item);
                List<UserCount> komisariatCount = dataKaderLA2ActivityViewModel.getKomisariatUserFromCabang(cabang);
                UserCountAdapter userKomisariatCountAdapter = new UserCountAdapter(komisariatCount, true);

                rvKomisariatItem.setLayoutManager(new LinearLayoutManager(this));
                rvKomisariatItem.setAdapter(userKomisariatCountAdapter);

                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setView(dialogView)
                        .create();

                dialog.show();
            });

            rvCabangList.setLayoutManager(new LinearLayoutManager(this));
            rvCabangList.setAdapter(userCountAdapter);

            llDataKaderWrapper.addView(v);
        }
    }
}
