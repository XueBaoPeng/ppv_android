package com.star.mobile.video.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.star.mobile.video.R;

public class UseCouponItemView extends LinearLayout{

	private TextView tvCouponFace;
	private boolean isSelect = false;//是否选中
	
	public UseCouponItemView(Context context) {
		this(context, null);
	}
	
	public UseCouponItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.view_use_coupon_item, this);
		tvCouponFace = (TextView) findViewById(R.id.tv_coupon_face);
	}
	
	
	/**
	 * 优惠卷面值
	 * @param value
	 */
	public void setCouponFace(String value) {
		tvCouponFace.setText(value);
	}

	public void setBackgrod(int resid) {
		tvCouponFace.setBackgroundResource(resid);
	}
	
	public void setTextColor(int color) {
		tvCouponFace.setTextColor(color);
	}
	
	public boolean isSelect() {
		return isSelect;
	}
	
	public void setIsSelect(boolean isSelect) {
		this.isSelect = isSelect;
	}

}
