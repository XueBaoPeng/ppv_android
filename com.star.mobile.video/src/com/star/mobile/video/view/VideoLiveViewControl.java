package com.star.mobile.video.view;

import org.libsdl.app.Player;

import com.star.cms.model.Content;
import com.star.cms.model.vo.VOD;
import com.star.mobile.video.R;
import com.star.mobile.video.account.ChooseAreaActivity;
import com.star.mobile.video.model.FunctionType;
import com.star.mobile.video.service.FunctionService;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.smartcard.SmartCardControlActivity;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.CommonUtil.PromptDialogClickListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 直播页面的视图控制
 * 
 * @author Lee @version1.0 2015/08/24
 *
 */
public class VideoLiveViewControl extends LinearLayout implements OnClickListener {
	private LayoutInflater mLayoutInflater;
	private Context mContext;
	private TextView mVideoLiveWatchCount;
	private RelativeLayout mVideoLiveRechargeRL;
	private RelativeLayout mVideoLiveBouquetRL;
	private LinearLayout mVideoLiveChatControlLL;
	private TextView mVideoLiveChatTV;
	private TextView mVideoLiveMoreVideosTV;
	private com.star.mobile.video.view.VideoLiveViewControlChat mVideoLiveViewControlChat;
	private com.star.mobile.video.view.VideoLiveViewControlMoreVideos mVideoLiveViewControlMoreVideos;
	private long mCurrentChannelId;
	private int mCurrentChannelType;
	private VOD mVod;
	private int mPosition;
	private Player mPlayer;
	
	private boolean isBackgroundChange;
	private EditText mEtChat;
	public VideoLiveViewControl(Context context) {
		super(context);
		initView(context);
		initData();
	}

	public VideoLiveViewControl(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		initData();
	}

	private void initView(Context context) {
		this.mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
		View view = mLayoutInflater.inflate(R.layout.view_video_live_control, this);
		mVideoLiveWatchCount = (TextView) view.findViewById(R.id.video_live_watch_count);
		mVideoLiveRechargeRL = (RelativeLayout) view.findViewById(R.id.video_live_recharge_rl);
		mVideoLiveBouquetRL = (RelativeLayout) view.findViewById(R.id.video_live_bouquet_rl);
		mVideoLiveMoreVideosTV = (TextView) view.findViewById(R.id.video_live_more_videos_tv);
		mVideoLiveChatTV = (TextView) view.findViewById(R.id.video_live_chat_tv);
		
		mEtChat = (EditText) view.findViewById(R.id.video_live_chat_edittext);
		ImageView sendChat = (ImageView) view.findViewById(R.id.video_live_content_send_iv);
		ImageView sendFace = (ImageView) view.findViewById(R.id.video_live_chat_face_iv);
		FaceContainer faceContainer = (FaceContainer) view.findViewById(R.id.faceContainer);
		faceContainer.setEditText(mEtChat);
		
		mVideoLiveViewControlChat = (VideoLiveViewControlChat) findViewById(R.id.video_live_chat_control_view);
		mVideoLiveViewControlChat.addView(mEtChat, sendChat, sendFace, faceContainer);
		mVideoLiveViewControlMoreVideos = (VideoLiveViewControlMoreVideos) findViewById(R.id.video_live_more_videos_control_view);
		mVideoLiveChatControlLL = (LinearLayout) findViewById(R.id.ll_three);
		if(FunctionService.doHideFuncation(FunctionType.SimpleVersion)){
			mVideoLiveRechargeRL.setVisibility(View.INVISIBLE);
			mVideoLiveBouquetRL.setVisibility(View.INVISIBLE);
		}
	}

	private void initData() {
		mVideoLiveRechargeRL.setOnClickListener(this);
		mVideoLiveBouquetRL.setOnClickListener(this);
		mVideoLiveChatTV.setOnClickListener(this);
		mVideoLiveMoreVideosTV.setOnClickListener(this);
		setMoreVideosData();
	}

	public void setPlayer(Player p){
		this.mPlayer = p;
		mVideoLiveViewControlMoreVideos.setPlayer(mPlayer);
	}
	
	public void setChannelID(long channelID) {
		this.mCurrentChannelId = channelID;
		mVideoLiveViewControlChat.setCurrentChatRoom(channelID);
		mVideoLiveViewControlMoreVideos.setChannelID(mCurrentChannelId);
	}

	public void setChannelType(int channelType) {
		this.mCurrentChannelType = channelType;
		mVideoLiveViewControlMoreVideos.setChannelType(mCurrentChannelType);
	}

	public void setVod(VOD vod) {
		this.mVod = vod;
		setVideoLiveWatchCount(mVod);
		mVideoLiveViewControlMoreVideos.setVod(mVod);
	}
	public void setPosition(int position){
		this.mPosition = position;
		mVideoLiveViewControlMoreVideos.setPosition(mPosition);
	}

	/**
	 * 设置more videos所需要的数据
	 */
	private void setMoreVideosData(){
		mVideoLiveViewControlMoreVideos.setVideoLiveViewControl(this);
	}
	/**
	 * 设置视频的观看次数
	 * 
	 * @param vod
	 */
	private void setVideoLiveWatchCount(VOD vod) {
		if (vod != null) {
			Content video = vod.getVideo();
			if (video != null) {
				Long watchCount = video.getSelCount();
				if (watchCount != 0) {
					setWatchCount(watchCount + 1);
				}
			}
		}
	}
	/**
	 * 设置推荐的观看次数
	 * 
	 * @param position
	 */
	public void setWatchCount(long watchCount) {
		mVideoLiveWatchCount.setText(String.valueOf(watchCount));
	}

	@Override
	public void onClick(View v) {
		String userName = SharedPreferencesUtil.getUserName(mContext);
		switch (v.getId()) {
		case R.id.video_live_recharge_rl:
		case R.id.video_live_bouquet_rl:
			if (userName == null || "".equals(userName)) {
//				CommonUtil.pleaseLogin(true, getContext());
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
			break;
		case R.id.video_live_chat_tv:
			// more videos隐藏，加载聊天的数据
			showChatView();
			break;
		case R.id.video_live_more_videos_tv:
			// chat隐藏，加载more videos的数据
			showMorevideos();
			break;
		default:
			break;
		}
	}

	/**
	 * 隐藏more videos显示chat视图
	 */
	private void showChatView() {
		mVideoLiveChatTV.setTextColor(mContext.getResources().getColor(R.color.white));
		if (isBackgroundChange) {
			isBackgroundChange = false;
			mVideoLiveChatTV.setBackgroundResource(R.drawable.btn_chattab_n);
			mVideoLiveMoreVideosTV.setTextColor(mContext.getResources().getColor(R.color.orange_color));
			mVideoLiveMoreVideosTV.setBackgroundResource(0);
			mVideoLiveViewControlMoreVideos.setVisibility(View.GONE);
			mVideoLiveViewControlChat.setVisibility(View.VISIBLE);
			mVideoLiveChatControlLL.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 隐藏chat视图，显示more videos
	 */
	private void showMorevideos() {
		mVideoLiveMoreVideosTV.setTextColor(mContext.getResources().getColor(R.color.white));
		if (!isBackgroundChange) {
			isBackgroundChange = true;
			mVideoLiveMoreVideosTV.setBackgroundResource(R.drawable.btn_chattab_n);
			mVideoLiveChatTV.setTextColor(mContext.getResources().getColor(R.color.orange_color));
			mVideoLiveChatTV.setBackgroundResource(0);
			mVideoLiveViewControlChat.setVisibility(View.GONE);
			mVideoLiveChatControlLL.setVisibility(View.GONE);
			mVideoLiveViewControlMoreVideos.setVisibility(View.VISIBLE);
			hideSoftKeyboard(mEtChat,mContext);
		}
	}
	
	public boolean hidefaceLayout(){
		return mVideoLiveViewControlChat.hidefaceLayout();
	}
	
	public void removeTimerAndTask(){
		mVideoLiveViewControlChat.onStop();
	}

	/**
	 * 刷新more videos的数据
	 */
	public void refreshVideoData() {
		mVideoLiveViewControlMoreVideos.refreshVideoData();
	}
	
	/**
	 * 隐藏软键盘
	 * 
	 * @param view
	 *            需要指出要隐藏那个控件的键盘
	 */
	private void hideSoftKeyboard(View view, Context context) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0); // 强制隐藏键盘
	}

}
