package com.star.mobile.video.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.star.cms.model.vo.ChannelVO;
import com.star.cms.model.vo.ProgramVO;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.view.BaseEpgItemView.OnItemCallBackListener;
import com.star.mobile.video.view.EpgOnAirItemView;

public class EpgOnAirListAdapter extends BaseAdapter {

	private Context context;
	private List<ChannelVO> channels;
	private List<ProgramVO> epgs;
	private boolean isShowChnNum;
	private int currentPage = Constant.PAGE_ONAIR_NOW;
	private OnItemCallBackListener mListener;
	private boolean clickable;
	
	public EpgOnAirListAdapter(Context context, List<ProgramVO> epgs) {
		this.context = context;
		this.epgs = epgs;
	}

	public EpgOnAirListAdapter(Context context, List<ChannelVO> channels,
			boolean isShowChnNum) {
		this.context = context;
		this.channels = channels;
		this.isShowChnNum = isShowChnNum;
	}
	
	public void setOnItemCallBackListener(OnItemCallBackListener mListener) {
		this.mListener = mListener;
	}
	
	public void setItemClickable(boolean clickable){
		this.clickable = clickable;
	}
	
	public void setCurrentPage(int page){
		this.currentPage = page;
	}

	public void updateChnDataAndRefreshUI(List<ChannelVO> channels) {
		this.channels = channels;
		notifyDataSetChanged();
	}
	
	public void updateEpgDataAndRefreshUI(List<ProgramVO> epgs) {
		this.epgs = epgs;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		if(epgs!=null)
			return epgs.size();
		else
			return channels.size();
	}

	@Override
	public Object getItem(int position) {
		if(epgs!=null)
			return epgs.get(position);
		else
			return channels.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		EpgOnAirItemView itemView = (EpgOnAirItemView)convertView;
		if (itemView == null) {
			itemView = new EpgOnAirItemView(context, currentPage);
		}
		if(epgs!=null){
			itemView.fillProgram(epgs.get(position));
		}else if(channels!=null){
			itemView.fillData(channels.get(position), isShowChnNum);
		}
		itemView.setOnItemCallBackListener(mListener);
		if(clickable)
			itemView.setOnClickable(true);
		return itemView;
	}
}
