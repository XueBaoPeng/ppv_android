package com.star.mobile.video.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.star.cms.model.Area;
import com.star.mobile.video.R;
import com.star.ui.ImageView;
/**
 * notify by Lee
 * @date 2015/11/20
 * 修改了布局
 */
public class CustomerServiceTimeAdapter extends BaseAdapter{
	
	private List<Area> areas;
	private Context context;
	private OnClickListener ol;
	
	
	public CustomerServiceTimeAdapter(Context context,List<Area> areas,OnClickListener ocl) {
		this.areas = areas;
		this.context = context;
		this.ol = ocl;
	}

	@Override
	public int getCount() {
		
		return areas.size();
	}

	@Override
	public Object getItem(int position) {
		
		return areas.get(position);
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
			view = LayoutInflater.from(context).inflate(R.layout.view_customer_service_time_item, null);
			holder.customerServicePhoneNumber = (TextView) view.findViewById(R.id.tv_customer_service_phone_number);
			holder.serviceTime = (TextView) view.findViewById(R.id.tv_service_time);
			holder.btnCall = (RelativeLayout) view.findViewById(R.id.rl_call); 
			holder.btnCall.setOnClickListener(ol);
			view.setTag(holder);
		} else {
			holder = (ViewHolder)view.getTag();
		}
		String []info = areas.get(position).getPhoneNumber().split(",");
		holder.customerServicePhoneNumber.setText(info[0].trim());
		if(info.length < 1) 
			holder.serviceTime.setText(info[1]);
		holder.btnCall.setTag(info[0]);
		return view;
	}

	class ViewHolder {
		public TextView customerServicePhoneNumber;
		public TextView serviceTime;
		public RelativeLayout btnCall;
	}
}
