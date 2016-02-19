package com.star.mobile.video.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;
import com.igexin.sdk.Tag;
import com.star.cms.model.enm.Sex;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.home.HomeActivity;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;

public class PushTenbreReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		switch (bundle.getInt(PushConsts.CMD_ACTION)) {

		case PushConsts.GET_MSG_DATA:
			// 获取透传数据
			// String appid = bundle.getString("appid");
			byte[] payload = bundle.getByteArray("payload");
			
			String taskid = bundle.getString("taskid");
			String messageid = bundle.getString("messageid");

			// smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
			boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
			System.out.println("第三方回执接口调用" + (result ? "成功" : "失败"));
			
			if (payload != null) {
				CommonUtil.startActivity(context, HomeActivity.class);

				
			}
			break;
		case PushConsts.GET_CLIENTID:
			List<String> tags=new ArrayList<String>();
			if(SharedPreferencesUtil.getUserName(context)!=null){
				tags.add(SharedPreferencesUtil.getUserName(context));
				tags.add("C_"+SharedPreferencesUtil.getAreaname(context));
				if(StarApplication.mUser!=null){
					tags.add("ID_"+StarApplication.mUser.getId());
					if (StarApplication.mUser.getSex() != null) {
						if (StarApplication.mUser.getSex().equals(Sex.MALE)) {
							tags.add("G_" + context.getString(R.string.sex_man));
						} else if (StarApplication.mUser.getSex().equals(Sex.WOMAN)) {
							tags.add("G_" + context.getString(R.string.sex_woman));
						} else {
							tags.add("G_" + context.getString(R.string.sex_defalut));
						}
					}else{
						tags.add("G_" + context.getString(R.string.sex_defalut));
					}
					if(StarApplication.mUser.getCoins()!=null){
						tags.add("Coin_"+(int)Math.floor(StarApplication.mUser.getCoins()/1000)+"k");
					}else {
						tags.add("Coin_0k");
					}
				}
			}else{
				tags.add(android.os.Build.MODEL);
				tags.add("C_"+SharedPreferencesUtil.getAreaname(context));

			}
			Tag[] tagParam = new Tag[tags.size()];
			for (int i = 0; i < tags.size(); i++) {
				Tag t = new Tag();
				t.setName(tags.get(i));
				tagParam[i] = t;
			}
			int i= PushManager.getInstance().setTag(context, tagParam);
			break;
		case PushConsts.THIRDPART_FEEDBACK:
			/*String appid = bundle.getString("appid");
			String taskid = bundle.getString("taskid");
			String actionid = bundle.getString("actionid");
			String result = bundle.getString("result");
			long timestamp = bundle.getLong("timestamp");

			Log.d("GetuiSdkDemo", "appid = " + appid);
			Log.d("GetuiSdkDemo", "taskid = " + taskid);
			Log.d("GetuiSdkDemo", "actionid = " + actionid);
			Log.d("GetuiSdkDemo", "result = " + result);
			Log.d("GetuiSdkDemo", "timestamp = " + timestamp);*/
			break;
		default:
			break;
		}
	}
}
