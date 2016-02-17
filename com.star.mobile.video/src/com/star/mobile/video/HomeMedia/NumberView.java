package com.star.mobile.video.HomeMedia;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.star.mobile.video.R;


/**
 * 数字按钮自定义布局
 * Created by xuebp on 2016/1/25.
 */
public class NumberView extends RelativeLayout{


    private RelativeLayout relativeLayout;
    private ImageView number;
    private Context context;

    public NumberView(Context context) {
        this(context, null);
    }
    public NumberView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        LayoutInflater.from(context).inflate(R.layout.view_remote_number,this);
        relativeLayout=(RelativeLayout)findViewById(R.id.relativelayout_remote_number);
        number=(ImageView) findViewById(R.id.remote_number);
    }
    public void setNumberBackground(int resourceId){
        number.setImageResource(resourceId);
    }
    public void setPowerNumberBackground(){
        relativeLayout.setBackground(getResources().getDrawable(R.drawable.remote_power_number_bg));
    }
}
