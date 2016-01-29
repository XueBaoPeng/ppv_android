package com.star.mobile.video.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.star.cms.model.ProgramPPV;
import com.star.cms.model.enm.OrderType;
import com.star.cms.model.vo.ChannelVO;
import com.star.cms.model.vo.ProgramVO;
import com.star.mobile.video.AlertManager;
import com.star.mobile.video.base.AbstractService;
import com.star.mobile.video.base.BaseService;
import com.star.mobile.video.dao.db.DBHelper;
import com.star.mobile.video.dao.impl.ProgramDAO;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.DataCache;
import com.star.mobile.video.util.IOUtil;
import com.star.util.json.JSONUtil;
import com.star.util.loader.LoadMode;
import com.star.util.loader.OnResultListener;

public class ProgramService extends AbstractService {
	private String requestURL;
	private ProgramDAO programDao;
	private String TAG = "ProgramService";
	private DBHelper dbHelper;
	private SharedPreferences mSharePre;
	private Context context;
	private boolean isUseAsync = true;
	public ProgramService(Context context) {
		super(context);
		this.context = context;
		requestURL = Constant.getEpgUrl();
		programDao = new ProgramDAO(DBHelper.getInstence(context));
		dbHelper = DBHelper.getInstence(context);
	}
	
	public List<ProgramVO> getEpgs(String keys,int index,int count) {
		String url = (Constant.getSearchUrl()+"?keys="+keys+"&count="+count+"&index="+index).replace(" ", "%20");
		try{
			String json = /*isUseAsync ? IOUtilAsync.httpGetToJSON(url):*/IOUtil.httpGetToJSON(url);
			if(json != null) {
				return JSONUtil.getFromJSON(json, new TypeToken<ArrayList<ProgramVO>>() {}.getType());
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<ProgramVO>();
	}
	
	public List<ProgramVO> getOnairEpgs(long channelId){
		try{
			List<ProgramVO> onAirPros = programDao.query(channelId, System.currentTimeMillis(), -1, 0, 1);
			if(onAirPros.size()>0){
				List<ProgramVO> nextPros = programDao.query(channelId, onAirPros.get(0).getEndDate().getTime()+1000*60+100, -1, 0, 1);
				if(nextPros.size()>0)
					onAirPros.add(nextPros.get(0));
			}
			if(onAirPros.size()!=0){
				Log.d(TAG, "onair and next program from local! channelID="+channelId);
				return onAirPros;
			}
			Log.e(TAG, "this channel has no programs, or no programs in local!");
		}catch (Exception e) {
			Log.e(TAG, "get onair and next program from local error!", e);
		}
		String url = Constant.getAirEpgUrl()+"?channelID="+channelId;
		return getProgramFromServer(url);
	}
	
	public List<ProgramVO> getProgramsByDate(Date date){
		return programDao.query(date);
	}
	
	public ProgramVO getEpgDetailByIdFromCache(long programId){
		ProgramVO epg = DataCache.getInstance().getEPGFromCache(programId);
		if(epg!=null)
			return epg;
		try{
			String url = requestURL+"/"+programId+"?programID="+programId;
			String json = IOUtil.getCachedJSON(url);
			if(json != null)
				return JSONUtil.getFromJSON(json, new TypeToken<ProgramVO>() {}.getType());
		}catch(Exception e){
			e.printStackTrace();
		}
		return getEpgDetailByIdFromServer(programId);
	}
	
	public ProgramVO getEpgDetailByIdFromServer(long programId){
//		try{
//			ProgramVO program = programDao.query(programId);
//			if(program != null){
//				mSharePre = context.getSharedPreferences("epglist_channelId_"+program.getChannelId(), Context.MODE_PRIVATE);
//				String json = mSharePre.getString("programId_"+program.getId(), "");
//				ProgramVO p = JSONUtil.getFromJSON(json, new TypeToken<ProgramVO>() {}.getType());
//				if(p!=null){
//					program.setDescription(p.getDescription());
//					program.setContents(p.getContents());
//				}
//				Log.d(TAG , "program detail come from local!");
//				return program;
//			}
//			Log.e(TAG, "no program in local, id ="+programId);
//		}catch(Exception e){
//			Log.e(TAG, "get program detail from local error!", e);
//		}
		String url = requestURL+"/"+programId+"?programID="+programId;
		String json = null;
		try{
			json = /*isUseAsync ? IOUtilAsync.httpGetToJSON(url, true) : */IOUtil.httpGetToJSON(url, true);
			if(json != null){
				ProgramVO p = JSONUtil.getFromJSON(json, new TypeToken<ProgramVO>() {}.getType());
				if(p!=null)
					DataCache.getInstance().getOneLevelEPGCache().put(p.getId(), p);
				return p;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		try{
			json = IOUtil.getCachedJSON(url);
			if(json != null)
				return JSONUtil.getFromJSON(json, new TypeToken<ProgramVO>() {}.getType());
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	public ProgramPPV getEpgDetailByIdServer(long programId){
		String url = Constant.getProgramUrl()+programId+"/ppv";
		String json = null;
		try{
			json = IOUtil.httpGetToJSON(url, true);
			if(json != null){
				ProgramPPV p = JSONUtil.getFromJSON(json, new TypeToken<ProgramPPV>() {}.getType());
				if(p!=null)
					DataCache.getInstance().getOneLevelEPGCache().put(p.getId(), p);
				return p;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		try{
			json = IOUtil.getCachedJSON(url);
			if(json != null)
				return JSONUtil.getFromJSON(json, new TypeToken<ProgramPPV>() {}.getType());
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	public void getEpgDetailByIdFromServer(long programId, OnResultListener<ProgramPPV> listener){
		doGet(Constant.getProgramUrl()+programId+"/ppv", ProgramPPV.class, LoadMode.CACHE_NET, listener);
	}
	public void getEpgDetailByIdFromServer(int channelNumber,long startTime, OnResultListener<ProgramPPV> listener){
		doGet(Constant.getProgramdetailUrl()+"?channelNumber="+channelNumber+"&startDate="+startTime, ProgramPPV.class, LoadMode.CACHE_NET, listener);
	}
	public boolean updateFavStatus(ProgramVO program, boolean isSync){
		return programDao.updateFavStatus(program, isSync);
	}
	
	public boolean updateFavStatus(ProgramVO program){
		if(programDao.queryIsExist(program.getId())){
			return programDao.updateFavStatus(program, false);
		}else{
			boolean result =  programDao.addOutline(program);
			setOutLinePrograms();
			return result;
		}
	}
	
	public void setOutLinePrograms(){
		AlertManager.getInstance(context).alertOutlines = programDao.queryOutlines();
	}
	
	public boolean hasOutlineEPGs(long channelId){
		ProgramVO program =  getLastEpgFromLocal(channelId);
		if(program!=null){
			return true;
		}
		return false;
	}
	
	public ProgramVO getLastEpgFromLocal(long channelId){
		return programDao.queryLastProgram(channelId);
	}
	
	public long getLastEpgOnline(long channelId){
		try{
			String json = /*isUseAsync ? IOUtilAsync.httpGetToJSON(Constant.getLastEpgUrl()+"?channelID="+channelId): */IOUtil.httpGetToJSON(Constant.getLastEpgUrl()+"?channelID="+channelId);
			if(json != null){
				return JSONUtil.getFromJSON(json, Long.class);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return -1;
	}
	
	public boolean updateCommentCount(ProgramVO program){
		return programDao.updateCommentCount(program);
	}
	
	public List<ProgramVO> getFavEpgs(boolean isFav, long startDate, long endDate, int index, int count){
		return programDao.query(isFav, startDate, endDate, index, count);
	}
	
	public List<ProgramVO> getFavEpgs(boolean isFav, long minDate, long maxDate){
		return programDao.query(isFav, minDate, maxDate);
	}
	
	private List<ProgramVO> getProgramFromServer(String url) {
		Log.i(TAG, "url---"+url);
		try{
			String json = /*isUseAsync ? IOUtilAsync.httpGetToJSON(url) : */IOUtil.httpGetToJSON(url);
			if(json != null)
				return JSONUtil.getFromJSON(json, new TypeToken<ArrayList<ProgramVO>>() {}.getType());
		}catch(Exception e){
			Log.e("", "",e);
		}
		return new ArrayList<ProgramVO>();
	}
	private List<ProgramPPV> getProgramFromppvServer(String url,boolean isLoacl,int index) {
		Log.i(TAG, "url---"+url);
		try{
			String json=null;
			if(isLoacl) {
				json = IOUtil.getCachedJSON(url);
			} else {
				if(index == 0){
					json = IOUtil.httpGetToJSON(url,true);
				}else{
					json = IOUtil.httpGetToJSON(url);
				}
			}
			if(json != null)
				return JSONUtil.getFromJSON(json, new TypeToken<ArrayList<ProgramPPV>>() {}.getType());
		}catch(Exception e){
			Log.e("", "",e);
		}
		return new ArrayList<ProgramPPV>();
	}
	
	public List<ProgramVO> getEpgs(long channelId, long currentTime, int index, int count){
		List<ProgramVO> localEpgs = null;
		if(!SyncService.getInstance(context).isLoading()){
			try{
				localEpgs = programDao.query(channelId, -1, currentTime, index, count);
				if(localEpgs.size()==Constant.request_item_count){
					Log.d(TAG, "program list come from local! channelID="+channelId+", index="+index+",count="+count);
					return localEpgs;
				}
				Log.e(TAG, "from local, programs size is "+localEpgs.size());
			}catch (Exception e) {
				Log.e(TAG, "get program list from local error!", e);
			}
		}
		String url = Constant.getEpgListUrl()+"?channelID="+channelId+"&index="+index+"&count="+count;
		List<ProgramVO> programs = getProgramFromServer(url);
		if(localEpgs!=null && localEpgs.size()!=0){
			for(ProgramVO program : localEpgs){
				for(ProgramVO vo : programs){
					if(vo.getId().equals(program.getId())){
						vo.setIsFav(program.isIsFav());
					}
				}
			}
		}
		return programs;
	}
	public List<ProgramPPV> getEpgsppv(long channelId, long currentTime, int index, int count,boolean isLoacl){
		List<ProgramPPV> localEpgs = null;
		String url = Constant.getEpgListUrl()+"?channelID="+channelId+"&index="+index+"&count="+count;
		List<ProgramPPV> programs = getProgramFromppvServer(url,isLoacl,index);
		if(localEpgs!=null && localEpgs.size()!=0){
			for(ProgramPPV program : localEpgs){
				for(ProgramPPV vo : programs){
					if(vo.getId().equals(program.getId())){
						vo.setIsFav(program.isIsFav());
					}
				}
			}
		}
		return programs;
	}
	public List<ProgramVO> getEpgs(OrderType orderType, int index, int count){
		String url = requestURL+"?orderBy="+orderType.getNum()+"&index="+index+"&count="+count;
		String json = null;
		try{
			json = /*isUseAsync ? IOUtilAsync.httpGetToJSON(url, true) : */IOUtil.httpGetToJSON(url, true);
			if(json != null){
				ArrayList<ProgramVO> list = JSONUtil.getFromJSON(json, new TypeToken<ArrayList<ProgramVO>>() {}.getType());
				if(list!=null && list.size()>0){
					return list;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		try{
			json = IOUtil.getCachedJSON(url);
			if(json != null)
				return JSONUtil.getFromJSON(json, new TypeToken<ArrayList<ProgramVO>>() {}.getType());
		}catch(Exception e){
			e.printStackTrace();
		}
		return new ArrayList<ProgramVO>();
	}

	private int index = 0;
	private String getSnapshotProgramUrl(long channelID, long startDate, long endDate, int count){
		String url =  Constant.getSnapshotProgramUrl()+"?channelID="+channelID+"&startDate="+startDate+"&endDate="+endDate+"&index="+index+"&count="+count;
		return url;
	}
	
	public synchronized void initPrograms(final long channelID, final long startDate, final long endDate, final Handler handler) {
		mSharePre = context.getSharedPreferences("epglist_channelId_"+channelID, Context.MODE_PRIVATE);
		mSharePre.edit().clear();
		index = 0;
		final int count = 50;
		Runnable initEpgs = new Runnable() {
			@Override
			public void run() {
				dbHelper.beginTransaction();
				long start = System.currentTimeMillis();
				List<ProgramVO> programs = getProgramFromServer(getSnapshotProgramUrl(channelID,startDate,endDate,count));
				while(programs.size()>0){
					for (ProgramVO program : programs) {
						programDao.add(program);
//						mSharePre.edit().putString("programId_"+program.getId(), JSONUtil.getJSON(program)).commit();
					}
					if(programs.size()<count)
						break;
					index += count;
					programs = getProgramFromServer(getSnapshotProgramUrl(channelID, startDate, endDate, count));
				}
				long end = System.currentTimeMillis();
				Log.d(TAG, "init programs, spend "+(end-start)/1000+"seconds, channelId = "+channelID);
				dbHelper.commit();
				handler.sendEmptyMessage(0);
			}
		};
		new Thread(initEpgs).start();
	}
	
	public List<ProgramVO> getNeedSyncPrograms(){
		return programDao.query(true);
	}
	
	public void removeAllPrograms(){
		programDao.clear();
	}
	
	public void compareProgram(Context context, ProgramVO program) {
		if(program == null)
			return;
		boolean faved = false;
		for(ProgramVO mark : AlertManager.getInstance(context).alertOutlines){
			if(program.getId().equals(mark.getId())){
				faved = true;
				program.setIsFav(mark.isIsFav());
				if(mark.isIsFav()){
					program.setFavCount(mark.getFavCount());
				}
				break;
			}
		}
		if(!faved&&program.isIsFav()){
			program.setIsFav(false);
		}
	}
	
	
}
