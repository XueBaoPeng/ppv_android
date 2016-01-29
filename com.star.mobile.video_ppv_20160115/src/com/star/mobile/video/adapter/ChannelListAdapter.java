package com.star.mobile.video.adapter;

import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.star.cms.model.vo.ChannelVO;
import com.star.mobile.video.R;
import com.star.ui.ImageView;

public class ChannelListAdapter extends BaseAdapter {

	private List<ChannelVO> channels;
	private Context context;

	public ChannelListAdapter(Context context, List<ChannelVO> channels) {
		this.channels = channels;
		this.context = context;
	}

	public void updateDataAndRefreshUI(List<ChannelVO> channels) {
		this.channels = channels;
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
		ViewHolder holder;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.view_allchn_item, null);
//			convertView.setBackgroundDrawable(null);
			holder = new ViewHolder();
			holder.tv_chn_name = (TextView) convertView.findViewById(R.id.tv_channel_name);
			holder.iv_chn_icon = (ImageView) convertView.findViewById(R.id.iv_channel_icon);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		ChannelVO channel = channels.get(position);
		holder.tv_chn_name.setText(channel.getName());
//		holder.iv_chn_icon.setBackgroundResource(R.drawable.icon_channel_logo);
		holder.iv_chn_icon.setImageResource(R.drawable.icon_channel_logo);
		try {
//			holder.iv_chn_icon.setImageURI(Uri.parse(channel.getLogo().getResources().get(0).getUrl()));
			holder.iv_chn_icon.setUrl(channel.getLogo().getResources().get(0).getUrl());
		} catch (Exception e) {
		}
		return convertView;
	}
	
	class ViewHolder{
//		SimpleDraweeView iv_chn_icon;
		com.star.ui.ImageView iv_chn_icon;
		TextView tv_chn_name;
	}
}
