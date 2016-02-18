package com.star.mobile.video.HomeMedia;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.star.mobile.video.R;

public class ControlView extends RelativeLayout{

	private ImageView number_down;
	private ImageView number_up;
	private ImageView number_left;
	private ImageView number_right;
	private ImageView number_center;
	private Context context;
	 public ControlView(Context context) {
	        this(context, null);
	    }
	    public ControlView(Context context, AttributeSet attrs) {
	        super(context, attrs);
	        this.context=context;
	        LayoutInflater.from(context).inflate(R.layout.view_control,this);
	        initView();
	        initEvent();
	    }
		private void initView() {
		 number_up=(ImageView) findViewById(R.id.number_up);
		 number_down=(ImageView) findViewById(R.id.number_down);
		 number_left=(ImageView) findViewById(R.id.number_left);
		 number_right=(ImageView) findViewById(R.id.number_right);
		 number_center=(ImageView) findViewById(R.id.number_center);
		}
		private void initEvent(){
			number_center.setOnClickListener(onclick);
			number_down.setOnClickListener(onclick);
			number_left.setOnClickListener(onclick);
			number_right.setOnClickListener(onclick);
			number_up.setOnClickListener(onclick);
		}
		
		OnClickListener onclick=new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		};
	    
}
