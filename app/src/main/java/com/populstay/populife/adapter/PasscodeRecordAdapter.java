package com.populstay.populife.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.populstay.populife.R;
import com.populstay.populife.entity.LockOperateRecord;

import java.util.List;
import java.util.Map;


/**
 * Created by Jerry
 */

public class PasscodeRecordAdapter extends BaseExpandableListAdapter {

	private Context mContext;
	private List<String> mGroupNames;
	private Map<String, List<LockOperateRecord>> mRecords;

	/**
	 * 构造函数
	 *
	 * @param context    上下文
	 * @param groupNames 组元素列表
	 * @param records    子元素列表
	 */
	public PasscodeRecordAdapter(Context context, List<String> groupNames,
								 Map<String, List<LockOperateRecord>> records) {
		this.mContext = context;
		this.mGroupNames = groupNames;
		this.mRecords = records;
	}

	@Override
	public int getGroupCount() {
		return mGroupNames.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		String groupName = mGroupNames.get(groupPosition);
		return mRecords.get(groupName).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return mGroupNames.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		List<LockOperateRecord> itemNames = mRecords.get(mGroupNames.get(groupPosition));
		return itemNames.get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

		final GroupViewHolder gholder;
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.item_lock_operate_record_group, null);
			gholder = new GroupViewHolder();
			gholder.groupName = convertView.findViewById(R.id.tv_lock_record_group_name);
			convertView.setTag(gholder);
		} else {
			gholder = (GroupViewHolder) convertView.getTag();
		}
		String groupName = (String) getGroup(groupPosition);
		gholder.groupName.setText(groupName);

		return convertView;
	}

	@SuppressLint("SetTextI18n")
	@Override
	public View getChildView(final int groupPosition, final int childPosition,
							 final boolean isLastChild, View convertView, final ViewGroup parent) {

		final ChildViewHolder cholder;
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.item_passcode_operate_record_child, null);
			cholder = new ChildViewHolder();
			cholder.content = convertView.findViewById(R.id.tv_passcode_record_child_content);

			convertView.setTag(cholder);
		} else {
			cholder = (ChildViewHolder) convertView.getTag();
		}
		LockOperateRecord record = (LockOperateRecord) getChild(groupPosition, childPosition);
		cholder.content.setText(record.getContent() + " " + record.getCreateDate());

		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	/**
	 * 组元素绑定器
	 */
	static class GroupViewHolder {
		TextView groupName;
	}

	/**
	 * 子元素绑定器
	 */
	static class ChildViewHolder {
		TextView content;
	}
}

