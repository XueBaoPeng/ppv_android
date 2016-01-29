package com.star.mobile.video.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.star.mobile.video.R;
import com.star.mobile.video.activity.AlbumActivity.OnSelectCallBack;
import com.star.mobile.video.model.ImageBean;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.DensityUtil;
import com.star.mobile.video.util.ToastUtil;
import com.star.util.loader.ImageLoader;

public class AlbumAdapter extends BaseAdapter {

	private Context context;
	private List<ImageBean> imageBeans;
	private List<ImageBean> selectPic = new ArrayList<ImageBean>();
	private int logowidth;
	private OnSelectCallBack callback;
	private View takePhotoBtn;
	private boolean canSelect;
	public AlbumAdapter(Context context, List<ImageBean> imageBeans){
		this.context = context;
		this.imageBeans = imageBeans;
		logowidth = (Constant.WINDOW_WIDTH - 5*DensityUtil.dip2px(context, 6))/4;
		takePhotoBtn = LayoutInflater.from(context).inflate(R.layout.view_album_camera, null);
		takePhotoBtn.setLayoutParams(new AbsListView.LayoutParams(logowidth, logowidth+1));
	}
	
	public void setOnSelectCallBack(OnSelectCallBack callback){
		this.callback = callback;
	}
	
	public void setCanSelectMore(boolean canSelect){
		this.canSelect = canSelect;
	}
	
	@Override
	public int getCount() {
		return imageBeans.size()+1;
	}

	@Override
	public Object getItem(int position) {
		if(position==0)
			return null;
		return imageBeans.get(position-1);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(position == 0){
			return takePhotoBtn;
		}
		position--;
		ViewHolder holder;
		if(convertView == null || convertView.getTag()==null){
			convertView = LayoutInflater.from(context).inflate(R.layout.view_album_item, null);
			holder = new ViewHolder();
			holder.iv_pic = (ImageView) convertView.findViewById(R.id.iv_pic);
			holder.iv_pic_select = (ImageView) convertView.findViewById(R.id.iv_pic_select);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		ImageBean currentBean = imageBeans.get(position);
		if(canSelect){
			convertView.setOnClickListener(new OnSelectClickListener(position));
			if(currentBean.isSelected())
				holder.iv_pic_select.setVisibility(View.VISIBLE);
		}else{
			holder.iv_pic_select.setVisibility(View.GONE);
		}
		holder.iv_pic.setScaleType(ScaleType.CENTER);
		holder.iv_pic.setImageResource(R.drawable.album_loading_picture);
		ImageLoader.getInstance(5,ImageLoader.Type.LIFO).loadImage(currentBean.getPath(), holder.iv_pic);
		convertView.setLayoutParams(new AbsListView.LayoutParams(logowidth, logowidth));
		return convertView;
	}
	
	class OnSelectClickListener implements OnClickListener{
		
		private int selectPos;
		public OnSelectClickListener(int position){
			this.selectPos = position;
		}
		
		@Override
		public void onClick(View v) {
			ImageBean bean = imageBeans.get(selectPos);
			bean.setSelected(!bean.isSelected());
			if(selectPic.contains(bean)){
				v.findViewById(R.id.iv_pic_select).setVisibility(View.GONE);
				selectPic.remove(bean);
			}else{
				if(selectPic.size()>4){
					ToastUtil.centerShowToast(context, "Less than 5 pictures one time.");
					return;
				}
				v.findViewById(R.id.iv_pic_select).setVisibility(View.VISIBLE);
				selectPic.add(bean);
			}
			if(callback != null){
				callback.onSelected(selectPic);
			}
		}
	};
	
	class ViewHolder{
		ImageView iv_pic;
		ImageView iv_pic_select;
	}
}
