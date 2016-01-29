package com.star.mobile.video.smartcard;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.star.mobile.video.R;

public class BindCardEditViewA extends LinearLayout {
	private EditText[] ets;
	private EditText et;
	private Context mContext;
	private boolean isDel;
	private InputMethodManager imm;
	public BindCardEditViewA(Context context) {
		this(context,null);
	}
	public BindCardEditViewA(Context context, AttributeSet attrs){
		super(context, attrs);
		this.mContext = context;
		LayoutInflater.from(context).inflate(R.layout.view_smart_card_a, this);
		et = (EditText)findViewById(R.id.et_card_num);
		imm = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
		ets = new EditText[11];
		ets[0]=(EditText)findViewById(R.id.et_card_num_1);
		ets[1]=(EditText)findViewById(R.id.et_card_num_2);
		ets[2]=(EditText)findViewById(R.id.et_card_num_3);
		ets[3]=(EditText)findViewById(R.id.et_card_num_4);
		ets[4]=(EditText)findViewById(R.id.et_card_num_5);
		ets[5]=(EditText)findViewById(R.id.et_card_num_6);
		ets[6]=(EditText)findViewById(R.id.et_card_num_7);
		ets[7]=(EditText)findViewById(R.id.et_card_num_8);
		ets[8]=(EditText)findViewById(R.id.et_card_num_9);
		ets[9]=(EditText)findViewById(R.id.et_card_num_10);
		ets[10]=(EditText)findViewById(R.id.et_card_num_11);
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
					if(((Activity) mContext).getWindow().getAttributes().softInputMode!=WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED){
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
            	 if(et.getText().toString().length()==11){
            		 cardNumberACallback.onNotice(true);
            	 }else{
            		 cardNumberACallback.onNotice(false);
            	 }
                   
             }  
         });  
		 Timer timer = new Timer();
	     timer.schedule(new TimerTask()
	     {
	         
	         public void run()
	         {
	        	 if(((Activity) mContext).getWindow().getAttributes().softInputMode!=WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED){
	        		 imm.showSoftInput(et, 0);	 
	        	 }
	             
	         }
	         
	     },998);
    }
	public String getEditText(){
		StringBuffer sb = new StringBuffer();  
        for (int i = 0; i < ets.length; i++) {  
            sb.append(ets[i].getText().toString().trim());  
        }  
		return sb.toString();
	}
	
	public CardNumberACallback getCardNumberACallback() {
		return cardNumberACallback;
	}
	public void setCardNumberACallback(CardNumberACallback cardNumberACallback) {
		this.cardNumberACallback = cardNumberACallback;
	}

	private CardNumberACallback cardNumberACallback;

	public interface CardNumberACallback {
		void onNotice(boolean isShow);
	}

}
