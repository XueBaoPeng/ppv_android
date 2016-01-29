package com.star.mobile.video.smartcard;

import java.io.Serializable;
import java.util.List;

import com.star.cms.model.enm.SmartCardType;
import com.star.cms.model.vo.SmartCardInfoVO;
import com.star.mobile.video.R;
import com.star.mobile.video.activity.AccountBillActivity;
import com.star.mobile.video.base.BaseFragment;
import com.star.mobile.video.changebouquet.ChangeBouquetActivity;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.smartcard.recharge.RechargeSmartCardActivity;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.ToastUtil;
import com.star.mobile.video.view.LoadingView;
import com.star.util.loader.OnResultListener;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * smart card详情页面
 * 
 * @author Lee
 * @version 1.0
 * @date 2015/11/20
 *
 */
public class FragmentSmartCardInfo extends BaseFragment implements OnClickListener {
	private View mView;
	private ImageView mSmartCardImageView;
	private TextView mSmartCardNumber;
	private TextView mSmartCardBalance;
	private LoadingView mLoadDetailBalance;
	private TextView mSmartCardPackage;
	private LoadingView mLoadDetailPackage;
	private LoadingView mLoadDetailCustomerName;
	private TextView mSmartCardDeadLine;
	private TextView mSmartCardCustomerName;
	private TextView mSmartCardCustomerPhone;
	private SmartCardInfoVO mSmartCardInfoVO;
	private List<SmartCardInfoVO> mSmartinfos;
	private String mChangePkgSmartCardNumber;
	private SmartCardService mSmartcardService;
	private static boolean isAllowDeleteSmartCard = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		if (bundle != null) {
			mSmartCardInfoVO = (SmartCardInfoVO) bundle.getSerializable("smartCardInfoVO");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_smart_card_info, container, false);
		initView();
		return mView;
	}

	private void initView() {
		mSmartCardImageView = (ImageView) mView.findViewById(R.id.smart_card_imagview);
		mSmartCardNumber = (TextView) mView.findViewById(R.id.smart_card_number);
		mSmartCardCustomerName = (TextView) mView.findViewById(R.id.smart_card_customer_name);
		mSmartCardCustomerPhone = (TextView) mView.findViewById(R.id.smart_card_customer_phone);
		mSmartCardDeadLine = (TextView) mView.findViewById(R.id.smart_card_deadline);
		mSmartCardBalance = (TextView) mView.findViewById(R.id.smart_card_detail_balance);
		mLoadDetailBalance = (LoadingView) mView.findViewById(R.id.load_detail_balance);
		mSmartCardPackage = (TextView) mView.findViewById(R.id.bouquet_package_textview);
		mLoadDetailPackage = (LoadingView) mView.findViewById(R.id.load_detail_package);
		mLoadDetailCustomerName = (LoadingView) mView.findViewById(R.id.load_detail_customer_name);
		mView.findViewById(R.id.balance_rl).setOnClickListener(this);
		mView.findViewById(R.id.bouquet_rl).setOnClickListener(this);
		mView.findViewById(R.id.account_bill_rl).setOnClickListener(this);
	}

	public void initData() {
		mSmartcardService = new SmartCardService(getActivity());
		if (mSmartCardInfoVO != null) {
			getDetailSmartCardInfo(mSmartCardInfoVO);
		} else {
			Toast.makeText(getActivity(), "mSmartCardInfoVO is null", Toast.LENGTH_LONG).show();
		}
	}
	

	@Override
	public void onResume() {
		Log.i("initData", "FragmentSmartCardInfo onResume");
		initData();
		super.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (this.isAdded()){
			getActivity().getSupportFragmentManager().putFragment(outState, "mFragment", this);
		}
	}

	/**
	 * 传递参数的接口
	 * 
	 * @return
	 */
	public static FragmentSmartCardInfo newInstance(SmartCardInfoVO smartCardInfoVO) {
		FragmentSmartCardInfo f = new FragmentSmartCardInfo();
		Bundle bundle = new Bundle();
		bundle.putSerializable("smartCardInfoVO", smartCardInfoVO);
		f.setArguments(bundle);
		return f;
	}

	public void setNoInfoVisibility(int visibility) {
		mSmartCardBalance.setVisibility(visibility);
		mSmartCardPackage.setVisibility(visibility);
	}

	public void setSmartinfos(List<SmartCardInfoVO> smartinfos) {
		this.mSmartinfos = smartinfos;
	}

	public void setChangePkgSmartCardNO(String changePkgSmartCardNumber) {
		this.mChangePkgSmartCardNumber = changePkgSmartCardNumber;
	}

	/**
	 * 设置卡号
	 * 
	 * @param cardNo
	 */
	public void setSmartCardNo(String cardNo) {
		mSmartCardNumber.setText(cardNo);
	}

	/**
	 * 设置卡的余额
	 * 
	 * @param money
	 */
	public void setCardMoney(Double money) {
		mSmartCardBalance.setText(getResources().getString(R.string.balance_s)+":"+SharedPreferencesUtil.getCurrencSymbol(getActivity()) + money);
	}

	/**
	 * 用户名
	 * 
	 * @param customerName
	 */
	public void setCustomerName(String customerName) {
		mSmartCardCustomerName.setText(customerName);
	}

	/**
	 * 用户电话
	 * 
	 * @param customerPhone
	 */
	public void setCustomerPhone(String customerPhone) {
		mSmartCardCustomerPhone.setText(customerPhone);
	}

	public String getCardMoney() {
		return mSmartCardBalance.getText().toString().substring(2);
	}

	public void setCardMoneyTextColor(int color) {
		mSmartCardBalance.setTextColor(getResources().getColor(color));
	}

	/**
	 * 设置节目包名称
	 * 
	 * @param name
	 */
	public void setPackageName(String name) {
		mSmartCardPackage.setText(name);
	}

	public void setPackageNameTextColor(int color) {
		mSmartCardPackage.setTextColor(color);
	}

	/**
	 * 剩余多少天
	 * 
	 * @param days
	 */
	public void setRemainingDays(String days) {
		mSmartCardDeadLine.setText(days);
	}

	/**
	 * 剩余多少天
	 * 
	 * @param days
	 */
	public void setRemainingDays(Integer days) {
		if (days != null && days <= 7) {
			mSmartCardDeadLine.setTextColor(getActivity().getResources().getColor(R.color.check_mob_tex));
		} else {
			mSmartCardDeadLine.setTextColor(getActivity().getResources().getColor(R.color.alert_setting_text));
		}
		if (days > 0) {
			String text = getActivity().getString(R.string.days_left);
			if (days == 1)
				text = getActivity().getString(R.string.day_left);
			mSmartCardDeadLine.setText(days + " " + text);
		} else {
			mSmartCardDeadLine.setText(getActivity().getString(R.string.please_recharge));
		}
	}

	/**
	 * 获得智能卡的详情信息
	 * @param vo
	 */
	private void getDetailSmartCardInfo(final SmartCardInfoVO vo) {
		mSmartcardService.getSmartCardInfo(vo.getSmardCardNo(), new OnResultListener<SmartCardInfoVO>() {
			
			@Override
			public void onSuccess(SmartCardInfoVO scv) {
				if(!isAdded())
					return;
				hideDetailLoading();
				if (scv != null) {
					setData(vo, scv);
					setPackageNameData(scv);
				}
			}

			
			@Override
			public boolean onIntercept() {
				fillData(vo);
				String programName = vo.getProgramName();
				if (programName != null) {
					setPackageName(programName);
				}
				return false;
			}
			
			@Override
			public void onFailure(int errorCode, String msg) {
				if(!isAdded())
					return;
				hideDetailLoading();
			}
		});
		
	}

	public boolean isAllowDeleteSmartCard() {
		return isAllowDeleteSmartCard;
	}

	public void setAllowDeleteSmartCard(boolean isAllowDeleteSmartCard) {
		this.isAllowDeleteSmartCard = isAllowDeleteSmartCard;
	}
	/**
	 * 设置包名
	 * @param scv
	 */
	private void setPackageNameData(SmartCardInfoVO scv) {
		String programName = scv.getProgramName();
		if (programName != null) {
			setPackageName(programName);
		}else {
			setPackageName(getString(R.string.no_bouquet));
		}
	}
	/**
	 * 隐藏loading
	 */
	private void hideDetailLoading() {
		setAllowDeleteSmartCard(true);
		mLoadDetailBalance.setVisibility(View.GONE);
		mLoadDetailPackage.setVisibility(View.GONE);
		mLoadDetailCustomerName.setVisibility(View.GONE);
	}

	private void setData(final SmartCardInfoVO vo, SmartCardInfoVO scv) {
		mSmartCardInfoVO = scv;
//		MemoryCacheUtil.setSmartCardInfoVO(vo.getSmardCardNo(), scv);
		fillData(scv);
	}

	/**
	 * 设置数据
	 * 
	 * @param sc
	 */
	private void fillData(SmartCardInfoVO sc) {
		if (!isAdded()) {
			return;
		}
		int smartCardType = SharedPreferencesUtil.getSmartCardType(getActivity());
		if (SmartCardType.DTH.getNum() == smartCardType) {
			mSmartCardImageView.setImageResource(R.drawable.smartcard_dth);
		} else if (SmartCardType.DTT.getNum() == smartCardType) {
			mSmartCardImageView.setImageResource(R.drawable.smartcard_dtt);
		}

		if (sc.getStopDays() != null) {
			setRemainingDays(sc.getStopDays());
		} else if (!TextUtils.isEmpty(sc.getPenaltyStop())) {
			setRemainingDays(sc.getPenaltyStop());
		}
		Double money = sc.getMoney();
		if (money != null) {
			setCardMoney(money);
		}
		if (sc.getCode() != null) {
			if (sc.getCode().equals(SmartCardInfoVO.NET_WORK_ERROR)) {
				setNoInfoVisibility(View.VISIBLE);
			}
		}
//		String programName = sc.getProgramName();
//		if (programName != null) {
//			setPackageName(programName);
//		}else {
//			setPackageName(getString(R.string.no_bouquet));
//		}
		setSmartCardNo(formatSmarCardNo(sc.getSmardCardNo()));
		// 设置用户名和用户电话
		if (sc.getAccountName() != null) {
			setCustomerName(sc.getAccountName());
		}
		if (sc.getPhoneNumber() != null) {
			setCustomerPhone(sc.getPhoneNumber());
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

	public void setRemainingDaysTextColor(int color) {
		mSmartCardDeadLine.setTextColor(color);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.balance_rl:
			// 跳转到Balance页面
			if (mSmartCardInfoVO.getMoney() != null) {
			Intent i = new Intent();
			i.putExtra("smartcardinfovo", (Serializable) mSmartCardInfoVO);
			// i.putExtra("smartinfos", (Serializable) mSmartinfos);
			// i.setClass(getActivity(), TopupActivity.class);
			i.setClass(getActivity(), RechargeSmartCardActivity.class);
			CommonUtil.startActivity(getActivity(), i);
			}else {
				ToastUtil.centerShowToast(getActivity(), getString(R.string.loading_prompt));
			}
			break;
		case R.id.bouquet_rl:
			// 跳转到Bouquet页面
			if (mSmartCardInfoVO.getMoney() != null) {
				Intent intent = new Intent();
				intent.putExtra("smartCardInfoVO", mSmartCardInfoVO);
				intent.setClass(getActivity(), ChangeBouquetActivity.class);
				CommonUtil.startActivity(getActivity(), intent);
			}else {
				ToastUtil.centerShowToast(getActivity(), getString(R.string.loading_prompt));
			}
			break;
		case R.id.account_bill_rl:
			// 点击AccountBill跳转
			if (mSmartCardInfoVO.getMoney() != null) {
				Intent accountBillIntent = new Intent(getActivity(), AccountBillActivity.class);
				accountBillIntent.putExtra("smartCardInfo", mSmartCardInfoVO);
				CommonUtil.startActivity(getActivity(), accountBillIntent);
			}else {
				ToastUtil.centerShowToast(getActivity(), getString(R.string.loading_prompt));
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onDestroy() {
//		Log.i("initDate", "smart card info onDestroy()");
		// 当Activity退出时清空mSmartCardInfoVOMap；
//		MemoryCacheUtil.clearSmardCardInfoVO();
		super.onDestroy();
	}

}
