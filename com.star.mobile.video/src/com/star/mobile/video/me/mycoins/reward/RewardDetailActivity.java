package com.star.mobile.video.me.mycoins.reward;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.star.cms.model.Award;
import com.star.cms.model.vo.AwardVO;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.dialog.AlertDialog;
import com.star.mobile.video.me.coupon.ExchangeService;
import com.star.mobile.video.me.coupon.MyCouponsActivity;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.LoadingDataTask;
import com.star.mobile.video.util.ToastUtil;
import com.star.util.loader.OnResultListener;

public class RewardDetailActivity extends BaseActivity implements OnClickListener {
	
	private com.star.ui.ImageView ivDetailRewardIcon;
	private TextView tvRewardName;
	private TextView tvRewardDetailInfo;
	private TextView tvRewardPrice;
	private Button btConformExchange;
	private Intent intent;
	private ExchangeService exchangeService;
	private ImageView ivCoinsIcon;
	private TextView tvFaceValue;
	private AwardVO awardVo;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reward_detail);
		intent = getIntent();
		exchangeService = new ExchangeService(this);
		initView();
	}


	private void initView() {
		((TextView) findViewById(R.id.tv_actionbar_title)).setText(getResources().getString(R.string.reward_detail_title));
		((ImageView) findViewById(R.id.iv_actionbar_back)).setOnClickListener(this);
		ivDetailRewardIcon = (com.star.ui.ImageView) findViewById(R.id.iv_detail_reward_icon);
		tvRewardName = (TextView) findViewById(R.id.tv_reward_detail_name);
		tvRewardDetailInfo = (TextView) findViewById(R.id.tv_reward_detail_info);
		tvRewardPrice = (TextView) findViewById(R.id.tv_reward_price);
		btConformExchange = (Button) findViewById(R.id.bt_conform_exchange);
		ivCoinsIcon = (ImageView) findViewById(R.id.iv_detail_coins_icon);
		tvFaceValue = (TextView) findViewById(R.id.tv_face_value);
		currentIntent(getIntent());
	}	

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		currentIntent(intent);
	}
	
	private void currentIntent(Intent intent) {
		awardVo = (AwardVO) intent.getSerializableExtra("awardVo");
		tvRewardName.setText(awardVo.getName());
		tvRewardDetailInfo.setText(awardVo.getDescription());
		tvRewardPrice.setText(awardVo.getPrice()+"");
		if(awardVo.getPrice() == null|| awardVo.getName() == null) {
			finish();
			return;
		}
		if(intent.getIntExtra("mycoins", 0) < awardVo.getPrice()) {
			btConformExchange.setBackgroundResource(R.drawable.need_more_coins_button);
			btConformExchange.setText(getString(R.string.need_more_coins));
			btConformExchange.setOnClickListener(null);
			ivCoinsIcon.setBackgroundResource(R.drawable.need_more_coin_reward_detail);
			tvRewardPrice.setTextColor(getResources().getColor(R.color.onair_btn_unfocus));
		} else {
			btConformExchange.setBackgroundResource(R.drawable.orange_button_bg);
			btConformExchange.setText(getString(R.string.conform_to_exchange));
			btConformExchange.setOnClickListener(this);
			ivCoinsIcon.setBackgroundResource(R.drawable.coin_reward_detail);
			tvRewardPrice.setTextColor(getResources().getColor(R.color.orange));
		}
		
		if(awardVo.getType() == Award.Type_Wallet) {
			tvFaceValue.setVisibility(View.VISIBLE);
			String symbol = SharedPreferencesUtil.getCurrencSymbol(RewardDetailActivity.this);
			tvFaceValue.setText(symbol+(int)awardVo.getFaceValue()+"");
			int typeGet = awardVo.getTypeGet();
			if(typeGet == Award.FREE_COUPON) {
				ivDetailRewardIcon.setImageResource(R.drawable.free_coupon_dec);
			} else if(typeGet == Award.TYPE_COMMON) {
				ivDetailRewardIcon.setImageResource(R.drawable.blue_coupon_dec);
			} else if(typeGet == Award.TYPE_EXCHANGE) {
				ivDetailRewardIcon.setImageResource(R.drawable.free_coupon_dec);
			} else if(typeGet == Award.TYPE_NEW_CUSTOMER) {
				ivDetailRewardIcon.setImageResource(R.drawable.yellow_coupon_dec);
			}
		} else if(awardVo.getType() == Award.Type_Object) {
			tvFaceValue.setVisibility(View.GONE);
			String imageUrl = awardVo.getPoster().getResources().get(0).getUrl();
			if(imageUrl == null || "".equals(imageUrl)) {
				ivDetailRewardIcon.setImageResource(R.drawable.nopicture_reward_detail);
			} else {
				ivDetailRewardIcon.setUrl(imageUrl);
			}
		}
	}
	
	private void goExchange() {
				 exchangeService.exchange(awardVo.getId(),new OnResultListener<Integer>() {
					private Integer result;
					private AlertDialog dialog;
					@Override
					public void onSuccess(Integer value) {
						result=value;
						CommonUtil.closeProgressDialog();
						if(result == null)
							return;
						switch (result) {
						case 0:
							if(StarApplication.mUser!=null){
								StarApplication.mUser.setCoins(StarApplication.mUser.getCoins() - awardVo.getPrice());
							}
							
							CommonUtil.getInstance().showPromptDialog(RewardDetailActivity.this, null, String.format(getString(R.string.you_have_just_exchanged),awardVo.getName(),awardVo.getPrice()), getString(R.string.go_to_check_coupon), null, new CommonUtil.PromptDialogClickListener() {
								
								@Override
								public void onConfirmClick() {
									CommonUtil.startActivity(RewardDetailActivity.this, MyCouponsActivity.class);
									CommonUtil.finishActivity(RewardDetailActivity.this);
								}
								
								@Override
								public void onCancelClick() {
									
								}
							});
							break;
						case 1:
							ToastUtil.centerShowToast(RewardDetailActivity.this, getString(R.string.number_prizes_insufficinet));
							break;
						case 2:
							ToastUtil.centerShowToast(RewardDetailActivity.this, getString(R.string.current_balance_insufficinet));
							break;
						case 4:
							ToastUtil.centerShowToast(RewardDetailActivity.this, getString(R.string.server_is_busy));
							break;
						default:
							break;
						}
					}
					
					@Override
					public boolean onIntercept() {
						CommonUtil.showProgressDialog(RewardDetailActivity.this, null, getString(R.string.loading));
						return false;
					}
					
					@Override
					public void onFailure(int errorCode, String msg) {
					}
				});
	}
	

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.bt_conform_exchange:
			goExchange();
			break;
		case R.id.iv_actionbar_back:
			CommonUtil.finishActivityAnimation(RewardDetailActivity.this);
			break;
		default:
			break;
		}
		
	}
}
