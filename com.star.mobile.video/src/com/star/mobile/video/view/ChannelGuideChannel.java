package com.star.mobile.video.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.star.cms.model.vo.ChannelVO;
import com.star.cms.model.vo.ProgramVO;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.adapter.EpgListAdapter;
import com.star.mobile.video.epg.EpgDetailActivity;
import com.star.mobile.video.fragment.ChannelGuideFragment;
import com.star.mobile.video.service.ChannelService;
import com.star.mobile.video.service.EggAppearService;
import com.star.mobile.video.service.ProgramService;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.LoadingDataTask;
import com.star.mobile.video.util.ToastUtil;
import com.star.mobile.video.view.BaseEpgItemView.OnItemCallBackListener;

public class ChannelGuideChannel extends LinearLayout implements OnScrollListener {
	private List<ProgramVO> epgs = new ArrayList<ProgramVO>();
	private ChannelDetailLayout detailLayout;
	private EpgListView lv_epg_list;
	private View loadingView;
	private LinearLayout ll_noData;
	private EpgListAdapter mAdapter;
	private View footerView;
	private TextView tv_noDate;
	private int offset;
	private int responsSize;
	private boolean isFirstLoad = true;
	private long currentTime;
	private final int WHAT_HIDE_LISTFOOTER = 1;
	private boolean isLoading;
	private ProgramService epgService;
	private ChannelService chnService;
	private Context context;
	private ChannelVO mChannel;

	public Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WHAT_HIDE_LISTFOOTER:
				lv_epg_list.removeFooterView(footerView);
				break;
			}
		}
	};
	
	public ChannelGuideChannel(Context context, ChannelGuideFragment parentFragment) {
		super(context);
		this.context = context;
		LayoutInflater.from(context).inflate(R.layout.fragment_channel_guide, this);
		initView();
	}

	private void initView() {
		detailLayout = new ChannelDetailLayout(context);
		lv_epg_list = (EpgListView) findViewById(R.id.lv_epg_list);
		ll_noData = (LinearLayout) findViewById(R.id.ll_no_data);
		tv_noDate = (TextView) findViewById(R.id.tv_no_data);
		tv_noDate.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		tv_noDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				StarApplication.mTracker.send(new HitBuilders.EventBuilder().setCategory(Constant.GA_EVENT_BUSINESS)
						.setAction(Constant.GA_EVENT_EPG_WISH).setLabel("Channel:"+(mChannel==null?"":mChannel.getName())).setValue(1).build());
				ToastUtil.centerShowToast(context, context.getString(R.string.channnel_guide_send_request));
			}
		});
		
		loadingView = findViewById(R.id.loadingView);
		lv_epg_list.setDividerHeight(0);
		lv_epg_list.setOnScrollListener(this);
		lv_epg_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(position<1){
					return;
				}
				Intent intent = new Intent(context,EpgDetailActivity.class);
				intent.putExtra("programId", epgs.get(position-1).getId());
				CommonUtil.startActivity(context, intent);
			}
		});
		
		footerView = new LoadingProgressBar(context);
		lv_epg_list.addHeaderView(detailLayout);
	}
	
	public void updateProgramList(){
		if(mChannel == null)
			return;
		if(mAdapter != null && epgs.size()>0){
			epgs.clear();
			mAdapter.updateEpgDataAndRefreshUI(epgs);
		}
		mAdapter = new EpgListAdapter(context, epgs);
		mAdapter.setOnItemCallBackListener(itemCallBack);
		lv_epg_list.removeFooterView(footerView);
		lv_epg_list.addFooterView(footerView);
		lv_epg_list.setAdapter(mAdapter);
		offset = 0;
		responsSize = 0;
		isFirstLoad = true;
		currentTime = System.currentTimeMillis();
		executeLoadingTask();
	}
	
	private void setChannelDetail(){
		if(detailLayout != null){
			detailLayout.setCurrentChannel(mChannel);
			EggAppearService.appearEgg(context, EggAppearService.ChannelGuide);
		}
	}
	
	public void setCurrentChannel(ChannelVO channel){
		if(channel == null)
			return;
		mChannel = channel;
		setChannelDetail();
		updateProgramList();
	}
	
	public void executeLoadingTask() {
		new LoadingDataTask() {
			List<ProgramVO> es;
			@Override
			public void onPreExecute() {
				ll_noData.setVisibility(View.GONE);
				loadingView.setVisibility(View.VISIBLE);
				if(isFirstLoad){
					footerView.setVisibility(View.INVISIBLE);
				}else if(footerView.getVisibility() == View.INVISIBLE){
					footerView.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onPostExecute() {
				if(es == null){
					ll_noData.setVisibility(View.VISIBLE);
					return;
				}
				responsSize = es.size();
				epgs.addAll(es);
				loadingView.setVisibility(View.GONE);
				if(isFirstLoad && responsSize<Constant.request_item_count){
					handler.sendEmptyMessage(WHAT_HIDE_LISTFOOTER);
				}
				isFirstLoad = false;
				if(epgs.size() == 0){
					detailLayout.setSyncIconResid(R.drawable.icon_download_no_epg);
					ll_noData.setVisibility(View.VISIBLE);
					return;
				}else{
					detailLayout.setSyncIconResid(R.drawable.icon_download);
				}
				mAdapter.updateEpgDataAndRefreshUI(epgs);
				isLoading = false;
			}

			@Override
			public void doInBackground() {
				isLoading = true;
				es = getProgramService().getEpgs(mChannel.getId(), currentTime, offset, Constant.request_item_count);
			}
		}.execute();
	}
	
	private OnItemCallBackListener itemCallBack = new OnItemCallBackListener() {
		@Override
		public void onItemCallBack() {
			updateProgramList();
		}
	};

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if(isLoading){
			return;
		}
		switch (scrollState) {
		case OnScrollListener.SCROLL_STATE_IDLE:
			int position = lv_epg_list.getLastVisiblePosition();
			int total = epgs.size();
			if (position == total+1) {
				offset += Constant.request_item_count;
				if (responsSize < Constant.request_item_count) {
					ToastUtil.centerShowToast(context, getResources().getString(R.string.no_more_programs));
					handler.sendEmptyMessageDelayed(WHAT_HIDE_LISTFOOTER, 1000);
					return;
				}
				executeLoadingTask();
			}
			break;
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
	}
	
	protected ProgramService getProgramService(){
		if(epgService == null)
			epgService = new ProgramService(context);
		return epgService;
	}

	protected ChannelService getChannelService(){
		if(chnService == null)
			chnService = new ChannelService(context);
		return chnService;
	}
}
