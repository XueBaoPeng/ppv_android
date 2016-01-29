package com.star.mobile.video.dao;

import java.util.List;

import com.star.cms.model.Package;
public interface IPackageDAO {

	void add(Package p);
	void clear();
	List<Package> query();
	Package query(long packageId);
}
