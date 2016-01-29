package com.star.mobile.video.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.star.cms.model.Chart;
import com.star.cms.model.User;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.model.ChatVO;
import com.star.mobile.video.util.ExpressionUtil;

public class VideoLiveChatContentAdapter extends BaseAdapter {

	private static final String TAG = VideoLiveChatContentAdapter.class.getName();

	private Context context;
	private List<ChatVO> chats;
	private User mUser;
	private String regExpres = "f0[0-9]{2}|f10[0-7]";

	public VideoLiveChatContentAdapter(Context c, List<ChatVO> chats) {
		this.context = c;
		this.chats = chats;
		mUser = StarApplication.mUser;
	}

	public void updateDateRefreshUi(List<ChatVO> chats) {
		this.chats = chats;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return chats.size();
	}

	@Override
	public Object getItem(int position) {
		return chats.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder holder;
		if(view == null){
			holder = new ViewHolder();
			view = LayoutInflater.from(context).inflate(R.layout.view_video_chat_item, null);
			holder.chatContent = (TextView) view.findViewById(R.id.tv_chatContent);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		Chart chat = chats.get(position);
		String msg = chat.getUserName()+": "+chat.getMsg();
		SpannableString spannableString = ExpressionUtil.getSpannableString(context, msg, regExpres);
		SpannableStringBuilder style = new SpannableStringBuilder(spannableString);
		
		if(mUser!=null&&mUser.getId().equals(chat.getUserId()))
			style.setSpan(new ForegroundColorSpan(Color.parseColor("#3253B9")), 0, chat.getUserName().length()+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		else
			style.setSpan(new ForegroundColorSpan(Color.parseColor("#999999")), 0, chat.getUserName().length()+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		style.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")), chat.getUserName().length()+1, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		holder.chatContent.setText(style);
		return view;
	}
	
	private class ViewHolder {
		private TextView chatContent;
	}
}