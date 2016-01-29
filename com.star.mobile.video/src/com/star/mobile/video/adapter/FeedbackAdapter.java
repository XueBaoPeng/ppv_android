package com.star.mobile.video.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.star.cms.model.Comment;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.activity.HeadMagnifyActivity;
import com.star.mobile.video.util.BitmapUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.DateFormat;
import com.star.ui.ImageView;
import com.star.ui.ImageView.Finisher;

public class FeedbackAdapter extends BaseAdapter{

	private List<Comment> datas;
	private Context context;
	
	public FeedbackAdapter(List<Comment> data,Context context) {
		this.datas = data;
		this.context = context;
	}
	
	@Override
	public int getCount() {
		
		return datas.size();
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
		final ViewHolder viewHolder;
		Comment comment = datas.get(position);
		if(view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(context).inflate(R.layout.view_feedback_item, null);
			viewHolder.rlUser = (RelativeLayout) view.findViewById(R.id.rl_user);
			viewHolder.rlStartimes = (RelativeLayout) view.findViewById(R.id.rl_startime);
			viewHolder.ivUserHeand = (ImageView) view.findViewById(R.id.iv_user_heand);
			viewHolder.ivStarHaend = (ImageView) view.findViewById(R.id.iv_startime_heand);
			viewHolder.tvUserMessageCreateDate = (TextView) view.findViewById(R.id.tv_user_message_create_date);
			viewHolder.tvUserMessage = (TextView) view.findViewById(R.id.tv_user_message);
			viewHolder.tvUserName = (TextView) view.findViewById(R.id.tv_user_name);
			viewHolder.tvStarMessageCreateDate = (TextView) view.findViewById(R.id.tv_startime_create_date);
			viewHolder.tvStarName = (TextView) view.findViewById(R.id.tv_startime_name);
			viewHolder.tvStartMessage = (TextView) view.findViewById(R.id.tv_startime_message);
			viewHolder.ivUserHeand.setFinisher(new Finisher() {
				@Override
				public void run() {
					viewHolder.ivUserHeand.setImageBitmap(BitmapUtil.toRoundBitmap(viewHolder.ivUserHeand.getImage()));
				}
			});
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)view.getTag();
		}
		if(comment.getChildren().size() != 0) {
			viewHolder.rlStartimes.setVisibility(View.VISIBLE);
			viewHolder.tvStarMessageCreateDate.setText(DateFormat.format(comment.getChildren().get(0).getCreateDate(), "yy-MM-dd HH:mm"));
			viewHolder.tvStarName.setText(comment.getChildren().get(0).getNickName());
			viewHolder.tvStartMessage.setText(comment.getChildren().get(0).getMsg());
		} else {
			viewHolder.rlStartimes.setVisibility(View.GONE);
		}
		viewHolder.tvUserMessageCreateDate.setText(DateFormat.format(comment.getCreateDate(), "yy-MM-dd HH:mm"));
		viewHolder.tvUserMessage.setText(comment.getMsg());
		if(StarApplication.mUser!=null && StarApplication.mUser.getId().equals(comment.getUserId())){
			viewHolder.ivUserHeand.setUrl(StarApplication.mUser.getHead());
			viewHolder.tvUserName.setText(StarApplication.mUser.getNickName());
			setHeadIconOnClick(viewHolder,StarApplication.mUser.getHead(),StarApplication.mUser.getNickName(),comment.getUserId());
		}else{
			viewHolder.ivUserHeand.setUrl(comment.getIcoURL());
			setHeadIconOnClick(viewHolder,comment.getIcoURL(),comment.getNickName(),comment.getUserId());
		}
		
		return view;
	}
	private void setHeadIconOnClick(ViewHolder holder,final String headIcon,final String nickName,final Long userId) {
		holder.ivUserHeand.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context,HeadMagnifyActivity.class);
				intent.putExtra("nickname", nickName);
				intent.putExtra("headurl", headIcon);
				intent.putExtra("userId", userId);
				CommonUtil.startActivity((Activity)context, intent);
			}
		});
		holder.ivStarHaend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context,HeadMagnifyActivity.class);
				intent.putExtra("nickname", nickName);
				intent.putExtra("headurl", headIcon);
				intent.putExtra("userId", userId);
				CommonUtil.startActivity((Activity)context, intent);
			}
		});	
	}
	 class ViewHolder{
		public RelativeLayout rlUser;
		public RelativeLayout rlStartimes;
		public ImageView ivUserHeand;
		public TextView tvUserName;
		public TextView tvUserMessageCreateDate;
		public TextView tvUserMessage;
		public ImageView ivStarHaend;
		public TextView tvStarName;
		public TextView tvStarMessageCreateDate;
		public TextView tvStartMessage;
	}
}
