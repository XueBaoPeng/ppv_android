package com.star.mobile.video.util.adapterutils;

import java.util.List;

import com.star.util.Log;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
/**
 * 共同的adapter
 * @author Lee 
 * @version 1.0 
 * @date 2015/10/26
 * @param <T>
 *
 */
public abstract class CommonAdapter<T> extends BaseAdapter {
	
	protected Context mContext;
	protected List<T> mDatas;
	protected LayoutInflater mInflater;
	protected int mLayoutId;
	
	public CommonAdapter(Context context,List<T> datas, int layoutId) {
		this.mContext = context;
		this.mDatas = datas;
		this.mLayoutId = layoutId;
		mInflater = LayoutInflater.from(context);
	}
	 
	public void setDatas(List<T> datas){
		this.mDatas = datas;
		notifyDataSetInvalidated();
	}
	
	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public T getItem(int position) {
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.i("initData", "position="+position);
		ViewHolder holder = ViewHolder.get(mContext,convertView,parent,mLayoutId,position);
		convert(holder, getItem(position));
		return holder.getConvertView();
	}
	public abstract void convert(ViewHolder holder,T t);
}
