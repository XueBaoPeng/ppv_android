package com.star.mobile.video.fragment;

import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.star.cms.model.Channel;
import com.star.cms.model.enm.OrderType;
import com.star.cms.model.vo.ChannelVO;
import com.star.mobile.video.R;
import com.star.mobile.video.base.BaseFragment;
import com.star.mobile.video.base.BaseFragmentActivity;
import com.star.mobile.video.util.LoadingDataTask;
import com.star.mobile.video.util.MaskUtil;
import com.star.mobile.video.view.ChannelGuideChannel;
import com.star.mobile.video.view.ChannelGuideVideo;

public class ChannelGuideFragment extends BaseFragment {
	private ViewGroup mView;
	private BaseFragmentActivity channelGuideActivity;
	private ChannelVO mChannel;
	private AllChannelFragment mChildFragment;	
	private ChannelGuideChannel channelLayout;
	private ChannelGuideVideo videoLayout;
	private Long channelID;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(mView != null){
			ViewGroup parent = (ViewGroup) mView.getParent();  
			if(parent != null) {  
				parent.removeView(mView);  
			}   
			return mView;
		}
		channelGuideActivity = (BaseFragmentActivity) getActivity();
		mView = new LinearLayout(channelGuideActivity);
		mView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mChildFragment = new AllChannelFragment(this, channelGuideActivity);
		channelGuideActivity.mAllChannelFragment = mChildFragment;
		
		if(mChannel!=null&&mChannel.getType()!=null){
			if(mChannel.getType()==Channel.LIVE_TYPE){
				mView.addView(getChannelLayout());
			}else{
				mView.addView(getVideoLayout());
			}
		}else if(mChannel==null && channelID==null){
			executeFavTask();
		}
		return mView;
	}
	
	@Override
	public void onStart() {
		if(mChannel!=null){
			setCurrentChannel(mChannel.getId());
		}
		if(mChildFragment!=null){
			mChildFragment.loadingData();
		}
		super.onStart();
	}
	
	public void setCurrentChannel(final Long channelID){
		this.channelID = channelID;
		handler.sendEmptyMessage(0);
	}
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			new LoadingDataTask() {
				private ChannelVO ch;
				@Override
				public void onPreExecute() {
				}

				@Override
				public void onPostExecute() {
					setCurrentChannel(ch);
				}
				@Override
				public void doInBackground() {
					ch = getChannelService().getChannelById(channelID);
				}
			}.execute();
		};
	};
	
	
	private void executeFavTask(){
		new LoadingDataTask() {
			private List<ChannelVO> chns;
			@Override
			public void onPreExecute() {
			}
			
			@Override
			public void onPostExecute() {
				if(chns != null && chns.size()>0){
					ChannelVO vo = chns.get(0);
					setCurrentChannel(vo);
				}
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						if(channelGuideActivity!=null && !channelGuideActivity.isFromTask())
							channelGuideActivity.showAllchannelFragment();
					}
				}, 300);
			}
			
			@Override
			public void doInBackground() {
				chns = getChannelService().getChannels(OrderType.FAVORITE, 0, 1);
			}
		}.execute();
	}
	
	public void setCurrentChannel(ChannelVO channel){
		if(channel!=null && channel.equals(mChannel)){
			return;
		}
		Integer pageType = Channel.LIVE_TYPE;
		if(channel!=null)
			pageType = channel.getType()==null?0:channel.getType();
		if(mView != null){
			if(pageType == Channel.LIVE_TYPE){
				if(mChannel==null || (mChannel!=null&&!pageType.equals(mChannel.getType()))){
					mView.removeAllViews();
					mView.addView(getChannelLayout());
				}
				setChannelGuideChannel(channel);
			}else{
				if(mChannel==null || (mChannel!=null&&!pageType.equals(mChannel.getType()))){
					mView.removeAllViews();
					mView.addView(getVideoLayout());
				}
				channelGuideVideoSet(channel);
			}
		}
		mChannel = channel;
	}
	
	private void setChannelGuideChannel(ChannelVO channel){
		getChannelLayout().setCurrentChannel(channel);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if(!mChildFragment.isShown()&&channelGuideActivity!=null&&!channelGuideActivity.isFromTask())
					MaskUtil.showChatroomOnTVGuideFrame(channelGuideActivity);
			}
		}, 500);
	}

	/**
	 * channelGuideVideo的设置
	 * @param channel
	 */
	private void channelGuideVideoSet(ChannelVO channel) {
		getVideoLayout().setCurrentChannel(channel);
	}

	public ChannelGuideChannel getChannelLayout() {
		channelGuideActivity.setCurrentFragmentTag(channelGuideActivity.getResources().getString(R.string.fragment_tag_channelGuide));
		if(channelLayout==null){
			channelLayout = new ChannelGuideChannel(channelGuideActivity, this);
		}
		return channelLayout;
	}

	public ChannelGuideVideo getVideoLayout() {
		channelGuideActivity.setCurrentFragmentTag(channelGuideActivity.getResources().getString(R.string.tag_video_list));
		if(videoLayout==null){
			videoLayout = new ChannelGuideVideo(channelGuideActivity);
		}
		return videoLayout;
	}

	
}
