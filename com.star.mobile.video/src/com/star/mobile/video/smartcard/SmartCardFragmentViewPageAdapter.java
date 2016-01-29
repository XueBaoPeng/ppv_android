package com.star.mobile.video.smartcard;

import java.util.ArrayList;
import java.util.List;

import com.star.mobile.video.R;
import com.star.mobile.video.widget.indicator.IconPagerAdapter;

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
    private List<FragmentSmartCardInfo> mFragments = new ArrayList<FragmentSmartCardInfo>();
    public SmartCardFragmentViewPageAdapter(android.support.v4.app.FragmentManager fm) {
        super(fm);
    }


    public void addFragment(List<FragmentSmartCardInfo> fragments)
    {
        this.mFragments = fragments;
        notifyDataSetChanged();
    }

    @Override
    public FragmentSmartCardInfo getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public int getIconResId(int index) {
        return resId;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Yet another bug in FragmentStatePagerAdapter that destroyItem is called on fragment that hasnt been added. Need to catch
        try {
            super.destroyItem(container, position, object);
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}
