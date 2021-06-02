package com.roma.android.sihmi.core;

import android.app.Application;

import com.roma.android.sihmi.utils.Constant;

public class CoreApplication extends Application {
    private static CoreApplication INSTANCE;
    private Constant constant;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        constant = new Constant(this);
    }

    public static CoreApplication get(){
        return INSTANCE;
    }

    public Constant getConstant() {
        return constant;
    }
}
