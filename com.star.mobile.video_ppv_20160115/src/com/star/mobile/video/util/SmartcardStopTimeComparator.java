package com.star.mobile.video.util;

import java.util.Comparator;
import java.util.Date;

import com.star.cms.model.vo.SmartCardInfoVO;

public class SmartcardStopTimeComparator implements Comparator<SmartCardInfoVO> {

	@Override
	public int compare(SmartCardInfoVO lhs, SmartCardInfoVO rhs) {
		long time1 = new Date(lhs.getPenaltyStop()).getTime();
		long time2 = new Date(rhs.getPenaltyStop()).getTime();
		return (int) (time1 - time2);
	}
}
