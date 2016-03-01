package com.star.mobile.video.channel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.star.cms.model.vo.ChannelVO;
import com.star.cms.model.vo.CommentVO;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.CalculationTimeUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.LoadingDataTask;
import com.star.mobile.video.util.ToastUtil;
import com.star.mobile.video.view.LoadingProgressBar;
import com.star.mobile.video.view.NoScrollListView;
import com.star.ui.ImageView;
import com.star.util.Logger;
import com.star.util.loader.OnListResultListener;
import com.star.util.loader.OnResultListener;

public class ChannelRateActivity extends BaseActivity implements OnClickListener,OnScrollListener{
	
	private RelativeLayout rl_user_message;
	private ImageView iv_user_logo;
	private TextView tv_user_name;
	private TextView tv_user_comment_content;
	private TextView tv_comment_time;
	private long user_commentId;
	private View load_user;
	private RatingBar rating_user;
	private TextView tv_tips;
	private TextView tv_channel_rate;
	private RatingBar rating_channel;
	private NoScrollListView listView;
	private View load_comment;
	private LinearLayout ll_no_data;
	private TextView tv_comment_tips;
	private LinearLayout ll_channel_rate;
	private ChannelVO mChannelVO;
	private long mChannelID;
	private boolean isLoading;
	private boolean isFisrt;
	private boolean isChannelVO;
	private ChannelService channelService;
	private com.star.mobile.video.service.ChannelService mChannelService;
	private int offset = 0;
	private int requestCount = 10;
	private ChannelCommentAdapter channelCommentAdapter;
	private CommentDialog commentDialog;
	private List<CommentVO> mComments = new ArrayList<CommentVO>();
	private CommentVO userCommentVO;
	private float userRating=0;
	private int responsSize;
	private View footerView;
	private final int WHAT_HIDE_LISTFOOTER = 1;
	private final int WHAT_SHOW_USER = 2;
	private float channelScore;
	public Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WHAT_HIDE_LISTFOOTER:
				listView.removeFooterView(footerView);
				break;
			case WHAT_SHOW_USER:
				rating_user.setRating(userCommentVO.getScore());
				user_commentId=userCommentVO.getCommentID();
				if(StarApplication.mUser!=null && userCommentVO.getMsg()!=null&& !"".equals(userCommentVO.getMsg())){
					tv_tips.setVisibility(View.GONE);
					rl_user_message.setVisibility(View.VISIBLE);
					tv_user_name.setText(StarApplication.mUser.getNickName());
					iv_user_logo.setUrl(StarApplication.mUser.getHead());
					tv_user_comment_content.setText(userCommentVO.getMsg());
					tv_comment_time.setText(CalculationTimeUtil.getTimeDisplay(userCommentVO.getCreateDate(), ChannelRateActivity.this));
					if(mComments.size() == 0){
						ll_no_data.setVisibility(View.VISIBLE);
						tv_comment_tips.setText(getString(R.string.channel_comment_tips));
						listView.setVisibility(View.GONE);
					}
				}else{
					tv_tips.setOnClickListener(ChannelRateActivity.this);
					tv_tips.setVisibility(View.VISIBLE);
					tv_tips.setText(getString(R.string.tips_score_comment));
					rl_user_message.setVisibility(View.GONE);
					if(mComments.size() == 0){
						ll_no_data.setVisibility(View.VISIBLE);
						tv_comment_tips.setText(getString(R.string.channel_comment_tips));
						listView.setVisibility(View.GONE);
					}
				}
				break;
			}
		}
	};
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_starplus);
	        channelService = new ChannelService(this);
	        commentDialog = new CommentDialog(this);
	        mChannelService = new com.star.mobile.video.service.ChannelService(this);
	        try {
		        mChannelVO = (ChannelVO) getIntent().getSerializableExtra("channel");
			} catch (Exception e) {
				Logger.d("channel = null");
				mChannelID=getIntent().getLongExtra("channelID", 0);
			}
	        initView();
	        rating_user.setIsIndicator(true);
	        if(mChannelVO == null){
				getChannelVO();
			}else{
				initData();	
			}
	        
	    }

	private void initView() {
		rl_user_message = (RelativeLayout)findViewById(R.id.rl_user);
		iv_user_logo = (ImageView) findViewById(R.id.user_logo);
		tv_user_name = (TextView)findViewById(R.id.tv_comment_username);
		tv_user_comment_content = (TextView)findViewById(R.id.tv_comment_content);
		tv_comment_time = (TextView)findViewById(R.id.tv_data_time);
		tv_tips = (TextView)findViewById(R.id.no_rating_tips);
		tv_tips.setOnClickListener(null);
		tv_channel_rate = (TextView)findViewById(R.id.channel_rating);
		load_user = (View) findViewById(R.id.loadingView_user);
		load_comment = (View) findViewById(R.id.loadingView_comment);
		rating_user = (RatingBar) findViewById(R.id.rating_user);
		rating_channel = (RatingBar) findViewById(R.id.rating_channel);
		listView = (NoScrollListView)findViewById(R.id.comments_listView);
		ll_no_data = (LinearLayout)findViewById(R.id.ll_no_data);
		tv_comment_tips = (TextView)findViewById(R.id.tv_comment_tips);
		ll_channel_rate= (LinearLayout)findViewById(R.id.ll_channel_rate);
		((android.widget.ImageView) findViewById(R.id.iv_actionbar_back)).setOnClickListener(this);
		if(mChannelVO.getName() != null){
			((TextView) findViewById(R.id.tv_actionbar_title)).setText(mChannelVO.getName());
		}else{
			((TextView) findViewById(R.id.tv_actionbar_title)).setText(getString(R.string.starplus));
		}
		footerView = new LoadingProgressBar(this);
		listView.addFooterView(footerView);
		channelCommentAdapter = new ChannelCommentAdapter(this, mComments);
		listView.setAdapter(channelCommentAdapter);
	}
	private void initData() {
//		if(mChannelVO.getCommentTotalScore() <= 0){
//			rating_user.setIsIndicator(false);
//			rl_user_message.setVisibility(View.GONE);
//			ll_channel_rate.setVisibility(View.GONE);
//			listView.setVisibility(View.GONE);
//			ll_no_data.setVisibility(View.VISIBLE);
//		}else{
			rl_user_message.setVisibility(View.GONE);
			ll_channel_rate.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
			ll_no_data.setVisibility(View.GONE);
			load_comment.setVisibility(View.VISIBLE);
			loadData(mChannelVO.getId());
//		}
		 rating_user.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
			 @Override
			 public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
				 if (SharedPreferencesUtil.getUserName(ChannelRateActivity.this) != null) {
					 if (rating > 0 && userRating != rating) {
						 rating_user.setIsIndicator(true);
						 if (isFisrt) {
							 commentDialog.showCommentDialog(true, rating, tv_user_comment_content.getText().toString(), mChannelVO.getId(), user_commentId);
						 } else {
							 commentDialog.showCommentDialog(false, rating, null, mChannelVO.getId(), -1);
						 }
					 }
				 } else {
					 rating_user.setRating(0f);
					 if (userRating != rating) {
						 CommonUtil.pleaseLogin(ChannelRateActivity.this);
					 }
				 }
			 }
		 });
		rl_user_message.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				rating_user.setIsIndicator(true);
				if (isFisrt) {
					commentDialog.showCommentDialog(true, userRating, tv_user_comment_content.getText().toString(), mChannelVO.getId(), user_commentId);
				} else {
					commentDialog.showCommentDialog(false, userRating, null, mChannelVO.getId(), -1);
				}
			}
		});
		 commentDialog.setOnCancelListener(new OnCancelListener() {

			 @Override
			 public void onCancel(DialogInterface dialog) {
				 rating_user.setIsIndicator(false);
				 rating_user.setRating(userRating);
			 }
		});
	}
	private void getChannelVO() {
		rl_user_message.setVisibility(View.GONE);
		ll_channel_rate.setVisibility(View.VISIBLE);
		listView.setVisibility(View.GONE);
		ll_no_data.setVisibility(View.GONE);
		load_comment.setVisibility(View.VISIBLE);
		channelService.getChannelById(mChannelID, new OnResultListener<ChannelVO>() {

			@Override
			public boolean onIntercept() {
				return false;
			}

			@Override
			public void onSuccess(ChannelVO value) {
				mChannelVO = value;
				if(mChannelVO != null){
					initData();
				}else{
					ToastUtil.centerShowToast(ChannelRateActivity.this, getString(R.string.failed));
					onBackPressed();
				}
			}

			@Override
			public void onFailure(int errorCode, String msg) {
				ToastUtil.centerShowToast(ChannelRateActivity.this, getString(R.string.failed));
				onBackPressed();
			}
		});
	}

	private void setChannelScore(int totalScore,int count){
		DecimalFormat df=new DecimalFormat("0.0");
		channelScore=(float)totalScore/count;
		tv_channel_rate.setText(df.format(channelScore));
		rating_channel.setRating(channelScore);
	}
	private void loadData(long channelId){
		channelService.getChannelComment(channelId,offset,requestCount, new OnListResultListener<CommentVO>() {

			@Override
			public boolean onIntercept() {
				if(isLoading){
					return true;
				}
				isLoading = true;
				return false;
			}

			@Override
			public void onFailure(int errorCode, String msg) {
				isLoading = false;
				load_comment.setVisibility(View.GONE);
				ToastUtil.centerShowToast(ChannelRateActivity.this, getString(R.string.failed));
			}

			@Override
			public void onSuccess(List<CommentVO> result) {
				isLoading = false;
				load_comment.setVisibility(View.GONE);
				rating_user.setIsIndicator(false);
				if(result!=null && result.size()>0){
					if(mChannelVO.getCommentTotalScore() <= 0 && !isChannelVO ){
						int score=0;
						Iterator<CommentVO> it = result.iterator();
						while (it.hasNext()) {
							CommentVO value = it.next();
							score=score+value.getScore();
						}
						mChannelVO.setCommentTotalScore((long)score);
						mChannelVO.setCommentTotalCount((long)result.size());
						mChannelService.updateChannel(mChannelVO);
						setChannelScore(score,result.size());
						isChannelVO=true;
					}else{
						setChannelScore(Integer.parseInt(String.valueOf(mChannelVO.getCommentTotalScore())),Integer.parseInt(String.valueOf(mChannelVO.getCommentTotalCount())));
					}
					responsSize = result.size();
					ll_no_data.setVisibility(View.GONE);
					listView.setVisibility(View.VISIBLE);
					if(result.size()<requestCount){
						handler.sendEmptyMessage(WHAT_HIDE_LISTFOOTER);
					}
					if(StarApplication.mUser!=null && StarApplication.mUser.getId().equals(result.get(0).getUserId()) && !isFisrt){
						userCommentVO = result.get(0);
						userRating=userCommentVO.getScore();
						handler.sendEmptyMessage(WHAT_SHOW_USER);
						isFisrt = true;
					}
					if(StarApplication.mUser!=null){
						Iterator<CommentVO> it = result.iterator();
						while (it.hasNext()) {
							CommentVO value = it.next();
							if (value.getUserId().equals(StarApplication.mUser.getId())) {
								it.remove();
							}
						}	
					}
					if(result.size()>0){
						mComments.addAll(result);
						channelCommentAdapter.setChannelComment(mComments);
					}else{
						ll_no_data.setVisibility(View.VISIBLE);
						listView.setVisibility(View.GONE);
					}
				}else{
					if(mComments.size()<=0){
						rating_user.setIsIndicator(false);
						rl_user_message.setVisibility(View.GONE);
						ll_channel_rate.setVisibility(View.GONE);
						listView.setVisibility(View.GONE);
						ll_no_data.setVisibility(View.VISIBLE);
					}
				}
			}
		});
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_actionbar_back:
			onBackPressed();
			break;
		case R.id.no_rating_tips:
			rating_user.setIsIndicator(true);
			if (isFisrt) {
				commentDialog.showCommentDialog(true, userRating, tv_user_comment_content.getText().toString(), mChannelVO.getId(), user_commentId);
			} else {
				commentDialog.showCommentDialog(false, userRating, null, mChannelVO.getId(), -1);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if(isLoading)
			return;
		switch (scrollState) {
		case OnScrollListener.SCROLL_STATE_IDLE:
			int position = listView.getLastVisiblePosition();
			if (position == mComments.size()) {
				offset += requestCount;
				if (responsSize < requestCount) {
					ToastUtil.centerShowToast(this, getString(R.string.no_more_comment));
					handler.sendEmptyMessageDelayed(WHAT_HIDE_LISTFOOTER, 1000);
					return;
				}
				loadData(mChannelVO.getId());
			}
			break;
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
	}
	public void submitComment(boolean isEdit,float rate,String content,long channelID,long commentID){
		load_user.setVisibility(View.VISIBLE);
		rl_user_message.setVisibility(View.GONE);
		if(isEdit){
			channelService.postChannelCommentEdit(commentID, content,(int)rate, new OnResultListener<CommentVO>() {

				@Override
				public boolean onIntercept() {
					return false;
				}

				@Override
				public void onSuccess(CommentVO result) {
					rating_user.setIsIndicator(false);
					mChannelVO.setCommentTotalScore(mChannelVO.getCommentTotalScore()-(int)userRating+result.getScore());
					load_user.setVisibility(View.GONE);
					rl_user_message.setVisibility(View.VISIBLE);
					userRating = result.getScore();
					rating_user.setRating(result.getScore());
					user_commentId=result.getCommentID();
					if(mChannelService.updateChannel(mChannelVO)){
						setChannelScore(Integer.parseInt(String.valueOf(mChannelVO.getCommentTotalScore())),Integer.parseInt(String.valueOf(mChannelVO.getCommentTotalCount())));
					}
					if(StarApplication.mUser!=null && result.getMsg()!=null&& !"".equals(result.getMsg())){
						tv_tips.setVisibility(View.GONE);
						tv_user_name.setText(StarApplication.mUser.getNickName());
						iv_user_logo.setUrl(StarApplication.mUser.getHead());
						tv_user_comment_content.setText(result.getMsg());
						tv_comment_time.setText(CalculationTimeUtil.getTimeDisplay(result.getCreateDate(), ChannelRateActivity.this));
						if(mComments.size() == 0){
							ll_no_data.setVisibility(View.VISIBLE);
							tv_comment_tips.setText(getString(R.string.channel_comment_tips));
							listView.setVisibility(View.GONE);
						}
					}else{
						tv_tips.setVisibility(View.VISIBLE);
						tv_tips.setText(getString(R.string.tips_score_comment));
						tv_tips.setOnClickListener(ChannelRateActivity.this);
						rl_user_message.setVisibility(View.GONE);
						if(mComments.size() == 0){
							ll_no_data.setVisibility(View.VISIBLE);
							tv_comment_tips.setText(getString(R.string.channel_comment_tips));
							listView.setVisibility(View.GONE);
						}
					}
				}

				@Override
				public void onFailure(int errorCode, String msg) {
					rating_user.setIsIndicator(false);
					load_user.setVisibility(View.GONE);
					rl_user_message.setVisibility(View.VISIBLE);
					if(msg !=null && !"".equals(msg)){
						ToastUtil.centerShowToast(ChannelRateActivity.this, msg);	
					}else{
						ToastUtil.centerShowToast(ChannelRateActivity.this, getString(R.string.channel_comment_fail_edit));
					}
				}
			});
		}else{
			channelService.postChannelComment(channelID, content,(int)rate, new OnResultListener<CommentVO>() {
				
				@Override
				public boolean onIntercept() {
					return false;
				}

				@Override
				public void onSuccess(CommentVO result) {
					rating_user.setIsIndicator(false);
					isFisrt= true;
					load_user.setVisibility(View.GONE);
					rl_user_message.setVisibility(View.VISIBLE);
					ll_channel_rate.setVisibility(View.VISIBLE);
					userRating = result.getScore();
					rating_user.setRating(result.getScore());
					user_commentId=result.getCommentID();
					mChannelVO.setCommentTotalCount(mChannelVO.getCommentTotalCount()+1);
					mChannelVO.setCommentTotalScore(mChannelVO.getCommentTotalScore()+result.getScore());
					if(mChannelService.updateChannel(mChannelVO)){
						setChannelScore(Integer.parseInt(String.valueOf(mChannelVO.getCommentTotalScore())),Integer.parseInt(String.valueOf(mChannelVO.getCommentTotalCount())));
					}
					if(StarApplication.mUser!=null && result.getMsg()!=null&& !"".equals(result.getMsg())){
						tv_tips.setVisibility(View.GONE);
						tv_user_name.setText(StarApplication.mUser.getNickName());
						iv_user_logo.setUrl(StarApplication.mUser.getHead());
						tv_user_comment_content.setText(result.getMsg());
						tv_comment_time.setText(CalculationTimeUtil.getTimeDisplay(result.getCreateDate(), ChannelRateActivity.this));
						if(mComments.size() == 0){
							ll_no_data.setVisibility(View.VISIBLE);
							tv_comment_tips.setText(getString(R.string.channel_comment_tips));
							listView.setVisibility(View.GONE);
						}
					}else{
						tv_tips.setVisibility(View.VISIBLE);
						tv_tips.setOnClickListener(ChannelRateActivity.this);
						tv_tips.setText(getString(R.string.tips_score_comment));
						rl_user_message.setVisibility(View.GONE);
						if(mComments.size() == 0){
							ll_no_data.setVisibility(View.VISIBLE);
							tv_comment_tips.setText(getString(R.string.channel_comment_tips));
							listView.setVisibility(View.GONE);
						}
					}
				}

				@Override
				public void onFailure(int errorCode, String msg) {
					rating_user.setIsIndicator(false);
					load_user.setVisibility(View.GONE);
					if(msg !=null && !"".equals(msg)){
						ToastUtil.centerShowToast(ChannelRateActivity.this, msg);	
					}else{
						ToastUtil.centerShowToast(ChannelRateActivity.this, getString(R.string.channel_comment_fail));
					}
				}
			});
			
		}
	}

}
