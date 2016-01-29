package com.star.mobile.video.smartcard;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.star.cms.model.Command;
import com.star.cms.model.vo.ChannelVO;
import com.star.cms.model.vo.SMSHistory;
import com.star.mobile.video.R;
import com.star.mobile.video.adapter.DemandVideoAdapter;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.ToastUtil;
import com.star.mobile.video.view.ListView.LoadingListener;
import com.star.ui.HorizontalListView;
import com.star.util.loader.OnListResultListener;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * MyOrder页面
 * @author Lee
 * @date 2016/01/04
 * @version 1.0
 *
 */
public class MyOrderActivity extends BaseActivity implements OnClickListener{
	private com.star.mobile.video.view.ListView mMyOrderListView;
	private View mSmartCardLoading;
	private SmartCardService mSmartCardService;
	private List<SMSHistory> mMyOrderLists = new ArrayList<SMSHistory>();
	private MyOrderAdapter mMyOrderAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myorder);
		initView();
		initData();
	}
	/**
	 * view初始化
	 */
	private void initView() {
		((ImageView) findViewById(R.id.iv_actionbar_back)).setOnClickListener(this);
		((TextView)findViewById(R.id.tv_actionbar_title)).setText(getString(R.string.my_order));
		mMyOrderListView = (com.star.mobile.video.view.ListView) findViewById(R.id.my_order_listview);
		mSmartCardLoading = (View) findViewById(R.id.smartcard_loadingView);
	}
	/**
	 * 数据初始化
	 */
	private void initData(){
		mSmartCardService = new SmartCardService(this);
		mMyOrderAdapter = new MyOrderAdapter(MyOrderActivity.this, mMyOrderLists);
		mMyOrderListView.setAdapter(mMyOrderAdapter);
		if (mMyOrderLists != null && mMyOrderLists.size()>0) {
			mMyOrderLists.clear();
		}
		getOrderList();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
//		if (mMyOrderLists != null && mMyOrderLists.size()>0) {
//			mMyOrderLists.clear();
//		}
//		getOrderList();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_actionbar_back:
			onBackPressed();
			break;

		default:
			break;
		}
	}
	/**
	 * 获得订单列表信息
	 */
	private void getOrderList(){
		mMyOrderListView.setLoadingListener(new LoadingListener<SMSHistory>() {
			
			@Override
			public List<SMSHistory> loadingS(int offset, int requestCount) {
				return mSmartCardService.getOrderList(offset,requestCount,true);
			}

			@Override
			public void loadPost(List<SMSHistory> datas) {
				mSmartCardLoading.setVisibility(View.GONE);
				if(datas!=null && datas.size()>0){
					mMyOrderLists.addAll(datas);
					mMyOrderAdapter.setMyOrderData(mMyOrderLists);
					mMyOrderListView.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							SMSHistory smsHistory = mMyOrderLists.get(position);
							Intent it = new Intent();
							it.putExtra("smsHistoryID", smsHistory.getId());
							it.putExtra("smsHistoryType", smsHistory.getType());
							it.setClass(MyOrderActivity.this, MyOrderDetailActivity .class);
							CommonUtil.startActivityForResult(MyOrderActivity.this, it, 200);
						}
					});
				}else {
					ToastUtil.centerShowToast(MyOrderActivity.this, getString(R.string.no_data));
				}
			}

			@Override
			public List<SMSHistory> loadingL(int offset, int requestCount) {
				mSmartCardLoading.setVisibility(View.GONE);
				return mSmartCardService.getOrderListFromLocal(MyOrderActivity.this, offset, requestCount);
			}

			@Override
			public List<SMSHistory> getFillList() {
				return mMyOrderLists;
			}

			@Override
			public void onNoMoreData() {
			}
		});
		mMyOrderListView.loadingData(true);
		
	}

}
