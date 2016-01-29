package com.star.mobile.video.dao;

import java.util.List;

import com.star.cms.model.Package;
import com.star.cms.model.enm.OrderType;
import com.star.cms.model.vo.ChannelVO;

public interface IChannelDAO{

	void add(ChannelVO channel);
	void clear();
	List<ChannelVO> query();
	ChannelVO query(Long channelId);
	List<ChannelVO> query(boolean isfav, int index, int count);
	List<ChannelVO> query(long categoryId, boolean isfav, Package p);
	List<ChannelVO> query(long categoryId, OrderType orderType, int index, int count);
	boolean addChannelToCategory(long cat_id, long chn_id);
	List<ChannelVO> query(boolean isChange);
	boolean updateChannel(ChannelVO channel, boolean isSync);
	boolean updateEpgStatus(ChannelVO channel, boolean hasEpg);
	List<ChannelVO> query (int index, int count, OrderType orderType, boolean hasEpg);
	List<ChannelVO> query (int index, int count, List<Integer> types);
	List<ChannelVO> query(int index, int count);
	List<String> queryLikeName(String key, int index, int count);
}