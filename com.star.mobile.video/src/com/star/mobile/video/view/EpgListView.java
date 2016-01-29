package com.star.mobile.video.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ListView;

import com.star.mobile.video.R;

public class EpgListView extends ListView implements OnTouchListener{
	
	private final String TAG = this.getClass().getSimpleName();
	private long downTime;
	private boolean timeOut;
	private BaseEpgItemView currentLongClickView = null;
	private BaseEpgItemView lastLongClickView;
	protected boolean isLoading;
	private float mLastY;

	public EpgListView(Context context, AttributeSet attrs) {
		super(context, attrs);
//		setOnTouchListener(this);
	}

	public EpgListView(Context context) {
		this(context, null);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		try{
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				timeOut = false;
				int x = (int) event.getX();
				int y = (int) event.getY();
				int position = ((ListView)v).pointToPosition(x, y);
				if (position != ListView.INVALID_POSITION) {
					View view = ((ListView)v).getChildAt(position-((ListView)v).getFirstVisiblePosition());
					if(view instanceof BaseEpgItemView){
						currentLongClickView = (BaseEpgItemView)view;
					}else{
						currentLongClickView = (BaseEpgItemView)view.findViewById(R.id.chnGuid_list_item);
					}
				}
				mLastY = event.getY();
				downTime = System.currentTimeMillis();
				break;
			case MotionEvent.ACTION_MOVE:
				long current = System.currentTimeMillis();
				if(Math.abs(event.getY()-mLastY)>10){
					smoothBackLastone();
					return false;
				}
				if ((current-downTime>400)&&!timeOut) {
					Log.d(TAG, "Press timeout. ");
					if(!currentLongClickView.equals(lastLongClickView))
						smoothBackLastone();
					lastLongClickView = currentLongClickView;
					if (currentLongClickView != null && !currentLongClickView.isShownCancelBtn()){
						currentLongClickView.smoothScrollTo();
					}
					timeOut = true;
					return false;
				}
				break;
			case MotionEvent.ACTION_UP:
				if(Math.abs(event.getY()-mLastY)>10){
					return false;
				}
				if (!timeOut) {
					if(lastLongClickView != null && lastLongClickView.isShownCancelBtn()){
						lastLongClickView.smoothScrollBack();
					}else{
						currentLongClickView.onClick(currentLongClickView);
					}
				}
				break;
			}
		}catch(Exception e){
		}
		return false;
	}

	private void smoothBackLastone() {
		if(lastLongClickView != null && (lastLongClickView.isShownCancelBtn())) {
			lastLongClickView.smoothScrollBack();
		}
	}
}
