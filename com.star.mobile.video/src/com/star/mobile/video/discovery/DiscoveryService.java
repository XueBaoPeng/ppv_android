package com.star.mobile.video.discovery;

import android.content.Context;

import com.star.cms.model.Discovery;
import com.star.mobile.video.base.AbstractService;
import com.star.mobile.video.util.ApplicationUtil;
import com.star.mobile.video.util.Constant;
import com.star.util.loader.LoadMode;
import com.star.util.loader.OnListResultListener;

public class DiscoveryService extends AbstractService{
	

	public DiscoveryService(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public void getDiscovery(OnListResultListener<Discovery> listener) {
		doGet(Constant.getDiscoveryUrl()+"?versionCode="+ApplicationUtil.getAppVerison(context), Discovery.class, LoadMode.CACHE_NET, listener);
	}
	
}
