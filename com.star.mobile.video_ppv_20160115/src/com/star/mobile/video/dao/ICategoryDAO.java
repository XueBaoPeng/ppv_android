package com.star.mobile.video.dao;

import java.util.List;

import com.star.cms.model.Category;

public interface ICategoryDAO {

	void add(Category category);
	void clear();
	List<Category> query();
}