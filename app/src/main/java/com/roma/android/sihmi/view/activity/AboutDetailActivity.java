package com.roma.android.sihmi.view.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Sejarah;
import com.roma.android.sihmi.model.database.interfaceDao.SejarahDao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("NonConstantResourceId")
public class AboutDetailActivity extends BaseActivity {
    public static String ID_ABOUT = "id_about";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_title)
    TextView tvTtitle;
    @BindView(R.id.tv_desc)
    TextView tvDesc;

    SejarahDao sejarahDao;
    String idSejarah;
    Sejarah sejarah;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_detail);

        ButterKnife.bind(this);
        sejarahDao = AppDb.getInstance(this).sejarahDao();

        idSejarah = getIntent().getStringExtra(ID_ABOUT);
        sejarah = sejarahDao.getSejarahById(idSejarah);

        toolbar.setTitle(sejarah.getJudul().toUpperCase());
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        tvTtitle.setVisibility(View.GONE);
        tvTtitle.setText(sejarah.getJudul());
        tvDesc.setText(sejarah.getDeskripsi());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
