package com.star.mobile.video.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.star.mobile.video.dao.IChatDAO;
import com.star.mobile.video.dao.db.DBHelper;
import com.star.mobile.video.model.ChatVO;

public class ChatDAO implements IChatDAO {

	private final String TAG = this.getClass().getSimpleName();
	private SQLiteDatabase db;

	public ChatDAO(DBHelper dbHelper) {
		this.db = dbHelper.getWritableDatabase();
	}

	@Override
	public boolean add(ChatVO chat,long channelID) {
		ContentValues cv = new ContentValues();
		cv.put("chatId", chat.getId());
		cv.put("channelID", channelID);
		cv.put("userName", chat.getUserName());
		cv.put("userId", chat.getUserId());
		cv.put("icoURL", chat.getIcoURL());
		cv.put("imageURL", chat.getImageURL());
		cv.put("msg",  chat.getMsg());
		cv.put("createDate", chat.getCreateDate().getTime());
		cv.put("type", chat.getType());
		cv.put("status", chat.getStatus());
		long rowid = -1;
		try{
			rowid = db.insert("chat", null, cv);
		}catch(SQLiteConstraintException e){
		}
		if (rowid == -1) {
			Log.w(TAG, "Chat insert ignored. id:"+chat.getId());
			return false;
		}else{
			Log.d(TAG, "Insert a Chat. id:"+chat.getId()+", name is "+chat.getUserName());
			return true;
		}
	}

	@Override
	public List<ChatVO> query(long channelID,int index, int count) {
		List<ChatVO> cs = new ArrayList<ChatVO>();
		String sql = "select chatId,userId,icoURL,imageURL,userName,msg,createDate,type,status from chat where channelID="+channelID+" order by createDate desc limit " + count + " offset " + index;
		Log.d(TAG, "Now SQL:"+sql);
		Cursor c = db.rawQuery(sql, null);
		if (c == null || c.getCount() < 1) {
			return cs;
		}
		while (true) {
			c.moveToNext();
			ChatVO chat = new ChatVO();
			chat.setId(c.getString(0));
			chat.setUserId(c.getLong(1));
			chat.setIcoURL(c.getString(2));
			chat.setImageURL(c.getString(3));
			chat.setUserName(c.getString(4));
			chat.setMsg(c.getString(5));
			chat.setCreateDate(new Date(c.getLong(6)));
			chat.setType(c.getInt(7));
			chat.setStatus(c.getInt(8));
			cs.add(chat);
			if (c.isLast()) {
				break;
			}
		}
		c.close();
		return cs;
	}

	@Override
	public List<ChatVO> query(long channelID, long nowTime) {
		List<ChatVO> cs = new ArrayList<ChatVO>();
		String sql = "select chatId,userId,icoURL,imageURL,userName,msg,createDate,type,status from chat where channelID="+channelID+" and createDate > "+nowTime+" order by createDate asc";
		Log.d(TAG, "Now SQL:"+sql);
		Cursor c = db.rawQuery(sql, null);
		if (c == null || c.getCount() < 1) {
			return cs;
		}
		while (true) {
			c.moveToNext();
			ChatVO chat = new ChatVO();
			chat.setId(c.getString(0));
			chat.setUserId(c.getLong(1));
			chat.setIcoURL(c.getString(2));
			chat.setImageURL(c.getString(3));
			chat.setUserName(c.getString(4));
			chat.setMsg(c.getString(5));
			chat.setCreateDate(new Date(c.getLong(6)));
			chat.setType(c.getInt(7));
			chat.setStatus(c.getInt(8));
			cs.add(chat);
			if (c.isLast()) {
				break;
			}
		}
		c.close();
		return cs;
	}
}
