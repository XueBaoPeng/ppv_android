package com.star.mobile.video.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ViewGroup.LayoutParams;

import com.star.mobile.video.R;
import com.star.mobile.video.util.ImageUtil;
import com.star.ui.ImageView;
import com.star.ui.ImageView.Finisher;

public class PreviewImageActivity extends Activity{
	
	private ImageView ivPreview;
	private Bitmap bm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preview_image);
		ivPreview = (ImageView) findViewById(R.id.iv_preview);
		String imageUrl = getIntent().getStringExtra("imageUrl");
		if(imageUrl==null)
			finish();
		if(imageUrl.startsWith("tenbre://")){
			bm = ImageUtil.loadImage(imageUrl.substring(9));
			if(bm != null) {
				ivPreview.setImageBitmap(bm);
				handler.sendEmptyMessage(101);
			}
		}else{
			ivPreview.setUrl(imageUrl);
			ivPreview.setFinisher(new Finisher() {
				@Override
				public void run() {
					bm = ivPreview.getImage();
					if(bm != null) {
						handler.sendEmptyMessage(101);
					}
				}
			});
		}
	}

	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what == 101) {
				amplificationImage();
			}
		}
	};
	
	private void amplificationImage() {
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		int screenWidth = metric.widthPixels;  // 屏幕宽度（像素）
        int screenHeight = metric.heightPixels;  // 屏幕高度（像素）
        float density = 1;
		float deltaWidth = screenWidth*1.0f / bm.getWidth();
		float deltaHeight = screenHeight*1.0f / bm.getHeight();
		LayoutParams lp = ivPreview.getLayoutParams();
		if(deltaHeight > deltaWidth){
			lp.width = (int)(screenWidth * density);
			lp.height = (int)(bm.getHeight()*(1+(screenWidth-bm.getWidth())/(bm.getWidth()*1.0f))* density);
		}else{
			lp.width = (int)(bm.getWidth()*(1+(screenHeight-bm.getHeight())/(bm.getHeight()*1.0f)) * density);
			lp.height = (int)(screenHeight * density);
		}
		ivPreview.setLayoutParams(lp);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			finish();
			break;
		default:
			break;
		}
		return super.onTouchEvent(event);
	}
}
