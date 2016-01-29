package com.star.mobile.video.adapter;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.star.cms.model.vo.VOD;
import com.star.mobile.video.R;
import com.star.mobile.video.util.DateFormat;

public class VideoAdapter extends BaseAdapter {
	private List<VOD> videos;
	private Context context;

	public VideoAdapter(Context context, List<VOD> videos) {
		this.videos = videos;
		this.context = context;
	}

	public void updateDataAndRefreshUI(List<VOD> videos){
		this.videos = videos;
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		return videos.size();
	}

	@Override
	public Object getItem(int position) {
		return videos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.view_video_item, null);
			holder = new ViewHolder();
			holder.tv_video_name = (TextView) convertView.findViewById(R.id.tv_video_name);
//			holder.tv_video_time = (TextView) convertView.findViewById(R.id.tv_video_time);
//			holder.tv_video_views = (TextView) convertView.findViewById(R.id.tv_video_views);
			holder.iv_video_icon = (com.star.ui.ImageView) convertView.findViewById(R.id.iv_video_icon);
			holder.mCathyWatchCount = (TextView) convertView.findViewById(R.id.cathy_watch_count);
			holder.mCathyDuration = (TextView) convertView.findViewById(R.id.cathy_duration);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		VOD vod = videos.get(position);
		holder.tv_video_name.setText(vod.getName());
		holder.iv_video_icon.setImageResource(R.drawable.icon_channel_logo);
		try {
			holder.iv_video_icon.setUrl(vod.getPoster().getResources().get(0).getUrl());
		} catch (Exception e) {
		}
		if(vod.getVideo()!=null){
//			holder.tv_video_views.setText((vod.getVideo().getSelCount()==null?0:vod.getVideo().getSelCount())+" views");
			holder.mCathyWatchCount.setText((vod.getVideo().getSelCount()==null?0:vod.getVideo().getSelCount())+"");
			//时长
//			if(vod.getVideo().getResources()!=null&&vod.getVideo().getResources().size()>0){
//				Long times = vod.getVideo().getResources().get(0).getSize();
//				if(times!=null){
////					holder.tv_video_time.setText(times+"s");
////					holder.mCathyDuration.setText(DateFormat.formatTime(times));
//				}
//			}
		}
		
		if (vod != null) {
			Date createDate = vod.getCreateDate();
			if (createDate != null) {
				//创建日期
				holder.mCathyDuration.setText(DateFormat.formatDateMonthAbbr(createDate));
			}
		}
		
		return convertView;
	}
	
	class ViewHolder{
		com.star.ui.ImageView iv_video_icon;
		TextView tv_video_name;
//		TextView tv_video_time;
//		TextView tv_video_views;
		TextView mCathyWatchCount;
		TextView mCathyDuration;
	}
}
