package com.star.mobile.video.adapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.star.cms.model.vo.ProgramVO;
import com.star.mobile.video.R;
import com.star.mobile.video.view.BaseEpgItemView.OnItemCallBackListener;
import com.star.mobile.video.view.EpgChnGuideItemView;

public class EpgListAdapter extends BaseAdapter {

	private List<ProgramVO> epgs;
	private Context context;
	private SimpleDateFormat simpleDateFormat;
	private String[] weeks;
	private String[] months;
	private OnItemCallBackListener mListener;

	public EpgListAdapter(Context context, List<ProgramVO> epgs) {
		this.context = context;
		simpleDateFormat = new SimpleDateFormat("dd");
		weeks = context.getResources().getStringArray(R.array.weekdays_f);
		months = context.getResources().getStringArray(R.array.months);
		updateEpgDataAndRefreshUI(epgs);
	}

	public void updateEpgDataAndRefreshUI(List<ProgramVO> epgs) {
		this.epgs = epgs;
		notifyDataSetChanged();
	}
	
	public void setOnItemCallBackListener(OnItemCallBackListener mListener) {
		this.mListener = mListener;
	}

	@Override
	public int getCount() {
		return epgs.size();
	}

	@Override
	public Object getItem(int position) {
		return epgs.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.view_epg_item_for_chnguid, parent, false);
			holder = new ViewHolder();
			holder.itemView = (EpgChnGuideItemView) convertView.findViewById(R.id.chnGuid_list_item);
			holder.rl_date = (RelativeLayout) convertView.findViewById(R.id.rl_date);
			holder.tv_week = (TextView) convertView.findViewById(R.id.tv_data_time);
			holder.tv_date = (TextView) convertView.findViewById(R.id.tv_data_date);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		Date date = epgs.get(position).getStartDate();
		String dateSection = simpleDateFormat.format(date);
		if(position==getPositionForDateSection(dateSection) && position!=0){
			holder.rl_date.setVisibility(View.VISIBLE);
			holder.tv_week.setText(getWeekStr(date));
			if(dateSection.startsWith("0"))
				dateSection = dateSection.substring(1);
			holder.tv_date.setText(getMonsStr(date)+" "+dateSection);
		} else {
			holder.rl_date.setVisibility(View.GONE);
		}
		holder.itemView.setOnItemCallBackListener(mListener);
		holder.itemView.fillProgramData(epgs.get(position));
		holder.itemView.fillChannel();
		return convertView;
	}
	
	class ViewHolder{
		EpgChnGuideItemView itemView;
		RelativeLayout rl_date;
		TextView tv_week;
		TextView tv_date;
	}
	
	public int getPositionForDateSection(String dateSection) {
		for (int i = 0; i < getCount(); i++) {
			if(dateSection.equals(simpleDateFormat.format(epgs.get(i).getStartDate()))){
				return i;
			}
		}
		return -1;
	}
	
	public String getWeekStr(Date date){
		Calendar calendar= Calendar.getInstance(); 
		calendar.setTime(date);
		int day = calendar.get(Calendar.DAY_OF_WEEK);
		return weeks[day-1];
	}
	
	public String getMonsStr(Date date){
		Calendar calendar= Calendar.getInstance(); 
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		return months[month];
	}
}
