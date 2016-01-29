package com.star.mobile.video.search;

import org.libsdl.app.Player;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.star.cms.model.Channel;
import com.star.cms.model.ChatRoom;
import com.star.cms.model.Program;
import com.star.cms.model.vo.VOD;
import com.star.mobile.video.R;
import com.star.mobile.video.activity.ChatActivity;
import com.star.mobile.video.home.HomeActivity;
import com.star.mobile.video.service.ChannelService;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.LoadingDataTask;
import com.star.mobile.video.util.TextUtil;
import com.star.ui.ImageView;

public class SearchMoreItemView<T> extends RelativeLayout {
	private Context context;
	private com.star.ui.ImageView ivSearch;
	private TextView tvSearchTitle;
	private TextView tvSearchDes;
	private LinearLayout ll_search_skip;

	public SearchMoreItemView(Context context) {
		this(context, null);
	}

	public SearchMoreItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initView();
	}

	private void initView() {
		LayoutInflater.from(context).inflate(R.layout.view_search_twolevel_item, this);
		ll_search_skip = (LinearLayout) findViewById(R.id.ll_search_skip);
		ivSearch = (ImageView) findViewById(R.id.iv_search_icon);
		tvSearchTitle = (TextView) findViewById(R.id.tv_search_title);
		tvSearchDes = (TextView) findViewById(R.id.tv_search_des);
	}

	public void setData(T t, String key) {
		if (t instanceof Channel) {
			setChannel((Channel) t, key);
		} else if (t instanceof Program) {
			setProgram((Program)t, key);
		} else if (t instanceof ChatRoom) {
			setChat((ChatRoom)t, key);
		} else if (t instanceof VOD) {
			setVod((VOD)t, key);
		}
	}

	protected void loadChannelLogoTask(final com.star.ui.ImageView view,
			final Program program) {
		view.setTag(program);
		new LoadingDataTask() {
			private ViewHolder holder;

			@Override
			public void onPreExecute() {
			}

			@Override
			public void onPostExecute() {
				if (holder != null && holder.channel != null && holder.imageview.getTag().equals(holder.program)) {
					try {
						view.setUrl(holder.channel.getLogo().getResources().get(0).getUrl());
					} catch (Exception e) {
					}
				}
			}

			@Override
			public void doInBackground() {
				Channel chn = new ChannelService(context).getChannelById(program.getChannelId());
				holder = new ViewHolder();
				holder.channel = chn;
				holder.imageview = view;
				holder.program = program;
			}
		}.execute();
	}

	class ViewHolder {
		com.star.ui.ImageView imageview;
		Channel channel;
		Program program;
	}

	private void setChannel(final Channel chn, String key) {
		try {
			String chNum = chn.getChannelNumber()==null?"":chn.getChannelNumber()+" ";
			TextUtil.getInstance().setText(tvSearchTitle, chNum + chn.getName(), key);
			ivSearch.setUrl(chn.getLogo().getResources().get(0).getUrl());
		} catch (Exception e) {
		}
		ll_search_skip.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent =new Intent(context, HomeActivity.class);
				intent.putExtra("channelId", chn.getId());
				CommonUtil.startActivity(context, intent);
//				CommonUtil.startFragmentActivity(context, context.getString(R.string.fragment_tag_channelGuide), chn.getId());
			}
		});
	}

	private void setProgram(final Program epg, String key) {
		TextUtil.getInstance().setText(tvSearchTitle, epg.getName(), key);
		if (epg.getDescription() != null) {
			TextUtil.getInstance().setText(tvSearchDes, epg.getDescription(), key);
			tvSearchDes.setVisibility(View.VISIBLE);
		} else {
			tvSearchDes.setVisibility(View.GONE);
		}
		loadChannelLogoTask(ivSearch, epg);
		ll_search_skip.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CommonUtil.startEpgActivity(context, epg.getId());
			}
		});
	}

	private void setVod(final VOD vod, String key) {
		try {
			TextUtil.getInstance().setText(tvSearchTitle, vod.getName(), key);
			ivSearch.setUrl(vod.getPoster().getResources().get(0).getUrl());
		} catch (Exception e) {
		}
		ll_search_skip.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, Player.class);
//				intent.putExtra("vod", vod);
				intent.putExtra("filename", vod.getVideo().getResources().get(0).getUrl());
				intent.putExtra("epgname", vod.getName());
				intent.putExtra("isLive", "false");
				intent.putExtra("dejia", "1");
				intent.putExtra("isSchedule", "true");
				CommonUtil.startActivity(context, intent);
			}
		});
	}

	private void setChat(final ChatRoom chatroom, String key) {
		try {
			TextUtil.getInstance().setText(tvSearchTitle, chatroom.getName(), key);
			ivSearch.setUrl(chatroom.getLogo());
		} catch (Exception e) {
		}
		ll_search_skip.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(context, ChatActivity.class);
				intent.putExtra("chatroom", chatroom);
				CommonUtil.startActivity(context, intent);
			}
		});
	}
}
