package com.star.mobile.video.search;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.star.mobile.video.base.BaseAdapter;

public class SearchMoreAdapter<T> extends BaseAdapter<T> {

	private String key;
	
	public SearchMoreAdapter(Context context, List<T> datas, String searchKey) {
		super(context, datas);
		this.key = searchKey;
	}
	public void updateSearchResult(List<?> datas) {
		data = (List<T>) datas;
		notifyDataSetChanged();
	}
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		SearchMoreItemView<T> itemView = (SearchMoreItemView<T>) view;
		if(itemView == null) {
			itemView = new SearchMoreItemView<T>(context);
		}
		itemView.setData(data.get(position), key);
		return itemView;
	}
}
