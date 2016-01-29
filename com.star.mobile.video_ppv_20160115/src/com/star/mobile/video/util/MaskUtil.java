package com.star.mobile.video.util;

import android.app.Activity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.star.mobile.video.R;
import com.star.mobile.video.R.layout;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.home.HomeActivity;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.view.MaskGuideView;

public class MaskUtil {

	private static final String TAG = "MaskUtil";
	public static boolean hasMaskShown;

	public static void showSettingMask(final Activity activity){
		final MaskGuideView frameLayout = (MaskGuideView) activity.findViewById(R.id.rl_setting_tellfriend);
		frameLayout.showMask(frameLayout.new MaskDefaultHandler() {
			
			@Override
			public void setOntouchListener(OnTouchListener listener) {
				activity.findViewById(R.id.frame_center).setOnTouchListener(listener);
				activity.findViewById(R.id.tv_settingFrame_dimiss).setOnTouchListener(listener);
			}
			
		}, Constant.GUIDE_TELLFRIEND, Constant.GUIDE_BY_VERSION, R.id.frame_center);
	}
	
	public static void showMeGuideFrame(final Activity activity){
		final MaskGuideView layout = (MaskGuideView) activity.findViewById(R.id.rl_me_headView);
		layout.showMask(layout.new MaskDefaultHandler() {
			
			@Override
			public void setOntouchListener(OnTouchListener listener) {
				activity.findViewById(R.id.frame_head).setOnTouchListener(listener);
			}
			
		}, Constant.GUIDE_ME, Constant.GUIDE_BY_USER, R.id.frame_head);
	}
	
	public static void showMenuGuideFrame(final Activity activity){
		final MaskGuideView layout = (MaskGuideView) activity.findViewById(R.id.rl_menubar_guide);
		layout.showMask(layout.new MaskDefaultHandler() {
			
			@Override
			public void setOntouchListener(OnTouchListener listener) {
				activity.findViewById(R.id.iv_menu_guide).setOnTouchListener(listener);
			}
			
		}, Constant.GUIDE_MENUBAR, Constant.GUIDE_BY_VERSION, R.id.iv_menu_guide);
	}
	
	public static void showChannelGuideFrame(final Activity activity){
		final MaskGuideView layout = (MaskGuideView) activity.findViewById(R.id.rl_chnGuide_guide);
		boolean status = layout.showMask(layout.new MaskHandler() {
			
			@Override
			public void setOntouchListener() {
				activity.findViewById(R.id.frame_list).setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						layout.setOnInterceptTouch(false);
						layout.setVisibility(View.GONE);
						return false;
					}
				});
			}
		}, Constant.GUIDE_EPGALERT, Constant.GUIDE_BY_USER, R.id.frame_list);
		if(!status){
//			StarApplication.mHomeActivity.setNotFromTask();
		}
	}
	
//	public static void showBindSmardcardGuide(final Activity activity) {
//		final MaskGuideView layout = (MaskGuideView) activity.findViewById(R.id.smartcard_guide);
//		layout.showMask(layout.new MaskDefaultHandler() {
//			@Override
//			public void setOntouchListener(OnTouchListener listener) {
//				activity.findViewById(R.id.iv_smartcard_guide).setOnTouchListener(listener);
//			}
//			
//		}, Constant.GUIDE_SMARTCARD, Constant.GUIDE_BY_USER, R.id.iv_smartcard_guide);
//	}
	
	public static boolean hideMask(Activity activity, String maskId, boolean saveStatus){
		if(!MaskUtil.hasMaskShown){
			return false;
		}
		int maskResId = -1;
		String maskType = null;
		if(maskId.equals(Constant.GUIDE_EPGALERT)){
			activity.findViewById(R.id.rl_chnGuide_guide).setVisibility(View.GONE);
			activity.findViewById(R.id.rl_epgDetail_alert).setVisibility(View.GONE);
			maskType = Constant.GUIDE_BY_USER;
		}else if(maskId.equals(Constant.GUIDE_ME)){
			maskResId = R.id.rl_me_headView;
			maskType = Constant.GUIDE_BY_USER;
		}else if(maskId.equals(Constant.GUIDE_ALLCHN)){
			maskResId = R.id.rl_allchn_guide;
			maskType = Constant.GUIDE_BY_VERSION;
		}else if(maskId.equals(Constant.GUIDE_CHATROOM)){
			maskResId = R.id.rl_chatroom_guide;
			maskType = Constant.GUIDE_BY_VERSION;
		}else if(maskId.equals(Constant.GUIDE_FAVORITE_COLLECT)){
			maskResId = R.id.favorite_collect_mask;
			maskType = Constant.GUIDE_BY_VERSION;
		}
		if(maskResId != -1){
			activity.findViewById(maskResId).setVisibility(View.GONE);
		}
		MaskUtil.hasMaskShown = false;
		if(saveStatus){
//			StarApplication.mHomeActivity.setNotFromTask(200);
			Log.d(TAG, "hide "+maskId+" frame, save status!");
			if(maskType.equals(Constant.GUIDE_BY_VERSION)){
				SharedPreferencesUtil.getGuideSharePreferencesByAppVersion(activity).edit().putBoolean(maskId, true).commit();
			}else if(maskType.equals(Constant.GUIDE_BY_USER)){
				SharedPreferencesUtil.getGuideSharePreferencesByUser(activity).edit().putBoolean(maskId, true).commit();
			}
		}else{
			Log.d(TAG, "hide "+maskId+"frame, but not save status!");
		}
		return true;
	}
	
	public static void showEpgAlertFrame(final Activity activity){
		final MaskGuideView layout = (MaskGuideView) activity.findViewById(R.id.rl_epgDetail_alert);
		boolean status = layout.showMask(layout.new MaskHandler() {
			
			@Override
			public void setOntouchListener() {
				View frame = activity.findViewById(R.id.frame_alert_above);
				if(Constant.POSTER_HEIGHT!=0){
					RelativeLayout.LayoutParams params = (LayoutParams) frame.getLayoutParams();
					params.height = Constant.POSTER_HEIGHT;
					frame.setLayoutParams(params);
				}
				activity.findViewById(R.id.frame_alert).setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						layout.setOnInterceptTouch(false);
						hideMask(activity, Constant.GUIDE_EPGALERT, true);
						return false;
					}
				});
			}
		}, Constant.GUIDE_EPGALERT, Constant.GUIDE_BY_USER, R.id.frame_alert);
		if(!status){
//			StarApplication.mHomeActivity.setNotFromTask();
		}
	}
	
	public static void showAllChannelFrame(final Activity activity){
		final MaskGuideView layout = (MaskGuideView) activity.findViewById(R.id.rl_allchn_guide);
		layout.showMask(layout.new MaskHandler() {
			
			@Override
			public void setOntouchListener() {
				final View fav_ = activity.findViewById(R.id.rl_allchn_fav);
				final View pkg_ = activity.findViewById(R.id.rl_allchn_pkg);
				final View cgy_ = activity.findViewById(R.id.rl_allchn_cgy);
				fav_.setVisibility(View.VISIBLE);
				pkg_.setVisibility(View.GONE);
				cgy_.setVisibility(View.GONE);
				OnTouchListener l = new OnTouchListener() {
					
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						switch (v.getId()) {
						case R.id.frame_fav_next:
							fav_.setVisibility(View.GONE);
							pkg_.setVisibility(View.VISIBLE);
							break;
						case R.id.frame_pkg_next:
							pkg_.setVisibility(View.GONE);
							cgy_.setVisibility(View.VISIBLE);
							break;
						case R.id.frame_cgy_ok:
							hideMask(activity, Constant.GUIDE_ALLCHN, true);
							break;
						}
						return false;
					}
				};
				activity.findViewById(R.id.frame_pkg_next).setOnTouchListener(l);
				activity.findViewById(R.id.frame_cgy_ok).setOnTouchListener(l);
				activity.findViewById(R.id.frame_fav_next).setOnTouchListener(l);
			}
		}, Constant.GUIDE_ALLCHN, Constant.GUIDE_BY_VERSION, -1);
	}
	
	public static void showChatroomOnTVGuideFrame(final Activity activity){
		final MaskGuideView layout = (MaskGuideView) activity.findViewById(R.id.rl_chatroom_guide);
		layout.setOnInterceptTouch(true);
		layout.showMask(layout.new MaskDefaultHandler() {
			
			@Override
			public void setOntouchListener(OnTouchListener listener) {
				activity.findViewById(R.id.frame_chatroom_ok).setOnTouchListener(listener);
			}
			
		}, Constant.GUIDE_CHATROOM, Constant.GUIDE_BY_VERSION, -1);
	}
	/**
	 * 显示收藏引导
	 * @param activity
	 */
	public static void showFavoriteCollectMask(final Activity activity){
		final MaskGuideView frameLayout = (MaskGuideView) activity.findViewById(R.id.favorite_collect_mask);
		frameLayout.showMask(frameLayout.new MaskDefaultHandler() {
			@Override
			public void setOntouchListener(OnTouchListener listener) {
				final ImageView smallPointImageView = (ImageView) activity.findViewById(R.id.actionbar_small_point_imageview);
				final ImageView bigPointImageView = (ImageView) activity.findViewById(R.id.actionbar_big_point_imageview);
				final ImageView actionBarMoreMask = (ImageView) activity.findViewById(R.id.mask_finger_imageview);
				bigPointImageView.setOnTouchListener(new OnTouchListener() {
					
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						frameLayout.setOnInterceptTouch(false);
						hideMask(activity, Constant.GUIDE_FAVORITE_COLLECT, true);
						ToastUtil.centerShowToast(activity,activity.getResources().getString(R.string.favorite_collect_mask));
						return false;
					}
				});
				
				//50dp
				int dip2px = DensityUtil.dip2px(activity, 50);
				TranslateAnimation ta = new TranslateAnimation(0, dip2px, 0, -dip2px);
				ta.setDuration(1000);
				ta.setRepeatMode(Animation.RESTART);
				ta.setRepeatCount(Animation.INFINITE);
				actionBarMoreMask.startAnimation(ta);
				
				smallPointImageView.setVisibility(View.VISIBLE);
				Animation an = AnimationUtils.loadAnimation(activity, R.anim.anim_alpha);
				an.setFillAfter(true);
				an.setDuration(1000);
				smallPointImageView.startAnimation(an);
				bigPointImageView.setVisibility(View.VISIBLE);
				Animation all = AnimationUtils.loadAnimation(activity, R.anim.anim_alpha_scale);
				bigPointImageView.startAnimation(all);
			}
			
		}, Constant.GUIDE_FAVORITE_COLLECT, Constant.GUIDE_BY_VERSION, R.id.actionbar_big_point_imageview);
	}
	
}
