package com.star.mobile.video.search;

import android.content.Context;

import com.star.util.SharedPreferences;

public class SearchSharedPre extends SharedPreferences {
	
	public final static String HISTORY = "history";//历史

	public SearchSharedPre(Context context) {
		super(context);
	}

	@Override
	public String getSharedName() {
		return "search_history";
	}

	@Override
	public int getSharedMode() {
		return 0;
	}

}
