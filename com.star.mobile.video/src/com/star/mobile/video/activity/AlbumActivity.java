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
import android.widget.ImageView;
import android.widget.TextView;

import com.star.mobile.video.R;
import com.star.mobile.video.adapter.AlbumAdapter;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.chatroom.ChatService;
import com.star.mobile.video.model.ImageBean;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.LoadingDataTask;
import com.star.mobile.video.util.ToastUtil;
import com.star.util.loader.ImageLoader;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AlbumActivity extends BaseActivity implements OnClickListener{
	
	private GridView gv_pics;
	private AlbumAdapter mAdapter;
	private ImageView iv_send;
	private ChatService mChatService;
	private List<ImageBean> imageBeans = new ArrayList<ImageBean>();
	private List<ImageBean> selectImages = new ArrayList<ImageBean>();
	private String photoPath;
	private long channelId = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album);
		mChatService = new ChatService(this);
		channelId = getIntent().getLongExtra("channelId", -1);
		if(channelId == -1){
			finish();
			return;
		}
		((TextView) findViewById(R.id.tv_actionbar_title)).setText(getString(R.string.album));
		findViewById(R.id.iv_actionbar_back).setOnClickListener(this);
		iv_send = (ImageView)findViewById(R.id.iv_actionbar_search);
		iv_send.setImageResource(R.drawable.can_not_send);
		iv_send.setOnClickListener(this);
		gv_pics = (GridView) findViewById(R.id.gv_image_group);
		gv_pics.setOnItemClickListener(itemClickListener);
		scanImages();
	}

	private void scanImages() {
		new LoadingDataTask() {
			private List<String> imagePaths;
			@Override
			public void onPreExecute() {
				CommonUtil.showProgressDialog(AlbumActivity.this);
			}
			
			@Override
			public void onPostExecute() {
				if(imagePaths != null){
					ImageBean bean;
					for(String path : imagePaths){
						bean = new ImageBean(path, false);
						imageBeans.add(bean);
					}
					OnSelectCallBack onSelectCallBack = new OnSelectCallBack() {
						@Override
						public void onSelected(List<ImageBean> beans) {
							selectImages = beans;
							if(beans.size()>0)
								iv_send.setImageResource(R.drawable.icon_send_feedback);
							else
								iv_send.setImageResource(R.drawable.can_not_send);
						}
					};
					mAdapter = new AlbumAdapter(AlbumActivity.this, imageBeans);
					mAdapter.setCanSelectMore(true);
					mAdapter.setOnSelectCallBack(onSelectCallBack);
					gv_pics.setAdapter(mAdapter);
				}
				CommonUtil.closeProgressDialog();
 			}
			
			@Override
			public void doInBackground() {
				imagePaths = ImageLoader.getInstance().getImagePaths(AlbumActivity.this);
			}
		}.execute();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_actionbar_back:
			super.onBackPressed();
			break;
		case R.id.iv_actionbar_search:
			if(selectImages.size()>0){
				sendImage();
			}
			break;
		default:
			break;
		}
	}
	
	private void sendImage() {
		Intent data = new Intent();  
        data.putExtra("selectImages", (Serializable)selectImages);
        setResult(Activity.RESULT_OK, data);  
        onBackPressed();
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
			startActivityForResult(intent, 200);
		} else {
			ToastUtil.centerShowToast(this, getString(R.string.no_sdcard));
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Activity.RESULT_OK){
			if(selectImages.size()!=0)
				selectImages.clear();
			ImageBean bean = new ImageBean(photoPath, false);
			selectImages.add(bean);
			sendImage();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private OnItemClickListener itemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if(position == 0){
				takePhoto();
			}
		}
	};
	
	public interface OnSelectCallBack{ 
		public abstract void onSelected(List<ImageBean> beans);
	}
}
