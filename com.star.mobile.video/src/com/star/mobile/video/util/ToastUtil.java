package com.star.mobile.video.util;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.star.mobile.video.R;

public class ToastUtil {

	private static final boolean isShowToast = false;
	
	public static void centerShowToast(Context context, String message) {
		if(context == null)
			return;
		Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		TextView textview = new TextView(context);
		textview.setTextSize(18);
		textview.setText(message);
		textview.setGravity(Gravity.CENTER);
		textview.setTextColor(Color.WHITE);
		textview.setBackgroundResource(R.drawable.tips_bg_tost_black);
		toast.setView(textview);
		toast.show();
	}

	public static void buttomShowToast(Context context, String message) {
		if(context == null)
			return;
		Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
		TextView textview = new TextView(context);
		textview.setTextSize(18);
		textview.setText(message);
		textview.setGravity(Gravity.CENTER);
		textview.setTextColor(Color.WHITE);
		textview.setBackgroundResource(R.drawable.tips_bg_tost_black);
		toast.setView(textview);
		toast.show();
	}

	public static void showToastWithImage(Context context, String msg, int imgId) {
		if(context == null)
			return;
		View layout = LayoutInflater.from(context).inflate(R.layout.toast_image, null);

		ImageView image = (ImageView) layout.findViewById(R.id.iv_toast_img);
		image.setImageResource(imgId);
		TextView text = (TextView) layout.findViewById(R.id.iv_toast_text);
		text.setText(msg);

		Toast toast = new Toast(context);
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(layout);
		toast.show();
	}
	
	/**
	 * isShowToast is true 弹出  GA 统计测试用
	 * @param context
	 * @param message
	 */
	public static void showToast(Context context, String message) {
		if(context == null)
			return;
		Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		TextView textview = new TextView(context);
		textview.setTextSize(18);
		textview.setText(message);
		textview.setGravity(Gravity.CENTER);
		textview.setTextColor(Color.WHITE);
		textview.setBackgroundResource(R.drawable.tips_bg_tost_black);
		toast.setView(textview);
		if(isShowToast) {
			toast.show();
		}
	}

}
