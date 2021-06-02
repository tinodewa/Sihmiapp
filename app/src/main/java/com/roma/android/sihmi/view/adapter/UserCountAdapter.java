package com.roma.android.sihmi.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.entity.UserCount;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class UserCountAdapter extends RecyclerView.Adapter<UserCountAdapter.ViewHolder> {
    private List<UserCount> userCountList;
    private boolean hideNumber;
    private OnItemClick onItemClick;

    public UserCountAdapter(List<UserCount> userCountList, boolean hideNumber) {
        this.userCountList = userCountList;
        this.hideNumber = hideNumber;
        onItemClick = null;
    }

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cabang_total, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserCount userCount = userCountList.get(position);

        if (!hideNumber) {
            String numberText = String.valueOf(position+1) + ". ";
            holder.tvNumber.setText(numberText);
            holder.tvNumber.setVisibility(View.VISIBLE);
        }
        else {
            holder.tvNumber.setVisibility(View.GONE);
        }

        holder.tvMasterValue.setText(userCount.getValue());

        holder.tvTotal.setText(userCount.getUserCountStr());

        if (onItemClick != null) {
            holder.itemView.setOnClickListener(v -> {
                onItemClick.onClick(userCount.getValue());
            });
        }
    }

    @Override
    public int getItemCount() {
        return userCountList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_number)
        TextView tvNumber;
        @BindView(R.id.tv_master_value)
        TextView tvMasterValue;
        @BindView(R.id.tv_total)
        TextView tvTotal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemClick{
        void onClick(String value);
    }
}
