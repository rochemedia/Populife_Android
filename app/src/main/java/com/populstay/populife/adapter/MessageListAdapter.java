package com.populstay.populife.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.populstay.populife.R;
import com.populstay.populife.entity.ContentInfo;

import java.util.List;

/**
 * Created by Jerry
 */

public class MessageListAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private List<ContentInfo> mMessageList;

	public MessageListAdapter(Context context, List<ContentInfo> messageList) {
		mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
		mMessageList = messageList;
	}

	// 获取数量
	public int getCount() {
		return mMessageList.size();
	}

	// 获取当前选项
	public Object getItem(int position) {
		return mMessageList.get(position);
	}

	// 获取当前选项的 id
	public long getItemId(int position) {
		return position;
	}

	// 获取 View
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.item_message_list, null);
			holder = new ViewHolder();

			holder.title = convertView.findViewById(R.id.tv_item_message_title);
			holder.date = convertView.findViewById(R.id.tv_item_message_date);
			holder.content = convertView.findViewById(R.id.tv_item_message_content);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ContentInfo message = mMessageList.get(position);
		holder.title.setText(message.getTitle());
		holder.date.setText(message.getCreateTime());
		holder.content.setText(message.getContent());

		return convertView;
	}

	class ViewHolder {
		TextView title, date, content;
	}
}
