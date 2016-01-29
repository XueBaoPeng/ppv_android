package com.star.mobile.video.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.star.cms.model.vo.ChannelVO;
import com.star.mobile.video.R;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.ImageUtil;

public class DemandVideoAdapter extends BaseAdapter {
	private List<ChannelVO> channels;
	private Context context;

	public DemandVideoAdapter(Context context, List<ChannelVO> channels) {
		this.channels = channels;
		this.context = context;
	}
	
	public void updateDateChange(List<ChannelVO> chs){
		this.channels = chs;
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
		final ViewHolder holder;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.view_channel_video_item, null);
			holder = new ViewHolder();
			holder.tv_chn_name = (TextView) convertView.findViewById(R.id.tv_channel_name);
			holder.tv_chn_desc = (TextView) convertView.findViewById(R.id.tv_channel_desc);
			holder.iv_reminder = convertView.findViewById(R.id.iv_video_reminder);
			holder.iv_chn_icon = (com.star.ui.ImageView) convertView.findViewById(R.id.iv_channel_icon);
			holder.iv_chn_football_live = (ImageView) convertView.findViewById(R.id.live_football_imageview);
//			holder.iv_chn_icon.setFinisher(new Finisher() {
//				
//				@Override
//				public void run() {
//					holder.iv_chn_icon.setImageBitmap(ImageUtil.getCircleBitmap(holder.iv_chn_icon.getImage()));
//				}
//			});
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		ChannelVO channel = channels.get(position);
		holder.tv_chn_name.setText(channel.getName());
		holder.tv_chn_desc.setText(channel.getDescription());
		holder.iv_chn_icon.setImageResource(R.drawable.icon_channel_logo);
		//德甲或者意甲的时候显示live和足球图片
		if (context.getResources().getString(R.string.bundesliga_name).toLowerCase().equals(channel.getName().toLowerCase()) || context.getResources().getString(R.string.serie_a_name).toLowerCase().equals(channel.getName().toLowerCase())) {
			holder.iv_chn_football_live.setVisibility(View.VISIBLE);
		}else {
			holder.iv_chn_football_live.setVisibility(View.GONE);
		}
		try {
			holder.iv_chn_icon.setUrl(channel.getLogo().getResources().get(0).getUrl());
		} catch (Exception e) {
			holder.iv_chn_icon.setImageBitmap(ImageUtil.getCircleBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_channel_logo)));
		}
		holder.iv_reminder.setVisibility(View.INVISIBLE);
		if(SharedPreferencesUtil.isChannelHasNewVideo(context, channel.getId()))
			holder.iv_reminder.setVisibility(View.VISIBLE);
		return convertView;
	}
	
	class ViewHolder{
		com.star.ui.ImageView iv_chn_icon;
		TextView tv_chn_name;
		TextView tv_chn_desc;
		ImageView iv_chn_football_live;
		View iv_reminder;
	}
}
