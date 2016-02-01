package com.star.mobile.video.smartcard;

import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.star.cms.model.BindCardCommand;
import com.star.cms.model.Command;
import com.star.cms.model.PpvCMD;
import com.star.cms.model.ProgramPPV;
import com.star.cms.model.RechargeCMD;
import com.star.cms.model.User;
import com.star.cms.model.code.ChangePackageCode;
import com.star.cms.model.dto.RechargeResult;
import com.star.cms.model.vo.ChangePackageCMDVO;
import com.star.cms.model.vo.SmartCardInfoVO;
import com.star.mobile.video.R;
import com.star.mobile.video.account.AccountService;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.ppv.ppvorder.PPVOrderDetailView;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.DateFormat;
import com.star.mobile.video.util.ImageUtil;
import com.star.mobile.video.util.ToastUtil;
import com.star.ott.ppvup.model.enums.RentleType;
import com.star.ott.ppvup.model.remote.Content;
import com.star.ott.ppvup.model.remote.EpgContent;
import com.star.ott.ppvup.model.remote.LiveContent;
import com.star.ott.ppvup.model.remote.Product;
import com.star.ui.ImageView.Finisher;
import com.star.util.loader.OnResultListener;

/**
 * MyOrder详情页面
 * 
 * @author Lee
 * @date 2016/01/04
 * @version 1.0
 *
 */
public class MyOrderDetailActivity extends BaseActivity implements OnClickListener {
	private PPVOrderDetailView mPPVOrderDetailView;
	private com.star.ui.ImageView mUserHeader;
	private TextView mMyOrderDetailType;
	private TextView mMyOrderDetailCardNumber;
	private TextView mMyOrderDetailInfo;
	private View mSmartCardLoading;
	// Receive的信息
	private ImageView mReceiveImageView;
	private TextView mReceiveTV;
	private TextView mReceiveTimeTV;
	private TextView mReceiveStatusTV;
	// process的信息
	private ImageView mProcessImageView;
	private TextView mProcessTV;
	private TextView mProcessTimeTV;
	private TextView mProcessStatusTV;
	private TextView mProcessReasonTV;
	private TextView mProcessReasonContentTV;
	// result的信息
	private ImageView mResultImageView;
	private TextView mResultTV;
	private TextView mResultTimeTV;
	private TextView mResultStatusTV;
	private TextView mResultReasonTV;
	private TextView mResultReasonContentTV;
	private AccountService mAccountService;
	private SmartCardService mSmartCardService;
	private Long mSmsHistoryID;
	private Integer mSmsHistoryType;
	private PpvCMD mPPVCMD;

	private String mTotalPrice;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myorder_detail);
		Intent intent = getIntent();
		if (intent != null) {
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				mSmsHistoryID = (Long) bundle.get("smsHistoryID");
				mSmsHistoryType = (Integer) bundle.get("smsHistoryType");
				mTotalPrice = intent.getStringExtra("totalPrice");
				mPPVCMD = (PpvCMD)intent.getSerializableExtra("ppvcmd");
			}
		}
		initView();
		initData();
	}

	/**
	 * view初始化
	 */
	private void initView() {
		((ImageView) findViewById(R.id.iv_actionbar_back)).setOnClickListener(this);
		((TextView) findViewById(R.id.tv_actionbar_title)).setText(getString(R.string.my_order));

		mPPVOrderDetailView = (PPVOrderDetailView) findViewById(R.id.ppv_order_detail_view);

		mSmartCardLoading = (View) findViewById(R.id.smartcard_loadingView);
		mUserHeader = (com.star.ui.ImageView) findViewById(R.id.my_order_detail_user_header);
		mMyOrderDetailType = (TextView) findViewById(R.id.my_order_type);
		mMyOrderDetailCardNumber = (TextView) findViewById(R.id.my_order_card_number);
		mMyOrderDetailInfo = (TextView) findViewById(R.id.my_order_card_change);

		mReceiveImageView = (ImageView) findViewById(R.id.my_oreder_success_iv);
		mReceiveTV = (TextView) findViewById(R.id.receive_textview);
		mReceiveTimeTV = (TextView) findViewById(R.id.my_order_detail_time);
		mReceiveStatusTV = (TextView) findViewById(R.id.my_order_detail_status);

		mProcessImageView = (ImageView) findViewById(R.id.my_oreder_process_iv);
		mProcessTV = (TextView) findViewById(R.id.process_textview);
		mProcessTimeTV = (TextView) findViewById(R.id.my_order_failed_time);
		mProcessStatusTV = (TextView) findViewById(R.id.my_order_failed_status);
		mProcessReasonTV = (TextView) findViewById(R.id.my_order_failed_reason);
		mProcessReasonContentTV = (TextView) findViewById(R.id.my_order_failed_reason_content);

		mResultImageView = (ImageView) findViewById(R.id.my_oreder_result_iv);
		mResultTV = (TextView) findViewById(R.id.result_textview);
		mResultTimeTV = (TextView) findViewById(R.id.my_order_result_time);
		mResultStatusTV = (TextView) findViewById(R.id.my_order_result_status);
		mResultReasonTV = (TextView) findViewById(R.id.my_order_result_reason);
		mResultReasonContentTV = (TextView) findViewById(R.id.my_order_result_reason_content);
		hideAllView();
	}

	/**
	 * 数据初始化
	 */
	private void initData() {
		mSmartCardService = new SmartCardService(this);
		mAccountService = new AccountService(this);
		getUserInfo();
		switch (mSmsHistoryType) {
		case Command.BIND_CARD:
			mPPVOrderDetailView.setVisibility(View.GONE);
			getBindCardOrderDetail();
			break;
		case Command.CHANGE_PACKAGE:
			mPPVOrderDetailView.setVisibility(View.GONE);
			getChangePackageDetail();
			break;
		case Command.RECARGE:
			mPPVOrderDetailView.setVisibility(View.GONE);
			getRechargeDetail();
			break;
		case Command.PPV:
			mPPVOrderDetailView.setVisibility(View.VISIBLE);
			//设置ppv的订单详情
			if (mPPVCMD != null){
				
				mPPVOrderDetailView.setOrderDate(mPPVCMD);

			}
			getPPVDetail();
			break;
		}



	}

	private void getPPVDetail(){
		mSmartCardService.getPPVDetail(mSmsHistoryID, new OnResultListener<PpvCMD>() {
			@Override
			public boolean onIntercept() {
				return false;
			}

			@Override
			public void onSuccess(PpvCMD result) {
				mSmartCardLoading.setVisibility(View.GONE);
				showAllView();
				if (result != null) {

					String failedReason = null;
					int acceptStatus = result.getAcceptStatus();
					switch (acceptStatus) {

						case RechargeResult.CARD_DOES_NOT_EXIST:
							failedReason = getString(R.string.recharge_card_number_is_invalid);
							break;
						case RechargeResult.CUSTOMER_PASSWORD_IS_NOT_CORRECT:
							failedReason = getString(R.string.client_password_is_incorrect);
							break;
						case RechargeResult.CARD_IS_ALREADY_IN_USE:
							failedReason = getString(R.string.recharge_card_number_has_been_used_before);
							break;
						case RechargeResult.CARD_HAS_EXPIRED:
							failedReason = getString(R.string.recharge_card_has_expired);
							break;
						default:
							failedReason = getString(R.string.service_abnormal) + "(" + acceptStatus + ")";
							break;
					}
//					String dec = "";
//					Double money = result.getRechargeMoney();
//					if (money != null && money > 0) {
//						dec = SharedPreferencesUtil.getCurrencSymbol(MyOrderDetailActivity.this) + money;
//					}
					setMyOrderDetailInfo(getString(R.string.recharge), result.getSmartcardNo(), "",
							result.getCreateDate(), R.drawable.ic_cancel, result.getUpdateDate(),
							failedReason, R.drawable.ic_alert, result.getCreateDate(),
							result.getUpdateDate(), getString(R.string.recharge_success),
							result.getProgress(), result.getAcceptStatus());
				} else {
					ToastUtil.centerShowToast(MyOrderDetailActivity.this, getString(R.string.no_data));
				}

			}

			@Override
			public void onFailure(int errorCode, String msg) {
				mSmartCardLoading.setVisibility(View.GONE);
				showAllView();
				ToastUtil.centerShowToast(MyOrderDetailActivity.this, getString(R.string.service_abnormal) + "(" + errorCode + ")");
			}
		});


	}

	/**
	 * 充值详情
	 */
	private void getRechargeDetail() {
		mSmartCardService.getChargeDetail(mSmsHistoryID, new OnResultListener<RechargeCMD>() {

			@Override
			public boolean onIntercept() {
				return false;
			}

			@Override
			public void onSuccess(RechargeCMD result) {
				mSmartCardLoading.setVisibility(View.GONE);
				showAllView();
				if (result != null) {

					String failedReason = null;
					int acceptStatus = result.getAcceptStatus();
					switch (acceptStatus) {

						case RechargeResult.CARD_DOES_NOT_EXIST:
							failedReason = getString(R.string.recharge_card_number_is_invalid);
							break;
						case RechargeResult.CUSTOMER_PASSWORD_IS_NOT_CORRECT:
							failedReason = getString(R.string.client_password_is_incorrect);
							break;
						case RechargeResult.CARD_IS_ALREADY_IN_USE:
							failedReason = getString(R.string.recharge_card_number_has_been_used_before);
							break;
						case RechargeResult.CARD_HAS_EXPIRED:
							failedReason = getString(R.string.recharge_card_has_expired);
							break;
						default:
							failedReason = getString(R.string.service_abnormal) + "(" + acceptStatus + ")";
							break;
					}
					String dec = "";
					Double money = result.getRechargeMoney();
					if (money != null && money > 0) {
						dec = SharedPreferencesUtil.getCurrencSymbol(MyOrderDetailActivity.this) + money;
					}
					setMyOrderDetailInfo(getString(R.string.recharge), result.getSmartCardNo(), dec,
							result.getCreateDate(), R.drawable.ic_cancel, result.getUpdateDate(),
							failedReason, R.drawable.ic_alert, result.getCreateDate(),
							result.getUpdateDate(), getString(R.string.recharge_success),
							result.getProgress(), result.getAcceptStatus());
				} else {
					ToastUtil.centerShowToast(MyOrderDetailActivity.this, getString(R.string.no_data));
				}
			}

			@Override
			public void onFailure(int errorCode, String msg) {
				mSmartCardLoading.setVisibility(View.GONE);
				showAllView();
				ToastUtil.centerShowToast(MyOrderDetailActivity.this, getString(R.string.service_abnormal) + "(" + errorCode + ")");
			}
		});
	}

	/**
	 * 设置TextView里的文字显示不同颜色
	 * 
	 * @param textView
	 *            要设置的TextView
	 * @param content
	 *            显示的内容
	 * @param firstPosition
	 *            开始的位置
	 * @param offset
	 *            结束的位置
	 * @param color
	 *            要显示的颜色
	 */
	private void setTextViewStyle(TextView textView, String content, int firstPosition, int offset, int color) {
		SpannableStringBuilder textViewStyle = new SpannableStringBuilder(content);
		textViewStyle.setSpan(new ForegroundColorSpan(color), firstPosition, offset,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		textView.setText(textViewStyle);
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
	 * 获得用户信息
	 */
	private void getUserInfo() {
		mAccountService.getUser(new OnResultListener<User>() {

			@Override
			public boolean onIntercept() {
				return false;
			}

			@Override
			public void onSuccess(User user) {
				mUserHeader.setUrl(user.getHead());
				mUserHeader.setFinisher(new Finisher() {

					@Override
					public void run() {
						mUserHeader.setImageBitmap(ImageUtil.getCircleBitmap(mUserHeader.getImage()));
					}
				});
			}

			@Override
			public void onFailure(int errorCode, String msg) {
			}
		});
	}

	/**
	 * 获得绑卡的详情
	 */
	private void getBindCardOrderDetail() {
		mSmartCardService.getBindCardOrderDetail(mSmsHistoryID, new OnResultListener<BindCardCommand>() {

			@Override
			public boolean onIntercept() {
				return false;
			}

			@Override
			public void onSuccess(BindCardCommand bindCardCommand) {
				mSmartCardLoading.setVisibility(View.GONE);
				showAllView();
				if (bindCardCommand != null) {

					String failedReason = null;
					int acceptStatus = bindCardCommand.getAcceptStatus();
					switch (acceptStatus) {
						case BindCardCommand.CARD_IS_BIND_RESULT:
							// 智能卡已经绑定
							failedReason = getString(R.string.smart_card_has_been_bound);
							break;
						case BindCardCommand.MORE_THAN:
							// 绑定智能卡超出限制的次数
							failedReason = getString(R.string.smart_card_count_more_than_5);
							break;
						case BindCardCommand.NO_CARD_RESULT:
							// 智能卡不存在
							failedReason = getString(R.string.smart_card_not_exist);
							break;
						case BindCardCommand.SMS_ERROR_RESULT:
							// Boss异常
							failedReason = getString(R.string.service_abnormal);
							break;
						default:
							failedReason = getString(R.string.service_abnormal) + "(" + acceptStatus + ")";
							break;
					}

					setMyOrderDetailInfo(getString(R.string.bingding_card), bindCardCommand.getSmartCardNo(), "",
							bindCardCommand.getCreateDate(), R.drawable.ic_cancel, bindCardCommand.getUpdateDate(),
							failedReason, R.drawable.ic_alert, bindCardCommand.getCreateDate(),
							bindCardCommand.getUpdateDate(), getString(R.string.binding_card_successfully),
							bindCardCommand.getProgress(), bindCardCommand.getAcceptStatus());
				} else {
					ToastUtil.centerShowToast(MyOrderDetailActivity.this, getString(R.string.no_data));
				}

			}

			@Override
			public void onFailure(int errorCode, String msg) {
				mSmartCardLoading.setVisibility(View.GONE);
				showAllView();
				ToastUtil.centerShowToast(MyOrderDetailActivity.this, getString(R.string.service_abnormal) + "(" + errorCode + ")");
			}
		});
	}

	/**
	 * 换包详情
	 */
	private void getChangePackageDetail() {
		mSmartCardService.getChangePackageDetail(mSmsHistoryID, new OnResultListener<ChangePackageCMDVO>() {

			@Override
			public boolean onIntercept() {
				return false;
			}

			@Override
			public void onSuccess(ChangePackageCMDVO changePackageCMD) {
				mSmartCardLoading.setVisibility(View.GONE);
				showAllView();
				if (changePackageCMD != null) {
					// 换包reason提示
					String failedReason = null;
					int acceptStatus = changePackageCMD.getAccepStatus();
					switch (acceptStatus) {
						case ChangePackageCode.BOX_NUMBER_NOT_MATCH:
							// 更换产品包输入的机顶盒号不匹配
							failedReason = getString(R.string.decoder_is_correct);
							break;
						case ChangePackageCode.CHANGE_PACKAGE_ERROR:
							// 该用户没有此智能卡
							failedReason = getString(R.string.smart_card_not_exist);
							break;
						case ChangePackageCode.STB_CODE_NULL:
							// 机顶盒号为空
							failedReason = getString(R.string.set_top_is_empty);
							break;
						case ChangePackageCode.LACK_BALANCE:
							// 账户余额不足新包费用
							failedReason = getString(R.string.balance_cannot_bouquet);
							break;
						case ChangePackageCode.PHONE_NO_MATCHING:
							// 输入的手机号不匹配
							failedReason = getString(R.string.phone_number_not_match);
							break;
						case ChangePackageCode.CHECK_NUMBER_LOSE:
							// 验证码无效
							failedReason = getString(R.string.invalid_verification_code);
							break;
						default:
							failedReason = getString(R.string.service_abnormal) + "(" + acceptStatus + ")";
							break;
					}

					String desc = String.format(getString(R.string.change_bouquet_desc),
							changePackageCMD.getFromPackageName() == null ? "" : changePackageCMD.getFromPackageName(),
							changePackageCMD.getToPackageName() == null ? "" : changePackageCMD.getToPackageName());
					setMyOrderDetailInfo(getString(R.string.change_bouquet), changePackageCMD.getSmartCardNo(), desc,
							changePackageCMD.getCreateDate(), R.drawable.ic_cancel, changePackageCMD.getUpdateDate(),
							failedReason, R.drawable.ic_alert, changePackageCMD.getCreateDate(),
							changePackageCMD.getUpdateDate(), getString(R.string.change_bouquet_successfully),
							changePackageCMD.getProgress(), changePackageCMD.getAccepStatus());
				} else {
					ToastUtil.centerShowToast(MyOrderDetailActivity.this, getString(R.string.no_data));
				}
			}

			@Override
			public void onFailure(int errorCode, String msg) {
				mSmartCardLoading.setVisibility(View.GONE);
				showAllView();
				ToastUtil.centerShowToast(MyOrderDetailActivity.this, getString(R.string.service_abnormal) + "(" + errorCode + ")");
			}
		});
	}

	/**
	 * 设置订单详情的信息
	 * 
	 * @param type
	 * @param cardNumber
	 * @param des
	 * @param receiveTime
	 * @param failedImage
	 * @param failedTime
	 * @param failedReason
	 * @param prcessImage
	 * @param processTime
	 * @param successTime
	 * @param successReason
	 * @param status
	 */
	private void setMyOrderDetailInfo(String type, String cardNumber, String des, Date receiveTime, int failedImage,
			Date failedTime, String failedReason, int prcessImage, Date processTime, Date successTime,
			String successReason, int status, int resultStatus) {
		mMyOrderDetailType.setText(type);
		mMyOrderDetailCardNumber.setText(formatSmarCardNo(cardNumber));
		mMyOrderDetailInfo.setText(des);

		setTextViewStyle(mReceiveTimeTV,
				getString(R.string.time) + " " + DateFormat.format(receiveTime, "yyyy-MM-dd HH:mm"), 0, 6,
				R.color.gray_bg);
		setTextViewStyle(mReceiveStatusTV, getString(R.string.accept_status), 0, 7, R.color.gray_bg);

		mProcessImageView.setImageResource(R.drawable.ic_alert);

		setTextViewStyle(mProcessTimeTV,
				getString(R.string.time) + " " + DateFormat.format(processTime, "yyyy-MM-dd HH:mm"), 0, 6,
				R.color.gray_bg);
		setTextViewStyle(mProcessStatusTV, getString(R.string.processing_status), 0, 7, R.color.gray_bg);
		mProcessReasonTV.setVisibility(View.GONE);
		mProcessReasonContentTV.setVisibility(View.GONE);
		
		if (status == Command.Progress_error ||status == Command.Progress_success) {// 成功、失败的状态
			mProcessImageView.setImageResource(R.drawable.ic_confirm);

			if (resultStatus == BindCardCommand.BIND_CARED_SUCCESS_RESULT
					|| resultStatus == ChangePackageCode.CHANGE_SUCCESS) {// result为成功的时候
				mResultImageView.setImageResource(R.drawable.ic_confirm);
				setTextViewStyle(mResultTimeTV,
						getString(R.string.time) + " " + DateFormat.format(successTime, "yyyy-MM-dd HH:mm"), 0, 6,
						R.color.gray_bg);
				setTextViewStyle(mResultStatusTV, getString(R.string.success_status), 0, 7, R.color.gray_bg);
				mResultReasonTV.setVisibility(View.GONE);
			} else {
				mResultImageView.setImageResource(R.drawable.ic_cancel);
				setTextViewStyle(mResultTimeTV,
						getString(R.string.time) + " " + DateFormat.format(failedTime, "yyyy-MM-dd HH:mm"), 0, 6,
						R.color.gray_bg);
				setTextViewStyle(mResultStatusTV, getString(R.string.failed_status), 0, 7, R.color.gray_bg);
				if (failedReason == null) {
					mResultReasonTV.setVisibility(View.GONE);
				}
				mResultReasonContentTV.setText(failedReason);
			}
		} else {

			mResultImageView.setImageResource(R.drawable.ic_av_timer);
			setTextViewStyle(mResultStatusTV, getString(R.string.wait_status), 0, 7, R.color.gray_bg);
			mResultReasonTV.setVisibility(View.GONE);
			mResultReasonContentTV.setVisibility(View.GONE);
		}
	}

	private String formatSmarCardNo(String cmardNo) {
		StringBuffer sb = new StringBuffer();
		if (cmardNo != null) {
			for (int i = 0; i < 3; i++) {
				sb.append(cmardNo.charAt(i));
			}
			sb.append("-");
			for (int i = 3; i < cmardNo.length(); i++) {
				if (i % 7 == 0 && i != 0) {
					sb.append("-");
				}
				sb.append(cmardNo.charAt(i));
			}
			return sb.toString();
		} else {
			return "";
		}

	}
	/**
	 * 隐藏所有View
	 */
	private void hideAllView(){
		mReceiveImageView.setVisibility(View.GONE);
		mReceiveTV.setVisibility(View.GONE);
		mReceiveTimeTV.setVisibility(View.GONE);
		mReceiveStatusTV.setVisibility(View.GONE);
		                       
		mProcessImageView.setVisibility(View.GONE);
		mProcessTV.setVisibility(View.GONE);
		mProcessTimeTV.setVisibility(View.GONE);
		mProcessStatusTV.setVisibility(View.GONE);
		mProcessReasonTV.setVisibility(View.GONE);
		mProcessReasonContentTV.setVisibility(View.GONE);
		                       
		mResultImageView.setVisibility(View.GONE);
		mResultTV.setVisibility(View.GONE);
		mResultTimeTV.setVisibility(View.GONE);
		mResultStatusTV.setVisibility(View.GONE);
		mResultReasonTV.setVisibility(View.GONE);
		mResultReasonContentTV.setVisibility(View.GONE);
	}
	/**
	 * 显示所有View
	 */
	private void showAllView(){
		mReceiveImageView.setVisibility(View.VISIBLE);
		mReceiveTV.setVisibility(View.VISIBLE);
		mReceiveTimeTV.setVisibility(View.VISIBLE);
		mReceiveStatusTV.setVisibility(View.VISIBLE);
		                       
		mProcessImageView.setVisibility(View.VISIBLE);
		mProcessTV.setVisibility(View.VISIBLE);
		mProcessTimeTV.setVisibility(View.VISIBLE);
		mProcessStatusTV.setVisibility(View.VISIBLE);
		mProcessReasonTV.setVisibility(View.VISIBLE);
		mProcessReasonContentTV.setVisibility(View.VISIBLE);
		                       
		mResultImageView.setVisibility(View.VISIBLE);
		mResultTV.setVisibility(View.VISIBLE);
		mResultTimeTV.setVisibility(View.VISIBLE);
		mResultStatusTV.setVisibility(View.VISIBLE);
		mResultReasonTV.setVisibility(View.VISIBLE);
		mResultReasonContentTV.setVisibility(View.VISIBLE);
	}


}
