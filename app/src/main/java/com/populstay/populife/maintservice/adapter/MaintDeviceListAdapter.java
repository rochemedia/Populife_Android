package com.populstay.populife.maintservice.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.populstay.populife.R;
import com.populstay.populife.maintservice.entity.MaintDevice;
import com.populstay.populife.ui.recycler.HeaderAndFooterAdapter;
import com.populstay.populife.ui.recycler.ViewHolder;

import java.util.List;


public class MaintDeviceListAdapter extends HeaderAndFooterAdapter<MaintDevice> {

    private Context mContext;

    public MaintDeviceListAdapter(Context context, List<MaintDevice> list) {

        super(list);
        mContext = context;
    }

    @Override
    public ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.maint_device_list_item, parent, false);
        return new MaintDeviceViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(ViewHolder holder, int position, final MaintDevice item) {

        final MaintDeviceViewHolder videoViewHolder = (MaintDeviceViewHolder) holder;
        videoViewHolder.tvName.setText(!TextUtils.isEmpty(item.getName()) ? item.getName() : "");
        videoViewHolder.tvBuyStatus.setText(videoViewHolder.tvPrice.getContext().getString(item.isBuyed() ? R.string.bought : R.string.not_to_buy));
        videoViewHolder.tvAlias.setText(!TextUtils.isEmpty(item.getAlias()) ? item.getAlias() : "");
        videoViewHolder.tvPrice.setText(String.format(videoViewHolder.tvPrice.getContext().getString(R.string.price),item.getFee()));

        int type = item.getType();
        if (MaintDevice.DeviceType.KEY_BOX == type){
            videoViewHolder.ivIcon.setImageResource(R.drawable.keybox_inactive);
        }else if (MaintDevice.DeviceType.DEADBOLT == type){
            videoViewHolder.ivIcon.setImageResource(R.drawable.deadbolt_inactive);
        }else if (MaintDevice.DeviceType.GATEWAY == type){
            videoViewHolder.ivIcon.setImageResource(R.drawable.gateway_inactive);
        }
    }

    class MaintDeviceViewHolder extends ViewHolder {
        TextView tvName, tvBuyStatus, tvAlias, tvPrice;
        ImageView ivIcon;

        public MaintDeviceViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvBuyStatus = itemView.findViewById(R.id.tvBuyStatus);
            tvAlias = itemView.findViewById(R.id.tvAlias);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            ivIcon = itemView.findViewById(R.id.ivIcon);
        }
    }
}
