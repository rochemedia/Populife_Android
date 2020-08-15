package com.populstay.populife.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.populstay.populife.R;
import com.populstay.populife.ui.ViewHolder;
import com.ttlock.bl.sdk.scanner.ExtendedBluetoothDevice;

import java.util.List;

public class GatewayAddListAdapter extends BaseAdapter {

	private Context mContext;
	private List<ExtendedBluetoothDevice> mLeDevices;

	public GatewayAddListAdapter(Context context, List<ExtendedBluetoothDevice> mLeDevices) {
		mContext = context;
		this.mLeDevices = mLeDevices;
	}

	/**
	 * update scan device
	 *
	 * @param extendedBluetoothDevice
	 */
	public void updateDevice(ExtendedBluetoothDevice extendedBluetoothDevice) {
		boolean contain = false;
		boolean update = false;
		for (ExtendedBluetoothDevice device : mLeDevices) {
			if (device.equals(extendedBluetoothDevice)) {
				contain = true;
				if (device.isSettingMode() != extendedBluetoothDevice.isSettingMode()) {
					device.setSettingMode(extendedBluetoothDevice.isSettingMode());
					update = true;
				}
				if (device.getRssi() != extendedBluetoothDevice.getRssi()) {
					device.setRssi(extendedBluetoothDevice.getRssi());
					update = true;
				}
			}
		}
		if (!contain) {
			mLeDevices.add(extendedBluetoothDevice);
			update = true;
		}
		if (update)
			notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mLeDevices.size();
	}

	@Override
	public Object getItem(int position) {
		return mLeDevices.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public boolean isEnabled(int position) {
		return ((ExtendedBluetoothDevice) getItem(position)).isSettingMode();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ExtendedBluetoothDevice device = mLeDevices.get(position);
		ViewHolder viewHolder = ViewHolder.get(mContext, convertView, R.layout.item_gateway_add_list);
		TextView name = viewHolder.getView(R.id.tv_item_gateway_add_name);
		TextView signal = viewHolder.getView(R.id.tv_item_gateway_add_signal);
		ImageView addIcon = viewHolder.getView(R.id.iv_item_gateway_add_mark);
		name.setText(device.getName());
		if (device.isSettingMode()) {
			addIcon.setVisibility(View.VISIBLE);
			name.setTextColor(mContext.getResources().getColor(R.color.text_gray_dark));
		} else {
			addIcon.setVisibility(View.GONE);
			name.setTextColor(mContext.getResources().getColor(R.color.text_gray_light));
		}
		signal.setText(String.valueOf(device.getRssi()));
		return viewHolder.getConvertView();
	}
}
