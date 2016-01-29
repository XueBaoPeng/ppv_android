package com.star.mobile.video.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.star.cms.model.vo.ExchangeVO;
import com.star.mobile.video.R;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.view.UseCouponItemView;

public class UseCouponAdapter extends BaseAdapter{

	private List<ExchangeVO> datas;
	private Context context;
	private int pos = -1;
	
	public UseCouponAdapter(List<ExchangeVO> datas,Context context) {
		this.datas = datas;
		this.context = context;
	}
	
	public void setSelectPos(int pos){
		this.pos = pos;
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
		UseCouponItemView ucv;
		if(view == null) {
			ucv = new UseCouponItemView(context);
		} else {
			ucv = (UseCouponItemView) view;
		}
		if(pos == position){
			ucv.setBackgrod(R.drawable.choose_coupon_bg);
			ucv.setTextColor(context.getResources().getColor(R.color.white));
			ucv.setIsSelect(true);
		}
		ucv.setTag(datas.get(position));
		ucv.setCouponFace(SharedPreferencesUtil.getCurrencSymbol(context)+(int)datas.get(position).getFaceValue());
		return ucv;
	}

}
