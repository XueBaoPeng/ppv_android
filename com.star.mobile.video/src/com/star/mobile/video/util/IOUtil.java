package com.star.mobile.video.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

import com.star.mobile.video.model.NETException;
import com.star.util.InternalStorage;

/**
 * 
 * @author yaohw
 */
public class IOUtil extends com.star.util.http.IOUtil{
	private final static String TAG = IOUtil.class.getName();

	/**
	 * send get http request.
	 * 
	 * @param url
	 * @return
	 */
	public synchronized static String httpGetToJSON(String url) {
		return httpGetToJSON(url, false);
	}
	
	public synchronized static String httpGetToJSON(String url, boolean isCache) {
		if (httpclient == null) {
			httpclient = new DefaultHttpClient();
		}
		HttpGet httpGet = new HttpGet(url);
//		httpGet.setHeader("token", TOKEN);
//		httpGet.setHeader("lnCode", Locale.getDefault().getLanguage());
		try {
			HttpResponse response = excuteHttpRequest(httpGet);
			HttpEntity he = response.getEntity(); // entity is null：HTTP entity may not be null
				if(he != null) {
					String json = entityToString(response);
					if(isCache && !"[]".equals(json)){
						InternalStorage.getStorage(context).save(url, json);
					}
					return json;
				} else {
					return null;
				}
		} catch (Exception e) {
			Log.e(TAG, "Http Get error", e);
			throw new NETException("network error.", e);
		}
	}
}