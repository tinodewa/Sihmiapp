package com.roma.android.sihmi.view.fragment;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Chat;
import com.roma.android.sihmi.model.database.entity.Chatting;
import com.roma.android.sihmi.model.database.entity.Contact;
import com.roma.android.sihmi.model.database.interfaceDao.ChatingDao;
import com.roma.android.sihmi.model.database.interfaceDao.ContactDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.model.network.ApiClient;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.response.ContactSingleResponse;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.StateView;
import com.roma.android.sihmi.utils.Tools;
import com.roma.android.sihmi.view.activity.ChatActivity;
import com.roma.android.sihmi.view.activity.ContactActivity;
import com.roma.android.sihmi.view.adapter.ChatAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalFragment extends Fragment {
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.empty_screen)
    FrameLayout emptyLayout;

    @BindView(R.id.tv_empty)
    TextView tvEmpty;

    StateView stateView;

    List<Chatting> chattings = new ArrayList<>();

    ChatAdapter adapter;

    DatabaseReference referenceChats;
    ValueEventListener eventListenerChats;

    AppDb appDb;
    UserDao userDao;
    ContactDao contactDao;
    ChatingDao chatingDao;


    public PersonalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_konstitusi, container, false);
        ButterKnife.bind(this, v);
        setHasOptionsMenu(true);

        appDb = AppDb.getInstance(getContext());
        userDao = appDb.userDao();
        contactDao = appDb.contactDao();
        chatingDao = appDb.chatingDao();

        stateView = new StateView(v);
        stateView.setEmptyMessage("Percakapan kosong,\nmulai perkapanmu sekarang");

        getListChating();

        initAdapter();

        fab.setOnClickListener(v1 -> startActivity(new Intent(getActivity().getApplicationContext(), ContactActivity.class)));

        refreshLayout.setRefreshing(false);
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(false);
        });

        chatingDao.getListChating().observe(getActivity(), chatings -> {
            adapter.updateData(chatings);
            checkRvCount();
        });

        return v;
    }

    private void checkRvCount() {
        int rvCount = adapter.getItemCount();
        stateView.checkState(rvCount);
        stateView.setLoading(false);
    }

    private void initAdapter(){
        adapter = new ChatAdapter(getContext().getApplicationContext(), chattings, (chating, isLongCLick) -> {
            Contact contact = contactDao.getContactById(chating.get_id());
            if (!isLongCLick){
                startActivity(new Intent(getContext().getApplicationContext(), ChatActivity.class)
                        .putExtra("nama", contact.getFullName()).putExtra("iduser", contact.get_id()));

            } else {
                Tools.showDialogRb(getActivity(), chating.get_id(), adapter);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext().getApplicationContext()));
        recyclerView.setAdapter(adapter);
        refreshLayout.setRefreshing(false);
    }

    private void getListChating() {
        Log.e("roma", "onDataChange getListChating: Chats_v2");
        referenceChats = FirebaseDatabase.getInstance().getReference("Chats_v2");
        eventListenerChats = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("roma", "onDataChange: personalfragment 187");
                stateView.setLoading(true);
                new getListChatingAsync().execute(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        referenceChats.addValueEventListener(eventListenerChats);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            SearchView searchView = (SearchView) item.getActionView();
            searchView.setQueryHint(getString(R.string.cari));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    chattings = chatingDao.getSearchChating("%"+newText+"%");
                    adapter.updateData(chattings);
                    return false;
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private class getListChatingAsync extends AsyncTask<DataSnapshot, Void, Boolean> {

        @Override
        protected Boolean doInBackground(DataSnapshot... dataSnapshots) {
            try {
                String myuserid = userDao.getUser().get_id();
                for (DataSnapshot snapshot : dataSnapshots[0].getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat != null && (chat.getSender().equals(myuserid) || chat.getReceiver().equals(myuserid))) {
                        String userId;
                        if (chat.getSender().equals(myuserid)) {
                            userId = chat.getReceiver();
                        }
                        else {
                            userId = chat.getSender();
                        }

                        Chatting thisChatting = chatingDao.getChatingById(userId);

                        if (thisChatting == null || chat.getTime() > thisChatting.getTime_message()) {
                            int unreadCount = (thisChatting != null) ? thisChatting.getUnread() : 0;
                            if (chat.getSender().equals(userId) && chat.isNotSeen()) {
                                unreadCount++;
                            }
                            String lastMsg;
                            if (chat.getType().equalsIgnoreCase(Constant.IMAGE)) {
                                lastMsg = Constant.IMAGE;
                            } else if (chat.getType().equalsIgnoreCase(Constant.DOCUMENT)) {
                                lastMsg = Constant.DOCUMENT;
                            } else {
                                lastMsg = chat.getMessage();
                            }

                            Contact c = contactDao.getContactById(userId);
                            boolean isGroupChat = userId.contains(" ");
                            if (!isGroupChat) {
                                if (c == null) {
                                    c = new Contact("Unknown", null);
                                    c.set_id(userId);
                                    c.setNama_belakang("");

                                    getContact(userId);
                                    contactDao.insertContact(c);
                                }
                                chatingDao.insertChating(new Chatting(userId, c.getFullName(),
                                        chat.getReceiver() + "split100x" + lastMsg,
                                        chat.getTime(), unreadCount));
                            }
                        }
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
                return true;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            checkRvCount();
            super.onPostExecute(aBoolean);
        }
    }

    private void getContact(String userId) {
        MasterService service = ApiClient.getInstance().getApi();
        Call<ContactSingleResponse> call = service.getContact(Constant.getToken(), userId);
        call.enqueue(new Callback<ContactSingleResponse>() {
            @Override
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
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<ContactSingleResponse> call, Throwable t) {

            }
        });
    }

}
