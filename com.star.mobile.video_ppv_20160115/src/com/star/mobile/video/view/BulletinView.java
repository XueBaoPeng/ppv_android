package com.star.mobile.video.view;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

/**
 * 跑马灯效果
 * @author Lee 
 * @version1.0 2015/08/19
 */
public class BulletinView extends TextView implements Runnable {
	private int currentScrollX;// 当前滚动的位置
	private boolean isStop = false;
	private int textWidth;

	public BulletinView(Context context) {
		this(context, null);
	}

	public BulletinView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public void init() {
		setClickable(true);
		setSingleLine(true);
		setEllipsize(TruncateAt.MARQUEE);
		setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
	}

	public void setData(String str) {
		setText(str);
		setTag(str);
		startScroll();
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		super.setText(text, type);
		MeasureTextWidth();
	}

	/**
	 * 获取文字宽度
	 */
	private void MeasureTextWidth() {
		Paint paint = this.getPaint();
		String str = this.getText().toString();
		textWidth = (int) paint.measureText(str);
	}

	@Override
	public void run() {
		if (textWidth < 1) {
			return;
		}
		currentScrollX += 6;// 滚动速度
		scrollTo(currentScrollX, 0);
		if (isStop) {
			return;
		}
		if (getScrollX() >= textWidth) {
			currentScrollX = -getWidth();
			scrollTo(currentScrollX, 0);
		}

		postDelayed(this, 30);
	}


	// 开始滚动
	public void startScroll() {
		isStop = false;
		this.removeCallbacks(this);
		post(this);
	}

	// 停止滚动
	public void stopScroll() {
		isStop = true;
	}
}
