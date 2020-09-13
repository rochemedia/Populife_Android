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
import com.populstay.populife.ui.widget.extextview.ExTextView;
import com.populstay.populife.util.DensityUtils;
import com.populstay.populife.util.date.DateUtil;

import java.util.List;


public class KeyPwdListAdapter extends HeaderAndFooterAdapter<KeyPwd> {

    private Context mContext;
    private int iconPadding;
    private ListViewActionBtnClickListener mSendBtnClickListener, mDeleteBtnClickListener, mEditBtnClickListener;

    public KeyPwdListAdapter(Context context, List<KeyPwd> list) {

        super(list);
        mContext = context;
        iconPadding = DensityUtils.dp2px(mContext,12);
    }

    @Override
    public ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.key_pwd_list_item, parent, false);
        return new KeyPwdViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(ViewHolder holder, int position, final KeyPwd item) {

        final KeyPwdViewHolder videoViewHolder = (KeyPwdViewHolder) holder;
        videoViewHolder.tvName.setText(item.getSendUser());

        videoViewHolder.tvTime.setVisibility(View.GONE);
        videoViewHolder.llTimeRange.setVisibility(View.GONE);
        videoViewHolder.tvPwd.setVisibility(View.GONE);
        videoViewHolder.llActionBtns.setVisibility(View.GONE);

        videoViewHolder.tvName.setText(item.getAlias());

        // 蓝牙
        if (1 == item.getKeyType()){
            videoViewHolder.tvName.setRawIcon(R.drawable.bt_key_icon, iconPadding);

            videoViewHolder.llTimeRange.setVisibility(View.VISIBLE);
            videoViewHolder.tvStartTime.setText(DateUtil.getDateToStringConvert(item.getStartDate(), DateUtil.DATE_TIME_PATTERN_1));
            videoViewHolder.tvEndTime.setText(DateUtil.getDateToStringConvert(item.getEndDate(), DateUtil.DATE_TIME_PATTERN_1));

            // 待接收才显示分享按钮
            if ("110402".equals(item.getStatus())){
                videoViewHolder.ivShare.setVisibility(View.VISIBLE);
            }else {
                videoViewHolder.ivShare.setVisibility(View.GONE);
            }
        }
        // 密码
        else {
            //2失效不显示分享按钮
            if ("2".equals(item.getStatus())){
                videoViewHolder.ivShare.setVisibility(View.GONE);
            }else {
                videoViewHolder.ivShare.setVisibility(View.VISIBLE);
            }
            videoViewHolder.tvPwd.setVisibility(View.VISIBLE);
            videoViewHolder.tvPwd.setText(item.getKeyboardPwd());
            // 自定义密码       15
            //One-time			1
            //Permanent		    2
            //Period			3
            if (15 == item.getKeyboardPwdType()){
                videoViewHolder.tvName.setRawIcon(R.drawable.pwd_custom_icon, iconPadding);

                videoViewHolder.llTimeRange.setVisibility(View.VISIBLE);
                videoViewHolder.tvStartTime.setText(DateUtil.getDateToString(item.getStartDate(),DateUtil.DATE_TIME_PATTERN_1));
                videoViewHolder.tvEndTime.setText(DateUtil.getDateToString(item.getEndDate(),DateUtil.DATE_TIME_PATTERN_1));

            }else if (1 == item.getKeyboardPwdType()){
                videoViewHolder.tvName.setRawIcon(R.drawable.pwd_one_time_icon, iconPadding);
                videoViewHolder.tvTime.setVisibility(View.VISIBLE);
                videoViewHolder.tvTime.setText(DateUtil.getDateToString(item.getCreateDate(),DateUtil.DATE_TIME_PATTERN_1));

            }else if (2 == item.getKeyboardPwdType()){
                videoViewHolder.tvName.setRawIcon(R.drawable.pwd_permanent_icon, iconPadding);
                videoViewHolder.tvTime.setVisibility(View.VISIBLE);
                videoViewHolder.tvTime.setText(DateUtil.getDateToString(item.getCreateDate(),DateUtil.DATE_TIME_PATTERN_1));

            }else if (3 == item.getKeyboardPwdType()){
                videoViewHolder.tvName.setRawIcon(R.drawable.pwd_period_icon, iconPadding);

                videoViewHolder.llTimeRange.setVisibility(View.VISIBLE);
                videoViewHolder.tvStartTime.setText(DateUtil.getDateToString(item.getStartDate(),DateUtil.DATE_TIME_PATTERN_1));
                videoViewHolder.tvEndTime.setText(DateUtil.getDateToString(item.getEndDate(),DateUtil.DATE_TIME_PATTERN_1));
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (View.VISIBLE != videoViewHolder.llActionBtns.getVisibility()){
                    videoViewHolder.llActionBtns.setVisibility(View.VISIBLE);
                }else {
                    videoViewHolder.llActionBtns.setVisibility(View.GONE);
                }
            }
        });

        videoViewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mDeleteBtnClickListener){
                    mDeleteBtnClickListener.onClick(v, item);
                }
            }
        });

        videoViewHolder.ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mSendBtnClickListener){
                    mSendBtnClickListener.onClick(v, item);
                }
            }
        });

        videoViewHolder.ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mEditBtnClickListener){
                    mEditBtnClickListener.onClick(v, item);
                }
            }
        });


    }

    class KeyPwdViewHolder extends ViewHolder {
        ExTextView tvName;
        TextView tvTime;
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

    public interface ListViewActionBtnClickListener {
        void onClick(View view, KeyPwd item);
    }

    public void setmSendBtnClickListener(ListViewActionBtnClickListener mSendBtnClickListener) {
        this.mSendBtnClickListener = mSendBtnClickListener;
    }

    public void setmDeleteBtnClickListener(ListViewActionBtnClickListener mDeleteBtnClickListener) {
        this.mDeleteBtnClickListener = mDeleteBtnClickListener;
    }

    public void setmEditBtnClickListener(ListViewActionBtnClickListener mEditBtnClickListener) {
        this.mEditBtnClickListener = mEditBtnClickListener;
    }
}
