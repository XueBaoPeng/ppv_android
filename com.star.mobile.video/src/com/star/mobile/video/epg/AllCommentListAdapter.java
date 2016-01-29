package com.star.mobile.video.epg;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.star.cms.model.Comment;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.tenb.TenbActivity;
import com.star.mobile.video.util.BitmapUtil;
import com.star.mobile.video.util.CalculationTimeUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.ExpressionUtil;
import com.star.ui.ImageView;
import com.star.ui.ImageView.Finisher;

public class AllCommentListAdapter extends BaseAdapter{
	
	private Context context;
	private List<Comment> datas;
	private String regExpres = "f0[0-9]{2}|f10[0-7]";
	public AllCommentListAdapter(Context c,List<Comment> data) {
		this.context = c;
		this.datas = data;
	}
	
	public void updateDateRefreshUi(List<Comment> data) {
		this.datas = data;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return datas.size();
	}
	public void setText(TextView textview,String content,String ZengZe){
		SpannableString spannableString = ExpressionUtil.getSpannableString(context, content, ZengZe);
		textview.setText(spannableString);
	}
	@Override
	public Object getItem(int position) {
		
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		
		final ViewHolder holder;
		Comment comment = datas.get(position);
		if(view == null) {
			holder = new ViewHolder();
			view = LayoutInflater.from(context).inflate(R.layout.chat_comment_item, null);
			holder.userLogo = (com.star.ui.ImageView) view.findViewById(R.id.user_logo);
			holder.commentContent = (TextView) view.findViewById(R.id.tv_comment_content);
			holder.userName = (TextView) view.findViewById(R.id.tv_comment_username);
			holder.dataTime = (TextView) view.findViewById(R.id.tv_data_time);
			holder.userLogo.setFinisher(new Finisher() {
				@Override
				public void run() {
					holder.userLogo.setImageBitmap(BitmapUtil.toRoundBitmap(holder.userLogo.getImage()));
				}
			});
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
			
		}
		setText(holder.commentContent, comment.getMsg(),regExpres);
		//holder.commentContent.setText(comment.getMsg());
		if(datas.get(position).getNickName() == null || "".equals(datas.get(position).getNickName())){
			holder.userName.setText("Guest");
		} else {
			if(SharedPreferencesUtil.getUserName(context) != null) {
				if(StarApplication.mUser != null && StarApplication.mUser.getId().equals(comment.getUserId())) {
					holder.userName.setText(StarApplication.mUser.getNickName());
				} else {
					holder.userName.setText(comment.getNickName());
				}
			} else {
				holder.userName.setText(comment.getNickName());
			}
		}
		holder.userLogo.setImageResource(R.drawable.no_portrait);
		if(StarApplication.mUser!=null && StarApplication.mUser.getId().equals(comment.getUserId())){
			holder.userLogo.setUrl(StarApplication.mUser.getHead());
		}else{
			if(comment.getIcoURL() != null) {
				holder.userLogo.setUrl(comment.getIcoURL());
			} else {
				holder.userLogo.setBackgroundResource(R.drawable.no_portrait);
			}
		}
		holder.dataTime.setText(CalculationTimeUtil.getTimeDisplay(comment.getCreateDate(), context));
		setHeadIconOnClick(holder,comment.getIcoURL(),comment.getNickName(),comment.getUserId());
		return view;
	}
	
	private void setHeadIconOnClick(ViewHolder holder,final String headIcon,final String nickName,final Long userId) {
		holder.userLogo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context,TenbActivity.class);
				intent.putExtra("nickname", nickName);
				intent.putExtra("headurl", headIcon);
				intent.putExtra("userId", userId);
				CommonUtil.startActivity((Activity)context, intent);
			}
		});
	}
	
	class ViewHolder {
		public ImageView userLogo;
		public TextView userName;
		public TextView commentContent;
		public TextView dataTime;
	}

}
