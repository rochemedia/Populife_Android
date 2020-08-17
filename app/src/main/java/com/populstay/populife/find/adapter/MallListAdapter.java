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
import com.populstay.populife.find.entity.Product;
import com.populstay.populife.find.entity.ProductInfo;
import com.populstay.populife.util.CollectionUtil;

import java.util.List;

public class MallListAdapter extends RecyclerView.Adapter<MallListAdapter.ViewHolder> {

    private List<Product> mDatas;
    private Context mContext;
    private int mSelectedPosition;
    private MallListAdapter.OnItemClickListener mOnItemClickListener;

    public MallListAdapter(List<Product> mDatas, Context mContext) {
        this.mDatas = mDatas;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MallListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.mall_list_card_item, viewGroup,false);
        return new MallListAdapter.ViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return CollectionUtil.isEmpty(mDatas) ? 0 : mDatas.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final MallListAdapter.ViewHolder viewHolder, int position) {

        Product item = mDatas.get(position);
        viewHolder.tvProductName.setText(ProductInfo.getNameByType(item.getType()));

        float priceByType = ProductInfo.getPriceByType(item.getType());
        viewHolder.tvProductPrice.setText(String.format(viewHolder.tvProductPrice.getContext().getString(R.string.product_price),priceByType));

        int productDesc = ProductInfo.getDescByType(item.getType());
        if (-1 != productDesc){
            viewHolder.tvProductDesc.setVisibility(View.VISIBLE);
            viewHolder.tvProductDesc.setText(productDesc);
        }

        viewHolder.ivProductIcon.setImageResource(ProductInfo.getPhotoByType(item.getType()));


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

        public TextView tvProductName;
        public TextView tvProductPrice;
        public TextView tvProductDesc;
        public ImageView ivProductIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price);
            tvProductDesc = itemView.findViewById(R.id.tv_product_desc);
            ivProductIcon = itemView.findViewById(R.id.iv_product_icon);
        }
    }

    public void selectItem(int position){
        mSelectedPosition = position;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener{
        void onItemClick(View v, int position);
    }

    public void setOnItemClickListener(MallListAdapter.OnItemClickListener l){
        mOnItemClickListener = l;
    }

}
