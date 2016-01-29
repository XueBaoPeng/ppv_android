package com.star.mobile.video.tenb;

import com.star.cms.model.vo.CommentVO;
import com.star.util.loader.AbsLoader;
import com.star.util.loader.ImageLoader;

public class CommentLoader extends AbsLoader<CommentVO>{

	private static CommentLoader mInstance;

	public CommentLoader(int threadCount, AbsLoader.Type type) {
		super(threadCount, type);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 单例获得该实例对象
	 * 
	 * @return
	 */
	public static CommentLoader getInstance() {
		if (mInstance == null) {
			synchronized (ImageLoader.class) {
				if (mInstance == null) {
					mInstance = new CommentLoader(5, Type.LIFO);
				}
			}
		}
		return mInstance;
	}
	
}
