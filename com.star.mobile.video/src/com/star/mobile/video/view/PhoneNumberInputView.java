package com.star.mobile.video.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.star.cms.model.Area;
import com.star.mobile.video.R;
import com.star.mobile.video.adapter.AreaAdapter;
import com.star.mobile.video.shared.SharedPreferencesUtil;

public class PhoneNumberInputView extends LinearLayout {
	
	private LinearLayout ll_phone_number;
	private TextView tvSelAreaNumber;
	private EditText etPhoneNumber;
	private TextView tvErrorText;
	
	private String[] areaList;
	private String[] areaNumber;
	private String selAreaNumber;
	
	private Context context;
	
	public PhoneNumberInputView(Context context) {
		this(context, null);
	}

	public PhoneNumberInputView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		LayoutInflater.from(context).inflate(R.layout.input_phone_view, this);
		ll_phone_number = (LinearLayout) findViewById(R.id.ll_phone_number);
		tvSelAreaNumber = (TextView) findViewById(R.id.tv_area_txt);
		etPhoneNumber = (EditText) findViewById(R.id.et_phone_number);
		tvErrorText = (TextView) findViewById(R.id.tv_error_msg);
		
		areaList = getResources().getStringArray(R.array.area_name_list);
		areaNumber = getResources().getStringArray(R.array.area_number);
		
		setSelAreaNumber();
	}

	
	private void showPopuWindow() {
		View popView =  LayoutInflater.from(context).inflate(R.layout.view_report_answer_list, null);
		NoScrollGridView lv_answer_list = (NoScrollGridView) popView.findViewById(R.id.lv_answer_list);
		AreaAdapter adapter = new AreaAdapter(context, areaList);
		final PopupWindow mWindow = new PopupWindow(popView, ll_phone_number.getWidth(), LayoutParams.WRAP_CONTENT, true);  
		mWindow.setTouchable(true);
		mWindow.setOutsideTouchable(true);
		mWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
		lv_answer_list.setAdapter(adapter);
		lv_answer_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				tvSelAreaNumber.setText(areaNumber[arg2]);
//				setSelAreaNumber(areaNumber[arg2]);
				mWindow.dismiss();
			}
		});
		mWindow.showAsDropDown(ll_phone_number, 0, 0);
		mWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {

			}
		});
	}

	public String getSelAreaNumber() {
		return selAreaNumber;
	}

	public void setSelAreaNumber() {
		String areaCode = SharedPreferencesUtil.getAreaCode(context);
		if(Area.NIGERIA_CODE.equals(areaCode)){
			this.selAreaNumber = context.getString(R.string.nijeria_number);
		} else if(Area.TANZANIA_CODE.equals(areaCode)) {
			this.selAreaNumber = context.getString(R.string.tanzania_number);
		} else if(Area.KENYA_CODE.equals(areaCode)) {
			this.selAreaNumber = context.getString(R.string.kenya_area_number);
		} else if(Area.UGANDA_CODE.equals(areaCode)){
			this.selAreaNumber = context.getString(R.string.uganda_number);
		} else if(Area.SOUTHAFRICA_CODE.equals(areaCode)){
			this.selAreaNumber = context.getString(R.string.south_africa_number);
		} else if(Area.RWANDA_CODE.equals(areaCode)){
			this.selAreaNumber = context.getString(R.string.rwanda_number);
		}
		tvSelAreaNumber.setText("+"+this.selAreaNumber);
	}

	public String getPhoneNumber() {
		
		return etPhoneNumber.getText().toString();
	}
	
	public void setPhoneNumber(String text) {
//		String areaNum = SharedPreferencesUtil.getPhoneAreaNumber(context);
//		if(selAreaNumber!=null && selAreaNumber.equals(areaNum))
			etPhoneNumber.setText(text);
	}
	
	public void setPhoneNumberChangedListener(TextWatcher watcher) {
		etPhoneNumber.addTextChangedListener(watcher);
	}
	
	public void setPhoneNumberOnFocusChangeListener(OnFocusChangeListener l) {
		etPhoneNumber.setOnFocusChangeListener(l);
	}
	
	public void clearPhoneNumber() {
		etPhoneNumber.getEditableText().clear();
	}

	/**
	 * 电话号码输入框不让修改
	 */
	public void setEtPhoneEnable(){
		etPhoneNumber.setEnabled(false);
	}
	/**
	 * 电话号码输入框让修改
	 */
	public void setEtPhoneEdit(){
		etPhoneNumber.setEnabled(true);
	}
	public void setErrorText(String text) {
		tvErrorText.setText(text);
		if(text == null ||"".equals(text)) {
			setErrorTextVisibility(View.GONE);
		} else {
			setErrorTextVisibility(View.GONE);
		}
	}
	
	public void setErrorTextColor(int color) {
		tvErrorText.setTextColor(color);
	}
	
	private void setErrorTextVisibility(int visibility) {
		tvErrorText.setVisibility(visibility);
	}
	
}
