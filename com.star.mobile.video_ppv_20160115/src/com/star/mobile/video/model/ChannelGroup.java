package com.star.mobile.video.model;

import java.util.List;

import com.star.cms.model.Category;
import com.star.cms.model.vo.ChannelVO;

public class ChannelGroup {

	private Category category;
	private List<ChannelVO> channels;
	private int resId;

	public ChannelGroup(Category category, List<ChannelVO> channels, int resId) {
		super();
		this.category = category;
		this.channels = channels;
		this.resId = resId;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public List<ChannelVO> getChannels() {
		return channels;
	}

	public void setChannels(List<ChannelVO> channels) {
		this.channels = channels;
	}

	public int getResId() {
		return resId;
	}

	public void setResId(int resId) {
		this.resId = resId;
	}
}
