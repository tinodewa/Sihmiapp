package com.roma.android.sihmi.view.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.AboutUs;
import com.roma.android.sihmi.model.database.interfaceDao.AboutUsDao;
import com.roma.android.sihmi.model.network.ApiClient;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.response.AboutUsResponse;
import com.roma.android.sihmi.model.response.GeneralResponse;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.StateView;
import com.roma.android.sihmi.utils.Tools;
import com.roma.android.sihmi.view.activity.HelpFormActivity;
import com.roma.android.sihmi.view.activity.MainActivity;
import com.roma.android.sihmi.view.adapter.HelpAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
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
public class BantuanFragment extends Fragment implements HelpAdapter.itemClickListener {
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.fab_add)
    FloatingActionButton fab;
    @BindView(R.id.empty_screen)
    FrameLayout emptyLayout;
    List<AboutUs> list = new ArrayList<>();
    HelpAdapter adapter;

    AboutUsDao aboutUsDao;
    MasterService service;

    private StateView stateView;

    public BantuanFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for getActivity() fragment
        View v = inflater.inflate(R.layout.fragment_agenda, container, false);
        ButterKnife.bind(this, v);
        setHasOptionsMenu(true);
        ((MainActivity) getActivity()).setToolBar(getString(R.string.judul_bantuan));

        Log.d("hairoma", "onCreateView: " + Constant.SUPER_ADMIN + " ----- " + Constant.getIdRoles());

        stateView = new StateView(v);
        stateView.setEmptyMessage("Bantuan belum ada");

        service = ApiClient.getInstance().getApi();
        aboutUsDao = AppDb.getInstance(getContext()).aboutUsDao();

        adapter = new HelpAdapter(getActivity(), list, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        aboutUsDao.getAllAboutUs().observe(getActivity(), aboutUses -> {
            adapter.updateData(aboutUses);
            stateView.checkState(aboutUses.size());
            stateView.setLoading(false);
        });

        getData();

        if (Tools.isSuperAdmin() || Tools.isSecondAdmin()) {
            fab.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.GONE);
        }
        fab.setOnClickListener(v1 -> startActivityForResult(new Intent(getActivity().getApplicationContext(), HelpFormActivity.class).putExtra(HelpFormActivity.IS_NEW, true), Constant.REQUEST_BANTUAN));
        return v;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_BANTUAN && resultCode == Activity.RESULT_OK) {
            getData();
        }
    }

    private void showDialog(String id) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View dialogView = inflater.inflate(R.layout.dialog_tindakan, null);
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(dialogView)
                .create();
        dialog.show();
        dialogView.findViewById(R.id.tv_ubah).setOnClickListener(v -> {
            dialog.dismiss();
            startActivityForResult(new Intent(getActivity().getApplicationContext(), HelpFormActivity.class)
                    .putExtra(HelpFormActivity.IS_NEW, false).putExtra(HelpFormActivity.ID_HELP, id), Constant.REQUEST_BANTUAN);
        });
        dialogView.findViewById(R.id.tv_hapus).setOnClickListener(v ->
                Tools.deleteDialog(getActivity(), getString(R.string.konfirmasi), ket -> {
                    deleteData(id);
                    dialog.dismiss();

                }));
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

    @Override
    public void onItemClick(String id) {
        if (Tools.isSuperAdmin() || Tools.isSecondAdmin()) {
            showDialog(id);
        }
    }

    @Override
    public void onLongItemClick(String id) {
        if (Tools.isSuperAdmin() || Tools.isSecondAdmin()) {
            showDialog(id);
        }
    }

    private void getData(){
        stateView.setLoading(true);
        if (Tools.isOnline(getActivity())) {
            Tools.showProgressDialog(getActivity(), getString(R.string.load_data));
            service.aboutUs().enqueue(new Callback<AboutUsResponse>() {
                @Override
                public void onResponse(Call<AboutUsResponse> call, Response<AboutUsResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getStatus().equalsIgnoreCase("ok")) {
                            aboutUsDao.insertAboutUs(response.body().getData());
                            new Handler().postDelayed(() -> recyclerView.findViewHolderForAdapterPosition(0).itemView.performClick(), 1000);
                        } else {
                            Tools.showToast(getActivity(), response.body().getMessage());
                        }
                    } else {
                        Tools.showToast(getActivity(), response.message());
                    }
                    Tools.dissmissProgressDialog();
                }

                @Override
                public void onFailure(Call<AboutUsResponse> call, Throwable t) {
                    Tools.showToast(getActivity(), t.getMessage());
                    Tools.dissmissProgressDialog();
                }
            });
        } else {
            Tools.showToast(getActivity(), getString(R.string.tidak_ada_internet));
        }
    }

    private void deleteData(String id) {
        service.deleteAboutUs(Constant.getToken(), id).enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (response.isSuccessful()){
                    if (response.body().getStatus().equalsIgnoreCase("ok")){
                        Tools.showToast(getActivity(), getString(R.string.berhasil_hapus));
                        aboutUsDao.deleteAboutById(id);
                    } else {
                        Tools.showToast(getActivity(), getString(R.string.gagal_hapus));
                    }
                } else {
                    Tools.showToast(getActivity(), getString(R.string.gagal_hapus));
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                Tools.showToast(getActivity(), getString(R.string.gagal_hapus));
            }
        });

    }
}
