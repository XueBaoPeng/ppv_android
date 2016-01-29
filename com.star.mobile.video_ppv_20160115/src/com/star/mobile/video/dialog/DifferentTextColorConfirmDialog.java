package com.star.mobile.video.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.star.mobile.video.R;

public class DifferentTextColorConfirmDialog extends Dialog{

	private TextView title;
	private TextView content2;
	private TextView content1;
	private RelativeLayout reContent;
	private RelativeLayout rlCancel;
	private RelativeLayout rlOk;
	private TextView tvCancel;
	private TextView tvOk;
	private ImageView titleLine;
	private boolean isShowTitle;
	
	
	public DifferentTextColorConfirmDialog(Context context,boolean isShowTitle) {
		this(context,0);
		this.isShowTitle = isShowTitle;
	}
	public DifferentTextColorConfirmDialog(Context context, int theme){
        super(context, R.style.TaskInfoDialog);
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.different_textcolor_confirm_dialog);
		content2 = (TextView) findViewById(R.id.tv_content_2);
		content1 = (TextView) findViewById(R.id.tv_content_1);
		title = (TextView) findViewById(R.id.tv_title);
		titleLine = (ImageView) findViewById(R.id.iv_line);
		reContent = (RelativeLayout) findViewById(R.id.rl_up);
		rlCancel = (RelativeLayout) findViewById(R.id.rl_cancel);
		rlOk = (RelativeLayout) findViewById(R.id.rl_ok);
		tvCancel = (TextView) findViewById(R.id.cancel_btn);
		tvOk = (TextView) findViewById(R.id.ok_btn);
		
		if(isShowTitle) {
			title.setVisibility(View.VISIBLE);
			titleLine.setVisibility(View.VISIBLE);
		} else {
			title.setVisibility(View.GONE);
			titleLine.setVisibility(View.GONE);
		}
	}
	
	public void setTitle(String str) {
		title.setText(str);
	}
	
	public void setTwoMessage(String str) {
		content2.setText(str);
	}
	
	public void setOneMessage(String str) {
		content1.setVisibility(View.VISIBLE);
		content1.setText(str);
	}
	
	public void setOneMessageTextColor(int color) {
		content1.setTextColor(color);
	}
	
	public void setLeftButtonText(String text) {
		tvCancel.setText(text);
	}
	
	public void setRightButtonText(String text) {
		tvOk.setText(text);
	}
	
	public void setContentHeight(int height) {
		reContent.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, height));
	}
	
	public void setLeftButtonOnClick(android.view.View.OnClickListener l) {
		rlCancel.setOnClickListener(l);
	} 
	
	public void setRigthButtonOnClick(android.view.View.OnClickListener l) {
		rlOk.setOnClickListener(l);
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
