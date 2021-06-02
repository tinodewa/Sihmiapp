package com.roma.android.sihmi.ViewModel;

import android.content.Context;
import android.util.Log;

import com.github.mikephil.charting.data.Entry;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Contact;
import com.roma.android.sihmi.model.database.entity.DataGrafik;
import com.roma.android.sihmi.model.database.entity.MasterCount;
import com.roma.android.sihmi.model.network.ApiClient;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.response.ContactResponse;
import com.roma.android.sihmi.model.response.MasterResponse;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Query;
import com.roma.android.sihmi.utils.Tools;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.sqlite.db.SimpleSQLiteQuery;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class ReportLK1ViewModel extends ViewModel {
    private Context context;
    private AppDb appDb;
    private MutableLiveData<Boolean> loading;
    private int yearNow;
    private MasterService service;
    private String TAG;
    private boolean loadingContact;
    private boolean loadingMaster;

    public void init(Context context) {
        this.context = context;
        appDb = AppDb.getInstance(context);
        yearNow = Integer.parseInt(Tools.getYearFromMillis(System.currentTimeMillis()));
        loading = new MutableLiveData<>();
        loading.setValue(true);
        service = ApiClient.getInstance().getApi();
        TAG = "Report LK1";
        loadingContact = true;
        loadingMaster = true;

        getData();
    }

    public LiveData<Boolean> isLoading() {
        return loading;
    }

    public List<Entry> getKaderChartDataset(String commissariat) {
        List<Entry> dataSetEntry = new ArrayList<>();

        for (int i = yearNow - 4, j = 0; i <= yearNow; i++, j++) {
            int count = appDb.contactDao().countRawQueryContact(
                    new SimpleSQLiteQuery(Query.countReportKaderLK1(i, commissariat)));
            dataSetEntry.add(new Entry(j, (float) count));
        }

        return dataSetEntry;
    }

    public List<String> getKaderChartLabel() {
        List<String> labelList = new ArrayList<>();

        for (int i = yearNow - 4; i <= yearNow; i++) {
            labelList.add(String.valueOf(i));
        }

        return labelList;
    }

    public List<DataGrafik> getListKader(String commissariat) {
        List<DataGrafik> dataGrafikList = new ArrayList<>();


        for (int i = yearNow; i >= yearNow-4; i--) {
            int countL = appDb.contactDao().countRawQueryContact(
                    new SimpleSQLiteQuery(Query.countReportKaderLK1Male(i, commissariat))
            );

            int countP = appDb.contactDao().countRawQueryContact(
                    new SimpleSQLiteQuery(Query.countReportKaderLK1Female(i, commissariat))
            );

            dataGrafikList.add(new DataGrafik(i, countL+countP, countP, countL));
        }

        return dataGrafikList;
    }

    public   List<MasterCount> getMasterList() {
        List<MasterCount> masterCounts = new ArrayList<>();

        masterCounts.add(appDb.masterDao().getMasterBadkoCount());
        masterCounts.add(appDb.masterDao().getMasterCabangCount());
        masterCounts.add(appDb.masterDao().getMasterKomisariatCount());

        return masterCounts;
    }

    private void getData() {
        getContact();
        getMaster();
    }

    private void getContact() {
        Call<ContactResponse> call = service.getContact(Constant.getToken());
        call.enqueue(new Callback<ContactResponse>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<ContactResponse> call, Response<ContactResponse> response) {
                if (response.isSuccessful()) {
                    ContactResponse body = response.body();

                    if (body != null && body.getStatus().equalsIgnoreCase("success")) {
                        List<Contact> contacts = body.getData();
                        for (int i = 0; i < contacts.size() ; i++) {
                            Contact c = contacts.get(i);
                            Contact thisContact = appDb.contactDao().getContactById(c.get_id());
                            if (thisContact != null) {
                                c.setBisukan(thisContact.isBisukan());
                            }
                            c.setId_level(appDb.levelDao().getPengajuanLevel(c.getId_roles()));
                            c.setTahun_daftar(Tools.getYearFromMillis(Long.parseLong(c.getTanggal_daftar())));

                            String tanggalLk1 = c.getTanggal_lk1();
                            if (tanggalLk1 != null) {
                                String tahunLk1 = tanggalLk1.split("-")[2];
                                c.setTahun_lk1(tahunLk1);
                            }

                            appDb.contactDao().insertContact(c);
                        }
                    }
                    else {
                        if (body != null) Tools.showToast(context, body.getMessage());
                    }
                }
                else {
                    Tools.showToast(context, response.message());
                }

                loadingContact = true;
                loading.setValue(loadingMaster);
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<ContactResponse> call, Throwable t) {
                Tools.showToast(context, t.getMessage());
                Log.e(TAG, TAG + " " + t.getMessage());

                loadingContact = false;
                loading.setValue(loadingMaster);
            }
        });
    }

    private void getMaster() {
        Call<MasterResponse> call = service.getMaster(Constant.getToken(), "0");
        call.enqueue(new Callback<MasterResponse>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<MasterResponse> call, Response<MasterResponse> response) {
                if (response.isSuccessful()){
                    MasterResponse body = response.body();
                    if (body != null && body.getStatus().equalsIgnoreCase("ok")){
                        appDb.masterDao().insertMaster(body.getData());
                    }
                    else {
                        if (body != null) Tools.showToast(context, body.getMessage());
                    }
                }
                else {
                    Tools.showToast(context, response.message());
                }

                loadingMaster = false;
                loading.setValue(loadingContact);
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<MasterResponse> call, Throwable t) {
                Tools.showToast(context, t.getMessage());
                Log.e(TAG, TAG + " " + t.getMessage());

                loadingMaster = true;
                loading.setValue(loadingContact);
            }
        });
    }
}
