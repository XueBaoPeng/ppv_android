package com.star.mobile.video.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.MaskUtil;

/**
 * 
 * @author dujr
 *
 */
public class MaskGuideView extends RelativeLayout {

	private boolean intercept = true;

	public MaskGuideView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mGuideByVersion = SharedPreferencesUtil.getGuideSharePreferencesByAppVersion(getContext());
		mGuideByUser = SharedPreferencesUtil.getGuideSharePreferencesByUser(getContext());
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return intercept;
	}

	public void setOnInterceptTouch(boolean intercept) {
		this.intercept = intercept;
	}
	
	public boolean showMask(MaskHandler handler, final String maskId, final String maskType, int focusViewId){
		return handler.show(maskId, maskType, focusViewId);
	}
	
	private SharedPreferences mGuideByVersion;
	private SharedPreferences mGuideByUser;
	public OnTouchListener touchListener;
	
	public abstract class MaskHandler{
		public abstract void setOntouchListener();
		protected boolean shown;
		
		/**
		 * 
		 * @param maskId
		 * @param maskType
		 * @param focusViewId
		 * @return show 当前蒙版状态
		 */
		public boolean show(final String maskId, final String maskType, int focusViewId){
			if(maskType.equals(Constant.GUIDE_BY_VERSION)){
				shown = mGuideByVersion.getBoolean(maskId, false);
			}else if(maskType.equals(Constant.GUIDE_BY_USER)){
				shown = mGuideByUser.getBoolean(maskId, false);
			}
			if(!shown){
				setOnInterceptTouch(true);
				setVisibility(View.VISIBLE);
				MaskUtil.hasMaskShown = true;
				setOntouchListener();
			}
			return !shown;
		}
	}
	
	public abstract class MaskDefaultHandler extends MaskHandler{
		@Override
		public final void setOntouchListener() {}
		public abstract void setOntouchListener(OnTouchListener listener);
		@Override
		public boolean show(String maskId, String maskType, int focusViewId) {
			newOntouchListener(maskId, maskType, focusViewId);
			setOntouchListener(touchListener);
			return super.show(maskId, maskType, focusViewId);
		}
		
		private void newOntouchListener(final String maskId, final String maskType, final int focusViewId) {
			touchListener = new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if(focusViewId == v.getId())
						setOnInterceptTouch(false);
					setVisibility(View.GONE);
					MaskUtil.hasMaskShown = false;
					if(maskType.equals(Constant.GUIDE_BY_VERSION)){
						mGuideByVersion.edit().putBoolean(maskId, true).commit();
					}else if(maskType.equals(Constant.GUIDE_BY_USER)){
						mGuideByUser.edit().putBoolean(maskId, true).commit();
					}
					return false;
				}
			};
		}
	}
}
