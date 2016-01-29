package com.star.mobile.video.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.star.cms.model.vo.ChannelVO;
import com.star.mobile.video.R;

public class ChannelLogoAdapter extends BaseAdapter {

	private List<ChannelVO> channels;
	private Context context;
	private int currentPos = -1;
	private int logoWidth;
	private boolean isShowBlankView;
	private boolean isShowChnNumber = true;
	public ChannelLogoAdapter(Context context, List<ChannelVO> channels, int logoWidth){
		this.context = context;
		this.logoWidth = logoWidth;
		updateDataAndRefreshUI(channels);
	}
	
	public void setShowBlankView(boolean isShown){
		this.isShowBlankView = isShown;
	}
	
	public void setShowChnNumber(boolean isShown){
		this.isShowChnNumber = isShown;
	}
	
	public void updateDataAndRefreshUI(List<ChannelVO> channels){
		this.channels = channels;
		notifyDataSetChanged();
	}
	
	public void setSelection(int position){
		this.currentPos = position;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return channels.size();
	}

	@Override
	public Object getItem(int position) {
		return channels.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(channels.size()==0)
			return null;
		ViewHolder holder;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.view_channel_logo_item, null);
			holder = new ViewHolder();
			holder.iv_chn_icon = (com.star.ui.ImageView) convertView.findViewById(R.id.iv_channel_icon);
			holder.iv_chn_icon_cov = (ImageView) convertView.findViewById(R.id.iv_channel_icon_cover);
			holder.tv_fav_count = (TextView) convertView.findViewById(R.id.tv_chn_fav_count);
			holder.iv_fav_status = (ImageView) convertView.findViewById(R.id.iv_fav_status);
			holder.iv_hot_icon = (ImageView) convertView.findViewById(R.id.iv_channel_hot);
			holder.v_blank = (View) convertView.findViewById(R.id.v_blank);
			holder.rl_fav_layout = (LinearLayout) convertView.findViewById(R.id.rl_fav_layout);
			
			holder.iv_chn_icon.setLayoutParams(new RelativeLayout.LayoutParams(logoWidth, logoWidth));
			holder.iv_chn_icon_cov.setLayoutParams(new RelativeLayout.LayoutParams(logoWidth, logoWidth));
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		final ChannelVO channel = channels.get(position);
		if(position == currentPos){
			holder.iv_chn_icon_cov.setBackgroundResource(R.drawable.channel_focus_bg);
		}else{
			holder.iv_chn_icon_cov.setBackgroundResource(R.drawable.channel_detail_bg);
		}
		if(isShowChnNumber){
			if(channel.isFav())
				holder.iv_fav_status.setImageDrawable(context.getResources().getDrawable(R.drawable.all_channel_favorite));
			else
				holder.iv_fav_status.setImageDrawable(context.getResources().getDrawable(R.drawable.all_channel_nofavorite));
			Long count = channel.getFavCount();
			if(count == null){
				holder.tv_fav_count.setText("0");
			}else{
				holder.tv_fav_count.setText(String.valueOf(count));
			}
			holder.rl_fav_layout.setVisibility(View.VISIBLE);
		}else{
			holder.rl_fav_layout.setVisibility(View.GONE);
		}
		if(isShowBlankView)
			holder.v_blank.setVisibility(View.VISIBLE);
		/*if(channel.isHotChat()){
			holder.iv_hot_icon.setVisibility(View.VISIBLE);
		}else{
			holder.iv_hot_icon.setVisibility(View.GONE);
		}*/
		return convertView;
	}
	
	class ViewHolder{
		com.star.ui.ImageView iv_chn_icon;
		ImageView iv_chn_icon_cov;
		ImageView iv_fav_status;
		ImageView iv_hot_icon;
		TextView tv_fav_count;
		View v_blank;
		LinearLayout rl_fav_layout;
	}
}
