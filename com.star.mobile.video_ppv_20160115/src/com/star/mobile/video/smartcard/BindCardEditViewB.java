package com.star.mobile.video.smartcard;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.star.mobile.video.R;

public class BindCardEditViewB extends LinearLayout {
	private EditText ed;
	private Context context;
	public BindCardEditViewB(Context context) {
		this(context,null);
	}
	public BindCardEditViewB(Context context, AttributeSet attrs){
		super(context, attrs);
		this.context = context;
		LayoutInflater.from(context).inflate(R.layout.view_smart_card_b, this);
		ed = (EditText) findViewById(R.id.et_card_num);
		ed.addTextChangedListener(textWatcher);
    }
	private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        	 if (count == 1){
//        	        if (length == 3 || length == 8){
//        	            ed.setText(s + "-");
//        	            ed.setSelection(ed.getText().toString().length());
//        	        }
        	        addSeparation();
        	    }else if(count>1){
        	    	if(!ed.getText().toString().contains("-") && s.toString().length()>=3){
        	    		addSeparation();
        	    	}
        	    	if(s.toString().indexOf("-", 5)<0 && s.toString().length()>=8){
        	    		addSeparation();
        	    	}
        	    }
        	if(ed.getText().toString().length() ==13){
  	        	cardNumberBCallback.onNotice(true);
  	        }else{
  	        	cardNumberBCallback.onNotice(false);
  	        }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };
    private void addSeparation(){
    	if(ed.getText().toString().length()>=3 && ed.getText().toString().length()<8){
    		ed.setText(insertStringInParticularPosition(ed.getText().toString().replaceAll("-", ""), "-", 3));
    		ed.setSelection(ed.getText().toString().length());
    	}
    	if(ed.getText().toString().length()>=8){
    		String content=insertStringInParticularPosition(ed.getText().toString().replaceAll("-", ""), "-", 3);
    		ed.setText(insertStringInParticularPosition(content, "-", 8));
    		ed.setSelection(ed.getText().toString().length());
    	}
    }
    public String insertStringInParticularPosition(String src, String dec, int position){
    	StringBuffer stringBuffer = new StringBuffer(src);
    	return stringBuffer.insert(position, dec).toString();
    }
    public String getEditText(){
    	return ed.getText().toString().replaceAll("-", "");
    }

	public CardNumberBCallback getCardNumberBCallback() {
		return cardNumberBCallback;
	}
	public void setCardNumberBCallback(CardNumberBCallback cardNumberBCallback) {
		this.cardNumberBCallback = cardNumberBCallback;
	}
	private CardNumberBCallback cardNumberBCallback;

	public interface CardNumberBCallback {
		void onNotice(boolean isShow);
	}

}
