package com.populstay.populife.find.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.populstay.populife.R;
import com.populstay.populife.find.entity.UserManual;
import com.populstay.populife.find.entity.UserManualInfo;
import com.populstay.populife.util.CollectionUtil;

import java.util.List;

public class UserManualListAdapter extends RecyclerView.Adapter<UserManualListAdapter.ViewHolder> {

    private List<UserManual> mDatas;
    private Context mContext;
    private int mSelectedPosition = -1;
    private UserManualListAdapter.OnItemClickListener mOnItemClickListener;

    public UserManualListAdapter(List<UserManual> mDatas, Context mContext) {
        this.mDatas = mDatas;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public UserManualListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.user_manual_list_card_item, viewGroup,false);
        return new UserManualListAdapter.ViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return CollectionUtil.isEmpty(mDatas) ? 0 : mDatas.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final UserManualListAdapter.ViewHolder viewHolder, int position) {

        UserManual item = mDatas.get(position);
        viewHolder.tvDeviceName.setText(UserManualInfo.getNameByType(item.getType()));
        viewHolder.ivDeviceIcon.setImageResource(UserManualInfo.getIconByType(item.getType()));

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnItemClickListener){
                    mOnItemClickListener.onItemClick(v, viewHolder.getAdapterPosition());
                }
            }
        });

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvDeviceName;
        public ImageView ivDeviceIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDeviceName = itemView.findViewById(R.id.tv_device_name);
            ivDeviceIcon = itemView.findViewById(R.id.iv_device_icon);
        }
    }

    public void selectItem(int position){
        mSelectedPosition = position;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener{
        void onItemClick(View v, int position);
    }

    public void setOnItemClickListener(UserManualListAdapter.OnItemClickListener l){
        mOnItemClickListener = l;
    }

}
