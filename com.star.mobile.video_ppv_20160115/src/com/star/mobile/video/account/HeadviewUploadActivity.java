package com.star.mobile.video.account;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.star.mobile.video.R;
import com.star.mobile.video.service.UserService;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.ImageUtil;
import com.star.mobile.video.util.ToastUtil;
import com.star.mobile.video.view.ClipZoomImageView;
import com.star.util.loader.OnResultListener;

public class HeadviewUploadActivity extends Activity implements OnClickListener {
	private ClipZoomImageView mZoomImage;
	private UserService userService;
	private AccountService accountService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_headview);
		accountService = new AccountService(this);
		userService = new UserService();
		((TextView) findViewById(R.id.tv_actionbar_title)).setText(R.string.album);
		mZoomImage = (ClipZoomImageView) findViewById(R.id.id_clipImageLayout);
		View cView = findViewById(R.id.view_c);
		ViewGroup.LayoutParams params = cView.getLayoutParams();
		params.height = params.width = Constant.WINDOW_WIDTH * 2 / 3;
		cView.setLayoutParams(params);
		mZoomImage.setHorizontalPadding(Constant.WINDOW_WIDTH / 6);
		findViewById(R.id.iv_actionbar_back).setOnClickListener(this);
		((TextView) findViewById(R.id.tv_bouquet_btn)).setText(R.string.title_save);
		findViewById(R.id.tv_bouquet_btn).setOnClickListener(this);
		String localUrl = getIntent().getStringExtra("localUrl");
		if (!TextUtils.isEmpty(localUrl)) {
			mZoomImage.setImageBitmap(ImageUtil.loadImage(localUrl));
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_actionbar_back:
			finish();
			break;
		case R.id.tv_bouquet_btn:
			uploadImage(mZoomImage.clip());
			break;
		default:
			break;
		}
	}

	private void uploadImage(final Bitmap bitmap) {
		accountService.uploadHead(bitmap, new OnResultListener<Boolean>() {
			
			@Override
			public void onSuccess(Boolean value) {
				CommonUtil.closeProgressDialog();
				ToastUtil.centerShowToast(HeadviewUploadActivity.this, HeadviewUploadActivity.this.getString(R.string.success_upload_picture));
//				userService.initUserInfo(HeadviewUploadActivity.this);
				CommonUtil.startActivity(HeadviewUploadActivity.this,  ModifyMeActivity.class);
			}
			
			@Override
			public boolean onIntercept() {
				CommonUtil.showProgressDialog(HeadviewUploadActivity.this);
				return false;
			}
			
			@Override
			public void onFailure(int errorCode, String msg) {
				CommonUtil.closeProgressDialog();
				ToastUtil.centerShowToast(HeadviewUploadActivity.this, HeadviewUploadActivity.this.getString(R.string.fail_upload_picture));
			}
		});
	}
}
