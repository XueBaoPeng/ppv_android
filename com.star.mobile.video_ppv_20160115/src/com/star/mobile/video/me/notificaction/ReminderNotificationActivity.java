package com.star.mobile.video.me.notificaction;

import java.util.ArrayList;
import java.util.List;

import com.star.mobile.video.R;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.model.AboutItemData;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.Constant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class ReminderNotificationActivity extends BaseActivity {

	private ListView lvData;
	private NotificationAdapter mAdapter;
	private SharedPreferences mSharePre;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_activity);
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		lvData = (ListView) findViewById(R.id.lv_about_list);
		((TextView) findViewById(R.id.tv_actionbar_title)).setText(getString(R.string.reminder));
		mSharePre = SharedPreferencesUtil.getAlertSharePreferences(this);
		lvData.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				AboutItemData aid = (AboutItemData) mAdapter.getItem(position);
				if (aid.getTarget() == null) {
					return;
				}
				Intent intent = new Intent(ReminderNotificationActivity.this, aid.getTarget());
				CommonUtil.goActivityOrFargment(ReminderNotificationActivity.this, aid.getTarget(), intent);
			}
		});
		findViewById(R.id.iv_actionbar_back).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stubv
		super.onStart();
		if (mAdapter == null) {
			mAdapter = new NotificationAdapter(getData(), this);
			lvData.setAdapter(mAdapter);
		} else {
			mAdapter.updateData(getData());
		}

	}

	private List<AboutItemData> getData() {
		List<AboutItemData> data = new ArrayList<AboutItemData>();

		AboutItemData rington = new AboutItemData(RingtonActivity.class);
		rington.setIcon(R.drawable.ic_queue_music);
		rington.setItemName(getString(R.string.ringtone));
		data.add(rington);

		AboutItemData ringTime = new AboutItemData(ReminderSettingAcitivity.class);
		ringTime.setlItemName(getString(R.string.remind_me));
		ringTime.setrItemName(getString(R.string.ahead));
		String[] values = getResources().getStringArray(R.array.Alert_Remind);
		ringTime.setItemName(" " + values[mSharePre.getInt(Constant.REMIND_TIME_POS, 0)] + " ");
		ringTime.setIcon(R.drawable.ic_history);
		data.add(ringTime);
		return data;

	}

}
