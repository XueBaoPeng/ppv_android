package com.star.mobile.video.me.mycoins;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;

public class ScrollView extends android.widget.ScrollView {

	public ScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {

		return 0;
	}
}
