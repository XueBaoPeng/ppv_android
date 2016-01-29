package com.star.mobile.video.me.account;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.star.cms.model.enm.AccountType;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.account.ResetPasswordActivity;
import com.star.mobile.video.activity.AccountConnectActivity;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.model.AboutItemData;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.CommonUtil;


/**
 * 
 * @author zhangkai
 *
 */
public class AccountAcitivity extends BaseActivity {
	
	private ListView lvData;
	private AccountAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_activity);
		initView();
	}
	
	
	private void initView() {
		lvData = (ListView) findViewById(R.id.lv_about_list);
		mAdapter = new AccountAdapter(getData(), this);
		((TextView) findViewById(R.id.tv_actionbar_title)).setText(getString(R.string.account));
		lvData.setAdapter(mAdapter);
		lvData.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				AboutItemData aid = (AboutItemData) mAdapter.getItem(position);
				if(SharedPreferencesUtil.getUserName(AccountAcitivity.this) == null) {
					CommonUtil.pleaseLogin(AccountAcitivity.this);
				}else{
					if(getString(R.string.reset_password).equals(aid.getItemName())) {
						Intent intent = new Intent(AccountAcitivity.this, aid.getTarget());
						CommonUtil.goActivityOrFargment(AccountAcitivity.this,aid.getTarget(),intent);
					} else {
						Intent intent = new Intent(AccountAcitivity.this, aid.getTarget());
						CommonUtil.goActivityOrFargment(AccountAcitivity.this,aid.getTarget(),intent);	
					}
				}
				
			}
		});
		findViewById(R.id.iv_actionbar_back).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}

	
	private List<AboutItemData> getData() {
		List<AboutItemData> data = new ArrayList<AboutItemData>();
		AboutItemData chatrommNotif = new AboutItemData(getString(R.string.reset_password), ResetPasswordActivity.class);
		chatrommNotif.setIcon(R.drawable.ic_redo);
		if(!(AccountType.Facebook.equals(StarApplication.mUser.getType()) || AccountType.Twitter.equals(StarApplication.mUser.getType()))) {
			data.add(chatrommNotif);
		}
		AboutItemData reminder = new AboutItemData(getString(R.string.account_connect_title),AccountConnectActivity.class);
		reminder.setIcon(R.drawable.ic_share);
		data.add(reminder);
		return data;
	}
}
