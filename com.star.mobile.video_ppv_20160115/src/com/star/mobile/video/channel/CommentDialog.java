package com.star.mobile.video.channel;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.star.cms.model.vo.CommentVO;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.ToastUtil;
import com.star.ui.ImageView;
import com.star.util.loader.OnResultListener;

public class CommentDialog extends Dialog {

	private TextView username;
	private EditText et_content;
	private ImageView userhead;
	private RelativeLayout send;
	private RatingBar ratingBar;
	private Context context;
	public CommentDialog(Context context) {
		this(context,0);
		this.context = context;
	}
	public CommentDialog(Context context, int theme){
        super(context, R.style.TaskInfoDialog);
        setContentView(R.layout.dialog_comment);
        et_content = (EditText) findViewById(R.id.et_comment_content);
		send = (RelativeLayout) findViewById(R.id.iv_comment_send);
		username = (TextView) findViewById(R.id.tv_comment_username);
		userhead = (ImageView) findViewById(R.id.user_logo);
		ratingBar = (RatingBar)findViewById(R.id.rt_user_score);
    }
	
	public void showCommentDialog(final boolean isEdit,final float rate,String content,final long channelID,final long commentID){
		ratingBar.setRating(rate);
		username.setText(StarApplication.mUser.getNickName());
		userhead.setUrl(StarApplication.mUser.getHead());
		if(content != null && !"".equals(content)){
			et_content.setText(content);
			et_content.setSelection(et_content.length());
		}
		final InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);  
		send.setOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String content = et_content.getText().toString().trim();
				 if (CommonUtil.containsEmoji(content)) {//不支持输入表情
					 ToastUtil.centerShowToast(context, context.getString(R.string.not_Emoji_input));
                 } else if(content.length()<500){
					if("".equals(content) || content.length() >= 6){
						((ChannelRateActivity)context).submitComment(isEdit,rate,content,channelID,commentID);
						imm.hideSoftInputFromWindow(et_content.getWindowToken(), 0); 
						dismiss();	
					}else{
						ToastUtil.centerShowToast(context, context.getString(R.string.comment_commit_limit));
					}
				}else{
					ToastUtil.centerShowToast(context, context.getString(R.string.comment_commit_limit));
				}
			}
		});
		show();
	}
	@Override
	public void show() {
		try{
			super.show();
		}catch (Exception e) {
			Log.w("", "show dialog error!", e);
		}
	}
	
}
