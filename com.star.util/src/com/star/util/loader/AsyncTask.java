package com.star.util.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;

import android.content.Context;
import android.text.TextUtils;

import com.star.util.InternalStorage;
import com.star.util.Log;
import com.star.util.Logger;
import com.star.util.app.GA;
import com.star.util.http.HTTPReqt;
import com.star.util.http.IOUtil;
import com.star.util.json.JSONUtil;
import com.star.util.thread.ThreadLocalMap;
import com.star.util.thread.ThreadLocalString;

/**
 * 异步加载器
 * @author dujr
 *
 * @param <T>
 */
public class AsyncTask<T> extends android.os.AsyncTask<RequestVO<T>, Integer, List<T>> {
	public Request req = Request.GET; // 默认get请求
	public OnResultListener<T> listener; // 服务器数据回调监听
	public String msg; // 友好的消息提示
	public Context context; // 上下文环境，不能为空，否者无法使用
	public int statusCode;
	public static final int NETWORK_ERROR = 0;
	public static final int JSON_PASER_ERROR = 1;
	private long now;
	
	public enum Request {
		GET, POST, DELETE, POST_IMAGE
	}
	
	public AsyncTask(Context context) {
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected List<T> doInBackground(RequestVO<T>... params) {
		AsyncTaskHolder.getInstance(context).removeAsync(this);
		if (params[0].listener != null && listener == null) {
			this.listener = params[0].listener; // 设置回调监听器
		}
		HttpResponse response = null;
		List<T> ts = null;
		if(req != Request.POST_IMAGE)
			ts = http_(response, params);
		else
			ts = postImage(response, params);
		try{
			now = System.currentTimeMillis();
			Object http = ThreadLocalMap.get(ThreadLocalString.HTTP_REQ_TAG);
			if(http!=null){
				HTTPReqt hrt = (HTTPReqt)http;
				GA.sendEvent(hrt.getUrl(), hrt.getPath(), hrt.getParams(), now-hrt.getStartDate());
				Logger.d("REQ_HTTP:"+ hrt.toString()+(now-hrt.getStartDate()));
	//			Log.d("REQ_HTTP1", hrt.toString()+(now - b));
			}
		}finally{
			ThreadLocalMap.remove(ThreadLocalString.HTTP_REQ_TAG);
		}
		return ts;
	}

	private List<T> postImage(HttpResponse response, RequestVO<T>... params) {
		if(params[0].params!=null){
			List<BitmapUploadParams> bitmaps = (List<BitmapUploadParams>) params[0].params.get("bitmaps");
			List<T> datas = new ArrayList<T>();
			try {
				statusCode = NETWORK_ERROR;
				for(BitmapUploadParams image : bitmaps){
					Map<String, Object> ps = new HashMap<String, Object>();
					ps.put("image", image.bitmap);
					response = IOUtil.httpImagePost(ps, image.url, image.format);
					String result = IOUtil.httpResonseToJSON(response);
					T t = JSONUtil.getFromJSON(result, params[0].clazz);
					if(t!=null){
						statusCode = response.getStatusLine().getStatusCode();
						datas.add(t);
					}
				}
				return datas;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private List<T> http_(HttpResponse response, RequestVO<T>... params) {
		String result = null;
		try{
			if (req == Request.GET) {
				response = IOUtil.httpGet(params[0].url);
			} else if (req == Request.POST) {
				response = IOUtil.httpPost(params[0].url, params[0].params);
			} else if (req == Request.DELETE) {
				response = IOUtil.httpDelete(params[0].url);
			}
			if(response != null){
				statusCode = response.getStatusLine().getStatusCode();
				if(statusCode >= 200 && statusCode < 300){
					result = IOUtil.httpResonseToJSON(response);
					if(params[0].mode!=LoadMode.NET && !TextUtils.isEmpty(result)){
						InternalStorage.getStorage(context).save(params[0].url, result);
					}
				}
			}else{
				statusCode = NETWORK_ERROR;
			}
		}catch(Exception e){
			Logger.e("load error.", e);
			statusCode = NETWORK_ERROR;
		}
		List<T> ts = null;
		if(result!=null){
			try {
				if (listener!=null&& listener instanceof OnListResultListener) {
					//下面这种方式报错,编译的时候会把泛型擦到
					//ts = JSONUtil.getFromJSON(result,  new TypeToken<List<T>>() {}.getType());
					ts = JSONUtil.parserJsonToList(params[0].clazz, result);
				}else{
					ts = new ArrayList<T>();
					T t = JSONUtil.getFromJSON(result, params[0].clazz);
					ts.add(t);
				}
			} catch (Exception e) {
				statusCode = JSON_PASER_ERROR;
				Logger.e("paser error.", e);
			}
		}
		return ts;
	}

	@Override
	protected void onPostExecute(List<T> result) {
		Log.d("REQ_POSTUI", ""+(System.currentTimeMillis()-now));
		if(statusCode >= 200 && statusCode < 300 && listener!=null){
			if (listener instanceof OnListResultListener) {
				((OnListResultListener<T>)listener).onSuccess(result);
			}else{
				if(result==null||result.size()==0)
					listener.onSuccess(null);
				else
					listener.onSuccess(result.get(0));
			}
		} else if (listener!=null){
			listener.onFailure(statusCode, "");
		}
	}
}
