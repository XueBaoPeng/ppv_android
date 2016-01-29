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
public class RechargeCardNumActivity extends BaseActivity implements OnClickListener{
	
	 
	private ListView cardLV;
	private List<SmartCardInfoVO> scInfo; //所有卡号
	private	SmartCardAdapter adapter;
	private Button btnGo;
	private UserService userService;
	private int currentPosition;
	private ExchangeVO selectExchange;
	private SmartCardService mSmartCardService;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_card_num);
		userService=new UserService();
		selectExchange = (ExchangeVO) getIntent().getSerializableExtra("exchange");
		initView();
		initData();
	}

	/**
	 * 
	 */
	private void initData() {
	 mSmartCardService = new SmartCardService(this);
	 scInfo=new ArrayList<SmartCardInfoVO>();
	 getSmartCardNos();
	 cardLV.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent,
					View view, int position, long id) {
				adapter.changeState(position);
				currentPosition=position;
				btnGo.setBackgroundResource(R.drawable.orange_button_bg);
			}
		});
	}
	private void getSmartCardNos() {
		CommonUtil.showProgressDialog(RechargeCardNumActivity.this, null, getString(R.string.loading));
		mSmartCardService.getAllSmartCardInfos(new OnListResultListener<SmartCardInfoVO>() {
			
			@Override
			public boolean onIntercept() {
				return false;
			}
			
			@Override
			public void onFailure(int errorCode, String msg) {
				CommonUtil.closeProgressDialog();
			}
			
			@Override
			public void onSuccess(List<SmartCardInfoVO> value) {
				CommonUtil.closeProgressDialog();
				scInfo = value;
				if(scInfo == null || scInfo.size() <= 0) {
					ToastUtil.centerShowToast(RechargeCardNumActivity.this, getString(R.string.binding_smart_card));
					CommonUtil.startActivity(RechargeCardNumActivity.this, SmartCardControlActivity.class);
					RechargeCardNumActivity.this.finish();
					return;
				} 
				adapter=new SmartCardAdapter(scInfo);
				adapter.changeState(0);
				btnGo.setBackgroundResource(R.drawable.orange_button_bg);
				cardLV.setAdapter(adapter);
				adapter.notifyDataSetChanged();	
			}
		});
	}
	/**
	 * @param smardInfo 
	 * 
	 */
	protected void initSmartcardData(List<SmartCardInfoVO> smardInfo) {
		for(SmartCardInfoVO sm:smardInfo){
			getDetailSmartCardInfo(sm );
		}	
	}
	private void getDetailSmartCardInfo(final SmartCardInfoVO vo ) {
		mSmartCardService.getSmartCardInfo(vo.getSmardCardNo(), new OnResultListener<SmartCardInfoVO>() {
			
			@Override
			public void onSuccess(SmartCardInfoVO value) {
				if (value != null) {
					scInfo.add(value);
				}
			}
			
			@Override
			public boolean onIntercept() {
				return false;
			}
			
			@Override
			public void onFailure(int errorCode, String msg) {
				
			}
		});
	}

	/**
	 * 
	 */
	private void initView() {
		cardLV=(ListView) findViewById(R.id.card_ListView);
		btnGo=(Button) findViewById(R.id.bt_mob_reg_go);
		btnGo.setOnClickListener(this);	
		((ImageView) findViewById(R.id.iv_actionbar_back)).setOnClickListener(this);
		((TextView) findViewById(R.id.tv_actionbar_title)).setText(RechargeCardNumActivity.this.getString(R.string.choose_card));;
		
	}

	private class SmartCardAdapter extends BaseAdapter{
		private Context context;
		private List<SmartCardInfoVO> mDatas;
		private int pos;
		private int lastPosition = -1;   //lastPosition 记录上一次选中的图片位置，-1表示未选中                             
		private Vector<Boolean> vector = new Vector<Boolean>();// 定义一个向量作为选中与否容器   
		public SmartCardAdapter(List<SmartCardInfoVO> datas){
			this.mDatas=datas;
			 for (int i = 0; i < mDatas.size(); i++) {
		            vector.add(false);
		        }
		}
		@Override
		public int getCount() {
			return mDatas.size();
		}

		@Override
		public Object getItem(int position) {
			return mDatas.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder=null;
			SmartCardInfoVO data=mDatas.get(position);
			if(convertView==null){
				holder=new ViewHolder();
				convertView=LayoutInflater.from(RechargeCardNumActivity.this).inflate(R.layout.view_choose_item, null);
				holder.tvName=(TextView) convertView.findViewById(R.id.tv_item_name_l);
				holder.image=(ImageView) convertView.findViewById(R.id.iv_item_icon);
				convertView.setTag(holder);
			}else{
				holder=(ViewHolder) convertView.getTag();
			}
			String symb=formatSmarCardNo(data.getSmardCardNo());
			/*SpannableStringBuilder style=new SpannableStringBuilder(symb);
			style.setSpan(new ForegroundColorSpan(Color.rgb(30, 144,255)),0, symb.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); */			
		 
			holder.tvName.setText(symb);
			if(vector.elementAt(position) == true){
	            holder.image.setImageResource(R.drawable.sel);
	        }else{
	            holder.image.setImageResource(R.drawable.no_sel);
	        }
			return  convertView;
		}
		
		
		 class ViewHolder{
			 TextView tvName;
			 ImageView image;
		}
		 /**
		     * 修改选中时的状态
		     * @param position
		     */
		    public void changeState(int position){    
		        if(lastPosition != -1)    
		            vector.setElementAt(false, lastPosition);                   //取消上一次的选中状态    
		        vector.setElementAt(!vector.elementAt(position), position);     //直接取反即可    
		        lastPosition = position;                                        //记录本次选中的位置    
		        notifyDataSetChanged();                                         //通知适配器进行更新    
		    }    
	}
	private String formatSmarCardNo(String cmardNo) {
		StringBuffer sb = new StringBuffer();
		if (cmardNo != null) {
			for (int i = 0; i < cmardNo.length(); i++) {
				if (i % 4 == 0 && i != 0) {
					sb.append("-");
				}
				sb.append(cmardNo.charAt(i));
			}
			return sb.toString();
		} else {
			return "";
		}

	}
	@Override
	public void onClick(View v) {
		if( v.getId()==R.id.iv_actionbar_back){
			onBackPressed();
		}
		if(v.getId()==R.id.bt_mob_reg_go){
			if(scInfo.get(currentPosition)!=null){
				 if(selectExchange.getTypeGet()==ExchangeVO.TYPE_COMMON||selectExchange.getTypeGet()==ExchangeVO.TYPE_NEW_CUSTOMER || selectExchange.getTypeGet() == ExchangeVO.FREE_COUPON){
						Intent intent = new Intent(RechargeCardNumActivity.this, RechargeCouponActivity.class);
						intent.putExtra("smartcardinfovo",(Serializable) scInfo.get(currentPosition));
						intent.putExtra("exchange", selectExchange);
						CommonUtil.startActivity((Activity)RechargeCardNumActivity.this, intent);
					 	finish();
					}else if(selectExchange.getTypeGet()==ExchangeVO.TYPE_EXCHANGE||selectExchange.getTypeGet()==ExchangeVO.CLUB_COUPON){
						Intent intent = new Intent(RechargeCardNumActivity.this, RechargeSmartCardActivity.class);
						intent.putExtra("smartcardinfovo",(Serializable) scInfo.get(currentPosition));
						intent.putExtra("exchange", selectExchange);
						intent.putExtra("hideCoupon", true);
						CommonUtil.startActivity((Activity)RechargeCardNumActivity.this, intent);
					}else{ 
						Intent intent = new Intent(RechargeCardNumActivity.this, RechargeSmartCardActivity.class);
						intent.putExtra("exchange", selectExchange);
						intent.putExtra("smartcardinfovo",(Serializable) scInfo.get(currentPosition));
						intent.putExtra("hideCoupon", false);
						CommonUtil.startActivity((Activity)RechargeCardNumActivity.this, intent);
					}

			}else{
				
			} 
		}
		
	}
}
