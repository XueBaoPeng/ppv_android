package com.star.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.Log;

import com.google.zxing.WriterException;
import com.star.util.QRCodeUtil;

public class QRImageView extends android.widget.ImageView{
	final static String TAG = "QRImageView";
	private String qrValue;
	private Bitmap bmp;
	private Bitmap lastBmp;
	private Thread lastThread;
	public QRImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setQRValue(String qrValue){
		if(lastThread != null){
			lastThread.interrupt();
		}
		if(bmp != null){
			bmp.recycle();
			bmp = null;
		}
		setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
		if(qrValue==null){
			qrValue="No string for QR.";
		}
		this.qrValue = qrValue;
		new QRMakeThread().start();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if(bmp != null  && lastBmp != bmp){
			lastBmp = bmp;
			setImageBitmap(bmp);
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	class QRMakeThread extends Thread{
		@Override
		public void run() {
			try {
				Log.d(TAG, "create qr bitmap : " + qrValue);
				bmp = QRCodeUtil.createQRCodeBitmap(qrValue);
				Log.d(TAG, "qr bitmap create complete : " + qrValue);
				post(new Runnable() {
					@Override
					public void run() {
						requestLayout();
					}
				});
				Log.d(TAG, "request set qr bitmap : " + qrValue);
			} catch (WriterException e) {
				Log.e("QRImageView", "", e);
			}
		}
	}
}
