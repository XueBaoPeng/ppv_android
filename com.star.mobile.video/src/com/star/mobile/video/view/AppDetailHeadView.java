package com.star.mobile.video.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.star.mobile.video.R;
import com.star.mobile.video.shared.SharedPreferencesUtil;

public class AppDetailHeadView extends RelativeLayout{

	private ImageView ivAppIcon;
	private TextView tvAppName;
	private TextView tvAppVersion;
	private TextView tvAppSize;
	private Button btnNewVersion;
	private String appVersion;
	private Context context;
	private float appSize;
	private LinearLayout llInformation;
	private ProgressBar pbDownload;
	private View vLine;
	
	public AppDetailHeadView(Context context,String appVersion,float appSize) {
		this(context, null);
		this.appVersion = appVersion;
		this.appSize = appSize;
		this.context = context;
		initData();
	}
	
	public AppDetailHeadView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.view_appinfo_head, this);
		initView();
		if(SharedPreferencesUtil.isNewVersion(context)) {
			btnNewVersion.setVisibility(View.VISIBLE);
		} else {
			btnNewVersion.setVisibility(View.INVISIBLE);
		}
	}

	private void initView() {
		ivAppIcon = (ImageView) findViewById(R.id.iv_appinfo_applogo);
		tvAppName = (TextView) findViewById(R.id.tv_appname);
		tvAppVersion = (TextView) findViewById(R.id.tv_appversion);
		tvAppSize = (TextView) findViewById(R.id.tv_appsize);
		btnNewVersion = (Button) findViewById(R.id.btn_new_version);
		llInformation = (LinearLayout) findViewById(R.id.ll_information);
		pbDownload = (ProgressBar) findViewById(R.id.pb_download_status);
		vLine = findViewById(R.id.app_v_line);
	}
	
	private void initData() {
		tvAppName.setText(context.getResources().getString(R.string.app_name));
		tvAppVersion.setText(appVersion);
		tvAppSize.setText(appSize+"M");
	}
	
	public void setNewVersionBtnOnClick(OnClickListener l) {
		btnNewVersion.setOnClickListener(l);
	}
	
	public void setNewVersionBtnText(String text) {
		btnNewVersion.setText(text);
	}
	
	public String getNewVersionBtnText() {
		return btnNewVersion.getText().toString();
	}
	
	
	public void setNewVersionBtnBg(int resId){
		btnNewVersion.setBackgroundResource(resId);
	}
	
	public void setNewVersionBtnBg(Drawable d) {
		btnNewVersion.setBackgroundDrawable(d);
	}
	
	public void setNewVersionBtnColor(int color) {
		btnNewVersion.setTextColor(color);
	}
	
	public void setDownloadProgress(int progress) {
		pbDownload.setProgress(progress);
	}

	public void setProgressMax(int max) {
		pbDownload.setMax(max);
	}
	
	public void setProgressVisibility(boolean visible) {
		if(visible){
			pbDownload.setVisibility(View.VISIBLE);
			vLine.setVisibility(View.GONE);
		} else {
			pbDownload.setVisibility(View.GONE);
			vLine.setVisibility(View.VISIBLE);
		}
	}
}
