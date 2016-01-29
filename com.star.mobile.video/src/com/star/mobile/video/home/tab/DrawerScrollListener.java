package com.star.mobile.video.home.tab;

import java.util.List;

import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.star.mobile.video.util.ImageUtil;
/**
 * 
 * @author dujr
 *
 * @param <T>
 */
public class DrawerScrollListener implements OnScrollListener {

	private int mHeight;
	private int mItemCount;
	private int mItemOffsetY[];
	private boolean scrollIsComputed = false;
	private UpDrawerView mTopDrawer;
	private DownDrawerView mBottomDrawer;
	private com.star.ui.DragTopLayout mDragLayout;
	private List<Integer> headerHeights;
	private List<Integer> footerHeights;
	
	public void setDrawerView(DownDrawerView bottomDrawer, UpDrawerView topview, com.star.ui.DragTopLayout dragLayout){
		this.mBottomDrawer = bottomDrawer;
		this.mTopDrawer = topview;
		this.mDragLayout = dragLayout;
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if(scrollState == SCROLL_STATE_IDLE) {
			if(mBottomDrawer!=null){
				mBottomDrawer.move(view.getFirstVisiblePosition());
			}
			if(mTopDrawer!=null){
				mTopDrawer.move(view.getFirstVisiblePosition());
				if(mBottomDrawer!=null&&mBottomDrawer.getTransY()==0&&mTopDrawer.getTransY()!=0&&view.getFirstVisiblePosition()==0&&view.getChildAt(0)!=null&&view.getChildAt(0).getTop()==0){
					mTopDrawer.scrollTo(0, 0);
					mTopDrawer.init();
					mBottomDrawer.init();
				}
			}
		}
		if(view!=null && view instanceof com.star.mobile.video.view.ListView){
			com.star.mobile.video.view.ListView listview = (com.star.mobile.video.view.ListView)view;
			listview.scrollToLoading(scrollState);
		}
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		boolean intercept = false;
		if(mDragLayout!=null){
			intercept = ImageUtil.isAdapterViewAttach(view);
			mDragLayout.setTouchMode(intercept);
		}
		if (scrollYIsComputed()) {
			if(!intercept||firstVisibleItem==0){
				int scrollY = getComputedScrollY(view);
				if(mTopDrawer!=null){
					if(mBottomDrawer==null||mBottomDrawer.getTransY()==0){
						mTopDrawer.scrollY = scrollY+mTopDrawer.offsetY;
						if(mBottomDrawer!=null){
							mTopDrawer.scrollY -= mBottomDrawer.offsetY;
						}
						mTopDrawer.scrollTop();
					}
				}
				if(mBottomDrawer!=null){
					if(mTopDrawer==null){
//						Log.e("AAA", "scrollY="+scrollY+"///offsetY="+mBottomDrawer.offsetY);
						mBottomDrawer.scrollY = scrollY-mBottomDrawer.offsetY;
						mBottomDrawer.scrollBottom();
					}else if(Math.abs(mTopDrawer.getTransY())>=mTopDrawer.getHeight()){
						mBottomDrawer.scrollY = scrollY-mBottomDrawer.offsetY-mTopDrawer.getHeight()+mTopDrawer.offsetY;
						mBottomDrawer.scrollBottom();
					}
				}
			}
		}else{
			computeScrollY(view);
		}
	}

	public void computeScrollY(AbsListView mScrollView) {
		mHeight = 0;
		mItemOffsetY = null;
		mItemCount = mScrollView.getAdapter().getCount();
		if (mItemOffsetY == null) {
			mItemOffsetY = new int[mItemCount];
		}
		for (int i = 0; i < mItemCount; ++i) {
			View view = mScrollView.getAdapter().getView(i, null, mScrollView);
			view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
			mItemOffsetY[i] = mHeight;
			if(headerHeights!=null){
				switch (headerHeights.size()) {
				case 0:
					nextMeasure(view, i);
					break;
				case 1:
					if(i==0){
						mHeight = headerHeights.get(0);
					}else{
						nextMeasure(view, i);
					}
					break;
				case 2:
					if(i==0){
						mHeight = headerHeights.get(0);
					}else if(i==1){
						mHeight += headerHeights.get(1);
					}else{
						nextMeasure(view, i);
					}
					break;
				}
			}else{
				nextMeasure(view, i);
			}
		}
		scrollIsComputed = true;
	}

	private void nextMeasure(View view, int pos) {
		if(footerHeights!=null){
			switch (footerHeights.size()) {
			case 0:
				mHeight += view.getMeasuredHeight();
				break;
			case 1:
				if(pos==mItemCount-1)
					mHeight += footerHeights.get(0);
				else
					mHeight +=view.getMeasuredHeight();
				break;
			case 2:
				if(pos==mItemCount-2)
					mHeight += footerHeights.get(0);
				if(pos==mItemCount-1)
					mHeight += footerHeights.get(1);
				else
					mHeight += view.getMeasuredHeight();
				break;
			}
		}else{
			mHeight += view.getMeasuredHeight();
		}
	}

	public boolean scrollYIsComputed() {
		return scrollIsComputed;
	}
	
	public void setscrollYIsComputed(boolean scrollIsComputed){
		this.scrollIsComputed = scrollIsComputed;
	}

	public void initView() {
		if(mBottomDrawer!=null){
			mBottomDrawer.init();
		}
		if(mTopDrawer!=null){
			mTopDrawer.init();
		}
	}

	public int getComputedScrollY(AbsListView mScrollView) {
		int pos, nScrollY = 0, nItemY = 0;
		View view = null;
		pos = mScrollView.getFirstVisiblePosition();
		view = mScrollView.getChildAt(0);
		if(view!=null&&mItemOffsetY.length>pos){
			nItemY = view.getTop();
			nScrollY = mItemOffsetY[pos] - nItemY;
		}
//		Log.e("AAA", "+++++++++"+pos+"++++++++"+mItemOffsetY[pos]+"+++++++++"+nItemY+"--------"+mBottomDrawer.offsetY);
		return nScrollY;
	}

	public void setHeaderHeights(List<Integer> headerHeights) {
		this.headerHeights = headerHeights;
	}

	public void setFooterHeights(List<Integer> footerHeights) {
		this.footerHeights = footerHeights;
	}
	
}
