package com.star.mobile.video.channel;

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
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView.OnEditorActionListener;

import com.star.cms.model.Chart;
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
import com.star.mobile.video.smartcard.BindCardEditViewA.CardNumberACallback;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.DensityUtil;
import com.star.mobile.video.util.LoadingDataTask;
import com.star.mobile.video.util.ToastUtil;
import com.star.mobile.video.view.FaceContainer;
import com.star.util.loader.OnResultListener;

/**
 * 频道聊天室
 * 
 * @author Lee @version1.0 2015/08/24
 *
 */
public class ChannelViewControlChat extends LinearLayout implements OnClickListener {
	private LayoutInflater mLayoutInflater;
	private Context mContext;
	private EditText etChat;
	private ImageView sendChat;
	private ImageView sendFace;

	private VideoLiveChatContentAdapter mAdapter;
	private ChatService mChatService;
	private List<ChatVO> chats = new ArrayList<ChatVO>();

	private boolean isFaceShow = false;
	private InputMethodManager imm;
	private android.widget.ListView chatContentList;;
	private Handler mDefaultHandler = new Handler();
	private Runnable mChatTimerTask;
	private Long channelId;
	private View faceLayout;
	private View llChatNotice;
	private LinearLayout loadingView;
	private View headerView;
	public ChannelViewControlChat(Context context) {
		super(context);
		initView(context);
	}

	public ChannelViewControlChat(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	private void initView(Context context) {
		this.mContext = context;
		mChatService = new ChatService(context);
		mLayoutInflater = LayoutInflater.from(context);
		View view = mLayoutInflater.inflate(R.layout.view_video_live_control_chat, this);
		loadingView = (LinearLayout) view.findViewById(R.id.loadingView);
		chatContentList = (android.widget.ListView) view.findViewById(R.id.video_live_chat_listview);
		llChatNotice = view.findViewById(R.id.ll_chat_notice);
		imm = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
		headerView = new View(getContext());
		headerView.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT,DensityUtil.dip2px(getContext(), 48)));
	}
	
	public void addView(EditText edittext, ImageView sendChat, ImageView sendFace, FaceContainer faceLayout){
		this.etChat = edittext;
		this.sendChat = sendChat;
		this.sendFace = sendFace;
		this.faceLayout = faceLayout;
		etChat.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				imm.showSoftInput(etChat, 0);
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						setFaceLayoutVisibility(View.GONE);
					}
				}, 50);
				isFaceShow = false;
			}
		});
		etChat.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
				if (arg1 == EditorInfo.IME_ACTION_UNSPECIFIED) {
					sendChatContent();
				}
				return false;
			}
		});
		this.sendChat.setOnClickListener(this);
		this.sendFace.setOnClickListener(this);
	}

	public void setCurrentChatRoom(final Long channelID){
		this.channelId = channelID;
		if(channelId==null)
			return;
		intoChatRoom();
	}
	public void clearChat(){
		loadingView.setVisibility(View.VISIBLE);
	}
	private void intoChatRoom() {
		chats.clear();
		chatContentList.removeHeaderView(headerView);
		chatContentList.addHeaderView(headerView);
		mAdapter = new VideoLiveChatContentAdapter(mContext, chats);
		chatContentList.setAdapter(mAdapter);
		showChatNotice();
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
		loadingView.setVisibility(View.VISIBLE);
		getHistoryChatList();
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
						Iterator<Chart> it = cs.iterator();
						while (it.hasNext()) {
							Chart value = it.next();
							if (value.getUserId().equals(StarApplication.mUser.getId())) {
								it.remove();
							}
						}
						if(cs.size()>0){
							hideChatNotice();
	//						getChatNum();
							chats.addAll(mChatService.parserChartToChatVO(cs));
							mAdapter.updateDateRefreshUi(chats);
							scrollToBottom();
						}
						
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
	private void getHistoryChatList() {
		new LoadingDataTask() {
			private List<ChatVO> cs;
			@Override
			public void onPreExecute() {
			}

			@Override
			public void onPostExecute() {
				loadingView.setVisibility(View.GONE);
				if(cs!=null && cs.size()>0){
					for(ChatVO c : cs){
						if(c.getType() == ChatVO.TYPE_TEXT){
							chats.add(0, c);	
						}
					}
					if(chats.size()>0){
						hideChatNotice();
					}
					listAnimCallback.onNotice(chatContentList);
 					mAdapter.updateDateRefreshUi(chats);
				}
			}

			@Override
			public void doInBackground() {
				cs = mChatService.getHistoryChats(channelId, 0, 5);
			}
		}.execute();
	}
	
	public void getChatList(long time) {
    	if(mChatTimerTask == null)
    		return;
    	mDefaultHandler.removeCallbacks(mChatTimerTask);
		mDefaultHandler.postDelayed(mChatTimerTask, time);
	}
	
	public void onStop() {
		if(mChatTimerTask == null || mDefaultHandler == null)
    		return;
		mDefaultHandler.removeCallbacks(mChatTimerTask);
		mChatTimerTask = null;
	}
	
	private void hideChatNotice() {
			llChatNotice.setVisibility(View.GONE);
	}
	private void showChatNotice() {
			llChatNotice.setVisibility(View.VISIBLE);
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
	public ListAnimCallback getListAnimCallback() {
		return listAnimCallback;
	}

	public void setListAnimCallback(ListAnimCallback listAnimCallback) {
		this.listAnimCallback = listAnimCallback;
	}
	private ListAnimCallback listAnimCallback;

	public interface ListAnimCallback {
		void onNotice(ListView listView);
	}
}
