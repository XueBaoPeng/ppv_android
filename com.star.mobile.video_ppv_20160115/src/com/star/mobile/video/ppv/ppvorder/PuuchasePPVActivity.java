package com.star.mobile.video.ppv.ppvorder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.star.mobile.video.R;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.search.SearchActivity;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.view.ViewProgramDetail;

import java.util.ArrayList;
import java.util.List;

/**
 * ppv详情页面
 * Created by Lee on 2016/1/15.
 */
public class PuuchasePPVActivity extends BaseActivity implements View.OnClickListener,PurchasePPVAdapter.onCheckedChanged {

    private com.star.mobile.video.view.ViewProgramDetail mViewProgramDetail;
    private TextView mPPVTotalPrice;
    private Button mPurchaseNextButton;
    private ListView mPurchasePPVListView;
    private PurchasePPVAdapter mPurchasePPVAdapter;

    private List<PPVPurchaseBean> mPurchaseDatas = new ArrayList<PPVPurchaseBean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_ppv);
        initView();
        initData();
    }

    private void initView() {
        findViewById(R.id.iv_actionbar_back).setOnClickListener(this);
        findViewById(R.id.iv_actionbar_search).setOnClickListener(this);
        TextView actionBarTitle = (TextView) findViewById(R.id.tv_actionbar_title);
        actionBarTitle.setText(R.string.fragment_tag_epgDetail);

//        mViewProgramDetail = (ViewProgramDetail) findViewById(R.id.view_program_detail);
        mPPVTotalPrice = (TextView) findViewById(R.id.ppv_total_price);
        mPurchaseNextButton = (Button) findViewById(R.id.purchase_next_button);
        mPurchasePPVListView = (ListView) findViewById(R.id.purchase_ppv_listview);
        mViewProgramDetail = new ViewProgramDetail(this);
    }
    private void initData() {
        mPurchasePPVListView.addHeaderView(mViewProgramDetail);

        mPurchaseDatas.clear();
        PPVPurchaseBean purchaseBean1 = new PPVPurchaseBean();
        purchaseBean1.setProductCode("123456789");
        purchaseBean1.setPurchasePrice(1.00f);
        purchaseBean1.setPurchaseTime("10:00-12:00");
        mPurchaseDatas.add(purchaseBean1);

        PPVPurchaseBean purchaseBean2 = new PPVPurchaseBean();
        purchaseBean2.setProductCode("123456789");
        purchaseBean2.setPurchasePrice(2.00f);
        purchaseBean2.setPurchaseTime("13:00-16:00");
        mPurchaseDatas.add(purchaseBean2);

        PPVPurchaseBean purchaseBean3 = new PPVPurchaseBean();
        purchaseBean3.setProductCode("123456789");
        purchaseBean3.setPurchasePrice(3.00f);
        purchaseBean3.setPurchaseTime("16:00-18:00");
        mPurchaseDatas.add(purchaseBean3);

        PPVPurchaseBean purchaseBean4 = new PPVPurchaseBean();
        purchaseBean4.setProductCode("123456789");
        purchaseBean4.setPurchasePrice(4.00f);
        purchaseBean4.setPurchaseFavorablePrice("$3.00");
        purchaseBean4.setPurchaseTime("All day deal");
        mPurchaseDatas.add(purchaseBean4);

        PPVPurchaseBean purchaseBean5 = new PPVPurchaseBean();
        purchaseBean5.setProductCode("123456789");
        purchaseBean5.setPurchasePrice(5.00f);
        purchaseBean5.setPurchaseTime("10:00-12:00");
        mPurchaseDatas.add(purchaseBean5);

        PPVPurchaseBean purchaseBean6 = new PPVPurchaseBean();
        purchaseBean6.setProductCode("123456789");
        purchaseBean6.setPurchasePrice(6.00f);
        purchaseBean6.setPurchaseTime("13:00-16:00");
        mPurchaseDatas.add(purchaseBean6);

        PPVPurchaseBean purchaseBean7 = new PPVPurchaseBean();
        purchaseBean7.setProductCode("123456789");
        purchaseBean7.setPurchasePrice(7.00f);
        purchaseBean7.setPurchaseTime("16:00-18:00");
        mPurchaseDatas.add(purchaseBean7);

        PPVPurchaseBean purchaseBean8 = new PPVPurchaseBean();
        purchaseBean8.setProductCode("123456789");
        purchaseBean8.setPurchasePrice(8.00f);
        purchaseBean8.setPurchaseFavorablePrice("$3.00");
        purchaseBean8.setPurchaseTime("All day deal");
        mPurchaseDatas.add(purchaseBean8);

        PPVPurchaseBean purchaseBean9 = new PPVPurchaseBean();
        purchaseBean9.setProductCode("123456789");
        purchaseBean9.setPurchasePrice(9.00f);
        purchaseBean9.setPurchaseTime("10:00-12:00");
        mPurchaseDatas.add(purchaseBean9);

        PPVPurchaseBean purchaseBean10 = new PPVPurchaseBean();
        purchaseBean10.setProductCode("123456789");
        purchaseBean10.setPurchasePrice(10.00f);
        purchaseBean10.setPurchaseTime("13:00-16:00");
        mPurchaseDatas.add(purchaseBean10);

        PPVPurchaseBean purchaseBean11 = new PPVPurchaseBean();
        purchaseBean11.setProductCode("123456789");
        purchaseBean11.setPurchasePrice(11.00f);
        purchaseBean11.setPurchaseTime("16:00-18:00");
        mPurchaseDatas.add(purchaseBean11);

        PPVPurchaseBean purchaseBean12 = new PPVPurchaseBean();
        purchaseBean12.setProductCode("123456789");
        purchaseBean12.setPurchasePrice(12.00f);
        purchaseBean12.setPurchaseFavorablePrice("$3.00");
        purchaseBean12.setPurchaseTime("All day deal");
        mPurchaseDatas.add(purchaseBean12);

        mPurchasePPVAdapter = new PurchasePPVAdapter(PuuchasePPVActivity.this,mPurchaseDatas);
        mPurchasePPVAdapter.setOnCheckedChanged(this);
        mPurchasePPVListView.setAdapter(mPurchasePPVAdapter);
        mPurchasePPVListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PPVPurchaseBean purchaseBean = (PPVPurchaseBean) mPurchasePPVAdapter.getItem(position);
            }
        });

        mPurchaseNextButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_actionbar_back:
                onBackPressed();
                break;
            case R.id.purchase_next_button:
                Intent it = new Intent();
                it.setClass(this, PPVOrderDetail.class);
                CommonUtil.startActivity(this, it);
                break;
            case R.id.iv_actionbar_search:
                //����
                CommonUtil.startActivity(this, SearchActivity.class);
                break;
            default:
                break;
        }
    }
    private float mTotalPrice = 0;
    /** adapter的回调函数，当点击CheckBox的时候传递点击位置和checkBox的状态 */
    @Override
    public void getChoiceData(int position, boolean isChoice) {
        if(isChoice){
            if (mPurchaseDatas != null && mPurchaseDatas.size() > 0){
                mTotalPrice += mPurchaseDatas.get(position).getPurchasePrice();
            }
        }else{
            if (mPurchaseDatas != null && mPurchaseDatas.size() > 0){
                mTotalPrice -= mPurchaseDatas.get(position).getPurchasePrice();
            }
        }
        mPPVTotalPrice.setText("$"+mTotalPrice);
    }
}
