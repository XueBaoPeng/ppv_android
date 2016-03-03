package com.star.mobile.video.account;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.star.cms.model.Area;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.home.HomeActivity;
import com.star.mobile.video.service.SyncService;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.ApplicationUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.view.ListView;
import com.star.ui.ImageView;
import com.star.util.loader.OnListResultListener;
import com.star.util.loader.OnResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChooseAreaActivity extends BaseActivity {

	private List<Area> areas = new ArrayList<Area>();
	private AreaService areaService;
	private TextView tv_area_name;
	private ImageView image_area_flag;
	private ImageView image_area_map;
	private ListView lvAreas;
	private LinearLayout item_layout;
	private Area purrentArea ;//当前的地区
	private String localAreaCode;
	private android.widget.ImageView place_image;
	private TextView tv_isLoading;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StarApplication.mChooseAreaActivity = this;
		setContentView(R.layout.activity_choose_areas);
		TextView tvTitle = (TextView) findViewById(R.id.tv_actionbar_title);
		tvTitle.setText(getString(R.string.choose_country));
		lvAreas = (com.star.mobile.video.view.ListView)findViewById(R.id.lv_areas);
		tv_area_name= (TextView) findViewById(R.id.iv_area_name);
		image_area_flag= (ImageView) findViewById(R.id.iv_area_flag);
		image_area_map= (ImageView) findViewById(R.id.iv_area_map);
		item_layout= (LinearLayout) findViewById(R.id.mabe_item_layout);
		place_image= (android.widget.ImageView) findViewById(R.id.place_image);
		tv_isLoading= (TextView) findViewById(R.id.tv_isLoading);
		lvAreas.setOnItemClickListener(itemClickListener);
		areaService = new AreaService(this);
		mAdapter = new AreasAdapter();
		lvAreas.setAdapter(mAdapter);
		getAreas(lvAreas);
		item_layout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setOnclick();
			}
		});
	}

	private void setAnimatinon(){
		Animation translateAnimation = new TranslateAnimation(0.0f, 0.0f,0.f,10.0f);
		translateAnimation.setDuration(500);
		translateAnimation.setRepeatCount(-1);
		place_image.setAnimation(translateAnimation);
	}
	private void setOnclick(){
		String areaCode = SharedPreferencesUtil.getAreaCode(ChooseAreaActivity.this);
		if(purrentArea!=null){
			if(areaCode==null || !areaCode.equals(purrentArea.getCode())) {
				SyncService.getInstance(ChooseAreaActivity.this).doResetStatus(null, purrentArea.getId());
				SharedPreferencesUtil.saveArea(ChooseAreaActivity.this, purrentArea);
			}
			CommonUtil.startActivity(ChooseAreaActivity.this, LoginActivity.class);
		}else{
			return;
		}

	}
	/**
	 *动态设置listview的高度
	 */
	private void setListViewHeight(){
		int totalHeight = 0;
		for (int i = 0; i < mAdapter.getCount(); i++) {
			View listItem = mAdapter.getView(i, null, lvAreas);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = lvAreas.getLayoutParams();
		params.height = totalHeight
				+ (lvAreas.getDividerHeight() * (lvAreas.getCount() - 1));
		lvAreas.setLayoutParams(params);
	}
	private void loadPlaceByIpcode(){

		areaService.getAreaCode(new OnResultListener<String>() {
			@Override
			public boolean onIntercept() {
				setAnimatinon();
				return false;
			}

			@Override
			public void onSuccess(String value) {
				/*
		 * 通过定位回来的地区，跟新当前位置*/
				Area area=null;
				if(value!=null){
					try {
						localAreaCode=new JSONObject(value).getString("code");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					for (int i=0;i<areas.size();i++){
						area=areas.get(i);
						if(area.getCode().equals(localAreaCode)){
							setMaybeOpention(area);
							purrentArea=area;
						}
					}
					if(purrentArea.getCode().equals("8")){
						setMaybeOpention(purrentArea);
					}else{
						return;
					}

				}else{
					setMaybeOpention(null);
				}
			}
			@Override
			public void onFailure(int errorCode, String msg) {
				//定位失败
				setMaybeOpention(null);
			}
		});

	}

	/**
	 * 初始化位置界面
	 * @param area
	 */
	private void setMaybeOpention(Area area){
		if(place_image==null||place_image.getAnimation()==null)
			return;
		if (area==null){
			tv_isLoading.setText(R.string.request_fail);
				place_image.getAnimation().cancel();
			return;
		}
		place_image.getAnimation().cancel();
		item_layout.setVisibility(View.VISIBLE);
		tv_area_name.setText(area.getName());
		image_area_map.setImageDrawable(null);
		image_area_flag.setImageDrawable(null);
		try{
			if(!TextUtils.isEmpty(area.getCountryMap())){
				image_area_map.setUrl(area.getCountryMap());
				image_area_map.setVisibility(View.VISIBLE);
			}else{
				image_area_map.setVisibility(View.GONE);
			}

		}catch (Exception e) {
		}
		try{
			if(!TextUtils.isEmpty(area.getNationalFlag())){
				image_area_flag.setUrl(area.getNationalFlag());
				image_area_flag.setVisibility(View.VISIBLE);
			}else{
				image_area_flag.setVisibility(View.GONE);
			}
		}catch (Exception e) {

		}

	}
	private void getAreas(ListView listview) {
		CommonUtil.showProgressDialog(ChooseAreaActivity.this, null, getResources().getString(R.string.loading_region_information));
		areaService.getAreas(ApplicationUtil.getAppVerison(ChooseAreaActivity.this), new OnListResultListener<Area>() {
			@Override
			public void onSuccess(List<Area> responseDatas) {
				CommonUtil.closeProgressDialog();
				if (responseDatas != null && responseDatas.size() > 0) {
					areas.clear();
					areas.addAll(responseDatas);
					mAdapter.notifyDataSetChanged();
					setCurrentArea();
					loadPlaceByIpcode();
					setListViewHeight();

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

	/**
	 * 设置默认的国家
	 */
	private void setCurrentArea(){
		for (Area area:areas){
			if (area.getCode().equals("8")){
				purrentArea=area;
			}
		}
	}
	
	private void goHomeActivity() {
		Intent intent = new Intent(this,HomeActivity.class);
		intent.putExtra("fragment", getString(R.string.fragment_tag_Home));
		CommonUtil.startActivity(this, intent);
		finish();
	}
	
	OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Area area = areas.get(position);
			String areaCode = SharedPreferencesUtil.getAreaCode(ChooseAreaActivity.this);
			if(areaCode==null || !areaCode.equals(area.getCode())) {
				SyncService.getInstance(ChooseAreaActivity.this).doResetStatus(null, area.getId());
				SharedPreferencesUtil.saveArea(ChooseAreaActivity.this, area);
			}
			CommonUtil.startActivity(ChooseAreaActivity.this, LoginActivity.class);
		}
	};
	private AreasAdapter mAdapter;
	
	class AreasAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return areas.size();
		}

		@Override
		public Object getItem(int position) {
			return areas.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if(convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(ChooseAreaActivity.this).inflate(R.layout.view_areas_item, null);
				viewHolder.areaName = (TextView) convertView.findViewById(R.id.iv_area_name);
				viewHolder.areaMap = (com.star.ui.ImageView) convertView.findViewById(R.id.iv_area_map);
				viewHolder.areaFlag = (ImageView) convertView.findViewById(R.id.iv_area_flag);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder)convertView.getTag();
			}
			Area area = areas.get(position);
			viewHolder.areaName.setText(area.getName());
			viewHolder.areaMap.setImageDrawable(null);
			viewHolder.areaFlag.setImageDrawable(null);
			try{
				if(!TextUtils.isEmpty(area.getCountryMap())){
//					viewHolder.areaMap.setImageURI(Uri.parse(area.getCountryMap()));
 			       viewHolder.areaMap.setUrl(area.getCountryMap());
					viewHolder.areaMap.setVisibility(View.VISIBLE);
				}else{
					viewHolder.areaMap.setVisibility(View.GONE);
				}
					
			}catch (Exception e) {
			}
			try{
				if(!TextUtils.isEmpty(area.getNationalFlag())){
//					viewHolder.areaFlag.setImageURI(Uri.parse(area.getNationalFlag()));
					viewHolder.areaFlag.setUrl(area.getNationalFlag());
					viewHolder.areaFlag.setVisibility(View.VISIBLE);
				}else{
					viewHolder.areaFlag.setVisibility(View.GONE);
				}
			}catch (Exception e) {
				
			}
			return convertView;
		}
		
		class ViewHolder{
			TextView areaName;
//			SimpleDraweeView areaMap;
//			SimpleDraweeView areaFlag;
 			com.star.ui.ImageView areaMap;
			com.star.ui.ImageView areaFlag;
		}
	}
}
