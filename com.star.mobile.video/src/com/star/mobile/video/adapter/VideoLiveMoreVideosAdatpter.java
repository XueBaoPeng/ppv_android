package com.star.mobile.video.adapter;

import java.util.List;

import com.star.cms.model.Content;
import com.star.cms.model.Resource;
import com.star.cms.model.vo.VOD;
import com.star.mobile.video.R;
import com.star.mobile.video.util.DateFormat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 视频直播，More Videos
 * 
 * @author Lee
 * @version 1.0 2015/08/26
 *
 */
public class VideoLiveMoreVideosAdatpter extends BaseAdapter {
	private List<VOD> mRecommentsVideo;// 推荐视频
	private LayoutInflater mInflater;

	public VideoLiveMoreVideosAdatpter(Context context, List<VOD> mRecommentsVideo) {
		mInflater = LayoutInflater.from(context);
		this.mRecommentsVideo = mRecommentsVideo;
	}

	public void setRecommentVideo(List<VOD> mRecommentsVideo) {
		this.mRecommentsVideo = mRecommentsVideo;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (mRecommentsVideo == null) {
			return 0;
		}
		return mRecommentsVideo.size();
	}

	@Override
	public Object getItem(int position) {
		return mRecommentsVideo.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		View view = null;
		if (convertView == null) {
			holder = new ViewHolder();
			view = mInflater.inflate(R.layout.video_live_more_videos_item, parent, false);
			holder.mRecommendVideoIV = (com.star.ui.ImageView) view.findViewById(R.id.recommend_video_iv);
			holder.mVideoCenterPlay = (ImageView) view.findViewById(R.id.video_center_play);
			holder.mVideoName = (TextView) view.findViewById(R.id.video_name);
			holder.mVideoDuration = (TextView) view.findViewById(R.id.video_duration);
			holder.mVideoWatchCount = (TextView) view.findViewById(R.id.video_watch_count);
			view.setTag(holder);
		}else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}
		
		if (mRecommentsVideo != null) {
			//获得图片url
			Content poster=mRecommentsVideo.get(position).getPoster();
			List<Resource> resposter=poster.getResources();
			String url = resposter.get(0).getUrl();
			holder.mRecommendVideoIV.setUrl(url); 
			//获得电影名字
			VOD vod = mRecommentsVideo.get(position);
			String videoName = vod.getName();
			holder.mVideoName.setText(videoName);
			//获得观看次数
			Content videoContent = mRecommentsVideo.get(position).getVideo();
			if (videoContent != null && videoContent.getSelCount() != null) {
				long watchCount = videoContent.getSelCount();
				holder.mVideoWatchCount.setText(String.valueOf(watchCount));
			}
			
			if(videoContent.getResources()!=null&&videoContent.getResources().size()>0){
				//获得时长
				Long times = videoContent.getResources().get(0).getSize();
				if(times != null){
					holder.mVideoDuration.setText(DateFormat.formatTime(times));
				}
			}
		}
		return view;
	}

	class ViewHolder{
		com.star.ui.ImageView mRecommendVideoIV;
		ImageView mVideoCenterPlay;
		TextView mVideoName;
		TextView mVideoDuration;
		TextView mVideoWatchCount;
	}
	
}
