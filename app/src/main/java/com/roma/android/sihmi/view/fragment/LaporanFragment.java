package com.roma.android.sihmi.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.helper.AgendaScheduler;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Agenda;
import com.roma.android.sihmi.model.database.entity.Contact;
import com.roma.android.sihmi.model.database.entity.DataKader;
import com.roma.android.sihmi.model.database.entity.Training;
import com.roma.android.sihmi.model.database.entity.User;
import com.roma.android.sihmi.model.database.interfaceDao.ContactDao;
import com.roma.android.sihmi.model.database.interfaceDao.InterfaceDao;
import com.roma.android.sihmi.model.database.interfaceDao.MasterDao;
import com.roma.android.sihmi.model.database.interfaceDao.TrainingDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.model.network.ApiClient;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.response.AgendaResponse;
import com.roma.android.sihmi.model.response.KonstituisiResponse;
import com.roma.android.sihmi.model.response.PengajuanResponse;
import com.roma.android.sihmi.model.response.SejarahResponse;
import com.roma.android.sihmi.model.response.TrainingResponse;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Query;
import com.roma.android.sihmi.utils.QueryRoomDao;
import com.roma.android.sihmi.view.activity.LaporanDetailActivity;
import com.roma.android.sihmi.view.activity.LaporanGrafikActivity;
import com.roma.android.sihmi.view.activity.MainActivity;
import com.roma.android.sihmi.view.adapter.DataKaderAdapter;
import com.roma.android.sihmi.view.adapter.LaporanAdapter;
import com.roma.android.sihmi.view.adapter.LaporanUserAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
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
public class LaporanFragment extends Fragment {

    @BindView(R.id.cv_aktivitas_pengguna)
    CardView cvAktivitasPengguna;
    @BindView(R.id.tv_vm_aktivitas_pengguna)
    TextView tvVmAktivitasPengguna;
    @BindView(R.id.tv_aktivitas_pengguna)
    TextView tvAktivitasPengguna;
    @BindView(R.id.rv_aktivitas_pengguna)
    RecyclerView rvAktivitasPengguna;

    @BindView(R.id.cv_media_pengguna)
    CardView cvMediaPengguna;
    @BindView(R.id.tv_vm_media_pengguna)
    TextView tvVmMediaPengguna;
    @BindView(R.id.tv_media_pengguna)
    TextView tvMediaPengguna;
    @BindView(R.id.rv_media_pengguna)
    RecyclerView rvMediaPengguna;

    @BindView(R.id.cv_approval_user)
    CardView cvApprovalUser;
    @BindView(R.id.tv_vm_approval_user)
    TextView tvVmApprovalUser;
    @BindView(R.id.rv_approval_user)
    RecyclerView rvApprovalUser;

    @BindView(R.id.cv_admin)
    CardView cvAdmin;
    @BindView(R.id.tv_vm_admin)
    TextView tvVmAdmin;
    @BindView(R.id.tv_admin)
    TextView tvAdmin;
    @BindView(R.id.rv_admin)
    RecyclerView rvAdmin;

    @BindView(R.id.cv_data_admin)
    CardView cvDataAdmin;
    @BindView(R.id.tv_vm_data_admin)
    TextView tvVmDataAdmin;
    @BindView(R.id.rv_data_admin)
    RecyclerView rvDataAdmin;

    @BindView(R.id.cv_copywriter_agenda)
    CardView cvCopyWriterAgenda;
    @BindView(R.id.tv_vm_copywriter_agenda)
    TextView tvVmCopyWriterAgenda;
    @BindView(R.id.tv_copywriter_agenda)
    TextView tvCopyWriterAgenda;
    @BindView(R.id.rv_copywriter_agenda)
    RecyclerView rvCopyWriterAgenda;

    @BindView(R.id.cv_copywriter_tentang_kami)
    CardView cvCopywriterTentangKami;
    @BindView(R.id.tv_vm_copywriter_tentang_kami)
    TextView tvVmCopywriterTentangKami;
    @BindView(R.id.tv_copywriter_tentang_kami)
    TextView tvCopywriterTentangKami;
    @BindView(R.id.rv_copywriter_tentang_kami)
    RecyclerView rvCopywriterTentangKami;

    @BindView(R.id.cv_copywriter_konstitusi)
    CardView cvCopywriterKonstitusi;
    @BindView(R.id.tv_vm_copywriter_konstitusi)
    TextView tvVmCopywriterKonstitusi;
    @BindView(R.id.tv_copywriter_konstitusi)
    TextView tvCopywriterKonstitusi;
    @BindView(R.id.rv_copywriter_konstitusi)
    RecyclerView rvCopywriterKonstitusi;

    @BindView(R.id.ll_badko)
    LinearLayout llBadko;
    @BindView(R.id.ll_cabang)
    LinearLayout llCabang;
    @BindView(R.id.ll_korkom)
    LinearLayout llKorkom;
    @BindView(R.id.ll_komisariat)
    LinearLayout llKomisariat;
    @BindView(R.id.ll_alumni)
    LinearLayout llAlumni;
    @BindView(R.id.ll_training)
    LinearLayout llTraining;
    @BindView(R.id.rv_badko)
    RecyclerView rvBadko;
    @BindView(R.id.rv_cabang)
    RecyclerView rvCabang;
    @BindView(R.id.rv_korkom)
    RecyclerView rvKorkom;
    @BindView(R.id.rv_komisariat)
    RecyclerView rvKomisariat;
    @BindView(R.id.rv_alumni)
    RecyclerView rvAlumni;
    @BindView(R.id.rv_training)
    RecyclerView rvTraining;

    @BindView(R.id.tv_total_kader)
    TextView tvTotalKader;
    @BindView(R.id.tv_total_all)
    TextView tvTotalAll;
    @BindView(R.id.tv_total_badko)
    TextView tvTotalBadko;
    @BindView(R.id.tv_total_cabang)
    TextView tvTotalCabang;
    @BindView(R.id.tv_total_korkom)
    TextView tvTotalKorkom;
    @BindView(R.id.tv_total_komisariat)
    TextView tvTotalKomisariat;
    @BindView(R.id.tv_total_alumni)
    TextView tvTotalAlumni;

    @BindView(R.id.tv_detail)
    TextView tvDetail;

    MasterService service;

    LaporanUserAdapter adapterAktivitas, adapterMedia, adapterAdmin, adapterApprove, adapterCwAgenda, adapterCwSejarah, adapterCwKonstitusi;

    String type, value;

    User user;

    AppDb appDb;
    UserDao userDao;
    ContactDao contactDao;
    InterfaceDao interfaceDao;
    MasterDao masterDao;
    TrainingDao trainingDao;

    public LaporanFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for getActivity() fragment
        View v = inflater.inflate(R.layout.fragment_laporan, container, false);
        ButterKnife.bind(this, v);
        setHasOptionsMenu(true);

        ((MainActivity) getActivity()).setToolBar(getString(R.string.laporan));

        service = ApiClient.getInstance().getApi();
        appDb = AppDb.getInstance(getContext());
        userDao = appDb.userDao();
        interfaceDao = appDb.interfaceDao();
        contactDao = appDb.contactDao();
        masterDao = appDb.masterDao();
        trainingDao = appDb.trainingDao();

        user = userDao.getUser();

        initView();
        initAdapter();
        initMaster();
        getAgenda();
        getKonstitusi();
        getSejarah();
        getTraining();
        return v;
    }

    private void initView(){
        llBadko.setVisibility(View.VISIBLE);
        llCabang.setVisibility(View.VISIBLE);
        llKorkom.setVisibility(View.VISIBLE);
        llKomisariat.setVisibility(View.VISIBLE);
        llAlumni.setVisibility(View.VISIBLE);
        llTraining.setVisibility(View.VISIBLE);
        cvAktivitasPengguna.setVisibility(View.VISIBLE);
        cvMediaPengguna.setVisibility(View.VISIBLE);
        cvAdmin.setVisibility(View.VISIBLE);
        cvCopyWriterAgenda.setVisibility(View.VISIBLE);
        cvCopywriterTentangKami.setVisibility(View.VISIBLE);
        cvCopywriterKonstitusi.setVisibility(View.VISIBLE);

        int countNonLK = contactDao.countRawQueryContact(new SimpleSQLiteQuery(Query.countReportSuperAdminNonLK()));
        int countLK = contactDao.countRawQueryContact(new SimpleSQLiteQuery(Query.countReportSuperAdmin()));
        int total = countLK + countNonLK;

        tvTotalKader.setText(": " + countLK + "\n: " + countNonLK);
        tvTotalAll.setText("" + total);
    }

    private void initMaster(){
        int totalBadko = masterDao.getMasterNameByType("1").size();
        int totalCabang =masterDao.getMasterNameByType("2").size();
        int totalKorkom = masterDao.getMasterNameByType("3").size();
        int totalKomisariat = masterDao.getMasterNameByType("4").size();
        int totalAlumni = masterDao.getMasterNameByType("2").size();
        int totalTraining = masterDao.getMasterNameByType("5").size();

        tvTotalBadko.setText(""+totalBadko);
        tvTotalCabang.setText(""+totalCabang);
        tvTotalKorkom.setText(""+totalKorkom);
        tvTotalKomisariat.setText(""+totalKomisariat);
        tvTotalAlumni.setText(""+totalAlumni);

    }

    private void initAdapter(){
        LaporanAdapter adapter = new LaporanAdapter(getActivity());

        adapterAktivitas = new LaporanUserAdapter(getActivity(), contactDao.getAllListContact(), Constant.LAP_AKTIVITAS_PENGGUNA, false);
        rvAktivitasPengguna.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvAktivitasPengguna.setAdapter(adapterAktivitas);

        adapterMedia = new LaporanUserAdapter(getActivity(), contactDao.getAllListContact(), Constant.LAP_MEDIA_PENGGUNA, false);
        rvMediaPengguna.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvMediaPengguna.setAdapter(adapterMedia);

        rvApprovalUser.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvApprovalUser.setAdapter(adapter);

        adapterAdmin = new LaporanUserAdapter(getActivity(), contactDao.getListAdmin(), Constant.LAP_ADMIN_AKTIF, false);
        rvAdmin.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvAdmin.setAdapter(adapterAdmin);

        adapterApprove = new LaporanUserAdapter(getActivity(), contactDao.getListAdmin(), Constant.LAP_APPROVE_USER, false);
        rvDataAdmin.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvDataAdmin.setAdapter(adapterApprove);

        adapterCwAgenda = new LaporanUserAdapter(getActivity(), contactDao.getListAdmin(), Constant.LAP_KONTEN_AGENDA, false);
        rvCopyWriterAgenda.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvCopyWriterAgenda.setAdapter(adapterCwAgenda);

        adapterCwSejarah = new LaporanUserAdapter(getActivity(), contactDao.getListAdmin(), Constant.LAP_KONTEN_SEJARAH, false);
        rvCopywriterTentangKami.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvCopywriterTentangKami.setAdapter(adapterCwSejarah);

        adapterCwKonstitusi = new LaporanUserAdapter(getActivity(), contactDao.getListAdmin(), Constant.LAP_KONTEN_KONSTITUSI, false);
        rvCopywriterKonstitusi.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvCopywriterKonstitusi.setAdapter(adapterCwKonstitusi);

        DataKaderAdapter adapterBadko = new DataKaderAdapter(getActivity(), dataKaders(Constant.M_BADKO, "1"), dataKader -> {
            // no action
        });
        rvBadko.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvBadko.setAdapter(adapterBadko);

        DataKaderAdapter adapterCabang = new DataKaderAdapter(getActivity(), dataKaders(Constant.M_CABANG, "2"), dataKader -> {
//            goToGrafik(Constant.M_CABANG, dataKader.getNama());

            String allItem = getString(R.string.semua)+ " " + getName(Constant.M_CABANG);
            if (!dataKader.getNama().equalsIgnoreCase(allItem)) {
                Bundle arguments = new Bundle();
                arguments.putBoolean(ReportFragment.SUPERADMIN_CABANG, true);
                arguments.putString(ReportFragment.CABANG_NAME, dataKader.getNama());

                ((MainActivity) Objects.requireNonNull(getActivity())).replaceFragment(new ReportFragment(), arguments);
            }
        });
        rvCabang.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvCabang.setAdapter(adapterCabang);

        DataKaderAdapter adapterKorkom = new DataKaderAdapter(getActivity(), dataKaders(Constant.M_KORKOM,  "3"), dataKader -> {
            // no action
        });
        rvKorkom.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvKorkom.setAdapter(adapterKorkom);

        DataKaderAdapter adapterKomisariat = new DataKaderAdapter(getActivity(), dataKaders(Constant.M_KOMISARIAT,  "4"), dataKader -> {
//            goToGrafik(Constant.M_KOMISARIAT, dataKader.getNama());

            String allItem = getString(R.string.semua)+ " " + getName(Constant.M_KOMISARIAT);
            if (!dataKader.getNama().equalsIgnoreCase(allItem)) {
                Bundle arguments = new Bundle();
                arguments.putBoolean(ReportFragment.SUPERADMIN_KOMISARIAT, true);
                arguments.putString(ReportFragment.KOMISARIAT_NAME, dataKader.getNama());

                ((MainActivity) Objects.requireNonNull(getActivity())).replaceFragment(new ReportFragment(), arguments);
            }
        });
        rvKomisariat.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvKomisariat.setAdapter(adapterKomisariat);

        DataKaderAdapter adapterAlumni = new DataKaderAdapter(getActivity(), dataKaders(Constant.M_ALUMNI, "2"), dataKader -> {
//            goToGrafik(Constant.M_ALUMNI, dataKader.getNama());

            String allItem = getString(R.string.semua)+ " " + getName(Constant.M_ALUMNI);
            if (!dataKader.getNama().equalsIgnoreCase(allItem)) {
                Bundle arguments = new Bundle();
                arguments.putBoolean(ReportFragment.SUPERADMIN_ALUMNI, true);
                arguments.putString(ReportFragment.CABANG_NAME, dataKader.getNama());

                ((MainActivity) Objects.requireNonNull(getActivity())).replaceFragment(new ReportFragment(), arguments);
            }
        });
        rvAlumni.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvAlumni.setAdapter(adapterAlumni);

        DataKaderAdapter adapterTraining = new DataKaderAdapter(getActivity(), dataKaders(Constant.M_TRAINING, "5"), dataKader -> {
            goToGrafik(Constant.M_TRAINING, dataKader.getNama());
        });
        rvTraining.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvTraining.setAdapter(adapterTraining);
    }

    private void goToGrafik(String type, String ket){
        if (type.equals(Constant.M_CABANG) || type.equals(Constant.M_KOMISARIAT) || type.equals(Constant.M_ALUMNI) || type.equals(Constant.M_TRAINING)) {
            Bundle bundle = new Bundle();
            bundle.putString(LaporanGrafikActivity.TYPE, type);
            bundle.putString(LaporanGrafikActivity.VALUE, ket);
            startActivity(new Intent(getActivity(), LaporanGrafikActivity.class).putExtras(bundle));
        }
    }

    private String getName(String type){
        String name="";
        if (type.equals(Constant.M_BADKO)){
            name = getString(R.string.badko);
        } else if (type.equals(Constant.M_CABANG)){
            name = getString(R.string.cabang);
        } else if (type.equals(Constant.M_KORKOM)){
            name = getString(R.string.korkom);
        } else if (type.equals(Constant.M_KOMISARIAT)){
            name = getString(R.string.komisariat);
        } else if (type.equals(Constant.M_TRAINING)){
            name = getString(R.string.pelatihan);
        } else {
            name = type;
        }
        return name;
    }

    private List<DataKader> dataKaders(String type, String masterType){
        List<DataKader> list = new ArrayList<>();

        if (!type.equals(Constant.M_TRAINING)) {
            SimpleSQLiteQuery query = QueryRoomDao.getUser(type, "", "", "", "");
            List<Contact> allMember = contactDao.rawQueryContact(query);

            list.add(new DataKader(getString(R.string.semua) +" "+ getName(type), allMember.size()));

            List<String> stringValue = masterDao.getMasterNameByType(masterType);
            for (int i = 0; i < stringValue.size(); i++) {
                String name = stringValue.get(i);

                SimpleSQLiteQuery queryValue = QueryRoomDao.getUser(type, name, "", "", "");
                List<Contact> memberValue = contactDao.rawQueryContact(queryValue);

                list.add(new DataKader(name, memberValue.size()));
            }
        } else {
            SimpleSQLiteQuery query = QueryRoomDao.getTraining("", "", "", "");
            List<Training> allTraining = trainingDao.rawQueryTraining(query);

            list.add(new DataKader(getString(R.string.semua)+" " + getName(type), allTraining.size()));

            List<String> stringValue = masterDao.getMasterNameByType(masterType);
            for (int i = 0; i < stringValue.size(); i++) {
                String name = stringValue.get(i);

                SimpleSQLiteQuery querys = QueryRoomDao.getTraining(name, "", "", "");
                List<Training> trainings = trainingDao.rawQueryTraining(querys);

                list.add(new DataKader(name, trainings.size()));
            }
        }
        return list;
    }

    @OnClick({R.id.tv_vm_aktivitas_pengguna, R.id.tv_vm_media_pengguna, R.id.tv_vm_admin, R.id.tv_vm_copywriter_agenda, R.id.tv_vm_copywriter_tentang_kami, R.id.tv_vm_copywriter_konstitusi, R.id.tv_detail})
    public void viewMore(TextView textView){
        switch (textView.getId()){
            case R.id.tv_vm_aktivitas_pengguna:
                startActivity(new Intent(getActivity(), LaporanDetailActivity.class).putExtra(LaporanDetailActivity.TYPE_LAPORAN, Constant.LAP_AKTIVITAS_PENGGUNA));
                break;
            case R.id.tv_vm_media_pengguna:
                startActivity(new Intent(getActivity(), LaporanDetailActivity.class).putExtra(LaporanDetailActivity.TYPE_LAPORAN, Constant.LAP_MEDIA_PENGGUNA));
                break;
            case R.id.tv_vm_admin:
                startActivity(new Intent(getActivity(), LaporanDetailActivity.class).putExtra(LaporanDetailActivity.TYPE_LAPORAN, Constant.LAP_ADMIN_AKTIF));
                break;
            case R.id.tv_vm_copywriter_agenda:
                startActivity(new Intent(getActivity(), LaporanDetailActivity.class).putExtra(LaporanDetailActivity.TYPE_LAPORAN, Constant.LAP_KONTEN_AGENDA));
                break;
            case R.id.tv_vm_copywriter_tentang_kami:
                startActivity(new Intent(getActivity(), LaporanDetailActivity.class).putExtra(LaporanDetailActivity.TYPE_LAPORAN, Constant.LAP_KONTEN_SEJARAH));
                break;
            case R.id.tv_vm_copywriter_konstitusi:
                startActivity(new Intent(getActivity(), LaporanDetailActivity.class).putExtra(LaporanDetailActivity.TYPE_LAPORAN, Constant.LAP_KONTEN_KONSTITUSI));
                break;
            case R.id.tv_detail:
                startActivity(new Intent(getActivity(), LaporanGrafikActivity.class));
                break;
        }
    }

    private void getApprovalUser(){
        List<Contact> list = new ArrayList<>();
        Call<PengajuanResponse> call = service.getPengajuanUser(Constant.getToken());
        call.enqueue(new Callback<PengajuanResponse>() {
            @Override
            public void onResponse(Call<PengajuanResponse> call, Response<PengajuanResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        if (response.body().getData().size() > 0){
                            for (int i = 0; i < response.body().getData().size() ; i++) {
                                Contact contact = contactDao.getContactById(response.body().getData().get(i).getId_user());
                                list.add(contact);
                            }
                            adapterCwKonstitusi.updateData(list);
                        }
                    } else {
                    }
                } else {
                }
            }

            @Override
            public void onFailure(Call<PengajuanResponse> call, Throwable t) {

            }
        });
    }

    private void getAgenda(){
        List<Contact> list = new ArrayList<>();
        Call<AgendaResponse> call = service.getAgenda(Constant.getToken(), "0");
        call.enqueue(new Callback<AgendaResponse>() {
            @Override
            public void onResponse(Call<AgendaResponse> call, Response<AgendaResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equalsIgnoreCase("ok")) {
                        List<Agenda> agendas = response.body().getData();
                        if (agendas.size() > 0){
                            List<String> agendaIds = new ArrayList<>();
                            for (int i = 0; i < agendas.size() ; i++) {
                                Agenda agenda = agendas.get(i);
                                Agenda checkDuplicate = appDb.agendaDao().getAgendaById(agenda.get_id());
                                agendaIds.add(agenda.get_id());
                                if (checkDuplicate != null){
                                    agenda.setReminder(checkDuplicate.isReminder());
                                }
                                Contact contact = contactDao.getContactById(agenda.getId_user());
                                contact.setKeterangan(agenda.getNama());
                                list.add(contact);
                            }
                            adapterCwAgenda.updateData(list);

                            appDb.agendaDao().deleteUnusedAgenda(agendaIds);
                            appDb.agendaDao().insertAgenda(response.body().getData());

                            AgendaScheduler.setupUpcomingAgendaNotifier(getContext());
                        }
                    } else {

                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<AgendaResponse> call, Throwable t) {

            }
        });
    }

    private void getKonstitusi(){
        List<Contact> list = new ArrayList<>();
        Call<KonstituisiResponse> call = service.getKonstitusi("0");
        call.enqueue(new Callback<KonstituisiResponse>() {
            @Override
            public void onResponse(Call<KonstituisiResponse> call, Response<KonstituisiResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equalsIgnoreCase("ok")) {
                        if (response.body().getData().size() > 0){
                            for (int i = 0; i < response.body().getData().size() ; i++) {
                                Contact contact = contactDao.getContactById(response.body().getData().get(i).getId_user());
                                contact.setKeterangan(response.body().getData().get(i).getNama());
                                list.add(contact);
                            }
                            adapterCwKonstitusi.updateData(list);
                        }
                    } else {

                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<KonstituisiResponse> call, Throwable t) {

            }
        });
    }

    private void getSejarah(){
        List<Contact> list = new ArrayList<>();
        Call<SejarahResponse> call = service.getSejarah(0);
        call.enqueue(new Callback<SejarahResponse>() {
            @Override
            public void onResponse(Call<SejarahResponse> call, Response<SejarahResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        if (response.body().getData().size() > 0){
                            for (int i = 0; i < response.body().getData().size() ; i++) {
                                Contact contact = contactDao.getContactById(response.body().getData().get(i).getId_user());
                                contact.setKeterangan(response.body().getData().get(i).getJudul());
                                list.add(contact);
                            }
                            adapterCwSejarah.updateData(list);
                        }
                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<SejarahResponse> call, Throwable t) {

            }
        });
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
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<TrainingResponse> call, Throwable t) {

            }
        });
    }


//    @Override
//    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
//        menu.clear();
//        inflater.inflate(R.menu.main, menu);
//        MenuItem searchItem = menu.findItem(R.id.action_search);
//        searchItem.setVisible(false);
//        MenuItem gridItem = menu.findItem(R.id.action_list);
//        gridItem.setVisible(false);
//        super.onCreateOptionsMenu(menu, inflater);
//    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }
}