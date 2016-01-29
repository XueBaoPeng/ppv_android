package com.star.mobile.video.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
/**
 * 跑马灯效果
 * @author Lee
 * @dete 2015/12/24
 *
 */
public class AlwaysMarqueeTextView extends TextView {
	public AlwaysMarqueeTextView(Context context) {
		super(context);
	}

	public AlwaysMarqueeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AlwaysMarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean isFocused() {
		return true;
	}
}
