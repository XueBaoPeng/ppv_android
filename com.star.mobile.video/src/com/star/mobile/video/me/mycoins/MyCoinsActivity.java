package com.star.mobile.video.me.mycoins;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.star.cms.model.User;
import com.star.mobile.video.R;
import com.star.mobile.video.activity.BrowserActivity;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.me.mycoins.reward.RewardView;
import com.star.mobile.video.service.UserService;
import com.star.mobile.video.service.UserService.CallbackListener;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.TextUtil;
import com.star.mobile.video.view.ControlTabView;

public class MyCoinsActivity extends BaseActivity implements OnClickListener {

	private TextView tvMyCoins;
	private UserService userService;
	private TaskView mTaskView;
	private RewardView mRewardView;
	private ControlTabView controlTabView;
	private List<String> tab =new ArrayList<String>();
	private List<View> view =new ArrayList<View>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mycoins);
		userService = new UserService();
		initView();
	}
	
	@Override
	protected void onStart() {
		updateUI();
		super.onStart();
	}
	
	public void updateUI(){
		if(mTaskView!=null){
			mTaskView.getTasks();
		}
		updateCoins();
	}
	
	public void updateCoins(){
		getUserCoins();
	}
	
	private void initView() {
		findViewById(R.id.iv_actionbar_search).setVisibility(View.GONE);
		tvMyCoins = (TextView) findViewById(R.id.tv_my_coins);
		findViewById(R.id.iv_actionbar_back).setOnClickListener(this);
		findViewById(R.id.iv_help).setOnClickListener(this);
		TextView tvTitle = (TextView) findViewById(R.id.tv_actionbar_title);
		tvTitle.setText(getString(R.string.my_coins));
		mTaskView = (TaskView) findViewById(R.id.mycoins_task);
		mRewardView = (RewardView) findViewById(R.id.mycoins_reward);
		controlTabView = (ControlTabView) findViewById(R.id.ll_control);
		tab.add(getResources().getString(R.string.earn));
		tab.add(getResources().getString(R.string.spend));
		controlTabView.setTabData(tab);
		view.add(mTaskView);
		view.add(mRewardView);
		controlTabView.setView(view);
	}
	
	/**
	 * 获取用户积分
	 */
	private void getUserCoins() {
		userService.setCallbackListener(new CallbackListener() {
			@Override
			public void callback(User user) {
				if (user != null) {
					setCoins(user);
				} else {
					tvMyCoins.setText(null);
				}
			}
		});
		userService.getUser(this, false);
	}

	private void setCoins(User user) {
		String coins = TextUtil.getInstance().addComma3(user.getCoins()+"") + " "+getString(R.string.coins);
		int index = coins.indexOf(" "+getString(R.string.coins));
		SpannableString styledText = new SpannableString(coins);
		styledText.setSpan(new AbsoluteSizeSpan(50, true), 0, index, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		styledText.setSpan(new AbsoluteSizeSpan(20, true), index, coins.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		tvMyCoins.setText(styledText, TextView.BufferType.SPANNABLE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_actionbar_back:
			onBackPressed();
			break;
		case R.id.iv_help:
			Intent intent = new Intent(this, BrowserActivity.class);
			intent.putExtra("loadUrl", "http://tenbre.me/bbs/tenbrePosts/list/207.page");
			CommonUtil.startActivity(this, intent);
			break;
		default:
			break;
		}
	}
}
