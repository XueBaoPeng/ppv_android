package com.star.mobile.video.me.feedback;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.star.cms.model.APPInfo;
import com.star.cms.model.Comment;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.account.AccountService;
import com.star.mobile.video.appversion.CurrentVersionAppDetailActivity;
import com.star.mobile.video.adapter.FeedbackAdapter;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.model.FunctionType;
import com.star.mobile.video.service.ApplicationService;
import com.star.mobile.video.service.EggAppearService;
import com.star.mobile.video.service.FeedbackService;
import com.star.mobile.video.service.FunctionService;
import com.star.mobile.video.util.ApplicationUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.ToastUtil;
import com.star.mobile.video.view.FaceContainer;
import com.star.util.loader.OnListResultListener;
import com.star.util.loader.OnResultListener;

public class FeedbackActivity extends BaseActivity implements OnClickListener {

	private FeedbackService feedbackService;
	private RelativeLayout rlFourLayer;
	private RelativeLayout rlFeedbackUpgrade;
	private ImageView mSplitLine;
	private ImageView ivFourLayerPoint;
	private ImageView ivUpgradePoint;
	private ListView lvFeedback;
//	private EditText etFeedback;
	private TextView mNoFeedBackIV;
//	private ChatBottomInputView chatBottomInputView;
	private ApplicationService applicationService;
//	private ImageView ivSendFeedback;
	private boolean reportDone;
	private DoFourLayerReciver receiver;
	private EditText mEtChat;
	private ImageView sendChat;
	private ImageView sendFace;
	private FaceContainer faceContainer;
	private AccountService accountService;
	private String content;
	private String TAG=FeedbackActivity.class.getName();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		feedbackService = FeedbackService.getInstance(this);
		applicationService = new ApplicationService(this);
		setContentView(R.layout.activity_feedback);
		Bundle bundle=getIntent().getExtras();
		if(bundle!=null){
			content = bundle.getString("content");
		}
		receiver = new DoFourLayerReciver();
		registerReceiver(receiver, new IntentFilter(FeedbackService.CAN_FOUR_LAYER));
		initView();
		EggAppearService.appearEgg(this, EggAppearService.Feedback);
		accountService = new AccountService(this);
		initData();
	}

	@Override
	protected void onResume() {
		super.onResume();
		getFeedbackData();
	}

	private void initView() {
		((ImageView) findViewById(R.id.iv_actionbar_back)).setOnClickListener(this);
		((TextView) findViewById(R.id.tv_actionbar_title)).setText(getResources().getString(R.string.feedback));
		rlFourLayer = (RelativeLayout) findViewById(R.id.rl_four_layer);
		rlFeedbackUpgrade = (RelativeLayout) findViewById(R.id.rl_feedback_upgrade);
		ivFourLayerPoint = (ImageView) findViewById(R.id.iv_four_layer_point);
		ivUpgradePoint = (ImageView) findViewById(R.id.iv_upgrade_point);
		lvFeedback = (ListView) findViewById(R.id.lv_feedback);
		mSplitLine = (ImageView) findViewById(R.id.layout_split_line_iv);
		mEtChat=(EditText) findViewById(R.id.video_live_chat_edittext);
		sendChat=(ImageView) findViewById(R.id.video_live_content_send_iv);
		sendFace=(ImageView) findViewById(R.id.video_live_chat_face_iv);
		faceContainer=(FaceContainer) findViewById(R.id.faceContainer);
//		etFeedback = chatBottomInputView.getmEtChat();
		mEtChat.setCursorVisible(false);
		mEtChat.setHint(getString(R.string.leave_your_precious_advice));
		mEtChat.setHintTextColor(Color.parseColor("#D2D2D2"));
		mEtChat.setTextSize(16);
		mEtChat.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mEtChat.setCursorVisible(true);
				if (mEtChat.getHint().toString() != null) {
					mEtChat.setHint("");
				}
			}
		});
//		ivSendFeedback = chatBottomInputView.getSendChat();
		mNoFeedBackIV = (TextView) findViewById(R.id.no_feedback_iv);
		if (FunctionService.doHideFuncation(FunctionType.FastReport)
				|| StarApplication.CURRENT_VERSION == Constant.FINAL_VERSION) {
			rlFourLayer.setVisibility(View.GONE);
			mSplitLine.setVisibility(View.GONE);
		} else {
			mSplitLine.setVisibility(View.VISIBLE);
			rlFourLayer.setOnClickListener(this);
			rlFourLayer.setVisibility(View.VISIBLE);
		}
		rlFeedbackUpgrade.setOnClickListener(this);
		sendChat.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				submitAppComment();
			}
		});

	}

	private void initData() {
		if(content!=null){
			mEtChat.setCursorVisible(true);
			mEtChat.setText(content);
			mEtChat.setSelection(mEtChat.getText().toString().length());
		}
	}

	private void getFeedbackData() {
		feedbackService.getAppComments(new OnListResultListener<Comment>() {

			@Override
			public boolean onIntercept() {
				return false;
			}

			@Override
			public void onFailure(int errorCode, String msg) {

			}

			@Override
			public void onSuccess(List<Comment> comments) {
				if (comments != null && comments.size() > 0) {
					mNoFeedBackIV.setVisibility(View.GONE);
					lvFeedback.setVisibility(View.VISIBLE);
					FeedbackAdapter adapter = new FeedbackAdapter(comments, FeedbackActivity.this);
					lvFeedback.setAdapter(adapter);
				} else {
					lvFeedback.setVisibility(View.GONE);
					mNoFeedBackIV.setVisibility(View.VISIBLE);
				}
			}
		});
		applicationService.getAppNewest(new OnResultListener<APPInfo>() {

			@Override
			public void onSuccess(APPInfo appInfo) {

				if (appInfo != null) {
					if (appInfo.getVersion() > ApplicationUtil.getAppVerison(FeedbackActivity.this)) {
						ivUpgradePoint.setVisibility(View.VISIBLE);
					} else {
						ivUpgradePoint.setVisibility(View.INVISIBLE);
					}
					FeedbackService fs = FeedbackService.getInstance(FeedbackActivity.this);
					reportDone = fs.isDoFourLayer();
					if (reportDone) {
						ivFourLayerPoint.setVisibility(View.VISIBLE);
					} else {
						ivFourLayerPoint.setVisibility(View.INVISIBLE);
					}
				} else {
					ToastUtil.centerShowToast(FeedbackActivity.this, getString(R.string.error_network));
				}

			}

			@Override
			public boolean onIntercept() {
				return false;
			}

			@Override
			public void onFailure(int errorCode, String msg) {
				ToastUtil.centerShowToast(FeedbackActivity.this, getString(R.string.error_network));
			}
		});
	}

	private void submitAppComment() {
		final String msg = mEtChat.getText().toString();
		if (msg.trim().length() <= 0) {
			ToastUtil.centerShowToast(FeedbackActivity.this, getString(R.string.comment_should));
			return;
		} else if (msg.trim().length() < 6) {
			ToastUtil.centerShowToast(FeedbackActivity.this, getString(R.string.comment_liment));
			return;
		}
		CommonUtil.showProgressDialog(FeedbackActivity.this, null, getString(R.string.loading));
		accountService.commitFeedback(msg, ApplicationUtil.getAppVerisonName(FeedbackActivity.this),
				new OnResultListener<Comment>() {

					@Override
					public void onSuccess(Comment result) {
						CommonUtil.closeProgressDialog();
						mEtChat.getEditableText().clear();
						ToastUtil.centerShowToast(FeedbackActivity.this, getString(R.string.submit_feedback_success));
						getFeedbackData();
					}

					@Override
					public boolean onIntercept() {
						return false;
					}

					@Override
					public void onFailure(int errorCode, String msg) {
						CommonUtil.closeProgressDialog();
						ToastUtil.centerShowToast(FeedbackActivity.this, getString(R.string.error_network));
					}
				});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_actionbar_back:
			super.onBackPressed();
			break;
		case R.id.rl_feedback_upgrade:
			CommonUtil.startActivity(FeedbackActivity.this, CurrentVersionAppDetailActivity.class);
			break;
		case R.id.rl_four_layer:
			if (reportDone) {
				startActivity(new Intent(FeedbackActivity.this, UserReportActivity.class));
				// CommonUtil.startActivity(FeedbackActivity.this,
				// UserReportActivity.class);
			} else {
				ToastUtil.centerShowToast(FeedbackActivity.this, getString(R.string.no_user_report));
			}
			break;
		default:
			break;
		}
	}

	private class DoFourLayerReciver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction() == FeedbackService.CAN_FOUR_LAYER) {
				ivUpgradePoint.setVisibility(View.VISIBLE);
			}
		}

	}
}
