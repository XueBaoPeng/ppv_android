package com.star.mobile.video.activity;

import static com.star.mobile.video.util.Constants.FORUM_CANCLE_STICK_STATE;
import static com.star.mobile.video.util.Constants.FORUM_DELETE_MESSAGE_STATE;
import static com.star.mobile.video.util.Constants.FORUM_DELETE_STATE;
import static com.star.mobile.video.util.Constants.FORUM_MORE_FIVE_STICK_STATE;
import static com.star.mobile.video.util.Constants.FORUM_STICK_STATE;

import java.net.MalformedURLException;
import java.net.URL;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebBackForwardList;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.star.cms.model.Chart;
import com.star.cms.model.vo.ExchangeVO;
import com.star.cms.model.vo.ProgramVO;
import com.star.mobile.video.AlertManager;
import com.star.mobile.video.R;
import com.star.mobile.video.account.ChooseAreaActivity;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.chatroom.ShareChatRoomActivity;
import com.star.mobile.video.home.HomeActivity;
import com.star.mobile.video.me.feedback.FeedbackActivity;
import com.star.mobile.video.model.LinkPkg;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.smartcard.SmartCardControlActivity;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.CommonUtil.PromptDialogClickListener;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.LinkUtil;
import com.star.mobile.video.util.ToastUtil;
import com.star.util.Log;
public class BrowserActivity extends BaseActivity implements OnClickListener{

	private WebView wv_webshow;
	private ProgressBar progressbar;
	private double total;
	private ExchangeVO exchange;
	private RelativeLayout webActionbar;
	private TextView tvTitle;
	private ImageView ivBack;
	private View mLoadView;
	private final Uri PROFILE_URI = Uri.parse(Constant.SCHEAM);
	private String currentUrl;
	private LinkPkg lp;
	private final int UPDATE_CHANNEL_GUID = 100;
	private final int UPDATE_PROGRAM_DETAIL = 101;
	private final int RECEIVED_ERROR = 102;
	private final int GOTO_STARTIMES_POST = 103;
	private final int FORUM_PROMPT = 104;
	private final int OPEN_EMAIL = 105;
	
	private int isBbs;
	private String loadUrl;
	private String userName;
	private String selfServiceError;
	private int type;
	private Long ownid = null;
	private String page;
	private boolean isAlertEpg;

	private long mProgramID;
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATE_CHANNEL_GUID:
				CommonUtil.goActivityOrFargment(BrowserActivity.this, type, page, ownid, null, null, isAlertEpg);
				break;
			case UPDATE_PROGRAM_DETAIL:
				String programId = (String) msg.obj;
				if (programId != null && !programId.isEmpty()) {
					mProgramID = Long.parseLong(programId);
					showDialogPrompt(mProgramID);
				}
				break;
			case RECEIVED_ERROR:
				//error的时候back按钮的处理
				ToastUtil.centerShowToast(BrowserActivity.this, getString(R.string.url_not_find));
				goBack();
				break;
			case GOTO_STARTIMES_POST:
				wv_webshow.loadUrl("javascript:gotoStartimesPost()"); 
				break;
			case FORUM_PROMPT:
				int promptState = (Integer) msg.obj;
				switch (promptState) {
				case FORUM_STICK_STATE:
					CommonUtil.getInstance().showPromptDialog(BrowserActivity.this, getString(R.string.tips),
							getString(R.string.forum_stick_prompt), getString(R.string.forum_confirm),
							getString(R.string.forum_cancle), new PromptDialogClickListener() {
								
								@Override
								public void onConfirmClick() {
									//TODO 论坛置顶确认按钮点击
									wv_webshow.loadUrl("javascript:topicStick()"); 
								}
								
								@Override
								public void onCancelClick() {
								}
							});
					break;
				case FORUM_CANCLE_STICK_STATE://论坛取消置顶提示
					CommonUtil.getInstance().showPromptDialog(BrowserActivity.this, getString(R.string.tips),
							getString(R.string.forum_cancle_stick_prompt), getString(R.string.forum_confirm),
							getString(R.string.forum_cancle), new PromptDialogClickListener() {
								
								@Override
								public void onConfirmClick() {
									wv_webshow.loadUrl("javascript:topicStick()"); 
								}
								
								@Override
								public void onCancelClick() {
								}
							});
					break;
				case FORUM_DELETE_STATE://论坛删帖提示
					CommonUtil.getInstance().showPromptDialog(BrowserActivity.this, getString(R.string.tips),
							getString(R.string.forum_delete_prompt), getString(R.string.forum_confirm),
							getString(R.string.forum_cancle), new PromptDialogClickListener() {
								
								@Override
								public void onConfirmClick() {
									wv_webshow.loadUrl("javascript:deleteTopic()"); 
								}
								
								@Override
								public void onCancelClick() {
								}
							});
					break;
				case FORUM_MORE_FIVE_STICK_STATE://论坛 置顶超过5个提示
					CommonUtil.getInstance().showPromptDialog(BrowserActivity.this, getString(R.string.tips),
							getString(R.string.forum_more_five_stick_prompt), getString(R.string.ok),
							null, null);
					break;
				case FORUM_DELETE_MESSAGE_STATE://论坛 长按删除消息提示
					CommonUtil.getInstance().showPromptDialog(BrowserActivity.this, getString(R.string.tips),
							getString(R.string.forum_delete_message_prompt), getString(R.string.forum_confirm),
							getString(R.string.forum_cancle), new PromptDialogClickListener() {
								
								@Override
								public void onConfirmClick() {
									wv_webshow.loadUrl("javascript:forumMessageDeletePrompt()"); 
								}
								
								@Override
								public void onCancelClick() {
								}
							});
					break;
				default:
					break;
				}
				
				break;
			case OPEN_EMAIL:
				String emailAddr = (String) msg.obj;
				Intent data=new Intent(Intent.ACTION_SENDTO); 
				data.setData(Uri.parse("mailto:"+emailAddr)); 
				startActivity(data); 
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recommend);
		wv_webshow = (WebView) findViewById(R.id.wv_webshow);
		wv_webshow.setBackgroundColor(getResources().getColor(R.color.window_bg));
		progressbar = (ProgressBar) findViewById(R.id.pb_webpage);
		webActionbar=(RelativeLayout) findViewById(R.id.iv_actionbar_web);
		ivBack = (ImageView) findViewById(R.id.iv_actionbar_back);
		ivBack.setOnClickListener(this);
		tvTitle = (TextView) findViewById(R.id.tv_actionbar_title);
		mLoadView = findViewById(R.id.loadingView);
		mLoadView.setVisibility(View.GONE);
		currentIntent(getIntent());
		findViewById(R.id.iv_actionbar_close).setOnClickListener(this);
		findViewById(R.id.iv_actionbar_share).setOnClickListener(this);
		setConfig();
		progressbar.setVisibility(View.VISIBLE);
		progressbar.setProgress(5);
	}

	@Override
	protected String getScreenName() {

		return getClass().getSimpleName() + "#" + currentUrl;
	}

	private void currentIntent(Intent intent) {
		String pageName = intent.getStringExtra("pageName");
		if (pageName != null) {
			tvTitle.setText(pageName);
		}
		loadUrl = intent.getStringExtra("loadUrl");
		if(loadUrl!=null){
			total = getIntent().getDoubleExtra("total", 0);
			exchange = (ExchangeVO) intent.getSerializableExtra("exchange");
			isBbs=intent.getIntExtra("isBbs", 0);
			userName=intent.getStringExtra("userName");
			selfServiceError=intent.getStringExtra("selfServiceError");
			String bbs_url="bbs";
			if(loadUrl.contains(bbs_url)){
				isBbs = 1;
			}
			if(isBbs == 1) {
				/*loadUrl = */synCookies(this,loadUrl);
				webActionbar.setVisibility(View.GONE);
			}else{
				synCookies(this,loadUrl);
			}
			
			if (loadUrl != null && !"".equals(loadUrl)) {
				wv_webshow.loadUrl(loadUrl);
				currentUrl = loadUrl;
				SharedPreferencesUtil.setLastUrl(loadUrl, BrowserActivity.this);
			} else {
				if (wv_webshow.getUrl() == null) {
					wv_webshow.loadUrl(SharedPreferencesUtil.getLastUrl(BrowserActivity.this));
					currentUrl = SharedPreferencesUtil.getLastUrl(BrowserActivity.this);
				}
			}
			if(loadUrl != null && !loadUrl.isEmpty() && loadUrl.startsWith(getString(R.string.paga_url))) {
				
	//			findViewById(R.id.iv_actionbar_line).setVisibility(View.GONE);
				findViewById(R.id.iv_actionbar_share).setVisibility(View.GONE);
			} else {
	//			findViewById(R.id.iv_actionbar_line).setVisibility(View.VISIBLE);
				findViewById(R.id.iv_actionbar_share).setVisibility(View.VISIBLE);
			}
		}else{
			extractUidFromUri(intent);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		currentIntent(intent);
	}

	private void extractUidFromUri(Intent intent) {
		Uri uri = intent.getData();
		String uid = null;
		if (uri != null && PROFILE_URI.getScheme().equals(uri.getScheme())) {
			uid = uri.getQueryParameter(Constant.UID);
			if (uid != null && uid.toLowerCase().indexOf("http") == -1) {
				uid = "http://" + uid;
			}
		}
		if (uid != null) {
			synCookies(this,uid);
			wv_webshow.loadUrl(uid);
			currentUrl = uid;
			return;
		}
	}

	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	private void setConfig() {
		WebSettings s = wv_webshow.getSettings();
		s.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
		s.setUseWideViewPort(true);
		s.setLoadWithOverviewMode(true);
		s.setSavePassword(true);
		s.setSaveFormData(true);
		s.setSupportZoom(true);
		s.setJavaScriptEnabled(true);
		wv_webshow.setWebViewClient(new CusWebViewClient());
		wv_webshow.setWebChromeClient(new WebChromeClient());
		wv_webshow.addJavascriptInterface(new JavascriptInterface(), "getChannelId");
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && wv_webshow.canGoBack()) {
			goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	private void goBack() {
		WebBackForwardList wf = wv_webshow.copyBackForwardList();
		if (wf.getCurrentIndex() <= 0) {
			onBackPressed();
		}else{
			try {
				wv_webshow.goBack();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	class CusWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url_) {
			view.clearView();
			view.loadUrl(url_);
			currentUrl = url_;
			SharedPreferencesUtil.setLastUrl(url_, BrowserActivity.this);
			return true;
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			Message msg=handler.obtainMessage();
			msg.what=RECEIVED_ERROR;
			handler.sendMessage(msg);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			view.loadUrl("javascript:window.getChannelId.showSource('<head>'+"
					+ "document.getElementsByTagName('html')[0].innerHTML+'</head>');");
			super.onPageFinished(view, url);
		}
	}

	public class WebChromeClient extends android.webkit.WebChromeClient {

		@Override
		public void onReceivedTitle(WebView view, String title) {
			super.onReceivedTitle(view, title);
			tvTitle.setText(title);
		}

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			if (newProgress == 100) {
				progressbar.setVisibility(View.GONE);
			} else {
				progressbar.setVisibility(View.VISIBLE);
				progressbar.setProgress(newProgress);
			}
			super.onProgressChanged(view, newProgress);
		}

	}

	public class JavascriptInterface {

		@android.webkit.JavascriptInterface
		public void btnGoChannelGuide(final String channelId) {
			Long channelID = Long.parseLong(channelId);
			Intent intent = new Intent(BrowserActivity.this, HomeActivity.class);
			intent.putExtra("fragment", getString(R.string.fragment_tag_channelGuide));
			intent.putExtra("paramId", channelID);
			startActivity(intent);
		}

		@android.webkit.JavascriptInterface
		public void dismms(final String faceValue, final String pagaRC) {

			if (!faceValue.equals("undefined") && exchange != null) {
				double fv = Double.parseDouble(faceValue);
				CommonUtil.showRechargeSuccessDialog(BrowserActivity.this, total, fv, Double.parseDouble(pagaRC),
						exchange);
			}
		}

		@android.webkit.JavascriptInterface
		public void goFeedback() {
			Intent intent=new Intent(BrowserActivity.this, FeedbackActivity.class);
			if(selfServiceError!=null){
				intent.putExtra("content", selfServiceError);
			}
			CommonUtil.startActivity(BrowserActivity.this,intent);
			finish();
		}

		@android.webkit.JavascriptInterface
		public void showSource(String html) {
			lp = LinkUtil.processLink_(currentUrl + "###source=" + html);
		}

		@android.webkit.JavascriptInterface
		public void showAlertPrompt(String programId) {
			Message msg = handler.obtainMessage();
			msg.what = UPDATE_PROGRAM_DETAIL;
			msg.obj = programId;
			handler.sendMessage(msg);
		}
		@android.webkit.JavascriptInterface
		public void shareChatRoom() {
			if(lp!=null){
				Intent intent = new Intent(BrowserActivity.this, ShareChatRoomActivity.class);
				intent.putExtra("linkpkg", lp);
				intent.putExtra("type", Chart.TYPE_LINK);
				CommonUtil.startActivity(BrowserActivity.this, intent);	
			}else{
				ToastUtil.showToast(BrowserActivity.this, getString(R.string.browser_share_tip));
			}
		}
		
		/**
		 * 如果德甲、意甲没有登录点击换包或充值时的提示
		 */
		@android.webkit.JavascriptInterface
		public void showAlertPromptIfMatchNotLogin(){
			String userName = SharedPreferencesUtil.getUserName(BrowserActivity.this);
			if (userName == null || "".equals(userName)) {
				CommonUtil.getInstance().showPromptDialog(BrowserActivity.this, null,
						getString(R.string.alert_login), getString(R.string.login_btn),
						getString(R.string.later), new PromptDialogClickListener() {
							
							@Override
							public void onConfirmClick() {
								Intent intentChoose = new Intent (BrowserActivity.this, ChooseAreaActivity.class);
								CommonUtil.startActivity(BrowserActivity.this, intentChoose);
							}
							
							@Override
							public void onCancelClick() {
							}
						});
			} else {
				
				// 跳转到充值界面
				Intent chargeIntent = new Intent(BrowserActivity.this, SmartCardControlActivity.class);
				startActivity(chargeIntent);
//				finish();
			}
		}
		
		/**
		 * 判断有没有登录
		 */
		@android.webkit.JavascriptInterface
		public void isLogin(){
			String userName = SharedPreferencesUtil.getUserName(BrowserActivity.this);
			if (userName == null || "".equals(userName)) {
				CommonUtil.getInstance().showPromptDialog(BrowserActivity.this, null,
						getString(R.string.post_prompt_msg), getString(R.string.login_btn),
						getString(R.string.later), new PromptDialogClickListener() {
							
							@Override
							public void onConfirmClick() {
								Intent intent = new Intent (BrowserActivity.this, ChooseAreaActivity.class);
								intent.putExtra("relogin", true);
								CommonUtil.startActivity(BrowserActivity.this, intent);
							}
							
							@Override
							public void onCancelClick() {
							}
						});
			}else{
				handler.sendEmptyMessage(GOTO_STARTIMES_POST);
			}
		}
		/**
		 * 论坛置顶提示
		 */
		@android.webkit.JavascriptInterface
		public void forumStickDeletePrompt(String forumPromptState){
			int promptState = Integer.parseInt(forumPromptState);
			Message msg=handler.obtainMessage();
			msg.what=FORUM_PROMPT;
			msg.obj = promptState;
			handler.sendMessage(msg);
			
		}
		/**
		 * 打开系统邮箱
		 */
		@android.webkit.JavascriptInterface
		public void openEmail(String emailAddr) {
			Message msg=handler.obtainMessage();
			msg.what=OPEN_EMAIL;
			msg.obj = emailAddr;
			handler.sendMessage(msg);
		}
		@android.webkit.JavascriptInterface
		public void toAppPage(String types, String page, String ownId) {
			BrowserActivity.this.page = page;
			type = Integer.parseInt(types);
			if (ownId != null && !"".equals(ownId)) {
				ownid = Long.parseLong(ownId);
			}
			handler.sendEmptyMessage(UPDATE_CHANNEL_GUID);
		}
		
		@android.webkit.JavascriptInterface
		public void toast(String msg) {
			ToastUtil.centerShowToast(BrowserActivity.this, msg);
		}
		
		
		
		@android.webkit.JavascriptInterface
		public void finish() {
			BrowserActivity.super.onBackPressed();
		}
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_actionbar_back:
			goBack();
			break;
		case R.id.iv_actionbar_share:
			if(lp!=null){
				Intent intent = new Intent(BrowserActivity.this, ShareChatRoomActivity.class);
				intent.putExtra("linkpkg", lp);
				intent.putExtra("type", Chart.TYPE_LINK);
				CommonUtil.startActivity(BrowserActivity.this, intent);	
			}else{
				ToastUtil.showToast(BrowserActivity.this, getString(R.string.browser_share_tip));
			}
			break;
		case R.id.iv_actionbar_close:
			BrowserActivity.super.onBackPressed();
			break;
		default:
			break;
		}
	}



	/**
	 * 是否显示对话框
	 * 
	 * @param p
	 */
	private void showDialogPrompt(Long programId) {
		boolean favStatus = false;
		if(programId==null)
			return;
		for (ProgramVO mark : AlertManager.getInstance(BrowserActivity.this).alertOutlines) {
			if (programId.equals(mark.getId())) {
				favStatus = mark.isIsFav();
				break;
			}
		}
		if (!favStatus) {
			CommonUtil.getInstance().showPromptDialog(BrowserActivity.this, null,
					getString(R.string.prompt_msg), getString(R.string.ok),
					getString(R.string.later), new PromptDialogClickListener() {
						
						@Override
						public void onConfirmClick() {
							CommonUtil.startEpgActivity(BrowserActivity.this, mProgramID);
						}
						
						@Override
						public void onCancelClick() {
						}
					});
		} else {
			// 吐司说明已经预约
			ToastUtil.centerShowToast(BrowserActivity.this, getResources().getString(R.string.alert_prompt));
		}
	}
	/** 
	 * 同步cookie 
	 */  
	public String synCookies(Context context, String url) {
		String userName;
		if(SharedPreferencesUtil.getUserName(this) != null) {
			userName=SharedPreferencesUtil.getUserName(this);
		} else {
			userName=android.os.Build.MODEL;
		}
	    CookieSyncManager.createInstance(context);  
        CookieManager cookieManager = CookieManager.getInstance();  
        cookieManager.removeSessionCookie();
        cookieManager.removeAllCookie();
        String d="";
        try {
        	URL ur=new URL(url);
        	int index = ur.getPath().indexOf(";");
        	String path = index<0?ur.getPath():ur.getPath().substring(0,index);
        	d=ur.getProtocol()+"://"+ur.getHost()+":"+ur.getPort()+path;
        	cookieManager.setCookie(d, "jforumUserInfo="+userName+"; path=/");  
        	cookieManager.setCookie(d, "token="+SharedPreferencesUtil.getToken(this)+"; path=/");  
            String cookie = cookieManager.getCookie(d);
            if(cookie!=null){
            	Log.i("Cookie", cookie+" URL:"+url+" cookieURL:"+d);
            }
     	    CookieSyncManager.getInstance().sync();
     	    return d;
        } catch (MalformedURLException e) {
			e.printStackTrace();
		}
        
        return null;
       
	}
	
}
