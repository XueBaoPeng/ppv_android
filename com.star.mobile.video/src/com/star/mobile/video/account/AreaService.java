package com.star.mobile.video.account;

import android.content.Context;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.star.cms.model.Area;
import com.star.mobile.video.base.AbstractService;
import com.star.mobile.video.model.NETException;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.IOUtil;
import com.star.util.InternalStorage;
import com.star.util.json.JSONUtil;
import com.star.util.loader.LoadMode;
import com.star.util.loader.OnListResultListener;
import com.star.util.loader.OnResultListener;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AreaService extends AbstractService{
	
	public AreaService(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	private static final String TAG = AreaService.class.getName();
	private String QUERYADDRESS = "http://maps.googleapis.com/maps/api/geocode/json?latlng=%s,%s&sensor=true&language=en";

	public void getAreaCode(OnResultListener<String> listener){
		doGet(Constant.getAreaCode(),String.class, LoadMode.NET,listener);
	}
	/*public String getAreaCode(){
		String code=null;
		try{
			String json=IOUtil.httpGetToJSON(Constant.getAreaCode(),true);
			if (json!=null){
				code=new JSONObject(json).getString("code");
			}
		} catch (Exception e){
			Log.e(TAG,"",e);
		}
		return code;
	}*/
	public void getAreas(int versionCode , OnListResultListener<Area> listener) {
	/*	try{
			String json = IOUtil.httpGetToJSON(Constant.getAreaUrl()+"?versionCode="+versionCode, true);
			if(json != null) {
				return JSONUtil.getFromJSON(json, new TypeToken<ArrayList<Area>>() {}.getType());
			}
		} catch (Exception e) {
			Log.e(TAG, "",e);
		}
		return new ArrayList<Area>();*/
 		doGet(Constant.getAreaUrl()+"?versionCode="+versionCode, Area.class, LoadMode.CACHE_NET, listener);
	}
	
	public List<Area> getAreasFromLocal(Context context, int versionCode) {
		try {
			String json = InternalStorage.getStorage(context).get(Constant.getAreaUrl() + "?versionCode=" + versionCode);
			if(json!=null)
				return JSONUtil.getFromJSON(json, new TypeToken<ArrayList<Area>>() {}.getType());
		} catch (Exception e) {
			Log.e(TAG, "",e);
		}
		return new ArrayList<Area>();
	}
	
	public void updateUserArea(long areaId) {
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("areaID", areaId);
		HttpResponse re = IOUtil.httpPost(Constant.getLoginArea(), params);
		if(re==null){
			throw new NETException(re+"");
		}
	}
	
	public String queryLocation(final double lat, final double lng) {
		String address = null;
		try {
			String url = String.format(QUERYADDRESS, lat, lng);
			String result = IOUtil.httpGetToJSON(url);
			JSONArray jsonArray = new JSONObject(result).getJSONArray("results");
			if (jsonArray.length() > 1) {
				JSONObject jsonObject = jsonArray.getJSONObject(0);
				address = jsonObject.optString("formatted_address");
			}
		} catch (Exception e) {
			Log.e(TAG, "query location by lat,lng error!",e);
		}
		return address;
	}
}
