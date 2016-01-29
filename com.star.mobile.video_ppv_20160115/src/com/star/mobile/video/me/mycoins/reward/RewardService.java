package com.star.mobile.video.me.mycoins.reward;


import android.content.Context;

import com.star.cms.model.vo.AwardVO;
import com.star.mobile.video.base.AbstractService;
import com.star.mobile.video.util.Constant;
import com.star.util.loader.LoadMode;
import com.star.util.loader.OnListResultListener;
import com.star.util.loader.OnResultListener;

public class RewardService extends AbstractService {

	public RewardService(Context context) {
		super(context);
	}

	public void cashPrize(String exchangeId, OnResultListener<Boolean> listener) {
		doPost(Constant.getCashUrl(exchangeId), Boolean.class, null, listener);
	}
	
	public void getRewards(boolean fromLocal,OnListResultListener<AwardVO>listener){
		if(fromLocal){
			doGet(Constant.getRewardsUrl(), AwardVO.class, LoadMode.CACHE, listener);
		}else{
			doGet(Constant.getRewardsUrl(), AwardVO.class, LoadMode.NET, listener);
		}
	}
	  
	/*public List<AwardVO> getRewards(boolean fromLocal) {
		try{
			String json = null;
			if(fromLocal) {
				json = IOUtil.getCachedJSON(Constant.getRewardsUrl());
			} else {
				json = IOUtil.httpGetToJSON(Constant.getRewardsUrl(), true);
			}
			if(json != null) {
				return JSONUtil.getFromJSON(json, new TypeToken<ArrayList<AwardVO>>() {}.getType());
			}
		} catch (Exception e) {
			Log.e(TAG, "Get rewards",e);
		}
		
		return null;
	}*/
}
