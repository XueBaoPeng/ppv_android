package com.star.mobile.video.channel;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.star.cms.model.vo.ChannelVO;
import com.star.mobile.video.R;
import com.star.mobile.video.home.tab.UpDrawerView;
import com.star.mobile.video.util.DensityUtil;
import com.star.mobile.video.util.ToastUtil;
import com.star.mobile.video.view.FaceContainer;
import com.star.util.app.GA;

public class ChannelControlView extends UpDrawerView implements OnClickListener{
	private RadioGroup radioGroup;
	private RadioButton mVideoBtn;
	private RadioButton mEpgBtn;
	private RadioButton mChatBtn;
	int screenWidth;
	private TextView mTextViewLine;
	private TextView mTextViewLineChannel;
	int currenttab=0;
	public static  int TAB_COUNT = 3;
	private ChannelVO mChannelVO;
	private ViewPagerCompat mViewPagerCompat;
	private VideoView videoView;
	private EpgView epgView;
	private ChatView chatView;
	private ChatCustomizeCallback chatCustomizeCallback;
	private EditText mEtChat;
	private ImageView sendChat;
	private ImageView sendFace;
	private FaceContainer faceContainer;
	private Context mContext;
	
	
	public ChannelControlView(Context context) {
		super(context);
		this.mContext = context;
		LayoutInflater.from(context).inflate(R.layout.view_channel_control, this);
		initButton();
	}	
	public ChannelControlView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		LayoutInflater.from(context).inflate(R.layout.view_channel_control, this);
		initButton();
	}
	/**
	 * 初始化按钮
	 */
	private void initButton() {
		radioGroup = (RadioGroup) findViewById(R.id.rg_group);
		mVideoBtn = (RadioButton)findViewById(R.id.id_rb_video);
		mEpgBtn = (RadioButton)findViewById(R.id.id_rb_epg);
		mChatBtn = (RadioButton)findViewById(R.id.id_rb_chat);
		mTextViewLine = (TextView) findViewById(R.id.line);
		mTextViewLineChannel =(TextView) findViewById(R.id.line_channel);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.id_rb_video:
			selectVideo();
			setVideoView();
			break;

		case R.id.id_rb_epg:
			selectEpg();
			setEpgView();
			break;
			
		case R.id.id_rb_chat:
			selectChat();
			setChatView();
			break;
		}
	}
	public void selectVideo(){
		imageMove(0);
		currenttab=0;
//		radioGroup.clearCheck();
		mVideoBtn.setSelected(true);
		mEpgBtn.setSelected(false);
		mChatBtn.setSelected(false);
//		getmViewPagerCompat().setCurrentItem(0);
		videoView.setVisibility(View.VISIBLE);
		epgView.setVisibility(View.GONE);
		chatView.setVisibility(View.GONE);
	}
	public void selectEpg(){
		imageMove(1);
		currenttab=1;
//		radioGroup.clearCheck();
		mVideoBtn.setSelected(false);
		mEpgBtn.setSelected(true);
//		getmViewPagerCompat().setCurrentItem(1);
		mChatBtn.setSelected(false);
		videoView.setVisibility(View.GONE);
		epgView.setVisibility(View.VISIBLE);
		chatView.setVisibility(View.GONE);
		
	}
	public void selectChat(){
		if(mChannelVO.getType() != ChannelVO.LIVE_TYPE){
			imageMove(1);
			currenttab=1;
		}else{
			imageMove(2);
			currenttab=2;	
		}
//		radioGroup.clearCheck();
		mVideoBtn.setSelected(false);
		mEpgBtn.setSelected(false);
		mChatBtn.setSelected(true);
//		getmViewPagerCompat().setCurrentItem(2);
		videoView.setVisibility(View.GONE);
		epgView.setVisibility(View.GONE);
		chatView.setVisibility(View.VISIBLE);
		//GA toal
		ToastUtil.showToast(mContext, mChannelVO.getName());
		GA.sendEvent("Chat", "Chat", mChannelVO.getName(), 1);
	}
	public void setVideoView(){
		videoView.setChannel(mChannelVO);
		chatView.onStop();
		chatView.hideSoftKeyboard(getContext());
		chatCustomizeCallback.onChatBottom(false);
	}
	public void setEpgView(){
		epgView.setChannel(mChannelVO);
		chatView.onStop();
		chatView.hideSoftKeyboard(getContext());
		chatCustomizeCallback.onChatBottom(false);
	}
	public void setChatView(){
		chatView.setChannel(mChannelVO);
		chatCustomizeCallback.onChatBottom(true);
		chatView.setChatBottom(mEtChat, sendChat, sendFace, faceContainer);
		chatView.hideSoftKeyboard(getContext());
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
//	public void setView(int index){
//		if(index == 0){
//			imageMove(0);
//			currenttab=0;
//			mVideoBtn.setSelected(true);
//			mEpgBtn.setSelected(false);
//			mChatBtn.setSelected(false);
////			getmViewPagerCompat().setCurrentItem(0);	
////			videoView.setChannel(mChannelVO);
//			epgView.setVisibility(View.GONE);
//			chatView.setVisibility(View.GONE);
//		}else if(index == 1){
//			imageMove(1);
//			currenttab=1;
//			mVideoBtn.setSelected(false);
//			mEpgBtn.setSelected(true);
//			mChatBtn.setSelected(false);
//			
//			epgView.setVisibility(View.GONE);
//			chatView.setVisibility(View.GONE);
////			getmViewPagerCompat().setCurrentItem(1);
//		}else{
//			if(mChannelVO.getType() != ChannelVO.LIVE_TYPE){
//				imageMove(1);
//				currenttab=1;
//			}else{
//				imageMove(2);
//				currenttab=2;	
//			}
//			mVideoBtn.setSelected(false);
//			mEpgBtn.setSelected(false);
//			mChatBtn.setSelected(true);
////			getmViewPagerCompat().setCurrentItem(2);
//	
//		}
//	}

	public ViewPagerCompat getmViewPagerCompat() {
		return mViewPagerCompat;
	}

	public void setmViewPagerCompat(ViewPagerCompat mViewPagerCompat) {
		this.mViewPagerCompat = mViewPagerCompat;
	}

	public void setmChannelVO(ChannelVO mChannelVO) {
		this.mChannelVO = mChannelVO;
		chatCustomizeCallback.onChatBottom(false);
		
		if(mChannelVO.getType() != ChannelVO.LIVE_TYPE){
			TAB_COUNT = 2;
		}else{
			TAB_COUNT = 3;
		}
		screenWidth=getResources().getDisplayMetrics().widthPixels;
		LinearLayout.LayoutParams imageParams=new LinearLayout.LayoutParams(screenWidth/TAB_COUNT, DensityUtil.dip2px(getContext(), 4));
		imageParams.gravity = Gravity.BOTTOM;
		mTextViewLine.setLayoutParams(imageParams);
		if(mChannelVO.getType() != ChannelVO.LIVE_TYPE){
			mEpgBtn.setVisibility(View.GONE);
		}else{
			mEpgBtn.setVisibility(View.VISIBLE);
		}
	}
	public void setUI(VideoView videoView,EpgView epgView,ChatView chatView) {
		this.videoView = videoView;
		this.chatView = chatView;
		this.epgView = epgView;
		mVideoBtn.setOnClickListener(this);
		mEpgBtn.setOnClickListener(this);
		mChatBtn.setOnClickListener(this);

	}
	public ChatCustomizeCallback getChatCustomizeCallback() {
		return chatCustomizeCallback;
	}
	public void setChatCustomizeCallback(ChatCustomizeCallback chatCustomizeCallback) {
		this.chatCustomizeCallback = chatCustomizeCallback;
	}
	public void setChatBottom(EditText mEtChat,ImageView sendChat,ImageView sendFace,FaceContainer faceContainer){
		this.mEtChat = mEtChat;
		this.sendChat = sendChat;
		this.sendFace = sendFace;
		this.faceContainer = faceContainer;
	}
	public int SelectBtn(){
		if(mVideoBtn.isChecked()){
			return 1;
		}else if(mEpgBtn.isChecked()){
			return 2;
		}else if(mChatBtn.isChecked()){
			return 3;
		}
		return 0;
	}
}
