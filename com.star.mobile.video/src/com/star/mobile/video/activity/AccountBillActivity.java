package com.star.mobile.video.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.star.cms.model.vo.SmartCardInfoVO;
import com.star.mobile.video.R;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.view.AccountBillListView;
import com.star.mobile.video.view.ControlTabView;
import com.star.mobile.video.view.SmartCardInfoView;

public class AccountBillActivity extends BaseActivity implements OnClickListener,OnPageChangeListener{
	
	private ViewPager viewPager;
	private SmartCardInfoVO smartCardInfoVO;
	private ControlTabView controlTabView;
	private SmartCardInfoView smartCardInfoView;
	private List<String> tab =new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_bill);
		currentIntetn(getIntent());
		initView();
	}

	private void initView() {
		((TextView) findViewById(R.id.tv_actionbar_title)).setText(getString(R.string.account_bill_title));
		smartCardInfoView = (SmartCardInfoView) findViewById(R.id.bill_smartcard);
		controlTabView  = (ControlTabView) findViewById(R.id.bill_controltabview);
		viewPager = (ViewPager) findViewById(R.id.vp_bill_content);
		AccountBillListView rechargeListView = new AccountBillListView(AccountBillActivity.this,true,false,smartCardInfoVO.getSmardCardNo());
		AccountBillListView billListView = new AccountBillListView(AccountBillActivity.this,false,true,smartCardInfoVO.getSmardCardNo());
		
		List<View> views = new ArrayList<View>();
		views.add(rechargeListView);
		views.add(billListView);
		ViewPagerAdapter adapter = new ViewPagerAdapter(views);
		viewPager.setAdapter(adapter);
		findViewById(R.id.iv_actionbar_back).setOnClickListener(this);
		viewPager.setOnPageChangeListener(this);

		tab.add(getResources().getString(R.string.bill_recharge));
		tab.add(getResources().getString(R.string.bill_deduct_refund));
		controlTabView.setTabData(tab);
		controlTabView.setViewPager(viewPager);
		smartCardInfoView.setData(smartCardInfoVO);
	}
	
	private void currentIntetn(Intent intent) {
		smartCardInfoVO = (SmartCardInfoVO)intent.getSerializableExtra("smartCardInfo");
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		currentIntetn(intent);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_actionbar_back:
			onBackPressed();
			break;
		default:
			break;
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
	}

	@Override
	public void onPageSelected(int arg0) {
		controlTabView.selectTab(arg0);
	}

	class ViewPagerAdapter extends PagerAdapter {
		
		private List<View> views;
		
		
		public ViewPagerAdapter(List<View> views) {
			this.views = views;
		}

		@Override
		public int getCount() {
			
			return views.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			
			return arg0 == arg1;
		}
		
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View view = views.get(position);
			((ViewPager) container).addView(view);
			return view;
		}
	}
}
