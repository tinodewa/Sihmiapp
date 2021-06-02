package com.roma.android.sihmi.view.fragment;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Leader;
import com.roma.android.sihmi.model.database.interfaceDao.LeaderDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.model.network.ApiClient;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.response.GeneralResponse;
import com.roma.android.sihmi.model.response.LeaderResponse;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.StateView;
import com.roma.android.sihmi.utils.Tools;
import com.roma.android.sihmi.view.activity.LeaderFormActivity;
import com.roma.android.sihmi.view.adapter.LeaderDetailAdapter;
import com.roma.android.sihmi.view.adapter.LeaderDetailNonSuperAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class LeaderFragment extends Fragment {
    @BindView(R.id.rv_leader)
    RecyclerView rvLeader;
    @BindView(R.id.fab_add)
    FloatingActionButton fabAdd;

    @BindView(R.id.empty_screen)
    FrameLayout emptyLayout;

    @BindView(R.id.tv_empty)
    TextView tvEmpty;

    StateView stateView;

    int type;
    MasterService service;
    LeaderDetailAdapter adapter;
    LeaderDetailNonSuperAdapter adapterNonSuper;

    LiveData<List<Leader>> listLiveData;
    List<Leader> list;

    AppDb appDb;
    LeaderDao leaderDao;
    UserDao userDao;

    public LeaderFragment() {
        // Required empty public constructor
    }

    public LeaderFragment(int type) {
        this.type = type;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_leader, container, false);
        ButterKnife.bind(this, v);
        setHasOptionsMenu(true);

        stateView = new StateView(v);
        stateView.setEmptyMessage("Data Pemimpin kosong");

        service = ApiClient.getInstance().getApi();
        appDb = AppDb.getInstance(getContext());
        leaderDao = appDb.leaderDao();
        userDao = appDb.userDao();

        list = new ArrayList<>();
        if (type == 1){
            listLiveData = leaderDao.getLeaderNasional();
        } else if (type == 2){
            if (Tools.isSecondAdmin() || Tools.isSuperAdmin()){
                listLiveData = leaderDao.getLeaderByType("2-%");
            } else {
                listLiveData = leaderDao.getLeaderByType("%"+userDao.getUser().getCabang());
            }
        } else if (type == 3){
            if (Tools.isSecondAdmin() || Tools.isSuperAdmin()){
                listLiveData = leaderDao.getLeaderByType("4-%");
            } else {
                listLiveData = leaderDao.getLeaderByType("%"+userDao.getUser().getKomisariat());
            }
        }

        listLiveData.observe(getActivity(), leaders -> {
            try {
                if (!Tools.isSuperAdmin() || type == 1) {
                    adapter.updateData(leaders);
                } else {
                    adapterNonSuper.updateData(leaders);
                }

                checkRvCount();
            } catch (NullPointerException e){

            }
        });

        Tools.visibilityFab(fabAdd);
        if (type == 1 && Tools.isLA2()){
            fabAdd.setVisibility(View.VISIBLE);
        }
        if (type == 2 && Tools.isLA1()){
            fabAdd.setVisibility(View.VISIBLE);
        }
        if (type == 3 && Tools.isAdmin1()){
            fabAdd.setVisibility(View.VISIBLE);
        }


        if (!Tools.isSuperAdmin() || type == 1){
            initAdapterSuper();
        } else {
            initAdapterNonSuper();
        }

        fabAdd.setOnClickListener(v1 -> startActivityForResult(new Intent(getActivity(), LeaderFormActivity.class).putExtra(LeaderFormActivity.IS_NEW, true).putExtra(LeaderFormActivity.TYPE, type), Constant.REQUEST_LEADER));
        return v;
    }

    private void initAdapterSuper(){
        adapter = new LeaderDetailAdapter(getContext(), list, (leader, type) -> {
            if (type == Constant.READ) {
                // empty
            } else if (type == Constant.UPDATE){
                startActivityForResult(new Intent(getActivity(), LeaderFormActivity.class).putExtra(LeaderFormActivity.IS_NEW, false).putExtra(LeaderFormActivity.ID_LEAD, leader.get_id()), Constant.REQUEST_LEADER);
            } else {
                confirmDelete(leader.get_id());
            }
        });

        if ((type == 2 && Tools.isLA1()) || (type == 3 && Tools.isAdmin1())) {
            adapter.setAllowDelete(true);
            adapter.setAllowUpdate(true);
        }
        else if (type == 1 && Tools.isLA2()) {
            adapter.setAllowUpdate(true);
        }

        rvLeader.setLayoutManager(new LinearLayoutManager(getContext()));
        rvLeader.setAdapter(adapter);
    }

    private void initAdapterNonSuper(){
        adapterNonSuper = new LeaderDetailNonSuperAdapter(getContext(), list, (leader, type) -> {
            if (type == Constant.READ) {
                // empty
            } else if (type == Constant.UPDATE){
                startActivityForResult(new Intent(getActivity(), LeaderFormActivity.class).putExtra(LeaderFormActivity.IS_NEW, false).putExtra(LeaderFormActivity.ID_LEAD, leader.get_id()), Constant.REQUEST_LEADER);
            } else {
                confirmDelete(leader.get_id());
            }
        });
        rvLeader.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvLeader.setAdapter(adapterNonSuper);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getData();
    }

    private void confirmDelete(String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.komfirmasi_title));
        builder.setMessage(R.string.konfirmasi);
        builder.setPositiveButton(getString(R.string.ya), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteData(id);
            }
        });
        builder.setNegativeButton(getString(R.string.tidak), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteData(String id) {
        Tools.showProgressDialog(getContext(), getString(R.string.menghapus));
        Call<GeneralResponse> call = service.deleteLeader(Constant.getToken(), id);
        call.enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equalsIgnoreCase("ok")) {
                        Tools.showToast(getContext(), getResources().getString(R.string.berhasil_hapus));
                        leaderDao.deleteLeaderById(id);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_LEADER && resultCode == Activity.RESULT_OK){
            getData();
        }
    }

    private void getData(){
        stateView.setLoading(true);
        Call<LeaderResponse> call = service.getLeader(Constant.getToken(), "0");
        call.enqueue(new Callback<LeaderResponse>() {
            @Override
            public void onResponse(Call<LeaderResponse> call, Response<LeaderResponse> response) {
                if (response.isSuccessful()){
                    if (response.body().getStatus().equalsIgnoreCase("ok")){
                        leaderDao.insertLeader(response.body().getData());
                        checkRvCount();
                    }
                }
            }

            @Override
            public void onFailure(Call<LeaderResponse> call, Throwable t) {
                checkRvCount();
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
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    List<Leader> list = new ArrayList<>();
                    if (type == 1){
//                        code lama
//                        list = leaderDao.getSearchLeaderByType("0-PB HMI", "%"+newText+"%");
                        list = leaderDao.getSearchLeaderNasional("0-PB HMI", "%"+newText+"%");
                    } else if (type == 2){
                        if (Tools.isSecondAdmin() || Tools.isSuperAdmin()){
                            list = leaderDao.getSearchLeaderByType("2-%", "%" + newText + "%");
                        } else {
                            list = leaderDao.getSearchLeaderByType("%" + userDao.getUser().getCabang(), "%" + newText + "%");
                        }
                    } else if (type == 3){
                        if (Tools.isSecondAdmin() || Tools.isSuperAdmin()){
                            list = leaderDao.getSearchLeaderByType("4-%", "%" + newText + "%");
                        } else {
                            list = leaderDao.getSearchLeaderByType("%" + userDao.getUser().getKomisariat(), "%" + newText + "%");
                        }
                    } else {
                        list = leaderDao.getSearchLeader("%"+newText+"%");
                    }
                    if (adapter != null) {
                        adapter.updateData(list);
                    }
                    else {
                        adapterNonSuper.updateData(list);
                    }
                    return true;
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkRvCount() {
        int rvCount = rvLeader.getAdapter().getItemCount();
        stateView.checkState(rvCount);
        stateView.setLoading(false);
    }
}
