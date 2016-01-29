package com.star.mobile.video.model;

import java.util.Timer;
import java.util.TimerTask;

public class MyTimer extends Timer {

	public TimerTask innerTask;
	public boolean cancel = false;
	
	@Override
	public void cancel() {
		if(innerTask != null){
			cancel = true;
			innerTask.cancel();
			innerTask = null;
		}
		super.cancel();
	}
}
