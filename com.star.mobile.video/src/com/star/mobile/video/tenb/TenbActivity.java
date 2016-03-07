package com.star.mobile.video.tenb;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.star.cms.model.TenbMe;
import com.star.cms.model.User;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.activity.BrowserActivity;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.dao.ServerUrlDao;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.DifferentUrlContral;
import com.star.mobile.video.view.ListView;
import com.star.mobile.video.view.ListView.LoadingListener;
import com.star.ui.ImageView;

public class TenbActivity extends BaseActivity implements OnClickListener {
	
	private ListView lvTenb;
	private TenbService tenbService;
	private ProgressBar loding;
	private TenbAdapter mAdapter;
	private List<TenbMe> currentData = new ArrayList<TenbMe>();
	private Long userId;
	private TenbFootView tenFootView;
	
	private ImageView ivHeader;
	private TextView tvName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tenb);
		tenbService = new TenbService(this);
		initView();
		initUserInfo(getIntent());
	}

	private void initView() {
		lvTenb = (ListView) findViewById(R.id.lv_tenb);
		((TextView) findViewById(R.id.tv_actionbar_title)).setText(getString(R.string.tenb));
		loding = (ProgressBar) findViewById(R.id.pb_title_loading);
		findViewById(R.id.iv_sendbbs).setOnClickListener(this);
		findViewById(R.id.iv_actionbar_back).setOnClickListener(this);
		loding.setVisibility(View.VISIBLE);
		lvTenb.setLoadingListener(new LoadingListener<TenbMe>() {

			@Override
			public List<TenbMe> loadingS(int offset, int requestCount) {
				return tenbService.getTenbData(userId,offset,requestCount,false);
			}

			@Override
			public void loadPost(List<TenbMe> responseDatas) {
				loding.setVisibility(View.GONE);
				if(responseDatas!=null&&responseDatas.size()>0){
					currentData.addAll(responseDatas);
					mAdapter.updateData(currentData);
				}
			}

			@Override
			public List<TenbMe> loadingL(int offset, int requestCount) {
				return tenbService.getTenbData(userId,offset,requestCount,true);
			}

			@Override
			public List<TenbMe> getFillList() {
				return currentData;
			}

			@Override
			public void onNoMoreData() {
				if(lvTenb.getFooterViewsCount()>=2){
					return;
				}
				tenFootView = new TenbFootView(TenbActivity.this);
				lvTenb.addFooterView(tenFootView);
			}
		});
	}

	private void initUserInfo(Intent intent) {
		View headerView = LayoutInflater.from(this).inflate(R.layout.view_tenb_header, null);
		lvTenb.addHeaderView(headerView);
		ivHeader = (ImageView) headerView.findViewById(R.id.iv_user_header);
		tvName = (TextView) headerView.findViewById(R.id.tv_user_nick);
		ReSetUserInfo(intent);//设置用户信息
		mAdapter = new TenbAdapter(this, currentData);
		lvTenb.setAdapter(mAdapter);
		
		lvTenb.loadingData(true);
	}
	
	private void ReSetUserInfo(Intent intent){
		User user = StarApplication.mUser;
		String nickname = intent.getStringExtra("nickname");
		String headUrl = intent.getStringExtra("headurl");
		userId = intent.getLongExtra("userId", user.getId());
		ivHeader.setImageResource(R.drawable.no_portrait);
		
		if(nickname==null&&user!=null){
			tvName.setText(user.getNickName());
		}else{
			if(nickname!=null&&user!=null&&!nickname.equals(user.getNickName())){
				((TextView) findViewById(R.id.tv_actionbar_title)).setText(nickname+"'s feed");
				findViewById(R.id.iv_sendbbs).setVisibility(View.GONE);
			}
			tvName.setText(nickname);
		}
		
		 if(StarApplication.mUser != null&&StarApplication.mUser.getId().equals(userId)){ 
			ivHeader.setUrl(StarApplication.mUser.getHead());
		 } else {
			if(headUrl!=null){
				ivHeader.setUrl(headUrl);		
			}else{
				ivHeader.setBackgroundResource(R.drawable.no_portrait);
			}
		}
	}
	@Override
	protected void onNewIntent(Intent intent) {
		currentData.clear();
		if(tenFootView!=null)
		lvTenb.removeFooterView(tenFootView);
		mAdapter.notifyDataSetChanged();
		ReSetUserInfo(intent);//设置用户信息
		lvTenb.loadingData(false);
		super.onNewIntent(intent);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_actionbar_back:
			onBackPressed();
			break;
		case R.id.iv_sendbbs:
			ServerUrlDao serverUrlDao = DifferentUrlContral.diffUrlContral(this);
			Intent intent = new Intent(this, BrowserActivity.class);
//			intent.putExtra("loadUrl",getString(R.string.tenb_post_bbs_url));
			intent.putExtra("loadUrl",serverUrlDao.getTenbPostBbsUrl());
			intent.putExtra("isBbs",1);
			CommonUtil.startActivity(this, intent);
			break;
		default:
			break;
		}
	}
}
