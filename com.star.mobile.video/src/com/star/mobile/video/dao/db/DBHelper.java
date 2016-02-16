package com.star.mobile.video.dao.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.ApplicationUtil;

public class DBHelper extends SQLiteOpenHelper {
	private static String TAG = DBHelper.class.getName();
	private boolean dbReady = true;
	private SQLiteDatabase db;
	private static DBHelper dbHelper;
	private static String[] sqlLists;
	private Context context;
	
	public static synchronized DBHelper getInstence(Context context){
		if(dbHelper == null){
			dbHelper = new DBHelper(context);
		}
		return dbHelper;		
	}
	
    private DBHelper(Context context) {
        super(context, "shonngo.db", null, ApplicationUtil.getAppVerison(context));
        this.context = context;
        try {
			sqlLists = context.getAssets().list("sql");
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(sqlLists==null || sqlLists.length==0){
			sqlLists = new String[]{"init.sql", "update_5.sql", "update_19.sql", "update_23.sql", "update_28.sql", "update_40.sql", "update_75.sql", "update_81.sql"};
		}
		Collections.sort(Arrays.asList(sqlLists), new SqlSortComparator());
        Log.d(TAG, "DBHelper created.");
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
    	this.db = db;
    	Log.d(TAG, "Create the tables.");
    	db.execSQL("PRAGMA foreign_keys = ON;");
		for(String sql : sqlLists){
			executeSQLFile(db, "/assets/sql/"+sql);
		}
    	setDbReady(false);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	Log.d(TAG, "upgrade the tables.");
    	SharedPreferencesUtil.upgradeClear(context);
    	for(String sql : sqlLists){
    		if(sql.equals("init.sql"))
    			continue;
    		int version;
    		try{
	    		int start = sql.indexOf("_");
	    		int end = sql.indexOf(".");
    			version = Integer.parseInt(sql.substring(start+1, end));
    		}catch(Exception e){
    			version = Integer.MAX_VALUE;
    		}
    		if(oldVersion<version && newVersion>=version)
    			executeSQLFile(db, "/assets/sql/"+sql);
    	}
    }
    
    public void beginTransaction(){
    	if(db == null){
    		db = getWritableDatabase();
    	}
    	db.beginTransaction();
    }
    
    public void commit(){
    	db.setTransactionSuccessful();
    	db.endTransaction();
    }
    
    private void executeSQLFile(SQLiteDatabase db,String file){
    	InputStream is = this.getClass().getResourceAsStream(file);
    	if(is == null){
    		Log.e(TAG, "Can't get local sql scripts.");
    	}
    	BufferedReader br = new BufferedReader(new InputStreamReader(is));
    	String line;
		try {
			line = br.readLine();
			while(line != null){
				Log.i(TAG, "Now sql:"+line);
	    		db.execSQL(line);
	    		line = br.readLine();
	    	}
			br.close();
			is.close();
		} catch (IOException e) {
			Log.e(TAG, "execute init sql error.",e);
		}
    }

	public boolean isDbReady() {
		return dbReady;
	}

	public void setDbReady(boolean dbReady) {
		this.dbReady = dbReady;
	}

}
