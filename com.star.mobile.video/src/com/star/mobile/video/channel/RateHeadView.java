package com.star.mobile.video.channel;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.star.cms.model.vo.ChannelVO;
import com.star.cms.model.vo.CommentVO;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.home.tab.DownDrawerView;
import com.star.mobile.video.home.tab.DrawerScrollListener;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.CalculationTimeUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.ToastUtil;
import com.star.ui.ImageView;
import com.star.util.loader.OnResultListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class RateHeadView extends LinearLayout implements View.OnClickListener{
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
	private CommentVO userCommentVO;
	private ChannelVO mChannelVO;
	private Context context;
	private CommentDialog commentDialog;
	private float userRating=0;
	private boolean isFisrt;
	private float channelScore;
	private ChannelService channelService;
	private LinearLayout ll_channel_rate;
	private com.star.mobile.video.service.ChannelService mChannelService;
	public RateHeadView(Context context) {
		super(context);
		initView(context);
	}

	public RateHeadView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public RateHeadView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}
	/**
	 * 初始化按钮
	 */
	private void initView(final Context context) {
		mChannelService = new com.star.mobile.video.service.ChannelService(context);
		channelService = new ChannelService(context);
		commentDialog = new CommentDialog(context,this);
		this.context=context;
		LayoutInflater.from(context).inflate(R.layout.starplus_head, this);
		rl_user_message = (RelativeLayout)findViewById(R.id.rl_user);
		iv_user_logo = (ImageView) findViewById(R.id.user_logo);
		tv_user_name = (TextView)findViewById(R.id.tv_comment_username);
		tv_user_comment_content = (TextView)findViewById(R.id.tv_comment_content);
		tv_comment_time = (TextView)findViewById(R.id.tv_data_time);
		ll_channel_rate= (LinearLayout)findViewById(R.id.ll_channel_rate);
		tv_tips = (TextView)findViewById(R.id.no_rating_tips);
		tv_tips.setOnClickListener(null);
		tv_channel_rate = (TextView)findViewById(R.id.channel_rating);
		load_user = (View) findViewById(R.id.loadingView_user);
		rating_user = (RatingBar) findViewById(R.id.rating_user);
		rating_channel = (RatingBar) findViewById(R.id.rating_channel);
		rating_user.setIsIndicator(true);
		rating_user.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
				if (SharedPreferencesUtil.getUserName(context) != null) {
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
						CommonUtil.pleaseLogin(context);
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
		commentDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				rating_user.setIsIndicator(false);
				rating_user.setRating(userRating);
			}
		});
	}

	public void setData(CommentVO userCommentVO){
		this.userCommentVO=userCommentVO;
		rating_user.setRating(userCommentVO.getScore());
		user_commentId=userCommentVO.getCommentID();
		if(StarApplication.mUser!=null && userCommentVO.getMsg()!=null&& !"".equals(userCommentVO.getMsg())){
			tv_tips.setVisibility(View.GONE);
			rl_user_message.setVisibility(View.VISIBLE);
			tv_user_name.setText(StarApplication.mUser.getNickName());
			iv_user_logo.setUrl(StarApplication.mUser.getHead());
			tv_user_comment_content.setText(userCommentVO.getMsg());
			tv_comment_time.setText(CalculationTimeUtil.getTimeDisplay(userCommentVO.getCreateDate(), context));

		}else{
			tv_tips.setOnClickListener(this);
			tv_tips.setVisibility(View.VISIBLE);
			tv_tips.setText(context.getString(R.string.tips_score_comment));
			rl_user_message.setVisibility(View.GONE);

		}
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
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
	public void setChannelScore(int totalScore,int count){
		DecimalFormat df=new DecimalFormat("0.0");
		channelScore=(float)totalScore/count;
		tv_channel_rate.setText(df.format(channelScore));
		rating_channel.setRating(channelScore);
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
						tv_comment_time.setText(CalculationTimeUtil.getTimeDisplay(result.getCreateDate(), context));
					}else{
						tv_tips.setVisibility(View.VISIBLE);
						tv_tips.setText(context.getString(R.string.tips_score_comment));
						tv_tips.setOnClickListener(RateHeadView.this);
						rl_user_message.setVisibility(View.GONE);
					}
				}

				@Override
				public void onFailure(int errorCode, String msg) {
					rating_user.setIsIndicator(false);
					load_user.setVisibility(View.GONE);
					rl_user_message.setVisibility(View.VISIBLE);
					if(msg !=null && !"".equals(msg)){
						ToastUtil.centerShowToast(context, msg);
					}else{
						ToastUtil.centerShowToast(context, context.getString(R.string.channel_comment_fail_edit));
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
						tv_comment_time.setText(CalculationTimeUtil.getTimeDisplay(result.getCreateDate(), context));

					}else{
						tv_tips.setVisibility(View.VISIBLE);
						tv_tips.setOnClickListener(RateHeadView.this);
						tv_tips.setText(context.getString(R.string.tips_score_comment));
						rl_user_message.setVisibility(View.GONE);

					}
				}

				@Override
				public void onFailure(int errorCode, String msg) {
					rating_user.setIsIndicator(false);
					load_user.setVisibility(View.GONE);
					if(msg !=null && !"".equals(msg)){
						ToastUtil.centerShowToast(context, msg);
					}else{
						ToastUtil.centerShowToast(context, context.getString(R.string.channel_comment_fail));
					}
				}
			});

		}
	}

	public void setUserRate(boolean isIndicator){
		rating_user.setIsIndicator(isIndicator);
	}
	public void setIsFisrt(boolean isFisrt){
		this.isFisrt = isFisrt;
	}
	public boolean getIsFisrt(){
		return isFisrt;
	}
	public void setUserMessage(int isShow){
		rl_user_message.setVisibility(isShow);
	}
	public void setChannelRate(int isShow){
		ll_channel_rate.setVisibility(isShow);
	}
	public void setUserRating(float userRating){
		this.userRating=userRating;
	}
	public void setChannelVo(ChannelVO mChannelVO){
		this.mChannelVO=mChannelVO;
		if(mChannelVO.getCommentTotalScore() <= 0){
			rating_user.setIsIndicator(false);
			rl_user_message.setVisibility(View.GONE);
			ll_channel_rate.setVisibility(View.GONE);
		}else{
			ll_channel_rate.setVisibility(View.VISIBLE);
			rl_user_message.setVisibility(View.GONE);
		}
	}
}
