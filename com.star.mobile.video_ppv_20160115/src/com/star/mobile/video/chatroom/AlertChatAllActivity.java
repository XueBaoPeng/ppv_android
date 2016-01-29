package com.star.mobile.video.chatroom;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.star.mobile.video.R;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.util.loader.OnResultListener;

public class AlertChatAllActivity extends BaseActivity implements OnClickListener{
	
	private ImageView sound_switch;
	private ImageView vibration_switch;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_all_chat_alert);
		((ImageView) findViewById(R.id.iv_actionbar_back)).setOnClickListener(this);
		((TextView) findViewById(R.id.tv_actionbar_title)).setText(R.string.chat_room_notification);
		initView();
	}
	
	private void initView() {
		sound_switch=(ImageView)findViewById(R.id.sound_switch);
		vibration_switch=(ImageView)findViewById(R.id.vibration_switch);
		sound_switch.setOnClickListener(this);
		vibration_switch.setOnClickListener(this);
		if(SharedPreferencesUtil.getSoundChatAlertAll(this)) {
			sound_switch.setBackgroundResource(R.drawable.switch_on);
		}else {
			sound_switch.setBackgroundResource(R.drawable.switch_off);
		}
		if(SharedPreferencesUtil.getVibrationChatAlertAll(this)) {
			vibration_switch.setBackgroundResource(R.drawable.switch_on);
		}else {
			vibration_switch.setBackgroundResource(R.drawable.switch_off);
		}
		
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_actionbar_back:
			onBackPressed();
			break;
		case R.id.sound_switch:
			if(SharedPreferencesUtil.getSoundChatAlertAll(this)) {
				SharedPreferencesUtil.setSoundChatAlertAll(this, false);//先修改本地
				setting(false,SharedPreferencesUtil.getVibrationChatAlertAll(this));
				sound_switch.setBackgroundResource(R.drawable.switch_off);
			}else {
				SharedPreferencesUtil.setSoundChatAlertAll(this, true);//先修改本地
				setting(true,SharedPreferencesUtil.getVibrationChatAlertAll(this));
				sound_switch.setBackgroundResource(R.drawable.switch_on);	
			}
			break;
		case R.id.vibration_switch:
			if(SharedPreferencesUtil.getVibrationChatAlertAll(this)) {
				SharedPreferencesUtil.setVibrationChatAlertAll(this, false); //先修改本地
				setting(SharedPreferencesUtil.getSoundChatAlertAll(this),false);
				vibration_switch.setBackgroundResource(R.drawable.switch_off);
			}else {
				SharedPreferencesUtil.setVibrationChatAlertAll(this, true);//先修改本地
				setting(SharedPreferencesUtil.getSoundChatAlertAll(this),true);
				vibration_switch.setBackgroundResource(R.drawable.switch_on);	
			}
			
			break;
		default:
			break;
		}
	}
	
	private void setting(final boolean sound,final boolean vibration) {
		final ChatService chatservice = new ChatService(this);
		chatservice.setAllChatRoomAlert(sound, vibration, new OnResultListener<Boolean>() {
			
			@Override
			public void onSuccess(Boolean value) {
				chatservice.saveChatRoomSetting();
			}
			
			@Override
			public boolean onIntercept() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void onFailure(int errorCode, String msg) {
				// TODO Auto-generated method stub
				
			}
		});
	}
}
