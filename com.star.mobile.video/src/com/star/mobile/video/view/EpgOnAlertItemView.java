package com.star.mobile.video.view;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.star.cms.model.vo.ChannelVO;
import com.star.cms.model.vo.ProgramVO;
import com.star.mobile.video.AlertManager;
import com.star.mobile.video.R;
import com.star.mobile.video.adapter.EpgOnAlertListAdapter;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.LoadingDataTask;
import com.star.mobile.video.util.ToastUtil;

public class EpgOnAlertItemView extends BaseEpgItemView {

	private EpgOnAlertListAdapter mListAdapter;
	private ImageView iv;
	public EpgOnAlertItemView(Context context, EpgOnAlertListAdapter mListAdapter) {
		super(context);
		initView(context);
		this.mListAdapter = mListAdapter;
	}

	public EpgOnAlertItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	private void initView(Context context) {
		LayoutInflater.from(context).inflate(R.layout.view_epg_item_for_alert, this);
		tv_epg_name = (TextView) findViewById(R.id.tv_epg_name);
		tv_epg_startime = (TextView) findViewById(R.id.tv_epg_startime);
		pb_play_rate = (ProgressBar) findViewById(R.id.pb_play_rate);
		iv_channel_icon = (com.star.ui.ImageView) findViewById(R.id.iv_channel_icon);
		ivEpgItemMenu = (ImageView) findViewById(R.id.iv_epg_alert_item_menu);
		iv = (ImageView) findViewById(R.id.iv_epg_alert_item_icon);
		menuParentView = findViewById(R.id.v_menu_parent);
//		iv_alert_cancle = (ImageView) findViewById(R.id.iv_alert_cancle);
//		iv_alert_cancle.setOnClickListener(this);
		iv_channel_icon.setOnClickListener(this);
	}

	public void fillProgramData(ProgramVO program) {
		iv.setVisibility(View.VISIBLE);
		loadChannelLogoTask();
		updateUI(program);
	}

	private void updateUI(ProgramVO program) {
		this.program = program;
		tv_epg_name.setText(program.getName());
		tv_epg_name.setTextColor(getContext().getResources().getColor(R.color.list_text_color));
		tv_epg_startime.setText(Constant.format.format(program.getStartDate()));
		scheduleEPGProgress();
	}
	
	public void fillChannelData(ChannelVO channel){
		mChannel = channel;
		fillChannelLogo();
		loadProgramTask();
	}

	protected void changeProgramFavStatus(){
		new LoadingDataTask() {
			boolean favStatus;
			@Override
			public void onPreExecute() {
			}

			@Override
			public void onPostExecute() {
				if(favStatus){
					ToastUtil.centerShowToast(getContext(), "success.");
					removeFullProgram();
					shrink();
				}else{
					ToastUtil.centerShowToast(getContext(), "failure.");
					program.setIsFav(true);
				}
			}

			@Override
			public void doInBackground() {
				program.setIsFav(false);
				favStatus = epgService.updateFavStatus(program);
			}
		}.execute();
	}
	
	public void removeFullProgram() {
		if(mListAdapter != null){
			List<ProgramVO> epgs = mListAdapter.getData();
			epgs.remove(program);
			program = null;
			mChannel = null;
			mListAdapter.updateChnDataAndRefreshUI(epgs);
			AlertManager.getInstance(getContext()).startAlertTimer();
		}
	}
	
	private void loadProgramTask() {
		new LoadingDataTask() {
			@Override
			public void onPreExecute() {
			}

			@Override
			public void onPostExecute() {
				if(program != null){
					updateUI(program);
				}
			}

			@Override
			public void doInBackground() {
				List<ProgramVO> epgs = epgService.getOnairEpgs(mChannel.getId());
				if(epgs.size()>0)
					program = epgs.get(0);
			}
		}.execute();
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId()== R.id.iv_epg_alert_item_menu) {
			showMenu();
			return;
		}
		/*if(v.getId() == R.id.iv_alert_cancle){
			changeProgramFavStatus();
			return;
		}else*/ if(v.getId() == R.id.iv_channel_icon && mChannel != null){
//			Fragment fragment = homeActivity.setFragmentByTag(homeActivity.getResources().getString(R.string.fragment_tag_channelGuide));
			CommonUtil.startChannelActivity(getContext(), mChannel);
			if(mListener != null){
				mListener.onItemCallBack();
			}
			return;
		}
		if(program != null) {
//			Fragment fragment = homeActivity.setFragmentByTag(getContext().getResources().getString(R.string.fragment_tag_epgDetail));
			CommonUtil.startEpgActivity(getContext(), program.getId());
			if(mListener != null){
//				mListener.onItemCallBack();
			}
		} else {
			ToastUtil.centerShowToast(getContext(), "Current channel has no program");
		}
		
	}
	public void showMenu() {
		View menuView = LayoutInflater.from(mContext).inflate(R.layout.epg_item_menu_view, null);
		TextView context = (TextView) menuView.findViewById(R.id.tv_menu_name);
		context.setText(mContext.getString(R.string.click_to_cancel));
		context.setTextColor(mContext.getResources().getColor(R.color.orange));
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
}
