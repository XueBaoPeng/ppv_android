package com.star.mobile.video.service;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.star.cms.model.Area;
import com.star.cms.model.Egg;
import com.star.cms.model.User;
import com.star.cms.model.dto.EggBreakResult;
import com.star.mobile.video.R;
import com.star.mobile.video.base.AbstractService;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.ShareUtil;
import com.star.util.loader.LoadMode;
import com.star.util.loader.OnResultListener;

public class EggService extends AbstractService{
	
	public EggService(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public void homeIsExchanges(OnResultListener<Boolean> listener) {
		doGet(Constant.getHomeExchangesUrl(), Boolean.class, LoadMode.NET, listener);
	}
	
	/**
	 * 砸彩蛋
	 * @return
	 */
	public void breakOneEgg(String userName,String eggCode,int userType, OnResultListener<EggBreakResult> listener) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("eggCode", eggCode);
		doPost(Constant.breakOneEggUrl(), EggBreakResult.class, params, listener);
	} 
	
	/**
	 * 是否有彩蛋
	 * @return
	 */
	public void isExistEgg(OnResultListener<EggBreakResult> listener) {
		doGet(Constant.getExistEggUrl(), EggBreakResult.class, LoadMode.NET, listener);
	}
	
	/**
	 * 用户是否砸过蛋
	 * @return
	 */
	public void isUserBreakEgg(OnResultListener<Boolean> listener) {
		doGet(Constant.isUserBreakEgg(), Boolean.class, LoadMode.NET, listener);
	}
	
	public void shareUserEgg(OnResultListener<Egg> listener) {
		doGet(Constant.shareUserEggUrl(), Egg.class, LoadMode.IFCACHE_NOTNET, listener);
	}
	
	public void shareSysEgg(OnResultListener<Egg> listener) {
		doGet(Constant.getSysEggUrl(), Egg.class, LoadMode.NET, listener);
	}
	
	public void shareFreeCouponExchange(final Context context,final int shareTiming) {
		shareUserEgg(new OnResultListener<Egg>() {

			@Override
			public boolean onIntercept() {
				CommonUtil.showProgressDialog(context);
				return false;
			}

			@Override
			public void onSuccess(Egg egg) {
				CommonUtil.closeProgressDialog();
				if(egg != null && egg.getSharedUrl()!=null){ 
					ShareUtil.shareEgg(context, "",egg.getSharedUrl(),shareTiming,0.0);
				}
			}

			@Override
			public void onFailure(int errorCode, String msg) {
				CommonUtil.closeProgressDialog();
			}
		});
	}
	
	@Deprecated
	public void shareOneEgg(final Context context, final User user, final double faceValue){
		shareUserEgg(new OnResultListener<Egg>() {

			@Override
			public boolean onIntercept() {
				CommonUtil.showProgressDialog(context);
				return false;
			}

			@Override
			public void onSuccess(Egg egg) {
				CommonUtil.closeProgressDialog();
				if(egg != null && egg.getSharedUrl()!=null){
					if(faceValue == 0.0){
						String shareInfo = context.getString(R.string.share_egg_default);	
						String areaCode = SharedPreferencesUtil.getAreaCode(context);
						if(Area.TANZANIA_CODE.equals(areaCode)){
							shareInfo = String.format(context.getString(R.string.share_egg), SharedPreferencesUtil.getCurrencSymbol(context)+10000);
						}else if(Area.NIGERIA_CODE.equals(areaCode)){
							shareInfo = String.format(context.getString(R.string.share_egg), SharedPreferencesUtil.getCurrencSymbol(context)+1000);
						}
						ShareUtil.shareEgg(context, shareInfo,egg.getSharedUrl(),ShareUtil.DIRECTLY_SHARE,faceValue);
					}else{
						String info = String.format(context.getString(R.string.share_egg_after_recharge), faceValue);
						ShareUtil.shareEgg(context, info,egg.getSharedUrl(),ShareUtil.RECHARGE_SHARE,faceValue);
					}
				}
			}

			@Override
			public void onFailure(int errorCode, String msg) {
				CommonUtil.closeProgressDialog();
			}
		});
	}
}
