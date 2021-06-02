package com.roma.android.sihmi.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.entity.AgendaComment;
import com.roma.android.sihmi.utils.Tools;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    Context context;
    List<AgendaComment> list;

    public CommentAdapter(Context context, List<AgendaComment> list) {
        this.context = context;
        this.list = list;
    }

    public void updateData(List<AgendaComment> agendaList){
        list = agendaList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_comment, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        AgendaComment agendaComment = list.get(i);
        viewHolder.tvTime.setText(Tools.getDateTimeLaporanFromMillis(agendaComment.getTime()));
        viewHolder.tvUsername.setText(agendaComment.getUsername());
        viewHolder.tvComment.setText(agendaComment.getMessage());
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
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.tv_username)
        TextView tvUsername;
        @BindView(R.id.tv_comment)
        TextView tvComment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
