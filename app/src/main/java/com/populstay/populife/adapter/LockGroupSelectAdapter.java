package com.populstay.populife.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.populstay.populife.R;
import com.populstay.populife.entity.LockGroup;
import com.populstay.populife.util.string.StringUtil;

import java.util.List;

/**
 * Created by Jerry
 */

public class LockGroupSelectAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private List<LockGroup> mList;


	public LockGroupSelectAdapter(Context context, List<LockGroup> list) {
		mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
		mList = list;
	}

	// 获取数量
	public int getCount() {
		return mList.size();
	}

	// 获取当前选项
	public Object getItem(int position) {
		return mList.get(position);
	}

	// 获取当前选项的 id
	public long getItemId(int position) {
		return position;
	}

	@Override
	public boolean isEnabled(int position) {
		return !StringUtil.isBlank(((LockGroup) getItem(position)).getId());
	}

	// 获取 View
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.item_lock_group_select, null);
			holder = new ViewHolder();

			holder.name = convertView.findViewById(R.id.tv_lock_group_select_name);
			holder.symbol = convertView.findViewById(R.id.iv_lock_group_select_symbol);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		LockGroup lockGroup = mList.get(position);
		String id = lockGroup.getId();
		Resources res = mContext.getResources();
		if (!StringUtil.isBlank(id)) {
			holder.name.setTextColor(res.getColor(R.color.text_gray_dark));
		} else {
			holder.name.setTextColor(res.getColor(R.color.text_gray_light));
		}
		holder.name.setText(lockGroup.getName());
		if (lockGroup.isSelected()) {
			holder.symbol.setVisibility(View.VISIBLE);
		} else {
			holder.symbol.setVisibility(View.GONE);
		}
		return convertView;
	}

	class ViewHolder {
		TextView name;
		ImageView symbol;
	}
}
