package com.star.mobile.video.service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestHandle;
import com.star.cms.model.Comment;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.IOUtil;
import com.star.mobile.video.util.http.HTTPInvoker;
import com.star.util.json.JSONUtil;

import android.content.Context;
import android.util.Log;

public class CommentService_old extends AbsService {

	public CommentService_old(Context context) {
	}

	public RequestHandle getCommentsByEpgId(HTTPInvoker<List<Comment>> invoker, long programID) {
		return getCommentsByEpgId(invoker, programID, false);
	}

	public RequestHandle getCommentsByEpgId(HTTPInvoker<List<Comment>> invoker, long programID, boolean fromLocal) {
		invoker.setUrl(Constant.getEpgCommentsUrl(programID));
		Type type = new TypeToken<ArrayList<Comment>>() {
		}.getType();
		invoker.setTypeReturn(type);
		if (fromLocal) {
			getFromLocal(invoker);
		} else {
			return httpClient.<List<Comment>> httpGetToJSON(invoker, true);
		}
		return null;
	}

	public RequestHandle getCommentCountByProgramId(HTTPInvoker<Long> invoker, long programID) {
		return getCommentCountByProgramId(invoker, programID, false);
	}

	public RequestHandle getCommentCountByProgramId(HTTPInvoker<Long> invoker, long programID, boolean fromLocal) {
		invoker.setUrl(Constant.getEpgCommentsUrl(programID) + "/count");
		invoker.setTypeReturn(Long.class);
		if (fromLocal) {
			getFromLocal(invoker);
		} else {
			return httpClient.httpGetToJSON(invoker, true);
		}
		return null;
	}

	public RequestHandle getCommentCountByChannelId(HTTPInvoker<Long> invoker, long channelId) {
		return getCommentCountByChannelId(invoker, channelId, false);
	}

	public RequestHandle getCommentCountByChannelId(HTTPInvoker<Long> invoker, long channelId, boolean fromLocal) {
		invoker.setUrl(Constant.getChannelCommentsUrl(channelId) + "/count");
		invoker.setTypeReturn(Long.class);
		if (fromLocal) {
			getFromLocal(invoker);
		} else {
			return httpClient.httpGetToJSON(invoker, true);
		}
		return null;
	}

	public List<Comment> getCommentsByChannelId(long channelId,int offset,int count) {
		try {
			String json = IOUtil.httpGetToJSON(Constant.getChannelCommentsUrl(channelId)+"?index="+offset+"&count="+count);
			if (json != null)
				return JSONUtil.getFromJSON(json, new TypeToken<ArrayList<Comment>>() {}.getType());
		} catch (Exception e) {
			Log.e("", "get comment list from server error!", e);
		}
		return null;
	}
	
	public RequestHandle getCommentsByChannelId(HTTPInvoker<List<Comment>> invoker, long channelId) {
		return getCommentsByChannelId(invoker, channelId, false);
	}

	public RequestHandle getCommentsByChannelId(HTTPInvoker<List<Comment>> invoker, long channelId, boolean fromLocal) {
		invoker.setUrl(Constant.getChannelCommentsUrl(channelId));
		Type type = new TypeToken<ArrayList<Comment>>() {
		}.getType();
		invoker.setTypeReturn(type);
		if (fromLocal) {
			getFromLocal(invoker);
		} else {
			return httpClient.httpGetToJSON(invoker, true);
		}
		return null;
	}

	public Comment submitComment(long programID,String msg){
		Comment comment=null;
		Map<String,Object> params=new HashMap<String,Object>();
		try{
		params.put("programID",programID);
		params.put("msg",msg);
		String json = IOUtil.httpPostToJSON(params,Constant.getEpgCommentsUrl(programID));
		if (json != null) {
			comment = JSONUtil.getFromJSON(json, new TypeToken<Comment>() {}.getType());
			 
		}
		} catch (Exception e) {
		 
		}
	return comment;
	}
	
	public RequestHandle submitComment(HTTPInvoker<Comment> invoker, long programID, String msg) {
		return submitComment(invoker, programID, -1, msg);
	}

	public RequestHandle submitComment(HTTPInvoker<Comment> invoker, long programID, long parentID, String msg) {
		invoker.setUrl(Constant.getEpgCommentsUrl(programID));
		invoker.setTypeReturn(Comment.class);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("programID", programID);
		params.put("msg", msg);
		if (parentID != -1)
			params.put("parentID", parentID);
		return httpClient.httpPostToJSON(invoker, params);
	}

	public RequestHandle getCommentsOfEpg(HTTPInvoker<List<Comment>> invoker,long programID, boolean fromLocal) {
		String url = Constant.getEpgCommentsUrl(programID);
		invoker.setUrl(url);
		return getComments(invoker, fromLocal);
	}

	public RequestHandle getCommentsOfChannel(HTTPInvoker<List<Comment>> invoker,long channelID, boolean fromLocal) {
		String url = Constant.getChannelCommentsUrl(channelID);
		invoker.setUrl(url);
		return getComments(invoker, fromLocal);
	}

	public RequestHandle addPraise(HTTPInvoker<String> invoker, String commentID) {
		invoker.setUrl(Constant.getPraiseUrl(commentID));
		return httpClient.httpPost(invoker, commentID);
	}

	public RequestHandle delPraise(HTTPInvoker<String> invoker, String commentID) {
		invoker.setUrl(Constant.getPraiseUrl(commentID));
		return httpClient.httpDelete(invoker);
	}

	private RequestHandle getComments(HTTPInvoker<List<Comment>> invoker, boolean fromLocal) {
		Log.i("DaoService", "CommentService---" + invoker.getUrl());
		Type type = new TypeToken<ArrayList<Comment>>() {
		}.getType();
		invoker.setTypeReturn(type);
		if (fromLocal) {
			getFromLocal(invoker);
		} else {
			return httpClient.httpGetToJSON(invoker, true);
		}
		return null;
	}
	/**
	 * 获得分页数据
	 * @param invoker
	 * @param channelId
	 * @param offset
	 * @param count
	 * @param fromLocal
	 * @return
	 */
	public RequestHandle getChannelComments(HTTPInvoker<List<Comment>> invoker, long channelId,int offset,int count, boolean fromLocal){
		invoker.setUrl(Constant.getChannelCommentsUrl(channelId)+"?index="+offset+"&count="+count);
		Type type = new TypeToken<ArrayList<Comment>>() {
		}.getType();
		invoker.setTypeReturn(type);
		if (fromLocal) {
			getFromLocal(invoker);
		} else {
			return httpClient.httpGetToJSON(invoker, true);
		}
		return null;
	}
	
}
