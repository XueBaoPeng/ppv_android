package com.star.mobile.video.chatroom.faq;

import java.util.ArrayList;
import java.util.List;

import com.star.cms.model.Chart;
import com.star.cms.model.User;
import com.star.mobile.video.R;
import com.star.mobile.video.account.AccountService;
import com.star.mobile.video.activity.ChatActivity;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.channel.ChatBottomInputView;
import com.star.mobile.video.model.ChatVO;
import com.star.mobile.video.service.EggAppearService;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.ToastUtil;
import com.star.util.loader.OnResultListener;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 机器人和用户聊天的类
 * 
 * @author Lee
 * @date 2016/01/08
 * @version 1.0
 *
 */
public class RobotCustomerChatActivity extends BaseActivity implements OnClickListener {
	private com.star.mobile.video.view.ListView mRobotCustomeChatListView;
	private RobotCustomerChatAdapter mRobotCustomerChatAdapter;
	private View mRobotCustomeChatLoading;

	private ChatBottomInputView chatInputView;
	private EditText etFeedback;
	private ImageView ivSendFeedback;
	private List<FAQVO> mChatContentList = new ArrayList<FAQVO>();
	private AccountService mAccountService;
	private User mUser;
	private FAQService mFaqService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_robot_custoner_chat);
		initView();
		initData();
	}

	/**
	 * view初始化
	 */
	private void initView() {
		((ImageView) findViewById(R.id.iv_actionbar_back)).setOnClickListener(this);
		((TextView) findViewById(R.id.tv_actionbar_title)).setText(getString(R.string.faq));
		mRobotCustomeChatListView = (com.star.mobile.video.view.ListView) findViewById(R.id.robot_customer_listview);
		
//		mRobotCustomeChatListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		
		mRobotCustomeChatLoading = (View) findViewById(R.id.robot_customer_loadingView);
		mRobotCustomeChatLoading.setVisibility(View.GONE);

		chatInputView = (ChatBottomInputView) findViewById(R.id.chatInputView);
		ivSendFeedback = chatInputView.getSendChat();
		chatInputView.setSendFaceGone();
	}

	/**
	 * 数据初始化
	 */
	private void initData() {
		mFaqService = new FAQService(this);
		mAccountService = new AccountService(this);
		etFeedback = chatInputView.getmEtChat();
		etFeedback.setCursorVisible(false);
		etFeedback.setHint(getString(R.string.faq_hint_text));
		etFeedback.setHintTextColor(Color.parseColor("#D2D2D2"));
		etFeedback.setTextSize(16);
		etFeedback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				etFeedback.setCursorVisible(true);
				if (etFeedback.getHint().toString() != null) {
					etFeedback.setHint("");
				}
			}
		});
		ivSendFeedback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				submitAppComment();
			}
		});
		getUserInfo();
//		CommonUtil.showProgressDialog(RobotCustomerChatActivity.this, null, getString(R.string.loading));
		mFaqService.seekToFAQ("0", new OnResultListener<FAQ>() {

			@Override
			public boolean onIntercept() {
				return false;
			}

			@Override
			public void onSuccess(FAQ faq) {
				CommonUtil.closeProgressDialog();
				FAQVO chart = new FAQVO();
				String robotContent = faq.response;

				chart.setMsg(robotContent);
				mChatContentList.add(chart);
				mRobotCustomerChatAdapter = new RobotCustomerChatAdapter(RobotCustomerChatActivity.this,
						mChatContentList);
				mRobotCustomeChatListView.setAdapter(mRobotCustomerChatAdapter);
			}

			@Override
			public void onFailure(int errorCode, String msg) {
				CommonUtil.closeProgressDialog();
			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
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

	private String mIndexCon = null;
	private String mFaqType = null;
	/**
	 * 提交内容
	 */
	private void submitAppComment() {
		String msg = etFeedback.getText().toString();
		if (msg.trim().length() <= 0) {
			ToastUtil.centerShowToast(RobotCustomerChatActivity.this, getString(R.string.faq_comment_should));
			return;
		}
		CommonUtil.showProgressDialog(RobotCustomerChatActivity.this, null, getString(R.string.loading));
		chatInputView.getmEtChat().getEditableText().clear();
		// 把数据存放到chart对象里，以用户的模式展现
		FAQVO chart = new FAQVO();
		chart.setMsg(msg);
		if (mUser != null) {
			chart.setUserId(mUser.getId());
			chart.setUserName(mUser.getUserName());
		}
		mChatContentList.add(chart);
		mRobotCustomerChatAdapter.setRobotCustomerChatDatas(mChatContentList);

		if (FAQ.type_leaf_node.equals(mFaqType)) {
			msg = "0";
		}
		if ("0".equals(mIndexCon) || mIndexCon == null || mFaqType.equals(FAQ.type_leaf_node)) {
			mIndexCon = "";
		} else {
			mIndexCon = mIndexCon + "-";
		}
		mFaqService.seekToFAQ(mIndexCon+msg, new OnResultListener<FAQ>() {

			@Override
			public boolean onIntercept() {
				return false;
			}

			@Override
			public void onSuccess(FAQ faq) {
				CommonUtil.closeProgressDialog();
				FAQVO chartRobot = new FAQVO();
				String robotContent = faq.response;
				mFaqType = faq.type;
				mIndexCon = faq.index;
				chartRobot.setNodeType(faq.type);
				chartRobot.setMsg(robotContent);
				mChatContentList.add(chartRobot);
				mRobotCustomerChatAdapter.setRobotCustomerChatDatas(mChatContentList);
			}

			@Override
			public void onFailure(int errorCode, String msg) {
				CommonUtil.closeProgressDialog();
			}
		});

		scrollMyListViewToBottom();
	}

	/**
	 * 获得用户信息
	 */
	private void getUserInfo() {
		mAccountService.getUser(new OnResultListener<User>() {

			@Override
			public boolean onIntercept() {
				return false;
			}

			@Override
			public void onSuccess(User user) {
				mUser = user;
			}

			@Override
			public void onFailure(int errorCode, String msg) {
			}
		});
	}
	/**
	 * 移动到底部
	 */
	private void scrollMyListViewToBottom() {
		mRobotCustomeChatListView.postDelayed(new Runnable() {
	        @Override
	        public void run() {
	            // Select the last row so it will scroll into view...
	        	mRobotCustomeChatListView.setSelection(mRobotCustomeChatListView.getCount() - 1);
	        }
	    }, 500);
	}
	
	
//	private void submitChatContent(final String msg, final int type, final boolean showToast){
//		mChatService.sendText(channelId, msg, type, new OnResultListener<Chart>() {
//			private ChatVO chatVO;
//			@Override
//			public void onSuccess(Chart value) {
//				if(value != null){
//					chatVO.setId(value.getId());
//					chatVO.setCreateDate(value.getCreateDate());
//					chatVO.setStatus(ChatVO.STATUS_VALID);
////					SharedPreferencesUtil.saveChatRoomMsgCount(ChatActivity.this, chatroom, 1);
//					EggAppearService.appearEgg(ChatActivity.this, EggAppearService.Chatroom_chat);
//					if(showToast){
//						ToastUtil.showToastWithImage(ChatActivity.this, getString(R.string.share_successful), R.drawable.right_icon_register_green);
//					}
//				}else{
//					chatVO.setStatus(ChatVO.STATUS_INVALID);
//				}
//				mChatService.addChatToDB(chatVO, channelId);
//				mAdapter.updateDateRefreshUi(chats);
//				scrollToBottom();
//			}
//			
//			@Override
//			public boolean onIntercept() {
//				chatVO = newChatToSend(msg, null, type);
//				chats.add(chatVO);
//				if(noChat.getVisibility() == View.VISIBLE){
//					noChat.setVisibility(View.GONE);
//				}
//				mAdapter.updateDateRefreshUi(chats);
//				scrollToBottom();
//				return false;
//			}
//			
//			@Override
//			public void onFailure(int errorCode, String msg) {
//				chatVO.setStatus(ChatVO.STATUS_INVALID);
//				mChatService.addChatToDB(chatVO, channelId);
//				mAdapter.updateDateRefreshUi(chats);
//				scrollToBottom();
//			}
//		});
//	}
}
