/**
 * 
 */
package com.star.mobile.video.smartcard.recharge;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.BitArray;
import com.google.zxing.oned.OneDReader;
import com.star.cms.model.vo.ExchangeVO;
import com.star.cms.model.vo.SmartCardInfoVO;
import com.star.mobile.video.R;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.model.NETException;
import com.star.mobile.video.service.UserService;
import com.star.mobile.video.smartcard.SmartCardControlActivity;
import com.star.mobile.video.smartcard.SmartCardService;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.DefaultLoadingTask;
import com.star.mobile.video.util.LoadingDataTask;
import com.star.mobile.video.util.ToastUtil;
import com.star.mobile.video.view.AllSmardCardListView;
import com.star.util.Log;
import com.star.util.loader.OnListResultListener;
import com.star.util.loader.OnResultListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author xbp


 * 2015年11月30日
*/
public class RechargeCardNumActivity extends BaseActivity implements OnClickListener,AllSmardCardListView.ScListCallback{
	
	 
	private Button btnGo;
	private ExchangeVO selectExchange;
	private AllSmardCardListView allSmardCardListView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_card_num);
		selectExchange = (ExchangeVO) getIntent().getSerializableExtra("exchange");
		initView();
		allSmardCardListView.setData(null);
	}



	/**
	 * 
	 */
	private void initView() {
		allSmardCardListView=(AllSmardCardListView) findViewById(R.id.sc_list_view);
		allSmardCardListView.setScListCallback(this);
		btnGo=(Button) findViewById(R.id.bt_mob_reg_go);
		btnGo.setOnClickListener(this);	
		((ImageView) findViewById(R.id.iv_actionbar_back)).setOnClickListener(this);
		((TextView) findViewById(R.id.tv_actionbar_title)).setText(RechargeCardNumActivity.this.getString(R.string.choose_card));
		
	}


	@Override
	public void onClick(View v) {
		if( v.getId()==R.id.iv_actionbar_back){
			onBackPressed();
		}
		if(v.getId()==R.id.bt_mob_reg_go){
			if(allSmardCardListView.getData()!=null){
				 if(selectExchange.getTypeGet()==ExchangeVO.TYPE_COMMON||selectExchange.getTypeGet()==ExchangeVO.TYPE_NEW_CUSTOMER || selectExchange.getTypeGet() == ExchangeVO.FREE_COUPON){
						Intent intent = new Intent(RechargeCardNumActivity.this, RechargeCouponActivity.class);
						intent.putExtra("smartcardinfovo",(Serializable) allSmardCardListView.getData());
						intent.putExtra("exchange", selectExchange);
						CommonUtil.startActivity((Activity)RechargeCardNumActivity.this, intent);
					}else if(selectExchange.getTypeGet()==ExchangeVO.TYPE_EXCHANGE||selectExchange.getTypeGet()==ExchangeVO.CLUB_COUPON){
						Intent intent = new Intent(RechargeCardNumActivity.this, RechargeSmartCardActivity.class);
						intent.putExtra("smartcardinfovo",(Serializable) allSmardCardListView.getData());
						intent.putExtra("exchange", selectExchange);
						intent.putExtra("hideCoupon", true);
						CommonUtil.startActivity((Activity)RechargeCardNumActivity.this, intent);
					}else{ 
						Intent intent = new Intent(RechargeCardNumActivity.this, RechargeSmartCardActivity.class);
						intent.putExtra("exchange", selectExchange);
						intent.putExtra("smartcardinfovo",(Serializable) allSmardCardListView.getData());
						intent.putExtra("hideCoupon", false);
						CommonUtil.startActivity((Activity)RechargeCardNumActivity.this, intent);
					}

			}else{
				
			} 
		}
		
	}

	@Override
	public void OnSize(int size) {
		if(size>0){
			btnGo.setBackgroundResource(R.drawable.orange_button_bg);
		}
	}
}
