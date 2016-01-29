package com.star.mobile.video.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.star.mobile.video.R;
import com.star.mobile.video.me.coupon.MyCouponsActivity;
import com.star.mobile.video.me.mycoins.MyCoinsActivity;
import com.star.mobile.video.util.CommonUtil;

public class RewardHeadView extends LinearLayout{
	
	private View mView;
	private Activity activity;
	private TextView tvCoinsNumber;
	private TextView tvCouponsNumber;
	private TextView tvConisText;
	private String coinsNumber;
	private String couponsNumber;
	private RelativeLayout rlMyCoupons;
	
	/**
	 * @param context
	 */
	public RewardHeadView(Activity activity) {
		this(activity, null);
		this.activity = activity;
	}
	

	public RewardHeadView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mView = LayoutInflater.from(context).inflate(R.layout.view_reward_head, this);
		tvCoinsNumber = (TextView) mView.findViewById(R.id.tv_coin_number);
		tvCouponsNumber = (TextView) mView.findViewById(R.id.tv_coupon_number);
		tvConisText = (TextView) mView.findViewById(R.id.tv_coins_text);
		rlMyCoupons = (RelativeLayout) mView.findViewById(R.id.rl_mycoupons);
		rlMyCoupons.setOnClickListener(click);
		tvConisText.setOnClickListener(click);
		tvConisText.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		if(coinsNumber != null) {
			tvConisText.setText(coinsNumber);
		}
		if(couponsNumber != null) {
			tvCouponsNumber.setText(couponsNumber);
		}
	}

	public void setCoinsNumber(String number) {
		tvCoinsNumber.setText(number);
	}
	
	public void setCouponsNumber(String number) {
		tvCouponsNumber.setText(number);
	}
	
	
	private OnClickListener click = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.rl_mycoupons:
				CommonUtil.startActivity(activity, MyCouponsActivity.class);
				break;
			case R.id.tv_coins_text:
				CommonUtil.startActivity(activity, MyCoinsActivity.class);
				break;
			default:
				break;
			}
		}
	};
}
