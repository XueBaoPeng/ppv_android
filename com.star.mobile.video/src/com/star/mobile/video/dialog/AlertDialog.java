package com.star.mobile.video.dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.star.mobile.video.R;

public class AlertDialog extends Dialog {

	private TextView title;
	private TextView content;
	private Button goBtn;
	private RelativeLayout reContent;
	private ImageView titleLine;
	private boolean isShowTitle;
	
	
	public AlertDialog(Context context,boolean isShowTitle) {
		this(context,0);
		this.isShowTitle = isShowTitle;
	}
	public AlertDialog(Context context, int theme){
        super(context, R.style.TaskInfoDialog);
        setContentView(R.layout.alert_dialog);
		content = (TextView) findViewById(R.id.tv_content);
		goBtn = (Button) findViewById(R.id.task_dialog_btn);
		title = (TextView) findViewById(R.id.tv_title);
		titleLine = (ImageView) findViewById(R.id.iv_line);
		reContent = (RelativeLayout) findViewById(R.id.rl_up);
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
	public void setMessage(String str) {
		content.setText(str);
	}
	
	public void setMessageTextColor(int color) {
		content.setTextColor(color);
	}
	
	public void setButtonText(String text) {
		goBtn.setText(text);
	}
	
	public void setContentHeight(int height) {
		reContent.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, height));
	}
	
	
	public void setButtonOnClick(android.view.View.OnClickListener l) {
		goBtn.setOnClickListener(l);
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
