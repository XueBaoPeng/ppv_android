package com.star.mobile.video.fragment;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.star.cms.model.vo.ChannelVO;
import com.star.mobile.video.R;
import com.star.mobile.video.activity.BrowserActivity;
import com.star.mobile.video.adapter.DemandVideoAdapter;
import com.star.mobile.video.base.BaseFragment;
import com.star.mobile.video.service.ChannelService;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.view.ListView.LoadingListener;
public class VideoFragment extends BaseFragment {

	private View mView;
	private Activity channelGuideActivity;
	private com.star.mobile.video.view.ListView lvVideoList;
	private ChannelService chnService;
	private List<ChannelVO> chns = new ArrayList<ChannelVO>();
	private DemandVideoAdapter mAdapter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (mView != null) {
			ViewGroup parent = (ViewGroup) mView.getParent();
			if (parent != null) {
				parent.removeView(mView);
			}
			return mView;
		}
		channelGuideActivity = getActivity();
		chnService = new ChannelService(channelGuideActivity);
		mView = inflater.inflate(R.layout.fragment_video, null);
		lvVideoList = (com.star.mobile.video.view.ListView) mView.findViewById(R.id.lv_video_list);
		lvVideoList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(chns!=null&&position<chns.size()){
					ChannelVO vo = chns.get(position);
					SharedPreferencesUtil.setChannelHasNewVideo(channelGuideActivity, vo.getId(), false);
					String aredCode = SharedPreferencesUtil.getAreaCode(channelGuideActivity);
					if (channelGuideActivity.getResources().getString(R.string.bundesliga_name).toLowerCase().equals(vo.getName().toLowerCase())) {
						// 如果vo.getId()==448为德甲,跳转到浏览器
//						transBrowser(channelGuideActivity.getResources().getString(R.string.bundesliga_url) + aredCode,channelGuideActivity.getResources().getString(R.string.bundesliga_name));
					}else if (channelGuideActivity.getResources().getString(R.string.serie_a_name).toLowerCase().equals(vo.getName().toLowerCase())) {
//						transBrowser(channelGuideActivity.getResources().getString(R.string.serie_a_url) + aredCode,channelGuideActivity.getResources().getString(R.string.serie_a_name));
					}else{
						CommonUtil.startChannelActivity(channelGuideActivity, vo);
					}
				}
			}

			
		});
		lvVideoList.setLoadingListener(new LoadingListener<ChannelVO>() {
			
			@Override
			public List<ChannelVO> loadingS(int offset, int requestCount) {
				return chnService.getDemandVideos(offset,requestCount);
			}

			@Override
			public void loadPost(List<ChannelVO> datas) {
				CommonUtil.closeProgressDialog();
				if(datas!=null && datas.size()>0){
					chns.addAll(datas);
					mAdapter.updateDateChange(chns);
				}
			}

			@Override
			public List<ChannelVO> loadingL(int offset, int requestCount) {
				CommonUtil.closeProgressDialog();
				return chnService.getDemandVideosFromLocal(getActivity(),offset,requestCount);
			}

			@Override
			public List<ChannelVO> getFillList() {
				return chns;
			}

			@Override
			public void onNoMoreData() {
			}
		});
		mAdapter = new DemandVideoAdapter(channelGuideActivity, chns);
		lvVideoList.setAdapter(mAdapter);
		CommonUtil.showProgressDialog(channelGuideActivity);
		lvVideoList.loadingData(true);
		return mView;
	}
	/**
	 * 跳转到网页
	 * @param position
	 */
	private void transBrowser(String url,String name) {
		Intent intent = new Intent(channelGuideActivity, BrowserActivity.class);
		intent.putExtra("loadUrl", url);
		intent.putExtra("pageName", name);
		CommonUtil.startActivity(channelGuideActivity, intent);
		
	}
}
