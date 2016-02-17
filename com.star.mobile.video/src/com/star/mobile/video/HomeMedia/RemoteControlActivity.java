package com.star.mobile.video.HomeMedia;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.star.mobile.video.R;
import com.star.mobile.video.base.BaseActivity;

/**
 * Created by xuebp on 2016/1/25.
 */
public class RemoteControlActivity extends BaseActivity {
    private NumberView number0;
    private NumberView number1;
    private NumberView number2;
    private NumberView number3;
    private NumberView number4;
    private NumberView number5;
    private NumberView number6;
    private NumberView number7;
    private NumberView number8;
    private NumberView number9;
    private NumberView replay;
    private NumberView menu;
    private NumberView setting_power;
    private NumberView exit;
    private ControlView control;
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_control);
        initView();
        initEvent();
    }

    private void initView() {
        tvTitle = (TextView) findViewById(R.id.tv_actionbar_title);
        tvTitle.setText("Gateway Moniter");
        number0=(NumberView) findViewById(R.id.number_0);
        number1=(NumberView) findViewById(R.id.number_1);
        number2=(NumberView) findViewById(R.id.number_2);
        number3=(NumberView) findViewById(R.id.number_3);
        number4=(NumberView) findViewById(R.id.number_4);
        number5=(NumberView) findViewById(R.id.number_5);
        number6=(NumberView) findViewById(R.id.number_6);
        number7=(NumberView) findViewById(R.id.number_7);
        number8=(NumberView) findViewById(R.id.number_8);
        number9=(NumberView) findViewById(R.id.number_9);
        replay=(NumberView) findViewById(R.id.number_replay);
        menu=(NumberView) findViewById(R.id.number_menu);
        setting_power=(NumberView) findViewById(R.id.number_set_power);
        exit=(NumberView) findViewById(R.id.number_exit);
        control=(ControlView) findViewById(R.id.control_key);
        setBackground();
    }
    private void setBackground() {
        number0.setNumberBackground(R.drawable.ic_number0);
        number1.setNumberBackground(R.drawable.ic_number1);
        number2.setNumberBackground(R.drawable.ic_number2);
        number3.setNumberBackground(R.drawable.ic_number3);
        number4.setNumberBackground(R.drawable.ic_number4);
        number5.setNumberBackground(R.drawable.ic_number5);
        number6.setNumberBackground(R.drawable.ic_number6);
        number7.setNumberBackground(R.drawable.ic_number7);
        number8.setNumberBackground(R.drawable.ic_number8);
        number9.setNumberBackground(R.drawable.ic_number9);
        replay.setNumberBackground(R.drawable.ic_replay);
        menu.setNumberBackground(R.drawable.ic_menu);
        setting_power.setNumberBackground(R.drawable.ic_settings_power);
        setting_power.setPowerNumberBackground();
        exit.setNumberBackground(R.drawable.ic_exit_to_app);
    }

    private void initEvent(){
        findViewById(R.id.iv_actionbar_back).setOnClickListener(onclick);
        number0.setOnClickListener(onclick);
        number1.setOnClickListener(onclick);
        number2.setOnClickListener(onclick);
        number3.setOnClickListener(onclick);
        number4.setOnClickListener(onclick);
        number5.setOnClickListener(onclick);
        number6.setOnClickListener(onclick);
        number7.setOnClickListener(onclick);
        number8.setOnClickListener(onclick);
        number9.setOnClickListener(onclick);
        replay.setOnClickListener(onclick);
        menu.setOnClickListener(onclick);
        setting_power.setOnClickListener(onclick);
        exit.setOnClickListener(onclick);
    }
    View.OnClickListener onclick=new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.iv_actionbar_back:
                    onBackPressed();
                    break;
                default:
                    break;
            }

        }
    };
}
