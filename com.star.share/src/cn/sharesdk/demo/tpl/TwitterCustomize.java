package cn.sharesdk.demo.tpl;


import java.io.File;
import java.util.HashMap;
import java.util.List;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import cn.sharesdk.demo.tpl.FacebookCustomize.ShareParams;
import cn.sharesdk.framework.CustomPlatform;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.EditPage.ColseFriendCallback;
import cn.sharesdk.onekeyshare.EditPage.TwitterShareCallback;

public class TwitterCustomize extends CustomPlatform  {
	private static final int MSG_TOAST = 1;
	public static final String NAME = TwitterCustomize.class.getSimpleName();
	private String twitterShareContent;
	private TwitterShareCallback huidiaoCallback;
	private ColseFriendCallback colseCallback;
	public void callback(ColseFriendCallback colseCallback){
		this.colseCallback=colseCallback;
	}
	public void hdcallback(TwitterShareCallback huidiaoCallback){
		this.huidiaoCallback=huidiaoCallback;
	}
	
	public TwitterCustomize(Context context) {
		super(context);
	}
	
	public String getName() {
		return NAME;
	}

	protected boolean checkAuthorize(int action, Object extra) {
		return isValid();
	}

	public boolean isValid() {
		return isClientInstalled();
		
	}

	private boolean isClientInstalled() {
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setPackage("com.twitter.android");
		i.setType("image/*");
		PackageManager pm = getContext().getPackageManager();
		List<?> ris = pm.queryIntentActivities(
				i, PackageManager.GET_ACTIVITIES);
		return ris != null && ris.size() > 0;
	}

	protected void doShare(Platform.ShareParams params) {
		ShareParams sp = new ShareParams(params.toMap());
		String imagePath = sp.getImagePath();
		if( huidiaoCallback!= null) {
			huidiaoCallback.hui(twitterShareContent);
			twitterShareContent=OnekeyShare.getSetContent();
		}
		if (!TextUtils.isEmpty(twitterShareContent)) {
			 File filePath = new File(imagePath);
			 Intent shareIntent = new Intent();
			 shareIntent.setPackage("com.twitter.android");
		     shareIntent.setAction(Intent.ACTION_SEND);
		     shareIntent.putExtra(Intent.EXTRA_TEXT, twitterShareContent);
		     shareIntent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(filePath));  //optional//use this when you want to send an image
		     shareIntent.setType("image/jpeg");
//		     shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		     shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
		     getContext().startActivity(shareIntent);
//			Intent intent=new Intent(getContext(),TwitterShare.class);
//			intent.putExtra("text", twitterShareContent);
//			intent.putExtra("imagepath", imagePath);
//			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			getContext().startActivity(intent);
//			TwitterAuthConfig authConfig = new TwitterAuthConfig(CONSUMER_KEY, CONSUMER_SECRET);
//			 Fabric.with(getContext(),new TwitterCore(authConfig), new TweetComposer());
////			 Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.drawable.ic_launcher);
//			File myImageFile = new File(imagePath);
//			Uri myImageUri = Uri.fromFile(myImageFile);
//			TweetComposer.Builder builder = new TweetComposer.Builder(getContext())
//		     .text(text)
//		     .image(myImageUri);
//			builder.show();
			if( colseCallback!= null) {
				colseCallback.colse("");
			}
		} else if (listener != null) {
			listener.onError(this, ACTION_SHARE, new Throwable("Share content is empty!"));
		}
	}

	public static class ShareParams extends Platform.ShareParams {

		public ShareParams() {
			super();
		}

		public ShareParams(HashMap<String, Object> params) {
			super(params);
		}

		public ShareParams(String jsonParams) {
			super(jsonParams);
		}

		public void setText(String text) {
			set(ShareParams.TEXT, text);
		}

		public String getText() {
			return get(ShareParams.TEXT, String.class);
		}

		public void setImagePath(String imagePath) {
			set(ShareParams.IMAGE_PATH, imagePath);
		}

		public String getImagePath() {
			return get(ShareParams.IMAGE_PATH, String.class);
		}

	}



}
