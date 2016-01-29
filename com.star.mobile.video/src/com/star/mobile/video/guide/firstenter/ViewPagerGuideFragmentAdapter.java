package com.star.mobile.video.guide.firstenter;

import java.util.Random;

import com.star.util.verticalviewpager.PagerAdapter;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

/**
 * 引导页面的adapter
 * 
 * @author Lee
 *
 */
public class ViewPagerGuideFragmentAdapter extends PagerAdapter {
	private int[] mImageViewResources;
	private Random mRandom = new Random();
	public ViewPagerGuideFragmentAdapter(int[] imageViewResources) {
		super();
		this.mImageViewResources = imageViewResources;
	}

	@Override
	public int getCount() {
		return mImageViewResources.length;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		ImageView imageView = new ImageView(container.getContext());
		imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		imageView.setBackgroundResource(mImageViewResources[position]);
		container.addView(imageView);
		return imageView;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}
}
