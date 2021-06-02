package com.roma.android.sihmi.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Contact;
import com.roma.android.sihmi.model.database.interfaceDao.LevelDao;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Tools;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class LaporanUserAdapter extends RecyclerView.Adapter<LaporanUserAdapter.ViewHolder> {
    Context context;
    List<Contact> list;
    int type;
    boolean viewMore;
    LevelDao levelDao;
    //    ItemClickListener listener;

    public LaporanUserAdapter(Context context, List<Contact> list, int type, boolean viewMore) {
        this.context = context;
        this.list = list;
        this.type = type;
        this.viewMore = viewMore;
        levelDao = AppDb.getInstance(context).levelDao();
    }


    public void updateData(List<Contact> agendaList){
        list = agendaList;
        notifyDataSetChanged();
    }

//    public LaporanAdapter(Context context, List<Job> list, ItemClickListener listener) {
//        this.context = context;
//        this.list = list;
//        this.listener = listener;
//    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_laporan, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        Contact contact = list.get(i);
        if (type == Constant.LAP_AKTIVITAS_PENGGUNA) {
            String tglDftar, lastLogin;
            try {
                if (contact.getTanggal_daftar() == null || contact.getTanggal_daftar().isEmpty()) {
                    tglDftar = "01/01/2019";
                } else {
                    tglDftar = Tools.getDateLaporanFromMillis(Long.parseLong(contact.getTanggal_daftar()));
                }
            } catch (Exception e){
                e.printStackTrace();
                tglDftar = "01/01/2019";
            }

            try {
                if (contact.getLast_login() == null || contact.getLast_login().isEmpty()){
                    lastLogin = Tools.getDateTimeLaporanFromMillis(Long.parseLong(contact.getTanggal_daftar()));
                } else {
                    lastLogin = Tools.getDateTimeLaporanFromMillis(Long.parseLong(contact.getLast_login()));
                }
            } catch (Exception e) {
                e.printStackTrace();
                lastLogin = "01/01/2019, 09:00";
            }

            viewHolder.tvCol1.setText(contact.getFullName());
            viewHolder.tvCol2.setText(tglDftar);
            viewHolder.tvCol3.setText(lastLogin);
            viewHolder.tvCol4.setVisibility(View.GONE);
            viewHolder.tvCol5.setVisibility(View.GONE);
        } else if (type == Constant.LAP_MEDIA_PENGGUNA){
            String device;
            if (contact.getDevice_name() == null || contact.getDevice_name().isEmpty()){
                device = "Belum Ada Device";
            } else {
                device = contact.getDevice_name();
            }
            viewHolder.tvCol1.setText(contact.getFullName());
            viewHolder.tvCol2.setText(device);
            viewHolder.tvCol3.setVisibility(View.GONE);
            viewHolder.tvCol4.setVisibility(View.GONE);
            viewHolder.tvCol5.setVisibility(View.GONE);
        } else if (type == Constant.LAP_ADMIN_AKTIF){
            String status;
            if (contact.getStatus_online() == 0){
                status = "Offline";
            } else {
                status = "Online";
            }
            viewHolder.tvCol1.setText(contact.getFullName());
            viewHolder.tvCol2.setText(levelDao.getNamaLevel(contact.getId_roles()));
            viewHolder.tvCol3.setText(status);
            viewHolder.tvCol4.setVisibility(View.GONE);
            viewHolder.tvCol5.setVisibility(View.GONE);
        } else if (type == Constant.LAP_KONTEN_AGENDA){
            viewHolder.tvCol1.setText(contact.getKeterangan());
            viewHolder.tvCol2.setText(contact.getNama_depan());
            viewHolder.tvCol2.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            viewHolder.tvCol3.setVisibility(View.GONE);
            viewHolder.tvCol4.setVisibility(View.GONE);
            viewHolder.tvCol5.setVisibility(View.GONE);
        } else if (type == Constant.LAP_KONTEN_SEJARAH){
            viewHolder.tvCol1.setText(contact.getKeterangan());
            viewHolder.tvCol2.setText(contact.getNama_depan());
            viewHolder.tvCol2.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            viewHolder.tvCol3.setVisibility(View.GONE);
            viewHolder.tvCol4.setVisibility(View.GONE);
            viewHolder.tvCol5.setVisibility(View.GONE);
        } else if (type == Constant.LAP_KONTEN_KONSTITUSI){
            viewHolder.tvCol1.setText(contact.getKeterangan());
            viewHolder.tvCol2.setText(contact.getNama_depan());
            viewHolder.tvCol2.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            viewHolder.tvCol2.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            viewHolder.tvCol3.setVisibility(View.GONE);
            viewHolder.tvCol4.setVisibility(View.GONE);
            viewHolder.tvCol5.setVisibility(View.GONE);
        } else if (type == Constant.LAP_KADER){
            viewHolder.tvCol1.setText(contact.getKeterangan());
            viewHolder.tvCol2.setText(contact.getNama_depan());
            viewHolder.tvCol2.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            viewHolder.tvCol2.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            viewHolder.tvCol3.setVisibility(View.GONE);
            viewHolder.tvCol4.setVisibility(View.GONE);
            viewHolder.tvCol5.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        if (viewMore){
            return list.size();
        } else {
            if (list.size()>5) {
                return 5;
            } else {
                return list.size();
            }
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_col_1)
        TextView tvCol1;
        @BindView(R.id.tv_col_2)
        TextView tvCol2;
        @BindView(R.id.tv_col_3)
        TextView tvCol3;
        @BindView(R.id.tv_col_4)
        TextView tvCol4;
        @BindView(R.id.tv_col_5)
        TextView tvCol5;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface ItemClickListener{
        void onItemClickId(String id);
    }
}
