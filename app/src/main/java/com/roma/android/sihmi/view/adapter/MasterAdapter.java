package com.roma.android.sihmi.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.entity.Master;
import com.roma.android.sihmi.utils.Constant;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MasterAdapter extends RecyclerView.Adapter<MasterAdapter.ViewHolder> {
    Context context;
    ItemClickListener listener;
    List<Master> list;

    public void updateData(List<Master> agendaList){
        list = agendaList;
        notifyDataSetChanged();

    }

    public MasterAdapter(Context context, List<Master> list, ItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_master, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        Master master = list.get(i);
        viewHolder.tvValue.setText(master.getValue());
        viewHolder.imgEdit.setOnClickListener(v -> {
            listener.onItemClickId(Constant.UBAH, master);
        });
        viewHolder.imgDelete.setOnClickListener(v -> {
            listener.onItemClickId(Constant.HAPUS, master);
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_value)
        TextView tvValue;
        @BindView(R.id.img_edit)
        ImageView imgEdit;
        @BindView(R.id.img_delete)
        ImageView imgDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface ItemClickListener{
        void onItemClickId(String type, Master master);
    }
}
