package com.star.mobile.video.dao.impl;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.star.cms.model.Category;
import com.star.cms.model.Content;
import com.star.cms.model.Resource;
import com.star.mobile.video.dao.ICategoryDAO;
import com.star.mobile.video.dao.db.DBHelper;

public class CategoryDAO implements ICategoryDAO {

	private final String TAG = this.getClass().getSimpleName();
	private SQLiteDatabase db;
	
	public CategoryDAO(DBHelper dbHelper) {
		this.db = dbHelper.getWritableDatabase();
	}
	
	@Override
	public void add(Category category) {
		ContentValues cv = new ContentValues();
		cv.put("categoryId", category.getId());
		cv.put("name", category.getName());
		try{
			String logoUrl = category.getLogo().getResources().get(0).getUrl();
			cv.put("logoUrl", logoUrl);
		}catch(Exception e){
		}
		long rowid = db.insert("category", null, cv);
		if (rowid == -1) {
			Log.e(TAG, "Insert category error.");
		}
		Log.d(TAG, "Insert a category, name is "+category.getName());
	}

	@Override
	public void clear() {
		db.execSQL("delete from cat_chn");
		db.execSQL("delete from category");
		Log.i(TAG, "clear all categorys");
	}

	@Override
	public List<Category> query() {
		List<Category> caList = new ArrayList<Category>();
		StringBuilder querySQL = new StringBuilder("select categoryId, name, logoUrl from category where 1=1 ");
		Cursor c = db.rawQuery(querySQL.toString(), null);
		if(c.getCount()<1){
			c.close();
			return caList;
		}
		while (true) {
			c.moveToNext();
			Category ca = new Category();
			ca.setId(c.getLong(0));
			ca.setName(c.getString(1));
			//logo
			List<Resource> res = new ArrayList<Resource>();
			Resource resource = new Resource();
			resource.setUrl(c.getString(2));
			res.add(resource);
			Content content = new Content();
			content.setResources(res);
			ca.setLogo(content);
			
			caList.add(ca);
			if(c.isLast()){
				break;
			}
		}
		c.close();
		return caList;
	}
}
