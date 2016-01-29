package com.star.mobile.video.me;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.Profile;
import com.facebook.login.widget.ProfilePictureView;
import com.star.cms.model.Area;
import com.star.cms.model.User;
import com.star.cms.model.enm.AccountType;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.account.ChooseAreaActivity;
import com.star.mobile.video.account.ModifyMeActivity;
import com.star.mobile.video.activity.AlertListActicity;
import com.star.mobile.video.activity.TellFriendActivity;
import com.star.mobile.video.home.tab.TabFragment;
import com.star.mobile.video.me.about.AboutActivity;
import com.star.mobile.video.me.account.AccountAcitivity;
import com.star.mobile.video.me.coupon.ExchangeService;
import com.star.mobile.video.me.coupon.MyCouponsActivity;
import com.star.mobile.video.me.language.LanguageActivity;
import com.star.mobile.video.me.mycoins.MyCoinsActivity;
import com.star.mobile.video.me.mycoins.TaskService;
import com.star.mobile.video.me.notificaction.NotificationActivity;
import com.star.mobile.video.model.FunctionType;
import com.star.mobile.video.model.MenuItem;
import com.star.mobile.video.service.EggAppearService;
import com.star.mobile.video.service.FunctionService;
import com.star.mobile.video.service.UserService;
import com.star.mobile.video.service.UserService.CallbackListener;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.shared.TaskSharedUtil;
import com.star.mobile.video.smartcard.SmartCardControlActivity;
import com.star.mobile.video.tenb.TenbActivity;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.LoadingDataTask;
import com.star.ui.ImageView.Finisher;
import com.star.util.loader.OnResultListener;

public class MeFragment extends TabFragment implements OnClickListener {

	private UserService userService;
	private View mView;
	private View headerView;
	private View footerView;
	// private HomeActivity homeActivity;
	private List<MenuItem<MeMenuItemRes>> meItems;
	private User mUser;
	private MeMenuItemRes myCoins;
	private ItemAdapter mAdapter;
	private MeMenuItemRes myCoupons;
	private MeMenuItemRes mInvitationCode;
	private MeMenuItemRes mReminders;
	private TextView user_name;
	private TextView user_nick;
	private ImageView btEdit;
	private com.star.ui.ImageView user_header;
	private LinearLayout accountInfo;
	private TextView tvLogin;
	private ImageView loginPlatfromLogo;
	private ListView lvMeGroup;
	private ProfilePictureView mFacebookProfilePictureView;
	private TaskService taskService;
	ExchangeService exchangeService;
	public static boolean isBoundCard;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// homeActivity = StarApplication.mHomeActivity;
		if (mView != null) {
			ViewGroup parent = (ViewGroup) mView.getParent();
			if (parent != null) {
				parent.removeView(mView);
			}
			return mView;
		}
		userService = new UserService();
		taskService = new TaskService(getActivity());
		exchangeService=new ExchangeService(getActivity());
		mView = inflater.inflate(R.layout.fragment_me, null);
		headerView = inflater.inflate(R.layout.view_me_header, null);
		footerView = inflater.inflate(R.layout.view_me_foot, null);
		initItems();
		initView();
		EggAppearService.appearEgg(getActivity(), EggAppearService.Profile);
		return mView;
	}

	@Override
	public void onStart() {
		// homeActivity.setCurrentFragmentTag(getActivity().getResources().getString(R.string.fragment_tag_Me));
		super.onStart();
	}

	@Override
	public void onResume() {
		getUserInfo();
		getMyCouponStatus();
//		getCoinsStatus();
		super.onResume();
	}
	
	public void updateList(){
		if(mAdapter!=null)
			mAdapter.notifyDataSetChanged();
	}

	private void getUserInfo() {
		userService.setCallbackListener(new CallbackListener() {
			@Override
			public void callback(User u) {
				if (u == null) {
					return;
				}
				mUser = u;
				initUserInfo();
			}
		});
		userService.getUser(getActivity(), false);
	}

	private void initUserInfo() {
		if (mUser == null)
			return;
		if(myCoins!=null)
			myCoins.setItemContent(mUser.getCoins() + "");
		// if(AccountType.Facebook.equals(mUser.getType())){
		//// btEdit.setOnClickListener(null);
		// }
		mAdapter.notifyDataSetChanged();
		
		if (mUser.getHead() != null) {
			AccountType at = mUser.getType();
			if (AccountType.Facebook.equals(at) || AccountType.Twitter.equals(at)) {
				Profile profile = Profile.getCurrentProfile();
				user_header.setVisibility(View.GONE);
				mFacebookProfilePictureView.setVisibility(View.VISIBLE);
				mFacebookProfilePictureView.setProfileId(profile.getId());
//				user_header.setUrl(mUser.getHead(), false);// 用户头像不需要格式化url
															// twitter 和
															// facebook
															// 头像地址都是特殊的
			} else {
				user_header.setVisibility(View.VISIBLE);
				mFacebookProfilePictureView.setVisibility(View.GONE);
				user_header.setUrl(mUser.getHead());
			}
		}

		if (mUser.getUserName() == null) {
			accountInfo.setVisibility(View.INVISIBLE);
			tvLogin.setVisibility(View.VISIBLE);
		} else {
			accountInfo.setVisibility(View.VISIBLE);
			tvLogin.setVisibility(View.GONE);
			String userName = mUser.getUserName();
			if (userName.contains(User.PrefixOfUsr3Party)) {
				user_name.setText("");
				AccountType userType = mUser.getType();
				if (userType != null) {
					loginPlatfromLogo.setVisibility(View.VISIBLE);
					if (userType == AccountType.Facebook) {
						loginPlatfromLogo.setBackgroundResource(R.drawable.facebook_me);
					} else if (userType == AccountType.Twitter) {
						loginPlatfromLogo.setBackgroundResource(R.drawable.twitter_me);
					}
				}
			} else {
				AccountType userType = mUser.getType();
				if (userType != null) {
					if (userType == AccountType.PhoneNumber) {
						String uName = null;
						if (Area.SOUTHAFRICA_CODE.equals(SharedPreferencesUtil.getAreaCode(getActivity()))) {
							uName = userName.substring(2, userName.length());
						} else {
							uName = userName.substring(3, userName.length());
						}
//						StringBuilder builder=new StringBuilder();
//						for(int i=0;i<uName.length();i++){
//							if(i<5){
//								builder.append("×");
//								continue;
//							}
//							builder.append(uName.charAt(i));
//						}
						user_name.setText(uName);
					} else {
						user_name.setText(userName);
					}
				} else {
					user_name.setText(userName);
				}
			}
			user_nick.setText(mUser.getNickName());
		}
	}

	private void initItems() {
		meItems = new ArrayList<MenuItem<MeMenuItemRes>>();
		// my tenbre play begin
		MeMenuItemRes tenbreRes = new MeMenuItemRes(getString(R.string.my_tenbre));
		tenbreRes.setUnfocusRes(R.drawable.icon_mytenbreplay);
		List<MeMenuItemRes> tenbreItem = new ArrayList<MeMenuItemRes>();
		// my tenb
		MeMenuItemRes mytenb = new MeMenuItemRes(getString(R.string.tenb), TenbActivity.class);
		tenbreItem.add(mytenb);
		
		/*简版没有mycoins和mycoupons*/
		if(!FunctionService.doHideFuncation(FunctionType.SimpleVersion)){
			//my coin
			myCoins = new MeMenuItemRes(getString(R.string.mycoins),MyCoinsActivity.class);
			tenbreItem.add(myCoins);
			// my conoup
			myCoupons = new MeMenuItemRes(getString(R.string.my_coupons), MyCouponsActivity.class);
			tenbreItem.add(myCoupons);
		}
		/*
		 * //Invitation code mInvitationCode = new
		 * MeMenuItemRes(getString(R.string.invitation_code),InvitationActivity.
		 * class); tenbreItem.add(mInvitationCode);
		 */
		// MyReminders
		mReminders = new MeMenuItemRes(getString(R.string.MyReminders), AlertListActicity.class);
		tenbreItem.add(mReminders);

		MenuItem<MeMenuItemRes> myTenbre = new MenuItem<MeMenuItemRes>(tenbreRes, tenbreItem);
		meItems.add(myTenbre);
 
		//my tenbre play end
		
		//account manager
		if(!FunctionService.doHideFuncation(FunctionType.SimpleVersion)){
//			MeMenuItemRes accountRes = new MeMenuItemRes(getString(R.string.fragment_tag_ccount_manager),AccountManagerActivity.class);
			MeMenuItemRes accountRes = new MeMenuItemRes(getString(R.string.fragment_tag_ccount_manager),SmartCardControlActivity.class);
			accountRes.setUnfocusRes(R.drawable.ic_account);
			MenuItem<MeMenuItemRes> accountManager = new MenuItem<MeMenuItemRes>(accountRes);
			meItems.add(accountManager);
			/*
			 * //TellFriend MeMenuItemRes tellRes = new
			 * MeMenuItemRes(getString(R.string.invititions),TellFriendActivity.
			 * class); tellRes.setUnfocusRes(R.drawable.ic_share);
			 * MenuItem<MeMenuItemRes> tellFriend = new
			 * MenuItem<MeMenuItemRes>(tellRes); meItems.add(tellFriend);
			 */
			// TellFriend
			MeMenuItemRes tellRes = new MeMenuItemRes(getString(R.string.invititions), TellFriendActivity.class);
			tellRes.setUnfocusRes(R.drawable.ic_share);
			MenuItem<MeMenuItemRes> tellFriend = new MenuItem<MeMenuItemRes>(tellRes);
			meItems.add(tellFriend);
		}

		// setting
		MeMenuItemRes settingRes = new MeMenuItemRes(getString(R.string.setting));
		List<MeMenuItemRes> settingItem = new ArrayList<MeMenuItemRes>();
		MeMenuItemRes account = new MeMenuItemRes(getString(R.string.account), AccountAcitivity.class);
		settingItem.add(account);

		MeMenuItemRes notification = new MeMenuItemRes(getString(R.string.notification), NotificationActivity.class);
		settingItem.add(notification);
		
		MeMenuItemRes language=new MeMenuItemRes(getString(R.string.language),LanguageActivity.class);
		settingItem.add(language);
		
		MeMenuItemRes about = new MeMenuItemRes(getString(R.string.about), AboutActivity.class);
		settingItem.add(about);

		settingRes.setUnfocusRes(R.drawable.ic_settings);
		MenuItem<MeMenuItemRes> setting = new MenuItem<MeMenuItemRes>(settingRes, settingItem);
		meItems.add(setting);
	}

	private void initView() {
		lvMeGroup = (ListView) mView.findViewById(R.id.lv_me_group);
		initHeaderView();
		lvMeGroup.addHeaderView(headerView);
		lvMeGroup.addFooterView(footerView);
		mAdapter = new ItemAdapter(getActivity(), meItems);
		lvMeGroup.setAdapter(mAdapter);
		initScrollListener(lvMeGroup);
	}

	private void initHeaderView() {
		accountInfo = (LinearLayout) headerView.findViewById(R.id.rl_account_info);
		user_name = (TextView) headerView.findViewById(R.id.tv_user_name);
		user_nick = (TextView) headerView.findViewById(R.id.tv_user_nick);
		btEdit = (ImageView) headerView.findViewById(R.id.iv_edit);
		tvLogin = (TextView) headerView.findViewById(R.id.tv_login);
		mFacebookProfilePictureView = (ProfilePictureView) headerView.findViewById(R.id.facebook_profile_picture);
		mFacebookProfilePictureView.setOnClickListener(this);
		loginPlatfromLogo = (ImageView) headerView.findViewById(R.id.iv_login_platform_logo);
		tvLogin.setOnClickListener(this);
		user_header = (com.star.ui.ImageView) headerView.findViewById(R.id.iv_user_header);
		user_header.setFinisher(new Finisher() {

			@Override
			public void run() {
				// user_header.setImageBitmap(ImageUtil.getCircleBitmap(user_header.getImage()));
			}
		});
		btEdit.setOnClickListener(this);
		user_header.setOnClickListener(this);
		// return headerView;
	}

	public void getMyCouponStatus() {
		if(myCoupons==null)
			return;
		exchangeService.getExchengNoReceiveNumber(new OnResultListener<Integer>() {
			Integer number;
			@Override
			public void onSuccess(Integer value) {
				if(value!=null){
					number=value;
				}
				if (!isAdded())
					return;
				TaskSharedUtil.setCouponsStatus(getActivity(), false);
				if (number != null) {
					myCoupons.setItemContent(number + "");
					if (number > 0) {
						myCoupons.setFoucs(true);
					} else {
						myCoupons.setFoucs(false);
					}
					mAdapter.notifyDataSetChanged();
				}
			}
			
			@Override
			public boolean onIntercept() {
				return false;
			}
			
			@Override
			public void onFailure(int errorCode, String msg) {
				
			}
		});
	}

	/*public void getCoinsStatus() {
		taskService.getTasks(ApplicationUtil.getAppVerison(getActivity()), new OnListResultListener<TaskVO>() {
			
			@Override
			public boolean onIntercept() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void onFailure(int errorCode, String msg) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onSuccess(List<TaskVO> datas) {
				if (!isAdded())
					return;
				if (datas != null && datas.size() > 0) {
					// 用户是否有可领取的积分
					TaskSharedUtil.setCoinsStatus(getActivity(), false);
					for (TaskVO t : datas) {
						if (t.getCoins() != 0) {
							TaskSharedUtil.setCoinsStatus(getActivity(), true);
							break;
						}
					}
					mAdapter.notifyDataSetChanged();
				}
			}
		});
	}*/

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.facebook_profile_picture:
		case R.id.iv_user_header:
		case R.id.iv_edit:
			//CommonUtil.startActivity(getActivity(), ModifyMeActivity.class);
			 
			   if(SharedPreferencesUtil.getUserName(getActivity())!=null &&
			   		mUser!=null) { 
				   CommonUtil.startActivity(getActivity(),ModifyMeActivity.class); 
			   } else {
				   CommonUtil.pleaseLogin(getActivity()); } 
			break;
		case R.id.tv_login:
			StarApplication.mUser = null;
			if (SharedPreferencesUtil.getUserName(getActivity()) != null) {
				CommonUtil.startActivity(getActivity(), ChooseAreaActivity.class);
			} else {
				CommonUtil.pleaseLogin(getActivity());
			}
			break;

		default:
			break;
		}
	}
}
