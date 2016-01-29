package com.star.mobile.video.epg;

import java.util.ArrayList;
import java.util.List;

import org.libsdl.app.Player;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.star.cms.model.Program;
import com.star.cms.model.Task.TaskCode;
import com.star.cms.model.Tenb;
import com.star.cms.model.enm.OrderType;
import com.star.cms.model.vo.ChannelVO;
import com.star.cms.model.vo.ProgramVO;
import com.star.cms.model.vo.VOD;
import com.star.mobile.video.AlertManager;
import com.star.mobile.video.R;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.base.BaseFragmentActivity;
import com.star.mobile.video.channel.NewVideoAdapter;
import com.star.mobile.video.epg.CommentListView.OnFaceClick;
import com.star.mobile.video.me.mycoins.TaskService;
import com.star.mobile.video.service.ChannelService;
import com.star.mobile.video.service.EggAppearService;
import com.star.mobile.video.service.ProgramService;
import com.star.mobile.video.service.VideoService;
import com.star.mobile.video.tenb.TenbService;
import com.star.mobile.video.util.ApplicationUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.DateFormat;
import com.star.mobile.video.util.IOUtil;
import com.star.mobile.video.util.ImageUtil;
import com.star.mobile.video.util.LoadingDataTask;
import com.star.mobile.video.util.ShareUtil;
import com.star.mobile.video.util.ToastUtil;
import com.star.mobile.video.view.NoScrollListView;
import com.star.util.app.GA;
import com.star.util.loader.OnResultListener;

public class EpgDetailActivity extends BaseActivity {
	
	private TextView tvEpgStartDate;
	private ImageView iv_alert_icon;
	private TextView epg_number;
	private TextView epg_descrip;
	private TextView epg_subhead;
	private TextView epg_class;
	private TextView tv_epg_name;
	private TextView tv_epg_startime;
	private ImageView ivPlayBtn;
	private TextView tvTitle;
	
	private CommentListView comList;
	private com.star.ui.ImageView iv_epg_poster;
	private com.star.ui.ImageView iv_channel_icon;
	
	private LinearLayout Channl_linear;
	private LinearLayout class_linear;
	private LinearLayout subhead_linear;
	private LinearLayout des_linear;
	private RelativeLayout rl_comment_layout;
 

	private TaskService taskService;
	
	private RelativeLayout RecommentLayout;
	private NoScrollListView lv_video_list;
	private NewVideoAdapter mVideoPlayAdapter;
	private List<VOD> mRecommentsVideo = new ArrayList<VOD>();// 推荐视频
	private int Offset = 0;
	private int COUNT = 4;
	 
	
	private ChannelService channelService;
	private ProgramService programService;
	private CommentService comnService;
	private VideoService videoService;
	private ProgramVO program;
	private ChannelVO channel;
	
	private boolean playStatus;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_epg_detail);
		programService = new ProgramService(this);
		channelService = new ChannelService(this);
		comnService = new CommentService(this);
		videoService = new VideoService(this);
		taskService = new TaskService(this);
		videoService = new VideoService(this);
		EggAppearService.appearEgg(this, EggAppearService.Program_in);
		initView();
	}
	private void initView() {
		RelativeLayout rl_poster_layout = (RelativeLayout) findViewById(R.id.rl_poster_layout);
		ImageUtil.measureLayout(rl_poster_layout, 9f/16); 
		tvTitle = (TextView) findViewById(R.id.tv_actionbar_title);
		tvTitle.setText(R.string.program_dital);
		iv_epg_poster = (com.star.ui.ImageView) findViewById(R.id.iv_epg_poster);
		Channl_linear=(LinearLayout) findViewById(R.id.channel_number);
		class_linear=(LinearLayout) findViewById(R.id.classification_linear);
		subhead_linear=(LinearLayout) findViewById(R.id.subhead_linear);
		des_linear=(LinearLayout) findViewById(R.id.descripion_linear);
		epg_number=(TextView) findViewById(R.id.tv_epg_number);
		epg_descrip = (TextView) findViewById(R.id.tv_epg_descripion);
		epg_class = (TextView) findViewById(R.id.tv_epg_classification);
		epg_subhead = (TextView) findViewById(R.id.tv_epg_subhead);
		tv_epg_name = (TextView) findViewById(R.id.tv_epg_name);
		tv_epg_startime = (TextView) findViewById(R.id.tv_epg_startime);
		tvEpgStartDate = (TextView) findViewById(R.id.tv_epg_stardate);
		comList = (CommentListView) findViewById(R.id.epg_comment_list);
		rl_comment_layout = (RelativeLayout) findViewById(R.id.rl_comment_layout);
		RecommentLayout=(RelativeLayout) findViewById(R.id.re_related_layout); 
	 
		
		lv_video_list = (NoScrollListView)findViewById(R.id.lv_video_list);

		
		iv_alert_icon = (ImageView) findViewById(R.id.iv_alert_icon);
		iv_channel_icon = (com.star.ui.ImageView) findViewById(R.id.iv_channel_icon);
		ivPlayBtn = (ImageView) findViewById(R.id.iv_epg_play_btn);
		ivPlayBtn.setVisibility(View.GONE);
		ivPlayBtn.setOnClickListener(playClick);
	 
		findViewById(R.id.iv_share_icon).setOnClickListener(new ShareOnClick());
		findViewById(R.id.iv_alert_icon).setOnClickListener(new AlertOnClick());
		findViewById(R.id.iv_comment_icon).setOnClickListener(new CommentOnClick());
		rl_comment_layout.setOnClickListener(new AllCommentOnClick());
		findViewById(R.id.iv_actionbar_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		comList.setOnFaceClick(new OnFaceClick() {
			
			@Override
			public void click(int height) {
				Handler hand=new Handler();
				hand.post(new Runnable() {
					
					@Override
					public void run() {
 				((ScrollView) findViewById(R.id.sv_epg_detail)).fullScroll(ScrollView.FOCUS_DOWN);
					}
				});
			}
		});
//		comList.setEpgDetailFragment(this);
		
//		updateUI();
		CommonUtil.showProgressDialog(EpgDetailActivity.this);
		currentIntent(getIntent());
		
	}
 
	private void initData() {
		new LoadingDataTask() {
			
			@Override
			public void onPreExecute() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPostExecute() {
				CommonUtil.closeProgressDialog();
				// TODO Auto-generated method stub
				if(mRecommentsVideo.size()<4){
					RecommentLayout.setVisibility(View.GONE);
				}else{
					mVideoPlayAdapter = new NewVideoAdapter(EpgDetailActivity.this, mRecommentsVideo);
					lv_video_list.setAdapter(mVideoPlayAdapter);
				}
			}
			
			@Override
			public void doInBackground() {
			// TODO Auto-generated method stub
			//mRecommentsVideo=videoService.getRecommendVideo(channel.getId(), Offset, COUNT, channel.getType(),false);
			mRecommentsVideo=videoService.getChannelByCannelID(channel.getId(), Offset,COUNT);
			
			}
		}.execute();
	 
	}
 	@Override
	protected void onRestart() {
		super.onRestart();
		if(program != null){
			setCurrentProgram(program.getId());
			comList.setCurrentProgram(program);
			comList.getListAdapter().notifyDataSetChanged();
		}
		 
	}
 	 
	private void updateUI() {
		if (program == null) {	
			return;
		}
		
		if(program.getType() == Program.LIVE) {
			setPlayStatus(true);
		}else {
			setPlayStatus(false);
		}
	 
		try{
			iv_epg_poster.setUrl(program.getContents().get(0).getResources().get(0).getUrl());
		}catch (Exception e) {
		}
		updateAlertIconStatus();
		if(program.getClassification()==null||"NA".equals(program.getClassification())||"".equals(program.getClassification())) {
			class_linear.setVisibility(View.GONE);
		}else {
			class_linear.setVisibility(View.VISIBLE);
			epg_class.setText(program.getClassification());
		}
		if(program.getSubhead()==null||"NA".equals(program.getSubhead())||"".equals(program.getSubhead())) {
			subhead_linear.setVisibility(View.GONE);
		} else {
			subhead_linear.setVisibility(View.VISIBLE);
			epg_subhead.setText(program.getSubhead());
		}
		if(program.getDescription()==null||"NA".equals(program.getDescription())||"".equals(program.getDescription())) {
			des_linear.setVisibility(View.GONE);
		}else {
			des_linear.setVisibility(View.VISIBLE);
			epg_descrip.setText(program.getDescription());
		}
		if(program.getChannelId()==null){
			Channl_linear.setVisibility(View.GONE);
		}else{
			Channl_linear.setVisibility(View.VISIBLE);
			epg_number.setText(Long.toString(program.getChannelId()));
			
		}
		tv_epg_name.setText(program.getName());
		tvEpgStartDate.setText(DateFormat.formatMonth(program.getStartDate()));
		tv_epg_startime.setText(Constant.format.format(program.getStartDate())+"-"+Constant.format.format(program.getEndDate()));
		comList.setCurrentProgram(program);
		comList.getListAdapter().notifyDataSetChanged();
		executeChannelInfoTask();
	}
	
	public void updateCommentCount(){
		/*new HTTPInvoker<Long>() {
			@Override
			public void onFail(int statusLine) {
				ToastUtil.centerShowToast(EpgDetailActivity.this, "Fail to search the comment count.");
			}
			@Override
			public void onSuccess(Long dataReturn) {
				program.setCommentCount(dataReturn);
				updateCommentCountForDB();
			}
			public RequestHandle http(){
				return comnService.getCommentCountByProgramId(this,program.getId());
			}
		}.go();*/
		comnService.getCommentCountByProgramId(program.getId(), new OnResultListener<Long>() {

			@Override
			public boolean onIntercept() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onSuccess(Long dataReturn) {
				program.setCommentCount(dataReturn);
				updateCommentCountForDB();
			}

			@Override
			public void onFailure(int errorCode, String msg) {
				ToastUtil.centerShowToast(EpgDetailActivity.this, "Fail to search the comment count.");
			}
		});
	}
	
	private void updateCommentCountForDB() {
		/*
		new HTTPInvoker<Long>() {
			@Override
			public void onFail(int statusLine) {
				ToastUtil.centerShowToast(EpgDetailActivity.this, "Fail to search the comment count of channel.");
			}
			@Override
			public void onSuccess(Long dataReturn) {
				programService.updateCommentCount(program);
				if(channel!=null){
					channel.setCommentCount(dataReturn);
				}else{
					channel = new ChannelVO();
					channel.setId(program.getChannelId());
					channel.setCommentCount(dataReturn);
				}
				channelService.updateCommentCount(channel);
			}
			public RequestHandle http(){
				return comnService.getCommentCountByChannelId(this,program.getChannelId());
			}
		}.go();
		*/
		comnService.getCommentCountByChannelId(program.getChannelId(), new OnResultListener<Long>() {

			@Override
			public boolean onIntercept() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onSuccess(Long dataReturn) {
				programService.updateCommentCount(program);
				if(channel!=null){
					channel.setCommentCount(dataReturn);
				}else{
					channel = new ChannelVO();
					channel.setId(program.getChannelId());
					channel.setCommentCount(dataReturn);
				}
				channelService.updateCommentCount(channel);
			}

			@Override
			public void onFailure(int errorCode, String msg) {
				ToastUtil.centerShowToast(EpgDetailActivity.this, "Fail to search the comment count of channel.");
			}
			
		});
	}
	
	public void executeExploreTask(){
		new LoadingDataTask() {
			List<ChannelVO> exploreChannels;
			@Override
			public void onPreExecute() {
			}
			
			@Override
			public void onPostExecute() {
				try{
				 
				}catch(Exception e){
				}
			}
			
			@Override
			public void doInBackground() {
				try{
					exploreChannels = channelService.getChannels(channel.getCategories().get(0).getId(), OrderType.HOT, 0, 2);
				}catch(Exception e){
					exploreChannels = new ArrayList<ChannelVO>();
				}
//				exploreChannels = chnService.getChannels(3, OrderType.HOT, 0, 2);
			}
		}.execute();
	}
	
	public void executeChannelInfoTask(){
		new LoadingDataTask() {
			@Override
			public void onPreExecute() {
			}
			
			@Override
			public void onPostExecute() {
				if(channel == null)
					return;
				try{
					iv_channel_icon.setUrl(channel.getLogo().getResources().get(0).getUrl());
					iv_channel_icon.setOnClickListener(mChannelIconClickListener);
					initData(); 
				}catch (Exception e) {
					iv_channel_icon.setVisibility(View.INVISIBLE);
				}
				executeExploreTask();
			}
			
			@Override
			public void doInBackground() {
				channel = channelService.getChannelById(program.getChannelId());
			}
		}.execute();
	}
	
	private OnClickListener mChannelIconClickListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(EpgDetailActivity.this,BaseFragmentActivity.class);
			intent.putExtra("channel", channel);
			CommonUtil.startActivity(EpgDetailActivity.this, intent);
			
			//TODO
//			if(channel != null){
//				Fragment fragment = homeActivity.setFragmentByTag(EpgDetailActivity.this.getResources().getString(R.string.fragment_tag_channelGuide));
//				if (fragment != null){
//					((ChannelGuideFragment) fragment).setCurrentChannel(channel);
//				}
//			}
		}
	};
	
	public void setCurrentProgram(ProgramVO program) {
		this.program = program;
	}
	
	public void setPlayStatus(boolean status) {
		this.playStatus = status;
		if(ivPlayBtn != null){
			if(status)
				ivPlayBtn.setVisibility(View.VISIBLE);
			 else 
				ivPlayBtn.setVisibility(View.GONE);
		}
	}
	
	public void setCurrentProgram(final long programID) {
		setCurrentProgram(programID,false);
	}
	
	public void setCurrentProgram(final long programID,boolean isShowAlert) {
		new LoadingDataTask() {
			ProgramVO p;
			@Override
			public void onPreExecute() {
			}
			
			@Override
			public void onPostExecute() {
				
				if(p!=null){
					if(program!=null){
						program.setDescription(p.getDescription());
						program.setContents(p.getContents());
						program.setCommentCount(p.getCommentCount());
						program.setSubhead(p.getSubhead());
						program.setClassification(p.getClassification());
						updateCommentCountForDB();
						if(!program.isIsFav())
							program.setFavCount(p.getFavCount());
					}else{
						program = p;
						programService.compareProgram(EpgDetailActivity.this, program);
					}
				}
				updateUI();
			}
			
			@Override
			public void doInBackground() {
				p = programService.getEpgDetailByIdFromServer(programID);
			}
		}.execute();
	}
	
	private void updateAlertIconStatus() {
		boolean isFav = false;
		if(program!=null)
			isFav = program.isIsFav();
		if(isFav){
			iv_alert_icon.setImageResource(R.drawable.ic_access_alarms_blue_24dp);
		}else{
			iv_alert_icon.setImageResource(R.drawable.ic_access_alarms_grey_24dp);	
		}
	}
	
	private class AlertOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			if(program == null) return;
			if(program.getStartDate().getTime()<System.currentTimeMillis()){
				boolean fav = program.isIsFav();
				if(fav){
					ToastUtil.centerShowToast(EpgDetailActivity.this,getString(R.string.cannot_cancel_alert));
				}else{
					ToastUtil.centerShowToast(EpgDetailActivity.this, getString(R.string.cannot_alert));
				}
				return;
			}
			new LoadingDataTask() {
				boolean favStatus;
				boolean isFav;
				@Override
				public void onPreExecute() {
					isFav = program.isIsFav();
				}

				@Override
				public void onPostExecute() {
					if(favStatus){
						if(isFav){
							ToastUtil.centerShowToast(EpgDetailActivity.this, getString(R.string.alert_success));
							ToastUtil.showToast(EpgDetailActivity.this, ""+program.getId());
							GA.sendEvent("Reminder", "Reminder_detail", ""+program.getId(), 1);
							taskService.doTask(TaskCode.Book_program,ApplicationUtil.getAppVerison(EpgDetailActivity.this));
						}else{
							ToastUtil.centerShowToast(EpgDetailActivity.this, getString(R.string.alert_cancel_success));
						}
						AlertManager.getInstance(EpgDetailActivity.this).startAlertTimer();
						updateAlertIconStatus();
						
						EggAppearService.appearEgg(EpgDetailActivity.this, EggAppearService.Program_book);
					}else{
						rechangeFavStatus();
					}
					CommonUtil.closeProgressDialog();
				}

				private void rechangeFavStatus() {
					isFav = !isFav;
					program.setIsFav(isFav);
					long favCount = isFav ? program.getFavCount()+1 : program.getFavCount()-1;
					if(favCount<0)
						favCount = 0;
					program.setFavCount(favCount);
				}
				
				@Override
				public void doInBackground() {
					rechangeFavStatus();
					favStatus = programService.updateFavStatus(program);
					if(isFav) {
						TenbService tenbService = new TenbService(EpgDetailActivity.this);
						tenbService.doTenbData(Tenb.TENB_FAV_EPG, program.getId());
					}
				}
			}.execute();
		}
		
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		currentIntent(intent);
	}
	
	private void currentIntent(Intent intent) {
		long programId = -1;
		try{
			programId = Long.parseLong(intent.getStringExtra("programId"));
		}catch (Exception e) {
			programId = intent.getLongExtra("programId", -1);
		}
		if(programId == -1) {
			return;
		}
		setCurrentProgram(programId);
	}
	
	private class CommentOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			((ScrollView) findViewById(R.id.sv_epg_detail)).smoothScrollTo(0, rl_comment_layout.getTop());
		}
		
	}
	
	private class AllCommentOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			if(program == null)
				return;
			Intent intent = new Intent();
			intent.setClass(EpgDetailActivity.this,  AllCommentActivity.class);
			intent.putExtra("programId", program.getId());
			CommonUtil.startActivity(EpgDetailActivity.this, intent);
		}
		
	}
	
	private class ShareOnClick implements OnClickListener{

		@Override
		public void onClick(View v) {
			if(program==null){
				return ;
			}else{
				
				StringBuffer text = new StringBuffer();
				text.append(getResources().getString(R.string.share_text));
				try {
					text.append(getString(R.string.share_program)+program.getName()+getString(R.string.share_channel)+channel.getChannelNumber()+"-"
							+channel.getName()+"\n"+DateFormat.formatTuesday(program.getStartDate())+"\n"+ DateFormat.formatTime(program.getStartDate()) 
							+ "-"+DateFormat.formatTime(program.getEndDate())+"\n"+getString(R.string.share_download)+"\n"+"www.startimestv.com");
				} catch (Exception e) {
					Log.e("TAG", "Data format error",e);
					try {
						text.append(getString(R.string.share_program)+program.getName()+"\n"+DateFormat.formatTuesday(program.getStartDate())+"\n"+ DateFormat.formatTime(program.getStartDate()) 
								+ "-"+DateFormat.formatTime(program.getEndDate())+"\n"+getString(R.string.share_download)+"\n"+"www.startimestv.com");
					} catch (Exception e2) {
						text.append(getString(R.string.share_from_startime));
					}
				}
				String url;
				try{
					url = channel.getLogo().getResources().get(0).getUrl();
				}catch (Exception e) {
					url = "http://tenbre.me/portal/img/shonngo_logo.png";
				}
				ShareUtil.showShare(EpgDetailActivity.this,text.toString(),url, getString(R.string.share_from_tenbre),program);
				ToastUtil.showToast(EpgDetailActivity.this, channel.getName()+"_"+program.getId());
				GA.sendEvent("Program", "Program_share",channel.getName()+"_"+program.getId() , 1);
			}
			
		}
	}
	
	
	
	private OnClickListener playClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			//			Intent intent = new Intent(getActivity(),LandscapePalyActivity.class);
			//			intent.putExtra("uri", channel.getLiveURL());
			//			intent.putExtra("epgname", program.getName());
			//			intent.putExtra("islive", true);
			//			intent.putExtra("startTime", DateFormat.formatDateTime(program.getStartDate()));
			//			intent.putExtra("endTime", DateFormat.formatDateTime(program.getEndDate()));
			//			CommonUtil.startActivity(getActivity(), intent);


			if(program == null) return;
			try{
				long currentTime = System.currentTimeMillis();
				if(IOUtil.sysTime!=null && Math.abs(IOUtil.sysTime-currentTime)/(1000*60)>2){
					currentTime = IOUtil.sysTime;
				}
				if(program.getStartDate().getTime() > currentTime) {
					ToastUtil.centerShowToast(EpgDetailActivity.this,  getResources().getString(R.string.live_video_not_yet_begin) + " " + DateFormat.formatMonth(program.getStartDate()) + " "  + Constant.format.format(program.getStartDate()));
				}else if(program.getEndDate().getTime() < currentTime){
					ToastUtil.centerShowToast(EpgDetailActivity.this,  getResources().getString(R.string.live_video_ended));
				}else {
//					videoService.play(program.getContents().get(0).getResources().get(1).getUrl(), program.getName(), program.getContents().get(0).getId(), channel, true);
					Intent intent = new Intent(EpgDetailActivity.this, Player.class);
					intent.putExtra("filename",program.getContents().get(0).getResources().get(1).getUrl());
					intent.putExtra("epgname",program.getName());
					intent.putExtra("isLive", "true");
					intent.putExtra("dejia", "1");
					videoService.takeCount(program.getContents().get(0).getId());
					startActivity(intent);
				}
			}
			catch (Exception e) {
			}
		}
	};

}
