package com.populstay.populife.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.populstay.populife.R;
import com.populstay.populife.entity.Passcode;
import com.populstay.populife.util.date.DateUtil;

import java.util.List;

/**
 * Created by Jerry
 */

public class PasscodeListAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private List<Passcode> mPasscodeList;

	public PasscodeListAdapter(Context context, List<Passcode> passcodeList) {
		mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
		mPasscodeList = passcodeList;
	}

	// 获取数量
	public int getCount() {
		return mPasscodeList.size();
	}

	// 获取当前选项
	public Object getItem(int position) {
		return mPasscodeList.get(position);
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
			convertView = mLayoutInflater.inflate(R.layout.item_passcode_list, null);
			holder = new ViewHolder();

			holder.pwd = convertView.findViewById(R.id.tv_item_passcode_pwd);
			holder.time = convertView.findViewById(R.id.tv_item_passcode_time);
			holder.type = convertView.findViewById(R.id.tv_item_passcode_type);
			holder.status = convertView.findViewById(R.id.tv_item_passcode_status);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Passcode passcode = mPasscodeList.get(position);
		holder.pwd.setText(passcode.getKeyboardPwd());
		/**
		 * type	value
		 * One-time			1
		 * Permanent		2
		 * Period			3
		 * Delete			4
		 * Weekend Cyclic	5
		 * Daily Cyclic		6
		 * Workday Cyclic	7
		 * Monday Cyclic	8
		 * Tuesday Cyclic	9
		 * Wednesday Cyclic	10
		 * Thursday Cyclic	11
		 * Friday Cyclic	12
		 * Saturday Cyclic	13
		 * Sunday Cyclic	14
		 */
		int passcodeType = passcode.getKeyboardPwdType();//密码类型
		Resources res = mContext.getResources();
		String createDate = DateUtil.getDateToString(passcode.getCreateDate(), "yyyy.MM.dd HH:mm");
		String startDate = DateUtil.getDateToString(passcode.getStartDate(), "HH:00");
		String endDate = DateUtil.getDateToString(passcode.getEndDate(), "HH:00");
		String cyclicTime = " " + startDate + "-" + endDate;
		switch (passcodeType) {
			case 1:
				holder.time.setText(createDate);
				holder.type.setText(R.string.one_time);
				break;

			case 2:
				holder.time.setText(createDate);
				holder.type.setText(R.string.permanent);
				break;

			case 3:
				String startTime = DateUtil.getDateToString(passcode.getStartDate(), "yyyy.MM.dd HH:mm");
				String endTime = DateUtil.getDateToString(passcode.getEndDate(), "yyyy.MM.dd HH:mm");
				holder.time.setText(startTime + "-" + endTime);
				holder.type.setText(R.string.period);
				break;

			case 4:
				holder.time.setText(createDate);
				holder.type.setText(R.string.clear);
				break;

			case 5:
				holder.time.setText(createDate);
				holder.type.setText(res.getString(R.string.weekend) + cyclicTime);
				break;

			case 6:
				holder.time.setText(createDate);
				holder.type.setText(res.getString(R.string.daily) + cyclicTime);
				break;

			case 7:
				holder.time.setText(createDate);
				holder.type.setText(res.getString(R.string.workday) + cyclicTime);
				break;

			case 8:
				holder.time.setText(createDate);
				holder.type.setText(res.getString(R.string.monday) + cyclicTime);
				break;

			case 9:
				holder.time.setText(createDate);
				holder.type.setText(res.getString(R.string.tuesday) + cyclicTime);
				break;

			case 10:
				holder.time.setText(createDate);
				holder.type.setText(res.getString(R.string.wednesday) + cyclicTime);
				break;

			case 11:
				holder.time.setText(createDate);
				holder.type.setText(res.getString(R.string.thursday) + cyclicTime);
				break;

			case 12:
				holder.time.setText(createDate);
				holder.type.setText(res.getString(R.string.friday) + cyclicTime);
				break;

			case 13:
				holder.time.setText(createDate);
				holder.type.setText(res.getString(R.string.saturday) + cyclicTime);
				break;

			case 14:
				holder.time.setText(createDate);
				holder.type.setText(res.getString(R.string.sunday) + cyclicTime);
				break;

			default:
				break;
		}
		int status = passcode.getStatus();
		switch (status) {
//			case 0://删除
//				break;

			case 2://失效
				holder.status.setVisibility(View.VISIBLE);
				holder.status.setText(R.string.expired);
				break;

			case 1://未激活
			case 3://正常
			default:
				holder.status.setVisibility(View.GONE);
				break;
		}

		return convertView;
	}

	class ViewHolder {
		TextView pwd, type, time, status;
	}
}
