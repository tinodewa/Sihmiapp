package com.roma.android.sihmi.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Contact;
import com.roma.android.sihmi.model.database.interfaceDao.ContactDao;
import com.roma.android.sihmi.utils.Query;
import com.roma.android.sihmi.view.adapter.LaporanDataKaderAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.sqlite.db.SimpleSQLiteQuery;
import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("NonConstantResourceId")
public class DataKaderActivity extends BaseActivity {
    public static final String TAHUN_KADER = "tahun_kader";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_ket)
    TextView tvKet;
    @BindView(R.id.rv_non_kader)
    RecyclerView rvNonKader;
    @BindView(R.id.rv_kader)
    RecyclerView rvKader;
    @BindView(R.id.tv_no_non_kader)
    TextView tvNoNonKader;
    @BindView(R.id.tv_no_kader)
    TextView tvNoKader;

    int tahun;
    List<Contact> listNonLk = new ArrayList<>();
    List<Contact> listLk = new ArrayList<>();
    LaporanDataKaderAdapter adapterNon;
    LaporanDataKaderAdapter adapterLK;

    ContactDao contactDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_kader);
        ButterKnife.bind(this);
        initToolbar();

        tahun = getIntent().getIntExtra(TAHUN_KADER, 0);
        String titleText = getString(R.string.pengguna_sihmi)+" - "+tahun;
        tvKet.setText(titleText);
        contactDao = AppDb.getInstance(this).contactDao();

        listNonLk = contactDao.rawQueryContact(new SimpleSQLiteQuery(Query.ReportSuperAdminNonLK(tahun)));
        listLk = contactDao.rawQueryContact(new SimpleSQLiteQuery(Query.ReportSuperAdmin(tahun)));

        if (listNonLk.size() > 0){
            tvNoNonKader.setVisibility(View.GONE);
        } else {
            tvNoNonKader.setVisibility(View.VISIBLE);
        }

        if (listLk.size() > 0){
            tvNoKader.setVisibility(View.GONE);
        } else {
            tvNoKader.setVisibility(View.VISIBLE);
        }

        adapterNon = new LaporanDataKaderAdapter(this, listNonLk,
                contact -> startActivity(new Intent(DataKaderActivity.this, ProfileChatActivity.class).putExtra("iduser", contact.get_id())));

        adapterLK = new LaporanDataKaderAdapter(this, listLk,
                contact -> startActivity(new Intent(DataKaderActivity.this, ProfileChatActivity.class).putExtra("iduser", contact.get_id())));

        rvNonKader.setLayoutManager(new LinearLayoutManager(this));
        rvNonKader.setAdapter(adapterNon);

        rvKader.setLayoutManager(new LinearLayoutManager(this));
        rvKader.setAdapter(adapterLK);
    }


    private void initToolbar() {
        toolbar.setTitle(getString(R.string.data_kader));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }
}
