package com.star.mobile.video.fragment;

import java.util.ArrayList;
import java.util.List;

import org.libsdl.app.Player;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.loopj.android.http.RequestHandle;
import com.star.cms.model.Content;
import com.star.cms.model.Program;
import com.star.cms.model.Task.TaskCode;
import com.star.cms.model.Tenb;
import com.star.cms.model.dto.DoTaskResult;
import com.star.cms.model.enm.OrderType;
import com.star.cms.model.vo.ChannelVO;
import com.star.cms.model.vo.ProgramVO;
import com.star.cms.model.vo.VOD;
import com.star.mobile.video.AlertManager;
import com.star.mobile.video.R;
import com.star.mobile.video.base.BaseFragment;
import com.star.mobile.video.channel.NewVideoAdapter;
import com.star.mobile.video.channel.VideoAdapter;
import com.star.mobile.video.epg.AllCommentActivity;
import com.star.mobile.video.epg.CommentListView;
import com.star.mobile.video.epg.EpgDetailActivity;
import com.star.mobile.video.me.mycoins.TaskService;
import com.star.mobile.video.service.CommentService_old;
import com.star.mobile.video.service.EggAppearService;
import com.star.mobile.video.service.VideoService;
import com.star.mobile.video.tenb.TenbService;
import com.star.mobile.video.util.ApplicationUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.DateFormat;
import com.star.mobile.video.util.IOUtil;
import com.star.mobile.video.util.LoadingDataTask;
import com.star.mobile.video.util.ShareUtil;
import com.star.mobile.video.util.ToastUtil;
import com.star.mobile.video.util.http.HTTPInvoker;
import com.star.mobile.video.view.ClipTextView;
import com.star.mobile.video.view.EpgOnAlertItemView;
import com.star.mobile.video.view.NoScrollListView;
import com.star.util.loader.OnResultListener;

public class EpgDetailFragment extends BaseFragment {

	private View mView;
	private ProgramVO program;
	private ChannelVO channel;
	private Activity homeActivity;
	private CommentService_old comnService;
	private TaskService taskService;
	private TenbService tenbService;
	private VideoService videoService;
	private com.star.ui.ImageView iv_channel_icon;
	private TextView tv_comment_count;
	private TextView tvEpgStartDate;
	private RelativeLayout rl_comment_layout;
	private ImageView iv_alert_icon;
	private com.star.ui.ImageView iv_epg_poster;
	private TextView epg_number;
	private TextView epg_descrip;
	private TextView epg_subhead;
	private TextView epg_class;
	private TextView tv_epg_name;
	private TextView tv_epg_startime;
	private CommentListView comList;
	private ImageView ivPlayBtn;
	private boolean playStatus;
	
	
	private RelativeLayout  re_related_layout;
	private NoScrollListView lv_video_list;
	private NewVideoAdapter mVideoPlayAdapter;
	private List<VOD> mRecommentsVideo = new ArrayList<VOD>();// 推荐视频
	private int Offset = 0;
	private int COUNT = 4;
	
	private LinearLayout Channl_linear;
	private LinearLayout class_linear;
	private LinearLayout subhead_linear;
	private LinearLayout des_linear;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (mView != null) {
			ViewGroup parent = (ViewGroup) mView.getParent();
			if (parent != null) {
				parent.removeView(mView);
			}
			return mView;
		}
		homeActivity = getActivity();
		mView = inflater.inflate(R.layout.activity_epg_detail, null);
		
		comnService = new CommentService_old(getActivity());
		videoService = new VideoService(getActivity());
		taskService = new TaskService(getActivity());
		tenbService = new TenbService(getActivity());
		initView();
		EggAppearService.appearEgg(getActivity(), EggAppearService.Program_in);
		((ScrollView)mView).smoothScrollTo(0, 0);
		return mView;
	}

	private void initView() {
		if(Constant.POSTER_HEIGHT != 0){
			RelativeLayout rl_poster_layout = (RelativeLayout) mView.findViewById(R.id.rl_poster_layout);
			ViewGroup.LayoutParams params = rl_poster_layout.getLayoutParams();
			params.height = Constant.POSTER_HEIGHT;
			rl_poster_layout.setLayoutParams(params);
		}
		iv_epg_poster = (com.star.ui.ImageView) mView.findViewById(R.id.iv_epg_poster);
		Channl_linear=(LinearLayout) mView.findViewById(R.id.channel_number);
		class_linear=(LinearLayout)mView.findViewById(R.id.classification_linear);
		subhead_linear=(LinearLayout)mView.findViewById(R.id.subhead_linear);
		des_linear=(LinearLayout)mView.findViewById(R.id.descripion_linear);
		epg_number=(TextView) mView.findViewById(R.id.tv_epg_number);
		epg_descrip = (TextView) mView.findViewById(R.id.tv_epg_descripion);
		epg_class = (TextView) mView.findViewById(R.id.tv_epg_classification);
		epg_subhead = (TextView) mView.findViewById(R.id.tv_epg_subhead);
		tv_comment_count = (TextView) mView.findViewById(R.id.tv_comment_count);
		tv_epg_name = (TextView) mView.findViewById(R.id.tv_epg_name);
		tv_epg_startime = (TextView) mView.findViewById(R.id.tv_epg_startime);
		tvEpgStartDate = (TextView) mView.findViewById(R.id.tv_epg_stardate);
		comList = (CommentListView) mView.findViewById(R.id.epg_comment_list);
		rl_comment_layout = (RelativeLayout) mView.findViewById(R.id.rl_comment_layout);
 
		 
		re_related_layout=(RelativeLayout) mView.findViewById(R.id.re_related_layout);
		lv_video_list = (NoScrollListView)mView.findViewById(R.id.lv_video_list);
		
		iv_alert_icon = (ImageView) mView.findViewById(R.id.iv_alert_icon);
 
		iv_channel_icon = (com.star.ui.ImageView) mView.findViewById(R.id.iv_channel_icon);
		ivPlayBtn = (ImageView) mView.findViewById(R.id.iv_epg_play_btn);
		ivPlayBtn.setVisibility(View.GONE);
		ivPlayBtn.setOnClickListener(playClick);
	 
		
		mView.findViewById(R.id.iv_share_icon).setOnClickListener(new ShareOnClick());
		mView.findViewById(R.id.iv_alert_icon).setOnClickListener(new AlertOnClick());
		mView.findViewById(R.id.iv_comment_icon).setOnClickListener(new CommentOnClick());
		mView.findViewById(R.id.iv_target_icon).setOnClickListener(new AllCommentOnClick());
		comList.setEpgDetailFragment(this);
		
//		updateUI();
	}
	
	/*@Override
	public void onStart() {
		if(program != null){
			setCurrentProgram(program.getId());
			comList.setCurrentProgram(program);
			comList.getListAdapter().notifyDataSetChanged();
		}
		super.onStart();
	}*/
	@Override
	public void onResume() {
		super.onResume();
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
		tv_comment_count.setText(String.valueOf(program.getCommentCount()));
		tvEpgStartDate.setText(DateFormat.formatMonth(program.getStartDate()));
		tv_epg_startime.setText(Constant.format.format(program.getStartDate())+"-"+Constant.format.format(program.getEndDate()));
		comList.setCurrentProgram(program);
		comList.getListAdapter().notifyDataSetChanged();
		executeChannelInfoTask();
	}
	private void initData() {
		new LoadingDataTask() {
			
			@Override
			public void onPreExecute() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPostExecute() {
				if(mRecommentsVideo.size()<4){
					re_related_layout.setVisibility(View.GONE);
				}else{
					mVideoPlayAdapter = new NewVideoAdapter(homeActivity, mRecommentsVideo);
					lv_video_list.setAdapter(mVideoPlayAdapter);
				}
			}
			
			@Override
			public void doInBackground() {
				// TODO Auto-generated method stub
			// mRecommentsVideo=videoService.getRecommendVideo(channel.getId(), Offset, COUNT, channel.getType(),false);
			mRecommentsVideo=videoService.getChannelByCannelID(channel.getId(), Offset,COUNT);
			}
		}.execute();
	 
	}
	public void updateCommentCount(){
//		new LoadingDataTask() {
//			long commentCount=0;
//			@Override
//			public void onPreExecute() {
//				commentCount = program.getCommentCount();
//			}
//			
//			@Override
//			public void onPostExecute() {
//				tv_comment_count.setText(String.valueOf(commentCount));
//				program.setCommentCount(commentCount);
//				updateCommentCountForDB();
//			}
//			
//
//			@Override
//			public void doInBackground() {
//				commentCount = comnService.getCommentCountByProgramId(program.getId());
//			}
//		}.execute();
		new HTTPInvoker<Long>() {
			@Override
			public void onFail(int statusLine) {
				ToastUtil.centerShowToast(EpgDetailFragment.this.getActivity(), "Fail to search the comment count.");
			}
			@Override
			public void onSuccess(Long dataReturn) {
				tv_comment_count.setText(String.valueOf(dataReturn));
				program.setCommentCount(dataReturn);
				updateCommentCountForDB();
			}
			public RequestHandle http(){
				return comnService.getCommentCountByProgramId(this,program.getId());
			}
		}.go();
	}
	
	private void updateCommentCountForDB() {
		new HTTPInvoker<Long>() {
			@Override
			public void onFail(int statusLine) {
				ToastUtil.centerShowToast(EpgDetailFragment.this.getActivity(), "Fail to search the comment count of channel.");
			}
			@Override
			public void onSuccess(Long dataReturn) {
				getProgramService().updateCommentCount(program);
				if(channel!=null){
					channel.setCommentCount(dataReturn);
				}else{
					channel = new ChannelVO();
					channel.setId(program.getChannelId());
					channel.setCommentCount(dataReturn);
				}
				getChannelService().updateCommentCount(channel);
			}
			public RequestHandle http(){
				return comnService.getCommentCountByChannelId(this,program.getChannelId());
			}
		}.go();
		
//		new Thread(){
//			public void run() {
//				epgService.updateCommentCount(program);
//				long count = comnService.getCommentCountByChannelId(program.getChannelId());
//				if(channel!=null){
//					channel.setCommentCount(count);
//				}else{
//					channel = new ChannelVO();
//					channel.setId(program.getChannelId());
//					channel.setCommentCount(count);
//				}
//				chnService.updateCommentCount(channel);
//			};
//		}.start();
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
					exploreChannels = getChannelService().getChannels(channel.getCategories().get(0).getId(), OrderType.HOT, 0, 2);
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
				channel = getChannelService().getChannelById(program.getChannelId());
			}
		}.execute();
	}
	
	private OnClickListener mChannelIconClickListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			if(channel != null){
				CommonUtil.startChannelActivity(homeActivity, channel);
			}
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
						getProgramService().compareProgram(homeActivity, program);
					}
				}
				updateUI();
			}
			
			@Override
			public void doInBackground() {
				p = getProgramService().getEpgDetailByIdFromServer(programID);
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
					ToastUtil.centerShowToast(getActivity(), getActivity().getResources().getString(R.string.cannot_cancel_alert));
				}else{
					ToastUtil.centerShowToast(getActivity(), getActivity().getResources().getString(R.string.cannot_alert));
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
							taskService.doTask(TaskCode.Book_program,ApplicationUtil.getAppVerison(getActivity()), new OnResultListener<DoTaskResult>() {
								
								@Override
								public void onSuccess(DoTaskResult value) {
									taskService.showTaskDialog(homeActivity, value);
								}
								
								@Override
								public boolean onIntercept() {
									// TODO Auto-generated method stub
									return false;
								}
								
								@Override
								public void onFailure(int errorCode, String msg) {
									// TODO Auto-generated method stub
									
								}
							});
							ToastUtil.centerShowToast(homeActivity, getString(R.string.alert_success));
						}else{
							ToastUtil.centerShowToast(homeActivity, getString(R.string.alert_cancel_success));
						}
						AlertManager.getInstance(homeActivity).startAlertTimer();
						updateAlertIconStatus();
						
						EggAppearService.appearEgg(homeActivity, EggAppearService.Program_book);
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
					favStatus = getProgramService().updateFavStatus(program);
					if(isFav) {
						tenbService.doTenbData(Tenb.TENB_FAV_EPG, program.getId());
					}
				}
			}.execute();
		}
		
	}
	
	private class CommentOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			((ScrollView)mView).smoothScrollTo(0, rl_comment_layout.getTop());
		}
		
	}
	
	private class AllCommentOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			if(program == null)
				return;
			Intent intent = new Intent();
			intent.setClass(getActivity(),  AllCommentActivity.class);
			intent.putExtra("programId", program.getId());
			CommonUtil.startActivity(getActivity(), intent);
		}
		
	}
	
	private class ShareOnClick implements OnClickListener{

		@Override
		public void onClick(View v) {
			StringBuffer text = new StringBuffer();
			text.append(getResources().getString(R.string.share_text));
			try {
				text.append(getString(R.string.share_program)+program.getName()+getString(R.string.share_channel)+channel.getChannelNumber()+"-"
						+channel.getName()+"\n"+DateFormat.formatTuesday(program.getStartDate())+"\n"+ DateFormat.formatTime(program.getStartDate()) 
						+ "-"+DateFormat.formatTime(program.getEndDate()));
			} catch (Exception e) {
				Log.e("TAG", "Data format error",e);
				try {
					text.append(getString(R.string.share_program)+program.getName()+"\n"+DateFormat.formatTuesday(program.getStartDate())+"\n"+ DateFormat.formatTime(program.getStartDate()) 
							+ "-"+DateFormat.formatTime(program.getEndDate()));
				} catch (Exception e2) {
					text.append(getString(R.string.share_from_startime));
				}
			}
			
//			Intent intent = new Intent(Intent.ACTION_SEND);
//			intent.setType("image/*");
//			intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
//			intent.putExtra(Intent.EXTRA_TEXT, text);
//			intent.putExtra(Intent.EXTRA_TITLE, program.getName());
//			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			startActivity(Intent.createChooser(intent, "Please choose which to share."));
			
//			Bitmap tv_ico_url;
			String url;
			try{
				url = channel.getLogo().getResources().get(0).getUrl();
//				url =  Environment.getExternalStorageDirectory().getPath()+"/DCIM/Camera/dd.jpg";
//				tv_ico_url = com.star.ui.ImageView.getBitmap(url,80, 80);
			}catch (Exception e) {
				url = "http://tenbre.me/portal/img/shonngo_logo.png";
//				tv_ico_url = BitmapFactory.decodeResource(getResources(), R.drawable.app_icon);
			}
//			WX.sendReq(getActivity(), text, tv_ico_url, "Share From StarGO.",this);
//			ShareUtil.share(getActivity(), "tencent", tv_ico_url, text);
//			text.append("\t http://www.baidu.com");
//			Log.v("TAG", "SHARE"+text.toString());
			ShareUtil.showShare(getActivity(),text.toString(),url, getString(R.string.share_from_tenbre),program);
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
					ToastUtil.centerShowToast(getActivity(),  getResources().getString(R.string.live_video_not_yet_begin) + " " + DateFormat.formatMonth(program.getStartDate()) + " "  + Constant.format.format(program.getStartDate()));
				}else if(program.getEndDate().getTime() < currentTime){
					ToastUtil.centerShowToast(getActivity(),  getResources().getString(R.string.live_video_ended));
				}else {
//					videoService.play(program.getContents().get(0).getResources().get(1).getUrl(), program.getName(), program.getContents().get(0).getId(), channel, true);
					Intent intent = new Intent(getActivity(), Player.class);
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
