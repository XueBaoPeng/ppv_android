package com.star.mobile.video.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.star.cms.model.Chart;
import com.star.cms.model.ChatRoom;
import com.star.cms.model.User;
import com.star.cms.model.dto.ChatRoomReturn;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.adapter.VideoLiveChatContentAdapter;
import com.star.mobile.video.chatroom.ChatService;
import com.star.mobile.video.model.ChatVO;
import com.star.mobile.video.service.EggAppearService;
import com.star.mobile.video.service.UserService;
import com.star.mobile.video.service.UserService.CallbackListener;
import com.star.mobile.video.util.ToastUtil;
import com.star.util.loader.OnResultListener;

/**
 * 直播页面的视图控制
 * 
 * @author Lee @version1.0 2015/08/24
 *
 */
public class VideoLiveViewControlChat extends LinearLayout implements OnClickListener {
	private LayoutInflater mLayoutInflater;
	private Context mContext;
	private EditText etChat;
	private ImageView sendChat;
	private ImageView sendFace;

	private VideoLiveChatContentAdapter mAdapter;
	private ChatService mChatService;
	private List<ChatVO> chats = new ArrayList<ChatVO>();

	private boolean isFaceShow = false;
	private ChatRoom chatroom;
	private InputMethodManager imm;
	private android.widget.ListView chatContentList;;
	private Handler mDefaultHandler = new Handler();
	private Runnable mChatTimerTask;
	private Long channelId;
	private View faceLayout;
	private View llChatNotice;
	
	public VideoLiveViewControlChat(Context context) {
		super(context);
		initView(context);
	}

	public VideoLiveViewControlChat(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	private void initView(Context context) {
		this.mContext = context;
		mChatService = new ChatService(context);
		mLayoutInflater = LayoutInflater.from(context);
		View view = mLayoutInflater.inflate(R.layout.view_video_live_control_chat, this);

		chatContentList = (android.widget.ListView) view.findViewById(R.id.video_live_chat_listview);
		llChatNotice = view.findViewById(R.id.ll_chat_notice);
		imm = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
	}
	
	public void addView(EditText edittext, ImageView sendChat, ImageView sendFace, FaceContainer faceLayout){
		this.etChat = edittext;
		this.sendChat = sendChat;
		this.sendFace = sendFace;
		this.faceLayout = faceLayout;
		etChat.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
				if (arg1 == EditorInfo.IME_ACTION_UNSPECIFIED) {
					sendChatContent();
				}
				return false;
			}
		});
	}

	public void setCurrentChatRoom(final Long channelID){
		this.channelId = channelID;
		mChatService.getChatroomByCashId(channelId, new OnResultListener<ChatRoom>() {
			
			@Override
			public void onSuccess(ChatRoom value) {
				chatroom = value;
				if(chatroom != null){
					intoChatRoom();
				}
			}
			
			@Override
			public boolean onIntercept() {
				if(channelId==null)
					return true;
				return false;
			}
			
			@Override
			public void onFailure(int errorCode, String msg) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	private void intoChatRoom() {
		if(chatroom.getHotChatRate()!=null){
			StarApplication.mChannelIdOfChatRoom.remove(chatroom.getId());
//			SharedPreferencesUtil.saveChatRoomMsgCount(mContext, chatroom.getId(), chatroom.getHotChatRate());
		}else{
//			getChatNum();
		}
		chats.clear();
		mAdapter = new VideoLiveChatContentAdapter(mContext, chats);
		chatContentList.setAdapter(mAdapter);
		sendChat.setOnClickListener(this);
		sendFace.setOnClickListener(this);
		initData();
	}
	
//	private void getChatNum(){
//		new LoadingDataTask() {
//			private int count;
//			@Override
//			public void onPreExecute() {
//			}
//
//			@Override
//			public void onPostExecute() {
//				chatroom.setHotChatRate(count);
//				StarApplication.mChannelIdOfChatRoom.remove(chatroom.getId());
//				SharedPreferencesUtil.saveChatRoomMsgCount(mContext, chatroom.getId(), count);
//			}
//			@Override
//			public void doInBackground() {
//				count = mChatService.getChatNum(chatroom.getCashId());
//			}
//		}.execute();
//	}
	
	private void initData() {
		if(StarApplication.mUser == null){
			getUserInfo();
		}else{
			User user = StarApplication.mUser;
			if(user.getNickName()==null||"".equals(user.getNickName())){
				String deviceName = new Build().MODEL;
				user.setNickName(deviceName);
			}
			getChatList();
		}
		mChatTimerTask = new Runnable() {
	        @Override
	        public void run() {
	        	getChatList();
	        }
	    };
	}
	
	private void getUserInfo() {
		UserService mService = new UserService();
		mService.setCallbackListener(new CallbackListener() {
			@Override
			public void callback(User user) {
				if(user.getNickName()==null||"".equals(user.getNickName())){
					String deviceName = new Build().MODEL;
					user.setNickName(deviceName);
				}
				StarApplication.mUser = user;
				getChatList();
			}
		});
		mService.getUser(mContext, false);
	}
	
	private void getChatList() {
		Long lastChartTime = null;
		if(chats.size()>0){
			lastChartTime = chats.get(chats.size()-1).getCreateDate().getTime()+10;
		}
		mChatService.getChats(channelId, lastChartTime, new OnResultListener<ChatRoomReturn>() {
			
			@Override
			public void onSuccess(ChatRoomReturn crr) {
				if(crr != null){
					List<Chart> cs = crr.getCharts();
					if(cs!=null && cs.size()>0){
						hideChatNotice();
						Iterator<Chart> it = cs.iterator();
						while (it.hasNext()) {
							Chart value = it.next();
							if (value.getUserId().equals(StarApplication.mUser.getId())) {
								it.remove();
							}
						}
						if(cs.size()>0){
	//						getChatNum();
							chats.addAll(mChatService.parserChartToChatVO(cs));
							mAdapter.updateDateRefreshUi(chats);
						}
						scrollToBottom();
					}
				}
				getChatList(2000);
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
	
	public void getChatList(long time) {
    	if(mChatTimerTask == null)
    		return;
    	mDefaultHandler.removeCallbacks(mChatTimerTask);
		mDefaultHandler.postDelayed(mChatTimerTask, time);
	}
	
	public void onStop() {
		mDefaultHandler.removeCallbacks(mChatTimerTask);
		mChatTimerTask = null;
	}
	
	private void hideChatNotice() {
		if(llChatNotice.getVisibility()==View.VISIBLE)
			llChatNotice.setVisibility(View.GONE);
	}
	
	private void sendChatContent() {
		String content = etChat.getText().toString();
		if(content != null && !content.isEmpty()){
			if(content.trim().length() == 0) {
				//内容不能为空
				ToastUtil.centerShowToast(mContext, mContext.getString(R.string.blank_message_not_allowed));
			} else if (content.length() > 500) {
				// 内容太长
				ToastUtil.centerShowToast(mContext, mContext.getString(R.string.only));
			} else {
				submitChatContent(etChat.getText().toString().trim());
				etChat.getEditableText().clear();
			}
		} 
	}
	
	private ChatVO newChatToSend(String msg) {
		Chart chart = new Chart(msg, StarApplication.mUser.getNickName(), StarApplication.mUser.getId(), new Date(), StarApplication.mUser.getHead());
		chart.setType(Chart.TYPE_TEXT);
		ChatVO chatVO = new ChatVO(chart, ChatVO.STATUS_SENDING);
		return chatVO;
	}
	
	private void submitChatContent(final String msg){
		mChatService.sendText(channelId, msg, Chart.TYPE_TEXT, new OnResultListener<Chart>() {
			private ChatVO chatVO;
			@Override
			public boolean onIntercept() {
				hideChatNotice();
				chatVO = newChatToSend(msg);
				chats.add(chatVO);
				mAdapter.updateDateRefreshUi(chats);
				scrollToBottom();
				return false;
			}

			@Override
			public void onSuccess(Chart result) {
				if(result != null){
					chatVO.setId(result.getId());
					chatVO.setCreateDate(result.getCreateDate());
					chatVO.setStatus(ChatVO.STATUS_VALID);
//					SharedPreferencesUtil.saveChatRoomMsgCount(mContext, chatroom, 1);
					EggAppearService.appearEgg(mContext, EggAppearService.Chatroom_chat);
				}else{
					chatVO.setStatus(ChatVO.STATUS_INVALID);
				}
				mChatService.addChatToDB(chatVO, channelId);
				mAdapter.updateDateRefreshUi(chats);
				scrollToBottom();
			}

			@Override
			public void onFailure(int errorCode, String msg) {
				chatVO.setStatus(ChatVO.STATUS_INVALID);
				mChatService.addChatToDB(chatVO, channelId);
				mAdapter.updateDateRefreshUi(chats);
				scrollToBottom();
			}
		});
	}
	
	private void scrollToBottom() {
		chatContentList.setSelection(chatContentList.getBottom());
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				setFaceLayoutVisibility(View.GONE);
				imm.hideSoftInputFromWindow(etChat.getWindowToken(), 0);
			}
		}, 50);
	}

	private void setFaceLayoutVisibility(int visible) {
		if(visible == View.VISIBLE){
			faceLayout.setVisibility(View.VISIBLE);
			isFaceShow = true;
		}if(visible == View.GONE){
			faceLayout.setVisibility(View.GONE);
			isFaceShow = false;
		}
	}
	
	public boolean hidefaceLayout(){
		if(isFaceShow){
			setFaceLayoutVisibility(View.GONE);
			return true;
		}
		return false;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.video_live_content_send_iv:
			sendChatContent();
			break;
		case R.id.video_live_chat_face_iv:
			if (!isFaceShow) {
				imm.hideSoftInputFromWindow(etChat.getWindowToken(), 0);
				setFaceLayoutVisibility(View.VISIBLE);
			} else {
				setFaceLayoutVisibility(View.GONE);
			}
			break;

		default:
			break;
		}
	}
}
