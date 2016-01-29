package com.star.mobile.video.ppv;

import android.content.Context;

import com.star.mobile.video.base.AbstractService;
import com.star.mobile.video.util.Constant;
import com.star.ott.ppvup.model.remote.Content;
import com.star.util.loader.LoadMode;
import com.star.util.loader.OnListResultListener;

/**
 * Created by xuebp on 2016/1/15.
 */
public class PpvService extends AbstractService {

    public PpvService(Context context) {
        super(context);
    }

    /**
     * 获取ppv列表数据
     * @param smart_card_number
     * @param listener
     */
    public void getPpvData(String smart_card_number, OnListResultListener<Content> listener) {
        doGet(Constant.getPPvDataUrl(), Content.class, LoadMode.NET, listener);
    }
}
