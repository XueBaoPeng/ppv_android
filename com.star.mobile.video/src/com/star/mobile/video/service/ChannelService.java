package com.star.mobile.video.service;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.star.cms.model.Category;
import com.star.cms.model.Channel;
import com.star.cms.model.Package;
import com.star.cms.model.enm.OrderType;
import com.star.cms.model.enm.TVPlatForm;
import com.star.cms.model.vo.ChannelVO;
import com.star.mobile.video.dao.db.DBHelper;
import com.star.mobile.video.dao.impl.ChannelDAO;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.IOUtil;
import com.star.util.InternalStorage;
import com.star.util.json.JSONUtil;

public class ChannelService {

	private final String TAG = "ChannelService";
	private Context context;
	private String requestURL;
	private ChannelDAO channelDao;

	public ChannelService(Context context) {
		this.context = context;
		requestURL = Constant.getChannelUrl();
		channelDao = new ChannelDAO(DBHelper.getInstence(context));
	}
	
	public List<ChannelVO> getChannels(Category category, boolean isFav, Package p, TVPlatForm tvPlatForm) {
		long categoryId = category==null?-1:category.getId();
		if(SyncService.getInstance(context).isDBReady()){
			List<ChannelVO> channels = channelDao.query(categoryId, isFav, p, tvPlatForm);
			return channels;
		}
		String url = requestURL;
		boolean append = false;
		if(category != null){
			append = true;
			url = "?categoryID=" + category.getId();
		}
		if (p!=null && p.getCode()!=null){
			if(append)
				url = url + "&packageCode=" + p.getCode();
			else
				url = url + "&categoryID=" + p.getCode();
			append = true;
		}
		if(append){
			url = url + "&count=" + Integer.MAX_VALUE;
		}else{
			url = url + "?count=" + Integer.MAX_VALUE;
		}
		return getChannelsFromServer(url);
	}

	public List<ChannelVO> getFavChannels(boolean isfav, int index, int count) {
		if(SyncService.getInstance(context).isDBReady()){
			List<ChannelVO> channels = channelDao.query(isfav, index, count);
			return channels;
		}
		return new ArrayList<ChannelVO>();
	}

	public List<ChannelVO> getChannels(OrderType orderType, int index, int count) {
		if(SyncService.getInstance(context).isDBReady()){
			try{
				List<ChannelVO> channels = channelDao.query(-1, orderType, index, count);
				if(channels.size()!=0){
					Log.d(TAG, "channel list come from local!");
					return channels;
				}
				Log.e(TAG, "no channels in local!");
			}catch (Exception e) {
				Log.e(TAG, "get channel list from local error!", e);
			}
		}
		String url;
		if (index != -1 && count != -1)
			url = requestURL + "?orderBy=" + orderType.getNum() + "&index="	+ index + "&count=" + count;
		else
			url = requestURL + "?orderBy=" + orderType.getNum();
		return getChannelsFromServer(url);
	}

	public List<ChannelVO> getChannels(long categoryId, OrderType orderType,
			int index, int count) {
		if(SyncService.getInstance(context).isDBReady()){
			try{
				List<ChannelVO> channels = channelDao.query(categoryId, orderType, index, count);
				if(channels.size()!=0){
					Log.d(TAG, "channel list come from local! no epg.");
					return channels;
				}
				Log.e(TAG, "no channels in local!");
			}catch (Exception e) {
				Log.e(TAG, "get channel list from local error!", e);
			}
		}
		String url = requestURL + "?categoryID=" + categoryId + "&orderBy="	+ orderType.getNum() + "&index=" + index + "&count=" + count;
		return getChannelsFromServer(url);
	}
	
	/**
	 * 当前频道下所有epg的评论数量，针对频道下epg
	 * @param channel
	 * @return
	 */
	public boolean updateCommentCount(ChannelVO channel) {
		return channelDao.updateCommentCount(channel);
	}
	
	public boolean updateChannel(ChannelVO channel) {
		return channelDao.updateChannel(channel, false);
	}
	
	public boolean updateChannel(ChannelVO channel, boolean isSync) {
		return channelDao.updateChannel(channel, isSync);
	}
	
	public List<ChannelVO> getOfflineChannels(){
		return channelDao.queryOfflineChannels();
	}
	
	private List<ChannelVO> getChannelsFromServer(String url) {
		return getChannelsFromServer(url, false);
	}
	
	private List<ChannelVO> getChannelsFromServer(String url, boolean cache) {
		Log.i(TAG, "url = " + url);
		try {
			String json = IOUtil.httpGetToJSON(url, cache);
			if (json != null)
				return JSONUtil.getFromJSON(json, new TypeToken<ArrayList<ChannelVO>>() {}.getType());
		} catch (Exception e) {
			Log.e(TAG, "get channel list from server error!", e);
		}
		return null;
	}

	public ChannelVO getChannelById(long channelId) {
		if(SyncService.getInstance(context).isDBReady()){
			try{
				ChannelVO channel = channelDao.query(channelId);
				if(channel != null){
					Log.d(TAG, "channel detail come from local!");
					return channel;
				}
				Log.e(TAG, "no channel in local, id ="+channelId);
			}catch(Exception e){
				Log.e(TAG, "get channel detail from local error!", e);
			}
		}
		String url = requestURL + "/" + channelId;
		try {
			String json = IOUtil.httpGetToJSON(url);
			if (json != null) {
				return JSONUtil.getFromJSON(json, new TypeToken<ChannelVO>() {}.getType());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean initChannels() {
		List<Integer> types = new ArrayList<Integer>();
		types.add(Channel.DEMAND_TYPE);
		types.add(Channel.LIVE_TYPE);
		types.add(Channel.APPLY_TYPE);
		String url =  Constant.getSnapshotChannelUrl(types)+"&count="+Integer.MAX_VALUE+"&platformTypes="+ TVPlatForm.DTH.getNum()+"&platformTypes="+TVPlatForm.DTT.getNum();
		List<ChannelVO> channels = getChannelsFromServer(url);
		if(channels==null || channels.size()==0)
			return false;
		ChannelDAO channelDao = new ChannelDAO(DBHelper.getInstence(context));
		channelDao.clear();
		for (ChannelVO channel : channels) {
			channelDao.add(channel);
			for (Category category : channel.getCategories()) {
				channelDao.addChannelToCategory(category.getId(), channel.getId());
			}
		}
		return true;
	}

	public List<ChannelVO> getNeedSyncChannels() {
		return channelDao.query(true);
	}
	
	public List<Long> getLocalEpgOfChannelIDs() {
		return channelDao.queryLocalEpgOfChannelIDs();
	}
	
	public boolean updateEpgStatus(ChannelVO channel, boolean hasEpg){
		return channelDao.updateEpgStatus(channel, hasEpg);
	}
	
	public List<ChannelVO> getChannels(int index, int count, OrderType type, boolean hasEpg){
		if(SyncService.getInstance(context).isDBReady()){
			try{
				List<ChannelVO> channels = channelDao.query(index, count, type, hasEpg);
				if(channels.size()!=0){
					Log.d(TAG, "channels come from local, epg.");
					return channels;
				}
			}catch (Exception e) {
			}
		}
		return null;
	}
	/**
	 * @param offset 开始位置
	 * @param count 查询数量
	 * @return
	 */
	public List<ChannelVO> getChannels(int offset, int count){
		if(SyncService.getInstance(context).isDBReady()){
			try{
				List<ChannelVO> channels = channelDao.query(offset,count);
				if(channels.size()!=0){
					Log.d(TAG, "channels come from local, epg.");
					return channels;
				}
			}catch (Exception e) {
				Log.d("ChannelService", "get from local error!", e);
			}
		}
		return null;
	}
	
	public void clearData(){
		channelDao.clear();
	}
	
	public List<ChannelVO> getDemandVideos(int index, int count){
		return getChannelsFromServer(Constant.getVideoChannelUrl()+"?index="+index+"&count="+count,true);
	}
	
	public List<ChannelVO> getDemandVideosFromLocal(Context context, int index, int count){
		try {
			String json = InternalStorage.getStorage(context).get(Constant.getVideoChannelUrl()+"?index="+index+"&count="+count);
			if(json!=null)
				return JSONUtil.getFromJSON(json, new TypeToken<ArrayList<ChannelVO>>() {}.getType());
		} catch (Exception e) {
			Log.e(TAG, "get channel list from local error!", e);
		}
		return new ArrayList<ChannelVO>();
	}
	
	public List<String> getChannelLikeName(String key, int index, int count){
		return channelDao.queryLikeName(key, index, count);
	}
}
