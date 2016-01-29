package com.star.mobile.video.service;

import org.xmlpull.v1.XmlPullParser;

import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.model.FunctionType;
import com.star.mobile.video.util.Constant;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Log;

/**
 * 
 * @author dujr
 *
 */
public class FunctionService {

	/**
	 * 
	 * @param functionType
	 * @return true 隐藏相应功能
	 */
	public static boolean doHideFuncation(FunctionType functionType) {
		for (FunctionType type : StarApplication.mAreaFunctions) {
			if (functionType.equals(type)) {
				return true;
			}
		}
		return false;
	}

	public static void initAreaFunctions(Context context, String areaName) {
		boolean newCountry = false;
		StarApplication.mAreaFunctions.clear();
		XmlResourceParser xmlParser = context.getResources().getXml(R.xml.countrys);  
		try {  
		    int eventType = xmlParser.getEventType();  
		    while (eventType != XmlPullParser.END_DOCUMENT) {   
		        if(eventType==XmlPullParser.START_TAG && xmlParser.getName().equals("country")){
		        	String coName = xmlParser.getAttributeValue(0);
		        	if(coName!=null && coName.equalsIgnoreCase(areaName)){
		        		newCountry = true;
		        		eventType = xmlParser.next();  
		        		if(eventType==XmlPullParser.START_TAG && xmlParser.getName().equals("functionIds")){
		        			String funcStr = xmlParser.nextText();
		        			if(funcStr.equals(""))
		        				break;
		        			Log.d("FunctionService", "Need shield functionsId: "+funcStr);
		        			String[] types = funcStr.split(",");
		    				for (String type : types) {
	    						int num = Integer.valueOf(type);
	    						StarApplication.mAreaFunctions.add(FunctionType.getFunctionType(num));
		    				}
		        			break;
		        		}
		        	}
		        }
		        eventType = xmlParser.next();  
		    }  
		} catch (Exception e) {  
			Log.d("FunctionService", "init areaFunctions error!", e);
		} 
		if(!newCountry){
			StarApplication.mAreaFunctions.add(FunctionType.FastReport);
			StarApplication.mAreaFunctions.add(FunctionType.Invitation);
			StarApplication.mAreaFunctions.add(FunctionType.InviteFriends);
			StarApplication.mAreaFunctions.add(FunctionType.RechargeCard);
			StarApplication.mAreaFunctions.add(FunctionType.RechargeWithPaga);
			StarApplication.mAreaFunctions.add(FunctionType.SmartCard);
			StarApplication.mAreaFunctions.add(FunctionType.SimpleVersion);
			StarApplication.mAreaFunctions.add(FunctionType.RegisterWithPhone);
			StarApplication.mAreaFunctions.add(FunctionType.PPV);
		}
		if(StarApplication.CURRENT_VERSION==Constant.FINAL_VERSION && !StarApplication.mAreaFunctions.contains(FunctionType.FastReport)){
			StarApplication.mAreaFunctions.add(FunctionType.FastReport);
		}
	}
}
