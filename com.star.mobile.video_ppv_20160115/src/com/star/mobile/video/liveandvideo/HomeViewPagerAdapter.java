package com.star.mobile.video.liveandvideo;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.star.cms.model.vo.ChannelVO;

/**
 * viewpager adapter
 * 
 * @author Lee
 * @version 1.0
 * @date 2015/10/29
 *
 */
public class HomeViewPagerAdapter extends PagerAdapter {

	private List<ChannelVO> mTotalChannels;
	private List<View> mViewDatas;
//	private List<View> cachedViews = new ArrayList<View>();
	public HomeViewPagerAdapter(List<View> viewDatas, List<ChannelVO> totalChannels) {
		this.mViewDatas = viewDatas;
		this.mTotalChannels = totalChannels;
	}

	public void setTotalChannels(List<ChannelVO> totalChannels) {
		this.mTotalChannels = totalChannels;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mTotalChannels.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		super.setPrimaryItem(container, position, object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
//		for (View v: cachedViews) {
//			ChannelDetailView channelDetailView=(ChannelDetailView)v;
//			channelDetailView.onStop();
//		}
//		cachedViews.clear();
//		cachedViews.add(mViewDatas.get(position%mViewDatas.size()));
		// 对ViewPager页号求模取出View列表中要显示的项
		position %= mViewDatas.size();
		View view = mViewDatas.get(position);
		// 如果View已经在之前添加到了一个父组件，则必须先remove，否则会抛出IllegalStateException。
		ViewParent vp = view.getParent();
		if (vp != null) {
			ViewGroup parent = (ViewGroup) vp;
			parent.removeView(view);
		}

		container.addView(view);
		return view;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// Warning：不要在这里调用removeView
//		ChannelDetailView channelDetailView = (ChannelDetailView) mViewDatas.get(position%mViewDatas.size());
//		channelDetailView.onStop();
	}

}
