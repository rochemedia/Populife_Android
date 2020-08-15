package com.populstay.populife.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.populstay.populife.R;
import com.populstay.populife.entity.IcCard;
import com.populstay.populife.util.date.DateUtil;

import java.util.List;

/**
 * Created by Jerry
 */

public class IcCardListAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private List<IcCard> mIcCardList;

	public IcCardListAdapter(Context context, List<IcCard> icCardList) {
		mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
		mIcCardList = icCardList;
	}

	// 获取数量
	public int getCount() {
		return mIcCardList.size();
	}

	// 获取当前选项
	public Object getItem(int position) {
		return mIcCardList.get(position);
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
			convertView = mLayoutInflater.inflate(R.layout.item_ic_card_list, null);
			holder = new ViewHolder();

			holder.name = convertView.findViewById(R.id.tv_item_ic_card_name);
			holder.cardNumber = convertView.findViewById(R.id.tv_item_ic_card_number);
			holder.time = convertView.findViewById(R.id.tv_item_ic_card_time);
			holder.type = convertView.findViewById(R.id.tv_item_ic_card_type);
			holder.status = convertView.findViewById(R.id.tv_item_ic_card_status);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		IcCard icCard = mIcCardList.get(position);

		holder.name.setText(icCard.getRemark());
		holder.cardNumber.setText(icCard.getCardNumber());
		Integer type = icCard.getType();
		if (Integer.valueOf(1).equals(type)) {//永久
			holder.time.setText(DateUtil.getDateToString(icCard.getCreateDate(), "yyyy-MM-dd HH:mm:ss"));
			holder.type.setText(R.string.permanent);
			holder.type.setVisibility(View.VISIBLE);
		} else if (Integer.valueOf(2).equals(type)) {//限时
			String startDate = DateUtil.getDateToString(icCard.getStartDate(), "yyyy.MM.dd HH:mm");
			String endDate = DateUtil.getDateToString(icCard.getEndDate(), "yyyy.MM.dd HH:mm");
			holder.time.setText(startDate + "-" + endDate);
			holder.type.setVisibility(View.GONE);
		}
		if ("Y".equals(icCard.getExpire())) {
			holder.status.setVisibility(View.VISIBLE);
			holder.status.setText(R.string.expired);
		} else {
			holder.status.setVisibility(View.GONE);
		}

		return convertView;
	}

	class ViewHolder {
		TextView name, cardNumber, time, type, status;
	}
}
