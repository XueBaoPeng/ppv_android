package com.star.mobile.video.smartcard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.reflect.TypeToken;
import com.star.cms.model.BindCardCommand;
import com.star.cms.model.RechargeCMD;
import com.star.cms.model.User;
import com.star.cms.model.dto.RechargeResult;
import com.star.cms.model.vo.ChangePackageCMDVO;
import com.star.cms.model.vo.ChannelVO;
import com.star.cms.model.vo.SMSHistory;
import com.star.cms.model.vo.SmartCardInfoVO;
import com.star.mobile.video.base.AbstractService;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.IOUtil;
import com.star.util.InternalStorage;
import com.star.util.json.JSONUtil;
import com.star.util.loader.LoadMode;
import com.star.util.loader.OnListResultListener;
import com.star.util.loader.OnResultListener;

import android.content.Context;
import android.util.Log;

/**
 * 与绑卡有关的服务类
 * 
 * @author Lee
 * @date 2015/12/25
 */
public class SmartCardService extends AbstractService {

	public SmartCardService(Context context) {
		super(context);
	}

	/**
	 * 根据智能卡号删除绑定的智能卡
	 * 
	 * @param smartCardId
	 *            智能卡号
	 * @param listener
	 */
	public void delSmartCard(long smartCardId, OnResultListener<Boolean> listener) {
		doDelete(Constant.getDelSmartCardUrl(smartCardId), Boolean.class, listener);
	}

	public void delUserSmartCardNo(String smartCardNo, OnResultListener<Boolean> listener) {
		doDelete(Constant.getDelUserSmartCardNoUrl(smartCardNo), Boolean.class, listener);
	}

	/**
	 * 获得绑定卡的所有信息
	 * 
	 * @param listener
	 * @author Lee
	 */
	public void getAllSmartCardInfos(OnListResultListener<SmartCardInfoVO> listener) {
		doGet(Constant.getSmartCardNoUrl(), SmartCardInfoVO.class, LoadMode.CACHE_NET, listener);
	}

	/**
	 * 获得智能卡的信息
	 * 
	 * @param smartCardNo
	 *            智能卡号
	 * @param listener
	 */
	public void getSmartCardInfo(String smartCardNo, OnResultListener<SmartCardInfoVO> listener) {
		doGet(Constant.getSmartCardInfo(smartCardNo), SmartCardInfoVO.class, LoadMode.CACHE_NET, listener);
	}

	/**
	 * 绑定智能卡
	 * 
	 * @param cardNum
	 *            只能卡号
	 * @param stbCode
	 *            机顶盒号后四位
	 * @param versionCode
	 *            当前app的versionCode
	 * @param listener
	 */

	public void getbindSmartCard(String cardNum, OnResultListener<Integer> listener) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("smartcardNo", cardNum);
		doPost(Constant.getBindingCardAsynUrl(), Integer.class, params, listener);
	}

	/**
	 * 获得token
	 * 
	 * @param u
	 * @param listener
	 */
	public void bankToken(User u, OnResultListener<String> listener) {
		String o = System.nanoTime() + "";
		String token = u.genKey(o);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("t", token);
		params.put("o", o);
		doPost(Constant.getBkkkkUrl(), String.class, params, listener);
	}

	/**
	 * 充值
	 * 
	 * @param rechargeCard
	 *            充值卡
	 * @param smartCardNo
	 *            智能卡号
	 * @param exchageIDs
	 *            兑换的id
	 * @param appVersion
	 *            app的版本号
	 * @param rechargeBeforeMoney
	 *            充值前的金额
	 * @param listener
	 */
	public void recharge(String rechargeCard, String smartCardNo, List<Long> exchageIDs, int appVersion,
			Double rechargeBeforeMoney, OnResultListener<RechargeResult> listener) {
		Map<String, Object> params = new HashMap<String, Object>();
		if (rechargeCard != null) {
			params.put("rechargeCard", rechargeCard);
		}
		params.put("smartCardNo", smartCardNo);
		// params.put("rechargeBeforeMoney", rechargeBeforeMoney);
		params.put("appVersion", appVersion);
		if (exchageIDs.size() > 0)
			params.put("exchangeIds", exchageIDs);
		doPost(Constant.getRechargeUrl(), RechargeResult.class, params, listener);
	}
	
	/**
	 * 充值 异步
	 * @param rechargeCard
	 *            充值卡
	 * @param smartCardNo
	 *            智能卡号
	 * @param exchageIDs
	 *            兑换的id
	 * @param appVersion
	 *            app的版本号
	 * @param rechargeBeforeMoney
	 *            充值前的金额
	 * @param listener
	 */
	public void rechargeCard(String rechargeCard, String smartCardNo, List<Long> exchageIDs, int appVersion,
			Double rechargeBeforeMoney, OnResultListener<Integer> listener) {
		Map<String, Object> params = new HashMap<String, Object>();
		if (rechargeCard != null) {
			params.put("rechargeCard", rechargeCard);
		}
		params.put("smartcardNo", smartCardNo);
		params.put("rechargeType", RechargeCMD.RECHARGE_CARD_TYPE);
		if (exchageIDs.size() > 0)
			params.put("exchangeIds", exchageIDs);
		doPost(Constant.getAsyncRechargeUrl(), Integer.class, params, listener);
	}

	/**
	 * 判断用户是否绑过卡
	 * 
	 * @param listener
	 */
	public void isBindSmartCard(OnResultListener<Integer> listener) {
		doGet(Constant.getConstomerBindCardUrl(), Integer.class, LoadMode.NET, listener);
	}

	/**
	 * 获得订单的列表数据
	 * 
	 * @param listener
	 */
	public void getOrderList(OnListResultListener<SMSHistory> listener) {
		doGet(Constant.getOrderListUrl(), SMSHistory.class, LoadMode.CACHE_NET, listener);
	}
	/**
	 * 获得绑卡的订单详情
	 * @param listener
	 */
	public void getBindCardOrderDetail(long id,OnResultListener<BindCardCommand> listener){
		doGet(Constant.getBindCardDetailUrl(id), BindCardCommand.class, LoadMode.CACHE_NET, listener);
	}
	/**
	 * 获得换包的详情
	 * @param listener
	 */
	public void getChangePackageDetail(long id,OnResultListener<ChangePackageCMDVO> listener){
		doGet(Constant.getChangePackageDetailUrl(id), ChangePackageCMDVO.class, LoadMode.CACHE_NET, listener);
	}
	/**
	 * 获得充值的详情
	 * @param listener
	 */
	public void getChargeDetail(long id,OnResultListener<RechargeCMD> listener){
		doGet(Constant.getChargeDetailUrl(id), RechargeCMD.class, LoadMode.CACHE_NET, listener);
	}
	/**
	 * 获得绑卡的订单详情
	 */
	public List<SMSHistory> getOrderList(int index, int count, boolean cache){
		try {
			String json = IOUtil.httpGetToJSON(Constant.getOrderListUrl()+"?index="+index+"&count="+count, cache);
			if (json != null)
				return JSONUtil.getFromJSON(json, new TypeToken<ArrayList<SMSHistory>>() {}.getType());
		} catch (Exception e) {
			
		}
		return null;
	}
	/**
	 * 获得本地的数据
	 * @param context
	 * @param index
	 * @param count
	 * @return
	 */
	public List<SMSHistory> getOrderListFromLocal(Context context, int index, int count){
		try {
			String json = InternalStorage.getStorage(context).get(Constant.getOrderListUrl()+"?index="+index+"&count="+count);
			if(json!=null)
				return JSONUtil.getFromJSON(json, new TypeToken<ArrayList<SMSHistory>>() {}.getType());
		} catch (Exception e) {
		}
		return new ArrayList<SMSHistory>();
	}
}
