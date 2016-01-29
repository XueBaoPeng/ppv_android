package com.star.mobile.video.view;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.star.cms.model.vo.ChannelVO;
import com.star.mobile.video.R;
import com.star.mobile.video.fragment.ChannelGuideFragment;
import com.star.mobile.video.home.HomeActivity;

public class ChannelItemView extends LinearLayout implements OnClickListener{

	private TextView tv_channel_name;
	private ChannelVO channel;
	private TextView tv_chn_fav_count;
	private HomeActivity homeActivity;
	private com.star.ui.ImageView iv_channel_icon;
	private ImageView iv_fav_status;

	public ChannelItemView(Context context, ChannelVO channel) {
		super(context);
		initView(context);
		fillData(channel);
		homeActivity = (HomeActivity) context;
	}

	public ChannelItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		homeActivity = (HomeActivity) context;
	}

	private void initView(Context context) {
		LayoutInflater.from(context).inflate(R.layout.view_channel_grid_item, this);
		findViewById(R.id.rl_fav_layout).setVisibility(View.VISIBLE);
		iv_channel_icon = (com.star.ui.ImageView) findViewById(R.id.iv_channel_icon);
		tv_channel_name = (TextView) findViewById(R.id.tv_channel_name);
		iv_fav_status = (ImageView) findViewById(R.id.iv_fav_status);
		tv_chn_fav_count = (TextView) findViewById(R.id.tv_chn_fav_count);
		setOnClickListener(this);
	}
	
	public void fillData(ChannelVO channel){
		this.channel = channel;
		tv_channel_name.setText(channel.getName());
		String favCount = String.valueOf(channel.getFavCount());
		if(favCount.length()>4){
			favCount = "9999+";
		}
		tv_chn_fav_count.setText(favCount);
		if(channel.isFav()){
			iv_fav_status.setImageResource(R.drawable.favorite);
		}else{
			iv_fav_status.setImageResource(R.drawable.no_favorite);
		}
		setChannelLogo(channel);
	}

	private void setChannelLogo(ChannelVO channel) {
		try{
		String logoUrl = channel.getLogo().getResources().get(0).getUrl();
		iv_channel_icon.setUrl(logoUrl);
//		BitmapUtil.loadImageDrawable(new DrawableLoadingCallback() {
//			
//			@Override
//			public void setImageDrawable(Drawable drawable) {
//				iv_channel_icon.setImageDrawable(drawable);
//			}
//		}, logoUrl, homeActivity);
		}catch(Exception e){
//			Log.e("LoadingLogo", "load channel log error", e);
		}
	}

	@Override
	public void onClick(View v) {
		if(channel == null)
			return ;
		Fragment fragment = homeActivity.setFragmentByTag(getContext().getResources().getString(R.string.fragment_tag_channelGuide));
		if(fragment != null)
			((ChannelGuideFragment)fragment).setCurrentChannel(channel);
	}
}
