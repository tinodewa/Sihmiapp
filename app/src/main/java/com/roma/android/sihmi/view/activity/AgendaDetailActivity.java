package com.roma.android.sihmi.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Agenda;
import com.roma.android.sihmi.model.database.entity.AgendaComment;
import com.roma.android.sihmi.model.database.interfaceDao.AgendaDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.utils.Tools;
import com.roma.android.sihmi.view.adapter.CommentAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class AgendaDetailActivity extends BaseActivity {
    public static String ID_AGENDA = "id_agenda";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.iv_image)
    ImageView ivImage;
    @BindView(R.id.iv_fullscreen)
    ImageView ivFullscreen;
    @BindView(R.id.tv_nama)
    TextView tvNama;
    @BindView(R.id.tv_tgl)
    TextView tvTgl;
    @BindView(R.id.tv_jam)
    TextView tvJam;
    @BindView(R.id.tv_tempat)
    TextView tvTempat;
    @BindView(R.id.tv_alamat)
    TextView tvAlamat;
    @BindView(R.id.tv_desc)
    TextView tvDesc;
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.rlMessage)
    RelativeLayout rlMessage;
    @BindView(R.id.et_message)
    EditText etMessage;
    @BindView(R.id.img_send)
    ImageView imgSend;
    @BindView(R.id.rv_comment)
    RecyclerView rvComment;
    @BindView(R.id.cv_comment)
    CardView cvComment;

    Agenda agenda;
    String id_agenda;
    String url = "";


    DatabaseReference databaseReference;
    DatabaseReference databaseReferenceSeen;
    ValueEventListener seenListener;
    List<AgendaComment> list = new ArrayList<>();
    CommentAdapter adapter;

    AppDb appDb;
    UserDao userDao;
    AgendaDao agendaDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda_detail);
        ButterKnife.bind(this);

        appDb = AppDb.getInstance(this);
        agendaDao = appDb.agendaDao();
        userDao = appDb.userDao();

        id_agenda = getIntent().getStringExtra(ID_AGENDA);
        agenda = agendaDao.getAgendaById(id_agenda);

        toolbar.setTitle(agenda.getNama());
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        String[] type = agenda.getType().split("-");
        tvType.setText(type[1]);
        tvNama.setText(agenda.getNama());
        tvTgl.setText(Tools.getFullDateFromMillis(agenda.getDate_expired()));
        String jamStr = Tools.getTimeFromMillis(agenda.getDate_expired()) + getString(R.string.sampai_selesai);
        tvJam.setText(jamStr);
        tvTempat.setText(agenda.getTempat());
        tvAlamat.setText(agenda.getLokasi());
        if (agenda.getImage() != null && !agenda.getImage().isEmpty()) {
            Glide.with(AgendaDetailActivity.this)
                    .load(Uri.parse(agenda.getImage()))
                    .centerCrop()
                    .into(ivImage);
            url = agenda.getImage();
        }

        adapter = new CommentAdapter(this, list);
        rvComment.setLayoutManager(new LinearLayoutManager(this));
        rvComment.setHasFixedSize(true);
        rvComment.setAdapter(adapter);
        readComment();

        if (Tools.isNonLK()){
            rlMessage.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.iv_fullscreen)
    public void goFullScreen() {
        startActivity(new Intent(AgendaDetailActivity.this, FullImageActivity.class).putExtra(FullImageActivity.URL_IMAGE, url));
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    @OnClick(R.id.img_send)
    public void sendComment(){
        Toast.makeText(this, getString(R.string.kirim_pesan), Toast.LENGTH_SHORT).show();
        if (!etMessage.getText().toString().trim().isEmpty()){
            sendComment(etMessage.getText().toString());
        }
    }

    private void sendComment(String message){
        databaseReference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id_agenda", id_agenda);
        hashMap.put("id_user", userDao.getUser().get_id());
        hashMap.put("username", userDao.getUser().getUsername());
        hashMap.put("message", message);
        hashMap.put("time", System.currentTimeMillis());

        databaseReference.child("Agenda").push().setValue(hashMap);

        etMessage.setText("");
    }

    private void readComment(){
        databaseReferenceSeen = FirebaseDatabase.getInstance().getReference("Agenda");
        seenListener = databaseReferenceSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("roma", "onDataChange: agendadetailactivity 175");
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    AgendaComment comment = snapshot.getValue(AgendaComment.class);
                    if (comment != null && comment.getId_agenda().equals(id_agenda)) {
                        if (cvComment.getVisibility() == View.GONE) {
                            cvComment.setVisibility(View.VISIBLE);
                        }
                        list.add(comment);
                    }
                }
                adapter.updateData(list);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        databaseReferenceSeen.removeEventListener(seenListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReferenceSeen.removeEventListener(seenListener);
    }
}
