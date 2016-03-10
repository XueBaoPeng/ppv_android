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
import com.star.util.Logger;
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
import android.widget.LinearLayout;
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
	private LinearLayout mMyOrderNodatall;
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
		mMyOrderNodatall = (LinearLayout) findViewById(R.id.my_order_nodata_rl);
	}
	/**
	 * 数据初始化
	 */
	private void initData(){
		mSmartCardService = new SmartCardService(this);
		if (mMyOrderLists != null && mMyOrderLists.size()>0) {
			mMyOrderLists.clear();
		}
		mMyOrderAdapter = new MyOrderAdapter(MyOrderActivity.this, mMyOrderLists);
		mMyOrderListView.setAdapter(mMyOrderAdapter);
		getOrderList();
	}

	@Override
	protected void onResume() {
		super.onResume();
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
	private int mCurrentPostiont = -1;
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
				mMyOrderLists.addAll(datas);
				if(mMyOrderLists!=null && mMyOrderLists.size()>0){
					mMyOrderNodatall.setVisibility(View.GONE);
					mMyOrderAdapter.setMyOrderData(mMyOrderLists);
					mMyOrderListView.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							mCurrentPostiont = position;
							SMSHistory smsHistory = mMyOrderLists.get(position);
							Intent it = new Intent();
							it.putExtra("smsHistoryID", smsHistory.getId());
							it.putExtra("smsHistoryType", smsHistory.getType());
							it.setClass(MyOrderActivity.this, MyOrderDetailActivity .class);
							CommonUtil.startActivityForResult(MyOrderActivity.this, it, 200);
						}
					});
				}else {
					mMyOrderNodatall.setVisibility(View.VISIBLE);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 200) {
			if (data != null) {
				Bundle bundle = data.getExtras();
				if (bundle != null) {
					int resultStatus = bundle.getInt("resultSatus");
					int status = bundle.getInt("status");
					SMSHistory smsHistory = mMyOrderLists.get(mCurrentPostiont);
					smsHistory.setAcceptStatus(resultStatus);
					smsHistory.setProgress(status);
					mMyOrderLists.set(mCurrentPostiont,smsHistory);
					mMyOrderAdapter.setMyOrderData(mMyOrderLists);
				} else {
					Log.i("initData", "bundle is null");
				}
			} else {
				Log.i("initData", "data is null");
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
