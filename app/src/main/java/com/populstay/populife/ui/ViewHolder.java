package com.populstay.populife.ui;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;

public class ViewHolder {

	private View mConvertView;
	private SparseArray<View> mViews;

	private ViewHolder(Context context, int layoutId) {
		mViews = new SparseArray<>();
		mConvertView = LayoutInflater.from(context).inflate(layoutId, null);
		mConvertView.setTag(this);
	}

	public static ViewHolder get(Context context, View convertView, int layoutId) {
		if (convertView == null)
			return new ViewHolder(context, layoutId);
		else return (ViewHolder) convertView.getTag();
	}

	public <V extends View> V getView(int viewId) {
		View view = mViews.get(viewId);
		if (view == null) {
			view = mConvertView.findViewById(viewId);
			mViews.put(viewId, view);
		}
		return (V) view;
	}

	public View getConvertView() {
		return mConvertView;
	}
}
