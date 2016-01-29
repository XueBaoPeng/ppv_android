package com.star.mobile.video.channel;

import java.io.Serializable;
import java.util.List;

import org.libsdl.app.Player;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.star.cms.model.Content;
import com.star.cms.model.Resource;
import com.star.cms.model.vo.ChannelVO;
import com.star.cms.model.vo.VOD;
import com.star.mobile.video.R;
import com.star.mobile.video.util.DateFormat;

public class VideoAdapter extends BaseAdapter {
	private List<VOD> mRecommentsVideo;// 推荐视频
	private LayoutInflater mInflater;
	private ViewHolder holder;
	private Context mContext;
	private ChannelVO mChannel;
	public VideoAdapter(Context context, List<VOD> mRecommentsVideo) {
		mInflater = LayoutInflater.from(context);
		mContext = context;
		this.mRecommentsVideo = mRecommentsVideo;
	}
	public void setRecommentVideo(List<VOD> mRecommentsVideo) {
		this.mRecommentsVideo = mRecommentsVideo;
		notifyDataSetChanged();
	}
	public void clearRecommentVideo(List<VOD> mRecommentsVideo) {
		this.mRecommentsVideo = mRecommentsVideo;
		notifyDataSetInvalidated();
	}

	@Override
	public int getCount() {
		if (mRecommentsVideo == null) {
			return 0;
		}
		return (int) Math.ceil((double)(mRecommentsVideo.size()-1)/2);
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
	public View getView(final int location, View convertView, final ViewGroup parent) {
		final int position = location + 1;
		View view = null;
		if (convertView == null) {
			holder = new ViewHolder();
			view = mInflater.inflate(R.layout.view_recommend_video_item, parent, false);
			holder.mRecommendVideoIV_left = (com.star.ui.ImageView) view.findViewById(R.id.iv_recommend_video_icon_left);
			holder.mVideoName_left = (TextView) view.findViewById(R.id.tv_recommend_video_name_left);
			holder.mVideoDuration_left = (TextView) view.findViewById(R.id.tv_video_duration_left);
			holder.mVideoViews_left = (TextView) view.findViewById(R.id.tv_video_views_left);
			holder.rl_recommend_left = (RelativeLayout) view.findViewById(R.id.rl_recommend_left);
			holder.rl_recommend_left_view= (View) view.findViewById(R.id.rl_recommend_left_view);
			holder.rl_recommend_right_view= (View) view.findViewById(R.id.rl_recommend_right_view);
			holder.rl_recommend_right = (RelativeLayout) view.findViewById(R.id.rl_recommend_right);
			holder.mRecommendVideoIV_right = (com.star.ui.ImageView) view.findViewById(R.id.iv_recommend_video_icon_right);
			holder.mVideoName_right = (TextView) view.findViewById(R.id.tv_recommend_video_name_right);
			holder.mVideoDuration_right = (TextView) view.findViewById(R.id.tv_video_duration_right);
			holder.mVideoViews_right = (TextView) view.findViewById(R.id.tv_video_views_right);
			view.setTag(holder);
		}else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}
		
		if (mRecommentsVideo != null) {
			if(position*2 <= (mRecommentsVideo.size()-1)){
				load_left(position*2-1);
				load_right(position*2);
			}else if(position*2-1 <= (mRecommentsVideo.size()-1)){
				load_left(position*2-1);
				holder.rl_recommend_right.setVisibility(View.GONE);
				holder.rl_recommend_right_view.setVisibility(View.VISIBLE);
			}
			holder.rl_recommend_left.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(mRecommentsVideo.size() > position*2-1) {
						VOD vod = mRecommentsVideo.get(position*2-1);
						Content video=vod.getVideo();
						List<Resource> resvideo=video.getResources();
						Intent intent = new Intent(mContext,Player.class);
						intent.putExtra("videocontent", (Serializable)mRecommentsVideo);
						intent.putExtra("position", position*2-1);
						intent.putExtra("channel", getmChannel());
						intent.putExtra("filename",resvideo.get(0).getUrl() );
						intent.putExtra("epgname", vod.getName());
						mContext.startActivity(intent);
					}
				}
			});
			holder.rl_recommend_right.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(mRecommentsVideo.size() > position*2) {
						VOD vod = mRecommentsVideo.get(position*2);
						Content video=vod.getVideo();
						List<Resource> resvideo=video.getResources();
						Intent intent = new Intent(mContext,Player.class);
						intent.putExtra("videocontent", (Serializable)mRecommentsVideo);
						intent.putExtra("position", position*2);
						intent.putExtra("channel", getmChannel());
						intent.putExtra("filename",resvideo.get(0).getUrl() );
						intent.putExtra("epgname", vod.getName());
						mContext.startActivity(intent);
					}
				}
			});
		}
		return view;
	}

	class ViewHolder{
		com.star.ui.ImageView mRecommendVideoIV_left;
		ImageView mVideoCenterPlay_left;
		TextView mVideoName_left;
		TextView mVideoDuration_left;
		TextView mVideoViews_left;
		RelativeLayout rl_recommend_left;
		RelativeLayout rl_recommend_right;
		com.star.ui.ImageView mRecommendVideoIV_right;
		ImageView mVideoCenterPlay_right;
		TextView mVideoName_right;
		TextView mVideoDuration_right;
		TextView mVideoViews_right;
		View rl_recommend_left_view;
		View rl_recommend_right_view;
	}
	private void load_left(int position){
		//获得图片url
		Content poster_left=mRecommentsVideo.get(position).getPoster();
		if(poster_left.getResources() != null) {
			List<Resource> resposter_left=poster_left.getResources();
			String url_left = resposter_left.get(0).getUrl();
			holder.mRecommendVideoIV_left.setUrl(url_left); 
		}
		//获得电影名字
		VOD vod_left = mRecommentsVideo.get(position);
		String videoName_left = vod_left.getName();
		holder.mVideoName_left.setText(videoName_left);
		//获得观看次数
		Content videoContent_left = mRecommentsVideo.get(position).getVideo();
		if (videoContent_left != null && videoContent_left.getSelCount() != null) {
			long watchCount_left = videoContent_left.getSelCount();
			holder.mVideoViews_left.setText(String.valueOf(watchCount_left));
		}
		
		if(videoContent_left.getResources()!=null&&videoContent_left.getResources().size()>0){
			//获得时长
			Long times_left = videoContent_left.getResources().get(0).getSize();
			if(times_left != null){
				holder.mVideoDuration_left.setText(DateFormat.formatTime(times_left));
			}
		}
		holder.rl_recommend_left.setVisibility(View.VISIBLE);
		holder.rl_recommend_left_view.setVisibility(View.GONE);
	}
	private void load_right(int position){
		//获得图片url
		Content poster_right=mRecommentsVideo.get(position).getPoster();
		if(poster_right.getResources() !=null){
			List<Resource> resposter_right=poster_right.getResources();
			String url_right = resposter_right.get(0).getUrl();
			holder.mRecommendVideoIV_right.setUrl(url_right); 
		}
		//获得电影名字
		VOD vod_right = mRecommentsVideo.get(position);
		String videoName_right = vod_right.getName();
		holder.mVideoName_right.setText(videoName_right);
		//获得观看次数
		Content videoContent_right = mRecommentsVideo.get(position).getVideo();
		if (videoContent_right != null && videoContent_right.getSelCount() != null) {
			long watchCount_right = videoContent_right.getSelCount();
			holder.mVideoViews_right.setText(String.valueOf(watchCount_right));
		}
		
		if(videoContent_right.getResources()!=null&&videoContent_right.getResources().size()>0){
			//获得时长
			Long times_right = videoContent_right.getResources().get(0).getSize();
			if(times_right != null){
				holder.mVideoDuration_right.setText(DateFormat.formatTime(times_right));
			}
		}
		holder.rl_recommend_right.setVisibility(View.VISIBLE);
		holder.rl_recommend_right_view.setVisibility(View.GONE);
	}
	public ChannelVO getmChannel() {
		return mChannel;
	}
	public void setmChannel(ChannelVO mChannel) {
		this.mChannel = mChannel;
	}
	
}
