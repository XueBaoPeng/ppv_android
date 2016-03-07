/**
 * Copyright (c) 2014-present, Facebook, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by Facebook.
 *
 * As with any software that integrates with the Facebook platform, your use of
 * this software is subject to the Facebook Developer Principles and Policies
 * [http://developers.facebook.com/policy/]. This copyright notice shall be
 * included in all copies or substantial portions of the software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package cn.sharesdk.demo.tpl;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.star.util.DifferentUrlContral;
import com.star.util.ServerUrlDao;

import cn.sharesdk.onekeyshare.EditPage.ColseFriendCallback;

public class FacebookShare extends Activity {
	 private static final String PERMISSION = "publish_actions";
    private PendingAction pendingAction = PendingAction.NONE;
    private boolean canPresentShareDialog;
    private CallbackManager callbackManager;
    private ShareDialog shareDialog;
    private String text;
    private String url;
    private String imagepath;
    private String title;
    private ShareContent shareContent;
    private static ColseFriendCallback colseCallback;
    
//    public static Handler handler = new Handler(){
//    	public void handleMessage(android.os.Message msg) {
//    		if(msg.what == 102) {
//    			ColseFriendCallback cf = (ColseFriendCallback)msg.obj;
//    			colseCallback = cf;
//    		}
//    	};
//    };

	private FacebookCallback<Sharer.Result> shareCallback = new FacebookCallback<Sharer.Result>() {
        @Override
        public void onCancel() {
            Log.d("HelloFacebook", "Canceled");
            finish();
        }

        @Override
        public void onError(FacebookException error) {
            Log.d("HelloFacebook", String.format("Error: %s", error.toString()));
            String title = getString(R.string.error);
            String alertMessage = error.getMessage();
//            showResult(title, alertMessage);
            finish();
        }

        @Override
        public void onSuccess(Sharer.Result result) {
            Log.d("HelloFacebook", "Success!");
            if (result.getPostId() != null) {
                String title =getString(R.string.success);
                String id = result.getPostId();
                String alertMessage = getString(R.string.successfully_posted_post, id);
                showResult(title, alertMessage);
            }
            finish();
        }

        private void showResult(String title, String alertMessage) {
        	/*final PromptDialog dialog =new PromptDialog(FacebookShare.this);
        	
        	dialog.setTitle(getString(R.string.share_success_title));
        	dialog.setMessage(getString(R.string.share_success_content));
        	dialog.setConfirmText(getString(R.string.ok));
        	dialog.setConfirmOnClick(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});
        	dialog.setCancelOnClick(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
				}
			});
        	dialog.show();*/
        	/* final AlertDialog dialog = new AlertDialog(FacebookShare.this, true);
        	dialog.show();
			dialog.setTitle(getString(R.string.share_success_title));
			dialog.setMessage(getString(R.string.share_success_content));
			dialog.setButtonText(getString(R.string.ok));
			dialog.setButtonOnClick(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					
				}
			});*/
			
        }
    };

    private enum PendingAction {
        NONE,
        POST_PHOTO,
        POST_STATUS_UPDATE
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(
                callbackManager,
                shareCallback);
       
        Intent i=getIntent();
        shareContent=(ShareContent)i.getSerializableExtra("shareContent");
        text=shareContent.getText();
        url=shareContent.getUrl();
        title=shareContent.getTitle();
        imagepath=shareContent.getImagepath();
        // Can we present the share dialog for regular links?
        canPresentShareDialog = ShareDialog.canShow(
                ShareLinkContent.class);
        onClickPostStatusUpdate();
    }
    private void onClickPostStatusUpdate() {
        performPublish(PendingAction.POST_STATUS_UPDATE, canPresentShareDialog);
    }
    private void postStatusUpdate() {
        ServerUrlDao serverUrlDao = DifferentUrlContral.diffUrlContral(this);
        Profile profile = Profile.getCurrentProfile();
        ShareLinkContent linkContent = new ShareLinkContent.Builder()
        		.setContentTitle(title)
                .setContentDescription(text)
//                .setImageUrl(Uri.parse(getString(R.string.resource_prefix_url)+"/portal/img/share/invitation.png"))
                .setImageUrl(Uri.parse(serverUrlDao.getResourcePrefixUrl() + "/portal/img/share/invitation.png"))
                .setContentUrl(Uri.parse(url))
                .build();
        
        if (canPresentShareDialog) {
            shareDialog.show(linkContent);
        } else if (profile != null && hasPublishPermission()) {
            ShareApi.share(linkContent, shareCallback);
        } else {
            pendingAction = PendingAction.POST_STATUS_UPDATE;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
   
    private boolean hasPublishPermission() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null && accessToken.getPermissions().contains("publish_actions");
    }

    private void performPublish(PendingAction action, boolean allowNoToken) {
//        AccessToken accessToken = AccessToken.getCurrentAccessToken();
//        if (accessToken != null) {
//            pendingAction = action;
//            if (hasPublishPermission()) {
//                // We can do the action right away.
//                handlePendingAction();
//                return;
//            } else {
//                // We need to get new permissions, then complete the action when we get called back.
//                LoginManager.getInstance().logInWithPublishPermissions(
//                        this,
//                        Arrays.asList(PERMISSION));
//                return;
//            }
//        }
//
//        if (allowNoToken) {
//            pendingAction = action;
//            handlePendingAction();
//        }
       pendingAction = action;
       handlePendingAction();
    }
    private void handlePendingAction() {
        PendingAction previouslyPendingAction = pendingAction;
        // These actions may re-set pendingAction if they are still pending, but we assume they
        // will succeed.
        pendingAction = PendingAction.NONE;

        switch (previouslyPendingAction) {
            case NONE:
                break;
            case POST_STATUS_UPDATE:
                postStatusUpdate();
                break;
        }
    }
	
  
}
