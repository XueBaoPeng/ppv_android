package cn.sharesdk.demo.tpl;

import java.util.HashMap;

import com.facebook.FacebookSdk;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.framework.utils.UIHandler;

public abstract class ThirdLoginActivity extends Activity implements Callback, 
		OnClickListener, PlatformActionListener {
	private static final int MSG_USERID_FOUND = 1;
	private static final int MSG_LOGIN = 2;
	private static final int MSG_AUTH_CANCEL = 3;
	private static final int MSG_AUTH_ERROR= 4;
	private static final int MSG_AUTH_COMPLETE = 5;
	
//	protected abstract int thirdLoginButtonID();
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ShareSDK.initSDK(this);
		ShareSDK.registerPlatform(BbmCustomize.class);
		ShareSDK.registerPlatform(FacebookCustomize.class);
		ShareSDK.registerPlatform(TwitterCustomize.class);
		
//		findViewById(thirdLoginButtonID()).setOnClickListener(this);
	}
	
	protected void onDestroy() {
		ShareSDK.stopSDK(this);
		super.onDestroy();
	}
	
	public void onClick(View v) {
//		if(v.getId()!=thirdLoginButtonID()){
//			Dialog dlg = (Dialog) v.getTag();
//			dlg.dismiss();
//		}
//		if(v.getId() == thirdLoginButtonID()){
//			authorize(null);
//		}else if(v.getId() == R.id.tvFacebook){
//			authorize(new Facebook(this));
//		}else if(v.getId() == R.id.tvTwitter){
//			authorize(new Twitter(this));
//		}
	}
	
	protected void authorize(Platform plat) {
		if (plat == null) {
			popupOthers();
			return;
		}
		
		if(plat.isValid()) {
			String userId = plat.getDb().getUserId();
			if (!TextUtils.isEmpty(userId)) {
				UIHandler.sendEmptyMessage(MSG_USERID_FOUND, this);
				sendLogin(plat);
				return;
			}
		}
		plat.setPlatformActionListener(this);
		plat.SSOSetting(true);
		plat.showUser(null);
	}
	
	private void popupOthers() {
		Dialog dlg = new Dialog(this);
		View dlgView = View.inflate(this, R.layout.other_plat_dialog, null);
		View tvFacebook = dlgView.findViewById(R.id.tvFacebook);
		tvFacebook.setTag(dlg);
		tvFacebook.setOnClickListener(this);
		View tvTwitter = dlgView.findViewById(R.id.tvTwitter);
		tvTwitter.setTag(dlg);
		tvTwitter.setOnClickListener(this);
//		View tvSinaWeibo = dlgView.findViewById(R.id.tvWeibo);
//		tvSinaWeibo.setTag(dlg);
//		tvSinaWeibo.setOnClickListener(this);
		
		dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dlg.setContentView(dlgView);
		dlg.show();
	}
	
	public void onComplete(Platform platform, int action,
			HashMap<String, Object> res) {
		if (action == Platform.ACTION_USER_INFOR) {
			UIHandler.sendEmptyMessage(MSG_AUTH_COMPLETE, this);
			sendLogin(platform);
		}
		System.out.println(res);
	}
	
	public void onError(Platform platform, int action, Throwable t) {
		if (action == Platform.ACTION_USER_INFOR) {
			UIHandler.sendEmptyMessage(MSG_AUTH_ERROR, this);
		}
		t.printStackTrace();
	}
	
	public void onCancel(Platform platform, int action) {
		if (action == Platform.ACTION_USER_INFOR) {
			UIHandler.sendEmptyMessage(MSG_AUTH_CANCEL, this);
		}
	}
	
	private void sendLogin(Platform plat) {
		Message msg = new Message();
		msg.what = MSG_LOGIN;
		msg.obj = plat;
		UIHandler.sendMessage(msg, this);
	}
	
	public boolean handleMessage(Message msg) {
		switch(msg.what) {
			case MSG_USERID_FOUND: {
				Toast.makeText(this, R.string.userid_found, Toast.LENGTH_SHORT).show();
			}
			break;
			case MSG_LOGIN: {
				Platform plat = (Platform)msg.obj;
				String text = getString(R.string.logining, plat.getName());
				Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
				thirdLogin(plat,plat.getDb().getUserId(),plat.getDb().getUserName(),plat.getDb().getUserIcon(),plat.getDb().getUserName());
//				Builder builder = new Builder(this);
//				builder.setTitle(R.string.if_register_needed);
//				builder.setMessage(R.string.after_auth);
//				builder.setPositiveButton(R.string.ok, null);
//				builder.create().show();
			}
			break;
			case MSG_AUTH_CANCEL: {
				Toast.makeText(this, R.string.auth_cancel, Toast.LENGTH_SHORT).show();
			}
			break;
			case MSG_AUTH_ERROR: {
				Toast.makeText(this, R.string.auth_error, Toast.LENGTH_SHORT).show();
			}
			break;
			case MSG_AUTH_COMPLETE: {
				Toast.makeText(this, R.string.auth_complete, Toast.LENGTH_SHORT).show();
			}
			break;
		}
		return false;
	}
	
	protected abstract void thirdLogin(Platform plat, String userID, String userName, String ico,String nickName);
	
}
