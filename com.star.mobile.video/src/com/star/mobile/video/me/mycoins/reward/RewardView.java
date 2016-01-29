package com.star.mobile.video.me.mycoins.reward;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.star.cms.model.User;
import com.star.cms.model.vo.AwardVO;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.dialog.AlertDialog;
import com.star.mobile.video.me.coupon.ExchangeService;
import com.star.mobile.video.me.coupon.MyCouponsActivity;
import com.star.mobile.video.me.mycoins.MyCoinsActivity;
import com.star.mobile.video.service.EggAppearService;
import com.star.mobile.video.service.UserService;
import com.star.mobile.video.service.UserService.CallbackListener;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.CommonUtil.PromptDialogClickListener;
import com.star.mobile.video.util.LoadingDataTask;
import com.star.mobile.video.util.ToastUtil;
import com.star.mobile.video.view.NoScrollListView;
import com.star.util.loader.OnListResultListener;
import com.star.util.loader.OnResultListener;

public class RewardView extends FrameLayout {

	private View mView;
	private Context context;
	private NoScrollListView lvRewardList;
	private View loadingView;
	private RewardService rewardService;
	private ExchangeService exchangeService;
	private UserService userService;
	private RewardListAdapter adapter;
	private List<AwardVO> awards = new ArrayList<AwardVO>();
	private User user;
	private int myCoins;
	private AwardVO mAwardVO;

	public RewardView(Context context) {
		this(context, null);
	}

	public RewardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		mView = LayoutInflater.from(context).inflate(R.layout.view_reward, this);
		initView();
		initData();
	}

	private void initView() {
		lvRewardList = (NoScrollListView) mView.findViewById(R.id.lv_reward);
		loadingView = mView.findViewById(R.id.loadingView);
	}

	public void initData() {
		rewardService = new RewardService(context);
		exchangeService=new ExchangeService(context);
		userService = new UserService();
		refreshCoin();
		getAwardsTask(false);
		EggAppearService.appearEgg(context, EggAppearService.SpendCoins);
	}

	private void refreshCoin() {
		userService.setCallbackListener(new CallbackListener() {
			@Override
			public void callback(User u) {
				user = u;
				myCoins = user.getCoins();
				if (adapter != null) {
					adapter.updateDataAndRefreshUI(awards, myCoins);
				}

			}
		});
		userService.getUser(context, false);
	}

	private boolean fromNetAlready = false;

	private void getAwardsTask(final boolean fromLocal){
		rewardService.getRewards(fromLocal,new OnListResultListener<AwardVO>() {
			
			@Override
			public boolean onIntercept() {
				loadingView.setVisibility(View.VISIBLE);
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return false;
			}
			
			@Override
			public void onFailure(int errorCode, String msg) {
				
			}
			
			@Override
			public void onSuccess(List<AwardVO> aws) {
				if (!fromLocal) {
					lvRewardList.setVisibility(View.VISIBLE);
					loadingView.setVisibility(View.GONE);
					fromNetAlready = true;
				}
				if (aws == null || aws.size() == 0) {
					if (!fromLocal && awards.size() == 0)
						ToastUtil.centerShowToast(context, context.getString(R.string.no_data));
					return;
				}
				if (fromLocal && fromNetAlready)
					return;
				awards.clear();
				awards.addAll(aws);
				if (adapter == null) {
					adapter = new RewardListAdapter(context, awards, myCoins);
					adapter.setOnClick(heandItemClick, coupontemClick);
					lvRewardList.setAdapter(adapter);
				} else {
					adapter.updateDataAndRefreshUI(awards, myCoins);
				}
			}
		});
	}

	private OnClickListener heandItemClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(context, RewardDetailActivity.class);
			intent.putExtra("awardVo", (AwardVO) ((TextView) v.findViewById(R.id.tv_reward_object_name)).getTag());
			intent.putExtra("mycoins", user.getCoins());
			CommonUtil.startActivity(context, intent);
		}
	};
	private OnClickListener coupontemClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (user.getCoins() < ((AwardVO) ((TextView) v.findViewById(R.id.tv_reward_name)).getTag()).getPrice()) {
				CommonUtil.getInstance().showPromptDialog(context, null,
						context.getString(R.string.coins_not_coupon_msg), context.getString(R.string.confirm), null,
						null);
			} else {
				mAwardVO = (AwardVO) ((TextView) v.findViewById(R.id.tv_reward_name)).getTag();
				String msg = context.getString(R.string.coins_coupon_msg, mAwardVO.getPrice(), mAwardVO.getName());
				CommonUtil.getInstance().showPromptDialog(context, context.getString(R.string.alert),
						msg, context.getString(R.string.confirm), context.getString(R.string.later),
						new PromptDialogClickListener() {
							
							@Override
							public void onConfirmClick() {
								goExchange(mAwardVO);
							}
							
							@Override
							public void onCancelClick() {
							}
						});
			}

		}
	};

	private void goExchange(final AwardVO awardVo) {
		
		exchangeService.exchange(awardVo.getId(), new OnResultListener<Integer>() {
			private Integer result;
			private AlertDialog dialog;

			@Override
			public void onSuccess(Integer value) {
				if(value!=null){
					result=value;
				}

				CommonUtil.closeProgressDialog();
				if (result == null)
					return;
				switch (result) {
				case 0:
					if (StarApplication.mUser != null) {
						StarApplication.mUser.setCoins(StarApplication.mUser.getCoins() - awardVo.getPrice());
					}
					// CommonUtil.getInstance().promptDialog(context,
					// R.string.tips, R.string.coins_coupon_msg_success,
					// R.string.coins_coupon_check, R.string.later);
					CommonUtil.getInstance().showPromptDialog(context, context.getString(R.string.tips),
							context.getString(R.string.coins_coupon_msg_success),
							context.getString(R.string.coins_coupon_check), context.getString(R.string.later),
							new PromptDialogClickListener() {

						@Override
						public void onConfirmClick() {
							CommonUtil.startActivity(context, MyCouponsActivity.class);
							if (context instanceof MyCoinsActivity) {
								CommonUtil.finishActivity(((MyCoinsActivity) context));
							}
						}

						@Override
						public void onCancelClick() {
							refreshCoin();
							if (context instanceof MyCoinsActivity) {
								((MyCoinsActivity) context).updateCoins();
							}
						}
					});
					break;
				case 1:
					ToastUtil.centerShowToast(context, context.getString(R.string.number_prizes_insufficinet));
					break;
				case 2:
					ToastUtil.centerShowToast(context, context.getString(R.string.current_balance_insufficinet));
					break;
				case 4:
					ToastUtil.centerShowToast(context, context.getString(R.string.server_is_busy));
					break;
				default:
					break;
				}
			}
			
			@Override
			public boolean onIntercept() {
				CommonUtil.showProgressDialog(context, null, context.getString(R.string.loading));
				return false;
			}
			@Override
			public void onFailure(int errorCode, String msg) {
				
			}
		});
	}
}
