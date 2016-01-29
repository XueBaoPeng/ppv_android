package com.star.util.loader;

import java.util.List;


/**
 * 网络数据处理
 * 
 * @author Administrator
 * 
 */
public abstract class OnListResultListener<T> implements OnResultListener<T>{

	public final void onSuccess(T value) {};
	
	public abstract void onSuccess(List<T> value);
	
}
