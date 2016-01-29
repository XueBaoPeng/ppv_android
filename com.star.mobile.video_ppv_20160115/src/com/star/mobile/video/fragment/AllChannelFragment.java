package com.star.mobile.video.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.star.cms.model.Category;
import com.star.cms.model.Package;
import com.star.cms.model.vo.ChannelVO;
import com.star.mobile.video.R;
import com.star.mobile.video.adapter.CategoryAdapter;
import com.star.mobile.video.adapter.ChannelListAdapter;
import com.star.mobile.video.adapter.PackageAdapter;
import com.star.mobile.video.base.BaseFragmentActivity;
import com.star.mobile.video.service.CategoryService;
import com.star.mobile.video.service.ChannelService;
import com.star.mobile.video.service.PackageService;
import com.star.mobile.video.util.LoadingDataTask;
import com.star.mobile.video.util.ToastUtil;
import com.star.mobile.video.view.NoScrollGridView;
import com.star.util.app.GA;
import com.star.util.loader.OnListResultListener;

public class AllChannelFragment extends Fragment implements OnClickListener, OnItemClickListener {
	private View mView;
	private BaseFragmentActivity channelGuideActivity;
	private NoScrollGridView pkgGroupList;
	private NoScrollGridView cgyGroupList;
	private NoScrollGridView chnGroupList;
	private ImageView chnFavIcon;
	private TextView chnFavText;
	private PackageService pkgService;
	private CategoryService cateService;
	private ChannelService chnService;
	private List<Category> categorys = new ArrayList<Category>();
	private List<Package> packages = new ArrayList<Package>();
	private List<ChannelVO> channels = new ArrayList<ChannelVO>();
	private PackageAdapter mPackageAdapter;
	private CategoryAdapter mCategoryAdapter;
	private ChannelListAdapter mChannelAdapter;
	private boolean isfav;
	private Package selectPkg = null;
	private Category selectCgy = null;
	private ChannelGuideFragment parentFragment;
	private boolean shown;
	
	public AllChannelFragment(ChannelGuideFragment parentFragment,BaseFragmentActivity activity) {
		this.parentFragment = parentFragment;
		channelGuideActivity = activity;
		init(LayoutInflater.from(channelGuideActivity));
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (mView != null) {
			ViewGroup parent = (ViewGroup) mView.getParent();
			if (parent != null) {
				parent.removeView(mView);
			}
			return mView;
		}
//		init(inflater);
		return mView;
	}
	
	private void init(LayoutInflater inflater) {
		mView = inflater.inflate(R.layout.fragment_allchannel, null);
		initView();
		pkgService = new PackageService(channelGuideActivity);
		cateService = new CategoryService(channelGuideActivity);
		chnService = new ChannelService(channelGuideActivity);
	}
	
	private void initView() {
		pkgGroupList = (NoScrollGridView) mView.findViewById(R.id.chn_package_group);
		cgyGroupList = (NoScrollGridView) mView.findViewById(R.id.chn_category_group);
		chnGroupList = (NoScrollGridView) mView.findViewById(R.id.chn_channel_group);
		chnFavIcon = (ImageView) mView.findViewById(R.id.chn_fav_icon);
		chnFavText = (TextView) mView.findViewById(R.id.chn_fav_text);
		mView.findViewById(R.id.ll_fav_layout).setOnClickListener(this);
		pkgGroupList.setOnItemClickListener(this);
		cgyGroupList.setOnItemClickListener(this);
		chnGroupList.setOnItemClickListener(this);
		mView.findViewById(R.id.v_allchannel_blank).setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				channelGuideActivity.hideAllchannelFragment();
				return true;
			}
		});
	}
	
	public void setShown(boolean isShown){
		this.shown = isShown;
	}
	
	public boolean isShown(){
		return shown;
	}
	
	public void loadingData(){
		new LoadingDataTask() {
			@Override
			public void onPreExecute() {
			}

			@Override
			public void onPostExecute() {
				if(packages != null){
					if(mPackageAdapter == null){
						mPackageAdapter = new PackageAdapter(channelGuideActivity, packages);
						pkgGroupList.setAdapter(mPackageAdapter);
					}else{
						mPackageAdapter.updateDataAndRefreshUI(packages);
					}
				}else{
					getChannelPackage();
				}
				if(categorys != null){
					if(mCategoryAdapter == null){
						mCategoryAdapter = new CategoryAdapter(channelGuideActivity, categorys);
						cgyGroupList.setAdapter(mCategoryAdapter);
					}else{
						mCategoryAdapter.updateDataAndRefreshUI(categorys);
					}
				}else{
					getCategory();	
				}
				getChannelsAndUpdateUI();
			}
			@Override
			public void doInBackground() {
				packages = pkgService.getPackages();
				categorys = cateService.getCategorys();
			}
		}.execute();
	}
	private void getCategory(){
		cateService.getCategorysFromServer(new OnListResultListener<Category>() {
			
			@Override
			public boolean onIntercept() {
				return false;
			}
			
			@Override
			public void onFailure(int errorCode, String msg) {
				
			}
			
			@Override
			public void onSuccess(List<Category> value) {
				categorys = value;
				if(categorys != null){
					if(mCategoryAdapter == null){
						mCategoryAdapter = new CategoryAdapter(channelGuideActivity, categorys);
						cgyGroupList.setAdapter(mCategoryAdapter);
					}else{
						mCategoryAdapter.updateDataAndRefreshUI(categorys);
					}
				}
			}
		});
	}
	private void getChannelPackage(){
		List<Integer> types = new ArrayList<Integer>();
		pkgService.getPackagesFromServer(types, new OnListResultListener<Package>() {

			@Override
			public boolean onIntercept() {
				return false;
			}

			@Override
			public void onFailure(int errorCode, String msg) {
				
			}

			@Override
			public void onSuccess(List<Package> value) {
				if(packages != null){
					if(mPackageAdapter == null){
						mPackageAdapter = new PackageAdapter(channelGuideActivity, packages);
						pkgGroupList.setAdapter(mPackageAdapter);
					}else{
						mPackageAdapter.updateDataAndRefreshUI(packages);
					}
				}
			}
		});
	}
	public void getChannelsAndUpdateUI(){
		new LoadingDataTask() {
			List<ChannelVO> chns;
			@Override
			public void onPreExecute() {
			}

			@Override
			public void onPostExecute() {
				if(chns != null){
					channels.clear();
					channels.addAll(chns);
					if(mChannelAdapter == null){
						mChannelAdapter = new ChannelListAdapter(channelGuideActivity, channels);
						chnGroupList.setAdapter(mChannelAdapter);
					}else{
						mChannelAdapter.updateDataAndRefreshUI(channels);
					}
				}
			}
			@Override
			public void doInBackground() {
				chns = chnService.getChannels(selectCgy, isfav, selectPkg);
			}
		}.execute();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_fav_layout:
			isfav = !isfav;
			if(isfav){
				chnFavIcon.setBackgroundResource(R.drawable.all_channel_icon_favorite_choose);
				chnFavText.setTextColor(channelGuideActivity.getResources().getColor(R.color.color_yellow));
			}else{
				chnFavIcon.setBackgroundResource(R.drawable.all_channel_icon_favorite);
				chnFavText.setTextColor(channelGuideActivity.getResources().getColor(R.color.onair_btn_unfocus));
			}
			getChannelsAndUpdateUI();
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		switch (parent.getId()) {
		case R.id.chn_package_group:
			Package p = packages.get(position);
			if(p.equals(selectPkg)){
				selectPkg = null;
				mPackageAdapter.setSelectPos(-1);
			}else{
				selectPkg = p;
				mPackageAdapter.setSelectPos(position);
			}
			getChannelsAndUpdateUI();
			break;
		case R.id.chn_category_group:
			Category c = categorys.get(position);
			if(c.equals(selectCgy)){
				selectCgy = null;
				mCategoryAdapter.setSelectPos(-1);
			}else{
				selectCgy = c;
				mCategoryAdapter.setSelectPos(position);
			}
			getChannelsAndUpdateUI();
			break;
		case R.id.chn_channel_group:
			ChannelVO chn = channels.get(position);
			if(selectCgy!=null){
				List<Category> categories = new ArrayList<Category>();
				categories.add(selectCgy);
				chn.setCategories(categories);
			}
			parentFragment.setCurrentChannel(chn);
			
			ToastUtil.showToast(channelGuideActivity, chn.getName());
			GA.sendEvent("EGP", "Discovery_EPG", chn.getName(), 1);
			channelGuideActivity.hideAllchannelFragment();
			break;
		default:
			break;
		}
	}
}
