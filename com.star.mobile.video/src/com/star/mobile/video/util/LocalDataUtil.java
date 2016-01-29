package com.star.mobile.video.util;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.util.Log;

public class LocalDataUtil {

	private static final String TAG = "LocalDataUtil";

	public static String getDataFormAssets(Context context, String filename){
		try {
			InputStream is = context.getAssets().open(filename);
			return IOUtil.streamToString(is,false);
		} catch (IOException e) {
			Log.e(TAG, "get json from assets error", e);
			e.printStackTrace();
		}
		return null;
	}
}