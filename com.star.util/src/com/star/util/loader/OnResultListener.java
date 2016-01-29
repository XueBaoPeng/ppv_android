package com.star.util.loader;

public interface OnResultListener<T> {

	/**
	 * 是否拦截此次请求
	 * @return true 拦截不会再请求数据
	 */
	boolean onIntercept();
	/**
	 * 请求到正确结果会调用
	 * @param value
	 */
	void onSuccess(T value);
	
	/**
	 * 状态码
	 * @param statusCode
	 * @param msg
	 */
	void onFailure(int errorCode, String msg);
	
}
