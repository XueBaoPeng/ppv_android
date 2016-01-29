package cn.sharesdk.demo.tpl;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AlertDialog extends Dialog {

	private TextView title;
	private TextView content;
	private Button goBtn;
	
	
	public AlertDialog(Activity context,boolean isShowTitle) {
		this(context,0);
	}
	public AlertDialog(Activity context, int theme){
        super(context, R.style.TaskInfoDialog);
        setContentView(R.layout.alert_dialog);
		content = (TextView) findViewById(R.id.tv_content);
		goBtn = (Button) findViewById(R.id.task_dialog_btn);
		title = (TextView) findViewById(R.id.tv_title);
		title.setVisibility(View.VISIBLE);
		
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
	
	/*public void setContentHeight(int height) {
		reContent.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, height));
	}*/
	
	
	public void setButtonOnClick(android.view.View.OnClickListener l) {
		goBtn.setOnClickListener(l);
	} 
}
