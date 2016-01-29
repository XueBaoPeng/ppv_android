/**
 * 
 */
package com.star.mobile.video.smartcard.recharge;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.star.cms.model.RechargeCMD;
import com.star.cms.model.dto.RechargeResult;
import com.star.cms.model.vo.ExchangeVO;
import com.star.cms.model.vo.SmartCardInfoVO;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.changebouquet.ChangeSuccessActivity;
import com.star.mobile.video.dialog.RechargeDialog;
import com.star.mobile.video.me.mycoins.TaskService;
import com.star.mobile.video.service.UserService;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.smartcard.MyOrderActivity;
import com.star.mobile.video.smartcard.SmartCardService;
import com.star.mobile.video.util.ApplicationUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.ToastUtil;
import com.star.util.loader.OnResultListener;

/**
 * @author xbp
 * 2015年11月28日
*/
public class RechargeCardActivity extends BaseActivity implements OnClickListener{
	private View mView;
	private EditText etVouchNo;
	private View btConfirm;
	private UserService userService;
	private double beforeAmount;
	protected String selectSmartCardNo;
	protected SmartCardInfoVO SmartcardVo;
	protected ExchangeVO selectExchange;
	private boolean isOnResume = false;
	protected String unit;
	protected boolean doHide;
	private SmartCardService mSmartCardService;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recharge_card);
		unit = SharedPreferencesUtil.getCurrencSymbol(RechargeCardActivity.this);
		selectExchange = (ExchangeVO) getIntent().getSerializableExtra("exchange");
		SmartcardVo=(SmartCardInfoVO) getIntent().getSerializableExtra("smartcardinfovo");
		selectSmartCardNo = SmartcardVo.getSmardCardNo();
		userService = new UserService();
		initView();
	}
	/**
	 * 
	 */
	private void initView() {
		((TextView)findViewById(R.id.tv_actionbar_title)).setText(R.string.recharge_card);
		etVouchNo = (EditText) findViewById(R.id.et_recharge_no);
		etVouchNo.addTextChangedListener(tw);
		etVouchNo.setOnFocusChangeListener(new EditFocusChange());
		btConfirm = findViewById(R.id.bt_mob_reg_go);	
		findViewById(R.id.iv_actionbar_back).setOnClickListener(this);

		if(doHide){
			TextView tvLimit = (TextView) mView.findViewById(R.id.tv_recharge_limit);
			tvLimit.setVisibility(View.VISIBLE);
			if(selectExchange!=null){
				tvLimit.setVisibility(View.VISIBLE);
				if(selectExchange.isAccepted()){
					tvLimit.setText(RechargeCardActivity.this.getString(R.string.smcartcard_used));
				}else{
					tvLimit.setText(String.format(getString(R.string.recharge_limit), unit+selectExchange.getFaceValue(), unit+selectExchange.getLessAmount()));
				}
			}
		}
		mSmartCardService = new SmartCardService(this);
	}
	 
	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
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
	private class EditFocusChange implements OnFocusChangeListener {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
//			switch (v.getId()) {
//			case R.id.et_recharge_no:
//				if(hasFocus) {
//					etVouchNo.setHint(getString(R.string.please_enter_numbers));
//				} else {
//					etVouchNo.setHint(getString(R.string.vouch_no));
//				}
//				break;
//			default:
//				break;
//			}
		}
	}
	private TextWatcher tw = new TextWatcher() {
		
		int beforeTextLength = 0;  
        int onTextLength = 0;  
        boolean isChanged = false;  

        int location = 0;// 记录光标的位置  
        private char[] tempChar;  
        private StringBuffer buffer = new StringBuffer();  
        int konggeNumberB = 0; 
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			 onTextLength = s.length();  
			 if(onTextLength < 12) {
				 btConfirm.setBackgroundResource(R.drawable.need_more_coins_button);
				 btConfirm.setOnClickListener(null);
			 } else {
				 btConfirm.setBackgroundResource(R.drawable.orange_button_bg);
				 btConfirm.setOnClickListener(RechargeCardActivity.this);
			 }
            buffer.append(s.toString());  
            if (onTextLength == beforeTextLength || onTextLength <= 3  
                    || isChanged) {  
                isChanged = false;  
                return;  
            }  
            isChanged = true;  
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			beforeTextLength = s.length();  
           if (buffer.length() > 0) {  
               buffer.delete(0, buffer.length());  
           }  
           konggeNumberB = 0;  
           for (int i = 0; i < s.length(); i++) {  
               if (s.charAt(i) == ' ') {  
                   konggeNumberB++;  
               }  
           }  
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			 if (isChanged) {  
                location = etVouchNo.getSelectionEnd();  
                int index = 0;  
                while (index < buffer.length()) {  
                    if (buffer.charAt(index) == ' ') {  
                        buffer.deleteCharAt(index);  
                    } else {  
                        index++;  
                    }  
                }  
                index = 0;  
                int konggeNumberC = 0;  
                while (index < buffer.length()) {  
                    if ((index == 4 || index == 9 || index == 14 || index == 19)) {  
                        buffer.insert(index, ' ');  
                        konggeNumberC++;  
                    }  
                    index++;  
                }  
                if (konggeNumberC > konggeNumberB) {  
                    location += (konggeNumberC - konggeNumberB);  
                }  
                tempChar = new char[buffer.length()];  
                buffer.getChars(0, buffer.length(), tempChar, 0);  
                String str = buffer.toString();  
                if (location > str.length()) {  
                    location = str.length();  
                } else if (location < 0) {  
                    location = 0;  
                }  
                etVouchNo.setText(str);  
                Editable etable = etVouchNo.getText();  
                Selection.setSelection(etable, location);  
                isChanged = false;  
            }  
		}
	};
	public void rechargeResult(final RechargeResult rr) {
		if(rr!=null){
			boolean recharge = rr.getRechargeMoney()!=null&&rr.getRechargeMoney()>0;
			String by = "(CARD"+rr.getRechargeCardStatus()+")";
			StarApplication.mTracker.send(new HitBuilders.EventBuilder().setCategory(Constant.GA_EVENT_BUSINESS)
				.setAction(Constant.GA_EVENT_RECHARGE).setLabel("SMARTCARD:"+selectSmartCardNo+"; STATUS:"+(recharge?"SUCCESS":"FAILURE"+"; WITH"+by)).setValue(1).build());
			
		}
	
		if(rr!=null && rr.getByExchangeVO()!=null && (rr.getRechargeMoney()!=null||rr.getExchangeMoney()!=null)&& SmartcardVo.getMoney()!=null){
			Dialog d = CommonUtil.showRechargeSuccessDialog(this, rr, SmartcardVo.getMoney());
			d.setOnDismissListener(new Dialog.OnDismissListener() {
				
				@Override
				public void onDismiss(DialogInterface dialog) {
					if(rr.getExchangeMoney()!=null){
						selectExchange.setIsAccepted(true);
					}
					new TaskService(RechargeCardActivity.this).showTaskDialog(RechargeCardActivity.this, rr.getTaskResult());
					userService.saveExpectedStopSmartcard(RechargeCardActivity.this);
				}
			});
			return;
		}
		if(!isOnResume) { //如果activity 不在前台dialog 就不弹出
			return;
		}
		final RechargeDialog dialog = new RechargeDialog(RechargeCardActivity.this);
		Double rechargeMoney = 0.0;
		dialog.setButtonOnClick(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				onBackPressed();
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
//				else if("通过智能卡找不到合法的用户，合法用户指有效、暂停、罚停的用户".contains(rr.getRechargeCardMessage())) {
//					dialog.setRechargeFail("The valid user can't be found with the smart card.");
//					dialog.setRechargeFailVisibility(View.VISIBLE);
//				}
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
			//充值失败
			if(rr != null && rr.getStatus() == RechargeResult.FREQUENT) { //充值过于频繁 两次充值要间隔1分钟
				dialog.setRechargeFail(getString(R.string.recharget_frequent));
			} else {
				dialog.setRechargeFail(getString(R.string.recharge_failure));
			}
			dialog.setRechargeSuccessVisibility(View.GONE);
			dialog.setRechargeFailVisibility(View.VISIBLE);
		}
		if( rechargeMoney > 0.0) {
			String msg = "<font color=#858585 size=18>"+getString(R.string.recharge_successfully_your_balance_now)+"</font>";
			String money = "<font color=#FF7E07 size=23 style='margin-left: 10px;'>"+SharedPreferencesUtil.getCurrencSymbol(RechargeCardActivity.this)+rechargeMoney+"</font>";
			dialog.setSuccessMsg(msg+money);
			dialog.setRechargeSuccessVisibility(View.VISIBLE);
			userService.saveExpectedStopSmartcard(RechargeCardActivity.this);
		} 
		dialog.setOnDismissListener(new Dialog.OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				if(rr.getExchangeMoney()!=null){
					selectExchange.setIsAccepted(true);
				}
				 
				new TaskService(RechargeCardActivity.this).showTaskDialog(RechargeCardActivity.this, rr.getTaskResult());
				//initSmartCard();
			}
		});
		dialog.show();
	}
	
	private void recharge() {
		if(selectSmartCardNo==null || selectSmartCardNo.isEmpty()) {
			ToastUtil.centerShowToast(RechargeCardActivity.this, getString(R.string.vouchno_can_not_be_empty));
			return;
		} 
		CommonUtil.showProgressDialog(RechargeCardActivity.this, null, getString(R.string.recharging));
		
		String vouchNo = null;
		List<Long> exchangeIDs = new ArrayList<Long>();
		if(selectExchange != null && !(selectExchange.isValid()||selectExchange.isAccepted())) {
			exchangeIDs.add(selectExchange.getId());
		}
		vouchNo = etVouchNo.getEditableText().toString().replace(" ", "");
		mSmartCardService.rechargeCard(vouchNo, selectSmartCardNo, exchangeIDs, ApplicationUtil.getAppVerison(RechargeCardActivity.this), SmartcardVo.getMoney(), new OnResultListener<Integer>() {

			@Override
			public boolean onIntercept() {
				return false;
			}

			@Override
			public void onSuccess(Integer value) {
				CommonUtil.closeProgressDialog();
				if(value != null && value == RechargeCMD.SUCCESS) {
					Application a = getApplication();
					if(a instanceof StarApplication) {
						((StarApplication)a).finishActivityBClazz(RechargeSmartCardActivity.class);
						((StarApplication)a).finishActivityBClazz(RechargeCardActivity.class);
					}
					Intent intent = new Intent(RechargeCardActivity.this,ChangeSuccessActivity.class);
					CommonUtil.startActivity(RechargeCardActivity.this, intent);
				} else if(value == RechargeCMD.RECHARGE_CARD_EXIST) {
					//充值卡已存在
					CommonUtil.getInstance().showPromptDialog(RechargeCardActivity.this, getString(R.string.tips), getString(R.string.performed), getString(R.string.check), getString(R.string.later_big), new CommonUtil.PromptDialogClickListener() {

						@Override
						public void onConfirmClick() {
							Application a = getApplication();
							if(a instanceof StarApplication) {
								((StarApplication)a).finishActivityBClazz(RechargeSmartCardActivity.class);
								((StarApplication)a).finishActivityBClazz(RechargeCardActivity.class);
							}
							Intent intent = new Intent(RechargeCardActivity.this, MyOrderActivity.class);
							CommonUtil.startActivity(RechargeCardActivity.this,intent);
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
			}
		});
		
	}
	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.bt_mob_reg_go){
			recharge();
		}
		if(v.getId()==R.id.iv_actionbar_back){
			onBackPressed();
		}
	}
}
