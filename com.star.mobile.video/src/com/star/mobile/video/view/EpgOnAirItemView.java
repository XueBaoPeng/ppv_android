package com.star.mobile.video.view;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.star.cms.model.vo.ChannelVO;
import com.star.cms.model.vo.ProgramVO;
import com.star.mobile.video.R;
import com.star.mobile.video.service.ProgramService;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.DataCache;
import com.star.mobile.video.util.LoadingDataTask;
import com.star.mobile.video.util.ToastUtil;

public class EpgOnAirItemView extends BaseEpgItemView implements OnClickListener {

	private boolean isShowChnNum;
	private int currentPage;
	private ProgressBar lpb_loading;

	public EpgOnAirItemView(Context context, int page) {
		super(context);
		setCurrentPage(page);
		initView(context);
	}

	public EpgOnAirItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	private void initView(Context context) {
		LayoutInflater.from(context).inflate(R.layout.view_epg_onair_item, this);
		tv_epg_name = (TextView) findViewById(R.id.tv_epg_name);
		tv_channel_number = (TextView) findViewById(R.id.tv_channel_number);
		pb_play_rate = (ProgressBar) findViewById(R.id.pb_play_rate);
		iv_channel_icon = (com.star.ui.ImageView) findViewById(R.id.iv_channel_icon);
		ivEpgItemMenu = (ImageView) findViewById(R.id.iv_epg_item_menu);
		iv_alert_icon = (ImageView) findViewById(R.id.iv_alert_item_icon);
		lpb_loading = (ProgressBar) findViewById(R.id.pb_loading);
		ivEpgItemMenu = (ImageView) findViewById(R.id.iv_epg_item_menu);
//		iv_alert_cancle = (ImageView) findViewById(R.id.iv_alert_cancle);
//		iv_alert_cancle.setOnClickListener(this);
		iv_channel_icon.setOnClickListener(this);
		
		epgService = new ProgramService(getContext());
	}

	private void fillProgramData(ProgramVO pm) {
		if(pm != null && pm.getChannelId().equals(mChannel.getId())){
			tv_epg_name.setText(pm.getName());
			tv_epg_name.setTextColor(getContext().getResources().getColor(R.color.list_text_color));
			if(currentPage == Constant.PAGE_ONAIR_NOW){
				scheduleEPGProgress();
			}
			if(!pm.isIsFav())
				compareProgram();
			updateAlertIconStatus();
		}
	}
	
	public void fillProgram(ProgramVO pm) {
		program = pm;
		loadChannelLogoTask();
		if(pm != null){
			tv_epg_name.setText(pm.getName());
			scheduleEPGProgress();
			updateAlertIconStatus();
		}
	}

	public void fillData(ChannelVO channel) {
		this.mChannel = channel;
		if(isShowChnNum){
			tv_channel_number.setText(String.valueOf(mChannel.getChannelNumber()));
			tv_channel_number.setVisibility(View.VISIBLE);
		}
		if(lpb_loading.getVisibility() == View.VISIBLE)
			lpb_loading.setVisibility(View.GONE);
		List<ProgramVO> epgs = DataCache.getInstance().getOneLevelProgramCache().get(mChannel.getId());
		if(epgs!=null&&epgs.size()>0){
			if(currentPage == Constant.PAGE_ONAIR_NOW)
				program = epgs.get(0);
			else if(currentPage == Constant.PAGE_ONAIR_NEXT)
				if(epgs.size()>1)
					program = epgs.get(1);
			fillProgramData(program);
		}else{
			addTask();
		}
		setChannelLogo(channel);
	}

	public void setCurrentPage(int page){
		this.currentPage = page;
	}

	private void setChannelLogo(ChannelVO channel) {
		iv_channel_icon.setImageResource(R.drawable.icon_channel_logo);
		try{
			String logoUrl = channel.getLogo().getResources().get(0).getUrl();
			iv_channel_icon.setUrl(logoUrl);
		}catch(Exception e){
		}
	}
	
	public void fillData(ChannelVO channel, boolean isShowChnNum) {
		this.isShowChnNum = isShowChnNum;
		fillData(channel);
	}
	
	public void addTask() {
		new LoadingDataTask() {
			@Override
			public void onPreExecute() {
				program = null;
				tv_epg_name.setText("");
				if(pb_play_rate.getVisibility()==View.VISIBLE)
					pb_play_rate.setVisibility(View.GONE);
				updateAlertIconStatus();
				lpb_loading.setVisibility(View.VISIBLE);
			}

			@Override
			public void onPostExecute() {
				fillProgramData(program); 
				lpb_loading.setVisibility(View.GONE);
			}

			@Override
			public void doInBackground() {
				List<ProgramVO> epgs = epgService.getOnairEpgs(mChannel.getId());
				if(epgs!=null&&epgs.size()>0&&epgs.get(0).getChannelId().equals(mChannel.getId())){
					DataCache.getInstance().getOneLevelProgramCache().put(mChannel.getId(), epgs);
					if(currentPage == Constant.PAGE_ONAIR_NOW)
						program = epgs.get(0);
					else if(currentPage == Constant.PAGE_ONAIR_NEXT)
						if(epgs.size()>1)
							program = epgs.get(1);
				}
			}
		}.execute();
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		/*if(v.getId() == R.id.iv_alert_cancle){
			changeProgramFavStatus();
			this.shrink();
			return;
		}else */if(v.getId() == R.id.iv_channel_icon && mChannel != null){
			CommonUtil.startChannelActivity(getContext(), mChannel);
			return;
		}
		if (program == null){
			ToastUtil.centerShowToast(getContext(), "No program guide now.");
			return;
		}
		CommonUtil.startEpgActivity(getContext(), program.getId());
	}
}
