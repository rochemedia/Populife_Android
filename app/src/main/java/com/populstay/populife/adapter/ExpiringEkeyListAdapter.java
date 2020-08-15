package com.populstay.populife.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

public class ExpiringEkeyListAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private List<BluetoothKey> mBluetoothKeyList;

	public ExpiringEkeyListAdapter(Context context, List<BluetoothKey> bluetoothKeyList) {
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
			convertView = mLayoutInflater.inflate(R.layout.item_expiring_ekey_list, null);
			holder = new ViewHolder();

			holder.avatar = convertView.findViewById(R.id.civ_item_expring_ekey_avatar);
			holder.name = convertView.findViewById(R.id.tv_item_expring_ekey_name);
			holder.type = convertView.findViewById(R.id.tv_item_expring_ekey_type);
			holder.dayNum = convertView.findViewById(R.id.tv_item_expring_ekey_day_num);
			holder.status = convertView.findViewById(R.id.tv_item_expring_ekey_status);

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
		holder.type.setText(DateUtil.getDateToString(bluetoothKey.getStartDate(), "yyyy/MM/dd") +
				"-" + DateUtil.getDateToString(bluetoothKey.getEndDate(), "yyyy/MM/dd"));
		holder.dayNum.setText(String.valueOf(bluetoothKey.getDayNum()) + mContext.getResources().getString(R.string.days));
		holder.status.setText(bluetoothKey.getLockAlias());

		return convertView;
	}

	class ViewHolder {
		TextView name, type, status, dayNum;
		CircleImageView avatar;
	}
}
