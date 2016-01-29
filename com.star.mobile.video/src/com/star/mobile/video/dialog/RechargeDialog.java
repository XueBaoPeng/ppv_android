package com.star.mobile.video.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.star.mobile.video.R;

public class RechargeDialog extends Dialog{
	
	private LinearLayout llRechargeFail;//充值失败
	private TextView tvRechargeFail;//充值卡充值失败
	private TextView tvConpouFail;//优惠卷充值失败
	private Button rechargeDialogBtn;
	private EditText etSuccessMsg;
	
	
	public RechargeDialog(Context context) {
		super(context, R.style.TaskInfoDialog);
		setContentView(R.layout.recharge_dialog);
		etSuccessMsg = (EditText) findViewById(R.id.et_success_msg);
		llRechargeFail = (LinearLayout) findViewById(R.id.ll_recharge_fail);
		tvRechargeFail = (TextView) findViewById(R.id.tv_recharge_fail);
		tvConpouFail = (TextView) findViewById(R.id.tv_coupon_fail);
		rechargeDialogBtn = (Button) findViewById(R.id.btn_recharge_dialog);
		etSuccessMsg.setCursorVisible(false);
		etSuccessMsg.clearFocus();
	}
	
	public void setConpouFail(String text) {
		tvConpouFail.setText(text);
		setConpouFailVisibility(View.VISIBLE);
	}
	
	public void setRechargeFail(String text) {
		tvRechargeFail.setText(text);
		setRechargeCardFailVisibiltiy(View.VISIBLE);
	}
	
	public String getRechargeFail() {
		return tvRechargeFail.getText().toString();
	}
	
	public void setRechargeCardFailVisibiltiy(int visibility) {
		tvRechargeFail.setVisibility(visibility);
	}
	
	public void setConpouFailVisibility(int visibility) {
		tvConpouFail.setVisibility(visibility);
	}
	
	public void setRechargeFailVisibility(int visibility) {
		llRechargeFail.setVisibility(visibility);
	}
	
	public int getRechargeFailVisibility() {
		return llRechargeFail.getVisibility();
	}
	
	public void setSuccessMsg(String text) {
		etSuccessMsg.setText(Html.fromHtml(text));
	}
	
	public void setRechargeSuccessVisibility(int visibility) {
		etSuccessMsg.setVisibility(visibility);
	}
	
	public int getRechargeSuccessVisibility() {
		return etSuccessMsg.getVisibility();
	}
	
	public void setButtonOnClick(android.view.View.OnClickListener l) {
		rechargeDialogBtn.setOnClickListener(l);
	}
	
	@Override
	public void show() {
		try{
			super.show();
		}catch (Exception e) {
			Log.w("", "show dialog error!", e);
		}
	}
}
