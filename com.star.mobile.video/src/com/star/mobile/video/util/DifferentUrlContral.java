package com.star.mobile.video.util;


import android.content.Context;

import com.star.mobile.video.R;
import com.star.mobile.video.dao.ServerUrlDao;

/**
 * 控制不同url
 * Created by Lee on 2016/3/4.
 */
public class DifferentUrlContral {
    public static ServerUrlDao diffUrlContral(Context context) {
        //测试环境
        int test_url = 0;
        //prepare环境
        int prepate_url = 1;
        //线上环境
        int online_url = 2;
        int appUrl = Integer.parseInt(context.getResources().getString(R.string.app_current_url));
        ServerUrlDao serverUrlDao = new ServerUrlDao();
        if (appUrl == test_url) {//测试环境
            serverUrlDao.setServerUrl(context.getResources().getString(R.string.server_url_test));
            serverUrlDao.setBBSFaqUrl(context.getResources().getString(R.string.bbs_faq_url_test));
            serverUrlDao.setBBSFaqRecharge(context.getResources().getString(R.string.bbs_faq_recharge_test));
            serverUrlDao.setBBSFaqChangeBouquet(context.getResources().getString(R.string.bbs_faq_changebouquet_test));
            serverUrlDao.setBBSFaqBindCard(context.getResources().getString(R.string.bbs_faq_bindCard_test));
            serverUrlDao.setBBSNewTopicUrl(context.getResources().getString(R.string.bbs_new_topic_url_test));
            serverUrlDao.setBBSDetail(context.getResources().getString(R.string.bbs_detail_test));
            serverUrlDao.setTenbPostBbsUrl(context.getResources().getString(R.string.tenb_post_bbs_url_test));
            serverUrlDao.setHtmlPrefixUrl(context.getResources().getString(R.string.html_prefix_url_test));
            serverUrlDao.setBundesligaUrl(context.getResources().getString(R.string.bundesliga_url_test));
            serverUrlDao.setSerieAUrl(context.getResources().getString(R.string.serie_a_url_test));
            serverUrlDao.setResourcePrefixUrl(context.getResources().getString(R.string.resource_prefix_url_test));
            serverUrlDao.setIsShowCrashDialog(context.getResources().getString(R.string.is_show_crash_dialog_test));
            serverUrlDao.setRobotIconUrl(context.getResources().getString(R.string.robot_icon_url_test));
            serverUrlDao.setGATrackingId(context.getResources().getString(R.string.ga_trackingId_test));
            serverUrlDao.setApkUrl(context.getResources().getString(R.string.apk_url_test));
            serverUrlDao.setGetuiAppId(context.getResources().getString(R.string.getui_appId_test));
            serverUrlDao.setGetuiAppKey(context.getResources().getString(R.string.getui_appKey_test));
            serverUrlDao.setGetuiAppSecret(context.getResources().getString(R.string.getui_appSecret_test));
            serverUrlDao.setGetuiMaseterSecret(context.getResources().getString(R.string.getui_masterSecret_test));
        } else if (appUrl == prepate_url) {//prepare环境
            serverUrlDao.setServerUrl(context.getResources().getString(R.string.server_url_prepare));
            serverUrlDao.setBBSFaqUrl(context.getResources().getString(R.string.bbs_faq_url_prepare));
            serverUrlDao.setBBSFaqRecharge(context.getResources().getString(R.string.bbs_faq_recharge_prepare));
            serverUrlDao.setBBSFaqChangeBouquet(context.getResources().getString(R.string.bbs_faq_changebouquet_prepare));
            serverUrlDao.setBBSFaqBindCard(context.getResources().getString(R.string.bbs_faq_bindCard_prepare));
            serverUrlDao.setBBSNewTopicUrl(context.getResources().getString(R.string.bbs_new_topic_url_prepare));
            serverUrlDao.setBBSDetail(context.getResources().getString(R.string.bbs_detail_prepare));
            serverUrlDao.setTenbPostBbsUrl(context.getResources().getString(R.string.tenb_post_bbs_url_prepare));
            serverUrlDao.setHtmlPrefixUrl(context.getResources().getString(R.string.html_prefix_url_prepare));
            serverUrlDao.setBundesligaUrl(context.getResources().getString(R.string.bundesliga_url_prepare));
            serverUrlDao.setSerieAUrl(context.getResources().getString(R.string.serie_a_url_prepare));
            serverUrlDao.setResourcePrefixUrl(context.getResources().getString(R.string.resource_prefix_url_prepare));
            serverUrlDao.setIsShowCrashDialog(context.getResources().getString(R.string.is_show_crash_dialog_prepare));
            serverUrlDao.setRobotIconUrl(context.getResources().getString(R.string.robot_icon_url_prepare));
            serverUrlDao.setGATrackingId(context.getResources().getString(R.string.ga_trackingId_online));
            serverUrlDao.setApkUrl(context.getResources().getString(R.string.apk_url_online));
            serverUrlDao.setGetuiAppId(context.getResources().getString(R.string.getui_appId_online));
            serverUrlDao.setGetuiAppKey(context.getResources().getString(R.string.getui_appKey_online));
            serverUrlDao.setGetuiAppSecret(context.getResources().getString(R.string.getui_appSecret_online));
            serverUrlDao.setGetuiMaseterSecret(context.getResources().getString(R.string.getui_masterSecret_online));
        } else if (appUrl == online_url) {//线上环境
            serverUrlDao.setServerUrl(context.getResources().getString(R.string.server_url_online));
            serverUrlDao.setBBSFaqUrl(context.getResources().getString(R.string.bbs_faq_url_online));
            serverUrlDao.setBBSFaqRecharge(context.getResources().getString(R.string.bbs_faq_recharge_online));
            serverUrlDao.setBBSFaqChangeBouquet(context.getResources().getString(R.string.bbs_faq_changebouquet_online));
            serverUrlDao.setBBSFaqBindCard(context.getResources().getString(R.string.bbs_faq_bindCard_online));
            serverUrlDao.setBBSNewTopicUrl(context.getResources().getString(R.string.bbs_new_topic_url_online));
            serverUrlDao.setBBSDetail(context.getResources().getString(R.string.bbs_detail_online));
            serverUrlDao.setTenbPostBbsUrl(context.getResources().getString(R.string.tenb_post_bbs_url_online));
            serverUrlDao.setHtmlPrefixUrl(context.getResources().getString(R.string.html_prefix_url_online));
            serverUrlDao.setBundesligaUrl(context.getResources().getString(R.string.bundesliga_url_online));
            serverUrlDao.setSerieAUrl(context.getResources().getString(R.string.serie_a_url_online));
            serverUrlDao.setResourcePrefixUrl(context.getResources().getString(R.string.resource_prefix_url_online));
            serverUrlDao.setIsShowCrashDialog(context.getResources().getString(R.string.is_show_crash_dialog_online));
            serverUrlDao.setRobotIconUrl(context.getResources().getString(R.string.robot_icon_url_online));
            serverUrlDao.setGATrackingId(context.getResources().getString(R.string.ga_trackingId_online));
            serverUrlDao.setApkUrl(context.getResources().getString(R.string.apk_url_online));
            serverUrlDao.setGetuiAppId(context.getResources().getString(R.string.getui_appId_online));
            serverUrlDao.setGetuiAppKey(context.getResources().getString(R.string.getui_appKey_online));
            serverUrlDao.setGetuiAppSecret(context.getResources().getString(R.string.getui_appSecret_online));
            serverUrlDao.setGetuiMaseterSecret(context.getResources().getString(R.string.getui_masterSecret_online));
        }
        return serverUrlDao;
    }


}

