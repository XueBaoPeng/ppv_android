package com.star.mobile.video.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.star.mobile.video.R;
import com.star.mobile.video.util.DensityUtil;

public class AddSmardCartView extends LinearLayout{

	private RelativeLayout rlAddSmartCardBtn;
	private Scroller mScroller;
	private ImageView ivAddCardIconBtn;
	private TextView btnRecharge;
	
	public AddSmardCartView(Context context) {
		this(context, null);
	}
	
	public AddSmardCartView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.view_add_smartcard, this);
		rlAddSmartCardBtn = (RelativeLayout) findViewById(R.id.rl_account_add);
		ivAddCardIconBtn = (ImageView) findViewById(R.id.iv_add_card_btn);
		btnRecharge = (TextView) findViewById(R.id.btn_recharge_text);
		mScroller = new Scroller(context);
	}
	
	public void setAddCartBtnClick(OnClickListener l) {
		rlAddSmartCardBtn.setOnClickListener(l);
	}
	
	public void setIconAddCartBtnClick(OnClickListener l) {
		ivAddCardIconBtn.setOnClickListener(l);
	}
	
	public void setIconAddCardRes(int resId){
		btnRecharge.setText(resId);
	}
	
	public void showOptionBtn() {
		mScroller.startScroll(0, 0, DensityUtil.dip2px(getContext(), 60), 0, 300);
		invalidate();
	}
	
	public void hideOptionBtn() {
		mScroller.startScroll(getLeft(), 0, 0, 0, 300);
		invalidate();
	}
	
	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		}
	}

}
