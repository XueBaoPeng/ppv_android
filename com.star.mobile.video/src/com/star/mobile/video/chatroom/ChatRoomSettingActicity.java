package com.star.mobile.video.chatroom;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.star.cms.model.ChatRoom;
import com.star.mobile.video.R;
import com.star.mobile.video.activity.BrowserActivity;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.ToastUtil;
import com.star.util.loader.OnResultListener;

public class ChatRoomSettingActicity extends BaseActivity implements OnClickListener{
	
	private ImageView ivNotifiSwitch;
	private boolean alertStatus = true;
	private ChatRoom chatRoom;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_room_setting);
		((TextView) findViewById(R.id.tv_actionbar_title)).setText(getString(R.string.chat_room_title));
		ivNotifiSwitch = (ImageView) findViewById(R.id.iv_notifi_switch);
		ivNotifiSwitch.setOnClickListener(this);
		findViewById(R.id.rl_top_post).setOnClickListener(this);
		findViewById(R.id.iv_actionbar_back).setOnClickListener(this);
		
		currentIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		currentIntent(intent);
	}
	
	private void currentIntent(Intent intent) {
		ChatRoom cr = (ChatRoom)intent.getSerializableExtra("room");
		boolean open = intent.getBooleanExtra("open", true);
		if(!open){
			findViewById(R.id.rl_chat_alert).setVisibility(View.GONE);
		}
		if(cr!=null)
			chatRoom = cr;
		if(chatRoom==null){
			finish();
			return;
		}
		List<Long> ids = SharedPreferencesUtil.getSettingChatRoomIds(this);
		if(ids.contains(chatRoom.getId())) {
			alertStatus = false;
		}
		if(!alertStatus) {
			ivNotifiSwitch.setImageResource(R.drawable.switch_off);
		} else {
			ivNotifiSwitch.setImageResource(R.drawable.switch_on);
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_top_post:
			Intent i = new Intent(this, BrowserActivity.class);
//			i.putExtra("pageName", (chatroom==null||chatroom.getName()==null)?roomName:chatroom.getName());
			i.putExtra("loadUrl",getString(R.string.html_prefix_url)+"/forum/chat_top.html?id="/*+channelId*/);
			CommonUtil.startActivity(this, i);
			break;
		case R.id.iv_notifi_switch:
			setOneChatRoomAlert();
			break;
		case R.id.iv_actionbar_back:
			onBackPressed();
			break;
		default:
			break;
		}
	}

	private void setOneChatRoomAlert() {
		if(chatRoom == null) {
			ToastUtil.centerShowToast(this,getString(R.string.setting_error));
			return;
		}
		if(SharedPreferencesUtil.getSettingChatRoomIds(ChatRoomSettingActicity.this).contains(chatRoom.getId())) {
			alertStatus = true;
			ivNotifiSwitch.setImageResource(R.drawable.switch_on);
			List<Long> ids = SharedPreferencesUtil.getSettingChatRoomIds(ChatRoomSettingActicity.this);
			ids.remove(chatRoom.getId());
			SharedPreferencesUtil.setSettingChatRoomIds(ChatRoomSettingActicity.this, ids); 
		} else {
			alertStatus = false;
			ivNotifiSwitch.setImageResource(R.drawable.switch_off);
			List<Long> ids = SharedPreferencesUtil.getSettingChatRoomIds(ChatRoomSettingActicity.this);
			ids.add(chatRoom.getId());
			SharedPreferencesUtil.setSettingChatRoomIds(ChatRoomSettingActicity.this, ids); 
		}
		
		final ChatService chatservice = new ChatService(this);
		chatservice.setOneChatRoomAlert(chatRoom.getCashId(), alertStatus, new OnResultListener<Boolean>() {
			
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
