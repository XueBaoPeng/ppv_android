package com.star.mobile.video.ppv;

import android.content.Context;

import com.star.util.SharedPreferences;

/**
 * Created by xuebp on 2016/1/15.
 */
public class PpvSharesPre extends SharedPreferences {

    public final static String FIRST_COME_PPV="first_come_ppv";//第一次进入ppv

    /**
     * 构造方法。
     *
     * @param context
     */
    public PpvSharesPre(Context context) {
        super(context);
    }

    @Override
    public String getSharedName() {
        return  "ppv";
    }
    @Override
    public int getSharedMode() {
        return 0;
    }
    public void setFirstComePpv(boolean isCome){
        put(FIRST_COME_PPV, isCome);
    }
    public boolean isFirstCome(){
        return getBoolean(FIRST_COME_PPV,true);
    }
}
