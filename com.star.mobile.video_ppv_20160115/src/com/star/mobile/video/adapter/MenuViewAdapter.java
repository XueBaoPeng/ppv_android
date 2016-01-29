package com.star.mobile.video.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.star.mobile.video.AlertManager;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.model.FunctionType;
import com.star.mobile.video.model.MenuItemRes;
import com.star.mobile.video.service.FeedbackService;
import com.star.mobile.video.service.FunctionService;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.shared.TaskSharedUtil;

public class MenuViewAdapter extends BaseAdapter {

	private List<MenuItemRes> items;
	private Context context;
	private int clickPos = -1;
	private FeedbackService fbService;

	public MenuViewAdapter(Context context, List<MenuItemRes> items) {
		this.items = items;
		this.context = context;
		fbService = FeedbackService.getInstance(context);
	}
	
	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}
	
	public void setOnClickPos(int clickPos){
		this.clickPos = clickPos;
		notifyDataSetChanged();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.view_menu_item, parent, false);
			holder = new ViewHolder();
			holder.item_name = (TextView) convertView.findViewById(R.id.tv_menu_name);
			holder.item_icon = (ImageView) convertView.findViewById(R.id.iv_menu_icon);
			holder.item_select = (ImageView) convertView.findViewById(R.id.iv_menu_select);
			holder.item_football = (ImageView) convertView.findViewById(R.id.iv_menu_football);
			holder.item_line = convertView.findViewById(R.id.v_bottom_line);
			holder.item_bottom = convertView.findViewById(R.id.v_bottom_bg);
			holder.menu_reminder = (ImageView) convertView.findViewById(R.id.iv_menu_reminder);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.menu_reminder.setVisibility(View.INVISIBLE);
		String itemName = items.get(position).getItemTitle();
		holder.item_name.setText(itemName);
		holder.item_icon.setImageDrawable(context.getResources().getDrawable(items.get(position).getUnfocusRes()));
		if(position == clickPos){
			holder.item_line.setVisibility(View.INVISIBLE);
			convertView.setBackgroundResource(R.drawable.menu_focus_bg);
			holder.item_name.setTextAppearance(context, R.style.menu_text_focus_style);
			holder.item_select.setImageResource(R.drawable.menu_arrow_focus);
			holder.item_icon.setImageResource(items.get(position).getFocusRes());
			holder.item_name.setSelected(true);
		}else{
			if(position == clickPos-1 || position == getCount()-1){
				holder.item_line.setVisibility(View.INVISIBLE);
			}else{
				holder.item_line.setVisibility(View.VISIBLE);
			}
			convertView.setBackgroundDrawable(null);
			holder.item_name.setTextAppearance(context, R.style.menu_text_style);
			holder.item_select.setImageResource(R.drawable.menu_arrow);
			holder.item_icon.setImageResource(items.get(position).getUnfocusRes());
			holder.item_name.setSelected(false);
		}
		if(itemName.equals(context.getString(R.string.feedback)) && fbService.isDoFourLayer()){
			holder.menu_reminder.setVisibility(View.VISIBLE);
		}
		if(itemName.equals(context.getString(R.string.fragment_tag_ccount_manager)) && AlertManager.getInstance(context).isSmartCardCloseToStop()){
			holder.menu_reminder.setVisibility(View.VISIBLE);
		}
		if(itemName.equals(context.getString(R.string.fragment_tag_chatRooms)) && StarApplication.mChannelIdOfChatRoom.size()>0){
			holder.menu_reminder.setVisibility(View.VISIBLE);
		}
		if(itemName.equals(context.getString(R.string.setting))){
			if(SharedPreferencesUtil.isNewVersion(context)){
				holder.menu_reminder.setVisibility(View.VISIBLE);
			}
			if(!FunctionService.doHideFuncation(FunctionType.Invitation)) {
				if(!SharedPreferencesUtil.isSelInvitation(context, SharedPreferencesUtil.getUserName(context))) {
					holder.menu_reminder.setVisibility(View.VISIBLE);
				}
			}
		}
		if (itemName.equals(context.getString(R.string.fragment_tag_video))) {
			holder.item_football.setVisibility(View.VISIBLE);
		}else {
			holder.item_football.setVisibility(View.GONE);
		}
		if(itemName.equals(context.getString(R.string.fragment_tag_video)) && SharedPreferencesUtil.isChannelHasNewVideo(context)){
			holder.menu_reminder.setVisibility(View.VISIBLE);
		}
		if(itemName.equals(context.getString(R.string.fragment_tag_Me))){
			if(TaskSharedUtil.getCouponsStatus(context) || TaskSharedUtil.getCoinsStatus(context)){
				holder.menu_reminder.setVisibility(View.VISIBLE);
			}
		}
		if(itemName.equals(context.getString(R.string.activity_forum_title))){
			if(SharedPreferencesUtil.hasNewTopic(context)){
				holder.menu_reminder.setVisibility(View.VISIBLE);
			}
		}
		if(itemName.equals(context.getString(R.string.fragment_tag_channelGuide)) 
				|| itemName.equals(context.getString(R.string.feedback))
				|| itemName.equals(context.getString(R.string.fragment_tag_video))
				|| itemName.equals(context.getString(R.string.fragment_tag_Home))
				|| itemName.equals(context.getString(R.string.fragment_tag_chatRooms))
				|| itemName.equals(context.getString(R.string.activity_forum_title))
				|| itemName.equals(context.getString(R.string.fragment_tag_ccount_manager))){
			holder.item_bottom.setVisibility(View.VISIBLE);
			holder.item_line.setVisibility(View.INVISIBLE);
		}else{
			holder.item_bottom.setVisibility(View.GONE);
		}
		return convertView;
	}
	
	class ViewHolder{
		ImageView menu_reminder;
		ImageView item_football;
		TextView item_name;
		ImageView item_icon;
		ImageView item_select;
		View item_line;
		View item_bottom;
	}
}
