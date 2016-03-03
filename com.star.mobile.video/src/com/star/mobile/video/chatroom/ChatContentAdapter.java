package com.star.mobile.video.chatroom;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.plus.model.people.Person;
import com.star.cms.model.Chart;
import com.star.cms.model.ChatRoom;
import com.star.cms.model.User;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.activity.BrowserActivity;
import com.star.mobile.video.activity.ChatActivity;
import com.star.mobile.video.activity.PreviewImageActivity;
import com.star.mobile.video.model.ChatVO;
import com.star.mobile.video.model.LinkPkg;
import com.star.mobile.video.tenb.TenbActivity;
import com.star.mobile.video.util.BitmapUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.DateFormat;
import com.star.mobile.video.util.DensityUtil;
import com.star.mobile.video.util.ExpressionUtil;
import com.star.mobile.video.util.LinkUtil;
import com.star.mobile.video.view.ChatPopWindow;
import com.star.ui.ImageView.Finisher;
import com.star.util.Logger;
import com.star.util.loader.ImageLoader;

public class ChatContentAdapter extends BaseAdapter {

	private static final String TAG = ChatContentAdapter.class.getName();

	private Context context;
	private List<ChatVO> chats;
	private User mUser;
	private String regExpres = "f0[0-9]{2}|f10[0-7]";
	private Bitmap coverBitmap;
	private ClipboardManager cmb;
	private android.text.ClipboardManager cmb_low;
	private ChatPopWindow popupWindow;
	private View onLClickView;

	private List<Long> leaderIds;
	private List<Long> masterIds;

	private int lv_top;

	private View listview;

	private ChatRoom chatroom;

	public ChatContentAdapter(Context c, List<ChatVO> chats, ChatRoom chatroom) {
		this.context = c;
		this.chats = chats;
		this.chatroom = chatroom;
		mUser = StarApplication.mUser;
//		coverBitmap = BitmapFactory.decodeResource(c.getResources(),R.drawable.recommended_picture_frame);

		leaderIds = new ArrayList<Long>();
		if (chatroom != null && chatroom.getUserIds() != null) {
			String[] ids = chatroom.getUserIds().split(",");
			for (String id : ids) {
				try {
					leaderIds.add(Long.parseLong(id));
				} catch (Exception e) {
				}
			}
		}
		masterIds = new ArrayList<Long>();
		if (chatroom != null && chatroom.getMasterIds() != null){
			String[] ids = chatroom.getMasterIds().split(",");
			for (String id : ids) {
				try {
					masterIds.add(Long.parseLong(id));
				} catch (Exception e) {
				}
			}
		}
		if (isLow())
			cmb_low = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		else
			cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		initPopuptWindow();
	}

	private boolean isLow() {
		if (Build.VERSION.SDK_INT > 11)
			return false;
		else
			return true;
	}

	public void updateDateRefreshUi(List<ChatVO> chats) {
		this.chats = chats;
		notifyDataSetChanged();
	}

	public void setListView(ListView listview) {
		this.listview = listview;
	}

	private int getTop() {
		if (lv_top != 0)
			return lv_top;
		int[] listL = new int[2];
		listview.getLocationOnScreen(listL);
		lv_top = listL[1];
		return lv_top;
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
		final ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();
			view = findView(holder);
			initView(holder);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		holder.leaderIcon_t.setVisibility(View.GONE);
		holder.leaderIcon_f.setVisibility(View.GONE);
		holder.userIcon_t.setImageResource(R.drawable.no_portrait);
		holder.userIcon_f.setImageResource(R.drawable.no_portrait);
		ChatVO chat = chats.get(position);
		if (mUser != null && mUser.getId().equals(chat.getUserId())) {
			holder.chatContainer_f.setVisibility(View.GONE);
			holder.chatContainer_t.setVisibility(View.VISIBLE);
			if(mUser.getNickName()!=null)
				holder.userName_t.setText(mUser.getNickName());
			else
				holder.userName_t.setText(chat.getUserName());
			try {
				if (TextUtils.isEmpty(mUser.getHead())) {
					holder.userIcon_t.setUrl(chat.getIcoURL());
				} else {
					holder.userIcon_t.setUrl(mUser.getHead());
				}
			} catch (Exception e) {
			}
			addLeaderMark(holder.leaderIcon_t, chat, holder.masterIcon_t);
			addMasterMark(holder.masterIcon_t, chat, holder.leaderIcon_t, holder.chatContent_t, R.drawable.chat_kanu_bg_t,R.drawable.chat_user_bg);
			formatContent(position, holder.chatContent_t, holder.chatImage_t, holder.linkGroup_t, chat);
			setChatStatus(chat, holder.chatStatus_t);
		} else {
			holder.chatContainer_t.setVisibility(View.GONE);
			holder.chatContainer_f.setVisibility(View.VISIBLE);
			holder.userName_f.setText(chat.getUserName());
			try {
				holder.userIcon_f.setUrl(chat.getIcoURL());
			} catch (Exception e) {
				Log.e(TAG, "get chatimge-f error", e);
			}
			addLeaderMark(holder.leaderIcon_f, chat,holder.masterIcon_t);
			Logger.e("position=" + position);
			addMasterMark(holder.masterIcon_f, chat, holder.leaderIcon_f, holder.chatContent_f, R.drawable.chat_kanu_bg_f,R.drawable.chat_others_bg);
			formatContent(position, holder.chatContent_f, holder.chatImage_f, holder.linkGroup_f, chat);
		}
		Linkify.addLinks(holder.chatContent_f, Patterns.WEB_URL,Constant.SCHEAM + "?" + Constant.UID + "=");
		Linkify.addLinks(holder.chatContent_t, Patterns.WEB_URL,Constant.SCHEAM + "?" + Constant.UID + "=");
		formatTime(position, holder, chat);
		setHeadIconOnClick(holder,chat.getIcoURL(),chat.getUserName(),chat.getUserId());
		return view;
	}

	private void setHeadIconOnClick(ViewHolder holder,final String headIcon,final String nickName,final Long userId) {
		holder.userIcon_f.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context,TenbActivity.class);
				intent.putExtra("nickname", nickName);
				intent.putExtra("headurl", headIcon);
				intent.putExtra("userId", userId);
				CommonUtil.startActivity((ChatActivity)context, intent);
			}
		});
		holder.userIcon_t.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context,TenbActivity.class);
				intent.putExtra("nickname", nickName);
				intent.putExtra("headurl", headIcon);
				intent.putExtra("userId", userId);
				CommonUtil.startActivity((ChatActivity)context, intent);
			}
		});
	}
	
	private void setChatStatus(ChatVO chat, RelativeLayout layout) {
		if (chat.getStatus() == ChatVO.STATUS_VALID) {
			layout.setVisibility(View.INVISIBLE);
		} else {
			layout.setVisibility(View.VISIBLE);
			View sendV = layout.findViewById(R.id.pb_t);
			View invalidV = layout.findViewById(R.id.iv_invalid_t);
			if(sendV==null||invalidV==null)
				return;
			if (chat.getStatus() == ChatVO.STATUS_INVALID) {
				sendV.setVisibility(View.INVISIBLE);
				invalidV.setVisibility(View.VISIBLE);
			} else if (chat.getStatus() == ChatVO.STATUS_SENDING) {
				sendV.setVisibility(View.VISIBLE);
				invalidV.setVisibility(View.INVISIBLE);
			} else {
				layout.setVisibility(View.INVISIBLE);
			}
		}
	}

	private void initView(final ViewHolder holder) {
		// holder.chatImage_f.setBackgroundResource(R.drawable.corner_bg);
		// holder.chatImage_t.setBackgroundResource(R.drawable.corner_bg);
		holder.chatImage_f.setImageResource(R.drawable.nopicture_bg);
		holder.chatImage_t.setImageResource(R.drawable.nopicture_bg);

		holder.userIcon_f.setFinisher(new Finisher() {
			@Override
			public void run() {
				holder.userIcon_f.setImageBitmap(BitmapUtil.toRoundBitmap(holder.userIcon_f.getImage()));
			}
		});
		holder.userIcon_t.setFinisher(new Finisher() {
			@Override
			public void run() {
				holder.userIcon_t.setImageBitmap(BitmapUtil.toRoundBitmap(holder.userIcon_t.getImage()));
			}
		});
		holder.chatContent_f.setOnLongClickListener(onLongListener);
		holder.chatContent_t.setOnLongClickListener(onLongListener);
		holder.linkGroup_f.setOnLongClickListener(onLongListener);
		holder.linkGroup_t.setOnLongClickListener(onLongListener);
		holder.linkGroup_f.setOnClickListener(onLinkClickListener);
		holder.linkGroup_t.setOnClickListener(onLinkClickListener);
		// holder.chatImage_t.setFinisher(new Finisher() {
		// @Override
		// public void run() {
		// holder.chatImage_t.setImageBitmap(BitmapUtil.toConformBitmap(holder.chatImage_t.getImage(),
		// coverBitmap));
		// }
		// });
		// holder.chatImage_f.setFinisher(new Finisher() {
		// @Override
		// public void run() {
		// holder.chatImage_f.setImageBitmap(BitmapUtil.toConformBitmap(holder.chatImage_f.getImage(),
		// coverBitmap));
		// }
		// });
	}

	private View findView(final ViewHolder holder) {
		View view = LayoutInflater.from(context).inflate(R.layout.view_chat_item, null);
		holder.chatContainer_f = (RelativeLayout) view.findViewById(R.id.chart_from_container);
		holder.chatContainer_t = (RelativeLayout) view.findViewById(R.id.chart_to_container);
		holder.userIcon_f = (com.star.ui.ImageView) view.findViewById(R.id.iv_user_icon_f);
		holder.userIcon_t = (com.star.ui.ImageView) view.findViewById(R.id.iv_user_icon_t);
		holder.chatImage_f = (com.star.ui.ImageView) view.findViewById(R.id.iv_chat_image_f);
		holder.chatImage_t = (com.star.ui.ImageView) view.findViewById(R.id.iv_chat_image_t);
		holder.chatContent_f = (TextView) view.findViewById(R.id.tv_chat_content_f);
		holder.chatContent_t = (TextView) view.findViewById(R.id.tv_chat_content_t);
		holder.userName_f = (TextView) view.findViewById(R.id.tv_user_name_f);
		holder.userName_t = (TextView) view.findViewById(R.id.tv_user_name_t);
		holder.chatTime_f = (TextView) view.findViewById(R.id.tv_chat_time_f);
		holder.chatTime_t = (TextView) view.findViewById(R.id.tv_chat_time_t);
		holder.leaderIcon_f = (ImageView) view.findViewById(R.id.iv_leader_f);
		holder.leaderIcon_t = (ImageView) view.findViewById(R.id.iv_leader_t);
		holder.masterIcon_f = (ImageView) view.findViewById(R.id.iv_leader_f_kanu);
		holder.masterIcon_t = (ImageView) view.findViewById(R.id.iv_leader_t_kanu);
		holder.linkGroup_f = (RelativeLayout) view.findViewById(R.id.rl_chat_link_f);
		holder.linkGroup_t = (RelativeLayout) view.findViewById(R.id.rl_chat_link_t);

		holder.chatStatus_t = (RelativeLayout) view.findViewById(R.id.rl_right_status);
		return view;
	}

	private void formatContent(int position, final TextView contentView,
			final com.star.ui.ImageView imageView,
			final RelativeLayout linkGroup, ChatVO chat) {
		switch (chat.getType()) {
		case Chart.TYPE_TEXT:
			contentView.setTag(chat);
			setText(contentView, imageView, linkGroup, chat);
			break;
		case Chart.TYPE_IMAGE:
			setImage(position, contentView, imageView, linkGroup, chat);
			break;
		case Chart.TYPE_LINK:
			linkGroup.setTag(chat);
			setLink(contentView, imageView, linkGroup, chat);
			break;
		default:
			if (chat.getImageURL() != null) {
				setImage(position, contentView, imageView, linkGroup, chat);
			} else {
				setText(contentView, imageView, linkGroup, chat);
			}
			break;
		}
	}

	private void setLink(TextView contentView, com.star.ui.ImageView imageView,	RelativeLayout linkGroup, Chart chat) {
		contentView.setVisibility(View.GONE);
		imageView.setVisibility(View.GONE);
		linkGroup.setVisibility(View.VISIBLE);
		com.star.ui.ImageView icon = null;
		TextView title = null;
		TextView desc = null;
		if (mUser != null && mUser.getId().equals(chat.getUserId())) {
			icon = (com.star.ui.ImageView) linkGroup.findViewById(R.id.iv_link_icon_t);
			title = (TextView) linkGroup.findViewById(R.id.tv_link_title_t);
			desc = (TextView) linkGroup.findViewById(R.id.tv_link_desc_t);
		} else {
			icon = (com.star.ui.ImageView) linkGroup.findViewById(R.id.iv_link_icon_f);
			title = (TextView) linkGroup.findViewById(R.id.tv_link_title_f);
			desc = (TextView) linkGroup.findViewById(R.id.tv_link_desc_f);
		}
		LinkPkg linkP = LinkUtil.processLink_(chat.getMsg());
		if (icon != null) {
			icon.setImageResource(R.drawable.link_picture_chat_grey);
			try {
				icon.setUrl(linkP.getImgurl());
			} catch (Exception e) {
			}
		}
		if (title != null) {
			if (linkP.getTitle() == null)
				title.setVisibility(View.GONE);
			else
				title.setText(linkP.getTitle());
		}
		if (desc != null)
			desc.setText(linkP.getDescription() == null ? linkP.getUrl(): linkP.getDescription());
	}

	private void setImage(int position, final TextView contentView,	final com.star.ui.ImageView imageView,	final RelativeLayout linkGroup, Chart chat) {
		try {
			contentView.setVisibility(View.GONE);
			linkGroup.setVisibility(View.GONE);
			String imageUrl = chat.getImageURL();
			if(imageUrl!=null && imageUrl.startsWith("tenbre://")){
				ImageLoader.getInstance(3,ImageLoader.Type.LIFO).loadImage(imageUrl.substring(9), imageView);
			}else{
				imageView.setUrl(imageUrl);
			}
			imageView.setVisibility(View.VISIBLE);
			imageView.setOnClickListener(new OnClick(position));
		} catch (Exception e) {
		}
	}

	private void setText(final TextView contentView,final com.star.ui.ImageView imageView,final RelativeLayout linkGroup, Chart chat) {
		imageView.setVisibility(View.GONE);
		linkGroup.setVisibility(View.GONE);
		SpannableString spannableString = ExpressionUtil.getSpannableString(context, chat.getMsg(), regExpres);
		contentView.setText(spannableString);
		contentView.setVisibility(View.VISIBLE);
	}

	private void addLeaderMark(final ImageView view, Chart chat,ImageView masterImage) {
		for (Long id : leaderIds) {
			if (chat.getUserId()!=null && chat.getUserId().equals(id)) {
				view.setVisibility(View.VISIBLE);
				masterImage.setVisibility(View.GONE);
				break;
			}
		}

	}

	private void addMasterMark(final ImageView view, Chart chat,ImageView leaderImage,TextView masterContentBg,int materContentBgRes,int leadContentBgRes){
		for(Long id : masterIds) {
			if (chat.getUserId()!=null && chat.getUserId().equals(id)) {
				view.setVisibility(View.VISIBLE);
				leaderImage.setVisibility(View.GONE);
				masterContentBg.setBackgroundResource(materContentBgRes);
				masterContentBg.setTextColor(context.getResources().getColor(R.color.white));
				break;
			}else{
				view.setVisibility(View.GONE);
				masterContentBg.setBackgroundResource(leadContentBgRes);
				masterContentBg.setTextColor(context.getResources().getColor(R.color.black_color));
			}
		}
	}

	private void formatTime(int position, final ViewHolder holder, Chart chat) {
		String dateSection = DateFormat.formatTime(chat.getCreateDate());
		String date = dateSection;
//		if (!DateFormat.formatMonth(chat.getCreateDate()).equals(
//				DateFormat.formatMonth(new Date()))) {
//		}
		date = DateFormat.formatTuesday(chat.getCreateDate()) + " "	+ dateSection;
		holder.chatTime_f.setText(date);
		holder.chatTime_t.setText(date);
	}

	OnLongClickListener onLongListener = new OnLongClickListener() {

		@Override
		public boolean onLongClick(View v) {
			onLClickView = v;
			switch (v.getId()) {
			case R.id.tv_chat_content_f:
				v.setBackgroundResource(R.drawable.chat_bg_from_copy);
				break;
			case R.id.tv_chat_content_t:
				v.setBackgroundResource(R.drawable.chat_bg_to_copy);
				break;
			case R.id.rl_chat_link_f:
				v.setBackgroundResource(R.drawable.chat_bg_from_copy);
				break;
			case R.id.rl_chat_link_t:
				v.setBackgroundResource(R.drawable.link_pressbg_user_greenline);
				break;
			}
			v.setPadding(DensityUtil.dip2px(context, 10),DensityUtil.dip2px(context, 12),DensityUtil.dip2px(context, 10),DensityUtil.dip2px(context, 10));

			Chart chat = (Chart) v.getTag();
			if (chat == null)
				return true;
			int[] location = new int[2];
			v.getLocationOnScreen(location);
			if (location[1] - popupWindow.getPopHeight() < getTop()) {
				popupWindow.showVDown(v, chat);
			} else {
				popupWindow.showVAbove(v, chat);
			}
			return true;
		}
	};

	OnClickListener onLinkClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Chart chat = (Chart) v.getTag();
			if (chat != null && chat.getMsg() != null) {
				LinkPkg link_ = LinkUtil.processLink_(chat.getMsg());
				if (link_ != null) {
					Intent intent = new Intent(context, BrowserActivity.class);
					intent.putExtra("loadUrl", link_.getUrl());
					CommonUtil.startActivity((Activity) context, intent);
				}
			}
		}
	};

	public int getPositionForDateSection(String dateSection) {
		for (int i = 0; i < getCount(); i++) {
			if (dateSection.equals(DateFormat.formatTime(chats.get(i).getCreateDate()))) {
				return i;
			}
		}
		return -1;
	}

	class ViewHolder {
		public com.star.ui.ImageView userIcon_f;
		public com.star.ui.ImageView userIcon_t;
		public com.star.ui.ImageView chatImage_f;
		public com.star.ui.ImageView chatImage_t;
		public TextView userName_f;
		public TextView chatContent_f;
		public TextView userName_t;
		public TextView chatContent_t;
		public TextView chatTime_f;
		public TextView chatTime_t;

		public RelativeLayout chatContainer_f;
		public RelativeLayout chatContainer_t;

		public ImageView leaderIcon_f;
		public ImageView leaderIcon_t;
		public ImageView masterIcon_f;
		public ImageView masterIcon_t;
		public RelativeLayout linkGroup_f;
		public RelativeLayout linkGroup_t;

		public RelativeLayout chatStatus_t;
	}

	class OnClick implements OnClickListener {

		private int postion;

		public OnClick(int postion) {
			this.postion = postion;
		}

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(context, PreviewImageActivity.class);
			intent.putExtra("imageUrl",	((Chart) getItem(postion)).getImageURL());
			context.startActivity(intent);
		}
	}

	protected void initPopuptWindow() {
		popupWindow = new ChatPopWindow(context);
		popupWindow.setCopyOnclickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (onLClickView != null && onLClickView instanceof TextView) {
					TextView copyText = (TextView) onLClickView;
					if (isLow())
						cmb_low.setText(copyText.getText().toString().trim());
					else
						cmb.setPrimaryClip(ClipData.newPlainText("Content",
								copyText.getText().toString().trim()));
				}
				popupWindow.dismiss();
			}
		});
		popupWindow.setForwardOnclickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (onLClickView != null) {
					Chart chat = (Chart) onLClickView.getTag();
					if (chat != null) {
						Intent intent = new Intent(context,
								ShareChatRoomActivity.class);
						int type = chat.getType();
						intent.putExtra("type", type);
						if (type == Chart.TYPE_TEXT)
							intent.putExtra("msg", chat.getMsg());
						else if (type == Chart.TYPE_LINK)
							intent.putExtra("linkpkg",LinkUtil.processLink_(chat.getMsg()));
						context.startActivity(intent);
					}
				}
				popupWindow.dismiss();
			}
		});
		popupWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				if (onLClickView != null) {
					switch (onLClickView.getId()) {
					case R.id.tv_chat_content_f:
						onLClickView.setBackgroundResource(R.drawable.chat_bg_from);
						break;
					case R.id.tv_chat_content_t:
						onLClickView.setBackgroundResource(R.drawable.chat_bg_to);
						break;
					case R.id.rl_chat_link_f:
						onLClickView.setBackgroundResource(R.drawable.chat_bg_from);
						break;
					case R.id.rl_chat_link_t:
						onLClickView.setBackgroundResource(R.drawable.link_bg_user_greenline);
						break;
					}
					onLClickView.setPadding(DensityUtil.dip2px(context, 10),DensityUtil.dip2px(context, 12),DensityUtil.dip2px(context, 10),DensityUtil.dip2px(context, 10));
					onLClickView = null;
				}
			}
		});
	}
}
