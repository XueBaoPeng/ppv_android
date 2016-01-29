package com.star.mobile.video.ppv;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.star.mobile.video.R;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.base.BaseAdapter;
import com.star.mobile.video.epg.EpgDetailActivity;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.DateFormat;
import com.star.mobile.video.view.ListView;
import com.star.ott.ppvup.model.remote.Content;
import com.star.ui.ImageView;
import com.star.util.loader.OnListResultListener;

/**
 * Created by xuebp on 2016/1/15.
 */
public class PpvActivity extends BaseActivity {

    private ListView ppvListView;
    private ppvAdapter ppvAdapter;
    private List<Content>ppvData;
    private PpvService ppvService;
    private TextView tvTitle;
    private PpvSharesPre ppvSharesPre;
    private int current=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ppv);
        ppvService=new PpvService(this);
        ppvSharesPre=new PpvSharesPre(this);
        initView();
        getPPVData();
        initEvent();
    }

    private void initEvent() {
        ppvListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("content", (Serializable) ppvData.get(position));
                intent.setClass(PpvActivity.this, EpgDetailActivity.class);
                CommonUtil.startActivity(PpvActivity.this, intent);
            }
        });
    }


    public void  getPPVData(){
        ppvService.getPpvData(null, new OnListResultListener<Content>() {
            @Override
            public void onSuccess(List<Content> value) {
                CommonUtil.closeProgressDialog();
                if (value != null) {
                    ppvData=new ArrayList<Content>();
                    ppvData.addAll(value);
                    ppvAdapter = new ppvAdapter(PpvActivity.this, ppvData);
                    ppvListView.setAdapter(ppvAdapter);
                }
            }

            @Override
            public boolean onIntercept() {
                CommonUtil.showProgressDialog(PpvActivity.this);
                return false;
            }

            @Override
            public void onFailure(int errorCode, String msg) {

            }
        });

    }

    private void initView() {
        tvTitle = (TextView) findViewById(R.id.tv_actionbar_title);
        tvTitle.setText(R.string.ppv);
        findViewById(R.id.iv_actionbar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ppvListView= (ListView) findViewById(R.id.lv_ppv_list);

        if(ppvSharesPre.isFirstCome()){
            CommonUtil.getInstance().showPromptDialog(PpvActivity.this, getString(R.string.ppv_promotion), getString(R.string.ppv_promotion_message), getString(R.string.ok),null, new  CommonUtil.PromptDialogClickListener() {
                @Override
                public void onConfirmClick() {
                    ppvSharesPre.setFirstComePpv(false);
                }

                @Override
                public void onCancelClick() {
                }
            });
        }
    }
    class ppvAdapter extends BaseAdapter< Content>{

        public ppvAdapter(Context context, List<Content> data) {
            super(context, data);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder  hoder;
            if(convertView==null){
                convertView= LayoutInflater.from(context).inflate(R.layout.view_ppv_item,null);
                hoder=new ViewHolder();
                hoder.ppv_coin= (ImageView) convertView.findViewById(R.id.iv_ppv_icon);
                hoder.ratingBar_ppv= (RatingBar) convertView.findViewById(R.id.rating_ppv);
                hoder.tv_ppv_name= (TextView) convertView.findViewById(R.id.tv_ppv_name);
                hoder.tv_ppv_date= (TextView) convertView.findViewById(R.id.tv_ppv_data);
                hoder.tv_ppv_comment= (TextView) convertView.findViewById(R.id.tv_ppv_comment);
                hoder.tv_ppv_money= (TextView) convertView.findViewById(R.id.tv_ppv_money);
                convertView.setTag(hoder);
            }else{
                hoder= (ViewHolder) convertView.getTag();
            }
            if(data.get(position).getPosterResourceList()==null||data.get(position).getPosterResourceList().get(current).getResourceURL()==null){
                hoder.ppv_coin.setUrl("https://123p3.sogoucdn.com/imgu/2016/01/20160119140854_883.jpg");
            }else{
                hoder.ppv_coin.setUrl(data.get(position).getPosterResourceList().get(current).getResourceURL(), false);
            }
            if(data.get(position).getProductList()==null){
                hoder.tv_ppv_date.setText("");
                hoder.tv_ppv_money.setText("");
            }else{
                if(data.get(position).getProductList().get(current).getEpgContentList().get(current).getStartDate()==null){
                    hoder.tv_ppv_date.setText("");
                }else{
                    hoder.tv_ppv_date.setText(DateFormat.formatDay(new Date( data.get(position).getProductList().get(current).getEpgContentList().get(current).getStartDate().longValue())));
                }
                if(data.get(position).getProductList().get(current).getCashPrice()==null){
                    hoder.tv_ppv_money.setText("");
                }else{
                    hoder.tv_ppv_money.setText(data.get(position).getProductList().get(current).getCashPrice());
                }
            }
            if (data.get(position).getName()==null){
                hoder.tv_ppv_name.setText("");
            }else{
                hoder.tv_ppv_name.setText(data.get(position).getName());
            }
            if(data.get(position).getDescription()==null){
                hoder.tv_ppv_comment.setText("");
            }else{
                hoder.tv_ppv_comment.setText(data.get(position).getDescription());
            }
            if(data.get(position).getGrade()==null){
                hoder.ratingBar_ppv.setRating(3);
            }else{
                hoder.ratingBar_ppv.setRating(Integer.parseInt(data.get(position).getGrade()));
            }
            return convertView;
        }

        class ViewHolder{
            ImageView ppv_coin;
            TextView tv_ppv_name;
            TextView tv_ppv_date;
            TextView tv_ppv_comment;
            TextView tv_ppv_money;
            RatingBar ratingBar_ppv;

        }
    }
}
