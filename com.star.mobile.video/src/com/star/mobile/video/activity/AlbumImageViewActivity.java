package com.star.mobile.video.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.account.HeadviewUploadActivity;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.model.AlbumImageData;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.DensityUtil;
import com.star.mobile.video.util.ToastUtil;
import com.star.util.loader.ImageLoader;
/**
 * 图片文件夹显示
 * @author xbp
 *	
 */
public class AlbumImageViewActivity  extends BaseActivity implements OnClickListener{
	private GridView mGirDir;
	private String photoPath;
	private AlbumAdapter mAdapter;
	private ProgressDialog mProgressDialog;
	/**
	 * 所有的图片
	 */
	private List<String>mImgs;
	/**
	 * 临时的辅助类，用于防止同一个文件夹的多次扫描
	 */
	private HashSet<String> mDirPaths = new HashSet<String>();
	/**
	 * 扫描拿到所有的图片文件夹
	 */
	private List<AlbumImageData> mDatas=new ArrayList<AlbumImageData>();
	
	private Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			mProgressDialog.dismiss();
			initView();
			
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_album);
		getImages();
		
	}
	private void initEvent() {
		((TextView) findViewById(R.id.tv_actionbar_title)).setText(getString(R.string.album));
		findViewById(R.id.iv_actionbar_back).setOnClickListener(this);
		findViewById(R.id.iv_actionbar_search).setVisibility(View.INVISIBLE);
		 mGirDir.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				 
				if (position == 0) {
					takePhoto();
				} else {
					Intent intent = new Intent(AlbumImageViewActivity.this, PhotoActivity.class);
					intent.putExtra("localUrl", mDatas.get(position - 1).getDir());
					startActivityForResult(intent, 200);
				}
				 
			}
		});
		
	}

	private void initView() {
		mGirDir=(GridView) findViewById(R.id.gv_image_group);
		mAdapter = new AlbumAdapter(AlbumImageViewActivity.this, mDatas);
		mGirDir.setAdapter(mAdapter);
		initEvent();
	}
	/**
	 * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
	 */
	private void getImages(){
		if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			Toast.makeText(this, "暂无外部存储", Toast.LENGTH_LONG);
			return ;
		}
		//显示进度条
		mProgressDialog=ProgressDialog.show(this, null, "");
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				 String firstImage=null;
				 Uri mImageUri=MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				 ContentResolver mContentResolver=AlbumImageViewActivity.this.getContentResolver();
				 
				 //只查询jpeg和png的图片
				 Cursor mCursor = mContentResolver.query(mImageUri, null,
							MediaStore.Images.Media.MIME_TYPE + "=? or "
									+ MediaStore.Images.Media.MIME_TYPE + "=?",
							new String[] { "image/jpeg", "image/png" },
							MediaStore.Images.Media.DATE_MODIFIED);
				 while(mCursor.moveToNext()){
					 //获取图片的路径
					 String path=mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
					 //拿到第一张图片的路径
					 if(firstImage==null)
						 firstImage=path;
					 //获取该图片的父路径
					 File parentFile=new File(path).getParentFile();
					 if(parentFile==null)
						 continue;
					 String dirPath=parentFile.getAbsolutePath();
					 
					 AlbumImageData imageData=null;
					 if(mDirPaths.contains(dirPath)){
						 continue;
					 }else{
						  mDirPaths.add(dirPath);
						  imageData=new AlbumImageData();
						  imageData.setDir(dirPath);
						  imageData.setFirstImagePath(path);
					 }
					 mDatas.add(imageData);
				 }
				mCursor.close();
				mDirPaths=null;
				//通知Handler扫描图片完成
				mHandler.sendEmptyMessage(0x110);
			}
		}).start();
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_actionbar_back:
			super.onBackPressed();
			break;
		default:
			break;
		}
	}
	public void takePhoto() {
		String sdcardStatus = Environment.getExternalStorageState();
		if (sdcardStatus.equals(Environment.MEDIA_MOUNTED)) {
			File dir = new File(Environment.getExternalStorageDirectory() + "/shonngo/image/");
			if (!dir.exists())
				dir.mkdirs();
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			File file = new File(dir, String.valueOf(System.currentTimeMillis()) + ".jpg");
			photoPath = file.getAbsolutePath();
			Uri imageUri = Uri.fromFile(file);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			startActivityForResult(intent, 100);
		} else {
			ToastUtil.centerShowToast(this, getString(R.string.no_sdcard));
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == 200) {
				boolean result = data.getBooleanExtra("uploadResult", false);
				if (result) {
					finish();
					ToastUtil.centerShowToast(this, getString(R.string.success_upload_picture));
					StarApplication.mUser = null;
				} else {
					ToastUtil.centerShowToast(this, getString(R.string.fail_upload_picture));
				}
			} else if (requestCode == 100) {
				Intent intent = new Intent(AlbumImageViewActivity.this, HeadviewUploadActivity.class);
				intent.putExtra("localUrl", photoPath);
				startActivityForResult(intent, 200);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

 class AlbumAdapter extends BaseAdapter {

		private Context context;
		private List<AlbumImageData> imageBeans;
		private int logowidth;
		private View takePhotoBtn;
		public AlbumAdapter(Context context, List<AlbumImageData> imageBeans) {
			this.context = context;
			this.imageBeans = imageBeans;
			logowidth = (Constant.WINDOW_WIDTH - 5 * DensityUtil.dip2px(context, 6)) / 4;
			takePhotoBtn = LayoutInflater.from(context).inflate(R.layout.view_album_camera, null);
			takePhotoBtn.setLayoutParams(new AbsListView.LayoutParams(logowidth, logowidth));
		}
		@Override
		public int getCount() {
			return imageBeans.size() + 1;
		}

		@Override
		public Object getItem(int position) {
			if (position == 0)
				return null;
			return imageBeans.get(position - 1);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (position == 0) {
				return takePhotoBtn;
			}
			position--;
			ViewHolder holder;
			if (convertView == null || convertView instanceof LinearLayout) {
				convertView = LayoutInflater.from(context).inflate(R.layout.view_album_item, null);
				holder = new ViewHolder();
				holder.iv_pic = (ImageView) convertView.findViewById(R.id.iv_pic);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			AlbumImageData currentBean = imageBeans.get(position); 
			holder.iv_pic.setScaleType(ScaleType.CENTER);
			holder.iv_pic.setImageResource(R.drawable.album_loading_picture);
			ImageLoader.getInstance(5, ImageLoader.Type.LIFO).loadImage(currentBean.getFirstImagePath(), holder.iv_pic);
			convertView.setLayoutParams(new AbsListView.LayoutParams(logowidth, logowidth));
			return convertView;
		}
		class ViewHolder {
			ImageView iv_pic;
		}
	 
}}
