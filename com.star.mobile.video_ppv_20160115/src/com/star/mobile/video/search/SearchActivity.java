package com.star.mobile.video.search;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.star.cms.model.Channel;
import com.star.cms.model.ChatRoom;
import com.star.cms.model.Program;
import com.star.cms.model.vo.SearchVO;
import com.star.cms.model.vo.VOD;
import com.star.mobile.video.R;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.me.mycoins.ScrollView;
import com.star.mobile.video.service.ChannelService;
import com.star.mobile.video.util.LoadingDataTask;
import com.star.mobile.video.util.ToastUtil;
import com.star.mobile.video.view.NoScrollListView;
import com.star.util.loader.AsyncTask;
import com.star.util.loader.OnListResultListener;
import com.star.util.loader.OnResultListener;

public class SearchActivity extends BaseActivity implements OnClickListener {

	private List<String> historyHotKey = new ArrayList<String>();
	private List<String> recommendHotKey = new ArrayList<String>();
	private List<String> predictKey = new ArrayList<String>();
	private ImageView ivSearchIcon;
	private ImageView ivSearchCancle;
	private EditText etSearchInput;
	private InputMethodManager imm;
	private ScrollView sv_all;
	private NoScrollListView lvRecommend;
	private NoScrollListView lvHistory;
	private NoScrollListView lvSearchPredictPage;
	private LinearLayout llSearchResult;
	private LinearLayout llNoResult;//未搜索到匹配项
	private LinearLayout llSearchHistory;//搜索历史
	private LinearLayout llSearchMorePage;//搜索更多
	private LinearLayout llSearchHotPage;//推荐内容页
	private LinearLayout llSearchResultPage;//搜索页面
	private LinearLayout llNetErrorPage;//网络状况页
	private View loadingView;
	private TextView tvSearchKey;
	private boolean loading = false;
	private ChannelService chnService;
	private SearchService searchService;
	private SearchHistoryAdapter historyAdapter;
	private SearchHistoryAdapter predictAdapter;
	private boolean fromHot = false;//区分手输还是推荐
	private SearchSharedPre searchSharedPre;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		chnService = new ChannelService(this);
		searchService = new SearchService(this);
		searchSharedPre = new SearchSharedPre(this);
		initView();
		initData();
		loadRecommedKey();
	}

	private void initView() {
		findViewById(R.id.iv_actionbar_back).setOnClickListener(this);
		etSearchInput = (EditText) findViewById(R.id.et_search);
		ivSearchIcon = (ImageView) findViewById(R.id.iv_search_btn);
		ivSearchCancle = (ImageView) findViewById(R.id.iv_search_clear);
		ivSearchCancle.setOnClickListener(this);
		findViewById(R.id.ll_tryagain).setOnClickListener(this);
		etSearchInput.addTextChangedListener(texWatcher);
		etSearchInput.setOnEditorActionListener(editorActionListener);
		sv_all = (ScrollView) findViewById(R.id.sv_all);
		lvRecommend = (NoScrollListView) findViewById(R.id.lv_search_recommend);
		lvHistory = (NoScrollListView) findViewById(R.id.lv_search_history);
		lvSearchPredictPage = (NoScrollListView) findViewById(R.id.lv_search_prepare);
		lvRecommend.setOnItemClickListener(itemClickListener);
		lvHistory.setOnItemClickListener(itemClickListener);
		lvSearchPredictPage.setOnItemClickListener(itemClickListener);
		llSearchResult = (LinearLayout) findViewById(R.id.lv_search_result);
		llNoResult = (LinearLayout) findViewById(R.id.ll_no_result);
		llSearchMorePage = (LinearLayout) findViewById(R.id.ll_search_more);
		llSearchHotPage = (LinearLayout) findViewById(R.id.ll_search_hot);
		llSearchResultPage = (LinearLayout) findViewById(R.id.lv_search_layout);
		llSearchHistory = (LinearLayout) findViewById(R.id.ll_history);
		llNetErrorPage = (LinearLayout) findViewById(R.id.ll_net_error);
		loadingView = findViewById(R.id.loadingView);
		tvSearchKey = (TextView) findViewById(R.id.tv_search_key);
		etSearchInput.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				etSearchInput.setCursorVisible(true);
				return false;
			}
		});
	}
	
	private void initData(){
		List<String> list = searchSharedPre.getList(SearchSharedPre.HISTORY, String.class);
		if(list!=null){
			historyHotKey.addAll(list);
			showHistory();
		}
		historyAdapter = new SearchHistoryAdapter(this, historyHotKey);
		lvHistory.setAdapter(historyAdapter);
		predictAdapter = new SearchHistoryAdapter(SearchActivity.this, predictKey);
		predictAdapter.setIconStatus(false);
		lvSearchPredictPage.setAdapter(predictAdapter);
	}
	
	private OnItemClickListener itemClickListener = new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			fromHot = true;
			if(parent.getId()==R.id.lv_search_recommend){
				etSearchInput.setText(recommendHotKey.get(position));
			}else if(parent.getId()==R.id.lv_search_history){
				etSearchInput.setText(historyHotKey.get(position));
			}else if(parent.getId()==R.id.lv_search_prepare){
				etSearchInput.setText(predictKey.get(position));
			}
			loadSearchTask();
		}
	};
	
	private String getKey(){
		return etSearchInput.getText().toString().trim();
	}
	
	private void loadRecommedKey() {
		searchService.getHotKeys(new OnListResultListener<String>() {

			@Override
			public boolean onIntercept() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onFailure(int errorCode, String msg) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(List<String> result) {
				if(result!=null&&result.size()>0){
					recommendHotKey.clear();
					recommendHotKey.addAll(result);
					SearchHotAdapter simAdapter = new SearchHotAdapter(SearchActivity.this, recommendHotKey);
					lvRecommend.setAdapter(simAdapter);
				}
			}
		});
	}
	
	private void loadSearchTask() {
		final String key = getKey();
		searchService.getSearchResult(key, new OnResultListener<SearchVO>() {

			@Override
			public boolean onIntercept() {
				if(key.length()>200){
					ToastUtil.centerShowToast(SearchActivity.this, getString(R.string.search_too_many));
					return true;
				}
				if(loading){
					return true;
				}
				loading = true;
				hideSoftInputWindow();
				showSearching();
				return false;
			}

			@Override
			public void onSuccess(SearchVO result) {
				if(result != null){
					llSearchResult.removeAllViews();
					//channel
					List<Channel> chns = result.getChannelList();
					int chnCount = chns==null?0:chns.size();
					//program
					List<Program> epgs = result.getProgramsList();
					int epgCount = epgs==null?0:epgs.size();
					//video
					List<VOD> vods = result.getVodList();
					int vodCount = vods==null?0:vods.size();
					//chatroom
					List<ChatRoom> crs = result.getChatRoomList();
					int crCount = crs==null?0:crs.size();
					if(chnCount>0){
						llSearchResult.addView(new SearchItem<Channel>(SearchActivity.this, chns, key, new OnClickListener() {
							@Override
							public void onClick(View v) {
								showSearchMore(new SearchMoreLayout<Channel>(SearchActivity.this, key, SearchVO.CHANNEL_TYPE));
							}
						}, chnCount>2, epgCount==0&&vodCount==0&&crCount==0));
					}
					if(epgCount>0){
						llSearchResult.addView(new SearchItem<Program>(SearchActivity.this, epgs, key, new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								showSearchMore(new SearchMoreLayout<Program>(SearchActivity.this, key, SearchVO.PROGRAM_TYPE));
							}
						}, epgCount>2, vodCount==0&&crCount==0));
					}
					if(vodCount>0){
						llSearchResult.addView(new SearchItem<VOD>(SearchActivity.this, vods, key, new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								showSearchMore(new SearchMoreLayout<VOD>(SearchActivity.this, key, SearchVO.VOD_TYPE));
							}
						}, vodCount>2, crCount==0));
					}
					if(crCount>0){
						llSearchResult.addView(new SearchItem<ChatRoom>(SearchActivity.this, crs, key, new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								showSearchMore(new SearchMoreLayout<ChatRoom>(SearchActivity.this, key, SearchVO.CHATROOM_TYPE));
							}
						}, crCount>2, true));
					}
					//只要有数据
					if(chnCount==0&&epgCount==0&&vodCount==0&&crCount==0){
						showNoResult();
					}else{
						tvSearchKey.setText(key);
						showSearchResult();
					}
					updateSearchHistory();
				}else{
					showNoResult();
				}
				loading = false;
				loadingView.setVisibility(View.GONE);
			}

			@Override
			public void onFailure(int errorCode, String msg) {
				if(errorCode==AsyncTask.NETWORK_ERROR){
					showNetworkError();
				}else{
					showNoResult();
				}
				loading = false;
				loadingView.setVisibility(View.GONE);
			}

			private void updateSearchHistory() {
				int index = historyHotKey.indexOf(key);
				if(index!=-1){
					historyHotKey.remove(index);
				}
				historyHotKey.add(0, key);
				if(historyHotKey.size()>3){
					historyHotKey.remove(historyHotKey.size()-1);
				}
				searchSharedPre.put(SearchSharedPre.HISTORY, historyHotKey);
			}
		});
	}
	
	/**
	 * 开始搜索
	 */
	private void showSearching(){
		loadingView.setVisibility(View.VISIBLE);
		llSearchHotPage.setVisibility(View.GONE);
		llNetErrorPage.setVisibility(View.GONE);
		llSearchResultPage.setVisibility(View.GONE);
		lvSearchPredictPage.setVisibility(View.GONE);
	}
	
	/**
	 * 显示预测内容页面
	 */
	private void showSearchPredict(){
		llSearchMorePage.removeAllViews();
		llSearchHotPage.setVisibility(View.GONE);
		llSearchResultPage.setVisibility(View.GONE);
		llNetErrorPage.setVisibility(View.GONE);
		lvSearchPredictPage.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 显示搜索匹配的更多数据
	 */
	private void showSearchMore(View view){
		sv_all.setVisibility(View.GONE);
		llSearchMorePage.addView(view);
		llSearchHotPage.setVisibility(View.GONE);
		llSearchResultPage.setVisibility(View.VISIBLE);
		llNetErrorPage.setVisibility(View.GONE);
		lvSearchPredictPage.setVisibility(View.GONE);
	}
	
	/**
	 * 显示网络状况页面
	 */
	private void showNetworkError(){
		sv_all.setVisibility(View.VISIBLE);
		llSearchHotPage.setVisibility(View.GONE);
		llSearchResultPage.setVisibility(View.GONE);
		llNetErrorPage.setVisibility(View.VISIBLE);
		lvSearchPredictPage.setVisibility(View.GONE);
	}
	
	/**
	 * 显示搜索结果页面
	 */
	private void showSearchResult(){
		sv_all.setVisibility(View.VISIBLE);
		llSearchMorePage.removeAllViews();
		llSearchHotPage.setVisibility(View.GONE);
		llSearchResultPage.setVisibility(View.VISIBLE);
		llNetErrorPage.setVisibility(View.GONE);
		llSearchResult.setVisibility(View.VISIBLE);
		lvSearchPredictPage.setVisibility(View.GONE);
	}
	
	/**
	 * 显示搜索推荐页面
	 */
	private void showSearchHot(){
		sv_all.setVisibility(View.VISIBLE);
		llSearchMorePage.removeAllViews();
		llSearchHotPage.setVisibility(View.VISIBLE);
		llSearchResultPage.setVisibility(View.GONE);
		llNetErrorPage.setVisibility(View.GONE);
		lvSearchPredictPage.setVisibility(View.GONE);
		showHistory();
	}
	
	private void showNoResult(){
		showSearchHot();
		llSearchHistory.setVisibility(View.GONE);
		llNoResult.setVisibility(View.VISIBLE);
	}
	
	private void showHistory(){
		if(historyHotKey.size()>0){
			llSearchHistory.setVisibility(View.VISIBLE);
		}
		llNoResult.setVisibility(View.GONE);
	}
	
	/**
	 * 本地查找预测内容
	 */
	private void queryPreKey(){
		new LoadingDataTask() {
			List<String> chns;
			@Override
			public void onPreExecute() {
			}
			
			@Override
			public void onPostExecute() {
				predictKey.clear();
				if(chns!=null){
					predictKey.addAll(chns);
				}
				predictAdapter.notifyDataSetChanged(predictKey);
				showSearchPredict();
			}
			
			@Override
			public void doInBackground() {
				chns = chnService.getChannelLikeName(getKey(), 0, 3);
			}
		}.execute();
	}
	
	private TextWatcher texWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if (s.toString().trim().length() != 0) {
				ivSearchCancle.setVisibility(View.VISIBLE);
				ivSearchIcon.setImageResource(R.drawable.ic_search_white_24dp);
				ivSearchIcon.setOnClickListener(SearchActivity.this);
				if(!fromHot)
					queryPreKey();//输入字符有变化，本地查找匹配预测
				fromHot = false;
			} else {
				ivSearchCancle.setVisibility(View.GONE);
				ivSearchIcon.setImageResource(R.drawable.ic_search_white_t_24dp);
				ivSearchIcon.setOnClickListener(null);
				//清除输入框，显示推荐内容
				if(!loading){
					showSearchHot();
					historyAdapter.notifyDataSetChanged(historyHotKey);
				}
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
		}
	};
	
	private OnEditorActionListener editorActionListener = new OnEditorActionListener() {

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if(getKey().length()>0){
				loadSearchTask();
				hideSoftInputWindow();
			}
			return true;
		}
		
	};
	
	public void onBackPressed() {
		if(llSearchMorePage.getChildCount()>0){
			showSearchResult();
			return;
		}
		super.onBackPressed();
	};
	
	private void hideSoftInputWindow(){
		imm.hideSoftInputFromWindow(etSearchInput.getWindowToken(), 0);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_actionbar_back:
			hideSoftInputWindow();
			super.onBackPressed();
			break;
		case R.id.iv_search_clear:
			etSearchInput.getEditableText().clear();
			break;
		case R.id.iv_search_btn:
			loadSearchTask();
			break;
		case R.id.ll_tryagain:
			loadSearchTask();
			break;

		default:
			break;
		}
	}
}
