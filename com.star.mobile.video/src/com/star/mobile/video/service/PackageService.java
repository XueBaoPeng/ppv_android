package com.star.mobile.video.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.star.cms.model.APPInfo;
import com.star.cms.model.Package;
import com.star.cms.model.enm.TVPlatForm;
import com.star.mobile.video.base.AbstractService;
import com.star.mobile.video.dao.db.DBHelper;
import com.star.mobile.video.dao.impl.PackageDAO;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.IOUtil;
import com.star.util.Log;
import com.star.util.json.JSONUtil;
import com.star.util.loader.LoadMode;
import com.star.util.loader.OnListResultListener;
import com.star.util.loader.OnResultListener;

public class PackageService extends AbstractService {
	private static final String TAG = "PackageService";
	private PackageDAO packageDao;
	private Context context;

	public PackageService(Context context){
		super(context);
		this.context = context;
		packageDao = new PackageDAO(DBHelper.getInstence(context));
	}
	
//	public List<Package> getPackagesFromServer(List<Integer> types){
//		try{
//			String json = IOUtil.httpGetToJSON(Constant.getPackageUrl(context,types));
//			if(json != null){
//				List<Package> packages = JSONUtil.getFromJSON(json, new TypeToken<ArrayList<Package>>() {
//				}.getType());
//				return packages;
//			}
//		}catch(Exception e){
//			Log.e(TAG, "get package list from server error!", e);
//		}
//		return new ArrayList<Package>();
//	}
	public void getPackagesFromServer(List<Integer> types,OnListResultListener<Package> listener){
		doGet(Constant.getPackageUrl(context,types), Package.class, LoadMode.CACHE_NET, listener);
	}
	public List<Package> getPackages(TVPlatForm platForm){
		try{
			List<Package> packages = packageDao.query(platForm);
			
			if(packages.size()==0){
				Log.e(TAG, "no categorys in local!");
				return null;
			}
			Log.d(TAG, "package list come from local!");
			return packages;
		}catch(Exception e){
			Log.e(TAG, "get package list from server!", e);
			return null;
		}
	}
	
	public Package getPackageById(long id){
		return packageDao.query(id);
	}
	
	public boolean  initPackages(){
		List<Integer> types = new ArrayList<Integer>();
		try{
			String url = Constant.getPackageUrl(context,types);
			if(SharedPreferencesUtil.getAreaId(context)!=0)
				url += ("&areaId="+SharedPreferencesUtil.getAreaId(context));
			url += "&platformTypes="+ TVPlatForm.DTH.getNum()+"&platformTypes="+TVPlatForm.DTT.getNum();
			Log.i(TAG, "getPackage url="+url);
			String json = IOUtil.httpGetToJSON(url);
			if(json != null){
				List<Package> packages = JSONUtil.getFromJSON(json, new TypeToken<ArrayList<Package>>() {
				}.getType());
				if(packages==null || packages.size()==0)
					return false;
				packageDao.clear();
				for(Package p : packages){
					packageDao.add(p);
				}
			}
			return true;
		}catch(Exception e){
			Log.e(TAG, "init package table error!", e);
			return false;
		}
	}
	
//	public int changePackage(String smartCardNo, String packageCode, String stbNumber){
//		try{
//			Map<String, Object> params = new HashMap<String, Object>();
//			params.put("smartCardNo", smartCardNo);
//			params.put("packageCode", packageCode);
//			params.put("stbNumber", stbNumber);
//			String json = IOUtil.httpPostToJSON(params, Constant.SERVER_URL+"/packages/change");
//			return Integer.parseInt(json);
//		}catch(Exception e){
//			Log.e(TAG, "get package list from server error!", e);
//		}
//		return -1;
//	}
//	public void changePackage(String smartCardNo, String packageCode, String stbNumber,OnResultListener<Integer> listener){
//		Map<String, Object> params = new HashMap<String, Object>();
//		params.put("smartCardNo", smartCardNo);
//		params.put("packageCode", packageCode);
//		params.put("stbNumber", stbNumber);
//		doPost( Constant.SERVER_URL+"/packages/change", Integer.class, params, listener);
//	}
	public void changePackage(String smartCardNo,String code, String packageCode, String stbNumber,int type,OnResultListener<Integer> listener){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("smartcardNo", smartCardNo);
		params.put("fromProductCode", code);
		params.put("toProductCode", packageCode);
		params.put("checkNumber", stbNumber);
		params.put("checkType", type);
		doPost( Constant.getChangeBouquetUrl(), Integer.class, params, listener);
	}
	public void clearData(){
		packageDao.clear();
	}
}
