package com.roma.android.sihmi.view.fragment;


import android.app.AlertDialog;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Chat;
import com.roma.android.sihmi.model.database.entity.GroupChat;
import com.roma.android.sihmi.model.database.entity.GroupChatSeen;
import com.roma.android.sihmi.model.database.entity.User;
import com.roma.android.sihmi.model.database.interfaceDao.GroupChatDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.utils.StateView;
import com.roma.android.sihmi.utils.Tools;
import com.roma.android.sihmi.view.activity.ChatGroupActivity;
import com.roma.android.sihmi.view.adapter.ChatGroupAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends Fragment {
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.refresh)
    SwipeRefreshLayout refreshLayout;

    ChatGroupAdapter adapter;
    List<GroupChat> list = new ArrayList<>();
    String searchText="";

    DatabaseReference referenceChats, referenceUsers;
    ValueEventListener eventListenerChats, eventListenerUsers;

    private StateView stateView;

    AppDb appDb;
    UserDao userDao;
    GroupChatDao groupChatDao;
    private User user;

    public GroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, v);
        appDb = AppDb.getInstance(getContext());
        userDao = appDb.userDao();
        groupChatDao = appDb.groupChatDao();

        stateView = new StateView(v, "Group masih kosong!");

        setHasOptionsMenu(true);
        user = userDao.getUser();

        if (Tools.isSuperAdmin() || Tools.isSecondAdmin()){
            groupChatDao.getAllGroupLiveData().observe(getActivity(), this::updateAdapterData);
        } else {
            groupChatDao.getAllGroupLiveDataNotSuperAdmin(checkKosong(user.getCabang()), checkKosong(user.getKomisariat()),
                    "Alumni "+checkKosong(user.getDomisili_cabang())).observe(getActivity(), this::updateAdapterData);
        }

        adapter = new ChatGroupAdapter(getActivity(), list, (groupChat, isLongClick) -> {
            if (!isLongClick){
                startActivity(new Intent(getActivity(), ChatGroupActivity.class).putExtra(ChatGroupActivity.NAMA_GROUP, groupChat.getNama()));
                addUserGroupLastSeen(groupChat.getNama(), System.currentTimeMillis());
            } else {
                CharSequence[] grpName = getActivity().getResources().getStringArray(R.array.notfikasi_chat_array);
                int pos = groupChat.isBisukan() ? 1 : 0;

                AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setSingleChoiceItems(grpName, pos, (dialog1, which) -> {
                            boolean bisu = which != 0;
                            groupChat.setBisukan(bisu);
                            groupChatDao.insertGroupChat(groupChat);
                            dialog1.dismiss();
                        })
                        .create();
                dialog.show();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        refreshLayout.setEnabled(false);
        getGroupChatUnRead();

        return v;
    }

    private void updateAdapterData(List<GroupChat> groupChatList) {
        adapter.updateData(groupChatList);
        stateView.checkState(groupChatList.size());
        stateView.setLoading(false);
    }

    private String checkKosong(String nama){
        if (nama != null) {
            if (nama.trim().isEmpty()) {
                return "tidakada";
            } else {
                return nama;
            }
        } else {
            return "tidakada";
        }
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
                    Log.d("checckk", "onQueryTextSubmit: fragment");
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (Tools.isSuperAdmin() || Tools.isSecondAdmin()) {
                        if (newText.trim().length() == 0) {
                            list = groupChatDao.getAllGroupList();
                        }
                        else {
                            list = groupChatDao.getSearchGroupChatByName("%"+newText+"%");
                        }
                    }
                    else {
                        if (newText.trim().length() == 0) {
                            list = groupChatDao.getAllGroupListNotSuperAdmin(user.getCabang(), user.getKomisariat(), "Alumni " + checkKosong(user.getDomisili_cabang()));
                        }
                        else {
                            list = groupChatDao.getSearchGroupChatNotSuperAdmin(user.getCabang(), user.getKomisariat(), "Alumni " + checkKosong(user.getDomisili_cabang()), "%"+newText+"%");
                        }
                    }
                    adapter.updateData(list);
                    return true;
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

    private void getGroupChat(){
        Log.e("roma", "onDataChange getGroupChat: Chats_v2" );
        referenceChats = FirebaseDatabase.getInstance().getReference("Chats_v2");
        eventListenerChats =  referenceChats.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("roma", "onDataChange: group fragment 159");
                new getGroupChatAsync().execute(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private class getGroupChatAsync extends AsyncTask<DataSnapshot, Void, Boolean> {

        @Override
        protected Boolean doInBackground(DataSnapshot... dataSnapshots) {
            try {
                for (DataSnapshot snapshot : dataSnapshots[0].getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);

                    GroupChat groupChat = groupChatDao.getGroupChatByName(chat.getReceiver());
                    if (groupChat != null) {
                        if (chat.getTime() > groupChat.getTime()) {
                            groupChat.setLast_msg(chat.getType() + "split100x" + chat.getMessage());
                            groupChat.setTime(chat.getTime());
                            groupChat.setLast_seen(groupChat.getLast_seen());
                            Log.d("roma", "onDataChange: masuk if !=null " + chat.getSender() + " - " + chat.getReceiver() + " - " + chat.getMessage());
                            int unread;
                            if (chat.getTime() > groupChat.getLast_seen() && !chat.getSender().equals(userDao.getUser().get_id())) {
                                Log.d("roma", "onDataChange: masuk if unread++ " + chat.getSender() + " - " + chat.getReceiver() + " - " + chat.getMessage());
                                unread = groupChat.getUnread() + 1;
                            } else {
                                unread = 0;
                            }
                            groupChat.setUnread(unread);
                            groupChatDao.insertGroupChat(groupChat);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return true;
            }
            return true;
        }
    }

    private void getGroupChatUnRead() {
        Log.e("roma", "onDataChange getGroupChatUnRead: Users");
        referenceUsers = FirebaseDatabase.getInstance().getReference("Users");
        eventListenerUsers = referenceUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("roma", "onDataChange: groupfragment 201");
                stateView.setLoading(true);
                new getGroupChatUnReadAsync().execute(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private class getGroupChatUnReadAsync extends AsyncTask <DataSnapshot, Void, Boolean> {

        @Override
        protected Boolean doInBackground(DataSnapshot... dataSnapshots) {
            for (DataSnapshot snapshot : dataSnapshots[0].getChildren()) {
                GroupChatSeen groupChatSeen = snapshot.getValue(GroupChatSeen.class);
                User user = userDao.getUser();
                if (groupChatSeen != null && user != null && groupChatSeen.getId_user() != null && groupChatSeen.getId_user().equals(user.get_id())) {
                    groupChatDao.updateLastSeen(groupChatSeen.getNama(), groupChatSeen.getLast_seen());
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            getGroupChat();
        }
    }


    private void addUserGroupLastSeen(String name, long time){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id_user", userDao.getUser().get_id());
        hashMap.put("nama", name);
        hashMap.put("last_seen", time);

        databaseReference.child("Users").child(userDao.getUser().get_id()+"-"+name).setValue(hashMap);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("roma", "onDestroy: ");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("roma", "onPause: ");
    }
}
