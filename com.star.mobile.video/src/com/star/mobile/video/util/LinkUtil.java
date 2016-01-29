package com.star.mobile.video.util;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;





import org.apache.http.Header;
import org.unbescape.html.HtmlEscape;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.star.cms.model.Chart;
import com.star.cms.model.vo.ChannelVO;
import com.star.mobile.video.model.LinkPkg;
import com.star.util.Log;

public class LinkUtil {

	//chat.msg "http://www.google.com/index.html###title=Big News###imgurl=http://www.google.com/img.jpg###description=a great news"

	public static boolean isValidURL(String url) {  

		URL u = null;

		try {  
			u = new URL(url);  
		} catch (MalformedURLException e) {  
			return false;  
		}

		try {  
			u.toURI();  
		} catch (URISyntaxException e) {  
			return false;  
		}  

		return true;  
	} 

	private static String url = null;

	public static String processLink(String msg, String title) {	
		return processLink(msg, title, null, null);		
	}

	public static String processLink(String msg, String title, String imgurl  ) {	
		return processLink(msg, title, imgurl, null);				
	}

	public static String processLink(String msg, String title, String imgurl, String description ) {

		return  processLinkPkg( msg,  title,  imgurl,  description ).toString();
	}

	public static LinkPkg processLink_(String msg) {	
		return processLinkPkg(msg, null, null, null);		
	}

	public static LinkPkg processLinkPkg(String msg, String title, String imgurl, String description ) {	
		try{
			String  source = null;
			String [] segments = null;

			if(msg != null) segments = msg.split("###",5);

			for(String s : segments){
				if(s.toLowerCase().startsWith("http")) {
					url = s;
				} else if(title == null && s.startsWith("title")) {
					title = s.substring(6);
				} else if(imgurl == null && s.startsWith("imgurl")) {
					imgurl = s.substring(7);
				} else if(description == null && s.startsWith("description")) {
					description = s.substring(12);
					description=description.replaceAll("\"", "");
				} else if(source == null && s.startsWith("source")) {
					source = s.substring(7);
					if(source.equals("failed")) return null;
				}     

			}

			if(source != null && (title == null || imgurl == null || description == null)) {

				source = HtmlEscape.unescapeHtml(source);
				source = HtmlEscape.unescapeHtml(source);

				Pattern p;
				Matcher m;

				if(title == null) {

					p = Pattern.compile("<[^>]*title[^>]*>(.*)<[^>]*/title[^>]*>",Pattern.CASE_INSENSITIVE);
					m = p.matcher(source);

					if(m.find()) {
						title = m.group(1);

					} 
				}

				if(imgurl == null) {

					p = Pattern.compile("<img[^>]*src\\s*=\\s*['\"]?([^>'\"\\s]*)['\"]?\\s*[^>]*>",Pattern.CASE_INSENSITIVE);
					m = p.matcher(source);

					while(m.find()) {
						imgurl = m.group(1);
						if(imgurl.length() == 0) imgurl = null;
						else break;
					}  

					if(imgurl != null && !imgurl.startsWith("http"))  {
						int slashIndex = url.lastIndexOf("/");
						if(slashIndex > 0 && url.charAt(slashIndex - 1) != '/')
							imgurl = url.substring(0, slashIndex) + "/" + imgurl;
					}
					if(!isValidURL(imgurl)) imgurl = null;

				}

				if(description == null) {
					p = Pattern.compile("<meta[^>]*['\"]?description['\"]?[^>]*content\\s*=\\s*['\"]?([^>]*)['\"]?\\s*[^>]*>",Pattern.CASE_INSENSITIVE);
					m = p.matcher(source);

					if(m.find()) {
						description = m.group(1);
						description=description.replaceAll("\"", "");
					}
					if(description!=null &&description.length()==0){
						description=null;
					}
					if(description != null && description.length() > 50) description = description.substring(0, 50);
					if(description == null)  description = url;
				}
			}

			else if (source == null && (title == null || imgurl == null || description == null)  && url != null) {
				AsyncHttpClient client = new AsyncHttpClient();
				client.get(url, new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers, byte[] response) {
						String result = url  + "###source=" + new String(response);
						processLinkPkg(result,null,null,null);

					}

					@Override
					public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
						String result = url  + "###source=failed";
						processLinkPkg(result,null,null,null);

					}


				});
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		LinkPkg linkPkg = new LinkPkg();
		linkPkg.setUrl(url);
		linkPkg.setTitle(title);
		linkPkg.setImgurl(imgurl);
		linkPkg.setDescription(description);


		return linkPkg;		
	}

}
