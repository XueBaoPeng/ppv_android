package com.star.mobile.video.search;

import android.content.Context;

import com.star.cms.model.vo.SearchVO;
import com.star.mobile.video.base.AbstractService;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.TextUtil;
import com.star.util.loader.LoadMode;
import com.star.util.loader.OnResultListener;

public class SearchService extends AbstractService{

	public SearchService(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 搜索匹配的关键字（cms只会返回三条数据）
	 * @param key 关键字
	 * @param listener 结果回调
	 */
	public void getSearchResult(String key, OnResultListener<SearchVO> listener){
		doGet(Constant.getSearchTopUrl(TextUtil.getInstance().encodeText(key)), SearchVO.class, LoadMode.NET, listener);
	}
	
	/**
	 * 获取热门搜索
	 * @param listener
	 */
	public void getHotKeys(OnResultListener<String> listener){
		doGet(Constant.getSearchHostKeyUrl(), String.class, LoadMode.CACHE_NET, listener);
	}
	
	/**
	 * 搜索匹配的关键字，type相关所有结果
	 * @param keys
	 * @param index
	 * @param count
	 * @param type channel;epg;vod;chatroom
	 * @param listener
	 */
	public void getSearchDetail(String keys,int index,int count,int type,OnResultListener<SearchVO> listener){
		doGet(Constant.getSearchDetail()+"?keys="+TextUtil.getInstance().encodeText(keys)+"&index="+index+"&count="+count+"&searchType="+type, SearchVO.class, LoadMode.CACHE_NET, listener);
	}
}
