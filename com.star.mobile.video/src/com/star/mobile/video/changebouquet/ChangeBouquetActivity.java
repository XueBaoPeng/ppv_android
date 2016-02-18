package com.star.mobile.video.changebouquet;

import java.util.ArrayList;
import java.util.List;

import com.star.cms.model.Package;
import com.star.cms.model.vo.SmartCardInfoVO;
import com.star.mobile.video.R;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.service.PackageService;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.view.NoScrollGridView;
import com.star.mobile.video.view.SmartCardInfoView;
import com.star.mobile.video.view.SmartCardInfoView.ChangeBouquetListener;
import com.star.util.loader.OnListResultListener;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 
 * @author motify by lee
 * @date 2015/11/30
 *
 */
public class ChangeBouquetActivity extends BaseActivity implements OnClickListener,ChangeBouquetListener{
	private SmartCardInfoView mSmartCardInfoView;
	private View btnChangePackage;
	private PackageService pkgService;
	private View clickView = null;
	private Package changeToPkg = null;
	private NoScrollGridView gvPackageList;
	private TextView tvChangetitle;
	private String mCurrentSmartCardNO;
	private SmartCardInfoVO mSmartCardInfoVO;
	private View mLoading;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_bouquet);
		Intent intent = getIntent();
		if (intent != null) {
			mSmartCardInfoVO = (SmartCardInfoVO) intent.getSerializableExtra("smartCardInfoVO");
		}
		pkgService = new PackageService(this);
		initView();
		initData();
//		updatePackageList();
	}
	
	private void initView() {
		mSmartCardInfoView = (SmartCardInfoView) findViewById(R.id.smartcard_info_view);
		mSmartCardInfoView.setChangeBouquetListner(this);
		btnChangePackage = findViewById(R.id.btn_change_package);
		btnChangePackage.setVisibility(View.GONE);
		gvPackageList = (NoScrollGridView) findViewById(R.id.gv_pakcage_list);
		tvChangetitle = (TextView) findViewById(R.id.tv_change_title);
		mLoading = findViewById(R.id.smartcard_loadingView);
		btnChangePackage.setOnClickListener(this);
		((ImageView) findViewById(R.id.iv_actionbar_back)).setOnClickListener(this);
		((TextView)findViewById(R.id.tv_actionbar_title)).setText(R.string.change_bouquet);
	}
	private void initData(){
		mSmartCardInfoView.setChangeBouquetListner(this);
		if (mSmartCardInfoVO != null) {
			mSmartCardInfoView.setData(mSmartCardInfoVO);
			mCurrentSmartCardNO =  mSmartCardInfoVO.getSmardCardNo();
		}
		setNoClickButton();
	}
	public void updatePackageList(SmartCardInfoVO smartCardInfoVO) {
//		new LoadingDataTask() {
//			List<Package> pkgs;
//			@Override
//			public void onPreExecute() {
//				btnChangePackage.setVisibility(View.GONE);
//				mLoading.setVisibility(View.VISIBLE);
//			}
//			
//			@Override
//			public void onPostExecute() {
//				btnChangePackage.setVisibility(View.VISIBLE);
//				mLoading.setVisibility(View.GONE);
//				refreshPackagelistUi(pkgs);
//			}
//			
//			@Override
//			public void doInBackground() {
//				List<Integer> types = new ArrayList<Integer>();
//				types.add(Package.BASIC_TYPE);
//				types.add(Package.SPECIAL_TYPE);
//				pkgs = pkgService.getPackagesFromServer(types);
//			}
//		}.execute();
		List<Integer> types = new ArrayList<Integer>();
		types.add(Package.BASIC_TYPE);
		types.add(Package.SPECIAL_TYPE);
		List<Integer> platformTypes = new ArrayList<Integer>();
		if (smartCardInfoVO != null){
			if (smartCardInfoVO.getTvPlatForm() != null){
				platformTypes.add(smartCardInfoVO.getTvPlatForm().getNum());
			}
		}
		pkgService.getPackagesFromServer(types,platformTypes, new OnListResultListener<Package>() {
			
			@Override
			public boolean onIntercept() {
				btnChangePackage.setVisibility(View.GONE);
				mLoading.setVisibility(View.VISIBLE);
				return false;
			}
			
			@Override
			public void onFailure(int errorCode, String msg) {
				
			}
			
			@Override
			public void onSuccess(List<Package> pkgs) {
				btnChangePackage.setVisibility(View.VISIBLE);
				mLoading.setVisibility(View.GONE);
				refreshPackagelistUi(pkgs);
			}
		});
	}
	
	public void refresData(){
		
	}
	
	private void refreshPackagelistUi(List<Package> pkgs) {
		if(pkgs!=null && pkgs.size()>0){
			final List<Package> ps = new ArrayList<Package>();
			for(Package p : pkgs){
				if (mSmartCardInfoView != null) {
					String productCode = mSmartCardInfoView.getProductCode();
					if (productCode != null) {
						if (productCode.equals(p.getProductCode())) {
							mSmartCardInfoVO.setProductCode(p.getBossPackageCode());
							continue;
						}
					}
				}
				ps.add(p);
			}
			if(ps.size()>0){
				Package p = new Package();
				ps.add(p);
			}
			SimpleAdapter adapter = new SimpleAdapter(ps);
			gvPackageList.setAdapter(adapter);
			gvPackageList.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent,
						View view, int position, long id) {
					if(clickView!=null){
						((ImageView)clickView.findViewById(R.id.iv_pkg_select)).setImageResource(R.drawable.no_sel);
//						((ImageView)clickView.findViewById(R.id.choose_imageview)).setVisibility(View.GONE);
					}
					((ImageView)view.findViewById(R.id.iv_pkg_select)).setImageResource(R.drawable.sel);
//					((ImageView)view.findViewById(R.id.choose_imageview)).setVisibility(View.VISIBLE);
					changeToPkg = ps.get(position);
					clickView = view;
					setButton();
				}
			});
			setChangeTitle();
		}
	}

	/**
	 * 设置标题，包名的颜色改变，从第6位开始到strs.length()-11
	 */
	private void setChangeTitle() {
		String strs = getString(R.string.change)+" "+(TextUtils.isEmpty(mSmartCardInfoView.getCurPackageName())?" "+getString(R.string.bouquet_to):mSmartCardInfoView.getCurPackageName()+" "+getString(R.string.bouquet_to));
		SpannableStringBuilder style=new SpannableStringBuilder(strs);   
		style.setSpan(new ForegroundColorSpan(Color.rgb(255, 92, 05)),6,strs.length()-12,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 
		tvChangetitle.setText(style);
	}
	
	private class SimpleAdapter extends BaseAdapter{

		private List<Package> packages;

		public SimpleAdapter(List<Package> pkgs) {
			this.packages = pkgs;
		}
		
		@Override
		public int getCount() {
			return packages.size();
		}

		@Override
		public Object getItem(int position) {
			return packages.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			View view = null;
			if (convertView == null) {
				holder = new ViewHolder();
				view = LayoutInflater.from(ChangeBouquetActivity.this).inflate(R.layout.view_change_pkg_item, null);
				holder.tvName = (TextView) view.findViewById(R.id.tv_pkg_name);
				holder.tvPrice = (TextView) view.findViewById(R.id.tv_pkg_price);
				holder.ivPkgSelect = (ImageView) view.findViewById(R.id.iv_pkg_select);
				holder.ivSplitLine = (ImageView) view.findViewById(R.id.pkg_split_line);
				view.setTag(holder);
			}else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}
			Package p = packages.get(position);
			if(position!=packages.size()-1){
				holder.tvName.setText(p.getName());
				holder.tvPrice.setText(SharedPreferencesUtil.getCurrencSymbol(ChangeBouquetActivity.this)+p.getPrice()+"/month");
			}else{
				holder.ivPkgSelect.setVisibility(View.GONE);
				holder.ivSplitLine.setVisibility(View.GONE);
			}
			return view;
		}
		
	}
	
	class ViewHolder{
		TextView tvName;
		TextView tvPrice;
		ImageView ivPkgSelect;
		ImageView ivSplitLine;
	}
	
	private TextWatcher verifyTw = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if(s.length() >= 6) {
				 setButton();
			} else {
				btnChangePackage.setBackgroundResource(R.drawable.need_more_coins_button);
				btnChangePackage.setOnClickListener(null);
			}
		}

		
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			
		}
	};
	/**
	 * 设置点击按钮
	 */
	private void setButton() {
		btnChangePackage.setBackgroundResource(R.drawable.orange_button_bg);
		btnChangePackage.setOnClickListener(ChangeBouquetActivity.this);
	}
	/**
	 * 设置没有点击的button
	 */
	private void setNoClickButton(){
		btnChangePackage.setBackgroundResource(R.drawable.need_more_coins_button);
		btnChangePackage.setOnClickListener(null);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_change_package:
			Intent intent = new Intent(this,ChangeWayActivity.class);
			intent.putExtra("smartCardInfoVO", mSmartCardInfoVO);
			intent.putExtra("changeToPkg", changeToPkg);
			CommonUtil.startActivity(this, intent);
			break;
		case R.id.iv_actionbar_back:
			onBackPressed();
			break;
		default:
			break;
		}
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 300) {
			if (data != null) {
				Bundle bundle = data.getExtras();
				if (bundle != null) {
					String string = bundle.getString("refreshResult");
					if ("refresh".equals(string)) {
						updatePackageList(mSmartCardInfoVO);
					}
				} else {
					Log.i("initData", "bundle is null");
				}
			} else {
				Log.i("initData", "data is null");
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void getChangeBouquet() {
		updatePackageList(mSmartCardInfoVO);
	}
}
