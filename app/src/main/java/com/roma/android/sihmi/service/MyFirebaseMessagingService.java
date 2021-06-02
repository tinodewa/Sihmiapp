package com.roma.android.sihmi.service;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.roma.android.sihmi.core.CoreApplication;
import com.roma.android.sihmi.helper.NotificationHelper;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Contact;
import com.roma.android.sihmi.model.database.entity.notification.Token;
import com.roma.android.sihmi.model.database.interfaceDao.ContactDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.model.network.ApiClient;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.response.ContactSingleResponse;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Tools;

import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "Firebase";
    public static String GROUP_TOPIC_PREFIX = "com.roma.android.sihmi.GROUP.";
    public static String AGENDA_TOPIC = "com.roma.android.sihmi.AGENDA";

    UserDao userDao;
    ContactDao contactDao;
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        try {
            Log.d(TAG, "From: message " + remoteMessage.getFrom() + " - " + remoteMessage.getData().get("title") + " - " + remoteMessage.getData().get("body"));

            userDao = CoreApplication.get().getConstant().getUserDao();
            contactDao = CoreApplication.get().getConstant().getContactDao();

            String sented = remoteMessage.getData().get("sented");
            String user = remoteMessage.getData().get("user");

            SharedPreferences preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
            String currentUser = preferences.getString("currentuser", "none");


            Log.d(TAG, "onMessageReceived: Message " + sented + " - " + user + " - " + currentUser + " - " + userDao.getUser().get_id());
            Contact contact = contactDao.getContactById(user);

            if (user != null && contact != null && !user.equals(userDao.getUser().get_id())) {
                NotificationHelper.sendNotification(remoteMessage, contact, this);
            }
            else if (contact == null){
                getContact(user, remoteMessage);
            }
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    private void getContact(String sender_id, RemoteMessage remoteMessage) {
        AppDb appDb = CoreApplication.get().getConstant().getAppDb();
        MasterService service = ApiClient.getInstance().getApi();
        Call<ContactSingleResponse> call = service.getContact(Constant.getToken(), sender_id);
        call.enqueue(new Callback<ContactSingleResponse>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<ContactSingleResponse> call, Response<ContactSingleResponse> response) {
                if (response.isSuccessful()) {
                    ContactSingleResponse body = response.body();
                    if (body != null && body.getStatus().equalsIgnoreCase("success")) {
                        Contact c = body.getData();
                        c.setId_level(appDb.levelDao().getPengajuanLevel(c.getId_roles()));
                        c.setTahun_daftar(Tools.getYearFromMillis(Long.parseLong(c.getTanggal_daftar())));

                        String tanggalLk1 = c.getTanggal_lk1();
                        if (tanggalLk1 != null) {
                            String tahunLk1 = tanggalLk1.split("-")[2];
                            c.setTahun_lk1(tahunLk1);
                        }

                        contactDao.insertContact(c);
                        NotificationHelper.sendNotification(remoteMessage, c,
                                MyFirebaseMessagingService.this);
                    }
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<ContactSingleResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token = new Token(s);
        reference.child(userDao.getUser().get_id()).setValue(token);
        Log.d(TAG, "onNewToken: Message "+s);
    }

}
