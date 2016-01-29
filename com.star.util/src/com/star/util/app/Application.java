package com.star.util.app;

import com.google.android.gms.analytics.Tracker;

public class Application extends android.app.Application{
	public static Tracker mTracker;
	@Override
	public void onCreate() {
		super.onCreate();
		mTracker = GA.init(this);
	}
}
