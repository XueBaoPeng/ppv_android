package com.star.mobile.video.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.star.cms.model.Bill;
import com.star.cms.model.Payment;
import com.star.mobile.video.view.AccountBillItemView;

public class AccountBillAdapter extends BaseAdapter {
	
	private List<Bill> billData;
	private List<Payment> paymentData;
	private Context context;
	private String smartCard;
	
	public AccountBillAdapter(Context context,List<Bill> billData,List<Payment> paymentData,String smartCard) {
		this.billData = billData;
		this.paymentData = paymentData;
		this.context = context;
		this.smartCard = smartCard;
	}

	@Override
	public int getCount() {
		if(billData != null) {
			return billData.size();
		} else if(paymentData != null) {
			return paymentData.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		if(billData != null) {
			return billData.get(position);
		} else if(paymentData != null) {
			return paymentData.get(position);
		} else {
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		AccountBillItemView accountBillItemView = null;
		if(view == null) {
			if(billData != null) {
				accountBillItemView = new AccountBillItemView(context);
			} else if(paymentData != null) {
				accountBillItemView = new AccountBillItemView(context);
			}
		} else {
			accountBillItemView = (AccountBillItemView)view;
		}
		if(billData != null) {
			if(position == billData.size() -1) {
				accountBillItemView.setLineVisible(View.GONE);
			} else {
				accountBillItemView.setLineVisible(View.VISIBLE);
			}
			accountBillItemView.setData(billData.get(position), null);
		} else if(paymentData != null) {
			if(position == paymentData.size() -1) {
				accountBillItemView.setLineVisible(View.GONE);
			} else {
				accountBillItemView.setLineVisible(View.VISIBLE);
			}
			accountBillItemView.setData(null, paymentData.get(position));
		}
		accountBillItemView.setCardNo(smartCard);
		
		return accountBillItemView;
	}

}
