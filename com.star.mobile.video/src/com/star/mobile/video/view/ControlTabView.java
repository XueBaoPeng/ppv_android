package com.star.mobile.video.view;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.star.cms.model.vo.ChannelVO;
import com.star.mobile.video.R;
import com.star.mobile.video.home.tab.UpDrawerView;
import com.star.mobile.video.util.DensityUtil;
import com.star.mobile.video.view.FaceContainer;

public class ControlTabView extends LinearLayout {
	private RadioGroup radioGroup;
	private HorizontalScrollView mHorizontalScrollView;
	int screenWidth;
	private TextView mTextViewLine;
	private TextView mTextViewLineChannel;
	private int columnSelectIndex = 0;
	private int mItemWidth = 0;
	int currenttab=0;
	public  int TAB_COUNT = 3;
	private ViewPager mViewPager;
	private List<View> view;
	private Context mContext;
	private boolean isScroll = true;
	public ControlTabView(Context context) {
		super(context);
		mContext = context;
		initButton();
	}	
	public ControlTabView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initButton();
	}
	/**
	 * 初始化按钮
	 */
	private void initButton() {
		LayoutInflater.from(mContext).inflate(R.layout.view_control_tab, this);
		mHorizontalScrollView = (HorizontalScrollView) findViewById(R.id.mHorizontalScrollView);
		radioGroup = (RadioGroup) findViewById(R.id.rg_group);
		mTextViewLine = (TextView) findViewById(R.id.line);
		mTextViewLineChannel =(TextView) findViewById(R.id.line_channel);
		mHorizontalScrollView.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return isScroll;
			}
		});
	}
	
	private void imageMove(final int moveToTab)
	{
		mTextViewLineChannel.setVisibility(View.GONE);
		mTextViewLine.setVisibility(View.VISIBLE);
		int startPosition=0;
		int movetoPosition=0;
		
		startPosition=currenttab*(screenWidth/TAB_COUNT);
		movetoPosition=moveToTab*(screenWidth/TAB_COUNT);
		//平移动画
		TranslateAnimation translateAnimation=new TranslateAnimation(startPosition,movetoPosition, 0, 0);
		translateAnimation.setFillAfter(true);
		translateAnimation.setDuration(200);
		mTextViewLine.startAnimation(translateAnimation);
		translateAnimation.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {
				
			}
			
			public void onAnimationRepeat(Animation animation) {
				
			}
			
			public void onAnimationEnd(Animation animation) {
				LinearLayout.LayoutParams imageParams=new LinearLayout.LayoutParams(screenWidth/TAB_COUNT, DensityUtil.dip2px(getContext(), 4));
				imageParams.setMargins(moveToTab*(screenWidth/TAB_COUNT), 0, 0, 0);
				imageParams.gravity = Gravity.BOTTOM;
				mTextViewLineChannel.setLayoutParams(imageParams);
				mTextViewLineChannel.setVisibility(View.VISIBLE);
				mTextViewLine.setVisibility(View.GONE);
			}
		} );
		
	}
	public void setTabData(List<String> tab){
		if(tab.size()<3){
			TAB_COUNT = tab.size();
		}else{
			TAB_COUNT = 3;
		}
		screenWidth=getResources().getDisplayMetrics().widthPixels;
		LinearLayout.LayoutParams imageParams=new LinearLayout.LayoutParams(screenWidth/TAB_COUNT, DensityUtil.dip2px(getContext(), 4));
		imageParams.gravity = Gravity.BOTTOM;
		mTextViewLine.setLayoutParams(imageParams);
//		LinearLayout.LayoutParams imageParams=new LinearLayout.LayoutParams(screenWidth/TAB_COUNT, DensityUtil.dip2px(getContext(), 4));
//		imageParams.gravity = Gravity.BOTTOM;
//		mTextViewLineChannel.setLayoutParams(imageParams);
//		mTextViewLineChannel.setVisibility(View.VISIBLE);
//		mTextViewLine.setVisibility(View.GONE);
		mItemWidth = screenWidth / TAB_COUNT;
		for(int i = 0; i<tab.size();i++){
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mItemWidth , DensityUtil.dip2px(getContext(), 52));
			RadioButton radioButton = new RadioButton(mContext);
			radioButton.setGravity(Gravity.CENTER);
			radioButton.setPadding(0, 0, 0, 0);
			radioButton.setId(i);
			radioButton.setText(tab.get(i));
			radioButton.setTextColor(getResources().getColorStateList(R.color.tab_btn_selector));
			radioButton.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
			if(columnSelectIndex == i){
				radioButton.setSelected(true);
			}
			radioButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
			          for(int i = 0;i < radioGroup.getChildCount();i++){
				          View localView = radioGroup.getChildAt(i);
				          if (localView != v){
				        	  localView.setSelected(false);
					          if(mViewPager == null){
								  view.get(i).setVisibility(View.GONE);
							   }
				          }else{
				        	  localView.setSelected(true);
				        	  selectTab(i);
				        	  if(mViewPager != null){
					        		 mViewPager.setCurrentItem(i);
				        	  }else{
				        		  view.get(i).setVisibility(View.VISIBLE);
				        	  }
				          }
			          }
				}
			});
			radioGroup.addView(radioButton, i ,params);
		}
	}
	public void setViewPager(ViewPager viewPager){
		this.mViewPager = viewPager;
	}
	public void setView(List<View> view) {
		this.view = view;

	}
	public void selectTab(int tab_postion) {
		if(columnSelectIndex != tab_postion){
			if(tab_postion == 0){
				imageMove(0);
				currenttab=0;	
			}else if (tab_postion == radioGroup.getChildCount()-1){
				if(tab_postion == 1){
					imageMove(1);
					currenttab=1;				
				}else{
					imageMove(2);
					currenttab=2;	
				}
			}else{
				imageMove(1);
				currenttab=1;
			}
		}
		columnSelectIndex = tab_postion;
		for (int i = 0; i < radioGroup.getChildCount(); i++) {
			View checkView = radioGroup.getChildAt(tab_postion);
			int k = checkView.getMeasuredWidth();
			int l = checkView.getLeft();
			int i2 = l + k / 2 - screenWidth / 2;
			mHorizontalScrollView.smoothScrollTo(i2, 0);
		}
		for (int j = 0; j <  radioGroup.getChildCount(); j++) {
			View checkView = radioGroup.getChildAt(j);
			boolean ischeck;
			if (j == tab_postion) {
				ischeck = true;
			} else {
				ischeck = false;
			}
			checkView.setSelected(ischeck);
		}
	}
	public void setForbidScroll(boolean isForbidScroll){
		this.isScroll = isForbidScroll;
	}
}
