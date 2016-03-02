package com.star.mobile.video.appversion;

import android.content.Context;

import com.star.util.SharedPreferences;

/**
 * Created by dujr on 2016/3/1.
 */
public class AppInfoCacheService extends SharedPreferences {

    public static final String APP_POSTER = "app_poster";

    public AppInfoCacheService(Context context){
        super(context);
    }

    @Override
    public String getSharedName() {
        return "app_info";
    }

    @Override
    public int getSharedMode() {
        return 0;
    }

    public String getAppPoster(int versionCode){
        return getString(APP_POSTER+"_"+versionCode, null);
    }

    public void setAppPoster(int versionCode, String posterUrl){
        put(APP_POSTER+"_"+versionCode, posterUrl);
    }
}
