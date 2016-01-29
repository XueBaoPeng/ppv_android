package com.star.mobile.video.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.libsdl.app.Player;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.RequestHandle;
import com.star.cms.model.Content;
import com.star.cms.model.Resource;
import com.star.cms.model.Tenb;
import com.star.cms.model.vo.ChannelVO;
import com.star.cms.model.vo.ProgramVO;
import com.star.cms.model.vo.VOD;
import com.star.mobile.video.R;
import com.star.mobile.video.activity.ChannelCommentActivity;
import com.star.mobile.video.activity.ChatActivity;
import com.star.mobile.video.adapter.VideoHorizontalListViewAdapter;
import com.star.mobile.video.base.BaseFragmentActivity;
import com.star.mobile.video.dialog.SyncDialog;
import com.star.mobile.video.service.ChannelService;
import com.star.mobile.video.service.CommentService_old;
import com.star.mobile.video.service.ProgramService;
import com.star.mobile.video.service.SyncService;
import com.star.mobile.video.service.VideoService;
import com.star.mobile.video.tenb.TenbService;
import com.star.mobile.video.util.BitmapUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.DateFormat;
import com.star.mobile.video.util.DensityUtil;
import com.star.mobile.video.util.LoadingDataTask;
import com.star.mobile.video.util.http.HTTPInvoker;
import com.star.ui.ImageView.Finisher;

public class ChannelDetailLayout extends LinearLayout implements OnClickListener{
	public static final String TAG = ChannelDetailLayout.class.getSimpleName();
	private TextView tv_channelName;
	private TextView tv_fav_count;
	private TextView tv_comment_count;
	private BaseFragmentActivity homeActivity;
	private TextView tv_channelNum;
	private ImageView iv_fav_icon;
	private ChannelVO mChannel;
	private ImageView downBtn;
	private View loadingBar;
	private ProgramService epgService;
	private ChannelService chnService;
	private com.star.ui.ImageView iv_channelIcon;
	private com.star.ui.ImageView iv_categoryIcon;
	private LinearLayout ll_channel_video;
	
	private MyHorizontalListView mChannelGuideHorizontalListView;
	private View mLoadingView;
	private List<VOD> mVideoContent = new ArrayList<VOD>();
	private TextView tv_packageName;
	private TextView tv_categoryName;
	private TextView tv_chnDescription;
	private Bitmap coverBitmap;
	private CommentService_old comnSerivce;
	private VideoService videoSerivce;
	private String video_url_one;
	private String video_name_one;
	private String video_name_two;
	private String video_name_three;
	private String video_url_two;
	private String video_url_three;
	private Long contentId_one;
	private Long contentId_two;
	private Long contentId_three;
	
	private boolean isLoading;//加载状态
	private int mOffset = 0;//获取数据的偏移位置
	private int mResponsSize = 0;//获得的数据量
	private int COUNT = 20;
	public ChannelDetailLayout(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.view_channel_detail, this);
		initView();
		homeActivity = (BaseFragmentActivity)context;
		epgService = new ProgramService(context);
		chnService = new ChannelService(context);
		comnSerivce = new CommentService_old(context);
		videoSerivce=new VideoService(context);
		initData();
	}

	private void initView() {
		iv_channelIcon = (com.star.ui.ImageView) findViewById(R.id.iv_channel_icon);
		tv_channelNum = (TextView) findViewById(R.id.tv_channel_number);
		tv_channelName = (TextView) findViewById(R.id.tv_channel_name);
		tv_comment_count = (TextView) findViewById(R.id.tv_comment_count);
		tv_chnDescription = (TextView) findViewById(R.id.tv_chn_description);
		tv_fav_count = (TextView) findViewById(R.id.tv_fav_count);
		iv_fav_icon = (ImageView) findViewById(R.id.iv_fav_icon);
		tv_packageName = (TextView) findViewById(R.id.tv_package_name);
		tv_categoryName = (TextView) findViewById(R.id.tv_category_name);
		iv_categoryIcon = (com.star.ui.ImageView) findViewById(R.id.iv_category_icon);
		ll_channel_video=(LinearLayout)findViewById(R.id.ll_channel_video);
		
		mChannelGuideHorizontalListView = (MyHorizontalListView)findViewById(R.id.channel_vedio_horizontal_listview);
		mLoadingView = findViewById(R.id.loadingView);
//		mChannelGuideHorizontalListView.setOnScrollListener(new MyOnscrollListener());
		int itemWidth = (Constant.WINDOW_WIDTH -3*DensityUtil.dip2px(getContext(), 6)- 2*DensityUtil.dip2px(getContext(), 10))/2;
		android.view.ViewGroup.LayoutParams params =  mChannelGuideHorizontalListView.getLayoutParams();
		params.height = itemWidth*9/16;
		mChannelGuideHorizontalListView.setLayoutParams(params);
		
		findViewById(R.id.ll_fav).setOnClickListener(this);
		findViewById(R.id.ll_comment).setOnClickListener(this);
		findViewById(R.id.ll_chatroom).setOnClickListener(this);
		downBtn = (ImageView)findViewById(R.id.iv_actionbar_download);
		loadingBar = findViewById(R.id.pb_loading);
		coverBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.gray_cover);
	}
	/**
	 * 初始化数据
	 */
	private void initData(){
		mChannelGuideAdapter = new VideoHorizontalListViewAdapter(homeActivity, mVideoContent); 
		mChannelGuideHorizontalListView.setAdapter(mChannelGuideAdapter);
		mChannelGuideHorizontalListView.setOnItemClickListener(new MyOnItemClickListener());
	}
	
	/**
	 * 横向listview点击item的事件
	 * @author Lee
	 *
	 */
	private class MyOnItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (mVideoContent!=null && mVideoContent.size() > 0) {
				transferHorizontalListView(position);
			}
		}

	}
	/**
	 * 点击item播放视频
	 * @param position
	 */
	private void transferHorizontalListView(int position) {
		VOD vod = mVideoContent.get(position);
		Content video=vod.getVideo();
		List<Resource> resvideo=video.getResources();
		
		Intent intent = new Intent(homeActivity,Player.class);
		intent.putExtra("videocontent", (Serializable)mVideoContent);
		intent.putExtra("position", position);
		intent.putExtra("channel", mChannel);
		intent.putExtra("filename",resvideo.get(0).getUrl() );
		intent.putExtra("epgname", vod.getName());
		homeActivity.startActivity(intent);
	}
	
	public void setCurrentChannel(ChannelVO channel){
		mVideoContent.clear();
		mChannelGuideAdapter.setmChannelVideoItemDomain(mVideoContent);
		mOffset = 0;
		mChannel = channel;
		updateUI();
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.ll_fav:
			onClickFavArea();
			break;
		case R.id.ll_comment:
			intent = new Intent();
			intent.setClass(homeActivity,  ChannelCommentActivity.class);
			intent.putExtra("channel", mChannel);
			CommonUtil.startActivity(homeActivity, intent);
			break;
		case R.id.rl_download:
			if(downBtn.getVisibility()==View.INVISIBLE || SyncService.getInstance(homeActivity).isLoading()){
				return;
			}
			showSyncDialog();
			break;
		case R.id.ll_chatroom:
			intent = new Intent();
			intent.setClass(homeActivity,  ChatActivity.class);
			intent.putExtra("channelId", mChannel.getId()+"");
			intent.putExtra("chatRoom", mChannel.getName());
			CommonUtil.startActivity(homeActivity, intent);
			break;
		case R.id.rl_video_one:
			videoSerivce.play(video_url_one, video_name_one, contentId_one, mChannel);
			break;
		case R.id.rl_video_two:
			videoSerivce.play(video_url_two, video_name_two, contentId_two, mChannel);
			break;
		case R.id.rl_video_three:
			videoSerivce.play(video_url_three, video_name_three, contentId_three, mChannel);
			break;
		default:
			break;
		}
	}
	
	private void updateUI(){
		if(mChannel == null)
			return;
		tv_channelName.setText(mChannel.getName());
		tv_channelNum.setText(String.valueOf(mChannel.getChannelNumber()));
		getChannelCommentCount();
		if(mChannel.getDescription()!=null){
			tv_chnDescription.setText(mChannel.getDescription());
			findViewById(R.id.ll_channel_description).setVisibility(View.VISIBLE);
		}
		try{
			String logoUrl = mChannel.getLogo().getResources().get(0).getUrl();
			iv_channelIcon.setUrl(logoUrl);
		}catch(Exception e){
		}
		if(mChannel.getOfPackage()!=null)
			tv_packageName.setText(mChannel.getOfPackage().getName());
		if(mChannel.getCategories()==null || mChannel.getCategories().size()==0)
			getCategorys();
		else
			setCategroyInfo();
		updateFavIconStatus();
//		iv_video_one.setImageResource(R.drawable.twitter_bg);
//		iv_video_one.setUrl("http://218.205.169.218:8581/portal/img/video_image/th.jpg");
		getChannelVideo();
	}
	
	private void getChannelVideo() {
		new LoadingDataTask() {
			private List<VOD> content;
			@Override
			public void onPreExecute() {
				mLoadingView.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void onPostExecute() {
				isLoading = false;
				if(content==null || content.size()==0){
					if(mVideoContent.size()==0)
						ll_channel_video.setVisibility(View.GONE);
				}else{
					mVideoContent.addAll(content);
					mResponsSize = content.size();
					mChannelGuideAdapter.setmChannelVideoItemDomain(mVideoContent);
					ll_channel_video.setVisibility(View.VISIBLE);
				}
				mLoadingView.setVisibility(View.GONE);
			}
	
			@Override
			public void doInBackground() {
				isLoading = true;
//				mVideoContent = videoSerivce.getChannelVideos(Long.toString(mChannel.getId()));
				//分页获取数据
//				content = videoSerivce.getChannelVideos(Long.toString(mChannel.getId()),mOffset,20);
				content = videoSerivce.getRecommendVideo(mChannel.getId(), mOffset, COUNT, mChannel.getType());
			}
		}.execute();	
	}

	private void getChannelCommentCount(){
//		new LoadingDataTask() {
//			private long count;
//			@Override
//			public void onPreExecute() {
//			}
//			
//			@Override
//			public void onPostExecute() {
//				if(count != 0){
//					mChannel.setCommentCount(count);
//				}
//				tv_comment_count.setText(String.valueOf(mChannel.getCommentCount()==null?0:mChannel.getCommentCount()));
//			}
//
//			@Override
//			public void doInBackground() {
//				count = comnSerivce.getCommentCountByChannelId(mChannel.getId());
//			}
//		}.execute();
		
		new HTTPInvoker<Long>(){
			@Override
			public void onFail(int statusLine) {
			}

			@Override
			public void onSuccess(Long dataReturn) {
				mChannel.setCommentCount(dataReturn);
				tv_comment_count.setText(String.valueOf(mChannel.getCommentCount()==null?0:mChannel.getCommentCount()));
			}

			@Override
			public RequestHandle http() {
				return comnSerivce.getCommentCountByChannelId(this,mChannel.getId());
			}
			
		}.go();
	}
	
	private void setCategroyInfo() {
		if(mChannel.getCategories()==null||mChannel.getCategories().size()==0)
			return;
		try{
			tv_categoryName.setText(mChannel.getCategories().get(0).getName());
		}catch(Exception e){
		}
		try{
			iv_categoryIcon.setFinisher(new Finisher() {
				@Override
				public void run() {
					iv_categoryIcon.setImageBitmap(BitmapUtil.toConformBitmap(iv_categoryIcon.getImage(), coverBitmap));
				}
			});
			iv_categoryIcon.setUrl(mChannel.getCategories().get(0).getLogo().getResources().get(0).getUrl());
		}catch(Exception e){
		}
	}
	
	private void getCategorys(){
		new LoadingDataTask() {
			private ChannelVO vo;
			@Override
			public void onPreExecute() {
			}
			
			@Override
			public void onPostExecute() {
				if(vo!=null){
					mChannel = vo;
					setCategroyInfo();
				}
			}

			@Override
			public void doInBackground() {
				vo = chnService.getChannelById(mChannel.getId());
			}
		}.execute();
	}
	
	private void showSyncDialog() {
		if(mChannel == null)
			return;
		new LoadingDataTask() {
			int day;
			ProgramVO vo;
			@Override
			public void onPreExecute() {
				CommonUtil.showProgressDialog(homeActivity);
			}
			
			@Override
			public void onPostExecute() {
				CommonUtil.closeProgressDialog();
				final SyncDialog dialog = SyncDialog.getInstance(homeActivity);
				dialog.setDialogContent(day, mChannel, vo);
				dialog.setButtonOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						if(day>0){
							SyncService.getInstance(homeActivity).setLoading(true);
							downBtn.setVisibility(View.INVISIBLE);
							loadingBar.setVisibility(View.VISIBLE);
							if(vo==null){
								epgService.initPrograms(mChannel.getId(),
										DateFormat.getZeroTimeOfDate(new Date()).getTime(),
										DateFormat.getZeroTimeOfDate(new Date()).getTime()+((day+1)*1000*60*60*24L-1), handler);
							}else{
								epgService.initPrograms(mChannel.getId(),
										DateFormat.getZeroTimeOfDate(vo.getStartDate()).getTime(),
										DateFormat.getZeroTimeOfDate(vo.getStartDate()).getTime()+((day+1)*1000*60*60*24L-1), handler);
							}
						}else if(day == 0 && vo == null){
							SyncService.getInstance(homeActivity).setLoading(true);
							downBtn.setVisibility(View.INVISIBLE);
							loadingBar.setVisibility(View.VISIBLE);
							epgService.initPrograms(mChannel.getId(),
									DateFormat.getZeroTimeOfDate(new Date()).getTime(),
									DateFormat.getZeroTimeOfDate(new Date()).getTime()+((day+1)*1000*60*60*24L-1), handler);
						}
					}
				});
				dialog.show();
			}
			
			@Override
			public void doInBackground() {
				vo =  epgService.getLastEpgFromLocal(mChannel.getId());
				day = getNeedSyncDays(mChannel.getId(), vo);
			}
		}.execute();
	}
	
	private int getNeedSyncDays(long channelId, ProgramVO program){
		Date local = new Date();
		int days = 0;
		if(program!=null){
			days = DateFormat.getDiffDays(local, program.getStartDate());
			if(days>=6)
				return 0;
			local = program.getStartDate();
		}
		long online = epgService.getLastEpgOnline(channelId);
		if(online==-1)
			return -1;
		int syncDays = DateFormat.getDiffDays(local, new Date(online));
		if((6-days)>syncDays){
			return syncDays;
		}else{
			return 6-days;
		}
	}

	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			SyncService.getInstance(homeActivity).setLoading(false);
			setSyncIconResid(R.drawable.icon_download_finish);
			downBtn.setVisibility(View.VISIBLE);
			loadingBar.setVisibility(View.GONE);
		};
	};
	private VideoHorizontalListViewAdapter mChannelGuideAdapter;
	
	private void updateFavIconStatus() {
		boolean isFav = mChannel.isFav();
		tv_fav_count.setText(String.valueOf(mChannel.getFavCount()));
		if(isFav){
			iv_fav_icon.setImageResource(R.drawable.icon_channel_guide_favourite);
		}else{
			iv_fav_icon.setImageResource(R.drawable.icon_channel_guide_unfavourite);	
		}
	}
	
	private void onClickFavArea() {
		new LoadingDataTask() {
			boolean favStatus;
			@Override
			public void onPreExecute() {
			}

			@Override
			public void onPostExecute() {
				if(favStatus){
					updateFavIconStatus();
				}else{
					rechangeFavStatus();
				}
			}

			private void rechangeFavStatus() {
				boolean isFav = mChannel.isFav();
				mChannel.setFav(!isFav);
				long favCount = isFav ? mChannel.getFavCount()-1 : mChannel.getFavCount()+1;
				if(favCount<0)
					favCount = 0;
				mChannel.setFavCount(favCount);
			}

			@Override
			public void doInBackground() {
				rechangeFavStatus();
				favStatus = chnService.updateChannel(mChannel);
				if(mChannel.isFav()){
					TenbService tenbService = new TenbService(getContext());
					tenbService.doTenbData(Tenb.TENB_FAV_CHANNEL, mChannel.getId());
				}
			}
		}.execute();
	}
	
	public void setSyncIconResid(int resId){
		if(SyncService.getInstance(homeActivity).isLoading()){
			return;
		}
		if(resId == R.drawable.icon_download_no_epg){
			downBtn.setImageResource(resId);
			findViewById(R.id.rl_download).setOnClickListener(null);
		}else{
			if(epgService.hasOutlineEPGs(mChannel.getId())){
				downBtn.setImageResource(R.drawable.icon_download_finish);
			}else{
				downBtn.setImageResource(R.drawable.icon_download);
			}
			findViewById(R.id.rl_download).setOnClickListener(this);
		}
	}

}
