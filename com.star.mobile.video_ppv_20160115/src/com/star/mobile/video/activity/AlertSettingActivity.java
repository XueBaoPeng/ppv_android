package com.star.mobile.video.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.star.mobile.video.R;
import com.star.mobile.video.adapter.AlertSettingListAdapter;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.model.AlertSettingItem;
import com.star.mobile.video.search.SearchActivity;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.RingtoneUtil;

public class AlertSettingActivity extends BaseActivity implements OnClickListener{

	private List<AlertSettingItem> items = new ArrayList<AlertSettingItem>();
	private SharedPreferences mSharePre;
	private AlertSettingListAdapter listAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alertsetting);
		mSharePre = SharedPreferencesUtil.getAlertSharePreferences(this);
		ListView lv_alert_list = (ListView) findViewById(R.id.lv_alert_list);
		((TextView)findViewById(R.id.tv_actionbar_title)).setText(R.string.activity_alertsetting_title);
		((ImageView)findViewById(R.id.iv_actionbar_search)).setOnClickListener(this);
		((ImageView)findViewById(R.id.iv_actionbar_search)).setImageResource(R.drawable.actionbar_search);
		findViewById(R.id.iv_actionbar_back).setOnClickListener(this);
		listAdapter = new AlertSettingListAdapter(this, initAlertItemData());
		lv_alert_list.setAdapter(listAdapter);
		
	}

	private List<AlertSettingItem> initAlertItemData() {
		AlertSettingItem item;
		int pos;
		item = new AlertSettingItem();
		item.setDrawable(getResources().getDrawable(R.drawable.icon_ringtone));
		item.setItemContent("Ringtone");
		pos = mSharePre.getInt(Constant.RINGTONE_POS, 0);
		item.setSelectPos(pos);
		item.setItemId(Constant.RINGTONE_ID);
//		item.setChildItems(getResources().getStringArray(R.array.Alert_Ringtone));
		item.setChildItems(RingtoneUtil.getInstance(this).getRingtoneList().toArray(new String[RingtoneUtil.getInstance(this).getRingtoneList().size()]));
		items.add(item);
		
		item = new AlertSettingItem();
		item.setDrawable(getResources().getDrawable(R.drawable.alert_setting_icon_alert));
		pos = mSharePre.getInt(Constant.REMIND_TIME_POS, 0);
		item.setSelectPos(pos);
		item.setItemId(Constant.REMIND_TIME_ID);
		String[] values = getResources().getStringArray(R.array.Alert_Remind);
		item.setItemContent(values[pos]);
		item.setChildItems(values);
		items.add(item);
		
		return items;
	}

	@Override
	public void onBackPressed() {
		RingtoneUtil.getInstance(this).stopRingtone();
		super.onBackPressed();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_actionbar_back:
			RingtoneUtil.getInstance(this).stopRingtone();
			super.onBackPressed();
			 Intent intent=new Intent(this,AlertListActicity.class);
			 startActivity(intent);
			break;
		case R.id.iv_actionbar_search:
			CommonUtil.startActivity(this, SearchActivity.class);
		default:
			break;
		}
	}
}
