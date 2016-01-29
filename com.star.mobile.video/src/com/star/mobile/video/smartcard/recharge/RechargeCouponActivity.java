package com.star.mobile.video.smartcard.recharge;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.star.cms.model.Award;
import com.star.cms.model.RechargeCMD;
import com.star.cms.model.dto.RechargeResult;
import com.star.cms.model.vo.ExchangeVO;
import com.star.cms.model.vo.SmartCardInfoVO;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.changebouquet.ChangeSuccessActivity;
import com.star.mobile.video.dialog.RechargeDialog;
import com.star.mobile.video.me.coupon.MyCouponsActivity;
import com.star.mobile.video.me.mycoins.TaskService;
import com.star.mobile.video.service.UserService;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.smartcard.MyOrderActivity;
import com.star.mobile.video.smartcard.SmartCardService;
import com.star.mobile.video.util.ApplicationUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.ToastUtil;
import com.star.mobile.video.view.SmartCardInfoView;
import com.star.util.loader.OnResultListener;
 

/**
 * @author xbp


 * 2015年11月30日
*/
public class RechargeCouponActivity extends BaseActivity implements OnClickListener{
	private UserService userService;
	private Button btnRecharge;
	private TextView tvSymbol;
	private TextView tvFac;
	private String selectSmartCardNo;
	private ExchangeVO selectExchange;
	private SmartCardInfoView smView;
	private SmartCardInfoVO smartcardVo;
	private Double beforeAmount;
	private boolean isOnResume = false;
	private Double newmoney;
	private SmartCardService mSmartCardService;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recharge_coupon);
		userService = new UserService(); 
		btnRecharge = (Button)  findViewById(R.id.bt_mob_reg_go);
		tvSymbol = (TextView) findViewById(R.id.tv_symbol);
		smView=(SmartCardInfoView) findViewById(R.id.smardInfo);
		((TextView)findViewById(R.id.tv_actionbar_title)).setText(R.string.recharge);
		((ImageView) findViewById(R.id.iv_actionbar_back)).setOnClickListener(this);
		selectExchange = (ExchangeVO) getIntent().getSerializableExtra("exchange");
		smartcardVo=(SmartCardInfoVO) getIntent().getSerializableExtra("smartcardinfovo");
		mSmartCardService = new SmartCardService(this);
		if(smartcardVo!=null){
			selectSmartCardNo =smartcardVo.getSmardCardNo();
			getDetailSmartCardInfo(smartcardVo);
		}else{
			finish();
		}
		if(selectExchange.getType() == Award.Type_Wallet) {
			String symbol = SharedPreferencesUtil.getCurrencSymbol(RechargeCouponActivity.this);
			String price=(int)selectExchange.getFaceValue()+"";
			String symb=symbol+price+" Coupon "+RechargeCouponActivity.this.getString(R.string.coupon_msg);
			SpannableStringBuilder style=new SpannableStringBuilder(symb);
			style.setSpan(new ForegroundColorSpan(Color.rgb(255, 92, 05)),0, symbol.length()+price.length()+8,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 			
			tvSymbol.setText(style);
		}
		initView();
	}	 
	/**
	 * 
	 */
	private void initView() {
		smView.setData(smartcardVo);
	}
	private void getDetailSmartCardInfo(final SmartCardInfoVO vo) {
		CommonUtil.showProgressDialog(RechargeCouponActivity.this, null, getString(R.string.loading));
		mSmartCardService.getSmartCardInfo(vo.getSmardCardNo(), new OnResultListener<SmartCardInfoVO>() {

			@Override
			public void onSuccess(SmartCardInfoVO value) {
				CommonUtil.closeProgressDialog();
				if (value != null) {
					smartcardVo = value;
					initView();
					btnRecharge.setBackgroundResource(R.drawable.btn_orange);
					btnRecharge.setOnClickListener(RechargeCouponActivity.this);
				} else {
					btnRecharge.setBackgroundResource(R.drawable.btn_grey);
					btnRecharge.setOnClickListener(null);
				}
			}

			@Override
			public boolean onIntercept() {
				return false;
			}

			@Override
			public void onFailure(int errorCode, String msg) {
				CommonUtil.closeProgressDialog();
				btnRecharge.setBackgroundResource(R.drawable.btn_grey);
				btnRecharge.setOnClickListener(null);
			}
		});
	}
	@Override
	protected void onResume() {
		super.onResume();
		isOnResume=true;
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		isOnResume = false;
	}
	
	public void rechargeResult(final RechargeResult rr) {
		if(rr!=null){
			boolean exchage = rr.getExchangeMoney() != null &&  rr.getExchangeMoney() > 0;
			String by = "(COUPON"+rr.getExchangeStatus()+")";
			StarApplication.mTracker.send(new HitBuilders.EventBuilder().setCategory(Constant.GA_EVENT_BUSINESS)
				.setAction(Constant.GA_EVENT_RECHARGE).setLabel("SMARTCARD:"+selectSmartCardNo+"; STATUS:"+(exchage?"SUCCESS":"FAILURE"+"; WITH"+by)).setValue(1).build());
			if(rr.getExchangeMoney()!=null){
				smView.setRechargeAccountBalance(Double.toString(rr.getExchangeMoney()));
			}
		}
	
		if(rr!=null && rr.getByExchangeVO()!=null && (rr.getRechargeMoney()!=null||rr.getExchangeMoney()!=null)&& smartcardVo.getMoney()!=null){
			Dialog d = CommonUtil.showRechargeSuccessDialog(this, rr, smartcardVo.getMoney());
			d.setOnDismissListener(new Dialog.OnDismissListener() {
				
				@Override
				public void onDismiss(DialogInterface dialog) {
					if(rr.getExchangeMoney()!=null){
						selectExchange.setIsAccepted(true);
					}
				 
//					if(rr.getRechargeCoins() > 0) {
//						 showGoGetConisDialog(rr.getRechargeCoins());
//					}
					new TaskService(RechargeCouponActivity.this).showTaskDialog(RechargeCouponActivity.this, rr.getTaskResult());
					 
					userService.saveExpectedStopSmartcard(RechargeCouponActivity.this);
				}
			});
			return;
		}
		if(!isOnResume) { //如果activity 不在前台dialog 就不弹出
			return;
		}
		final RechargeDialog dialog = new RechargeDialog(RechargeCouponActivity.this);
		Double rechargeMoney = 0.0;
		dialog.setButtonOnClick(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				getDetailSmartCardInfo(smartcardVo);
				/*String currency = SharedPreferencesUtil.getCurrencSymbol(RechargeCouponActivity.this);
				smView.setRechargeAccountBalance(currency+newmoney);*/
			}
		});
		if(rr == null) {
			dialog.setRechargeFail(getString(R.string.boss_unavailable_now));
			dialog.setRechargeSuccessVisibility(View.GONE);
			dialog.setRechargeFailVisibility(View.VISIBLE);
			dialog.show();
			return;
		}
		if(rr.isRechargeCard()) {
			if(rr.getRechargeCardStatus() != null) {
				CommonUtil.closeProgressDialog();
				if(rr.getRechargeCardStatus() == RechargeResult.CARD_DOES_NOT_EXIST) {
					dialog.setRechargeFail(getString(R.string.recharge_card_number_is_invalid));
					dialog.setRechargeFailVisibility(View.VISIBLE);
				} else if(rr.getRechargeCardStatus() == RechargeResult.CUSTOMER_PASSWORD_IS_NOT_CORRECT) {
					dialog.setRechargeFail(getString(R.string.client_password_is_incorrect));
					dialog.setRechargeFailVisibility(View.VISIBLE);
				} else if(rr.getRechargeCardStatus() == RechargeResult.CARD_IS_ALREADY_IN_USE) {
					dialog.setRechargeFail(getString(R.string.recharge_card_number_has_been_used_before));
					dialog.setRechargeFailVisibility(View.VISIBLE);
				} else if(rr.getRechargeCardStatus() == RechargeResult.CARD_HAS_EXPIRED) {
					dialog.setRechargeFail(getString(R.string.recharge_card_has_expired));
					dialog.setRechargeFailVisibility(View.VISIBLE);
				} 
			}
			if(rr.getRechargeCardStatus()!= null) {
				if(rr.getRechargeCardStatus() == 0 || rr.getRechargeCardStatus() == 1) {
					rechargeMoney = rr.getRechargeMoney();
					dialog.setRechargeSuccessVisibility(View.VISIBLE);
				} else {
					if(dialog.getRechargeFail() == null) {
						dialog.setRechargeFail(getString(R.string.recharge_with_card));
					}
					dialog.setRechargeFailVisibility(View.VISIBLE);
				}
			}
		} else if(rr.isExchange()) {
			if(rr.getExchangeMoney() != null && rr.getExchangeMoney() > 0.0) {
				rechargeMoney = rr.getExchangeMoney();
				dialog.setRechargeSuccessVisibility(View.VISIBLE);
			} else {
				if(rr != null && rr.getStatus() == RechargeResult.FREQUENT) { //充值过于频繁 两次充值要间隔1分钟
					dialog.setRechargeFail(getString(R.string.recharget_frequent));
				} else {
					dialog.setRechargeFail(getString(R.string.recharge_failure));
				}
				if(rr.getExchangeStatus() != null) {
					if(rr.getExchangeStatus() == 500) {
						dialog.setConpouFail(getString(R.string.recharge_with_coupon));
						dialog.setRechargeFailVisibility(View.VISIBLE);
					} else if(rr.getExchangeStatus() == RechargeResult.EXCHANGE_FAILURE) {
						dialog.setConpouFail(getString(R.string.coupon_has_expired)); //优惠卷失效
						dialog.setRechargeFailVisibility(View.VISIBLE);
					} else if(rr.getExchangeStatus() == RechargeResult.HAVE_EXCHANGE) {
						dialog.setConpouFail(getString(R.string.coupon_has_expired)); //参数有误
						dialog.setRechargeFailVisibility(View.VISIBLE);
					} else if(rr.getExchangeStatus() == RechargeResult.FCC_LIMITED_COUNT) {
						dialog.setConpouFail(getString(R.string.first_times)); //新人卷使用超出十次
						dialog.setRechargeFailVisibility(View.VISIBLE);
					}
				}
			}
		} else {
			dialog.setConpouFail(getString(R.string.recharge_with_coupon));
			dialog.setRechargeSuccessVisibility(View.GONE);
			dialog.setRechargeFailVisibility(View.VISIBLE);
		}
		if( rechargeMoney > 0.0) {
			String msg = "<font color=#858585 size=18>"+getString(R.string.recharge_successfully_your_balance_now)+"</font>";
			String money = "<font color=#FF7E07 size=23 style='margin-left: 10px;'>"+SharedPreferencesUtil.getCurrencSymbol(RechargeCouponActivity.this)+rechargeMoney+"</font>";
			newmoney=rechargeMoney;
			dialog.setSuccessMsg(msg+money);
			dialog.setRechargeSuccessVisibility(View.VISIBLE);
			userService.saveExpectedStopSmartcard(RechargeCouponActivity.this);
		} 
		dialog.setOnDismissListener(new Dialog.OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				if (rr.getExchangeMoney() != null) {
					selectExchange.setIsAccepted(true);
					Intent intent = new Intent();
					intent.setClass(RechargeCouponActivity.this, MyCouponsActivity.class);
					CommonUtil.startActivity(RechargeCouponActivity.this, intent);
				}

				new TaskService(RechargeCouponActivity.this).showTaskDialog(RechargeCouponActivity.this, rr.getTaskResult());
				//initSmartCard();
			}
		});
		dialog.show();
	}
	private void recharge() {
		if(selectSmartCardNo==null || selectSmartCardNo.isEmpty()) {
			ToastUtil.centerShowToast(RechargeCouponActivity.this, getString(R.string.vouchno_can_not_be_empty));
			return;
		} 
		CommonUtil.showProgressDialog(RechargeCouponActivity.this, null, getString(R.string.recharging));
		List<Long> exchangeIDs = new ArrayList<Long>();
		if(selectExchange != null && !(selectExchange.isValid()||selectExchange.isAccepted())) {
			exchangeIDs.add(selectExchange.getId());
		}
		mSmartCardService.recharge(null, selectSmartCardNo, exchangeIDs, ApplicationUtil.getAppVerison(RechargeCouponActivity.this), smartcardVo.getMoney(), new OnResultListener<RechargeResult>() {

			@Override
			public boolean onIntercept() {
				return false;
			}

			@Override
			public void onSuccess(RechargeResult value) {
				CommonUtil.closeProgressDialog();
				rechargeResult(value);
			}

			@Override
			public void onFailure(int errorCode, String msg) {
				CommonUtil.closeProgressDialog();
				RechargeDialog dialog = new RechargeDialog(RechargeCouponActivity.this);
				dialog.setRechargeFail(getString(R.string.boss_unavailable_now));
				dialog.setRechargeSuccessVisibility(View.GONE);
				dialog.setRechargeFailVisibility(View.VISIBLE);
				dialog.show();
			}
		});
		
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_mob_reg_go:
//			recharge();
			syncRecharge();
			break;
		case R.id.iv_actionbar_back:
			onBackPressed();
			break;
		default:
			break;
		}
	}

	private void syncRecharge() {
		if(selectSmartCardNo==null || selectSmartCardNo.isEmpty()) {
			ToastUtil.centerShowToast(RechargeCouponActivity.this, getString(R.string.vouchno_can_not_be_empty));
			return;
		}
		CommonUtil.showProgressDialog(RechargeCouponActivity.this, null, getString(R.string.recharging));
		List<Long> exchangeIDs = new ArrayList<Long>();
		if(selectExchange != null && !(selectExchange.isValid()||selectExchange.isAccepted())) {
			exchangeIDs.add(selectExchange.getId());
		}

		mSmartCardService.rechargeCard(null,selectSmartCardNo,exchangeIDs,ApplicationUtil.getAppVerison(RechargeCouponActivity.this),
				smartcardVo.getMoney(),RechargeCMD.EXCHANGE_TYPE,new OnResultListener<Integer>(){

					@Override
					public boolean onIntercept() {
						return false;
					}

					@Override
					public void onSuccess(Integer value) {
						CommonUtil.closeProgressDialog();
						if(value == null) {
							RechargeDialog dialog = new RechargeDialog(RechargeCouponActivity.this);
							dialog.setRechargeFail(getString(R.string.boss_unavailable_now));
							dialog.setRechargeSuccessVisibility(View.GONE);
							dialog.setRechargeFailVisibility(View.VISIBLE);
							dialog.show();
							return;
						}
						if(value == RechargeCMD.SUCCESS) {
							Application a = getApplication();
							if(a instanceof StarApplication) {
								((StarApplication)a).finishActivityBClazz(RechargeSmartCardActivity.class);
								((StarApplication)a).finishActivityBClazz(MyCouponsActivity.class);
								((StarApplication)a).finishActivityBClazz(RechargeCouponActivity.class);
							}
							Intent intent = new Intent(RechargeCouponActivity.this,ChangeSuccessActivity.class);
							CommonUtil.startActivity(RechargeCouponActivity.this, intent);
						} else if(value == RechargeResult.EXCHANGE_FAILURE) {
							//优惠券不可用
							CommonUtil.getInstance().showPromptDialog(RechargeCouponActivity.this, getString(R.string.tips), getString(R.string.performed), getString(R.string.check), getString(R.string.later_big), new CommonUtil.PromptDialogClickListener() {

								@Override
								public void onConfirmClick() {
									Application a = getApplication();
									if(a instanceof StarApplication) {
										((StarApplication)a).finishActivityBClazz(RechargeSmartCardActivity.class);
										((StarApplication)a).finishActivityBClazz(MyCouponsActivity.class);
										((StarApplication)a).finishActivityBClazz(RechargeCouponActivity.class);
									}
									Intent intent = new Intent(RechargeCouponActivity.this, MyOrderActivity.class);
									CommonUtil.startActivity(RechargeCouponActivity.this,intent);
								}

								@Override
								public void onCancelClick() {
									onBackPressed();
								}
							});
						} else if(value.equals(RechargeCMD.RUNING_EXCHANGE)) {

							CommonUtil.getInstance().showPromptDialog(RechargeCouponActivity.this, getString(R.string.tips),
									getString(R.string.runing_exchange_recharge), getString(R.string.check), getString(R.string.later_big), new CommonUtil.PromptDialogClickListener() {

										@Override
										public void onConfirmClick() {
											Application a = getApplication();
											if(a instanceof StarApplication) {
												((StarApplication)a).finishActivityBClazz(RechargeSmartCardActivity.class);
												((StarApplication)a).finishActivityBClazz(MyCouponsActivity.class);
												((StarApplication)a).finishActivityBClazz(RechargeCouponActivity.class);
											}
											Intent intent = new Intent(RechargeCouponActivity.this, MyOrderActivity.class);
											CommonUtil.startActivity(RechargeCouponActivity.this,intent);
										}

										@Override
										public void onCancelClick() {
											onBackPressed();
										}
									});

						} else {
							//TODO 失败
						}
					}

					@Override
					public void onFailure(int errorCode, String msg) {
						CommonUtil.closeProgressDialog();
						RechargeDialog dialog = new RechargeDialog(RechargeCouponActivity.this);
						dialog.setRechargeFail(getString(R.string.boss_unavailable_now));
						dialog.setRechargeSuccessVisibility(View.GONE);
						dialog.setRechargeFailVisibility(View.VISIBLE);
						dialog.show();
					}
				});
	}
}
