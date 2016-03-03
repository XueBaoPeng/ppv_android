package org.libsdl.app;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import com.star.cms.model.vo.VOD;
import com.star.mobile.video.adapter.VerticalVideoPlayAdapter;
import com.star.mobile.video.service.ChannelService;
import com.star.mobile.video.service.VideoService;
import com.star.mobile.video.util.LoadingDataTask;
import com.star.mobile.video.view.VideoEndView;
import com.star.mobile.video.view.ListView.LoadingListener;

public class PlayerEndView {
	
	VOD mVod;
	VideoEndView mVideoEndView;
	long mCurrentChannelId;
	int mCurrentChannelType;
	private boolean isRecommentNone;
	private int RECOMMENT_MAX_COUNT = 4;//视频结束时推荐的最大数
	private VerticalVideoPlayAdapter mVideoPlayAdapter;
	List<VOD> mVideoContent = new ArrayList<VOD>();// 挂载视频
	List<VOD> mRecommentsVideo = new ArrayList<VOD>();// 推荐视频
	VideoService videoSerivce;
	private boolean isFirstLoadBundesliga = true;
	//是否获得的数据为空
	private boolean isResponseDatasNone = true;
	private Player mPlayer;
	public PlayerEndView(Player p){
		mPlayer = p;
		videoSerivce = new VideoService(mPlayer);
	}
	/**
	 * 弹出视频结束时的推荐页面
	 */
	 void showRecommentView() {
		mPlayer.videoPlayerRL.removeView(mVideoEndView);
		if(mVideoEndView == null || mPlayer.videoPlayerRL == null) mPlayer.exitActivity();
		mPlayer.videoPlayerRL.addView(mVideoEndView);
		// 弹出推荐页面
		List<VOD> recommentsVideo = new ArrayList<VOD>();
		List<VOD> recommentsVideoTotal = new ArrayList<VOD>();
		if (mRecommentsVideo.size() > RECOMMENT_MAX_COUNT) {
			for (int i = 0; i < RECOMMENT_MAX_COUNT; i++) {
				recommentsVideo.add(mRecommentsVideo.get(i));
			}
			recommentsVideoTotal.addAll(recommentsVideo);
			mVideoEndView.setRecommentVideo(recommentsVideoTotal);
		} else if (mRecommentsVideo.size() == 0) {
			isRecommentNone = true;
			mCurrentChannelId = 448;
			mCurrentChannelType = 1;
			refreshVideoData();
		} else {
			recommentsVideoTotal.addAll(mRecommentsVideo);
			mVideoEndView.setRecommentVideo(recommentsVideoTotal);
		}
		mVideoEndView.showChangeBouquetPromptRL();
	}
	
	void initData() {

		mPlayer.mVideoMoreInfoListview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(!mRecommentsVideo.get(position).getVideo().getResources().get(0).getUrl().isEmpty()){
					mPlayer.transferHorizontalListView(position, true);
				}
			}
		});

		mPlayer.mVideoMoreInfoListview.setRequestCount(6);
		mPlayer.mVideoMoreInfoListview.setLoadingListener(new MyLoadingListener());
		mPlayer.mHorizontalVideoPlayInfoView.setVod(mVod);
		mVideoPlayAdapter = new VerticalVideoPlayAdapter(mPlayer, mRecommentsVideo);
		mPlayer.mVideoMoreInfoListview.setAdapter(mVideoPlayAdapter);

		if(mPlayer.mChannel != null) {
			mVideoEndView = new VideoEndView(mPlayer,mPlayer.mChannel,mPlayer);
			mCurrentChannelId = mPlayer.mChannel.getId();
			mCurrentChannelType = mPlayer.mChannel.getType();
		}
	}
	
	void initDejiaData() {
		mPlayer.mVideoMoreInfoListview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(!mRecommentsVideo.get(position).getVideo().getResources().get(0).getUrl().isEmpty()){
					mPlayer.transferHorizontalListView(position, true);
				}
			}
		});
		mPlayer.mVideoMoreInfoListview.setRequestCount(6);
		mPlayer.mVideoMoreInfoListview.setLoadingListener(new DejiaLoadingListener());
		mVideoPlayAdapter = new VerticalVideoPlayAdapter(mPlayer, mRecommentsVideo);
		mPlayer.mVideoMoreInfoListview.setAdapter(mVideoPlayAdapter);
		final ChannelService cs = new ChannelService(mPlayer);
		new LoadingDataTask() {
			@Override
			public void onPreExecute() {
			}
			@Override
			public void onPostExecute() {
				if(mPlayer.mChannel != null) {
					mPlayer.mPlayerEndView.mVideoEndView = new VideoEndView(mPlayer,mPlayer.mChannel,mPlayer);
				}
			}
			@Override
			public void doInBackground() {
				mPlayer.mChannel = cs.getChannelById(mPlayer.mPlayerEndView.mCurrentChannelId);
			}
		}.execute();
	}

	/**
	 * 刷新视频数据
	 */
	void refreshVideoData() {
		mRecommentsVideo.clear();
		mVideoPlayAdapter.setDatas(mRecommentsVideo);
		mPlayer.mVideoMoreInfoListview.loadingData(false);
	}

	/**
	 * 加载数据接口
	 * 
	 * @author Lee
	 *
	 */
	private class MyLoadingListener implements LoadingListener<VOD> {
		@Override
		public List<VOD> loadingS(int offset, int requestCount) {
			return videoSerivce.getRecommendVideo(mCurrentChannelId, offset, requestCount, mCurrentChannelType);
		}

		@Override
		public void loadPost(List<VOD> responseDatas) {
			if (responseDatas != null && responseDatas.size() > 0) {
				Iterator<VOD>  iterator = responseDatas.iterator();
				while (iterator.hasNext()) {
					long id = iterator.next().getId();
					if (mVod.getId()==id) {
						iterator.remove();
						break;
					}
				}
				mRecommentsVideo.addAll(responseDatas);
				mVideoPlayAdapter.setDatas(mRecommentsVideo);
				if (isRecommentNone) {
					isRecommentNone = false;
					if(mPlayer.isDejia != null){
						if(mPlayer.isDejia.equals("1")) mCurrentChannelId = 448;
						else mCurrentChannelId = 449;
					}
					
					mCurrentChannelType = 1;
					mVideoEndView.setRecommentVideo(mRecommentsVideo);
				}
				if (responseDatas.size() > 0) {
					isResponseDatasNone = false;
				}
			}
			showNoneMoreVideo(responseDatas);
		}

		
		// 加载本地数据
		@Override
		public List<VOD> loadingL(int offset, int requestCount) {
			return null;
		}
		@Override
		public List<VOD> getFillList() {
			return mRecommentsVideo;
		}

		@Override
		public void onNoMoreData() {
			// TODO Auto-generated method stub
		}
	}
	private boolean isSchedule;
	public void setIsSchedule(boolean isSchedule){
		this.isSchedule = isSchedule;
	}
	/**
	 * 显示没有morevideo
	 * @param responseDatas
	 */
	private void showNoneMoreVideo(List<VOD> responseDatas) {
		if (responseDatas.size() == 0 && isResponseDatasNone) {
			mPlayer.mNoneMoreVideoTextView.setVisibility(View.VISIBLE);
		}else {
			mPlayer.mNoneMoreVideoTextView.setVisibility(View.GONE);
		}
	}
	private class DejiaLoadingListener implements LoadingListener<VOD> {

		@Override
		public List<VOD> loadingS(int offset, int requestCount) {
			return videoSerivce.getRecommendVideo(mPlayer.mPlayerEndView.mCurrentChannelId, offset, requestCount, 1);
		}

		@Override
		public void loadPost(List<VOD> responseDatas) {
			if (responseDatas != null && responseDatas.size() > 0) {
				if (isFirstLoadBundesliga) {
					isFirstLoadBundesliga = false;
					mPlayer.mPlayerEndView.mVod = responseDatas.get(mPlayer.mPosition);
					if (!isSchedule) {
						mPlayer.mHorizontalVideoPlayInfoView.setVod(mPlayer.mPlayerEndView.mVod);
					}
				}
				
				Iterator<VOD>  iterator = responseDatas.iterator();
				while (iterator.hasNext()) {
					long id = iterator.next().getId();
					if (mPlayer.mPlayerEndView.mVod.getId()==id) {
						iterator.remove();
						break;
					}
				}
				mRecommentsVideo.addAll(responseDatas);
				mVideoPlayAdapter.setDatas(mRecommentsVideo);	
				if (responseDatas.size() > 0) {
					isResponseDatasNone = false;
				}
			}
			showNoneMoreVideo(responseDatas);
		}

		@Override
		public List<VOD> loadingL(int offset, int requestCount) {
			return null;
		}

		@Override
		public List<VOD> getFillList() {
			return mRecommentsVideo;
		}

		@Override
		public void onNoMoreData() {
			// TODO Auto-generated method stub
		}

	}
}
