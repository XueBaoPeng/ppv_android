package com.star.mobile.video.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.star.cms.model.vo.ChannelVO;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.activity.WelcomeActivity;
import com.star.mobile.video.base.FragmentActivity;
import com.star.mobile.video.discovery.DiscoveryFragment;
import com.star.mobile.video.guide.GuideCustomizeCallback;
import com.star.mobile.video.guide.GuideHomeDown;
import com.star.mobile.video.guide.GuideHomeLeft;
import com.star.mobile.video.home.tab.DownDrawerView;
import com.star.mobile.video.home.tab.DrawerScrollListener;
import com.star.mobile.video.liveandvideo.PlayFragment;
import com.star.mobile.video.me.MeFragment;
import com.star.mobile.video.model.FunctionType;
import com.star.mobile.video.search.SearchActivity;
import com.star.mobile.video.service.ChannelService;
import com.star.mobile.video.service.FeedbackService;
import com.star.mobile.video.service.FunctionService;
import com.star.mobile.video.service.InitService;
import com.star.mobile.video.service.ProgramService;
import com.star.mobile.video.service.SyncService;
import com.star.mobile.video.service.SyncStatusService;
import com.star.mobile.video.service.UserService;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.smartcard.SmartCardService;
import com.star.mobile.video.smartcard.SmartCardSharedPre;
import com.star.mobile.video.tenb.TenbService;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.LoadingDataTask;
import com.star.mobile.video.util.MaskUtil;
import com.star.mobile.video.util.ToastUtil;
import com.star.mobile.video.util.config.AppConfig;
import com.star.mobile.video.view.MenuPage;
import com.star.util.app.GA;
import com.star.util.loader.OnResultListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.sharesdk.onekeyshare.OnekeyShare;

public class HomeActivity extends FragmentActivity implements OnClickListener,GuideCustomizeCallback{


	private static final String TAG = HomeActivity.class.getSimpleName();
	private FragmentManager fragmentManager;
	private Map<String, HomeMenuItemRes> mResGroup = new HashMap<String, HomeMenuItemRes>();
	private ImageView ivPlay;
	private ImageView ivDiscovery;
	private ImageView ivMe;
	private RelativeLayout llme;
	private TextView tvPlay;
	private TextView tvDiscovery;
	private TextView tvMe;
	private String fragmentTag;
	private long exitTime = 0;
	private ImageView tvActionTitle;
	private SyncService syncService;
	private DoFourLayerReciver receiver;
	private TenbService tenbService;
	private UserService userService;
	private SmartCardService smartCardService;
	private DownDrawerView bottomTabs;
	private ImageView mActionBarMoreIV;
	private GuideHomeLeft guideHomeLeft;
	private GuideHomeDown guideHomeDown;
	private PlayFragment mPlayFragment;
	private boolean isActionBarMoreClick = false;
	private long channelId = 0;
	private ImageView coin;
	public static int phoneNumber;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_new);
		smartCardService=new SmartCardService(this);
		phoneNumber=0;
		initView();
		tenbService = new TenbService(this);
		userService = new UserService();
		mChannelService = new ChannelService(this);
		mSmartCardSharedPre = new SmartCardSharedPre(HomeActivity.this);
		fragmentManager = getSupportFragmentManager();
		setFragmentByTag(AppConfig.TAG_fragment_play);
		currentIntent(getIntent());
		syncService = SyncService.getInstance(HomeActivity.this);
		if(syncService.needInit()){
			if(!syncService.isLoading()){
				startService(new Intent(this, InitService.class));
			}
			CommonUtil.showProgressDialog(this, null, getString(R.string.waiting), true, false);
			final BroadcastReceiver initReceiver = new BroadcastReceiver(){
				
				@Override
				public void onReceive(Context context, Intent intent) {
					if(intent.getAction().equals(InitService.initAction_failure)){
						ToastUtil.centerShowToast(context, "Init error!");
					}
					CommonUtil.closeProgressDialog();
					getLocationCoverFlowData();
					doOncreate();
					doOnstart();
					unregisterReceiver(this);
				}
			};
			registerReceiver(initReceiver, new IntentFilter(InitService.initAction_success));
			registerReceiver(initReceiver, new IntentFilter(InitService.initAction_failure));
		}else{
			doOncreate();
		}
		
		new ProgramService(this).setOutLinePrograms();
		new MenuPage(this);//设置tag和类的映射
		
		receiver = new DoFourLayerReciver();
		registerReceiver(receiver, new IntentFilter(FeedbackService.CAN_FOUR_LAYER));
		
		//四格体验服务不在监听
//		Intent intent = new Intent(this,FourLayerService.class);
//		startService(intent);
//		checkLoginStatus();
		setDimension();
	}
	private void setDimension() {
		if (StarApplication.mUser != null) {
			GA.sendCustomDimension(5, String.valueOf(StarApplication.mUser.getId()));
		}
	}
	private void checkLoginStatus(){
		if(!SyncService.getInstance(this).isDBReady()&&!SyncService.getInstance(this).isLoading()){
			com.star.util.Logger.d("not login, must go welcome!");
			ToastUtil.centerShowToast(this, "Sorry, you need login again!");
			CommonUtil.startActivity(this, WelcomeActivity.class);
			finish();

		}
	}

	private void initView() {
		OnekeyShare.at=this;
		guideHomeLeft = (GuideHomeLeft) findViewById(R.id.guide_left_right_mask);
		guideHomeDown = (GuideHomeDown) findViewById(R.id.guide_down_mask);
		findViewById(R.id.ll_play).setOnClickListener(this);
		findViewById(R.id.ll_discovery).setOnClickListener(this);
		llme=(RelativeLayout) findViewById(R.id.ll_me); 
		llme.setOnClickListener(this);
		ivPlay = (ImageView) findViewById(R.id.iv_icon_play);
		ivDiscovery = (ImageView) findViewById(R.id.iv_icon_discovery);
		ivMe = (ImageView) findViewById(R.id.iv_icon_me);
		coin=(ImageView) findViewById(R.id.coin);
		
		tvPlay = (TextView) findViewById(R.id.tv_text_play);
		tvDiscovery = (TextView) findViewById(R.id.tv_text_discovery);
		tvMe = (TextView) findViewById(R.id.tv_text_me);
		
		tvActionTitle = (ImageView) findViewById(R.id.tv_actionbar_title);
		bottomTabs = (DownDrawerView) findViewById(R.id.dv_tab_down);
		
		mActionBarMoreIV = (ImageView) findViewById(R.id.iv_actionbar_more);
		mActionBarMoreIV.setOnClickListener(this);
		findViewById(R.id.iv_actionbar_search).setOnClickListener(this);
		mPlayFragment = new PlayFragment();
//		getSmartCardInfo();//判断是否绑卡
		initBottomBar();
		getDisplayWidthHeight();
	}
	
	/**
	 * 判断是否绑过智能卡
	 */
	private void getSmartCardInfo(){
		if(FunctionService.doHideFuncation(FunctionType.SmartCard)){
			return;
		}
		final int status = mSmartCardSharedPre.getFirstBindStatus();
		 if(status==1&&!mSmartCardSharedPre.isClickMeButton()){
			 coin.setVisibility(View.VISIBLE);
			 startAnimation(coin);
		 }else{
			 coin.setVisibility(View.GONE);
		 }
		smartCardService.isBindSmartCard(new OnResultListener<Integer>() {
			
			@Override
			public void onSuccess(Integer value) {
				if(value!=null){
					 if(value==1&&!mSmartCardSharedPre.isClickMeButton()){
							coin.setVisibility(View.VISIBLE);
							startAnimation(coin);
					 }else{
							coin.setVisibility(View.GONE);
					 }
					 mSmartCardSharedPre.setFirstBindStatus(value);
					 MeFragment fragment = (MeFragment) mResGroup.get(AppConfig.TAG_fragment_me).getFragment();
					 if(fragment!=null){
						 fragment.updateList();
					 }
				} 
			}
			@Override
			public boolean onIntercept() {
			 
				return false;
			}
			@Override
			public void onFailure(int errorCode, String msg) {
				
			}
		});
		
	}
	/**
	 * 金币动画
	 * @param imageview
	 */
	private void startAnimation(final ImageView imageview){
		int toY=(int) (llme.getHeight()*1.5);
		int Y=imageview.getBottom();
 		ObjectAnimator translation = ObjectAnimator.ofFloat(imageview, "translationY", Y, Y-toY);
		ObjectAnimator rotation = ObjectAnimator.ofFloat(imageview, "rotationY", 0,360);
		ObjectAnimator alpha1 = ObjectAnimator.ofFloat(imageview, "alpha", 0.2f,1.0f);
		ObjectAnimator alpha2 = ObjectAnimator.ofFloat(imageview, "alpha", 1.0f,0);
		translation.setDuration(1000);
		translation.setInterpolator(new DecelerateInterpolator());
 		alpha1.setDuration(1000);
 		rotation.setDuration(1500);
 		alpha2.setDuration(500);
		AnimatorSet animSet = new AnimatorSet();
//		animSet.setInterpolator(new DecelerateInterpolator());
		// 两个动画同时执行
 	 	animSet.play(translation).with(alpha1);
 	 	animSet.play(rotation).after(translation);
 	 	animSet.play(alpha2).after(rotation);
 	 	animSet.addListener(new AnimatorListenerAdapter() {
 	 		@Override
 	 		public void onAnimationEnd(Animator animation) {
 	 			super.onAnimationEnd(animation);
 	 			startAnimation(imageview);
 	 		}
		});
		animSet.start();
   
	}
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		currentIntent(intent);
	
	}
	private void currentIntent(Intent intent){
		replaceFragmentByTag(intent);
		try{
			channelId = Long.parseLong(intent.getStringExtra("channelId"));
		}catch (Exception e) {
			channelId = intent.getLongExtra("channelId", 0);
		}
		if(channelId != 0){
			setFragmentByTag(AppConfig.TAG_fragment_play);
			mPlayFragment.setChannelId(channelId);
		}
		
	}
	private void doOncreate() {
		if(!CommonUtil.show(HomeActivity.this, Constant.GUIDE_DOWN, Constant.GUIDE_BY_VERSION)){
			guideHomeDown.setVisibility(View.VISIBLE);
			guideHomeDown.setGuideCustomizeCallback(HomeActivity.this);
		}else{
			if(!CommonUtil.show(HomeActivity.this, Constant.GUIDE_LEFT_RIGHT, Constant.GUIDE_BY_VERSION)){
				guideHomeLeft.setVisibility(View.VISIBLE);
				guideHomeLeft.setGuideCustomizeCallback(HomeActivity.this);
			}else{
				MaskUtil.showFavoriteCollectMask(HomeActivity.this);	
			}
		}
		userService.initUserInfo(this);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				syncService.doSync();
				startService();
			}
		}, 1000);
	}
	
	private void doOnstart() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				syncService.sync_();
//				tenbService.checkNewTopic(HomeActivity.this);
			}
		}, 2000);
		getSmartCardInfo();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		if(syncService.isDBReady()) {
			doOnstart();
		}
	}
	
	private void startService(){
		startService(new Intent(this, SyncStatusService.class));
		if(!SharedPreferencesUtil.isDiciveIdLogin(this))
			startService(new Intent(this, SmartCardService.class));
	}
	

	private void initBottomBar() {
		HomeMenuItemRes playItem = new HomeMenuItemRes(AppConfig.TAG_fragment_play,R.drawable.ic_play_shopping_orange_24dp,R.drawable.ic_play_shopping_deepgrey_24dp,mPlayFragment);
		mPlayFragment.setHomeBottomTab(bottomTabs);
		DiscoveryFragment discoveryFragment = new DiscoveryFragment();
		discoveryFragment.setHomeBottomTab(bottomTabs);
		HomeMenuItemRes discItem = new HomeMenuItemRes(AppConfig.TAG_fragment_discovery,R.drawable.ic_discovery_orange_24dp,R.drawable.ic_discovery_deepgrey_24dp,discoveryFragment);
		MeFragment meFragment = new MeFragment();
		meFragment.setHomeBottomTab(bottomTabs);
		HomeMenuItemRes meItem = new HomeMenuItemRes(AppConfig.TAG_fragment_me,R.drawable.ic_me_orange_24dp,R.drawable.ic_me_deepgrey_24dp,meFragment);
		
		mPlayFragment.setDrawerListener(new DrawerScrollListener());
		discoveryFragment.setDrawerListener(new DrawerScrollListener());
		meFragment.setDrawerListener(new DrawerScrollListener());
		
		mResGroup.put(AppConfig.TAG_fragment_play, playItem);
		mResGroup.put(AppConfig.TAG_fragment_discovery, discItem);
		mResGroup.put(AppConfig.TAG_fragment_me, meItem);
	}
	
	private void updatebottomBar(String tag){
		HomeMenuItemRes lastRes = mResGroup.get(fragmentTag);
		if(lastRes!=null){
			if(AppConfig.TAG_fragment_play.equals(fragmentTag)){
				tvPlay.setTextColor(getResources().getColor(R.color.black));
				ivPlay.setImageResource(lastRes.getUnfocusRes());
			}else if(AppConfig.TAG_fragment_discovery.equals(fragmentTag)){
				tvDiscovery.setTextColor(getResources().getColor(R.color.black));
				ivDiscovery.setImageResource(lastRes.getUnfocusRes());
			}else if(AppConfig.TAG_fragment_me.equals(fragmentTag)){
				tvMe.setTextColor(getResources().getColor(R.color.black));
				ivMe.setImageResource(lastRes.getUnfocusRes());
			}
		}
		HomeMenuItemRes res = mResGroup.get(tag);
		res.setFoucs(true);
		if(AppConfig.TAG_fragment_play.equals(tag)){
			tvPlay.setTextColor(getResources().getColor(R.color.home_orange));
			ivPlay.setImageResource(res.getFocusRes());
		}else if(AppConfig.TAG_fragment_discovery.equals(tag)){
			tvDiscovery.setTextColor(getResources().getColor(R.color.home_orange));
			ivDiscovery.setImageResource(res.getFocusRes());
		}else if(AppConfig.TAG_fragment_me.equals(tag)){
			tvMe.setTextColor(getResources().getColor(R.color.home_orange));
			ivMe.setImageResource(res.getFocusRes());
		}
		if(tag.equals(AppConfig.TAG_fragment_play)){
			mActionBarMoreIV.setVisibility(View.VISIBLE);
		}else{
			mActionBarMoreIV.setVisibility(View.GONE);
		}
	}
	
	private class DoFourLayerReciver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction() == FeedbackService.CAN_FOUR_LAYER || intent.getAction() == FeedbackService.DO_FINISH_FOUR_LAYER) {
				
			}
		}
	}
	 
	@Override
	protected void onResume() {
		super.onResume();
		getLocationCoverFlowData();
	}
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(receiver);
		super.onDestroy();
	}
	
	private void replaceFragmentByTag(Intent intent) {
		String tag = intent.getStringExtra("fragmentTag");
		if(tag!=null){
			setFragmentByTag(tag);
		}
	}		
	
	public void setActionBarMoreClick(boolean isActionBarMoreClick) {
		this.isActionBarMoreClick = isActionBarMoreClick;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_play:
			setFragmentByTag(AppConfig.TAG_fragment_play);
			break;
		case R.id.ll_discovery:
			setFragmentByTag(AppConfig.TAG_fragment_discovery);
			break;
		case R.id.ll_me:
			if(!mSmartCardSharedPre.isClickMeButton()){
				mSmartCardSharedPre.setClickMeButton(true);
				coin.setVisibility(View.GONE);
			}
			setFragmentByTag(AppConfig.TAG_fragment_me);
			break;
		case R.id.iv_actionbar_more:
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				if(channelVOs != null && channelVOs.size()>0){
					if (!isActionBarMoreClick) {
						mPlayFragment.clearAndRefreshData();
						isActionBarMoreClick = true;
						Animation anticlockAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_reverse_rotate_anticlock);
						mActionBarMoreIV.startAnimation(anticlockAnimation);
						mPlayFragment.showTitleCoverFlowGridView();
						mPlayFragment.hidePlayCoverFlowCricleImage();
						ToastUtil.showToast(HomeActivity.this, "Channel_list");
						GA.sendEvent("channel", "Channel_list", "Channel_list", 1);
					}else {
						controlFavoriteCollectAndActionBar();
					}
				}else {
					ToastUtil.centerShowToast(this,getString(R.string.no_data));
				}
			}else {
				Log.i(TAG, "系统版本过低！");
			}
			break;
		case R.id.iv_actionbar_search:
			CommonUtil.startActivity(this, SearchActivity.class);
			break;
		default:
			break;
		}
	}

	/**
	 * actionBar顺时针动画
	 */
	public void actionBarClockAnimation() {
		Animation clockAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_reverse_rotate_clock);
		mActionBarMoreIV.startAnimation(clockAnimation);
	}
	
	public Fragment setFragmentByTag(String tag) {
		if(tag.equals(fragmentTag))
			return null;
		Fragment fragment = null;
		try {
			FragmentTransaction transaction = fragmentManager.beginTransaction();
			fragment = mResGroup.get(tag).getFragment();
//			transaction.setCustomAnimations(R.anim.tran_next_in, R.anim.tran_next_out);
			transaction.replace(R.id.home_container, fragment);
			transaction.addToBackStack(null);
			transaction.commitAllowingStateLoss();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(fragment != null){
			updatebottomBar(tag);
		}
		fragmentTag = tag;
		return fragment;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 5000) {
				if (mPlayFragment.getFavoriteCollectStatu() == View.VISIBLE) {
					controlFavoriteCollectAndActionBar();
				}else {
					ToastUtil.centerShowToast(this, getString(R.string.quit_again_click));
					exitTime = System.currentTimeMillis();
				}
				return true;
			} else {
				if (getApplication() instanceof StarApplication) {
					((StarApplication) getApplication()).exit();
				}
			}
			
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 控制收藏列表和actionbar
	 */
	private void controlFavoriteCollectAndActionBar() {
		isActionBarMoreClick = false;
		actionBarClockAnimation();
		mPlayFragment.hideTitleCoverFlowGridView();
	}

	private ChannelService mChannelService;
	private SmartCardSharedPre mSmartCardSharedPre;
	private List<ChannelVO> channelVOs = new ArrayList<ChannelVO>();
	/**
	 * 从本地加载频道列表
	 */
	private void getLocationCoverFlowData(){
		new LoadingDataTask() {
			List<ChannelVO> chns;
			@Override
			public void onPreExecute() {
			}

			@Override
			public void onPostExecute() {
				if(chns==null || chns.size()==0 ){
					return;
				}else {
					channelVOs.clear();
					channelVOs.addAll(chns);
					initChannels(chns);
				}
			}

			@Override
			public void doInBackground() {
				//获得的收藏列表
				chns = mChannelService.getChannels(-1,-1);
			}
		}.execute();
	}
	
	private void initChannels(List<ChannelVO> chns) {
		mPlayFragment.initChannels(chns);
	}
	
	/**
	 * 获得屏幕的宽高
	 */
	private void getDisplayWidthHeight(){
		//获得屏幕分辨率
		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		int height = displayMetrics.heightPixels;
		int width = displayMetrics.widthPixels;
		Constant.WINDOW_WIDTH = width;
		Constant.POSTER_HEIGHT = height;
	}

	@Override
	public void onGuide(int direction) {
		if(direction == 1){
			mPlayFragment.setCurrentChannelLeft();
		}else{
			mPlayFragment.setCurrentChannelRight();
		}
		guideHomeLeft.setVisibility(View.GONE);
		CommonUtil.setShow(Constant.GUIDE_LEFT_RIGHT, Constant.GUIDE_BY_VERSION);
		MaskUtil.showFavoriteCollectMask(HomeActivity.this);
	}

	@Override
	public void onGuide() {
		mPlayFragment.openTopView();
		guideHomeDown.setVisibility(View.GONE);
		CommonUtil.setShow(Constant.GUIDE_DOWN, Constant.GUIDE_BY_VERSION);
		guideHomeLeft.setVisibility(View.VISIBLE);
		guideHomeLeft.setGuideCustomizeCallback(HomeActivity.this);
	}
}
