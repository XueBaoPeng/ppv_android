package com.star.mobile.video.ppv;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.star.cms.model.ProgramPPV;
import com.star.cms.model.vo.SmartCardInfoVO;
import com.star.mobile.video.R;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.ppv.ppvorder.PurchasePPVActivity;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.view.AllSmardCardListView;

import java.util.List;

public class PpvCardNumActivity extends BaseActivity implements OnClickListener,AllSmardCardListView.ScListCallback{

    List<SmartCardInfoVO> smartCardInfos;
    private Button btnGo;
    private ProgramPPV program;
    private AllSmardCardListView allSmardCardListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_num);
        smartCardInfos=(List<SmartCardInfoVO>) getIntent().getSerializableExtra("smartCardInfos");
        program=(ProgramPPV)getIntent().getSerializableExtra("program");
        initView();
        allSmardCardListView.setData(smartCardInfos);
    }


    /**
     *
     */
    private void initView() {
        allSmardCardListView=(AllSmardCardListView) findViewById(R.id.sc_list_view);
        allSmardCardListView.setScListCallback(this);
        btnGo=(Button) findViewById(R.id.bt_mob_reg_go);
        btnGo.setOnClickListener(this);
        ((ImageView) findViewById(R.id.iv_actionbar_back)).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_actionbar_title)).setText(getString(R.string.choose_card));

    }


    @Override
    public void onClick(View v) {
        if( v.getId()==R.id.iv_actionbar_back){
            onBackPressed();
        }
        if(v.getId()==R.id.bt_mob_reg_go){
            if(allSmardCardListView.getData()!=null){
                Intent intent = new Intent(this, PurchasePPVActivity.class);
                intent.putExtra("smartCardInfoVO", allSmardCardListView.getData());
                intent.putExtra("program", program);
                CommonUtil.startActivity(this, intent);
            }else{
            }
        }

    }

    @Override
    public void OnSize(int size) {
        if(size>0){
            btnGo.setBackgroundResource(R.drawable.orange_button_bg);
        }
    }
}

