package com.star.mobile.video.smartcard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.star.cms.model.BindCardCommand;
import com.star.cms.model.RechargeCMD;
import com.star.cms.model.vo.ChangePackageCMDVO;
import com.star.mobile.video.R;
import com.star.mobile.video.activity.BrowserActivity;
import com.star.mobile.video.dao.ServerUrlDao;
import com.star.mobile.video.me.feedback.FeedbackActivity;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.DifferentUrlContral;

public class AsynAlertDialogActivity extends Activity implements OnClickListener{
	private TextView mDialogPromptTitle;
	private TextView mDialogPromptContent;
	private TextView mDialogPromptOK;
	private TextView mDialgoPromptLater;
	private View view;
	private String title;
	private String message;
	private String cancel;
	private String confirm;
	private BindCardCommand bindCardCommand;
	private ChangePackageCMDVO changePackageCMDVO;
	private RechargeCMD rc;
	
	private String errorType;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prompt_dialog);
		initView();
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			title = bundle.getString("title");
			message = bundle.getString("message");
			cancel = bundle.getString("cancel");
			confirm = bundle.getString("confirm");
			bindCardCommand = (BindCardCommand) bundle.getSerializable("bindCardCommand");
			changePackageCMDVO=(ChangePackageCMDVO)bundle.getSerializable("changePackageCMDVO");
			rc = (RechargeCMD) bundle.getSerializable("rc");
			errorType=bundle.getString("errorType");
		}
		if(title!=null){
			setTitle(title);
		} 
		if(message!=null){
			setMessage(message);
		}
		if(cancel!=null){
			setCancelText(cancel);
		}
		if(errorType !=null){
			setCancelText(getString(R.string.feedback_big));
		}
		if(confirm!=null){
			setConfirmText(confirm);
		}
	}

	private void initView() {
		mDialogPromptTitle = (TextView) findViewById(R.id.prompt_title);
		mDialogPromptContent = (TextView) findViewById(R.id.prompt_content);
		mDialogPromptOK = (TextView) findViewById(R.id.prompt_ok);
		view = (View) findViewById(R.id.prompt_title_down);
		mDialgoPromptLater = (TextView) findViewById(R.id.prompt_later);
		mDialgoPromptLater.setVisibility(View.GONE);
		mDialogPromptTitle.setVisibility(View.GONE);
		view.setVisibility(View.VISIBLE);
		mDialogPromptOK.setOnClickListener(null);
		mDialgoPromptLater.setOnClickListener(null);
	}
	public void setTitle(String str) {
		if (str == null && str.isEmpty()) {
			mDialogPromptTitle.setVisibility(View.GONE);
			view.setVisibility(View.VISIBLE);
		}else{
			mDialogPromptTitle.setVisibility(View.VISIBLE);
			view.setVisibility(View.GONE);
			mDialogPromptTitle.setText(str);
		}
	}


	public void setMessage(String str) {
		mDialogPromptContent.setText(str);
	}


	public void setConfirmText(String text) {
		mDialogPromptOK.setText(text);
		mDialogPromptOK.setOnClickListener(this);
	}

	@SuppressWarnings("null")
	public void setCancelText(String text) {
		if (text == null && text.isEmpty()) {
			mDialgoPromptLater.setVisibility(View.GONE);
		}else{
			mDialgoPromptLater.setVisibility(View.VISIBLE);
			mDialgoPromptLater.setText(text);
			mDialgoPromptLater.setOnClickListener(this);
		}
	}

	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}


	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.prompt_ok:
			intent = new Intent();
			if(errorType!=null){
				ServerUrlDao serverUrlDao = DifferentUrlContral.diffUrlContral(this);
				intent.setClass(this, BrowserActivity.class);
				if(bindCardCommand !=null){
//					intent.putExtra("loadUrl", getString(R.string.bbs_faq_bindCard));
					intent.putExtra("loadUrl", serverUrlDao.getBBSFaqBindCard());
					String content=String.format(getString(R.string.bindCard_error), bindCardCommand.getSmartCardNo(),getString(R.string.binding_failure)+bindCardCommand.getAcceptStatus());
					intent.putExtra("selfServiceError", content);
				}else if(changePackageCMDVO !=null){
//					intent.putExtra("loadUrl", getString(R.string.bbs_faq_changebouquet));
					intent.putExtra("loadUrl", serverUrlDao.getBBSFaqChangeBouquet());
					String content=String.format(getString(R.string.changePackage_error), changePackageCMDVO.getFromPackageName(),changePackageCMDVO.getToPackageName(),changePackageCMDVO.getSmartCardNo(),getString(R.string.fail_to_change_your_bouquet)+changePackageCMDVO.getAccepStatus());
					intent.putExtra("selfServiceError", content);
				} else if(rc != null) {
//					intent.putExtra("loadUrl", getString(R.string.bbs_faq_recharge));
					intent.putExtra("loadUrl", serverUrlDao.getBBSFaqRecharge());
					String rechargeTypes = "";
					if(rc.getRechargeType() == RechargeCMD.RECHARGE_CARD_TYPE) {
						rechargeTypes = "recharge card";
					} else {
						rechargeTypes = "excharge ";
					}
					String content = getString(R.string.recharge_error,rc.getSmartCardNo(),rechargeTypes)+rc.getAcceptStatus();
					intent.putExtra("selfServiceError", content);
				}
				CommonUtil.startActivity(this, intent);
			}else{
				intent.setClass(this, MyOrderDetailActivity.class);
				if(bindCardCommand !=null){
					intent.putExtra("smsHistoryID", bindCardCommand.getId());
					intent.putExtra("smsHistoryType", bindCardCommand.getType());
				}else if(changePackageCMDVO !=null){
					intent.putExtra("smsHistoryID", changePackageCMDVO.getId());
					intent.putExtra("smsHistoryType", changePackageCMDVO.getType());
				} else if(rc != null) {
					intent.putExtra("smsHistoryID", rc.getId());
					intent.putExtra("smsHistoryType", rc.getType());
				}
				CommonUtil.startActivity(this, intent);	
			}
			finish();
			break;
		case R.id.prompt_later:
			if(errorType!=null){
				intent = new Intent();
				intent.setClass(this, FeedbackActivity.class);
				if(bindCardCommand !=null){
					String content=String.format(getString(R.string.bindCard_error), bindCardCommand.getSmartCardNo(),getString(R.string.binding_failure)+bindCardCommand.getAcceptStatus());
					intent.putExtra("content", content);
				}else if(changePackageCMDVO !=null){
					String content=String.format(getString(R.string.changePackage_error), changePackageCMDVO.getFromPackageName(),changePackageCMDVO.getToPackageName(),changePackageCMDVO.getSmartCardNo(),getString(R.string.fail_to_change_your_bouquet)+changePackageCMDVO.getAccepStatus());
					intent.putExtra("content", content);
				} else if(rc != null) {
					String rechargeTypes = "";
					if(rc.getRechargeType() == RechargeCMD.RECHARGE_CARD_TYPE) {
						rechargeTypes = "recharge card";
					} else {
						rechargeTypes = "excharge ";
					}
					intent.putExtra("content", getString(R.string.recharge_error,rc.getSmartCardNo(),rechargeTypes)+rc.getAcceptStatus());
				}
				CommonUtil.startActivity(this, intent);
			}
			finish();
			break;

		default:
			break;
		}
	}
	public com.star.mobile.video.smartcard.AsynAlertDialogActivity.systemDialogCallback getSystemDialogCallback() {
		return systemDialogCallback;
	}

	public void setSystemDialogCallback(com.star.mobile.video.smartcard.AsynAlertDialogActivity.systemDialogCallback systemDialogCallback) {
		this.systemDialogCallback = systemDialogCallback;
	}
	private com.star.mobile.video.smartcard.AsynAlertDialogActivity.systemDialogCallback systemDialogCallback;
	
	public interface systemDialogCallback{
		void onConfirm();
		void onCancel();
	}
}
