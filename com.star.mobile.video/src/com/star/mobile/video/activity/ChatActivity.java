package com.star.mobile.video.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.star.cms.model.Chart;
import com.star.cms.model.ChatRoom;
import com.star.cms.model.User;
import com.star.cms.model.dto.ChatRoomReturn;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.chatroom.ChatContentAdapter;
import com.star.mobile.video.chatroom.ChatRoomSettingActicity;
import com.star.mobile.video.chatroom.ChatService;
import com.star.mobile.video.chatroom.faq.FAQ;
import com.star.mobile.video.chatroom.faq.FAQService;
import com.star.mobile.video.dialog.ForwardDialog;
import com.star.mobile.video.model.ChatVO;
import com.star.mobile.video.model.ImageBean;
import com.star.mobile.video.model.LinkPkg;
import com.star.mobile.video.service.EggAppearService;
import com.star.mobile.video.service.SyncStatusService;
import com.star.mobile.video.service.UserService;
import com.star.mobile.video.service.UserService.CallbackListener;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.ImageUtil;
import com.star.mobile.video.util.LoadingDataTask;
import com.star.mobile.video.util.ToastUtil;
import com.star.mobile.video.view.BulletinView;
import com.star.mobile.video.view.FaceContainer;
import com.star.mobile.video.view.LoadingProgressBar;
import com.star.util.loader.OnListResultListener;
import com.star.util.loader.OnResultListener;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class ChatActivity extends BaseActivity implements OnClickListener, OnTouchListener, OnScrollListener {

	private static final int MSG_FROM_SERVER = 10;
	private ChatService mChatService;
	private ChatContentAdapter mAdapter;
	private List<ChatVO> chats = new ArrayList<ChatVO>();
	private FaceContainer faceLayout;
	private RelativeLayout mChatRoomPromptRL;
	private BulletinView mChatRoomPromptTextView;
	private ImageView mChatRoomPomptCancelIV;
	private EditText etChat;
	private ImageView sendChat;
	private boolean isFirstTime = true;
	private boolean isFaceShow = false;
	private ListView chatContentList;
	private InputMethodManager imm;
	private LoadingProgressBar headerView;
	private View noChat;
	private final int requestCount = 10;
	private int offset=0;
	private int responsSize;
	private boolean isCanScroll = true;
	private TextView newChat;
	private int newChatCount = 0;
	private long channelId = -1;
	private String roomName = "Chat Room";
	private ChatRoom chatroom;
	private boolean historyBefore = false;
	private ImageView olineIcon;
	private TextView onlineCount;
	private boolean byShare;
	private boolean isShareConfirm;
	private ChatRoom lastChatRoom;
	private boolean notifSettingOpen = true;;

	private boolean isRobotCustomerChat = false;
	private FAQService mFaqService;
	private String regExpres = "[0-9]*";
	//机器人聊天室的code号
	private String ROBOT_CHAT_CODE = "105";
	
	private Handler mDefaultHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_FROM_SERVER:
				if(chatroom!=null&&chatroom.getId()!=null)
					getChatRoomPrompt(chatroom.getId());
				break;

			default:
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		mFaqService = new FAQService(this);
		mChatService = new ChatService(this);
		if(getIntent().getStringExtra("chatRoom")!=null)
			roomName = getIntent().getStringExtra("chatRoom");
		imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		try{
			channelId = Long.parseLong(getIntent().getStringExtra("channelId"));
		}catch(Exception e){
			chatroom = (ChatRoom) getIntent().getSerializableExtra("chatroom");
			parserIntent(getIntent());
		}
		if(channelId == -1 && chatroom == null){
			return;
		}
		initView();
		if(channelId != -1){
			setCurrentChatRoom(channelId);
		}else{
			 if(chatroom!=null) {
				channelId = chatroom.getCashId();
			}
				
			intoChatRoom();
		}
		
		EggAppearService.appearEgg(this, EggAppearService.Chatroom_in);
	}

	private void parserIntent(Intent intent) {
		int chatType = intent.getIntExtra("type", Chart.TYPE_TEXT);
		final String chatMsg = intent.getStringExtra("msg");
		final LinkPkg linkP = (LinkPkg) intent.getSerializableExtra("linkpkg");
		byShare = intent.getBooleanExtra("byShare", false);
		isShareConfirm = false;
		if(chatroom!=null){
			dialog = new ForwardDialog(this);
			if(chatType==Chart.TYPE_TEXT && chatMsg!=null){
				dialog.showTextDialog(this, chatroom, chatType);
				dialog.setConfirmClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						submitChatContent(chatMsg, Chart.TYPE_TEXT, true);
						isShareConfirm = true;
					}
				});
			}else if(chatType==Chart.TYPE_LINK && linkP!=null){
				dialog.showLinkDialog(this, linkP, chatType);
				dialog.setConfirmClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(!TextUtils.isEmpty(dialog.getForwardEditText())&&dialog.getForwardEditText().length()>500){
							ToastUtil.centerShowToast(ChatActivity.this, getString(R.string.only));
							return;
						}
						dialog.dismiss();
						submitChatContent(linkP.toString().trim(), Chart.TYPE_LINK, true);
						if(!TextUtils.isEmpty(dialog.getForwardEditText())){
							submitChatContent(dialog.getForwardEditText(), Chart.TYPE_TEXT, true);
						}
						isShareConfirm = true;
					}
				});
			}
		}
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		ChatRoom cr = (ChatRoom) intent.getSerializableExtra("chatroom");
		if(cr == null && byShare) {
			cr = lastChatRoom;
			isShareConfirm = true;
		}else{
			if(chatroom != null){
				lastChatRoom = chatroom;
			}
		}
		if(cr != null) {
			chatroom = cr;
			if(chatroom!=null){
//				CommonUtil.showProgressDialog(this);
				channelId = chatroom.getCashId();
				isFirstTime = true;
				historyBefore = false;
				intoChatRoom();
			}
			parserIntent(intent);
		}
		super.onNewIntent(intent);
	}
	
	private void intoChatRoom() {
		//消掉消息栏消息
		Intent intent = new Intent(this, SyncStatusService.class);
		intent.putExtra("roomId", chatroom.getId());
		startService(intent);
		if(chatroom.getHotChatRate()!=null){
			StarApplication.mChannelIdOfChatRoom.remove(chatroom.getId());
//			SharedPreferencesUtil.saveChatRoomMsgCount(this, chatroom.getId(), chatroom.getHotChatRate());
		}else{
//			getChatNum();
		}
		if(chatroom.getName()==null){
			chatroom.setName(roomName);
		}else{
			((TextView) findViewById(R.id.tv_actionbar_title)).setText(chatroom.getName());
		}
		chats.clear();
		mAdapter = new ChatContentAdapter(this, chats, chatroom);
		chatContentList.setAdapter(mAdapter);
		mAdapter.setListView(chatContentList);
		initData();
		//5秒后请求chat room提示获得数据
		mDefaultHandler.sendEmptyMessageDelayed(MSG_FROM_SERVER, 5*1000);
		
		if (ROBOT_CHAT_CODE.equals(chatroom.getCode())) {
			isRobotCustomerChat = true;
		}
	}

	private void initData() {
		if(StarApplication.mUser == null){
			getUserInfo();
		}else{
			User user = StarApplication.mUser;
			if(user.getNickName()==null||"".equals(user.getNickName())){
				String deviceName = new Build().MODEL;
				user.setNickName(deviceName);
			}
			getHistoryChatList();
		}
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
//				SharedPreferencesUtil.saveChatRoomMsgCount(ChatActivity.this, chatroom.getId(), count);
//			}
//			@Override
//			public void doInBackground() {
//				count = mChatService.getChatNum(chatroom.getCashId());
//			}
//		}.execute();
//	}

	public void setCurrentChatRoom(final long channelID){
		mChatService.getChatroomByCashId(channelID, new OnResultListener<ChatRoom>() {

			@Override
			public boolean onIntercept() {
				((TextView) findViewById(R.id.tv_actionbar_title)).setText(roomName);
				return false;
			}

			@Override
			public void onSuccess(ChatRoom value) {
				chatroom = value;
				if(chatroom == null){
					chatroom = new ChatRoom();
					chatroom.setId(channelID);
					chatroom.setName(roomName);
					chatroom.setCashId(channelID);
					notifSettingOpen = false;
				}
				intoChatRoom();
			}

			@Override
			public void onFailure(int errorCode, String msg) {
				// TODO Auto-generated method stub
			}
		});
	}
	private String mChatRoomPrompt = null;
	public void getChatRoomPrompt(final long channelID){
		mChatService.getChatroomPrompt(ChatActivity.this,channelID, new OnResultListener<String>(){

			@Override
			public boolean onIntercept() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onSuccess(String value) {
				mChatRoomPrompt = value;
				if(mChatRoomPrompt != null && !mChatRoomPrompt.isEmpty()){
					mChatRoomPromptRL.setVisibility(View.VISIBLE);
					//添加显示动画
					mChatRoomPromptRL.startAnimation(AnimationUtils.loadAnimation(ChatActivity.this, R.anim.tran_next_in));
					mChatRoomPromptTextView.setData(mChatRoomPrompt);
				}
			}

			@Override
			public void onFailure(int errorCode, String msg) {
			}
			
		});
	}

	private void initView() {
//		CommonUtil.showProgressDialog(this);
		findViewById(R.id.iv_actionbar_back).setOnClickListener(this);
		View chatTopV = findViewById(R.id.ll_right);
		chatTopV.setVisibility(View.VISIBLE);
		chatTopV.setOnClickListener(this);
		olineIcon = (ImageView) findViewById(R.id.iv_actionbar_onlineIcon);
		onlineCount = (TextView) findViewById(R.id.tv_actionbar_onlineCount);
		olineIcon.setVisibility(View.VISIBLE);
		onlineCount.setVisibility(View.VISIBLE);
		mChatRoomPromptTextView = (BulletinView) findViewById(R.id.chat_room_prompt_textview);
		mChatRoomPromptRL = (RelativeLayout) findViewById(R.id.chat_room_prompt_rl);
		mChatRoomPomptCancelIV = (ImageView) findViewById(R.id.chat_room_prompt_cancel);
		mChatRoomPomptCancelIV.setOnClickListener(this);
		chatContentList = (ListView) findViewById(R.id.lv_chat_list);
		faceLayout = (FaceContainer) findViewById(R.id.ll_face_layout);
		noChat = findViewById(R.id.iv_no_chat);
		newChat = (TextView) findViewById(R.id.iv_new_chat);
		etChat = (EditText) findViewById(R.id.et_chat_content);
		etChat.addTextChangedListener(commentWatcher);
		sendChat = (ImageView) findViewById(R.id.iv_chat_send);
		sendChat.setOnClickListener(this);
		newChat.setOnClickListener(this);
		etChat.setOnClickListener(this);
		chatContentList.setOnTouchListener(this);
		chatContentList.setOnScrollListener(this);
		findViewById(R.id.iv_image_send).setOnClickListener(this);
		findViewById(R.id.iv_chat_face).setOnClickListener(this);
		headerView = new LoadingProgressBar(this);
		chatContentList.addHeaderView(headerView);
		faceLayout.setEditText(etChat);
		
		etChat.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2){
				if (arg1 == EditorInfo.IME_ACTION_UNSPECIFIED) {
					sendChatContent();
				}
				return false;
			}
		});
	}
	
	private void setChatRoombackgroud() {
		if(chatroom.getBgImage()==null)
			return;
		new LoadingDataTask() {
			private Bitmap bitmap;
			@Override
			public void onPreExecute() {
			}
			
			@Override
			public void onPostExecute() {
				if(bitmap != null){
					getWindow().setBackgroundDrawable(new BitmapDrawable(getResources(),bitmap));
				}
			}
			
			@Override
			public void doInBackground() {
				bitmap = ImageUtil.getBitmap(ChatActivity.this, chatroom.getBgImage());
			}
		}.execute();
	}

	protected void onStart() {
		super.onStart();
		if(channelId==-1 && chatroom==null){
			this.finish();
			return;
		}
		isCanScroll = true;
		mChatTimerTask = new Runnable() {
	        @Override
	        public void run() {
	        	getChatList();
	        }
	    };
		getChatList(0);
	}

	@Override
	protected void onStop() {
		mDefaultHandler.removeCallbacks(mChatTimerTask);
		mChatTimerTask = null;
		super.onStop();
	}
	
	private Runnable mChatTimerTask;
    
    private void getChatList() {
		Long lastChartTime = null;
		if(chats.size()>0){
			lastChartTime = chats.get(chats.size()-1).getCreateDate().getTime()+10;
		}
		mChatService.getChats(channelId, lastChartTime, new OnResultListener<ChatRoomReturn>(){

			@Override
			public boolean onIntercept() {
				if(!historyBefore){
					getChatList(2000);
		    		return true;
		    	}
				return false;
			}

			@Override
			public void onFailure(int errorCode, String msg) {
			}

			@Override
			public void onSuccess(ChatRoomReturn crr) {
				if(isFirstTime){
//					CommonUtil.closeProgressDialog();
					isFirstTime = false;
					if(chats==null||chats.size()==0){
						noChat.setVisibility(View.VISIBLE);
					}else{
						noChat.setVisibility(View.GONE);
					}
				}
				if(crr != null){
					refreshOnlineCount(crr.getOnLineNum());
					List<Chart> cs = crr.getCharts();
					if(cs!=null && cs.size()>0){
						boolean isBottom = false;
						if(chatContentList.getLastVisiblePosition()==chats.size() || isCanScroll){
							isBottom = true;
							isCanScroll = false;
						}
						Iterator<Chart> it = cs.iterator();
						while (it.hasNext()) {
							Chart value = it.next();
							if (value.getUserId().equals(StarApplication.mUser.getId())) {
								it.remove();
								break;
							}
							boolean result = mChatService.addChatToDB(new ChatVO(value, ChatVO.STATUS_VALID), channelId);
							if(!result){
								it.remove();
								break;
							}
						}
						if(cs.size()>0){
	//						getChatNum();
							List<ChatVO> vos = mChatService.parserChartToChatVO(cs);
							chats.addAll(vos);
							mAdapter.updateDateRefreshUi(chats);
						}
						if(isBottom){
							scrollToBottom();
						}else{
							newChatCount += cs.size();
							if(newChatCount>0){
								newChat.setText(String.valueOf(newChatCount));
								newChat.setVisibility(View.VISIBLE);
							}
						}
					}
				}
				getChatList(2000);
			}
			
			private void refreshOnlineCount(Long onLineNum) {
				if(onLineNum!=null && onLineNum>0){
					olineIcon.setImageResource(R.drawable.people_icon_actionbar_grey);
					onlineCount.setText(onLineNum+"");
					onlineCount.setTextColor(Color.parseColor("#ffffff"));
				}else{
					olineIcon.setImageResource(R.drawable.people_icon_actionbar_grey);
					onlineCount.setText("0");
					onlineCount.setTextColor(Color.parseColor("#ffffff"));
				}
			}
			
		});
	}
    
    private void getHistoryChatList() {
		new LoadingDataTask() {
			private List<ChatVO> cs;
			@Override
			public void onPreExecute() {
			}

			@Override
			public void onPostExecute() {
				historyBefore = true;
				responsSize = cs!=null ? cs.size() : 0;
				if(cs!=null && cs.size()>0){
					for(ChatVO c : cs){
						chats.add(0, c);
					}
 					mAdapter.updateDateRefreshUi(chats);
				}
 				if(isFirstTime){
 					if(responsSize < requestCount) {
 						chatContentList.removeHeaderView(headerView);//会刷新list?
 					}
 					if(responsSize > 0)
 						scrollToBottom();
 					//判断是否是FAQ聊天室
 					if(isRobotCustomerChat){
 						olineIcon.setVisibility(View.GONE);
 						onlineCount.setVisibility(View.GONE);
 						mFaqService.seekToFAQ("0", new OnResultListener<FAQ>() {
 							@Override
 							public boolean onIntercept() {
 								scrollToBottom();
 								return false;
 							}
 							
 							@Override
 							public void onSuccess(FAQ faq) {
 								Log.i("initData", "isRobotCustomerChat="+isRobotCustomerChat+"  "+faq.response);
 								ChatVO chartRobot = robotSay(faq.response, null, getString(R.string.faq_name));
 								chats.add(chartRobot);
 								mAdapter.updateDateRefreshUi(chats);
 								scrollToBottom();
 							}
 							
 							@Override
 							public void onFailure(int errorCode, String msg) {
 								scrollToBottom();
 							}
 						});
 					}else {
 						olineIcon.setVisibility(View.VISIBLE);
 						onlineCount.setVisibility(View.VISIBLE);
					}
					if(chats.size()>0){
//						CommonUtil.closeProgressDialog();
						noChat.setVisibility(View.GONE);
						isFirstTime = false;
					}
				}else{
					chatContentList.setSelection(responsSize);
				}
			}

			@Override
			public void doInBackground() {
				cs = mChatService.getHistoryChats(channelId, offset, requestCount);
			}
		}.execute();
	}
    
    public void getChatList(long time) {
    	if(mChatTimerTask == null)
    		return;
    	mDefaultHandler.removeCallbacks(mChatTimerTask);
		mDefaultHandler.postDelayed(mChatTimerTask, time);
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
				getHistoryChatList();
			}
		});
		mService.getUser(this, false);
	}
	
	private void submitChatContent(final String msg, final int type){
		if(!isRobotCustomerChat)
			submitChatContent(msg, type, false);
		else
			submitChatContentRobot(msg, type);
	}
	private void submitChatContentRobot(final String msg, final int type) {
		if (TextUtils.isEmpty(mIndexCon)) {
			mIndexCon = "0-";
		} else {
			mIndexCon = mIndexCon + "-";
		}
		mFaqService.seekToFAQ(mIndexCon+msg, new OnResultListener<FAQ>() {

			@Override
			public boolean onIntercept() {
				scrollToBottom();
				return false;
			}

			@Override
			public void onSuccess(FAQ faq) {
				ChatVO chartRobot = robotSay(faq.response, null, getString(R.string.faq_name));
				mIndexCon = faq.index;
				if (faq.index.equals(mIndexCon+msg)||msg.matches(regExpres)) {
					if(faq.type == FAQ.type_leaf_node){
						chartRobot.setType(Chart.TYPE_LINK);
						mIndexCon = null;
					}
					chats.add(robotSay(msg, StarApplication.mUser.getId(), StarApplication.mUser.getNickName()));
					chats.add(chartRobot);
					mAdapter.updateDateRefreshUi(chats);
				}else{
					if(faq.type == FAQ.type_leaf_node){
						chartRobot.setType(Chart.TYPE_LINK);
						mIndexCon = null;
					}
					submitChatContent(msg, type, false, faq);
				}
				scrollToBottom();
			}

			@Override
			public void onFailure(int errorCode, String msg) {
				scrollToBottom();
			}
		});
	}
	
	private ChatVO robotSay(String msg, Long userId, String userName) {
		ChatVO chartRobot = new ChatVO();
		chartRobot.setCreateDate(new Date());
		chartRobot.setMsg(msg);
		chartRobot.setUserId(userId);
		chartRobot.setUserName(userName);
		chartRobot.setIcoURL(getString(R.string.robot_icon_url));
		return chartRobot;
	}
	private String mIndexCon = null;
	
	private void submitChatContent(final String msg, final int type, final boolean showToast){
		submitChatContent(msg, type, showToast, null);
	}
		
	private void submitChatContent(final String msg, final int type, final boolean showToast, final FAQ faq){
		mChatService.sendText(channelId, msg, type, new OnResultListener<Chart>() {
			private ChatVO chatVO;
			@Override
			public void onSuccess(Chart value) {
				if(value != null){
					chatVO.setId(value.getId());
					chatVO.setCreateDate(value.getCreateDate());
					chatVO.setStatus(ChatVO.STATUS_VALID);
//					SharedPreferencesUtil.saveChatRoomMsgCount(ChatActivity.this, chatroom, 1);
					EggAppearService.appearEgg(ChatActivity.this, EggAppearService.Chatroom_chat);
					if(showToast){
						ToastUtil.showToastWithImage(ChatActivity.this, getString(R.string.share_successful), R.drawable.right_icon_register_green);
					}
					
					if(isRobotCustomerChat){
						ChatVO waitFeedback = robotSay(getString(R.string.robot_propmt), null, getString(R.string.faq_name));
						chats.add(waitFeedback);
						if(faq!=null){
							chats.add(robotSay(faq.response, null, getString(R.string.faq_name)));
						}
					}
				}else{
					chatVO.setStatus(ChatVO.STATUS_INVALID);
				}
				mChatService.addChatToDB(chatVO, channelId);
				mAdapter.updateDateRefreshUi(chats);
				scrollToBottom();
			}
			
			@Override
			public boolean onIntercept() {
				chatVO = newChatToSend(msg, null, type);
				chats.add(chatVO);
				if(noChat.getVisibility() == View.VISIBLE){
					noChat.setVisibility(View.GONE);
				}
				mAdapter.updateDateRefreshUi(chats);
				scrollToBottom();
				return false;
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
	
	private ChatVO newChatToSend(String msg, String imageUrl, int type) {
		Chart chart = new Chart(msg, StarApplication.mUser.getNickName(), StarApplication.mUser.getId(), new Date(), StarApplication.mUser.getHead());
		chart.setType(type);
		chart.setImageURL(imageUrl);
		ChatVO chatVO = new ChatVO(chart, ChatVO.STATUS_SENDING);
		return chatVO;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Activity.RESULT_OK){
			try{
				List<ImageBean> images = (List<ImageBean>) data.getSerializableExtra("selectImages");
				if(images!=null && images.size()>0){
					sendImage(images);
				}
			}catch (Exception e) {
				ToastUtil.centerShowToast(ChatActivity.this, getString(R.string.send_picture_fail));
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void sendImage(final List<ImageBean> selectImages) {
		mChatService.sendImage(channelId, selectImages, new OnListResultListener<Chart>() {
			
			@Override
			public boolean onIntercept() {
				for(ImageBean bean : selectImages){
					chats.add(newChatToSend(null, "tenbre://"+bean.getPath(), Chart.TYPE_IMAGE));
				}
				mAdapter.updateDateRefreshUi(chats);
				scrollToBottom();
				return false;
			}
			
			@Override
			public void onFailure(int errorCode, String msg) {
				ToastUtil.centerShowToast(ChatActivity.this, getString(R.string.send_picture_fail));
				int i = chats.size()-selectImages.size();
				for(; i<chats.size();i++){
					chats.get(i).setStatus(ChatVO.STATUS_INVALID);
					mChatService.addChatToDB(chats.get(i), channelId);
				}
				mAdapter.updateDateRefreshUi(chats);
				//发图片的处理
				sendImageInRobotChat();
			}
			
			@Override
			public void onSuccess(List<Chart> result) {
				if(result != null && result.size() != 0) {
					for(int i=0; i<result.size(); i++){
						ChatVO vo = chats.get(chats.size()-result.size()+i);
						Chart ch = result.get(i);
						vo.setId(ch.getId());
						vo.setCreateDate(ch.getCreateDate());
//						vo.setImageURL(ch.getImageURL());
						vo.setStatus(ChatVO.STATUS_VALID);
						mChatService.addChatToDB(vo, channelId);
					}
//					SharedPreferencesUtil.saveChatRoomMsgCount(ChatActivity.this, chatroom, result.size());
					if(result.size()==1){
						ToastUtil.centerShowToast(ChatActivity.this, String.format(getString(R.string.send_picture_success), 1));
						
					}else{
						ToastUtil.centerShowToast(ChatActivity.this, String.format(getString(R.string.send_picture_success), selectImages.size()));
					}
					//发图片的处理
					sendImageInRobotChat();
					
				} else {
					ToastUtil.centerShowToast(ChatActivity.this, getString(R.string.send_picture_fail));
					int i = chats.size()-selectImages.size();
					for(; i<chats.size();i++){
						chats.get(i).setStatus(ChatVO.STATUS_INVALID);
						mChatService.addChatToDB(chats.get(i), channelId);
					}
				}
				mAdapter.updateDateRefreshUi(chats);
			}

			
		});
	}
	/**
	 * 发送图片处理
	 */
	private void sendImageInRobotChat() {
		if(isRobotCustomerChat){
			mFaqService.seekToFAQ(mIndexCon + "", new OnResultListener<FAQ>() {

				@Override
				public boolean onIntercept() {
					scrollToBottom();
					return false;
				}

				@Override
				public void onSuccess(FAQ faq) {
					ChatVO chartRobot = robotSay(faq.response, null, getString(R.string.faq_name));
					chats.add(chartRobot);
					mIndexCon = faq.index;
					mAdapter.updateDateRefreshUi(chats);
					scrollToBottom();
				}

				@Override
				public void onFailure(int errorCode, String msg) {
					scrollToBottom();
				}
			});
		}
	}
	@Override
	public void onBackPressed() {
		if (isFaceShow) {
			setFaceLayoutVisibility(View.GONE);
			return;
		}
//		if(byShare){
//			if(isShareConfirm){
//				Intent intent = new Intent(this, HomeActivity.class);  
//				intent.putExtra("fragment", getString(R.string.fragment_tag_chatRooms));
//				CommonUtil.startActivityFromLeft(this, intent);
//				finish();
//			}else{
//				Intent intent = new Intent(this, ShareChatRoomActivity.class);  
//				CommonUtil.startActivityFromLeft(this, intent);
//			}
//			if(dialog!=null&&dialog.isShowing()){
//				dialog.dismiss();
//			}
//			return ;
//		}
		super.onBackPressed();
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
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_actionbar_back:
			onBackPressed();
			break;
		case R.id.iv_new_chat:
			scrollToBottom();
			break;
		case R.id.iv_chat_send:
			sendChatContent();
			break;
		case R.id.iv_image_send:
			Intent intent = new Intent();
			intent.setClass(this, AlbumActivity.class);
			intent.putExtra("channelId", channelId);
			CommonUtil.startActivityForResult(this, intent, 100);
			break;
		case R.id.iv_chat_face:
			if (!isFaceShow) {
				imm.hideSoftInputFromWindow(etChat.getWindowToken(), 0);
				setFaceLayoutVisibility(View.VISIBLE);
			} else {
				setFaceLayoutVisibility(View.GONE);
			}
			break;
		case R.id.et_chat_content:
			imm.showSoftInput(etChat, 0);
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					setFaceLayoutVisibility(View.GONE);
				}
			}, 100);
			isFaceShow = false;
			break;
		case R.id.ll_right:
			if(chatroom==null)
				return;
			Intent i = new Intent(this, ChatRoomSettingActicity.class);
			i.putExtra("room", chatroom);
			i.putExtra("open", notifSettingOpen);
			CommonUtil.startActivity(this, i);
			break;
		case R.id.chat_room_prompt_cancel:
			//隐藏提示
			mChatRoomPromptRL.setVisibility(View.GONE);
			break;
			
		default:
			break;
		}
	}

	private void scrollToBottom() {
		dismissNewChatView();
		chatContentList.postDelayed(new Runnable() {
	        @Override
	        public void run() {
	            // Select the last row so it will scroll into view...
	        	chatContentList.setSelection(chatContentList.getCount() - 1);
	        }
	    }, 100);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				setFaceLayoutVisibility(View.GONE);
//				imm.hideSoftInputFromWindow(etChat.getWindowToken(), 0);
			}
		}, 50);
	}

	private void dismissNewChatView() {
		if(newChat.getVisibility() == View.VISIBLE){
			newChat.setVisibility(View.GONE);
		}
		newChatCount = 0;
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
		case R.id.lv_chat_list:
			imm.hideSoftInputFromWindow(etChat.getWindowToken(), 0);
			setFaceLayoutVisibility(View.GONE);
			break;
		default:
			break;
		}
		return false;
	}

	private void sendChatContent() {
		String content = etChat.getText().toString();
		if(content != null && !content.isEmpty()){
			if(content.trim().length() == 0) {
				//内容不能为空
				ToastUtil.centerShowToast(ChatActivity.this, getString(R.string.blank_message_not_allowed));
			} else if (content.length() > 500) {
				// 内容太长
				ToastUtil.centerShowToast(ChatActivity.this, getString(R.string.only));
			} else {
				submitChatContent(etChat.getText().toString().trim(), Chart.TYPE_TEXT);
				sendChat.setVisibility(View.GONE);
				etChat.getEditableText().clear();
			}
		} 
	}
	
	private TextWatcher commentWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if(s.length() != 0) {
//				etChat.setBackgroundResource(R.drawable.chat_line_short);
				sendChat.setVisibility(View.VISIBLE);
			} else {
//				etChat.setBackgroundResource(R.drawable.chat_line_long);
				sendChat.setVisibility(View.GONE);
			}
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}
		
		@Override
		public void afterTextChanged(Editable s) {
		}
	};
	private ForwardDialog dialog;
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		switch (scrollState) {
		case OnScrollListener.SCROLL_STATE_IDLE:
			if (chatContentList.getFirstVisiblePosition() == 0) {
				offset += requestCount;
				if (responsSize < requestCount) {
					chatContentList.removeHeaderView(headerView);
					return;
				}
				getHistoryChatList();
			}
			if((chatContentList.getLastVisiblePosition()!=chats.size()-1&&chatContentList.getHeaderViewsCount()==0)
					||(chatContentList.getLastVisiblePosition()!=chats.size()&&chatContentList.getHeaderViewsCount()>0)){
				isCanScroll = false;
			}else{
				dismissNewChatView();
			}
			break;
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
	}
}
