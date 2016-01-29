package com.star.mobile.video.guide.firstenter;


import com.star.mobile.video.R;
import com.star.mobile.video.account.ChooseAreaActivity;
import com.star.mobile.video.activity.WelcomeActivity;
import com.star.mobile.video.guide.firstenter.flakeview.FlakeView;
import com.star.mobile.video.home.HomeActivity;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.util.verticalviewpager.VerticalViewPager;
import com.star.util.verticalviewpager.VerticalViewPager.OnPageChangeListener;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
/**
 * 欢迎页的引导
 * @author Lee
 * @version 1.0 
 * @date 2015/12/4
 *
 */
public class WelcomeGuideView extends RelativeLayout implements OnClickListener{
	private int[] mImageViewResources = new int[]{R.drawable.guide01,R.drawable.guide02,R.drawable.guide03,R.drawable.guide04};
	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private View mView;
	private ViewPagerGuideFragmentAdapter mViewPagerGuideFragmentAdapter;
	private LinearLayout mLinearLayoutContent;
	private ImageView mViewPagerStartImageView;
	private VerticalViewPager mVerticalViewPager;
	private FlakeView mFlakeView;
	private RelativeLayout mGuideArrowRL;
	private ImageView mGuideArrow1;
	private ImageView mGuideArrow2;
	
	private int mGuideArrow1Left;
	private int mGuideArrow1Height;
	private int mGuideArrow1Right;
	private int mGuideArrow1Top;
	private int mGuideArrow1Temp;
	private ArrowRunable mArrowRunable;
	Handler mHandlerRain = new Handler();
	Runnable mRunnableRain = new Runnable() {
		
		@Override
		public void run() {
			mFlakeView.addFlakes(15);
			mHandlerRain.postDelayed(mRunnableRain, 2000);
			if(mFlakeView.getNumFlakes() > 70)
			{
				mHandlerRain.removeCallbacks(mRunnableRain);
			}
		}
	};
	
	public WelcomeGuideView(Context context) {
		super(context);
		initView(context);
		initData();
	}

	public WelcomeGuideView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		initData();
	}

	public WelcomeGuideView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
		initData();
	}

	private void initView(Context context){
		this.mContext = context;
		mLayoutInflater = LayoutInflater.from(mContext);
		mView = mLayoutInflater.inflate(R.layout.view_welcome_guide, this);
		mLinearLayoutContent = (LinearLayout) mView.findViewById(R.id.ly_content);
		mViewPagerStartImageView = (ImageView) mView.findViewById(R.id.viewpager_start_imageview);
		mVerticalViewPager = (VerticalViewPager) mView.findViewById(R.id.directional_viewpager);
		mGuideArrowRL = (RelativeLayout) findViewById(R.id.guide_arrow);
		mGuideArrow1 = (ImageView) findViewById(R.id.guide_arrow_1);  
		mGuideArrow2 = (ImageView) findViewById(R.id.guide_arrow_2);  
  
	}
	private void initData(){
		mFlakeView = new FlakeView(mContext);
		mLinearLayoutContent.addView(mFlakeView);
		mViewPagerGuideFragmentAdapter = new ViewPagerGuideFragmentAdapter(mImageViewResources);
		mVerticalViewPager.setAdapter(mViewPagerGuideFragmentAdapter);
		mVerticalViewPager.setCurrentItem(0);
		mVerticalViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
			}
			
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				if(position == mImageViewResources.length-1){
					mGuideArrowRL.setVisibility(View.GONE);
					mViewPagerStartImageView.setVisibility(View.VISIBLE);
				}else{
					mGuideArrowRL.setVisibility(View.VISIBLE);
					mViewPagerStartImageView.setVisibility(View.GONE);
				}
			}
			
			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
        mViewPagerStartImageView.setOnClickListener(this);
        mHandlerRain.postDelayed(mRunnableRain, 0);
        
        mArrowRunable = new ArrowRunable();
        mGuideArrow1.getViewTreeObserver().addOnGlobalLayoutListener(  
                new OnGlobalLayoutListener() {  
  
                    @Override  
                    public void onGlobalLayout() {  
                        // TODO Auto-generated method stub  
                    	mGuideArrow1Left = mGuideArrow1.getLeft();  
                    	mGuideArrow1Height = mGuideArrow1.getHeight();  
                    	mGuideArrow1Right = mGuideArrow1.getRight();  
                    	mGuideArrow1Top = mGuideArrow1.getTop();  
                    	mGuideArrow1Temp = mGuideArrow1Top;  
                    	mGuideArrow1.getViewTreeObserver().removeGlobalOnLayoutListener(  
                                this);  
                    	mHandlerRain.post(mArrowRunable);  
                    }  
                });  
        
	}

	class ArrowRunable implements Runnable {  
		  
        @Override  
        public void run() {  
        	mGuideArrow1.layout(mGuideArrow1Left, mGuideArrow1Temp - mGuideArrow1Height-1, mGuideArrow1Right, mGuideArrow1Temp-1);  
        	mGuideArrow2.layout(mGuideArrow1Left, mGuideArrow1Temp, mGuideArrow1Right, mGuideArrow1Temp + mGuideArrow1Height);  
        	mGuideArrow1Temp -= mGuideArrow1Height;  
            if (mGuideArrow1Temp + 3 * mGuideArrow1Height < 0) {  
            	mGuideArrow1Temp = mGuideArrow1Top;  
            }  
            mHandlerRain.postDelayed(this, 180);  
        }  
    }  
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.viewpager_start_imageview:
			if(SharedPreferencesUtil.getAreaCode((Activity)getContext())==null){
				CommonUtil.startActivity((Activity)getContext(), ChooseAreaActivity.class);
			}else{
				CommonUtil.startActivity((Activity)getContext(), HomeActivity.class);
			}
			SharedPreferencesUtil.setAppGuideDone(getContext());
			CommonUtil.finishActivity((WelcomeActivity) getContext());
			break;

		default:
			break;
		}
	}
	
}
