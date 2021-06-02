package com.roma.android.sihmi.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.entity.DataKader;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class LaporanDataKaderPelatihanAdapter extends RecyclerView.Adapter<LaporanDataKaderPelatihanAdapter.ViewHolder> {
    Context context;
    List<DataKader> list;
    ItemClickListener listener;

    public LaporanDataKaderPelatihanAdapter(Context context, List<DataKader> list, ItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    public void updateData(List<DataKader> agendaList){
        list = agendaList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_data_kader_pelatihan, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        DataKader dataKader = list.get(i);
        viewHolder.tvTahun.setText(""+dataKader.getTahun());
        viewHolder.tvLK1.setText(""+dataKader.getLk1());
        viewHolder.tvLK2.setText(""+dataKader.getLk2());
        viewHolder.tvLK3.setText(""+dataKader.getLk3());
        viewHolder.tvSC.setText(""+dataKader.getSc());
        viewHolder.tvTID.setText(""+dataKader.getTid());
        viewHolder.itemView.setOnClickListener(v ->
                listener.onItemClick(dataKader));
    }

    @Override
    public int getItemCount() {
        if (list.size() > 0){
            return list.size();
        } else {
            return 0;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_tahun)
        TextView tvTahun;
        @BindView(R.id.tv_lk1)
        TextView tvLK1;
        @BindView(R.id.tv_lk2)
        TextView tvLK2;
        @BindView(R.id.tv_lk3)
        TextView tvLK3;
        @BindView(R.id.tv_sc)
        TextView tvSC;
        @BindView(R.id.tv_tid)
        TextView tvTID;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface ItemClickListener{
        void onItemClick(DataKader dataKader);
    }
}
