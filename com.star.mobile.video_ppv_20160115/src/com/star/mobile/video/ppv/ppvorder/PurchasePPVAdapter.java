package com.star.mobile.video.ppv.ppvorder;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.star.mobile.video.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Lee on 2016/1/15.
 */
public class PurchasePPVAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    private List<PPVPurchaseBean> mPurchaseDatas;
    private Map<Integer,Integer> selected;
    public PurchasePPVAdapter(Context context,List<PPVPurchaseBean> purchaseDatas) {
        this.mContext = context;
        this.mPurchaseDatas = purchaseDatas;
        mLayoutInflater = LayoutInflater.from(context);
        selected=new HashMap<Integer,Integer>();
    }

    @Override
    public int getCount() {
        return mPurchaseDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mPurchaseDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.view_purchase_ppv_item,parent,false);
            viewHolder.mPurchaseTime = (TextView) convertView.findViewById(R.id.purchase_time_tv);
            viewHolder.mPurchaseProductCode = (TextView) convertView.findViewById(R.id.purchase_product_code_tv);
            viewHolder.mPurchaseCheckBoxRL = (RelativeLayout) convertView.findViewById(R.id.purchase_checkbox_rl);
            viewHolder.mPurchaseCheckBox = (CheckBox) convertView.findViewById(R.id.purchase_checkbox);
            viewHolder.mPurchaseCheckBox.setChecked(false);
            viewHolder.mPurchaseRadioButton = (RadioButton) convertView.findViewById(R.id.purchase_radiobutton);
            viewHolder.mPurchasePrice = (TextView) convertView.findViewById(R.id.purshase_price_tv);
            viewHolder.mPurchaseFavorablePrice = (TextView) convertView.findViewById(R.id.favorable_price_tv);
            viewHolder.mPurchaseFavorablePrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            viewHolder.mPurchaseFavorablePrice.getPaint().setAntiAlias(true);
            viewHolder.mPurchaseStatusIV = (ImageView) convertView.findViewById(R.id.purchase_status_iv);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        PPVPurchaseBean purchaseBean = mPurchaseDatas.get(position);
        if (purchaseBean != null){
            viewHolder.mPurchaseTime.setText(purchaseBean.getPurchaseTime()==null?"":purchaseBean.getPurchaseTime());
            viewHolder.mPurchaseProductCode.setText(purchaseBean.getProductCode() == null ? "" : purchaseBean.getProductCode());
            viewHolder.mPurchasePrice.setText("$"+purchaseBean.getPurchasePrice());
            if (TextUtils.isEmpty(purchaseBean.getPurchaseFavorablePrice())){
                viewHolder.mPurchaseFavorablePrice.setVisibility(View.GONE);
            }else{
                viewHolder.mPurchaseFavorablePrice.setVisibility(View.VISIBLE);
                viewHolder.mPurchaseFavorablePrice.setText(purchaseBean.getPurchaseFavorablePrice());
            }

            viewHolder.mPurchaseCheckBox.setChecked(purchaseBean.isChecked());

            viewHolder.mPurchaseCheckBox.setOnClickListener(new myOnClickListtener(viewHolder.mPurchaseCheckBox,purchaseBean,position));

        }

        return convertView;
    }


    class ViewHolder{
        TextView mPurchaseTime;
        TextView mPurchaseProductCode;
        CheckBox mPurchaseCheckBox;
        TextView mPurchasePrice;
        TextView mPurchaseFavorablePrice;
        ImageView mPurchaseStatusIV;
        RelativeLayout mPurchaseCheckBoxRL;
        RadioButton mPurchaseRadioButton;
    }

    private class myOnClickListtener implements View.OnClickListener{
        private CheckBox purchaseCheckBox;
        private PPVPurchaseBean purchaseBean;
        private int position;
        public myOnClickListtener(CheckBox purchaseCheckBox,PPVPurchaseBean purchaseBean,int position) {
            this.purchaseCheckBox = purchaseCheckBox;
            this.purchaseBean = purchaseBean;
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            purchaseBean.setChecked(purchaseCheckBox.isChecked());
            listener.getChoiceData(position, purchaseCheckBox.isChecked());
        }
    }


    /**
     * 定义checkbox是否选中的接口回调
     */
    private onCheckedChanged listener;
    public interface onCheckedChanged{
        void getChoiceData(int position,boolean isChoice);
    }
    public void setOnCheckedChanged(onCheckedChanged listener){
        this.listener=listener;
    }
}
