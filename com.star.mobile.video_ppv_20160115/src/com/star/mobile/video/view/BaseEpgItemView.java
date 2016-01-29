package com.star.mobile.video.view;

import java.util.TimerTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Scroller;
import android.widget.TextView;

import com.star.cms.model.Tenb;
import com.star.cms.model.dto.DoTaskResult;
import com.star.cms.model.vo.ChannelVO;
import com.star.cms.model.vo.ProgramVO;
import com.star.mobile.video.AlertManager;
import com.star.mobile.video.R;
import com.star.mobile.video.home.HomeActivity;
import com.star.mobile.video.me.mycoins.TaskService;
import com.star.mobile.video.model.MyTimer;
import com.star.mobile.video.model.TaskCode;
import com.star.mobile.video.service.ChannelService;
import com.star.mobile.video.service.ProgramService;
import com.star.mobile.video.tenb.TenbService;
import com.star.mobile.video.util.ApplicationUtil;
import com.star.mobile.video.util.DataCache;
import com.star.mobile.video.util.LoadingDataTask;
import com.star.mobile.video.util.ToastUtil;
import com.star.util.app.GA;
import com.star.util.loader.OnResultListener;

public class BaseEpgItemView extends LinearLayout implements OnClickListener{

	protected final String TAG = this.getClass().getSimpleName();
	protected TextView tv_epg_name;
	protected ProgramVO program;
	protected ChannelVO mChannel;
	protected TextView tv_epg_startime;
	protected ProgressBar pb_play_rate;
	protected ImageView ivEpgItemMenu;
	protected ProgramService epgService;
	protected ChannelService chnService;
	protected TextView tv_channel_number;
	protected com.star.ui.ImageView iv_channel_icon;
	protected ImageView iv_alert_icon;
	protected View menuParentView;
//	protected ImageView iv_alert_cancle;
	private Scroller mScroller;
	protected ImageView ivEgpLine;
	private boolean isShowCancleBtn;
	private TaskService taskService;
	private MyTimer timer;
	private final int REFREASH_PROGRESS = 0;
	private final int VISIABLE_PLAY_BTN = 1;
	private boolean isPlay = false;
	private boolean isClickable = false;
	private boolean isNewProgram = true;
	
	
	protected Context mContext;
	
	public Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == REFREASH_PROGRESS && pb_play_rate!=null){
				updateProgressbarStatus();
			}/*else if(msg.what == VISIABLE_PLAY_BTN && ivEpgItemPlay != null){
				ivEpgItemPlay.setVisibility(View.VISIBLE);
			}*/
		}
	};
	
	protected void scheduleEPGProgress() {
		isNewProgram = true;
		if(this instanceof EpgChnGuideItemView){
//			if(ivEpgItemPlay!=null&&ivEpgItemPlay.getVisibility()==View.VISIBLE){
//				ivEpgItemPlay.setVisibility(View.GONE);
//			}
//			new Thread(){
//				public void run() {
//					mChannel =  chnService.getChannelById(program.getChannelId());
//					if(mChannel != null){
//						String playUrl = mChannel.getLiveURL();
//						if(playUrl!=null)
//							handler.sendEmptyMessage(VISIABLE_PLAY_BTN);
//					}
//				};
//			}.start();
		}
		if(!(this instanceof EpgOnAirItemView)){
			if(pb_play_rate!=null&&pb_play_rate.getVisibility()==View.VISIBLE) {
				pb_play_rate.setVisibility(GONE);
			}
		}
		if(ivEpgItemMenu != null) {
			ivEpgItemMenu.setVisibility(View.VISIBLE);
			ivEpgItemMenu.setOnClickListener(this);
		}
		startTimer();
		timer.innerTask = new TimerTask() {
			@Override
			public void run() {
				handler.sendEmptyMessage(0);
			}
		};
		if(!timer.cancel)
			timer.schedule(timer.innerTask, 0 ,10000);
	}
	
	private void startTimer(){
		if(timer != null && timer.cancel){
			timer = new MyTimer();
			timer.cancel = false;
			LoadingDataTask.addTimerToList(timer);
		}
		if(timer.innerTask != null){
			timer.innerTask.cancel();
			timer.innerTask = null;
		}
	}
	
	private void cancelTask(){
		if(timer != null && !timer.cancel){
			timer.cancel = true;
			if(timer.innerTask != null){
				timer.innerTask.cancel();
				timer.innerTask = null;
			}
			LoadingDataTask.removeTimer(timer);
		}
	}

	public BaseEpgItemView(Context context) {
		this(context,null);
	}
	
	public BaseEpgItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		initData();
	}
	
	private void initData(){
		mScroller = new Scroller(getContext());
		epgService = new ProgramService(getContext());
		taskService = new TaskService(getContext());
		chnService = new ChannelService(getContext());
		
		timer = new MyTimer();
		LoadingDataTask.addTimerToList(timer);
	}

	public boolean isShownCancelBtn() {
		return isShowCancleBtn;
	}
	
	public void setOnClickable(boolean clickable){
		this.isClickable = clickable;
		if(clickable)
			setOnClickListener(this);
	}
	
	protected void compareProgram() {
		epgService.compareProgram(getContext(), program);
	}
	
	protected void fillChannelLogo() {
		if(mChannel != null && iv_channel_icon!=null){
			try{
				iv_channel_icon.setImageResource(R.drawable.icon_channel_logo);
				String logoUrl = mChannel.getLogo().getResources().get(0).getUrl();
				iv_channel_icon.setUrl(logoUrl);
			}catch(Exception e){
				iv_channel_icon.setImageResource(R.drawable.icon_channel_logo);
			}
		}
	}

	protected void loadChannelLogoTask() {
		new LoadingDataTask() {
			@Override
			public void onPreExecute() {
			}

			@Override
			public void onPostExecute() {
				fillChannelLogo();
			}

			@Override
			public void doInBackground() {
				if(program != null)
					mChannel = chnService.getChannelById(program.getChannelId());
			}
		}.execute();
	}
	
	private void updateProgressbarStatus() {
		if(program==null){
			cancelTask();
			return;
		}
		isPlay = false;
		long current = System.currentTimeMillis() - program.getStartDate().getTime();
		long duration = program.getEndDate().getTime() - program.getStartDate().getTime();
 		if(current<0){
 			cancelTask();
 		}else if(current>=duration){
 			cancelTask();
 			if(ivEpgItemMenu != null) {
	 			ivEpgItemMenu.setVisibility(View.VISIBLE);
	 			ivEpgItemMenu.setOnClickListener(this);
 			}
 			if(this instanceof EpgOnAirItemView && mChannel!=null){
 				DataCache.getInstance().getOneLevelProgramCache().remove(mChannel.getId());
			}
 			if(isNewProgram){
 				Log.i(TAG, "the program was old, name is "+program.getName());
 				pb_play_rate.setVisibility(View.GONE);
 				if(ivEpgItemMenu != null) {
 					ivEpgItemMenu.setVisibility(View.VISIBLE);
 	 				ivEpgItemMenu.setOnClickListener(this);
 				}
 				tv_epg_name.setTextColor(getContext().getResources().getColor(R.color.grey_));
 				return;
 			}
 			Log.i(TAG, "current program has ended, name is "+program.getName());
			if(this instanceof EpgOnAirItemView){
				if(mListener != null){
					mListener.onItemCallBack();
				}else{
					((EpgOnAirItemView)this).addTask();
				}
			}else if(this instanceof EpgOnAlertItemView){
				if(!isClickable)
					((EpgOnAlertItemView)this).removeFullProgram();
				else
					tv_epg_name.setTextColor(getContext().getResources().getColor(R.color.grey_));
			}else if(this instanceof EpgChnGuideItemView){
				if(mListener != null){
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							mListener.onItemCallBack();
						}
					}, 2000);
				}
			}
		}else{
			isNewProgram = false;
			isPlay = true;
			pb_play_rate.setProgress((int) (((float)current/duration)*1000));
			pb_play_rate.setVisibility(View.VISIBLE);
			if(ivEpgItemMenu != null) {
				ivEpgItemMenu.setVisibility(View.GONE);
				ivEpgItemMenu.setOnClickListener(null);
			}
		}
	}
	
	public void shrink(){
        if (getScrollX() != 0) {
        	smoothScrollBack();
        }
    }

	public void smoothScrollTo() {
		if(isPlay){
			Log.d(TAG, "It's on air. You can watch it on your TV right now!");
			ToastUtil.centerShowToast(getContext(), getContext().getResources().getString(R.string.cannot_alert));
			return;
		}
//		mScroller.startScroll(0, 0, DensityUtil.dip2px(getContext(), 60), 0, 300);
//		isShowCancleBtn = true;
		invalidate();
	}

	public void smoothScrollBack() {
//		mScroller.startScroll(getLeft(), 0, 0, 0, 300);
//		isShowCancleBtn = false;
		invalidate();
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
//			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		}
	}
	
	protected void updateAlertIconStatus() {
		boolean isFav = false;
		if(program != null)
			isFav = program.isIsFav();
		if(isFav){
			if(iv_alert_icon != null)
				iv_alert_icon.setVisibility(View.VISIBLE);
//			if(tv_epg_startime != null)
//				tv_epg_startime.setTextColor(homeActivity.getResources().getColor(R.color.alert_setting_text));
//			if(iv_alert_cancle != null)
//				iv_alert_cancle.setImageDrawable(getContext().getResources().getDrawable(R.drawable.slide_cancle_alert));
		}else{
			if(iv_alert_icon != null)
				iv_alert_icon.setVisibility(View.INVISIBLE);
//			if(tv_epg_startime != null)
//				tv_epg_startime.setTextColor(homeActivity.getResources().getColor(R.color.alert_setting_text));
//			if(iv_alert_cancle != null)
//				iv_alert_cancle.setImageDrawable(getContext().getResources().getDrawable(R.drawable.slide_alert));
		}
	}
	
	protected void changeProgramFavStatus(){
		new LoadingDataTask() {
			boolean favStatus;
			DoTaskResult result;
			@Override
			public void onPreExecute() {
			}

			@Override
			public void onPostExecute() {
				if(program == null)
					return;
				if(favStatus){
					if(program.isIsFav()){
						ToastUtil.centerShowToast(getContext(), getContext().getString(R.string.alert_success));
						if(mContext instanceof HomeActivity) {
							String channelName = mChannel != null?mChannel.getName():"";
							ToastUtil.showToast(getContext(), channelName+"_"+program.getId());
							GA.sendEvent("Reminder", "Reminder_home", channelName+"_"+program.getId(), 1);
						}
						taskService.doTask(TaskCode.Book_program,ApplicationUtil.getAppVerison(getContext()));
					}else{
						ToastUtil.centerShowToast(getContext(), getContext().getString(R.string.alert_cancel_success));
					}
					updateAlertIconStatus();
					AlertManager.getInstance(getContext()).startAlertTimer();
				}else{
					if(program.isIsFav())
						ToastUtil.centerShowToast(getContext(), getContext().getString(R.string.alert_unsuccess));
					else
						ToastUtil.centerShowToast(getContext(), getContext().getString(R.string.alert_cancel_unsuccess));
					program.setIsFav(!program.isIsFav());
					program.setFavCount(program.isIsFav()?program.getFavCount()-1:program.getFavCount()+1);
				}
			}

			@Override
			public void doInBackground() {
				if(program == null)
					return;
				boolean isFav = program.isIsFav();
				program.setFavCount(isFav?program.getFavCount()-1:program.getFavCount()+1);
				program.setIsFav(!isFav);
				favStatus = epgService.updateFavStatus(program);
				if(!isFav) {
					TenbService tenbService = new TenbService(getContext());
					tenbService.doTenbData(Tenb.TENB_FAV_EPG, program.getId());
				}
			}
		}.execute();
	}

	@Override
	public void onClick(View v) {
		if (program == null){
			ToastUtil.centerShowToast(getContext(), "Current channel has no program.");
			return;
		}
		if(v.getId()== R.id.iv_epg_item_menu) {
			showMenu();
			return;
		}
	}
	
	public interface OnItemCallBackListener{
		public abstract void onItemCallBack();
	}
	
	public void showMenu() {
		View menuView = LayoutInflater.from(mContext).inflate(R.layout.epg_item_menu_view, null);
		TextView context = (TextView) menuView.findViewById(R.id.tv_menu_name);
		if(program.isIsFav()) {
			context.setText(mContext.getString(R.string.click_to_cancel));
			context.setTextColor(mContext.getResources().getColor(R.color.orange));
		} else {
			context.setText(mContext.getString(R.string.remind_me));
			context.setTextColor(mContext.getResources().getColor(R.color.darkGray));
		}
		
		final PopupWindow mWindow = new PopupWindow(menuView,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,true);
		mWindow.setTouchable(true);
		mWindow.setOutsideTouchable(true);
		mWindow.setBackgroundDrawable(new BitmapDrawable(mContext.getResources(), (Bitmap) null));
		mWindow.showAsDropDown(menuParentView);
		menuView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				changeProgramFavStatus();
				mWindow.dismiss();
			}
		});
		
	}
	
	protected OnItemCallBackListener mListener;
	public void setOnItemCallBackListener(OnItemCallBackListener mListener){
		this.mListener = mListener;
	}
}
