package com.roma.android.sihmi.view.fragment;


import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.material.tabs.TabLayout;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.view.activity.MainActivity;
import com.roma.android.sihmi.view.adapter.ViewPagerAdapter;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {
    @BindView(R.id.frameTab)
    FrameLayout frameLayout;
    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tab, container, false);
        ((MainActivity)getActivity()).setActionBarTitle("SIHMI");
        ButterKnife.bind(this, v);

        setupViewPager(viewPager);
        tabs.setupWithViewPager(viewPager);
        if (tabs.getTabCount() > 0){
            tabs.getTabAt(0).setIcon(R.drawable.ic_chat);
            tabs.getTabAt(1).setIcon(R.drawable.ic_question_answer);
            tabs.getTabAt(0).getIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
            tabs.getTabAt(1).getIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        }

        return v;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new PersonalFragment(), "");
        adapter.addFragment(new GroupFragment(), "");
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

}
