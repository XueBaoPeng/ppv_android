package com.star.mobile.video.base;

import java.util.List;
import java.util.Map;

import com.star.util.loader.BitmapUploadParams;
import com.star.util.loader.LoadMode;
import com.star.util.loader.OnResultListener;

public interface OnHttpInterface {

	/**
	 * get 请求
	 * @param clzz 请求对象class
	 * @param mode 请求方式 cache or net
	 * @param listener 请求后的回调
	 */
	<T> void doGet(String url, Class<T> clzz, LoadMode mode, OnResultListener<T> listener);
	
	/**
	 * post 请求
	 * @param url
	 * @param clzz
	 * @param params
	 * @param listener
	 */
	<T> void doPost(String url, Class<T> clzz, Map<String, Object> params, OnResultListener<T> listener);
	
	/**
	 * delete 请求
	 * @param url
	 * @param clzz
	 * @param mode
	 * @param listener
	 */
	<T> void doDelete(String url, Class<T> clzz, OnResultListener<T> listener);

	/**
	 * 上传图片
	 * @param url
	 * @param clzz
	 * @param params
	 * @param format
	 * @param listener
	 */
	<T> void doPostImage(Class<T> clzz, List<BitmapUploadParams> bitmapParams, OnResultListener<T> listener);
}
