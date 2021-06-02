package com.roma.android.sihmi.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Sejarah;
import com.roma.android.sihmi.model.database.interfaceDao.ContactDao;
import com.roma.android.sihmi.utils.Tools;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TentangKamiAdapter extends RecyclerView.Adapter<TentangKamiAdapter.ViewHolder> {
    Context context;
    List<Sejarah> list;
    itemClickListener listener;
    ContactDao contactDao;

    public TentangKamiAdapter(Context context, List<Sejarah> list, itemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
        contactDao = AppDb.getInstance(context).contactDao();
    }


    public void updateData(List<Sejarah> agendaList){
        list = agendaList;
        notifyDataSetChanged();
    }
//
//    public TentangKamiAdapter(Context context, List<Agenda> list) {
//        this.context = context;
//        this.list = list;
//    }

//    public TentangKamiAdapter(Context context, boolean type, List<Agenda> list) {
//        this.context = context;
//        this.list = list;
//    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_tentang_kami, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        viewHolder.tvTitle.setText(list.get(i).getJudul());
        viewHolder.tvDesc.setText(list.get(i).getDeskripsi());
        viewHolder.tvCreator.setText(contactDao.getContactById(list.get(i).getId_user()).getFullName());
        viewHolder.tvDate.setText(Tools.getDateLaporanFromMillis(list.get(i).getDate_created()));
        viewHolder.itemView.setOnClickListener(v -> listener.onItemClick(list.get(i), false));
        viewHolder.itemView.setOnLongClickListener(v -> {
            listener.onItemClick(list.get(i), true);
            return true;
        });

        if (Tools.isSecondAdmin() || Tools.isSuperAdmin()){
            viewHolder.llCreate.setVisibility(View.VISIBLE);
        } else {
            viewHolder.llCreate.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_desc)
        TextView tvDesc;
        @BindView(R.id.tv_creator)
        TextView tvCreator;
        @BindView(R.id.tv_date)
        TextView tvDate;
        @BindView(R.id.llCreate)
        LinearLayout llCreate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface itemClickListener{
        void onItemClick(Sejarah sejarah, boolean isLongClick);
    }
}
