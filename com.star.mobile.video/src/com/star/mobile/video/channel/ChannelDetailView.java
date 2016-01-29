package com.star.mobile.video.channel;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.star.cms.model.vo.ChannelVO;
import com.star.mobile.video.R;
import com.star.mobile.video.home.tab.DownDrawerView;
import com.star.mobile.video.home.tab.DrawerScrollListener;

public class ChannelDetailView extends RelativeLayout{
//	private RadioButton mVideoBtn;
//	private RadioButton mEpgBtn;
//	private RadioButton mChatBtn;
	
//	private TextView mTextViewLine;
	
	
	private ViewPagerCompat mViewPager;
	private ListFragmentPagerAdapter mPagerAdapter;
	private List<Fragment> mFragments = new ArrayList<Fragment>();
	
	private final int FIRST_FRAGMENT = 0;
	private final int SECOND_FRAGMENT = 1;
	private final int THIRD_FRAGMENT = 2;
	int screenWidth;
	int currenttab=-1;
	private ChannelVO channel;
	private FragmentManager mFragmentManager;
	private VideoView videoView;
	private EpgView epgView;
	private ChatView chatView;
	private ChannelControlView channelControlView;
	public ChannelDetailView(Context context) {
		super(context);
//		mContext = context;
//		this.mFragmentManager=mFragmentManager;
		initView(context);
//		initViewPager();
//		layout.addView(channelControView);
	}	
	
	public ChannelDetailView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}
	
	public ChannelDetailView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}
	/**
	 * 初始化按钮
	 */
	private void initView(Context context) {
		LayoutInflater.from(context).inflate(R.layout.view_channel_detail_home, this);
//		mVideoBtn = (RadioButton)findViewById(R.id.id_rb_video);
//		mVideoBtn.setOnClickListener(this);
//		mEpgBtn = (RadioButton)findViewById(R.id.id_rb_epg);
//		mEpgBtn.setOnClickListener(this);
//		mChatBtn = (RadioButton)findViewById(R.id.id_rb_chat);
//		mChatBtn.setOnClickListener(this);
//		mTextViewLine = (TextView) findViewById(R.id.line);
		videoView = (VideoView) findViewById(R.id.video_control_view);
		epgView = (EpgView) findViewById(R.id.epg_control_view);
		chatView = (ChatView) findViewById(R.id.chat_control_view);
	}
	
	public void setChannelDetailDrawer(com.star.ui.DragTopLayout dragLayout){
		videoView.setDragLayout(dragLayout);
		epgView.setDragLayout(dragLayout);
		chatView.setDragLayout(dragLayout);
	}
	
	public void setChannelControlView(ChannelControlView controlView) {
		channelControlView = controlView;
		channelControlView.setUI(videoView, epgView, chatView);
		videoView.setControlTab(channelControlView);
		epgView.setControlTab(channelControlView);
		chatView.setControlTab(channelControlView);
	}
	
	public void setBottomDrawer(DownDrawerView downDrawer){
		videoView.setHomeBottomTab(downDrawer);
		epgView.setHomeBottomTab(downDrawer);
		chatView.setHomeBottomTab(downDrawer);
	}
	
//	public void setChannelDetailDrawer(UpDrawerView chnDrawer){
//		videoFragment.setUpTabDrawer(chnDrawer);
//		epgFragment.setUpTabDrawer(chnDrawer);
//		chatFragment.setUpTabDrawer(chnDrawer);
//	}
//	
//	public void setBottomDrawer(DownDrawerView downDrawer){
//		
//		videoFragment.setHomeBottomTab(downDrawer);
//		epgFragment.getHomeBottomTab();
//		chatFragment.setHomeBottomTab(downDrawer);
//	}
	
//	/**
//	 * 初始化ViewPager控件
//	 */
//	private void initViewPager() {
//		mViewPager = (ViewPagerCompat)findViewById(R.id.id_vp_viewpager);
//		//关闭预加载，默认一次只加载一个Fragment
//		mViewPager.setOffscreenPageLimit(1);
//		videoFragment = new VideoView();
//		epgFragment = new EpgView();
//		chatFragment = new ChatView();
////		videoFragment = VideoFragment.newInstance(FIRST_FRAGMENT);
////		epgFragment = EpgFragment.newInstance(SECOND_FRAGMENT);
////		chatFragment = ChatFragment.newInstance(THIRD_FRAGMENT);
//		
//	}
//	private VideoView videoFragment;
//	private EpgView epgFragment;
//	private ChatView chatFragment;
	
	
//	@Override
//	public void onClick(View v) {
////		switch (v.getId()) {
////		case R.id.id_rb_video:
////			mViewPager.setCurrentItem(FIRST_FRAGMENT);
////			break;
////
////		case R.id.id_rb_epg:
////			mViewPager.setCurrentItem(SECOND_FRAGMENT);
////			break;
////			
////		case R.id.id_rb_chat:
////			mViewPager.setCurrentItem(THIRD_FRAGMENT);
////			break;
////		}
//	}
	
	public void setChannel(ChannelVO mChannel){
		this.channel=mChannel;
		channelControlView.setmChannelVO(mChannel);
		if(channelControlView.SelectBtn() == 1){
			channelControlView.selectVideo();
		}else if(channelControlView.SelectBtn() == 2){
			if(mChannel.getType() != ChannelVO.LIVE_TYPE){
				channelControlView.selectVideo();
			}else{
				channelControlView.selectEpg();
			}
		}else if(channelControlView.SelectBtn() == 3){
			channelControlView.selectChat();
		}
		videoView.clearVideo();
		epgView.clearEpg();
		chatView.clearChat();
//		channelControView.setmViewPagerCompat(mViewPager);
		
		//添加Fragment
//		mFragments.add(videoFragment);
//		if(channel.getType() != 2){
//			epgView.setChannel(mChannel);
////			mFragments.add(epgFragment);
//		}
//		chatView.setChannel(mChannel);
//		mFragments.add(chatFragment);
		//适配器
//		mPagerAdapter = new ListFragmentPagerAdapter(mFragmentManager, mFragments);
//		mViewPager.setAdapter(mPagerAdapter);
		
		
	}
	public void loadVideo(ChannelVO mChannel){
		if(channelControlView.SelectBtn() == 1){
			channelControlView.setVideoView();
		}else if(channelControlView.SelectBtn() == 2){
			if(mChannel.getType() != ChannelVO.LIVE_TYPE){
				channelControlView.setVideoView();
			}else{
				channelControlView.setEpgView();
			}
		}else if(channelControlView.SelectBtn() == 3){
			channelControlView.setChatView();
		}
	}
	public void updateUiChat(){
		if(channelControlView.SelectBtn() == 3){
			channelControlView.updateUiChat();
		}
	}
	public void onStop(){
		chatView.onStop();
		chatView.hideSoftKeyboard(getContext());
	}

	public void setScrollListener(DrawerScrollListener drawerListener) {
		videoView.setDrawerListener(drawerListener);
		epgView.setDrawerListener(drawerListener);
		chatView.setDrawerListener(drawerListener);
	}
	public int select(){
		return channelControlView.SelectBtn();
	}
}
