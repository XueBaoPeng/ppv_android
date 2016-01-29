package com.star.mobile.video.model;

import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.star.mobile.video.R;
import com.star.util.loader.AbsLoader.Type;
import com.star.util.loader.ImageLoader;

public class PhotoAdapter extends BaseAdapter{

	private Context context;
	private List<String> mDatas;
	private String fileDir;
	private int id;
	private int pos;
	private int lastPosition = -1;   //lastPosition 记录上一次选中的图片位置，-1表示未选中                             
	private Vector<Boolean> vector = new Vector<Boolean>();// 定义一个向量作为选中与否容器   
	public PhotoAdapter(Context context, List<String>datas,int id, String dir){
		this.context=context;
		this.mDatas=datas;
		this.id=id;
		this.fileDir=dir;
		 for (int i = 0; i < mDatas.size(); i++) {
	            vector.add(false);
	        }
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mDatas.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder=null;
		String data=mDatas.get(position);
		if(convertView==null){
			holder=new ViewHolder();
			convertView=LayoutInflater.from(context).inflate(id, null);
			holder.mImageView=(ImageView) convertView.findViewById(R.id.iv_pic);
			holder.mSelected=(ImageView) convertView.findViewById(R.id.iv_pic_select);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		holder.mImageView.setScaleType(ScaleType.CENTER);
		holder.mImageView.setImageResource(R.drawable.album_loading_picture);
		ImageLoader.getInstance(3,Type.LIFO).loadImage(fileDir + "/" + data,holder.mImageView);
		if(vector.elementAt(position) == true){
            holder.mSelected.setVisibility(View.VISIBLE);
        }else{
            holder.mSelected.setVisibility(View.GONE);
        }
		return  convertView;
	}
	
	
	 class ViewHolder{
		ImageView  mImageView;
		ImageView mSelected;
	}
	 /**
	     * 修改选中时的状态
	     * @param position
	     */
	    public void changeState(int position){    
	        if(lastPosition != -1)    
	            vector.setElementAt(false, lastPosition);                   //取消上一次的选中状态    
	        vector.setElementAt(!vector.elementAt(position), position);     //直接取反即可    
	        lastPosition = position;                                        //记录本次选中的位置    
	        notifyDataSetChanged();                                         //通知适配器进行更新    
	    }    
}


/*public class PhotoAdapter extends MyCommonAdapter<String>{

	*//**
	 * 用户选择的图片，存储为图片的完整路径
	 *//*
 	public static final List<String> mSelectedImage = new LinkedList<String>();
 	
 	private int lastPosition = -1; // 记录上一次选中的图片位置，-1表示未选中任何图片
    private boolean multiChoose; // 表示当前适配器是否允许多选
    private List<Boolean> mImage_bs = new ArrayList<Boolean>(); // 定义一个向量作为选中与否容器
    private List<Integer> mSelectItems = new ArrayList<Integer>();
    private int size;
 	
	*//**
	 * 文件夹路径
	 *//*
	private String mDirPath;

	public PhotoAdapter(Context context, List<String> mDatas, int itemLayoutId,
			String dirPath) {
		super(context, mDatas, itemLayoutId);
		this.mDirPath = dirPath;
		multiChoose=false; 
		for (int i = 0; i < mDatas.size(); i++){
			 mImage_bs.add(false);
		}
           
		size=mDatas.size();
	}
	  public void changeState(int position) {
     // 多选时
      if (multiChoose == true) {
    	  
      mImage_bs.set(position, true);// 直接取反即可
     }
    // 单选时
    else {
     if (lastPosition != -1)
    	 mImage_bs.set(position, false); // 取消上一次的选中状态
     	mImage_bs.set(position, true);// 直接取反即可
     	lastPosition = position; // 记录本次选中的位置
     }
	  }
	@Override
	public void convert(ViewHolder holder, final String item, final int position) {
		 
		//设置no_pic
				holder.setImageResource(R.id.iv_pic_select, R.drawable.pictures_no);
				 
				//设置图片
				holder.setImageByUrl(R.id.iv_pic, mDirPath + "/" + item);
				
				final ImageView mImageView = holder.getView(R.id.iv_pic);
				final ImageView mSelect = holder.getView(R.id.iv_pic_select);
				
				mImageView.setColorFilter(null);
				
				mImageView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						//  已经选择过该图片
						 changeState(position);
						  for(String s:mSelectedImage){
							  if(s!=null){
									mSelect.setVisibility(View.GONE);
							  }
						  }
						 mSelect.setVisibility(View.GONE);
						 if(mImage_bs.get(position)){
							mSelectedImage.add(mDirPath+"/"+item); 
							mSelect.setVisibility(View.VISIBLE);
						 }else{
							mSelect.setVisibility(View.GONE);
						 }
						if(mSelectedImage.contains(mDirPath+"/"+item)){
							mSelectedImage.clear();
							mSelectedImage.remove(mDirPath+"/"+item);
							mSelect.setVisibility(View.GONE);
						}else{
							mSelectedImage.add(mDirPath + "/" + item);
							mSelect.setVisibility(View.VISIBLE);
						}
					}
				});
				//设置ImageView的点击事件
				mImageView.setOnClickListener(new OnClickListener()
				{
					//选择，则将图片变暗，反之则反之
					@Override
					public void onClick(View v)
					{

						// 已经选择过该图片
						if (mSelectedImage.contains(mDirPath + "/" + item))
						{
							mSelectedImage.remove(mDirPath + "/" + item);
							mSelect.setImageResource(R.drawable.ic_chose_image);
							mImageView.setColorFilter(null);
						} else
						// 未选择该图片
						{
							mSelectedImage.add(mDirPath + "/" + item);
							mImageView.setColorFilter(Color.parseColor("#77000000"));
						}

					}
				});
				
				*//**
				 * 已经选择过的图片，显示出选择过的效果
				 *//*
				if (mSelectedImage.contains(mDirPath + "/" + item))
				{
					mSelect.setImageResource(R.drawable.ic_chose_image);
					mImageView.setColorFilter(Color.parseColor("#77000000"));
				}

		
	}
	

}
*/