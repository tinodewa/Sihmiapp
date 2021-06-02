package com.roma.android.sihmi.view.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Contact;
import com.roma.android.sihmi.model.database.entity.DataKader;
import com.roma.android.sihmi.model.database.entity.Training;
import com.roma.android.sihmi.model.database.entity.User;
import com.roma.android.sihmi.model.database.interfaceDao.ContactDao;
import com.roma.android.sihmi.model.database.interfaceDao.MasterDao;
import com.roma.android.sihmi.model.database.interfaceDao.TrainingDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.model.network.ApiClient;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.response.TrainingResponse;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Tools;
import com.roma.android.sihmi.view.activity.LaporanGrafikActivity;
import com.roma.android.sihmi.view.activity.MainActivity;
import com.roma.android.sihmi.view.adapter.LaporanDataKaderPelatihanAdapter;
import com.roma.android.sihmi.view.adapter.LaporanDataMasterAdapter;
import com.roma.android.sihmi.view.adapter.LaporanDataPelatihanAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.sqlite.db.SimpleSQLiteQuery;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class LaporanNonSuperAdminFragment extends Fragment {
    @BindView(R.id.llAlumni)
    LinearLayout llAlumni;
    @BindView(R.id.tv_detail_alumni)
    TextView tvDetailAlumni;
    @BindView(R.id.chart_alumni)
    LineChart chartAlumni;
    @BindView(R.id.llKaderisasi)
    LinearLayout llKaderisasi;
    @BindView(R.id.tv_detail_kaderisasi)
    TextView tvDetailKaderisasi;
    @BindView(R.id.chart_kaderisasi)
    LineChart chartKaderisasi;
    @BindView(R.id.llDataKader)
    LinearLayout llDataKader;
    @BindView(R.id.rv_kaderisasi)
    RecyclerView rvKaderisasi;
    @BindView(R.id.rv_semua_data)
    RecyclerView rvSemuaData;
    @BindView(R.id.llPelatihan)
    LinearLayout llPelatihan;
    @BindView(R.id.rv_data_pelatihan)
    RecyclerView rvDataPelatihan;
    @BindView(R.id.cv_chart_gender)
    CardView cvChartGender;
    @BindView(R.id.pie_chart)
    PieChart pieChart;
    @BindView(R.id.btn_detail)
    Button btnDetail;

    MasterService service;
    AppDb appDb;
    UserDao userDao;
    ContactDao contactDao;
    MasterDao masterDao;
    TrainingDao trainingDao;

    User user;
    int now, batas_tahun;


    public LaporanNonSuperAdminFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_laporan_non_super_admin, container, false);
        ButterKnife.bind(this, v);

        setHasOptionsMenu(true);

        ((MainActivity) getActivity()).setToolBar(getString(R.string.laporan));

        service = ApiClient.getInstance().getApi();
        appDb = AppDb.getInstance(getContext());
        userDao = appDb.userDao();
        contactDao = appDb.contactDao();
        masterDao = appDb.masterDao();
        trainingDao = appDb.trainingDao();

        user = userDao.getUser();

        now = Integer.valueOf(Tools.getYearFromMillis(System.currentTimeMillis()));
        batas_tahun = now - 4;

        initView();
        getTraining();
        return v;
    }

    public void initView(){
        if (Tools.isLK()){
            viewLK1();
        } else if (Tools.isAdmin1()){
            viewAdminKomisariat();
        } else if (Tools.isAdmin2()){
            viewAdminBPL();
        } else if (Tools.isAdmin3()){
            viewAdminAlumni();
        } else if (Tools.isLA1()){
            viewAdminCabang();
        } else if (Tools.isLA2() || Tools.isSecondAdmin()){
            viewAdminPBHMI();
        }

//        initLineChartAlumni();
//        initPieChart();
    }

    public void viewLK1(){
        llAlumni.setVisibility(View.GONE);
        llPelatihan.setVisibility(View.GONE);
        cvChartGender.setVisibility(View.GONE);
        tvDetailKaderisasi.setVisibility(View.GONE);
        getDataMaster();
        initLineChartKaderisasi();
    }

    public void viewAdminAlumni(){
        tvDetailKaderisasi.setVisibility(View.GONE);
        rvSemuaData.setVisibility(View.GONE);
        llPelatihan.setVisibility(View.GONE);
        cvChartGender.setVisibility(View.GONE);

        initLineChartAlumni();
        initLineChartKaderisasi();
    }

    public void viewAdminKomisariat(){
        llAlumni.setVisibility(View.GONE);
        llDataKader.setVisibility(View.GONE);
        rvSemuaData.setVisibility(View.GONE);

        initLineChartKaderisasi();
        initPieChart();
    }

    public void viewAdminBPL(){
        llAlumni.setVisibility(View.GONE);
        llKaderisasi.setVisibility(View.GONE);
        rvSemuaData.setVisibility(View.GONE);
        cvChartGender.setVisibility(View.GONE);
        tvDetailKaderisasi.setVisibility(View.GONE);

        initLineChartKaderisasi();
    }

    public void viewAdminCabang(){
        llAlumni.setVisibility(View.GONE);
        llDataKader.setVisibility(View.GONE);
        rvSemuaData.setVisibility(View.GONE);

        initLineChartKaderisasi();
        initPieChart();
    }

    public void viewAdminPBHMI(){
        llAlumni.setVisibility(View.GONE);
        llDataKader.setVisibility(View.GONE);
        rvSemuaData.setVisibility(View.GONE);

        initLineChartKaderisasi();
        initPieChart();
    }

    private void initDataKader(){
        String query = "SELECT COUNT (*) FROM Training WHERE (id_level != 19 AND id_level !=20) ";
        if (Tools.isLK()){

        } else if (Tools.isAdmin3()){
            query += " AND komisariat = '"+user.getKomisariat()+"' ";
        }

        List<DataKader> list = new ArrayList<>();
        for (int i = now; i >= batas_tahun ; i--) {
            int tahun = i;

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

            list.add(new DataKader(tahun, lk1, lk2, lk3, sc, tid));
        }

        LaporanDataKaderPelatihanAdapter adapter = new LaporanDataKaderPelatihanAdapter(getActivity(), list, new LaporanDataKaderPelatihanAdapter.ItemClickListener() {
            @Override
            public void onItemClick(DataKader dataKader) {

            }
        });

        rvKaderisasi.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvKaderisasi.setHasFixedSize(true);
        rvKaderisasi.setAdapter(adapter);

    }

    private void initDataPelatihan(){
        List<DataKader> list = new ArrayList<>();

        String query = "SELECT COUNT (*) FROM Training WHERE (id_level != 19 AND id_level !=20) ";
        if (Tools.isAdmin1()){
            query += " AND komisariat = '"+user.getKomisariat()+"' ";
        } else if (Tools.isLA1()){
            query += " AND cabang = '"+user.getCabang()+"' ";
        }

        int lk1 = trainingDao.countRawQueryTraining(
                new SimpleSQLiteQuery(query + " AND tipe = '" + Constant.TRAINING_LK1 + "';"));
        int lk2 = trainingDao.countRawQueryTraining(
                new SimpleSQLiteQuery(query + " AND tipe = '" + Constant.TRAINING_LK2 + "';"));
        int lk3 = trainingDao.countRawQueryTraining(
                new SimpleSQLiteQuery(query + " AND tipe = '" + Constant.TRAINING_LK3 + "';"));
        int sc = trainingDao.countRawQueryTraining(
                new SimpleSQLiteQuery(query + " AND tipe = '" + Constant.TRAINING_SC + "';"));
        int tid = trainingDao.countRawQueryTraining(
                new SimpleSQLiteQuery(query + " AND tipe = '" + Constant.TRAINING_TID + "';"));

        list.add(new DataKader("LK1 (Basic Training)", lk1));
        list.add(new DataKader("LK2 (Intermediate Training)", lk2));
        list.add(new DataKader("LK3 (Advance Training)", lk3));
        list.add(new DataKader("SC (Senior Course)", sc));
        list.add(new DataKader("TID (Training Instruktur Dasar)", tid));


        LaporanDataPelatihanAdapter adapter = new LaporanDataPelatihanAdapter(getActivity(), true, list, dataKader -> {
            Bundle bundle = new Bundle();
            bundle.putString(LaporanGrafikActivity.TYPE, Constant.PELATIHAN);
            startActivity(new Intent(getActivity(), LaporanGrafikActivity.class).putExtras(bundle));
        });

        if (Tools.isAdmin2()){
            rvDataPelatihan.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        } else {
            rvDataPelatihan.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        }
//        rvDataPelatihan.setHasFixedSize(true);
        rvDataPelatihan.setAdapter(adapter);
    }

    private void initLineChartAlumni(){
        ArrayList meber = new ArrayList();
        for (int i = batas_tahun; i <= now; i++) {
            String query = "SELECT COUNT (*) FROM Contact WHERE (id_level != 1 AND id_level !=19 AND id_level !=20) AND tahun_daftar ='"+i+"' AND domisili_cabang != '"+"';";
            int count = contactDao.countRawQueryContact(new SimpleSQLiteQuery(query));
            meber.add(new Entry(i, (float) count));
        }

        LineDataSet dataSet = new LineDataSet(meber, "");
        dataSet.setColors(new int[]{getResources().getColor(R.color.colorPrimary)});
        LineData data = new LineData(dataSet);

        final String[] quarters = new String[] {  };

        YAxis yAxis = chartAlumni.getAxisLeft();
        yAxis.setLabelCount(5, false);
        yAxis.setAxisMaximum(100f);
        yAxis.setAxisMinimum(0f);

        chartAlumni.getAxisRight().setEnabled(false);

        XAxis xAxis = chartAlumni.getXAxis();
        xAxis.setSpaceMax(0.1f);
        xAxis.setSpaceMin(0.1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(quarters));

        chartAlumni.setData(data);
        chartAlumni.getDescription().setEnabled(false);
        chartAlumni.getLegend().setEnabled(false);
        chartAlumni.invalidate();
    }

    private void initLineChartKaderisasi(){
        String query = "SELECT COUNT (*) FROM Contact WHERE (id_level != 19 AND id_level !=20) ";
        if (Tools.isLK()){

        } else if (Tools.isAdmin1() || Tools.isAdmin2() || Tools.isAdmin3()){
            query += " AND komisariat = '"+user.getKomisariat()+"' ";
        } else if (Tools.isLA1()){
            query += " AND cabang = '"+user.getCabang()+"' ";
        }

        ArrayList meber = new ArrayList();
        for (int i = batas_tahun; i <= now; i++) {
            int count = contactDao.countRawQueryContact(new SimpleSQLiteQuery(query+" AND tahun_daftar = '"+i+"';"));
            Log.d("testtt", "initLineChartKaderisasi: "+query);
            meber.add(new Entry((float) i, (float) count));
        }

        // the labels that should be drawn on the XAxis
//        final String[] year = new String[] { ""+(now-4),""+(now-3), ""+(now-2), ""+(now-1), ""+now };
        final String[] quarters = new String[] { };

        LineDataSet dataSet = new LineDataSet(meber, "");
        dataSet.setColors(new int[]{getResources().getColor(R.color.colorPrimary)});
        LineData data = new LineData(dataSet);

        // the labels that should be drawn on the XAxis

        YAxis yAxis = chartKaderisasi.getAxisLeft();
        yAxis.setLabelCount(5, false);
        yAxis.setAxisMaximum(100f);
        yAxis.setAxisMinimum(0f);

        chartKaderisasi.getAxisRight().setEnabled(false);

        XAxis xAxis = chartKaderisasi.getXAxis();
        xAxis.setSpaceMax(0.1f);
        xAxis.setSpaceMin(0.1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(quarters));

        chartKaderisasi.setData(data);
        chartKaderisasi.getDescription().setEnabled(false);
        chartKaderisasi.getLegend().setEnabled(false);
        chartKaderisasi.invalidate();
    }

    private void initPieChart() {
        String queryL = "SELECT COUNT (*) FROM Contact WHERE (id_level !=19 AND id_level !=20) AND jenis_kelamin = '"+0+"' ";
        String queryP = "SELECT COUNT (*) FROM Contact WHERE (id_level !=19 AND id_level !=20) AND jenis_kelamin = '"+1+"' ";

        if (Tools.isLK()) {

        } else if (Tools.isAdmin1() || Tools.isAdmin3()) {
            queryL += " AND komisariat = '" + user.getKomisariat() + "' ";
            queryP += " AND komisariat = '" + user.getKomisariat() + "' ";
        } else if (Tools.isLA1()) {
            queryL += " AND cabang = '" + user.getCabang() + "' ";
            queryP += " AND cabang = '" + user.getCabang() + "' ";
        }

        queryL += ";";
        queryP += ";";

        int totalL = contactDao.countRawQueryContact(new SimpleSQLiteQuery(queryL));
        int totalP = contactDao.countRawQueryContact(new SimpleSQLiteQuery(queryP));

        double total = totalL + totalP;
        double percentP = ((double) totalP / total) * 100;
        double percentL = 100 - percentP;

        ArrayList numberMember = new ArrayList();
        numberMember.add(new PieEntry((float) percentL, "Laki-laki"));
        numberMember.add(new PieEntry((float) percentP, "Perempuan"));
        PieDataSet dataSet = new PieDataSet(numberMember, "");
        dataSet.setColors(new int[]{getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimaryLight)});
        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawSliceText(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.invalidate();
    }

    private void getDataMaster(){
        int badko = masterDao.getListMasterByType("1").size();
        int cabang = masterDao.getListMasterByType("2").size();
        int korkom = masterDao.getListMasterByType("3").size();
        int komisariat = masterDao.getListMasterByType("4").size();

        List<DataKader> list = new ArrayList<>();
        list.add(new DataKader(getString(R.string.badko), badko));
        list.add(new DataKader(getString(R.string.cabang), cabang));
        list.add(new DataKader(getString(R.string.komisariat), komisariat));

        LaporanDataMasterAdapter adapter = new LaporanDataMasterAdapter(getActivity(), list, dataKader -> {

        });
        rvSemuaData.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        rvSemuaData.setHasFixedSize(true);
        rvSemuaData.setAdapter(adapter);
    }

    private void getTraining(){
        List<Training> list = new ArrayList<>();
        Call<TrainingResponse> call = service.getTraining(Constant.getToken(), "0");
        call.enqueue(new Callback<TrainingResponse>() {
            @Override
            public void onResponse(Call<TrainingResponse> call, Response<TrainingResponse> response) {
                if (response.isSuccessful()){
                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        int size = response.body().getData().size();
                        if (size > 0) {
                            for (int i = 0; i < size ; i++) {
                                Training training = response.body().getData().get(i);
                                Contact contact = contactDao.getContactById(training.getId_user());
                                if (training.getNama_training().contains("LK1")) {
                                    contact.setLk1(training.getNama_training());
                                    contact.setTahun_lk1(training.getTahun());
                                } else if (training.getNama_training().contains("LK2")) {
                                    contact.setLk2(training.getNama_training());
                                } else if (training.getNama_training().contains("LK3")) {
                                    contact.setLk3(training.getNama_training());
                                } else if (training.getNama_training().contains("SC")) {
                                    contact.setSc(training.getNama_training());
                                } else if (training.getNama_training().contains("TID")) {
                                    contact.setTid(training.getNama_training());
                                }

                                contactDao.insertContact(contact);


                                training.setId(training.getId());
                                training.setId_user(training.getId_user());
                                training.setId_level(contact.getId_level());
                                training.setCabang(contact.getCabang());
                                training.setKomisariat(contact.getKomisariat());
                                training.setDomisili_cabang(contact.getDomisili_cabang());
                                training.setJenis_kelamin(contact.getJenis_kelamin());

                                if (trainingDao.checkTrainingAvailable(training.getId_user(), training.getTipe(), training.getTahun()) == null){
                                    trainingDao.insertTraining(training);
                                }

                            }
                            initDataKader();
                            initDataPelatihan();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<TrainingResponse> call, Throwable t) {

            }
        });
    }

    @OnClick({R.id.tv_detail_kaderisasi, R.id.tv_detail_alumni, R.id.btn_detail})
    public void onClick(){
        Bundle bundle = new Bundle();
        bundle.putString(LaporanGrafikActivity.TYPE, Constant.KADERISASI);
        startActivity(new Intent(getActivity(), LaporanGrafikActivity.class).putExtras(bundle));
//        switch (textView.getId()){
//            case R.id.tv_detail_kaderisasi:
//                startActivity(new Intent(getActivity(), LaporanGrafikActivity.class));
//                break;
//            case R.id.tv_detail_alumni:
//                startActivity(new Intent(getActivity(), LaporanGrafikActivity.class));
//                break;
//        }
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

}
