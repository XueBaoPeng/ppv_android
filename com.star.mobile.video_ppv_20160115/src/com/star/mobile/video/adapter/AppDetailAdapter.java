package com.star.mobile.video.adapter;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.star.mobile.video.R;
import com.star.mobile.video.util.ImageUtil;
import com.star.ui.ImageView;

public class AppDetailAdapter extends BaseAdapter{

	private List<String> datas;
	private Context context;
	private String reg = "\\[([^\\[\\]]+)\\]";
	private Pattern p = Pattern.compile(reg);
	
	public AppDetailAdapter(List<String> data,Context context) {
		this.datas = data;
		this.context = context;
	}
	
	@Override
	public int getCount() {
		
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder holder;
		if(view == null) {
			holder = new ViewHolder();
			view = LayoutInflater.from(context).inflate(R.layout.view_app_details_item, null);
			holder.tvAppDetail = (TextView) view.findViewById(R.id.tv_app_detaails);
			holder.llImages = (LinearLayout) view.findViewById(R.id.ll_images);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		holder.llImages.removeAllViews();
		String info = datas.get(position);
		Matcher m = p.matcher(info);
		info = info.replaceAll(reg, "");
		while(m.find()) {
			ImageView iv = new ImageView(context);
			ImageUtil.measureLayout(iv, 5f/3);
			iv.setImageResource(R.drawable.default_1);
			iv.setUrl(m.group(1),false);
			holder.llImages.addView(iv);
		}
		holder.tvAppDetail.setText(info);
		return view;
	}

	class ViewHolder{
		public TextView tvAppDetail;
		public LinearLayout llImages;
	}
	
}
