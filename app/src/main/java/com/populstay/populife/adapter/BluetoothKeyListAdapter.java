package com.populstay.populife.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.populstay.populife.R;
import com.populstay.populife.entity.BluetoothKey;
import com.populstay.populife.util.date.DateUtil;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Jerry
 */

public class BluetoothKeyListAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private List<BluetoothKey> mBluetoothKeyList;

	public BluetoothKeyListAdapter(Context context, List<BluetoothKey> bluetoothKeyList) {
		mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
		mBluetoothKeyList = bluetoothKeyList;
	}

	// 获取数量
	public int getCount() {
		return mBluetoothKeyList.size();
	}

	// 获取当前选项
	public Object getItem(int position) {
		return mBluetoothKeyList.get(position);
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
			convertView = mLayoutInflater.inflate(R.layout.item_bluetooth_key_list, null);
			holder = new ViewHolder();

			holder.avatar = convertView.findViewById(R.id.civ_item_bluetooth_key_avatar);
			holder.auth = convertView.findViewById(R.id.iv_item_bluetooth_key_auth);
			holder.name = convertView.findViewById(R.id.tv_item_bluetooth_key_name);
			holder.type = convertView.findViewById(R.id.tv_item_bluetooth_key_type);
			holder.status = convertView.findViewById(R.id.tv_item_bluetooth_key_status);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		BluetoothKey bluetoothKey = mBluetoothKeyList.get(position);
		Glide.with(mContext)
				.load(bluetoothKey.getAvatar())
				.asBitmap()
				.diskCacheStrategy(DiskCacheStrategy.ALL)
				.dontAnimate()
				.centerCrop()
				.placeholder(R.drawable.ic_user_avatar)
				.error(R.drawable.ic_user_avatar)
				.into(holder.avatar);
		holder.name.setText(bluetoothKey.getAlias());
		boolean isAuth = bluetoothKey.getKeyRight() == 1;
		if (isAuth) {
			holder.auth.setVisibility(View.VISIBLE);
		} else {
			holder.auth.setVisibility(View.GONE);
		}
		int keyType = bluetoothKey.getType();//钥匙类型（1限时，2永久，3单次，4循环）
		switch (keyType) {
			case 1:
				String startTime = DateUtil.getDateToString(bluetoothKey.getStartDate(), "yyyy.MM.dd HH:mm");
				String endTime = DateUtil.getDateToString(bluetoothKey.getEndDate(), "yyyy.MM.dd HH:mm");
				holder.type.setText(startTime + "-" + endTime);
				break;

			case 2:
				holder.type.setText(DateUtil.getDateToString(bluetoothKey.getSendDate(), "yyyy.MM.dd HH:mm")
						+ " " + mContext.getResources().getString(R.string.permanent));
				break;

			case 3:
				holder.type.setText(DateUtil.getDateToString(bluetoothKey.getSendDate(), "yyyy.MM.dd HH:mm")
						+ " " + mContext.getResources().getString(R.string.one_time));
				break;

			case 4:

				break;

			default:
				break;
		}
		String keyStatus = bluetoothKey.getKeyStatus();
		switch (keyStatus) {
			case "110401"://正常使用
				holder.status.setVisibility(View.GONE);
				break;

			case "110402"://待接收
				holder.status.setVisibility(View.VISIBLE);
				holder.status.setText(R.string.pending);
				break;

			case "110405"://已冻结
				holder.status.setVisibility(View.VISIBLE);
				holder.status.setText(R.string.frozen);
				break;

//						case "110408"://已删除
//						case "110410"://已重置
//
//							break;

			case "110500"://已过期
				holder.status.setVisibility(View.VISIBLE);
				holder.status.setText(R.string.expired);
				break;

			default:
				holder.status.setVisibility(View.GONE);
				break;
		}

		return convertView;
	}

	class ViewHolder {
		TextView name, type, status;
		CircleImageView avatar;
		ImageView auth;
	}
}
