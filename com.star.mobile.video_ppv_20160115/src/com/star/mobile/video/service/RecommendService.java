package com.star.mobile.video.service;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.star.cms.model.Recommend;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.IOUtil;
import com.star.util.Log;
import com.star.util.json.JSONUtil;

public class RecommendService {

	private final String TAG = "RecommendService";
	
	public List<Recommend> getRecommends(boolean fromLocal, Context context) {
		try{
			String json = null;
			if(fromLocal){
				json = IOUtil.getCachedJSON(Constant.getRecommendUrl(context));
			}else{
				json = IOUtil.httpGetToJSON(Constant.getRecommendUrl(context), true);
			}
			if(json != null)
				return JSONUtil.getFromJSON(json, new TypeToken<ArrayList<Recommend>>() {
			}.getType());
		}catch(Exception e){
			Log.e(TAG, "", e);
		}
		return new ArrayList<Recommend>();
	}
}
