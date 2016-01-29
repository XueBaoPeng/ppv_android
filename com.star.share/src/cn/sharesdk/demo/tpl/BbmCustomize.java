package cn.sharesdk.demo.tpl;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import cn.sharesdk.framework.CustomPlatform;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.onekeyshare.EditPage.ColseFriendCallback;

public class BbmCustomize extends CustomPlatform  {
	private static final int MSG_TOAST = 1;
	public static final String NAME = BbmCustomize.class.getSimpleName();
	private ColseFriendCallback colseCallback;
	public void callback(ColseFriendCallback colseCallback){
		this.colseCallback=colseCallback;
	}
	public BbmCustomize(Context context) {
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
		i.setPackage("com.bbm");
		i.setType("image/*");
		PackageManager pm = getContext().getPackageManager();
		List<?> ris = pm.queryIntentActivities(
				i, PackageManager.GET_ACTIVITIES);
		return ris != null && ris.size() > 0;
	}

	protected void doShare(Platform.ShareParams params) {
		ShareParams sp = new ShareParams(params.toMap());
		Intent i = new Intent(Intent.ACTION_SEND);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.setPackage("com.bbm");
		String imagePath = sp.getImagePath();
		String text = sp.getText();
		if (!TextUtils.isEmpty(text)) {
			i.putExtra(Intent.EXTRA_TEXT, text);
			i.setType("text/plain");
			try {
				getContext().startActivity(i);
				if( colseCallback!= null) {
					colseCallback.colse("");
				}
				if (listener != null) {
					HashMap<String, Object> res = new HashMap<String, Object>();
					res.put("ShareParams", params);
					listener.onComplete(this, ACTION_SHARE, res);
				}
			} catch (Throwable t) {
				listener.onError(this, ACTION_SHARE, t);
			}
		
		} else if (!TextUtils.isEmpty(imagePath)
				&& (new File(imagePath).exists())) {
			Uri uri = Uri.fromFile(new File(imagePath));
			i.putExtra(Intent.EXTRA_STREAM, uri);
			i.setType("image/*");
			try {
				getContext().startActivity(i);
				if (listener != null) {
					HashMap<String, Object> res = new HashMap<String, Object>();
					res.put("ShareParams", params);
					listener.onComplete(this, ACTION_SHARE, res);
				}
			} catch (Throwable t) {
				listener.onError(this, ACTION_SHARE, t);
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
