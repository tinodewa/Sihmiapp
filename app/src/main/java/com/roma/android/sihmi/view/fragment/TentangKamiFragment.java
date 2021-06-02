package com.roma.android.sihmi.view.fragment;


import android.annotation.SuppressLint;
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
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Sejarah;
import com.roma.android.sihmi.model.database.interfaceDao.SejarahDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.model.network.ApiClient;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.response.GeneralResponse;
import com.roma.android.sihmi.model.response.SejarahResponse;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Tools;
import com.roma.android.sihmi.view.activity.AboutDetailActivity;
import com.roma.android.sihmi.view.activity.AboutFormActivity;
import com.roma.android.sihmi.view.activity.MainActivity;
import com.roma.android.sihmi.view.adapter.TentangKamiAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("NonConstantResourceId")
public class TentangKamiFragment extends Fragment {
    private static final String TAG = "TentangKamiFragment";
    @BindView(R.id.rv_tentang_kami)
    RecyclerView rvTentangKami;
    @BindView(R.id.tv_desc)
    TextView tvDesc;
    @BindView(R.id.fab_add)
    FloatingActionButton fab;
    @BindView(R.id.refresh)
    SwipeRefreshLayout refreshLayout;
    MasterService service;
    int type;
    List<Sejarah> list = new ArrayList<>();
    TentangKamiAdapter adapter;

    LiveData<List<Sejarah>> listLiveData;

    AppDb appDb;
    SejarahDao sejarahDao;
    UserDao userDao;

    public TentangKamiFragment() {
        // Required empty public constructor
    }

    public TentangKamiFragment(int type) {
        this.type = type;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tentang_kami, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.setToolBar(getString(R.string.judul_tentang_kami));
        }

        service = ApiClient.getInstance().getApi();
        appDb = AppDb.getInstance(getContext());
        sejarahDao = appDb.sejarahDao();
        userDao = appDb.userDao();

//        Tools.visibilityFab(fab);
        if (Tools.isSuperAdmin() || (type == 2 && Tools.isLA1()) ||
                (type == 3 && Tools.isAdmin1()) || Tools.isLA2()){
            fab.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.GONE);
        }

        fab.setOnClickListener(v -> startActivityForResult(new Intent(getContext(), AboutFormActivity.class).putExtra(AboutFormActivity.IS_NEW, true).putExtra(AboutFormActivity.TYPE, type), Constant.REQUEST_TENTANG_KAMI));

        initAdapter();

        if (type == 1){
            getData();
            listLiveData = sejarahDao.getSejarahNasional();
        } else if (type == 2){
            if (Tools.isSecondAdmin() || Tools.isSuperAdmin()) {
                listLiveData = sejarahDao.getSejarahByType("2-%");
            } else {
                listLiveData = sejarahDao.getSejarahByType("%" + userDao.getUser().getCabang());
            }
        } else {
            if (Tools.isSecondAdmin() || Tools.isSuperAdmin()) {
                listLiveData = sejarahDao.getSejarahByType("4-%");
            } else {
                listLiveData = sejarahDao.getSejarahByType("%" + userDao.getUser().getKomisariat());
            }
        }

        listLiveData.observe(getActivity(), sejarahs -> adapter.updateData(sejarahs));

        refreshLayout.setRefreshing(false);
        refreshLayout.setOnRefreshListener(this::getData);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_TENTANG_KAMI && resultCode == Activity.RESULT_OK){
            Log.d(TAG, "onActivityResult: tentang kami");
            getData();
        }
    }

    private void initAdapter() {
        list = new ArrayList<>();
        adapter = new TentangKamiAdapter(getContext(), list, (sejarah, isLongClick) -> {
            if (isLongClick){
                if (allowLongClick()){
                    Tools.showDialogTindakan(getActivity(), ket -> {
                        if (ket.equals(Constant.UBAH))
                        {
                            if (allowUpdate(sejarah)) {
                                startActivityForResult(new Intent(getActivity().getApplicationContext(), AboutFormActivity.class).putExtra(AboutFormActivity.IS_NEW, false).putExtra(AboutFormActivity.ID_SEJ, sejarah.get_id()), Constant.REQUEST_TENTANG_KAMI);
                            } else {
                                Tools.showToast(getActivity(), getString(R.string.hak_akses_perbarui));
                            }
                        }
                        else if (ket.equals(Constant.HAPUS))
                        {
                            if (allowDelete()) {
                                Tools.confirmDelete(getActivity(), ket1 -> {
                                    if (ket1.equals(Constant.HAPUS)) {
                                        deleteData(sejarah.get_id());
                                    }
                                });
                            } else {
                                Tools.showToast(getActivity(), getString(R.string.hak_akses_hapus));
                            }
                        }
                    });
                }
            } else {
                startActivity(new Intent(getActivity().getApplicationContext(), AboutDetailActivity.class).putExtra(AboutDetailActivity.ID_ABOUT, sejarah.get_id()));
            }
        });
        rvTentangKami.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTentangKami.setAdapter(adapter);
    }

    private boolean allowLongClick(){
        if (Tools.isAdmin1() || Tools.isLA1() || Tools.isLA2() || Tools.isSecondAdmin() || Tools.isSuperAdmin()){
            return true;
        } else {
            return false;
        }
    }

    private boolean allowUpdate (Sejarah sejarah){
        boolean allow;
        if (Tools.isAdmin1() && sejarah.getType().contains("4-"+userDao.getUser().getKomisariat())){
            allow = true;
        } else if (Tools.isLA1() && sejarah.getType().contains("2-"+userDao.getUser().getCabang())) {
            allow = true;
        } else if (Tools.isLA2() && sejarah.getType().contains("0-PB HMI")) {
            allow = true;
        } else if (Tools.isSecondAdmin() || Tools.isSuperAdmin()){
            allow = true;
        } else {
            allow = false;
        }
        return allow;
    }

    private boolean allowDelete (){
        if ((Tools.isSecondAdmin() && type != 1) || Tools.isSuperAdmin()){
            return true;
        } else {
            return false;
        }
    }

    private void deleteData(String id) {
        Tools.showProgressDialog(getContext(), getString(R.string.menghapus_tentang_kami));
        Call<GeneralResponse> call = service.deleteSejarah(Constant.getToken(), id);
        call.enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        Tools.showToast(getContext(), getResources().getString(R.string.berhasil_hapus));
                        sejarahDao.deleteSejarahById(id);
                        Tools.dissmissProgressDialog();
                    } else {
                        Tools.dissmissProgressDialog();
                        Tools.showToast(getContext(), getResources().getString(R.string.gagal_hapus));
                    }
                } else {
                    Tools.dissmissProgressDialog();
                    Tools.showToast(getContext(), getResources().getString(R.string.gagal_hapus));

                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                Tools.dissmissProgressDialog();
                Tools.showToast(getContext(), getResources().getString(R.string.gagal_hapus));

            }
        });

    }

    private void getData() {
        Tools.showProgressDialog(getContext(), getString(R.string.load_data));
        Call<SejarahResponse> call = service.getSejarah(0);
        call.enqueue(new Callback<SejarahResponse>() {
            @Override
            public void onResponse(Call<SejarahResponse> call, Response<SejarahResponse> response) {
                if (refreshLayout.isRefreshing()) {
                    refreshLayout.setRefreshing(false);
                }
                if (response.isSuccessful()) {
                    sejarahDao.deleteAllSejarah();
                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        sejarahDao.insertSejarah(response.body().getData());
                        Tools.dissmissProgressDialog();
                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<SejarahResponse> call, Throwable t) {
                if (refreshLayout.isRefreshing()) {
                    refreshLayout.setRefreshing(false);
                }
            }
        });
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
                    List<Sejarah> list;
                    if (type == 1){
                        list = sejarahDao.getSearchSejarah("0-PB HMI", "%"+newText+"%");
                    } else if (type == 2){
                        if (Tools.isSecondAdmin() || Tools.isSuperAdmin()) {
                            list = sejarahDao.getSearchSejarah("2-%", "%"+newText+"%");
                        } else {
                            list = sejarahDao.getSearchSejarah("%" + userDao.getUser().getCabang(), "%"+newText+"%");
                        }
                    } else if (type == 3) {
                        if (Tools.isSecondAdmin() || Tools.isSuperAdmin()) {
                            list = sejarahDao.getSearchSejarah("4-%", "%"+newText+"%");
                        } else {
                            list = sejarahDao.getSearchSejarah("%" + userDao.getUser().getKomisariat(), "%"+newText+"%");
                        }
                    } else {
                        list = sejarahDao.getSearchSejarah("", "%"+newText+"%");
                    }

                    adapter.updateData(list);
                    return true;
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

}
