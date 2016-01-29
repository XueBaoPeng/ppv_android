package com.star.mobile.video.discovery;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.star.cms.model.Discovery;
import com.star.mobile.video.R;
import com.star.ui.ImageView;

public class DiscoveryAdapter extends BaseAdapter {

	private List<Discovery> items;
	private Context mContext;
	
	public DiscoveryAdapter(List<Discovery> items,Context context) {
		this.items = items;
		this.mContext = context;
	}
	
	public void updateData(List<Discovery> items) {
		this.items = items;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHandler handler = null;
		Discovery data = items.get(position);
		if(view == null) {
			handler = new ViewHandler();
			view = LayoutInflater.from(mContext).inflate(R.layout.dicobery_item_view, null);
			handler.ivDicIcon = (ImageView) view.findViewById(R.id.iv_dic_icon);
			handler.tvDicTitle = (TextView) view.findViewById(R.id.tv_dic_title);
			handler.tvDicContent = (TextView) view.findViewById(R.id.tv_dic_content);
			view.setTag(handler);
		} else {
			handler = (ViewHandler) view.getTag();
		}
//		handler.ivDicIcon.setImageURI(Uri.parse(data.getLogo()));
		handler.ivDicIcon.setUrl(data.getLogo());
		handler.tvDicTitle.setText(data.getName());
		handler.tvDicContent.setText(data.getDescription());
		return view;
	}
	
	private class ViewHandler {
//		SimpleDraweeView ivDicIcon;
		ImageView ivDicIcon;
		TextView tvDicTitle;
		TextView tvDicContent;
	} 

}
