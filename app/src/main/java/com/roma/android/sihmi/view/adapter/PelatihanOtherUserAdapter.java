package com.roma.android.sihmi.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.entity.Training;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PelatihanOtherUserAdapter extends RecyclerView.Adapter<PelatihanOtherUserAdapter.ViewHolder> {
    Context context;
    List<Training> list;


    public PelatihanOtherUserAdapter(Context context, List<Training> list) {
        this.context = context;
        this.list = list;
    }

    public void updateData(List<Training> list){
        this.list = list;
        notifyDataSetChanged();

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_pelatihan_otheruser, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        Training training = list.get(i);
        viewHolder.setIsRecyclable(false);
        viewHolder.etKomisariat.setText(training.getTipe() + " - " + training.getTahun());
    }

    @Override
    public int getItemCount() {
        return list.size() > 0 ? list.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.et_komisariat)
        EditText etKomisariat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
