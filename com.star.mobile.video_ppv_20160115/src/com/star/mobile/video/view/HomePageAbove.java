package com.star.mobile.video.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.igexin.sdk.PushManager;
import com.igexin.sdk.Tag;
import com.star.cms.model.ChatRoom;
import com.star.cms.model.Recommend;
import com.star.cms.model.enm.AccountType;
import com.star.cms.model.enm.OrderType;
import com.star.cms.model.vo.ChannelVO;
import com.star.cms.model.vo.TaskVO;
import com.star.cms.model.vo.VOD;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.activity.BrowserActivity;
import com.star.mobile.video.activity.TellFriendActivity;
import com.star.mobile.video.adapter.ViewPagerAdapter;
import com.star.mobile.video.chatroom.ChatRoomsAdapter;
import com.star.mobile.video.chatroom.ChatService;
import com.star.mobile.video.dialog.DoTaskClickListener;
import com.star.mobile.video.dialog.GetCoinClickListener;
import com.star.mobile.video.home.HomeActivity;
import com.star.mobile.video.me.mycoins.MyCoinsActivity;
import com.star.mobile.video.me.mycoins.TaskItemView;
import com.star.mobile.video.me.mycoins.TaskService;
import com.star.mobile.video.model.FunctionType;
import com.star.mobile.video.model.TaskCode;
import com.star.mobile.video.service.ChannelService;
import com.star.mobile.video.service.FunctionService;
import com.star.mobile.video.service.RecommendService;
import com.star.mobile.video.service.VideoService;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.ImageUtil;
import com.star.mobile.video.util.LoadingDataTask;

public class HomePageAbove extends LinearLayout implements OnClickListener, OnPageChangeListener{
	private Context context;
	private ViewPager vp_poster_group;
	private ViewGroup ll_pager_group;
	private List<ChannelVO> favChannels;
	private List<ChannelVO> noFavChannels;
	private List<ChannelVO> onAirChns;
	private ChannelItemView chnLayout_2;
	private ChannelItemView chnLayout_1;
	private ChannelItemView chnLayout_3;
	private ChannelItemView chnLayout_4;
	private EpgOnAirItemView epgLayout_1;
	private EpgOnAirItemView epgLayout_2;
	private EpgOnAirItemView epgLayout_3;
	private ImageView[] dots;
	private List<View> advs = new ArrayList<View>();
	private List<Recommend> recommends = new ArrayList<Recommend>();
	private ViewPagerAdapter mAdapter;
	private int currentPage;
	private ChatService chatService;
	private ChannelService chnService;
	private RecommendService remService;
	private VideoService videoService;
	private TaskService taskService;
	private Timer timer = new Timer();
	private TimerTask timTask;
	private GridView gv_chatRooms;
	private TaskItemView taskItemView;
	private ChatRoomsAdapter logoAdapter;
	private OnClickListener doTaskListener;
	private OnClickListener getCoinListener;
	private int currentTask = 0;
	private Animation animRotate;
	private ImageView btnRefresh;
	private TextView tvCoin;
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				currentPage++;
				if(currentPage > advs.size()-1) {
					currentPage = 0;
				}
				vp_poster_group.setCurrentItem(currentPage);
				break;
			case 1000:
				executeTaskTask(false);
				break;
			}
		}
	};
	
	/*private ChatReminderCallBack callback = new ChatReminderCallBack() {
		
		@Override
		public void callback(List<ChatRoom> cs) {
			updateChatRoom(cs);
		}
	};*/
	
	private void updateChatRoom(List<ChatRoom> cs) {
		if(cs==null || cs.size()==0)
			return;
		if(cs.size()>4)
			cs = cs.subList(0, 4);
		if(logoAdapter == null){
			logoAdapter = new ChatRoomsAdapter(context, cs);
			gv_chatRooms.setAdapter(logoAdapter);
		}else{
			logoAdapter.updateDataAndRefreshUI(cs);
		}
	}
	
	/*private void getLocalData() {
		new LoadingDataTask() {
			private List<ChatRoom> cs;
			@Override
			public void onPreExecute() {
			}
			
			@Override
			public void onPostExecute() {
				updateChatRoom(cs);
 			}

			@Override
			public void doInBackground() {
				cs = chatService.getChatRooms(true);
			}
		}.execute();
	}*/
	
	private List<TaskVO> tasks_ = new ArrayList<TaskVO>();
	private View videoLayout_1;
	private View videoLayout_2;
	private View videoLayout_3;
	private void executeTaskTask(final boolean fromLocal){
		new LoadingDataTask() {
			private List<TaskVO> ts;
			@Override
			public void onPreExecute() {
				refreshdoing();
			}
			
			@Override
			public void onPostExecute() {
				findViewById(R.id.task_loading).setVisibility(View.GONE);
				if(ts != null){
					for (FunctionType type : StarApplication.mAreaFunctions) {
						for(String code : type.getTaskCodes()){
							Iterator<TaskVO> it = ts.iterator();
							while (it.hasNext()) {
								TaskVO task = it.next();
								if(task.getCode().equals(code)) {
									it.remove();
									break;
								}
							}
						}
					}
					tasks_.clear();
					List<TaskVO> undoTasks = new ArrayList<TaskVO>();
					List<TaskVO> doneTasks = new ArrayList<TaskVO>();
					for(TaskVO t :ts) {
						if(StarApplication.mUser!=null && StarApplication.mUser.getType()==AccountType.PhoneNumber) {
							if(t.getCode().equals(com.star.cms.model.Task.TaskCode.Link_ThirdAccount)) {
								continue;
							}
						}
						if(t.getTimesToday()<t.getTimesLimitToday()&&t.getTimesLimitToday()>0) {
							if(t.getCode().equals(TaskCode.Sign_in)){
								tasks_.add(0, t);
							}else{
//								if(t.getId()==19||t.getId()==20)
//									break;
								undoTasks.add(t);
								StarApplication.mUndoTask.add(t);
							}
						}
						if(t.getCoins() != 0) {
							if(t.getCode().equals(TaskCode.Sign_in)){
								tasks_.add(0, t);
							}else{
								doneTasks.add(t);
							}
						} 
					}
					tasks_.addAll(doneTasks);
					tasks_.addAll(undoTasks);
					if(doneTasks.size()>0 || currentTask>=tasks.size()){
						currentTask = 0;
					}
					updateTaskStatus();
				}else{
					refreshFails();
					findViewById(R.id.iv_task_update).setOnClickListener(HomePageAbove.this);
				}
			}

			@Override
			public void doInBackground() {
//				ts = taskService.getTasks(fromLocal, ApplicationUtil.getAppVerison(context));
			}
		}.execute();
	}
	
	private void refreshFails() {
		findViewById(R.id.ll_task_item).setVisibility(View.GONE);
		findViewById(R.id.rl_task_complete).setVisibility(View.GONE);
		findViewById(R.id.ll_refresh_fail).setVisibility(View.VISIBLE);
		findViewById(R.id.ll_task_refresh).setVisibility(View.GONE);
	}
	
	private void refreshSuccess() {
		findViewById(R.id.ll_task_item).setVisibility(View.VISIBLE);
		findViewById(R.id.rl_task_complete).setVisibility(View.GONE);
		findViewById(R.id.ll_refresh_fail).setVisibility(View.GONE);
	}
	
	private void refreshCompleted() {
		findViewById(R.id.rl_task_complete).setVisibility(View.VISIBLE);
		findViewById(R.id.ll_task_item).setVisibility(View.GONE);
		findViewById(R.id.ll_refresh_fail).setVisibility(View.GONE);
		findViewById(R.id.ll_task_refresh).setVisibility(View.GONE);
	}
	
	private void refreshdoing() {
		findViewById(R.id.task_loading).setVisibility(View.VISIBLE);
		findViewById(R.id.rl_task_complete).setVisibility(View.GONE);
		findViewById(R.id.ll_task_refresh).setVisibility(View.GONE);
		findViewById(R.id.ll_task_item).setVisibility(View.GONE);
		findViewById(R.id.ll_refresh_fail).setVisibility(View.GONE);
	}
	
	private void updateTaskStatus() {
		if(tasks_.size()>0){
			TaskVO task = tasks_.get(currentTask); //数组越界
			if(task.getCode().equals(TaskCode.Sign_in)){
				findViewById(R.id.ll_task_refresh).setVisibility(View.GONE);
			}else{
				findViewById(R.id.ll_task_refresh).setVisibility(View.VISIBLE);
			}
//			if(task.getCoins()>0){
//				taskItemView.setTask(task, getCoinListener);
//			}else{
//				taskItemView.setTask(task, doTaskListener);
//			}
			refreshSuccess();
		}else{
			refreshCompleted();
			int coins = SharedPreferencesUtil.getTaskCoinSharedPreferences(context).getInt("coins", 0);
			tvCoin.setText(String.valueOf(coins));
		}
	}

	public HomePageAbove(Context context,HomeActivity homeActivity) {
		super(context);
		this.context = context;
		LayoutInflater.from(context).inflate(R.layout.page_home_above, this);
//		chatService = ChatService.getInstance(context);
		chnService = new ChannelService(context);
		remService = new RecommendService();
//		taskService = new TaskService();
		videoService = new VideoService(context);
		
		initView(context);
		executePosterTask(true);
//		getLocalData();
//		ChatService.getInstance(context).setChatReminderCallBack(callback);
		animRotate = AnimationUtils.loadAnimation(context, R.anim.anim_rotate);
		setTag();
	}
	
	private void initView(Context context) {
		OnekeyShare.at=(Activity)context;
		final RelativeLayout rl_poster_layout = (RelativeLayout) findViewById(R.id.rl_poster_layout);
		ImageUtil.measureLayout(rl_poster_layout, 9f/16); 
		vp_poster_group = (ViewPager) findViewById(R.id.vp_poster_group);
		ll_pager_group = (ViewGroup) findViewById(R.id.ll_pager_group);
		vp_poster_group.setOnPageChangeListener(this);
		findViewById(R.id.tv_onair_more_bg).setOnClickListener(this);
		findViewById(R.id.tv_channel_more_bg).setOnClickListener(this);
		findViewById(R.id.tv_chatroom_more_bg).setOnClickListener(this);
		findViewById(R.id.tv_task_more_bg).setOnClickListener(this);
		
		ImageView coupon=(ImageView)findViewById(R.id.btn_share_coupon);
		coupon.setOnClickListener(this);
		CommonUtil.startAnimation(coupon);
		
		ImageView football = (ImageView) findViewById(R.id.btn_home_football);
		football.setOnClickListener(this);
		CommonUtil.startAnimation(football);
		
		chnLayout_1 = (ChannelItemView) findViewById(R.id.chnLayout_1);
		chnLayout_2 = (ChannelItemView) findViewById(R.id.chnLayout_2);
		chnLayout_3 = (ChannelItemView) findViewById(R.id.chnLayout_3);
		chnLayout_4 = (ChannelItemView) findViewById(R.id.chnLayout_4);
		
		epgLayout_1 = (EpgOnAirItemView) findViewById(R.id.epgLayout_1);
		epgLayout_2 = (EpgOnAirItemView) findViewById(R.id.epgLayout_2);
		epgLayout_3 = (EpgOnAirItemView) findViewById(R.id.epgLayout_3);
		epgLayout_1.setOnClickable(true);
		epgLayout_2.setOnClickable(true);
		epgLayout_3.setOnClickable(true);
		
		videoLayout_1 = findViewById(R.id.rl_video_one);
		videoLayout_2 = findViewById(R.id.rl_video_two);
		videoLayout_3 = findViewById(R.id.rl_video_three);
		videoLayout_1.setOnClickListener(this);
		videoLayout_2.setOnClickListener(this);
		videoLayout_3.setOnClickListener(this);
		
		mAdapter = new ViewPagerAdapter(getContext(), recommends, advs);
		vp_poster_group.setAdapter(mAdapter);
		
		gv_chatRooms = (GridView) findViewById(R.id.gv_chat_room);
		taskItemView = (TaskItemView) findViewById(R.id.taskItemView);
		taskItemView.setOnClickListener(this);
		findViewById(R.id.ll_task_item).setVisibility(View.GONE);
		btnRefresh = (ImageView) findViewById(R.id.iv_task_refresh);
		btnRefresh.setOnClickListener(this);
		doTaskListener = new DoTaskClickListener((Activity)context);
		getCoinListener = new GetCoinClickListener((Activity)context, handler);
		tvCoin = (TextView) findViewById(R.id.tv_receive_coin);
		findViewById(R.id.task_loading).setVisibility(View.VISIBLE);
	}

	public void onStart(boolean dbReady){
		if(!FunctionService.doHideFuncation(FunctionType.InviteFriends)){
			findViewById(R.id.rl_share).setVisibility(View.VISIBLE);
		}else{
			findViewById(R.id.rl_share).setVisibility(View.GONE);
		}
		if(!FunctionService.doHideFuncation(FunctionType.SimpleVersion)){
			findViewById(R.id.ll_task).setVisibility(View.VISIBLE);
			findViewById(R.id.ll_onair).setVisibility(View.VISIBLE);
			findViewById(R.id.ll_channelGuide).setVisibility(View.VISIBLE);
		}else{
			findViewById(R.id.ll_task).setVisibility(View.GONE);
			findViewById(R.id.ll_onair).setVisibility(View.GONE);
			findViewById(R.id.ll_channelGuide).setVisibility(View.GONE);
		}
		if(dbReady){
			executePosterTask(false);
			getRecommendVideos();
			if(!FunctionService.doHideFuncation(FunctionType.SimpleVersion)){
				executeChnTask();
				executeOnAirTaskWithEpg();
				executeTaskTask(false);
			}
		}
	}

	private void initPosterData() {
		dots = new ImageView[recommends.size()];
		ImageView imageView;
		com.star.ui.ImageView poster;
		ll_pager_group.removeAllViews();
		advs.clear();
		for(int i=0;i<recommends.size();i++){
			poster = new com.star.ui.ImageView(getContext());
			poster.setScaleType(ScaleType.FIT_XY);
			poster.setImageResource(R.drawable.default_program);
			try{
				poster.setUrl(recommends.get(i).getPosters().get(0).getUrl());
			}catch (Exception e) {
			}
			advs.add(poster);
			
			imageView = new ImageView(getContext());
			imageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			dots[i] = imageView;
			if (i == 0) {
				dots[i].setBackgroundResource(R.drawable.poster_page_focus);
			} else {
				dots[i].setBackgroundResource(R.drawable.poster_page_unfocus);
			}
			ll_pager_group.addView(dots[i]);
		}
		mAdapter.updateDataAndRefreshUI(recommends, advs);
		if(timTask != null)
			timTask.cancel();
		timTask = new TimerTask() {
			@Override
			public void run() {
				handler.sendEmptyMessage(0);
			}
		};
		timer.schedule(timTask, 2000, 5000);
	}
	
	private boolean fromNetAlready = false;
	private void executePosterTask(final boolean fromLocal){
		new LoadingDataTask() {
			List<Recommend> rs;
			@Override
			public void onPreExecute() {
			}
			
			@Override
			public void onPostExecute() {
				if(rs==null || rs.size()==0)
					return;
				if(fromLocal && fromNetAlready)
					return;
				recommends.clear();
				recommends.addAll(rs);
				initPosterData();
				if(!fromLocal){
					fromNetAlready = true;
				}
			}
			
			@Override
			public void doInBackground() {
				rs = remService.getRecommends(fromLocal, context);
			}
		}.execute();
	}
	
	private void executeChnTask(){
		new LoadingDataTask() {
			@Override
			public void onPreExecute() {
			}
			
			@Override
			public void onPostExecute() {
				if(noFavChannels.size()!=0)
					initChannelLayout();
			}
			
			@Override
			public void doInBackground() {
				favChannels = chnService.getFavChannels(true, 0, 4);
				noFavChannels = chnService.getFavChannels(false, 0, 4);
			}
		}.execute();
	}
	
	private void executeOnAirTask(){
		new LoadingDataTask() {
			@Override
			public void onPreExecute() {
			}
			
			@Override
			public void onPostExecute() {
				initOnAirLayout();
			}
			
			@Override
			public void doInBackground() {
				onAirChns = chnService.getChannels(OrderType.FAVORITE, 0, 3);
			}
		}.execute();
	}
	
	private void executeOnAirTaskWithEpg(){
		new LoadingDataTask() {
			private List<ChannelVO> chns;
			@Override
			public void onPreExecute() {
			}
			
			@Override
			public void onPostExecute() {
				if(chns!=null && chns.size()>0){
					onAirChns = chns;
					initOnAirLayout();
				}else{
					executeOnAirTask();
				}
			}
			
			@Override
			public void doInBackground() {
				chns = chnService.getChannels(0, 3, OrderType.FAVORITE, true);
			}
		}.execute();
	}
	
	private void initOnAirLayout() {
		epgLayout_1.setVisibility(View.GONE);
		epgLayout_2.setVisibility(View.GONE);
		epgLayout_3.setVisibility(View.GONE);
		epgLayout_1.setCurrentPage(Constant.PAGE_ONAIR_NOW);
		epgLayout_2.setCurrentPage(Constant.PAGE_ONAIR_NOW);
		epgLayout_3.setCurrentPage(Constant.PAGE_ONAIR_NOW);
		try{
			epgLayout_1.fillData(onAirChns.get(0), false);
			epgLayout_1.setVisibility(View.VISIBLE);
			epgLayout_2.fillData(onAirChns.get(1), false);
			epgLayout_2.setVisibility(View.VISIBLE);
			epgLayout_3.fillData(onAirChns.get(2), false);
			epgLayout_3.setVisibility(View.VISIBLE);
		}catch (Exception e){
		}
	}

	private void initChannelLayout() {
		try{
			switch (favChannels.size()) {
			case 0:
				chnLayout_1.fillData(noFavChannels.get(0));
				chnLayout_2.fillData(noFavChannels.get(1));
				chnLayout_3.fillData(noFavChannels.get(2));
				chnLayout_4.fillData(noFavChannels.get(3));
				break;
			case 1:
				chnLayout_1.fillData(favChannels.get(0));
				chnLayout_2.fillData(noFavChannels.get(0));
				chnLayout_3.fillData(noFavChannels.get(1));
				chnLayout_4.fillData(noFavChannels.get(2));
				break;
			default:
				chnLayout_1.fillData(favChannels.get(0));
				chnLayout_2.fillData(noFavChannels.get(0));
				chnLayout_3.fillData(favChannels.get(1));
				chnLayout_4.fillData(noFavChannels.get(1));
				break;
			}
		}catch (Exception e){
		}
	}
	
	private List<VOD> videos;
	private void getRecommendVideos(){
		new LoadingDataTask() {
			@Override
			public void onPreExecute() {
			}
			
			@Override
			public void onPostExecute() {
				if(videos!= null && videos.size()>0){
					try{
						com.star.ui.ImageView ivIcon_1 = (com.star.ui.ImageView) videoLayout_1.findViewById(R.id.iv_video_one);
						TextView tvName_1 = (TextView) videoLayout_1.findViewById(R.id.tv_video_one);
						try{
//							ivIcon_1.setUrl(videos.get(0).getPoster().getResources().get(0).getUrl());
						}catch (Exception e) {
							Log.d("TAG", "fill poster error!" ,e);
						}
//						tvName_1.setText(videos.get(0).getName());
						tvName_1.setText(context.getResources().getString(R.string.bundesliga));
						videoLayout_1.setVisibility(View.VISIBLE);
						
						com.star.ui.ImageView ivIcon_2 = (com.star.ui.ImageView) videoLayout_2.findViewById(R.id.iv_video_two);
						TextView tvName_2 = (TextView) videoLayout_2.findViewById(R.id.tv_video_two);
						try{
//							ivIcon_2.setUrl(videos.get(1).getPoster().getResources().get(0).getUrl());
						}catch (Exception e) {
							Log.d("TAG", "fill poster error!" ,e);
						}
//						tvName_2.setText(videos.get(1).getName());
						tvName_2.setText(context.getResources().getString(R.string.seriea));
						videoLayout_2.setVisibility(View.VISIBLE);
						
						com.star.ui.ImageView ivIcon_3 = (com.star.ui.ImageView) videoLayout_3.findViewById(R.id.iv_video_three);
						TextView tvName_3 = (TextView) videoLayout_3.findViewById(R.id.tv_video_three);
						try{
							ivIcon_3.setUrl(videos.get(2).getPoster().getResources().get(0).getUrl());
						}catch (Exception e) {
							Log.d("TAG", "fill poster error!" ,e);
						}
						tvName_3.setText(videos.get(2).getName());
						videoLayout_3.setVisibility(View.VISIBLE);
					}catch (Exception e) {
						Log.d("TAG", "fill video in home error!", e);
					}
				}
			}
			
			@Override
			public void doInBackground() {
				videos = videoService.getRecommendVideo(0, 3);
				//remService.getRecommends(fromLocal, homeActivity);
			}
		}.execute();
	}

	
	
	
	
	
	@Override
	public void onClick(View v) {
		HomeActivity activity = ((HomeActivity)getContext());
		String aredCode = SharedPreferencesUtil.getAreaCode(activity);
		switch (v.getId()) {
		case R.id.tv_onair_more_bg:
			activity.setFragmentByTag(context.getString(R.string.fragment_tag_onAir));
			break;
		case R.id.tv_channel_more_bg:
			activity.setFragmentByTag(context.getString(R.string.fragment_tag_channelGuide));
			break;
		case R.id.tv_chatroom_more_bg:
			activity.setFragmentByTag(context.getString(R.string.fragment_tag_chatRooms));
			break;
		case R.id.tv_task_more_bg:
			CommonUtil.startActivity((Activity)context, MyCoinsActivity.class);
			break;
		case R.id.btn_share_coupon:
			CommonUtil.startActivity((Activity)context, TellFriendActivity.class);
			break;
		case R.id.btn_home_football:
//			//和侧滑栏的video&live点击后的效果一样
//			String tagName = context.getResources().getString(R.string.fragment_tag_video);
//			Class<?> clazz = MenuHandle.getMenuItemClass(tagName);
//			try {
//				Object object = clazz.getConstructor().newInstance();
//				if(object instanceof Fragment){
//					homeActivity.setFragmentByTag(tagName);
//					homeActivity.setFragment((Fragment)object);
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			
			break;
		case R.id.taskItemView:
			CommonUtil.startActivity((Activity)context, MyCoinsActivity.class);
			break;
		case R.id.iv_task_refresh: 
			currentTask++;
			if(currentTask>tasks_.size()-1)
				currentTask = 0;
			btnRefresh.startAnimation(animRotate);
			updateTaskStatus();
			break;
		case R.id.iv_task_update:
			executeTaskTask(false);
			break;
		case R.id.rl_video_one:
//			try{
//				videoService.play(videos.get(0).getVideo().getResources().get(0).getUrl(), videos.get(0).getName(), videos.get(0).getVideo().getId());
//			}catch (Exception e) {
//			}
			//跳转到德甲网页
//			transBrowser(context.getResources().getString(R.string.bundesliga_url)+aredCode,context.getResources().getString(R.string.bundesliga_name));
			break;
		case R.id.rl_video_two:
//			try{
//				videoService.play(videos.get(1).getVideo().getResources().get(0).getUrl(), videos.get(1).getName(), videos.get(1).getVideo().getId());
//			}catch (Exception e) {
//			}
			//跳转到意甲网页
//			transBrowser(context.getResources().getString(R.string.serie_a_url)+aredCode,context.getResources().getString(R.string.serie_a_name));
			break;
		case R.id.rl_video_three:
//			try{
//				videoService.play(videos.get(2).getVideo().getResources().get(0).getUrl(), videos.get(2).getName(), videos.get(2).getVideo().getId());
//			}catch (Exception e) {
//			}
//			Fragment fragment = homeActivity.setFragmentByTag(homeActivity.getResources().getString(R.string.fragment_tag_channelGuide));
//			if(fragment != null){
//				Long channelID = 138L;
//				if(homeActivity.getString(R.string.server_url).contains("tenbre.me"))
//					channelID = 303L;
//				((ChannelGuideFragment)fragment).setCurrentChannel(channelID);
//			}
			break;
		}
	}
	/**
	 * 跳转到网页
	 * @param position
	 */
	private void transBrowser(String url,String name) {
		Intent intent = new Intent(getContext(), BrowserActivity.class);
		intent.putExtra("loadUrl", url);
		intent.putExtra("pageName", name);
		CommonUtil.startActivity(getContext(), intent);
		
	}
	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int arg0) {
		currentPage = arg0;
		for (int i = 0; i < advs.size(); i++) {
			if (i == arg0) {
				dots[i].setBackgroundResource(R.drawable.poster_page_focus);
			} else {
				dots[i].setBackgroundResource(R.drawable.poster_page_unfocus);
			}
		}
	}
	/**
	 * 设置推送的tag
	 */
	public void setTag(){
		String[] tags=new String[2];
		if(SharedPreferencesUtil.getUserName(context)!=null){
			tags[0]=SharedPreferencesUtil.getUserName(context);
			tags[1]=SharedPreferencesUtil.getAreaCode(context);
		}else{
			tags[0]=android.os.Build.MODEL;
			tags[1]=SharedPreferencesUtil.getAreaCode(context);
		}
        Tag[] tagParam = new Tag[tags.length];
        for (int i = 0; i < tags.length; i++) {
            Tag t = new Tag();
            t.setName(tags[i]);
            tagParam[i] = t;
        }
        int i=PushManager.getInstance().setTag(context, tagParam);
        Log.d("set tag", String.valueOf(i));
	}
}
