package com.roma.android.sihmi.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Chatting;
import com.roma.android.sihmi.model.database.entity.Contact;
import com.roma.android.sihmi.model.database.interfaceDao.ContactDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Tools;

import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    int size;
    Context context;
    List<Chatting> list;
    itemClickListener listener;
//    String theLastMsg, theLastTimeMsg;
    AppDb appDb;
    ContactDao contactDao;
    UserDao userDao;

    public ChatAdapter(Context context, int size) {
        this.context = context;
        this.size = size;
    }

    public ChatAdapter(Context context, List<Chatting> list, itemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
        appDb = AppDb.getInstance(context);
        contactDao = appDb.contactDao();
        userDao = appDb.userDao();
    }

    public ChatAdapter(Context context, int size, itemClickListener listener) {
        this.size = size;
        this.context = context;
        this.listener = listener;
    }

    public void updateData(List<Chatting> contacts){
        this.list = contacts;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final Chatting chatting = list.get(i);
        String[] msg = chatting.getLast_message().split("split100x");
        String receiver = msg[0];
        String lastMessage = msg[1];

        Contact user = contactDao.getContactById(chatting.get_id());
        viewHolder.tvNama.setText(user.getFullName());
        viewHolder.tvDesc.setText(lastMessage);

        if (user.isBisukan()) {
            viewHolder.notifIcon.setVisibility(View.VISIBLE);
        }
        else {
            viewHolder.notifIcon.setVisibility(View.GONE);
        }

        if (chatting.getUnread() > 0 && receiver.equals(userDao.getUser().get_id())){
            viewHolder.tvUnread.setVisibility(View.VISIBLE);
            viewHolder.tvUnread.setText(String.valueOf(chatting.getUnread()));
        } else {
            viewHolder.tvUnread.setVisibility(View.GONE);
        }

        if (Tools.getDateFromMillis(chatting.getTime_message()).equalsIgnoreCase(Tools.getDateFromMillis(System.currentTimeMillis()))){
            if (Constant.getLanguage().equals("id")){
                viewHolder.tvTime.setText("Hari Ini");
            } else {
                viewHolder.tvTime.setText("Today");
            }
        } else if (Tools.getDateFromMillis(chatting.getTime_message()+ TimeUnit.DAYS.toMillis(1)).equalsIgnoreCase(Tools.getDateFromMillis(System.currentTimeMillis()))){
            if (Constant.getLanguage().equals("id")){
                viewHolder.tvTime.setText("Kemarin");
            } else {
                viewHolder.tvTime.setText("Yesterday");
            }
        } else {
            viewHolder.tvTime.setText(Tools.getDateFromMillis(chatting.getTime_message()));
        }

        String firstLetter = String.valueOf(user.getFullName().charAt(0));
        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        int color = generator.getColor(firstLetter);
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(firstLetter, color); // radius in px

        try {
            if (user.getImage() != null && !user.getImage().trim().isEmpty()) {
                Glide.with(context).load(user.getImage()).into(viewHolder.ivPhoto);
                viewHolder.ivPhoto.setVisibility(View.VISIBLE);
                viewHolder.ivIntial.setVisibility(View.GONE);
            } else {
                viewHolder.ivIntial.setImageDrawable(drawable);
                viewHolder.ivIntial.setVisibility(View.VISIBLE);
                viewHolder.ivPhoto.setVisibility(View.GONE);
            }
        } catch (NullPointerException e){
            viewHolder.ivIntial.setImageDrawable(drawable);
            viewHolder.ivIntial.setVisibility(View.VISIBLE);
            viewHolder.ivPhoto.setVisibility(View.GONE);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(chatting, false);
            }
        });

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.onItemClick(chatting, true);
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tvNama)
        TextView tvNama;
        @BindView(R.id.tvTime)
        TextView tvTime;
        @BindView(R.id.tvDesc)
        TextView tvDesc;
        @BindView(R.id.ivPhoto)
        ImageView ivPhoto;
        @BindView(R.id.ivInitial)
        ImageView ivIntial;
        @BindView(R.id.tvUnread)
        TextView tvUnread;
        @BindView(R.id.notifIcon)
        ImageView notifIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface itemClickListener{
        void onItemClick(Chatting chatting, boolean isLongCLick);
    }
}
