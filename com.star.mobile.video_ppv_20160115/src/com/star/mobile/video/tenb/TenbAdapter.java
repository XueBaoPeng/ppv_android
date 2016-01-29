package com.star.mobile.video.tenb;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.star.cms.model.TenbMe;

public class TenbAdapter extends BaseAdapter {

	private Context mContext;
	private List<TenbMe> datas;
	
	public TenbAdapter(Context context,List<TenbMe> datas) {
		this.mContext = context;
		this.datas = datas;
	}
	
	public void updateData(List<TenbMe> datas) {
		this.datas = datas;
		notifyDataSetChanged();
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
		TenbMe tenb = datas.get(position);
		if(view == null) {
			view = new TenbItemView(mContext);
		}
		((TenbItemView) view).setTenb(tenb,position);
		return view;
	}
}
