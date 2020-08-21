package com.populstay.populife.maintservice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.populstay.populife.R;
import com.populstay.populife.maintservice.entity.MaintRequest;
import com.populstay.populife.ui.recycler.HeaderAndFooterAdapter;
import com.populstay.populife.ui.recycler.ViewHolder;
import com.populstay.populife.util.date.DateUtil;

import java.util.List;


public class MaintRequestListAdapter extends HeaderAndFooterAdapter<MaintRequest> {

    private Context mContext;

    public MaintRequestListAdapter(Context context, List<MaintRequest> list) {

        super(list);
        mContext = context;
    }

    @Override
    public ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.maint_request_list_item, parent, false);
        return new KeyPwdViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(ViewHolder holder, int position, final MaintRequest item) {

        final KeyPwdViewHolder videoViewHolder = (KeyPwdViewHolder) holder;
        videoViewHolder.tvApplyNo.setText(item.getApplyNo());
        videoViewHolder.tvPurchasedDate.setText(DateUtil.getDateToString(item.getPurchasedDate(), DateUtil.DATE_TIME_PATTERN_3));
        videoViewHolder.tvDescription.setText(item.getDescription());

    }

    class KeyPwdViewHolder extends ViewHolder {
        TextView tvApplyNo, tvPurchasedDate, tvDescription;

        public KeyPwdViewHolder(View itemView) {
            super(itemView);
            tvApplyNo = itemView.findViewById(R.id.tvApplyNo);
            tvPurchasedDate = itemView.findViewById(R.id.tvPurchasedDate);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }
    }
}
