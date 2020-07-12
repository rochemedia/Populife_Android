package com.populstay.populife.home.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.populstay.populife.R;
import com.populstay.populife.home.entity.Home;
import com.populstay.populife.util.CollectionUtil;

import java.util.List;

public class HomeListAdapter extends RecyclerView.Adapter<HomeListAdapter.ViewHolder> {

    private List<Home> mDatas;
    private Context mContext;
    private int mShowType = SHOW_TYPE_NORMAL;
    public static final int SHOW_TYPE_NORMAL = 0;
    public static final int SHOW_TYPE_CARD = 1;
    private int mSelectedPosition;
    private OnItemClickListener mOnItemClickListener;

    public HomeListAdapter(List<Home> mDatas, Context mContext, int mShowType) {
        this.mDatas = mDatas;
        this.mContext = mContext;
        this.mShowType = mShowType;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(mContext).inflate(mShowType == SHOW_TYPE_NORMAL ? R.layout.space_list_item : R.layout.space_list_card_item, viewGroup,false);
        return new ViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return CollectionUtil.isEmpty(mDatas) ? 0 : mDatas.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {

        Home item = mDatas.get(position);
        viewHolder.tvHomeName.setText(item.getName());
        viewHolder.tvHomeCount.setText(String.format(mContext.getResources().getString(R.string.device_num_match),item.getLockCount()));
        if (SHOW_TYPE_CARD == mShowType){
            viewHolder.ivHomeSelectedIcon.setVisibility(mSelectedPosition == position ? View.VISIBLE : View.GONE);
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

        public TextView tvHomeName;
        public TextView tvHomeCount;
        public ImageView ivHomeSelectedIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHomeName = itemView.findViewById(R.id.tv_space_name);
            tvHomeCount = itemView.findViewById(R.id.tv_home_count);
            ivHomeSelectedIcon = itemView.findViewById(R.id.iv_home_selected_icon);
        }
    }

    public void selectItem(int position){
        mSelectedPosition = position;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener{
        void onItemClick(View v, int position);
    }

    public void setOnItemClickListener(OnItemClickListener l){
        mOnItemClickListener = l;
    }

}
