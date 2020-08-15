package com.populstay.populife.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.populstay.populife.R;
import com.populstay.populife.entity.LockAction;

import java.util.List;

/**
 * Created by Jerry
 */

public class LockActionAdapter extends BaseAdapter {

	private final List<LockAction> mActions;
	private Context mContext;
	private LayoutInflater mLayoutInflater;

	public LockActionAdapter(Context context, List<LockAction> actions) {
		mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
		mActions = actions;
	}

	// 获取数量
	public int getCount() {
		return mActions.size();
	}

	// 获取当前选项
	public Object getItem(int position) {
		return mActions.get(position);
	}

	// 获取当前选项的 id
	public long getItemId(int position) {
		return position;
	}

	// 获取 View
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.item_lock_action, null);
			holder = new ViewHolder();

			holder.icon = convertView.findViewById(R.id.iv_lock_action_icon);
			holder.title = convertView.findViewById(R.id.tv_lock_action_title);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		LockAction lockAction = mActions.get(position);
		Resources res = mContext.getResources();
		int colorDark = res.getColor(R.color.text_gray_dark);
		int colorGray = res.getColor(R.color.text_gray_light);
		boolean isEnable = lockAction.isEnable();

		holder.title.setTextColor(isEnable ? colorDark : colorGray);
		holder.title.setText(lockAction.getTitleResInt());
		holder.icon.setImageResource(lockAction.getIconResInt());
//		holder.itemView.setEnabled(isEnable);
//		holder.itemView.setTag(position);

		return convertView;
	}

	class ViewHolder {
		AppCompatImageView icon;
		AppCompatTextView title;
	}
}
