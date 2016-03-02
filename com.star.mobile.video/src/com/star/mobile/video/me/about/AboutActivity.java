package com.star.mobile.video.me.about;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.star.mobile.video.R;
import com.star.mobile.video.activity.BrowserActivity;
import com.star.mobile.video.appversion.CurrentVersionAppDetailActivity;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.me.feedback.FeedbackActivity;
import com.star.mobile.video.model.AboutItemData;
import com.star.mobile.video.util.CommonUtil;

/**
 * 
 * @author zhangkai
 *
 */

public class AboutActivity extends BaseActivity {
	
	private ListView lvAbout;
	private AboutAdapter mAdapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_activity);
		initView();
	}
	
	
	private void initView() {
		lvAbout = (ListView) findViewById(R.id.lv_about_list);
		((TextView) findViewById(R.id.tv_actionbar_title)).setText(getString(R.string.about));
		mAdapter = new AboutAdapter(getData(), this);
		lvAbout.setAdapter(mAdapter);
		lvAbout.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				AboutItemData aid = (AboutItemData) mAdapter.getItem(position);
				Intent intent = new Intent(AboutActivity.this,aid.getTarget());
				Object target;
				try {
					target = aid.getTarget().getConstructor().newInstance();
					if(target instanceof BrowserActivity) {
						intent.putExtra("loadUrl", getString(R.string.bbs_faq_url));
					}
				} catch (Exception e) {
					e.printStackTrace();
				} 
				CommonUtil.goActivityOrFargment(AboutActivity.this,aid.getTarget(),intent);
			}
		});
		
		findViewById(R.id.iv_actionbar_back).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}
	
	private List<AboutItemData> getData() {
		List<AboutItemData> data = new ArrayList<AboutItemData>();
		AboutItemData currentVersion = new AboutItemData(R.drawable.ic_current_version,getString(R.string.current_version), CurrentVersionAppDetailActivity.class);
		data.add(currentVersion);
		AboutItemData faq = new AboutItemData(R.drawable.ic_faq,getString(R.string.faq), BrowserActivity.class);
		data.add(faq);
		AboutItemData feedback = new AboutItemData(R.drawable.ic_feedback,getString(R.string.feedback), FeedbackActivity.class);
		data.add(feedback);
		return data;
	}

}
