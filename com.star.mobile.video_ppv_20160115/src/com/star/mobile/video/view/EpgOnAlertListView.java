package com.star.mobile.video.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.star.cms.model.vo.ProgramVO;
import com.star.mobile.video.R;
import com.star.mobile.video.adapter.EpgOnAlertListAdapter;
import com.star.mobile.video.model.WeekModel;
import com.star.mobile.video.service.ProgramService;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.LoadingDataTask;
import com.star.mobile.video.util.ToastUtil;

public class EpgOnAlertListView extends BaseEpgListView implements OnScrollListener{
	private ListView lv_epg_list;
	private EpgOnAlertListAdapter mAdapter;
	private View footerView;
	private List<ProgramVO> epgs = new ArrayList<ProgramVO>();;
	private int offset;
	private int responsSize;
	private ProgramService epgService;
	private WeekModel weekMode;
	private boolean isFirstLoad = true;
	private final int WHAT_HIDE_LISTFOOTER = 1;
	private View iv_no_alert;
	private View loadingView;
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
	
	public EpgOnAlertListView(Context context, WeekModel weekMode) {
		super(context);
		initView();
		this.weekMode = weekMode;
		epgService = new ProgramService(context);
		executeLoadingTask();
	}

	public void initView() {
		LayoutInflater.from(getContext()).inflate(R.layout.view_epg_list, this);
		iv_no_alert = findViewById(R.id.iv_no_data);
		lv_epg_list = (ListView) findViewById(R.id.lv_epg_list);
		loadingView = findViewById(R.id.loadingView);
		loadingView.setVisibility(View.VISIBLE);
		lv_epg_list.setOnScrollListener(this);
		lv_epg_list.setOnTouchListener(this);
		
		footerView = new LoadingProgressBar(getContext());
		lv_epg_list.addFooterView(footerView);
		mAdapter = new EpgOnAlertListAdapter(getContext(), epgs);
		mAdapter.setOnItemCallBackListener(mListener);
		lv_epg_list.setAdapter(mAdapter);
	}
	
	public void executeLoadingTask() {
		new LoadingDataTask() {
			List<ProgramVO> es;
			@Override
			public void onPreExecute() {
				if(isFirstLoad){
					footerView.setVisibility(View.INVISIBLE);
					loadingView.setVisibility(View.VISIBLE);
				}else if(footerView.getVisibility() == View.INVISIBLE){
					footerView.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onPostExecute() {
				if(es == null)
					return;
				responsSize = es.size();
				epgs.addAll(es);
				if(loadingView.getVisibility()==View.VISIBLE)
					loadingView.setVisibility(View.GONE);
				if(isFirstLoad && responsSize<Constant.request_item_count){
					handler.sendEmptyMessage(WHAT_HIDE_LISTFOOTER);
				}
				isFirstLoad = false;
				if(epgs.size() == 0){
					iv_no_alert.setVisibility(View.VISIBLE);
					return;
				}else{
					iv_no_alert.setVisibility(View.GONE);
				}
				mAdapter.updateChnDataAndRefreshUI(epgs);
				mAdapter.setOnItemCallBackListener(mListener);
				isLoading = false;
			}

			@Override
			public void doInBackground() {
				isLoading = true;
				es = epgService.getFavEpgs(true, weekMode.getStartime(), weekMode.getEndtime(), offset, Constant.request_item_count);
			}
		}.execute();
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if(isLoading)
			return;
		switch (scrollState) {
		case OnScrollListener.SCROLL_STATE_IDLE:
			int position = lv_epg_list.getLastVisiblePosition();
			int total = epgs.size();
			if (position == total) {
				offset += Constant.request_item_count;
				if (responsSize < Constant.request_item_count) {
					ToastUtil.centerShowToast(getContext(), "No more programs");
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
}
