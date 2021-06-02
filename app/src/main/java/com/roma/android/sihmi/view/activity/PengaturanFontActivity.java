package com.roma.android.sihmi.view.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.utils.Constant;

import java.util.Arrays;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class PengaturanFontActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_ukuran)
    TextView tvUkuran;
    @BindView(R.id.iv_ukuran)
    ImageView ivUkuran;
    @BindView(R.id.cv_gaya)
    CardView cvGaya;
    @BindView(R.id.tv_gaya)
    TextView tvGaya;
    @BindView(R.id.tv_gaya_ket)
    TextView tvGayaKet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengaturan_font);
        ButterKnife.bind(this);

        toolbar.setTitle(getString(R.string.font));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        tvGayaKet.setText(Constant.getFontName());
    }

    @OnClick(R.id.cv_gaya)
    public void clickGaya(){
        String[] nameFonts = {"Default", "Choco cooky", "Rosemary", "Gothic Bold"};
        int pos = Arrays.asList(nameFonts).indexOf(Constant.getFontName());

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setSingleChoiceItems(nameFonts, pos, (dialog1, which) -> {
                    dialog1.dismiss();
                    Constant.setFontName(nameFonts[which]);
                    startActivity(new Intent(PengaturanFontActivity.this, MainActivity.class).putExtra(MainActivity.CHANGE_THEME, true));
//                    tvGayaKet.setText(nameFonts[which]);
                })
                .create();
        dialog.show();
    }

    @OnClick(R.id.iv_ukuran)
    public void clickUkuran(){
        String[] nameFonts = {"Kecil", "Sedang", "Besar"};
        int pos = Arrays.asList(nameFonts).indexOf(Constant.getFontSizeName());

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setSingleChoiceItems(nameFonts, pos, (dialog1, which) -> {
                    dialog1.dismiss();
                    float size;
                    if (which == 0){
                        size = 0.75f;
                    } else if (which == 1){
                        size = 1;
                    } else {
                        size = 1.25f;
                    }
                    Constant.setFontSizeName(nameFonts[which]);
                    Constant.setFontSize(size);
                    startActivity(new Intent(PengaturanFontActivity.this, MainActivity.class).putExtra(MainActivity.CHANGE_THEME, true));
                    tvGayaKet.setText(nameFonts[which]);
                })
                .create();
        dialog.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
