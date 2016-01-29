package com.star.mobile.video.dialog;

import java.util.Date;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;

import com.star.cms.model.vo.TaskVO;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.me.mycoins.TaskService;
import com.star.mobile.video.service.UserService;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.ApplicationUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.DateFormat;
import com.star.mobile.video.util.ToastUtil;
import com.star.util.loader.OnResultListener;

public class GetCoinClickListener implements OnClickListener {

	private long taskId;
	private Float times;
	private int coins;
	private Activity context;
	private final int UPDATE_VIEW = 1000;
	private Handler handler;
	private SharedPreferences mSharePre;

	public GetCoinClickListener(Activity context, Handler handler){
		this.context = context;
		this.handler = handler;
		mSharePre = SharedPreferencesUtil.getTaskCoinSharedPreferences(context);
	}
	
	@Override
	public void onClick(View v) {
		TaskVO tv = (TaskVO)v.getTag();
		taskId = tv.getId();
		times = tv.getTimes();
		coins = tv.getCoins();
		CommonUtil.showProgressDialog(context, null, "Getting coin...");
		new TaskService(context).getGold(taskId, ApplicationUtil.getAppVerison(context), new OnResultListener<Boolean>() {
			
			@Override
			public void onSuccess(Boolean value) {
				CommonUtil.closeProgressDialog();
				long time = mSharePre.getLong("CoinTime", 0);
				if(time!=0){
					int day = DateFormat.getDiffDays(new Date(time), new Date());
					if(day!=0){
						mSharePre.edit().clear().commit();
						mSharePre.edit().putLong("CoinTime", new Date().getTime()).commit();
					}
				}else{
					mSharePre.edit().putLong("CoinTime", new Date().getTime()).commit();
				}
				int cacheCoin = mSharePre.getInt("coins", 0);
				final int userCurrentCoins = coins+cacheCoin;
				final ReceiveCoinDialog dialog = new ReceiveCoinDialog(context);
				dialog.setLeftButtonOnClick(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						dialog.dismiss();
					}
				});
				
				dialog.setRightButtonOnClick(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						dialog.dismiss();
					}
				});
				dialog.setAlertInfo(context.getString(R.string.redeem_reward));
				dialog.setCoin(coins);
				dialog.setOnDismissListener(new OnDismissListener() {
					
					@Override
					public void onDismiss(DialogInterface arg0) {
						Message msg = Message.obtain();
						msg.what = UPDATE_VIEW;
						handler.sendMessage(msg);
					}
				});
				dialog.show();
				
				if(StarApplication.mUser!=null){
					new UserService().updateCoins(context, coins);
				}
				mSharePre.edit().putInt("coins", userCurrentCoins).commit();
//					getCoinsStatus();
			}
			
			@Override
			public boolean onIntercept() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void onFailure(int errorCode, String msg) {
				CommonUtil.closeProgressDialog();
				ToastUtil.centerShowToast(context, context.getString(R.string.error_network));
			}
		});
	}
	
	/*public void getCoinsStatus() {
		new LoadingDataTask() {
			TaskService taskService;
			List<TaskVO> datas;
			@Override
			public void onPreExecute() {
				taskService = new TaskService();
			}
			
			@Override
			public void onPostExecute() {
				if(datas!=null && datas.size()>0){
					TaskSharedUtil.setCoinsStatus(context, false);
					for(TaskVO t :datas) {
						if(t.getCoins() != 0) {
							TaskSharedUtil.setCoinsStatus(context, true);
							break;
						} 
					}
				}
			}
			
			@Override
			public void doInBackground() {
				datas = taskService.getTasks(false,ApplicationUtil.getAppVerison(context));
			}
		}.execute();
	}*/
}
