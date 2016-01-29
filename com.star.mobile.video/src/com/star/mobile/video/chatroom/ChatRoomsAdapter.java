package com.star.mobile.video.chatroom;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.star.cms.model.ChatRoom;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.activity.ChatActivity;
import com.star.mobile.video.chatroom.faq.RobotCustomerChatActivity;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.DensityUtil;

public class ChatRoomsAdapter extends BaseAdapter {
	private static String FAQ_CHAT = "104";
	private List<ChatRoom> chatrooms;
	private Context context;
	private int currentPos = -1;
	private int logoWidth;
	private ChatService chatService;
	public ChatRoomsAdapter(Context context, List<ChatRoom> chatrooms){
		this.context = context;
		this.chatrooms = chatrooms;
		this.chatService = new ChatService(context);
		this.logoWidth = (Constant.WINDOW_WIDTH - 2*DensityUtil.dip2px(context, 6) - 3*DensityUtil.dip2px(context, 3))/4;
	}
	
	public void updateDataAndRefreshUI(List<ChatRoom> chatrooms){
		this.chatrooms = chatrooms;
		notifyDataSetChanged();
	}
	
	public void setSelection(int position){
		this.currentPos = position;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return chatrooms.size();
	}

	@Override
	public Object getItem(int position) {
		return chatrooms.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(chatrooms.size()==0)
			return null;
		final ViewHolder holder;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.view_chatroom_item, null);
			holder = new ViewHolder();
			holder.iv_chn_icon = (com.star.ui.ImageView) convertView.findViewById(R.id.iv_chatroom_icon);
			holder.iv_chn_icon_cov = (ImageView) convertView.findViewById(R.id.iv_chatroom_icon_cover);
			holder.tv_chat_count = (TextView) convertView.findViewById(R.id.tv_chat_count);
			holder.iv_room_hot = (ImageView) convertView.findViewById(R.id.iv_chatroom_hot);
			
			holder.iv_chn_icon.setLayoutParams(new RelativeLayout.LayoutParams(logoWidth, logoWidth));
			holder.iv_chn_icon_cov.setLayoutParams(new RelativeLayout.LayoutParams(logoWidth, logoWidth));
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		final ChatRoom room = chatrooms.get(position);
		if(position == currentPos){
//			holder.iv_chn_icon_cov.setBackgroundResource(R.drawable.channel_focus_bg);
		}else{
//			holder.iv_chn_icon_cov.setBackgroundResource(R.drawable.channel_detail_bg);
		}
		holder.iv_chn_icon.setImageResource(R.drawable.channel_detail_bg);
		try {
			holder.iv_chn_icon.setUrl(room.getLogo());
		} catch (Exception e) {
		}
//		holder.iv_room_hot.setVisibility(View.GONE);
//		if(position==0){
//			holder.iv_room_hot.setImageResource(R.drawable.no_1);
//			holder.iv_room_hot.setVisibility(View.VISIBLE);
//		}else if(position == 1){
//			holder.iv_room_hot.setImageResource(R.drawable.no_2);
//			holder.iv_room_hot.setVisibility(View.VISIBLE);
//		}else if(position == 2){
//			holder.iv_room_hot.setImageResource(R.drawable.no_3);
//			holder.iv_room_hot.setVisibility(View.VISIBLE);
//		}
		holder.tv_chat_count.setVisibility(View.GONE);
		int count = chatService.getNewChatCount(room);
		if(count>0){
			holder.tv_chat_count.setText(String.valueOf(count>99?"99+":count));
			holder.tv_chat_count.setVisibility(View.VISIBLE);
		}
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(context, ChatActivity.class);
				if(ChatRoom.ROBOT_CASH_CODE.equals(room.getCode())) {
					room.setCashId(room.getFaqCashId(StarApplication.mUser.getId()));
				}
				intent.putExtra("chatroom", room);
				CommonUtil.startActivity((Activity)context, intent);
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						holder.tv_chat_count.setVisibility(View.GONE);
					}
				}, 1000);
			}
		});
		return convertView;
	}

	class ViewHolder{
		com.star.ui.ImageView iv_chn_icon;
		ImageView iv_chn_icon_cov;
		ImageView iv_room_hot;
		TextView tv_chat_count;
	}
}
