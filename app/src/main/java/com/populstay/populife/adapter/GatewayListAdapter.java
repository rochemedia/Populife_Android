package com.populstay.populife.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.populstay.populife.R;
import com.populstay.populife.entity.Gateway;
import com.populstay.populife.util.string.StringUtil;

import java.util.List;

/**
 * Created by Jerry
 */

public class GatewayListAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private List<Gateway> mGatewayList;

	public GatewayListAdapter(Context context, List<Gateway> gatewayList) {
		mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
		mGatewayList = gatewayList;
	}

	// 获取数量
	public int getCount() {
		return mGatewayList.size();
	}

	// 获取当前选项
	public Object getItem(int position) {
		return mGatewayList.get(position);
	}

	// 获取当前选项的 id
	public long getItemId(int position) {
		return position;
	}

	// 获取 View
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.item_gateway_list, null);
			holder = new ViewHolder();

			holder.name = convertView.findViewById(R.id.tv_item_gateway_name);
			holder.netStatus = convertView.findViewById(R.id.tv_item_gateway_net_status);
			holder.lockNum = convertView.findViewById(R.id.tv_item_gateway_lock_num);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Resources res = mContext.getResources();
		Gateway gateway = mGatewayList.get(position);
		holder.name.setText(StringUtil.isBlank(gateway.getName())?gateway.getGatewayName():gateway.getName());
		if (gateway.getIsOnline() == 1) {
			holder.netStatus.setText(R.string.online);
			holder.netStatus.setCompoundDrawablesWithIntrinsicBounds(
					res.getDrawable(R.drawable.ic_gateway_online), null, null, null);
		} else {
			holder.netStatus.setText(R.string.offline);
			holder.netStatus.setCompoundDrawablesWithIntrinsicBounds(
					res.getDrawable(R.drawable.ic_gateway_offline), null, null, null);

		}
		holder.lockNum.setText(String.valueOf(gateway.getLockNum()));
		return convertView;
	}

	class ViewHolder {
		TextView name, netStatus, lockNum;
	}
}
