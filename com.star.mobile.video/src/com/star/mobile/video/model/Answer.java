package com.star.mobile.video.model;

public class Answer extends com.star.cms.model.Answer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3488737710525150503L;

	private boolean isSelect;
	
	public Answer(com.star.cms.model.Answer answer) {
		setId(answer.getId());
		setName(answer.getName());
		setCode(answer.getCode());
		setAppInfoID(answer.getAppInfoID());
		setCreateDate(answer.getCreateDate());
		setQuestionID(answer.getQuestionID());
		setStatus(answer.getStatus());
		setDescription(answer.getDescription());
		setSelect(false);
	}

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
	}
}
