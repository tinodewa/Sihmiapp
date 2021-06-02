package com.roma.android.sihmi.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.entity.Contact;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class LaporanDataKaderAdapter extends RecyclerView.Adapter<LaporanDataKaderAdapter.ViewHolder> {
    Context context;
    List<Contact> list;
    itemClickListener listener;

    public LaporanDataKaderAdapter(Context context, List<Contact> list, itemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    public void updateData(List<Contact> list1){
        this.list = list1;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_data_kader, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        int nomor = i+1;
        Contact contact = list.get(i);
        viewHolder.tvNama.setText(nomor+". "+contact.getFullName());
        viewHolder.itemView.setOnClickListener(v -> {
            listener.onItemClick(contact);
        });
    }

    @Override
    public int getItemCount() {
        return list.size()>0 ? list.size() : 0;
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_nama)
        TextView tvNama;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface itemClickListener{
        void onItemClick(Contact contact);
    }
}
