package com.star.mobile.video.search;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.star.cms.model.Channel;
import com.star.cms.model.ChatRoom;
import com.star.cms.model.Program;
import com.star.cms.model.vo.SearchVO;
import com.star.cms.model.vo.VOD;
import com.star.mobile.video.R;
import com.star.mobile.video.util.ToastUtil;
import com.star.mobile.video.view.LoadingProgressBar;
import com.star.util.loader.OnResultListener;

public class SearchMoreLayout<T> extends LinearLayout implements OnScrollListener {

	private ListView listView;
//	private TextView textView;
//	private TextView tv_searchContent;
	private View loadingView;
	private View footerView;
	private SearchHeadView searchHeadView;
	private List<Channel> channels = new ArrayList<Channel>();
	private List<Program> programs = new ArrayList<Program>();
	private List<VOD> vods = new ArrayList<VOD>();
	private List<ChatRoom> chatRooms = new ArrayList<ChatRoom>();
	private SearchService searchService;
	private boolean isLoading;
	private int offset = 0;
	private int requestCount = 10;
	private SearchMoreAdapter<?> searchMoreAdapter = null;
	private int type;
	private int responsSize;
	private final int WHAT_HIDE_LISTFOOTER = 1;
	private String searchkey;
	private boolean firstShow = true;
	
	public Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WHAT_HIDE_LISTFOOTER:
				listView.removeFooterView(footerView);
				break;
			}
		}
	};
	
	public SearchMoreLayout(Context context, String searchKey, int searchType) {
		this(context, null, searchKey, searchType);
	}
	
	public SearchMoreLayout(Context context, AttributeSet attrs, String searchKey, int searchType) {
		super(context, attrs);
		initView();
		searchService = new SearchService(context);
		setData(searchKey, searchType);
	}

	private void initView() {
		LayoutInflater.from(getContext()).inflate(R.layout.fragment_search_more, this);
//		textView = (TextView) findViewById(R.id.check_more_type);
//		tv_searchContent = (TextView) findViewById(R.id.tv_serachcontent);
		listView = (ListView) findViewById(R.id.check_more_list);
		loadingView = (View) findViewById(R.id.loadingView);
		listView.setOnScrollListener(this);
		searchHeadView = new SearchHeadView(getContext());
		listView.addHeaderView(searchHeadView);
		footerView = new LoadingProgressBar(getContext());
		listView.addFooterView(footerView);
	}
	
	public void setData(String searchKey, int searchType){
//		setSearchType(searchType);
		this.searchkey = searchKey;
		type=searchType;
		searchHeadView.setData(searchType, searchKey);
//		tv_searchContent.setText(searchKey);
		setLoadListenerAndLoad(searchKey, searchType);
	}

//	private void setSearchType(int searchType) {
//		if(searchType == SearchVO.CHANNEL_TYPE){
//			textView.setText("CHANNEL");
//		}else if(searchType == SearchVO.PROGRAM_TYPE){
//			textView.setText("PROGRAM");
//		}else if(searchType == SearchVO.VOD_TYPE){
//			textView.setText("VIDEO");
//		}else if(searchType == SearchVO.CHATROOM_TYPE){
//			textView.setText("CHATROOM");
//		}
//	}
	
	private void setLoadListenerAndLoad(final String searchKey, final int searchType){
		searchService.getSearchDetail(searchKey, offset, requestCount, searchType, new OnResultListener<SearchVO>() {
			
			@Override
			public boolean onIntercept() {
				if(isLoading){
					return true;
				}
				isLoading = true;
				return false;
			}
			
			@Override
			public void onSuccess(SearchVO searchVO) {
				isLoading = false;
				loadingView.setVisibility(View.GONE);
				if(searchVO != null){
					listView.setVisibility(View.VISIBLE);
					if(searchType == SearchVO.CHANNEL_TYPE){
						responsSize = searchVO.getChannelList().size();
						channels.addAll(searchVO.getChannelList());
						if(searchMoreAdapter == null){
							searchMoreAdapter = new SearchMoreAdapter<Channel>(getContext(), channels, searchKey);	
							listView.setAdapter(searchMoreAdapter);
						}else{
							searchMoreAdapter.updateSearchResult(channels);
						}
					}else if(searchType == SearchVO.PROGRAM_TYPE){
						responsSize = searchVO.getProgramsList().size();
						programs.addAll(searchVO.getProgramsList());
						if(searchMoreAdapter == null){
							searchMoreAdapter = new SearchMoreAdapter<Program>(getContext(), programs, searchKey);
							listView.setAdapter(searchMoreAdapter);
						}else{
							searchMoreAdapter.updateSearchResult(programs);
						}
					}else if(searchType == SearchVO.VOD_TYPE){
						responsSize = searchVO.getVodList().size();
						vods.addAll(searchVO.getVodList());
						if(searchMoreAdapter == null){
							searchMoreAdapter = new SearchMoreAdapter<VOD>(getContext(), vods, searchKey);
							listView.setAdapter(searchMoreAdapter);
						}else{
							searchMoreAdapter.updateSearchResult(vods);
						}
					}else if(searchType == SearchVO.CHATROOM_TYPE){
						responsSize = searchVO.getChatRoomList().size();
						chatRooms.addAll(searchVO.getChatRoomList());
						if(searchMoreAdapter == null){
							searchMoreAdapter = new SearchMoreAdapter<ChatRoom>(getContext(), chatRooms, searchKey);
							listView.setAdapter(searchMoreAdapter);
						}else{
							searchMoreAdapter.updateSearchResult(chatRooms);
						}
					}
					if(firstShow && responsSize<requestCount){
						handler.sendEmptyMessage(WHAT_HIDE_LISTFOOTER);
					}
					firstShow = false;
				}else{
					handler.sendEmptyMessage(WHAT_HIDE_LISTFOOTER);
					responsSize = 0;
				}
			}

			@Override
			public void onFailure(int errorCode, String msg) {
				isLoading = false;
				loadingView.setVisibility(View.GONE);
			}
		});
//		listView.setRequestCount(10);
//		listView.setLoadingListener(new LoadingListener<SearchVO>() {
//			@Override
//			public List<SearchVO> loadingS(int offset, int requestCount) {
//				return searchService.getSearchDetail(searchKey, offset, requestCount, searchType);
//			}
//
//			@Override
//			public void loadPost(List<SearchVO> datas) {
//				if(datas !=null && datas.size()>0){
//					SearchVO vo = datas.get(0);
//					SearchMoreAdapter<?> searchMoreAdapter = null;
//					if(searchType == SearchVO.CHANNEL_TYPE){
//						searchMoreAdapter = new SearchMoreAdapter<Channel>(getContext(), vo.getChannelList(), searchKey);
//					}else if(searchType == SearchVO.PROGRAM_TYPE){
//						searchMoreAdapter = new SearchMoreAdapter<Program>(getContext(), vo.getProgramsList(), searchKey);
//					}else if(searchType == SearchVO.VOD_TYPE){
//						searchMoreAdapter = new SearchMoreAdapter<VOD>(getContext(), vo.getVodList(), searchKey);
//					}else if(searchType == SearchVO.CHATROOM_TYPE){
//						searchMoreAdapter = new SearchMoreAdapter<ChatRoom>(getContext(), vo.getChatRoomList(), searchKey);
//					}
//					listView.setAdapter(searchMoreAdapter);
//				}
//			}
//
//			@Override
//			public List<SearchVO> loadingL(int offset, int requestCount) {
//				return null;
//			}
//
//			@Override
//			public List<SearchVO> getFillList() {
//				return searchVOs;
//			}
//
//			@Override
//			public void onNoMoreData() {
//			}
//		});
//		listView.loadingData(false);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if(isLoading)
			return;
		switch (scrollState) {
		case OnScrollListener.SCROLL_STATE_IDLE:
			int position = listView.getLastVisiblePosition();
			int total = 0;
			if(type == SearchVO.CHANNEL_TYPE){
				total = channels.size();	
			}else if(type == SearchVO.PROGRAM_TYPE){
				total = programs.size();
			}else if(type == SearchVO.VOD_TYPE){
				total = vods.size();
			}else if(type == SearchVO.CHATROOM_TYPE){
				total = chatRooms.size();
			}
			if (position == total) {
				offset += requestCount;
				if (responsSize < requestCount) {
					ToastUtil.centerShowToast(getContext(), getContext().getString(R.string.no_more_programs));
					handler.sendEmptyMessageDelayed(WHAT_HIDE_LISTFOOTER, 1000);
					return;
				}
				setLoadListenerAndLoad(searchkey, type);
			}
			break;
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
	}
}
