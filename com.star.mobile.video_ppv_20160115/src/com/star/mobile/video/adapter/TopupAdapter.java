package com.star.mobile.video.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.star.cms.model.vo.ExchangeVO;
import com.star.mobile.video.R;
import com.star.mobile.video.shared.SharedPreferencesUtil;

public class TopupAdapter extends BaseAdapter{
	
	private Context context;
	private List<ExchangeVO> datas;
	private long selectId;
	
	public TopupAdapter(Context context,List<ExchangeVO> datas,long selectId) {
		this.context = context;
		this.datas = datas;
		this.selectId = selectId;
	}

	public void updateDateAndRefreshUI(List<ExchangeVO> datas,long exchangeId){
		this.datas = datas;
		this.selectId = exchangeId;
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
			hv.line = view.findViewById(R.id.v_line);
			view.setTag(hv);
		} else {
			hv = (HandlerView)view.getTag();
		}
		hv.tvName.setText(SharedPreferencesUtil.getCurrencSymbol(context)+datas.get(position).getFaceValue());
		hv.tvName.setTag(datas.get(position).getId());
		if(getCount() -1 == position) {
			hv.line.setVisibility(View.INVISIBLE);
		} else {
			hv.line.setVisibility(View.VISIBLE);
		}
		if(datas.get(position).getId() == selectId) {
			hv.tvName.setTextColor(context.getResources().getColor(R.color.orange));
		} else {
			hv.tvName.setTextColor(context.getResources().getColor(R.color.choose_text));
		}
		return view;
	}

	class HandlerView {
		public TextView tvName;
		public View line;
	}
}
