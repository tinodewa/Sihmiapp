package com.roma.android.sihmi.view.fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Contact;
import com.roma.android.sihmi.model.database.interfaceDao.ContactDao;
import com.roma.android.sihmi.model.database.interfaceDao.LevelDao;
import com.roma.android.sihmi.model.database.interfaceDao.TrainingDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.model.network.ApiClient;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.response.ContactResponse;
import com.roma.android.sihmi.model.response.GeneralResponse;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.StateView;
import com.roma.android.sihmi.utils.Tools;
import com.roma.android.sihmi.view.adapter.UserAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
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
public class AnggotaFragment extends Fragment {
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.refresh)
    SwipeRefreshLayout refreshLayout;

    UserAdapter adapter;
    List<Contact> contacts;

    MasterService service;

    LiveData<List<Contact>> contactLiveData;

    UserFragment userFragment;

    int page;

    private StateView stateView;

    AppDb appDb;
    ContactDao contactDao;
    UserDao userDao;
    LevelDao levelDao;
    TrainingDao trainingDao;

    public AnggotaFragment() {
        // Required empty public constructor
    }

    public AnggotaFragment(int page) {
        this.page = page;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("hallogesss", "onCreateView: anggotaFragment");
        View v = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, v);
        setHasOptionsMenu(true);

        appDb = AppDb.getInstance(getContext());
        contactDao = appDb.contactDao();
        userDao = appDb.userDao();
        levelDao = appDb.levelDao();
        trainingDao = appDb.trainingDao();

        stateView = new StateView(v, "Belum ada Kader");

        userFragment = ((UserFragment)AnggotaFragment.this.getParentFragment());
        if (Tools.isSuperAdmin()){
            contacts = contactDao.getListAllAnggota();
            contactLiveData = contactDao.getLiveDataListAllAnggota();
        } else if (Tools.isAdmin1()) {
            contacts = contactDao.getListAnggotaByKomisariat(userDao.getUser().getKomisariat());
            contactLiveData = contactDao.getLiveDataListAnggotaByKomisariat(userDao.getUser().getKomisariat());
        }
        else if (Tools.isAdmin2() || Tools.isLA1()) {
            contacts = contactDao.getListAnggotaByCabang(userDao.getUser().getCabang());
            contactLiveData = contactDao.getListLiveDataAnggotaByCabang(userDao.getUser().getCabang());
        }
        else if (Tools.isLA2() || Tools.isSecondAdmin()) {
            contacts = contactDao.getListAnggota();
            contactLiveData = contactDao.getListLiveDataAnggota();
        }
        else if (Tools.isAdmin3()) {
            contacts = contactDao.getListAnggotaByDomisiliCabang(userDao.getUser().getDomisili_cabang());
            contactLiveData = contactDao.getListLiveDataAnggotaByDomisiliCabang(userDao.getUser().getDomisili_cabang());
        }

        contactLiveData.observe(getActivity(), contacts -> {
            adapter.updateData(contacts);
            userFragment.updateTab(page, contacts.size());
            stateView.setLoading(false);
            stateView.checkState(contacts.size());
        });
        contacts = new ArrayList<>();
        initAdapter();
        userFragment.updateTab(page, contacts.size());

        service = ApiClient.getInstance().getApi();

        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(true);
            getContact();
        });

        return v;
    }

    private void initAdapter(){
//        contacts = CoreApplication.get().getAppDb().interfaceDao().getListAdmin();
        adapter = new UserAdapter(getContext().getApplicationContext(), contacts, 3, new UserAdapter.itemClickListener() {
            @Override
            public void onItemClick(Contact contact) {
                Log.d("halloo", "onItemClick: ");
                if (contact.getId_level() != Constant.USER_NON_LK) {
                    changeRoles(contact.get_id());
                }
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext().getApplicationContext()));
        recyclerView.setAdapter(adapter);
    }

    private void changeRoles(String idUser){
        Call<GeneralResponse> call = service.updateUserLevel(Constant.getToken(), idUser, Constant.USER_NON_LK);
        call.enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (response.isSuccessful()){
                    String idRoles = levelDao.getIdRoles(Constant.USER_NON_LK);
                    sendNotif(idUser, "3", Constant.USER_NON_LK);
                    contactDao.updateRolesUser(idUser, idRoles, Constant.USER_NON_LK);
                } else {
                    Tools.showToast(getActivity(), getString(R.string.gagal_update));
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                Tools.showToast(getActivity(), getString(R.string.gagal_update));
            }
        });
    }

    private void sendNotif(String user, String status, int newLevel){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("from", userDao.getUser().get_id());
        hashMap.put("to", user);
        hashMap.put("status", status.trim());
        hashMap.put("time", System.currentTimeMillis());
        hashMap.put("isshow", false);
        hashMap.put("newLevel", newLevel);
        hashMap.put("type", "User");

        databaseReference.child("Notification").push().setValue(hashMap);
    }

    @Override
    public void onResume() {
        super.onResume();
//        getContact();
    }

    private void getContact(){
        stateView.setLoading(true);
        Call<ContactResponse> call = service.getContact(Constant.getToken());
        if (Tools.isOnline(getActivity())) {
//            Tools.showProgressDialog(getActivity(), "Load Data Admin...");
            call.enqueue(new Callback<ContactResponse>() {
                @Override
                public void onResponse(Call<ContactResponse> call, Response<ContactResponse> response) {
//                    Tools.dissmissProgressDialog();
                    if (refreshLayout.isRefreshing()){
                        refreshLayout.setRefreshing(false);
                    }
                    if (response.isSuccessful()) {
                        if (response.body().getStatus().equalsIgnoreCase("success")) {
                            List<Contact> contactsResponse = response.body().getData();
                            for (int i = 0; i < contactsResponse.size() ; i++) {
                                Contact c = contactsResponse.get(i);
                                Contact thisContact = contactDao.getContactById(c.get_id());
                                if (thisContact != null) {
                                    c.setBisukan(thisContact.isBisukan());
                                }
                                c.setId_level(levelDao.getPengajuanLevel(c.getId_roles()));
                                c.setTahun_daftar(Tools.getYearFromMillis(Long.parseLong(c.getTanggal_daftar())));

                                String tanggalLk1 = c.getTanggal_lk1();
                                if (tanggalLk1 != null) {
                                    String tahunLk1 = tanggalLk1.split("-")[2];
                                    c.setTahun_lk1(tahunLk1);
                                }

                                contactDao.insertContact(c);

                            }

                            userFragment.updateTab(page, contacts.size());
                            adapter.updateData(contacts);
//                            initAdapter();
                        } else {
                            Toast.makeText(getContext().getApplicationContext(), "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext().getApplicationContext(), "" + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ContactResponse> call, Throwable t) {
                    if (refreshLayout.isRefreshing()){
                        refreshLayout.setRefreshing(false);
                    }
                    Toast.makeText(getContext().getApplicationContext(), "" + t.getMessage(), Toast.LENGTH_SHORT).show();
//                    Tools.dissmissProgressDialog();
                }
            });
        } else{
            if (refreshLayout.isRefreshing()){
                refreshLayout.setRefreshing(false);
            }
            Toast.makeText(getActivity(), "Tidak Ada Internet!", Toast.LENGTH_SHORT).show();
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
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (newText.isEmpty()){
                        if (Tools.isSuperAdmin()){
                            contacts = contactDao.getListAllAnggota();
                        } else if (Tools.isAdmin1()) {
                            contacts = contactDao.getListAnggotaByKomisariat(userDao.getUser().getKomisariat());
                        }
                        else if (Tools.isAdmin2() || Tools.isLA1()) {
                            contacts = contactDao.getListAnggotaByCabang(userDao.getUser().getCabang());
                        }
                        else if (Tools.isLA2() || Tools.isSecondAdmin()) {
                            contacts = contactDao.getListAnggota();
                        }
                        else if (Tools.isAdmin3()) {
                            contacts = contactDao.getListAnggotaByDomisiliCabang(userDao.getUser().getDomisili_cabang());
                        }
                    } else {
                        if (Tools.isSuperAdmin()){
                            contacts = contactDao.getSearchListAllAnggota("%" + newText + "%");
                        } else if (Tools.isAdmin1()) {
                            contacts = contactDao.getSearchListAnggotaByKomisariat(userDao.getUser().getKomisariat(),"%" + newText + "%");
                        }
                        else if (Tools.isAdmin2() || Tools.isLA1()) {
                            contacts = contactDao.getSearchListAnggotaByCabang(userDao.getUser().getCabang(), "%" + newText + "%");
                        }
                        else if (Tools.isLA2() || Tools.isSecondAdmin()) {
                            contacts = contactDao.getSearchListAnggota("%" + newText + "%");
                        }
                        else if (Tools.isAdmin3()) {
                            contacts = contactDao.getSearchListAnggotaByDomisiliCabang(userDao.getUser().getDomisili_cabang(), "%" + newText + "%");
                        }
                    }
                    adapter.updateData(contacts);
                    return true;
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }
}
