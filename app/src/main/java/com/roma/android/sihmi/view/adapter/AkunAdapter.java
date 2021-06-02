package com.roma.android.sihmi.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.entity.Account;
import com.roma.android.sihmi.utils.Constant;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AkunAdapter extends RecyclerView.Adapter<AkunAdapter.ViewHolder> {
    Context context;
    List<Account> list;
    itemClickListener listener;

    public AkunAdapter(Context context, List<Account> list, itemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    public void updateData(List<Account> accounts){
        this.list = accounts;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_akun, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        Account account = list.get(i);
        viewHolder.tvNama.setText(account.getUsername());
        if (account.getImage() != null && !account.getImage().trim().isEmpty()){
            Glide.with(context).load(account.getImage()).into(viewHolder.ivPhoto);
        }

        viewHolder.btnMasuk.setOnClickListener(v -> listener.onItemClick(Constant.MASUK, account));

        viewHolder.imgDelete.setOnClickListener(v -> listener.onItemClick(Constant.HAPUS, account));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tvNama)
        TextView tvNama;
        @BindView(R.id.ivPhoto)
        ImageView ivPhoto;
        @BindView(R.id.btn_masuk)
        Button btnMasuk;
        @BindView(R.id.img_delete)
        ImageView imgDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface itemClickListener{
        void onItemClick(String type, Account account);
    }
}
