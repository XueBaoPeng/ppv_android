package com.star.mobile.video.epg;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.star.cms.model.Comment;
import com.star.cms.model.Program;
import com.star.mobile.video.R;
import com.star.mobile.video.channel.ChatBottomInputView;
import com.star.mobile.video.fragment.EpgDetailFragment;
import com.star.mobile.video.me.mycoins.TaskService;
import com.star.mobile.video.service.EggAppearService;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.ToastUtil;
import com.star.mobile.video.view.NoScrollGridView;
import com.star.util.loader.OnListResultListener;
import com.star.util.loader.OnResultListener;

public class CommentListView extends LinearLayout implements OnClickListener {

	private List<Comment> comments = new ArrayList<Comment>();
	private ChatBottomInputView chatView;
	private CommentService commentService;
	private Program program;
	private TextView no_data_image;
	private CommentListAdapter commentListAdapter;
	private TaskService taskService;
	private EpgDetailFragment fragment;
	private Context c;
	private int count;

	public CommentListView(Context context) {
		super(context);
		this.c = context;
		taskService = new TaskService(context);
	}

	public CommentListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		commentService = new CommentService(context);
		taskService = new TaskService(context);
		initView();
		this.c = context;
	}

	public void setEpgDetailFragment(EpgDetailFragment fragment) {
		this.fragment = fragment;
	}
	
	private void initView() {
		LayoutInflater.from(getContext()).inflate(R.layout.view_comment_list, this);
		chatView=(ChatBottomInputView) findViewById(R.id.chatBottomInputView);
		chatView.getmEtChat().setHint(R.string.leave_comment);
		chatView.getSendChat().setOnClickListener(this);
		chatView.getSendFace().setOnClickListener(this);
		no_data_image=(TextView) findViewById(R.id.no_data_image);
		NoScrollGridView commentList = (NoScrollGridView) findViewById(R.id.channel_comment_list);
		commentListAdapter = new CommentListAdapter(getContext(), comments);
		commentList.setAdapter(commentListAdapter); 
		
	}

	public CommentListAdapter getListAdapter(){
		return this.commentListAdapter;
	}
	public void setCurrentProgram(Program program) {
		this.program = program;
		/*if(program==null)
			return;*/
		executeGetTask();
	}

	private void executeGetTask() {
	/*	new LoadingDataTask() {
			List<Comment> cs;
			@Override
			public void onPreExecute() {
			}

			@Override
			public void onPostExecute() {
				CommonUtil.closeProgressDialog();
				if(cs==null || cs.size()==0)
					return;
				if(fromLocal && fromNetAlready)
					return;
				comments.clear();
				comments.addAll(cs);
				commentListAdapter.updateDateRefreshUi(comments);
				if(!fromLocal){
					fromNetAlready = true;
				}
			}

			@Override
			public void doInBackground() {
				cs = commentService.getCommentsOfEpg(program.getId(), fromLocal);
			}
		}.execute();*/
		
		/*new HTTPInvoker<List<Comment>>() {
			@Override
			public void onFail(int statusLine) {
				CommonUtil.closeProgressDialog();
			}

			@Override
			public void onSuccess(List<Comment> cs) {
				CommonUtil.closeProgressDialog();
				if(cs==null || cs.size()==0)
					return;
				if(fromLocal && fromNetAlready)
					return;
				comments.clear();
				comments.addAll(cs);
				commentListAdapter.updateDateRefreshUi(comments);
				if(!fromLocal){
					fromNetAlready = true;
				}
			}

			@Override
			public RequestHandle http() {
				return commentService.getCommentsOfEpg(this,program.getId(), fromLocal);
			}

		}.go();*/
		commentService.getCommentsByEpgId(program.getId(), new OnListResultListener<Comment>() {

			@Override
			public boolean onIntercept() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onFailure(int errorCode, String msg) {
				no_data_image.setVisibility(View.VISIBLE);
			}

			@Override
			public void onSuccess(List<Comment> cs) {
				if(cs==null || cs.size()==0){
					no_data_image.setVisibility(View.VISIBLE);
					return ;
				}
					
				comments.clear();
				no_data_image.setVisibility(View.GONE);
				comments.addAll(cs);
				commentListAdapter.updateDateRefreshUi(comments);
			}
		});
	}

	 
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.video_live_content_send_iv:
			chatView.getFaceContainer().setVisibility(View.GONE);
			String submitMsg = chatView.getmEtChat().getText().toString().trim();
			if (!TextUtils.isEmpty(submitMsg)) {
				executeCommitTask(submitMsg);
			}
			break;
		case R.id.video_live_chat_face_iv:
			 count++;
			if(count%2==1){
				chatView.getFaceContainer().setVisibility(View.VISIBLE);
				chatView.hideSoftKeyboard(c);
				if(onFaceClick != null) {
					onFaceClick.click((chatView.getFaceContainer().getBottom() - chatView.getFaceContainer().getTop())+(getBottom()-getTop()));
				}
				
			}else{
				chatView.getFaceContainer().setVisibility(View.GONE);
			}
			break;
		default:
			break;
		}

	}

	public void executeCommitTask(final String submitMsg) {
		if(submitMsg.length() < 6 || submitMsg.length() > 500) {
			ToastUtil.centerShowToast(c, "The comment should be no less than 6 letters and no more than 500 letters.");
			return;
		}
//		new LoadingDataTask() {
//			private Comment comment;
//			
//			@Override
//			public void onPreExecute() {
//				CommonUtil.showProgressDialog(c, null, "submitting comments...");
//			}
//
//			@Override
//			public void onPostExecute() {
//				CommonUtil.closeProgressDialog();
//				if(comment != null) {
//					etComment.clearComposingText();
//					fragment.updateCommentCount();
//					executeGetTask(false);
//					etComment.getEditableText().clear();
//					homeActivity.closeKeyboard();
//					sendComment.setVisibility(View.GONE);
//					EggAppearService.appearEgg(c, EggAppearService.Comments_commit);
//				} else {
//					ToastUtil.centerShowToast(c, "submitting comment failure!");
//				}
//				
//			}
//
//			@Override
//			public void doInBackground() {
//				comment = commentService.submitComment(program.getId(), submitMsg);
//			}
//		}.execute();
//		new HTTPInvoker<Comment>() {
//			@Override
//			public void onFail(int statusLine) {
//				CommonUtil.closeProgressDialog();
//				ToastUtil.centerShowToast(c, "submitting comment failure!");
//			}
//
//			@Override
//			public void onSuccess(Comment comment) {
//				CommonUtil.closeProgressDialog();
//				chatView.getmEtChat().clearComposingText();
//				executeGetTask();
//				chatView.getmEtChat().getEditableText().clear();
//				EggAppearService.appearEgg(c, EggAppearService.Comments_commit);
//			}
//
//			@Override
//			public RequestHandle http() {
//				return commentService.submitComment(this,program.getId(), submitMsg);
//			}
//
//		}.go();
		commentService.submitComment(program.getId(), submitMsg, new OnResultListener<Comment>() {

			@Override
			public boolean onIntercept() {
				CommonUtil.showProgressDialog(c, null, "submitting comments...");
				return false;
			}

			@Override
			public void onSuccess(Comment value) {
				CommonUtil.closeProgressDialog();
				no_data_image.setVisibility(View.GONE);
				chatView.getFaceContainer().setVisibility(View.GONE);
				executeGetTask();
				chatView.getmEtChat().getEditableText().clear();
				EggAppearService.appearEgg(c, EggAppearService.Comments_commit);
			}

			@Override
			public void onFailure(int errorCode, String msg) {
				CommonUtil.closeProgressDialog();
			}
		});
	}
	 
	private OnFaceClick onFaceClick;
	
	public OnFaceClick getOnFaceClick() {
		return onFaceClick;
	}

	public void setOnFaceClick(OnFaceClick onFaceClick) {
		this.onFaceClick = onFaceClick;
	}



	public interface OnFaceClick {
		public void click(int height);
	}

}
