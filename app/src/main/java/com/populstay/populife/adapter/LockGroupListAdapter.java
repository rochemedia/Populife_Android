package com.populstay.populife.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.populstay.populife.R;
import com.populstay.populife.entity.LockGroup;
import com.populstay.populife.util.date.DateUtil;
import com.populstay.populife.util.string.StringUtil;

import java.util.List;

/**
 * Created by Jerry
 */

public class LockGroupListAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private List<LockGroup> mList;


	public LockGroupListAdapter(Context context, List<LockGroup> list) {
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
			convertView = mLayoutInflater.inflate(R.layout.item_lock_group_list, null);
			holder = new ViewHolder();

			holder.name = convertView.findViewById(R.id.tv_item_lock_group_name);
			holder.time = convertView.findViewById(R.id.tv_item_lock_group_time);
			holder.count = convertView.findViewById(R.id.tv_item_lock_group_count);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		LockGroup lockGroup = mList.get(position);
		String id = lockGroup.getId();
		Resources res = mContext.getResources();
		if (!StringUtil.isBlank(id)) {
			holder.time.setVisibility(View.VISIBLE);
			holder.time.setText(DateUtil.getDateToString(lockGroup.getCreateTime(), "yyyy-MM-dd"));
			holder.name.setTextColor(res.getColor(R.color.text_gray_dark));
		} else {
			holder.time.setVisibility(View.GONE);
			holder.name.setTextColor(res.getColor(R.color.text_gray_light));
		}
		holder.name.setText(lockGroup.getName());
		holder.count.setText(String.valueOf(lockGroup.getLockCount()));

		return convertView;
	}

	class ViewHolder {
		TextView name, time, count;
	}
}
