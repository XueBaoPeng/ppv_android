package com.star.mobile.video.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.libsdl.app.Player;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.star.cms.model.vo.ChannelVO;
import com.star.cms.model.vo.VOD;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.IOUtil;
import com.star.util.InternalStorage;
import com.star.util.json.JSONUtil;

public class VideoService {
	
	private static final String TAG = VideoService.class.getName();
	private Context context;
	public VideoService(Context context) {
		this.context = context;
	}
	public List<VOD> getChannelVideos(String code) {
	
		try{
			String json = IOUtil.httpGetToJSON(Constant.getChannelVideoUrl()+code);
			if(json != null) {
				return JSONUtil.getFromJSON(json, new TypeToken<ArrayList<VOD>>() {}.getType());
			}
		} catch (Exception e) {
			Log.e(TAG, "login  error",e);
		}
		
		return null;
	}
	/**
	 * 分页获得数据
	 * @param code 频道id
	 * @param offset 开始位置
	 * @param count 每次获取的数量
	 * @return
	 * @author Lee
	 */
	public List<VOD> getChannelVideos(String code,int offset,int count) {
		
		try{
			String json = IOUtil.httpGetToJSON(Constant.getChannelVideoUrl()+code+"&index="+offset+"&count="+count);
			if(json != null) {
				return JSONUtil.getFromJSON(json, new TypeToken<ArrayList<VOD>>() {}.getType());
			}
		} catch (Exception e) {
			Log.e(TAG, "login  error",e);
		}
		
		return null;
	}

	/**
	 * 通过频道ID获取推荐视频
	 * @param channelId
	 * @param offest
	 * @param count
	 * @return
	 */
	public List<VOD> getChannelByCannelID(Long channelId,int offest,int count){
		try{
			String json=IOUtil.httpGetToJSON(Constant.getRecommendVideoByChannelId()+channelId+"&index="+offest+"&count="+count);
			if(json!=null){
				return JSONUtil.getFromJSON(json,  new TypeToken<ArrayList<VOD>>() {}.getType());
			}
		}catch(Exception e){
			Log.e(TAG, "login error",e);
		}
		return null;
	}
	
	public List<VOD> getChannelVideos(Long channelId, int offset, int count) {
		
		try{
			String json = IOUtil.httpGetToJSON(Constant.getVideoUrl()+channelId+"&index="+offset+"&count="+count, true);
			if(json != null) {
				return JSONUtil.getFromJSON(json, new TypeToken<ArrayList<VOD>>() {}.getType());
			}
		} catch (Exception e) {
			Log.e(TAG, "login  error",e);
		}
		return null;
	}
	
	public List<VOD> getChannelVideosFromLocal(Context context, Long channelId, int offset, int count) {
		try {
			String json = InternalStorage.getStorage(context).get(Constant.getVideoUrl()+channelId+"&index="+offset+"&count="+count);
			if(json!=null)
				return JSONUtil.getFromJSON(json, new TypeToken<ArrayList<VOD>>() {}.getType());
		} catch (Exception e) {
			Log.e(TAG, "get vod list from local error! channenId is "+channelId, e);
		}
		return new ArrayList<VOD>();
	}
	
	public List<VOD> getRecommendVideo(int index, int count) {
		
		try{
			String json = IOUtil.httpGetToJSON(Constant.getRecommendVideoUrl()+"?index="+index+"&count="+count);
			if(json != null) {
				return JSONUtil.getFromJSON(json, new TypeToken<ArrayList<VOD>>() {}.getType());
			}
		} catch (Exception e) {
			Log.e(TAG, "login  error",e);
		}
		return null;
	}
	/**
	 * 根据不同频道获取不同的推荐视频
	 * @param index
	 * @param count
	 * @param channelId 频道id
	 * @param type 视频类型，0：推荐视频，1：挂载视频
	 * @return
	 */
	public List<VOD> getRecommendVideo(long channelId, int index, int count,int type) {
		
		try{
			String json = IOUtil.httpGetToJSON(Constant.getRecommendVideo()+"?index="+index+"&count="+count+"&channelId="+channelId+"&type="+type);
			if(json != null) {
				return JSONUtil.getFromJSON(json, new TypeToken<ArrayList<VOD>>() {}.getType());
			}
		} catch (Exception e) {
			Log.e(TAG, "login  error",e);
		}
		return null;
	}
	public List<VOD> getAllVideo(int index, int count) {
			
			try{
				String json = IOUtil.httpGetToJSON(Constant.getAllVideo()+"?index="+index+"&count="+count);
				if(json != null) {
					return JSONUtil.getFromJSON(json, new TypeToken<ArrayList<VOD>>() {}.getType());
				}
			} catch (Exception e) {
				Log.e(TAG, "login  error",e);
			}
			return null;
		}
	public  List<VOD> getRecommendVideo(long channelId, int index, int count,int type,boolean isLoacl) {
		try{
			String json = null;
			if(isLoacl) {
				json = IOUtil.getCachedJSON(Constant.getRecommendVideo()+"?index="+index+"&count="+count+"&channelId="+channelId+"&type="+type);
			} else {
				if(index == 0){
					json = IOUtil.httpGetToJSON(Constant.getRecommendVideo()+"?index="+index+"&count="+count+"&channelId="+channelId+"&type="+type, true);	
				}else{
					json = IOUtil.httpGetToJSON(Constant.getRecommendVideo()+"?index="+index+"&count="+count+"&channelId="+channelId+"&type="+type);
				}
			}
			if(json != null)
				return JSONUtil.getFromJSON(json, new TypeToken<ArrayList<VOD>>() {}.getType());
		}catch(Exception e){
			Log.e(TAG, "login  error",e);
		}
		return null;
	}
	@Deprecated
	public void play(String playUrl, String videoName, Long contentId) {
		play(playUrl, videoName, contentId, null, false);
	}
	
	@Deprecated
	public void play(String playUrl, String videoName, Long contentId, ChannelVO channel) {
		play(playUrl, videoName, contentId, channel, false);
	}
	
	@Deprecated
	public void play(String playUrl, String videoName, Long contentId, ChannelVO channel, Boolean isLive) {
		if(playUrl==null){
			return;
		}
		Intent intent = new Intent(context, Player.class);
		intent.putExtra("filename",playUrl);
		intent.putExtra("epgname",videoName);
		intent.putExtra("channel", channel);
		intent.putExtra("isLive", isLive);
		context.startActivity(intent);
		takeCount(contentId);
	}
	
	public void takeCount(final Long contentId) {
		if(contentId == null)
			return;
		new Thread(){
			public void run() {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("contentID", contentId);
				try{
					IOUtil.httpPostToJSON(params, Constant.getPlayCountUrl());
				} catch (Exception e) {
					Log.e(TAG, "login  error",e);
				}
			};
		}.start();
	}
}
