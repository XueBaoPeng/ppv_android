package com.star.mobile.video.view;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.RequestHandle;
import com.star.mobile.video.R;
import com.star.mobile.video.me.mycoins.TaskService;
import com.star.mobile.video.service.CommentService_old;
import com.star.mobile.video.service.EggAppearService;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.ToastUtil;
import com.star.mobile.video.util.http.HTTPInvoker;

public class PraiseView  extends LinearLayout {
	
	private CommentService_old commentService;
	private String commentId;
	private ImageView ivPraiseIcon;
	private TextView tvPraiseCount;
	private int praiseCount;
	private Context c;
	private TaskService taskService;
	private TextView tvUserName;
	private boolean praiseStatus = true;
	
	public PraiseView(Context context) {
		this(context, null);
	}
	public PraiseView(Context context, AttributeSet attrs) {
		super(context, attrs);
		commentService = new CommentService_old(context);
		LayoutInflater.from(context).inflate(R.layout.praise_view, this);
		tvPraiseCount = (TextView) findViewById(R.id.tv_praise_count);
		ivPraiseIcon = (ImageView) findViewById(R.id.iv_praise_logo);
		tvUserName = (TextView) findViewById(R.id.tv_praise_username);
		
		taskService = new TaskService(context);
		this.c = context;
	}
	
	public String getCommentId() {
		return commentId;
	}
	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}
	
	public void setIvPraiseIcon(int resId) {
		ivPraiseIcon.setBackgroundResource(resId);
	}
	
	public void setPraiseCount(int count) {
		tvPraiseCount.setText(count+"");
		praiseCount = count;
		if(praiseCount <= 0) {
			setPraiseStatus(false);
		}
	}
	public void setPraiseStatus(boolean status) {
		this.praiseStatus = status;
	}
	
	public void setUserName(String userName) {
		tvUserName.setText(userName);
	}
	
	public String getUserName() {
		return tvUserName.getText().toString();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		switch(event.getAction()) {
			
			case MotionEvent.ACTION_UP:
				String userName = getUserName();
				if(match(Constant.NUMBER_REG, userName) && userName != null) {
					userName = userName.substring(3, userName.length());
				}
				if(userName != null && !"".equals(userName)) {
					if(userName.equals(SharedPreferencesUtil.getUserName(c)) || getUserName().equals(SharedPreferencesUtil.getDiciveId(c))) {
						ToastUtil.centerShowToast(c, c.getString(R.string.like_comment_from_yourself));
						return true;
					} else {
						if(praiseStatus){
							addPraise();
							tvPraiseCount.setText((praiseCount+=1)+"");
							ivPraiseIcon.setBackgroundResource(R.drawable.icon_favour_press);
							SharedPreferencesUtil.saveCommentId(c, commentId,true);
							praiseStatus = false;
						} else {
							if(praiseCount < 1) {
								break;
							}
							delPraise();
							tvPraiseCount.setText((praiseCount-=1)+"");
							ivPraiseIcon.setBackgroundResource(R.drawable.icon_favour);
							SharedPreferencesUtil.saveCommentId(c, commentId, false);
							praiseStatus = true;
						}
					}
				}
				break;
				default:
					break;
		}
		return true;
	}
	
	
	private void addPraise() {
		EggAppearService.appearEgg(c, EggAppearService.Comments_praise);
//		new LoadingDataTask() {
//			
//			@Override
//			public void onPreExecute() {
//				
//			}
//			
//			@Override
//			public void onPostExecute() {
//			}
//			
//			@Override
//			public void doInBackground() {
//				commentService.addPraise(getCommentId());
//			}
//		}.execute();
		ToastUtil.centerShowToast(PraiseView.this.getContext(), "Submitting...");
		new HTTPInvoker<String>() {
			@Override
			public void onFail(int statusLine) {
				ToastUtil.centerShowToast(PraiseView.this.getContext(), c.getString(R.string.failed));
			}

			@Override
			public void onSuccess(String dataReturn) {
				ToastUtil.centerShowToast(PraiseView.this.getContext(), "Success!");
			}

			@Override
			public RequestHandle http() {
				return commentService.addPraise(this,getCommentId());
			}

		}.go();
	}
	
	private void delPraise() {
//		new LoadingDataTask() {
//			boolean reslut;
//			@Override
//			public void onPreExecute() {
//				
//			}
//			
//			@Override
//			public void onPostExecute() {
//				if(!reslut) {
//					ToastUtil.centerShowToast(c, c.getString(R.string.failed));
//				}
//			}
//			
//			@Override
//			public void doInBackground() {
//				reslut = commentService.delPraise(getCommentId());
//			}
//		}.execute();
		new HTTPInvoker<String>() {
			@Override
			public void onFail(int statusLine) {
				ToastUtil.centerShowToast(c, c.getString(R.string.failed));
			}

			@Override
			public void onSuccess(String dataReturn) {
			}

			@Override
			public RequestHandle http() {
				return commentService.delPraise(this,getCommentId());
			}

		}.go();
	}
	
	private boolean match(String regex, String str) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}
}
