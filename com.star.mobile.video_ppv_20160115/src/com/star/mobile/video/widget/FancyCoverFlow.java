/*
 * Copyright 2013 David Schreiber
 *           2013 John Paul Nalog
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.star.mobile.video.widget;


import com.star.mobile.video.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Transformation;
import android.widget.Gallery;
import android.widget.Scroller;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class FancyCoverFlow extends Gallery {

	// =============================================================================
    // Constants
    // =============================================================================

    public static final int ACTION_DISTANCE_AUTO = Integer.MAX_VALUE;

    public static final float SCALEDOWN_GRAVITY_TOP = 0.0f;

    public static final float SCALEDOWN_GRAVITY_CENTER = 0.5f;

    public static final float SCALEDOWN_GRAVITY_BOTTOM = 1.0f;

    // =============================================================================
    // Private members
    // =============================================================================

    private float reflectionRatio = 0.4f;

    private int reflectionGap = 20;

    private boolean reflectionEnabled = false;

    /**
     * TODO: Doc
     */
    private float unselectedAlpha;

    /**
     * Camera used for view transformation.
     */
    private Camera transformationCamera;

    /**
     * TODO: Doc
     */
    private int maxRotation = 75;

    /**
     * Factor (0-1) that defines how much the unselected children should be scaled down. 1 means no scaledown.
     */
    private float unselectedScale;

    /**
     * TODO: Doc
     */
    private float scaleDownGravity = SCALEDOWN_GRAVITY_CENTER;

    /**
     * Distance in pixels between the transformation effects (alpha, rotation, zoom) are applied.
     */
    private int actionDistance;

    /**
     * Saturation factor (0-1) of items that reach the outer effects distance.
     */
    private float unselectedSaturation;
    
    private boolean isOverrideOnFling = false ;
    
    private Scroller mScroller;
    /**
     * 是否移动
     */
    private boolean isScroller = true;  

    // =============================================================================
    // Constructors
    // =============================================================================

    public FancyCoverFlow(Context context) {
        super(context);
        this.initialize(context);
    }

    public FancyCoverFlow(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize(context);
        this.applyXmlAttributes(attrs);
    }

    public FancyCoverFlow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.initialize(context);
        this.applyXmlAttributes(attrs);
    }

    private void initialize(Context context) {
        this.transformationCamera = new Camera();
        mScroller = new Scroller(context);
        this.setSpacing(0);
    }

    private void applyXmlAttributes(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.FancyCoverFlow);

        this.actionDistance = a.getInteger(R.styleable.FancyCoverFlow_actionDistance, ACTION_DISTANCE_AUTO);
        this.scaleDownGravity = a.getFloat(R.styleable.FancyCoverFlow_scaleDownGravity, 1.0f);
        this.maxRotation = a.getInteger(R.styleable.FancyCoverFlow_maxRotation, 45);
        this.unselectedAlpha = a.getFloat(R.styleable.FancyCoverFlow_unselectedAlpha, 0.3f);
        this.unselectedSaturation = a.getFloat(R.styleable.FancyCoverFlow_unselectedSaturation, 0.0f);
        this.unselectedScale = a.getFloat(R.styleable.FancyCoverFlow_unselectedScale, 0.75f);
    }

    // =============================================================================
    // Getter / Setter
    // =============================================================================

    public float getReflectionRatio() {
        return reflectionRatio;
    }

    public void setReflectionRatio(float reflectionRatio) {
        if (reflectionRatio <= 0 || reflectionRatio > 0.5f) {
            throw new IllegalArgumentException("reflectionRatio may only be in the interval (0, 0.5]");
        }

        this.reflectionRatio = reflectionRatio;

        if (this.getAdapter() != null) {
            ((FancyCoverFlowAdapter) this.getAdapter()).notifyDataSetChanged();
        }
    }

    public int getReflectionGap() {
        return reflectionGap;
    }

    public void setReflectionGap(int reflectionGap) {
        this.reflectionGap = reflectionGap;

        if (this.getAdapter() != null) {
            ((FancyCoverFlowAdapter) this.getAdapter()).notifyDataSetChanged();
        }
    }

    public boolean isReflectionEnabled() {
        return reflectionEnabled;
    }

    public void setReflectionEnabled(boolean reflectionEnabled) {
        this.reflectionEnabled = reflectionEnabled;

        if (this.getAdapter() != null) {
            ((FancyCoverFlowAdapter) this.getAdapter()).notifyDataSetChanged();
        }
    }

    /**
     * Use this to provide a {@link FancyCoverFlowAdapter} to the coverflow. This
     * method will throw an {@link ClassCastException} if the passed adapter does not
     * subclass {@link FancyCoverFlowAdapter}.
     *
     * @param adapter
     */
    @Override
    public void setAdapter(SpinnerAdapter adapter) {
        if (!(adapter instanceof FancyCoverFlowAdapter)) {
            throw new ClassCastException(FancyCoverFlow.class.getSimpleName() + " only works in conjunction with a " + FancyCoverFlowAdapter.class.getSimpleName());
        }

        super.setAdapter(adapter);
    }

    /**
     * Returns the maximum rotation that is applied to items left and right of the center of the coverflow.
     *
     * @return
     */
    public int getMaxRotation() {
        return maxRotation;
    }

    /**
     * Sets the maximum rotation that is applied to items left and right of the center of the coverflow.
     *
     * @param maxRotation
     */
    public void setMaxRotation(int maxRotation) {
        this.maxRotation = maxRotation;
    }

    /**
     * TODO: Write doc
     *
     * @return
     */
    public float getUnselectedAlpha() {
        return this.unselectedAlpha;
    }

    /**
     * TODO: Write doc
     *
     * @return
     */
    public float getUnselectedScale() {
        return unselectedScale;
    }

    /**
     * TODO: Write doc
     *
     * @param unselectedScale
     */
    public void setUnselectedScale(float unselectedScale) {
        this.unselectedScale = unselectedScale;
    }

    /**
     * TODO: Doc
     *
     * @return
     */
    public float getScaleDownGravity() {
        return scaleDownGravity;
    }

    /**
     * TODO: Doc
     *
     * @param scaleDownGravity
     */
    public void setScaleDownGravity(float scaleDownGravity) {
        this.scaleDownGravity = scaleDownGravity;
    }

    /**
     * TODO: Write doc
     *
     * @return
     */
    public int getActionDistance() {
        return actionDistance;
    }

    /**
     * TODO: Write doc
     *
     * @param actionDistance
     */
    public void setActionDistance(int actionDistance) {
        this.actionDistance = actionDistance;
    }

    /**
     * TODO: Write doc
     *
     * @param unselectedAlpha
     */
    @Override
    public void setUnselectedAlpha(float unselectedAlpha) {
        super.setUnselectedAlpha(unselectedAlpha);
        this.unselectedAlpha = unselectedAlpha;
    }

    /**
     * TODO: Write doc
     *
     * @return
     */
    public float getUnselectedSaturation() {
        return unselectedSaturation;
    }

    /**
     * TODO: Write doc
     *
     * @param unselectedSaturation
     */
    public void setUnselectedSaturation(float unselectedSaturation) {
        this.unselectedSaturation = unselectedSaturation;
    }

    public void setOverrideOnFling(boolean isOverrideOnFling) {
    	this.isOverrideOnFling = isOverrideOnFling;
    }
    
    // =============================================================================
    // Supertype overrides
    // =============================================================================

	@Override
    protected boolean getChildStaticTransformation(View child, Transformation t) {
        // We can cast here because FancyCoverFlowAdapter only creates wrappers.
        FancyCoverFlowItemWrapper item = (FancyCoverFlowItemWrapper) child;

        // Since Jelly Bean childs won't get invalidated automatically, needs to be added for the smooth coverflow animation
        if (android.os.Build.VERSION.SDK_INT >= 16) {
            item.invalidate();
        }
        final int coverFlowWidth = this.getWidth();
        final int coverFlowCenter = coverFlowWidth / 2;
        final int childWidth = item.getWidth();
        final int childHeight = item.getHeight();
        final int childCenter = item.getLeft() + childWidth / 2;
        // Use coverflow width when its defined as automatic.
        final int actionDistance = (this.actionDistance == ACTION_DISTANCE_AUTO) ? (int) ((coverFlowWidth + childWidth) / 2.0f) : this.actionDistance;

        // Calculate the abstract amount for all effects.
        final float effectsAmount = Math.min(1.0f, Math.max(-1.0f, (1.0f / actionDistance) * (childCenter - coverFlowCenter)));

        // Clear previous transformations and set transformation type (matrix + alpha).
        t.clear();
        t.setTransformationType(Transformation.TYPE_BOTH);

        // Alpha
        if (this.unselectedAlpha != 1) {
            final float alphaAmount = (this.unselectedAlpha - 1) * Math.abs(effectsAmount) + 1;
            t.setAlpha(alphaAmount);
        }

        // Saturation
        if (this.unselectedSaturation != 1) {
            // Pass over saturation to the wrapper.
            final float saturationAmount = (this.unselectedSaturation - 1) * Math.abs(effectsAmount) + 1;
            item.setSaturation(saturationAmount);
        }

        final Matrix imageMatrix = t.getMatrix();

        // Apply rotation.
        if (this.maxRotation != 0) {
            final int rotationAngle = (int) (-effectsAmount * this.maxRotation);
            this.transformationCamera.save();
            this.transformationCamera.rotateY(rotationAngle);
            this.transformationCamera.getMatrix(imageMatrix);
            this.transformationCamera.restore();
        }

        // Zoom.
        if (this.unselectedScale != 1) {
            final float zoomAmount = (this.unselectedScale - 1) * Math.abs(effectsAmount) + 1;
            // Calculate the scale anchor (y anchor can be altered)
            final float translateX = childWidth / 2.0f;
            final float translateY = childHeight * this.scaleDownGravity;
            imageMatrix.preTranslate(-translateX, -translateY);
            imageMatrix.postScale(zoomAmount, zoomAmount);
            imageMatrix.postTranslate(translateX, translateY);
        }

        
        return true;
    }

    // =============================================================================
    // Public classes
    // =============================================================================

    public static class LayoutParams extends Gallery.LayoutParams {
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int w, int h) {
            super(w, h);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }
    
    
 // 用户按下触摸屏，并拖动，由1个MotionEvent ACTION_DOWN, 多个ACTION_MOVE触发
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//    	System.out.println("onScroll:"+e1+" e2="+e2);
    	return super.onScroll(e1, e2, distanceX, distanceY);
    }
 // 用户按下触摸屏、快速移动后松开，由1个MotionEvent ACTION_DOWN, 多个ACTION_MOVE, 1个ACTION_UP触发
    // 参数解释：
	// e1：第1个ACTION_DOWN MotionEvent
	// e2：最后一个ACTION_MOVE MotionEvent
	// velocityX：X轴上的移动速度，像素/秒
	// velocityY：Y轴上的移动速度，像素/秒
	// 触发条件 ：
	// X轴的坐标位移大于FLING_MIN_DISTANCE，且移动速度大于FLING_MIN_VELOCITY个像素/秒
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//    	final int FLING_MIN_DISTANCE = 72, FLING_MIN_VELOCITY = 200;   
//        if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE   
//                && Math.abs(velocityX) < FLING_MIN_VELOCITY) {   
//            // Fling left    
//            Log.i("MyGesture", "Fling left");   
//            Toast.makeText(getContext(), "Fling Left", Toast.LENGTH_SHORT).show();   
//        } else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE   
//                && Math.abs(velocityX) < FLING_MIN_VELOCITY) {   
//            // Fling right    
//            Log.i("MyGesture", "Fling right");   
//            Toast.makeText(getContext(), "Fling Right", Toast.LENGTH_SHORT).show();   
//        }   
//    	
    	if (isOverrideOnFling) {
    		isOverrideOnFling = false;
    		super.onFling(e1, e2, velocityX, velocityY);
		}else {
			return false;
		}
		return false;
    }
    
    private class FlingGestureDetector extends GestureDetector.SimpleOnGestureListener {
    	   // 用户轻触触摸屏，由1个MotionEvent ACTION_DOWN触发    
    	  //Touch down时触发，不论是touch (包括long) ，scroll
    	    @Override   
    	    public boolean onDown(MotionEvent e) {   
    	        // TODO Auto-generated method stub    
    	        System.out.println("onDown");   
    	        return false;   
    	    } 
    	   /*  
    	     * 用户轻触触摸屏，尚未松开或拖动，由一个1个MotionEvent ACTION_DOWN触发  
    	     * 注意和onDown()的区别，强调的是没有松开或者拖动的状态 (单击没有松开或者移动时候就触发此事件，再触发onLongPress事件)  
    	     */   
    	//Touch了还没有滑动时触发
    	     //（与onDown，onLongPress比较
    	     //onDown只要Touch down一定立刻触发。

    	    @Override   
    	    public void onShowPress(MotionEvent e) {   
    	        // TODO Auto-generated method stub    
    	        System.out.println("onShowPress");   
    	    }   
    	   
    	    // 用户（轻触触摸屏后）松开，由一个1个MotionEvent ACTION_UP触发    
    	    @Override   
    	    public boolean onSingleTapUp(MotionEvent e) {   
    	        // TODO Auto-generated method stub    
    	        System.out.println("onSingleTopUp");   
    	        return false;   
    	    }   
    	   
    	    // 用户按下触摸屏，并拖动，由1个MotionEvent ACTION_DOWN, 多个ACTION_MOVE触发    
    	    @Override   
    	    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,   
    	            float distanceY) {   
    	        System.out.println("onScroll");   
    	        // TODO Auto-generated method stub    
    	        return false;   
    	    }   
    	   
    	    // 用户长按触摸屏，由多个MotionEvent ACTION_DOWN触发 
    	//Touch了不移动一直Touch down时触发
    	     //Touchdown后过一会没有滑动先触发onShowPress再是onLongPress。
    	   
    	    @Override   
    	    public void onLongPress(MotionEvent e) {   
    	        // TODO Auto-generated method stub    
    	        System.out.println("onLongPress");   
    	    }   
    	   
    	    /*  
    	     * 用户按下触摸屏、快速移动后松开，由1个MotionEvent ACTION_DOWN, 多个ACTION_MOVE,  
    	     * 1个ACTION_UP触发(non-Javadoc)  
    	     * Fling事件的处理代码：除了第一个触发Fling的ACTION_DOWN和最后一个ACTION_MOVE中包含的坐标等信息外  
    	     * ，我们还可以根据用户在X轴或者Y轴上的移动速度作为条件  
    	     * 比如下面的代码中我们就在用户移动超过100个像素，且X轴上每秒的移动速度大于200像素时才进行处理。  
    	     *   
    	     * @see Android.view.GestureDetector.OnGestureListener#onFling(android.view.  
    	     * MotionEvent, Android.view.MotionEvent, float, float)  
    	     * 这个例子中，tv.setLongClickable( true )是必须的，因为  
    	     * 只有这样，view才能够处理不同于Tap（轻触）的hold（即ACTION_MOVE，或者多个ACTION_DOWN）  
    	     * ，我们同样可以通过layout定义中的Android:longClickable来做到这一点  
    	     */   
    	    @Override   
    	    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,   
    	            float velocityY) {   
    	        // TODO Auto-generated method stub    
    	        // 参数解释：    
    	        // e1：第1个ACTION_DOWN MotionEvent    
    	        // e2：最后一个ACTION_MOVE MotionEvent    
    	        // velocityX：X轴上的移动速度，像素/秒    
    	        // velocityY：Y轴上的移动速度，像素/秒    
    	   
    	        // 触发条件 ：    
    	        // X轴的坐标位移大于FLING_MIN_DISTANCE，且移动速度大于FLING_MIN_VELOCITY个像素/秒    
    	        final int FLING_MIN_DISTANCE = 100, FLING_MIN_VELOCITY = 200;   
    	        if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE   
    	                && Math.abs(velocityX) > FLING_MIN_VELOCITY) {   
    	            // Fling left    
    	            Log.i("MyGesture", "Fling left");   
    	            Toast.makeText(getContext(), "Fling Left", Toast.LENGTH_SHORT).show();   
    	        } else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE   
    	                && Math.abs(velocityX) > FLING_MIN_VELOCITY) {   
    	            // Fling right    
    	            Log.i("MyGesture", "Fling right");   
    	            Toast.makeText(getContext(), "Fling Right", Toast.LENGTH_SHORT).show();   
    	        }   
    	        return true;   
    	    }   
    
    }
    
    
    
}



