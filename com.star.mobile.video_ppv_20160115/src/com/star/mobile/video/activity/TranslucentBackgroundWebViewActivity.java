package com.star.mobile.video.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.me.coupon.MyCouponsActivity;
import com.star.mobile.video.service.EggService;
import com.star.mobile.video.util.CommonUtil;

/**
 *  背景半透明WebView 
 * @author zhangkai
 *
 */
public class TranslucentBackgroundWebViewActivity extends BaseActivity{

	private String url ;
	private WebView web;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_translucent_webview);
		web = (WebView) findViewById(R.id.web);
		web.setBackgroundColor(getResources().getColor(R.color.translucent_background));
		setConfig();
		url = getIntent().getStringExtra("url");
		if(url != null && !"".equals(url)) {
			web.loadUrl(url);
		}
		
	}
	
	
	class CusWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url_) {
			view.clearView();
			view.loadUrl(url_);
			return true;
		}
	}
	
	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	private void setConfig() {
		WebSettings s = web.getSettings();
	    s.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
	    s.setUseWideViewPort(true);
	    s.setLoadWithOverviewMode(true);
	    s.setSavePassword(true);
	    s.setSaveFormData(true);
	    s.setSupportZoom(true);
	    s.setJavaScriptEnabled(true);
	    web.setWebViewClient(new CusWebViewClient());
	    web.addJavascriptInterface(new JavascriptInterface(), "goActivity");
	}
	
	
	public class JavascriptInterface {
			
			@android.webkit.JavascriptInterface
			public void btnGoMyCounp() {
				CommonUtil.startActivity(TranslucentBackgroundWebViewActivity.this, MyCouponsActivity.class);
				TranslucentBackgroundWebViewActivity.super.onBackPressed();
			}
			
			@android.webkit.JavascriptInterface
			public void btnShareCounp(final String coins) {
				new EggService(TranslucentBackgroundWebViewActivity.this).shareOneEgg(TranslucentBackgroundWebViewActivity.this, StarApplication.mUser, Double.parseDouble(coins));
			}
		}
	}
