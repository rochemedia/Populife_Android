package com.populstay.populife.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.populstay.populife.R;

import java.util.List;
import java.util.Map;


/**
 * Created by Jerry
 */

public class CommonQuestionAdapter extends BaseExpandableListAdapter {

	private Context mContext;
	private List<String> mGroupNames;
	private Map<String, List<String>> mItemNames;

	/**
	 * 构造函数
	 *
	 * @param context    上下文
	 * @param groupNames 组元素列表
	 * @param itemNames  子元素列表
	 */
	public CommonQuestionAdapter(Context context, List<String> groupNames,
								 Map<String, List<String>> itemNames) {
		this.mContext = context;
		this.mGroupNames = groupNames;
		this.mItemNames = itemNames;
	}

	@Override
	public int getGroupCount() {
		return mGroupNames.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		String groupName = mGroupNames.get(groupPosition);
		return mItemNames.get(groupName).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return mGroupNames.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		List<String> itemNames = mItemNames.get(mGroupNames.get(groupPosition));
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
			convertView = View.inflate(mContext, R.layout.item_common_question_group, null);
			gholder = new GroupViewHolder();
			gholder.groupName = convertView.findViewById(R.id.tv_item_question_group);
			convertView.setTag(gholder);
		} else {
			gholder = (GroupViewHolder) convertView.getTag();
		}
		String groupName = (String) getGroup(groupPosition);
		gholder.groupName.setText(groupName);

		return convertView;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition,
							 final boolean isLastChild, View convertView, final ViewGroup parent) {

		final ChildViewHolder cholder;
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.item_common_question_child, null);
			cholder = new ChildViewHolder();
			cholder.questionName = convertView.findViewById(R.id.tv_item_question_child);

			convertView.setTag(cholder);
		} else {
			cholder = (ChildViewHolder) convertView.getTag();
		}
		String cartgoods = (String) getChild(groupPosition, childPosition);
		cholder.questionName.setText(cartgoods);

		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
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
		TextView questionName;
	}
}

