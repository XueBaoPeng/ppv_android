package com.star.mobile.video.account;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.star.cms.model.Area;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.home.HomeActivity;
import com.star.mobile.video.service.AreaService;
import com.star.mobile.video.service.SyncService;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.ApplicationUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.view.ListView;
import com.star.mobile.video.view.ListView.LoadingListener;
import com.star.ui.ImageView;

import java.util.ArrayList;
import java.util.List;

public class ChooseAreaActivity extends BaseActivity {

	private List<Area> areas = new ArrayList<Area>();
	private AreaService areaService;
	private TextView tv_area_name;
	private ImageView image_area_flag;
	private ImageView image_area_map;
	private ListView lvAreas;
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
		lvAreas.setOnItemClickListener(itemClickListener);
		areaService = new AreaService(this);
		mAdapter = new AreasAdapter();
		lvAreas.setAdapter(mAdapter);
		getAreas(lvAreas);
	}
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
	private void loadPlace(){
		Area area=areas.get(0);
		tv_area_name.setText(areas.get(0).getName());
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
		listview.setRequestCount(20);
		listview.setLoadingListener(new LoadingListener<Area>() {

			@Override
			public List<Area> loadingS(int offset, int requestCount) {
				return areaService.getAreas(ApplicationUtil.getAppVerison(ChooseAreaActivity.this));
			}

			@Override
			public void loadPost(List<Area> responseDatas) {
				CommonUtil.closeProgressDialog();
				if(responseDatas != null && responseDatas.size() > 0) {
					areas.addAll(responseDatas);
					mAdapter.notifyDataSetChanged();
					loadPlace();
					setListViewHeight();
				}
			}

			@Override
			public List<Area> loadingL(int offset, int requestCount) {
				return areaService.getAreasFromLocal(ChooseAreaActivity.this,ApplicationUtil.getAppVerison(ChooseAreaActivity.this));
			}

			@Override
			public List<Area> getFillList() {
				return areas;
			}

			@Override
			public void onNoMoreData() {

			}
		});
		CommonUtil.showProgressDialog(ChooseAreaActivity.this, null, getResources().getString(R.string.loading_region_information));
		listview.loadingData(true);
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
