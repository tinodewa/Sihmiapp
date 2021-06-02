package com.roma.android.sihmi.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Contact;
import com.roma.android.sihmi.model.database.interfaceDao.ContactDao;
import com.roma.android.sihmi.model.database.interfaceDao.LevelDao;
import com.roma.android.sihmi.model.database.interfaceDao.TrainingDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.model.network.ApiClient;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.response.ContactResponse;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Tools;
import com.roma.android.sihmi.view.adapter.ContactAdapter;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("NonConstantResourceId")
public class ContactActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    MasterService service;
    ContactAdapter adapter;

    AppDb appDb;
    UserDao userDao;
    LevelDao levelDao;
    ContactDao contactDao;
    TrainingDao trainingDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        ButterKnife.bind(this);

        toolbar.setTitle(getString(R.string.cari_kontak).toUpperCase());
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        service = ApiClient.getInstance().getApi();
        appDb = AppDb.getInstance(this);
        userDao = appDb.userDao();
        levelDao = appDb.levelDao();
        contactDao = appDb.contactDao();
        trainingDao = appDb.trainingDao();
        initAdapter(contactDao.getAllListContact("%"+userDao.getUser().get_id()+"%"));
        getContact();
    }

    private void getContact(){
        Call<ContactResponse> call = service.getContact(Constant.getToken());
        if (Tools.isOnline(this)) {
            Tools.showProgressDialog(this, "Load Data...");
            call.enqueue(new Callback<ContactResponse>() {
                @Override
                public void onResponse(@NonNull Call<ContactResponse> call, @NonNull Response<ContactResponse> response) {
                    if (response.isSuccessful()) {
                        ContactResponse body = response.body();
                        if (body != null) {
                            if (body.getStatus().equalsIgnoreCase("success")) {
                                List<Contact> contacts = body.getData();
                                for (int i = 0; i < contacts.size() ; i++) {
                                    Contact c = contacts.get(i);
                                    Contact thisContact = contactDao.getContactById(c.get_id());
                                    if (thisContact != null) {
                                        c.setBisukan(thisContact.isBisukan());
                                    }
                                    c.setId_level(levelDao.getPengajuanLevel(c.getId_roles()));
                                    c.setTahun_daftar(Tools.getYearFromMillis(Long.parseLong(c.getTanggal_daftar())));

                                    String tanggalLk1 = c.getTanggal_lk1();
                                    if (tanggalLk1 != null) {
                                        String tahunLk1 = tanggalLk1.split("-")[2];
                                        c.setTahun_lk1(tahunLk1);
                                    }

                                    contactDao.insertContact(c);
                                }
                            } else {
                                Toast.makeText(ContactActivity.this, "" + body.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(ContactActivity.this, "" + response.message(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ContactActivity.this, "" + response.message(), Toast.LENGTH_SHORT).show();
                    }
                    Tools.dissmissProgressDialog();
                }

                @Override
                public void onFailure(@NonNull Call<ContactResponse> call, @NonNull Throwable t) {
                    Toast.makeText(ContactActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Tools.dissmissProgressDialog();
                }
            });
        } else{
            Toast.makeText(this, "Tidak Ada Internet!", Toast.LENGTH_SHORT).show();
        }
    }

    private void initAdapter(List<Contact> list){
        adapter = new ContactAdapter(this, list, contact -> {
            startActivity(new Intent(ContactActivity.this, ChatActivity.class)
                    .putExtra("nama", contact.getFullName()).putExtra("iduser", contact.get_id()));
            finish();
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(R.string.cari));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                Toast.makeText(ContactActivity.this, query, Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.updateData(contactDao.getSearchListContact("%"+userDao.getUser().get_id()+"%", "%"+newText+"%"));
//                adapter.updateData(contactDao.getSearchListContactKomisariat("%"+userDao.getUser().get_id()+"%", "%"+newText+"%"));
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
