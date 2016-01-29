package com.star.mobile.video.tenb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.star.cms.model.TenbMe;
import com.star.cms.model.TenbTopic;
import com.star.cms.model.vo.CommentVO;
import com.star.mobile.video.base.AbstractService;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.IOUtil;
import com.star.util.NetworkUtil;
import com.star.util.json.JSONUtil;
import com.star.util.loader.AbsLoader;
import com.star.util.loader.AbsLoader.Type;
import com.star.util.loader.ImageLoader;
import com.star.util.loader.LoadMode;
import com.star.util.loader.OnResultListener;
import com.star.util.loader.OnResultTagListener;

public class TenbService extends AbstractService{
	
 

	public TenbService(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public List<TenbMe> getTenbData(Long userId,int index,int count, boolean fromCache) {
		try{
			String url = Constant.getTenbUrl(index,count);
			if(userId!=null&&userId!=-1){
				url+="&userId="+userId;
			}
			String json = null;
			if(fromCache){
				json = IOUtil.getCachedJSON(url);
			}else{
				json = IOUtil.httpGetToJSON(url,true);
			}
			if (json != null) {
				return JSONUtil.getFromJSON(json, new TypeToken<ArrayList<TenbMe>>() {}.getType());
			} 
		} catch (Exception e) {
			Log.e("", "get TenbMe list from server error!", e);
		}
		return null;
	}
	
	public void getTenbTopic(final long topicId,final int type,OnResultListener<TenbTopic> listener){
		doGet( Constant.getTenbTopicUrl(topicId), TenbTopic.class, LoadMode.NET, listener);
	}
	public void getComment(final long foreignKey,final int type, OnResultTagListener<CommentVO> listener){
		doGet( Constant.getChannelSource(foreignKey), CommentVO.class, LoadMode.CACHE_NET, listener);
	}
	
	//修改后的doTenbData
	public void doTenbData(int tenbType,long foreignKey){
		Map<String, Object> params = new HashMap<String,Object>();
		params.put("tenbType", tenbType);
		params.put("foreignKey", foreignKey);
		if(!NetworkUtil.isNetworkAvailable(context))
			SharedPreferencesUtil.getTenbSharePre(context).edit().putInt(foreignKey+"", tenbType).commit();
		else	
			doPost(Constant.postTenbUrl(),null, params,null);
	}
	
	 
	private static TopicLoader mInstance;
	class TopicLoader extends AbsLoader<TenbTopic>{
		public TopicLoader(int threadCount, AbsLoader.Type type) {
			super(threadCount, type);
		}
	}
	
	public  TopicLoader getTopicLoader() {
		if (mInstance == null) {
			synchronized (ImageLoader.class) {
				if (mInstance == null) {
					mInstance = new TopicLoader(5, Type.LIFO);
				}
			}
		}
		return mInstance;
	}
	
	//修改前的doTenbData
/*	public void doTenbData(Context context,int tenbType,long foreignKey) {
		Map<String, Object> params = new HashMap<String,Object>();
		params.put("tenbType", tenbType);
		params.put("foreignKey", foreignKey);
		if(!NetworkUtil.isNetworkAvailable(context))
			SharedPreferencesUtil.getTenbSharePre(context).edit().putInt(foreignKey+"", tenbType).commit();
		else	
			IOUtil.httpPost(Constant.postTenbUrl(), params);
	}*/
	
	 
	
	/*public Integer getNewTopicStatus(long userId){
		try{
			String json = IOUtil.httpGetToJSON(Constant.getNewTopicStatusUrl(userId));
			if(json!=null){
				return JSONUtil.getFromJSON(json, new TypeToken<Integer>(){}.getType());
			}
		} catch (Exception e) {
		}
		return 0;
	}*/
	
/*
	public void checkNewTopic(final Context context) {
		if(StarApplication.mUser==null)
			return;
		new LoadingDataTask() {
			Integer result;
			@Override
			public void onPreExecute() {
			}
			
			@Override
			public void onPostExecute() {
				if(result!=null&&result==1){
					SharedPreferencesUtil.setNewTopic(context, true);
				}else{
					SharedPreferencesUtil.setNewTopic(context, false);
				}
			}
			
			@Override
			public void doInBackground() {
				result = getNewTopicStatus(StarApplication.mUser.getId());
			}
		}.execute();
	}*/
}
