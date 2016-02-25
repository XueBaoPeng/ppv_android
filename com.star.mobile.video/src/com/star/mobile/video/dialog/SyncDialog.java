package com.star.mobile.video.dialog;

import java.util.Date;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.star.cms.model.Channel;
import com.star.cms.model.Program;
import com.star.mobile.video.R;

public class SyncDialog extends Dialog {


	private com.star.ui.ImageView iv_icon;
	private TextView tv_title;
	private Button bt_ok;
	private static SyncDialog dialog;

	private SyncDialog(Context context) {
		super(context, R.style.TaskInfoDialog);
		setContentView(R.layout.dialog_sync_epg);
		iv_icon = (com.star.ui.ImageView)findViewById(R.id.iv_channel_icon);
		tv_title = (TextView) findViewById(R.id.tv_sync_title);
		bt_ok = (Button) findViewById(R.id.bt_confirm);
	}
	
	public static SyncDialog getInstance(Context context){
		return new SyncDialog(context);
	}
	
	public void setDialogContent(int day, Channel channel, Program program){
		try{
			String logoUrl = channel.getLogo().getResources().get(0).getUrl();
			iv_icon.setUrl(logoUrl);
		}catch(Exception e){
		}
		if(day == -1){
			tv_title.setText("No EPG now. Try it later!");
			bt_ok.setText("OK");
		}else if(day == 0 && program!=null){
			tv_title.setText("EPG alreay downloaded!");
			bt_ok.setText("OK");
		}else{
			String fromD = "";
			String toD = "";
			if(program==null){
				String[] fromArr = new Date().toString().split(" ");
				fromD = fromArr[1]+"."+fromArr[2];
				if(day!=0){
					String[] toArr = new Date(new Date().getTime()+(day*1000*60*60*24L)).toString().split(" ");
					toD = toArr[1]+"."+toArr[2];
				}
			}else{
				String[] fromArr = new Date(program.getStartDate().getTime()+1000*60*60*24L).toString().split(" ");
				fromD = fromArr[1]+"."+fromArr[2];
				if(day!=1){
					String[] toArr = new Date(program.getStartDate().getTime()+day*1000*60*60*24L).toString().split(" ");
					toD = toArr[1]+"."+toArr[2];
				}
			}
			String text;
			SpannableStringBuilder style;
			if("".equals(toD)){
				text = "Download the Program Guide for " + fromD;
				style = new SpannableStringBuilder(text); 
				int indexF = text.indexOf(fromD);
				style.setSpan(new StyleSpan(Typeface.BOLD), indexF, indexF+fromD.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); 
			}else{
				text = "Download the Program Guide from "+fromD+" to "+toD+" ?";
				style = new SpannableStringBuilder(text); 
				int indexF = text.indexOf(fromD);
				int indexT = text.indexOf(toD);
				style.setSpan(new StyleSpan(Typeface.BOLD), indexF, indexF+fromD.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				style.setSpan(new StyleSpan(Typeface.BOLD), indexT, indexT+toD.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); 
			}
			tv_title.setText(style);
			bt_ok.setText("Confirm to DownLoad");
		}
	}
	
	public void setButtonOnClickListener(android.view.View.OnClickListener listener){
		bt_ok.setOnClickListener(listener);
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
