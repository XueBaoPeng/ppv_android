package com.star.mobile.video.account;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.star.cms.model.User;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.service.UserService;
import com.star.mobile.video.service.UserService.CallbackListener;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.LoadingDataTask;
import com.star.mobile.video.util.ToastUtil;
import com.star.util.loader.OnResultListener;

public class ModifyNameActivity extends BaseActivity implements OnClickListener{
	
	private TextView title;
	private EditText name;
	private ScrollView sl_center;
	private Button save;
	private Button cancel;
	private UserService userService;
	private String nickName;
	private long userid;
	private AccountService mAccountService;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_name);
		((ImageView) findViewById(R.id.iv_actionbar_back)).setOnClickListener(this);
		title = (TextView) findViewById(R.id.tv_actionbar_title);
		setTitle(getIntent());
		userService = new UserService();
		initView();
		name.setOnTouchListener(new OnTouchListener() {  
            @Override  
            public boolean onTouch(View v, MotionEvent event) {  
                changeScrollView();   
                return false;  
            }  
        });  
	}
	
	private void initView() {
		sl_center=(ScrollView)findViewById(R.id.sl_center);
		save=(Button)findViewById(R.id.save_modify);
		cancel=(Button)findViewById(R.id.cancel_modify);
		name=(EditText)findViewById(R.id.new_name_modify);
		name.setOnClickListener(this);
		save.setOnClickListener(this);
		cancel.setOnClickListener(this);
		if(StarApplication.mUser == null){
			userService.setCallbackListener(new CallbackListener() {
				@Override
				public void callback(User u) {
					if(u == null){
						finish();
					}
					userid=StarApplication.mUser.getId();
					name.setText(StarApplication.mUser.getNickName());
					name.setSelection(name.getText().length());
				}
			});
			userService.getUser(this,true);
		}else{
			userid=StarApplication.mUser.getId();
			name.setText(StarApplication.mUser.getNickName());
			name.setSelection(name.getText().length());
		}
		mAccountService = new AccountService(this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setTitle(intent);
	}
	
	private void setTitle(Intent intent) {
		title.setText(intent.getStringExtra("title"));
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_actionbar_back:
			onBackPressed();
			break;
		case R.id.save_modify:
			nickName=name.getText().toString().trim();
			if(checking()){
			sendupdate();
			}
			break;
		case R.id.cancel_modify:
			onBackPressed();
			break;
		default:
			break;
		}
	}
	
	private void sendupdate() {
		mAccountService.updateNickNanme(userid, nickName, new OnResultListener<Integer>() {
			
			@Override
			public void onSuccess(Integer result) {
				CommonUtil.closeProgressDialog();
				if(result != null){
					if(result==User.UPDATE_NICKNAME_SUCCESS) {
						StarApplication.mUser.setNickName(nickName);
						onBackPressed();
					}else if(result==User.DOES_NOT_MATCH) {
						showToast(getString(R.string.does_not_match));
					}else if(result==User.NICKNAME_LENGTH_MISMATCH) {
						showToast(getString(R.string.update_nickname_hint));
					}
					
				}		
			}
			
			@Override
			public boolean onIntercept() {
				CommonUtil.showProgressDialog(ModifyNameActivity.this, null, getString(R.string.sending));
				return false;
			}
			
			@Override
			public void onFailure(int errorCode, String msg) {
				CommonUtil.closeProgressDialog();
			}
		});
	}
	private boolean checking() {
		if (nickName.length() < 4) {
			showToast(getString(R.string.update_nickname_hint));
			return false;
		}

		if (nickName.length() >20) {
			showToast(getString(R.string.update_nickname_hint));
			return false;
		}
		return true;
	}
	private void showToast(String msg) {
		ToastUtil.centerShowToast(this, msg);
	}
	
	/** 
     * 使ScrollView指向底部 
     */  
    private void changeScrollView(){  
        h.postDelayed(new Runnable() {  
            @Override  
            public void run() {  
                sl_center.scrollTo(0, sl_center.getHeight());   
            }  
        }, 300);  
    }  
    Handler h = new Handler(){  
        public void handleMessage(Message msg) {  
        };  
    };  
}
