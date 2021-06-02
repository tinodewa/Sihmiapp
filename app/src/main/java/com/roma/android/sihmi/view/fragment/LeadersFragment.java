package com.roma.android.sihmi.view.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.utils.Tools;
import com.roma.android.sihmi.view.activity.MainActivity;
import com.roma.android.sihmi.view.adapter.ViewPagerAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class LeadersFragment extends Fragment {
    @BindView(R.id.frameTab)
    FrameLayout frameLayout;
    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.appBar)
    AppBarLayout appBarLayout;
    MasterService service;

    public LeadersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tab, container, false);
        ButterKnife.bind(this, v);
        setHasOptionsMenu(true);

        ((MainActivity) getActivity()).setToolBar(getString(R.string.judul_ketua_umum));

        if (Tools.isNonLK()){
            appBarLayout.setVisibility(View.GONE);
        }

        setupViewPager(viewPager);
        tabs.setupWithViewPager(viewPager);

        return v;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        if (Tools.isNonLK()){
            adapter.addFragment(new LeaderFragment(1), getString(R.string.nasional));
        } else {
            adapter.addFragment(new LeaderFragment(1), getString(R.string.nasional));
            adapter.addFragment(new LeaderFragment(2), getString(R.string.cabang));
            adapter.addFragment(new LeaderFragment(3), getString(R.string.komisariat));
        }


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

}
