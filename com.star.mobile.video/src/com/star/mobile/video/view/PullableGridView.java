package com.star.mobile.video.view;

import com.star.mobile.video.util.Pullable;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 可以上拉、下拉的自定义GridView视图
 * 
 * @author Lee
 * @version 1.0 2015/08/31
 *
 */
public class PullableGridView extends GridView implements Pullable {

	private boolean canPlull = true;

	public PullableGridView(Context context) {
		super(context);
	}

	public PullableGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PullableGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean canPullDown() {
		// if (getCount() == 0)
		// {
		// // 没有item的时候也可以下拉刷新
		// return true;
		// } else if (getFirstVisiblePosition() == 0
		// && getChildAt(0).getTop() >= 0)
		// {
		// // 滑到顶部了
		// return true;
		// } else
		return false;
	}

	@Override
	public boolean canPullUp() {
		if (getCount() == 0) {
			// 没有item的时候不可以上拉加载
			return false;
		} else if (getLastVisiblePosition() == (getCount() - 1)) {
			if(!canPlull) {
				return false;
			}
			// 滑到底部了
			if (getChildAt(getLastVisiblePosition() - getFirstVisiblePosition()) != null
					&& getChildAt(getLastVisiblePosition() - getFirstVisiblePosition())
							.getBottom() <= getMeasuredHeight())
				return true;
		}
		return false;
	}

	public void setCanPlull(boolean canPlull) {
		this.canPlull = canPlull;
	}

}
