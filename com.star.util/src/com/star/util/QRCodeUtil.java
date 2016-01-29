package com.star.util;

import java.util.Hashtable;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class QRCodeUtil {
	public static Bitmap createQRCodeBitmap(String text) throws WriterException{
		Hashtable<EncodeHintType,Object> hints = new Hashtable<EncodeHintType,Object>();
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
		BitMatrix bitMartix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, 300, 300,hints);
		int[] rectangle = bitMartix.getEnclosingRectangle();
		int left = rectangle[0] -5;
		int top = rectangle[1] - 5;
		int width = rectangle[2] + 10;
		int height = rectangle[3] + 10;
		Bitmap bmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		for(int i = 0 ; i < width; i++){
			for(int j = 0 ; j < height; j++){
				if(bitMartix.get(i + left, j + top)){
					bmp.setPixel(i, j, Color.BLACK);
				}else{
					bmp.setPixel(i, j, Color.WHITE);
				}
			}
		}
		return bmp;
	}
}
