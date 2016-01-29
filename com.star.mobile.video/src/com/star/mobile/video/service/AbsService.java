package com.star.mobile.video.service;

import com.star.mobile.video.util.IOUtil;
import com.star.mobile.video.util.http.HTTPClient;
import com.star.mobile.video.util.http.HTTPInvoker;
import com.star.mobile.video.util.http.TypeMap;

public class AbsService {
	protected HTTPClient httpClient = HTTPClient.instance;
	
	@SuppressWarnings("unchecked")
	protected <T>void getFromLocal(HTTPInvoker<T> invoker){
		String json = IOUtil.getCachedJSON(invoker.getUrl());
		if (json != null) {
			invoker.onSuccess((T)TypeMap.string2Object(json, invoker.getTypeReturn()));
		}
	}
}
