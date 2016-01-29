package com.star.mobile.video.changebouquet;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.star.cms.model.Package;
import com.star.cms.model.vo.SmartCardInfoVO;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.service.PackageService;
import com.star.mobile.video.smartcard.MyOrderActivity;
import com.star.mobile.video.smartcard.SmartCardControlActivity;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.CommonUtil.PromptDialogClickListener;
import com.star.util.loader.OnResultListener;

public class EnterDecoderNumberActivity extends BaseActivity implements OnClickListener{
	private EditText mEnterDecoderNumberET;
	private Button mChangePackageButton;
	private String mCurrentSmartCardNO;
	private SmartCardInfoVO mSmartCardInfoVO;
	private Package mChangeToPkg = null;
	private String mCurrentPackageName;
	private PackageService pkgService;
	private final int WHAT_ERROR = 3;
	private final int PERFORMED = 4;//正在执行换包任务
	public Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WHAT_ERROR:
				CommonUtil.getInstance().showPromptDialog(EnterDecoderNumberActivity.this, getString(R.string.tips), getString(R.string.change_pkg_error_message), getString(R.string.ok), null, new PromptDialogClickListener() {
					
					@Override
					public void onConfirmClick() {
						
					}
					
					@Override
					public void onCancelClick() {
						
					}
				});
				break;
				case PERFORMED:
					CommonUtil.getInstance().showPromptDialog(EnterDecoderNumberActivity.this, getString(R.string.tips), getString(R.string.performed), getString(R.string.check), getString(R.string.later_big), new PromptDialogClickListener() {

						@Override
						public void onConfirmClick() {
							Intent intent = new Intent(EnterDecoderNumberActivity.this, MyOrderActivity.class);
							CommonUtil.startActivity(EnterDecoderNumberActivity.this,intent);
						}

						@Override
						public void onCancelClick() {
							onBackPressed();
						}
					});
					break;
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_verify_decoder_number);
		Intent intent = getIntent();
		if (intent != null) {
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				mSmartCardInfoVO = (SmartCardInfoVO) intent.getSerializableExtra("smartCardInfoVO");
				mChangeToPkg = (Package) bundle.getSerializable("changeToPkg");
				mCurrentSmartCardNO = mSmartCardInfoVO.getSmardCardNo();
			}
		}
		initView();
		initData();
	}
	private void initView() {
		mEnterDecoderNumberET = (EditText) findViewById(R.id.edittext_enter_decoder_number);
		mChangePackageButton = (Button) findViewById(R.id.change_package_button);
		((ImageView) findViewById(R.id.iv_actionbar_back)).setOnClickListener(this);
		((TextView)findViewById(R.id.tv_actionbar_title)).setText(R.string.change_bouquet);
	}
	
	private void initData(){
		pkgService = new PackageService(this);
		mChangePackageButton.setOnClickListener(this);
		mEnterDecoderNumberET.addTextChangedListener(new EditChangedListener());
	}
	
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.change_package_button:
			//换包按钮
			changePackage();
			break;
		case R.id.iv_actionbar_back:
			onBackPressed();
			break;
		default:
			break;
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
			if (s.length() >= 6) {
				mChangePackageButton.setEnabled(true);
				mChangePackageButton.setBackgroundResource(R.drawable.orange_button_bg);
			}else {
				mChangePackageButton.setEnabled(false);
				mChangePackageButton.setBackgroundResource(R.drawable.btn_grey);
			}
		}

		@Override
		public void afterTextChanged(Editable s) {

		}
	};
	
	private void changePackage(){
		if(TextUtils.isEmpty(mCurrentSmartCardNO) || mChangeToPkg==null || mEnterDecoderNumberET.getText().length()!=6)
			return;
		pkgService.changePackage(mCurrentSmartCardNO,mSmartCardInfoVO.getProductCode(), mChangeToPkg.getBossPackageCode(), mEnterDecoderNumberET.getText().toString(),2, new OnResultListener<Integer>() {
			
			@Override
			public void onSuccess(Integer result) {
				CommonUtil.closeProgressDialog();
				if(result ==1 ){
					if (getApplication() instanceof StarApplication) {
						((StarApplication) getApplication()).finishActivityBClazz(ChangeBouquetActivity.class);
						((StarApplication) getApplication()).finishActivityBClazz(ChangeWayActivity.class);
						((StarApplication) getApplication()).finishActivityBClazz(EnterDecoderNumberActivity.class);
					}
					Intent intent = new Intent(EnterDecoderNumberActivity.this,ChangeSuccessActivity.class);
					CommonUtil.startActivity(EnterDecoderNumberActivity.this, intent);
				} else if(result == PERFORMED) { //该卡还有未执行的换包任务
					handler.sendEmptyMessage(PERFORMED);
				} else {
					handler.sendEmptyMessage(WHAT_ERROR);
				}
//				String r = "FAILURE";
//				switch (result) {
//				case ChangePackageCode.CHANGE_SUCCESS:
//					if(mChangeToPkg != null){
//						mCurrentPackageName  = mChangeToPkg.getName();
//					}
//					mEnterDecoderNumberET.getEditableText().clear();
//					showChangePackageDialog();
//					r = "SUCCESS";
//					break;
//				case ChangePackageCode.BOX_NUMBER_NOT_MATCH:
//					ToastUtil.centerShowToast(EnterDecoderNumberActivity.this, getString(R.string.smartcard_number_does));
//					r = "BOX_SC_NOT_MATCH";
//					break;
//				case ChangePackageCode.LACK_BALANCE: //余额不足
//					ToastUtil.centerShowToast(EnterDecoderNumberActivity.this, getString(R.string.account_balance_is_not_enough));
//					r = "LACK_BALANCE";
//					break;
//				default:
//					ToastUtil.centerShowToast(EnterDecoderNumberActivity.this, getString(R.string.fail_to_change_your_bouquet)+result);
//					r = r+";ERROR_CODE="+result;
//					break;
//				}
//				StarApplication.mTracker.send(new HitBuilders.EventBuilder().setCategory(Constant.GA_EVENT_BUSINESS)
//						.setAction(Constant.GA_EVENT_CHANGE_PACKAGE).setLabel("SMARTCARD:"+mCurrentSmartCardNO+"; STATUS:"+r).setValue(1).build());
			}
			
			@Override
			public boolean onIntercept() {
				CommonUtil.showProgressDialog(EnterDecoderNumberActivity.this);
				return false;
			}
			
			@Override
			public void onFailure(int errorCode, String msg) {
				CommonUtil.closeProgressDialog();
				handler.sendEmptyMessage(WHAT_ERROR);
//				String r = "FAILURE";
//				ToastUtil.centerShowToast(EnterDecoderNumberActivity.this, getString(R.string.fail_to_change_your_bouquet)+(-1));
//				r = r+";ERROR_CODE="+(-1);
//				StarApplication.mTracker.send(new HitBuilders.EventBuilder().setCategory(Constant.GA_EVENT_BUSINESS)
//						.setAction(Constant.GA_EVENT_CHANGE_PACKAGE).setLabel("SMARTCARD:"+mCurrentSmartCardNO+"; STATUS:"+r).setValue(1).build());
			}
		});
	}
	public void showChangePackageDialog(){
		String text = getString(R.string.have_recharged);
		String text1 = text+" "+mCurrentPackageName+"!";
		SpannableStringBuilder style = new SpannableStringBuilder(text1);
		style.setSpan(new StyleSpan(Typeface.BOLD), text.length(), text1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); 
		style.setSpan(new ForegroundColorSpan(Color.parseColor("#FF7E07")), text.length(), text1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		CommonUtil.getInstance().showPromptDialog(EnterDecoderNumberActivity.this, null, style.toString(), getString(R.string.ok), null, new CommonUtil.PromptDialogClickListener() {
			
			@Override
			public void onConfirmClick() {
				finish();
				Intent intent = new Intent();
				intent.putExtra("changePkgSmartCardNumber", mCurrentSmartCardNO);
				intent.setClass(EnterDecoderNumberActivity.this, SmartCardControlActivity.class);
				CommonUtil.startActivity(EnterDecoderNumberActivity.this, intent);
			}
			
			@Override
			public void onCancelClick() {
			}
		});
	}
}
