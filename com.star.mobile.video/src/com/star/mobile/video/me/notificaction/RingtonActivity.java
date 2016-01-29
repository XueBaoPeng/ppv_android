package com.star.mobile.video.me.notificaction;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.star.mobile.video.R;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.RingtoneUtil;

/**
 * 
 * @author zhangkai
 *
 */
public class RingtonActivity extends BaseActivity {

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
		((TextView) findViewById(R.id.tv_actionbar_title)).setText(getString(R.string.ringtone));
		lvData = (ListView) findViewById(R.id.lv_about_list);
		mSharePre = SharedPreferencesUtil.getAlertSharePreferences(this);
		mAdapter = new ReminderSettingAdapter(this, getData(), mSharePre.getInt(Constant.RINGTONE_POS, 0));
		lvData.setAdapter(mAdapter);
		lvData.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				select = position;
				mAdapter.updataSelTextColor(position);
				RingtoneUtil.getInstance(RingtonActivity.this).playRingtone(select, true);
				mSharePre.edit().putInt(Constant.RINGTONE_POS, select).commit();
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
		data.setItemNames(RingtoneUtil.getInstance(this).getRingtoneList()
				.toArray(new String[RingtoneUtil.getInstance(this).getRingtoneList().size()]));
		data.setPos(mSharePre.getInt(Constant.RINGTONE_POS, 0));
		return data;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		RingtoneUtil.getInstance(this).stopRingtone();
	}
}
