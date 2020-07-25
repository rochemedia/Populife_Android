package com.populstay.populife.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.populstay.populife.R;
import com.populstay.populife.home.entity.HomeDevice;
import com.populstay.populife.home.entity.HomeDeviceInfo;
import com.populstay.populife.util.CollectionUtil;

import java.util.List;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.ViewHolder> {

    private List<HomeDevice> mDatas;
    private Context mContext;
    private int mShowType = SHOW_TYPE_CARD;
    public static final int SHOW_TYPE_CARD = 0;
    public static final int SHOW_TYPE_TWO_CARD = 1;
    private int mSelectedPosition;
    private DeviceListAdapter.OnItemClickListener mOnItemClickListener;
    private int mUseFrom = USE_FROM_DEVICE_LIST;
    public static final int USE_FROM_SELECT_DEVICE_TYPE_LIST = 0;
    public static final int USE_FROM_DEVICE_LIST = 1;

    public DeviceListAdapter(List<HomeDevice> mDatas, Context mContext, int mShowType,  int mUseFrom) {
        this.mDatas = mDatas;
        this.mContext = mContext;
        this.mShowType = mShowType;
        this.mUseFrom = mUseFrom;
    }

    @NonNull
    @Override
    public DeviceListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(mContext).inflate(mShowType == SHOW_TYPE_CARD ? R.layout.device_list_card_item : R.layout.device_list_two_card_item, viewGroup,false);
        return new DeviceListAdapter.ViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return CollectionUtil.isEmpty(mDatas) ? 0 : mDatas.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final DeviceListAdapter.ViewHolder viewHolder, int position) {

        HomeDevice item = mDatas.get(position);
        if (USE_FROM_SELECT_DEVICE_TYPE_LIST == mUseFrom){
            viewHolder.tvDeviceName.setText(HomeDeviceInfo.getTypeNameByName(item.getName()));
        }else {
            viewHolder.tvDeviceName.setText(item.getAlias());
        }
        if (mSelectedPosition == position ){
            viewHolder.tvDeviceName.setTextColor(mContext.getResources().getColor(R.color.device_card_text_color_active));
            viewHolder.ivDeviceIcon.setImageResource(HomeDeviceInfo.getIconActiveByName(item.getName()));
            viewHolder.itemView.setBackgroundResource(mShowType == SHOW_TYPE_CARD ? R.drawable.device_card_single_bg_selected : R.drawable.device_card_two_bg_selected);
        }else {
            viewHolder.tvDeviceName.setTextColor(mContext.getResources().getColor(R.color.device_card_text_color_inactive));
            viewHolder.ivDeviceIcon.setImageResource(HomeDeviceInfo.getIconInactiveByName(item.getName()));
            viewHolder.itemView.setBackgroundResource(mShowType == SHOW_TYPE_CARD ? R.drawable.device_card_single_bg : R.drawable.device_card_two_bg);
        }

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

    public void setOnItemClickListener(DeviceListAdapter.OnItemClickListener l){
        mOnItemClickListener = l;
    }

}
