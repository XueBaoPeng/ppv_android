/**
 * 
 */
package com.star.mobile.video.smartcard.recharge;

import java.io.Serializable;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.star.cms.model.enm.TVPlatForm;
import com.star.cms.model.vo.ExchangeVO;
import com.star.cms.model.vo.SmartCardInfoVO;
import com.star.mobile.video.R;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.me.coupon.MyCouponsActivity;
import com.star.mobile.video.model.FunctionType;
import com.star.mobile.video.service.FunctionService;
import com.star.mobile.video.service.UserService;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.smartcard.SmartCardService;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.view.SmartCardInfoView;
import com.star.util.loader.OnResultListener;

/**
 * @author xbp
 * 
 *         2015年11月27日
 */
public class RechargeSmartCardActivity extends BaseActivity implements OnClickListener {
	private RelativeLayout rlRechargeCpn;
	private RelativeLayout rlRechargeSc;
	private RelativeLayout rlRechargePay;
	private ImageView lineView;
	private SmartCardInfoView smartcardinfoView;// 通用的smarcard头部信息
	private SmartCardInfoVO smartcardinfo;
	private UserService userService;
	private ExchangeVO rechargeEx;
	private String unit;
	private boolean doHide;
	private Double money;
	private SmartCardService mSmartCardService;
	private TVPlatForm platForm;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recharge_smartcard);
		smartcardinfo = (SmartCardInfoVO) getIntent().getSerializableExtra("smartcardinfovo");
		rechargeEx = (ExchangeVO) getIntent().getSerializableExtra("exchange");
		platForm = (TVPlatForm)getIntent().getSerializableExtra("platForm");
		doHide = getIntent().getBooleanExtra("hideCoupon", false);
		unit = SharedPreferencesUtil.getCurrencSymbol(RechargeSmartCardActivity.this);
		userService = new UserService();
		mSmartCardService = new SmartCardService(this);
		initView();
		getDetailSmartCardInfo(smartcardinfo);
		initData();
	}

	/**
	 * 
	 */
	private void initData() {
		smartcardinfoView.setData(smartcardinfo,platForm);
	}

	private void getDetailSmartCardInfo(final SmartCardInfoVO vo) {
		mSmartCardService.getSmartCardInfo(vo.getSmardCardNo(), new OnResultListener<SmartCardInfoVO>() {

			@Override
			public void onSuccess(SmartCardInfoVO value) {
				smartcardinfo = value;
				initData();
			}

			@Override
			public boolean onIntercept() {
				return false;
			}

			@Override
			public void onFailure(int errorCode, String msg) {
			}
		});
	}

	/**
	 * 
	 */
	private void initView() {
		rlRechargeCpn = (RelativeLayout) findViewById(R.id.btn_recharge_coupon);
		rlRechargeSc = (RelativeLayout) findViewById(R.id.btn_recharge_smartcard);
		rlRechargePay = (RelativeLayout) findViewById(R.id.btn_recharge_payment);
		lineView = (ImageView) findViewById(R.id.line_bg2);
		((TextView) findViewById(R.id.tv_actionbar_title)).setText(R.string.recharge_smart);
		findViewById(R.id.iv_actionbar_back).setOnClickListener(this);
		rlRechargeCpn.setOnClickListener(this);
		rlRechargeSc.setOnClickListener(this);
		rlRechargePay.setOnClickListener(this);
		smartcardinfoView = (SmartCardInfoView) findViewById(R.id.smardInfo);
		currentIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		currentIntent(intent);
		currentIntent(getIntent());
	}

	private void initViewStatus() {
		if (FunctionService.doHideFuncation(FunctionType.RechargeWithPaga)) {
			lineView.setVisibility(View.GONE);
			rlRechargePay.setVisibility(View.GONE);
			rlRechargePay.setOnClickListener(null);
		}
		if (FunctionService.doHideFuncation(FunctionType.RechargeCard)) {
			rlRechargeSc.setVisibility(View.GONE);
			rlRechargePay.setOnClickListener(null);
		}
		if (doHide) {
			rlRechargeCpn.setVisibility(View.GONE);
			rlRechargeCpn.setOnClickListener(null);
			TextView tvLimit = (TextView) findViewById(R.id.tv_recharge_limit);
			if (rechargeEx != null) {
				tvLimit.setVisibility(View.VISIBLE);
				if (rechargeEx.isAccepted()) {
					tvLimit.setText(RechargeSmartCardActivity.this.getString(R.string.smcartcard_used));
				} else {
					tvLimit.setText(String.format(getString(R.string.recharge_limit), unit + rechargeEx.getFaceValue(),
							unit + rechargeEx.getLessAmount()));
				}
			}
		}
	}

	private void currentIntent(Intent intent) {
		smartcardinfo = (SmartCardInfoVO) intent.getSerializableExtra("smartcardinfovo");
		rechargeEx = (ExchangeVO) intent.getSerializableExtra("exchange");
		doHide = intent.getBooleanExtra("hideCoupon", false);
		initViewStatus();
	}

	@Override
	protected void onStart() {
		super.onStart();
		getDetailSmartCardInfo(smartcardinfo);
		initData();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_recharge_coupon:
			Intent intent = new Intent();
			intent.putExtra("smartcardinfovo", (Serializable) smartcardinfo);
			intent.putExtra("exchange", rechargeEx);
			intent.setClass(RechargeSmartCardActivity.this, MyCouponsActivity.class);
			CommonUtil.startActivity(RechargeSmartCardActivity.this, intent);
			break;
		case R.id.btn_recharge_smartcard:
			Intent i = new Intent();
			i.putExtra("smartcardinfovo", (Serializable) smartcardinfo);
			i.putExtra("exchange", rechargeEx);
			i.setClass(RechargeSmartCardActivity.this, RechargeCardActivity.class);
			CommonUtil.startActivity(RechargeSmartCardActivity.this, i);
			break;
		case R.id.btn_recharge_payment:
			Intent inte = new Intent();
			inte.putExtra("exchange", rechargeEx);
			inte.putExtra("money", money);
			inte.putExtra("hideCoupon", doHide);
			inte.setClass(RechargeSmartCardActivity.this, OnlinePaymentActivity.class);
			CommonUtil.startActivity(RechargeSmartCardActivity.this, inte);
			break;
		case R.id.iv_actionbar_back:
			onBackPressed();
			break;
		default:
			break;
		}

	}

}
