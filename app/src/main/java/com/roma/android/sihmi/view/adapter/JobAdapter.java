package com.roma.android.sihmi.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.entity.Job;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.ViewHolder> {
    Context context;
    ItemClickListener listener;
    List<Job> list;

    public void updateData(List<Job> agendaList){
        list = agendaList;
        notifyDataSetChanged();

    }

    public JobAdapter(Context context, List<Job> list, ItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_job, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
//        if (list.size() == 0){
//            viewHolder.tvKet.setVisibility(View.VISIBLE);
//            viewHolder.tvKet.setText("Sekolah dimana ?");
//        } else {
//            viewHolder.tvKet.setVisibility(View.GONE);
//        }
        Job pendidikan = list.get(i);
        viewHolder.etTahun.setText(pendidikan.getTahun_mulai());
        viewHolder.etJenjang.setText(pendidikan.getTahun_berakhir());
        viewHolder.etNama.setText(pendidikan.getNama_perusahaan());
        viewHolder.etJurusan.setText(pendidikan.getJabatan());
        viewHolder.etAlamat.setText(pendidikan.getAlamat());
        viewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClickId(pendidikan.get_id());
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.etTahun)
        EditText etTahun;
        @BindView(R.id.etJenjang)
        EditText etJenjang;
        @BindView(R.id.etNama)
        EditText etNama;
        @BindView(R.id.etJurusan)
        EditText etJurusan;
        @BindView(R.id.etAlamat)
        EditText etAlamat;
        @BindView(R.id.iv_delete)
        ImageView ivDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface ItemClickListener{
        void onItemClickId(String id);
    }
}
