package com.star.mobile.video.channel;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.star.cms.model.vo.ChannelVO;
import com.star.mobile.video.R;
import com.star.mobile.video.channel.ChannelViewControlChat.ListAnimCallback;
import com.star.mobile.video.home.tab.TabView;
import com.star.mobile.video.view.FaceContainer;

public class ChatView extends TabView<ListView> implements ListAnimCallback{

	private static final String FRAGMENT_INDEX = "fragment_index";

	private View mFragmentView;

	private int mCurIndex = -1;
	/** 标志位，标志已经初始化完成 */
	private boolean isPrepared;
	/** 是否已被加载过一次，第二次就不再去请求数据了 */
	private boolean mHasLoadedOnce = false;
	private ChannelViewControlChat mChannelViewControlChat;
	private EditText mEtChat;
	private ChannelVO mChannel;
	private Context mContext;
//	/**
//	/**
//	 * 创建新实例
//	 * 
//	 * @param index
//	 * @return
//	 */
//	public static ChatFragment newInstance(int index) {
//		Bundle bundle = new Bundle();
//		bundle.putInt(FRAGMENT_INDEX, index);
//		ChatFragment fragment = new ChatFragment();
////		fragment.setArguments(bundle);
//		return fragment;
//	}
	public ChatView(Context context) {
		super(context);
		mContext = context;
		initView();
	}
	public ChatView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initView();
	}
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		if(mFragmentView == null) {
//			mFragmentView =  inflater.inflate(R.layout.fragment_chat_room_home, container, false);
//			initView();
//			//获得索引值
////			Bundle bundle = getArguments();
////			if (bundle != null) {
////				mCurIndex = bundle.getInt(FRAGMENT_INDEX);
////			}
//			isPrepared = true;
//			lazyLoad();
//		}
//		
//		ViewGroup parent = (ViewGroup)mFragmentView.getParent();
//		if(parent != null) {
//			parent.removeView(mFragmentView);
//		}
//		return mFragmentView;
//	}
//	
	private void initView() {
		mFragmentView =  LayoutInflater.from(mContext).inflate(R.layout.fragment_chat_room_home, this);
//		mEtChat = (EditText) mFragmentView.findViewById(R.id.video_live_chat_edittext);
//		ImageView sendChat = (ImageView) mFragmentView.findViewById(R.id.video_live_content_send_iv);
//		ImageView sendFace = (ImageView) mFragmentView.findViewById(R.id.video_live_chat_face_iv);
//		FaceContainer faceContainer = (FaceContainer) mFragmentView.findViewById(R.id.faceContainer);
//		faceContainer.setEditText(mEtChat);
		
		mChannelViewControlChat = (ChannelViewControlChat) mFragmentView.findViewById(R.id.video_live_chat_control_view);
		mChannelViewControlChat.setListAnimCallback(this);
//		mChannelViewControlChat.addView(mEtChat, sendChat, sendFace, faceContainer);
	}

//	@Override
//	protected void lazyLoad() {
//		if (!isPrepared || !isVisible || mHasLoadedOnce) {
//			return;
//		}
//		mHasLoadedOnce=true;
//		setChannelID(mChannel.getId());
//	}
	public void updateUi(){
		if (mChannel != null){
			mChannelViewControlChat.updateChatRoom(mChannel.getId());
		}
	}
	private void setChannelID(long channelID) {
		mHasLoadedOnce = true;
		mChannelViewControlChat.setCurrentChatRoom(channelID);
	}
	public void setChannel(ChannelVO mChannel){
		this.mChannel = mChannel; 
//		if(!mHasLoadedOnce){
			setChannelID(mChannel.getId());	
//		}
		initTabsStatus();
	}
	public void clearChat(){
		mChannelViewControlChat.clearChat();
	}
	public void onStop(){
		mChannelViewControlChat.onStop();
	}
	public void hideSoftKeyboard(Context context) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if(mEtChat !=null)
			imm.hideSoftInputFromWindow(mEtChat.getWindowToken(), 0); // 强制隐藏键盘
	}
	public void setChatBottom(EditText mEtChat,ImageView sendChat,ImageView sendFace,FaceContainer faceContainer){
		mChannelViewControlChat.addView(mEtChat, sendChat, sendFace, faceContainer);
		this.mEtChat = mEtChat;
	}
	@Override
	public void onNotice(ListView listView) {
		initScrollListener(listView);
	}
}
