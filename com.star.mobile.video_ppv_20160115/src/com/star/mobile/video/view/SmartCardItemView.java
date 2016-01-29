package com.star.mobile.video.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.star.cms.model.vo.SmartCardInfoVO;
import com.star.mobile.video.R;
import com.star.mobile.video.model.NETException;
import com.star.mobile.video.service.UserService;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.smartcard.SmartCardService;
import com.star.mobile.video.util.DensityUtil;
import com.star.mobile.video.util.LoadingDataTask;
import com.star.util.loader.OnResultListener;

public class SmartCardItemView extends LinearLayout{

	private Scroller mScroller;
	private TextView tvSmartcardNo;
	private TextView tvCardMoney;
	private TextView tvPackageName;
	private TextView tvRemainingDays;//剩余天数
	private ImageView ivOptionBtn;
	private Context context;
	private LoadingView loadMoney;
	private NoSmartCardInfoView noMoney;
	private NoSmartCardInfoView noPackageName;
	private NoSmartCardInfoView noDays;
	protected UserService userService;
	private SmartCardService mSmartCardService;
	public SmartCardItemView(Context context) {
		this(context, null);
	}
	
	public SmartCardItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		LayoutInflater.from(context).inflate(R.layout.view_smartcard_item, this);
		mScroller = new Scroller(context);
		userService = new UserService();
		mSmartCardService = new SmartCardService(context);
		initView();
	}

	
	private void initView() {
		tvSmartcardNo = (TextView) findViewById(R.id.tv_smartcard_num);
		tvCardMoney = (TextView) findViewById(R.id.tv_card_money);
		tvPackageName = (TextView) findViewById(R.id.tv_package_name);
		tvRemainingDays = (TextView) findViewById(R.id.tv_remaining_days);
		ivOptionBtn = (ImageView) findViewById(R.id.iv_option_btn);
		loadMoney = (LoadingView) findViewById(R.id.load_money);
		noMoney = (NoSmartCardInfoView) findViewById(R.id.no_info_money);
		noPackageName = (NoSmartCardInfoView) findViewById(R.id.no_info_package);
		noDays = (NoSmartCardInfoView) findViewById(R.id.no_info_days);
		
		noMoney.setMsg("Balance");
		noPackageName.setMsg("Bouquest");
		noDays.setMsg("Remaining days");
	}
	
	public void setNoInfoVisibility(int visibility) {
		noMoney.setVisibility(visibility);
		noPackageName.setVisibility(visibility);
		noDays.setVisibility(visibility);
	}
	
	
	/**
	 * 设置卡号
	 * @param cardNo
	 */
	public void setSmartCardNo(String cardNo) {
		tvSmartcardNo.setText(cardNo);
	}
	
	/**
	 * 设置卡的余额
	 * @param money
	 */
	public void setCardMoney(Double money) {
		tvCardMoney.setText(SharedPreferencesUtil.getCurrencSymbol(context)+money);
	}
	
	public String getCardMoney() {
		return tvCardMoney.getText().toString().substring(2);
	}
	
	public void setCardMoneyTextColor(int color) {
		tvCardMoney.setTextColor(getResources().getColor(color));
	}
	
	/**
	 * 设置节目包名称
	 * @param name
	 */
	public void setPackageName(String name) {
		tvPackageName.setText(name);
	}
	
	public void setPackageNameTextColor(int color) {
		tvPackageName.setTextColor(color);
	}
	
	/**
	 * 剩余多少天
	 * @param days
	 */
	public void setRemainingDays(String days) {
		tvRemainingDays.setText(days);
//		Log.i("TAG", "days"+days);
//		Log.i("TAG", System.currentTimeMillis()+"");
//		int day = DateFormat.getDiffDays(d, new Date());
//		if(day < 7) {
//			tvRemainingDays.setText(day+" Days Left");
//			setRemainingDaysTextColor(getResources().getColor(R.color.red));
//		} else {
//			setRemainingDaysTextColor(getResources().getColor(R.color.smartcard_info));
//			tvRemainingDays.setText(day+" Days Left");
//		}
	}
	
	/**
	 * 剩余多少天
	 * @param days
	 */
	public void setRemainingDays(Integer days) {
		if(days!=null && days<=7){
			tvRemainingDays.setTextColor(context.getResources().getColor(R.color.check_mob_tex));
		}else{
			tvRemainingDays.setTextColor(context.getResources().getColor(R.color.alert_setting_text));
		}
		if(days>0){
			String text = context.getString(R.string.days_left);
			if(days==1)
				text = context.getString(R.string.day_left);
			tvRemainingDays.setText(days+" "+text);
		}else{
			tvRemainingDays.setText(context.getString(R.string.please_recharge));
		}
	}
	
	public void getDetailSmartCardInfo(final SmartCardInfoVO vo) {
		mSmartCardService.getSmartCardInfo(vo.getSmardCardNo(), new OnResultListener<SmartCardInfoVO>() {
			
			@Override
			public void onSuccess(SmartCardInfoVO value) {
				if(value != null) {
					fillData(value);
				}
			}
			
			@Override
			public boolean onIntercept() {
				fillData(vo);
				return false;
			}
			
			@Override
			public void onFailure(int errorCode, String msg) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	private void fillData(SmartCardInfoVO sc){
		if(sc.getStopDays()!=null){
			setRemainingDays(sc.getStopDays());
		}else if(!TextUtils.isEmpty(sc.getPenaltyStop())){
			setRemainingDays(sc.getPenaltyStop());
		}
		Double money = sc.getMoney();
		if(money != null) {
			setCardMoney(money);
			setLoadMoneyVisibility(View.GONE);
		} 
		if(sc.getCode() != null) {
			setLoadMoneyVisibility(View.GONE);
			if(sc.getCode().equals(SmartCardInfoVO.NET_WORK_ERROR)) {
				setVisibility(View.VISIBLE);
				setVisibility(View.VISIBLE);
				setNoInfoVisibility(View.VISIBLE);
			} else {
				setVisibility(View.GONE);
				setVisibility(View.GONE);
			}
		}
		String programName = sc.getProgramName();
		if(programName != null) {
			setPackageName(programName);
		} 
		setSmartCardNo(formatSmarCardNo(sc.getSmardCardNo()));
	}
	
	private String formatSmarCardNo(String cmardNo) {
		StringBuffer sb = new StringBuffer();
		if(cmardNo != null) {
			for(int i = 0;i < cmardNo.length();i++) {
				if(i % 4 == 0 && i != 0) {
					sb.append("-");
				}
				sb.append(cmardNo.charAt(i));
			}
			return sb.toString();
		} else {
			return "";
		}
		
	}
	
	public void setRemainingDaysTextColor(int color) {
		tvRemainingDays.setTextColor(color);
	}
	
	public void setOptionBtnClick(OnClickListener l) {
		ivOptionBtn.setOnClickListener(l);
	}
	
	public void setOptionBtnTag(Object o) {
		ivOptionBtn.setTag(o);
	}
	
	public void showOptionBtn() {
		mScroller.startScroll(0, 0, DensityUtil.dip2px(getContext(), 60), 0, 300);
		invalidate();
	}
	
	public void hideOptionBtn() {
		mScroller.startScroll(getLeft(), 0, 0, 0, 300);
		invalidate();
	}
	
	public void setLoadMoneyVisibility(int visibility) {
		loadMoney.setVisibility(visibility);
	}
	
	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		}
	}
}

