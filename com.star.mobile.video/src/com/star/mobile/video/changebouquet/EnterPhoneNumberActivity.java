package com.star.mobile.video.changebouquet;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.star.cms.model.Area;
import com.star.cms.model.Package;
import com.star.cms.model.vo.SmartCardInfoVO;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.adapter.CustomerServiceTimeAdapter;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.service.PackageService;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.smartcard.MyOrderActivity;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.CommonUtil.PromptDialogClickListener;
import com.star.mobile.video.util.ToastUtil;
import com.star.mobile.video.view.NoScrollListView;
import com.star.util.app.GA;
import com.star.util.loader.OnResultListener;

public class EnterPhoneNumberActivity extends BaseActivity implements OnClickListener{
	private ScrollView phoneScrollView;
	private Button button;
	private LinearLayout ll_phone;
	private LinearLayout ll_no_phone;
	private TextView tv_phone;
	private EditText[] ets;
	private EditText et;
	private int[] etSizes;
	private ImageView iv_verify;
	private SmartCardInfoVO mSmartCardInfoVO;
	private Package changeToPkg;
	private NoScrollListView lvCustomerService;
	private boolean isDel;
	private InputMethodManager imm;
	private String mShowPhoneNo;
	private PackageService pkgService;
	private final int WHAT_ERROR = 3;
	private final int PERFORMED = 4;//正在执行换包任务

	public Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WHAT_ERROR:
				CommonUtil.getInstance().showPromptDialog(EnterPhoneNumberActivity.this, getString(R.string.tips), getString(R.string.change_pkg_error_message), getString(R.string.ok), null, new PromptDialogClickListener() {
					
					@Override
					public void onConfirmClick() {
						
					}
					
					@Override
					public void onCancelClick() {
						
					}
				});
				break;
				case PERFORMED:
					CommonUtil.getInstance().showPromptDialog(EnterPhoneNumberActivity.this, getString(R.string.tips), getString(R.string.performed), getString(R.string.check), getString(R.string.later_big), new PromptDialogClickListener() {

						@Override
						public void onConfirmClick() {
							Intent intent = new Intent(EnterPhoneNumberActivity.this, MyOrderActivity.class);
							CommonUtil.startActivity(EnterPhoneNumberActivity.this,intent);
							onBackPressed();
						}

						@Override
						public void onCancelClick() {
							onBackPressed();
						}
					});
					break;

			}
		}
	};
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone_number);
        Intent intent = getIntent();
		if (intent != null) {
			mSmartCardInfoVO = (SmartCardInfoVO) intent.getSerializableExtra("smartCardInfoVO");
			changeToPkg =(Package) intent.getSerializableExtra("changeToPkg");
		}
		pkgService = new PackageService(this);
        initView();
        initData();
	}
	private void initView() {
		et = (EditText) findViewById(R.id.et_phone_num); 
		imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		ll_phone = (LinearLayout)findViewById(R.id.ll_phone);
		ll_no_phone = (LinearLayout)findViewById(R.id.ll_no_phone);
		tv_phone = (TextView)findViewById(R.id.tv_phone);
		ets = new EditText[4];
		etSizes = new int[4];
		ets[0]=(EditText)findViewById(R.id.et_phone_num_1);
		ets[1]=(EditText)findViewById(R.id.et_phone_num_2);
		ets[2]=(EditText)findViewById(R.id.et_phone_num_3);
		ets[3]=(EditText)findViewById(R.id.et_phone_num_4);
		lvCustomerService = (NoScrollListView) findViewById(R.id.lv_customer_phone);
		button = (Button)findViewById(R.id.btn_change_phone);
		iv_verify = (ImageView)findViewById(R.id.iv_verify);
		((ImageView) findViewById(R.id.iv_actionbar_back)).setOnClickListener(this);
		((TextView)findViewById(R.id.tv_actionbar_title)).setText(R.string.change_bouquet);
		/*phoneScrollView = (ScrollView) findViewById(R.id.phone_scrollview);
		phoneScrollView.smoothScrollTo(0, 0);*/
		setNoClickButton();
		et.requestFocus();
		et.setFocusable(true);
		et.setFocusableInTouchMode(true);
		for (int i =0; i < ets.length; i++) {
			ets[i].setFocusable(false);
			ets[i].setFocusableInTouchMode(false);
		}
		for (int i =0; i < ets.length; i++) {
			ets[i].setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					et.requestFocus();
					et.setFocusable(true);
					et.setFocusableInTouchMode(true);
					et.setSelection(et.getText().toString().length());
					if(getWindow().getAttributes().softInputMode!=WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED){
						imm.showSoftInput(et, 0);	
					}
				}
			});
		}
		 et.addTextChangedListener(new TextWatcher() {  
			  
             @Override  
             public void onTextChanged(CharSequence s, int start,  
                     int before, int count) {
            	 for (int i =0; i < ets.length; i++) {
            		 ets[i].setText("");
            	 }
             }  

             @Override  
             public void beforeTextChanged(CharSequence s, int start,  
                     int count, int after) {  
             }  

             @Override  
             public void afterTextChanged(Editable s) {
            	 String[] content = et.getText().toString().split("");
            	 for(int i=0;i<et.getText().toString().length();i++){
            		 ets[i].setText(content[i+1]);
            	 }
            	 if(et.getText().toString().length()==4){
            		 setButton();
            	 }else{
            		 setNoClickButton();
            	 }
                   
             }  
         });  
		 Timer timer = new Timer();
	     timer.schedule(new TimerTask()
	     {
	         
	         public void run()
	         {
	        	 if(getWindow().getAttributes().softInputMode!=WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED){
	        		 imm.showSoftInput(et, 0);	 
	        	 }
	             
	         }
	         
	     },998);
	}
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
//			
//		} else {
//		
//		}
//		return super.onKeyDown(keyCode, event);
//	}
//	@Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        // TODO Auto-generated method stub
//        final int keyCode = event.getKeyCode();
//        if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
//			ToastUtil.centerShowToast(this, "shan");
//		} else if (keyCode == KeyEvent.KEYCODE_0 && event.getAction() == KeyEvent.ACTION_DOWN) {
//			ToastUtil.centerShowToast(this, "0");
//		}else{
//			ToastUtil.centerShowToast(this, "12");
//		}
//        return super.dispatchKeyEvent(event);
//    }
	private void initData() {
		if(mSmartCardInfoVO!=null){
			if(mSmartCardInfoVO.getPhoneNumber() !=null && !"".equals(mSmartCardInfoVO.getPhoneNumber())&& mSmartCardInfoVO.getPhoneNumber().length()>4){
				ll_no_phone.setVisibility(View.GONE);
				ll_phone.setVisibility(View.VISIBLE);
				button.setVisibility(View.VISIBLE);
				String phoneNumber= mSmartCardInfoVO.getPhoneNumber().substring(0,mSmartCardInfoVO.getPhoneNumber().length()-4);
				String[] num=phoneNumber.split("");
				StringBuffer phone = new StringBuffer();
				for(int i=1;i<num.length;i++){
					phone.append(num[i]+" ");
				}
				tv_phone.setText(phone.toString());
			}else{
				ll_no_phone.setVisibility(View.VISIBLE);
				ll_phone.setVisibility(View.GONE);
				button.setVisibility(View.GONE);
			}
		}
		List<Area> datas = getPhoneData();
		CustomerServiceTimeAdapter adapter = new CustomerServiceTimeAdapter(this, datas, this);
		lvCustomerService.setAdapter(adapter);		
	}
	/**
	 * 获得手机号码数据
	 * 
	 * @return
	 */
	private List<Area> getPhoneData() {
		List<Area> datas = new ArrayList<Area>();
		String phone = SharedPreferencesUtil.getCustomerPhone(this);
		String[] phones;
		if (phone != null) {
			phones = phone.split("@");
			for (int i = 0; i < phones.length; i++) {
				Area area = new Area();
				area.setPhoneNumber(phones[i]);
				area.setNationalFlag(SharedPreferencesUtil.getNationalFlag(this));
				datas.add(area);
			}
		}
		return datas;
	}
	/**
	 * 设置点击按钮
	 */
	private void setButton() {
		button.setBackgroundResource(R.drawable.orange_button_bg);
		button.setOnClickListener(this);
	}
	/**
	 * 设置没有点击的button
	 */
	private void setNoClickButton(){
		button.setBackgroundResource(R.drawable.need_more_coins_button);
		button.setOnClickListener(null);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_change_phone:
			imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
			loadData();
			break;
		case R.id.iv_actionbar_back:
			imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
			onBackPressed();
			break;
		case R.id.rl_call:
			// 24小时服务热线
			callPhone((String) v.getTag());
			ToastUtil.showToast(this, "call_phone");
			GA.sendEvent("Self_service", "Call_center", "call_phone", 1);
			break;
		default:
			break;
		}
	}
	private void loadData() {
		String phone=tv_phone.getText().toString().replace(" ", "");
		String phoneNumber = phone+getEditText();
		CommonUtil.showProgressDialog(this);
		pkgService.changePackage(mSmartCardInfoVO.getSmardCardNo(),mSmartCardInfoVO.getProductCode(), changeToPkg.getBossPackageCode(), phoneNumber, 1, new OnResultListener<Integer>() {
			
			@Override
			public void onSuccess(Integer value) {
				CommonUtil.closeProgressDialog();
				if(value ==1 ){
					if (getApplication() instanceof StarApplication) {
						((StarApplication) getApplication()).finishActivityBClazz(ChangeBouquetActivity.class);
						((StarApplication) getApplication()).finishActivityBClazz(ChangeWayActivity.class);
						((StarApplication) getApplication()).finishActivityBClazz(EnterPhoneNumberActivity.class);
					}
					Intent intent = new Intent(EnterPhoneNumberActivity.this,ChangeSuccessActivity.class);
					CommonUtil.startActivity(EnterPhoneNumberActivity.this, intent);
				} else if(value == PERFORMED) {
					handler.sendEmptyMessage(PERFORMED);
				}else{
					handler.sendEmptyMessage(WHAT_ERROR);
				}
			}
			
			@Override
			public boolean onIntercept() {
				return false;
			}
			
			@Override
			public void onFailure(int errorCode, String msg) {
				CommonUtil.closeProgressDialog();
				handler.sendEmptyMessage(WHAT_ERROR);
			}
		});
	}
	/**
	 * 打电话
	 * @param showPhoneNo
	 */
	private void callPhone(String showPhoneNo) {
		mShowPhoneNo = showPhoneNo;
		CommonUtil.getInstance().showPromptDialog(this, getString(R.string.tips),
				String.format(getString(R.string.bind_smart_card_call_prompt), mShowPhoneNo), getString(R.string.call),
				getString(R.string.forum_later), new PromptDialogClickListener() {

					@Override
					public void onConfirmClick() {
						Intent intent = new Intent(Intent.ACTION_CALL);
						intent.setData(Uri.parse("tel:" + mShowPhoneNo));
						startActivity(intent);
					}

					@Override
					public void onCancelClick() {

					}
				});
	}
	
	public String getEditText(){
		StringBuffer sb = new StringBuffer();  
        for (int i = 0; i < ets.length; i++) {  
            sb.append(ets[i].getText().toString().trim());  
        }  
		return sb.toString();
	}
}
