package com.star.mobile.video.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.star.cms.model.Area;
import com.star.mobile.video.R;

public class AreaListAdapter extends BaseAdapter{
	
	private Context context;
	private List<Area> data;
	private Long areaId;
	
	public AreaListAdapter(Context c, List<Area> datas,Long areaId) {
		this.context = c;
		this.data = datas;
		this.areaId = areaId;
	}
	
	public void updateDateRefreshUi (Long areaId) {
		this.areaId = areaId;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		
		return data.get(position);
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
			view = LayoutInflater.from(context).inflate(R.layout.area_item, null);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		
		holder.tvAreaName = (TextView) view.findViewById(R.id.tv_area_name);
		holder.tvAreaId = (TextView) view.findViewById(R.id.tv_area_id);
		holder.areaLine = view.findViewById(R.id.area_line);
		if(areaId != null && areaId.equals(data.get(position).getId())) {
			holder.tvAreaName.setTextColor(context.getResources().getColor(R.color.orange));
		} else {
			holder.tvAreaName.setTextColor(context.getResources().getColor(R.color.choose_text));
		}
		if(position == getCount()-1) {
			holder.areaLine.setVisibility(View.GONE);
		} else {
			holder.areaLine.setVisibility(View.VISIBLE);
		}
		holder.tvAreaName.setText(data.get(position).getName());
		holder.tvAreaId.setText(data.get(position).getId()+"");
		return view;
	}
	
	
	class ViewHolder {
		public TextView tvAreaName;
		public TextView tvAreaId;
		public View areaLine; 
	}

}
