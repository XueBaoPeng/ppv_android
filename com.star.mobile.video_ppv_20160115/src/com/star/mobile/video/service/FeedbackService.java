package com.star.mobile.video.service;

import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.star.cms.model.Comment;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.activity.FeedbackAlertDialogActivity;
import com.star.mobile.video.base.AbstractService;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.ApplicationUtil;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.FourLayerServiceSharedUtil;
import com.star.util.loader.LoadMode;
import com.star.util.loader.OnListResultListener;

public class FeedbackService extends AbstractService {

	public static final String CAN_FOUR_LAYER= "canFourLayer";
	public static final String DO_FINISH_FOUR_LAYER="doFinishFourLayer";
	
	private Context context;
	private static FeedbackService service;
	
	private FeedbackService(Context context) {
		super(context);
		this.context = context;
	}
	
	public static FeedbackService getInstance(Context context){
		if(service == null){
			service = new FeedbackService(context);
		}
		return service;
	}
	
	public Handler handler = new Handler(){
			@Override
			public void dispatchMessage(Message msg) {
				super.dispatchMessage(msg);
				if(msg.what == 112) {
					if(!SharedPreferencesUtil.getReportDone(context, SharedPreferencesUtil.getUserName(context), ApplicationUtil.getAppVerison(context))) {
						if(isDoFourLayer()) {
							sendBroadcast();
							if(!ApplicationUtil.isApplicationInBackground(context)) {
								Intent intent = new Intent(context,FeedbackAlertDialogActivity.class);
								intent.putExtra("isShowTitle", true);
								intent.putExtra("left_btn_text", "Feedback");
								intent.putExtra("right_btn_text", "Dismiss");
								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								context.startActivity(intent);
							}
						}
					}
				}
			}
	};
	
	private void sendBroadcast() {
		context.sendBroadcast(new Intent(CAN_FOUR_LAYER));
	}
	
	public boolean isDoFourLayer() {
//		String userName = null;
//		String name = SharedPreferencesUtil.getUserName(context);
//		String deviceId = SharedPreferencesUtil.getDiciveId(context);
//		if( name != null) {
//			userName = name;
//		} else {
//			userName = deviceId;
//		}
		if(StarApplication.CURRENT_VERSION == Constant.FINAL_VERSION) {
			return false;
		} else {
			if(SharedPreferencesUtil.isQuestionActive(context) 
					&& !SharedPreferencesUtil.getReportDone(context, SharedPreferencesUtil.getUserName(context), ApplicationUtil.getAppVerison(context))) {
				return true;
			} else {
				return false;
			}
		}
	}
	public void getAppComments(OnListResultListener<Comment> listener){
		doGet(Constant.getFeedbackUrl(), Comment.class, LoadMode.CACHE_NET, listener);
	}
	/**
	 * 判断第一次提醒时间是否已过
	 * @return true 时间已到  false 时间还没有到
	 */
	public boolean isOneRemind() {
		if(FourLayerServiceSharedUtil.getOneRemindTime(context) < new Date().getTime()) {
			return true;
		} else {
			return false;
		}
	}
	
}
