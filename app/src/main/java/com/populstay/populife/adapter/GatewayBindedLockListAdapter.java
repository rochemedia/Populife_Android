package com.populstay.populife.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.populstay.populife.R;
import com.populstay.populife.entity.GatewayBindedLock;

import java.util.List;

/**
 * Created by Jerry
 */

public class GatewayBindedLockListAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private List<GatewayBindedLock> mLockList;

	public GatewayBindedLockListAdapter(Context context, List<GatewayBindedLock> lockList) {
		mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
		mLockList = lockList;
	}

	// 获取数量
	public int getCount() {
		return mLockList.size();
	}

	// 获取当前选项
	public Object getItem(int position) {
		return mLockList.get(position);
	}

	// 获取当前选项的 id
	public long getItemId(int position) {
		return position;
	}

	// 获取 View
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.item_gateway_binded_lock_list, null);
			holder = new ViewHolder();

			holder.name = convertView.findViewById(R.id.tv_item_gateway_binded_lock_name);
			holder.signal = convertView.findViewById(R.id.tv_item_gateway_binded_lock_signal);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Resources res = mContext.getResources();
		GatewayBindedLock lock = mLockList.get(position);
		holder.name.setText(lock.getAlias());
		int signal = lock.getSignal();
		if (signal >= -75) {//信号强
			holder.signal.setText(R.string.signal_strong);
			holder.signal.setCompoundDrawablesWithIntrinsicBounds(
					res.getDrawable(R.drawable.ic_gateway_lock_signal_strong), null, null, null);
		} else if (signal >= -85) {//信号中等
			holder.signal.setText(R.string.signal_medium);
			holder.signal.setCompoundDrawablesWithIntrinsicBounds(
					res.getDrawable(R.drawable.ic_gateway_lock_signal_medium), null, null, null);
		} else {//信号弱
			holder.signal.setText(R.string.signal_weak);
			holder.signal.setCompoundDrawablesWithIntrinsicBounds(
					res.getDrawable(R.drawable.ic_gateway_lock_signal_weak), null, null, null);
		}
		return convertView;
	}

	class ViewHolder {
		TextView name, signal;
	}
}
