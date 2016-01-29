package com.star.mobile.video.dao;

import java.util.List;

import com.star.mobile.video.model.ChatVO;

public interface IChatDAO{

	boolean add(ChatVO chat,long channelID);
	List<ChatVO> query(long channelId,int index,int count);
	List<ChatVO> query(long channelId,long nowTime);
}