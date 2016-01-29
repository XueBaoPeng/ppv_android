package com.star.mobile.video.chatroom;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.star.mobile.video.R;
import com.star.mobile.video.base.FragmentActivity;
import com.star.mobile.video.model.MenuHandle;
import com.star.mobile.video.search.SearchActivity;
import com.star.mobile.video.util.CommonUtil;

public class ChatRoomsActivity extends FragmentActivity implements OnClickListener {
	
	private FragmentManager fragmentManager;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.chatrooms_activity);
		initView();
	}
	
	
	public Fragment setFragmentByTag_Task(String tag){
		Fragment fragment = setFragmentByTag(tag);
		return fragment;
	}
	
	public Fragment setFragmentByTag(String tag) {
		try {
			FragmentTransaction transaction = fragmentManager.beginTransaction();
			Fragment fragment = (Fragment) MenuHandle.getMenuItemClass(tag).getConstructor().newInstance();
			transaction.setCustomAnimations(R.anim.tran_next_in, R.anim.tran_next_out);
			transaction.replace(R.id.home_container, fragment);
			transaction.addToBackStack(null);
			transaction.commitAllowingStateLoss();
			return fragment;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void initView() {
		findViewById(R.id.iv_actionbar_back).setOnClickListener(this);
		findViewById(R.id.iv_actionbar_search).setOnClickListener(this);
		TextView actionBarTitle = (TextView) findViewById(R.id.tv_actionbar_title);
		actionBarTitle.setText(R.string.fragment_tag_chatRooms);
		fragmentManager = getSupportFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		final ChatRoomsFragment chatroomsFragment = new ChatRoomsFragment();
		transaction.replace(R.id.ll_chatrooms_content, chatroomsFragment).commit();
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_actionbar_back:
			CommonUtil.finishActivityAnimation(this);
			break;
		case R.id.iv_actionbar_search:
			//搜索
			CommonUtil.startActivity(this, SearchActivity.class);
			break;
		default:
			break;
		}
	}

}
