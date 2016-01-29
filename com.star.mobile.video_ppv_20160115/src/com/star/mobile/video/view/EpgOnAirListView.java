package com.star.mobile.video.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.star.cms.model.enm.OrderType;
import com.star.cms.model.vo.ChannelVO;
import com.star.mobile.video.R;
import com.star.mobile.video.adapter.EpgOnAirListAdapter;
import com.star.mobile.video.service.ChannelService;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.LoadingDataTask;
import com.star.mobile.video.util.ToastUtil;

public class EpgOnAirListView extends BaseEpgListView implements OnScrollListener {
	private final String TAG = "EpgListView";
	private ListView lv_epg_list;
	private EpgOnAirListAdapter mAdapter;
	private View footerView;
	private List<ChannelVO> channels = new ArrayList<ChannelVO>();
	private int offset;
	private int responsSize;
	private ChannelService chnService;
	private final int WHAT_HIDE_LISTFOOTER = 1;
	private final int currentPage;
	private boolean firstShow = true;
	private boolean hasEpg = true;
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
	
	public EpgOnAirListView(Context context, int page) {
		super(context);
		this.currentPage = page;
		initView();
		chnService = new ChannelService(context);
		if(page == Constant.PAGE_ONAIR_NOW)
			CommonUtil.showProgressDialog(context, null, "Preparing data, Waiting...");
		getChannelsByhasEpg();
	}

	public void initView() {
		LayoutInflater.from(getContext()).inflate(R.layout.view_epg_list, this);
		lv_epg_list = (ListView) findViewById(R.id.lv_epg_list);
		lv_epg_list.setOnScrollListener(this);
		lv_epg_list.setOnTouchListener(this);
		
		footerView = new LoadingProgressBar(getContext());
		lv_epg_list.addFooterView(footerView);
	}
	
	public void getChannelsByhasEpg() {
		new LoadingDataTask() {
			List<ChannelVO> chns;
			@Override
			public void onPreExecute() {
			}

			@Override
			public void onPostExecute() {
				isLoading = false;
				if(chns==null || chns.size()==0){
					if(firstShow){
						hasEpg = false;
						executeLoadingTask();
					}else{
						responsSize = 0;
					}
					return;
				}
				hasEpg = true;
				responsSize = chns.size();
				channels.addAll(chns);
				if(firstShow && responsSize<Constant.request_item_count){
					handler.sendEmptyMessage(WHAT_HIDE_LISTFOOTER);
				}
				if (mAdapter == null) {
					mAdapter = new EpgOnAirListAdapter(getContext(), channels, true);
					mAdapter.setCurrentPage(currentPage);
					lv_epg_list.setAdapter(mAdapter);
				} else {
					mAdapter.updateChnDataAndRefreshUI(channels);
				}
				if(firstShow){
					CommonUtil.closeProgressDialog();
					firstShow = false;
				}
			}

			@Override
			public void doInBackground() {
				isLoading = true;
				chns = chnService.getChannels(offset, Constant.request_item_count, OrderType.FAVORITE, true);
			}
		}.execute();
	}
	
	public void executeLoadingTask() {
		new LoadingDataTask() {
			List<ChannelVO> chns;
			@Override
			public void onPreExecute() {
			}

			@Override
			public void onPostExecute() {
				isLoading = false;
				if(chns == null){
					handler.sendEmptyMessage(WHAT_HIDE_LISTFOOTER);
					responsSize = 0;
					return;
				}
				responsSize = chns.size();
				channels.addAll(chns);
				if(firstShow && responsSize<Constant.request_item_count){
					handler.sendEmptyMessage(WHAT_HIDE_LISTFOOTER);
				}
				if (mAdapter == null) {
					mAdapter = new EpgOnAirListAdapter(getContext(), channels, true);
					mAdapter.setCurrentPage(currentPage);
					lv_epg_list.setAdapter(mAdapter);
				} else {
					mAdapter.updateChnDataAndRefreshUI(channels);
				}
				if(firstShow){
					CommonUtil.closeProgressDialog();
					firstShow = false;
				}
			}

			@Override
			public void doInBackground() {
				isLoading = true;
				chns = chnService.getChannels(OrderType.FAVORITE, offset, Constant.request_item_count);
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
			int total = channels.size();
			Log.i(TAG, "position="+position+"total="+total+"offset="+offset+"respons="+responsSize);
			if (position == total) {
				offset += Constant.request_item_count;
				if (responsSize < Constant.request_item_count) {
					ToastUtil.centerShowToast(getContext(), getContext().getString(R.string.no_more_programs));
					handler.sendEmptyMessageDelayed(WHAT_HIDE_LISTFOOTER, 1000);
					return;
				}
				if(hasEpg){
					getChannelsByhasEpg();
				}else{
					executeLoadingTask();
				}
			}
			break;
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
	}
}
