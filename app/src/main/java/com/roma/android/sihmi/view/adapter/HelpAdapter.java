package com.roma.android.sihmi.view.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.entity.AboutUs;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class HelpAdapter extends RecyclerView.Adapter<HelpAdapter.ViewHolder> {
    private static final String TAG = "HelpAdapter";
    Context context;
    List<AboutUs> list;
    itemClickListener listener;

    int currentPosition = 101;

    public HelpAdapter(Context context, List<AboutUs> list, itemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    public void updateData(List<AboutUs> list1){
        list.clear();
        list =list1;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_help, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final int position = i;
        final AboutUs aboutUs = list.get(i);

        viewHolder.tvNama.setText(aboutUs.getNama());
        viewHolder.tvDesc.setText(aboutUs.getDeskripsi());
        viewHolder.tvNama.setTextSize(14);
        viewHolder.tvDesc.setTextSize(12);

        Log.d(TAG, "onBindViewHolder: "+currentPosition+" - "+position);

        if (currentPosition != position){
            viewHolder.llExpand.setVisibility(View.GONE);
            viewHolder.ivIcn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_keyboard_arrow_right));
            Log.d(TAG, "onBindViewHolder: !=");
        } else {
            viewHolder.llExpand.setVisibility(View.VISIBLE);
            viewHolder.ivIcn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_keyboard_arrow_down));
            Log.d(TAG, "onBindViewHolder: ==");
        }

//        if (currentPosition == position){
//            viewHolder.llExpand.setVisibility(View.VISIBLE);
//        } else {
//            viewHolder.llExpand.setVisibility(View.GONE);
//        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPosition == position){
                    listener.onItemClick(aboutUs.get_id());
                }
                currentPosition = position;
                notifyDataSetChanged();
            }
        });

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (currentPosition == position){
                    listener.onLongItemClick(aboutUs.get_id());
                }
                return true;
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
        @BindView(R.id.tvDesc)
        TextView tvDesc;
        @BindView(R.id.ivIcn)
        ImageView ivIcn;
        @BindView(R.id.llExpand)
        LinearLayout llExpand;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface itemClickListener{
        void onItemClick(String id);
        void onLongItemClick(String id);
    }
}
