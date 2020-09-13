package com.populstay.populife.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.populstay.populife.R;
import com.populstay.populife.util.CollectionUtil;
import com.ttlock.gateway.sdk.model.WiFi;

import java.util.List;

public class WifiListAdapter extends BaseAdapter {

    private List<WiFi> mDatas;
    private Context mContext;

    public WifiListAdapter(List<WiFi> mDatas, Context mContext) {
        this.mDatas = mDatas;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return !CollectionUtil.isEmpty(mDatas) ? mDatas.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return !CollectionUtil.isEmpty(mDatas) ? mDatas.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.wifi_list_item, null);
            holder = new ViewHolder();
            holder.name = convertView.findViewById(R.id.name);
            holder.rssi = convertView.findViewById(R.id.rssi);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        WiFi wiFi = mDatas.get(position);
        holder.name.setText(wiFi.ssid);
        holder.rssi.setText("rssi:" + String.valueOf(wiFi.rssi));

        return convertView;
    }

    class ViewHolder {
        TextView name, rssi;
    }
}
