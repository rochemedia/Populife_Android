package com.populstay.populife.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.populstay.populife.R;
import com.populstay.populife.entity.LockUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Jerry
 */

public class LockUserListAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private List<LockUser> mList;


	public LockUserListAdapter(Context context, List<LockUser> list) {
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

	// 获取 View
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.item_lock_user_list, null);
			holder = new ViewHolder();

			holder.avatar = convertView.findViewById(R.id.civ_item_lock_user_avatar);
			holder.name = convertView.findViewById(R.id.tv_item_lock_user_name);
			holder.alias = convertView.findViewById(R.id.tv_item_lock_user_alias);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		LockUser lockUser = mList.get(position);

		Glide.with(mContext)
				.load(lockUser.getAvatar())
				.asBitmap()
				.diskCacheStrategy(DiskCacheStrategy.ALL)
				.dontAnimate()
				.centerCrop()
				.placeholder(R.drawable.ic_user_avatar)
				.error(R.drawable.ic_user_avatar)
				.into(holder.avatar);
		holder.name.setText(lockUser.getUserName());
		holder.alias.setText(lockUser.getAlias());

		return convertView;
	}

	class ViewHolder {
		CircleImageView avatar;
		TextView name, alias;
	}
}
