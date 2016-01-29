/**
 * 
 */
package com.star.mobile.video.me.language;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.home.HomeActivity;
import com.star.mobile.video.model.AboutItemData;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.LanguageUtil;
import com.star.mobile.video.util.config.AppConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xbp
 * 2015年12月11日
*/
public class LanguageActivity extends BaseActivity implements OnClickListener{
	
	private ListView lvlanguage;
	private LanguageAdapter mAdapter;	 
	private List<AboutItemData> datas; 
	private TextView tvsave;
	private String  language;
	//全局变量，记录选中的item 
	public static int select_item = -1;
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			 setContentView(R.layout.activity_language);
			 language=SharedPreferencesUtil.getLanguage(this);
			 initView();
			 initData();
			 initEvent();
		}

	private void initEvent() {
		lvlanguage.setOnItemClickListener(new OnlanguageSelect());
		// lvlanguage.setOnItemSelectedListener(new languageSelect());
	}

	private void initView() {
			lvlanguage=(ListView) findViewById(R.id.lv_about_list);
			tvsave=(TextView)findViewById(R.id.tv_bouquet_btn);
			((ImageView) findViewById(R.id.iv_actionbar_back)).setOnClickListener(this);
			((TextView) findViewById(R.id.tv_actionbar_title)).setText(R.string.language);
			tvsave.setText(R.string.modify_save);
			tvsave.setOnClickListener(this);
		}
		/**
		 * 
		 */
		private void initData() {
			datas=new ArrayList<AboutItemData>();
			AboutItemData english=new AboutItemData();
			english.setItemName(getString(R.string.english));
			datas.add(english);
			AboutItemData francais=new AboutItemData();
			francais.setItemName(getString(R.string.francais));
			datas.add(francais);
			AboutItemData kiswahili=new AboutItemData();
			kiswahili.setItemName(getString(R.string.kiswahili));
			datas.add(kiswahili);
			mAdapter= new LanguageAdapter(datas, this);
			lvlanguage.setAdapter(mAdapter);
			inincolor();
		}

	 
		private void inincolor() {
			
			String lastlanguage=SharedPreferencesUtil.getLanguage(this);
			lastlanguage = lastlanguage==null?LanguageUtil.getLocalLanguage():lastlanguage;
			if(lastlanguage.equals("sw")){
				select_item =2; //当前选择的节目item
			}else if(lastlanguage.equals("fr")){
				select_item =1; //当前选择的节目item
			}else if(lastlanguage.equals("en")){
				select_item =0; //当前选择的节目item
			}else{
				select_item =0; //当前选择的节目item
			}
		
			
			mAdapter.notifyDataSetChanged();
		}
 
		@Override
		public void onClick(View v) {
		 switch (v.getId()) {
		case R.id.iv_actionbar_back:
			onBackPressed();
			break;
		case R.id.tv_bouquet_btn:
			LanguageUtil.switchLanguage(LanguageActivity.this,language);
			((StarApplication)getApplication()).exit();
			Intent intent = new Intent(this, HomeActivity.class);
		 	intent.putExtra("fragmentTag", AppConfig.TAG_fragment_me);
			CommonUtil.startActivityFromLeft(this, intent);		
			break;
		default:
			break;
		}
			
		}
		class languageSelect  implements OnItemSelectedListener{

			@Override
			public void onItemSelected(AdapterView<?> arg0, View view, int position, long arg3) {
				// TODO Auto-generated method stub
				TextView textView= (TextView) view.findViewById(R.id.tv_item_name);
				if(textView.getText().toString().equals(getString(R.string.english))){
					language="en";
				}else if(textView.getText().toString().equals(getString(R.string.francais))){
					language="fr";
				}else if(textView.getText().toString().equals(getString(R.string.kiswahili))){
					language="sw";
				}
				 select_item = position; //当前选择的节目item
				 mAdapter.notifyDataSetChanged(); //通知adapter刷新数据
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			 
			}
			
		}
 
		class OnlanguageSelect implements AdapterView.OnItemClickListener {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
				TextView textView= (TextView) view.findViewById(R.id.tv_item_name);
				if(textView.getText().toString().equals(getString(R.string.english))){
					language="en";
				 
				}else if(textView.getText().toString().equals(getString(R.string.francais))){
					language="fr";
				 
				}else if(textView.getText().toString().equals(getString(R.string.kiswahili))){
				 
					language="sw";
				}
				 select_item = position; //当前选择的节目item
				 mAdapter.notifyDataSetChanged(); //通知adapter刷新数据
			}
		}
}
