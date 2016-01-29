package com.star.mobile.video.util.http;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.star.mobile.video.util.IOUtil;

/**
 * 
 * @author liuj
 */
public class HTTPClient {

	public static HTTPClient instance = new HTTPClient();
	private AsyncHttpClient client;

//	private final static String TAG = IOUtilAsync.class.getName();
	private Context context;

	private HTTPClient() {
		client = new AsyncHttpClient();
		int num = Runtime.getRuntime().availableProcessors();
		ExecutorService pool = Executors.newFixedThreadPool(num * 2 + 1);
		client.setThreadPool(pool);
		client.setMaxConnections(20);
		client.setConnectTimeout(5000);
		client.setResponseTimeout(30000);
	}

	public synchronized <T> RequestHandle httpPost(HTTPInvoker<T> invoker, List<NameValuePair> nameValuePairs) {
//		IOUtil.checkLogon(invoker.getUrl());
		invoker.setHttpType(HttpPost.METHOD_NAME);
		client.addHeader("TOKEN", IOUtil.getTOKEN());
		RequestParams parameters = new RequestParams();
		Iterator<NameValuePair> iter = nameValuePairs.iterator();
		while (iter.hasNext()) {
			NameValuePair entry = iter.next();
			parameters.put(entry.getName(), entry.getValue());
		}
		return client.post(invoker.getUrl(), parameters, new AlwaysAsyncHttpResponseHandler<T>(false, invoker));
	}

	public synchronized <T> RequestHandle httpPost(HTTPInvoker<T> invoker, String param) {
//		IOUtil.checkLogon(invoker.getUrl());
		invoker.setHttpType(HttpPost.METHOD_NAME);
		client.addHeader("TOKEN", IOUtil.getTOKEN());
		HttpEntity entity;
		try {
			entity = new StringEntity(param);
		} catch (UnsupportedEncodingException e) {
			throw new HTTPException("error params!");
		}
		String contentType = "application/x-www-form-urlencoded";
		return client.post(context, invoker.getUrl(), entity, contentType, new AlwaysAsyncHttpResponseHandler<T>(false, invoker));
	}

	public synchronized <T> RequestHandle httpPostToJSON(HTTPInvoker<T> invoker, Map<String, Object> params) {
//		IOUtil.checkLogon(invoker.getUrl());
		invoker.setHttpType(HttpPost.METHOD_NAME);
		client.addHeader("TOKEN", IOUtil.getTOKEN());
		RequestParams parameters = new RequestParams();
		Iterator<Map.Entry<String, Object>> iter = params.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, Object> entry = (Map.Entry<String, Object>) iter.next();
			parameters.put(entry.getKey(), entry.getValue());
		}

		return client.post(invoker.getUrl(), parameters, new AlwaysAsyncHttpResponseHandler<T>(false, invoker));
	}

	public synchronized <T> RequestHandle httpGetToJSON(HTTPInvoker<T> invoker) {
		return httpGetToJSON(invoker, false);
	}

	public synchronized <T> RequestHandle httpGetToJSON(HTTPInvoker<T> invoker, final boolean isCache) {
//		IOUtil.checkLogon(invoker.getUrl());
		invoker.setHttpType(HttpGet.METHOD_NAME);
		client.addHeader("TOKEN", IOUtil.getTOKEN());
		return client.get(invoker.getUrl(), new AlwaysAsyncHttpResponseHandler<T>(isCache, invoker));
	}
	public synchronized <T> RequestHandle httpDelete(HTTPInvoker<T> invoker) {
//		IOUtil.checkLogon(invoker.getUrl());
		invoker.setHttpType(HttpDelete.METHOD_NAME);
		client.addHeader("TOKEN", IOUtil.getTOKEN());
		return client.delete(invoker.getUrl(), new AlwaysAsyncHttpResponseHandler<T>(false, invoker));
	}
	public void setContext(Context context) {
		this.context = context;
	}
}
