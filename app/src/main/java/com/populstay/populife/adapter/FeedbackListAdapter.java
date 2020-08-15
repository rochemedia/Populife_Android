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

public class FeedbackListAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private List<ContentInfo> mFeedbackList;
	private IFeedbackListCallback mFeedbackListCallback;

	public interface IFeedbackListCallback {

		void onDeleteClick(int position); // Delete被点击
	}

	public FeedbackListAdapter(Context context, List<ContentInfo> feedbackList, IFeedbackListCallback callback) {
		mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
		mFeedbackList = feedbackList;
		this.mFeedbackListCallback = callback;
	}

	// 获取数量
	public int getCount() {
		return mFeedbackList.size();
	}

	// 获取当前选项
	public Object getItem(int position) {
		return mFeedbackList.get(position);
	}

	// 获取当前选项的 id
	public long getItemId(int position) {
		return position;
	}

	// 获取 View
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.item_feedback_list, null);
			holder = new ViewHolder();

			holder.content = convertView.findViewById(R.id.tv_item_feedback_content);
			holder.date = convertView.findViewById(R.id.tv_item_feedback_date);
			holder.delete = convertView.findViewById(R.id.tv_item_feedback_delete);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ContentInfo feedback = mFeedbackList.get(position);
		holder.content.setText(feedback.getContent());
		holder.date.setText(feedback.getCreateTime());
		holder.delete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mFeedbackListCallback.onDeleteClick(position);
			}
		});

		return convertView;
	}

	class ViewHolder {
		TextView content, date, delete;
	}
}
