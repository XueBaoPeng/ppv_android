package com.star.mobile.video.smartcard;

import java.util.List;

import com.star.cms.model.vo.SmartCardInfoVO;
import com.star.mobile.video.R;
import com.star.mobile.video.widget.indicator.IconPagerAdapter;
import com.star.util.Logger;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

/**
 * SmartCard ViewPager的适配器
 * 
 * @author Lee
 * @version 1.0
 * @date 2015/11/23
 */
public class SmartCardFragmentViewPageAdapter extends FragmentStatePagerAdapter implements IconPagerAdapter {
	private final int resId = R.drawable.point_group;
	private List<SmartCardInfoVO> mSmartinfos;
	private String mChangePkgSmartCardNumber; //换包的卡号
	private SmartCardInfoVO smartCard;
	public SmartCardFragmentViewPageAdapter(android.support.v4.app.FragmentManager fm,
			List<SmartCardInfoVO> smartinfos,String changePkgSmartCardNumber) {
		super(fm);
		this.mSmartinfos = smartinfos;
		this.mChangePkgSmartCardNumber = changePkgSmartCardNumber;
	}

	public void setSmartInfos(List<SmartCardInfoVO> smartinfos,String changePkgSmartCardNumber) {
		this.mSmartinfos = smartinfos;
		this.mChangePkgSmartCardNumber = changePkgSmartCardNumber;
		notifyDataSetChanged();
	}

	@Override
	public Fragment getItem(int position) {
		FragmentSmartCardInfo fsc = FragmentSmartCardInfo.newInstance(mSmartinfos.get(position));
		smartCard = mSmartinfos.get(position);
//		fsc.setSmartinfos(mSmartinfos);
//		fsc.setChangePkgSmartCardNO(mChangePkgSmartCardNumber);
		return fsc;
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		if(mSmartinfos.size()>position&&!mSmartinfos.get(position).equals(smartCard)){
			Logger.e("FragmentSmartCardInfo is not currently in the FragmentManager.");
			return;
		}
		super.destroyItem(container, position, object);
	}

	@Override
	public int getCount() {
		return mSmartinfos.size();
	}

	@Override
	public int getIconResId(int index) {
		return resId;
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

}
