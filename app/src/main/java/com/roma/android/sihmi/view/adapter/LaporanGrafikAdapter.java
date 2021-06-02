package com.roma.android.sihmi.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.entity.DataGrafik;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class LaporanGrafikAdapter extends RecyclerView.Adapter<LaporanGrafikAdapter.ViewHolder> {
    Context context;
    List<DataGrafik> list;
    ItemClickListener listener;

    public LaporanGrafikAdapter(Context context, List<DataGrafik> list, ItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    public void updateData(List<DataGrafik> agendaList){
        list = agendaList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_data_grafik, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        DataGrafik dataGrafik = list.get(i);
        viewHolder.tvTahun.setText(""+dataGrafik.getTahun());
        viewHolder.tvJumlah.setText(""+dataGrafik.getJumlah());
        viewHolder.tvKader.setText(""+dataGrafik.getKader());
        viewHolder.tvNonKader.setText(""+dataGrafik.getNonKader());
        viewHolder.itemView.setOnClickListener(v ->
                listener.onItemClick(dataGrafik));
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
        @BindView(R.id.tv_jumlah)
        TextView tvJumlah;
        @BindView(R.id.tv_kader)
        TextView tvKader;
        @BindView(R.id.tv_non_kader)
        TextView tvNonKader;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface ItemClickListener{
        void onItemClick(DataGrafik dataGrafik);
    }
}
