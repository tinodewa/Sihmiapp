package com.roma.android.sihmi.utils;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.roma.android.sihmi.R;

public class StateView {
    private final FrameLayout frameLayout;
    private final TextView tvEmpty;
    private String emptyMessage;

    public StateView(View v, String emptyMessage) {
        this(v);
        this.emptyMessage = emptyMessage;
    }

    public StateView(View v) {
        frameLayout = v.findViewById(R.id.empty_screen);
        tvEmpty = v.findViewById(R.id.tv_empty);
    }

    public void setVisibility(boolean visibility) {
        if (visibility) {
            frameLayout.setVisibility(View.VISIBLE);
        } else {
            frameLayout.setVisibility(View.INVISIBLE);
        }
    }

    public void checkState(int rvCount) {
        setVisibility(rvCount == 0);
    }

    public void setEmptyMessage(String emptyMessage) {
        this.emptyMessage = emptyMessage;
    }

    public void setLoading(boolean loading) {
        if (loading) {
            tvEmpty.setText(R.string.start_fill);
        }
        else {
            tvEmpty.setText(emptyMessage);
        }
    }
}
