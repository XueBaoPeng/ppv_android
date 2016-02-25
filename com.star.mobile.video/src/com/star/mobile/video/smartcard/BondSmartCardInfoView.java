package com.star.mobile.video.smartcard;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.star.cms.model.enm.TVPlatForm;
import com.star.cms.model.vo.SmartCardInfoVO;
import com.star.mobile.video.R;
import com.star.mobile.video.activity.AccountBillActivity;
import com.star.mobile.video.changebouquet.ChangeBouquetActivity;
import com.star.mobile.video.model.FunctionType;
import com.star.mobile.video.service.FunctionService;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.smartcard.recharge.RechargeSmartCardActivity;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.ToastUtil;
import com.star.mobile.video.view.LoadingView;
import com.star.util.loader.OnResultListener;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Lee on 2016/2/1.
 */
public class BondSmartCardInfoView extends RelativeLayout implements View.OnClickListener{
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private ImageView mSmartCardImageView;
    private TextView mSmartCardNumber;
    private TextView mSmartCardBalance;
    private LoadingView mLoadDetailBalance;
    private TextView mSmartCardPackage;
    private LoadingView mLoadDetailPackage;
    private LoadingView mLoadDetailCustomerName;
    private TextView mSmartCardDeadLine;
    private TextView mSmartCardCustomerName;
    private TextView mSmartCardCustomerPhone;
    private SmartCardInfoVO mSmartCardInfoVO;
    private List<SmartCardInfoVO> mSmartinfos;
    private String mChangePkgSmartCardNumber;
    private SmartCardService mSmartcardService;
    private static boolean isAllowDeleteSmartCard = false;
    private boolean hasLoad = false;

    public BondSmartCardInfoView(Context context) {
        this(context, null);
    }

    public BondSmartCardInfoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BondSmartCardInfoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        initView(context);
    }

    private void initView(Context context){
        mLayoutInflater = LayoutInflater.from(context);
        View view = mLayoutInflater.inflate(R.layout.fragment_smart_card_info,this);
        mSmartCardImageView = (ImageView) findViewById(R.id.smart_card_imagview);
        mSmartCardNumber = (TextView) findViewById(R.id.smart_card_number);
        mSmartCardCustomerName = (TextView) findViewById(R.id.smart_card_customer_name);
        mSmartCardCustomerPhone = (TextView) findViewById(R.id.smart_card_customer_phone);
        mSmartCardDeadLine = (TextView)findViewById(R.id.smart_card_deadline);
        mSmartCardBalance = (TextView)findViewById(R.id.smart_card_detail_balance);
        mLoadDetailBalance = (LoadingView) findViewById(R.id.load_detail_balance);
        mSmartCardPackage = (TextView) findViewById(R.id.bouquet_package_textview);
        mLoadDetailPackage = (LoadingView) findViewById(R.id.load_detail_package);
        mLoadDetailCustomerName = (LoadingView) findViewById(R.id.load_detail_customer_name);
        findViewById(R.id.balance_rl).setOnClickListener(this);
        findViewById(R.id.bouquet_rl).setOnClickListener(this);
        findViewById(R.id.account_bill_rl).setOnClickListener(this);
        mSmartcardService = new SmartCardService(mContext);
    }

    public void initData(SmartCardInfoVO smartCardInfoVO) {
        if (!hasLoad){
        }
        if (smartCardInfoVO != null) {
            hasLoad = true;
            getDetailSmartCardInfo(smartCardInfoVO);
        }
    }



    public void setNoInfoVisibility(int visibility) {
        mSmartCardBalance.setVisibility(visibility);
        mSmartCardPackage.setVisibility(visibility);
    }

    public void setSmartinfos(List<SmartCardInfoVO> smartinfos) {
        this.mSmartinfos = smartinfos;
    }

    public void setChangePkgSmartCardNO(String changePkgSmartCardNumber) {
        this.mChangePkgSmartCardNumber = changePkgSmartCardNumber;
    }

    /**
     * 设置卡号
     *
     * @param cardNo
     */
    public void setSmartCardNo(String cardNo) {
        mSmartCardNumber.setText(cardNo);
    }

    /**
     * 设置卡的余额
     *
     * @param money
     */
    public void setCardMoney(Double money) {
        mSmartCardBalance.setText(getResources().getString(R.string.balance_s)+":"+SharedPreferencesUtil.getCurrencSymbol(mContext) + money);
    }

    /**
     * 用户名
     *
     * @param customerName
     */
    public void setCustomerName(String customerName) {
        mSmartCardCustomerName.setText(customerName);
    }

    /**
     * 用户电话
     *
     * @param customerPhone
     */
    public void setCustomerPhone(String customerPhone) {
        mSmartCardCustomerPhone.setText(customerPhone);
    }

    public String getCardMoney() {
        return mSmartCardBalance.getText().toString().substring(2);
    }

    public void setCardMoneyTextColor(int color) {
        mSmartCardBalance.setTextColor(getResources().getColor(color));
    }

    /**
     * 设置节目包名称
     *
     * @param name
     */
    public void setPackageName(String name) {
        mSmartCardPackage.setText(name);
    }

    public void setPackageNameTextColor(int color) {
        mSmartCardPackage.setTextColor(color);
    }

    /**
     * 剩余多少天
     *
     * @param days
     */
    public void setRemainingDays(String days) {
        mSmartCardDeadLine.setText(days);
    }

    /**
     * 剩余多少天
     *
     * @param days
     */
    public void setRemainingDays(Integer days) {
        if (days != null && days <= 7) {
            mSmartCardDeadLine.setTextColor(mContext.getResources().getColor(R.color.check_mob_tex));
        } else {
            mSmartCardDeadLine.setTextColor(mContext.getResources().getColor(R.color.alert_setting_text));
        }
        if (days > 0) {
            String text = mContext.getString(R.string.days_left);
            if (days == 1)
                text = mContext.getString(R.string.day_left);
            mSmartCardDeadLine.setText(days + " " + text);
        } else {
            mSmartCardDeadLine.setText(mContext.getString(R.string.please_recharge));
        }
    }




    /**
     * 获得智能卡的详情信息
     * @param vo
     */
    private void getDetailSmartCardInfo(final SmartCardInfoVO vo) {
        mSmartcardService.getSmartCardInfo(vo.getSmardCardNo(), new OnResultListener<SmartCardInfoVO>() {

            @Override
            public void onSuccess(SmartCardInfoVO scv) {
                hideDetailLoading();
                if (scv != null) {
                    setData(scv);
                    setPackageNameData(scv);
                }
            }


            @Override
            public boolean onIntercept() {
                fillData(vo);
                String programName = vo.getProgramName();
                if (programName != null) {
                    setPackageName(programName);
                }
                return false;
            }

            @Override
            public void onFailure(int errorCode, String msg) {
                hideDetailLoading();
            }
        });

    }


    public boolean isAllowDeleteSmartCard() {
        return isAllowDeleteSmartCard;
    }

    public void setAllowDeleteSmartCard(boolean isAllowDeleteSmartCard) {
        this.isAllowDeleteSmartCard = isAllowDeleteSmartCard;
    }
    /**
     * 设置包名
     * @param scv
     */
    private void setPackageNameData(SmartCardInfoVO scv) {
        String programName = scv.getProgramName();
        if (programName != null) {
            setPackageName(programName);
        }else {
            setPackageName(mContext.getString(R.string.no_bouquet));
        }
    }
    /**
     * 隐藏loading
     */
    private void hideDetailLoading() {
        setAllowDeleteSmartCard(true);
        mLoadDetailBalance.setVisibility(View.GONE);
        mLoadDetailPackage.setVisibility(View.GONE);
        mLoadDetailCustomerName.setVisibility(View.GONE);
    }

    private void setData( SmartCardInfoVO scv) {
        mSmartCardInfoVO = scv;
        fillData(scv);
    }
   private  TVPlatForm platForm;
    /**
     * 设置数据
     *
     * @param sc
     */
    private void fillData(SmartCardInfoVO sc) {
        if(sc.getTvPlatForm() != null){
            platForm = sc.getTvPlatForm();
            if (sc.getTvPlatForm().getNum()==TVPlatForm.DTT.getNum()){
                mSmartCardImageView.setImageResource(R.drawable.smartcard_dtt);
            }else {
//                disimssRechargeAndBouquet();
                mSmartCardImageView.setImageResource(R.drawable.smartcard_dth);
            }
        }

        if (sc.getStopDays() != null) {
            setRemainingDays(sc.getStopDays());
        } else if (!TextUtils.isEmpty(sc.getPenaltyStop())) {
            setRemainingDays(sc.getPenaltyStop());
        }
        Double money = sc.getMoney();
        if (money != null) {
            setCardMoney(money);
        }
        if (sc.getCode() != null) {
            if (sc.getCode().equals(SmartCardInfoVO.NET_WORK_ERROR)) {
                setNoInfoVisibility(View.VISIBLE);
            }
        }
        setSmartCardNo(formatSmarCardNo(sc.getSmardCardNo()));
        // 设置用户名和用户电话
        if (sc.getAccountName() != null) {
            setCustomerName(sc.getAccountName());
        }
        if (sc.getPhoneNumber() != null) {
            setCustomerPhone(sc.getPhoneNumber());
        }
    }

    private void disimssRechargeAndBouquet() {
        findViewById(R.id.balance_rl).setVisibility(View.GONE);
        findViewById(R.id.bouquet_rl).setVisibility(View.GONE);
    }

    private String formatSmarCardNo(String cmardNo) {
        StringBuffer sb = new StringBuffer();
        if (cmardNo != null) {
            for (int i = 0; i < cmardNo.length(); i++) {
                if (i % 4 == 0 && i != 0) {
                    sb.append("-");
                }
                sb.append(cmardNo.charAt(i));
            }
            return sb.toString();
        } else {
            return "";
        }

    }

    public void setRemainingDaysTextColor(int color) {
        mSmartCardDeadLine.setTextColor(color);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.balance_rl:
                // 跳转到Balance页面
                if (mSmartCardInfoVO != null && mSmartCardInfoVO.getMoney() != null) {
                    if (mSmartCardInfoVO.getTvPlatForm().getNum()==TVPlatForm.DTT.getNum()){
                        Intent i = new Intent();
                        i.putExtra("smartcardinfovo", (Serializable) mSmartCardInfoVO);
                        // i.putExtra("smartinfos", (Serializable) mSmartinfos);
                        // i.setClass(getActivity(), TopupActivity.class);
                        i.putExtra("platForm",platForm);
                        i.setClass(mContext, RechargeSmartCardActivity.class);
                        CommonUtil.startActivity(mContext, i);
                    }else{
                        ToastUtil.centerShowToast(mContext, mContext.getString(R.string.not_identify));
                    }
                }else {
                    ToastUtil.centerShowToast(mContext, mContext.getString(R.string.loading_prompt));
                }
                break;
            case R.id.bouquet_rl:
                // 跳转到Bouquet页面
                if (mSmartCardInfoVO != null && mSmartCardInfoVO.getMoney() != null) {
                    if (mSmartCardInfoVO.getTvPlatForm().getNum()==TVPlatForm.DTT.getNum()){
                        Intent intent = new Intent();
                        intent.putExtra("smartCardInfoVO", mSmartCardInfoVO);
                        intent.putExtra("platForm",platForm);
                        intent.setClass(mContext, ChangeBouquetActivity.class);
                        CommonUtil.startActivity(mContext, intent);
                    }else{
                        ToastUtil.centerShowToast(mContext, mContext.getString(R.string.not_identify));
                    }

                }else {
                    ToastUtil.centerShowToast(mContext, mContext.getString(R.string.loading_prompt));
                }
                break;
            case R.id.account_bill_rl:
                // 点击AccountBill跳转
                if (mSmartCardInfoVO != null && mSmartCardInfoVO.getMoney() != null) {
                    Intent accountBillIntent = new Intent(mContext, AccountBillActivity.class);
                    accountBillIntent.putExtra("platForm",platForm);
                    accountBillIntent.putExtra("smartCardInfo", mSmartCardInfoVO);
                    CommonUtil.startActivity(mContext, accountBillIntent);
                }else {
                    ToastUtil.centerShowToast(mContext, mContext.getString(R.string.loading_prompt));
                }
                break;
            default:
                break;
        }
    }



}
