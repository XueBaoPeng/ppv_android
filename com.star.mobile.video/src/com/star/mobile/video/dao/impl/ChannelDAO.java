package com.star.mobile.video.dao.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.star.cms.model.AreaTVPlatform;
import com.star.cms.model.Category;
import com.star.cms.model.Content;
import com.star.cms.model.Package;
import com.star.cms.model.Resource;
import com.star.cms.model.TVPlatformInfo;
import com.star.cms.model.enm.OrderType;
import com.star.cms.model.enm.TVPlatForm;
import com.star.cms.model.vo.ChannelVO;
import com.star.mobile.video.dao.IChannelDAO;
import com.star.mobile.video.dao.db.DBHelper;
import com.star.util.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChannelDAO implements IChannelDAO {

	private final String TAG = this.getClass().getSimpleName();
	private SQLiteDatabase db;
	
	public ChannelDAO(DBHelper dbHelper) {
		this.db = dbHelper.getWritableDatabase();
	}
	
	@Override
	public void add(ChannelVO channel) {
		ContentValues cv = new ContentValues();
		cv.put("channelId", channel.getId());
		cv.put("name", channel.getName());
//		cv.put("channelNumber", channel.getChannelNumber());
		cv.put("favCount", channel.getFavCount());
		cv.put("commentCount", channel.getCommentCount()==null?0:channel.getCommentCount());
		cv.put("description", channel.getDescription());
		cv.put("type", channel.getType());
		cv.put("comment_score", channel.getCommentTotalScore());
		cv.put("comment_count", channel.getCommentTotalCount());
		if(channel.isFav()==null)
			cv.put("isFav",  0);
		else
			cv.put("isFav",  channel.isFav() ? 1 : 0);
		cv.put("isChange", 0);
//		try{
//			cv.put("packageId", channel.getOfPackage().getId());
//		}catch(Exception e){
//		}
		try{
			String logoUrl = channel.getLogo().getResources().get(0).getUrl();
			cv.put("logoUrl", logoUrl);
		}catch(Exception e){
		}
		long rowid = db.insert("channel", null, cv);
		if (rowid == -1) {
			Logger.e("Channel insert error. id:"+channel.getId());
		}else{
			Logger.d("Insert a channel. id:"+channel.getId()+", name is "+channel.getName());
			savePlatFormInfo(channel);
		}
	}

	private void savePlatFormInfo(ChannelVO channel){
		List<AreaTVPlatform> infos = channel.getOfAreaTVPlatforms();
		if(infos!=null&&infos.size()>0) {
			for (TVPlatformInfo info : infos.get(0).getPlatformInfos()) {
				ContentValues cv = new ContentValues();
				cv.put("fk_channel", channel.getId());
				if(info.getTvPlatForm()!=null)
					cv.put("platform_type", info.getTvPlatForm().getNum());
				cv.put("channel_number", info.getChannelNumber());
				if(info.getOfPackage()!=null)
					cv.put("packageId", info.getOfPackage().getId());
				db.insert("channel_platform", null, cv);
			}
		}
	}

	@Override
	public void clear() {
		db.execSQL("delete from channel");
		db.execSQL("delete from channel_platform");
		Logger.d("clear all channel");
	}

	@Override
	public ChannelVO query(Long channelId) {
		StringBuilder querySQL = new StringBuilder("select * from channel where 1=1 ");
		querySQL.append(" and channelId="+channelId);
		String sql = querySQL.toString();
		Logger.d("Now SQL:"+sql);
		Cursor c = db.rawQuery(sql, null);
		if(c.getCount()<1){
			c.close();
			return null;
		}
		c.moveToNext();
		ChannelVO channel = extractChannelModel(c);
		String sql_ = "select c.* from cat_chn cc, category c where cc.cat_id=c.categoryId and cc.chn_id ="+channelId;
		Cursor c_ = db.rawQuery(sql_, null);
		List<Category> categories = new ArrayList<Category>();
		while (c_!=null&&c_.getCount()>0) {
			c_.moveToNext();
			Category category = new Category();
			category.setId(c_.getLong(c_.getColumnIndex("categoryId")));
			category.setName(c_.getString(c_.getColumnIndex("name")));
			List<Resource> list = new ArrayList<Resource>();
			Resource re = new Resource();
			re.setUrl(c_.getString(c_.getColumnIndex("logoUrl")));
			list.add(re);
			Content content = new Content();
			content.setResources(list);
			category.setLogo(content);
			categories.add(category);
			if (c_.isLast()) {
				break;
			}
		}
		channel.setCategories(categories);
		c.close();
		c_.close();
		return channel;
	}

	@Override
	public List<ChannelVO> query(boolean isChange) {
		StringBuilder querySQL = new StringBuilder("select * from channel where 1=1 ");
		querySQL.append(" and isChange=" + (isChange ? 1 : -1));
		String sql = querySQL.toString();
		return execQuery(sql);
	}
	
	public List<Long> queryLocalEpgOfChannelIDs() {
		String sql = new String("select distinct c.channelId from program p, channel c where c.channelId=p.channelId and endDate>"+new Date().getTime());
		List<Long> ids = new ArrayList<Long>();
		Cursor c = db.rawQuery(sql, null);
		if (c == null || c.getCount() < 1) {
			return ids;
		}
		while (true) {
			c.moveToNext();
			Long id = c.getLong(c.getColumnIndex("channelId"));
			ids.add(id);
			if (c.isLast()) {
				break;
			}
		}
		c.close();
		return ids;
	}

	private List<AreaTVPlatform> queryPlatInfos(long channelId){
		String sql = "select cp.platform_type, cp.channel_number, cp.packageId, p.name, p.code from channel_platform cp left join package p on cp.packageId=p.packageId where cp.channel_number is not null and cp.fk_channel="+channelId;
		Logger.d("Now SQL:"+sql);
		List<AreaTVPlatform> plats = new ArrayList<AreaTVPlatform>();
		Cursor c = db.rawQuery(sql, null);
		if (c == null || c.getCount() < 1) {
			return plats;
		}
		List<TVPlatformInfo> pfs = new ArrayList<TVPlatformInfo>();
		while (true) {
			c.moveToNext();
			TVPlatformInfo info = new TVPlatformInfo();
			info.setTvPlatForm(TVPlatForm.getTVPlatForm(c.getInt(c.getColumnIndex("cp.platform_type"))));
			info.setChannelNumber(c.getString(c.getColumnIndex("cp.channel_number")));
			Package pa = new Package();
			pa.setName(c.getString(c.getColumnIndex("p.name")));
			pa.setCode(c.getString(c.getColumnIndex("p.code")));
//			List<Resource> list = new ArrayList<Resource>();
//			Resource re = new Resource();
//			re.setUrl(c.getString(c.getColumnIndex("logoUrl")));
//			list.add(re);
//			Content content = new Content();
//			content.setResources(list);
//			pa.setPoster(content);
			info.setOfPackage(pa);
			pfs.add(info);
			if (c.isLast()) {
				break;
			}
		}
		AreaTVPlatform apf = new AreaTVPlatform();
		apf.setPlatformInfos(pfs);
		plats.add(apf);
		c.close();
		return plats;
	}
	
	private ChannelVO extractChannelModel(Cursor c) {
		ChannelVO channel = new ChannelVO();
		channel.setId(c.getLong(c.getColumnIndex("channelId")));
		channel.setName(c.getString(c.getColumnIndex("name")));
//		channel.setChannelNumber(c.getInt(c.getColumnIndex("channelNumber")));
		channel.setOfAreaTVPlatforms(queryPlatInfos(c.getLong(c.getColumnIndex("channelId"))));
		channel.setFavCount(c.getLong(c.getColumnIndex("favCount")));
		channel.setCommentCount(c.getLong(c.getColumnIndex("commentCount")));
		channel.setDescription(c.getString(c.getColumnIndex("description")));
		channel.setFav(c.getInt(c.getColumnIndex("isFav")) == 0 ? false : true);
		channel.setType(c.getInt(c.getColumnIndex("type")));
		channel.setCommentTotalCount(c.getLong(c.getColumnIndex("comment_count")));
		channel.setCommentTotalScore(c.getLong(c.getColumnIndex("comment_score")));
		//logo
		List<Resource> res = new ArrayList<Resource>();
		Resource resource = new Resource();
		resource.setUrl(c.getString(c.getColumnIndex("logoUrl")));
		res.add(resource);
		Content content = new Content();
		content.setResources(res);
		channel.setLogo(content);
		return channel;
	}
	
	public List<ChannelVO> query(int index, int count, List<Integer> types){
		StringBuilder querySQL = new StringBuilder("select * from channel where type in(");
		for(int i=0;i<types.size();i++){
			if(i==types.size()-1)
				querySQL.append(types.get(i)+")");
			else
				querySQL.append(types.get(i)+",");
		}
		if(count != -1 && index != -1){
			querySQL.append(" limit " + count + " offset " + index);
		}
		String sql = querySQL.toString();
		return execQuery(sql);
	}
	
	private List<ChannelVO> execQuery(String sql) {
		List<ChannelVO> chnList = new ArrayList<ChannelVO>();
		Logger.d("Now SQL:"+sql);
		Cursor c = db.rawQuery(sql, null);
		if (c == null || c.getCount() < 1) {
			return chnList;
		}
		while (true) {
			c.moveToNext();
			ChannelVO channel = extractChannelModel(c);
			chnList.add(channel);
			if (c.isLast()) {
				break;
			}
		}
		c.close();
		return chnList;
	}
	
	public List<ChannelVO> queryOfflineChannels(){
		String sql = "select distinct c.* from program p, channel c where c.channelId=p.channelId and p.isOutline=0 and endDate>"+new Date().getTime();
		return execQuery(sql);
	}

	@Override
	public List<ChannelVO> query(boolean isfav, int index, int count) {
		StringBuilder querySQL = new StringBuilder("select * from channel");
		querySQL.append(" where isFav = " + (isfav ? 1 : 0));
		if(index != -1 && count != -1){
			querySQL.append(" limit " + count + " offset " + index);
		}
		String sql = querySQL.toString();
		return execQuery(sql);
	}

	@Override
	public List<ChannelVO> query(long categoryId, boolean isfav, Package p, TVPlatForm tvPlatForm) {
		StringBuilder querySQL = new StringBuilder("select distinct c.* from channel c, cat_chn cc, package p, channel_platform cp");
		boolean append = false;
		if (categoryId != -1) {
			querySQL.append(" where c.channelId=cc.chn_id and cc.cat_id=" + categoryId);
			append = true;
		}
		if(isfav){
			querySQL.append((append?" and ":" where ")+"isFav=1");
			append = true;
		}
		if(p!=null){
			if(p.getType()==3)
				querySQL.append((append?" and ":" where ")+"c.packageId=p.packageId and p.code="+p.getCode()+" and p.type=3");
			else
				querySQL.append((append?" and ":" where ")+"c.packageId=p.packageId and p.code<="+p.getCode()+" and p.type="+p.getType());
			append = true;
		}
		if(tvPlatForm!=null){
			querySQL.append((append?" and ":" where ")+"cp.fk_channel=c.channelId and cp.platform_type="+tvPlatForm.getNum());
		}
		String sql = querySQL.toString();
		return execQuery(sql);
	}
	
	@Override
	public List<ChannelVO> query(long categoryId, OrderType orderType, int index,
			int count) {
		StringBuilder querySQL = new StringBuilder("select distinct c.* from channel c");
		if (categoryId != -1) {
			querySQL.append(" ,cat_chn cc where c.channelId=cc.chn_id and cc.cat_id=" + categoryId);
		}
		if (orderType != null) {
			String orderBy = null;
			if(orderType.equals(OrderType.FAVORITE)){
				orderBy = "isFav";
			}else if(orderType.equals(OrderType.HOT)){
				orderBy = "favCount";
			}else if(orderType.equals(OrderType.ID)){
				orderBy = "channelId";
			}
			querySQL.append(" order by " + orderBy +" desc");
		} 
		if(count != -1 && index != -1){
			querySQL.append(" limit " + count + " offset " + index);
		}
		String sql = querySQL.toString();
		return execQuery(sql);
	}

	@Override
	public boolean updateChannel(ChannelVO channel, boolean isSync) {
		boolean result = execUpdate(channel, isSync);
		return result;
	}
	
	public boolean updateCommentCount(ChannelVO channel){
		String sql = "update channel set commentCount="+channel.getCommentCount()+" where channelId="+channel.getId();
		return execUpdate(sql);
	}
	
	private boolean execUpdate(String sql) {
		try{
			db.execSQL(sql);
			return true;
		}catch(SQLException e) {
			Logger.e("update channel error.");
			return false;
		}
	}

	private boolean execUpdate(ChannelVO channel, boolean isSync) {
		StringBuilder updateSQL = new StringBuilder("update channel");
		if (channel != null) {
			updateSQL.append(" set favCount=" + channel.getFavCount());
			updateSQL.append(",commentCount=" + channel.getCommentCount());
			if(!isSync)
				updateSQL.append(",isFav=" + (channel.isFav() ? 1 : 0));
			updateSQL.append((isSync?",isChange=-1":",isChange=0-isChange"));
			updateSQL.append(",comment_count="+channel.getCommentTotalCount()+",comment_score="+channel.getCommentTotalScore());
			updateSQL.append(" where channelId=" + channel.getId());
		} 
		String sql = updateSQL.toString();
		Logger.d("Now SQL:"+sql);
		try{
			db.execSQL(sql);
			return true;
		}catch(SQLException e) {
			Logger.e("update Channel error. id:" + channel.getId()+"\n",e);
			return false;
		}
	}

	@Override
	public boolean addChannelToCategory(long cat_id, long chn_id) {
		ContentValues cv = new ContentValues();
		cv.put("cat_id", cat_id);
		cv.put("chn_id", chn_id);
		long rowid = db.insert("cat_chn", null, cv);
		if (rowid == -1) {
			Logger.e("Insert channel category relationship error.");
			return false;
		}
		return true;
	}

	@Override
	public boolean updateEpgStatus(ChannelVO channel, boolean hasEpg) {
		String sql = "update channel set hasEPG="+(hasEpg?1:0)+" where channelId="+channel.getId();
		try{
			db.execSQL(sql);
			return true;
		}catch(SQLException e) {
			Logger.e("update channel's hasEpg status error. id:" + channel.getId());
			return false;
		}
	}

	@Override
	public List<ChannelVO> query(int index, int count, OrderType orderType,	boolean hasEpg) {
		StringBuilder querySQL = new StringBuilder("select * from channel where hasEPG="+(hasEpg?1:0));
		if (orderType != null) {
			String orderBy = null;
			String sort = null;
			if(orderType.equals(OrderType.FAVORITE)){
				orderBy = "isFav";
				sort = " desc";
			}else if(orderType.equals(OrderType.HOT)){
				orderBy = "favCount";
				sort = " desc";
			}else if(orderType.equals(OrderType.ID)){
				orderBy = "channelId";
				sort = " desc";
			}else if (orderType.equals(OrderType.CHANNEL_NUMBER)) {
				orderBy = "channelNumber";
				sort = " asc";
			}
			querySQL.append(" order by " + orderBy +sort);
		} 
		if(count != -1 && index != -1){
			querySQL.append(" limit " + count + " offset " + index);
		}
		String sql = querySQL.toString();
		return execQuery(sql);
	}
	@Override
	public List<ChannelVO> query(int offset, int count) {
		StringBuilder querySQL = new StringBuilder("select * from channel order by isFav desc,type ,channelNumber asc");
		if(count != -1 && offset != -1){
			querySQL.append(" limit " + count + " offset " + offset);
		}
		String sql = querySQL.toString();
		return execQuery(sql);
	}

	@Override
	public List<ChannelVO> query() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> queryLikeName(String key, int index, int count) {
		StringBuilder querySQL = new StringBuilder("select name from channel where name like \""+key+"%\"");
		if(count != -1 && index != -1){
			querySQL.append(" limit " + count + " offset " + index);
		}
		String sql = querySQL.toString();
		List<String> chnList = new ArrayList<String>();
		Logger.d("Now SQL:"+sql);
		Cursor c = db.rawQuery(sql, null);
		if (c == null || c.getCount() < 1) {
			return chnList;
		}
		while (true) {
			c.moveToNext();
			chnList.add(c.getString(c.getColumnIndex("name")));
			if (c.isLast()) {
				break;
			}
		}
		c.close();
		return chnList;
	}
	
}
