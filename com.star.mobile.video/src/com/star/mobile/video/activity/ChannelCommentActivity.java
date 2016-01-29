package com.star.mobile.video.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.star.cms.model.Channel;
import com.star.cms.model.Comment;
import com.star.mobile.video.R;
import com.star.mobile.video.adapter.AllCommentListAdapter;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.search.SearchActivity;
import com.star.mobile.video.service.CommentService_old;
import com.star.mobile.video.service.EggAppearService;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.view.ListView.LoadingListener;

public class ChannelCommentActivity extends BaseActivity implements OnClickListener{
	
	private com.star.mobile.video.view.ListView commentListView;
	private AllCommentListAdapter adapter;
	private List<Comment> comments = new ArrayList<Comment>();
	private CommentService_old commentService;
	private Channel channel;
	private View loadingView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment);
		channel = (Channel) getIntent().getSerializableExtra("channel");
		if(channel==null){
			finish();
			return;
		}
		commentService = new CommentService_old(this);
		initView();
//		getData(true);
		commentListView.loadingData(false);
		adapter = new AllCommentListAdapter(this, comments);
		commentListView.setAdapter(adapter);
		EggAppearService.appearEgg(this, EggAppearService.Comments_in);
	}

	private void initView() {
		((TextView)findViewById(R.id.tv_actionbar_title)).setText(channel.getName()+" Comments");
		((ImageView) findViewById(R.id.iv_actionbar_search)).setOnClickListener(this);
		loadingView = findViewById(R.id.loadingView);
		commentListView = (com.star.mobile.video.view.ListView) findViewById(R.id.comment_list);
		findViewById(R.id.iv_actionbar_back).setOnClickListener(this);
		commentListView.setLoadingListener(new LoadingListener<Comment>() {
			
			@Override
			public List<Comment> loadingS(int offset, int requestCount) {
				return commentService.getCommentsByChannelId(channel.getId(), offset, requestCount);
			}
			
			@Override
			public List<Comment> loadingL(int offset, int requestCount) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public void loadPost(List<Comment> responseDatas) {
				List<Comment> cs = (List<Comment>) responseDatas;
				loadingView.setVisibility(View.GONE);
				if(cs.size()==0){
					return;
				}
//				comments.clear();
				comments.addAll(cs);
				adapter.updateDateRefreshUi(comments);
			}
			
			@Override
			public List<Comment> getFillList() {
				return comments;
			}

			@Override
			public void onNoMoreData() {
				// TODO Auto-generated method stub
			}
		});
	}
	
//	private void getData(final boolean fromLocal) {
//		new HTTPInvoker<List<Comment>>() {
//			@Override
//			public void onFail(int statusLine) {
//				loadingView.setVisibility(View.GONE);
//			}
//
//			@Override
//			public void onSuccess(List<Comment> cs) {
//				loadingView.setVisibility(View.GONE);
//				if(cs.size()==0){
//					return;
//				}
//				responsSize = cs.size();
////				comments.clear();
//				comments.addAll(cs);
//				adapter.updateDateRefreshUi(comments);
//			}
//
//			@Override
//			public RequestHandle http() {
//				return commentService.getChannelComments(this,channel.getId(), offset,Constant.request_item_count,fromLocal);
////				return commentService.getCommentsByChannelId(this,channel.getId(), fromLocal);
//			}
//
//		}.go();
//	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_actionbar_back:
			super.onBackPressed();
			break;
		case R.id.iv_actionbar_search:
			CommonUtil.startActivity(this, SearchActivity.class);
			break;
		default:
			break;
		}
		
	}

}
