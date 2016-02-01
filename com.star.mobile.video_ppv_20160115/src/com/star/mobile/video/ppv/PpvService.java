package com.star.mobile.video.ppv;

import android.content.Context;

import com.star.cms.model.PpvCMD;
import com.star.mobile.video.base.AbstractService;
import com.star.mobile.video.util.Constant;
import com.star.ott.ppvup.model.remote.Content;
import com.star.util.json.JSONUtil;
import com.star.util.loader.LoadMode;
import com.star.util.loader.OnListResultListener;
import com.star.util.loader.OnResultListener;

import java.util.HashMap;
import java.util.Map;

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

    /**
     * 把PPV订单的信息传到服务器
     * @author lee
     */
    public void uploadPPVOrder(PpvCMD ppv,OnResultListener<Integer> listener){

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("order", JSONUtil.getJSON(ppv));
        doPost( Constant.getPPVOrderDetailUrl(), Integer.class, params, listener);
    }

}
