package com.star.mobile.video.discovery;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

import com.star.cms.model.Discovery;
import com.star.mobile.video.R;
import com.star.mobile.video.home.HomeActivity;
import com.star.mobile.video.home.tab.TabFragment;
import com.star.mobile.video.model.FunctionType;
import com.star.mobile.video.service.FunctionService;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.util.loader.OnListResultListener;

import java.util.Iterator;
import java.util.List;

public class DiscoveryFragment extends TabFragment {
	
	private View mView;
	private ListView lvDiscobery;
	private DiscoveryAdapter mAdapter;
	private HomeActivity mHomeActivity;
	private DiscoveryService discoveryService;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(mView != null){
			ViewGroup parent = (ViewGroup) mView.getParent();  
			if(parent != null) {  
				parent.removeView(mView);  
			}   
			return mView;
		}
		mHomeActivity = (HomeActivity) getActivity();
		mView = inflater.inflate(R.layout.discovery_fragment, null);
		mView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		initView();
		return mView;
	}
	
	
	private void initView() {
		discoveryService = new DiscoveryService(getActivity());
		lvDiscobery = (ListView) mView.findViewById(R.id.lv_discovery);
		lvDiscobery.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				Discovery d = (Discovery) mAdapter.getItem(position);
				String code = d.getCode();
				if ("Bundesliga".equals(d.getName()) || "Serie A".equals(d.getName())) {
					code += SharedPreferencesUtil.getAreaCode(mHomeActivity);
				}
				if ("Forum".equals(d.getName())) {
					Class<?> target = null;
					try {
						target = Class.forName(d.getTarget());
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					Intent intent = new Intent(mHomeActivity, target);
					intent.putExtra("loadUrl", d.getCode());
					intent.putExtra("pageName", d.getName());
					intent.putExtra("isBbs", 1);
					CommonUtil.goActivityOrFargment(mHomeActivity, target, intent);
				} else {
					CommonUtil.goActivityOrFargment(mHomeActivity, d.getType().getNum(), d.getTarget(), null, code, d.getName());
				}
			}
		});
		
	}
	
	@Override
	public void onStart() {
		super.onStart();
		getDisovery();
	}

	private void getDisovery() {
		discoveryService.getDiscovery(new OnListResultListener<Discovery>() {

			@Override
			public boolean onIntercept() {
				CommonUtil.showProgressDialog(mHomeActivity);
				return false;
			}

			@Override
			public void onFailure(int errorCode, String msg) {
				CommonUtil.closeProgressDialog();
			}

			@Override
			public void onSuccess(List<Discovery> result) {
				CommonUtil.closeProgressDialog();
				checkData(result);
				if (result != null) {
					if (mAdapter == null) {
						mAdapter = new DiscoveryAdapter(result, mHomeActivity);
						lvDiscobery.setAdapter(mAdapter);
					} else {
						mAdapter.updateData(result);
					}
					initScrollListener(lvDiscobery);
				}
			}
		});
	}

	/**
	 * 屏蔽数据
	 * @param data
	 */
	private void checkData(List<Discovery> data) {
		if(FunctionService.doHideFuncation(FunctionType.PPV)) { //如果不是坦桑地区移除ppv
			Iterator iterator = data.iterator();
			while(iterator.hasNext()) {
				Discovery d = (Discovery) iterator.next();
				if("PPV".equals(d.getName())) {
					iterator.remove();
				}
			}
		}

	}
}
