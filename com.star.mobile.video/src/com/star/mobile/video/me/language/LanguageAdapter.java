package com.star.mobile.video.me.language;

import java.util.List;

import com.star.mobile.video.R;
import com.star.mobile.video.model.AboutItemData;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * 
 * @author zhangkai
 * @notify Lee Huicheng 2015/11/6 修改getview方法中的优化
 */
public class LanguageAdapter extends BaseAdapter {
	
	private List<AboutItemData> datas;
	private Context mContext;
	private int selectItem;
	public LanguageAdapter(List<AboutItemData> datas,Context context) {
		this.datas = datas;
		this.mContext = context;
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
		this.selectItem=LanguageActivity.select_item;
		if (view == null) {
			holder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.about_item_view, null);
			holder.itemName = (TextView) view.findViewById(R.id.tv_item_name);
			holder.imageView = (ImageView) view.findViewById(R.id.iv_target_icon);
			
			view.setTag(holder);
		}else {
			holder = (ViewHolder) view.getTag();
		}
		
		holder.imageView.setVisibility(View.GONE);
		holder.itemName.setText(datas.get(position).getItemName());
		try{
			if(this.selectItem == position){
	           holder.itemName.setTextColor(mContext.getResources().getColor(R.color.orange_color));//选中的Item字颜色变为橘色
	        }
	        else
		     holder.itemName.setTextColor(mContext.getResources().getColor(R.color.black));//未选中的字体颜色变为黑色
		}catch(Exception e){
			e.printStackTrace();
		}
		return view;
	}
	class ViewHolder{
		TextView itemName;
		ImageView imageView;
	}

}
