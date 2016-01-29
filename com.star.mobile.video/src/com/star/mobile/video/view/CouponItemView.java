package com.star.mobile.video.view;

import java.io.Serializable;

import com.star.cms.model.Award;
import com.star.cms.model.vo.ExchangeVO;
import com.star.cms.model.vo.SmartCardInfoVO;
import com.star.mobile.video.R;
import com.star.mobile.video.service.EggAppearService;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.smartcard.recharge.RechargeCardNumActivity;
import com.star.mobile.video.smartcard.recharge.RechargeCouponActivity;
import com.star.mobile.video.smartcard.recharge.RechargeSmartCardActivity;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.DateFormat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CouponItemView extends RelativeLayout{

	private View mView;
	private RelativeLayout rlCouponItem;
//	private ImageView rewardIcon;
	private TextView rewardName;
	private TextView exchangeDate;
//	private RelativeLayout couponBottom;
//	private Button rechargeBtn;
	private TextView tvNoOrLimit;
//	private TextView tvUseStatus;
	private TextView tvMyCouponPrice;
	private LinearLayout llPrice;
	private TextView tvSymbol;
	private android.widget.ImageView exchangeInfo;
	private android.widget.ImageView usedMark;
	private Context context;
	private String unit;
	private String cardNum;
	private SmartCardInfoVO smartInfo;
	
	public CouponItemView(Context context) {
		this(context, null);
	}
	
	public CouponItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		mView = LayoutInflater.from(context).inflate(R.layout.view_coupon_item, this);
		rlCouponItem = (RelativeLayout) mView.findViewById(R.id.rl_coupon);
//		rewardIcon = (ImageView) mView.findViewById(R.id.iv_coupon_reward_icon);
		rewardName = (TextView) mView.findViewById(R.id.tv_reward_name);
		exchangeDate = (TextView) mView.findViewById(R.id.tv_exchange_date);
		tvNoOrLimit = (TextView) mView.findViewById(R.id.tv_NoOrLimit);
//		tvUseStatus = (TextView) mView.findViewById(R.id.tv_use_status);
//		couponBottom = (RelativeLayout) mView.findViewById(R.id.ll_coupon_bottom);
//		rechargeBtn = (Button) mView.findViewById(R.id.btn_recharge);
		exchangeInfo = (android.widget.ImageView) mView.findViewById(R.id.tv_exchange_info);
		usedMark = (android.widget.ImageView) mView.findViewById(R.id.iv_used_mark);
		tvMyCouponPrice = (TextView) mView.findViewById(R.id.tv_my_coupon_price);
		llPrice = (LinearLayout) mView.findViewById(R.id.ll_icon_price);
		tvSymbol = (TextView) mView.findViewById(R.id.tv_symbol);
		
		unit = SharedPreferencesUtil.getCurrencSymbol(context);
	}
	
	public void fillData(final ExchangeVO vo,final String cardnum ,SmartCardInfoVO smartinfo){
		cardNum=cardnum;
		smartInfo=smartinfo;
		init();
		exchangeInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CommonUtil.showMyCouponsInfoDialog(context, vo);
			}
		});
		exchangeDate.setText(context.getString(R.string.expiration_date)+" "+DateFormat.format(vo.getValidDate(),"yyyy-MM-dd"));
		if((vo.getTypeGet()==ExchangeVO.TYPE_COMMON&&vo.getType()==ExchangeVO.Type_Wallet) 
				|| vo.getTypeGet()==ExchangeVO.TYPE_EXCHANGE 
				|| vo.getTypeGet()==ExchangeVO.TYPE_NEW_CUSTOMER 
				|| vo.getTypeGet() == ExchangeVO.FREE_COUPON 
				|| vo.getTypeGet()==ExchangeVO.CLUB_COUPON){
			if(vo.getName() == null){
				rewardName.setText(context.getString(R.string.coupon)+" ");
//				rewardName.setText(unit+Double.valueOf(vo.getFaceValue()).intValue()+" ");
			}else{
				rewardName.setText(vo.getName());
			}
		}else{
			rewardName.setText(vo.getDescription());
		}
		if(vo.getType() == ExchangeVO.Type_Object) {
//			rechargeBtn.setVisibility(View.GONE);
//			llPrice.setVisibility(View.GONE);
//			try{
//				List<Resource> res = vo.getPoster().getResources();
//				if(res != null && res.size() > 0) {
//					rewardIcon.setUrl(res.get(0).getUrl());
//				} else {
//					rewardIcon.setImageResource(R.drawable.no_picture_basic);
//				}
//			}catch(Exception e){
//				rewardIcon.setImageResource(R.drawable.no_picture_basic);
//			}
//			if(vo.isValid() || vo.isAccepted()) {
//				setValid(vo);
//			}
		} else {
			int typeGet = vo.getTypeGet();
			llPrice.setVisibility(View.VISIBLE);
			tvSymbol.setText(unit);
			tvMyCouponPrice.setText((int)vo.getFaceValue()+"");
			if(typeGet == ExchangeVO.TYPE_COMMON && typeGet == ExchangeVO.Type_Wallet){
				setUnlimited(vo);
			}else if(typeGet == ExchangeVO.TYPE_EXCHANGE || typeGet == ExchangeVO.CLUB_COUPON){
				setLimited(vo);
			}else if(typeGet == ExchangeVO.TYPE_NEW_CUSTOMER){
				setNew(vo);
			} else if (typeGet == ExchangeVO.FREE_COUPON) {
				setFreeCoupon(vo);
			} else{
//				llPrice.setVisibility(View.GONE);
//				tvNoOrLimit.setText(context.getString(R.string.exchange_no)+vo.getExchangeNo());
			}
			if(vo.isValid() || vo.isAccepted()){
				setValid(vo);
			}
		}
	}
	
	private void init(){
//		rewardIcon.setFinisher(null);
//		exchangeInfo.setVisibility(View.GONE);
//		rechargeBtn.setVisibility(View.GONE);
		rlCouponItem.setBackgroundResource(R.drawable.bg_red_coupon);
//		couponBottom.setBackgroundResource(R.drawable.my_coupon_bg_explan);
//		rewardName.setTextColor(getResources().getColor(R.color.choose_text));
//		tvNoOrLimit.setTextColor(getResources().getColor(R.color.choose_text));
		usedMark.setVisibility(View.GONE);
//		tvUseStatus.setVisibility(View.GONE);
	}
	


	private void setRechargeBtnStatus(final ExchangeVO vo) {
//		rechargeBtn.setVisibility(View.VISIBLE);
//		rechargeBtn.setText(context.getString(R.string.recharge));
		rlCouponItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				 if(cardNum!=null){
					 if(vo.getTypeGet()==ExchangeVO.TYPE_COMMON||vo.getTypeGet()==ExchangeVO.TYPE_NEW_CUSTOMER || vo.getTypeGet() == ExchangeVO.FREE_COUPON){
							Intent intent = new Intent(context, RechargeCouponActivity.class);
							intent.putExtra("smartcardinfovo",(Serializable) smartInfo);
							intent.putExtra("exchange", vo);
							CommonUtil.startActivity((Activity)context, intent);
						}else if(vo.getTypeGet()==ExchangeVO.TYPE_EXCHANGE||vo.getTypeGet()==ExchangeVO.CLUB_COUPON){
							Intent intent = new Intent(context, RechargeSmartCardActivity.class);
							intent.putExtra("smartcardinfovo", (Serializable)smartInfo);
							intent.putExtra("exchange", vo);
							intent.putExtra("hideCoupon", true);
							CommonUtil.startActivity((Activity)context, intent);
						}else{ 
							Intent intent = new Intent(context, RechargeSmartCardActivity.class);
							intent.putExtra("exchange", vo);
							intent.putExtra("smartcardinfovo",(Serializable)smartInfo);
							intent.putExtra("hideCoupon", false);
							CommonUtil.startActivity((Activity)context, intent);
						}
				 }else{
					 Intent intent=new Intent(context,RechargeCardNumActivity.class);
					 intent.putExtra("exchange",  vo);
					 CommonUtil.startActivity((Activity)context, intent);
				 } 
				
				 /* Intent intent = new Intent(context, TopupActivity.class);
				//Intent intent = new Intent(context, RechargeSmartCardActivity.class);
				intent.putExtra("exchange", vo);
				CommonUtil.startActivity((Activity)context, intent);*/
			}
		});
	}
	private void setShareBtnStatus(final ExchangeVO vo) {
		rlCouponItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EggAppearService.shareNewExchange(context, vo.getId());
			}
		});
	}
	private void setFreeCoupon(ExchangeVO vo) {
		if(vo.getUseStatus() == ExchangeVO.STATUS_AVAILABLE) {
			setRechargeBtnStatus(vo);
		}
//		rewardIcon.setImageResource(R.drawable.frren_coupon_list);
//		couponBottom.setBackgroundResource(R.drawable.my_coupon_bg_green);
		rlCouponItem.setBackgroundResource(R.drawable.coupon_bg_free);
		tvNoOrLimit.setText(context.getString(R.string.coupon_free));
//		rewardName.setTextColor(context.getResources().getColor(R.color.egg_green));
	}
	
	private void setUnlimited(ExchangeVO vo){
		if(vo.getUseStatus() == ExchangeVO.STATUS_AVAILABLE)
			setRechargeBtnStatus(vo);
//		rewardIcon.setImageResource(R.drawable.blue_coupon_list);
		rlCouponItem.setBackgroundResource(R.drawable.coupon_bg_common);
//		rewardName.setTextColor(context.getResources().getColor(R.color.egg_blue));
		tvNoOrLimit.setText(context.getString(R.string.coupon_common_limit));
//		couponBottom.setBackgroundResource(R.drawable.my_coupon_bg_blue);
		rewardName.setText(context.getString(R.string.coupon)+" ");
	}
	
	private void setLimited(ExchangeVO vo){
		if(vo.getUseStatus() == ExchangeVO.STATUS_AVAILABLE)
			setRechargeBtnStatus(vo);
//		rewardIcon.setImageResource(R.drawable.orange_coupon_list);
//		rlCouponItem.setBackgroundResource(R.drawable.coupon_bg_common);
//		rewardName.setTextColor(context.getResources().getColor(R.color.egg_red));
		int lessAmount = Double.valueOf(vo.getFaceValue()).intValue()*5;
		if(vo.getLessAmount()!=null){
			lessAmount = vo.getLessAmount().intValue();
		}
		tvNoOrLimit.setText(String.format(context.getString(R.string.coupon_exchange_limit), (unit+lessAmount)));
//		couponBottom.setBackgroundResource(R.drawable.my_coupon_bg_red);
		rewardName.setText(context.getString(R.string.coupon)+" ");
	}
	
	private void setNew(ExchangeVO vo){
//		rewardName.setTextColor(context.getResources().getColor(R.color.egg_yellow));
		rlCouponItem.setBackgroundResource(R.drawable.coupon_bg_common);
		tvNoOrLimit.setText(context.getString(R.string.coupon_newCus_limit));
		if(vo.getUseStatus() == ExchangeVO.STATUS_AVAILABLE){
			if(vo.getUseType() == ExchangeVO.UseType_Recharge){
				setRechargeBtnStatus(vo);
			}else if(vo.getUseType() == ExchangeVO.UseType_Share){
				setShareBtnStatus(vo);
			}
		}//		couponBottom.setBackgroundResource(R.drawable.my_coupon_bg_yellow);
		else{
			setValid(vo);
		}
		rewardName.setText(context.getString(R.string.coupon)+" ");
	}
	
	private void setValid(ExchangeVO vo){
//		rewardIcon.setImageResource(R.drawable.grey_coupon_list);
		rlCouponItem.setBackgroundResource(R.drawable.coupon_bg_explan);
		rlCouponItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
		int typeGet = vo.getTypeGet();
		if(typeGet != ExchangeVO.FREE_COUPON){
			rewardName.setText(context.getString(R.string.coupon)+" ");
		}
//		couponBottom.setBackgroundResource(R.drawable.my_expire_coupon_bg_explan);
//		rewardName.setTextColor(getResources().getColor(R.color.no_select_coupon_title));
//		tvNoOrLimit.setTextColor(getResources().getColor(R.color.no_select_coupon_title));
//		exchangeInfo.setVisibility(View.GONE);
//		rechargeBtn.setVisibility(View.GONE);
		if(vo.isAccepted()){
			vo.isValid();
			usedMark.setVisibility(View.VISIBLE);
			if(vo.getType() == Award.Type_Wallet) {
				llPrice.setVisibility(View.VISIBLE);
				tvSymbol.setText(unit);
				tvMyCouponPrice.setText((int)vo.getFaceValue()+"");
			}
		}else{
			usedMark.setVisibility(View.VISIBLE);
			usedMark.setImageResource(R.drawable.explred_icon_coupon);
		}
		
	}
}
