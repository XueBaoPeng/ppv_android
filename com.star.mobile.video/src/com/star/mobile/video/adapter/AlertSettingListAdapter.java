package com.star.mobile.video.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.star.mobile.video.model.AlertSettingItem;
import com.star.mobile.video.view.AlertSettingItemView;

public class AlertSettingListAdapter extends BaseAdapter {

	private List<AlertSettingItem> settingItems;
	private Context context;

	public AlertSettingListAdapter(Context context,
			List<AlertSettingItem> settingItems) {
		this.settingItems = settingItems;
		this.context = context;
	}

	@Override
	public int getCount() {
		return settingItems.size();
	}

	@Override
	public Object getItem(int position) {
		return settingItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		AlertSettingItemView view = new  AlertSettingItemView(context, settingItems.get(position));
		return view;
	}
}
