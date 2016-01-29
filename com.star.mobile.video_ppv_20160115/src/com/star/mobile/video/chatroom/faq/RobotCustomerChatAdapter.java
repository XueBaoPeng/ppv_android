package com.star.mobile.video.chatroom.faq;

import java.util.Date;
import java.util.List;

import com.star.cms.model.Chart;
import com.star.cms.model.User;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.activity.BrowserActivity;
import com.star.mobile.video.activity.PreviewImageActivity;
import com.star.mobile.video.model.ChatVO;
import com.star.mobile.video.model.LinkPkg;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.DateFormat;
import com.star.mobile.video.util.ExpressionUtil;
import com.star.mobile.video.util.LinkUtil;
import com.star.ui.ImageView;
import com.star.util.loader.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 机器人聊天室的adapter
 * 
 * @author Lee
 * @date 2016/01/08
 *
 */
public class RobotCustomerChatAdapter extends BaseAdapter {
	private String regExpres = "f0[0-9]{2}|f10[0-7]";
	private static final int TYPE_COUNT = 2;
	private static final int TYPE_ROBOT = 0;
	private static final int TYPE_CUSTOMER = 1;
	private int mSelectPosition = -1;
	private Context mContext;
	private List<FAQVO> mChatContentList;
	private LayoutInflater mLayoutInflater;
	private User mUser;

	public RobotCustomerChatAdapter(Context context, List<FAQVO> chatContentList) {
		this.mContext = context;
		this.mChatContentList = chatContentList;
		mLayoutInflater = LayoutInflater.from(context);
		mUser = StarApplication.mUser;

	}

	public void setRobotCustomerChatDatas(List<FAQVO> chatContentList) {
		this.mChatContentList = chatContentList;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mChatContentList.size();
	}

	@Override
	public Object getItem(int position) {
		return mChatContentList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		FAQVO chart = mChatContentList.get(position);
		if (chart != null) {
			if (mUser != null && mUser.getId().equals(chart.getUserId())) {
				return TYPE_CUSTOMER;
			} else {
				return TYPE_ROBOT;
			}
		} else {
			Log.i("initData", "RobotCustomerChatAdapter chart is null");
		}
		return super.getItemViewType(position);
	}

	@Override
	public int getViewTypeCount() {
		return TYPE_COUNT;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		RobotViewHolder robotViewHolder = null;
		CustomerViewHolder customerViewHolder = null;
		FAQVO chart = mChatContentList.get(position);
		int viewType = getItemViewType(position);
		switch (viewType) {
		case TYPE_ROBOT:
			if (convertView == null) {
				robotViewHolder = new RobotViewHolder();
				convertView = mLayoutInflater.inflate(R.layout.view_robot_chat, parent, false);

				robotViewHolder.mRobotHeadImageView = (com.star.ui.ImageView) convertView
						.findViewById(R.id.robot_header);
				robotViewHolder.mRobotUserName = (TextView) convertView.findViewById(R.id.robot_username);
				robotViewHolder.mRobotTime = (TextView) convertView.findViewById(R.id.robot_time);
				robotViewHolder.mRobotContent = (TextView) convertView.findViewById(R.id.robot_content);

				robotViewHolder.mRobotChatLinkRL = (RelativeLayout) convertView.findViewById(R.id.rl_chat_link_robot);
				robotViewHolder.mRobotLinkTitle = (TextView) convertView.findViewById(R.id.tv_link_title_robot);
				robotViewHolder.mRobotLinkIcon = (ImageView) convertView.findViewById(R.id.iv_link_icon_robot);
				robotViewHolder.mRobotLinkDesc = (TextView) convertView.findViewById(R.id.tv_link_desc_robot);

				convertView.setTag(robotViewHolder);
			} else {
				robotViewHolder = (RobotViewHolder) convertView.getTag();
			}

			robotViewHolder.mRobotHeadImageView
					.setImageDrawable(mContext.getResources().getDrawable(R.drawable.official_avatar));
			robotViewHolder.mRobotTime.setText(getCurrentTime());

			if (FAQ.type_leaf_node.equals(chart.getNodeType())) {
				// TODO 显示连接布局
				robotViewHolder.mRobotContent.setVisibility(View.GONE);
				robotViewHolder.mRobotChatLinkRL.setVisibility(View.VISIBLE);

				LinkPkg linkP = LinkUtil.processLink_(chart.getMsg());
				robotViewHolder.mRobotLinkIcon.setImageResource(R.drawable.link_picture_chat_grey);
				if (linkP != null) {
					robotViewHolder.mRobotLinkIcon.setUrl(linkP.getImgurl() == null ? "" : linkP.getImgurl());

					robotViewHolder.mRobotLinkTitle.setText(linkP.getTitle() == null ? "" : linkP.getTitle());
					robotViewHolder.mRobotLinkDesc
							.setText(linkP.getDescription() == null ? linkP.getUrl() : linkP.getDescription());
				}
				robotViewHolder.mRobotChatLinkRL.setTag(chart);
				robotViewHolder.mRobotChatLinkRL.setOnClickListener(onLinkClickListener);
			} else {
				robotViewHolder.mRobotContent.setVisibility(View.VISIBLE);
				robotViewHolder.mRobotChatLinkRL.setVisibility(View.GONE);
				robotViewHolder.mRobotContent.setText(chart.getMsg());
//				formatContent(position, robotViewHolder.mRobotContent, robotViewHolder.mRobotLinkIcon,robotViewHolder.mRobotChatLinkRL, chart);
//				Linkify.addLinks(robotViewHolder.mRobotContent, Patterns.WEB_URL,Constant.SCHEAM + "?" + Constant.UID + "=");
			}

			break;
		case TYPE_CUSTOMER:
			if (convertView == null) {
				customerViewHolder = new CustomerViewHolder();
				convertView = mLayoutInflater.inflate(R.layout.view_customer_chat, parent, false);

				customerViewHolder.mCustomerHeadImageView = (ImageView) convertView.findViewById(R.id.customer_header);
				customerViewHolder.mCustomerUserName = (TextView) convertView.findViewById(R.id.customer_username);
				customerViewHolder.mCustomerTime = (TextView) convertView.findViewById(R.id.customer_time);
				customerViewHolder.mCustomerContent = (TextView) convertView.findViewById(R.id.customer_content);
				customerViewHolder.mCustomerStatusRL = (RelativeLayout) convertView.findViewById(R.id.rl_right_status);
				convertView.setTag(customerViewHolder);
			} else {
				customerViewHolder = (CustomerViewHolder) convertView.getTag();
			}
			customerViewHolder.mCustomerHeadImageView.setUrl(mUser.getHead());
			customerViewHolder.mCustomerUserName.setText(mUser.getNickName());

			customerViewHolder.mCustomerTime.setText(getCurrentTime());
			customerViewHolder.mCustomerContent.setText(chart.getMsg());

			break;

		default:
			break;
		}

		return convertView;
	}

	class RobotViewHolder {
		com.star.ui.ImageView mRobotHeadImageView;
		TextView mRobotUserName;
		TextView mRobotTime;
		TextView mRobotContent;
		RelativeLayout mRobotChatLinkRL;
		TextView mRobotLinkTitle;
		com.star.ui.ImageView mRobotLinkIcon;
		TextView mRobotLinkDesc;
	}

	class CustomerViewHolder {
		com.star.ui.ImageView mCustomerHeadImageView;
		TextView mCustomerUserName;
		TextView mCustomerTime;
		TextView mCustomerContent;
		RelativeLayout mCustomerStatusRL;
	}

	private void formatContent(int position, final TextView contentView,
			final com.star.ui.ImageView imageView,
			final RelativeLayout linkGroup, FAQVO chat) {
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
	
	private void setText(final TextView contentView,final com.star.ui.ImageView imageView,final RelativeLayout linkGroup, Chart chat) {
		imageView.setVisibility(View.GONE);
		linkGroup.setVisibility(View.GONE);
		SpannableString spannableString = ExpressionUtil.getSpannableString(mContext, chat.getMsg(), regExpres);
		contentView.setText(spannableString);
		contentView.setVisibility(View.VISIBLE);
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
	
	/**
	 * 点击连接打开网页
	 */
	OnClickListener onLinkClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			FAQVO chat = (FAQVO) v.getTag();
			if (chat != null && chat.getMsg() != null) {
				LinkPkg link_ = LinkUtil.processLink_(chat.getMsg());
				if (link_ != null) {
					Intent intent = new Intent(mContext, BrowserActivity.class);
					intent.putExtra("loadUrl", link_.getUrl());
					CommonUtil.startActivity((Activity) mContext, intent);
				}
			}
		}
	};
	
	/**
	 * 获得当前时间 格式为： 11/04 Thursday 03:15
	 * 
	 * @return
	 */
	private String getCurrentTime() {
		long currentTimeMillis = System.currentTimeMillis();
		Date date = new Date(currentTimeMillis);
		return formatTime(date);
	}

	/**
	 * 格式化日期
	 * 
	 * @param date
	 * @return
	 */
	private String formatTime(Date date) {
		String dateSection = DateFormat.formatTime(date);
		return DateFormat.formatTuesday(date) + " " + dateSection;
	}
	
	class OnClick implements OnClickListener {

		private int postion;

		public OnClick(int postion) {
			this.postion = postion;
		}

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(mContext, PreviewImageActivity.class);
			intent.putExtra("imageUrl",	((Chart) getItem(postion)).getImageURL());
			mContext.startActivity(intent);
		}
	}
}
