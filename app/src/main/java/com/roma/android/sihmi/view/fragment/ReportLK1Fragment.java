package com.roma.android.sihmi.view.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.ViewModel.ReportLK1ViewModel;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.User;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.view.activity.MainActivity;
import com.roma.android.sihmi.view.adapter.LaporanGrafikAdapter;
import com.roma.android.sihmi.view.adapter.MasterDataAdapter;

import java.util.Objects;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ReportLK1Fragment extends Fragment {
    @BindView(R.id.loading_wrapper)
    RelativeLayout loadingWrapper;
    @BindView(R.id.content_wrapper)
    ScrollView contentWrapper;
    @BindView(R.id.chart_cadre)
    LineChart cadreChart;
    @BindView(R.id.rv_cadre_data)
    RecyclerView rvCadreData;
    @BindView(R.id.rv_master)
    RecyclerView rvMaster;
    AppDb appDb;;
    UserDao userDao;
    User user;

    private ReportLK1ViewModel mViewModel;

    public static ReportLK1Fragment newInstance() {
        return new ReportLK1Fragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.report_l_k1_fragment, container, false);
        ButterKnife.bind(this, v);

        MainActivity activity = (MainActivity) getActivity();

        if (activity != null) {
            setHasOptionsMenu(true);
            activity.setToolBar(getString(R.string.laporan));
        }

        return v;
    }

    private void initView() {
        mViewModel.isLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                loadingWrapper.setVisibility(View.VISIBLE);
                contentWrapper.setVisibility(View.GONE);
            }
            else {
                initCadreChart();
                initCadreDataAdapter();
                initMasterDataAdapter();
                loadingWrapper.setVisibility(View.GONE);
                contentWrapper.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initMasterDataAdapter() {
        MasterDataAdapter masterDataAdapter = new MasterDataAdapter(mViewModel.getMasterList());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        rvMaster.setLayoutManager(linearLayoutManager);
        rvMaster.setAdapter(masterDataAdapter);
    }

    private void initCadreDataAdapter() {
        LaporanGrafikAdapter adapter = new LaporanGrafikAdapter(getContext(),
                mViewModel.getListKader(getCommissariat()), dataGrafik -> {
            // do nothing
        });

        rvCadreData.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCadreData.setAdapter(adapter);
    }

    private void initCadreChart() {
        LineDataSet dataSet = new LineDataSet(mViewModel.getKaderChartDataset(getCommissariat()), "");
        dataSet.setColors(getResources().getColor(R.color.colorPrimary));
        LineData data = new LineData(dataSet);

        YAxis yAxis = cadreChart.getAxisLeft();
        yAxis.setLabelCount(5, false);
        yAxis.setAxisMaximum(100f);
        yAxis.setAxisMinimum(0f);

        cadreChart.getAxisRight().setEnabled(false);

        XAxis xAxis = cadreChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(mViewModel.getKaderChartLabel()));
        xAxis.setGranularity(1.0f);

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = Objects.requireNonNull(getContext()).getTheme();
        theme.resolveAttribute(R.attr.textcolor, typedValue, true);
        @ColorInt int textColor = typedValue.data;
        yAxis.setTextColor(textColor);
        xAxis.setTextColor(textColor);
        data.setValueTextColor(textColor);

        cadreChart.setData(data);
        cadreChart.getDescription().setEnabled(false);
        cadreChart.getLegend().setEnabled(false);
        cadreChart.invalidate();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ReportLK1ViewModel.class);
        mViewModel.init(getContext());
        initView();
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

    private String getCommissariat() {
        appDb = AppDb.getInstance(getContext());
        userDao = appDb.userDao();
        user = userDao.getUser();
        String a = user.getKomisariat();

        return a;
    }
}
