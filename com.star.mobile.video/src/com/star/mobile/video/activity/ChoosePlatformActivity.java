package com.star.mobile.video.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.star.mobile.video.R;
import com.star.mobile.video.base.BaseActivity;

/**
 * Created by xuebp on 2016/1/29.
 */
public class ChoosePlatformActivity extends BaseActivity implements View.OnClickListener {


    private TextView platform_name;
    private TextView platform_distion;
    private ImageView platform_image;
    private ImageView platfomr_big_image;
    private Integer platform_Type;
    private static final int DECODER_TYPE = 0;
    private static final int DISH_TYPE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_platform);
        platform_Type = getIntent().getIntExtra("platform_type", DECODER_TYPE);
        initView();
    }

    private void initView() {
        platform_name = (TextView) findViewById(R.id.tv_platform_name);
        platform_distion = (TextView) findViewById(R.id.tv_platform_distion);
        platform_image = (ImageView) findViewById(R.id.platform_imageview);
        platfomr_big_image= (ImageView) findViewById(R.id.platform_big_image);
        ((ImageView) findViewById(R.id.iv_actionbar_back)).setOnClickListener(this);
        if (platform_Type != null && platform_Type == DECODER_TYPE) {
            ((TextView) findViewById(R.id.tv_actionbar_title)).setText(R.string.decoder_dtt);
            platform_name.setText(R.string.decoder_dtt);
            platform_distion.setText(R.string.platform_distion_dtt);
            platform_image.setImageResource(R.drawable.ic_dtt);
            platfomr_big_image.setImageResource(R.drawable.dtt_big);
        } else if (platform_Type == DISH_TYPE) {
            ((TextView) findViewById(R.id.tv_actionbar_title)).setText(R.string.dish_dth);
            platform_name.setText(R.string.dish_dth);
            platform_distion.setText(R.string.platform_distion_dth);
            platform_image.setImageResource(R.drawable.ic_dth);
            platfomr_big_image.setImageResource(R.drawable.dth_big);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_actionbar_back:
                onBackPressed();
                break;
            default:
                break;
        }

    }

}