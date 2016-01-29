package com.star.mobile.video.me.notificaction;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.star.mobile.video.AlertManager;
import com.star.mobile.video.R;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.Constant;

/**
 * 设置提示音间隔时间
 * 
 * @author zhangkai
 *
 */
public class ReminderSettingAcitivity extends BaseActivity {

	private ListView lvData;
	private ReminderSettingAdapter mAdapter;
	private SharedPreferences mSharePre;
	private int select;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_activity);
		initView();
	}

	private void initView() {
		((TextView) findViewById(R.id.tv_actionbar_title)).setText(getString(R.string.reminder_setting));
		lvData = (ListView) findViewById(R.id.lv_about_list);
		mSharePre = SharedPreferencesUtil.getAlertSharePreferences(this);
		mAdapter = new ReminderSettingAdapter(this, getData(), mSharePre.getInt(Constant.REMIND_TIME_POS, 0));
		lvData.setAdapter(mAdapter);
		findViewById(R.id.iv_actionbar_back).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		lvData.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				select = position;
				mAdapter.updataSelTextColor(position);
				AlertManager.getInstance(ReminderSettingAcitivity.this).setAlertTime(select);
				mSharePre.edit().putInt(Constant.REMIND_TIME_POS, select).commit();
			}
		});

		findViewById(R.id.iv_actionbar_back).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}

	private ReminderSettingItemData getData() {
		ReminderSettingItemData data = new ReminderSettingItemData();
		data.setItemNames(getResources().getStringArray(R.array.Alert_Remind));
		data.setPos(mSharePre.getInt(Constant.REMIND_TIME_POS, 0));
		return data;
	}
}
