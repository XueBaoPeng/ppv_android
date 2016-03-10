package com.star.mobile.video.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.star.cms.model.BindCardCommand;
import com.star.cms.model.ChatRoom;
import com.star.cms.model.RechargeCMD;
import com.star.cms.model.dto.EggBreakResult;
import com.star.cms.model.dto.SyncAppStatus;
import com.star.cms.model.vo.ChangePackageCMDVO;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.activity.ChatActivity;
import com.star.mobile.video.base.BaseService;
import com.star.mobile.video.chatroom.ChatService;
import com.star.mobile.video.me.coupon.MyCouponsActivity;
import com.star.mobile.video.model.MyTimer;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.smartcard.SmartCardAsyncResult;
import com.star.mobile.video.util.ApplicationUtil;
import com.star.mobile.video.util.NotificationUtil;
import com.star.mobile.video.util.PostTimer;
import com.star.util.Logger;
import com.star.util.loader.LoadMode;
import com.star.util.loader.OnListResultListener;
import com.star.util.loader.OnResultListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.UUID;

public class SyncStatusService extends BaseService {
	
	private MyTimer syncTimer;
	private ChatService chatService;
	private NotificationManager notifManager;
	private Map<Long, Integer> msgCountSet = new HashMap<Long, Integer>(); 
	private final long periodtime_chatroom = 5000;
	private final long periodtime_syncstatus = 30000;
	
	private PostTimer chatroomHander = new PostTimer(){

		@Override
		public void execute() {
			moniterChatRoomsNewMsgCount();
		}
	};

	private PostTimer syncHanler = new PostTimer() {
		@Override
		public void execute() {
			sycnStatus();
		}
	};


	private final String TAG = SyncStatusService.class.getName();
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		chatService = new ChatService(this);
		notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		chatroomHander.startDelayed(0);
		syncHanler.startDelayed(0);
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		if(intent!=null) {
			rmNotification(intent.getLongExtra("roomId", -1));
		}
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, Service.START_REDELIVER_INTENT, startId);
	}
	@Override
	public void onDestroy() {
		rmNotification(-1);

		if (chatroomHander != null) {
			chatroomHander.stop();
		}
		if (syncHanler != null) {
			syncHanler.stop();
		}
		super.onDestroy();
	}
	

	private void moniterChatRoomsNewMsgCount(){
    	chatService.getChatRooms(StarApplication.CURRENT_VERSION,LoadMode.NET, new OnListResultListener<ChatRoom>() {
			
			@Override
			public boolean onIntercept() {
				if(!ApplicationUtil.isApplicationInBackground(SyncStatusService.this)){
					chatroomHander.startDelayed(periodtime_chatroom);
					return true;
				}
				return false;
			}
			
			@Override
			public void onFailure(int errorCode, String msg) {
				chatroomHander.startDelayed(periodtime_chatroom);
			}
			
			@Override
			public void onSuccess(List<ChatRoom> chatrooms) {
				Logger.d("---------------refresh newMsgCount----------------");
				List<Long> ids = SharedPreferencesUtil.getSettingChatRoomIds(SyncStatusService.this);
				if(chatrooms!=null&&ids!=null){
					for(ChatRoom room : chatrooms){
						if(ids.contains(room.getId())) {
							msgCountSet.remove(room.getId());
							continue;
						}
						if(room.getHotChatRate()==null)
							continue;
						if(room.getNewMsgCount()>0 && !msgCountSet.containsKey(room.getId())){
//							Integer count = msgCountSet.get(room.getId());
							msgCountSet.put(room.getId(), /*count==null?room.getNewMsgCount():count+room.getNewMsgCount()*/1);
				    		notifyChatRoomsMsgCount(room);
						}
					}
				}
				chatroomHander.startDelayed(periodtime_chatroom);
			}
		});
    }
	
	public void rmNotification(long roomId){
		if(roomId!=-1){
			msgCountSet.remove(roomId);
		}else{
			msgCountSet.clear();
		}
		try{
			notifManager.cancel(120);
		}catch(Exception e){
			
		}
    }
	
    public void notifyChatRoomsMsgCount(ChatRoom room){
    	Notification notif = new Notification();  
		notif.icon = R.drawable.app_icon;  
		notif.tickerText = "StarTimes";
		notif.flags |= Notification.FLAG_AUTO_CANCEL;  
//		Integer count = msgCountSet.get(room.getId());
		Intent intent = new Intent(this, ChatActivity.class);  
		intent.putExtra("channelId", String.valueOf(room.getCashId()));
		PendingIntent pIntent = PendingIntent.getActivity(this, UUID.randomUUID().hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);  
		if(SharedPreferencesUtil.getSoundChatAlertAll(this)) {
			notif.defaults |= Notification.DEFAULT_SOUND;  
		}
		if(SharedPreferencesUtil.getVibrationChatAlertAll(this)) {
			long[] vibrate = {100,200,100}; 
//			notif.defaults |= Notification.DEFAULT_VIBRATE; 
			notif.vibrate = vibrate;
		}
		if(msgCountSet.size()<=1){
			notif.setLatestEventInfo(this, "StarTimes", String.format(this.getString(R.string.chatRoom_newMessage), room.getName()), pIntent);  
		}else{
			notif.setLatestEventInfo(this, "StarTimes", String.format(this.getString(R.string.chatRoom_newMessage_s)), pIntent);  
		}
		Intent i = new Intent(clearAction);
		notif.deleteIntent = PendingIntent.getBroadcast(this, 0, i, 0);
		this.registerReceiver(receiver, new IntentFilter(clearAction));
		notifManager.notify(120, notif);  
    }
    
    private String clearAction = "com.star.mobile.video.notifClear";
    private BroadcastReceiver receiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(clearAction)){
				Log.d("AlertManager", "Clear chatroom's notification.");
				msgCountSet.clear();
				try{
					context.unregisterReceiver(receiver);
				}catch (Exception e) {
				}
			}
		}
    };
    
    private void sycnStatus(){
    	SyncService.getInstance(this).syncStatus(new OnResultListener<SyncAppStatus>() {
			
			@Override
			public void onSuccess(SyncAppStatus value) {
				Logger.d("SyncAppStatus--->"+value);
				if(value!= null){
					EggBreakResult breakResult = value.getEggBreakResult();
					if(breakResult!=null && breakResult.getBreakResult()==EggBreakResult.BreakReceive_Success){
						String message = null;
						String title = null;
						String tickerText = null;
						title= getString(R.string.alert_info_title);
						message = getString(R.string.alert_info_);
						Intent intent = new Intent(SyncStatusService.this, MyCouponsActivity.class);
//						intent.putExtra("fragment", HomeFragment.class.getSimpleName());
						intent.putExtra("exchange", breakResult.getExchange());
				        intent.addFlags(Intent.FILL_IN_DATA);
				        NotificationUtil.showNotification(message, title, tickerText, intent, SyncStatusService.this);
						Logger.d("break egg status:"+breakResult.getStatus()+" and get exchange:"+breakResult.getExchange());
					}
					List<ChangePackageCMDVO> changePackageCMDVOs = value.getChangePackages();
					for(ChangePackageCMDVO packageCMDVO: changePackageCMDVOs){
						SmartCardAsyncResult.getInstance().changePkgResult(getApplicationContext(), packageCMDVO,notifManager);
					}
					List<BindCardCommand> bindCommands = value.getBindCardCommand();
					for(BindCardCommand command: bindCommands){
						SmartCardAsyncResult.getInstance().bindCardResult(getApplicationContext(), command,notifManager);
						Logger.d("BindCardCommand: (smartcardno:"+command.getSmartCardNo()+", AcceptStatus:"+command.getAcceptStatus()+")");
					}
					
					List<RechargeCMD> recharges = value.getRecharges();
					for(RechargeCMD rc :recharges) {
						SmartCardAsyncResult.getInstance().rechargeResult(getApplicationContext(), rc,notifManager,rc.getRechargeType());
						Logger.d("Recharge: (smartcardno:"+rc.getSmartCardNo()+", AcceptStatus:"+rc.getAcceptStatus()+")");
					}
				}
				syncHanler.startDelayed(periodtime_syncstatus);
			}
			
			@Override
			public boolean onIntercept() {
				return false;
			}
			
			@Override
			public void onFailure(int errorCode, String msg) {
				Log.v("TAG", ">>>>>>>>>>>>>?");
				syncHanler.startDelayed(periodtime_syncstatus);
			}
		});
    }
	
}
