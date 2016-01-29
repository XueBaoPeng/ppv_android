package com.star.mobile.video.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;

import com.star.util.http.IOUtil;
import com.star.util.loader.FileCache;

public class ImageUtil {

	/**
     * 截取圆形图片,并加白边
     * @param bitmap 源Bitmap
     * @return
     */
    public static Bitmap getCircleBitmap(Bitmap bitmap) {
        int x = bitmap.getWidth();
        int y = bitmap.getHeight();
        final int color = 0xffffffff;
        Paint paint = new Paint();
        // 根据原来图片大小画一个矩形
        Rect rect;
        int min;
        if(x>y){
        	rect = new Rect(x/2-y/2, 0,x/2+y/2 , y);
        	min = y;
        }else{
        	rect = new Rect(0, y/2-x/2, x, y/2+x/2);
        	min = x;
        }
        
        Bitmap output = Bitmap.createBitmap(min,
                min, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        paint.setAntiAlias(true);
        paint.setColor(color);
        // 画出一个圆
        canvas.drawCircle(min/2, min/2, min/2, paint);
        //canvas.translate(-25, -6);
        // 取两层绘制交集,显示上层
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        
        // 将图片画上去
        canvas.drawBitmap(bitmap, rect, new Rect(0, 0, min, min), paint);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(0xff979797);
        int board_rate = 30;
        int d = min+min/board_rate;
        Bitmap output2 = Bitmap.createBitmap(d,
                d, Config.ARGB_8888);
        canvas = new Canvas(output2);
        canvas.drawCircle(d/2, d/2, d/2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        // 将circle draw 画上去
        rect = new Rect(0, 0, min,min);
        canvas.drawBitmap(output, rect, new Rect(min/board_rate/2, min/board_rate/2, d-min/board_rate/2,d-min/board_rate/2), paint);

        
        // 返回Bitmap对象
        return output2;
    }
    
    public static void measureLayout(final View view, final float scale) {
		ViewTreeObserver vto = view.getViewTreeObserver(); 
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { 
			private boolean hasMeasured;
			public boolean onPreDraw() { 
				if (hasMeasured == false) { 
					ViewGroup.LayoutParams params = view.getLayoutParams();
					int width = view.getMeasuredWidth(); 
					params.height = (int) (width*scale);
					Constant.POSTER_HEIGHT = params.height;
					Log.i("ImageUtil", "poster's width = "+width+", heigth = "+params.height);
					view.setLayoutParams(params);
					hasMeasured = true; 
				} 
				return true; 
			} 
		});
	}
    
    public static Bitmap loadImage(String imageFilePath) {
        if (imageFilePath == null || imageFilePath.length() < 1)
            return null;
        File file = new File(imageFilePath);
        Bitmap resizeBmp = null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = (int) (file.length() / (1*512*512) + 1); 
        resizeBmp = BitmapFactory.decodeFile(file.getPath(), opts);
        return resizeBmp;
    }
    
	/**
	 * url to bitmap
	 * @param url
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
    private static FileCache fileCache;
	public static Bitmap getBitmap(Context context, String url) {
		if(fileCache == null){
			fileCache = new FileCache(context);
		}
		File f = fileCache.getFile(url);
		Bitmap b = null;
		try {
			b = BitmapFactory.decodeStream(new FileInputStream(f));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if (b != null)
			return b;
		try {
			Bitmap bitmap = null;
			URL imageUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setInstanceFollowRedirects(true);
			InputStream is = conn.getInputStream();
			OutputStream os = new FileOutputStream(f);
			IOUtil.copyStream(is, os);
			bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
			os.close();
			Log.d("ImageUtil", "download and save bitmap!");
			return bitmap;
		} catch (Exception ex) {
			Log.e("ImageUtil", "download bitmap error!", ex);
			return null;
		}
	}
	
	/**
	  * 图片去色,返回灰度图片
	  * @param bmpOriginal 传入的图片
	  * @return 去色后的图片
	  */
	public static Bitmap toGrayscale(Bitmap bmpOriginal, int color) {
       int width, height;
       height = bmpOriginal.getHeight();
       width = bmpOriginal.getWidth();    
	   Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
       Canvas c = new Canvas(bmpGrayscale);
       Paint paint = new Paint();
       ColorMatrix cm = new ColorMatrix();
       cm.setSaturation(0);
       ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
       paint.setColorFilter(f);
       paint.setColor(color);
       c.drawBitmap(bmpOriginal, 0, 0, paint);
       return bmpGrayscale;
   }
	
	public static void measureView(View child) {  
        ViewGroup.LayoutParams params = child.getLayoutParams();  
        if (params == null) {  
            params = new ViewGroup.LayoutParams(  
                    ViewGroup.LayoutParams.MATCH_PARENT,  
                    ViewGroup.LayoutParams.WRAP_CONTENT);  
        }  
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, params.width);  
        int lpHeight = params.height;  
        int childHeightSpec;  
        if (lpHeight > 0) {  
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);  
        } else {  
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);  
        }  
        child.measure(childWidthSpec, childHeightSpec);  
    }
	
	public static boolean isAdapterViewAttach(AbsListView mScrollView){
        if (mScrollView != null && mScrollView.getChildCount() > 0) {
            if (mScrollView.getChildAt(0).getTop() < 0) {
                return false;
            }
        }
        return true;
    }
}
