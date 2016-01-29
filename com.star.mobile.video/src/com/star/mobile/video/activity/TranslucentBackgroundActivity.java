package com.star.mobile.video.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.star.cms.model.Award;
import com.star.cms.model.Exchange;
import com.star.cms.model.dto.EggBreakResult;
import com.star.mobile.video.R;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.dialog.AlertDialog;
import com.star.mobile.video.me.coupon.MyCouponsActivity;
import com.star.mobile.video.service.EggService;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.ShareUtil;
import com.star.mobile.video.view.AnimView;
import com.star.mobile.video.view.AnimView.AnimationFinish;
import com.star.util.loader.OnResultListener;

public class TranslucentBackgroundActivity extends BaseActivity implements OnClickListener{
	
	private Button btShare,btCheck;
	private ImageView ivCloseEgg;
	private AnimView avAlertEgg; //彩蛋刚出来的动画
	private AnimView avSmashing; // 砸开彩蛋动画
	private AnimView avLoadingExchange;//加载优惠卷
	private RelativeLayout rlButton;
	private String eggCode;
	private String userName;
	private int userType;
	private EggService eggService;
	private Double faceValue;//优惠卷面值
	private RelativeLayout rlBreakEggExchage;//砸开的优惠卷
	private com.star.ui.ImageView ivExchageLogo;//优惠卷
	private TextView tvExchangeDes;// 优惠卷描述
	private ImageView ivExchangeBg;
	private LinearLayout llEx;
	private TextView tvSymbol;
	private TextView tvFac;
	private int notId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_translucent);
		eggService = new EggService(this);
		initView();
		initData();
	}
	
	
	private void initView() {
		btShare = (Button) findViewById(R.id.bt_share);
		btCheck = (Button) findViewById(R.id.bt_check);
		avAlertEgg = (AnimView) findViewById(R.id.v_alert_egg);
		avSmashing = (AnimView) findViewById(R.id.v_smashing_egg);
		ivCloseEgg = (ImageView) findViewById(R.id.iv_egg_close);
		avLoadingExchange = (AnimView) findViewById(R.id.v_loading_exchange);
		avAlertEgg.setCustomAnimResource(R.drawable.egg_alert_out_animation_list);
		rlButton = (RelativeLayout) findViewById(R.id.rl_button);
		rlBreakEggExchage = (RelativeLayout) findViewById(R.id.rl_exchange);
		ivExchageLogo = (com.star.ui.ImageView) findViewById(R.id.iv_exchange_logo);
		tvExchangeDes = (TextView) findViewById(R.id.tv_exchange_des);
		ivExchangeBg = (ImageView) findViewById(R.id.iv_exchange_bg);
		llEx = (LinearLayout) findViewById(R.id.ll_ex);
		tvSymbol = (TextView) findViewById(R.id.tv_symbol);
		tvFac = (TextView) findViewById(R.id.tv_fac);
		btShare.setOnClickListener(this);
		btCheck.setOnClickListener(this);
		avAlertEgg.setOnClickListener(this);
		ivCloseEgg.setOnClickListener(this);
	}

	
	private void initData() {
		Intent intent = getIntent();
		eggCode = intent.getStringExtra("eggCode");
		userName = intent.getStringExtra("userName");
		userType = intent.getIntExtra("userType", 0);
		notId = intent.getIntExtra("notId", -1);
		Exchange exchange = (Exchange) intent.getSerializableExtra("exchange");
		if(exchange!=null){
			toReceiveExchange(exchange);
			avAlertEgg.setVisibility(View.GONE);
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
//		NotificationUtil.clertNotification(notId);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.bt_check:
			CommonUtil.startActivity(TranslucentBackgroundActivity.this, MyCouponsActivity.class);
			CommonUtil.finishActivityAnimation(TranslucentBackgroundActivity.this);
			break;
		case R.id.bt_share:
			eggService.shareFreeCouponExchange(TranslucentBackgroundActivity.this, ShareUtil.BREAK_EGG_SHARE);
			break;
		case R.id.v_alert_egg:
			avSmashing.setVisibility(View.VISIBLE);
			avSmashing.setAnimationFinishListener(new AnimationFinish() {
				
				@Override
				public void finish() {
					getExchange();
				}
			});
			avSmashing.setCustomAnimResource(R.drawable.smashing_egg_animation_list);
			avAlertEgg.setVisibility(View.GONE);
			break;
		case R.id.iv_egg_close:
			finish();
			break;
			default:
				break;
		}
	}

	/**
	 * 加载优惠卷
	 */
	private void loadingExchange() {
		avSmashing.setVisibility(View.GONE);
		avLoadingExchange.setVisibility(View.VISIBLE);
		avLoadingExchange.setAnimResource(R.drawable.loading_exchange_animation_list);
	}
	
	/**
	 * 加载优惠卷成功
	 */
	private void loadingExchangeSuccess() {
		avLoadingExchange.setVisibility(View.GONE);
	}
	
	private void getExchange() {
		eggService.breakOneEgg(userName, eggCode, userType, new OnResultListener<EggBreakResult>() {
			
			@Override
			public void onSuccess(EggBreakResult eggBreakResult) {
				if(eggBreakResult != null) {
					smashingEggStatus(eggBreakResult);
				}
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
	}
	
	
	private void smashingEggStatus(EggBreakResult eggBreakResult) {
		int breakResult = eggBreakResult.getBreakResult(); 
		if(breakResult >= 0) {
			if(breakResult == EggBreakResult.Break_Success) { //成功砸开一个券
				toReceiveExchange(eggBreakResult.getExchange());
			} else if(breakResult == EggBreakResult.BreakReceive_Success) { //成功砸开一个券并领取成功
				toReceiveExchange(eggBreakResult.getExchange());
			}
		} else {
			AlertDialog dialog = new AlertDialog(TranslucentBackgroundActivity.this, false);
			dialog.setButtonText(getString(R.string.ok));
			dialog.setButtonOnClick(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					finish();
				}
			});
			if(breakResult == EggBreakResult.Egg_notExist) { //彩蛋不存在
				dialog.setMessage("");
				dialog.show();
			} else if(breakResult == EggBreakResult.Egg_Received) { //彩蛋已被砸开并被传入的邮箱领取
				dialog.setMessage("");
				dialog.show();
			} else if(breakResult == EggBreakResult.Break_failure) { //没砸出券
				dialog.setMessage("");
				dialog.show();
			} else if(breakResult == EggBreakResult.Egg_byYourself) { //自己分享出的彩蛋，不能砸开
				dialog.setMessage("");
				dialog.show();
			}
		} 
	}
	
	private void toReceiveExchange(Exchange exchange) {
		faceValue = exchange.getFaceValue();
		String url = exchange.getPoster().getResources().get(1).getUrl();
		ivExchageLogo.setUrl(url);
		int typeGet = exchange.getTypeGet();
		if(typeGet == Award.TYPE_NEW_CUSTOMER) {
			tvExchangeDes.setText(getString(R.string.first_coupon));
			ivExchangeBg.setBackgroundResource(R.drawable.frist_time_coupon_bg);
		} else if(typeGet == Award.TYPE_EXCHANGE) {
			tvExchangeDes.setText(getString(R.string.limited_coupon));
			ivExchangeBg.setBackgroundResource(R.drawable.limited_coupon_bg);
		} else if(typeGet == Award.FREE_COUPON) {
			llEx.setVisibility(View.VISIBLE);
			ivExchangeBg.setBackgroundResource(R.drawable.free_coupon_dec);
			tvSymbol.setText(SharedPreferencesUtil.getCurrencSymbol(this));
			tvFac.setText((int)exchange.getFaceValue()+"");
			tvExchangeDes.setText(getString(R.string.free_coupon));
		}
		loadingExchangeSuccess();
		rlBreakEggExchage.setVisibility(View.VISIBLE);
		rlButton.setVisibility(View.VISIBLE);
	}
}
