package com.star.mobile.video.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.ListView;

import com.star.cms.model.vo.SmartCardInfoVO;
import com.star.mobile.video.R;
import com.star.mobile.video.smartcard.SmartCardControlActivity;
import com.star.mobile.video.smartcard.SmartCardService;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.ToastUtil;
import com.star.util.loader.OnListResultListener;
import com.star.util.loader.OnResultListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class AllSmardCardListView extends LinearLayout{

	private android.widget.ListView cardLV;
	private Context context;
	private SmartCardService mSmartCardService;
	private List<SmartCardInfoVO> scInfo; //所有卡号
	private	SmartCardAdapter adapter;
	private int currentPosition;
	public AllSmardCardListView(Context context, boolean isPayment, boolean isBill, String cardNo) {
		this(context, null);
	}

	public AllSmardCardListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.view_card_num, this);
		this.context=context;
		cardLV=(ListView) findViewById(R.id.card_ListView);
	}
	public void setData( List<SmartCardInfoVO> scs){
		mSmartCardService = new SmartCardService(context);
		scInfo=new ArrayList<SmartCardInfoVO>();
		if(scs!=null){
			initData(scs);
		}else {
			getSmartCardNos();
		}
		cardLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
				adapter.changeState(position);
				currentPosition=position;
			}
		});
	}
	public SmartCardInfoVO getData(){
		if(scInfo.size()>0){
			return	scInfo.get(currentPosition);
		}else{
			return null;
		}
	}
	private void getSmartCardNos() {
		CommonUtil.showProgressDialog(context, null, context.getString(R.string.loading));
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
				initData(value);
			}
		});
	}
	private void initData(List<SmartCardInfoVO> value){
		scInfo = value;
		if (scInfo == null || scInfo.size() <= 0) {
			scListCallback.OnSize(0);
			return;
		}
		scListCallback.OnSize(value.size());
		adapter = new SmartCardAdapter(scInfo);
		adapter.changeState(0);
		cardLV.setAdapter(adapter);
		adapter.notifyDataSetChanged();
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
	private class SmartCardAdapter extends BaseAdapter{
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
				convertView=LayoutInflater.from(context).inflate(R.layout.view_choose_item, null);
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

	public ScListCallback getScListCallback() {
		return scListCallback;
	}

	public void setScListCallback(ScListCallback scListCallback) {
		this.scListCallback = scListCallback;
	}

	protected ScListCallback scListCallback;
	public interface ScListCallback{
		void OnSize(int size);
	}
}
