package com.roma.android.sihmi.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.entity.Master;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AlamatMasterAdapter extends RecyclerView.Adapter<AlamatMasterAdapter.ViewHolder> {
    Context context;
    ItemClickListener listener;
    List<Master> list;

    public void updateData(List<Master> agendaList){
        list = agendaList;
        notifyDataSetChanged();

    }

    public AlamatMasterAdapter(Context context, List<Master> list, ItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_address_master, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        Master master = list.get(i);
        viewHolder.tvValue.setText(master.getValue());
        viewHolder.itemView.setOnClickListener(v -> {
            listener.onItemClickId(master);
        });

        if (master.isAvailableAddres()){
            viewHolder.imgCheck.setVisibility(View.GONE);
        } else {
            viewHolder.imgCheck.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_value)
        TextView tvValue;
        @BindView(R.id.img_check)
        ImageView imgCheck;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface ItemClickListener{
        void onItemClickId(Master master);
    }
}
