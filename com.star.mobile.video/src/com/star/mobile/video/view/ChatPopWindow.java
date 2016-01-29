package com.star.mobile.video.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

import com.star.cms.model.Chart;
import com.star.mobile.video.R;

public class ChatPopWindow extends PopupWindow {

	private View popView_up;
	private View popView_dw;
	private int popWidth;
	private int popHeight;
	private Context context;


	public ChatPopWindow(Context context) {
		super(context);
		this.context = context;
		init();
	}

	public ChatPopWindow(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}
	    

    private void init() {
    	popView_up = LayoutInflater.from(context).inflate(R.layout.view_chat_pop_up, null);
    	popView_dw = LayoutInflater.from(context).inflate(R.layout.view_chat_pop_dw, null);
		int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);	
		int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		popView_up.measure(w, h);	
		popWidth = popView_up.getMeasuredWidth();
		popHeight = popView_up.getMeasuredHeight();
		
		setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setOutsideTouchable(true);
    }
    
    public void setView(View view){
    	setContentView(view);
		setWidth(LayoutParams.WRAP_CONTENT);
		setHeight(LayoutParams.WRAP_CONTENT);
    }
    
    public void setCopyOnclickListener(OnClickListener listener){
    	popView_up.findViewById(R.id.tv_copy).setOnClickListener(listener);
    	popView_dw.findViewById(R.id.tv_copy).setOnClickListener(listener);
    }
    
    public void setForwardOnclickListener(OnClickListener listener){
    	popView_up.findViewById(R.id.tv_forward).setOnClickListener(listener);
    	popView_dw.findViewById(R.id.tv_forward).setOnClickListener(listener);
    }
    
    public void showVDown(View v, Chart chat){
    	setView(popView_dw);
    	if(chat.getType()!=Chart.TYPE_LINK){
    		showAsDropDown(v, v.getWidth()/2-popWidth/2, 0);
    		popView_dw.findViewById(R.id.tv_copy).setVisibility(View.VISIBLE);
    		popView_dw.findViewById(R.id.tv_forward).setBackgroundResource(R.drawable.forward_button_dw_bg);
    		popView_dw.findViewById(R.id.tv_forward).setPadding(0, 0, 0, 0);
    	}else{
    		showAsDropDown(v, v.getWidth()/2-popWidth/2+40, 0);
    		popView_dw.findViewById(R.id.tv_copy).setVisibility(View.GONE);
    		popView_dw.findViewById(R.id.tv_forward).setBackgroundResource(R.drawable.forward_single_button_dw_bg);
    		popView_dw.findViewById(R.id.tv_forward).setPadding(0, 0, 0, 0);
    	}
    }
    
    public void showVAbove(View v, Chart chat){
    	setView(popView_up);
    	if(chat.getType()!=Chart.TYPE_LINK){
    		showAsDropDown(v, v.getWidth()/2-popWidth/2, -v.getHeight()-popHeight+6);  
    		popView_up.findViewById(R.id.tv_copy).setVisibility(View.VISIBLE);
    		popView_up.findViewById(R.id.tv_forward).setBackgroundResource(R.drawable.forward_button_up_bg);
    		popView_up.findViewById(R.id.tv_forward).setPadding(0, 0, 0, 0);
    	}else{
    		showAsDropDown(v, v.getWidth()/2-popWidth/2+40, -v.getHeight()-popHeight+6);  
    		popView_up.findViewById(R.id.tv_copy).setVisibility(View.GONE);
    		popView_up.findViewById(R.id.tv_forward).setBackgroundResource(R.drawable.forward_single_button_up_bg);
    		popView_up.findViewById(R.id.tv_forward).setPadding(0, 0, 0, 0);
    	}
    }
    
    public int getPopHeight(){
    	return popHeight;
    }
}
