package com.star.mobile.video.view;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;

import com.star.mobile.video.util.LoadingDataTask;
import com.star.util.loader.AsyncTaskHolder;

/**
 * 
 * @author dujr
 *
 */
public class ListView extends android.widget.ListView{
	public Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WHAT_HIDE_LISTFOOTER:
				if(footerView != null) {
					removeFooterView(footerView);
					if(listener!=null){
						listener.onNoMoreData();
					}
				}
				break;
			}
		}
	};
	private final int WHAT_HIDE_LISTFOOTER = 1;
	private View footerView;
	private int requestCount;
	private LoadingListener listener;
	private int responseCount;
	private int offset = 0;
	private boolean loading = false;
	private boolean localBefore;

	public ListView(Context context) {
		super(context);
		init_();
	}
	
	public ListView(Context context, AttributeSet attrs) {
       super(context, attrs);
       init_();
    }

    public ListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init_();
    }
    
    private void init_() {
		setOnScrollListener(new MyOnScrollListener());
		footerView = new LoadingProgressBar(getContext());
		footerView.setVisibility(View.INVISIBLE);
		addFooterView(new View(getContext()));
	}
    
    private void reset_(){
    	responseCount = 0;
    	offset=0;
    	if(getFooterViewsCount()==1){
    		addFooterView(footerView);
    	}
    	footerView.setVisibility(View.INVISIBLE);
    }
    
    public void setRequestCount(int requestCount){
    	this.requestCount = requestCount;
    }
    
    public int getRequestCount(){
		if(requestCount==0)
			requestCount = 6;
		return requestCount;
    }

	private final class MyOnScrollListener implements OnScrollListener {
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			scrollToLoading(scrollState);
		}


		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
		}
	}
    
    public void scrollToLoading(int scrollState) {
    	if(getFooterViewsCount()==1)
    		return;
    	switch (scrollState) {
    	case OnScrollListener.SCROLL_STATE_IDLE:
    		int position = getLastVisiblePosition();
    		int total = getCount();
    		if (position == total-1) {
    			if(footerView.getVisibility()==View.INVISIBLE)
    				footerView.setVisibility(View.VISIBLE);
    			if(loading){
    				return;
    			}
    			if (responseCount < getRequestCount()) {
    				return;
    			}
    			if(listener!=null){
    				if(localBefore)
    					loadingDataFromLocal();
    				loadingData();
    			}
    		}
    		break;
    	}
    }
    
    public void loadingData(boolean localBefore){
    	reset_();
    	this.localBefore = localBefore;
    	if(localBefore){
    		loadingDataFromLocal();
    	}else{
    		loadingData();
    	}
    }

	private void loadingData() {
		new LoadingDataTask() {
			private List<?> datas;
			@Override
			public void onPreExecute() {
			}
			
			@Override
			public void onPostExecute() {
				if(datas!=null && datas.size()>0 && localBefore){
					if(listener.getFillList()!=null)
						listener.getFillList().clear();
					localBefore = false;
					offset = 0;
				}else{
					if(listener.getFillList()!=null&&listener.getFillList().size()==0)
						localBefore = false;
				}
				/**
				 * 无论能不能滑动都先计算开始的值
				 */
				offset +=getRequestCount();
				postData(datas);
			}
			
			@Override
			public void doInBackground() {
				loading = true;
				datas = listener.loadingS(offset, getRequestCount());
				Log.i("MyListView", "loading from server,offset="+offset+",count="+getRequestCount());
			}
		}.execute();
	}
	
	private void loadingDataFromLocal() {
		new LoadingDataTask() {
			private List<?> datas;
			@Override
			public void onPreExecute() {
			}
			
			@Override
			public void onPostExecute() {
				postData(datas);
				loadingData();
			}

			@Override
			public void doInBackground() {
				loading = true;
				datas = listener.loadingL(offset, getRequestCount());
				Log.i("MyListView", "loading from local,offset="+offset+",count="+getRequestCount());
			}
		}.execute();
	}
	
	private <T> void postData(List<T> datas) {
		loading = false;
		if(datas==null||(datas.size()==0&&localBefore))
			return;
		responseCount = datas.size();
		listener.loadPost(datas);
		if(responseCount<getRequestCount())
			handler.sendEmptyMessageDelayed(WHAT_HIDE_LISTFOOTER, 1000);
	}
	
    public interface LoadingListener<T>{
    	/**
    	 * loading from server
    	 * @param offset
    	 * @param requestCount
    	 * @return
    	 */
    	abstract List<T> loadingS(int offset, int requestCount);
    	abstract void loadPost(List<T> responseDatas);
    	/**
    	 * loading from local
    	 * @param offset
    	 * @param requestCount
    	 * @return
    	 */
    	abstract List<T> loadingL(int offset, int requestCount);
    	abstract List<T> getFillList();
    	abstract void onNoMoreData();
    }
    
    public <T> void setLoadingListener(LoadingListener<T> listener){
    	this.listener = listener;
    }
}
