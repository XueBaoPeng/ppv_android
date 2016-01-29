package com.star.mobile.video.me.coupon;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.star.cms.model.Exchange;
import com.star.cms.model.User;
import com.star.cms.model.vo.ExchangeVO;
import com.star.cms.model.vo.SmartCardInfoVO;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.activity.TellFriendActivity;
import com.star.mobile.video.activity.TranslucentBackgroundActivity;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.model.FunctionType;
import com.star.mobile.video.service.EggAppearService;
import com.star.mobile.video.service.FunctionService;
import com.star.mobile.video.service.UserService;
import com.star.mobile.video.service.UserService.CallbackListener;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.LoadingDataTask;
import com.star.mobile.video.util.ToastUtil;
import com.star.mobile.video.view.LoadingProgressBar;

public class MyCouponsActivity extends BaseActivity implements OnScrollListener {
	
	private ListView couponListView;
	private ExchangeService exchangeService;
	private ImageView ivShareEgg;
	private List<ExchangeVO> exchanges = new ArrayList<ExchangeVO>();
	private boolean firstLoad = true;
	private int offset;
	private int responsSize;
	private LoadingProgressBar footerView;
	private int requestCount = 10;
	private MyCouponListAdapter adapter;
	private SmartCardInfoVO smartCardInfo;
	private String selectSmartCardNo;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WHAT_HIDE_LISTFOOTER:
				if(footerView != null&&couponListView!=null) {
					couponListView.removeFooterView(footerView);
				}
				break;
			}
		}
	};
	private final int WHAT_HIDE_LISTFOOTER = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mycoupons);
		((TextView) findViewById(R.id.tv_actionbar_title)).setText(getResources().getString(R.string.my_coupons_title));
		((ImageView) findViewById(R.id.iv_actionbar_back)).setOnClickListener(click);
		smartCardInfo = (SmartCardInfoVO) getIntent().getSerializableExtra("smartcardinfovo");
		if(smartCardInfo!=null){
			selectSmartCardNo=smartCardInfo.getSmardCardNo();
		}else{
			selectSmartCardNo=null;
		}
		couponListView = (ListView) findViewById(R.id.lv_coupons);
		couponListView.setOnScrollListener(this);
		ivShareEgg = (ImageView) findViewById(R.id.iv_share_egg);
		exchangeService = new ExchangeService(this);
		footerView = new LoadingProgressBar(this);
		couponListView.addFooterView(footerView);
		EggAppearService.appearEgg(this, EggAppearService.Coupons);
		currenIntent(getIntent());
	}
	
	@Override
	protected void onResume() {
		clearData();
		getCouponTasks(false);
		super.onResume();
	}

	private void clearData() {
		CommonUtil.closeProgressDialog();
		firstLoad = true;
		offset = 0;
		responsSize = 0;
		footerView.setVisibility(View.INVISIBLE);
		if(exchanges.size()>0 && adapter!=null){
			exchanges.clear();
			adapter.updateDataAndRefreshUI(exchanges);
		}
	}
	
	private void getCouponTasks(final boolean fromLocal) {
		
		new LoadingDataTask() {
			private List<ExchangeVO> datas;
			@Override
			public void onPreExecute() {
				findViewById(R.id.iv_no_coupons).setVisibility(View.GONE);
				if(firstLoad){
					if(!fromLocal) {
						CommonUtil.showProgressDialog(MyCouponsActivity.this, null, getString(R.string.loading));
					}
					footerView.setVisibility(View.INVISIBLE);
				}else if(footerView.getVisibility() == View.INVISIBLE){
					footerView.setVisibility(View.VISIBLE);
				}
			}
			
			@Override
			public void onPostExecute() {
				CommonUtil.closeProgressDialog();
				if(firstLoad&&!FunctionService.doHideFuncation(FunctionType.InviteFriends)){
					findViewById(R.id.rl_share_info).setVisibility(View.VISIBLE);
					ivShareEgg.setVisibility(View.VISIBLE);
					ivShareEgg.setOnClickListener(click);
					CommonUtil.startAnimation(ivShareEgg);
				}
				if(datas == null) {
					if(firstLoad)
						ToastUtil.centerShowToast(MyCouponsActivity.this, getString(R.string.error_network));
					return;
				}
				
//				exchanges.clear();
//				List<ExchangeVO> inValids = new ArrayList<ExchangeVO>();
//				List<ExchangeVO> valids = new ArrayList<ExchangeVO>();
//				for(ExchangeVO vo : datas){
//					if (vo.isValid()||vo.isAccepted()) {
//						inValids.add(vo);
//					}else{
//						valids.add(vo);
//					}
//				}
//				exchanges.addAll(valids);
//				exchanges.addAll(inValids);
				responsSize = datas.size();
				exchanges.addAll(datas);
				if(firstLoad && responsSize<requestCount){
					handler.sendEmptyMessage(WHAT_HIDE_LISTFOOTER);
				}
				if(exchanges.size() != 0) {
					if(adapter == null){
						adapter = new MyCouponListAdapter(MyCouponsActivity.this, exchanges ,selectSmartCardNo,smartCardInfo);
						couponListView.setAdapter(adapter);
					}else{
						adapter.updateDataAndRefreshUI(exchanges);
					}
				}else{
					findViewById(R.id.iv_no_coupons).setVisibility(View.VISIBLE);
				}
				firstLoad = false;
			}
			
			@Override
			public void doInBackground() {
				datas = exchangeService.getExchanges(fromLocal, offset, requestCount);
			}
		}.execute();
	}
	
	private OnClickListener click = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_actionbar_back:
				MyCouponsActivity.super.onBackPressed();
				break;
			case R.id.iv_share_egg:
				if(SharedPreferencesUtil.getUserName(MyCouponsActivity.this) != null) {
					Intent  intent = new Intent(MyCouponsActivity.this,TellFriendActivity.class);
					CommonUtil.startActivity(MyCouponsActivity.this, intent);
				}else {
					CommonUtil.pleaseLogin(MyCouponsActivity.this);	
				}
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		switch (scrollState) {
		case OnScrollListener.SCROLL_STATE_IDLE:
			int position = couponListView.getLastVisiblePosition();
			int total = exchanges.size();
			if (position == total) {
				offset += requestCount;
				if (responsSize < requestCount) {
					ToastUtil.centerShowToast(this, "no more coupons");
					handler.sendEmptyMessageDelayed(WHAT_HIDE_LISTFOOTER, 1000);
					return;
				}
				getCouponTasks(false);
			}
			break;
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		currenIntent(intent);
	}
	
	
	private void currenIntent(Intent intent) {
		Exchange e = (Exchange)intent.getSerializableExtra("exchange");
		if(e != null) {
			popEgg(e);
		} 
	}
	 
	
	/**
	 * 弹出彩蛋
	 * @param eg
	 */
	public void popEgg(final Exchange ex) {
		User user = StarApplication.mUser;
		if(user != null) {
			pop(ex, user);
		}else{
			UserService userService = new UserService();
			userService.setCallbackListener(new CallbackListener() {
				@Override
				public void callback(User user) {
					if(user!=null){
						pop(ex, user);
					}
				}
			});
			userService.getUser(this, false);
		}
	}

	private void pop(Exchange ex, User user) {
		String username = user.getUserName();
		if(username==null)
			return;
		Intent i = new Intent(this, TranslucentBackgroundActivity.class);
		if(username.startsWith(User.PrefixOfUsr3Party)){
			try{
				username = username.split("#")[1];
			}catch(Exception e){
				
			}
		}
		i.putExtra("exchange", ex);
		i.putExtra("userName", username);
		i.putExtra("userType", user.getType().getNum());
		this.startActivity(i);
	}
}
