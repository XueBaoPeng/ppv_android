package com.star.mobile.video.util;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

public class BitmapUtil {

	/**
	 * 转换图片成圆形
	 * 
	 * @param bitmap
	 *            传入Bitmap对象
	 * @return
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			top = 0;
			bottom = width;
			left = 0;
			right = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}

		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right,
				(int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top,
				(int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);

		paint.setAntiAlias(true);

		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);
		return output;
	}

	public static Bitmap scaleBitmap(Bitmap bitmap, int size) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 80, out);
		float zoom = (float) Math.sqrt(size * 1024
				/ (float) out.toByteArray().length);

		Matrix matrix = new Matrix();
		matrix.setScale(zoom, zoom);

		Bitmap result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);

		out.reset();
		result.compress(Bitmap.CompressFormat.PNG, 80, out);
		while (out.toByteArray().length > size * 1024) {
			System.out.println(out.toByteArray().length);
			matrix.setScale(0.9f, 0.9f);
			result = Bitmap.createBitmap(result, 0, 0, result.getWidth(),
					result.getHeight(), matrix, true);
			out.reset();
			result.compress(Bitmap.CompressFormat.PNG, 80, out);
		}
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
			bitmap = null;
		}
		return result;
	}
	
	public static Bitmap toConformBitmap(Bitmap background, Bitmap foreground) {
        if( background == null ) {   
           return null;   
        }   
        int bgWidth = background.getWidth();   
        int bgHeight = background.getHeight();   
        Bitmap newbmp = Bitmap.createBitmap(bgWidth, bgHeight, Config.ARGB_8888);  
        Paint paint = new Paint(); 
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        Rect rect = new Rect(0, 0, bgWidth, bgHeight);  
        
        Canvas cv = new Canvas(newbmp);   
        cv.drawBitmap(background, 0, 0, null); 
        cv.drawBitmap(foreground, null, rect, paint);
        cv.save(Canvas.ALL_SAVE_FLAG);
	    cv.restore();
        return newbmp;   
   }
	
	/**
	 * 获得圆角图片的方法  
	 * @param bitmap
	 * @param roundPx 圆角的像素值
	 * @return
	 */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap,float roundPx){  
          
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap  
                .getHeight(), Config.ARGB_8888);  
        Canvas canvas = new Canvas(output);  
   
        final int color = 0xff424242;  
        final Paint paint = new Paint();  
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());  
        final RectF rectF = new RectF(rect);  
   
        paint.setAntiAlias(true);  
        canvas.drawARGB(0, 0, 0, 0);  
        paint.setColor(color);  
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);  
   
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));  
        canvas.drawBitmap(bitmap, rect, rect, paint);  
   
        return output;  
    }
}
