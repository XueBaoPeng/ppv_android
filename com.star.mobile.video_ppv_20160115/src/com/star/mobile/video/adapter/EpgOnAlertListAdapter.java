package com.star.mobile.video.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.star.cms.model.vo.ProgramVO;
import com.star.mobile.video.view.BaseEpgItemView.OnItemCallBackListener;
import com.star.mobile.video.view.EpgOnAlertItemView;

public class EpgOnAlertListAdapter extends BaseAdapter {

	private Context context;
	private List<ProgramVO> epgs;
	private OnItemCallBackListener mListener;
	private boolean clickable;

	public EpgOnAlertListAdapter(Context context, List<ProgramVO> epgs) {
		this.context = context;
		this.epgs = epgs;
	}

	public void setOnItemCallBackListener(OnItemCallBackListener mListener) {
		this.mListener = mListener;
	}

	public void updateChnDataAndRefreshUI(List<ProgramVO> epgs) {
		this.epgs = epgs;
		notifyDataSetChanged();
	}
	
	public void setItemClickable(boolean clickable){
		this.clickable = clickable;
	}
	
	public List<ProgramVO> getData(){
		return epgs;
	}

	@Override
	public int getCount() {
		return epgs.size();
	}

	@Override
	public Object getItem(int position) {
		return epgs.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		EpgOnAlertItemView itemView = (EpgOnAlertItemView)convertView;
		if (itemView == null) {
			itemView = new EpgOnAlertItemView(context, this);
		}
		itemView.fillProgramData(epgs.get(position));
		itemView.setOnItemCallBackListener(mListener);
		if(clickable)
			itemView.setOnClickable(true);
		return itemView;
	}
}
