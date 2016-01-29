package com.star.mobile.video.me.coupon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.star.cms.model.vo.ExchangeVO;
import com.star.mobile.video.base.AbstractService;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.IOUtil;
import com.star.util.json.JSONUtil;
import com.star.util.loader.LoadMode;
import com.star.util.loader.OnResultListener;
 

public class ExchangeService extends AbstractService {
	private final static String TAG = ExchangeService.class.getName();
	public ExchangeService(Context context) {
		super(context);
	}
	public List<ExchangeVO> getExchanges(boolean fromLocal, int offset, int count) {
		String url = Constant.getExchangesUrl()+"?index="+offset+"&count="+count;
		try{
			String json = null;
			if(fromLocal) {
				json = IOUtil.getCachedJSON(url);
			} else {
				json = IOUtil.httpGetToJSON(url, true);
			}
			if(json != null) {
				return JSONUtil.getFromJSON(json, new TypeToken<ArrayList<ExchangeVO>>(){}.getType());
			}
		}catch (Exception e) {
			Log.e(TAG, "Get exchanges",e);
		}
		return null;
	}
	public void exchange(Long awardId,OnResultListener<Integer>listener){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("awardID", awardId);
		doPost( Constant.getExchangeUrl(), Integer.class, params, listener);
	}
	public void getExchengNoReceiveNumber(OnResultListener<Integer>listener){
		doGet(Constant.getExchangeNoReceiveNumberUrl(), Integer.class, LoadMode.NET, listener);
	}

	/*public Integer getExchengNoReceiveNumber() {
		try{
			String json = IOUtil.httpGetToJSON(Constant.getExchangeNoReceiveNumberUrl(), true);
			if(json != null) {
				return JSONUtil.getFromJSON(json, Integer.class);
			} 
		}catch (Exception e) {
			Log.e(TAG, "Get exchengNoReceiveNumber",e);
		}
		return null;
	}*/
	
	/*public Integer exchange(Long awardId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("awardID", awardId);
		try{
			String json = IOUtil.httpPostToJSON(params, Constant.getExchangeUrl());
			if(json != null) {
				return JSONUtil.getFromJSON(json, Integer.class);
			}
		} catch (Exception e) {
			Log.e(TAG, "Add exchange",e);
		}
		return null;
	}*/

}
