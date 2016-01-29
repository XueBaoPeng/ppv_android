package com.star.mobile.video.smartcard;

import java.util.ArrayList;
import java.util.List;

import com.star.cms.model.vo.SmartCardInfoVO;
import com.star.mobile.video.R;
import com.star.mobile.video.widget.indicator.IconPagerAdapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
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
    private SmartCardInfoVO smartCard;

    public SmartCardFragmentViewPageAdapter(android.support.v4.app.FragmentManager fm, List<SmartCardInfoVO> mSmartinfos) {
        super(fm);
        this.mSmartinfos = mSmartinfos;
    }

    public void setSmartInfos(List<SmartCardInfoVO> smartinfos) {
        this.mSmartinfos = smartinfos;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        FragmentSmartCardInfo fsc = FragmentSmartCardInfo.newInstance(mSmartinfos.get(position));
        smartCard = mSmartinfos.get(position);
        return fsc;
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
