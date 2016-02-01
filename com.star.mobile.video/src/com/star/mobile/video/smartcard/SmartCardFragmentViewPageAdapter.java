package com.star.mobile.video.smartcard;

import java.util.List;

import com.star.cms.model.vo.SmartCardInfoVO;
import com.star.mobile.video.R;
import com.star.mobile.video.widget.indicator.IconPagerAdapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

/**
 * SmartCard ViewPager的适配器
 *
 * @author Lee
 * @version 1.0
 * @date 2015/11/23
 * @motify 2016/02/01 by Lee 把fragment修改为view
 */
public class SmartCardFragmentViewPageAdapter extends PagerAdapter implements IconPagerAdapter {
    private final int resId = R.drawable.point_group;
    private List<SmartCardInfoVO> mSmartinfos;
    private List<SmartCardInfoView> mViewDatas;

    public SmartCardFragmentViewPageAdapter(List<SmartCardInfoVO> mSmartinfos, List<SmartCardInfoView> viewDatas) {
        this.mSmartinfos = mSmartinfos;
        this.mViewDatas = viewDatas;
    }

    public void setSmartInfos(List<SmartCardInfoVO> smartinfos) {
        this.mSmartinfos = smartinfos;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mSmartinfos != null && mSmartinfos.size() > 0) {
            return mSmartinfos.size();
        } else {
            return 0;
        }
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public int getIconResId(int index) {
        return resId;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // 对ViewPager页号求模取出View列表中要显示的项
        position %= mViewDatas.size();
        View view = mViewDatas.get(position);
        // 如果View已经在之前添加到了一个父组件，则必须先remove，否则会抛出IllegalStateException。
        ViewParent vp = view.getParent();
        if (vp != null) {
            ViewGroup parent = (ViewGroup) vp;
            parent.removeView(view);
        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Warning：不要在这里调用removeView
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
