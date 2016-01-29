package com.star.mobile.video.chatroom;

import java.util.ArrayList;
import java.util.List;

import com.star.cms.model.ChatRoom;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.base.BaseFragment;
import com.star.mobile.video.me.feedback.FeedbackActivity;
import com.star.mobile.video.service.EggAppearService;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.PostTimer;
import com.star.util.loader.LoadMode;
import com.star.util.loader.OnListResultListener;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

public class ChatRoomsFragment extends BaseFragment implements OnClickListener{
	
	private ChatService chatService;
	List<ChatRoom> chatrooms = new ArrayList<ChatRoom>();
	List<ChatRoom> chatrooms_b = new ArrayList<ChatRoom>();
	private ChatRoomsActivity chatRoomsActivity;
	private View mView;
	private final long periodtime_chatroom = 5000;
	
	private boolean isFirstTime = true;
	private ChatRoomsAdapter logoAdapter;
	private ChatRoomsAdapter logoAdapter_b;
	private View rlChannelRoom;
	private View rlTenbreRoom;
	private PostTimer mTimer = new PostTimer() {
		
		@Override
		public void execute() {
			getData(LoadMode.NET);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		chatRoomsActivity = (ChatRoomsActivity) getActivity();
		chatService = new ChatService(chatRoomsActivity);
		if(mView != null){
			ViewGroup parent = (ViewGroup) mView.getParent();  
			if(parent != null) {  
				parent.removeView(mView);  
			}   
			return mView;
		}
		mView = inflater.inflate(R.layout.fragment_chat_room, null);
		initView();
		getData(LoadMode.CACHE_NET);
		EggAppearService.appearEgg(getActivity(), EggAppearService.Chatroom_list);
		return mView;
	}
	
	private void initView() {
		TextView tv_reportBtn = (TextView) mView.findViewById(R.id.tv_report_btn);
		tv_reportBtn.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		tv_reportBtn.setOnClickListener(this);
		GridView gv_chatRooms = (GridView) mView.findViewById(R.id.gv_chat_room);
		logoAdapter = new ChatRoomsAdapter(chatRoomsActivity, chatrooms);
		gv_chatRooms.setAdapter(logoAdapter);
		GridView gv_chatRooms_b = (GridView) mView.findViewById(R.id.gv_chatRoom_b);
		logoAdapter_b = new ChatRoomsAdapter(chatRoomsActivity, chatrooms_b);
		gv_chatRooms_b.setAdapter(logoAdapter_b);
		
		rlChannelRoom = mView.findViewById(R.id.rl_channelRoom);
		rlTenbreRoom = mView.findViewById(R.id.rl_tenbreRoom);
	}
	
	@Override
	public void onStart() {
		chatRoomsActivity.setFragmentByTag(chatRoomsActivity.getResources().getString(R.string.fragment_tag_chatRooms));
		mTimer.startDelayed(periodtime_chatroom);
		super.onStart();
	}
	
	@Override
	public void onStop() {
		mTimer.stop();
		super.onStop();
	}
	
    private void getData(LoadMode mode) {
		chatService.getChatRooms(StarApplication.CURRENT_VERSION,mode, new OnListResultListener<ChatRoom>() {
			
			@Override
			public boolean onIntercept() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void onFailure(int errorCode, String msg) {
				mTimer.startDelayed(periodtime_chatroom);
			}
			
			@Override
			public void onSuccess(List<ChatRoom> cs) {
				if(cs != null && cs.size()>0){
					getAppRoomChatrate(cs);
					logoAdapter.updateDataAndRefreshUI(chatrooms);
					logoAdapter_b.updateDataAndRefreshUI(chatrooms_b);
				}
				mTimer.startDelayed(periodtime_chatroom);
			}
		});
	}

	private void getAppRoomChatrate(List<ChatRoom> cs) {
		chatrooms.clear();
		chatrooms_b.clear();
		for(ChatRoom vo : cs){
			if(vo.getType() == 1){
				chatrooms_b.add(vo);
			}else{
				chatrooms.add(vo);
			}
		}
		if(chatrooms.size()>0){
			rlChannelRoom.setVisibility(View.VISIBLE);
		}else{
			rlChannelRoom.setVisibility(View.GONE);
		}
		if(chatrooms_b.size()>0){
			rlTenbreRoom.setVisibility(View.VISIBLE);
		}else{
			rlTenbreRoom.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_report_btn:
			Intent intent = new Intent(chatRoomsActivity,FeedbackActivity.class);
			CommonUtil.startActivity(chatRoomsActivity, intent);
			break;
		default:
			break;
		}
	}
	
	/*private OnItemClickListener itemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Intent intent = new Intent();
			intent.setClass(homeActivity, ChatActivity.class);
			intent.putExtra("channel", channels.get(position));
			CommonUtil.startActivity(homeActivity, intent);
		}
	};*/
}
