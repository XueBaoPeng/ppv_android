package com.star.mobile.video.activity;

import android.os.Bundle;
import android.widget.ListView;

import com.star.mobile.video.R;
import com.star.mobile.video.base.BaseActivity;

public class ChargingRecordActivity extends BaseActivity{
	
	private ListView lvWeTask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_charging_record);
		lvWeTask = (ListView) findViewById(R.id.lv_we_task);
	}

}
