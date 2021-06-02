package com.roma.android.sihmi.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Contact;
import com.roma.android.sihmi.model.database.entity.DataGrafik;
import com.roma.android.sihmi.model.database.entity.DataKader;
import com.roma.android.sihmi.model.database.entity.User;
import com.roma.android.sihmi.model.database.interfaceDao.ContactDao;
import com.roma.android.sihmi.model.database.interfaceDao.InterfaceDao;
import com.roma.android.sihmi.model.database.interfaceDao.MasterDao;
import com.roma.android.sihmi.model.database.interfaceDao.TrainingDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Query;
import com.roma.android.sihmi.utils.Tools;
import com.roma.android.sihmi.view.adapter.LaporanDataKaderPelatihanAdapter;
import com.roma.android.sihmi.view.adapter.LaporanGrafikAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.sqlite.db.SimpleSQLiteQuery;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.roma.android.sihmi.view.activity.DetailReportPelatihanActivity.VALUE_YEAR;

@SuppressLint("NonConstantResourceId")
public class LaporanGrafikActivity extends BaseActivity {
    public static final String TYPE = "type";
    public static final String VALUE = "value";
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.pie_chart)
    PieChart pieChart;
    @BindView(R.id.rv_data)
    RecyclerView rvData;

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_kader)
    TextView tvKader;
    @BindView(R.id.tv_non_kader)
    TextView tvNonKader;
    @BindView(R.id.cv_table)
    CardView cvTable;

    @BindView(R.id.pelatihan_chart)
    BarChart pelatihanChart;
    @BindView(R.id.rv_data_pelatihan)
    RecyclerView rvDataPelatihan;

    @BindView(R.id.ll_kader)
    LinearLayout llKader;
    @BindView(R.id.ll_pelatihan)
    LinearLayout llPelatihan;


    User user;
    List<DataGrafik> grafikList = new ArrayList<>();
    int totNonLk, totLk;

    int now, batas_tahun;
    String type="", value="";

    AppDb appDb;
    UserDao userDao;
    ContactDao contactDao;
    InterfaceDao interfaceDao;
    MasterDao masterDao;
    TrainingDao trainingDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan_grafik);
        ButterKnife.bind(this);

        initModule();
        initToolbar();
        initAdapter();
        getData();
    }

    private void initModule() {
        appDb = AppDb.getInstance(this);
        userDao = appDb.userDao();
        interfaceDao = appDb.interfaceDao();
        contactDao = appDb.contactDao();
        masterDao = appDb.masterDao();
        trainingDao = appDb.trainingDao();

        user = userDao.getUser();
        now = Integer.parseInt(Tools.getYearFromMillis(System.currentTimeMillis()));
        batas_tahun = now - 4;

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            type = bundle.getString(TYPE);
            value = bundle.getString(VALUE);
            Log.e("CEK DETAIL", "initModule: "+type+", value "+value);
            tvTitle.setText(value);
            if (type.equals(Constant.M_TRAINING)){
                llKader.setVisibility(View.GONE);
                llPelatihan.setVisibility(View.VISIBLE);
            } else{
                llKader.setVisibility(View.VISIBLE);
                llPelatihan.setVisibility(View.GONE);
            }
        }
    }

    private void initToolbar() {
        toolbar.setTitle(getString(R.string.pengguna_sihmi));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    private void getData() {
        String queryNonLk = Query.countReportSuperAdminNonLK();
        String queryLk = Query.countReportSuperAdmin();

        if (type.equals(Constant.M_CABANG)) {
            if (!value.contains(getString(R.string.semua))) {
                queryNonLk += "AND cabang = '" + value + "' ";
                queryLk += "AND cabang = '" + value + "' ";
            } else {
                queryNonLk += "AND cabang != '' ";
                queryLk += "AND cabang != '' ";
            }
        } else if (type.equals(Constant.M_KOMISARIAT)) {
            if (!value.contains(getString(R.string.semua))) {
                queryNonLk += "AND komisariat = '" + value + "' ";
                queryLk += "AND komisariat = '" + value + "' ";
            } else {
                queryNonLk += "AND komisariat != '' ";
                queryLk += "AND komisariat != '' ";
            }
        } else if (type.equals(Constant.M_ALUMNI)) {
            if (!value.contains(getString(R.string.semua))) {
                queryNonLk += "AND domisili_cabang = '" + value + "' ";
                queryLk += "AND domisili_cabang = '" + value + "' ";
            } else {
                queryNonLk += "AND domisili_cabang != '' ";
                queryLk += "AND domisili_cabang != '' ";
            }
        }

        Log.e("hahahaha", "getData: "+queryLk );

//        SimpleSQLiteQuery queryNonLk = new SimpleSQLiteQuery(Query.countReportSuperAdminNonLK());
//        SimpleSQLiteQuery queryLk = new SimpleSQLiteQuery(Query.countReportSuperAdmin());

        totNonLk = contactDao.countRawQueryContact(new SimpleSQLiteQuery(queryNonLk));
        totLk = contactDao.countRawQueryContact(new SimpleSQLiteQuery(queryLk));

        initPieChart(totLk, totNonLk);
        initPelatihanChart();
        initPelatihanAdapter();

        int now = Integer.parseInt(Tools.getYearFromMillis(System.currentTimeMillis()));

        for (int tahun = now; tahun >= 1947; tahun--) {

            String queryNonLkTahun = Query.countReportSuperAdminNonLK(tahun);
            String queryLkTahun = Query.countReportSuperAdmin(tahun);

            if (!value.contains(getString(R.string.semua))) {
                if (type.equals(Constant.M_CABANG)) {
                    queryNonLkTahun += "AND cabang = '" + value + "' ";
                    queryLkTahun += "AND cabang = '" + value + "' ";
                } else if (type.equals(Constant.M_KOMISARIAT)) {
                    queryNonLkTahun += "AND komisariat = '" + value + "' ";
                    queryLkTahun += "AND komisariat = '" + value + "' ";
                } else if (type.equals(Constant.M_ALUMNI)) {
                    queryNonLkTahun += "AND domisili_cabang = '" + value + "' ";
                    queryLkTahun += "AND domisili_cabang = '" + value + "' ";
                }
            }

            int NonLK = contactDao.countRawQueryContact(new SimpleSQLiteQuery(queryNonLkTahun));
            int LK = contactDao.countRawQueryContact(new SimpleSQLiteQuery(queryLkTahun));
            int total = NonLK + LK;
            grafikList.add(new DataGrafik(tahun, total, NonLK, LK));
        }
    }

    private void initAdapter() {
        LaporanGrafikAdapter adapter;// SUper Admin
        adapter = new LaporanGrafikAdapter(this, grafikList, dataGrafik -> {
            if (dataGrafik.getJumlah() > 0) {
                startActivity(new Intent(LaporanGrafikActivity.this, DataKaderActivity.class).putExtra(DataKaderActivity.TAHUN_KADER, dataGrafik.getTahun()));
            } else {
                Tools.showToast(this, getString(R.string.data_tidak_tersedia));
            }
        });
        rvData.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rvData.setHasFixedSize(true);
        rvData.setAdapter(adapter);
    }

    private void initPieChart(int total1, int total2) {
        double total = total1 + total2;
        double percent2 = ((double) total2 / total) * 100;
        double percent1 = 100 - percent2;

        ArrayList<PieEntry> numberMember = new ArrayList<>();
        numberMember.add(new PieEntry((float) percent1, "Kader"));
        numberMember.add(new PieEntry((float) percent2, "Non Kader"));
        PieDataSet dataSet = new PieDataSet(numberMember, "");
        dataSet.setColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimaryLight));
        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.getDescription().setEnabled(false);
        //noinspection deprecation
        pieChart.setDrawSliceText(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.invalidate();
    }

    private void initPelatihanChart(){
        String query = Query.countPelatihanLA2();

        ArrayList<BarEntry> member = new ArrayList<>();
        ArrayList<String> xAxisLabel = new ArrayList<>();
        for (int i = batas_tahun, j = 0; i <= now; i++, j++) {
            int lk1 = trainingDao.countRawQueryTraining(
                    new SimpleSQLiteQuery(query + " AND tipe = '" + Constant.TRAINING_LK1 + "' AND tahun = '" + i + "';"));
            int lk2 = trainingDao.countRawQueryTraining(
                    new SimpleSQLiteQuery(query + " AND tipe = '" + Constant.TRAINING_LK2 + "' AND tahun = '" + i + "';"));
            int lk3 = trainingDao.countRawQueryTraining(
                    new SimpleSQLiteQuery(query + " AND tipe = '" + Constant.TRAINING_LK3 + "' AND tahun = '" + i + "';"));
            int sc = trainingDao.countRawQueryTraining(
                    new SimpleSQLiteQuery(query + " AND tipe = '" + Constant.TRAINING_SC + "' AND tahun = '" + i + "';"));
            int tid = trainingDao.countRawQueryTraining(
                    new SimpleSQLiteQuery(query + " AND tipe = '" + Constant.TRAINING_TID + "' AND tahun = '" + i + "';"));
            int count = lk1+lk2+lk3+sc+tid;
            member.add(new BarEntry(j, (float) count));
            xAxisLabel.add(String.valueOf(i));
        }

        BarDataSet dataSet = new BarDataSet(member, "");
        dataSet.setColors(getResources().getColor(R.color.colorPrimary));
        BarData data = new BarData(dataSet);

        YAxis yAxis = pelatihanChart.getAxisLeft();
        yAxis.setLabelCount(5, false);
        yAxis.setAxisMaximum(100f);
        yAxis.setAxisMinimum(0f);

        pelatihanChart.getAxisRight().setEnabled(false);

        XAxis xAxis = pelatihanChart.getXAxis();
        xAxis.setSpaceMax(1.0f);
        xAxis.setSpaceMin(1.0f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));
        xAxis.setGranularity(1.0f);

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = this.getTheme();
        theme.resolveAttribute(R.attr.textcolor, typedValue, true);
        @ColorInt int textColor = typedValue.data;
        yAxis.setTextColor(textColor);
        xAxis.setTextColor(textColor);
        data.setValueTextColor(textColor);

        pelatihanChart.setData(data);
        pelatihanChart.getDescription().setEnabled(false);
        pelatihanChart.getLegend().setEnabled(false);
        pelatihanChart.invalidate();
    }

    // Pelatihan
    private List<DataKader> getListPelatihan(){
        String query = Query.countPelatihanLA2();
        List<DataKader> list = new ArrayList<>();
        for (int i = now; i >= 1947 ; i--) {
            int lk1 = trainingDao.countRawQueryTraining(
                    new SimpleSQLiteQuery(query + " AND tipe = '" + Constant.TRAINING_LK1 + "' AND tahun = '" + i + "';"));
            int lk2 = trainingDao.countRawQueryTraining(
                    new SimpleSQLiteQuery(query + " AND tipe = '" + Constant.TRAINING_LK2 + "' AND tahun = '" + i + "';"));
            int lk3 = trainingDao.countRawQueryTraining(
                    new SimpleSQLiteQuery(query + " AND tipe = '" + Constant.TRAINING_LK3 + "' AND tahun = '" + i + "';"));
            int sc = trainingDao.countRawQueryTraining(
                    new SimpleSQLiteQuery(query + " AND tipe = '" + Constant.TRAINING_SC + "' AND tahun = '" + i + "';"));
            int tid = trainingDao.countRawQueryTraining(
                    new SimpleSQLiteQuery(query + " AND tipe = '" + Constant.TRAINING_TID + "' AND tahun = '" + i + "';"));

            list.add(new DataKader(i, lk1, lk2, lk3, sc, tid));
        }
        return list;
    }

    private void initPelatihanAdapter(){
        LaporanDataKaderPelatihanAdapter pelatihanAdapter = new LaporanDataKaderPelatihanAdapter(this, getListPelatihan(), dataKader -> {
            int total = dataKader.getLk1()+dataKader.getLk2()+dataKader.getLk3()+dataKader.getSc()+dataKader.getTid();
            if (total > 0) {
                startActivity(new Intent(LaporanGrafikActivity.this, DetailReportPelatihanActivity.class).putExtra(VALUE_YEAR, dataKader.getTahun()));
            } else {
                Toast.makeText(this, getString(R.string.data_tidak_tersedia), Toast.LENGTH_SHORT).show();
            }
        });
        rvDataPelatihan.setLayoutManager(new LinearLayoutManager(this));
        rvDataPelatihan.setAdapter(pelatihanAdapter);

    }

}
