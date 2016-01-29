package com.star.mobile.video.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.star.mobile.video.AlertManager;
import com.star.mobile.video.R;
import com.star.mobile.video.model.AlertSettingItem;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.RingtoneUtil;

public class AlertSettingItemView extends LinearLayout implements
		OnClickListener, OnItemClickListener{

	private ImageView item_arrow;
	private AlertSettingItem settingItem;
	private TextView item_content;
	private LinearLayout ll_child_layout;
	private SharedPreferences mSharePre;
	private AlertChildAdapter alertAdapter;
	private Context context;
	
	public AlertSettingItemView(Context context, AlertSettingItem settingItem) {
		super(context);
		this.context = context;
		this.settingItem = settingItem;
		mSharePre = SharedPreferencesUtil.getAlertSharePreferences(context);
		
		LayoutInflater.from(context).inflate(R.layout.view_alertsetting_item, this);
		ImageView item_icon = (ImageView) findViewById(R.id.iv_alert_item_icon);
		item_content = (TextView) findViewById(R.id.tv_alert_item_content);
		TextView item_content_l = (TextView) findViewById(R.id.tv_alert_item_content_l);
		TextView item_content_r = (TextView) findViewById(R.id.tv_alert_item_content_r);
		item_arrow = (ImageView) findViewById(R.id.iv_alert_item_arrow);
		
		ll_child_layout = (LinearLayout) findViewById(R.id.ll_alert_child_layout);
		NoScrollGridView child_list = (NoScrollGridView) findViewById(R.id.lv_alert_child_list);
		alertAdapter = new AlertChildAdapter();
		child_list.setAdapter(alertAdapter);
		child_list.setOnItemClickListener(this);
		if (settingItem.getItemContent().endsWith("min")) {
			item_content_l.setVisibility(View.VISIBLE);
			item_content_r.setVisibility(View.VISIBLE);
			item_content.setTextColor(context.getResources().getColor(R.color.textcolor_orange));
		}
		item_icon.setImageDrawable(settingItem.getDrawable());
		setTimeText(settingItem.getItemContent());
		setChildListOpen(false);
		
		this.setOnClickListener(this);
	}
	
	private void setTimeText(String time){
		item_content.setText(" "+time+" ");
	}
	
	public void setChildListOpen(boolean isOpen){
		if(isOpen){
			ll_child_layout.setVisibility(View.VISIBLE);
			item_arrow.setImageDrawable(getContext().getResources().getDrawable(R.drawable.icon_arrow_up));
		}else{
			ll_child_layout.setVisibility(View.GONE);
			item_arrow.setImageDrawable(getContext().getResources().getDrawable(R.drawable.icon_arrow_down));
			if(settingItem.getItemId() == Constant.RINGTONE_ID){
				RingtoneUtil.getInstance(getContext()).stopRingtone();
			}
		}
	}

	class AlertChildAdapter extends BaseAdapter {

		private String[] childItems;
		private int clickPos;
		public AlertChildAdapter() {
			this.childItems = settingItem.getChildItems();
			clickPos = settingItem.getSelectPos();
		}
		
		public void setClickPos(int clickPos){
			this.clickPos = clickPos;
			notifyDataSetChanged();
		}
		
		@Override
		public int getCount() {
			return childItems.length;
		}

		@Override
		public Object getItem(int position) {
			return childItems[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder_C holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.view_alertsetting_child_item, null);
				holder = new ViewHolder_C();
				holder.item_icon = (ImageView) convertView.findViewById(R.id.iv_alert_child_select);
				holder.item_content = (TextView) convertView.findViewById(R.id.iv_alert_child_content);
				holder.item_line = (ImageView) convertView.findViewById(R.id.v_bottom_line);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder_C) convertView.getTag();
			}
			if(position == 0){
				holder.item_line.setVisibility(View.GONE);
			}else{
				holder.item_line.setVisibility(View.VISIBLE);
			}
			if(position == clickPos){
				holder.item_icon.setVisibility(View.VISIBLE);
				holder.item_content.setTextColor(getContext().getResources().getColor(R.color.textcolor_orange));
			}else{
				holder.item_icon.setVisibility(View.INVISIBLE);
				holder.item_content.setTextColor(getContext().getResources().getColor(R.color.alert_setting_text));
			}
			holder.item_content.setText(childItems[position]);
			return convertView;
		}
		
		class ViewHolder_C {
			public ImageView item_icon;
			public TextView item_content;
			public ImageView item_line;
		}
	}

	@Override
	public void onClick(View v) {
		if(ll_child_layout.getVisibility() == View.VISIBLE)
			setChildListOpen(false);
		else
			setChildListOpen(true);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		alertAdapter.setClickPos(position);
		if(settingItem.getItemId() == Constant.RINGTONE_ID){
			mSharePre.edit().putInt(Constant.RINGTONE_POS, position).commit();
			RingtoneUtil.getInstance(getContext()).playRingtone(position, true);
		}else if(settingItem.getItemId() == Constant.REMIND_TIME_ID){
			String childContent = settingItem.getChildItems()[position];
			setTimeText(childContent);
			mSharePre.edit().putInt(Constant.REMIND_TIME_POS, position).commit();
			AlertManager.getInstance(context).setAlertTime(position);
		}
	}
}
