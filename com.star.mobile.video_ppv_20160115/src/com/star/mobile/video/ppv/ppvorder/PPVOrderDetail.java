package com.star.mobile.video.ppv.ppvorder;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.star.mobile.video.R;
import com.star.mobile.video.base.BaseActivity;

/**
 * ppvµÄ¶©µ¥ÏêÇé
 * Created by Lee on 2016/1/15.
 */
public class PPVOrderDetail extends BaseActivity implements View.OnClickListener{
    private PPVOrderDetailView mPPVOrderDetailView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ppv_order_detail);
        initView();
        initData();
    }

    private void initView() {
        ((ImageView) findViewById(R.id.iv_actionbar_back)).setOnClickListener(this);
        ((TextView)findViewById(R.id.tv_actionbar_title)).setText(getString(R.string.order_detail));
        mPPVOrderDetailView = (PPVOrderDetailView) findViewById(R.id.ppv_order_detail_view);
    }

    private void initData() {

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
