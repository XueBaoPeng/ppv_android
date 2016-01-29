package com.star.mobile.video.view;

import java.util.List;

import com.star.cms.model.sms.Product;
import com.star.cms.model.vo.SmartCardInfoVO;
import com.star.mobile.video.R;
import com.star.mobile.video.changebouquet.ChangeBouquetActivity;
import com.star.mobile.video.service.UserService;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.LoadingDataTask;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
 
/**
 * 
 * @author xbp
 * 2015年11月28日
 */
public class SmartCardInfoView extends LinearLayout {
	
	private TextView smart_card_number;
	private TextView tv_bouque;
	private TextView tv_recharge_package_name;
	private TextView tv_recharge_package_price;
	private TextView tv_balance;
	private TextView tv_recharge_account_balance;
	private UserService userService;
	private Context mContext;
	private String curPackageName;
	private String currency;
	private String productCode;
	/**
	 * 
	 */
	public SmartCardInfoView(Context context) {
		// TODO Auto-generated constructor stub
		this(context,null);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public SmartCardInfoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext=context;
		userService=new UserService();
		LayoutInflater.from(context).inflate(R.layout.view_card_info,this);
		initView();
	}

	/**
	 * 
	 */
	private void initView() {
		smart_card_number=(TextView) findViewById(R.id.smart_card_number);
		tv_balance=(TextView) findViewById(R.id.tv_balance);
		tv_bouque=(TextView) findViewById(R.id.tv_bouque);
		tv_recharge_package_name=(TextView) findViewById(R.id.tv_recharge_package_name);
		tv_recharge_package_price=(TextView) findViewById(R.id.tv_recharge_package_price);
		tv_recharge_account_balance=(TextView) findViewById(R.id.tv_recharge_account_balance);
	}
	
	public void setSmartCardNumber(String num){
		smart_card_number.setText(formatSmarCardNo(num));
	}
	private String formatSmarCardNo(String cmardNo) {
		StringBuffer sb = new StringBuffer();
		if (cmardNo != null) {
			for (int i = 0; i < cmardNo.length(); i++) {
				if (i % 4 == 0 && i != 0) {
					sb.append("-");
				}
				sb.append(cmardNo.charAt(i));
			}
			return sb.toString();
		} else {
			return "";
		}

	}
	public void getProgramPackage(final String resCode) {
		new LoadingDataTask() {
			private List<Product> products;
			private String price;
			private String packageName;
			@Override
			public void onPreExecute() {
//				CommonUtil.showProgressDialog(TopupActivity.this, null, getString(R.string.loading));
			}  
			
			@Override
			public void onPostExecute() {	
				 setRechargePackagePrice(null);
				 setRechargePackageName(null);
				if(products != null) {
					for(Product p:products) {
						price = p.getPrice();
						curPackageName = p.getName();
						packageName = p.getName();
						if(price!=null) {
							 setRechargePackagePrice(price+"/month");
						} else {
							tv_recharge_package_name.setVisibility(View.GONE);
							tv_recharge_package_price.setVisibility(View.GONE);
						}
						setProductCode(p.getProductCode());
						setBouque(packageName);
						break;
					}
					if(mContext instanceof ChangeBouquetActivity){
						mChangeBouquetListner.getChangeBouquet();	
					}
				}
			}
			
			@Override
			public void doInBackground() {
				products = userService.getProduct(resCode);
			}
		}.execute();
	}
	public void setData(SmartCardInfoVO smvo){
		setSmartCardNumber(smvo.getSmardCardNo());
		String currency = SharedPreferencesUtil.getCurrencSymbol(mContext);
		setRechargeAccountBalance(currency+smvo.getMoney());
		if(smvo.getProductCode()!=null && !"".equals(smvo.getProductCode())){
			setProductCode(smvo.getProductCode());
			setBouque(smvo.getProgramName());
			setRechargePackagePrice(smvo.getProductPrice()+"/month");
			if(mContext instanceof ChangeBouquetActivity){
				mChangeBouquetListner.getChangeBouquet();	
			}
		}else{
			getProgramPackage(smvo.getSmardCardNo());	
		}
	}
	public void setBalance(String balance){
		tv_balance.setText(balance);
	}
	public void setBouque(String bouque){
		if(bouque!=null){			
			tv_bouque.setText(bouque);
		}
	}
	
	public void setRechargePackageName(String name){
		tv_recharge_package_name.setText(name);
	}
	public void setRechargePackagePrice(String price){
		tv_recharge_package_price.setText(price);
	}
	public void setRechargeAccountBalance(String account){
		tv_recharge_account_balance.setText(account);
	}

	public String getCurPackageName() {
		return curPackageName;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	
	private ChangeBouquetListener mChangeBouquetListner;

	public interface ChangeBouquetListener {
		void getChangeBouquet();
	}
	public void setChangeBouquetListner(ChangeBouquetListener changeBouquetListner) {
		this.mChangeBouquetListner = changeBouquetListner;
	}
	 
}
