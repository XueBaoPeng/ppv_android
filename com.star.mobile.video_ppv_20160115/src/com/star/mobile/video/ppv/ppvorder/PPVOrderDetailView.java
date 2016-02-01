package com.star.mobile.video.ppv.ppvorder;

import static com.star.mobile.video.R.id.program_detail_time_tv;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.star.cms.model.PpvCMD;
import com.star.cms.model.vo.SmartCardInfoVO;
import com.star.mobile.video.R;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.DateFormat;
import com.star.ott.ppvup.model.enums.RentleType;
import com.star.ott.ppvup.model.remote.EpgContent;
import com.star.ott.ppvup.model.remote.Product;

/**
 * Created by Lee on 2016/1/18.
 */
public class PPVOrderDetailView extends RelativeLayout {

    private TextView mChannelName;
    private RatingBar mChannelRatingBar;
    private TextView mProgramDate;
    private TextView mProgramDetailTime;
    private TextView mProgramProductCodeContent;
    private TextView mProgramChannelContent;
    private TextView mProgramPhoneNumberContent;
    private TextView mProgramSmartCardContent;
    private TextView mProgramTotalPriceContent;
    public PPVOrderDetailView(Context context) {
        this(context, null);
    }

    public PPVOrderDetailView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PPVOrderDetailView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.ppv_order_detail,this);
        mChannelName = (TextView) findViewById(R.id.program_name_tv);
        mChannelRatingBar = (RatingBar) findViewById(R.id.rating_channel);
        mProgramDate = (TextView) findViewById(R.id.program_date_tv);
        mProgramDetailTime = (TextView) findViewById(program_detail_time_tv);
        mProgramProductCodeContent = (TextView) findViewById(R.id.program_product_code_content_tv);
        mProgramChannelContent = (TextView) findViewById(R.id.program_channel_content_tv);
        mProgramPhoneNumberContent = (TextView) findViewById(R.id.program_phone_number_content_tv);
        mProgramSmartCardContent = (TextView) findViewById(R.id.program_smart_card_content_tv);
        mProgramTotalPriceContent = (TextView) findViewById(R.id.program_total_price_content_tv);
    }
    
    public void setOrderData(List<Product> products, SmartCardInfoVO smartCardInfo, String grade){
    	try{
    		mChannelName.setText(products.get(0).getEpgContentList().get(0).getName());
    		mChannelRatingBar.setRating(Float.valueOf(grade));
    		EpgContent epgContent = products.get(0).getEpgContentList().get(0);
			mProgramChannelContent.setText(epgContent.getLiveContent().getName() +" "+ epgContent.getLiveContent().getServiceId());
    		mProgramDate.setText(DateFormat.formatMonth(new Date(epgContent.getStartDate())));
    		String startime = "";
    		String productCode = "";
    		double totalPrice = 0;
    		for (Product product : products){
    			if (product.getRentleType().equals(RentleType.SINGLE)) {
    				List<EpgContent> epgContents = product.getEpgContentList();
    				startime += "/" + Constant.format.format(epgContents.get(0).getStartDate());
    				totalPrice += Integer.parseInt(product.getCashPrice());
    				productCode += "/" + product.getCode();
    			} else if(product.getRentleType().equals(RentleType.DAY)){
    				for(EpgContent epg : product.getEpgContentList()){
        				startime += "/" + Constant.format.format(epg.getStartDate());
    				}
    				totalPrice = Double.parseDouble(product.getCashPrice());
    				productCode = "/" + product.getCode();
    				break;
    			}
    		}
    		mProgramDetailTime.setText(startime.substring(1));
    		mProgramProductCodeContent.setText(productCode.substring(1));
    		mProgramTotalPriceContent.setText("$"+totalPrice);
    	}catch (Exception e) {
			// TODO: handle exception
		}
         mProgramPhoneNumberContent.setText(smartCardInfo.getPhoneNumber() == null ? "" : smartCardInfo.getPhoneNumber());
         mProgramSmartCardContent.setText(smartCardInfo.getSmardCardNo());
    }
    
    public void setOrderDate(PpvCMD ppvcmd){
    	mChannelName.setText(ppvcmd.getName());
    	mChannelRatingBar.setRating(Float.valueOf(ppvcmd.getSource()));
    	mProgramChannelContent.setText(ppvcmd.getName() +ppvcmd.getId());
    	mProgramDate.setText(DateFormat.formatMonth(ppvcmd.getCreateDate()));
    	mProgramDetailTime.setText(DateFormat.formatTime(ppvcmd.getCreateDate()));
    	mProgramProductCodeContent.setText(ppvcmd.getProductCode());
    	mProgramPhoneNumberContent.setText(ppvcmd.getPhoneNumber() == null ? "" : ppvcmd.getPhoneNumber());
    	mProgramSmartCardContent.setText(ppvcmd.getSmartcardNo());
    }
}
