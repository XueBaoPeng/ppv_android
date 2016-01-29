package com.star.mobile.video.search;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.star.mobile.video.R;
import com.star.mobile.video.base.BaseAdapter;
import com.star.mobile.video.util.DensityUtil;

public class SearchHotAdapter extends BaseAdapter<String> {

	public SearchHotAdapter(Context context, List<String> data) {
		super(context, data);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = LayoutInflater.from(context).inflate(R.layout.view_search_hot_item, null);
		ImageView ivHot = (ImageView) convertView.findViewById(R.id.iv_hot_icon);
		TextView tvHot = (TextView) convertView.findViewById(R.id.tv_hot_key);
		ivHot.setImageResource(R.drawable.ic_search_blue_24dp);
		tvHot.setText(data.get(position));
		tvHot.setTextColor(context.getResources().getColor(R.color.taks_blue));
		tvHot.setPadding(DensityUtil.dip2px(context, 5), 0, 0, 0);
		return convertView;
	}

}
