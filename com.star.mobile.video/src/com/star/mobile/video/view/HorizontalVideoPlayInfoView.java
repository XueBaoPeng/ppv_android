package com.star.mobile.video.view;

import com.star.cms.model.Content;
import com.star.cms.model.vo.ChannelVO;
import com.star.cms.model.vo.VOD;
import com.star.mobile.video.R;
import com.star.mobile.video.account.ChooseAreaActivity;
import com.star.mobile.video.model.FunctionType;
import com.star.mobile.video.service.FunctionService;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.smartcard.SmartCardControlActivity;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.CommonUtil.PromptDialogClickListener;
import com.star.mobile.video.util.ToastUtil;
import com.star.util.app.GA;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 横向视频播放的信息
 * 
 * @author Lee
 * @version 1.0 2015.07.29
 *
 */
public class HorizontalVideoPlayInfoView extends LinearLayout implements OnClickListener, PromptDialogClickListener {
	private LayoutInflater mLayoutInflater;
	private TextView mVideoWatchCount;
	private RelativeLayout mVerticalVideoRechargeLL;
	private RelativeLayout mVerticalVideoBouquetLL;
	private Context mContext;
	private VOD mVod;
	private ChannelVO mChannel;

	public HorizontalVideoPlayInfoView(Context context) {
		super(context);
		initView(context);
		initData();
	}

	public HorizontalVideoPlayInfoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		initData();
	}

	public void setVod(VOD vod) {
		this.mVod = vod;
		if (mVod == null)
			return;
		setVideoWatchCount();
	}
	
	public void setChannel(ChannelVO cv) {
		this.mChannel = cv;
	}

	private void initView(Context context) {
		this.mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
		View view = mLayoutInflater.inflate(R.layout.view_horizontal_video_info, this);
		mVideoWatchCount = (TextView) view.findViewById(R.id.video_views_count_textview);
		mVerticalVideoRechargeLL = (RelativeLayout) view.findViewById(R.id.vertical_video_recharge_ll);
		mVerticalVideoBouquetLL = (RelativeLayout) view.findViewById(R.id.vertical_video_bouquet_ll);
		if (FunctionService.doHideFuncation(FunctionType.SimpleVersion)) {
			mVerticalVideoRechargeLL.setVisibility(View.INVISIBLE);
			mVerticalVideoBouquetLL.setVisibility(View.INVISIBLE);
		}
	}

	private void initData() {
		mVerticalVideoRechargeLL.setOnClickListener(this);
		mVerticalVideoBouquetLL.setOnClickListener(this);
		CommonUtil.getInstance().setDialogClickListner(this);
	}

	/**
	 * 设置观看次数
	 */
	private void setVideoWatchCount() {
		long videoWatchCount = getVideoWatchCount();
		mVideoWatchCount.setText(String.valueOf(videoWatchCount+1));
	}

	/**
	 * 获得视频的观看次数
	 * 
	 * @param
	 * @return
	 */
	private long getVideoWatchCount() {
		Content videoContent = mVod.getVideo();
		if (videoContent != null && videoContent.getSelCount() != null) {
			return videoContent.getSelCount();
		}
		return 0;
	}

	/**
	 * 设置推荐的观看次数
	 * 
	 * @param
	 */
	public void setWatchCount(long watchCount) {
		mVideoWatchCount.setText(String.valueOf(watchCount));
	}

	@Override
	public void onClick(View v) {
		String channelName = mChannel != null?mChannel.getName():"";
		switch (v.getId()) {
		case R.id.vertical_video_recharge_ll:
			bouquetAndRecharge();
			
			ToastUtil.showToast(mContext, channelName+"_"+mVod.getName());
			GA.sendEvent("Self_service", "Recharge_video", channelName+"_"+mVod.getName(), 1);
			break;
		case R.id.vertical_video_bouquet_ll:
			ToastUtil.showToast(mContext, channelName+"_"+mVod.getName());
			GA.sendEvent("Self_service", "Bouquet_video", channelName+"_"+mVod.getName(), 1);
			bouquetAndRecharge();
			break;
		default:
			break;
		}
	}
	
	private void bouquetAndRecharge() {
		String userName = SharedPreferencesUtil.getUserName(mContext);
		if (userName == null || "".equals(userName)) {
			// CommonUtil.pleaseLogin(true, getContext());
			CommonUtil.getInstance().showPromptDialog(mContext, null, mContext.getString(R.string.alert_login),
					mContext.getString(R.string.login_btn), mContext.getString(R.string.later), new PromptDialogClickListener() {
						
						@Override
						public void onConfirmClick() {
							Intent intent = new Intent (mContext, ChooseAreaActivity.class);
							CommonUtil.startActivity((Activity)mContext, intent);
						}
						
						@Override
						public void onCancelClick() {
							// TODO Auto-generated method stub
							
						}
					});
		} else {
			// 跳转到充值界面
			Intent chargeIntent = new Intent(mContext, SmartCardControlActivity.class);
			mContext.startActivity(chargeIntent);
		}
	}

	@Override
	public void onConfirmClick() {
		Intent intent = new Intent(mContext, ChooseAreaActivity.class);
		CommonUtil.startActivity((Activity) mContext, intent);
	}

	@Override
	public void onCancelClick() {
	}
}
