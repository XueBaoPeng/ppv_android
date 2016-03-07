package com.star.mobile.video.view;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.libsdl.app.Player;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.star.cms.model.Content;
import com.star.cms.model.Resource;
import com.star.cms.model.vo.ChannelVO;
import com.star.cms.model.vo.VOD;
import com.star.mobile.video.R;
import com.star.mobile.video.activity.BrowserActivity;
import com.star.mobile.video.adapter.VideoAdapter;
import com.star.mobile.video.base.BaseFragmentActivity;
import com.star.mobile.video.dao.ServerUrlDao;
import com.star.mobile.video.service.VideoService;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.DifferentUrlContral;
import com.star.mobile.video.view.ListView.LoadingListener;
public class ChannelGuideVideo extends LinearLayout implements OnClickListener{

	private com.star.mobile.video.view.ListView lvVideo;
	private Context context;
	private ChannelVO mChannel;
	private VideoService videoService;
	private RelativeLayout mChannelVideoHeaderRL;
	private com.star.ui.ImageView ivIcon;
	private TextView tvName;
	private ImageView mChannelGuideLiveFootballIV;
	private VideoAdapter adapter;
	private List<VOD> videos = new ArrayList<VOD>();
	private BaseFragmentActivity channelGuideActivity;
	private View loadingView;

	public ChannelGuideVideo(Context context) {
		super(context);
		this.context = context;
		LayoutInflater.from(context).inflate(R.layout.fragment_video, this);
		lvVideo = (com.star.mobile.video.view.ListView) findViewById(R.id.lv_video_list);
		videoService = new VideoService(context);
		channelGuideActivity = (BaseFragmentActivity)context;
		initView();
	}

	private void initView() {
		View headerView = LayoutInflater.from(context).inflate(R.layout.view_channel_video_header, null);
		mChannelVideoHeaderRL = (RelativeLayout) headerView.findViewById(R.id.view_channel_video_header_rl);
		ivIcon = (com.star.ui.ImageView) headerView.findViewById(R.id.iv_channel_icon);
		tvName = (TextView) headerView.findViewById(R.id.tv_channel_name);
		mChannelGuideLiveFootballIV = (ImageView) headerView.findViewById(R.id.channel_guide_live_football_imageview);
		loadingView = findViewById(R.id.loadingView);
		lvVideo.addHeaderView(headerView);
		mChannelVideoHeaderRL.setOnClickListener(this);
		adapter = new VideoAdapter(context, videos);
		lvVideo.setAdapter(adapter);
		lvVideo.setLoadingListener(new LoadingListener<VOD>() {
			
			@Override
			public List<VOD> loadingS(int offset, int requestCount) {
//				return videoService.getChannelVideos(mChannel.getId(),offset,requestCount);
				return videoService.getRecommendVideo(mChannel.getId(), offset, requestCount, mChannel.getType());
			}

			@Override
			public void loadPost(List<VOD> datas) {
				if(datas!=null &&datas.size()>0){
					if(loadingView.getVisibility()==View.VISIBLE)
						loadingView.setVisibility(View.GONE);
					//id为德甲或者意甲的时候显示图片
					if (mChannel != null) {
						if (context.getResources().getString(R.string.bundesliga_name).toLowerCase().equals(mChannel.getName().toLowerCase()) || context.getResources().getString(R.string.serie_a_name).toLowerCase().equals(mChannel.getName().toLowerCase())) {
							mChannelGuideLiveFootballIV.setVisibility(View.VISIBLE);
						} else {
							mChannelGuideLiveFootballIV.setVisibility(View.GONE);
						}
					}
					videos.addAll(datas);
					adapter.updateDataAndRefreshUI(videos);
				}
			}

			@Override
			public List<VOD> loadingL(int offset, int requestCount) {
				return videoService.getChannelVideosFromLocal(context,mChannel.getId(),offset,requestCount);
			}

			@Override
			public List<VOD> getFillList() {
				return videos;
			}

			@Override
			public void onNoMoreData() {
				// TODO Auto-generated method stub
			}
		});
		
		lvVideo.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				try{
					if(videos!=null && position!=0){
						VOD vod = videos.get(position-1);
//						videoService.play(vod.getVideo().getResources().get(0).getUrl(), vod.getName(), vod.getVideo().getId(),mChannel);
//						VOD vod = mVideoContent.get(position);
						Content video=vod.getVideo();
						List<Resource> resvideo=video.getResources();
						
						Intent intent = new Intent(channelGuideActivity,Player.class);
						intent.putExtra("videocontent", (Serializable)videos);
						intent.putExtra("position", position-1);
						intent.putExtra("channel", mChannel);
						intent.putExtra("filename",resvideo.get(0).getUrl() );
						intent.putExtra("epgname", vod.getName());
						channelGuideActivity.startActivity(intent);
				}
				}catch (Exception e) {
					Log.d("ChannelGuideVideo", "parser video res error!");
				}
			}
		});
	}

	private void resetHeaderView() {
		if(mChannel!= null){
			try{
				String logoUrl = mChannel.getLogo().getResources().get(0).getUrl();
				ivIcon.setUrl(logoUrl);
			}catch(Exception e){
			}
			tvName.setText(mChannel.getName());
		}
	}

	public void setCurrentChannel(ChannelVO channel) {
		if(channel==null)
			return;
		this.mChannel = channel;
		resetHeaderView();
		videos.clear();
		adapter.updateDataAndRefreshUI(videos);
		loadingView.setVisibility(View.VISIBLE);
		lvVideo.loadingData(true);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.view_channel_video_header_rl:
			ServerUrlDao serverUrlDao = DifferentUrlContral.diffUrlContral(context);
			String aredCode = SharedPreferencesUtil.getAreaCode(context);
			if (context.getResources().getString(R.string.bundesliga_name).toLowerCase().equals(mChannel.getName().toLowerCase())) {
//				transBrowser(context.getResources().getString(R.string.bundesliga_url)+aredCode,context.getResources().getString(R.string.bundesliga_name));
				transBrowser(serverUrlDao.getBundesligaUrl()+aredCode,context.getResources().getString(R.string.bundesliga_name));
			}else if (context.getResources().getString(R.string.serie_a_name).toLowerCase().equals(mChannel.getName().toLowerCase())) {
//				transBrowser(context.getResources().getString(R.string.serie_a_url)+aredCode,context.getResources().getString(R.string.serie_a_name));
				transBrowser(serverUrlDao.getSerieAUrl()+aredCode,context.getResources().getString(R.string.serie_a_name));
			}
			break;

		default:
			break;
		}
	}

	/**
	 * 跳转到网页
	 */
	private void transBrowser(String url,String name) {
		Intent intent = new Intent(channelGuideActivity, BrowserActivity.class);
		intent.putExtra("loadUrl", url);
		intent.putExtra("pageName", name);
		CommonUtil.startActivity(channelGuideActivity, intent);
		
	}
	
	
}
