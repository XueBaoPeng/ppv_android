package com.star.mobile.video.me.coupon;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.star.cms.model.vo.ExchangeVO;
import com.star.cms.model.vo.SmartCardInfoVO;
import com.star.mobile.video.view.CouponItemView;

public class MyCouponListAdapter extends BaseAdapter{

	private List<ExchangeVO> datas;
	private Context context;
	private int inVaildPos = -1;
	private String cardNum; 
	private SmartCardInfoVO smartcardInfo;
	
	public MyCouponListAdapter(Context context, List<ExchangeVO> datas ,String cardNum ,SmartCardInfoVO smartcardInfo) {
		this.context = context;
		this.datas = datas;
		this.inVaildPos = getPositionForInValid();
		this.cardNum=cardNum;
		this.smartcardInfo=smartcardInfo;
	}
	
	public void updateDataAndRefreshUI(List<ExchangeVO> datas){
		this.datas = datas;
		this.inVaildPos = getPositionForInValid();
		notifyDataSetChanged();
	}
	
	public int getPositionForInValid() {
		for (int i = 0; i < getCount(); i++) {
			ExchangeVO vo = datas.get(i);
			if(vo.isValid() || vo.isAccepted()){
				return i;
			}
		}
		return -1;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		CouponItemView itemView = (CouponItemView)convertView;
		if (itemView == null) {
			itemView = new CouponItemView(context);
		}
		itemView.fillData(datas.get(position),cardNum,smartcardInfo);
		if(inVaildPos == position){
			itemView.setPadding(0, 20, 0, 0);
		}else if(itemView.getPaddingTop() != 0){
			itemView.setPadding(0, 0, 0, 0);
		}
		return itemView;
	}
}
