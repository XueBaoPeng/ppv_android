package com.star.mobile.video.account;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;
import com.star.cms.model.User;
import com.star.cms.model.enm.AccountType;
import com.star.cms.model.enm.Sex;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.activity.AlbumImageViewActivity;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.dialog.SelectSexDialog;
import com.star.mobile.video.dialog.SelectSexDialog.SelectListener;
import com.star.mobile.video.service.UserService;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.ImageUtil;
import com.star.mobile.video.util.ToastUtil;
import com.star.ui.ImageView.Finisher;
import com.star.util.loader.OnResultListener;

public class ModifyMeActivity extends BaseActivity implements OnClickListener, SelectListener {
	private UserService userService;
	private com.star.ui.ImageView user_header;
	private User mUser;
	private TextView user_email;
	private EditText et_username;
	private TextView user_sex;
	private String nickName;
	private Long userid;
	private Sex sex;
	private int sex_type;
	private String new_sex;
	private boolean isChangeSex;
	private boolean isChangeNickName;
	private SelectSexDialog dialog;
	private String lastName;
	private RelativeLayout title;
	private TextView tvsave;
	private ImageView emailImage;
	private ProfilePictureView mFacebookProfilePictureView;
	private AccountService mAccountService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_me);
		title = (RelativeLayout) findViewById(R.id.activity_me_title);
		tvsave = (TextView) title.findViewById(R.id.tv_bouquet_btn);
		((ImageView) findViewById(R.id.iv_actionbar_back)).setOnClickListener(this);
		((TextView) findViewById(R.id.tv_actionbar_title)).setText(R.string.inforamtion);
		tvsave.setText(R.string.title_save);
		tvsave.setVisibility(View.GONE);
		tvsave.setOnClickListener(this);
		userService = new UserService();
		mAccountService = new AccountService(this);
		lastName = SharedPreferencesUtil.getLastUserName(ModifyMeActivity.this);
		initView();
	}

	private void initView() {
		et_username = (EditText) findViewById(R.id.new_name_modify);
		et_username.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if (count > 0) {
					isChangeNickName = true;
					tvsave.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		et_username.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					et_username.setText("");
					InputMethodManager inputManager =
							(InputMethodManager) et_username.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
					inputManager.showSoftInput(et_username, 0);


				} else {
					et_username.setHint(R.string.five_to_eleven_letters);

				}
			}
		});
		user_sex = (TextView) findViewById(R.id.tv_information_sex);
		user_sex.setOnClickListener(this);
		user_email = (TextView) findViewById(R.id.tv_information_email);
		emailImage = (ImageView) findViewById(R.id.icon_mailbox);
		// user_email.setText(lastName);
		user_header = (com.star.ui.ImageView) findViewById(R.id.me_user_header);
		user_header.setFinisher(new Finisher() {
			@Override
			public void run() {
				user_header.setImageBitmap(ImageUtil.getCircleBitmap(user_header.getImage()));
			}
		});
		user_header.setOnClickListener(this);
		mFacebookProfilePictureView = (ProfilePictureView) findViewById(R.id.me_facebook_profile_picture);
		mFacebookProfilePictureView.setOnClickListener(this);
	}

	private void initUserHeader() {
		if (mUser == null){
			return;
		}
		if (mUser.getHead() != null) {
			user_header.setVisibility(View.VISIBLE);
			mFacebookProfilePictureView.setVisibility(View.GONE);
			user_header.setUrl(mUser.getHead());
			/*//Facebook用户第一次登陆没有head头像
			AccountType att = mUser.getType();
			if (AccountType.Facebook.equals(att) || AccountType.Twitter.equals(att)) {
				Profile profile = Profile.getCurrentProfile();
				user_header.setVisibility(View.GONE);
				mFacebookProfilePictureView.setVisibility(View.VISIBLE);
				//Facebook头像获取显示
				mFacebookProfilePictureView.setProfileId(profile.getId());

			}*/
		}
	}

	private void initSex() {
		if (mUser != null) {
			if (mUser.getSex() != null) {
				if (mUser.getSex().equals(Sex.MALE)) {
					user_sex.setText(R.string.sex_man);
				} else if (mUser.getSex().equals(Sex.WOMAN)) {
					user_sex.setText(R.string.sex_woman);
				} else {
					user_sex.setText(R.string.sex_defalut);
				}
			} else {
				user_sex.setText(R.string.sex_defalut);
			}

		} else {
			user_sex.setText(R.string.sex_defalut);
		}
	}

	/**
	 * 判断显示邮箱或者手机帐号
	 * 

	 */
	/*
	 * private void setChangeString() { userService.setCallbackListener(new
	 * CallbackListener() {
	 * 
	 * @Override public void callback(User u) { if (u == null) { finish(); }
	 * setEmail(); } });
	 * 
	 * }
	 */

	protected void setEmail() {
		if (mUser != null) {
			if (AccountType.PhoneNumber.equals(mUser.getType())) {
				emailImage.setImageResource(R.drawable.ic_smartphone);
				user_email.setText(lastName);
			} else if (AccountType.Facebook.equals(mUser.getType())) {
				user_email.setText("Linked from Facebook account");
				emailImage.setImageResource(R.drawable.ic_facebook);
			} else if (AccountType.Twitter.equals(mUser.getType())) {
				user_email.setText("Linked from Twitter");
				emailImage.setImageResource(R.drawable.ic_twitter);
			} else {
				user_email.setText(lastName);
				emailImage.setImageResource(R.drawable.icon_mailbox);
			}
		}

	}

	private void load() {
		userid = mUser.getId();
		et_username.setHint(mUser.getNickName());
		et_username.setSelection(et_username.getText().length());
		// user_name.setText(mUser.getNickName());
		// user_email.setText(mUser.get);
		user_header.setUrl(mUser.getHead());
	}

	@Override
	protected void onResume() {
		super.onResume();
		getUserInfo();
	}

	private void getUserInfo() {
		mAccountService.getUser(new OnResultListener<User>() {

			@Override
			public boolean onIntercept() {
				return false;
			}

			@Override
			public void onSuccess(User value) {
				mUser = value;
				load();
				initUserHeader();
				initSex();
				setEmail();
			}

			@Override
			public void onFailure(int errorCode, String msg) {
				finish();
			}
		});
	}

	private boolean checking() {
		if (et_username.length() < 4) {
			showToast(getString(R.string.update_nickname_hint));
			return false;
		}

		if (et_username.length() > 20) {
			showToast(getString(R.string.update_nickname_hint));
			return false;
		}
		return true;
	}

	private void showToast(String msg) {
		ToastUtil.centerShowToast(this, msg);
	}

	private void sendUserSexupdate(final int type) {
		mAccountService.updateSex(type, new OnResultListener<Integer>() {

			@Override
			public boolean onIntercept() {
				return false;
			}

			@Override
			public void onSuccess(Integer result) {
				CommonUtil.closeProgressDialog();
				userService.initUserInfo(ModifyMeActivity.this);
				showToast(getString(R.string.updata_sex_success));
			}

			@Override
			public void onFailure(int errorCode, String msg) {
				CommonUtil.closeProgressDialog();
				showToast(getString(R.string.updata_sex_faill));
			}
		});
	}

	private void sendNickNameupdate() {
		mAccountService.updateNickNanme(userid, nickName, new OnResultListener<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				CommonUtil.closeProgressDialog();
				if (mUser != null && result == User.UPDATE_NICKNAME_SUCCESS) {
					mUser.setNickName(nickName);
					if (StarApplication.mUser != null) {
						StarApplication.mUser.setNickName(nickName);
					}
					showToast(getString(R.string.updata_sex_success));
				} else if (result == User.DOES_NOT_MATCH) {
					showToast(getString(R.string.does_not_match));
				} else if (result == User.NICKNAME_LENGTH_MISMATCH) {
					showToast(getString(R.string.update_nickname_hint));
				} else {
					showToast(getString(R.string.error_network));
				}

			}

			@Override
			public boolean onIntercept() {
				return false;
			}

			@Override
			public void onFailure(int errorCode, String msg) {
				CommonUtil.closeProgressDialog();
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_actionbar_back:
			//关闭软键盘弹出
			InputMethodManager inputManager =
					(InputMethodManager) et_username.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(et_username.getWindowToken(), 0);
			onBackPressed();
			break;
		case R.id.tv_bouquet_btn:

			CommonUtil.showProgressDialog(ModifyMeActivity.this, null, getString(R.string.sending));
			if (isChangeNickName) {
				nickName = et_username.getText().toString().trim();
				if (checking()) {
					sendNickNameupdate();
					if (isChangeSex) {
						sendUserSexupdate(sex_type);
					}
				} else {
					CommonUtil.closeProgressDialog();
					if (isChangeSex) {
						sendUserSexupdate(sex_type);
					} else {
						CommonUtil.closeProgressDialog();
						break;
					}
				}
			} else if (isChangeSex) {
				sendUserSexupdate(sex_type);
				isChangeSex = false;
			} else {
				CommonUtil.closeProgressDialog();
				break;
			}

			break;
		case R.id.tv_information_sex:
			selectDialog();
			break;
		case R.id.me_user_header:
		case R.id.me_facebook_profile_picture:
			CommonUtil.startActivity(this, AlbumImageViewActivity.class);
			break;
		default:
			break;
		}
	}

	/*
	 * 选择性别弹框
	 */
	private void selectDialog() {
		String sex = user_sex.getText().toString();
		dialog = new SelectSexDialog(ModifyMeActivity.this, this, sex);
		dialog.show();
	}

	@Override
	public void SelectResult(String sex) {
		user_sex.setText(sex);
	}

	@Override
	public void SelectSexType(int type) {
		sex_type = type;
		isChangeSex = true;
		tvsave.setVisibility(View.VISIBLE);
	}

}
