package com.star.mobile.video.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListAdapter;

import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.DensityUtil;
import com.star.ui.HorizontalListView;

public class MyHorizontalListView extends HorizontalListView {

	public MyHorizontalListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
		int leftOffset = (Constant.WINDOW_WIDTH -3*DensityUtil.dip2px(getContext(), 6)- 2*DensityUtil.dip2px(getContext(), 10))/4+DensityUtil.dip2px(getContext(), 6)/2;
		setSelectionFromLeft(0, -leftOffset);
	}

}
