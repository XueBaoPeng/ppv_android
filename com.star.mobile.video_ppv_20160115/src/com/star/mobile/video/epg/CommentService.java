package com.star.mobile.video.epg;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.star.cms.model.Comment;
import com.star.mobile.video.base.AbstractService;
import com.star.mobile.video.util.Constant;
import com.star.util.loader.LoadMode;
import com.star.util.loader.OnListResultListener;
import com.star.util.loader.OnResultListener;

public class CommentService extends AbstractService {

	public CommentService(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public void getCommentsByEpgId(long programID, OnListResultListener<Comment> listener) {
		doGet(Constant.getEpgCommentsUrl(programID), Comment.class, LoadMode.CACHE_NET, listener);
	}
	
	public void submitComment(long programID, String msg, OnResultListener<Comment> listener) {
		submitComment(programID, -1, msg, listener);
	}
	
	public void submitComment(long programID, long parentID, String msg, OnResultListener<Comment> listener) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("programID", programID);
		params.put("msg", msg);
		if (parentID != -1)
			params.put("parentID", parentID);
		doPost(Constant.getEpgCommentsUrl(programID), Comment.class, params, listener);
	}
	
	public void getCommentCountByProgramId(long programID, OnResultListener<Long> listener) {
		doGet(Constant.getEpgCommentsUrl(programID) + "/count", Long.class, LoadMode.NET, listener);
	}
	
	public void getCommentCountByChannelId(long channelId, OnResultListener<Long> listener) {
		doGet(Constant.getChannelCommentsUrl(channelId) + "/count", Long.class, LoadMode.NET, listener);
	}
 
}
