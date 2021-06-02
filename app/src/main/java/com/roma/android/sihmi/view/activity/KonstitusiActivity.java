package com.roma.android.sihmi.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.network.MasterService;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class KonstitusiActivity extends BaseActivity {
    public static String TYPE_KONSTITUSI = "type_konstitusi";
    public static String NAMA_KONSTITUSI = "nama_konstitusi";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.llPilihan)
    LinearLayout llPilihan;

    @BindView(R.id.cv_nasional)
    CardView cvNasional;
    @BindView(R.id.cv_cabang)
    CardView cvCabang;
    @BindView(R.id.cv_komisariat)
    CardView cvKomisariat;

    @BindView(R.id.fab_add)
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konstitusi);
        ButterKnife.bind(this);

        String namaKonstitusi = getIntent().getStringExtra(NAMA_KONSTITUSI);
        if (namaKonstitusi == null) namaKonstitusi = "";
        toolbar.setTitle(namaKonstitusi.toUpperCase());
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @OnClick({R.id.cv_nasional, R.id.cv_cabang, R.id.cv_komisariat})
    public void menuClick(CardView button) {
        switch (button.getId()) {
            case R.id.cv_nasional:
                startActivity(new Intent(this, KonstitusiActivity.class).putExtra(TYPE_KONSTITUSI, 1).putExtra(KonstitusiActivity.NAMA_KONSTITUSI, "Konstitusi Nasional"));
                break;
            case R.id.cv_cabang:
                startActivity(new Intent(this, KonstitusiActivity.class).putExtra(TYPE_KONSTITUSI, 2).putExtra(KonstitusiActivity.NAMA_KONSTITUSI, "Konstitusi Cabang"));
                break;
            case R.id.cv_komisariat:
                startActivity(new Intent(this, KonstitusiActivity.class).putExtra(TYPE_KONSTITUSI, 3).putExtra(KonstitusiActivity.NAMA_KONSTITUSI, "Konstitusi Komisariat"));
                break;
        }
    }
}
