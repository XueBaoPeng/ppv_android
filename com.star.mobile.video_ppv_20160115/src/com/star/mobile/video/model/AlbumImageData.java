package com.star.mobile.video.model;

import java.util.List;

public class AlbumImageData {
	/*
	 * 图片的文件夹路径
	 */
	private String dir;
	/*
	 * 第一张图片的路径
	 */
	private String firstImagePath;
	
	
	public String getDir(){
		return dir;
	}
	
	public void setDir(String dir){
		 this.dir=dir;
	}
	
	public String getFirstImagePath()
	{
		return firstImagePath;
	}

	public void setFirstImagePath(String firstImagePath)
	{
		this.firstImagePath = firstImagePath;
	}

}
