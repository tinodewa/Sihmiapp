package com.roma.android.sihmi.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Account;
import com.roma.android.sihmi.model.database.entity.User;
import com.roma.android.sihmi.model.database.interfaceDao.ContactDao;
import com.roma.android.sihmi.model.database.interfaceDao.GroupChatDao;
import com.roma.android.sihmi.model.database.interfaceDao.LevelDao;
import com.roma.android.sihmi.model.database.interfaceDao.MasterDao;
import com.roma.android.sihmi.model.database.interfaceDao.TrainingDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.response.LoginResponse;
import com.roma.android.sihmi.model.response.ProfileResponse;
import com.roma.android.sihmi.view.activity.LoadDataActivity;

import java.util.List;

import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class LoginProcess {
    Activity activity;
    String username, password, image;
    String token;
    ProgressDialog dialog;
    MasterService service;
    AppDb appDb;

    MasterDao masterDao;
    UserDao userDao;
    LevelDao levelDao;
    TrainingDao trainingDao;
    ContactDao contactDao;
    GroupChatDao groupChatDao;

    public LoginProcess(Activity activity, MasterService service) {
        this.activity = activity;
        this.service = service;
        appDb = AppDb.getInstance(activity);
        masterDao = appDb.masterDao();
        userDao = appDb.userDao();
        levelDao = appDb.levelDao();
        trainingDao = appDb.trainingDao();
        groupChatDao = appDb.groupChatDao();
        contactDao = appDb.contactDao();
    }

    public void login (String username, String password){
        this.username = username;
        this.password = password;

        dialog = new ProgressDialog(activity);
        dialog.setMessage(activity.getString(R.string.loginn));
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();

        Call<LoginResponse> call = service.login(username, password, Build.MODEL);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    LoginResponse body = response.body();
                    if (body != null) {
                        if (body.getStatus().equalsIgnoreCase("ok")) {
                            token = body.getToken();
                            Constant.setToken(token);

                            new Handler().postDelayed(() -> getProfile(), 1000);
                        } else {
                            dialog.dismiss();
                            Tools.showDialogAlert(activity, body.getMessage());
                        }
                    }
                    else {
                        dialog.dismiss();
                        Tools.showToast(activity, activity.getString(R.string.gagal_login));
                    }
                } else {
                    dialog.dismiss();
                    Tools.showToast(activity, activity.getString(R.string.gagal_login));
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                dialog.dismiss();
                Tools.showToast(activity, activity.getString(R.string.gagal_login));
            }
        });
    }

    private void addSavedAccount(String username, String password) {
        List<Account> list = Constant.getLisAccount();

        if (akunAvailable(list)){
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getUsername().equalsIgnoreCase(username)) {
                    list.get(i).setPassword(password);
                }
            }
        } else {
            list.add(new Account(1, username, password, image));
        }

        Constant.setListAccount(list);
    }

    private void getProfile(){
        Call<ProfileResponse> call = service.getProfile(Constant.getToken());
        call.enqueue(new Callback<ProfileResponse>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful()){
                    assert response.body() != null;
                    if (response.body().getStatus().equalsIgnoreCase("ok")){
                        Constant.login();
                        userDao.insertUser(response.body().getData());
                        User user = response.body().getData();
                        Constant.setIdRoles(user.getId_roles());
                        if (user.getImage() != null && !user.getImage().isEmpty()){
                            image = user.getImage();
                        }

                        addSavedAccount(username, password);
                        dialog.dismiss();
                        activity.startActivity(new Intent(activity, LoadDataActivity.class));
                        activity.finish();
                    } else {
                        //error
                        dialog.dismiss();
                        Tools.showToast(activity, response.body().getMessage());
                    }
                } else {
                    //error
                    dialog.dismiss();
                    Tools.showToast(activity, activity.getString(R.string.gagal_login));
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                dialog.dismiss();
                Tools.showToast(activity, activity.getString(R.string.gagal_login));
            }
        });
    }

    private boolean akunAvailable(List<Account> list){
        boolean available = false;
        int size = list.size();
        if (size > 0){
            for (int i = 0; i < size; i++) {
                if (list.get(i).getUsername().equalsIgnoreCase(username)) {
                    available = true;
                    break;
                }
            }
        }
        return available;
    }
}
