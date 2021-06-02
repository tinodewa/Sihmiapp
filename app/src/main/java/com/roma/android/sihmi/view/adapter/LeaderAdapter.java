package com.roma.android.sihmi.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.roma.android.sihmi.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class LeaderAdapter extends RecyclerView.Adapter<LeaderAdapter.ViewHolder> {
    Context context;
//    List<Contact> list;
    itemClickListener listener;

    public LeaderAdapter(Context context, itemClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    //    public LeaderAdapter(Context context, List<Contact> list) {
//        this.context = context;
//        this.list = list;
//    }
//
//    public LeaderAdapter(Context context, List<Contact> list, itemClickListener listener) {
//        this.context = context;
//        this.list = list;
//        this.listener = listener;
//    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_contact, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
//        final Contact contact = list.get(i);
//        viewHolder.tvNama.setText(contact.getFullName());

        String name;
        if (i == 0){
            name = "Leader Nasional";
        } else if (i == 1){
            name = "Leader Cabang";
        } else {
            name = "Leader Komisariat";
        }
        viewHolder.tvNama.setText(name);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(name);
            }
        });

    }

    @Override
    public int getItemCount() {
        return 3;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.ivPhoto)
        ImageView ivPhoto;
        @BindView(R.id.tvNama)
        TextView tvNama;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface itemClickListener{
        void onItemClick(String name);
    }
}
