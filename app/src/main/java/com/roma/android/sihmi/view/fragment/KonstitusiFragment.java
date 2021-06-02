package com.roma.android.sihmi.view.fragment;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.roma.android.sihmi.model.database.entity.File;
import com.roma.android.sihmi.model.database.entity.Konstituisi;
import com.roma.android.sihmi.model.database.interfaceDao.KonstitusiDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.model.network.ApiClient;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.response.GeneralResponse;
import com.roma.android.sihmi.model.response.KonstituisiResponse;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.StateView;
import com.roma.android.sihmi.utils.Tools;
import com.roma.android.sihmi.view.activity.FileDetailActivity;
import com.roma.android.sihmi.view.activity.KonstitusiFormActivity;
import com.roma.android.sihmi.view.activity.MainActivity;
import com.roma.android.sihmi.view.adapter.KonstituisiAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.GridLayoutManager;
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
public class KonstitusiFragment extends Fragment {
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.empty_screen)
    FrameLayout emptyLayout;

    @BindView(R.id.tv_empty)
    TextView tvEmpty;

    StateView stateView;

    MasterService service;
    KonstituisiAdapter adapter;
    List<File> list = new ArrayList<>();
    List<Konstituisi> konstituisiList = new ArrayList<>();

    LiveData<List<Konstituisi>> listLiveData;

    int type;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    AppDb appDb;
    UserDao userDao;
    KonstitusiDao konstitusiDao;

    public KonstitusiFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_konstitusi, container, false);
        ButterKnife.bind(this, v);
        ((MainActivity)getActivity()).setActionBarTitle(getString(R.string.judul_konstitusi));
        setHasOptionsMenu(true);

        appDb = AppDb.getInstance(getContext());
        userDao = appDb.userDao();
        konstitusiDao = appDb.konstitusiDao();
        service = ApiClient.getInstance().getApi();

        stateView = new StateView(v);
        stateView.setEmptyMessage("Konstitusional kosong\nmulai tambahkan sekarang");

        checkPermission();

        type = Constant.LIST;
        initAdapter(type);

        if (Tools.isNonLK()){
            listLiveData = konstitusiDao.getKonstitusiNasional();
        } else if (Tools.isLK() || Tools.isAdmin1() || Tools.isAdmin2() || Tools.isAdmin3()){
            listLiveData = konstitusiDao.getKonstitusiNasAndKoms("%-"+
                    userDao.getUser().getKomisariat()+"-%");
        }
        else if (Tools.isLA1()) {
            listLiveData = konstitusiDao.getKonstitusiNasAndKoms("%-" +
                    userDao.getUser().getCabang());
        } else {
            listLiveData = konstitusiDao.getAllKonstitusi();
        }

        listLiveData.observe(getActivity(), konstituisis -> {
            konstituisiList = konstituisis;
            adapter.updateData(konstituisis);
            checkRvCount();
        });

        // set visibility of FAB based on role
        if (Tools.isAdmin1() || Tools.isSecondAdmin() || Tools.isSuperAdmin() || Tools.isLA1()
            || Tools.isLA2()){
            fab.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.GONE);
        }

        getData(0);

        fab.setOnClickListener(v1 -> startActivityForResult(new Intent(getActivity(), KonstitusiFormActivity.class), Constant.REQUEST_KONSTITUSI));

        return v;
    }

    private void getData(int type){
        stateView.setLoading(true);
        Call<KonstituisiResponse> call = service.getKonstitusi(String.valueOf(type));
        if (Tools.isOnline(getActivity())) {
            Tools.showProgressDialog(getActivity(), getString(R.string.load_data));
            call.enqueue(new Callback<KonstituisiResponse>() {
                @Override
                public void onResponse(Call<KonstituisiResponse> call, Response<KonstituisiResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getStatus().equalsIgnoreCase("ok")) {
                            konstitusiDao.insertKonstituisi(response.body().getData());
                            checkRvCount();
                        } else {
                            Tools.showToast(getActivity(), response.body().getMessage());
                        }
                    } else {
                        Tools.showToast(getActivity(), response.message());
                    }
                    Tools.dissmissProgressDialog();
                }

                @Override
                public void onFailure(Call<KonstituisiResponse> call, Throwable t) {
                    Tools.showToast(getActivity(), t.getMessage());
                    Tools.dissmissProgressDialog();
                }
            });
        } else {
            Tools.showToast(getActivity(), getString(R.string.tidak_ada_internet));
        }
    }

    private void checkRvCount() {
        int rvCount = adapter.getItemCount();
        stateView.checkState(rvCount);
        stateView.setLoading(false);
    }

    private void initAdapter(int type){
        if (type == Constant.LIST){
            initAdapterList();
        } else {
            initAdapterGrid();
        }
    }

    private void initAdapterList(){
        adapter = new KonstituisiAdapter(getActivity(), konstituisiList, (konstituisi, isLongClick) -> {
            if (isLongClick){
                if (allowLongClick()) {
                    if (Tools.isSecondAdmin()){
                        // Nothing
                    } else {
                        Tools.showDialogTindakan(getActivity(), ket -> {
                            if (ket.equals(Constant.UBAH)) {
                                if (allowUpdate(konstituisi)) {
                                    startActivityForResult(new Intent(getActivity(), KonstitusiFormActivity.class).putExtra(Constant.ID_KONSTITUSI, konstituisi.get_id()), Constant.REQUEST_KONSTITUSI);
                                } else {
                                    Tools.showToast(getActivity(), getString(R.string.hak_akses_perbarui));
                                }
                            } else if (ket.equals(Constant.HAPUS)) {
                                if (allowDelete()) {
                                    Tools.confirmDelete(getActivity(), ket1 -> {
                                        if (ket1.equals(Constant.HAPUS)) {
                                            deleteData(konstituisi.get_id());
                                        }
                                    });
                                } else {
                                    Tools.showToast(getActivity(), getString(R.string.hak_akses_hapus));
                                }
                            }
                        });
                    }
                }
            } else {
                startActivityForResult(new Intent(getActivity().getApplicationContext(), FileDetailActivity.class)
                                .putExtra("file", konstituisi.getFile())
                                .putExtra("_id", konstituisi.get_id())
                                .putExtra("judul", konstituisi.getNama()),
                        Constant.REQUEST_KONSTITUSI);
            }
        }, Constant.LIST);
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerview.setAdapter(adapter);
    }

    private boolean allowLongClick(){
        if (Tools.isAdmin1() || Tools.isLA2() || Tools.isLA1() || Tools.isSecondAdmin() || Tools.isSuperAdmin()){
            return true;
        } else {
            return false;
        }
    }

    private boolean allowUpdate (Konstituisi konstituisi){
        boolean allow;
        if (Tools.isAdmin1() && konstituisi.getType().contains("4-"+userDao.getUser().getKomisariat())){
            allow = true;
        }
        else if (konstituisi.getType().contains(userDao.getUser().getCabang())) {
            allow = true;
        } else if (Tools.isLA2() || Tools.isSecondAdmin() || Tools.isSuperAdmin()){
            allow = true;
        } else {
            allow = false;
        }
        return allow;
    }

    private boolean allowDelete(){
        boolean allow;
        if (Tools.isSecondAdmin() || Tools.isSuperAdmin()) {
            allow = true;
        } else {
            allow = false;
        }
        return allow;
    }

    private void initAdapterGrid(){
        adapter = new KonstituisiAdapter(getActivity(), konstituisiList, (konstituisi, isLongClick) -> {
            if (isLongClick){
                if (Tools.isAdmin1() || Tools.isLA2() || Tools.isSecondAdmin() || Tools.isSuperAdmin()) {
                    Tools.showDialogTindakan(getActivity(), ket -> {
                        if (ket.equals(Constant.UBAH))
                        {
                            if (Tools.isAdmin1()){
                                if (konstituisi.getType().toLowerCase().contains(userDao.getUser().getKomisariat())){
                                    startActivityForResult(new Intent(getActivity(), KonstitusiFormActivity.class).putExtra(Constant.ID_KONSTITUSI, konstituisi.get_id()), Constant.REQUEST_KONSTITUSI);
                                } else {
                                    Tools.showToast(getActivity(), getString(R.string.hak_akses_perbarui));
                                }
                            } else {
                                startActivityForResult(new Intent(getActivity(), KonstitusiFormActivity.class).putExtra(Constant.ID_KONSTITUSI, konstituisi.get_id()), Constant.REQUEST_KONSTITUSI);
                            }
                        }

                        else if (ket.equals(Constant.HAPUS))
                        {
                            if (Tools.isSecondAdmin() || Tools.isSuperAdmin()) {
                                Tools.confirmDelete(getActivity(), ket1 -> {
                                    if (ket1.equals(Constant.HAPUS)) {
                                        deleteData(konstituisi.get_id());
                                    }
                                });
                            } else {
                                Tools.showToast(getActivity(), getString(R.string.hak_akses_hapus));
                            }
                        }
                    });
                }
            } else {
                startActivityForResult(new Intent(getActivity().getApplicationContext(), FileDetailActivity.class)
                                .putExtra("file", konstituisi.getFile())
                                .putExtra("_id", konstituisi.get_id()),
                        Constant.REQUEST_KONSTITUSI);
            }
        }, Constant.GRID);
        recyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        recyclerview.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        checkPermission();
    }

    private void checkPermission(){
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if(ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

        }else{
            ActivityCompat.requestPermissions(getActivity(),
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
//                            mLocationPermissionsGranted = false;
                            return;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_KONSTITUSI && resultCode == Activity.RESULT_OK){
            getData(0);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main, menu);
        MenuItem gridItem = menu.findItem(R.id.action_list);
        gridItem.setVisible(true);
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
                    List<Konstituisi> list;
                    if (Tools.isNonLK()){
                        list = konstitusiDao.getSearchKonstituisiNasional("%"+newText+"%");
                    } else if (Tools.isLK() || Tools.isAdmin1() || Tools.isAdmin2() || Tools.isAdmin3() || Tools.isLA1()){
                        list = konstitusiDao.getSearchKonstituisiNasAndKoms("%"+userDao.getUser().getKomisariat(), "%"+newText+"%");
                    } else {
                        list = konstitusiDao.getSearchKonstituisi("%"+newText+"%");
                    }
                    adapter.updateData(list);
                    return true;
                }
            });
        } else if (id == R.id.action_list) {
            if (type == Constant.LIST){
                item.setIcon(getResources().getDrawable(R.drawable.ic_apps_white));
                type = Constant.GRID;
                initAdapter(type);
            } else {
                item.setIcon(getResources().getDrawable(R.drawable.ic_format_list_white));
                type = Constant.LIST;
                initAdapter(type);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteData(String id){
        Call<GeneralResponse> call = service.deleteKonstitusi(Constant.getToken(), id);

        if (Tools.isOnline(getActivity())) {
            Tools.showProgressDialog(getActivity(), getString(R.string.delete_konstitusi));
            call.enqueue(new Callback<GeneralResponse>() {
                @Override
                public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getStatus().equalsIgnoreCase("ok")) {
                            konstitusiDao.deleteKonstituisiById(id);
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
}
