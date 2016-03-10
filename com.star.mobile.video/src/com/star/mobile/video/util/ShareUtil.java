package com.star.mobile.video.util;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Parcelable;
import android.util.Log;
import cn.sharesdk.demo.tpl.BbmCustomize;
import cn.sharesdk.demo.tpl.FacebookCustomize;
import cn.sharesdk.demo.tpl.TwitterCustomize;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.EditPage.ColseFriendCallback;
import cn.sharesdk.onekeyshare.EditPage.TwitterShareCallback;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.sharesdk.system.email.Email;
import cn.sharesdk.system.text.ShortMessage;

import com.facebook.FacebookSdk;
import com.star.cms.model.Program;
import com.star.cms.model.Tenb;
import com.star.cms.model.User;
import com.star.mobile.video.R;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.tenb.TenbService;
import com.star.util.app.GA;
import com.star.util.http.IOUtil;

@SuppressLint("DefaultLocale")
public class ShareUtil {
	 
	/**充值分享*/
	public static final int RECHARGE_SHARE = 1; 
	
	/**直接分享*/
	public static final int DIRECTLY_SHARE = 2;
	
	/**新人卷转赠分享*/
	public static final int COUPLE_EXCHANGE_EXAMPLES = 3;
	
	/**Free coupon 分享*/
	public static final int FREE_COUPON_SHARE = 4;
	
	/**砸蛋后分享*/
	public static final int BREAK_EGG_SHARE = 6;
	
	public static void share(Context context, String nameApp, Bitmap imagePath, String text) {
		try {
			List<Intent> targetedShareIntents = new ArrayList<Intent>();
			Intent share = new Intent(android.content.Intent.ACTION_SEND);
			share.setType("image/jpeg");
			List<ResolveInfo> resInfo = context.getPackageManager()
					.queryIntentActivities(share, 0);
				for (ResolveInfo info : resInfo) {
					String pname = info.activityInfo.packageName;
//					String name = info.activityInfo.name;
					if (pname.toLowerCase().contains(nameApp)
							/*|| (!name.isEmpty()&&name.toLowerCase().contains(nameApp))*/) {
						Intent targetedShare = new Intent(
								android.content.Intent.ACTION_SEND);
						targetedShare.setType("image/jpeg"); // put here your mimetype
						targetedShare.putExtra(Intent.EXTRA_SUBJECT, text);
						targetedShare.putExtra(Intent.EXTRA_TEXT, text);
						targetedShare.putExtra(Intent.EXTRA_STREAM,imagePath/*Uri.fromFile(new File(imagePath))*/);
						targetedShare.setPackage(info.activityInfo.packageName);
						targetedShareIntents.add(targetedShare);
					}
				}
				Intent chooserIntent = Intent.createChooser(
						targetedShareIntents.remove(0), "Select app to share");
				chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
						targetedShareIntents.toArray(new Parcelable[] {}));
				context.startActivity(chooserIntent);
		} catch (Exception e) {
			Log.e("TTT", "",e);
		}
	}
	
	public static void showShare(final Context context, final String text, String imageURL, String title,final Program programe) {
        ShareSDK.initSDK(context);
        ShareSDK.registerPlatform(BbmCustomize.class);
        ShareSDK.registerPlatform(FacebookCustomize.class);
        ShareSDK.registerPlatform(TwitterCustomize.class);
        FacebookSdk.sdkInitialize(context.getApplicationContext());
        final OnekeyShare oks = new OnekeyShare();
//        oks.setTwitterShareCallback(new TwitterShareCallback() {
//			
//			@Override
//			public void hui(String str) {
//			    	oks.setSetContent("isdialog");
//			}
//		});
        oks.setSetContent("isdialog");
//        //关闭sso授权
//        oks.disableSSOWhenAuthorize();
//        oks.setSilent(false);
//        // 分享时Notification的图标和文字
        oks.setNotification(R.drawable.app_icon, title);
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(title);
//        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//        oks.setTitleUrl("http://www.baidu.com");
        // text是分享文本，所有平台都需要这个字段
        oks.setText(text);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath(imageURL);
        oks.setImageUrl(imageURL);
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://tenbre.tk/portal/apk.html");
//        oks.setFilePath("http://ec2-54-77-98-140.eu-west-1.compute.amazonaws.com/portal/com.star.mobile.video.apk");
//        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("Sincerely invite you to watch this program from StarTimes TV. "+text);
//        // site是分享此内容的网站名称，仅在QQ空间使用
//        oks.setSite("StarGO");
//        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
//        oks.setSiteUrl("http://www.baidu.com");

        // 启动分享GUI
        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
			
			@Override
			public void onShare(Platform platform, ShareParams paramsToShare) {
				GA.sendEvent("Share", "share", platform.getName(), 1);
				TenbService tenbService=new TenbService(context);
		        tenbService.doTenbData(Tenb.TENB_SHARE_EPG, programe.getId());
			}
		});
        oks.show(context);
        
   }

	/**
	 * 
	 * @param context
	 * @param text
	 * @param shareUrl
	 * @param imagePath 分享图片路径
	 * @param shareTiming 分享时机
	 */
	public static void shareEgg(Context context,String shareUrl,String text,String imagePath) {
		ShareSDK.initSDK(context);
		ShareSDK.registerPlatform(BbmCustomize.class);
		ShareSDK.registerPlatform(FacebookCustomize.class);
		ShareSDK.registerPlatform(TwitterCustomize.class);
		FacebookSdk.sdkInitialize(context.getApplicationContext());
        OnekeyShare oks = new OnekeyShare();
        oks.setNotification(R.drawable.app_icon, context.getString(R.string.share_from_tenbre));
        oks.setTitle(context.getString(R.string.share_from_tenbre));
        oks.setImageUrl("http://tenbre.tk/portal/img/shonngo_logo.png");
        oks.setUrl(shareUrl);
        oks.setSiteUrl(shareUrl);
        oks.setText(text);
        oks.setSilent(true);
        oks.show(context);
	}
	
	private static void setShareText(Context context,Platform platform, ShareParams paramsToShare, int shareTiming,double faceValue) {
		String platformName = platform.getName();
		if(platformName.equals(Email.NAME) || platformName.equals(ShortMessage.NAME)) {
			setEmaileShareTitle(context,paramsToShare);
		}
	}
	
	private static void setEmaileShareTitle(Context context,ShareParams paramsToShare) {
		paramsToShare.setTitle(context.getString(R.string.email_share_title));
		paramsToShare.setImagePath(null);
		paramsToShare.setImageUrl(null);
	}
	
	/**
	 * 
	 * @param context
	 * @param text
	 * @param shareUrl 分享链接
	 * @param shareTiming 分享时机
	 * @param faceValue 面值
	 */
	public static void shareEgg(final Context context, String text,final String shareUrl,final int shareTiming,final double faceValue) {
		ShareSDK.initSDK(context);
        ShareSDK.registerPlatform(BbmCustomize.class);
        ShareSDK.registerPlatform(FacebookCustomize.class);
        ShareSDK.registerPlatform(TwitterCustomize.class);
        FacebookSdk.sdkInitialize(context.getApplicationContext());
        final OnekeyShare oks = new OnekeyShare();
       
        IOUtil.asstesFileCopySdCard(context, "shareImage/invitatio.png", "/tenbre", "invitatio.png");
        oks.setColseFriendCallback(new ColseFriendCallback(){

			@Override
			public void colse(String str) {
				if(context instanceof Activity)
					CommonUtil.finishActivityAnimation((Activity)context);
			}
        	
        });
    	oks.setTwitterShareCallback(new TwitterShareCallback() {
			
			@Override
			public void hui(String str) {
				 if(shareTiming == BREAK_EGG_SHARE) { // 砸蛋后分享
					 oks.setSetContent(String.format(context.getString(R.string.twitter_break_egg_share), shareUrl));
			     } else if(shareTiming == DIRECTLY_SHARE) { // 直接分享
			    	 oks.setSetContent(String.format(context.getString(R.string.twitter_directly_share), shareUrl));
			     } else if(shareTiming == RECHARGE_SHARE) { // 充值分享
			    	 oks.setSetContent(String.format(context.getString(R.string.twitter_rec_share),shareUrl));
			     }
			}
		});
        oks.setImagePath(Environment.getExternalStorageDirectory()+"/invitatio.png");
        oks.setNotification(R.drawable.app_icon, context.getString(R.string.share_from_tenbre));
        oks.setTitle(context.getString(R.string.share_from_tenbre));
        oks.setImageUrl("http://tenbre.me/portal/img/recommend/invitatio.png");
        oks.setComment(context.getString(R.string.tell_friend_title));
        User user=SharedPreferencesUtil.getUserInfo(context);
        if(shareTiming == BREAK_EGG_SHARE) { // 砸蛋后分享
        	if(user!=null){
        		text = String.format(context.getString(R.string.directly_share),shareUrl, user.getId());
        	}else{
        		text =  String.format(context.getString(R.string.directly_share),shareUrl);

        	}
        		
        } else if(shareTiming == DIRECTLY_SHARE) { // 直接分享
        	
        	if(user!=null){
            	text = String.format(context.getString(R.string.directly_share),shareUrl,user.getId());
        //    	text = String.format(context.getString(R.string.directly_share),user.getId() + "", shareUrl);
        	}
        	//text = String.format(context.getString(R.string.directly_share), shareUrl);
        } else if(shareTiming == RECHARGE_SHARE) { // 充值分享
        	text = String.format(context.getString(R.string.rec_share),shareUrl);
        }
        oks.setText(text);
        if(shareUrl != null) {
        	oks.setUrl(shareUrl);
        }
        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
			
			@Override
			public void onShare(Platform platform, ShareParams paramsToShare) {
				setShareText(context,platform, paramsToShare, shareTiming,faceValue);
				
				ToastUtil.showToast(context, platform.getName());
				GA.sendEvent("Share", "share", platform.getName(), 1);
			}
		});
        oks.show(context);
        
   }
	
}
