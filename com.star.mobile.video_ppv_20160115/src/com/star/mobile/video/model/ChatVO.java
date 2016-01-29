package com.star.mobile.video.model;

import com.star.cms.model.Chart;

public class ChatVO extends Chart {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8235650201105337192L;

	public static final int STATUS_VALID = 0;
	public static final int STATUS_INVALID = 1;
	public static final int STATUS_SENDING = 2;

	private int status;
	/* 节点类型 */
	public String nodeType;

	public String getNodeType() {
		return nodeType;
	}

	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	public ChatVO(){};
	
	public ChatVO(Chart chat, int status) {
		setId(chat.getId());
		setMsg(chat.getMsg());
		setImageURL(chat.getImageURL());
		setIcoURL(chat.getIcoURL());
		setType(chat.getType());
		setUserId(chat.getUserId());
		setUserName(chat.getUserName());
		setCreateDate(chat.getCreateDate());
		setStatus(status);
	}
}
