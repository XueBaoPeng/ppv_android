package com.star.mobile.video.appversion;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.star.cms.model.APPInfo;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.adapter.AppDetailAdapter;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.me.feedback.UserReportActivity;
import com.star.mobile.video.model.FunctionType;
import com.star.mobile.video.service.ApplicationService;
import com.star.mobile.video.service.EggAppearService;
import com.star.mobile.video.service.FunctionService;
import com.star.mobile.video.util.ApplicationUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.ToastUtil;
import com.star.mobile.video.view.AppDetailFootView;
import com.star.mobile.video.view.AppDetailHeadView;
import com.star.util.loader.OnResultListener;

public class CurrentVersionAppDetailActivity extends BaseActivity implements OnClickListener{
	
	private ListView lvAppInfo;
	private ApplicationService applicationService;
	private APPInfo appInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_detail);
		applicationService = new ApplicationService(this);
		initView();
		initData();
		EggAppearService.appearEgg(this, EggAppearService.VersionInfo);
	}

	
	private void initView() {
		((TextView) findViewById(R.id.tv_actionbar_title)).setText(getString(R.string.current_version_title));
		((ImageView) findViewById(R.id.iv_actionbar_back)).setOnClickListener(this);
		lvAppInfo = (ListView) findViewById(R.id.lv_appinfo);
	}
	
	private void initData() {
		
		applicationService.getDetailAppInfo(ApplicationUtil.getAppVerison(CurrentVersionAppDetailActivity.this), new OnResultListener<APPInfo>() {
			
			@Override
			public void onSuccess(APPInfo value) {
				appInfo = value;
				CommonUtil.closeProgressDialog();
				if(appInfo != null) {
					AppDetailHeadView headView = new AppDetailHeadView(CurrentVersionAppDetailActivity.this,appInfo.getDescription(),appInfo.getApkSize());
					AppDetailFootView footView = new AppDetailFootView(CurrentVersionAppDetailActivity.this);
					footView.setFastUserButtonOnClick(CurrentVersionAppDetailActivity.this);
					headView.setNewVersionBtnOnClick(CurrentVersionAppDetailActivity.this);
					headView.setNewVersionBtnText(getString(R.string.new_version));
					lvAppInfo.addHeaderView(headView);
					if(!(FunctionService.doHideFuncation(FunctionType.FastReport)||StarApplication.CURRENT_VERSION==Constant.FINAL_VERSION))
						lvAppInfo.addFooterView(footView);
					List<String> datas = new ArrayList<String>();
					if(appInfo.getUpdateInfo()!=null){
						String [] data = appInfo.getUpdateInfo().split("\n");
						for(int i = 0;i < data.length;i++) {
							if(!"".equals(data[i])) {
								datas.add(data[i]);
							}
						}
					}else{
						ToastUtil.centerShowToast(CurrentVersionAppDetailActivity.this, getString(R.string.no_version_info));
					}
					AppDetailAdapter adapter = new AppDetailAdapter(datas, CurrentVersionAppDetailActivity.this);
					lvAppInfo.setAdapter(adapter);
				} else {
					ToastUtil.centerShowToast(CurrentVersionAppDetailActivity.this, getString(R.string.error_network));
				}
			}
			
			@Override
			public boolean onIntercept() {
				CommonUtil.showProgressDialog(CurrentVersionAppDetailActivity.this, null, getString(R.string.loading));
				return false;
			}
			
			@Override
			public void onFailure(int errorCode, String msg) {
				CommonUtil.closeProgressDialog();
				ToastUtil.centerShowToast(CurrentVersionAppDetailActivity.this, getString(R.string.error_network));
			}
		});
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_actionbar_back:
			super.onBackPressed();
			break;
		case R.id.btn_new_version:
			CommonUtil.startActivity(CurrentVersionAppDetailActivity.this, NewVersionAppDetailActivity.class);
			finish();
			break;
		case R.id.btn_fast_user:
			CommonUtil.startActivity(CurrentVersionAppDetailActivity.this, UserReportActivity.class);
			finish();
			break;
		default:
			break;
		}
	}
}
