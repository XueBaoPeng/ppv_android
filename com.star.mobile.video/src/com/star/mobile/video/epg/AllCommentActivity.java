package com.star.mobile.video.epg;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.star.cms.model.Comment;
import com.star.mobile.video.R;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.channel.ChatBottomInputView;
import com.star.mobile.video.search.SearchActivity;
import com.star.mobile.video.service.EggAppearService;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.DensityUtil;
import com.star.mobile.video.util.ToastUtil;
import com.star.mobile.video.view.ListView;
import com.star.util.loader.OnListResultListener;
import com.star.util.loader.OnResultListener;

public class AllCommentActivity extends BaseActivity implements OnClickListener{
	
	private  ListView channelCommentListView;
	private AllCommentListAdapter adapter;
	private List<Comment> comments = new ArrayList<Comment>();
 	private ScrollView sc;
	private TextView noData_image;
	private ChatBottomInputView chatInputView;
	private CommentService commentService;
	private long programId;
	private int count=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_channel_comment);
		
		Intent intent = getIntent();
		programId = intent.getLongExtra("programId", 330);
		commentService = new CommentService(this);
		
		initView();
		getData(true);
		getData(false);
		adapter = new AllCommentListAdapter(this, comments);
		channelCommentListView.setAdapter(adapter);
		EggAppearService.appearEgg(this, EggAppearService.Chatroom_in);
	}
	
	private void initView() {
		((TextView)findViewById(R.id.tv_actionbar_title)).setText(R.string.all_comments_title);
		((ImageView)findViewById(R.id.iv_actionbar_back)).setOnClickListener(this);
		((ImageView) findViewById(R.id.iv_actionbar_search)).setImageResource(R.drawable.actionbar_search);
		((ImageView) findViewById(R.id.iv_actionbar_search)).setOnClickListener(this);
		 sc = (ScrollView) findViewById(R.id.scrollview);
		noData_image=(TextView) findViewById(R.id.no_data_image);
		chatInputView=(ChatBottomInputView) findViewById(R.id.chatInputView);
		chatInputView.getmEtChat().addTextChangedListener(commentWatcher);
		chatInputView.getmEtChat().setHint(R.string.leave_comment);
		chatInputView.getSendChat().setOnClickListener(this); 
		chatInputView.getSendFace().setOnClickListener(this);
		channelCommentListView = (ListView) findViewById(R.id.channel_comment_list);
		View footerView = new View(this);
		footerView.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, DensityUtil.dip2px(this, 50)));
		channelCommentListView.addFooterView(footerView);
	}
	
	private boolean fromNetAlready = false;
	private void getData(final boolean fromLocal) {
//		new LoadingDataTask() {
//			List<Comment> cs;
//			@Override
//			public void onPreExecute() {
//			}
//
//			@Override
//			public void onPostExecute() {
//				if(cs==null || cs.size()==0){
//					return;
//				}
//				if(fromLocal && fromNetAlready)
//					return;
//				comments.clear();
//				comments.addAll(cs);
//				adapter.updateDateRefreshUi(comments);
//				if(!fromLocal){
//					fromNetAlready = true;
//				}
//			}
//			
//			@Override
//			public void doInBackground() {
//				cs = commentService.getCommentsByEpgId(programId, fromLocal);
//			}
//		}.execute();
	 
		commentService.getCommentsByEpgId(programId, new OnListResultListener<Comment>() {

			@Override
			public boolean onIntercept() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onFailure(int errorCode, String msg) {
				noData_image.setVisibility(View.VISIBLE);
			}

			@Override
			public void onSuccess(List<Comment> cs) {
				if(fromLocal && fromNetAlready){
					return;	
				}
				if(cs.size()==0){
					noData_image.setVisibility(View.VISIBLE);
				}else{ 
					comments.clear();
					comments.addAll(cs);
					adapter.updateDateRefreshUi(comments);
					if(!fromLocal){
						fromNetAlready = true;
					}
				}
			}
		});
	 
	}

	private void commitCommentTask() {
		if(chatInputView.getmEtChat().getText().toString().trim().length() < 6 || chatInputView.getmEtChat().getText().toString().trim().length() > 500) {
			ToastUtil.centerShowToast(AllCommentActivity.this, getString(R.string.comment_less));
			return;
		}
//		new LoadingDataTask() {
//			private Comment comment ;
//			@Override
//			public void onPreExecute() {
//				CommonUtil.showProgressDialog(AllCommentActivity.this, null, getString(R.string.submitting));
//			}
//			
//			@Override
//			public void onPostExecute() {
//				CommonUtil.closeProgressDialog();
//				if(comment != null) {
//					adapter.updateDateRefreshUi(comments);
//					sendComment.setVisibility(View.GONE);
//					etComment.getEditableText().clear();
//					InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
//					imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
//					EggAppearService.appearEgg(AllCommentActivity.this, EggAppearService.Comments_commit);
//				} else {
//					ToastUtil.centerShowToast(AllCommentActivity.this, getString(R.string.submitting_comment_failure));
//				}
//				
//			}
//			
//			@Override
//			public void doInBackground() {
//				comment = commentService.submitComment(programId,etComment.getText().toString());
//				comments = commentService.getCommentsByEpgId(programId);
//				
//			}
//		}.execute();
		
		CommonUtil.showProgressDialog(AllCommentActivity.this, null, getString(R.string.submitting));
	 
		commentService.submitComment(programId,chatInputView.getmEtChat().getText().toString(), new OnResultListener<Comment>() {
			
			@Override
			public boolean onIntercept() {
				 
				return false;
			}

			@Override
			public void onSuccess(Comment value) {
				CommonUtil.closeProgressDialog();
				getData(false);
				noData_image.setVisibility(View.GONE);
				chatInputView.getFaceContainer().setVisibility(View.GONE);
				chatInputView.hideSoftKeyboard(AllCommentActivity.this);
				chatInputView.getmEtChat().getEditableText().clear();
				EggAppearService.appearEgg(AllCommentActivity.this, EggAppearService.Comments_commit);
			}

			@Override
			public void onFailure(int errorCode, String msg) {
				ToastUtil.centerShowToast(AllCommentActivity.this, getString(R.string.submitting_comment_failure));
				noData_image.setVisibility(View.VISIBLE);
			}
		});
		
	}
	
	private TextWatcher commentWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if(s.length() != 0) {
				chatInputView.getmEtChat().setBackgroundResource(R.drawable.comment_frame_short);
			 
			} else {
				chatInputView.getmEtChat().setBackgroundResource(R.drawable.comment_frame);
		 
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_actionbar_back:
			super.onBackPressed();
			break;
		case R.id.video_live_content_send_iv:
			commitCommentTask();
			break;
		case R.id.video_live_chat_face_iv:
			count++;
			if(count%2==1){
				chatInputView.getFaceContainer().setVisibility(View.VISIBLE);
				chatInputView.hideSoftKeyboard(AllCommentActivity.this);
				 Handler hand=new Handler();
				hand.post(new Runnable() {
					
					@Override
					public void run() {
						sc.fullScroll(ScrollView.FOCUS_DOWN); 						
					}
				});
				 
			}else{
				chatInputView.getFaceContainer().setVisibility(View.GONE);
			}
			break;
		case R.id.iv_actionbar_search:
			CommonUtil.startActivity(this, SearchActivity.class);
			break;
		default:
			break;
		}
		
	}
}
