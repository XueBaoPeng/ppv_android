package com.star.mobile.video;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.UUID;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import com.star.cms.model.vo.ProgramVO;
import com.star.cms.model.vo.SmartCardInfoVO;
import com.star.mobile.video.activity.AlertDialogActivity;
import com.star.mobile.video.activity.AlertListActicity;
import com.star.mobile.video.model.MyTimer;
import com.star.mobile.video.service.ProgramService;
import com.star.mobile.video.service.UserService;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.smartcard.SmartCardControlActivity;
import com.star.mobile.video.smartcard.SmartCardService;
import com.star.mobile.video.util.ApplicationUtil;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.DateFormat;
import com.star.mobile.video.util.LoadingDataTask;
import com.star.util.loader.OnResultListener;

public class AlertManager {

	private static AlertManager manager;
	private Handler mDefaultHandler;
	private ProgramService epgService;
	private SmartCardService smartCardService;
	private long alertime;
	public List<ProgramVO> alertEpgs = new ArrayList<ProgramVO>();
	public List<ProgramVO> alertOutlines = new ArrayList<ProgramVO>();
	private List<String> alertTag = new ArrayList<String>();
	private ProgramVO program;
	private Context context;
	private Map<String, MyTimer> timers = new HashMap<String, MyTimer>();
	private UserService userService;
	private boolean willStop;
	private String[] times;
	private int currentPos;
	
	private AlertManager(Context context) {
		mDefaultHandler = new Handler();
		this.context = context;
		notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);  
		epgService = new ProgramService(context);
		userService = new UserService();
		smartCardService = new SmartCardService(context);
		SharedPreferences mSharePre = SharedPreferencesUtil.getAlertSharePreferences(context);
		currentPos = mSharePre.getInt(Constant.REMIND_TIME_POS, 0);
		times = context.getResources().getStringArray(R.array.Alert_Remind_min);
		alertime = Long.parseLong(times[currentPos])*60*1000;
		setAlertTime(currentPos);
	}

	public static AlertManager getInstance(Context context) {
		if(manager == null)
			manager = new AlertManager(context);
		return manager;
	}
	
	private Runnable mAlertTimerTask = new Runnable() {
        @Override
        public void run() {
        	if(program == null)
        		return;
        	List<ProgramVO> epgs = epgService.getProgramsByDate(program.getStartDate());
        	alert(epgs);
            startAlertTimer();
        }
    };
    
    public void setAlertTime(int pos){
    	long setTime = Long.parseLong(times[pos])*60*1000;
    	if(setTime>alertime){
    		startAlertTimer_(setTime, pos);
    	}else{
    		alertime = setTime;
    		currentPos = pos;
    		startAlertTimer();
    	}
    }
    
    public void notifyAlertWake(long time) {
    	mDefaultHandler.removeCallbacks(mAlertTimerTask);
    	mDefaultHandler.postDelayed(mAlertTimerTask, time);
    }
    
    public void startAlertTimer_(final long setTime, final int pos){
    	new LoadingDataTask() {
    		List<ProgramVO> pvs;
    		@Override
    		public void onPreExecute() {
    		}
    		@Override
    		public void onPostExecute() {
    			alertime = setTime;
    			currentPos = pos;
    			if(pvs!=null && pvs.size()>0){
    				alert(pvs);
    			}
    	    	startAlertTimer();
    		}
			
    		@Override
    		public void doInBackground() {
    			pvs = epgService.getFavEpgs(true, System.currentTimeMillis()+alertime, System.currentTimeMillis()+setTime);
    		}
    	}.execute();
    }
    
    private void alert(List<ProgramVO> pvs) {
		for(int i=pvs.size()-1; i>=0; i--){
			if(alertTag.contains("program_"+pvs.get(i).getId()))
    			continue;
			if(ApplicationUtil.isApplicationInBackground(context)){
				notifyAlertProgram();
			}else{
		        Intent intent = new Intent(context, AlertDialogActivity.class);
		        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		        intent.putExtra("program", pvs.get(i));
		        intent.putExtra("pos", currentPos);
		        context.startActivity(intent);
			}
			alertTag.add("program_"+pvs.get(i).getId());
		}
	}
    
    public void startAlertTimer(){
    	new LoadingDataTask() {
    		@Override
    		public void onPreExecute() {
    		}
    		@Override
    		public void onPostExecute() {
    			for(int i=0;i<alertEpgs.size();i++){
    				program = alertEpgs.get(i);
    				long time = program.getStartDate().getTime()-System.currentTimeMillis()-alertime;
    				if(time>0){
    					Log.i("AlertManager", "the latest program is "+program.getName()+", startime is "+ DateFormat.format(program.getStartDate()));
    					notifyAlertWake(time);
    					break;
    				}
    			}
    		}
    		@Override
    		public void doInBackground() {
    			alertEpgs = epgService.getFavEpgs(true, -1, System.currentTimeMillis(), -1, -1);
    		}
    	}.execute();
    }
    
    private Handler handler = new Handler(){
    	public void handleMessage(android.os.Message msg) {
    		switch (msg.what) {
			case 1:
				userService.verfyExpectedStopSmartcard(context);
				break;
			default:
				break;
			}
    	};
    };
    
    public void notifyAlertProgram(){
    	Intent intent = new Intent(context, AlertListActicity.class);  
    	PendingIntent pIntent = PendingIntent.getActivity(context, UUID.randomUUID().hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);  
    	Notification notif = new Notification();  
		notif.icon = R.drawable.app_icon;  
		notif.tickerText = "A reminding message";  
		notif.defaults |= Notification.DEFAULT_SOUND;  
		notif.defaults |= Notification.DEFAULT_VIBRATE;  
		notif.defaults |= Notification.DEFAULT_LIGHTS;  
		notif.flags |= Notification.FLAG_AUTO_CANCEL;  
		notif.flags |= Notification.FLAG_NO_CLEAR;  
		notif.setLatestEventInfo(context, "Program:"+program.getName()+" coming soon.", "Start Time:"+DateFormat.format(program.getStartDate()), pIntent);  
		notifManager.notify(110, notif);  
    }
    
    public void notifyAlertProgram(String message,String title,String tickerText,Class<?> cla){
    	Intent intent = new Intent(context, cla);  
    	PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);  
    	Notification notif = new Notification();  
		notif.icon = R.drawable.app_icon;  
		notif.tickerText = tickerText;  
		notif.defaults |= Notification.DEFAULT_SOUND;  
		notif.defaults |= Notification.DEFAULT_VIBRATE;  
		notif.defaults |= Notification.DEFAULT_LIGHTS;  
		notif.flags |= Notification.FLAG_AUTO_CANCEL;  
		notif.setLatestEventInfo(context,title, message, pIntent);  
		notifManager.notify(110, notif);  
    }
    
	private NotificationManager notifManager;
    
    public void alertExpectedStopSmartcard(List<SmartCardInfoVO> scvs, final boolean verfy){
    	for(MyTimer mytimer : timers.values()){
    		if(!mytimer.cancel){
    			mytimer.cancel();
    		}
    	}
    	timers.clear();
    	if(scvs.size()>0){
    		willStop = true;
    	}else{
    		willStop = false;
    	}
//    	if(StarApplication.mHomeActivity!=null)
//			StarApplication.mHomeActivity.closeStatus();
    	for(final SmartCardInfoVO vo : scvs){
    		if(vo.getStopDays()==null || !(vo.getStopDays()==1||vo.getStopDays()==3||vo.getStopDays()==7))
    			continue;
    		if(SharedPreferencesUtil.getLastAccessTime(context, vo.getSmardCardNo(), vo.getStopDays()))
    			continue;
    		MyTimer timer = new MyTimer();
			timer.innerTask = new TimerTask() {
				@Override
				public void run() {
					if(verfy){
						handler.sendEmptyMessage(1);
					}else{
						notifySmartCardStop(vo);
						SharedPreferencesUtil.saveLastAccessTime(context, vo.getSmardCardNo(), vo.getStopDays());
					}
				}
			};
			Date when = DateFormat.getDateOfBeforeDays(DateFormat.stringToDate(vo.getPenaltyStop(), "yyyyMMdd",null),vo.getStopDays(),10,0,DateFormat.getTimeZone(SharedPreferencesUtil.getAreaCode(context)));
			timer.schedule(timer.innerTask, when);
			Log.d("AlertManager", "smartcart:"+vo.getSmardCardNo()+", will be notified at "+when);
			timers.put(vo.getSmardCardNo(), timer);
    	}
    }
    
    public void notifySmartCardStop(SmartCardInfoVO vo){
    	Notification notif = new Notification();  
		notif.icon = R.drawable.app_icon;  
		notif.tickerText = "StarTimes";
		notif.flags |= Notification.FLAG_AUTO_CANCEL;  
		Intent intent = new Intent(context, SmartCardControlActivity.class);
		intent.putExtra("fragment", context.getString(R.string.fragment_tag_ccount_manager));
		PendingIntent pIntent = PendingIntent.getActivity(context, UUID.randomUUID().hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);  
		notif.setLatestEventInfo(context, "StarTimes", String.format(context.getString(R.string.punish_stop), vo.getSmardCardNo(), vo.getPenaltyStop(), vo.getStopDays()+""), pIntent);  
		notifManager.notify(vo.getStopDays(), notif);  
    }
    
    public boolean isSmartCardCloseToStop(){
    	return willStop;
    }
}
