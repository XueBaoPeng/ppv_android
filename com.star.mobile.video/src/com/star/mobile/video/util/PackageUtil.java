package com.star.mobile.video.util;

import java.util.ArrayList;
import java.util.List;

import com.star.mobile.video.dao.impl.ApkInfo;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;

/**
 * 获得程序的包名
 * 
 * @author Owen 2015.08.03
 */
public class PackageUtil {

	/**
	 * 获得已经安装的apk的信息
	 * 
	 * @param context
	 * @return
	 */
	public static List<ApkInfo> getInstalledApps(Context context) {
		List<ApkInfo> res = new ArrayList<ApkInfo>();
		List<PackageInfo> packs = context.getPackageManager()
				.getInstalledPackages(0);
		for (int i = 0; i < packs.size(); i++) {
			PackageInfo p = (PackageInfo) packs.get(i);
			ApkInfo newInfo = new ApkInfo();
			newInfo.setAppName(p.applicationInfo.loadLabel(
					context.getPackageManager()).toString());
			newInfo.setPackageName(p.packageName);
			newInfo.setVersionName(p.versionName);
			newInfo.setVersionCode(p.versionCode);
			newInfo.setIcon(p.applicationInfo.loadIcon(context
					.getPackageManager()));
			if ((p.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
				newInfo.setUserApp(false);
			} else {
				newInfo.setUserApp(true);
			}
			res.add(newInfo);
		}
		return res;
	}

	/**
	 * 判断设备是否安装facebook
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isInstallFaceBook(Context context) {
		List<ApkInfo> apkInfos = getInstalledApps(context);
		for (int i = 0; i < apkInfos.size(); i++) {
			boolean isUserApp = apkInfos.get(i).isUserApp();
			if (isUserApp) {
				String packageName = apkInfos.get(i).getPackageName();
				if ("com.facebook.katana".equals(packageName)) {
					return true;
				}
			}
		}
		return false;
	}

	
	
}
