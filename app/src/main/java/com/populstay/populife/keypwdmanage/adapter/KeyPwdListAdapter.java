package com.populstay.populife.keypwdmanage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.populstay.populife.R;
import com.populstay.populife.keypwdmanage.entity.KeyPwd;
import com.populstay.populife.ui.recycler.HeaderAndFooterAdapter;
import com.populstay.populife.ui.recycler.ViewHolder;

import java.util.List;


public class KeyPwdListAdapter extends HeaderAndFooterAdapter<KeyPwd> {

    private Context mContext;

    public KeyPwdListAdapter(Context context, List<KeyPwd> list) {

        super(list);
        mContext = context;
    }

    @Override
    public ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.key_pwd_list_item, parent, false);
        return new KeyPwdViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(ViewHolder holder, int position, KeyPwd item) {

        KeyPwdViewHolder videoViewHolder = (KeyPwdViewHolder) holder;
        videoViewHolder.tvName.setText(item.getSendUser());

        // 蓝牙
        if (1 == item.getKeyType()){

        }else {
            // 自定义密码       15
            //One-time			1
            //Permanent		    2
            //Period			3
            if (15 == item.getKeyboardPwdType()){

            }else if (1 == item.getKeyboardPwdType()){

            }else if (2 == item.getKeyboardPwdType()){

            }else if (3 == item.getKeyboardPwdType()){

            }

        }

    }

    class KeyPwdViewHolder extends ViewHolder {
        TextView tvName, tvTime;
        LinearLayout llTimeRange;
        TextView tvStartTime, tvEndTime;
        TextView tvPwd;
        LinearLayout llActionBtns;
        ImageView ivDelete, ivShare, ivEdit;

        public KeyPwdViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvTime = itemView.findViewById(R.id.tv_time);
            llTimeRange = itemView.findViewById(R.id.ll_time_range);
            tvStartTime = itemView.findViewById(R.id.tv_start_time);
            tvEndTime = itemView.findViewById(R.id.tv_end_time);
            tvPwd = itemView.findViewById(R.id.tv_pwd);
            llActionBtns = itemView.findViewById(R.id.ll_action_btns);
            ivDelete = itemView.findViewById(R.id.iv_delete);
            ivShare = itemView.findViewById(R.id.iv_share);
            ivEdit = itemView.findViewById(R.id.iv_edit);
        }
    }
}
