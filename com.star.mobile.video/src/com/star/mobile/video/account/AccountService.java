package com.star.mobile.video.account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.text.TextUtils;

import com.star.cms.model.Bill;
import com.star.cms.model.Comment;
import com.star.cms.model.Payment;
import com.star.cms.model.User;
import com.star.cms.model.dto.LogonResult;
import com.star.cms.model.dto.RegisterResult;
import com.star.cms.model.dto.ResetPwdResult;
import com.star.cms.model.enm.AccountType;
import com.star.cms.model.enm.RegisterStatus;
import com.star.mobile.video.base.AbstractService;
import com.star.mobile.video.util.ApplicationUtil;
import com.star.mobile.video.util.Constant;
import com.star.util.loader.BitmapUploadParams;
import com.star.util.loader.LoadMode;
import com.star.util.loader.OnListResultListener;
import com.star.util.loader.OnResultListener;

public class AccountService extends AbstractService {

	public AccountService(Context context) {
		super(context);
	}
	
	/**
	 * 弃用，设备登录和注册合并一个接口
	 * @param deviceId
	 * @param listener
	 */
	@Deprecated
	public void register(String deviceId, OnResultListener<Boolean> listener) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("deviceID", deviceId);
		doPost(Constant.getRegisterUrl(), Boolean.class, params, listener);
	}
	
	public void login(String deviceId, OnResultListener<LogonResult> listener) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("deviceID", deviceId);
		params.put("versionCode", ApplicationUtil.getAppVerison(context));
		doPost(Constant.getLoginUrl(), LogonResult.class, params, listener);
	}

	public void logout(OnResultListener<Boolean> listener) {
		doPost(Constant.getRegisterUrl(), Boolean.class, null, listener);
	}
	
	public void unbindAccount(String account, Long parentID, OnResultListener<Boolean> listener) {
		String url = Constant.getBindAccountUrl() + "?account=" + account + "&parentID=" + parentID;
		doDelete(url, Boolean.class, listener);
	}
	
	public void getBills(String cardNo, Long date, OnListResultListener<Bill> listener) {
		doGet(Constant.getBillUrl(cardNo, date), Bill.class, LoadMode.CACHE_NET, listener);
	}
	
	public void getPayments(String cardNo, Long date, OnListResultListener<Payment> listener) {
		doGet(Constant.getPaymentUrl(cardNo, date), Payment.class, LoadMode.CACHE_NET, listener);
	}
	
	/**
	 * 登录（匿名登录，手机号登录，邮箱登录）
	 * @param userName
	 * @param password
	 * @param timeZoneID 时区
	 * @param deviceID 唯一标示
	 * @param type
	 * @param versionCode
	 * @param listener
	 */
	public void login(String userName, String password, String timeZoneID, String deviceID, AccountType type, Integer versionCode,OnResultListener<LogonResult> listener) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("username", userName);
		if (type != null) {
			params.put("type", type.getNum());
		}
		params.put("pwd", password);
		params.put("timeZoneID", timeZoneID);
		params.put("deviceID", deviceID);
		params.put("versionCode", versionCode);
		doPost(Constant.getLoginUrl(), LogonResult.class, params, listener);

	}
	
	/**
	 * 注册（手机号注册和邮箱注册）
	 * @param userName
	 * @param password
	 * @param nickName
	 * @param type
	 * @param veriCode
	 * @param invited
	 * @param versionCode
	 * @param listener
	 */
	public void register(String userName, String password, String nickName, int type, String veriCode,
		String invited, Integer versionCode,OnResultListener<RegisterResult> listener) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("username", userName);
		params.put("pwd", password);
		params.put("nickName", nickName);
		params.put("type", type);
		if (!TextUtils.isEmpty(invited)) {
			params.put("invitedID", invited);
		}
		if (!TextUtils.isEmpty(veriCode)) {
			params.put("veriCode", veriCode);
		}
		params.put("versionCode", versionCode);
		
		doPost(Constant.getUserNameReg(), RegisterResult.class, params, listener);
	}
	
	/**
	 * 提交用户反馈app 信息
	 * @param msg
	 * @param listener
	 */
	public void commitFeedback(String msg,String appVersion,OnResultListener<Comment> listener) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("msg", msg);
		params.put("appVersion", appVersion);
		doPost(Constant.getFeedbackUrl(), Comment.class, params, listener);
	} 
	
	/**
	 * 获取用户对app 评论
	 * @param listener
	 */
	public void getAppComments(OnListResultListener<Comment> listener) {
		doGet(Constant.getFeedbackUrl(), Comment.class, LoadMode.NET, listener);
	}


	
	/**
	 * 用户忘记密码
	 * @param emailAddress
	 * @param listener
	 */
	public void forWordPassword(String emailAddress,OnResultListener<ResetPwdResult> listener) {
		doGet(Constant.getReResetPwdUrl(emailAddress), ResetPwdResult.class, LoadMode.NET, listener);
	}
	
	/**
	 * 重发激活链接
	 * @param emailAddress
	 * @param listener
	 */
	public void resendActivationLink(String emailAddress,OnResultListener<RegisterStatus> listener) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("emailAddress", emailAddress);
		doPost(Constant.getresendActivationlinkUrl(), RegisterStatus.class, params, listener);
	}
	
	
	/**
	 * 重置密码
	 * @param oldPwd
	 * @param newPwd
	 * @param userName
	 * @param listener
	 */
	public void resetPasswrod(String oldPwd, String newPwd, String userName,OnResultListener<RegisterStatus> listener) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("oldPwd", oldPwd);
		params.put("newPwd", newPwd);
		params.put("userName", userName);
		doPost(Constant.getResetPasswrodUrl(), RegisterStatus.class, params, listener);
	}
	
	
	/**
	 * 获取用户可做任务数
	 * @param resultListener
	 */
	public void getTaskNumCanDo(OnResultListener<Integer> resultListener) {
		doGet(Constant.getTaskNumUrl(), Integer.class, LoadMode.CACHE_NET, resultListener);
	}
	
	
	/**
	 * 更新头像
	 * @param bitmap
	 * @param listener
	 */
	public void uploadHead(Bitmap bitmap,OnResultListener<Boolean> listener) {
		List<BitmapUploadParams> bitmaps = new ArrayList<BitmapUploadParams>();
		BitmapUploadParams param = new BitmapUploadParams();
		param.bitmap = bitmap;
		param.format = CompressFormat.PNG;
		param.url = Constant.getUploadHeadUrl();
		bitmaps.add(param);
		doPostImage(Boolean.class, bitmaps, listener);
	}
	
	/**
	 * 获取验证码
	 * @param phoneNumber
	 * @param listener
	 */
	public void getVerifCode(String phoneNumber,OnResultListener<Integer> listener) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("pNo", phoneNumber);
		doPost(Constant.getVerifCodeUrl(), Integer.class, params, listener);
	}
	
	/**
	 * 判断验证码是否正确
	 * 
	 * @param phoneNumber
	 * @param verifCode
	 * @return
	 */
	public void checkVerifCode(String phoneNumber, String verifCode,OnResultListener<Boolean> listener) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("veriCode", verifCode);
		params.put("pNo", phoneNumber);
		doPost(Constant.getCheckVerifCodeUrl(), Boolean.class, params, listener);
	}
	
	/**
	 * 修改昵称
	 * @param userID
	 * @param nickName
	 * @param listener
	 */
	public void updateNickNanme(long userID, String nickName,OnResultListener<Integer> listener) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userID", userID);
		params.put("nickName", nickName);
		doPost(Constant.getNickNameUrl(), Integer.class, params, listener);
	}
	/**
	 * 修改用户性别
	 * @param type
	 * @return
	 */
	public void updateSex(int type,OnResultListener<Integer> listener){
		Map<String ,Object>params=new HashMap<String,Object>();
		params.put("sex", type);
		doPost(Constant.getSexUrl(), Integer.class, params, listener);
	}
	
	/**
	 * 重置密码 发送验证码
	 * 
	 * @param phoneNumber
	 */
	public void resetPwdSendCode(String phoneNumber, int type,OnResultListener<ResetPwdResult> listener) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userName", phoneNumber);
		params.put("type", type);
		doPost(Constant.getResetPwdSendCodeUrl(), ResetPwdResult.class, params, listener);
	}
	
	/**
	 * 重置密码 邮箱和手机号
	 * 
	 * @param userName
	 * @return
	 */
	public void resetPwd(String userName, String newPwd, int type, String veriCode,OnResultListener<ResetPwdResult> listener) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userName", userName);
		params.put("newPwd", newPwd);
		params.put("type", type);
		params.put("veriCode", veriCode);
		doPost(Constant.getResetPwdUrl(), ResetPwdResult.class, params, listener);
	}
	
	public void getUser(OnResultListener<User> listener){
		doGet(Constant.getMeUrl(), User.class, LoadMode.CACHE_NET, listener);
	}
}
