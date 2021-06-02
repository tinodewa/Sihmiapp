package com.roma.android.sihmi.view.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Agenda;
import com.roma.android.sihmi.model.database.interfaceDao.ContactDao;
import com.roma.android.sihmi.utils.Tools;

import java.util.List;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AgendaAdapter extends RecyclerView.Adapter<AgendaAdapter.ViewHolder> {
    Context context;
    itemClickListener listener;

    List<Agenda> list;
    ContactDao contactDao;

    public void updateData(List<Agenda> agendaList){
        list.clear();
        list = agendaList;
        notifyDataSetChanged();
    }

    public AgendaAdapter(Context context, List<Agenda> list, itemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
        contactDao = AppDb.getInstance(context).contactDao();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_agenda, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final Agenda agenda = list.get(i);
        viewHolder.tvNama.setText(agenda.getNama());
        String[] desk = agenda.getType().split("-");
        viewHolder.tvDesc.setText(desk[1]);
        viewHolder.tvDate.setText(Tools.getDateLaporanFromMillis(agenda.getDate_created()));
        viewHolder.tvCreator.setText(contactDao.getContactById(agenda.getId_user()).getFullName());
        if (agenda.getImage() != null && !agenda.getImage().isEmpty()) {
            Glide.with(context).load(agenda.getImage()).into(viewHolder.ivPhoto);
        } else {
            viewHolder.ivPhoto.setImageDrawable(context.getResources().getDrawable(R.drawable.icn_agenda1));
        }

        if (agenda.isReminder()){
            viewHolder.imgReminder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_notifications));

            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = context.getTheme();
            theme.resolveAttribute(R.attr.textcolor, typedValue, true);
            @ColorInt int textColor = typedValue.data;

            viewHolder.imgReminder.setColorFilter(textColor);
        } else {
            viewHolder.imgReminder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_notifications_off_black));

            @ColorInt int defaultColor = Color.parseColor("#686868");
            viewHolder.imgReminder.setColorFilter(defaultColor);
        }

        viewHolder.itemView.setOnClickListener(v -> listener.onItemClick(agenda, false));

        viewHolder.itemView.setOnLongClickListener(v -> {
            listener.onItemClick(agenda, true);
            return true;
        });

        viewHolder.imgReminder.setOnClickListener(v -> {
            //alarm manager
            Tools.showDialogAgendaRb(context, agenda);
        });

        if (Tools.isSecondAdmin() || Tools.isSuperAdmin()){
            viewHolder.tvCreator.setVisibility(View.VISIBLE);
            viewHolder.tvDate.setVisibility(View.VISIBLE);
            viewHolder.imgReminder.setVisibility(View.GONE);
        } else {
            viewHolder.tvCreator.setVisibility(View.GONE);
            viewHolder.tvDate.setVisibility(View.GONE);
            viewHolder.imgReminder.setVisibility(View.VISIBLE);

        }

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
        @BindView(R.id.tvDate)
        TextView tvDate;
        @BindView(R.id.tvCreator)
        TextView tvCreator;
        @BindView(R.id.ivPhoto)
        ImageView ivPhoto;
        @BindView(R.id.img_reminder)
        ImageView imgReminder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
//            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) itemView.getLayoutParams();
//            params.topMargin = 0;
        }
    }

    public interface itemClickListener{
        void onItemClick(Agenda agenda, boolean isLongClick);
    }
}
