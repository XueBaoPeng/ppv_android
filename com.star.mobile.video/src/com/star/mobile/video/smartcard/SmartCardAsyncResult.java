package com.star.mobile.video.smartcard;

import java.util.UUID;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.analytics.HitBuilders;
import com.star.cms.model.BindCardCommand;
import com.star.cms.model.RechargeCMD;
import com.star.cms.model.code.ChangePackageCode;
import com.star.cms.model.dto.RechargeResult;
import com.star.cms.model.vo.ChangePackageCMDVO;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.service.UserService;
import com.star.mobile.video.util.ApplicationUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.Constant;


public class SmartCardAsyncResult {
    private String df = "default";
    /**
     * 单利模式
     */
    private static SmartCardAsyncResult instance = new SmartCardAsyncResult();

    public static SmartCardAsyncResult getInstance() {
        return instance;
    }

    public void bindCardResult(final Context context, final BindCardCommand bindCardCommand, final NotificationManager notifManager) {
        String result = "FAILURE";
        String message = "";
        switch (bindCardCommand.getAcceptStatus()) {
            case BindCardCommand.BIND_CARED_SUCCESS_RESULT:
                result = "SUCCESS";
                if (bindCardCommand.getCoins() > 0) {
                    new UserService().updateCoins(context, bindCardCommand.getCoins());
                    message = String.format(context.getString(R.string.bind_success_message_first), bindCardCommand.getSmartCardNo(), bindCardCommand.getCoins());
                } else {
                    message = String.format(context.getString(R.string.bind_success_message), bindCardCommand.getSmartCardNo());
                }
                showDialog(context, bindCardCommand, null, message, notifManager, null);
                break;
            case BindCardCommand.NO_CARD_RESULT:
                result = "NOT_EXIST";
                message = context.getString(R.string.smartcard_number_doesn_exist);
                showDialog(context, bindCardCommand, null, message, notifManager, null);
                break;
            case BindCardCommand.CARD_IS_BIND_RESULT:
                result = "BOUND";
                message = context.getString(R.string.smartcard_has_already);
                showDialog(context, bindCardCommand, null, message, notifManager, null);
                break;
            case BindCardCommand.SMS_ERROR_RESULT:
                message = context.getString(R.string.smartcard_binding_fails);
                showDialog(context, bindCardCommand, null, message, notifManager, null);
                break;
            case BindCardCommand.MORE_THAN:
                result = "NOT_MATCH";
                message = context.getString(R.string.failed_ten);
                showDialog(context, bindCardCommand, null, message, notifManager, null);
                break;
            case BindCardCommand.SMART_CARD_NO_STB:
                result = "NOT_MATCH";
                message = context.getString(R.string.smartcard_number_does);
                showDialog(context, bindCardCommand, null, message, notifManager, null);
                break;
            case BindCardCommand.NOT_IDENTIFY://不识别的智能卡
                result = "NOT_MATCH";
                message = context.getString(R.string.smartcard_number_does);
                showDialog(context, bindCardCommand, null, message, notifManager, null);

            default:
                message = context.getString(R.string.feedback_faq);
                showDialog(context, bindCardCommand, null, message, notifManager, df);
                break;
        }

        StarApplication.mTracker.send(new HitBuilders.EventBuilder().setCategory(Constant.GA_EVENT_BUSINESS)
                .setAction(Constant.GA_EVENT_BIND_SMARTCAED)
                .setLabel("SMARTCARD:" + bindCardCommand.getSmartCardNo() + "; STATUS:" + result).setValue(1).build());
    }

    /**
     * 充值弹窗
     *
     * @param context
     * @param notifManager
     */
    public void rechargeResult(final Context context, final RechargeCMD rechargeCMD, final NotificationManager notifManager, int rechargeType) {
        String message = "";
        //优惠券充值提示
        if (rechargeType == RechargeCMD.EXCHANGE_TYPE) {
            switch (rechargeCMD.getAcceptStatus()) {
                case 1:
                case 0:
                    if (rechargeCMD.getCoins() > 0) {
                        new UserService().updateCoins(context, rechargeCMD.getCoins());
                        message = String.format(context.getString(R.string.recharge_result_coins), rechargeCMD.getRechargeMoney(), rechargeCMD.getSmartCardNo(), rechargeCMD.getCoins());
                    } else {
                        message = String.format(context.getString(R.string.recharege_result), rechargeCMD.getRechargeMoney(), rechargeCMD.getSmartCardNo());
                    }
                    showRechargeDialog(context, rechargeCMD, message, notifManager, null);
                    break;
                case RechargeResult.EXCHANGE_FAILURE:
                    message = context.getString(R.string.exchange_failue);
                    showRechargeDialog(context, rechargeCMD, message, notifManager, null);
                    break;
                default:
                    message = context.getString(R.string.feedback_faq);
                    showRechargeDialog(context, rechargeCMD, message, notifManager, df);
                    break;
            }
        } else {
            switch (rechargeCMD.getAcceptStatus()) {
                case 1:
                case 0:
                    if (rechargeCMD.getCoins() > 0) {
                        new UserService().updateCoins(context, rechargeCMD.getCoins());
                        message = String.format(context.getString(R.string.recharge_result_coins), rechargeCMD.getRechargeMoney(), rechargeCMD.getSmartCardNo(), rechargeCMD.getCoins());
                    } else {
                        message = String.format(context.getString(R.string.recharege_result), rechargeCMD.getRechargeMoney(), rechargeCMD.getSmartCardNo());
                    }
                    showRechargeDialog(context, rechargeCMD, message, notifManager, null);
                    break;
                case RechargeResult.CARD_DOES_NOT_EXIST:
                    message = context.getString(R.string.recharge_card_number_is_invalid);
                    showRechargeDialog(context, rechargeCMD, message, notifManager, null);
                    break;
                case RechargeResult.CUSTOMER_PASSWORD_IS_NOT_CORRECT:
                    message = context.getString(R.string.client_password_is_incorrect);
                    showRechargeDialog(context, rechargeCMD, message, notifManager, null);
                    break;
                case RechargeResult.CARD_IS_ALREADY_IN_USE:
                    message = context.getString(R.string.recharge_card_number_has_been_used_before);
                    showRechargeDialog(context, rechargeCMD, message, notifManager, null);
                    break;
                case RechargeResult.CARD_HAS_EXPIRED:
                    message = context.getString(R.string.recharge_card_has_expired);
                    showRechargeDialog(context, rechargeCMD, message, notifManager, null);
                    break;
                default:
                    message = context.getString(R.string.feedback_faq);
                    showRechargeDialog(context, rechargeCMD, message, notifManager, df);
                    break;
            }
        }
        long value = 0;
        if (rechargeCMD.getRechargeMoney() != null) {
            value = rechargeCMD.getRechargeMoney().longValue();
        }


        StarApplication.mTracker.send(new HitBuilders.EventBuilder().setCategory("Recharge")
                .setAction("Selfservice_recharge")
                .setLabel("Smart_card").setValue(value).build());

    }

    private void showDialog(final Context context, BindCardCommand bindCardCommand, ChangePackageCMDVO changePackageCMDVO, String message, NotificationManager notifManager, String errorType) {
        if (!ApplicationUtil.isApplicationInBackground(context)) {
            Intent intent = new Intent();
            intent.setClass(context, AsynAlertDialogActivity.class);
            intent.putExtra("title", context.getString(R.string.tips));
            intent.putExtra("message", message);
            if (errorType != null) {
                intent.putExtra("errorType", errorType);
            } else {
                intent.putExtra("cancel", context.getString(R.string.later_big));
            }
            intent.putExtra("confirm", context.getString(R.string.check));
            if (bindCardCommand != null) {
                intent.putExtra("bindCardCommand", bindCardCommand);
            }
            if (changePackageCMDVO != null) {
                intent.putExtra("changePackageCMDVO", changePackageCMDVO);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            CommonUtil.startActivity(context, intent);
        } else {
            notifyAsynTip(context, bindCardCommand, changePackageCMDVO, message, notifManager);
        }
    }

    private void showRechargeDialog(final Context context, RechargeCMD rc, String message, NotificationManager notifManager, String errorType) {
        if (!ApplicationUtil.isApplicationInBackground(context)) {
            Intent intent = new Intent();
            intent.setClass(context, AsynAlertDialogActivity.class);
            intent.putExtra("title", context.getString(R.string.tips));
            intent.putExtra("message", message);
            if (errorType != null) {
                intent.putExtra("errorType", errorType);
            } else {
                intent.putExtra("cancel", context.getString(R.string.later_big));
            }
            intent.putExtra("rc", rc);
            intent.putExtra("confirm", context.getString(R.string.check));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            CommonUtil.startActivity(context, intent);
        } else {
            notifyAsynRechargeTip(context, rc, message, notifManager);
        }
    }

    public void changePkgResult(final Context context, final ChangePackageCMDVO changePackageCMDVO, final NotificationManager notifManager) {
        String message = "";
        String r = "FAILURE";
        switch (changePackageCMDVO.getAccepStatus()) {
            case ChangePackageCode.CHANGE_SUCCESS:
                String text = String.format(context.getString(R.string.change_pkg_success), changePackageCMDVO.getFromPackageName(), changePackageCMDVO.getToPackageName());
                showDialog(context, null, changePackageCMDVO, text, notifManager, null);
                r = "SUCCESS";
                break;
            case ChangePackageCode.BOX_NUMBER_NOT_MATCH:
                message = context.getString(R.string.smartcard_number_does);
                showDialog(context, null, changePackageCMDVO, message, notifManager, null);
                r = "BOX_SC_NOT_MATCH";
                break;
            case ChangePackageCode.LACK_BALANCE: //余额不足
                message = context.getString(R.string.account_balance_is_not_enough);
                showDialog(context, null, changePackageCMDVO, message, notifManager, null);
                r = "LACK_BALANCE";
                break;
            case ChangePackageCode.PHONE_NO_MATCHING:
                message = context.getString(R.string.change_pkg_phone_not_match_error);
                showDialog(context, null, changePackageCMDVO, message, notifManager, null);
                r = "PHONE_NO_MATCHING";
                break;
            default:
                message = context.getString(R.string.feedback_faq);
                showDialog(context, null, changePackageCMDVO, message, notifManager, df);
                r = r + ";ERROR_CODE=" + changePackageCMDVO.getAccepStatus();
                break;
        }
        StarApplication.mTracker.send(new HitBuilders.EventBuilder().setCategory(Constant.GA_EVENT_BUSINESS)
                .setAction(Constant.GA_EVENT_CHANGE_PACKAGE).setLabel("SMARTCARD:" + changePackageCMDVO.getSmartCardNo() + "; STATUS:" + r).setValue(1).build());
    }

    public void notifyAsynTip(Context context, BindCardCommand bindCardCommand, ChangePackageCMDVO changePackageCMDVO, String message, NotificationManager notifManager) {
        Notification notif = new Notification();
        notif.icon = R.drawable.app_icon;
        notif.tickerText = "StarTimes";
        notif.flags |= Notification.FLAG_AUTO_CANCEL;
//		Integer count = msgCountSet.get(room.getId());
        Intent intent = new Intent(context, MyOrderDetailActivity.class);
        if (bindCardCommand != null) {
            intent.putExtra("smsHistoryID", bindCardCommand.getId());
            intent.putExtra("smsHistoryType", bindCardCommand.getType());
        } else if (changePackageCMDVO != null) {
            intent.putExtra("smsHistoryID", changePackageCMDVO.getId());
            intent.putExtra("smsHistoryType", changePackageCMDVO.getType());
        }
        PendingIntent pIntent = PendingIntent.getActivity(context, UUID.randomUUID().hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notif.setLatestEventInfo(context, "StarTimes", message, pIntent);
        notifManager.notify(150, notif);
    }

    public void notifyAsynRechargeTip(Context context, RechargeCMD rc, String message, NotificationManager notifManager) {
        Notification notif = new Notification();
        notif.icon = R.drawable.app_icon;
        notif.tickerText = "StarTimes";
        notif.flags |= Notification.FLAG_AUTO_CANCEL;
//		Integer count = msgCountSet.get(room.getId());
        Intent intent = new Intent(context, MyOrderDetailActivity.class);
        intent.putExtra("smsHistoryID", rc.getId());
        intent.putExtra("smsHistoryType", rc.getType());
        PendingIntent pIntent = PendingIntent.getActivity(context, UUID.randomUUID().hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notif.setLatestEventInfo(context, "StarTimes", message, pIntent);
        notifManager.notify(150, notif);
    }
}
