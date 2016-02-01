package com.star.mobile.video.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.star.cms.model.vo.ProgramVO;
import com.star.mobile.video.dao.IProgramDAO;
import com.star.mobile.video.dao.db.DBHelper;
import com.star.ott.ppvup.model.enums.CategoryType;

public class ProgramDAO implements IProgramDAO {

	private final String TAG = this.getClass().getSimpleName();
	private SQLiteDatabase db;

	public ProgramDAO(DBHelper dbHelper) {
		this.db = dbHelper.getWritableDatabase();
	}

	@Override
	public boolean add(ProgramVO program) {
		ContentValues cv = new ContentValues();
		cv.put("programId", program.getId());
		cv.put("name", program.getName());
		cv.put("channelId", program.getChannelId());
		cv.put("favCount", program.getFavCount());
		cv.put("commentCount", program.getFavCount());
		cv.put("isFav",  program.isIsFav() ? 1 : 0);
		cv.put("startDate", program.getStartDate().getTime());
		cv.put("endDate", program.getEndDate().getTime());
		cv.put("description", program.getDescription());
		cv.put("isChange", 0);
		cv.put("isOutline", 0);
		long rowid = db.insert("program", null, cv);
		if (rowid == -1) {
			Log.e(TAG, "Program insert error. id:"+program.getId());
			return execUpdate(program, true, true);
		}else{
			Log.d(TAG, "Insert a program. id:"+program.getId()+", name is "+program.getName());
			return true;
		}
	}
	
	public boolean addOutline(ProgramVO program) {
		ContentValues cv = new ContentValues();
		cv.put("programId", program.getId());
		cv.put("channelId", program.getChannelId());
		cv.put("name", program.getName());
		cv.put("startDate", program.getStartDate().getTime());
		cv.put("endDate", program.getEndDate().getTime());
		cv.put("favCount", program.getFavCount());
		cv.put("commentCount", program.getCommentCount());
		cv.put("description", program.getDescription());
		cv.put("isFav",  program.isIsFav() ? 1 : 0);
		cv.put("isChange", 1);
		cv.put("isOutline", 1);
		long rowid = db.insert("program", null, cv);
		if (rowid == -1) {
			Log.e(TAG, "add program mark error. id:"+program.getId());
			return execUpdate(program, false, false);
		}else{
			Log.d(TAG, "add a program mark. id:"+program.getId()+", name is "+program.getName());
			return true;
		}
	}
	

	@Override
	public boolean remove(long programId) {
		StringBuilder querySQL = new StringBuilder("delete from program where 1=1");
		querySQL.append(" and programId="+programId);
		String sql = querySQL.toString();
		Log.d(TAG, "Now SQL:"+sql);
		db.execSQL(sql);
		return true;
	}

	@Override
	public void clear() {
		db.execSQL("delete from program");
	}

	@Override
	public ProgramVO query(long programId) {
		StringBuilder querySQL = new StringBuilder("select * from program where 1=1");
		querySQL.append(" and programId="+programId);
		String sql = querySQL.toString();
		Log.d(TAG, "Now SQL:"+sql);
		Cursor c = db.rawQuery(sql, null);
		if(c.getCount()<1){
			c.close();
			return null;
		}
		c.moveToNext();
		ProgramVO program = extractProgramModel(c);
		c.close();
		return program;
	}
	
	public ProgramVO queryIsFav(long programId) {
		StringBuilder querySQL = new StringBuilder("select isFav from program where 1=1");
		querySQL.append(" and programId="+programId);
		String sql = querySQL.toString();
		Log.d(TAG, "Now SQL:"+sql);
		Cursor c = db.rawQuery(sql, null);
		if(c.getCount()<1){
			c.close();
			return null;
		}
		c.moveToNext();
		ProgramVO program = new ProgramVO();
		program.setIsFav(c.getInt(c.getColumnIndex("isFav")) == 0 ? false : true);
		c.close();
		return program;
	}
	
	public boolean queryIsChange(long programId) {
		StringBuilder querySQL = new StringBuilder("select isChange from program where 1=1");
		querySQL.append(" and programId="+programId);
		String sql = querySQL.toString();
		Log.d(TAG, "Now SQL:"+sql);
		Cursor c = db.rawQuery(sql, null);
		if(c.getCount()<1){
			c.close();
			return false;
		}
		c.moveToNext();
		boolean isChange = c.getInt(c.getColumnIndex("isChange")) == 0 ? false : true;
		c.close();
		return isChange;
	}
	
	public boolean queryIsExist(long programId) {
		StringBuilder querySQL = new StringBuilder("select channelId from program where 1=1");
		querySQL.append(" and programId="+programId);
		querySQL.append(" and isOutline=0");
		String sql = querySQL.toString();
		Log.d(TAG, "Now SQL:"+sql);
		Cursor c = db.rawQuery(sql, null);
		if(c.getCount()<1){
			c.close();
			return false;
		}
		c.close();
		return true;
	}
	
	public ProgramVO queryLastProgram(long channelId){
		StringBuilder querySQL = new StringBuilder("select * from program where 1=1");
		querySQL.append(" and channelId="+channelId);
		querySQL.append(" and isOutline=0");
		querySQL.append(" and endDate>="+System.currentTimeMillis());
		querySQL.append(" order by startDate desc ");
		String sql = querySQL.toString();
		Log.d(TAG, "Now SQL:"+sql);
		Cursor c = db.rawQuery(sql, null);
		if(c.getCount()<1){
			c.close();
			return null;
		}
		c.moveToNext();
		ProgramVO program = extractProgramModel(c);
		c.close();
		return program;
	}
	
	public List<ProgramVO> queryOutlines(){
		String sql = "select * from program where isOutline=1 and isFav=1";
		return execQuery(sql);
	}

	private ProgramVO extractProgramModel(Cursor c) {
		ProgramVO program = new ProgramVO();
		program.setId(c.getLong(c.getColumnIndex("programId")));
		program.setName(c.getString(c.getColumnIndex("name")));
		program.setChannelId(c.getLong(c.getColumnIndex("channelId")));
		program.setStartDate(new Date(c.getLong(c.getColumnIndex("startDate"))));
		program.setEndDate(new Date(c.getLong(c.getColumnIndex("endDate"))));
		program.setFavCount(c.getLong(c.getColumnIndex("favCount")));
		program.setCommentCount(c.getLong(c.getColumnIndex("commentCount")));
		program.setDescription(c.getString(c.getColumnIndex("description")));
		program.setIsFav(c.getInt(c.getColumnIndex("isFav")) == 0 ? false : true);
		return program;
	}
	
	public List<ProgramVO> query(Date startDate){
		String sql = "select * from program where isFav=1 and startDate="+startDate.getTime();
		return execQuery(sql);
	}
	
	@Override
	public List<ProgramVO> query(long channelId, long startDate, long endDate, int index,
			int count) {
		StringBuilder querySQL = new StringBuilder("select * from program where isOutline=0");
		if (channelId != -1) {
			querySQL.append(" and channelId=" + channelId);
		}
		if(startDate != -1 && endDate != -1){
			querySQL.append(" and startDate<="+endDate+" and endDate>="+startDate);
		}else if(startDate != -1 && endDate == -1){
			querySQL.append(" and startDate<="+startDate+" and endDate>="+startDate);
		}else if(startDate == -1 && endDate != -1){
			querySQL.append(" and endDate>="+endDate);
		}
		querySQL.append(" order by startDate asc ");
		if(index != -1 && count != -1){
			querySQL.append(" limit " + count + " offset " + index);
		}
		String sql = querySQL.toString();
		return execQuery(sql);
	}

	@Override
	public List<ProgramVO> query(boolean isFav, long startDate, long endDate,
			int index, int count) {
		StringBuilder querySQL = new StringBuilder("select * from program");
		querySQL.append(" where isFav = "+(isFav?1:0));
		if(startDate != -1 && endDate != -1){
			querySQL.append(" and startDate<="+endDate+" and endDate>="+startDate);
		}else if(startDate == -1 && endDate != -1){
			querySQL.append(" and endDate>="+endDate);
		}
		querySQL.append(" order by startDate asc ");
		if(index != -1 && count != -1){
			querySQL.append(" limit " + count + " offset " + index);
		}
		String sql = querySQL.toString();
		return execQuery(sql);
	}
	
	public List<ProgramVO> query(boolean isFav, long minDate, long maxDate) {
		StringBuilder querySQL = new StringBuilder("select * from program");
		querySQL.append(" where isFav = "+(isFav?1:0));
		if(minDate != -1 && maxDate != -1){
			querySQL.append(" and startDate>="+minDate+" and startDate<="+maxDate);
		}
		querySQL.append(" order by startDate asc ");
		String sql = querySQL.toString();
		return execQuery(sql);
	}

	@Override
	public boolean updateFavStatus(ProgramVO program, boolean isSync) {
		boolean result = execUpdate(program, isSync, false);
		return result;
	}
	
	public boolean updateCommentCount(ProgramVO program){
		String sql = "update program set commentCount="+program.getCommentCount()+" where programId="+program.getId();
		try{
			db.execSQL(sql);
			return true;
		}catch(SQLException e) {
			Log.e(TAG, "update program'commentCount error. id:" + program.getId());
			return false;
		}
	}
	
	private boolean execUpdate(ProgramVO program, boolean isSync, boolean isOutline) {
		StringBuilder updateSQL = new StringBuilder("update program");
		if (program != null) {
			if(!isSync){
				updateSQL.append(" set favCount=" + program.getFavCount());
				updateSQL.append(",isFav=" + (program.isIsFav() ? 1 : 0));
				boolean isChange = queryIsChange(program.getId());
				updateSQL.append(",isChange=" + (isChange ? 0 : 1));
			}else{
				updateSQL.append(" set favCount=" + program.getFavCount());
				updateSQL.append(",commentCount=" + program.getCommentCount());
				updateSQL.append(",isChange=0");
				if(isOutline){
					updateSQL.append(",isOutline=0");
				}
			}
			updateSQL.append(" where programId=" + program.getId());
		} 
		String sql = updateSQL.toString();
		Log.d(TAG, "Now SQL:"+sql);
		try{
			db.execSQL(sql);
			return true;
		}catch(SQLException e) {
			Log.e(TAG, "update program error. id:" + program.getId());
			return false;
		}
	}

	@Override
	public List<ProgramVO> query(boolean isChange) {
		StringBuilder querySQL = new StringBuilder("select * from program where 1=1 ");
		querySQL.append(" and isChange="+ (isChange?1:0));
		querySQL.append(" and endDate>="+ System.currentTimeMillis());
		String sql = querySQL.toString();
		return execQuery(sql);
	}

	private List<ProgramVO> execQuery(String sql) {
		List<ProgramVO> programList = new ArrayList<ProgramVO>();
		Log.d(TAG, "Now SQL:"+sql);
		Cursor c = db.rawQuery(sql, null);
		if (c == null || c.getCount() < 1) {
			return programList;
		}
		while (true) {
			c.moveToNext();
			ProgramVO program = extractProgramModel(c);
			programList.add(program);
			if (c.isLast()) {
				break;
			}
		}
		c.close();
		return programList;
	}

	@Override
	public CategoryType getCategoryType(long programId) {
		String sql = "select category_type from program where programId="+programId;
		Log.d(TAG, "Now SQL:"+sql);
		Cursor c = db.rawQuery(sql, null);
		if (c.getCount() < 1) {
			c.close();
			return null;
		}
		c.moveToNext();
		String type = c.getString(c.getColumnIndex("category_type"));
		c.close();
		return type==null?null:CategoryType.valueof(type);
	}

	@Override
	public void updateCategoryType(long programId, CategoryType categoryType) {
		String sql = "update program set category_type='"+categoryType.getName() +"' where programId="+programId;
		try{
			db.execSQL(sql);
		}catch (Exception e) {
		}
	}
}
