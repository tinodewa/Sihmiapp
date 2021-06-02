package com.roma.android.sihmi.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.entity.GroupChat;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Tools;

import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

//import com.example.root.sihmi.repository.model.entity.Contact;

public class ChatGroupAdapter extends RecyclerView.Adapter<ChatGroupAdapter.ViewHolder> {
    int size;
    Context context;
    List<GroupChat> list;
    itemClickListener listener;
//    GroupChat theLastMsg, theLastTimeMsg;

    public ChatGroupAdapter(Context context, List<GroupChat> list, itemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    public ChatGroupAdapter(Context context, List<GroupChat> list) {
        this.context = context;
        this.list = list;
    }

    public void updateData(List<GroupChat> contacts){
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
        GroupChat groupChat = list.get(i);

        viewHolder.tvNama.setText(groupChat.getNama());
        String theLastMessage = "Tidak Ada Pesan";
        if (groupChat.getLast_msg() != null && !groupChat.getLast_msg().isEmpty()) {
            String[] msg = groupChat.getLast_msg().split("split100x");
            if (msg[0].equalsIgnoreCase(Constant.IMAGE)){
                theLastMessage = Constant.IMAGE;
            } else if (msg[0].equalsIgnoreCase(Constant.DOCUMENT)){
                theLastMessage = Constant.DOCUMENT;
            } else {
                theLastMessage = msg[1];
            }
            viewHolder.tvDesc.setText(theLastMessage);

            if (Tools.getDateFromMillis(groupChat.getTime()).equalsIgnoreCase(Tools.getDateFromMillis(System.currentTimeMillis()))){
                viewHolder.tvTime.setText(context.getString(R.string.hari_ini));
            } else if (Tools.getDateFromMillis(groupChat.getTime()+ TimeUnit.DAYS.toMillis(1)).equalsIgnoreCase(Tools.getDateFromMillis(System.currentTimeMillis()))){
                viewHolder.tvTime.setText(context.getString(R.string.kemarin));
            } else {
                viewHolder.tvTime.setText(Tools.getDateFromMillis(groupChat.getTime()));
            }
        } else {
            viewHolder.tvDesc.setText(theLastMessage);
        }

        if (groupChat.isBisukan()) {
            viewHolder.notifIcon.setVisibility(View.VISIBLE);
        }
        else {
            viewHolder.notifIcon.setVisibility(View.GONE);
        }

        if (groupChat.getUnread() > 0){
            viewHolder.tvUnread.setVisibility(View.VISIBLE);
            viewHolder.tvUnread.setText(String.valueOf(groupChat.getUnread()));
        } else {
            viewHolder.tvUnread.setVisibility(View.GONE);
        }

        try {
            if (groupChat.getImage() != null && !groupChat.getImage().isEmpty()) {
                Glide.with(context).load(groupChat.getImage()).into(viewHolder.ivPhoto);
            } else if (groupChat.getImage().equals("")){
                Glide.with(context).load(R.drawable.logo_icon).into(viewHolder.ivPhoto);
            } else {
                Glide.with(context).load(R.drawable.logo_icon).into(viewHolder.ivPhoto);
            }
        } catch (NullPointerException e){
            Glide.with(context).load(R.drawable.logo_icon).into(viewHolder.ivPhoto);
        }

        viewHolder.itemView.setOnClickListener(v -> listener.onItemClick(groupChat, false));

        viewHolder.itemView.setOnLongClickListener(v -> {
            listener.onItemClick(groupChat, true);
            return true;
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
        @BindView(R.id.tvUnread)
        TextView tvUnread;
        @BindView(R.id.notifIcon)
        ImageView notifIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            tvTime.setVisibility(View.VISIBLE);
            tvDesc.setVisibility(View.VISIBLE);
        }
    }

    public interface itemClickListener{
        void onItemClick(GroupChat groupChat, boolean isLongClick);
    }
}
