package com.roma.android.sihmi.view.fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.interfaceDao.ContactDao;
import com.roma.android.sihmi.utils.Tools;
import com.roma.android.sihmi.view.activity.MainActivity;
import com.roma.android.sihmi.view.adapter.ViewPagerAdapter;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends Fragment implements Ifragment {
    @BindView(R.id.frameTab)
    FrameLayout frameLayout;
    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    ViewPagerAdapter viewPagerAdapter;

    int position = 0;

    ContactDao contactDao;

    public UserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tab, container, false);
        ((MainActivity)getActivity()).setActionBarTitle("USER");

        ((MainActivity) getActivity()).setToolBar(getString(R.string.judul_user));
        ButterKnife.bind(this, v);

        contactDao = AppDb.getInstance(getContext()).contactDao();

        setupViewPager(viewPager);
        tabs.setupWithViewPager(viewPager);
        int j = tabs.getTabCount()-1;
        Log.d("hallogesss", "onCreateView: userFragment "+j);
        viewPager.setOffscreenPageLimit(j);

        contactDao.getLiveDataListAdmin().observe(getActivity(), contacts -> {

        });

        for (int i = 0; i < tabs.getTabCount(); i++) {
//            changeCount(i, 50);
            TabLayout.Tab tab = tabs.getTabAt(i);
            tab.setCustomView(getTabView(i, 0));
        }

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                position = tab.getPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        return v;
    }

    public void updateTab(int page, int notif){
        TabLayout.Tab tab=tabs.getTabAt(page);
        View view=tab.getCustomView();
        TextView txtCount= (TextView) view.findViewById(R.id.text);
        String countStr = (notif <= 99) ? String.valueOf(notif) : "99+";
        txtCount.setText(countStr);

        if (notif > 0){
            txtCount.setVisibility(View.VISIBLE);
        } else {
            txtCount.setVisibility(View.GONE);
        }
    }

    public View getTabView(int position, int notif) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.notification_badge, null);
        TextView tv = (TextView) v.findViewById(R.id.text);
        TextView tv_title = (TextView) v.findViewById(R.id.tv_title);

        String title = viewPagerAdapter.getPageTitle(position).toString();
        tv_title.setText(title);
        tv.setText(String.valueOf(notif));;

        if (notif > 0){
            tv.setVisibility(View.VISIBLE);
        } else {
            tv.setVisibility(View.GONE);
        }
        return v;
    }

    private void setupViewPager(ViewPager viewPager) {
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        if (Tools.isSuperAdmin()){
            viewPagerAdapter.addFragment(new PermintaanFragment(0), getString(R.string.permintaan_admin_anggota));
            viewPagerAdapter.addFragment(new AdminFragment(1), getString(R.string.admin));
            viewPagerAdapter.addFragment(new AnggotaFragment(2), getString(R.string.anggota));
        } else if (Tools.isAdmin1() || Tools.isAdmin2()){
            viewPagerAdapter.addFragment(new PermintaanFragment(0), getString(R.string.permintaan_anggota));
            viewPagerAdapter.addFragment(new AnggotaFragment(1), getString(R.string.anggota));
        } else {
            viewPagerAdapter.addFragment(new PermintaanFragment(0), getString(R.string.permintaan_admin));
            viewPagerAdapter.addFragment(new AdminFragment(1), getString(R.string.admin));
        }
        viewPager.setAdapter(viewPagerAdapter);

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
    public void cobaInterface(String info) {
        Log.d("halloo", "cobaInterface: UserFragment "+info);
    }

}

interface Ifragment{
    void cobaInterface(String info);
}