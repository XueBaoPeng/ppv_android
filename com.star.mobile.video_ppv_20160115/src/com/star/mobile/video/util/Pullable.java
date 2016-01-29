package com.star.mobile.video.util;

/**
 * 下拉、上拉接口
 * @author Lee
 * @version 1.0 2015/08/31
 *
 */
public interface Pullable
{
	/**
	 * 判断是否可以下拉，如果不需要下拉功能可以直接return false
	 * 
	 * @return true如果可以下拉,否则返回false
	 */
	boolean canPullDown();

	/**
	 * 判断是否可以上拉，如果不需要上拉功能可以直接return false
	 * 
	 * @return true如果可以上拉,否则返回false
	 */
	boolean canPullUp();
}
