package com.star.mobile.video.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.star.cms.model.Bill;
import com.star.cms.model.Payment;
import com.star.mobile.video.R;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.DateFormat;

public class AccountBillItemView extends LinearLayout {
	
	private Context mContext;
	private Bill bill;
	private Payment payment;
	private String cardNo;
	private TextView tvType;
	private TextView tvMoney;
	private TextView tvSmartCardNo;
	private TextView tvRehargeNo;
	private TextView tvRechargeTxt;
	private TextView time;
	private View line;
	
	
	public AccountBillItemView(Context context) {
		this(context, null);
	}

	public AccountBillItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		LayoutInflater.from(context).inflate(R.layout.view_account_bill_item, this);
		tvType =(TextView) findViewById(R.id.tv_type);
		tvSmartCardNo = (TextView) findViewById(R.id.tv_smart_card_no);
		tvMoney = (TextView) findViewById(R.id.tv_money);
		tvRehargeNo = (TextView) findViewById(R.id.tv_recharge_no);
		tvRehargeNo.setSelected(true);
		tvRechargeTxt = (TextView) findViewById(R.id.tv_recharge_txt);
		time = (TextView) findViewById(R.id.tv_time);
		line = findViewById(R.id.v_line);
	}
	
	public void setData(Bill bill,Payment payment) {
		this.bill = bill;
		this.payment = payment;
		initData();
	}
	
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
		tvSmartCardNo.setText(cardNo);
	}
	
	public void setLineVisible(int vis) {
		line.setVisibility(vis);
	}
	
	
	private void initData() {
		tvSmartCardNo.setText(cardNo);
		if(bill != null) {
			double fee = bill.getFee();
			if(fee > 0) {
				tvType.setText(mContext.getString(R.string.deduct));
				tvMoney.setText("-"+formatMoney(fee));
			} else {
				tvType.setText(mContext.getString(R.string.refund));
				String fe = formatMoney(fee).replaceAll("-", "+");
				tvMoney.setText(fe);
			}
			time.setText(formatBillDate(bill.getCreateDate()));
			tvRehargeNo.setText(bill.getAccountItemType());
			tvRechargeTxt.setText(mContext.getString(R.string.details));
		} else if(payment != null) {
			tvRehargeNo.setVisibility(View.GONE);
			tvRechargeTxt.setVisibility(View.GONE);
			if(payment.getPayMethod() == Payment.CASH) {
				tvType.setText(mContext.getString(R.string.cash));
			} else if(payment.getPayMethod() == Payment.RECHARGE_CARD) {
				tvType.setText(mContext.getString(R.string.recharge_card));
			} else {
				tvType.setText(mContext.getString(R.string.other_method));
			}
			double payFee = payment.getPayFee();
			if(payFee > 0) {
				tvMoney.setText("+"+formatMoney(payFee));
			} else {
				tvMoney.setText(formatMoney(payFee));
			}
			time.setText(formatPayDate(payment.getCreateDate()));
		}
	}
	
	private String formatMoney(double money) {
		if(money == 0) {
			return "0.00";
		}
		java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");  
		return df.format(money);
	}
	
	private String formatBillDate(String date) {
		Date da = DateFormat.stringToDate(date, "yyyyMMdd",null);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(da);
	}
	
	private String formatPayDate(String date) {
		Date da = DateFormat.stringToDate(date, "yyyyMMddHHmmss",null);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(da);
	}
	
}
