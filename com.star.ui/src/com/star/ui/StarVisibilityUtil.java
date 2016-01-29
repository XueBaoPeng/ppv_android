package com.star.ui;

import android.view.View;

public class StarVisibilityUtil {
	public static final int GONE = -1;
	public static final int INVISIBLE = 0;
	public static final int VISIBLE = 1;
	
	public static int getPlatformVisibility(int starVisibility){
		switch(starVisibility){
		case GONE:
			return View.GONE;
		case INVISIBLE:
			return View.INVISIBLE;
		case VISIBLE:
			return View.VISIBLE;
		}
		throw new IllegalStateException();
	}
}
