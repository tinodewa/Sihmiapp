package com.roma.android.sihmi.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.entity.Pendidikan;
import com.roma.android.sihmi.utils.Constant;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PendidikanAdapter extends RecyclerView.Adapter<PendidikanAdapter.ViewHolder> {
    Context context;
    itemClickListener listener;

    List<Pendidikan> list;
    private boolean displayAdd;

    public void updateData(List<Pendidikan> agendaList){
        list = agendaList;
        notifyDataSetChanged();

    }

    public PendidikanAdapter(Context context, List<Pendidikan> list, itemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
        displayAdd = true;
    }

    public boolean isDisplayAdd() {
        return displayAdd;
    }

    public void setDisplayAdd(boolean displayAdd) {
        this.displayAdd = displayAdd;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_v2, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        viewHolder.setIsRecyclable(false);
        if (list.size() > 0) {
            Pendidikan pendidikan = list.get(i);
            viewHolder.etTahun.setText(pendidikan.getTahun());
            viewHolder.etStrata.setText(pendidikan.getStrata());
            viewHolder.etInstitusi.setText(pendidikan.getNama());
            viewHolder.etJurusan.setText(pendidikan.getJurusan());
            if (i == (list.size()-1)){
                viewHolder.fabAdd.setVisibility(View.VISIBLE);
            } else {
                viewHolder.fabAdd.setVisibility(View.INVISIBLE);
            }
        } else {
            viewHolder.fabAdd.setVisibility(View.VISIBLE);
        }
        viewHolder.fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClickId(Constant.FAB_ADD);
            }
        });

        if (!displayAdd) {
            viewHolder.fabAdd.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        if (list.size() == 0){
            return 1;
        }
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.ll_pelatihan)
        LinearLayout llPelatihan;
        @BindView(R.id.et_tahun_pendidikan)
        EditText etTahun;
        @BindView(R.id.et_strata)
        EditText etStrata;
        @BindView(R.id.et_institusi)
        EditText etInstitusi;
        @BindView(R.id.et_jurusan)
        EditText etJurusan;
        @BindView(R.id.fab_add_pendidikan)
        FloatingActionButton fabAdd;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            llPelatihan.setVisibility(View.GONE);
            fabAdd.setVisibility(View.GONE);
        }
    }

    public interface itemClickListener{
        void onItemClickId(String id);
    }
}
