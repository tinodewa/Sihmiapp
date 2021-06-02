package com.roma.android.sihmi.view.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.helper.AgendaScheduler;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Agenda;
import com.roma.android.sihmi.model.database.interfaceDao.AgendaDao;
import com.roma.android.sihmi.model.database.interfaceDao.MasterDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.model.network.ApiClient;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.response.AgendaResponse;
import com.roma.android.sihmi.model.response.GeneralResponse;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.StateView;
import com.roma.android.sihmi.utils.Tools;
import com.roma.android.sihmi.view.activity.AgendaDetailActivity;
import com.roma.android.sihmi.view.activity.AgendaFormActivity;
import com.roma.android.sihmi.view.activity.MainActivity;
import com.roma.android.sihmi.view.adapter.AgendaAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class AgendaFragment extends Fragment {
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.fab_add)
    FloatingActionButton fabAdd;

    @BindView(R.id.empty_screen)
    FrameLayout emptyLayout;

    @BindView(R.id.tv_empty)
    TextView tvEmpty;

    StateView stateView;

    MasterService service;
    AgendaAdapter adapter;
    List<Agenda> list= new ArrayList<>();

    LiveData<List<Agenda>> listLiveData;

    AppDb appDb;
    UserDao userDao;
    AgendaDao agendaDao;
    MasterDao masterDao;

    public AgendaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for getActivity() fragment
        View v = inflater.inflate(R.layout.fragment_agenda, container, false);
        ButterKnife.bind(this, v);
        setHasOptionsMenu(true);

        stateView = new StateView(v);
        stateView.setEmptyMessage("Agenda kosong,\nmulai tambahkan sekarang");

        service = ApiClient.getInstance().getApi();
        appDb = AppDb.getInstance(getContext());
        userDao = appDb.userDao();
        agendaDao = appDb.agendaDao();
        masterDao = appDb.masterDao();

        ((MainActivity) getActivity()).setToolBar(getString(R.string.judul_agenda));

//        Tools.visibilityFab(fabAdd);
        if (Tools.isAdmin1() || Tools.isAdmin2() || Tools.isLA1() || Tools.isLA2() || Tools.isSecondAdmin() || Tools.isSuperAdmin()){
            fabAdd.setVisibility(View.VISIBLE);
        } else {
            fabAdd.setVisibility(View.GONE);
        }

        if (Tools.isNonLK()){
            listLiveData = agendaDao.getAgendaNasional();
        } else if (Tools.isLK() || Tools.isAdmin1() || Tools.isAdmin2() || Tools.isAdmin3() || Tools.isLA1() || Tools.isLA2()){
            listLiveData = agendaDao.getAgendaNasCabKoms("%"+userDao.getUser().getCabang(),"%"+userDao.getUser().getKomisariat());
        } else {
            listLiveData = agendaDao.getAllAgenda();
        }

        listLiveData.observe(getActivity(), list -> {
            adapter.updateData(list);
            checkRvCount();
        });

        initAdapter();
        getData(0);
        return v;
    }

    @OnClick(R.id.fab_add)
    public void tambahAgenda(){
        startActivityForResult(new Intent(getActivity(), AgendaFormActivity.class).putExtra(AgendaFormActivity.IS_NEW, true), Constant.REQUEST_AGENDA);
    }

    private void initAdapter(){
        adapter = new AgendaAdapter(getActivity(), list, (agenda, isLongClick) -> {
            if (isLongClick){
                if (allowLongClick()) {
                    Tools.showDialogTindakan(getActivity(), ket -> {
                        if (ket.equals(Constant.UBAH)) {
                            if (allowUpdateDelete(agenda)) {
                                startActivityForResult(new Intent(getActivity(), AgendaFormActivity.class)
                                        .putExtra(AgendaFormActivity.ID_AGENDA, agenda.get_id()).putExtra(AgendaFormActivity.IS_NEW, false), Constant.REQUEST_AGENDA);
                            } else {
                                Tools.showToast(getActivity(), getString(R.string.hak_akses_perbarui));
                            }
                        } else if (ket.equals(Constant.HAPUS)) {
                            if (allowUpdateDelete(agenda)) {
                                Tools.confirmDelete(getActivity(), ket1 -> {
                                    if (ket1.equals(Constant.HAPUS)) {
                                        deleteData(agenda.get_id());
                                    }
                                });
                            } else {
                                Tools.showToast(getActivity(), getString(R.string.hak_akses_perbarui));
                            }
                        }
                    });
                }
            } else {
                startActivity(new Intent(getActivity().getApplicationContext(), AgendaDetailActivity.class)
                        .putExtra(AgendaDetailActivity.ID_AGENDA, agenda.get_id()));
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }

    private boolean allowLongClick(){
        if (Tools.isAdmin1() || Tools.isAdmin2() || Tools.isLA1() || Tools.isLA2() || Tools.isSecondAdmin() || Tools.isSuperAdmin()){
            return true;
        } else {
            return false;
        }
    }

    private boolean allowUpdateDelete (Agenda agenda){
        boolean allow;
        if (Tools.isAdmin1() && agenda.getType().contains("4-"+userDao.getUser().getKomisariat())){
            allow = true;
        } else if ((Tools.isAdmin2() || Tools.isLA1()) && agenda.getType().contains("2-"+userDao.getUser().getCabang())) {
            allow = true;
        } else if (Tools.isLA2() && agenda.getType().toLowerCase().contains("PB HMI".toLowerCase())) {
            allow = true;
        } else if (Tools.isSecondAdmin() || Tools.isSuperAdmin()){
            allow = true;
        } else {
            allow = false;
        }
        return allow;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_AGENDA && resultCode == Activity.RESULT_OK){
            getData(0);
        }
    }

    private void deleteData(String id) {
        Call<GeneralResponse> call = service.deleteAgenda(Constant.getToken(), id);
        if (Tools.isOnline(getActivity())) {
            Tools.showProgressDialog(getActivity(), getString(R.string.hapus_agenda));
            call.enqueue(new Callback<GeneralResponse>() {
                @Override
                public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getStatus().equalsIgnoreCase("ok")) {
                            agendaDao.deleteAgendaById(id);

                            AgendaScheduler.setupUpcomingAgendaNotifier(getContext());
                            Tools.showToast(getActivity(), getString(R.string.berhasil_hapus));
                        } else {
                            Tools.showToast(getActivity(), getString(R.string.gagal_hapus));
                        }
                    } else {
                        Tools.showToast(getActivity(), getString(R.string.gagal_hapus));
                    }
                    Tools.dissmissProgressDialog();
                }

                @Override
                public void onFailure(Call<GeneralResponse> call, Throwable t) {
                    Tools.showToast(getActivity(), getString(R.string.gagal_hapus));
                    Tools.dissmissProgressDialog();
                }
            });
        } else {
            Tools.showToast(getActivity(), getString(R.string.tidak_ada_internet));
        }
    }

    private void getData(int type){
        stateView.setLoading(true);
        Call<AgendaResponse> call = service.getAgenda(Constant.getToken(), String.valueOf(type));
        if (Tools.isOnline(getActivity())) {
            Tools.showProgressDialog(getActivity(), getString(R.string.load_data));
            call.enqueue(new Callback<AgendaResponse>() {
                @Override
                public void onResponse(Call<AgendaResponse> call, Response<AgendaResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getStatus().equalsIgnoreCase("ok")) {
                            List<Agenda> list = response.body().getData();
                            List<String> agendaIds = new ArrayList<>();
                            if (list.size() > 0){
                                for (int i = 0; i < list.size() ; i++) {
                                    Agenda agenda = list.get(i);
                                    Agenda checkDuplicate = agendaDao.getAgendaById(agenda.get_id());
                                    agendaIds.add(agenda.get_id());
                                    if (checkDuplicate != null){
                                        agenda.setReminder(checkDuplicate.isReminder());
                                    }
                                }
                            }
                            agendaDao.deleteUnusedAgenda(agendaIds);
                            agendaDao.insertAgenda(response.body().getData());

                            AgendaScheduler.setupUpcomingAgendaNotifier(getContext());
                            checkRvCount();
                        } else {
                            Tools.showToast(getActivity(), response.body().getMessage());
                        }
                    } else {
                        Tools.showToast(getActivity(), response.message());
                        checkRvCount();
                    }
                    Tools.dissmissProgressDialog();
                }

                @Override
                public void onFailure(Call<AgendaResponse> call, Throwable t) {
                    Tools.showToast(getActivity(), t.getMessage());
                    Tools.dissmissProgressDialog();
                    checkRvCount();
                }
            });
        } else {
            Tools.showToast(getActivity(), getString(R.string.tidak_ada_internet));
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            SearchView searchView = (SearchView) item.getActionView();
            searchView.setQueryHint(getString(R.string.cari));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Log.d("checckk", "onQueryTextSubmit: fragment");
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    List<Agenda> list;
                    if (Tools.isNonLK()){
                        list = agendaDao.getSearchAgendaNasional("%"+newText+"%");
                    } else if (Tools.isLK() || Tools.isAdmin1() || Tools.isAdmin2() || Tools.isAdmin3() || Tools.isLA1() || Tools.isLA2()){
                        list = agendaDao.getSearchAgendaNasCabKoms("%"+userDao.getUser().getCabang(), "%"+userDao.getUser().getKomisariat(),"%"+newText+"%");
                    } else {
                        list = agendaDao.getSearchAgenda("%"+newText+"%");
                    }
                    adapter.updateData(list);
                    return true;
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkRvCount() {
        int rvCount = adapter.getItemCount();
        stateView.checkState(rvCount);
        stateView.setLoading(false);
    }
}
