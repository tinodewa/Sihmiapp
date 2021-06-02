package com.roma.android.sihmi.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.entity.Contact;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    Context context;
    List<Contact> list;
    itemClickListener listener;

    public ContactAdapter(Context context, List<Contact> list) {
        this.context = context;
        this.list = list;
    }

    public ContactAdapter(Context context, List<Contact> list, itemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    public void updateData(List<Contact> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_contact, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final Contact contact = list.get(i);
        viewHolder.tvNama.setText(contact.getFullName());
        viewHolder.tvKomisariat.setText(contact.getKomisariat());

        String firstLetter = String.valueOf(contact.getFullName().charAt(0));
        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        // generate random color
        int color = generator.getColor(firstLetter);
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(firstLetter, color); // radius in px

        try {
            if (contact.getImage() != null && !contact.getImage().isEmpty() && !contact.getImage().equals(" ")) {
                Glide.with(context).load(contact.getImage()).into(viewHolder.ivPhoto);
                viewHolder.ivPhoto.setVisibility(View.VISIBLE);
                viewHolder.ivIntial.setVisibility(View.GONE);
            } else {
                viewHolder.ivIntial.setImageDrawable(drawable);
                viewHolder.ivIntial.setVisibility(View.VISIBLE);
                viewHolder.ivPhoto.setVisibility(View.GONE);
            }
        } catch (NullPointerException e){
            viewHolder.ivIntial.setImageDrawable(drawable);
            viewHolder.ivIntial.setVisibility(View.VISIBLE);
            viewHolder.ivPhoto.setVisibility(View.GONE);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(contact);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.ivPhoto)
        ImageView ivPhoto;
        @BindView(R.id.tvNama)
        TextView tvNama;
        @BindView(R.id.tvKomisariat)
        TextView tvKomisariat;
        @BindView(R.id.ivInitial)
        ImageView ivIntial;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface itemClickListener{
        void onItemClick(Contact contact);
    }
}
