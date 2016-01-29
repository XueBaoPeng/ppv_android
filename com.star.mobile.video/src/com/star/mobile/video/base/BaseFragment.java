package com.star.mobile.video.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.star.mobile.video.service.ChannelService;
import com.star.mobile.video.service.ProgramService;
import com.star.mobile.video.util.LoadingDataTask;
import com.star.util.app.GA;


public abstract class BaseFragment extends Fragment {
	private ChannelService chnService;
	private ProgramService epgService;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LoadingDataTask.cancelExistedTimers();
//		PagerTaskUtil.addToTask(this.getClass());
	}
	
	@Override
	public void onStart() {
//		HomeActivity homeActivity = StarApplication.mHomeActivity;
		GA.sendScreen(this.getClass().getSimpleName());
        super.onStart();
	}
	
	protected ChannelService getChannelService(){
		if(chnService == null)
			chnService = new ChannelService(getActivity());
		return chnService;
	}
	
	protected ProgramService getProgramService(){
		if(epgService == null)
			epgService = new ProgramService(getActivity());
		return epgService;
	}
}
