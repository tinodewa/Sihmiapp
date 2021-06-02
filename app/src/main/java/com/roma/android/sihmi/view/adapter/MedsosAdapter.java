package com.roma.android.sihmi.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.entity.Medsos;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MedsosAdapter extends RecyclerView.Adapter<MedsosAdapter.ViewHolder> {
    Context context;
    ItemClickListener listener;
    List<Medsos> list;

    public void updateData(List<Medsos> agendaList){
        list = agendaList;
        notifyDataSetChanged();

    }

    public MedsosAdapter(Context context, List<Medsos> list, ItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_medsos, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        Medsos medsos = list.get(i);
        viewHolder.etMedsos.setText(medsos.getMedsos());
        viewHolder.etUsername.setText(medsos.getUsername());
        viewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClickId(medsos.get_id());
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.et_medsos)
        EditText etMedsos;
        @BindView(R.id.et_username)
        EditText etUsername;
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
