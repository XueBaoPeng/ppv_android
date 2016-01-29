package com.star.mobile.video.tenb;

import com.star.cms.model.TenbTopic;
import com.star.util.loader.AbsLoader;
import com.star.util.loader.ImageLoader;


public class TopicLoader extends AbsLoader<TenbTopic>{

	private static TopicLoader mInstance;

	public TopicLoader(int threadCount, AbsLoader.Type type) {
		super(threadCount, type);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 单例获得该实例对象
	 * 
	 * @return
	 */
	public static TopicLoader getInstance() {
		if (mInstance == null) {
			synchronized (ImageLoader.class) {
				if (mInstance == null) {
					mInstance = new TopicLoader(5, Type.LIFO);
				}
			}
		}
		return mInstance;
	}
	
}
