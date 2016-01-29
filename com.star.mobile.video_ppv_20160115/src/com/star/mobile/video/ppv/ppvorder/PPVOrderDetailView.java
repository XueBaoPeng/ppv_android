package com.star.mobile.video.ppv.ppvorder;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.star.mobile.video.R;

import static com.star.mobile.video.R.id.program_detail_time_tv;

/**
 * Created by Lee on 2016/1/18.
 */
public class PPVOrderDetailView extends RelativeLayout {

    private TextView mChannelName;
    private RatingBar mChannelRatingBar;
    private TextView mProgramDate;
    private TextView mProgramDetailTime;
    private TextView mProgramProductCode;
    private TextView mProgramProductCodeContent;
    private TextView mProgramChannel;
    private TextView mProgramChannelContent;
    private TextView mProgramPhoneNumber;
    private TextView mProgramPhoneNumberContent;
    private TextView mProgramSmartCard;
    private TextView mProgramSmartCardContent;
    private TextView mProgramTotalPrice;
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
        mProgramProductCode = (TextView) findViewById(R.id.program_product_code_tv);
        mProgramProductCodeContent = (TextView) findViewById(R.id.program_product_code_content_tv);
        mProgramChannel = (TextView) findViewById(R.id.program_channel_tv);
        mProgramChannelContent = (TextView) findViewById(R.id.program_channel_content_tv);
        mProgramPhoneNumber = (TextView) findViewById(R.id.program_phone_number_tv);
        mProgramPhoneNumberContent = (TextView) findViewById(R.id.program_phone_number_content_tv);
        mProgramSmartCard = (TextView) findViewById(R.id.program_smart_card_tv);
        mProgramSmartCardContent = (TextView) findViewById(R.id.program_smart_card_content_tv);
        mProgramTotalPrice = (TextView) findViewById(R.id.program_total_price_tv);
        mProgramTotalPriceContent = (TextView) findViewById(R.id.program_total_price_content_tv);
    }

    /**
     * Ƶ����
     * @return
     */
    public TextView getmChannelName() {
        return mChannelName;
    }

    /**
     * Ƶ��RatingBar
     * @return
     */
    public RatingBar getmChannelRatingBar() {
        return mChannelRatingBar;
    }

    /**
     * ����
     * @return
     */
    public TextView getmProgramDate() {
        return mProgramDate;
    }

    /**
     * ����ʱ��
     * @return
     */
    public TextView getmProgramDetailTime() {
        return mProgramDetailTime;
    }

    /**
     * productCode����
     * @return
     */
    public TextView getmProgramProductCode() {
        return mProgramProductCode;
    }
    /**
     * productCode����
     * @return
     */
    public TextView getmProgramProductCodeContent() {
        return mProgramProductCodeContent;
    }
    /**
     * Ƶ������
     * @return
     */
    public TextView getmProgramChannel() {
        return mProgramChannel;
    }
    /**
     * Ƶ������
     * @return
     */
    public TextView getmProgramChannelContent() {
        return mProgramChannelContent;
    }

    /**
     * �绰����
     * @return
     */
    public TextView getmProgramPhoneNumber() {
        return mProgramPhoneNumber;
    }
    /**
     * �绰����
     * @return
     */
    public TextView getmProgramPhoneNumberContent() {
        return mProgramPhoneNumberContent;
    }
    /**
     * smartcard����
     * @return
     */
    public TextView getmProgramSmartCard() {
        return mProgramSmartCard;
    }
    /**
     * smartcard����
     * @return
     */
    public TextView getmProgramSmartCardContent() {
        return mProgramSmartCardContent;
    }
    /**
     * �ܼ۸����
     * @return
     */
    public TextView getmProgramTotalPriceContent() {
        return mProgramTotalPriceContent;
    }
    /**
     * �ܼ۸�����
     * @return
     */
    public TextView getmProgramTotalPrice() {
        return mProgramTotalPrice;
    }
}
