package com.roma.android.sihmi.view.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.material.tabs.TabLayout;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.interfaceDao.SejarahDao;
import com.roma.android.sihmi.model.network.ApiClient;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.response.SejarahResponse;
import com.roma.android.sihmi.view.activity.MainActivity;
import com.roma.android.sihmi.view.adapter.ViewPagerAdapter;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class TentangFragment extends Fragment {
    @BindView(R.id.frameTab)
    FrameLayout frameLayout;
    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    MasterService service;
    AppDb appDb;
    SejarahDao sejarahDao;

    public TentangFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tab, container, false);
        ButterKnife.bind(this, v);

        ((MainActivity) getActivity()).setToolBar(getString(R.string.judul_tentang_kami));

        service = ApiClient.getInstance().getApi();
        appDb = AppDb.getInstance(getContext());
        sejarahDao = appDb.sejarahDao();

        setupViewPager(viewPager);
        tabs.setupWithViewPager(viewPager);
//
        getData();

        return v;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new TentangKamiFragment(1), "HMI");
        adapter.addFragment(new TentangKamiFragment(2), getString(R.string.cabang));
        adapter.addFragment(new TentangKamiFragment(3), getString(R.string.komisariat));
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                session.setPosIncident(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void getData(){
//        Tools.showProgressDialog(getContext(), "Load Data...");
        Call<SejarahResponse> call = service.getSejarah(0);
        call.enqueue(new Callback<SejarahResponse>() {
            @Override
            public void onResponse(Call<SejarahResponse> call, Response<SejarahResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        sejarahDao.insertSejarah(response.body().getData());
//                        Tools.dissmissProgressDialog();
                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<SejarahResponse> call, Throwable t) {

            }
        });
    }
}
