package com.star.mobile.video.activity;

import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.star.cms.model.vo.ChannelVO;
import com.star.mobile.video.R;
import com.star.mobile.video.adapter.ChannelLogoAdapter;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.service.ChannelService;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.DensityUtil;
import com.star.mobile.video.util.LoadingDataTask;

public class OfflineEpgActivity extends BaseActivity implements OnClickListener{
	
	private GridView gv_epgs;
	private ChannelService chnService;
	private int logowidth;
	List<ChannelVO> channels;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_offline_group);
		chnService = new ChannelService(this);
		logowidth = (Constant.WINDOW_WIDTH - 2*DensityUtil.dip2px(this, 6) - 3*DensityUtil.dip2px(this, 3))/4;
		((TextView) findViewById(R.id.tv_actionbar_title)).setText(getString(R.string.offline_epg));
		findViewById(R.id.iv_actionbar_back).setOnClickListener(this);
		gv_epgs = (GridView) findViewById(R.id.gv_offline_epgs);
		gv_epgs.setOnItemClickListener(itemClickListener);
		getDateAndFreshUI();
	}

	private void getDateAndFreshUI() {
		new LoadingDataTask() {
			@Override
			public void onPreExecute() {
				CommonUtil.showProgressDialog(OfflineEpgActivity.this);
			}
			
			@Override
			public void onPostExecute() {
				if(channels != null){
					ChannelLogoAdapter logoAdapter = new ChannelLogoAdapter(OfflineEpgActivity.this, channels, logowidth);
					logoAdapter.setShowChnNumber(false);
					gv_epgs.setAdapter(logoAdapter);
				}
				CommonUtil.closeProgressDialog();
 			}
			
			@Override
			public void doInBackground() {
				channels = chnService.getOfflineChannels();
			}
		}.execute();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_actionbar_back:
			super.onBackPressed();
			break;
			
		default:
			break;
		}
	}
	
	private OnItemClickListener itemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			ChannelVO channel = channels.get(position);
			CommonUtil.startChannelActivity(OfflineEpgActivity.this, channel);
		}
	};
}
