package com.star.mobile.video.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.star.cms.model.ProgramPPV;
import com.star.mobile.video.R;
import com.star.mobile.video.util.Constant;

public class EpgChnGuideItemView extends BaseEpgItemView {

	private TextView tv_ppv_item_icon;
	public EpgChnGuideItemView(Context context) {
		super(context);
		initView(context);
	}
	
	public EpgChnGuideItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	private void initView(Context context) {
		LayoutInflater.from(context).inflate(R.layout.view_epg_item, this);
		tv_epg_name = (TextView) findViewById(R.id.tv_epg_name);
		tv_ppv_item_icon=(TextView)findViewById(R.id.tv_ppv_item_icon);
		tv_epg_startime = (TextView) findViewById(R.id.tv_epg_startime);
		iv_alert_icon = (ImageView) findViewById(R.id.iv_alert_item_icon);
//		iv_alert_cancle = (ImageView) findViewById(R.id.iv_alert_cancle);
		pb_play_rate = (ProgressBar) findViewById(R.id.pb_play_rate);
		ivEpgItemMenu = (ImageView) findViewById(R.id.iv_epg_item_menu);
		ivEgpLine = (ImageView) findViewById(R.id.iv_epg_line);
		menuParentView = findViewById(R.id.v_menu_parent);
//		iv_alert_cancle.setOnClickListener(this);
//		ivEpgItemMenu.setVisibility(View.GONE);
//		ivEpgItemPlay.setOnClickListener(this);
	}
	
	public void fillProgramData(ProgramPPV vo){
		if(vo.equals(program)){
			return;
		}
		setProgramCategoryType(vo.getCategoryType());
		if(vo.getPpvContent()!=null){
			tv_ppv_item_icon.setVisibility(View.VISIBLE);
		}else{
			tv_ppv_item_icon.setVisibility(View.GONE);
		}
		this.program = vo;
		tv_epg_name.setText(vo.getName());
		tv_epg_name.setTextColor(getContext().getResources().getColor(R.color.list_text_color));
		tv_epg_startime.setText(Constant.format.format(vo.getStartDate()));
		compareProgram();
		scheduleEPGProgress();
		updateAlertIconStatus();
	}
	
	public void fillChannel() {
		super.loadChannelLogoTask();
	}
	
}
