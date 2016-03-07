package com.star.util.app;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.star.util.DifferentUrlContral;
import com.star.util.ServerUrlDao;

public class GA {
	private static Tracker mTracker;
	
	protected static Tracker init(Application application){
//		UncaughtExceptionHandler.init(this);
		ServerUrlDao serverUrlDao = DifferentUrlContral.diffUrlContral(application);
		GoogleAnalytics analytics = GoogleAnalytics.getInstance(application);
//		mTracker = analytics.newTracker(application.getResources().getString(R.string.ga_trackingId));
		mTracker = analytics.newTracker(serverUrlDao.getGATrackingId());
		mTracker.enableExceptionReporting(true);
//		mTracker.enableAutoActivityTracking(true);
		return mTracker;
	}
	public static void sendCustomDimension(int index,String value){
		mTracker.send(new HitBuilders.ScreenViewBuilder().setCustomDimension(index, value).build());
	}
	public static void sendEvent(String category,String actionName, String tag,long value){
		mTracker.send(new HitBuilders.EventBuilder().setCategory(category)
				.setAction(actionName).setLabel(tag).setValue(value).build());
	}
	public static void sendScreen(String screenName){
		mTracker.setScreenName(screenName);
        mTracker.send(new HitBuilders.AppViewBuilder().build());
	}

}
