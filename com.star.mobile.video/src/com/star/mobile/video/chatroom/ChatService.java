package com.star.mobile.video.chatroom;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap.CompressFormat;
import android.os.Handler;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.star.cms.model.Chart;
import com.star.cms.model.ChatRoom;
import com.star.cms.model.ChatroomProperty;
import com.star.cms.model.dto.ChatRoomReturn;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.base.AbstractService;
import com.star.mobile.video.dao.IChatDAO;
import com.star.mobile.video.dao.db.DBHelper;
import com.star.mobile.video.dao.impl.ChatDAO;
import com.star.mobile.video.model.ChatVO;
import com.star.mobile.video.model.ImageBean;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.ApplicationUtil;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.IOUtil;
import com.star.mobile.video.util.ImageUtil;
import com.star.util.json.JSONUtil;
import com.star.util.loader.BitmapUploadParams;
import com.star.util.loader.LoadMode;
import com.star.util.loader.OnListResultListener;
import com.star.util.loader.OnResultListener;

public class ChatService extends AbstractService {
	
	
	private final String TAG = ChatService.class.getName();
	private IChatDAO chatDAO;
	private Handler mHandler;

	public ChatService(Context context) {
		super(context);
		this.chatDAO = new ChatDAO(DBHelper.getInstence(context));
		mHandler = new Handler();
	}
	
	public void sendImage(long channelID, List<ImageBean> selectImages, OnListResultListener<Chart> listener) {
		List<BitmapUploadParams> params = new ArrayList<BitmapUploadParams>();
		try {
			for (ImageBean bean : selectImages) {
				String extendFileName = bean.getPath().substring(bean.getPath().lastIndexOf(".") + 1,bean.getPath().length());
				BitmapUploadParams param = new BitmapUploadParams();
				param.bitmap = ImageUtil.loadImage(bean.getPath());
				if("png".equals(extendFileName) || "PNG".equals(extendFileName)){
					param.format = CompressFormat.PNG;
				}else{
					param.format = CompressFormat.JPEG;
				}
				String deviceName = android.os.Build.MODEL;
					deviceName = URLEncoder.encode(deviceName, "UTF-8");
				param.url = Constant.getChatSendImageUrl(channelID, deviceName, extendFileName);
				params.add(param);
			}
			doPostImage(Chart.class, params, listener);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public void sendText(long channelID, String msg, int type, OnResultListener<Chart> listener) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("channelID", channelID);
		params.put("msg", msg);
		params.put("type", type);
		params.put("deviceName", android.os.Build.MODEL);
		doPost(Constant.getChatSendTextUrl(channelID), Chart.class, params, listener);
	}
	
	public void getChats(Long channelID, Long nowTime, OnResultListener<ChatRoomReturn> listener) {
		String url = Constant.getChatsUrl(channelID) + "?index=" + 0 + "&count=" + Integer.MAX_VALUE;
		if(nowTime != null){
			url += "&timestamp=" + nowTime;
		}
		doGet(url, ChatRoomReturn.class, LoadMode.NET, listener);
	}
	
	public boolean addChatToDB(ChatVO chart, long channelID){
		return chatDAO.add(chart, channelID);
	}

	public List<ChatVO> getHistoryChats(Long channelID, int index, int count) {
		List<ChatVO> cs = chatDAO.query(channelID, index, count);
		if(cs.size()>=count)
			return cs;
		String url = Constant.getHistoryChatsUrl(channelID);
		if (index != -1 && count != -1) {
			url += "?index=" + index + "&count=" + count;
		}
		if(cs.size()>0){
			url += "&timestamp="+(cs.get(0).getCreateDate().getTime()+10);
		}
		int i = cs.size();
		try {
			String json = IOUtil.httpGetToJSON(url);
			if (json != null)
				cs = parserChartToChatVO((List<Chart>)JSONUtil.getFromJSON(json, new TypeToken<ArrayList<Chart>>() {}.getType()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(cs==null)
			return cs;
		for (; i < cs.size(); i++) {
			try {
				chatDAO.add(cs.get(i), channelID);
			} catch (Exception e) {
				Log.w(TAG, "Insert new chat error.", e);
			}
		}
		
		return cs;
	}
	
	public List<ChatVO> parserChartToChatVO(List<Chart> chats){
		if(chats==null)
			return null;
		List<ChatVO> cs = new ArrayList<ChatVO>();
		for(Chart c : chats){
			cs.add(new ChatVO(c, ChatVO.STATUS_VALID));
		}
		return cs;
	}
	
//	public void insertToDB(final List<ChatVO> cs, final long channelId){
//		new Thread(){
//			public void run() {
//				for (ChatVO chart : cs) {
//					addChatToDB(chart, channelId);
//				}
//			};
//		}.start();
//	}
	
    public void getChatRooms(int version,LoadMode mode, OnListResultListener<ChatRoom> listener) {
		doGet(Constant.getChatRoomsUrl(version), ChatRoom.class, mode, listener);
	}

	public void getChatroomByCashId(long cashId, OnResultListener<ChatRoom> listener) {
		doGet(Constant.getChatRoomDetailUrl(cashId), ChatRoom.class, LoadMode.NET, listener);
	}
	
	public void getChatroomPrompt(Context context,long cashId,OnResultListener<String> listener) {
		doGet(Constant.getChatRoomPromptUrl(cashId)+"?versionCode="+ApplicationUtil.getAppVerison(context), String.class, LoadMode.NET, listener);
	}
	
	public int getNewChatCount(ChatRoom room) {
//		Integer count = room.getHotChatRate();
//		int localCount = SharedPreferencesUtil.getChatRoomMsgCount(context, room.getId());
//		if(count!=null && count>0){
//			if(localCount==0 && room.getNewMsgCount()>0){
//				SharedPreferencesUtil.saveChatRoomMsgCount(context, room.getId(), count-room.getNewMsgCount());
//				if(!StarApplication.mChannelIdOfChatRoom.contains(room.getId()))
//					StarApplication.mChannelIdOfChatRoom.add(room.getId());
//				return room.getNewMsgCount();
//			}else if(localCount>0 && count-localCount>0){
//				if(!StarApplication.mChannelIdOfChatRoom.contains(room.getId()))
//					StarApplication.mChannelIdOfChatRoom.add(room.getId());
//				return count-localCount;
//			}else if(localCount==0 && room.getNewMsgCount()==0){
//				if(StarApplication.mChannelIdOfChatRoom.contains(room.getId()))
//					StarApplication.mChannelIdOfChatRoom.remove(room.getId());
//			}
//		}
//		return 0;
		
		if(room.getNewMsgCount()>0&&!StarApplication.mChannelIdOfChatRoom.contains(room.getId())){
			StarApplication.mChannelIdOfChatRoom.add(room.getId());
		}
		return room.getNewMsgCount();
	}
	
	/**
	 * 设置单个聊天室提醒
	 */
	public void setOneChatRoomAlert(long cashId,boolean status, OnResultListener<Boolean> listener) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cashId", cashId);
		params.put("status", status);
		doPost(Constant.getOneChatRoomAlertUrl(cashId), Boolean.class, params, listener);
	}
	
	/**
	 * 设置所有聊天室提醒
	 * @param sound 声音
	 * @param vibrate 震动
	 */
	public void setAllChatRoomAlert(boolean sound,boolean vibrate, OnResultListener<Boolean> listener) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("sound", sound);
		params.put("vibrate", vibrate);
		doPost(Constant.getAllChatRoomAlertSettingUrl(), Boolean.class, params, listener);
	}
	
	public void saveChatRoomSetting() {
		doGet(Constant.getAllCharRoomAlertUrl(), ChatroomProperty.class, LoadMode.NET, new OnResultListener<ChatroomProperty>() {

			@Override
			public boolean onIntercept() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onSuccess(ChatroomProperty value) {
				if(value != null) {
					SharedPreferencesUtil.setSoundChatAlertAll(context,value.isSoundable());
					SharedPreferencesUtil.setVibrationChatAlertAll(context,value.isVibrateable());
					SharedPreferencesUtil.setSettingChatRoomIds(context,value.getUnAlertChatRIds());
				}
			}

			@Override
			public void onFailure(int errorCode, String msg) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	/*public void startTask(long time){
		stop = false;
		mHandler.postDelayed(mChatTimerTask, time);
	}

	private boolean stop;
	public void removeTask(){
		stop = true;
		mHandler.removeCallbacks(mChatTimerTask);
	}
	
	public void executeTask(long time){
		mHandler.removeCallbacks(mChatTimerTask);
		mHandler.postDelayed(mChatTimerTask, time);
	}
	
	private Runnable mChatTimerTask = new Runnable() {
        @Override
        public void run() {
        	getDateAndFreshUI();
        }
    };
    
    private synchronized void getDateAndFreshUI() {
		new LoadingDataTask() {
			private List<ChatRoom> cs;
			@Override
			public void onPreExecute() {
			}
			
			@Override
			public void onPostExecute() {
				if(cs != null && cs.size()>0){
					boolean hasNew = false;
					for (ChatRoom vo : cs) {
						if(getNewChatCount(vo)>0){
							hasNew = true;
							break;
						}
					}
//					StarApplication.mHomeActivity.showRedDot(hasNew);
				}
				if(callback!=null)
					callback.callback(cs);
				if(!stop)
					executeTask(3000);
 			}

			@Override
			public void doInBackground() {
				cs = getChatRooms(false);
			}
		}.execute();
	}
    
    public interface ChatReminderCallBack{
    	public abstract void callback(List<ChatRoom> channels);
    }
    
    private ChatReminderCallBack callback;
    public void setChatReminderCallBack(ChatReminderCallBack callback){
    	this.callback = callback;
    }*/
    
}
