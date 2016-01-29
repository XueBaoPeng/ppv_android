package com.star.mobile.video.dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.star.mobile.video.R;

/**
 * 
 * @author zk
 *
 */
public class ReceiveCoinDialog extends Dialog {
	
	private TextView tvCoin;
	private TextView tvAlert;
	private TextView tvLater;
	private TextView tvGo;
	
	public ReceiveCoinDialog(Context context) {
		this(context,0);
	}

	public ReceiveCoinDialog(Context context, int theme) {
		super(context, R.style.TaskInfoDialog);
		setContentView(R.layout.task_dotask_dialog);
		tvCoin = (TextView) findViewById(R.id.tv_coin_num);
		tvAlert = (TextView) findViewById(R.id.tv_alert);
		tvLater = (TextView) findViewById(R.id.tv_later);
		tvGo = (TextView) findViewById(R.id.tv_go);
	}
	

	
	public void setCoin(float coin) {
		tvCoin.setText((int)coin+"");
	}
	
	public void setCoin(int oneMultiplier,int twoMultiplier ) {
		findViewById(R.id.tv_add_wa).setVisibility(View.GONE);
		tvCoin.setText(oneMultiplier+" × "+twoMultiplier);
	}
	
	/**
	 * 
	 * @param text
	 * @param l
	 */
	public void setLeftButtonOnClick(String text,android.view.View.OnClickListener l) {
		if(text != null || "".equals(text)) {
			tvLater.setText(text);
		} 
		tvLater.setOnClickListener(l);
	}
	
	public void setLeftButtonOnClick(android.view.View.OnClickListener l) {
		tvLater.setOnClickListener(l);
	}
	
	/**
	 * 
	 * @param text
	 * @param l
	 */
	public void setRightButtonOnClick(String text,android.view.View.OnClickListener l) {
		if(text != null || "".equals(text)) {
			tvGo.setText(text);
		} 
		tvGo.setOnClickListener(l);
	}
	
	public void setRightButtonOnClick(android.view.View.OnClickListener l) {
		tvGo.setOnClickListener(l);
	}
	
	/**
	 * 设置提醒信息
	 * @param text
	 */
	public void setAlertInfo(String text) {
		tvAlert.setText(text);
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
