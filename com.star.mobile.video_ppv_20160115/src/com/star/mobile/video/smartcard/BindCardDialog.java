package com.star.mobile.video.smartcard;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;

import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.ui.ImageView;

public class BindCardDialog extends Dialog {

	private ImageView userhead;
	public BindCardDialog(Context context) {
		this(context,0);
	}
	public BindCardDialog(Context context, int theme){
        super(context, R.style.TaskInfoDialog);
        setContentView(R.layout.dialog_bind_card);
        setCancelable(false);
		userhead = (ImageView) findViewById(R.id.user_logo);
    }
	
	public void showDialog(){
		if(StarApplication.mUser != null){
			userhead.setUrl(StarApplication.mUser.getHead());			
		}
		show();
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
