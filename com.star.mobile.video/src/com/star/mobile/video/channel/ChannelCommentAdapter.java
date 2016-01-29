package com.star.mobile.video.channel;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.star.cms.model.vo.CommentVO;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.tenb.TenbActivity;
import com.star.mobile.video.util.BitmapUtil;
import com.star.mobile.video.util.CalculationTimeUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.ui.ImageView;
import com.star.ui.ImageView.Finisher;
import com.star.util.Log;

public class ChannelCommentAdapter extends BaseAdapter {
	private List<CommentVO> mChannelComment;
	private LayoutInflater mInflater;
	private ViewHolder holder;
	private Context mContext;
	public ChannelCommentAdapter(Context context, List<CommentVO> mChannelComment) {
		mInflater = LayoutInflater.from(context);
		mContext = context;
		this.mChannelComment = mChannelComment;
	}
	public void setChannelComment(List<CommentVO> mChannelComment) {
		this.mChannelComment = mChannelComment;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (mChannelComment == null) {
			return 0;
		}
		return (mChannelComment.size());
	}

	@Override
	public Object getItem(int position) {
		return mChannelComment.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int location, View convertView, final ViewGroup parent) {
		final int position = location;
		View view = null;
		if (convertView == null) {
			holder = new ViewHolder();
			view = mInflater.inflate(R.layout.channel_comment_item, parent, false);
			holder.rl_comment_item = (RelativeLayout)view.findViewById(R.id.rl_comment_item);
			holder.userLogo = (com.star.ui.ImageView) view.findViewById(R.id.user_logo);
			holder.commentContent = (TextView) view.findViewById(R.id.tv_comment_content);
			holder.userName = (TextView) view.findViewById(R.id.tv_comment_username);
			holder.ratingBar = (RatingBar)view.findViewById(R.id.rt_user_score);
			holder.dataTime = (TextView) view.findViewById(R.id.tv_data_time);
			holder.userLogo.setFinisher(new Finisher() {
				@Override
				public void run() {
					if (holder.userLogo.getImage() != null) {
						holder.userLogo.setImageBitmap(BitmapUtil.toRoundBitmap(holder.userLogo.getImage()));
					}else {
						Log.i("initData", "holder.userLogo.getImage() is null");
					}
				}
			});
			view.setTag(holder);
		}else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}
		CommentVO comment = mChannelComment.get(position);
			if(comment.getNickName() == null || "".equals(comment.getNickName())){
				holder.userName.setText("Guest");
			} else {
				if(SharedPreferencesUtil.getUserName(mContext) != null) {
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
			if(comment.getIcoURL() != null) {
				holder.userLogo.setUrl(comment.getIcoURL());
			} else {
				holder.userLogo.setBackgroundResource(R.drawable.no_portrait);
			}
			holder.commentContent.setText(comment.getMsg());
			holder.ratingBar.setRating(comment.getScore());
			holder.dataTime.setText(CalculationTimeUtil.getTimeDisplay(comment.getCreateDate(), mContext));
			setHeadIconOnClick(holder,comment.getIcoURL(),comment.getNickName(),comment.getUserId());
		return view;
	}
	
	private void setHeadIconOnClick(ViewHolder holder,final String headIcon,final String nickName,final Long userId) {
		holder.rl_comment_item.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext,TenbActivity.class);
				intent.putExtra("nickname", nickName);
				intent.putExtra("headurl", headIcon);
				intent.putExtra("userId", userId);
				CommonUtil.startActivity((Activity)mContext, intent);
			}
		});
	}

	class ViewHolder{
		public RelativeLayout rl_comment_item;
		public ImageView userLogo;
		public TextView userName;
		public RatingBar ratingBar;
		public TextView commentContent;
		public TextView dataTime;
	}
	
}
