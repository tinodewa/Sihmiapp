package com.roma.android.sihmi.view.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Contact;
import com.roma.android.sihmi.model.database.entity.PengajuanHistory;
import com.roma.android.sihmi.model.database.entity.PengajuanHistoryJoin;
import com.roma.android.sihmi.model.database.interfaceDao.ContactDao;
import com.roma.android.sihmi.model.database.interfaceDao.LevelDao;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Tools;

import java.text.ParseException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PermintaanAdapter extends RecyclerView.Adapter<PermintaanAdapter.ViewHolder> {
    Context context;
    List<PengajuanHistoryJoin> list;
    itemClickListener listener;
    AppDb appDb;
    ContactDao contactDao;
    LevelDao levelDao;

    public PermintaanAdapter(Context context, List<PengajuanHistoryJoin> list, itemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
        appDb = AppDb.getInstance(context);
        contactDao = appDb.contactDao();
        levelDao = appDb.levelDao();
    }

    public void updateData(List<PengajuanHistoryJoin> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_pengguna, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        PengajuanHistory pengajuanHistory = list.get(i);
        Contact c = contactDao.getContactById(pengajuanHistory.getCreated_by());

        viewHolder.tvNama.setText(c.getNama_depan());
        if (pengajuanHistory.getTanggal_lk1().trim().isEmpty()) {
            viewHolder.tvDate.setText(Tools.getDateLaporanFromMillis(pengajuanHistory.getDate_created()));
        }
        else {
            long dateLong;
            try {
                dateLong = Tools.getMillisFromTimeStr(pengajuanHistory.getTanggal_lk1().trim(), "dd-MM-yyyy");
            } catch (ParseException e) {
                dateLong = System.currentTimeMillis();
                e.printStackTrace();
            }
            viewHolder.tvDate.setText(Tools.getDateLaporanFromMillis(dateLong));
        }
//        viewHolder.tvDate.setText(Tools.getDateLaporanFromMillis(System.currentTimeMillis()));
        if (pengajuanHistory.getStatus() == 0) {
            viewHolder.tvKet.setText(levelDao.getNamaLevel(pengajuanHistory.getId_roles()));
        } else if (pengajuanHistory.getStatus() == -1) {
            viewHolder.tvKet.setText("User " + levelDao.getNamaLevel(pengajuanHistory.getId_roles()));
        }

        if (c.getImage() != null && !c.getImage().trim().isEmpty()){
            Glide.with(context)
                    .load(Uri.parse(c.getImage()))
                    .into(viewHolder.ivPhoto);
            viewHolder.ivInitial.setVisibility(View.GONE);
            viewHolder.ivPhoto.setVisibility(View.VISIBLE);
        } else {
            Tools.initial(viewHolder.ivInitial, c.getNama_depan());
            viewHolder.ivInitial.setVisibility(View.VISIBLE);
            viewHolder.ivPhoto.setVisibility(View.GONE);
        }

        viewHolder.itemView.setOnClickListener(v -> {
            if (pengajuanHistory.getLevel() > 3) {
                listener.onItemClick(Constant.DOCUMENT, pengajuanHistory.get_id()+"-"+pengajuanHistory.getFile(), c, pengajuanHistory);
            }
        });

        viewHolder.ivDetail.setOnClickListener(v -> {
            listener.onItemClick(Constant.LIHAT, pengajuanHistory.get_id(), c, pengajuanHistory);
        });

        viewHolder.itemView.setOnClickListener(v -> {
            listener.onItemClick(Constant.LIHAT, pengajuanHistory.get_id(), c, pengajuanHistory);
        });

        viewHolder.aSwitch.setOnClickListener(v -> {
            viewHolder.aSwitch.setChecked(false);
            if (pengajuanHistory.getLevel() != 1 && pengajuanHistory.getStatus() != -1) {
                if (pengajuanHistory.getLevel() == 2 && pengajuanHistory.getStatus() == -1){
                    Tools.showDialogCustom(context, "Konfirmasi", context.getString(R.string.konfirm_akses_hapus_ket), Constant.LIHAT, ket -> {
                        listener.onItemClick(Constant.HAPUS, pengajuanHistory.get_id(), c, pengajuanHistory);
                        viewHolder.aSwitch.setChecked(false);
                    });
                } else {
                    Tools.showDialogCustom(context, "Konfirmasi", context.getString(R.string.konfirm_akses_ket), Constant.LIHAT, ket -> {
                        listener.onItemClick(Constant.UBAH, pengajuanHistory.get_id(), c, pengajuanHistory);
                    });
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size() > 0 ? list.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_nama)
        TextView tvNama;
        @BindView(R.id.tv_ket)
        TextView tvKet;
        @BindView(R.id.tv_date)
        TextView tvDate;
        @BindView(R.id.iv_photo)
        ImageView ivPhoto;
        @BindView(R.id.iv_initial)
        ImageView ivInitial;
        @BindView(R.id.iv_detail)
        ImageView ivDetail;
        @BindView(R.id.switch_admin)
        SwitchCompat aSwitch;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface itemClickListener{
        void onItemClick(String type, String id_pengajuan, Contact contact, PengajuanHistory pengajuanHistory);
    }
}
