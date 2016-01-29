package com.star.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.Log;

import com.star.util.http.IOUtil;
import com.star.util.loader.FileCache;
import com.star.util.loader.MemoryCache;

/**
 * Image loaded lazily with three levels . cache|file|URL.
 * 
 * @author yaohw
 * 
 */
public class ImageView extends android.widget.ImageView{
	private static MemoryCache memoryCache = new MemoryCache();
	private static FileCache fileCache;
	private static Map<android.widget.ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<android.widget.ImageView, String>());
	private static ExecutorService loadImageThreadPool;
	private static String TAG = ImageView.class.getName();
	private Context context;
	private String url;
	private volatile boolean onCache = false;
	private Integer resId;
	private Finisher finisher;
	
	private Bitmap image;
	
	private static final Pattern urlPattern =  Pattern.compile("(http://[^/]+)(.*)", Pattern.CASE_INSENSITIVE);
	public ImageView(Context context) {
		super(context);
		this.context = context;
	}

	public ImageView(Context context,AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}
	
	public void setUrl(String url) {
		setUrl(url,true);
	}
	
	public void setUrl(String url,boolean dynamicDomain) {
		init();
		if(dynamicDomain){
			this.url = replaceResourceUrl(url);
		}else{
			this.url = url;
		}
		onCache = false;
		requestLayout();
	}

	/**
	 * 以前资源文件域名或 ip port  取消 ，
	 * 变更为相对路径
	 * @param url
	 * @return
	 */
	private String replaceResourceUrl(String path) {
		Matcher m =  urlPattern.matcher(path);
		if(m.find()){
			path = context.getString(R.string.resource_prefix_url)+m.group(2);
		}
		return path;
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if(url!=null&&!onCache){
			displayImage(url,this, getWidth(), getHeight());
			onCache = true;
		}
	}
	
	private void init(){
		if(fileCache==null){
			fileCache = new FileCache(context);
		}
		if(loadImageThreadPool==null){
			loadImageThreadPool = Executors.newFixedThreadPool(4);
		}
		if(this.resId!=null){
			setImageResource(this.resId);
		}
	}
	@Override
	public void setImageResource(int resId) {
		super.setImageResource(resId);
		this.resId = resId;
	}
	
	protected void displayImage(String url, ImageView imageView, int reqWidth, int reqHeight) {
		imageViews.put(imageView, url);
		Bitmap bitmap = memoryCache.get(url);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
			image=bitmap;
			if(finisher!=null){
				finisher.run();
			}
		} else {
			queuePhoto(url, imageView, reqWidth, reqHeight);
		}
	}

	protected void displayImage(String url, ImageView imageView) {
		displayImage(url, imageView, Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	private void queuePhoto(String url, ImageView imageView, int reqWidth, int reqHeight) {
		PhotoToLoad p = new PhotoToLoad(url, imageView);
		loadImageThreadPool.submit(new PhotosLoader(p, reqWidth, reqHeight));
	}

	/**
	 * url to bitmap
	 * @param url
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap getBitmap(String url, int reqWidth, int reqHeight) {
		File f = fileCache.getFile(url);

		// from SD cache
		Bitmap b = decodeFile(f, reqWidth, reqHeight);
		if (b != null)
			return b;
		InputStream is = null;
		// from web
		try {
			long be = System.currentTimeMillis();
			Bitmap bitmap = null;
			URL imageUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setInstanceFollowRedirects(true);
			is = conn.getInputStream();
			OutputStream os = new FileOutputStream(f);
			IOUtil.copyStream(is, os);
			os.close();
			long be1 = System.currentTimeMillis();
			Log.d(TAG, (be1-be)+"ms used["+f.length()+"B] when load image from ["+url+"]");
//			bitmap = decodeFile(f, reqWidth, reqHeight);
			bitmap = BitmapFactory.decodeStream(is); 
			Log.d(TAG, (System.currentTimeMillis()-be1)+"ms used when scale image");
			return bitmap;
		} catch (Exception ex) {
			Log.w(TAG, "Cache image error.", ex);
			if(is !=null){
				return BitmapFactory.decodeStream(is); 
			}else{
				return null;	
			}
		}
	}

	/**
	 * decodes image and scales it to reduce memory consumption
	 * 
	 * @param f
	 * @return
	 */
	private static Bitmap decodeFile(File f, int reqWidth, int reqHeight) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = calculateInSampleSize(o, reqWidth, reqHeight);
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
			return null;
		}
	}

	/**
	 * 计算图片的缩放值
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}

	// Task for the queue
	private class PhotoToLoad {
		public String url;
		public ImageView imageView;

		public PhotoToLoad(String u, ImageView i) {
			url = u;
			imageView = i;
		}
	}

	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;
		int reqWidth = Integer.MAX_VALUE;
		int reqHeight = Integer.MAX_VALUE;

		PhotosLoader(PhotoToLoad photoToLoad, int reqWidth, int reqHeight) {
			this.photoToLoad = photoToLoad;
			this.reqWidth = reqWidth;
			this.reqHeight = reqHeight;
		}

		@Override
		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			Bitmap bmp = getBitmap(photoToLoad.url, reqWidth, reqHeight);
			memoryCache.put(photoToLoad.url, bmp);
			if (imageViewReused(photoToLoad))
				return;
			BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
			Activity a = (Activity) photoToLoad.imageView.getContext();
			a.runOnUiThread(bd);
		}
	}

	boolean imageViewReused(PhotoToLoad photoToLoad) {
		String tag = imageViews.get(photoToLoad.imageView);
		if (tag == null || !tag.equals(photoToLoad.url))
			return true;
		return false;
	}

	/**
	 * Used to display bitmap in the UI thread
	 * 
	 * @author yaohw
	 * 
	 */
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
		}

		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			if (bitmap != null)
			{	photoToLoad.imageView.setImageBitmap(bitmap);
				image=bitmap;
				if(finisher!=null){
					finisher.run();
				}
			}	
//			else
//				photoToLoad.imageView.setImageResource(stub_id);
		}
	}

	public static void clearCache() {
		if(memoryCache!=null){
			memoryCache.clear();
		}
		if(fileCache!=null){
			fileCache.clear();
		}
	}
	
	public void setFinisher(Finisher finisher) {
		this.finisher = finisher;
	}

	public Bitmap getImage() {
		return image;
	}

	public interface Finisher{
		void run();
	}
}
