package com.roma.android.sihmi.view.fragment;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Tools;
import com.roma.android.sihmi.view.activity.MainActivity;
import com.roma.android.sihmi.view.activity.PengaturanFontActivity;
import com.roma.android.sihmi.view.activity.SplashActivity;

import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class PengaturanFragment extends Fragment {
    @BindView(R.id.tv_bahasa)
    TextView tvBahasa;
    @BindView(R.id.iv_bahasa)
    ImageView ivBahasa;
    @BindView(R.id.switch_mode)
    Switch aSwitch;
    @BindView(R.id.tv_font)
    TextView tvFont;
    @BindView(R.id.cv_reset)
    CardView cvReset;

    String[] grpName;
    int pos = 0;

    public PengaturanFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_pengaturan, container, false);
        ButterKnife.bind(this, v);
        setHasOptionsMenu(true);
        ((MainActivity) getActivity()).setToolBar(getString(R.string.judul_pengaturan));

        String lang;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            lang= Resources.getSystem().getConfiguration().getLocales().get(0).getLanguage();
        } else {
            lang = getResources().getConfiguration().locale.getLanguage();
        }
        Log.d("hallogesss", "onCreate: pengaturan "+ Locale.getDefault().getLanguage()+" --- "+lang);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        grpName = getResources().getStringArray(R.array.bahasa_array);
        if (tvBahasa.getText().toString().toLowerCase().contains("bahasa")){
            pos = 0;
        } else {
            pos = 1;
        }
        tvBahasa.setText(getString(R.string.bahasa)+"-"+grpName[pos]);

        aSwitch.setChecked(Constant.loadNightModeState());
        aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                Constant.setNightModeState(true);
                changeTheme();
            } else {
                Constant.setNightModeState(false);
                changeTheme();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setVisible(false);
        MenuItem gridItem = menu.findItem(R.id.action_list);
        gridItem.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @OnClick(R.id.iv_bahasa)
    public void clickBahasa(){
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setSingleChoiceItems(grpName, pos, (dialog1, which) -> {
                    switch (which){
                        case 0:
                            changeLanguage(getActivity(), "id");
                            tvBahasa.setText(getString(R.string.bahasa)+"-"+grpName[pos]);
                            dialog1.dismiss();
                            break;
                        case 1:
                            changeLanguage(getActivity(), "en");
                            tvBahasa.setText(getString(R.string.bahasa)+"-"+grpName[pos]);
                            dialog1.dismiss();
                            break;
                    }
                })
                .create();
        dialog.show();
    }

    @OnClick(R.id.tv_font)
    public void changeFont(){
        startActivity(new Intent(getActivity(), PengaturanFontActivity.class));
//        Constant.setFontName("buka_puasa_bersama_5");
//        changeTheme();
    }

    @OnClick(R.id.cv_reset)
    public void reset(){
        Tools.reseteDialog(getActivity(), ket -> {
            Locale locale = new Locale("id");
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getActivity().getResources().updateConfiguration(config,
                    getActivity().getResources().getDisplayMetrics());

            Constant.setNightModeState(false);
            Constant.setFontName("Default");
            Constant.setFontSize(1);

            changeTheme();
        });

    }

    private void changeLanguage(Context context, String code){
        Constant.setLanguage(code);
        Locale locale = new Locale(code);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config,
                context.getResources().getDisplayMetrics());

        restartFragment();
    }

    private void restartFragment(){
        Objects.requireNonNull(getActivity()).startActivity(new Intent(getActivity(), SplashActivity.class));
        getActivity().finish();
//        ((MainActivity) getActivity()).restartActvity();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            getFragmentManager().beginTransaction().detach(this).commitNow();
            getFragmentManager().beginTransaction().attach(this).commitNow();
        } else {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }

    private void changeTheme(){
        Objects.requireNonNull(getActivity()).startActivity(new Intent(getActivity(), SplashActivity.class).putExtra(MainActivity.CHANGE_THEME, true));
        getActivity().finish();

    }

}
