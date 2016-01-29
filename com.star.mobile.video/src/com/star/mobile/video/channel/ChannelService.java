package com.star.mobile.video.channel;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.star.cms.model.vo.ChannelVO;
import com.star.cms.model.vo.CommentVO;
import com.star.mobile.video.base.AbstractService;
import com.star.mobile.video.util.Constant;
import com.star.util.loader.LoadMode;
import com.star.util.loader.OnListResultListener;
import com.star.util.loader.OnResultListener;

public class ChannelService extends AbstractService {

	public ChannelService(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public void getChannelComment(long channelId,int offset,int count, OnListResultListener<CommentVO> listener){
		doGet(Constant.getChannelCommentUrl(channelId)+"?index="+offset+"&count="+count+"&queryType=1", CommentVO.class, LoadMode.NET, listener);
	}
	
	public void postChannelComment(long channelId,String msg,int score, OnResultListener<CommentVO> listener){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("msg", msg);
		params.put("score", score);
		doPost(Constant.getChannelCommentRateUrl(channelId), CommentVO.class, params, listener);
	}
	public void postChannelCommentEdit(long commentId,String msg,int score, OnResultListener<CommentVO> listener){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("msg", msg);
		params.put("score", score);
		doPost(Constant.getChannelCommentRateEditUrl(commentId), CommentVO.class, params, listener);
	}
	public void getChannelById(long channelId, OnResultListener<ChannelVO> listener){
		doGet(Constant.getChannelUrl()+"/"+channelId, ChannelVO.class, LoadMode.NET, listener);
	}
}
