package com.roma.android.sihmi.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.entity.Training;
import com.roma.android.sihmi.utils.Constant;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TrainingAdapter extends RecyclerView.Adapter<TrainingAdapter.ViewHolder> {
    Context context;
    ItemClickListener listener;
    List<Training> list;

    public void updateData(List<Training> agendaList){
        list = agendaList;
        notifyDataSetChanged();

    }

    public TrainingAdapter(Context context, List<Training> list, ItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
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
            Training training = list.get(i);
            viewHolder.etTahun.setText(training.getTahun());
            viewHolder.etPelatihan.setText(training.getTipe());
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

//        viewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                listener.onItemClickId(pendidikan.get_id());
//            }
//        });

    }

    @Override
    public int getItemCount() {
        if (list.size() == 0){
            return 1;
        }
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.ll_pendidikan)
        LinearLayout llPendidikan;
        @BindView(R.id.et_pelatihan)
        EditText etPelatihan;
        @BindView(R.id.et_tahun_pelatihan)
        EditText etTahun;
        @BindView(R.id.fab_add_pelatihan)
        FloatingActionButton fabAdd;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            llPendidikan.setVisibility(View.GONE);
            fabAdd.setVisibility(View.GONE);
        }
    }

    public interface ItemClickListener{
        void onItemClickId(String id);
    }
}
