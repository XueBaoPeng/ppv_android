package com.star.mobile.video.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.star.cms.model.vo.ProgramVO;
import com.star.mobile.video.R;
import com.star.mobile.video.adapter.EpgOnAirListAdapter;
import com.star.mobile.video.base.BaseFragment;
import com.star.mobile.video.chatroom.ChatService;
import com.star.mobile.video.service.EggAppearService;
import com.star.mobile.video.service.EggService;
import com.star.mobile.video.service.ProgramService;
import com.star.mobile.video.service.SyncService;
import com.star.mobile.video.view.HomePageAbove;
import com.star.util.loader.OnResultListener;

public class HomeFragment extends BaseFragment {

	private ProgramService epgService;
	private List<ProgramVO> epgs = new ArrayList<ProgramVO>();
	private EpgOnAirListAdapter mAdapter;
	private ListView lv_epg_list;
	private View mView;
	private HomePageAbove homePage;
//	private LoadingProgressBar footerView;
	private ChatService chatService;
	private EggService eggService;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		if(mView != null){
			ViewGroup parent = (ViewGroup) mView.getParent();  
			if(parent != null) {  
				parent.removeView(mView);  
			}   
			return mView;
		}
		mView = inflater.inflate(R.layout.fragment_home, null);

		lv_epg_list = (ListView) mView.findViewById(R.id.lv_epg_list);
//		homePage = new HomePageAbove(getActivity(),homeActivity);
		lv_epg_list.addHeaderView(homePage);
		mAdapter = new EpgOnAirListAdapter(getActivity(), epgs);
//		mAdapter.setItemClickable(true);
//		mAdapter.setOnItemCallBackListener(new OnItemCallBackListener() {
//			@Override
//			public void onItemCallBack() {
//				executeLoadingTask();
//			}
//		});
//		footerView = new LoadingProgressBar(homeActivity);
		lv_epg_list.setAdapter(mAdapter);
		
		epgService = new ProgramService(getActivity());
//		chatService = ChatService.getInstance(getActivity());
		eggService = new EggService(getActivity());
//		EggAppearService.appearEgg(getActivity(), EggAppearService.Home_in);
//		goAppearEgg();
		return mView;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	/**
	 * 是否有彩蛋
	 */
	/*private void isExistEgg() {
		new DefaultLoadingTask() {
			Egg egg = null;
			@Override
			public void onPostExecute() {
				if(egg != null) {
//					getSysEgg(egg);
				} else {
					goAppearEgg();
				}
			}
			
			@Override
			public void doInBackground() {
				egg = eggService.isExistEgg();
			}
		}.execute();
	}*/
	
	/*public void isUserBreakEgg() {
		new DefaultLoadingTask() {
			Boolean result;
			@Override
			public void onPostExecute() {
				if(result != null && result) {
					goAppearEgg();
				} 
			}
			
			@Override
			public void doInBackground() {
				result = eggService.isUserBreakEgg();
			}
		}.execute();
	}*/
	
	public void goAppearEgg() {
		eggService.homeIsExchanges(new OnResultListener<Boolean>() {
			
			@Override
			public void onSuccess(Boolean result) {
				if(getActivity() == null){
					return;
				}
				if(result != null && !result) {
					EggAppearService.appearEgg(getActivity(), EggAppearService.Home_first_in);
				}else{
					EggAppearService.appearEgg(getActivity(), EggAppearService.Home_in);
				}
			}
			
			@Override
			public boolean onIntercept() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void onFailure(int errorCode, String msg) {
				EggAppearService.appearEgg(getActivity(), EggAppearService.Home_in);
			}
		});
	}
	
//	public void executeLoadingTask(){
//		new LoadingDataTask() {
//			private boolean isAddFooter;
//			private List<ProgramVO> es;
//			@Override
//			public void onPreExecute() {
//				if(epgs.size()==0){
//					lv_epg_list.addFooterView(footerView);
//					isAddFooter = true;
//				}
//			}
//			
//			@Override
//			public void onPostExecute() {
//				if(isAddFooter)
//					lv_epg_list.removeFooterView(footerView);
//				if(es==null || es.size()==0)
//					return;
//				epgs.clear();
//				epgs.addAll(es);
//				for(ProgramVO p : epgs){
//					for(ProgramVO mark : AlertManager.getInstance(homeActivity).alertEpgs){
//						if(p.getId().equals(mark.getId())){
//							p.setIsFav(mark.isIsFav());
//							if(mark.isIsFav()){
//								p.setFavCount(p.getFavCount()+1);
//							}
//							break;
//						}
//					}
//				}
//				mAdapter.updateEpgDataAndRefreshUI(epgs);
//			}
//			
//			@Override
//			public void doInBackground() {
//				try {
//					Thread.sleep(2000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				es = epgService.getEpgs(OrderType.HOT, 0, 5);
//			}
//		}.execute();
//	}
	
	@Override
	public void onStart() {
		super.onStart();
		excuteTask(SyncService.getInstance(getActivity()).isDBReady());
	}

	public void excuteTask(boolean dbReady) {
		homePage.onStart(dbReady);
//		executeLoadingTask();
//		if(dbReady)
//			chatService.startTask(0);
	}

	@Override
	public void onStop() {
//		chatService.removeTask();
		super.onStop();
	}
}
