package com.roma.android.sihmi.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Alamat;
import com.roma.android.sihmi.model.database.interfaceDao.ContactDao;
import com.roma.android.sihmi.utils.Tools;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AlamatAdapter extends RecyclerView.Adapter<AlamatAdapter.ViewHolder> {
    int size;
    Context context;
    itemClickListener listener;

    List<Alamat> list;
    ContactDao contactDao;

    public void updateData(List<Alamat> agendaList){
        list.clear();
        list = agendaList;
        notifyDataSetChanged();

    }

    public AlamatAdapter(Context context, List<Alamat> list, itemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
        contactDao = AppDb.getInstance(context).contactDao();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_alamat, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        if (Tools.isSuperAdmin()){
            viewHolder.llCreate.setVisibility(View.VISIBLE);
        } else {
            viewHolder.llCreate.setVisibility(View.GONE);
        }
        Alamat alamat = list.get(i);
        final String judul = alamat.getNama();
        final String id = alamat.get_id();
        String date = Tools.getDateLaporanFromMillis(alamat.getCreated_date());
        viewHolder.tvNama.setText(judul);
        viewHolder.tvDesc.setText(alamat.getAlamat());
        viewHolder.tvDate.setText(date);
        viewHolder.tvCreator.setText(contactDao.getContactById(alamat.getId_user()).getFullName());
        viewHolder.ivPhoto.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_location));

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(alamat, false);
            }
        });

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.onItemClick(alamat, true);
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
        @BindView(R.id.tvDesc)
        TextView tvDesc;
        @BindView(R.id.tvDate)
        TextView tvDate;
        @BindView(R.id.tvCreator)
        TextView tvCreator;
        @BindView(R.id.ivPhoto)
        ImageView ivPhoto;
        @BindView(R.id.llCreate)
        LinearLayout llCreate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface itemClickListener{
        void onItemClick(Alamat alamat, boolean isLongClick);
    }
}
