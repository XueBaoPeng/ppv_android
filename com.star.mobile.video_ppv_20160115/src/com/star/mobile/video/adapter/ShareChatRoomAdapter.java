package com.star.mobile.video.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.star.cms.model.ChatRoom;
import com.star.mobile.video.R;
import com.star.mobile.video.activity.ChatActivity;
import com.star.mobile.video.model.LinkPkg;
import com.star.ui.ImageView;

public class ShareChatRoomAdapter extends BaseAdapter {
	
	private Context context;
	private List<ChatRoom> data;
	private String msg;
	private LinkPkg lp;
	private String imgUrl;
	private int type;
	
	public ShareChatRoomAdapter(Context context,List<ChatRoom> data) {
		this.context = context;
		this.data = data;
	}

	public void setForwardMsg(String msg) {
		this.msg = msg;
	}
	
	public void setLinkPkg(LinkPkg lp) {
		this.lp = lp;
	}
	
	public void setShareType(int type) {
		this.type = type;
	} 
	
	public void setImageUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	
	public void updateData(List<ChatRoom> data) {
		this.data = data;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder viewHolder;
		if(view == null) {
			view = LayoutInflater.from(context).inflate(R.layout.view_share_chatroom_item, null);
			viewHolder = new ViewHolder();
			viewHolder.ivChatRoomIcon = (ImageView) view.findViewById(R.id.iv_chatroom_icon);
			viewHolder.tvChatRoomName = (TextView) view.findViewById(R.id.tv_chatroom_name);
			viewHolder.v_line = view.findViewById(R.id.v_line);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)view.getTag();
		}
		final ChatRoom chatRoom = data.get(position);
		view.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(context, ChatActivity.class);
				intent.putExtra("chatroom", chatRoom);
				if(msg != null) {
					intent.putExtra("msg", msg);
				}
				if(lp != null) {
					intent.putExtra("linkpkg", lp);
				}
				if(imgUrl != null) {
					intent.putExtra("imgUrl", imgUrl);
				}
				intent.putExtra("type", type);
				intent.putExtra("byShare", true);
				((Activity)context).startActivity(intent);
				((Activity)context).finish();
			}
		});
		if(getCount() - 1 == position) {
			viewHolder.v_line.setVisibility(View.GONE);
		} else {
			viewHolder.v_line.setVisibility(View.VISIBLE);
		}
		if (chatRoom != null) {
			if (chatRoom.getLogo() != null && !chatRoom.getLogo().isEmpty()) {
				viewHolder.ivChatRoomIcon.setUrl(chatRoom.getLogo());
			}
			if (chatRoom.getName() != null && !chatRoom.getName().isEmpty()) {
				viewHolder.tvChatRoomName.setText(chatRoom.getName());
			}
		}
		return view;
	}

	class ViewHolder {
		public ImageView ivChatRoomIcon;
		public TextView tvChatRoomName;
		public View v_line;
	}
}
