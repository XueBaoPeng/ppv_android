package com.star.mobile.video.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.star.mobile.video.R;
import com.star.mobile.video.adapter.ViewPagerAdapter;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.me.notificaction.ReminderNotificationActivity;
import com.star.mobile.video.model.WeekModel;
import com.star.mobile.video.service.EggAppearService;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.DensityUtil;
import com.star.mobile.video.view.BaseEpgItemView.OnItemCallBackListener;
import com.star.mobile.video.view.ControlTabView;
import com.star.mobile.video.view.EpgOnAlertListView;

public class AlertListActicity extends BaseActivity implements OnPageChangeListener, OnClickListener{

//	private com.star.ui.HorizontalListView hlv_weekday_group;
	private ViewPager vp_epg_group;
	private ViewPagerAdapter pagerAdapter;
	private ArrayList<View> views = new ArrayList<View>();
//	private WeekdayAdapter weekAdapter;
	private List<WeekModel> weeks;
	private List<String> tab =new ArrayList<String>();
	private ControlTabView controlTabView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alertlist);
		sortWeekday();
		initView();
		EggAppearService.appearEgg(this, EggAppearService.Alert_list);
	}

	private void sortWeekday() {
		weeks = new ArrayList<WeekModel>();
		String[] days = getResources().getStringArray(R.array.weekdays);
		Calendar calendar = Calendar.getInstance();
		int todayOfWeek =  calendar.get(Calendar.DAY_OF_WEEK);
		WeekModel week;
		for(int i=days.length-1; i>=todayOfWeek-1; i--){
			week = new WeekModel();
			if(i==todayOfWeek-1){
				week.setWeekday("Today");
				weeks.add(0, week);
				break;
			}
			week.setWeekday(days[i]);
			weeks.add(0, week);
		}
		for(int i=0; i<todayOfWeek-1; i++){
			week = new WeekModel();
			week.setWeekday(days[i]);
			weeks.add(week);
		}
		long todayTime = calendar.getTime().getTime();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		for(int j=0; j<weeks.size(); j++){
			if(j==0){
				weeks.get(j).setStartime(todayTime);
			}else{
				weeks.get(j).setStartime(calendar.getTime().getTime()+j*24*60*60*1000l);
			}
			weeks.get(j).setEndtime(calendar.getTime().getTime()+(j+1)*24*60*60*1000l-1);
		}
	}
	
	private void initView() {
		controlTabView = (ControlTabView) findViewById(R.id.controltabview);
		((TextView)findViewById(R.id.tv_actionbar_title)).setText(R.string.activity_alertlist_title);
		((ImageView)findViewById(R.id.iv_actionbar_search)).setImageResource(R.drawable.icon_bulling_alert_setting);
		((ImageView)findViewById(R.id.iv_actionbar_search)).setOnClickListener(this);
		findViewById(R.id.iv_actionbar_back).setOnClickListener(this);
//		hlv_weekday_group = (com.star.ui.HorizontalListView) findViewById(R.id.hlv_weekday_group);
		vp_epg_group = (ViewPager) findViewById(R.id.vp_epg_group);
//		hlv_weekday_group.setOnItemSelectedListener(this);
//		ViewTreeObserver vto = hlv_weekday_group.getViewTreeObserver(); 
//		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { 
//			private boolean hasMeasured;
//			public boolean onPreDraw() { 
//				if (hasMeasured == false) { 
//					int width = hlv_weekday_group.getMeasuredWidth(); 
//					weekAdapter = new WeekdayAdapter(width);
//					hlv_weekday_group.setAdapter(weekAdapter);
//					hasMeasured = true; 
//				} 
//				return true; 
//			} 
//		}); 
		OnItemCallBackListener listener = new OnItemCallBackListener() {
			@Override
			public void onItemCallBack() {
				CommonUtil.finishActivity(AlertListActicity.this);
			}
		};
		for (int i = 0; i < weeks.size(); i++) {
			EpgOnAlertListView listview = new EpgOnAlertListView(AlertListActicity.this, weeks.get(i));
			listview.setOnItemCallBackListener(listener);
			views.add(listview);
			tab.add(weeks.get(i).getWeekday());
		}
		pagerAdapter = new ViewPagerAdapter(this, views);
		vp_epg_group.setAdapter(pagerAdapter);
		vp_epg_group.setOnPageChangeListener(this);
		controlTabView.setTabData(tab);
		controlTabView.setViewPager(vp_epg_group);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int arg0) {
//		weekAdapter.setClickPos(arg0);
		controlTabView.selectTab(arg0);
//		hlv_weekday_group.setSelection(arg0);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_actionbar_back:
//			CommonUtil.startActivity(homeActivity, HomeActivity.class);
			super.onBackPressed();
			break;
		case R.id.iv_actionbar_search:
			CommonUtil.startActivity(this, ReminderNotificationActivity.class);
		default:
			break;
		}
	}
	
	class WeekdayAdapter extends BaseAdapter{

		private int itemwith;
		private int clickPos;

		public WeekdayAdapter(int listviewWidth) {
			itemwith = (listviewWidth-DensityUtil.dip2px(AlertListActicity.this, 25))/3;
		}
		
		public void setClickPos(int clickPos){
			this.clickPos = clickPos;
			notifyDataSetChanged();
		}
		
		@Override
		public int getCount() {
			return weeks.size();
		}

		@Override
		public Object getItem(int position) {
			return weeks.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if(convertView == null){
				convertView = LayoutInflater.from(AlertListActicity.this).inflate(R.layout.view_weekday_item, parent, false);
				holder = new ViewHolder();
				holder.iv_line = (ImageView) convertView.findViewById(R.id.iv_weekday_line);
				holder.tv_weekday = (TextView) convertView.findViewById(R.id.tv_weekday_date);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			if(position == clickPos){
				holder.tv_weekday.setTextColor(AlertListActicity.this.getResources().getColor(R.color.white));
				holder.iv_line.setVisibility(View.GONE);
				convertView.setBackgroundResource(R.drawable.alerts_focus_middle);
			}else{
				holder.tv_weekday.setTextColor(AlertListActicity.this.getResources().getColor(R.color.onair_btn_unfocus));
				if(position == clickPos+1)
					holder.iv_line.setVisibility(View.GONE);
				else
					holder.iv_line.setVisibility(View.VISIBLE);
				convertView.setBackgroundDrawable(null);
			}
			holder.tv_weekday.setText(weeks.get(position).getWeekday());
			convertView.setLayoutParams(new AbsListView.LayoutParams(itemwith, LayoutParams.MATCH_PARENT));
			return convertView;
		}
		
		class ViewHolder{
			ImageView iv_line;
			TextView tv_weekday;
		}
	}
}
