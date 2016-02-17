package com.star.mobile.video.dao;

import java.util.List;

import com.star.cms.model.Package;
import com.star.cms.model.enm.TVPlatForm;

public interface IPackageDAO {

	void add(Package p);
	void clear();
	List<Package> query(TVPlatForm platform);
	Package query(long packageId);
}
