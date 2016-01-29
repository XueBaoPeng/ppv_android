package com.star.mobile.video.adapter;

import java.util.List;

import com.star.mobile.video.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ChargingRecordListAdapter extends BaseAdapter{

	private List<Object> datas;
	private Context context;
	
	public ChargingRecordListAdapter(List<Object> data,Context context) {
		this.datas = data;
		this.context = context;
	}
	
	@Override
	public int getCount() {
		
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder holder;
		if(view == null) {
			holder = new ViewHolder();
			view = LayoutInflater.from(context).inflate(R.layout.charging_record_item, null);
			holder.ivWeLogo = (ImageView) view.findViewById(R.id.iv_we_task_icon);
			holder.tvWeName = (TextView) view.findViewById(R.id.tv_we_name);
			
		}
		
		return null;
	}
	
	class ViewHolder {
		public ImageView ivWeLogo;
		public TextView tvWeName;
		public TextView tvTwoLineName;
		public TextView tvTwoLineValue;
		public TextView tvThreeLineName;
		public TextView tvThreeLineValue;
		public TextView tvFourLineName;
		public TextView tvFourLineValue;
	}

}
