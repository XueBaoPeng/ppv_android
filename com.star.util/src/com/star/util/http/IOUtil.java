package com.star.util.http;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.util.Log;

import com.star.util.InternalStorage;
import com.star.util.error.StarBoxException;
import com.star.util.json.JSONUtil;
import com.star.util.thread.ThreadLocalMap;
import com.star.util.thread.ThreadLocalString;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * 
 * @author yaohw
 */
public class IOUtil {
	private final static String TAG = IOUtil.class.getName();

	private static final int SET_CONNECTION_TIMEOUT = 5000;
	private static final int SET_SOCKET_TIMEOUT = 30000;

	protected static HttpClient httpclient;
	private static String TOKEN;
	public static Long sysTime;
	public static Context context;
	private static Integer appVerison;
	
	static {
		try {
			HttpParams params = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(params, SET_CONNECTION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(params, SET_SOCKET_TIMEOUT);
			HttpConnectionParams.setTcpNoDelay(params, true);
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));
			ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
			httpclient = new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
		}
	}
	
	private static class MySSLSocketFactory extends SSLSocketFactory {
		SSLContext sslContext = SSLContext.getInstance("TLS");

		public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException,
			KeyManagementException, KeyStoreException, UnrecoverableKeyException {
			super(truststore);
			TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] chain, String authType)
						throws CertificateException {
				}
	
				public void checkServerTrusted(X509Certificate[] chain, String authType)
						throws CertificateException {
				}
		
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};
			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port, boolean autoClose)
				throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}
	
	protected static HttpResponse excuteHttpRequest(HttpUriRequest httpReq) throws ClientProtocolException, IOException{
		httpReq.setHeader("token", TOKEN);
		if(context!=null) {
			httpReq.setHeader("lnCode", context.getResources().getConfiguration().locale.getLanguage());
		}
		if(appVerison!=null&&appVerison!=0){
			httpReq.setHeader("appVersion", appVerison+"");
		}
		httpReq.addHeader("Accept-Encoding", "gzip");
		HTTPReqt http = new HTTPReqt();
		http.setUrl(httpReq.getURI().getHost()+":"+httpReq.getURI().getPort());
		http.setPath(httpReq.getURI().getPath()+"_"+httpReq.getMethod());
		http.setParams("PARAM?"+httpReq.getURI().getQuery());
		ThreadLocalMap.put(ThreadLocalString.HTTP_REQ_TAG, http);
		HttpResponse hr = httpclient.execute(httpReq);
		Header[] headers = hr.getHeaders("server_time");
		if(headers!=null && headers.length>0){
			try{
				sysTime = Long.parseLong(headers[0].getValue());
			}catch (Exception e) {
				sysTime = null;
			}
		}
//		long time = (System.currentTimeMillis()-http.getStartDate());
//		Log.v("REQ", http.toString()+time);
		return hr;
	}
	
	public static String entityToString(HttpResponse response) {
		String json = null;
		try {
			InputStream instream = response.getEntity().getContent();
			Header contentEncoding = response.getFirstHeader("Content-Encoding");
			if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
				json = streamToString(instream, true);
			} else {
				json = streamToString(instream, false);
			}
			response.getEntity().consumeContent();
		} catch (ParseException e) {
			Log.e(TAG, "HttpResponse un GZip error", e);
		} catch (IOException e) {
			Log.e(TAG, "HttpResponse un GZip error", e);
		}
		return json;
	}
	
	public static String streamToString(InputStream is,boolean isGzip) {
		if (isGzip) {
			try {
				is = new GZIPInputStream(is);
			} catch (IOException e) {
				Log.e(TAG,"InputStream GZip error",e);
			}
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			Log.e(TAG, "Read data from inputStream error", e);
		}
		try {
			reader.close();
			is.close();
		} catch (IOException e) {
			Log.e(TAG,
					"After inputStream parsing, io close error",e);
		}
		return sb.toString();
	}

	/**
	 * send post http request.
	 * 
	 * @param url
	 * @param nameValuePairs
	 * @return
	 */
	public static HttpResponse httpPost(String url,	List<NameValuePair> nameValuePairs) {

		HttpPost httppost = new HttpPost(url);
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
			HttpResponse response = excuteHttpRequest(httppost);
			return response;
		} catch (Exception e) {
			throw new StarBoxException("HTTP Post error", e);
		}
	}
	
	public synchronized static HttpResponse httpPost(String url, Map<String, Object> params) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		if(params != null){
			for (String key : params.keySet()) {
				Object value = params.get(key);
				if(value instanceof String){
					nameValuePairs.add(new BasicNameValuePair(key,(String)value));
				}else if(value instanceof List<?>){
					List<?> list = (List<?>)value;
					for(int i=0; i<list.size();i++){
						nameValuePairs.add(new BasicNameValuePair(key, JSONUtil.getJSON(list.get(i))));
					}
				}else{
					nameValuePairs.add(new BasicNameValuePair(key,JSONUtil.getJSON(value)));
				}
			}
		}
		HttpResponse hr = httpPost(url, nameValuePairs);
		return hr;
	}
	
	public static HttpResponse httpPost(String url,String param) {

		HttpPost httppost = new HttpPost(url);
		try {
			httppost.setEntity(new StringEntity(param));
			HttpResponse response = excuteHttpRequest(httppost);
			return response;
		} catch (Exception e) {
			throw new StarBoxException("HTTP Post error", e);
		}
	}
	/**
	 * send post http request.
	 * 
	 * @param params  KV  V:String/JSON
	 * @param url
	 * @return JSON
	 * @throws NETException 
	 */
	public static String httpPostToJSON(Map<String,Object> params,String url) throws NETException{
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		for (String key : params.keySet()) {
			Object value = params.get(key);
			if(value instanceof String){
				nameValuePairs.add(new BasicNameValuePair(key,(String)value));
			}else if(value instanceof List<?>){
				List<?> list = (List<?>)value;
				for(int i=0; i<list.size();i++){
					nameValuePairs.add(new BasicNameValuePair(key, JSONUtil.getJSON(list.get(i))));
				}
			}else{
				nameValuePairs.add(new BasicNameValuePair(key,JSONUtil.getJSON(value)));
			}
		}
		HttpResponse hr = IOUtil.httpPost(url, nameValuePairs);
		return httpResonseToJSON(hr);
	}
	
	public static String httpPostToJSON(String url,Object param){
		HttpResponse hr = IOUtil.httpPost(url, JSONUtil.getJSON(param));
		try {
			return httpResonseToJSON(hr);
		} catch (Exception e) {
			throw new StarBoxException("Parse the data from http post error.", e);
		}
	}

	public synchronized static HttpResponse httpGet(String url) throws NETException {
		HttpGet httpGet = new HttpGet(url);
		try {
			return excuteHttpRequest(httpGet);
		} catch (Exception e) {
			Log.e(TAG, "Http Get error", e);
			throw new NETException("network error.", e);
		}
	}
	
	public synchronized static HttpResponse httpDelete(String url) throws NETException{
		HttpDelete httpdelete = new HttpDelete(url);
		try {
			return excuteHttpRequest(httpdelete);
		} catch (Exception e) {
			Log.e(TAG,"HTTP Delete error", e);
			throw new NETException("network error.", e);
		}
	}

	public static void copyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024*10;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}

	public static byte[] streamToByteArray(InputStream is) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			byte[] buffer = new byte[1024*10];
			int n = is.read(buffer);
			while (n > -1) {
			    bos.write(buffer,0,n);
			    n = is.read(buffer);
			}
			bos.flush();
		} catch (IOException e) {
			throw new StarBoxException("inputstream to byte[] error.", e);
		}
		return bos.toByteArray();
	}
	
	public static void ifExistAndWrite(String filePath, String fileName,
			InputStream is) {
		if ((new File(filePath + fileName)).exists() == false) {
			File f = new File(filePath);
			if (!f.exists()) {
				f.mkdir();
			}
			try {
				OutputStream os = new FileOutputStream(filePath + fileName);
				byte[] buffer = new byte[1024];
				int length;
				while ((length = is.read(buffer)) > 0) {
					os.write(buffer, 0, length);
				}
				// 关闭文件流
				os.flush();
				os.close();
				is.close();
			} catch (Exception e) {
				Log.e(TAG, "", e);
			}
		}
	}
	
	/**
	 * send post http request.
	 * 
	 * @return JSON
	 * @throws Exception 
	 */
	public synchronized static String httpResonseToJSON(HttpResponse hr) throws NETException {
		if(hr==null){
			throw new NETException("Http post error");
		}
		if(hr.getEntity() == null) {
			return null;
		}
		try {
			InputStream instream = hr.getEntity().getContent();
			Header contentEncoding = hr.getFirstHeader("Content-Encoding");
			String json = null;
			if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
				json = streamToString(instream, true);
			} else {
				json = streamToString(instream, false);
			}
			hr.getEntity().consumeContent();
			return json;
		} catch (Exception e) {
			Log.e(TAG,"Parse the data from http post error.", e);
			throw new NETException("Http post error!",e); 
		}
	}
	
	public synchronized static HttpResponse httpImagePost(Map<String, Object> params, String url, CompressFormat format) {
		HttpPost httppost = new HttpPost(url);
		try {
//			httppost.setHeader("lnCode", Locale.getDefault().getLanguage());
			MultipartEntity entity = new MultipartEntity();
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				if(entry.getKey().matches("image")) {
					httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 120000);
					entity.addPart(entry.getKey(),new InputStreamBody(bitmapToInputStream((Bitmap)entry.getValue(), format), "image/jpeg"));
				} else {
					ContentBody stringBody = new StringBody((String)entry.getValue());
					entity.addPart(entry.getKey(), stringBody);
				}
			   }
			httppost.setEntity(entity);
			HttpResponse response = excuteHttpRequest(httppost);
			return response;
		} catch (Exception e) {
			Log.e(TAG,"HTTP Post error", e);
		}
		return null;
	}
	
	// 将Bitmap转换成InputStream
	public static InputStream bitmapToInputStream(Bitmap bm, CompressFormat format) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(format, 100, baos);
		InputStream is = new ByteArrayInputStream(baos.toByteArray());
		return is;
	}
	
	public static boolean asstesFileCopySdCard(Context context,String fromFileName,String toPath,String toFileName) {
		try {
            if(!Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            	return false;
            }
            
            File sdCardDir = Environment.getExternalStorageDirectory();
            File saveFile = new File(sdCardDir, toFileName);
            if(saveFile.exists()) {
            	return true; 
            }
            InputStream inStream = context.getResources().getAssets().open(fromFileName);
            FileOutputStream outStream = new FileOutputStream(saveFile);
            byte[] bytes = new byte[1024];
            for (;;) {
				int count = inStream.read(bytes, 0, 1024);
				if (count == -1)
					break;
				outStream.write(bytes, 0, count);
			}
            outStream.close();
            inStream.close();
            return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static String getCachedJSON(String cachedFileName) {
//		long b = System.currentTimeMillis();
		String json = null;
		try {
			json = InternalStorage.getStorage(context).get(cachedFileName);
		} catch (Exception e) {
			Log.w(TAG, "get local data error!", e);
		}
//		Log.i("REQ_LOCAL", (System.currentTimeMillis()-b)+"");
		return json;
	}
	
	/**
	 * 删除本地json 文件
	 * @param cachedFileName
	 * @return
	 */
	public static boolean delCachedJSON(String cachedFileName) {
		return InternalStorage.getStorage(context).delJsonFile(cachedFileName);
	}
	
	public static String getTOKEN() {
		return TOKEN;
	}

	public static void setTOKEN(String tOKEN) {
		TOKEN = tOKEN;
	}

	public static void setAppVerison(Integer version){
		appVerison = version;
	}
	
}
