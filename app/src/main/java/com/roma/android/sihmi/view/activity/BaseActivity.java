package com.roma.android.sihmi.view.activity;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.facebook.stetho.Stetho;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Tools;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseMessaging.getInstance().setAutoInitEnabled(false);
        Stetho.initializeWithDefaults(this);
        initStetho();
        initTheme();

        adjustFontScale(getResources().getConfiguration(), Constant.getFontSize());

        String fontPathStr;
        if (!Constant.getFontName().equalsIgnoreCase("Default")) {
            fontPathStr = "fonts/"+Constant.getFontName()+".ttf";
        }
        else {
            fontPathStr = null;
        }

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()

                                .setDefaultFontPath(fontPathStr)
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

    }

    private void initTheme(){
        try {
            if (Constant.loadNightModeState()) {
                setTheme(R.style.MainActivityThemeDark);
            } else {
                setTheme(R.style.AppTheme);
            }
        } catch (NullPointerException e){
            setTheme(R.style.AppTheme);
        }
    }

    private void initStetho(){
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(
                                Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(
                                Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void location() {
        String[] listPermission = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (requestPermission(listPermission)){
                requestLocation();
            }else{
                requestPermission(listPermission);
            }
        }else{
            requestPermission(listPermission);
        }
    }


    public boolean requestPermission(String[] permission){
        final boolean[] returned = {false};
        Dexter.withActivity(BaseActivity.this).withPermissions(permission)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        returned[0] = true;
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();

                        returned[0] = false;
                    }
                })
                .withErrorListener(error -> Tools.showToast(BaseActivity.this,"Go to Settings and Grant the permission to use this feature."))
                .onSameThread()
                .check();
        return returned[0];
    }

    private void requestLocation(){
//        1 Create a LocationRequest as per your wish
        LocationRequest mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)
                .setFastestInterval(1000);

//        2 Create Location Builder
        LocationSettingsRequest.Builder settingsBuilder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        settingsBuilder.setAlwaysShow(true);

//        3 Get LocationSettingsResponse Task using following code
        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(this)
                .checkLocationSettings(settingsBuilder.build());

//        4 Add a OnCompleteListener to get the result from the Task.When the Task completes, the client can check the location settings by looking at the status code from the LocationSettingsResponse object.
        result.addOnCompleteListener(task -> {
            try {
                task.getResult(ApiException.class);
            } catch (ApiException ex) {
                switch (ex.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            ResolvableApiException resolvableApiException =
                                    (ResolvableApiException) ex;
                            resolvableApiException
                                    .startResolutionForResult(BaseActivity.this,
                                            102);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                        break;
                }
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    public  void adjustFontScale(Configuration configuration, float scale) {
        configuration.fontScale = scale;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        metrics.scaledDensity = configuration.fontScale * metrics.density;
        getBaseContext().getResources().updateConfiguration(configuration, metrics);

    }
}
