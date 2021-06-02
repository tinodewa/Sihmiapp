package com.roma.android.sihmi.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Konstituisi;
import com.roma.android.sihmi.model.database.interfaceDao.ContactDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Tools;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class KonstituisiAdapter extends RecyclerView.Adapter<KonstituisiAdapter.ViewHolder> {
    Context context;
    List<Konstituisi> list;
    itemClickListener listener;
    int type;
    AppDb appDb;
    ContactDao contactDao;
    UserDao userDao;

    public KonstituisiAdapter(Context context, List<Konstituisi> list, itemClickListener listener, int type) {
        this.context = context;
        this.list = list;
        this.listener = listener;
        this.type = type;
        appDb = AppDb.getInstance(context);
        contactDao = appDb.contactDao();
        userDao = appDb.userDao();
    }

    public void updateData(List<Konstituisi> list1){
        this.list = list1;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view;
        if (type == Constant.LIST) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_konstitusi, viewGroup, false);
        } else {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_grid_konstitusi, viewGroup, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final Konstituisi konstituisi = list.get(i);
        if (Tools.isSuperAdmin() && type == Constant.LIST){
            viewHolder.llInfoSuperAdmin.setVisibility(View.VISIBLE);
        } else {
            viewHolder.llInfoSuperAdmin.setVisibility(View.GONE);
        }
        viewHolder.tvNama.setText(String.valueOf(konstituisi.getNama()));
        viewHolder.tvDesc.setText(String.valueOf(konstituisi.getDeskripsi()));
        viewHolder.ivPhoto.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_png));
        viewHolder.tvDate.setText(Tools.getDateLaporanFromMillis(Long.parseLong(konstituisi.getDate_created())));
        viewHolder.tvCreator.setText(type(konstituisi)+" - "+contactDao.getContactById(konstituisi.getId_user()).getNama_depan());
        viewHolder.itemView.setOnClickListener(v -> listener.onItemClick(konstituisi, false));

        viewHolder.itemView.setOnLongClickListener(v -> {
            listener.onItemClick(konstituisi, true);
            return true;
        });

    }

    private String type (Konstituisi konstituisi){
        String tipe;
        String type[] = konstituisi.getType().split("-");
        if (type[0].contains("0")){
            tipe = "PB HMI";
        } else if (type[0].contains("1")){
            tipe = userDao.getUser().getCabang();
        } else if (type[0].contains("2")){
            tipe = userDao.getUser().getKomisariat();
        } else {
            tipe = "tidak diketahui";
        }
        return tipe;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (type == Constant.LIST){
            return Constant.LIST;
        } else {
            return Constant.GRID;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tvNama)
        TextView tvNama;
        @BindView(R.id.tvDesc)
        TextView tvDesc;
        @BindView(R.id.ivPhoto)
        ImageView ivPhoto;
        @BindView(R.id.llInfoSuperAdmin)
        LinearLayout llInfoSuperAdmin;
        @BindView(R.id.tvDate)
        TextView tvDate;
        @BindView(R.id.tvCreator)
        TextView tvCreator;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface itemClickListener{
        void onItemClick(Konstituisi konstituisi, boolean isLongClick);
    }
}
