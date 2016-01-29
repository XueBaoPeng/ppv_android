package com.star.mobile.video.util.http;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;

import android.util.Log;

import com.google.android.gms.analytics.HitBuilders;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.ResponseHandlerInterface;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.util.IOUtil;
import com.star.mobile.video.util.http.HTTPInvoker.MessageType;
import com.star.util.InternalStorage;

public class AlwaysAsyncHttpResponseHandler<T> extends AsyncHttpResponseHandler {
	private boolean cacheResult = false;
	private final String TAG = "AsyncHTTP";
	private long begin;

	private HTTPInvoker<T> invoker;
	
	public AlwaysAsyncHttpResponseHandler(boolean cacheResult, HTTPInvoker<T> invoker) {
		begin = System.currentTimeMillis();
		this.cacheResult = cacheResult;
		this.invoker = invoker;
		invoker.checkSelf();
	}

	@Override
	public void onSuccess(int arg0, Header[] arg1, byte[] body) {
		if (body != null) {
			String jsonReturn = new String(body);
			if (cacheResult && !"[]".equals(jsonReturn))
				try {
					InternalStorage.getStorage(IOUtil.context).save(invoker.getUrl(), jsonReturn);
				} catch (IOException e) {
				}
			Object objectReturn = TypeMap.string2Object(jsonReturn, invoker.getTypeReturn());
			invoker.setObjectReturn(objectReturn);
			invoker.sendUIMessage(MessageType.SUCCESS);
		}
	}

	@Override
	public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		Log.w(TAG, "HTTP Fail", arg3);
		if (401 == arg0) {
//			IOUtil.goLoingActivityOrWelcomeActivity();
		}
		invoker.setStatusLine(arg0);
		invoker.sendUIMessage(MessageType.FAIL);
	}

	@Override
	public void onPostProcessResponse(ResponseHandlerInterface instance, HttpResponse response) {
		super.onPostProcessResponse(instance, response);
	}

	@Override
	public void onFinish() {
		URI uri = null;
		try {
			uri = new URI(invoker.getUrl());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		if (uri != null) {
			String ip = "AsynHTTP_" + uri.getHost() + ":" + uri.getPort();
			String urla = uri.getPath() + "_" + invoker.getHttpType();
			String la_ = "PARAM?" + uri.getQuery();
			long time = System.currentTimeMillis() - begin;

			StarApplication.mTracker.send(new HitBuilders.EventBuilder().setCategory(ip).setAction(urla).setLabel(la_).setValue(time).build());
			Log.d(TAG, ip + " " + urla + " " + la_ + " " + time);
		}

	}

	@Override
	public boolean getUseSynchronousMode() {
		return false;
	}

	public boolean isCacheResult() {
		return cacheResult;
	}

}
