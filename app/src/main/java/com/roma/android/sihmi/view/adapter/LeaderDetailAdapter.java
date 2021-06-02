package com.roma.android.sihmi.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.entity.Leader;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Tools;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class LeaderDetailAdapter extends RecyclerView.Adapter<LeaderDetailAdapter.ViewHolder> {
    Context context;
    List<Leader> list;
    itemClickListener listener;
    private boolean allowUpdate, allowDelete;

    public LeaderDetailAdapter(Context context, itemClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.allowDelete = false;
        this.allowUpdate = false;
    }

    public boolean isAllowUpdate() {
        return allowUpdate;
    }

    public void setAllowUpdate(boolean allowUpdate) {
        this.allowUpdate = allowUpdate;
    }

    public boolean isAllowDelete() {
        return allowDelete;
    }

    public void setAllowDelete(boolean allowDelete) {
        this.allowDelete = allowDelete;
    }

    public LeaderDetailAdapter(Context context, List<Leader> list, itemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    public void updateData(List<Leader> leaders){
        list = leaders;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_leader, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final Leader leader = list.get(i);
        String[] type = leader.getType().split("-");
        viewHolder.tvNama.setText(leader.getNama());
        viewHolder.tvPeriode.setText(leader.getPeriode()+" - "+leader.getSampai());
        viewHolder.tvKet.setText(type[1]);

        if (allowUpdate(leader)){
            viewHolder.fadEdit.setVisibility(View.VISIBLE);
            if (allowDelete()) {
                viewHolder.fabDelete.setVisibility(View.VISIBLE);
            } else {
                viewHolder.fabDelete.setVisibility(View.GONE);
            }
        } else {
            viewHolder.fadEdit.setVisibility(View.GONE);
            viewHolder.fabDelete.setVisibility(View.GONE);
        }

        if (leader.getImage() != null || !leader.getImage().isEmpty()) {
            Glide.with(context).load(leader.getImage()).into(viewHolder.ivCirclePhoto);
            viewHolder.ivCirclePhoto.setVisibility(View.VISIBLE);
            viewHolder.ivPhoto.setVisibility(View.GONE);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(leader, Constant.READ);
            }
        });

        viewHolder.fadEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(leader, Constant.UPDATE);
            }
        });

        viewHolder.fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(leader, Constant.DELETE);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private boolean allowUpdate(Leader leader){
        if (Tools.isSecondAdmin() || Tools.isSuperAdmin() || allowUpdate){
            return true;
        } else {
            return false;
        }
    }

    private boolean allowDelete(){
        if (Tools.isSecondAdmin() || Tools.isSuperAdmin() || allowDelete) {
            return true;
        } else {
            return false;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tvNama)
        TextView tvNama;
        @BindView(R.id.tvPeriode)
        TextView tvPeriode;
        @BindView(R.id.tvKet)
        TextView tvKet;
        @BindView(R.id.iv_photo)
        ImageView ivPhoto;
        @BindView(R.id.iv_circlephoto)
        CircleImageView ivCirclePhoto;
        @BindView(R.id.fab_delete)
        FloatingActionButton fabDelete;
        @BindView(R.id.fab_edit)
        FloatingActionButton fadEdit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface itemClickListener{
        void onItemClick(Leader leader, int type);
    }
}
