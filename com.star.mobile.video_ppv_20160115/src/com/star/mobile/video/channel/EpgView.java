package com.star.mobile.video.channel;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.star.cms.model.ProgramPPV;
import com.star.cms.model.vo.ChannelVO;
import com.star.cms.model.vo.ProgramVO;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.adapter.EpgListAdapter;
import com.star.mobile.video.epg.EpgDetailActivity;
import com.star.mobile.video.home.tab.TabView;
import com.star.mobile.video.service.ProgramService;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.DensityUtil;
import com.star.mobile.video.util.ToastUtil;
import com.star.mobile.video.view.BaseEpgItemView.OnItemCallBackListener;
import com.star.mobile.video.view.ListView;
import com.star.mobile.video.view.ListView.LoadingListener;

public class EpgView extends TabView<ListView>  {


	private static final String FRAGMENT_INDEX = "fragment_index";

	private View mFragmentView;

	private int mCurIndex = -1;
	/** 标志位，标志已经初始化完成 */
	private boolean isPrepared;
	/** 是否已被加载过一次，第二次就不再去请求数据了 */
	private boolean mHasLoadedOnce = false;
	private List<ProgramPPV> epgs = new ArrayList<ProgramPPV>();
	private ListView lv_epg_list;
	private View loadingView;
	private LinearLayout ll_noData;
	private EpgListAdapter mAdapter;
	private View footerView;
	private TextView tv_noDate;
	private long currentTime;
	private final int WHAT_HIDE_LISTFOOTER = 1;
	private ProgramService epgService;
	private Context context;
	private ChannelVO mChannel;
	private int Offset = 0;
	private int COUNT = 6;
	private View headerOne;
	private View headerView;
	private boolean isLoadService;
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

//	/**
//	 * 创建新实例
//	 * 
//	 * @param index
//	 * @return
//	 */
//	public static EpgFragment newInstance(int index) {
//		Bundle bundle = new Bundle();
//		bundle.putInt(FRAGMENT_INDEX, index);
//		EpgFragment fragment = new EpgFragment();
////		fragment.setArguments(bundle);
//		return fragment;
//	}
	public EpgView(Context context) {
		super(context);
		this.context = context;
		initView();
	}
	public EpgView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initView();
	}
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		if(mFragmentView == null) {
//			context = getActivity();
//			mFragmentView =  inflater.inflate(R.layout.fragment_epg_detail_home, container, false);
//			initView();
//			//获得索引值
////			Bundle bundle = getArguments();
////			if (bundle != null) {
////				mCurIndex = bundle.getInt(FRAGMENT_INDEX);
////			}
//			isPrepared = true;
//			lazyLoad();
//		}
//		
//		//因为共用一个Fragment视图，所以当前这个视图已被加载到Activity中，必须先清除后再加入Activity
//		ViewGroup parent = (ViewGroup)mFragmentView.getParent();
//		if(parent != null) {
//			parent.removeView(mFragmentView);
//		}
//		return mFragmentView;
//	}
	private void initView() {
		mFragmentView =  LayoutInflater.from(context).inflate(R.layout.fragment_epg_detail_home,this);
		lv_epg_list = (ListView) mFragmentView.findViewById(R.id.lv_epg_list);
		ll_noData = (LinearLayout) mFragmentView.findViewById(R.id.ll_no_data);
		tv_noDate = (TextView) mFragmentView.findViewById(R.id.tv_no_data);
		tv_noDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				StarApplication.mTracker.send(new HitBuilders.EventBuilder().setCategory(Constant.GA_EVENT_BUSINESS)
						.setAction(Constant.GA_EVENT_EPG_WISH).setLabel("Channel:"+(mChannel==null?"":mChannel.getName())).setValue(1).build());
				ToastUtil.centerShowToast(context, context.getString(R.string.channnel_guide_send_request));
			}
		});
		loadingView = mFragmentView.findViewById(R.id.loadingView);
		headerView = new View(getContext());
		headerView.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT,DensityUtil.dip2px(getContext(), 48)));
		lv_epg_list.setLoadingListener(new LoadingListener<ProgramPPV>() {
			
			@Override
			public List<ProgramPPV> loadingS(int offset, int requestCount) {
				isLoadService = true;
				return getProgramService().getEpgsppv(mChannel.getId(), currentTime, offset, requestCount,false);
			}

			@Override
			public void loadPost(List<ProgramPPV> datas) {
				mHasLoadedOnce = true;
				loadingView.setVisibility(View.GONE);
				if(datas!=null && datas.size()>0){
					lv_epg_list.setVisibility(View.VISIBLE);
					epgs.addAll(datas);
					if(mAdapter==null){
						mAdapter = new EpgListAdapter(context, epgs);
//						lv_epg_list.addHeaderView(headerOne);
						lv_epg_list.addHeaderView(headerView);
						lv_epg_list.setAdapter(mAdapter);
//						lv_epg_list.setSelection(1);
						mAdapter.setOnItemCallBackListener(itemCallBack);
					}else{
						mAdapter.notifyDataSetChanged();
					}
					initScrollListener(lv_epg_list);
				}else{
					if(epgs.size()<=0){
						ll_noData.setVisibility(View.VISIBLE);
						lv_epg_list.setVisibility(View.GONE);
					}
				}
			}

			@Override
			public List<ProgramPPV> loadingL(int offset, int requestCount) {
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(!isLoadService){
					return getProgramService().getEpgsppv(mChannel.getId(), currentTime, offset, requestCount,true);
				}else{
					return null;
				}
			}

			@Override
			public List<ProgramPPV> getFillList() {
				return epgs;
			}

			@Override
			public void onNoMoreData() {
			}
		});
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
	}
//	@Override
//	protected void lazyLoad() {
//		if (!isPrepared || !isVisible || mHasLoadedOnce) {
//			return;
//		}
//		updateProgramList();
//	}

	public void updateProgramList(){
		if(mChannel.getId() == 0)
			return;
		if(mAdapter != null && epgs.size()>0){
			epgs.clear();
			mAdapter.updateEpgDataAndRefreshUI(epgs);
		}
		Offset = 0 ;
		mAdapter = null;
//		lv_epg_list.removeHeaderView(headerOne);
		lv_epg_list.removeHeaderView(headerView);
		currentTime = System.currentTimeMillis();
		loadingView.setVisibility(View.VISIBLE);
//		lv_epg_list.setVisibility(View.GONE);
		ll_noData.setVisibility(View.GONE);
		lv_epg_list.loadingData(true);
//		getChannelEpg(null);
	}
	
	private OnItemCallBackListener itemCallBack = new OnItemCallBackListener() {
		@Override
		public void onItemCallBack() {
//			updateProgramList();
			setChannel(mChannel);
		}
	};
	public void clearEpg(){
		loadingView.setVisibility(View.VISIBLE);
		lv_epg_list.setVisibility(View.GONE);
	}
	protected ProgramService getProgramService(){
		if(epgService == null)
			epgService = new ProgramService(context);
		return epgService;
	}
	public void setChannel(ChannelVO mChannel){
		this.mChannel = mChannel;
//		if(!mHasLoadedOnce){
			updateProgramList();	
//		}
		initTabsStatus();
	}
	
//	private void getChannelEpg(final PullToRefreshLayout pullToRefreshLayout) {
//		new LoadingDataTask() {
//			private List<ProgramVO>  content;
//			@Override
//			public void onPreExecute() {
//			}
//			@Override
//			public void onPostExecute() {
////				mHasLoadedOnce= true;
//				loadingView.setVisibility(View.GONE);
//				if (content != null && content.size() > 0) {
//					pullToRefresh.setVisibility(View.VISIBLE);
//					lv_epg_list.setVisibility(View.VISIBLE);
//					if(content.size()<COUNT){
//						lv_epg_list.setCanPlull(false);
//					}
//					
//					epgs.addAll(content);
//					mAdapter.notifyDataSetChanged();
//					showNoneMoreVideo(content);
//					if (pullToRefreshLayout != null) {
//						pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
//					}
//					initListener(lv_epg_list);
//				}else {
//					if (pullToRefreshLayout != null) {
//						pullToRefreshLayout.setLoadDoneText(getResources().getString(R.string.load_no_videos));
//						pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.NOMOREDATA);
//					}else{
//						ll_noData.setVisibility(View.VISIBLE);
//						lv_epg_list.setVisibility(View.GONE);
//					}
//				}
//			}
//			
//			@Override
//			public void doInBackground() {
//				try {
//					Thread.sleep(300);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				content = getProgramService().getEpgs(mChannel.getId(), currentTime, Offset,COUNT);
//			}
//		}.execute();	
//	}
	
}
