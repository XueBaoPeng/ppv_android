package com.star.mobile.video.adapter;


import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.star.cms.model.Package;
import com.star.mobile.video.R;
import com.star.mobile.video.util.BitmapUtil;
import com.star.ui.ImageView.Finisher;

public class PackageAdapter extends BaseAdapter {

	private List<Package> packages;
	private Context context;
	private int selectPos = -1;
	private Bitmap coverBitmap;

	public PackageAdapter(Context context, List<Package> packages){
		this.context = context;
		this.packages = packages;
		coverBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.blue_cover);
	}
	
	public void setSelectPos(int position){
		this.selectPos = position;
		notifyDataSetChanged();
	}
	
	public void updateDataAndRefreshUI(List<Package> packages) {
		this.packages = packages;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return packages.size();
	}

	@Override
	public Object getItem(int position) {
		return packages.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = LayoutInflater.from(context).inflate(R.layout.view_logo_text, null);
		final com.star.ui.ImageView imageview = (com.star.ui.ImageView) view.findViewById(R.id.logo_icon);
		TextView textview = (TextView) view.findViewById(R.id.logo_text);
		if(position == selectPos){
			imageview.setFinisher(new Finisher() {
				@Override
				public void run() {
					imageview.setImageBitmap(BitmapUtil.toConformBitmap(imageview.getImage(), coverBitmap));
				}
			});
			textview.setSelected(true);
			textview.setTextColor(context.getResources().getColor(R.color.color_blue));
		}else{
			textview.setSelected(false);
			textview.setTextColor(context.getResources().getColor(R.color.onair_btn_unfocus));
		}
		Package pkg = packages.get(position);
		try{
			imageview.setUrl(pkg.getPoster().getResources().get(0).getUrl());
		}catch(Exception e){
		}
		textview.setText(pkg.getName());
		return view;
	}
}
