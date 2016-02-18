package com.star.mobile.video.smartcard;

import java.util.List;
import java.util.Random;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.star.cms.model.dto.BindCardResult;
import com.star.cms.model.vo.SmartCardInfoVO;
import com.star.mobile.video.R;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.service.UserService;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.smartcard.BindCardEditViewA.CardNumberACallback;
import com.star.mobile.video.smartcard.BindCardEditViewB.CardNumberBCallback;
import com.star.mobile.video.util.ABTestSharedPre;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.CommonUtil.PromptDialogClickListener;
import com.star.mobile.video.util.ToastUtil;
import com.star.util.loader.OnResultListener;

public class SmartCardActivity extends BaseActivity implements OnClickListener,CardNumberACallback,CardNumberBCallback{

//	private EditText etCardNum;
	// private EditText etStbNum;
	private List<SmartCardInfoVO> mSmartinfos;
	private Button btnOk;
	private UserService userService;
	private SharedPreferences mSharePre;
	private SmartCardService mSmartCardService;
	private LinearLayout ll_card_num;
	private BindCardEditViewB bindCardEditViewB;
	private BindCardEditViewA bindCardEditViewA;
//	private SmartCardSharedPre sharedPre;
	private ABTestSharedPre abSharePre;
	private String cardNum = null;
	private BindCardDialog bindCardDialog;
	private boolean isrun=true;
	private int runnum=0;
	private final int WHAT_ERROR = 3;
	private final int WHAT_AWAIT = 2;
	public Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WHAT_AWAIT:
				CommonUtil.getInstance().showPromptDialog(SmartCardActivity.this,
						getString(R.string.tips), getString(R.string.bind_tip_message), SmartCardActivity.this.getString(R.string.ok), null, new PromptDialogClickListener() {
					
					@Override
					public void onConfirmClick() {
						Intent intent =new Intent(SmartCardActivity.this,MyOrderActivity.class);
						CommonUtil.startActivity(SmartCardActivity.this, intent);
						SmartCardActivity.this.finish();
					}
					
					@Override
					public void onCancelClick() {
						
					}
				});
				break;
			case WHAT_ERROR:
				CommonUtil.getInstance().showPromptDialog(SmartCardActivity.this,
						getString(R.string.tips), getString(R.string.bind_card_failure), SmartCardActivity.this.getString(R.string.ok), null, new PromptDialogClickListener() {
					
					@Override
					public void onConfirmClick() {
						SmartCardActivity.this.finish();
					}
					
					@Override
					public void onCancelClick() {
						
					}
				});
				break;
				case BindCardResult.SMART_CARD_IS_BIND:
					CommonUtil.getInstance().showPromptDialog(SmartCardActivity.this, getString(R.string.tips), getString(R.string.performed), getString(R.string.check), getString(R.string.later_big), new CommonUtil.PromptDialogClickListener() {

						@Override
						public void onConfirmClick() {
							Intent intent = new Intent(SmartCardActivity.this, MyOrderActivity.class);
							CommonUtil.startActivity(SmartCardActivity.this,intent);
							SmartCardActivity.this.finish();
						}

						@Override
						public void onCancelClick() {
							onBackPressed();
						}
					});
					break;
				default:
					break;
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_smartcard);
		Intent intent = getIntent();
		mSmartinfos = (List<SmartCardInfoVO>) intent.getSerializableExtra("smartinfos");
		userService = new UserService();
		abSharePre = new ABTestSharedPre(this);
//		sharedPre = new SmartCardSharedPre(this);
		bindCardDialog =new BindCardDialog(this);
		mSharePre = SharedPreferencesUtil.getGuideSharePreferencesByUser(this);
		initView();
//		showGuide();
	}

	private void initView() {
		ll_card_num = (LinearLayout)findViewById(R.id.ll_card_num);
//		etCardNum = (EditText) findViewById(R.id.et_card_num);
//		etCardNum.setOnClickListener(this);
		// etStbNum = (EditText) findViewById(R.id.et_stb_num);
		btnOk = (Button) findViewById(R.id.bt_ok);
		btnOk.setEnabled(false);
		ButtonOnClick bc = new ButtonOnClick();
		((TextView) findViewById(R.id.tv_actionbar_title))
				.setText(getResources().getString(R.string.smart_card_activity_title));
		((ImageView) findViewById(R.id.iv_actionbar_back)).setOnClickListener(bc);
//		etCardNum.setText(getIntent().getStringExtra("cardId"));
//		etCardNum.addTextChangedListener(new EditChangedListener());
		btnOk.setOnClickListener(bc);
		// findViewById(R.id.iv_alert).setOnClickListener(bc);
		mSmartCardService = new SmartCardService(this);
		bindCardEditViewB  = new BindCardEditViewB(this);
		bindCardEditViewA =new BindCardEditViewA(this);
		bindCardEditViewA.setCardNumberACallback(this);
		bindCardEditViewB.setCardNumberBCallback(this);
		 if(abSharePre.getABTest().equals("A")){
			 ll_card_num.addView(bindCardEditViewA);
		}else if(abSharePre.getABTest().equals("B")){
			 ll_card_num.addView(bindCardEditViewB);
		}
		
	}
//	private void showGuide() {
//		boolean isShown = getIntent().getBooleanExtra("showFrame", false);
//		if (isShown)
//			MaskUtil.showBindSmardcardGuide(this);
//	}

	/**
	 * 绑定智能卡
	 */
	private void bindingCard() {
		if(abSharePre.getABTest().equals("A")){
			cardNum = bindCardEditViewA.getEditText();
		}else{
			cardNum = bindCardEditViewB.getEditText();
		}

		for (int i = 0;i<mSmartinfos.size();i++){
			if (mSmartinfos.get(i).equals(cardNum)){
				//TODO

			}
		}

		bindCardDialog.showDialog();
//		CommonUtil.showProgressDialog(SmartCardActivity.this, null, getString(R.string.binding));

		mSmartCardService.getbindSmartCard(cardNum, /*"", ApplicationUtil.getAppVerison(SmartCardActivity.this),*/ new OnResultListener<Integer>() {

			@Override
			public boolean onIntercept() {
				return false;
			}

			@Override
			public void onSuccess(Integer ss) {
				isrun=false;
				runnum=0;
				if(bindCardDialog !=null && bindCardDialog.isShowing()){
					bindCardDialog.dismiss();
				
//				CommonUtil.closeProgressDialog();
//				String result = "FAILURE";
					if(ss == 1){
						handler.sendEmptyMessage(WHAT_AWAIT);
					} else if(ss == BindCardResult.SMART_CARD_IS_BIND) { // 智能卡已经绑定
						handler.sendEmptyMessage(BindCardResult.SMART_CARD_IS_BIND);
					}else {
						handler.sendEmptyMessage(WHAT_ERROR);
					}
				}
			}

			@Override
			public void onFailure(int errorCode, String msg) {
				isrun=false;
				runnum=0;
				if(bindCardDialog !=null && bindCardDialog.isShowing()){
					bindCardDialog.dismiss();
					handler.sendEmptyMessage(WHAT_ERROR);
				}
//				bindCardDialog.dismissDialog();
//				CommonUtil.closeProgressDialog();
			}
		});
		new SmartCardSharedPre(this).setFirstBindStatus(0);
	}
	/**
	 * 发送卡号给调用者
	 * @param cardNum
	 */
	private void sendCardNo(final String cardNum) {
		Intent intent = new Intent();
		intent.putExtra("bindSmartCardNO", cardNum);
		//设置返回数据
		setResult(RESULT_OK, intent);
	}
	private void showToast(String msg) {
		ToastUtil.centerShowToast(SmartCardActivity.this, msg);
	}

	private class ButtonOnClick implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_actionbar_back:
				onBackPressed();
				break;
			case R.id.bt_ok:
				bindingCard();
				break;
			case R.id.iv_alert:
				CommonUtil.showAddSmartCardInfoDialog(SmartCardActivity.this);
				break;
			default:
				break;
			}
		}

	}

	/**
	 * 监听输入框的事件
	 * @author lee
	 *
	 */
	class EditChangedListener implements TextWatcher {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			//如果有文字输入的时候，确认按钮颜色改变
			if (s.length() > 0) {
				btnOk.setEnabled(true);
				btnOk.setBackgroundResource(R.drawable.orange_button_bg);
			}else {
				btnOk.setEnabled(false);
				btnOk.setBackgroundResource(R.drawable.btn_grey);
			}
		}

		@Override
		public void afterTextChanged(Editable s) {

		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
//		case R.id.et_card_num:
////			etCardNum.setCursorVisible(true);
////			if (etCardNum.getHint().toString() != null) {
////				etCardNum.setHint("");
////			}
//			break;

		default:
			break;
		}
	}

	@Override
	public void onNotice(boolean isShow) {
		if(isShow){
			btnOk.setEnabled(true);
			btnOk.setBackgroundResource(R.drawable.orange_button_bg);
		}else{
			btnOk.setEnabled(false);
			btnOk.setBackgroundResource(R.drawable.btn_grey);
		}
	}

}
