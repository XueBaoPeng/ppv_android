package com.star.mobile.video.me;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.star.mobile.video.model.MenuItem;

public class ItemAdapter extends BaseAdapter {

	private List<MenuItem<MeMenuItemRes>> items;
	private Context context;

	public ItemAdapter(Context context, List<MenuItem<MeMenuItemRes>> items) {
		this.items = items;
		this.context = context;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		 
		ItemView view = new ItemView(context, items.get(position));
		 
		return view;
	}

}
