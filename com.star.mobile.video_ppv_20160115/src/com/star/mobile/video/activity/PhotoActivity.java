package com.star.mobile.video.activity;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.star.mobile.video.R;
import com.star.mobile.video.account.HeadviewUploadActivity;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.model.PhotoAdapter;

public class PhotoActivity extends BaseActivity implements OnClickListener {

	private GridView gv_pics;
	private PhotoAdapter mAdapter;
	private File mImgDir;
	/**
	 * 所有的图片
	 */
	private List<String> mImgs;
	private int thisposition;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album);
		((TextView) findViewById(R.id.tv_actionbar_title)).setText(getString(R.string.photo));
		findViewById(R.id.iv_actionbar_back).setOnClickListener(this);
		findViewById(R.id.iv_actionbar_search).setVisibility(View.VISIBLE);
		((ImageView) findViewById(R.id.iv_actionbar_search)).setImageResource(R.drawable.button_confirm);
		findViewById(R.id.iv_actionbar_search).setOnClickListener(this);
		gv_pics = (GridView) findViewById(R.id.gv_image_group);
		mImgDir = new File(getIntent().getStringExtra("localUrl"));
		mImgs = Arrays.asList(mImgDir.list());
		mAdapter = new PhotoAdapter(getApplicationContext(), mImgs,
		R.layout.view_album_item, mImgDir.getAbsolutePath());
		gv_pics.setAdapter(mAdapter);
		gv_pics.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				mAdapter.changeState(position);
				thisposition=position;
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_actionbar_back:
			super.onBackPressed();
			break;
		case R.id.iv_actionbar_search:
			 Intent intent = new Intent(PhotoActivity.this, HeadviewUploadActivity.class);
			 intent.putExtra("localUrl", mImgDir.getAbsolutePath()+"/"+mImgs.get(thisposition));
			 startActivityForResult(intent, 200);
			break;
		default:
			break;
		}
	}

}
