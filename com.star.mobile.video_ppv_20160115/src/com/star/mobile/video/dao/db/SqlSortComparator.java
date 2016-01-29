package com.star.mobile.video.dao.db;

import java.util.Comparator;

public class SqlSortComparator implements Comparator<String> {

	@Override
	public int compare(String lhs, String rhs) {
		if(lhs.equals("init.sql"))
			return -1;
		if(rhs.equals("init.sql"))
			return 1;
		int start_1 = lhs.indexOf("_");
		int end_1 = lhs.indexOf(".");
		int verion_1 = Integer.parseInt(lhs.substring(start_1 + 1, end_1));
		int start_2 = rhs.indexOf("_");
		int end_2 = rhs.indexOf(".");
		int verion_2 = Integer.parseInt(rhs.substring(start_2 + 1, end_2));
		return verion_1 - verion_2;
	}
}
