package com.star.mobile.video.dao;

import java.util.List;

import com.star.cms.model.vo.ProgramVO;
import com.star.ott.ppvup.model.enums.CategoryType;

public interface IProgramDAO{

	boolean add(ProgramVO program);
	boolean remove(long programId);
	void clear();
	ProgramVO query(long programId);
	List<ProgramVO> query(boolean isFav, long startDate, long endDate, int index, int count);
	List<ProgramVO> query(boolean isChange);
	List<ProgramVO> query(long channelId, long startDate, long endDate, int index, int count);
	boolean updateFavStatus(ProgramVO program, boolean isSync);
	
	CategoryType getCategoryType(long programId);
	void updateCategoryType(long programId, CategoryType categoryType);
}