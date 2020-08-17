package com.populstay.populife.maintservice.adapter;

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

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {

    private List<HomeDevice> mDatas;
    private Context mContext;
    private int mSelectedPosition;
    private ProductListAdapter.OnItemClickListener mOnItemClickListener;

    public ProductListAdapter(List<HomeDevice> mDatas, Context mContext) {
        this.mDatas = mDatas;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ProductListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.product_list_card_item, viewGroup,false);
        return new ProductListAdapter.ViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return CollectionUtil.isEmpty(mDatas) ? 0 : mDatas.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final ProductListAdapter.ViewHolder viewHolder, int position) {

        HomeDevice item = mDatas.get(position);

        viewHolder.tvDeviceName.setText(HomeDeviceInfo.getTypeNameByName(item.getName()));
        viewHolder.tvDeviceName.setTextColor(mContext.getResources().getColor(R.color.device_card_text_color_inactive));
        viewHolder.ivDeviceIcon.setImageResource(HomeDeviceInfo.getProductPictureByName(item.getName()));

        viewHolder.itemView.setBackgroundResource(mSelectedPosition == position ? R.drawable.product_card_two_bg : R.drawable.device_card_two_bg);
        viewHolder.ivCheckIcon.setVisibility(mSelectedPosition == position ? View.VISIBLE : View.GONE);

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
        public ImageView ivCheckIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDeviceName = itemView.findViewById(R.id.tv_device_name);
            ivDeviceIcon = itemView.findViewById(R.id.iv_device_icon);
            ivCheckIcon = itemView.findViewById(R.id.iv_check_icon);
        }
    }

    public void selectItem(int position){
        mSelectedPosition = position;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener{
        void onItemClick(View v, int position);
    }

    public void setOnItemClickListener(ProductListAdapter.OnItemClickListener l){
        mOnItemClickListener = l;
    }

}
