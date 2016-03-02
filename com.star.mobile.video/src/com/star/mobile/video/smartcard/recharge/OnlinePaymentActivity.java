/**
 * 
 */
package com.star.mobile.video.smartcard.recharge;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.star.cms.model.User;
import com.star.cms.model.vo.ExchangeVO;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.activity.BrowserActivity;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.smartcard.SmartCardService;
import com.star.mobile.video.util.ApplicationUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.IOUtil;
import com.star.mobile.video.util.ToastUtil;
import com.star.util.loader.OnResultListener;

/**
 * @author xbp
 * 2015年11月28日
*/
public class OnlinePaymentActivity  extends BaseActivity implements OnClickListener{
	private View mView;
	private EditText etPayMoney;
	private TextView tvCurrencySymbol;
	private Button btRecharge;
	protected String selectSmartCardNo;
	protected String unit;
	protected double beforeAmount;
	protected ExchangeVO selectExchange;
	protected boolean doHide;
	private SmartCardService mSmartCardService;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_online_payment);
		unit = SharedPreferencesUtil.getCurrencSymbol(OnlinePaymentActivity.this);
		selectExchange = (ExchangeVO) getIntent().getSerializableExtra("exchange");
		doHide=getIntent().getBooleanExtra("hideCoupon", false);
		initView();
	}
	private void initView() {
		((TextView)findViewById(R.id.tv_actionbar_title)).setText(R.string.online_payment);
		etPayMoney = (EditText) findViewById(R.id.et_paymoney);
		tvCurrencySymbol = (TextView)findViewById(R.id.tv_currency_symbol);
		btRecharge = (Button)findViewById(R.id.bt_mob_reg_go);
		findViewById(R.id.iv_actionbar_back).setOnClickListener(this);
		tvCurrencySymbol.setText(unit);
		etPayMoney.addTextChangedListener(payTw);
		
		if(doHide){
			TextView tvLimit = (TextView) findViewById(R.id.tv_recharge_limit);
			tvLimit.setVisibility(View.VISIBLE);
			if(selectExchange!=null){
				tvLimit.setVisibility(View.VISIBLE);
				if(selectExchange.isAccepted()){
					tvLimit.setText(OnlinePaymentActivity.this.getString(R.string.smcartcard_used));
				}else{
					tvLimit.setText(String.format(getString(R.string.recharge_limit), unit+selectExchange.getFaceValue(), unit+selectExchange.getLessAmount()));
				}
			}
		}
		mSmartCardService = new SmartCardService(this);
	}
	
	private void goRecommendActivity(String key) {
		Intent i = new Intent(OnlinePaymentActivity.this, BrowserActivity.class);
		double money = Double.parseDouble(etPayMoney.getEditableText().toString());
		if(money < 50) {
			ToastUtil.centerShowToast(OnlinePaymentActivity.this, getString(R.string.minimum_amount));
			return;
		}
		String url = getString(R.string.paga_url)+"?money="+money+"&smartCardNo="+selectSmartCardNo+"&token="+IOUtil.getTOKEN()+"&t="+key+"&versionCode="+ApplicationUtil.getAppVerison(OnlinePaymentActivity.this);
		if(selectExchange != null){
			url = url+"&exchangeId="+selectExchange.getId();
			i.putExtra("exchange", selectExchange);
		}
		Log.i("TAG", url);
		i.putExtra("loadUrl",url);
		i.putExtra("pageName", getString(R.string.pay_title));
		i.putExtra("total", beforeAmount);
		CommonUtil.startActivity(OnlinePaymentActivity.this, i);
	}
	/**
	 * 校验
	 */
	private void verfiy(){
		if(StarApplication.mUser == null) {
			return;
		}
		mSmartCardService.bankToken(StarApplication.mUser, new OnResultListener<String>() {
			
			@Override
			public void onSuccess(String key) {
				CommonUtil.closeProgressDialog();
				User user = StarApplication.mUser;
				String o = System.nanoTime() + "";
				if(user != null) {
					key = user.genKey(o);
				}

				if(key != null){
					goRecommendActivity(key);
				}else{
					ToastUtil.centerShowToast(OnlinePaymentActivity.this, "network error!");
				}
				
			}
			
			@Override
			public boolean onIntercept() {
				CommonUtil.showProgressDialog(OnlinePaymentActivity.this);
				return false;
			}
			
			@Override
			public void onFailure(int errorCode, String msg) {
				CommonUtil.closeProgressDialog();
			}
		});
		
	}
	
	private TextWatcher payTw = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if(s.length() > 0 ) {
				if(s.charAt(0) != '0') {
					btRecharge.setBackgroundResource(R.drawable.orange_button_bg);
					btRecharge.setOnClickListener(OnlinePaymentActivity.this);
				} else {
					btRecharge.setBackgroundResource(R.drawable.need_more_coins_button);
					btRecharge.setOnClickListener(null);
				}
			} else {
				btRecharge.setBackgroundResource(R.drawable.need_more_coins_button);
				btRecharge.setOnClickListener(null);
			}
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			
		}
	};
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_mob_reg_go:
			verfiy();
			break;
		case R.id.iv_actionbar_back:
			onBackPressed();
			break;
		default:
			break;
		}
		 
	}
}
