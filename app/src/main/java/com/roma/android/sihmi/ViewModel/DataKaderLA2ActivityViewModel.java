package com.roma.android.sihmi.ViewModel;

import android.content.Context;

import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Master;
import com.roma.android.sihmi.model.database.entity.UserCount;

import java.util.List;

import androidx.lifecycle.ViewModel;

public class DataKaderLA2ActivityViewModel extends ViewModel {
    private AppDb appDb;
    private String year;

    public void init(Context context, String year) {
        this.appDb = AppDb.getInstance(context);
        this.year = year;
    }

    public List<Master> getAllBadko() {
        return appDb.masterDao().getListMasterByType("1");
    }

    public List<UserCount> getCabangUserFromBadko(String idBadko) {
        return appDb.contactDao().getCabangUserCount(idBadko, year);
    }

    public List<UserCount> getKomisariatUserFromCabang(String cabang) {
        return appDb.contactDao().getKomisariatUserCount(cabang, year);
    }
}
