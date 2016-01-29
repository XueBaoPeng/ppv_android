package org.libsdl.app;

import com.star.mobile.video.R;

import android.app.Activity;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.ImageView;

public class PlayerGesture {
	private AudioManager mAudioManager;

	private int mMaxVolume;
	private int mVolume = -1;
	private float mBrightness = -1f;
	
	private String TAG = "PlayerGesture";
	private Player mPlayer;
	private PlayerGestureListener mListener;
	
	public PlayerGestureListener getGestureListener(){
		if(mListener == null) mListener = new PlayerGestureListener();
		return mListener;
	}
	
	public PlayerGesture(Player p){
		mPlayer = p;
		mAudioManager = (AudioManager) mPlayer.getSystemService(Player.AUDIO_SERVICE);
		mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	}
	
	void endGesture() {
		mVolume = -1;
		mBrightness = -1f;

		mDismissHandler.removeMessages(0);
		mDismissHandler.sendEmptyMessageDelayed(0, 500);
	}
	
	private Handler mDismissHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mPlayer.mVolumeBrightnessLayout.setVisibility(View.GONE);
		}
	};

	private void onVolumeSlide(float percent) {
		if (mPlayer.isPortraitOrientation)
			return;

		if (mVolume == -1) {
			mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			if (mVolume < 0)
				mVolume = 0;

			mPlayer.mOperationBg.setImageResource(R.drawable.video_volumn_bg);
			mPlayer.mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
		}

		int index = (int) (percent * mMaxVolume) + mVolume;
		if (index > mMaxVolume)
			index = mMaxVolume;
		else if (index < 0)
			index = 0;

		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);

		ViewGroup.LayoutParams lp = mPlayer.mOperationPercent.getLayoutParams();
		lp.width = mPlayer.findViewById(R.id.operation_full).getLayoutParams().width * index / mMaxVolume;
		mPlayer.mOperationPercent.setLayoutParams(lp);
	}

	private void onBrightnessSlide(float percent) {
		if (mPlayer.isPortraitOrientation)
			return;

		if (mBrightness < 0) {
			mBrightness = mPlayer.getWindow().getAttributes().screenBrightness;
			if (mBrightness <= 0.00f)
				mBrightness = 0.50f;
			if (mBrightness < 0.01f)
				mBrightness = 0.01f;

			mPlayer.mOperationBg.setImageResource(R.drawable.video_brightness_bg);
			mPlayer.mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
		}
		WindowManager.LayoutParams lpa = mPlayer.getWindow().getAttributes();
		lpa.screenBrightness = mBrightness + percent;
		if (lpa.screenBrightness > 1.0f)
			lpa.screenBrightness = 1.0f;
		else if (lpa.screenBrightness < 0.01f)
			lpa.screenBrightness = 0.01f;
		mPlayer.getWindow().setAttributes(lpa);

		ViewGroup.LayoutParams lp = mPlayer.mOperationPercent.getLayoutParams();
		lp.width = (int) (mPlayer.findViewById(R.id.operation_full).getLayoutParams().width * lpa.screenBrightness);
		mPlayer.mOperationPercent.setLayoutParams(lp);
	}
	
	class PlayerGestureListener  extends SimpleOnGestureListener {

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			Log.w(TAG, "wl onSingleTapUp mHideContainer.getVisibility() = " + mPlayer.mHideContainer.getVisibility());

			if ((mPlayer.mHideContainer.getVisibility() == View.GONE) || (mPlayer.mHideContainer.getVisibility() == View.INVISIBLE)) {
				mPlayer.mHideContainer.setVisibility(View.VISIBLE);

			} else {
				mPlayer.mHideContainer.setVisibility(View.INVISIBLE);
			}

			return false;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			float mOldX = e1.getX(), mOldY = e1.getY();
			int y = (int) e2.getRawY();
			Display disp = mPlayer.getWindowManager().getDefaultDisplay();
			int windowWidth = disp.getWidth();
			int windowHeight = disp.getHeight();

			if (mOldX > windowWidth * 4.0 / 5)
				onVolumeSlide((mOldY - y) / windowHeight);
			else if (mOldX < windowWidth / 5.0)
				onBrightnessSlide((mOldY - y) / windowHeight);

			return super.onScroll(e1, e2, distanceX, distanceY);
		}
	}
}



