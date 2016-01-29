package com.star.mobile.video.view;

import org.libsdl.app.Player;

import com.star.mobile.video.R;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 视频质量控制页面
 * 
 * @author Lee
 * @version 1.0 2015/08/31
 *
 */
public class VideoQualitySettingView extends RelativeLayout implements OnClickListener {
	private LayoutInflater mLayoutInflater;
	private Context mContext;
	private LinearLayout mVideoQualtiyLL;
	private RelativeLayout mVideoQualityNormalRL;
	private ImageView mVideoQualityNormalIV;
	private RelativeLayout mVideoQualityLowRL;
	private ImageView mVideoQuailtyLowIV;
	private TextView mVideoQualityApplyTV;
	private Quality QUALITY = Quality.LOW;
	private Handler mHandler;
	private Player mPlayer;
	public VideoQualitySettingView(Context context) {
		super(context);
		initView(context);
		initData(context);
	}

	public VideoQualitySettingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		initData(context);
	}

	public VideoQualitySettingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
		initData(context);
	}

	private void initView(Context context) {
		this.mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
		View view = mLayoutInflater.inflate(R.layout.video_quality_setting_view, this);
		mVideoQualtiyLL = (LinearLayout) view.findViewById(R.id.video_quality_ll);
		mVideoQualityNormalRL = (RelativeLayout) view.findViewById(R.id.video_quality_normal_rl);
		mVideoQualityNormalIV = (ImageView) view.findViewById(R.id.video_quality_normal_iv);
		mVideoQualityLowRL = (RelativeLayout) view.findViewById(R.id.video_quality_low_rl);
		mVideoQuailtyLowIV = (ImageView) view.findViewById(R.id.video_quality_low_iv);
		mVideoQualityApplyTV = (TextView) view.findViewById(R.id.video_quality_apply_tv);
	}

	private void initData(Context context) {
		mVideoQualityNormalRL.setOnClickListener(this);
		mVideoQualityLowRL.setOnClickListener(this);
		mVideoQualityApplyTV.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.video_quality_normal_rl:
			hideImageView();
			mVideoQualityNormalIV.setVisibility(View.VISIBLE);
			QUALITY = Quality.NORMAL;
			break;
		case R.id.video_quality_low_rl:
			hideImageView();
			mVideoQuailtyLowIV.setVisibility(View.VISIBLE);
			QUALITY = Quality.LOW;
			break;
		case R.id.video_quality_apply_tv:
			Message msg = mHandler.obtainMessage();
			msg.what = Player.MSG_VIDEO_QUALITY;
			msg.obj = QUALITY;
			mHandler.sendMessage(msg);
			//隐藏设置页面
			mPlayer.mVideoQualitySettingView.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.tran_up_down));
			mPlayer.mVideoQualitySettingView.setVisibility(View.GONE);
			mPlayer.mPortraitVideoQualitySettingView.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.tran_up_down));
			mPlayer.mPortraitVideoQualitySettingView.setVisibility(View.GONE);
			break;

		default:
			break;
		}
	}
	/**
	 * 隐藏imageview
	 */
	private void hideImageView(){
		mVideoQualityNormalIV.setVisibility(View.GONE);
		mVideoQuailtyLowIV.setVisibility(View.GONE);
	}
	
	
	public enum Quality{
		NORMAL,LOW;
	}
	
	public void setHandler(Handler handler){
		this.mHandler = handler;
	}
	
	public void setPlayer(Player player){
		this.mPlayer = player;
	}
}
