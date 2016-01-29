package com.star.mobile.video.chatroom;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.star.cms.model.Chart;
import com.star.cms.model.ChatRoom;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.adapter.ShareChatRoomAdapter;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.model.LinkPkg;
import com.star.mobile.video.model.ShareChatRoomModel;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.util.loader.LoadMode;
import com.star.util.loader.OnListResultListener;

public class ShareChatRoomActivity extends BaseActivity implements OnClickListener {
	
	private ListView lvShareChat;
	private ShareChatRoomAdapter mAdapter;
	private List<ChatRoom> chatRooms = new ArrayList<ChatRoom>();
	private ChatService chatService;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_chatroom);
		chatService = new ChatService(this);
		
		lvShareChat = (ListView) findViewById(R.id.lv_share_list);
		findViewById(R.id.iv_actionbar_back).setOnClickListener(this);
		((TextView)findViewById(R.id.tv_actionbar_title)).setText(getString(R.string.send_to));
		mAdapter = new ShareChatRoomAdapter(ShareChatRoomActivity.this, chatRooms);
		lvShareChat.setAdapter(mAdapter);
		currentIntent(getIntent());
		getChatRoom();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		currentIntent(intent);
	}
	
	private void currentIntent(Intent intent) {
		String msg = intent.getStringExtra("msg");
		LinkPkg lp = (LinkPkg)intent.getSerializableExtra("linkpkg");
		String imgUrl = intent.getStringExtra("imgurl");
		int type = intent.getIntExtra("type", Chart.TYPE_TEXT); 
		if(msg==null && lp==null){
			ShareChatRoomModel scm = SharedPreferencesUtil.getShareChatRoomData(this);
			if(scm != null){
				msg = scm.getMsg();
				lp = scm.getPkg();
				type = scm.getType();
			}
		}
		if(lp != null) {
			mAdapter.setLinkPkg(lp);
		}
		if(msg != null) {
			mAdapter.setForwardMsg(msg);
		}
		if(imgUrl != null) {
			mAdapter.setImageUrl(imgUrl);
		}
		mAdapter.setShareType(type);
		ShareChatRoomModel model = new ShareChatRoomModel();
		model.setType(type);
		model.setMsg(msg);
		model.setPkg(lp);
		SharedPreferencesUtil.setShareChatRoomData(this, model);
	}
	
	
	private void getChatRoom() {
		chatService.getChatRooms(StarApplication.CURRENT_VERSION,LoadMode.CACHE_NET, new OnListResultListener<ChatRoom>() {
			
			@Override
			public boolean onIntercept() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void onFailure(int errorCode, String msg) {
				
			}
			
			@Override
			public void onSuccess(List<ChatRoom> value) {
				chatRooms = value;
				if(chatRooms != null && chatRooms.size() > 0) {
					mAdapter.updateData(chatRooms);
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_actionbar_back:
			onBackPressed();
			break;

		default:
			break;
		}
	}
}
