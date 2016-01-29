package com.star.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class AlphaLayout extends FrameLayout{
	private int alpha = 255;
	
	public AlphaLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public AlphaLayout(Context context) {
		super(context);
	}


	public void setIntAlpha(int alpha){
		this.alpha = alpha;
		invalidate();
	}
	
	public void setFloatAlpha(float alpha){
		if(alpha < 0.001){
			this.alpha = 0;
			invalidate();
		}
		if(alpha > 0.999){
			this.alpha = 255;
			invalidate();
		}
		this.alpha = (int)(255 * alpha + 0.5);
		invalidate();
	}
	
	@Override
	protected void dispatchDraw(Canvas canvas) {
		canvas.saveLayerAlpha(null, alpha, Canvas.ALL_SAVE_FLAG);
		super.dispatchDraw(canvas);
		canvas.restore();
	}

}
