package com.star.mobile.video.chatroom.faq;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.text.TextUtils;
import android.util.Xml;

import com.star.mobile.video.base.AbstractService;
import com.star.mobile.video.util.Constant;
import com.star.util.Logger;
import com.star.util.loader.AsyncTask;
import com.star.util.loader.LoadMode;
import com.star.util.loader.OnResultListener;

public class FAQService extends AbstractService {

	private FAQCacheService cacheService;

	public FAQService(Context context) {
		super(context);
		cacheService = new FAQCacheService(context);
	}

	/**
	 * 通过索引找到指定节点，并封装成faq对象
	 * @param context
	 * @param index
	 * @return
	 */
	public void seekToFAQ(final String index, final OnResultListener<FAQ> onListener) {
		doGet(Constant.getFAQPropertyUrl(), String.class, LoadMode.IFCACHE_NOTNET, new OnResultListener<String>() {

			@Override
			public boolean onIntercept() {
				return onListener.onIntercept();
			}

			@Override
			public void onSuccess(String value) {
				try {
					onListener.onSuccess(parserXML(index, value));
				} catch (Exception e) {
					onListener.onFailure(AsyncTask.JSON_PASER_ERROR, "");
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int errorCode, String msg) {
				onListener.onFailure(errorCode, msg);
			}
		});
	}

	private FAQ parserXML(String index, String xml) throws XmlPullParserException, UnsupportedEncodingException {
		XmlPullParser xmlParser = Xml.newPullParser();
		xmlParser.setInput(new ByteArrayInputStream(xml.getBytes("UTF-8")), "UTF-8");
		try {  
		    int eventType = xmlParser.getEventType();  
		    String response = "";
		    if(index!=null&&index.startsWith("0-")){
		    	index = index.substring(2);
		    }
		    while (eventType != XmlPullParser.END_DOCUMENT) {   
		    	if(eventType==XmlPullParser.START_TAG && xmlParser.getName().equals("faq")){
		    		cacheService.setFAQVersion(Integer.parseInt(xmlParser.getAttributeValue(null, "version")));
		    	}
		        if(eventType==XmlPullParser.START_TAG && xmlParser.getAttributeValue(null, "id").equals(index)){
		        	if(index.equals("0")){
		        		response = xmlParser.getAttributeValue(null, "description");
		        		eventType = xmlParser.next();
		        		while (eventType != XmlPullParser.END_DOCUMENT){
		        			if(eventType==XmlPullParser.START_TAG && xmlParser.getName().equals("one")){
	        					response +="\n"+xmlParser.getAttributeValue(null, "name");
		        			}
		        			eventType = xmlParser.next(); 
		        		}
		        		FAQ faq = getFAQ(index, response, FAQ.type_parent_node);
		        		return faq;
		        	}else{
			        	if(xmlParser.getAttributeValue(null, "type").equals(FAQ.type_parent_node)){
			        		response = xmlParser.getAttributeValue(null, "description");
			        		eventType = xmlParser.next();  
			        		while (eventType != XmlPullParser.END_DOCUMENT){
			        			if(eventType==XmlPullParser.START_TAG){
			        				if(!xmlParser.getAttributeValue(null, "id").startsWith(index))
			        					break;
			        				if(xmlParser.getAttributeValue(null, "id").split("-").length-index.split("-").length==1){
			        					response +="\n"+xmlParser.getAttributeValue(null, "name");
			        				}
			        			}
			        			eventType = xmlParser.next(); 
			        		}
			        		return getFAQ(index, response, FAQ.type_parent_node);
			        	}else if(xmlParser.getAttributeValue(null, "type").equals(FAQ.type_leaf_node)){
			        		response = xmlParser.nextText();
			        		return getFAQ(index, response, FAQ.type_leaf_node);
			        	}else if(xmlParser.getAttributeValue(null, "type").equals(FAQ.type_ref_node)){
			        		return parserXML(xmlParser.getAttributeValue(null, "ref"), xml);
			        	}
		        	}
		        }
		        eventType = xmlParser.next();  
		    }  
		} catch (Exception e) {  
			Logger.e("parser xml error!", e);
		} 
		if(index.contains("-")){
			index = index.substring(0, index.lastIndexOf("-"));
		}else{
			index = "0";
		}
		return parserXML(index, xml);
	}
	
	public void parserAndSaveFaqProperty(String faqProperty){
		if(TextUtils.isEmpty(faqProperty))
			return;
		try {
			saveCachedJSON(Constant.getFAQPropertyUrl(), faqProperty);
		} catch (IOException e1) {
			Logger.e("Sync faq property but save error!");
		}
		try {  
			XmlPullParser xmlParser = Xml.newPullParser();
			xmlParser.setInput(new ByteArrayInputStream(faqProperty.getBytes("UTF-8")), "UTF-8");
		    int eventType = xmlParser.getEventType();  
		    while (eventType != XmlPullParser.END_DOCUMENT) {   
		    	if(eventType==XmlPullParser.START_TAG && xmlParser.getName().equals("faq")){
		    		Integer newVersion = Integer.parseInt(xmlParser.getAttributeValue(null, "version"));
		    		cacheService.setFAQVersion(newVersion);
		    		break;
		    	}
		    	eventType = xmlParser.next();
		    }
	    }catch (Exception e) {
	    	Logger.e("Sync faq property but parser version error!");
		}
	}

	private FAQ getFAQ(String index, String response, String type) {
		FAQ faq = new FAQ();
		faq.index = index;
		faq.response = response.trim();
		faq.type = type;
		return faq;
	}
	
	public int getFAQVersion(){
		return cacheService.getFAQVersion();
	}
	
}
