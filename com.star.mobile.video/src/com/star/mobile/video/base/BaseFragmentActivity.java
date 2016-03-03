package com.star.mobile.video.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.star.cms.model.vo.ChannelVO;
import com.star.mobile.video.R;
import com.star.mobile.video.fragment.AllChannelFragment;
import com.star.mobile.video.fragment.ChannelGuideFragment;
import com.star.mobile.video.fragment.EpgDetailFragment;
import com.star.mobile.video.model.FunctionType;
import com.star.mobile.video.model.MenuHandle;
import com.star.mobile.video.search.SearchActivity;
import com.star.mobile.video.service.FunctionService;
import com.star.mobile.video.service.ProgramService;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.MaskUtil;

public class BaseFragmentActivity extends FragmentActivity implements OnClickListener {
	
	private FragmentManager fragmentManager;
	
	private TextView tv_title;
	private ImageView iv_logo;
	private ImageView iv_search;
	private ImageView iv_back;
	private ImageView iv_menu;
	
	
	public static boolean breakKeyStatus = false;
	private String tag_channelGuide;
	private String tag_epgDetail;
	private String tag_allchannel;
	private String tag_onair;
	private String tag_fragment;
	private String tag_video;
	private Fragment currFragment;
	private boolean isFromTask;
	
	public AllChannelFragment mAllChannelFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.channel_guide_activity);
		tag_onair = getString(R.string.fragment_tag_onAir);
		tag_channelGuide = getString(R.string.fragment_tag_channelGuide);
		tag_epgDetail = getString(R.string.fragment_tag_epgDetail);
		tag_allchannel = getString(R.string.activity_allchannels_title);
		tag_video = getString(R.string.fragment_tag_video);
		tag_fragment = tag_channelGuide;
		
		tv_title = (TextView) findViewById(R.id.tv_actionbar_title);
		iv_logo = (ImageView)findViewById(R.id.iv_actionbar_logo);
		iv_search = (ImageView) findViewById(R.id.iv_actionbar_search);
		iv_back = (ImageView) findViewById(R.id.iv_actionbar_back);
		setSearchIcon(R.drawable.actionbar_search);
		iv_search.setOnClickListener(this);
		findViewById(R.id.iv_actionbar_menu).setOnClickListener(this);
		
		iv_menu = ((ImageView) findViewById(R.id.iv_actionbar_menu));
		iv_menu.setOnClickListener(this);
		iv_back.setOnClickListener(this);
		fragmentManager = getSupportFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		currFragment = new ChannelGuideFragment();
		transaction.replace(R.id.home_container, currFragment, getString(R.string.fragment_tag_Home)).commit();
		
		new ProgramService(this).setOutLinePrograms();
		replaceFragmentByTag(getIntent());
		
	}
	
	
	private void replaceFragmentByTag(Intent intent) {
		String tag = intent.getStringExtra("fragment");
		Fragment fragment = null;
		if(tag!=null){
			fragment = setFragmentByTag(tag);
		}
		if(fragment!=null){
			setCurrentFragmentTag(tag);
		}
		ChannelVO channel = (ChannelVO) intent.getSerializableExtra("channel");
		if(channel!=null && fragment!=null && fragment instanceof ChannelGuideFragment){
			((ChannelGuideFragment)fragment).setCurrentChannel(channel);
		}
		long id = 0;
		try{
			id = Long.parseLong(intent.getStringExtra("id"));
		}catch (Exception e) {
			id = intent.getLongExtra("id", 0);
		}
		if(id != 0){
			if(fragment!=null&&fragment instanceof ChannelGuideFragment){
				((ChannelGuideFragment)fragment).setCurrentChannel(id);
			}else if(fragment!=null && fragment instanceof EpgDetailFragment){
				((EpgDetailFragment)fragment).setCurrentProgram(id);
			}
		}
	}		
	public boolean isFromTask(){
		return isFromTask;
	}
	
	public void setNotFromTask(long delayTime){
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				isFromTask = false;
			}
		}, delayTime);
	}
	
	public void setNotFromTask(){
		setNotFromTask(0);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		if(FunctionService.doHideFuncation(FunctionType.SimpleVersion)){
			hideSearchIcon();
		}else{
			showSearchIcon();
		}
	}
	
	private void setTitleText(String text){
		 if(tag_allchannel.equals(text)){
			iv_logo.setImageDrawable(MenuHandle.getFragmentLogo(tag_channelGuide));
			iv_logo.setVisibility(View.VISIBLE);
		}else{
			tv_title.setText(text);
		}
	}
	
	public void setCurrentFragmentTag(String tag){
		tag_fragment = tag;
		if(tag.equals(getString(R.string.tag_video_list))){
			tag = getString(R.string.fragment_tag_video);
		}
		if(MenuHandle.getFragmentLogo(tag) == null){
			iv_logo.setVisibility(View.GONE);
		}else{
			iv_logo.setImageDrawable(MenuHandle.getFragmentLogo(tag));
			iv_logo.setVisibility(View.VISIBLE);
		}
		setTitleText(tag);
		if(tag.equals(tag_channelGuide) || tag.equals(tag_allchannel) || tag.equals(tag_video)){
			iv_back.setVisibility(View.VISIBLE);
		}else{
			if(iv_back.getVisibility()==View.VISIBLE){
				iv_back.setVisibility(View.GONE);
			}
		}
		if(!tag.equals(tag_allchannel)){
			removeAllChannelFragment();
			if(findViewById(R.id.rl_allchn_guide).getVisibility()==View.VISIBLE)
				findViewById(R.id.rl_allchn_guide).setVisibility(View.GONE);
		}
	}
	
	public String getCurrentFragmentTag(){
		return tag_fragment;
	}
	
	public void closeKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
	}
	
	
	public void showLoadingIcon(){
		findViewById(R.id.pb_title_loading).setVisibility(View.VISIBLE);
	}
	
	public void hideLoadingIcon(){
		findViewById(R.id.pb_title_loading).setVisibility(View.GONE);
	}
	
	public void hideSearchIcon() {
		findViewById(R.id.ll_search).setVisibility(View.GONE);
	}
	
	public void showSearchIcon() {
		findViewById(R.id.ll_search).setVisibility(View.VISIBLE);
	}
	
	public void setSearchIcon(int resId) {
		iv_search.setImageResource(resId);
	}
	
	public void setSearchOnClick(OnClickListener l) {
		iv_search.setOnClickListener(l);
	}
	
	public void popBackStack(){
		fragmentManager.popBackStack();
	}
	
	public Fragment setFragmentByTag_Task(String tag){
		Fragment fragment = setFragmentByTag(tag);
		isFromTask = true;
		if(tag.equals(tag_channelGuide)){
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					isFromTask = true;
					MaskUtil.showChannelGuideFrame(BaseFragmentActivity.this);
				}
			}, 800);
		}else if(tag.equals(tag_epgDetail)){
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					isFromTask = true;
					MaskUtil.showEpgAlertFrame(BaseFragmentActivity.this);
				}
			}, 800);
		}
		return fragment;
	}
	
	public Fragment setFragmentByTag(String tag) {
		isFromTask = false;
		if(tag_fragment.equals(tag) && ! tag_fragment.equals(getString(R.string.fragment_tag_epgDetail))){
			return currFragment;
		}
		tag_fragment = tag;
		try {
			FragmentTransaction transaction = fragmentManager.beginTransaction();
			Fragment fragment = (Fragment) MenuHandle.getMenuItemClass(tag).getConstructor().newInstance();
			transaction.setCustomAnimations(R.anim.tran_next_in, R.anim.tran_next_out);
			transaction.replace(R.id.home_container, fragment);
			transaction.addToBackStack(null);
			transaction.commitAllowingStateLoss();
			return fragment;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void setFragment(Fragment fragment) {
		isFromTask = false;
		try {
			FragmentTransaction transaction = fragmentManager.beginTransaction();
			transaction.replace(R.id.home_container, fragment);
			transaction.addToBackStack(null);
			transaction.commitAllowingStateLoss();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_actionbar_search:
			CommonUtil.startActivity(this, SearchActivity.class);
			break;
		case R.id.iv_actionbar_menu:
			onBackPressed();
			break;
		case R.id.iv_actionbar_back:
			if(mAllChannelFragment.isShown()){
				hideAllchannelFragment();
			}else{
				showAllchannelFragment();
			}
			break;
		default:
			break;
		}
	}
	
	public boolean hideAllchannelFragment(){
		if(mAllChannelFragment==null || !mAllChannelFragment.isShown())
			return false;
		try{
			FragmentTransaction transaction = fragmentManager.beginTransaction();
			transaction.setCustomAnimations(R.anim.tran_pre_in, R.anim.tran_pre_out);
			transaction.remove(mAllChannelFragment).commit();
		}catch (Exception e) {
			e.printStackTrace();
		}
		iv_back.setImageResource(R.drawable.channel_back);
		setCurrentFragmentTag(getString(R.string.fragment_tag_channelGuide));
		MaskUtil.showChatroomOnTVGuideFrame(BaseFragmentActivity.this);
		return true;
	}
	
	public void showAllchannelFragment(){
		if(!(getCurrentFragmentTag().equals(getString(R.string.fragment_tag_channelGuide)) || getCurrentFragmentTag().equals(getString(R.string.tag_video_list))))
			return;
		if(mAllChannelFragment.isAdded())
			return;
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if(mAllChannelFragment.isShown())
					MaskUtil.showAllChannelFrame(BaseFragmentActivity.this);
			}
		}, 500);
		try{
			FragmentTransaction transaction = fragmentManager.beginTransaction();
			transaction.setCustomAnimations(R.anim.tran_next_in_allchn, R.anim.tran_next_out_allchn);
			transaction.add(R.id.other_container, mAllChannelFragment).commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		mAllChannelFragment.setShown(true);
		iv_back.setImageResource(R.drawable.back_right);
	}
	
	
	public void removeAllChannelFragment(){
		if(mAllChannelFragment != null && mAllChannelFragment.isShown()){
			fragmentManager.beginTransaction().remove(mAllChannelFragment).commit();
			iv_back.setImageResource(R.drawable.channel_back);
			mAllChannelFragment.setShown(false);
		}
	}
	
	@Override
	public void onBackPressed() {
		CommonUtil.finishActivityAnimation(this);
		super.onBackPressed();
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean intercept = false;
	    if(keyCode == KeyEvent.KEYCODE_BACK){
	    	if(tag_fragment.equals(tag_channelGuide)){
	    		if(isFromTask)
	    			intercept = MaskUtil.hideMask(this, Constant.GUIDE_EPGALERT, true);
	    		else
	    			intercept = MaskUtil.hideMask(this, Constant.GUIDE_CHATROOM, true);
	    	}else if(tag_fragment.equals(tag_epgDetail) && isFromTask){
	    		intercept = MaskUtil.hideMask(this, Constant.GUIDE_EPGALERT, true);
	    	}else if(tag_fragment.equals(tag_allchannel)){
	    		intercept = MaskUtil.hideMask(this, Constant.GUIDE_ALLCHN, true);
	    		try{
	    			if(mAllChannelFragment!=null && mAllChannelFragment.isShown()){
	    				removeAllChannelFragment();
	    				setCurrentFragmentTag(tag_channelGuide);
	    				return true;
	    			}
	    		}catch(Exception e){
	    		}
	    	}
	    }
	    if(intercept)
	    	return true;
	    return super.onKeyDown(keyCode, event);
	}

}
