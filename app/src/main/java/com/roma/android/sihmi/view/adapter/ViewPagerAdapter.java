package com.roma.android.sihmi.view.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.roma.android.sihmi.R;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        return mFragmentList.get(i);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    public View getTabView(Context context, int position) {
        // Given you have a custom layout in `res/layout/custom_tab.xml` with a TextView and ImageView
        View v = LayoutInflater.from(context).inflate(R.layout.notification_badge, null);
        TextView tv = (TextView) v.findViewById(R.id.text);
        TextView tv_title = (TextView) v.findViewById(R.id.tv_title);

        int notif;
        if (getPageTitle(position).equals(context.getString(R.string.permintaan_admin))){
            notif = 10;
        } else if (getPageTitle(position).equals(context.getString(R.string.permintaan_anggota))){
            notif = 20;
        }  else if (getPageTitle(position).equals(context.getString(R.string.anggota))){
            notif = 30;
        } else if (getPageTitle(position).equals(context.getString(R.string.admin))){
            notif = 40;
        } else {
            notif = 100;
        }
        tv_title.setText(getPageTitle(position));
        tv.setText(String.valueOf(notif));


        if (notif > 0){
            tv.setVisibility(View.GONE);
        } else {
            tv.setVisibility(View.VISIBLE);
        }
        return v;
    }


}
