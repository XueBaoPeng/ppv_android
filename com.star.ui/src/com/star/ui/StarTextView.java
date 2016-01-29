package com.star.ui;

import java.util.Locale;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.KeyEvent;

public class StarTextView extends android.widget.TextView{
	private OnKeyListener mOnKeyListener;
	private static Typeface zh_cn;
	public StarTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setIncludeFontPadding(false);
	}
	public StarTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setIncludeFontPadding(false);
	}
	public StarTextView(Context context) {
		super(context);
		setIncludeFontPadding(false);
	}
	
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		setTypeface(getTypeface(getContext()));
	}

	private static boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        return locale.equals(Locale.CHINA);
	}
	
	@Override
	public void setOnKeyListener(OnKeyListener l) {
		mOnKeyListener = l;
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if(mOnKeyListener != null){
			return mOnKeyListener.onKey(this, event.getKeyCode(), event);
		}
		return super.dispatchKeyEvent(event);
	}
	
	public static synchronized Typeface getTypeface(Context context){
		if(isZh(context)){
			try{
				if(zh_cn == null){
					zh_cn = Typeface.createFromAsset(context.getAssets(),"fonts/lantinghei.TTF");
				}
				return zh_cn;
			}
			catch(Exception e){
			}
		}
		return Typeface.DEFAULT;
	}
}
