package com.populstay.populife.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.populstay.populife.R;
import com.populstay.populife.entity.Key;
import com.populstay.populife.util.date.DateUtil;

import java.util.List;

/**
 * Created by Jerry
 */

public class LockListAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private List<Key> mLockList;

	public LockListAdapter(Context context, List<Key> lockList) {
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
	@SuppressLint("SetTextI18n")
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.item_lock_list, null);
			holder = new ViewHolder();

			holder.background = convertView.findViewById(R.id.ll_item_lock);
			holder.lockName = convertView.findViewById(R.id.tv_item_lock_name);
			holder.lockStatus = convertView.findViewById(R.id.tv_item_lock_status);
			holder.tvBattery = convertView.findViewById(R.id.tv_item_lock_battery);
			holder.tvAdmin = convertView.findViewById(R.id.tv_item_lock_admin);
			holder.lockType = convertView.findViewById(R.id.tv_item_lock_type);
			holder.ivBattery = convertView.findViewById(R.id.iv_item_lock_battery);
			holder.ivAdmin = convertView.findViewById(R.id.iv_item_lock_admin);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Key key = mLockList.get(position);

		holder.lockName.setText(key.getLockAlias());
		Resources res = mContext.getResources();
		int battery = key.getElectricQuantity();
		holder.tvBattery.setText(battery + res.getString(R.string.unit_percent));
		int batteryLevel = (battery - 1) / 20;
		int imgResInt = R.drawable.ic_battery_100;
		switch (batteryLevel) {
			case 0:
				imgResInt = R.drawable.ic_battery_20;
				break;

			case 1:
				imgResInt = R.drawable.ic_battery_40;
				break;

			case 2:
				imgResInt = R.drawable.ic_battery_60;
				break;

			case 3:
				imgResInt = R.drawable.ic_battery_80;
				break;

			case 4:
				imgResInt = R.drawable.ic_battery_100;
				break;

			default:
				break;
		}
		holder.ivBattery.setImageResource(imgResInt);

		if (key.isAdmin()) {//管理员钥匙
			holder.ivAdmin.setVisibility(View.VISIBLE);
			holder.tvAdmin.setVisibility(View.VISIBLE);
			holder.ivAdmin.setImageResource(R.drawable.ic_admin);
			holder.tvAdmin.setText(R.string.administrator);
		} else {//普通用户钥匙
			if (key.getKeyRight() == 1) {//钥匙已经被授权
				holder.ivAdmin.setVisibility(View.VISIBLE);
				holder.tvAdmin.setVisibility(View.VISIBLE);
				holder.ivAdmin.setImageResource(R.drawable.ic_admin_auth);
				holder.tvAdmin.setText(R.string.administrator);
			} else {//钥匙没有被授权
				holder.ivAdmin.setVisibility(View.GONE);
				holder.tvAdmin.setVisibility(View.GONE);
			}
		}

		if (key.isAdmin()) {//管理员钥匙
			holder.lockType.setText(R.string.permanent);
			holder.lockStatus.setVisibility(View.GONE);
		} else {//普通用户钥匙
			Integer keyType = key.getKeyType();//(int)钥匙的有效类型（1限时，2永久，3单次，4循环）
			//钥匙的状态（110401：正常使用，110402：待接收，110405：已冻结，110408：已删除，110410：已重置,110500:已过期）
			/**
			 * key status
			 * "110400"	Has not become valid
			 * "110401"	Normal use
			 * "110402"	To be received
			 * "110405"	Frozen
			 * "110408"	deleted
			 * "110410"	Reset
			 * "110500" Expired
			 */
			String keyStatus = key.getKeyStatus();
			String date = DateUtil.getDateToString(key.getStartDate(), "yyyy.MM.dd HH:mm") +
					"-" + DateUtil.getDateToString(key.getEndDate(), "yyyy.MM.dd HH:mm");
			String days = key.getDayNum() + (key.getDayNum() > 1 ? res.getString(R.string.days) : res.getString(R.string.day));
			String frozen = res.getString(R.string.frozen);
			String expired = res.getString(R.string.expired);
			int white = res.getColor(R.color.white);
			int colorItemGray = res.getColor(R.color.gray_item_bg);
			int colorRed = res.getColor(R.color.battery_low_red);
			int colorOrange = res.getColor(R.color.battery_middle_orange);
			switch (keyType) {
				case 1://限时
					holder.lockType.setText(date);
					holder.lockStatus.setVisibility(View.VISIBLE);
					switch (keyStatus) {
						case "110400": // 还未到生效时间
							holder.lockStatus.setVisibility(View.GONE);
							holder.background.setBackgroundColor(colorItemGray);
							break;

						case "110401"://正常使用
						case "110402"://待接收
							holder.lockStatus.setText(days);
							holder.lockStatus.setBackgroundColor(colorOrange);
							holder.background.setBackgroundColor(white);
							break;

						case "110405"://已冻结
							holder.lockStatus.setText(frozen);
							holder.lockStatus.setBackgroundColor(colorRed);
							holder.background.setBackgroundColor(colorItemGray);
							break;

//						case "110408"://已删除
//						case "110410"://已重置
//
//							break;

						case "110500"://已过期
							holder.lockStatus.setText(expired);
							holder.lockStatus.setBackgroundColor(colorRed);
							holder.background.setBackgroundColor(colorItemGray);
							break;

						default:
							holder.lockStatus.setVisibility(View.GONE);
							holder.background.setBackgroundColor(white);
							break;
					}
					break;

				case 2://永久
					holder.lockType.setText(R.string.permanent);
					switch (keyStatus) {
						case "110401"://正常使用
						case "110402"://待接收
							holder.lockStatus.setVisibility(View.GONE);
							holder.background.setBackgroundColor(white);
							break;

						case "110405"://已冻结
							holder.lockStatus.setVisibility(View.VISIBLE);
							holder.lockStatus.setText(frozen);
							holder.lockStatus.setBackgroundColor(colorRed);
							holder.background.setBackgroundColor(colorItemGray);
							break;

//						case "110408"://已删除
//						case "110410"://已重置
//
//							break;

						case "110500"://已过期
							holder.lockStatus.setVisibility(View.VISIBLE);
							holder.lockStatus.setText(expired);
							holder.lockStatus.setBackgroundColor(colorRed);
							holder.background.setBackgroundColor(colorItemGray);
							break;

						default:
							holder.lockStatus.setVisibility(View.GONE);
							holder.background.setBackgroundColor(white);
							break;
					}
					break;

				case 3://单次
					holder.lockType.setText(R.string.one_time);
					holder.lockStatus.setVisibility(View.GONE);
					holder.background.setBackgroundColor(white);
					break;

//				case 4://循环
//					holder.lockType.setText(netStatus);
//					holder.lockStatus.setVisibility(View.VISIBLE);
//					if (keyStatus.equals("110401")) {
//						holder.lockStatus.setText("" + dayNum + days);
//						holder.lockStatus.setTextColor(mContext.getResources().getColor(R.color.battery_middle_orange));
//					} else if (keyStatus.equals("110405")) {
//						holder.lockStatus.setText(frozen);
//						holder.lockStatus.setTextColor(mContext.getResources().getColor(R.color.battery_low_red));
//					} else if (keyStatus.equals("110500")) {
//						holder.lockStatus.setText(expired);
//						holder.lockStatus.setTextColor(mContext.getResources().getColor(R.color.battery_low_red));
//					}
//					break;

				default:
					holder.lockStatus.setVisibility(View.GONE);
					holder.background.setBackgroundColor(white);
					break;
			}
		}

		return convertView;
	}

	class ViewHolder {
		LinearLayout background;
		TextView lockName, lockStatus, tvBattery, tvAdmin, lockType;
		ImageView ivBattery, ivAdmin;
	}
}
