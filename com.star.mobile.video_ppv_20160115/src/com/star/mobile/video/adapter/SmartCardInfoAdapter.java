package com.star.mobile.video.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.star.cms.model.vo.SmartCardInfoVO;
import com.star.mobile.video.R;

public class SmartCardInfoAdapter extends BaseAdapter{

	private List<SmartCardInfoVO> datas;
	private Context context;
	private long selectId;
	
	public SmartCardInfoAdapter(Context context,List<SmartCardInfoVO> data,long selectId) {
		this.datas = data;
		this.context = context;
		this.selectId = selectId;
	}
	
	public void updateDateAndRefreshUI(List<SmartCardInfoVO> datas,long smartCardId){
		this.datas = datas;
		this.selectId = smartCardId;
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
		HandlerView hv;
		if(view == null) {
			hv = new HandlerView();
			view = LayoutInflater.from(context).inflate(R.layout.view_topup_item, null);
			hv.tvName = (TextView)view.findViewById(R.id.tv_name);
			hv.tvNo = (TextView)view.findViewById(R.id.tv_no);
			hv.line = view.findViewById(R.id.v_line);
			view.setTag(hv);
		} else {
			hv = (HandlerView)view.getTag();
		}
		hv.tvName.setText(datas.get(position).getSmardCardNo());
		hv.tvName.setTag(datas.get(position));
		if(getCount() -1 == position) {
			hv.line.setVisibility(View.INVISIBLE);
		} else {
			hv.line.setVisibility(View.VISIBLE);
		}
		if(datas.get(position).getId() == selectId) {
			hv.tvName.setTextColor(context.getResources().getColor(R.color.orange));
			hv.tvNo.setTextColor(context.getResources().getColor(R.color.orange));
		} else {
			hv.tvName.setTextColor(context.getResources().getColor(R.color.choose_text));
			hv.tvNo.setTextColor(context.getResources().getColor(R.color.choose_text));
		}
		return view;
	}

	class HandlerView {
		public TextView tvName;
		public TextView tvNo;
		public View line;
	}
}
