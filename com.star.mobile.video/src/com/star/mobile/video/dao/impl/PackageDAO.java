package com.star.mobile.video.dao.impl;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.star.cms.model.Content;
import com.star.cms.model.Package;
import com.star.cms.model.Resource;
import com.star.mobile.video.dao.IPackageDAO;
import com.star.mobile.video.dao.db.DBHelper;

public class PackageDAO implements IPackageDAO {
	private final String TAG = this.getClass().getSimpleName();
	private SQLiteDatabase db;
	
	public PackageDAO(DBHelper dbHelper) {
		this.db = dbHelper.getWritableDatabase();
	}
	@Override
	public void add(Package p) {
		ContentValues cv = new ContentValues();
		cv.put("packageId", p.getId());
		cv.put("name", p.getName());
		cv.put("code", p.getCode());
		cv.put("type", p.getType());
		try{
			String logoUrl = p.getPoster().getResources().get(0).getUrl();
			cv.put("logoUrl", logoUrl);
		}catch(Exception e){
		}
		long rowid = db.insert("package", null, cv);
		if (rowid == -1) {
			Log.e(TAG, "Insert package error.");
		}
		Log.d(TAG, "Insert a package, name is "+p.getName());
	}

	@Override
	public void clear() {
		db.execSQL("delete from package");
	}

	@Override
	public List<Package> query() {
		List<Package> packageList = new ArrayList<Package>();
		Cursor c = db.rawQuery("select * from package", null);
		if (c == null || c.getCount() < 1) {
			return packageList;
		}
		while (true) {
			c.moveToNext();
			Package p = extractPackageModel(c);
			packageList.add(p);
			if (c.isLast()) {
				break;
			}
		}
		c.close();
		return packageList;
	}

	private Package extractPackageModel(Cursor c) {
		Package p = new Package();
		p.setId(c.getLong(c.getColumnIndex("packageId")));
		p.setName(c.getString(c.getColumnIndex("name")));
		p.setCode(c.getString(c.getColumnIndex("code")));
		p.setType(c.getInt(c.getColumnIndex("type")));
		List<Resource> res = new ArrayList<Resource>();
		Resource resource = new Resource();
		resource.setUrl(c.getString(c.getColumnIndex("logoUrl")));
		res.add(resource);
		Content content = new Content();
		content.setResources(res);
		p.setPoster(content);
		return p;
	}

	@Override
	public Package query(long packageId) {
		Cursor c = db.rawQuery("select * from package where packageId=" + packageId, null);
		if (c == null || c.getCount() < 1) {
			return null;
		}
		c.moveToNext();
		Package p = extractPackageModel(c);
		c.close();
		return p;
	}
}
