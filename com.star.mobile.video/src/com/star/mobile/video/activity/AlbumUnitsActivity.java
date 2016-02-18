package com.star.mobile.video.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.account.HeadviewUploadActivity;
import com.star.mobile.video.adapter.AlbumAdapter;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.model.ImageBean;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.LoadingDataTask;
import com.star.mobile.video.util.ToastUtil;
import com.star.util.loader.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AlbumUnitsActivity extends BaseActivity implements OnClickListener{
	
	private GridView gv_pics;
	private AlbumAdapter mAdapter;
	private List<ImageBean> imageBeans = new ArrayList<ImageBean>();
	private String photoPath;
	private List<String> imagePaths;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album);
		((TextView) findViewById(R.id.tv_actionbar_title)).setText(getString(R.string.album));
		findViewById(R.id.iv_actionbar_back).setOnClickListener(this);
		findViewById(R.id.iv_actionbar_search).setVisibility(View.INVISIBLE);
		gv_pics = (GridView) findViewById(R.id.gv_image_group);
		gv_pics.setOnItemClickListener(itemClickListener);
		scanImages();
	}

	private void scanImages() {
		new LoadingDataTask() {
			@Override
			public void onPreExecute() {
				CommonUtil.showProgressDialog(AlbumUnitsActivity.this);
			}
			
			@Override
			public void onPostExecute() {
				if(imagePaths != null){
					ImageBean bean;
					for(String path : imagePaths){
						bean = new ImageBean(path, false);
						imageBeans.add(bean);
					}
					mAdapter = new AlbumAdapter(AlbumUnitsActivity.this, imageBeans);
					gv_pics.setAdapter(mAdapter);
				}
				CommonUtil.closeProgressDialog();
 			}
			
			@Override
			public void doInBackground() {
				imagePaths = ImageLoader.getInstance().getImagePaths(AlbumUnitsActivity.this);
			}
		}.execute();
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
			File dir = new File(Environment.getExternalStorageDirectory() + "/StarTimes/image/");
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
		if(resultCode == Activity.RESULT_OK){
			if(requestCode==200){
				boolean result = data.getBooleanExtra("uploadResult", false);
				if(result){
					finish();
					ToastUtil.centerShowToast(this, getString(R.string.success_upload_picture));
					StarApplication.mUser = null;
				}else{
					ToastUtil.centerShowToast(this, getString(R.string.fail_upload_picture));
				}
			}else if(requestCode==100){
				Intent intent = new Intent(AlbumUnitsActivity.this, HeadviewUploadActivity.class);
				intent.putExtra("localUrl", photoPath);
				startActivityForResult(intent, 200);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private OnItemClickListener itemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if(position == 0){
				takePhoto();
			}else{
				Intent intent = new Intent(AlbumUnitsActivity.this, HeadviewUploadActivity.class);
				intent.putExtra("localUrl", imagePaths.get(position-1));
				startActivityForResult(intent, 200);
			}
		}
	};
}
