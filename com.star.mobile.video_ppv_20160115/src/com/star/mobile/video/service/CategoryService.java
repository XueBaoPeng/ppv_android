package com.star.mobile.video.service;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.star.cms.model.APPInfo;
import com.star.cms.model.Category;
import com.star.mobile.video.base.AbstractService;
import com.star.mobile.video.dao.db.DBHelper;
import com.star.mobile.video.dao.impl.CategoryDAO;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.IOUtil;
import com.star.util.Log;
import com.star.util.json.JSONUtil;
import com.star.util.loader.LoadMode;
import com.star.util.loader.OnListResultListener;
import com.star.util.loader.OnResultListener;

public class CategoryService extends AbstractService {

	private final String TAG = "CategoryService";
	private CategoryDAO categoryDao;
	public CategoryService(Context context) {
		super(context);
		categoryDao = new CategoryDAO(DBHelper.getInstence(context));
	}
	
//	public List<Category> getCategorysFromServer() {
//		try{
//			String json = IOUtil.httpGetToJSON(Constant.getCategoryUrl());
//			if(json != null){
//				List<Category> categorys = JSONUtil.getFromJSON(json, new TypeToken<ArrayList<Category>>() {
//				}.getType());
//				return categorys;
//			}
//		}catch(Exception e){
//			Log.e(TAG, "get category list from server error!", e);
//		}
//		return new ArrayList<Category>();
//	}
	public void getCategorysFromServer(OnListResultListener<Category> listener){
		doGet(Constant.getCategoryUrl(), Category.class, LoadMode.NET, listener);
	}
	public List<Category> getCategorys() {
		try{
			List<Category> categorys = categoryDao.query();
			if(categorys.size()==0){
				Log.e(TAG, "no categorys in local!");
				return null;
			}
			Log.d(TAG, "category list come from local!");
			return categorys;
		}catch(Exception e){
			Log.e(TAG, "get category list from local error!", e);
			return null;
		}
	}
	
	public boolean initCategorys() {
		try{
			String json = IOUtil.httpGetToJSON(Constant.getCategoryUrl());
			if(json != null){
				List<Category> categorys = JSONUtil.getFromJSON(json, new TypeToken<ArrayList<Category>>() {
				}.getType());
				if(categorys==null || categorys.size()==0)
					return false;
				categoryDao.clear();
				for(Category category : categorys){
					categoryDao.add(category);
				}
			}
			return true;
		}catch(Exception e){
			Log.e(TAG, "init category table error!", e);
			return false;
		}
	}
//	public void initCategorys(OnResultListener<Boolean> listener){
//		doGet(Constant.getCategoryUrl(), Boolean.class, LoadMode.NET, listener);
//	}
	public void clearData(){
		categoryDao.clear();
	}
}
