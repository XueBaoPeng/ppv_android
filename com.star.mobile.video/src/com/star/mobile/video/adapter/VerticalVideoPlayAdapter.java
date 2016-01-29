package com.star.mobile.video.adapter;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.star.cms.model.Content;
import com.star.cms.model.Resource;
import com.star.cms.model.vo.VOD;
import com.star.mobile.video.R;
import com.star.mobile.video.util.DateFormat;
/**
 * 横批播放视频的适配器
 * @author Lee
 * @version1.0 2015.07.26
 *
 */
public class VerticalVideoPlayAdapter extends BaseAdapter {

	
	
	private LayoutInflater mLayoutInflater;
	private List<VOD> mRecommentsVideo;
	private int mSelectPosition = -1;

	public VerticalVideoPlayAdapter(Context context,List<VOD> recommentsVideo) {
		mLayoutInflater = LayoutInflater.from(context);
		this.mRecommentsVideo = recommentsVideo;
	}
	
	public void setDatas(List<VOD> recommentsVideo) {
		this.mRecommentsVideo = recommentsVideo;
		notifyDataSetChanged();
	}

	public void setSelectPosition(int position){
		this.mSelectPosition = position;
	}
	
	
	@Override
	public int getCount() {
		if (mRecommentsVideo == null & mRecommentsVideo.size() == 0) {
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
		View view;
		if (convertView == null) {
			holder = new ViewHolder();
			view = mLayoutInflater.inflate(R.layout.video_more_info_item, parent, false);
			holder.mVideoMoreRelativeLayout = (RelativeLayout) view.findViewById(R.id.video_more_relativelayout);
			holder.mVideoImageView = (com.star.ui.ImageView) view.findViewById(R.id.video_more_imageview);
			holder.mVideoMoreInfo = (TextView) view.findViewById(R.id.video_more_info_textview);
			holder.mVideoMoreWatchCount = (TextView) view.findViewById(R.id.video_more_watch_count);
			holder.mVideoMoreDuration = (TextView) view.findViewById(R.id.video_more_duration);
			view.setTag(holder);
		}else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}
		
		if (mSelectPosition == position) {
			holder.mVideoMoreRelativeLayout.setBackgroundResource(R.drawable.listbg_press);
		}else {
			holder.mVideoMoreRelativeLayout.setBackgroundResource(R.drawable.list_bg);
		}
		
		if (mRecommentsVideo != null) {
			//获得图片url
			Content poster=mRecommentsVideo.get(position).getPoster();
			List<Resource> resposter=poster.getResources();
			String url = resposter.get(0).getUrl();
			holder.mVideoImageView.setUrl(url); 
			//获得电影名字
			VOD vod = mRecommentsVideo.get(position);
			String videoName = vod.getName();
			holder.mVideoMoreInfo.setText(videoName);
			//获得观看次数
			Content videoContent = mRecommentsVideo.get(position).getVideo();
			if (videoContent != null && videoContent.getSelCount() != null) {
				long watchCount = videoContent.getSelCount();
				if (watchCount != 0) {
					holder.mVideoMoreWatchCount.setText(String.valueOf(watchCount));
				}
			}
			
			if (vod != null) {
				Date createDate = vod.getCreateDate();
				if (createDate != null) {
					//创建日期
					holder.mVideoMoreDuration.setText(DateFormat.formatDateMonthAbbr(createDate));
				}
			}
//			if(videoContent.getResources()!=null&&videoContent.getResources().size()>0){
//				//获得时长
//				Long times = videoContent.getResources().get(0).getSize();
//				if(times != null){
//					holder.mVideoMoreDuration.setText(DateFormat.formatTime(times));
//				}
//			}
			
		}
		
		return view;
	}
	
	class ViewHolder{
		private RelativeLayout mVideoMoreRelativeLayout;
		private com.star.ui.ImageView mVideoImageView;
		private TextView mVideoMoreInfo;
		private TextView mVideoMoreWatchCount;
		private TextView mVideoMoreDuration;
	}

	
}
