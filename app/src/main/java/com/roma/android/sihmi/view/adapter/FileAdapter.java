package com.roma.android.sihmi.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.entity.File;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {
    Context context;
    List<File> list;
    itemClickListener listener;

    public FileAdapter(Context context, List<File> list, itemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final File file = list.get(i);
        viewHolder.tvNama.setText(String.valueOf(file.getTitle()));
        viewHolder.tvDesc.setText(String.valueOf(file.getPath()));
        viewHolder.tvTime.setVisibility(View.GONE);
        viewHolder.ivPhoto.setImageDrawable(context.getResources().getDrawable(R.drawable.icn_pdf));
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(file);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tvNama)
        TextView tvNama;
        @BindView(R.id.tvTime)
        TextView tvTime;
        @BindView(R.id.tvDesc)
        TextView tvDesc;
        @BindView(R.id.ivPhoto)
        ImageView ivPhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface itemClickListener{
        void onItemClick(File file);
    }
}
