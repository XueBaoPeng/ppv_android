package com.star.mobile.video.smartcard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import com.star.cms.model.Area;
import com.star.cms.model.vo.SmartCardInfoVO;
import com.star.mobile.video.R;
import com.star.mobile.video.adapter.CustomerServiceTimeAdapter;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.model.FunctionType;
import com.star.mobile.video.search.SearchActivity;
import com.star.mobile.video.service.EggAppearService;
import com.star.mobile.video.service.FunctionService;
import com.star.mobile.video.service.UserService;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.CommonUtil.PromptDialogClickListener;
import com.star.mobile.video.util.DensityUtil;
import com.star.mobile.video.util.ToastUtil;
import com.star.mobile.video.view.NoScrollListView;
import com.star.mobile.video.widget.indicator.CirclePageIndicator;
import com.star.util.app.GA;
import com.star.util.loader.OnListResultListener;
import com.star.util.loader.OnResultListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * smart card控制页面
 *
 * @author Lee
 * @version 1.0
 * @date 2015/11/20
 * @notify 2015/11/27 lee
 */
public class SmartCardControlActivity extends BaseActivity implements OnClickListener{
	private ScrollView mSmartCardScrollView;
	private RelativeLayout mSmartCardInfoLL;
	private CirclePageIndicator mCirclePageIndicator;
	private ViewPager mViewPager;
	private NoScrollListView lvCustomerService;
	private SmartCardFragmentViewPageAdapter mSmartCardFragmentViewPagerAdapter;
	private View mSmartCardLoading;
	private ImageView mNoSmartCardIV;
	private TextView mGetCoinsTV;
	private UserService mUserService;
	private SmartCardService mSmartcardService;
	private PopupWindow popupWindow;
	private List<SmartCardInfoVO> mSmartinfos = new ArrayList<SmartCardInfoVO>();
	private List<BondSmartCardInfoView> mViewDatas = new ArrayList<BondSmartCardInfoView>();
	// 初始的时候布局宽度
	private int INITLAYOUTHEIGHT = 233;
	// 有数据时的布局宽度
	private int INCLUDEDATALAYOUTHEIGHT = 542;
	private String mShowPhoneNo;
	private boolean isFirstEnter = false;
	private String mBindSmartCardNO = null;// 绑卡的号码

	private String mChangePkgSmartCardNumber; //换包的卡号
	// viewpager加载的view数量
	private int VIEW_COUNT = 10;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_smart_card_control);
		initView();
		initData();
		EggAppearService.appearEgg(this, EggAppearService.AccountManager);
	}

	private void initView() {
		mSmartCardScrollView = (ScrollView) findViewById(R.id.smartcard_control_scrollview);
		mSmartCardInfoLL = (RelativeLayout) findViewById(R.id.smart_card_info_ll);
		mCirclePageIndicator = (CirclePageIndicator) findViewById(R.id.circle_indicator);
		mViewPager = (ViewPager) findViewById(R.id.view_pager);
		lvCustomerService = (NoScrollListView) findViewById(R.id.lv_customer_phone);
		mSmartCardLoading = (View) findViewById(R.id.smartcard_loadingView);
		mSmartCardScrollView.smoothScrollTo(0, 0);
		setLayoutParamsData(INITLAYOUTHEIGHT);
		mNoSmartCardIV = (ImageView) findViewById(R.id.no_smart_card_imagview);
		mNoSmartCardIV.setOnClickListener(this);
		mGetCoinsTV = (TextView) findViewById(R.id.getcoins_textview);
		mGetCoinsTV.setVisibility(View.GONE);
		findViewById(R.id.add_smart_card_iv).setOnClickListener(this);
		findViewById(R.id.iv_actionbar_back).setOnClickListener(this);
		findViewById(R.id.iv_actionbar_search).setOnClickListener(this);
		findViewById(R.id.iv_actionbar_delete).setOnClickListener(this);
		findViewById(R.id.my_order_rl).setOnClickListener(this);
		((TextView) findViewById(R.id.tv_actionbar_title)).setText(getString(R.string.fragment_tag_ccount_manager));
	}

	private void initData() {
		mUserService = new UserService();
		mSmartcardService = new SmartCardService(this);
		addFragmentView();
		List<Area> datas = getPhoneData();
		CustomerServiceTimeAdapter adapter = new CustomerServiceTimeAdapter(this, datas, this);
		lvCustomerService.setAdapter(adapter);
	}

	/**
	 * 添加view
	 */
	private void addFragmentView() {
		for (int i = 0; i < VIEW_COUNT; i++) {
			BondSmartCardInfoView view = new BondSmartCardInfoView(this);
			mViewDatas.add(view);
		}
		mSmartCardFragmentViewPagerAdapter = new SmartCardFragmentViewPageAdapter(mSmartinfos,mViewDatas);
		mViewPager.setAdapter(mSmartCardFragmentViewPagerAdapter);
		mCirclePageIndicator.setViewPager(mViewPager);
	}

	/**
	 * 获得手机号码数据
	 *
	 * @return
	 */
	private List<Area> getPhoneData() {
		List<Area> datas = new ArrayList<Area>();
		String phone = SharedPreferencesUtil.getCustomerPhone(this);
		String[] phones;
		if (phone != null) {
			phones = phone.split("@");
			for (int i = 0; i < phones.length; i++) {
				Area area = new Area();
				area.setPhoneNumber(phones[i]);
				area.setNationalFlag(SharedPreferencesUtil.getNationalFlag(this));
				datas.add(area);
			}
		}
		return datas;
	}
	private boolean isSmartCardLoading = true;
	/**
	 * 获得SmartCard数据
	 */
	public void getSmartCrad() {
		mSmartcardService.getAllSmartCardInfos(new OnListResultListener<SmartCardInfoVO>() {

			@Override
			public boolean onIntercept() {
				isSmartCardLoading = true;
				mSmartCardLoading.setVisibility(View.VISIBLE);
				return false;
			}

			@Override
			public void onFailure(int errorCode, String msg) {
				isSmartCardLoading = false;
				mSmartCardLoading.setVisibility(View.GONE);
			}

			@Override
			public void onSuccess(List<SmartCardInfoVO> smartCardInfos) {
				isSmartCardLoading = false;
				mSmartCardLoading.setVisibility(View.GONE);
				initSmartCard(smartCardInfos);
			}
		});

	}

	private void initSmartCard(List<SmartCardInfoVO> smartCardInfos) {
		if (smartCardInfos != null && smartCardInfos.size() > 0) {
			mSmartinfos.clear();
			mSmartinfos.addAll(smartCardInfos);
			mSmartCardFragmentViewPagerAdapter.setSmartInfos(mSmartinfos);
			setLayoutParamsData(INCLUDEDATALAYOUTHEIGHT);
			mNoSmartCardIV.setVisibility(View.GONE);
			if (mSmartinfos.size() == 1) {
				mCirclePageIndicator.setVisibility(View.INVISIBLE);
			} else {
				mCirclePageIndicator.setVisibility(View.VISIBLE);
			}
			if (isFirstEnter) {
				for (int i = 0; i < mSmartinfos.size(); i++) {
					String smardCardNo = mSmartinfos.get(i).getSmardCardNo();
					if (mBindSmartCardNO != null) {
						if (mBindSmartCardNO.equals(smardCardNo)) {
							mViewPager.setCurrentItem(i);
							break;
						}
					}
				}
			} else {
				isFirstEnter = true;
				mViewPager.setCurrentItem(0);
			}
		} else {
			mSmartinfos.clear();
			mSmartCardFragmentViewPagerAdapter = new SmartCardFragmentViewPageAdapter(mSmartinfos,mViewDatas);
			mViewPager.setAdapter(mSmartCardFragmentViewPagerAdapter);
			setLayoutParamsData(INITLAYOUTHEIGHT);
			// indicator隐藏，显示没有绑卡的图片
			mCirclePageIndicator.setVisibility(View.INVISIBLE);
			mNoSmartCardIV.setVisibility(View.VISIBLE);

			//判断用户有没有绑过卡，如果没有绑卡getcoins文字显示
			mSmartcardService.isBindSmartCard(new OnResultListener<Integer>() {

				@Override
				public boolean onIntercept() {
					return false;
				}

				@Override
				public void onSuccess(Integer value) {
					if (value == 1) {
						mGetCoinsTV.setVisibility(View.VISIBLE);
					} else {
						mGetCoinsTV.setVisibility(View.GONE);
					}
				}

				@Override
				public void onFailure(int errorCode, String msg) {

				}
			});
		}
		if (smartCardInfos == null) {
			ToastUtil.centerShowToast(this, getResources().getString(R.string.fail_to_get_the_smart_card_information));
		}
	}

	/**
	 * smartcard loading和smartcard 详情的布局参数
	 *
	 * @param height
	 */
	private void setLayoutParamsData(int height) {
		mSmartCardInfoLL.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, DensityUtil.dip2px(this, height)));
		mSmartCardLoading
                .setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, DensityUtil.dip2px(this, height)));
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!FunctionService.doHideFuncation(FunctionType.SmartCard)) {
			getSmartCrad();
		}
		View view =mViewDatas.get(mViewPager.getCurrentItem());
		if (view != null){
			if(view instanceof BondSmartCardInfoView){
				if (mSmartinfos != null && mSmartinfos.size()>0){
					((BondSmartCardInfoView)view).initData(mSmartinfos.get(mViewPager.getCurrentItem()));
					mSmartCardFragmentViewPagerAdapter.notifyDataSetChanged();
				}
			}
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			mChangePkgSmartCardNumber = bundle.getString("changePkgSmartCardNumber");
			mBindSmartCardNO = bundle.getString("bindSmartCardNO");
		} else {
			Log.i("initData", "bundle is null");
		}
		super.onNewIntent(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_call:
			// 24小时服务热线
			callPhone((String) v.getTag());
			ToastUtil.showToast(this, "call_phone");
			GA.sendEvent("Self_service", "Call_center", "call_phone", 1);
			break;
		case R.id.iv_actionbar_back:
			onBackPressed();
			break;
		case R.id.iv_actionbar_search:
			CommonUtil.startActivity(this, SearchActivity.class);
			break;
		case R.id.no_smart_card_imagview:
		case R.id.add_smart_card_iv:
			if (mSmartinfos != null && mSmartinfos.size() >= 10) {
				ToastUtil.centerShowToast(this, getString(R.string.bind_smart_card_limit));
			} else {
				// 添加
				goSmartCardActivity();
			}
			break;
		case R.id.iv_actionbar_delete:
			showPopupWindow(v);
			break;

		case R.id.my_order_rl:
			gotoMyOrderActivity();
			break;
		default:
			break;
		}
	}

	/**
	 * 打电话
	 * @param showPhoneNo
	 */
	private void callPhone(String showPhoneNo) {
		mShowPhoneNo = showPhoneNo;
		CommonUtil.getInstance().showPromptDialog(this, getString(R.string.tips),
				String.format(getString(R.string.bind_smart_card_call_prompt), mShowPhoneNo), getString(R.string.call),
				getString(R.string.forum_later), new PromptDialogClickListener() {

					@Override
					public void onConfirmClick() {
						Intent intent = new Intent(Intent.ACTION_CALL);
						intent.setData(Uri.parse("tel:" + mShowPhoneNo));
						startActivity(intent);
					}

					@Override
					public void onCancelClick() {

					}
				});
	}

	/**
	 * 进入绑卡页面
	 */
	private void goSmartCardActivity() {
		Intent it = new Intent();
		it.putExtra("smartinfos",(Serializable)mSmartinfos);
		it.setClass(this, SmartCardActivity.class);
		CommonUtil.startActivityForResult(this, it, 200);
	}

	/**
	 * 弹出popupwindow
	 *
	 * @param view
	 */
	private void showPopupWindow(View view) {
		// 一个自定义的布局，作为显示的内容
		View contentView = LayoutInflater.from(this).inflate(R.layout.smart_card_delete_popup_window, null);
		popupWindow = new PopupWindow(contentView, DensityUtil.dip2px(SmartCardControlActivity.this, 150),
				DensityUtil.dip2px(SmartCardControlActivity.this, 50), true);

		popupWindow.setTouchable(true);
		// 设置popwindow如果点击外面区域，便关闭。
		popupWindow.setOutsideTouchable(true);
		// 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
		popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.me_item_bg));
		// 设置好参数之后再show
		popupWindow.showAsDropDown(view, DensityUtil.dip2px(SmartCardControlActivity.this, -125),
				DensityUtil.dip2px(SmartCardControlActivity.this, -15));

		// 设置按钮的点击事件
		TextView deleteSmartCard = (TextView) contentView.findViewById(R.id.delete_smart_card_textview);
		deleteSmartCard.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
				if (mSmartinfos == null || mSmartinfos.size() == 0) {
					ToastUtil.centerShowToast(SmartCardControlActivity.this, getString(R.string.bind_smart_card_prompt));
				} else {
					SmartCardInfoVO smartCardInfoVO = getSmartCard();
					if (smartCardInfoVO != null) {
						int currentItem = mViewPager.getCurrentItem();
						BondSmartCardInfoView fragment = mViewDatas.get(currentItem);
						boolean allowDeleteSmartCard = fragment.isAllowDeleteSmartCard();
						if (!allowDeleteSmartCard || isSmartCardLoading) {
							ToastUtil.centerShowToast(SmartCardControlActivity.this, getResources().getString(R.string.loading_delete_smartcard));
						}else {
							// 删除处理
							CommonUtil.getInstance().showPromptDialog(SmartCardControlActivity.this, getString(R.string.tips),
									String.format(getString(R.string.delete_smart_card_prompt), getSmartCardNO()),
									getString(R.string.forum_confirm), getString(R.string.forum_later),
									new PromptDialogClickListener() {

								@Override
								public void onConfirmClick() {
									// 删除当前的viewpager里的内容
									popupWindow.dismiss();
									delUserSmardCartNo(getSmartCardID());
								}

								@Override
								public void onCancelClick() {
									popupWindow.dismiss();
								}
							});
						}
					}
				}

			}
		});

	}

	/**
	 * 跳转到MyOrder界面
	 */
	private void gotoMyOrderActivity() {
		Intent it = new Intent();
		it.setClass(SmartCardControlActivity.this, MyOrderActivity.class);
		CommonUtil.startActivity(SmartCardControlActivity.this, it);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 200) {
			if (data != null) {
				Bundle bundle = data.getExtras();
				if (bundle != null) {
					mBindSmartCardNO = bundle.getString("bindSmartCardNO");
				} else {
					Log.i("initData", "bundle is null");
				}
			} else {
				Log.i("initData", "data is null");
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	/**
	 * 获得当前智能卡
	 * @return
	 */
	private SmartCardInfoVO getSmartCard(){
		int currentItem = mViewPager.getCurrentItem();
		return mSmartinfos.get(currentItem);
	}
	/**
	 * 获得智能卡id
	 *
	 * @return
	 */
	private long getSmartCardID() {
		SmartCardInfoVO smartCardInfoVO = getSmartCard();
		if (smartCardInfoVO != null) {
			return smartCardInfoVO.getId();
		}
		return 0;
	}

	/**
	 * 获得卡号
	 *
	 * @return
	 */
	private String getSmartCardNO() {
		int currentItem = mViewPager.getCurrentItem();
		SmartCardInfoVO smartCardInfoVO = mSmartinfos.get(currentItem);
		if (smartCardInfoVO != null) {
			return smartCardInfoVO.getSmardCardNo();
		}
		return null;
	}

	/**
	 * 根据只能卡号删除只能卡
	 * @param smartCardId
	 */
	private void delUserSmardCartNo(final Long smartCardId) {
		mSmartcardService.delSmartCard(smartCardId, new OnResultListener<Boolean>() {

			@Override
			public boolean onIntercept() {
				CommonUtil.showProgressDialog(SmartCardControlActivity.this);
				return false;
			}

			@Override
			public void onSuccess(Boolean value) {
				CommonUtil.closeProgressDialog();
				// 成功
				mUserService.delCachedAllSmartCardNo();
				Iterator<SmartCardInfoVO> iterator = mSmartinfos.iterator();
				while (iterator.hasNext()) {
					SmartCardInfoVO info = iterator.next();
					if (info.getId().equals(smartCardId)) {
						iterator.remove();
						break;
					}
				}
				List<SmartCardInfoVO> vos = new ArrayList<SmartCardInfoVO>();
				vos.addAll(mSmartinfos);
				initSmartCard(vos);
				mUserService.saveExpectedStopSmartcard(SmartCardControlActivity.this);
			}

			@Override
			public void onFailure(int errorCode, String msg) {
				CommonUtil.closeProgressDialog();
				// 失败
				ToastUtil.centerShowToast(SmartCardControlActivity.this, getResources().getString(R.string.fail_to_delete_this_smart_card));
			}
		});
	}




}
