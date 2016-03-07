package com.star.mobile.video.tenb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.star.cms.model.*;
import com.star.cms.model.enm.Status;
import com.star.cms.model.enm.TVPlatForm;
import com.star.cms.model.vo.ChannelVO;
import com.star.cms.model.vo.CommentVO;
import com.star.cms.model.vo.ProgramVO;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.activity.BrowserActivity;
import com.star.mobile.video.channel.ChannelRateActivity;
import com.star.mobile.video.service.ChannelService;
import com.star.mobile.video.service.ProgramService;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.DateFormat;
import com.star.mobile.video.util.LoadingDataTask;
import com.star.util.loader.LoadMode;
import com.star.util.loader.OnResultTagListener;

import java.util.List;

public class TenbItemView extends LinearLayout {
	
	private ImageView ivTenbIcon;
	private TextView tvTenbComment;//评分的内容
	private RatingBar ratingBar;//评分的星星数
	private TextView tvTenbDate;
	private TextView tvTenbTitle;
	private FrameLayout flChannelLogoLayout;
	private com.star.ui.ImageView ivChannelLogo;
	private RelativeLayout llOneLive;
	private LinearLayout llTwoLive;
	private LinearLayout llThreeLive;
	private LinearLayout llFourLive;
	private TextView tvChannelDeatil;
	private TextView tvProgramDate;
	private TextView tvTenbDetail;
	
	
	private TextView tv_four_programData;
	private TextView tv_four_programTime;
	private LinearLayout llTenbContent;
	
	private int position;
	private TenbMe tenbMe;
	private Context mContext;
	
	private ChannelService channelService;
	private ProgramService programService;
	private TenbService tenbService;
	private RelativeLayout rlBbsPrz;
	private TextView tvFavCount;
	private TextView tvCmmCount;

	private TextView channel_dtt_number;
	private TextView channel_dth_number;
	private View dtt_layout;
	private View dth_layout;
	private RelativeLayout channel_name;
	private RelativeLayout channel_layout;

	private TextView channel_dtt_number_1;
	private TextView channel_dth_number_1;
	private View dtt_layout_1;
	private View dth_layout_1;
	private RelativeLayout channel_name_1;
	private RelativeLayout channel_layout_1;

	private TextView channel_dtt_number_2;
	private TextView channel_dth_number_2;
	private View dtt_layout_2;
	private View dth_layout_2;
	private RelativeLayout channel_name_2;
	private RelativeLayout channel_layout_2;

	public TenbItemView(Context context) {
		this(context, null);
	}

	public TenbItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		channelService = new ChannelService(context);
		programService = new ProgramService(context);
		tenbService = new TenbService(context);
		
		LayoutInflater.from(context).inflate(R.layout.tenb_item_view, this);
		ivTenbIcon = (ImageView)findViewById(R.id.iv_tenb_icon);
		tvTenbDate = (TextView) findViewById(R.id.tv_tenb_date);
		tvTenbTitle = (TextView) findViewById(R.id.tv_tenb_title);
		flChannelLogoLayout = (FrameLayout) findViewById(R.id.fl_channel_logo);
		ivChannelLogo = (com.star.ui.ImageView) findViewById(R.id.iv_channel_logo);
		llOneLive = (RelativeLayout) findViewById(R.id.ll_one_live);
		llTwoLive = (LinearLayout) findViewById(R.id.ll_two_live);
		llThreeLive = (LinearLayout) findViewById(R.id.ll_three_live);
		llFourLive=(LinearLayout) findViewById(R.id.ll_four_live);
		tvChannelDeatil = (TextView) findViewById(R.id.tv_channel_detail);
		tvProgramDate = (TextView) findViewById(R.id.tv_program_date);
		tvTenbDetail = (TextView) findViewById(R.id.tv_tenb_detail);
		llTenbContent = (LinearLayout) findViewById(R.id.ll_tenb_content);
		rlBbsPrz = (RelativeLayout) findViewById(R.id.rl_bbs_prz);
		tvFavCount = (TextView) findViewById(R.id.tv_fav_count);
		tvCmmCount = (TextView) findViewById(R.id.tv_cmm_count);
		tvTenbComment=(TextView) findViewById(R.id.comment_content);
		ratingBar=(RatingBar) findViewById(R.id.rating_channel);
		tv_four_programData=(TextView) findViewById(R.id.tv_programdata);
		tv_four_programTime=(TextView) findViewById(R.id.tv_program_time);
		channel_dtt_number= (TextView) findViewById(R.id.channel_dtt_number);
		channel_dth_number= (TextView) findViewById(R.id.channel_dth_number);
		dtt_layout = findViewById(R.id.decoder_relativelayout);
		dth_layout = findViewById(R.id.dish_relativelayout);
		channel_name= (RelativeLayout) findViewById(R.id.channel_name_relativelayout);
		channel_layout= (RelativeLayout) findViewById(R.id.channel_layout);
		channel_dtt_number_1= (TextView) findViewById(R.id.channel_dtt_number_1);
		channel_dth_number_1= (TextView) findViewById(R.id.channel_dth_number_1);
		dtt_layout_1 = findViewById(R.id.decoder_relativelayout_1);
		dth_layout_1 = findViewById(R.id.dish_relativelayout_1);
		channel_name_1= (RelativeLayout) findViewById(R.id.channel_name_relativelayout_1);
		channel_layout_1= (RelativeLayout) findViewById(R.id.channel_layout_1);
		channel_dtt_number_2= (TextView) findViewById(R.id.channel_dtt_number_2);
		channel_dth_number_2= (TextView) findViewById(R.id.channel_dth_number_2);
		dtt_layout_2 = findViewById(R.id.decoder_relativelayout_2);
		dth_layout_2 = findViewById(R.id.dish_relativelayout_2);
		channel_name_2= (RelativeLayout) findViewById(R.id.channel_name_relativelayout_2);
		channel_layout_2= (RelativeLayout) findViewById(R.id.channel_layout_2);
	}
	private void initView() {
		flChannelLogoLayout.setVisibility(View.GONE);
		llOneLive.setVisibility(View.GONE);
		llTwoLive.setVisibility(View.GONE);
		llThreeLive.setVisibility(View.GONE);
		llFourLive.setVisibility(View.GONE);
		rlBbsPrz.setVisibility(View.GONE);
		ivChannelLogo.setImageResource(R.drawable.list_channel_cover);
		tvTenbTitle.setText("");
		tvChannelDeatil.setText("");
		tvProgramDate.setText("");
		tv_four_programData.setText("");
		tv_four_programTime.setText("");
		tvTenbDetail.setText("");
		llTenbContent.setOnClickListener(null);
		tvCmmCount.setText("");
		tvFavCount.setText("");
		tvTenbComment.setText("");
	}
	
	/**
	 * @param tenb
	 */
	public void setTenb(TenbMe tenb,int position) {
		this.position = position;
		initView();
		this.tenbMe = tenb;
		if(tenbMe == null) {
			return;
		}
		tvTenbDate.setText(DateFormat.formatDateMonthAbbr(tenbMe.getCreateDate(), "HH:mm,MM-dd-yyyy"));
		User user = StarApplication.mUser;
		switch (tenb.getType()) {
		case Tenb.TENB_FAV_CHANNEL: //收藏频道
			initFavChannelView();
			getChannel(tenb.getForeignKey(),tenb.getType());
			break;
		case Tenb.TENB_FAV_EPG:// 节目收藏
			initFavEpgView();
			getProgram(tenb.getForeignKey(), tenb.getType());
			break;
		case Tenb.TENB_COMMENT_EPG://节目评论
			initTenbCommentEpgView();
			getProgram(tenb.getForeignKey(), tenb.getType());
			break;
		case Tenb.TENB_DOWNLOAD_EPG: //节目下载
			initDownloadEpgView();
			getChannel(tenb.getForeignKey(),tenb.getType());
			break;
		case Tenb.TENB_POST_TOPIC: //发的贴子
			initTenbPostView();
			if(tenbMe.getStatus().equals(Status.Deleted)){
				initTenbPostData(null);
				return;
			}
			getTenbTopic(tenb.getForeignKey(), tenb.getType());
			break;
		case Tenb.TENB_FIRST_POST:
			initDefaultTenbView();;
			initTenbreDate();
			break;
		case Tenb.TENB_COMMENT_CHANNEL://频道评分
			initChannelScoreView();
			getComment(tenb.getForeignKey(),tenb.getType());//获得评分详情
			break; 
		case Tenb.TENB_SHARE_EPG://分享节目的记录
			initShareEpgView();
			getProgram(tenb.getForeignKey(), tenb.getType());
			break;
		default: //默认
			initDefaultTenbView();
			initDefaultTenbData();
			break;
		}
	}
	
	private void initFavChannelView() {
		ivTenbIcon.setImageResource(R.drawable.ic_tenb_collect);
		flChannelLogoLayout.setVisibility(View.VISIBLE);
		llTwoLive.setVisibility(View.GONE);
		llOneLive.setVisibility(View.VISIBLE);
		llThreeLive.setVisibility(View.GONE);
		ratingBar.setVisibility(View.GONE);
		tvTenbComment.setVisibility(View.GONE);
	}
	
	private void initFavEpgView() {
		ivTenbIcon.setImageResource(R.drawable.ic_tenb_notifications);
		flChannelLogoLayout.setVisibility(View.VISIBLE);
		llTwoLive.setVisibility(View.VISIBLE);
		llOneLive.setVisibility(View.GONE);
		llThreeLive.setVisibility(View.GONE);
		tvTenbComment.setVisibility(View.GONE);

	}
	
	private void initTenbCommentEpgView() {
		ivTenbIcon.setImageResource(R.drawable.ic_tenb_comment);
		flChannelLogoLayout.setVisibility(View.VISIBLE);
		llTwoLive.setVisibility(View.VISIBLE);
		llOneLive.setVisibility(View.GONE);
		llThreeLive.setVisibility(View.GONE);
		llTwoLive.setVisibility(View.VISIBLE);
		llOneLive.setVisibility(View.GONE);
		llThreeLive.setVisibility(View.GONE);
		tvTenbComment.setVisibility(View.GONE);

	}
	
	private void initDownloadEpgView() {
		ivTenbIcon.setImageResource(R.drawable.ic_tenb_epg_download);
		flChannelLogoLayout.setVisibility(View.VISIBLE);
		llTwoLive.setVisibility(View.GONE);
		llOneLive.setVisibility(View.VISIBLE);
		llThreeLive.setVisibility(View.GONE);
		ratingBar.setVisibility(View.GONE);
		tvTenbComment.setVisibility(View.GONE);
	}
	
	/**
	 * 发帖
	 */
	private void initTenbPostView() {
		ivTenbIcon.setImageResource(R.drawable.ic_tenb_forum);
		llTwoLive.setVisibility(View.GONE);
		llOneLive.setVisibility(View.GONE);
		llThreeLive.setVisibility(View.VISIBLE);
		flChannelLogoLayout.setVisibility(View.GONE);
		rlBbsPrz.setVisibility(View.INVISIBLE);
		tvTenbComment.setVisibility(View.GONE);
	}
	/**
	 * 频道评分
	 */
	private void initChannelScoreView(){
		ivTenbIcon.setImageResource(R.drawable.ic_tenb_comment);
		flChannelLogoLayout.setVisibility(View.VISIBLE);
		llTwoLive.setVisibility(View.GONE);
		llOneLive.setVisibility(View.VISIBLE);
		llThreeLive.setVisibility(View.GONE);
		llTwoLive.setVisibility(View.GONE);
		llOneLive.setVisibility(View.VISIBLE);
		llThreeLive.setVisibility(View.GONE);
		tvTenbComment.setVisibility(View.VISIBLE);
	}
	/**
	 * 分享节目的记录
	 */
	private void initShareEpgView(){
		ivTenbIcon.setImageResource(R.drawable.ic_tenb_share);
		flChannelLogoLayout.setVisibility(View.GONE);
		llTwoLive.setVisibility(View.GONE);
		llOneLive.setVisibility(View.GONE);
		llThreeLive.setVisibility(View.GONE);
		llFourLive.setVisibility(View.VISIBLE);
		tvTenbComment.setVisibility(View.GONE);
	}
	/**
	 *默认
	 */
	private void initDefaultTenbView() {
		ivTenbIcon.setImageResource(R.drawable.ic_tenb_tenbre);
		llTwoLive.setVisibility(View.GONE);
		llOneLive.setVisibility(View.GONE);
		flChannelLogoLayout.setVisibility(View.GONE);
		llThreeLive.setVisibility(View.VISIBLE);
		rlBbsPrz.setVisibility(View.GONE);
		tvTenbComment.setVisibility(View.GONE);
	}
	
	private void initTenbreDate() {
		tvTenbTitle.setText(mContext.getString(R.string.welcome_tenbre_play));
		tvTenbDetail.setText(mContext.getString(R.string.welcome_msg));
	}
	
	private void initChannelData(ChannelVO channel, int type) {
		if(channel==null)
			return;
		ivChannelLogo.setUrl(channel.getLogo().getResources().get(0).getUrl());
		tvTenbTitle.setText(channel.getName());
		if(type == Tenb.TENB_FAV_CHANNEL)
			tvChannelDeatil.setText(mContext.getString(R.string.fav_channel, channel.getName()));
		else if(type == Tenb.TENB_DOWNLOAD_EPG)
			tvChannelDeatil.setText(mContext.getString(R.string.download_epg, channel.getName()));
		 
	}
	
	private void initChnEpgData(ChannelVO channel, ProgramVO program) {
		setChannelInfo(channel);
		if (channel != null) {
			ivChannelLogo.setUrl(channel.getLogo().getResources().get(0).getUrl());
			try {
				channel_layout.setVisibility(View.VISIBLE);
				dtt_layout.setVisibility(View.GONE);
				dth_layout.setVisibility(View.GONE);
				channel_name.setVisibility(View.GONE);
				List<TVPlatformInfo> infos = channel.getOfAreaTVPlatforms().get(0).getPlatformInfos();
				for(TVPlatformInfo info : infos) {
					if(info.getOfPackage()!=null)
						if (TVPlatForm.DTT.equals(info.getTvPlatForm())) {
							channel_dtt_number.setText(info.getChannelNumber());
							dtt_layout.setVisibility(View.VISIBLE);
							channel_name.setVisibility(View.VISIBLE);
						}else if(TVPlatForm.DTH.equals(info.getTvPlatForm())){
							channel_dth_number.setText(info.getChannelNumber());
							dth_layout.setVisibility(View.VISIBLE);
							channel_name.setVisibility(View.VISIBLE);
						}
				}
			}catch (Exception e){
			}
		}
		if(program!=null){
			tvProgramDate.setText(DateFormat.formatDateMonth(program.getStartDate(), "HH:mm MM-dd"));
			tvTenbTitle.setText(program.getName());
			tv_four_programTime.setText("Time: " + DateFormat.formatTime(program.getStartDate()) + "-" + DateFormat.formatTime(program.getEndDate()));
			tv_four_programData.setText("Date: "+DateFormat.formatDayAndMonth(program.getStartDate()));
		}
	}	
	private void initChnSource(ChannelVO channel) {//初始化频道评分
		 if(channel!=null){ 
			ivChannelLogo.setUrl(channel.getLogo().getResources().get(0).getUrl());
			 tvChannelDeatil.setVisibility(View.GONE);
			 setChannelInfo_for_one(channel);
			 tvTenbTitle.setText(channel.getName());
		}
	}
	
	/**
	 * 发帖
	 */
	private void initTenbPostData(final TenbTopic topic) {
		if(topic!=null&&!topic.getId().equals(tenbMe.getForeignKey())){
			return;
		}
		if(topic!=null){
			tvTenbTitle.setText(topic.getTopicTitle());
			tvTenbDetail.setText(topic.getTopicText());
			tvFavCount.setText(topic.getTopicPraise()+"");
			tvCmmCount.setText(topic.getTopicRepllies()+"");
			rlBbsPrz.setVisibility(View.VISIBLE);
			llTenbContent.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getContext(), BrowserActivity.class);
					intent.putExtra("loadUrl",String.format(getContext().getString(R.string.bbs_detail), topic.getId()));
					intent.putExtra("isBbs",1);
					CommonUtil.startActivity((Activity)getContext(), intent);	
				}
			});
		}else{
			rlBbsPrz.setVisibility(View.INVISIBLE);
			tvTenbTitle.setText("");
			tvTenbDetail.setText("Sorry, this post has been deleted.");
		}
	}
	
	/**
	 *默认
	 */
	private void initDefaultTenbData() {
		tvTenbTitle.setText("Welcome to tenbre play");
		tvTenbDetail.setText("Click here to set  your nickname & avatar,Start your tenbre jouney!");
	}

	private void getProgram(final long programId,final int type) {
		new LoadingDataTask() {
			ProgramVO program;
			@Override
			public void onPreExecute() {
				
			}
			
			@Override
			public void onPostExecute() {
				if(program != null){
					getChannel(program.getChannelId(), tenbMe.getType());
					initChnEpgData(null, program);
					llTenbContent.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							CommonUtil.startEpgActivity(mContext, program.getId());
						}
					});
				}
			}
			
			@Override
			public void doInBackground() {
				program = programService.getEpgDetailByIdFromCache(programId);
			}
		}.execute();
	}
	
	private void getChannel(final long channelId,final int type) {
		new LoadingDataTask() {
			private ChannelVO channel;
			@Override
			public void onPreExecute() {
				
			}
			
			@Override
			public void onPostExecute() {
				if(channel==null)
					return;
				
				switch (type) {
				case Tenb.TENB_FAV_CHANNEL:
				case Tenb.TENB_DOWNLOAD_EPG:
					initChannelData(channel, type);
					llTenbContent.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							
							CommonUtil.startChannelActivity(mContext, channel);
						}
					});
					break;
				case Tenb.TENB_COMMENT_EPG:
				case Tenb.TENB_FAV_EPG:
					case Tenb.TENB_SHARE_EPG:
						initChnEpgData(channel, null);
					break;
				case Tenb.TENB_COMMENT_CHANNEL:
					initChnSource(channel);
					llTenbContent.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Intent intent=new Intent(mContext,ChannelRateActivity.class);
							intent.putExtra("channel", channel);
							mContext.startActivity(intent); 
						}
					});
					break;
				default:
					break;
				}
				/*if(type==Tenb.TENB_FAV_CHANNEL||type==Tenb.TENB_DOWNLOAD_EPG){
					
				}else if(type==Tenb.TENB_COMMENT_EPG ){
					
				}else if(type==Tenb.TENB_COMMENT_CHANNEL){
					
				}*/
			}
		

			@Override
			public void doInBackground() {
				channel = channelService.getChannelById(channelId);
			}
		}.execute();
	}

	private void getComment(final long foreignKey,final int type){//获得评分详情
		tenbService.getComment(foreignKey, type, new OnResultTagListener<CommentVO>() {

			@Override
			public boolean onIntercept() {

				return false;
			}

			@Override
			public void onSuccess(CommentVO value) {
				// TODO Auto-generated method stub
				if (value != null) {
					ratingBar.setVisibility(View.VISIBLE);
					ratingBar.setRating(value.getScore());
					tvTenbComment.setText(value.getMsg());
					getChannel(value.getChannelID(), type);

				} else {
					ratingBar.setVisibility(View.GONE);
				}
			}

			@Override
			public void onFailure(int errorCode, String msg) {
				// TODO Auto-generated method stub

			}
		});
/*		 CommentLoader.getInstance().load(getContext(), Constant.getChannelSource(foreignKey),new OnResultTagListener<CommentVO>() {

			@Override
			public boolean onIntercept() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onSuccess(CommentVO value) {
				if(value!=null){
					ratingBar.setVisibility(View.VISIBLE);
					 ratingBar.setRating(value.getScore());
					 tvTenbComment.setText(value.getMsg());
					getChannel(value.getChannelID(),type);
					
				}else{
					ratingBar.setVisibility(View.GONE);
				}
			}

			@Override
			public void onFailure(int errorCode, String msg) {
				// TODO Auto-generated method stub
				
			}
		}, CommentVO.class, LoadMode.CACHE_NET);;*/
	}
	private void setChannelInfo_for_one(ChannelVO channel) {
		if (channel != null) {
			try {
				channel_layout_2.setVisibility(View.VISIBLE);
				dtt_layout_2.setVisibility(View.GONE);
				dth_layout_2.setVisibility(View.GONE);
				channel_name_2.setVisibility(View.GONE);
				List<TVPlatformInfo> infos = channel.getOfAreaTVPlatforms().get(0).getPlatformInfos();
				for(TVPlatformInfo info : infos) {
					if(info.getOfPackage()!=null)
						if (TVPlatForm.DTT.equals(info.getTvPlatForm())) {
							channel_dtt_number_2.setText(info.getChannelNumber());
							dtt_layout_2.setVisibility(View.VISIBLE);
							channel_name_2.setVisibility(View.VISIBLE);
						}else if(TVPlatForm.DTH.equals(info.getTvPlatForm())){
							channel_dth_number_2.setText(info.getChannelNumber());
							dth_layout_2.setVisibility(View.VISIBLE);
							channel_name_2.setVisibility(View.VISIBLE);
						}
				}
			}catch (Exception e){
			}
		}
	}
	private void setChannelInfo(ChannelVO channel) {
		if (channel != null) {
			try {
				channel_layout_1.setVisibility(View.VISIBLE);
				dtt_layout_1.setVisibility(View.GONE);
				dth_layout_1.setVisibility(View.GONE);
				channel_name_1.setVisibility(View.GONE);
				List<TVPlatformInfo> infos = channel.getOfAreaTVPlatforms().get(0).getPlatformInfos();
				for(TVPlatformInfo info : infos) {
					if(info.getOfPackage()!=null)
					if (TVPlatForm.DTT.equals(info.getTvPlatForm())) {
						channel_dtt_number_1.setText(info.getChannelNumber());
						dtt_layout_1.setVisibility(View.VISIBLE);
						channel_name_1.setVisibility(View.VISIBLE);
					}else if(TVPlatForm.DTH.equals(info.getTvPlatForm())){
						channel_dth_number_1.setText(info.getChannelNumber());
						dth_layout_1.setVisibility(View.VISIBLE);
						channel_name_1.setVisibility(View.VISIBLE);
					}
				}
			}catch (Exception e){
			}
		}
	}

	private void getTenbTopic(final long topicId,final int type) {
//		new LoadingDataTask() {
//			@Override
//			public void onPreExecute() {
//				topic = tenbService.getTenbTopic(topicId, true);
//				if(topic!=null){
//					setData();
//				}
//			}
//			
//			@Override
//			public void onPostExecute() {
//				setData();
//			}
//			
//			@Override
//			public void doInBackground() {
//				topic = tenbService.getTenbTopic(topicId, false);
//			}
//		}.execute();
		
		/*tenbService.getTenbTopic(topicId, type, new OnResultListener<TenbTopic>() {
			
			@Override
			public void onSuccess(TenbTopic value) {
				// TODO Auto-generated method stub
				initTenbPostData(value);
			}
			
			@Override
			public boolean onIntercept() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void onFailure(int errorCode, String msg) {
				 
			}
		});*/
			
		tenbService.getTopicLoader().load(getContext(), Constant.getTenbTopicUrl(topicId), new OnResultTagListener<TenbTopic>() {

			@Override
			public boolean onIntercept() {
				return false;
			}

			@Override
			public void onSuccess(TenbTopic value) {
				initTenbPostData(value);
			}

			@Override
			public void onFailure(int errorCode, String msg) {
			 
				
			}
		}, TenbTopic.class, LoadMode.NET);
	}
	
}
