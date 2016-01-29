package com.star.mobile.video.view;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.star.cms.model.User;
import com.star.mobile.video.R;
import com.star.mobile.video.service.UserService;
import com.star.mobile.video.service.UserService.CallbackListener;

public class TaskHeaderItem extends LinearLayout{

	private TextView tvCoins;
	private ImageView ivRewardsIcon;
	private TextView tvRefresh;
	private UserService userService;
	private LinearLayout llTaskHead;
	private ImageView ivLine;
	public final static int Page_SpendCoins = 1;
	public final static int Page_EarnCoins = 2;
	
	public TaskHeaderItem(Context context) {
		this(context, null);
	}
	
	public TaskHeaderItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		userService = new UserService();
		LayoutInflater.from(context).inflate(R.layout.view_task_header_item, this);
		llTaskHead = (LinearLayout) findViewById(R.id.ll_taskHead);
		ivLine = (ImageView) findViewById(R.id.iv_line);
		tvCoins = (TextView) findViewById(R.id.tv_my_coins);
		ivRewardsIcon = (ImageView) findViewById(R.id.iv_rewards);
		tvRefresh = (TextView) findViewById(R.id.tv_refresh);
		tvRefresh.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		getUserCoins();
		tvRefresh.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getUserCoins();
			}
		});
	}
	
	public void setConis(Integer coins) {
		if(coins == null) {
			tvRefresh.setVisibility(View.VISIBLE);
			tvCoins.setVisibility(View.GONE);
		} else {
			tvRefresh.setVisibility(View.GONE);
			tvCoins.setVisibility(View.VISIBLE);
			tvCoins.setText(coins.toString());
		}
	}
	
	public void setRewardIconOnClick(OnClickListener l) {
		ivRewardsIcon.setOnClickListener(l);
	}
	
	public void setRewardsHeadRes(int pageId){
		if(pageId == Page_SpendCoins){
			ivRewardsIcon.setBackgroundResource(R.drawable.icon_tasks_spengdcoins);
			llTaskHead.setBackgroundResource(R.drawable.orange_button_bg);
			ivLine.setBackgroundResource(R.drawable.title_orange_line);
			findViewById(R.id.rl_double_info).setVisibility(View.GONE);
		}
	}
	
	public void setRewardsHeadRes(int pageId, boolean isDouble){
		if(pageId == Page_EarnCoins && isDouble){
			findViewById(R.id.rl_double_info).setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * 获取用户积分
	 */
	private void getUserCoins() {
		userService.setCallbackListener(new CallbackListener() {
			@Override
			public void callback(User user) {
				if(user != null) {
					setConis(user.getCoins());
				} else {
					setConis(null);
				}
			}
		});
		userService.getUser(getContext(), false);
	}
}
