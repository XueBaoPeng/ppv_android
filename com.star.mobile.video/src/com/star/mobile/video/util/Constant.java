package com.star.mobile.video.util;

import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;

import com.star.mobile.video.StarApplication;

public class Constant {
	
	public static String SERVER_URL;
	private static String BBS_NEW_TOPIC_URL;
	public final static String NUMBER_REG = "^[0-9]+$";
	
	/**开发版*/
	public static final int DEVELOPMENT_VERSION = 1;
	
	/**正式版*/
	public static final int FINAL_VERSION = 0;
	
//	/**当前版本*/
//	public static final int CURRENT_VERSION = DEVELOPMENT_VERSION;
	
	public static void setServerIP(String url){
		SERVER_URL = url;
	}
	
	public static String getServerIp() {
		return SERVER_URL;
	}
	public static void setBbsNewTopicUrl(String url){
		BBS_NEW_TOPIC_URL = url;
	}
	
	public static String getEpgCommentsUrl(long programID){
		return SERVER_URL+"/programs/"+programID+"/comments";
	}
	
	public static String getEpgFavStatusUrl(long programID){
		return SERVER_URL+"/programs/"+programID+"/favs";
	}
	
	public static String getChnFavStatusUrl(long channelID){
		return SERVER_URL+"/channels/"+channelID+"/favs";
	}
	/**
	 * 获得频道评分的详情
	 * @param commentID
	 * @return
	 */
	public static String getChannelSource(long commentID){
		return SERVER_URL+"/channels/comments/"+commentID;
	}
	public static String getChannelCommentsUrl(long channelID){
		return SERVER_URL+"/channels/"+channelID+"/comments";
	}
	/**获取频道下视频url*/
	public static String getChannelVideoUrl() {
		return SERVER_URL +"/public/vod?code=";
	}
	/**获取有视频的频道*/
	public static String getVideoChannelUrl() {
		return SERVER_URL +"/video/channels";
	}
	/**获取频道下视频url*/
	public static String getVideoUrl() {
		return SERVER_URL +"/public/vods?channelID=";
	}
	/**获取推荐下视频url*/
	public static String getRecommendVideoUrl() {
		return SERVER_URL +"/vods/recomments";
	}
	/**
	 * 获取频道的评论
	 */
	public static String getChannelCommentUrl(long channelID){
		return SERVER_URL+"/channels/"+channelID+"/commentsScore";
	}
	/**
	 * 提交频道的评论
	 */
	public static String getChannelCommentRateUrl(long channelID){
		return SERVER_URL+"/channels/"+channelID+"/comments";
	}
	/**
	 * 编辑提交频道的评论
	 */
	public static String getChannelCommentRateEditUrl(long commentID){
		return SERVER_URL+"/channels/comments/"+commentID;
	}
	/**
	 * 根据频道ID，获取推荐的视频
	 * @return
	 */
	public static String getRecommendVideoByChannelId(){
		return SERVER_URL+"/vod/channel?channelId=";
	}
	/**频道推荐视频*/
	public static String getRecommendVideo() {
		return SERVER_URL +"/vods/recommend";
	}
	/**获取所有视频*/
	public static String getAllVideo() {
		return SERVER_URL +"/vods";
	}
	public static String getCategoryUrl(){
		return SERVER_URL+"/categories";
	}
	
	public static String getRecommendUrl(Context context){
		return SERVER_URL+"/recommends/all?status=1&versionType="+StarApplication.CURRENT_VERSION+"&versionCode="+ApplicationUtil.getAppVerison(context);
	}
	
	/**是否需要更新*/
	public static String getAppNewest() {
		return SERVER_URL+"/public/appNewest?versionType="+StarApplication.CURRENT_VERSION;
	}
	
	public static String getReportQuestionUrl(String appVersion){
		return SERVER_URL+"/questions/"+appVersion;
	}
	
	public static String getReportAnswerUrl(String appVersion){
		return SERVER_URL+"/answers/"+appVersion;
	}
	
	public static String getSyncUrl(){
		return SERVER_URL+"/snapshot/sych";
	}
	
	public static String getSyncUrl_nw(){
		return SERVER_URL+"/sync";
	}
	
	public static String getPlayCountUrl(){
		return SERVER_URL+"/vods/play";
	}
	
	public static String getPackageUrl(Context context, List<Integer> types){
		String url = "?version="+ApplicationUtil.getAppVerison(context);
		for(int i = 0;i<types.size();i++) {
			url+="&types="+types.get(i);
		}
		return SERVER_URL+"/packages"+url;
	}
	
	public static String getChannelUrl(){
		return SERVER_URL+"/channels";
	}
	public static String getSexUrl(){
		return SERVER_URL+"/user/sex";
	}
	public static String getSnapshotChannelUrl(List<Integer> types){
		String url = "";
		for(int i = 0;i<types.size();i++) {
			if(i == 0) {
				url = "?types="+types.get(i);
			} else {
				url+="&types="+types.get(i);
			}
		}
		return SERVER_URL+"/snapshot/channels"+url;
	}
	
	public static String getFAQPropertyUrl(){
		return SERVER_URL+"/chatrooms/faq/property";
	}
	
	public static String getSnapshotProgramUrl(){
		return SERVER_URL+"/snapshot/programs";
	}
	
	public static String getLastEpgUrl(){
		return SERVER_URL+"/programRecentTime";
	}
	
	public static String getEpgUrl(){
		return SERVER_URL+"/programs";
	}
	
	public static String getAirEpgUrl(){
		return SERVER_URL+"/air/programs";
	}
	
	/**点赞url*/
	public static String getPraiseUrl(String commentID) {
		return SERVER_URL+"/comments/"+commentID;
	}
	
	/**搜索url*/
	public static String getSearchUrl() {
		return SERVER_URL+"/search";
	}
	
	/**获取用户所有task*/
	public static String getTasksUrl(int versionCode) {
		return SERVER_URL+"/tasks?appVersion="+versionCode;
	}
	/**做任务*/
	public static String getTaskUrl() {
		return SERVER_URL+"/tasks/doAndAcceptCoin";
	}
	
	/**领取金币*/
	public static String getGoldUrl(long taskId) {
		return SERVER_URL+"/tasks/"+taskId;
	} 
	
	/**领取金币*/
	public static String getGoldUrl(String taskCode) {
		return SERVER_URL+"/tasks/"+taskCode;
	} 
	
	/**Me*/
	public static String getMeUrl() {
		return SERVER_URL + "/users/me";
	}
	
	/**修改NickName*/
	public static String getNickNameUrl() {
		return SERVER_URL + "/user/nickname";
	}
	
	/**登录url*/
	public static String getLoginUrl() {
		return SERVER_URL +"/login";
	}
	
	/**登出url*/
	public static String getLogoutUrl() {
		return SERVER_URL + "/loginout";
	}
	
	/**diciveId 注册 url*/
	public static String getRegisterUrl() {
		return SERVER_URL + "/register";
	}
	
	/**获取地区url*/
	public static String getAreaUrl() {
		return SERVER_URL+"/areas";
	}
	
	/** 分享用户egg*/
	public static String shareUserEggUrl() {
		return SERVER_URL+"/egg/shareByUser";
	}
	
	/** 分享exchange*/
	public static String shareExchangeUrl(long exchangeId) {
		return SERVER_URL+"/exchange/share/"+exchangeId;
	}
	
	/** username 注册*/
	public static String getUserNameReg() {
		return SERVER_URL+"/public/registerNew";
	}
	
	/**登录之后选区*/
	public static String getLoginArea() {
		return SERVER_URL +"/users/updateArea";
	}
	/**查看可做任务数*/
	public static String getTaskNumUrl() {
		return SERVER_URL +"/tasks/num";
	}
	
	/**绑定智能卡*/
	public static String getBindingCardUrl() {
		return SERVER_URL+"/users/updateSmartCard";
	}
	
	/**
	 * 绑定智能卡 1.2.7+
	 */
	public static String getBindingCardNewUrl() {
		return SERVER_URL+"/users/smartCardNew";
	}
	
	public static String getBindingCardAsynUrl() {
		return SERVER_URL+"/sms/smartcard/bind";
	}
//	/**查询余额 1.2.7+*/
//	@Deprecated
//	public static String getMoneyUrl(long cusID) {
//		return SERVER_URL+"/smartcards/"+cusID;
//	}
//	
//	/**查询余额 */
//	@Deprecated
//	public static String getMoneyUrl() {
//		return SERVER_URL+"/users/money";
//	}
	
	/**查询余额 1.2.9+*/
	public static String getMoneyUrl(String smartCard) {
		return SERVER_URL+"/smartcards?smartCardNo="+smartCard;
	}
	
	/**充值*/
	public static String getRechargeUrl() {
		return SERVER_URL+"/user/recharge";
	}
	
	/**充值 异步*/
	public static String getAsyncRechargeUrl() {
		return SERVER_URL+"/sms/recharge";
	}
	/**
	 * 换包
	 */
	public static String getChangeBouquetUrl() {
		return SERVER_URL+"/sms/change/package";
	}
	/**提交意见url*/
//	public static String getFeedbackUrl() {
//		return SERVER_URL+"/users/feedback";
//	}
	
	/**检测版本是否激活*/
	public static String getMyApp(int versionCode) {
		return SERVER_URL+"/app/myApp?version="+versionCode;
	}
	
	public static String getDetailAppInfo(int versionCode) {
		return SERVER_URL+"/public/appDetail?version="+versionCode;
	}
	
	public static String getUploadHeadUrl(){
		return SERVER_URL+"/user/updateHead";
	}
	
	/**获取所有奖品url*/
	public static String getRewardsUrl() {
		return SERVER_URL+"/awards";
	}
	
	/**获取以兑换的奖品*/
	public static String getExchangesUrl() {
		return SERVER_URL +"/exchanges";
	}
	
	/**兑现奖品url*/
	public static String getCashUrl(String exchangeId) {
		return SERVER_URL+"/exchange/"+exchangeId;
	}
	
	/**兑换奖品url*/
	public static String getExchangeUrl() {
		return SERVER_URL+"/exchanges";
	}
	
	/**搜索推荐词*/
	public static String getSearchHostKeyUrl() {
		return SERVER_URL+"/search/hotkeys";
	}
	/**搜索详情*/
	public static String getSearchDetail() {
		return SERVER_URL+"/search/detail";
	}
	
	
	/**搜索推荐词*/
	public static String getSearchTopUrl(String keys) {
		return SERVER_URL+"/search/suolue?keys="+keys;
	}
	
	/**没有领取优惠卷个数*/
	public static String getExchangeNoReceiveNumberUrl() {
		return SERVER_URL+"/exchange/noreceive";
	} 
	
	/**重置密码url*/
	@Deprecated
	public static String getResetPasswordUrl(String emailAddress) {
		return SERVER_URL+"/public/resetPwd?emailAddress="+emailAddress;
	}
	/**重置密码url 1.2.7*/
	public static String getReResetPwdUrl(String emailAddress) {
		return SERVER_URL+"/public/resetpwd?emailAddress="+emailAddress;
	}
	
	/**重新发送激活链接url*/
	public static String getresendActivationlinkUrl() {
		return SERVER_URL+"/public/reactivate";
	}
	
	/**获取用户反馈url*/
	public static String getFeedbackUrl() {
		return SERVER_URL +"/feedbacks";
	}
	
	/**发送图片url*/
	public static String getChatSendImageUrl(long channelID,String deviceName,String extendFileName) {
		return SERVER_URL +"/channels/"+channelID+"/charts/image?deviceName="+deviceName+"&extendFileName="+extendFileName;
	}
	
	/**发送文字url*/
	public static String getChatSendTextUrl(long channelID) {
		return SERVER_URL +"/channels/"+channelID+"/charts/text";
	}
	
	public static String getChatNum(long channelID) {
		return SERVER_URL +"/channels/"+channelID+"/chatNum";
	}
	
	/**获取chat历史url*/
	public static String getHistoryChatsUrl(long channelID) {
		return SERVER_URL+"/channels/"+channelID+"/historyChats";
	}
	
	/**获取chat url*/
	public static String getChatsUrl(long channelID) {
		return SERVER_URL+"/channels/"+channelID+"/chartsOnline";
	}
	
	public static String getChatRoomsUrl(int version) {
		return SERVER_URL+"/channel/chatRooms?version="+version;
	}
	
	public static String getChatRoomDetailUrl(long cashId) {
		return SERVER_URL+"/channel/chatRoom?cashId="+cashId;
	}
	
	public static String getChatRoomPromptUrl(long cashId){
		return SERVER_URL+"/chatroom/"+cashId+"/notification";
	}
	
	public static String getProductUrl(String resCode) {
		
		return SERVER_URL+"/users/queryProductsByResCode?resCode="+resCode;
	}
	/**重置密码*/
	public static String getResetPasswrodUrl() {
		
		return SERVER_URL+"/user/password";
	}
	
	/**获取面值*/
	public static String getExchangeFaceValueUrl() {
		return SERVER_URL+"/awards4Recharge";
	}
	
	/**删除用户smartCardNo url*/
	public static String getDelUserSmartCardNoUrl(String smartCardNo) {
		return SERVER_URL+"/user/smartCard/"+smartCardNo;
	}
	
	/**查询智能卡号url 1.2.7 +*/
	public static String getSmartCardNoUrl() {
		return SERVER_URL+"/user/smartcardNos";
	}
	
	/**删除智能卡 1.2.7 +*/
	public static String getDelSmartCardUrl (long smartCardInfoId) {
		return SERVER_URL+"/user/smartCardInfo/"+smartCardInfoId;
	}
	
	/**查询智能卡信息*/
	public static String getSmartCardInfo(String smartCardNo) {
		return SERVER_URL+"/user/smartcardinfo?smartCardNo="+smartCardNo;
	}
	
	/**即将罚停智能卡列表*/
	public static String getExpectedStopSmartcard() {
		return SERVER_URL+"/user/smartcardsStop";
	}
	
	
	/**查询客户信息url*/
	public static String getCustomers(String smartCardNo) {
		return SERVER_URL+"/users/queryAllCustomersByCondition?cardCode="+smartCardNo;
	}
	/*获得ppv节目列表数据的url*/
	public static String getPPvDataUrl(){
		return SERVER_URL+"/ppv";
	}
	public static String getIsexist(){
		return SERVER_URL+"/user/exist";
	}
	
	public static String getBindAccountUrl(){
		return SERVER_URL+"/user/bindAccountNew";
	}
	
	public static String getBindedAccountsUrl(Long parentID){
		return SERVER_URL+"/user/accounts?parentID="+parentID;
	}
	
	/**获取用户是否在首页砸过彩蛋 url*/
	public static String getHomeExchangesUrl() {
		return SERVER_URL+"/exchanges/sys";
	}
	
	/**砸彩蛋url*/
	public static String breakOneEggUrl() {
		
		return SERVER_URL+"/egg/breakApp";
	}
	
	public static String getEggProbabilityUrl(){
		return SERVER_URL+"/egg/probability";
	}
	
	/**url*/
	public static String getBkkkkUrl() {
		return SERVER_URL +"/sure/kkk";
	}
	/**
	 * 是否有彩蛋url
	 * @return
	 */
	public static String getExistEggUrl() {
		return SERVER_URL+"/egg/sureBreakCoupon";
	}
	
	/**
	 * 获取系统彩蛋 url
	 * @return
	 */
	public static String getSysEggUrl() {
		
		return SERVER_URL+"/egg/shareBySys";
	}
	
	/**
	 * 用户当天是否可以砸蛋
	 * @return
	 */
	public static String isUserBreakEgg() {
		
		return SERVER_URL+"/egg/canBreakToday";
	}
	
	/**
	 * 获取验证码
	 * @return
	 */
	public static String getVerifCodeUrl() {
		
		return SERVER_URL+"/public/phone";
	}
	
	/**
	 * 验证验证码是否正确
	 * @return
	 */
	public static String getCheckVerifCodeUrl() {
		
		return SERVER_URL+"/public/verfyCode";
	}
	
	/**
	 * 单个聊天室提醒
	 * @param cashId
	 * @return
	 */
	public static String getOneChatRoomAlertUrl(long cashId) {
		return SERVER_URL+"/chatroom/"+cashId+"/alert";
	}
	
	/**
	 * 设置所有聊天室提醒
	 * @return
	 */
	public static String getAllChatRoomAlertSettingUrl() {
		return SERVER_URL+"/chatroom/alert";
	}
	
	/**
	 * 获取所有聊天室提醒
	 * @return
	 */
	public static String getAllCharRoomAlertUrl() {
		
		return SERVER_URL+"/chatroom/property";
	}
	
	public static String getPaymentUrl(String cardNo,Long date) {
		return SERVER_URL+"/user/payment?smartCard="+cardNo+"&date="+date;
	}
	
	public static String getBillUrl(String cardNo,Long date) {
		
		return SERVER_URL+"/user/bill?smartCard="+cardNo+"&date="+date;
	}
	
	/**
	 * 重置密码 发送验证码
	 * @return
	 */
	public static String getResetPwdSendCodeUrl() {
		
		return SERVER_URL+"/public/smsCode";
	}
	
	public static String getTenbUrl(int index,int count) {
		return SERVER_URL+"/tenbMe?index="+index+"&count="+count;
	}
	
	public static String postTenbUrl() {
		return SERVER_URL+"/tenbMe";
	}
	
	public static String getTenbTopicUrl(long topicId) {
		return SERVER_URL+"/tenbTopic?topicId="+topicId;
	}
		
	public static String getDiscoveryUrl() {
		return SERVER_URL+"/discovery";
	}
	
	public static String getNewTopicStatusUrl(Long userId){
		return BBS_NEW_TOPIC_URL+"?userId="+userId;
	}
	
	public static String getSyncAppStatusUrl(){
		return SERVER_URL+"/sync/appstatus";
	}
	
	/**
	 * 重置密码(邮箱和手机号)
	 * @return
	 */
	public static String getResetPwdUrl() {
		
		return SERVER_URL+"/public/resetPassward";
	} 
	/**
	 * 获得用户是否绑卡的url
	 * @return
	 */
	public static String getConstomerBindCardUrl(){
		return SERVER_URL+"/smartcard/bind";
	}
	/**
	 * 获取订单的url
	 * @return
	 */
	public static String getOrderListUrl(){
		return SERVER_URL+"/sms/order";
	}
	/**
	 * 获得绑卡详情的url
	 * @return
	 */
	public static String getBindCardDetailUrl(long id){
		return SERVER_URL+"/sms/bindcard/order/"+id;
	}
	/**
	 * 获得换包的url
	 * @return
	 */
	public static String getChangePackageDetailUrl(long id){
		return SERVER_URL+"/sms/changepackage/order/"+id;
	}
	/**
	 * 获得充值的url
	 * @return
	 */
	public static String getChargeDetailUrl(long id){
		return SERVER_URL+"/sms/recharge/order/"+id;
	}
	
	public static int request_item_count = 6;
	
	public static String REMIND_TIME_POS = "remind_time_pos";
	public static String RINGTONE_POS = "ringtone_pos";
	public static int REMIND_TIME_ID = 0;
	public static int RINGTONE_ID = 1;
	
	public static int PAGE_ONAIR_NOW = 0;
	public static int PAGE_ONAIR_NEXT = 1;
	
	public static SimpleDateFormat format = new SimpleDateFormat("HH:mm");
	public static int WINDOW_WIDTH;
	public static int POSTER_HEIGHT;
	public static int MENU_ITEM_WIDTH;
	public static final int UPDATE_INTERVAL = 0;
	public static final String LAST_SYNC_TIME = "last_sync_time";
	public static final String DB_INFO = "db_info";
	
	/**根据版本清除数据*/
	public static final String GUIDE_BY_VERSION = "guide_verison";
	public static final String GUIDE_MENUBAR = "menubar_guide";
	public static final String GUIDE_ALLCHN = "allchn_guide";
	public static final String GUIDE_TELLFRIEND = "tellfriend_guide";
	public static final String GUIDE_CHATROOM = "chatroom_guide";
	public static final String GUIDE_FAVORITE_COLLECT = "favorite_collect";
	public static final String GUIDE_LEFT_RIGHT = "left_right";
	public static final String GUIDE_DOWN = "guide_down";
	
	/**根据用户清除数据*/
	public static final String GUIDE_BY_USER = "guide_user";
	public static final String GUIDE_ME = "me_guide";
	public static final String GUIDE_EPGALERT = "epgalert_guide";
	public static final String GUIDE_SMARTCARD = "smartcard_guide";
	
	/**
	 * epg wish from user
	 */
	public static final String GA_EVENT_BUSINESS = "BUSINESS";
	public static final String GA_EVENT_EPG_WISH = "EPG_WISH";
	public static final String GA_EVENT_REGISTER_PHONE = "REGISTER_BY_PHONE";//手机注册
	public static final String GA_EVENT_LOGIN_THIRD = "LOGIN_BY_THIRDPARTY";//第三方登陆
	public static final String GA_EVENT_RECHARGE = "RECHARGE";//充值
	public static final String GA_EVENT_CHANGE_PACKAGE = "CHANGE_PACKAGE";//换包
	public static final String GA_EVENT_CODE_RESET_PWD = "SENDCODE_RESET_PWD";//重置密码发送验证码
	public static final String GA_EVENT_CODE_REGISTER = "SENDCODE_RIGESTER";//注册发送验证码
	public static final String GA_EVENT_POSTER_CLICK = "RECOMMENDED_POSTER";//点击海报
	public static final String GA_EVENT_BIND_SMARTCAED = "BIND_SMARTCARD";//绑定智能卡
	public static final String GA_EVENT_GOOGLE_GO = "GOOGLEPLAY_CONFIRM";//确定去google
	public static final String GA_EVENT_GOOGLE_CANCLE = "GOOGLEPLAY_CANCLE";//取消去google
	
	public static final String SCHEAM = "tenbre://com.star.mobile.video";
	public static final String UID = "uid";
}
