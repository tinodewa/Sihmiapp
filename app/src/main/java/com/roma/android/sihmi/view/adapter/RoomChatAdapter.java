package com.roma.android.sihmi.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Chat;
import com.roma.android.sihmi.model.database.entity.Contact;
import com.roma.android.sihmi.model.database.interfaceDao.ContactDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.model.network.ApiClient;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.response.ContactSingleResponse;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Tools;

import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoomChatAdapter extends RecyclerView.Adapter<RoomChatAdapter.ViewHolder> {
    Context context;
    List<Chat> list;
    int type;
    itemClickListener listener;
    Typeface britanic;
    AppDb appDb;
    ContactDao contactDao;
    UserDao userDao;
    String user;
    MasterService service;

    public RoomChatAdapter(Context context, List<Chat> list, int type, itemClickListener listener) {
        this.context = context;
        this.list = list;
        this.type = type;
        this.listener = listener;
        britanic  = Typeface.createFromAsset(context.getAssets(),"fonts/Britanic.ttf");
        appDb = AppDb.getInstance(context);
        contactDao = appDb.contactDao();
        userDao = appDb.userDao();
        user = userDao.getUser().get_id();
        service = ApiClient.getInstance().getApi();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_room_chat, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        viewHolder.setIsRecyclable(false);
        Chat chat = list.get(i);
        Contact contact = contactDao.getContactById(chat.getSender());
        boolean nullContact = false;
        if (contact == null) {
            contact = new Contact("Unknown", null);
            contact.set_id(chat.getSender());
            contactDao.insertContact(contact);
            getContact(chat.getSender());
            nullContact = true;
        }

        if (type == 1){
            viewHolder.tvName.setVisibility(View.GONE);
        } else {
            viewHolder.tvName.setVisibility(View.VISIBLE);
            if (nullContact) {
                LiveData<Contact> contactLiveData = contactDao.getContactLiveDataById(chat.getSender());
                contactLiveData.observe((LifecycleOwner) context,
                        currentContact -> {
                    Log.d("Name change", "Name change " + currentContact.getNama_depan());
                    viewHolder.tvName.setText(currentContact.getNama_depan());
                    if (!currentContact.getNama_depan().equalsIgnoreCase("unknown")) {
                        contactLiveData.removeObservers((LifecycleOwner) context);
                    }
                });

            }
            else {
                viewHolder.tvName.setText(contact.getNama_depan());
            }
        }

        if (chat.getSender().equals(user)){
            Log.d("haiRoma", "onBindViewHolder: send "+chat.getMessage());
            viewHolder.llReceive.setVisibility(View.GONE);

            if (chat.getType().equalsIgnoreCase(Constant.TEXT)){
                viewHolder.tvSend.setVisibility(View.VISIBLE);
                viewHolder.tvTimeSend.setVisibility(View.VISIBLE);
                viewHolder.imgSend.setVisibility(View.GONE);
                viewHolder.tvMessageSendSticker.setVisibility(View.GONE);
            } else if (chat.getType().equalsIgnoreCase(Constant.STICKER)){
                viewHolder.tvSend.setVisibility(View.GONE);
                viewHolder.tvTimeSend.setVisibility(View.VISIBLE);
                viewHolder.imgSend.setVisibility(View.GONE);
                viewHolder.tvMessageSendSticker.setVisibility(View.VISIBLE);
                viewHolder.llSend.setBackgroundColor(Color.TRANSPARENT);
//                viewHolder.tvMessageSendSticker.setTextSize(50);
                if (Constant.loadNightModeState()) {
                    viewHolder.tvMessageSendSticker.getPaint().setShader(new LinearGradient(0f, 0f, 0f, viewHolder.tvSend.getTextSize(), Color.WHITE, viewHolder.itemView.getResources().getColor(R.color.colorGreen), Shader.TileMode.CLAMP));
                    viewHolder.tvTimeSend.setTextColor(viewHolder.itemView.getResources().getColor(R.color.colorTextLight));
                }
                else {
                    viewHolder.tvMessageSendSticker.getPaint().setShader(new LinearGradient(0f, 0f, 0f, viewHolder.tvSend.getTextSize(), Color.BLACK, viewHolder.itemView.getResources().getColor(R.color.colorGreen), Shader.TileMode.CLAMP));
                    viewHolder.tvTimeSend.setTextColor(Color.BLACK);
                }
                viewHolder.tvMessageSendSticker.setTypeface(britanic);

            } else {
                viewHolder.tvSend.setVisibility(View.GONE);
                viewHolder.tvTimeSend.setVisibility(View.VISIBLE);
                viewHolder.imgSend.setVisibility(View.VISIBLE);
                viewHolder.tvMessageSendSticker.setVisibility(View.GONE);
            }

            viewHolder.tvSend.setText(chat.getMessage());
            viewHolder.tvMessageSendSticker.setText(chat.getMessage().toUpperCase());
            viewHolder.tvTimeSend.setText(Tools.getTimeFromMillis(chat.getTime()));
            if (chat.getType().equalsIgnoreCase(Constant.IMAGE)){
                Glide.with(context).load(chat.getMessage()).into(viewHolder.imgSend);
            }

        } else {
            Log.d("haiRoma", "onBindViewHolder: receive "+chat.getMessage());
            viewHolder.llSend.setVisibility(View.GONE);

            if (chat.getType().equalsIgnoreCase(Constant.TEXT)){
                viewHolder.tvReceive.setVisibility(View.VISIBLE);
                viewHolder.tvTimeReceive.setVisibility(View.VISIBLE);
                viewHolder.imgReceive.setVisibility(View.GONE);
                viewHolder.tvMessageReceiveSticker.setVisibility(View.GONE);
            } else if (chat.getType().equalsIgnoreCase(Constant.STICKER)){
                viewHolder.tvReceive.setVisibility(View.GONE);
                viewHolder.tvTimeReceive.setVisibility(View.VISIBLE);
                viewHolder.imgReceive.setVisibility(View.GONE);
                viewHolder.tvMessageReceiveSticker.setVisibility(View.VISIBLE);
                viewHolder.llReceive.setBackgroundColor(Color.TRANSPARENT);
//                viewHolder.tvReceive.setTextSize(50);
                if (Constant.loadNightModeState()) {
                    viewHolder.tvMessageReceiveSticker.getPaint().setShader(new LinearGradient(0f, 0f, 0f, viewHolder.tvSend.getTextSize(), Color.WHITE, viewHolder.itemView.getResources().getColor(R.color.colorGreen), Shader.TileMode.CLAMP));
                    viewHolder.tvTimeReceive.setTextColor(viewHolder.itemView.getResources().getColor(R.color.colorTextLight));
                }
                else {
                    viewHolder.tvMessageReceiveSticker.getPaint().setShader(new LinearGradient(0f, 0f, 0f, viewHolder.tvSend.getTextSize(), Color.BLACK, viewHolder.itemView.getResources().getColor(R.color.colorGreen), Shader.TileMode.CLAMP));
                    viewHolder.tvTimeReceive.setTextColor(Color.BLACK);
                }
                viewHolder.tvMessageReceiveSticker.setTypeface(britanic);
            } else {
                viewHolder.tvReceive.setVisibility(View.GONE);
                viewHolder.tvTimeReceive.setVisibility(View.VISIBLE);
                viewHolder.imgReceive.setVisibility(View.VISIBLE);
                viewHolder.tvMessageReceiveSticker.setVisibility(View.GONE);
            }

            viewHolder.tvReceive.setText(chat.getMessage());
            viewHolder.tvMessageReceiveSticker.setText(chat.getMessage().toUpperCase());
            viewHolder.tvTimeReceive.setText(Tools.getTimeFromMillis(chat.getTime()));
            if (chat.getType().equalsIgnoreCase(Constant.IMAGE)){
                Glide.with(context).load(chat.getMessage()).into(viewHolder.imgReceive);
            }
        }

        if (i>0){
            if (Tools.getDateFromMillis(list.get(i).getTime()).equalsIgnoreCase(Tools.getDateFromMillis(list.get(i-1).getTime()))){
                viewHolder.tvDate.setVisibility(View.GONE);
            } else {
                viewHolder.tvDate.setVisibility(View.VISIBLE);
            }
        } else {
            viewHolder.tvDate.setVisibility(View.VISIBLE);
        }

        if (Tools.getDateFromMillis(chat.getTime()).equalsIgnoreCase(Tools.getDateFromMillis(System.currentTimeMillis()))){
            viewHolder.tvDate.setText(context.getString(R.string.hari_ini));
        } else if (Tools.getDateFromMillis(chat.getTime()+ TimeUnit.DAYS.toMillis(1)).equalsIgnoreCase(Tools.getDateFromMillis(System.currentTimeMillis()))){
            viewHolder.tvDate.setText(context.getString(R.string.kemarin));
        } else {
            viewHolder.tvDate.setText(Tools.getDateFromMillis(chat.getTime()));
        }

        viewHolder.itemView.setOnClickListener(v -> listener.onItemClick(chat));

    }

    private void getContact(String sender_id) {
        Call<ContactSingleResponse> call = service.getContact(Constant.getToken(), sender_id);
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
                    }
                }
            }

            @Override
            public void onFailure(Call<ContactSingleResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tvMessageReceive)
        TextView tvReceive;
        @BindView(R.id.tvMessageSend)
        TextView tvSend;
        @BindView(R.id.tvTimeReceive)
        TextView tvTimeReceive;
        @BindView(R.id.tvTimeSend)
        TextView tvTimeSend;
        @BindView(R.id.tvDate)
        TextView tvDate;
        @BindView(R.id.llReceive)
        LinearLayout llReceive;
        @BindView(R.id.llSend)
        LinearLayout llSend;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.img_receiver)
        ImageView imgReceive;
        @BindView(R.id.img_send)
        ImageView imgSend;
        @BindView(R.id.tvMessageReceiveSticker)
        TextView tvMessageReceiveSticker;
        @BindView(R.id.tvMessageSendSticker)
        TextView tvMessageSendSticker;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface itemClickListener{
        void onItemClick(Chat chat);
    }
}
