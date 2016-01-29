package cn.sharesdk.demo.tpl;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import cn.sharesdk.framework.CustomPlatform;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.onekeyshare.EditPage.ColseFriendCallback;


public class FacebookCustomize extends CustomPlatform  {
	public static final String NAME = FacebookCustomize.class.getSimpleName();
	private ColseFriendCallback colseCallback;
	public void callback(ColseFriendCallback colseCallback){
		this.colseCallback=colseCallback;
	}
	public FacebookCustomize(Context context) {
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
		i.setPackage("com.facebook.katana");
		i.setType("image/*");
		PackageManager pm = getContext().getPackageManager();
		List<?> ris = pm.queryIntentActivities(
				i, PackageManager.GET_ACTIVITIES);
		return ris != null && ris.size() > 0;
	}

	protected void doShare(Platform.ShareParams params) {
		
		ShareParams sp = new ShareParams(params.toMap());
		String imagePath = sp.getImagePath();
		String text = sp.getText();
		String url=sp.getUrl();
		ShareContent shareContent = new ShareContent();
		shareContent.setText(text);
		shareContent.setUrl(url);
		shareContent.setImagepath(imagePath);
		shareContent.setTitle(sp.getComment());
		if (!TextUtils.isEmpty(text)) {
			Intent intent=new Intent(getContext(),FacebookShare.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("shareContent", shareContent);
			intent.putExtras(bundle);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
			getContext().startActivity(intent);
//			Message msg = new Message();
//			msg.obj = colseCallback;
//			msg.what = 102;
//			FacebookShare.handler.handleMessage(msg);
			if(colseCallback !=null){
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
