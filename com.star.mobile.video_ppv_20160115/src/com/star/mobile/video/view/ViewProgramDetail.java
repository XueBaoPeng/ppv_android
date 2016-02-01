package com.star.mobile.video.view;

import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.star.cms.model.ProgramPPV;
import com.star.cms.model.vo.ChannelVO;
import com.star.mobile.video.R;
import com.star.mobile.video.base.BaseFragmentActivity;
import com.star.mobile.video.service.ChannelService;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.DateFormat;
import com.star.mobile.video.util.LoadingDataTask;
import com.star.ott.ppvup.model.enums.RentleType;
import com.star.ui.ImageView;


/**
 * Created by Lee on 2016/1/15.
 */
public class ViewProgramDetail extends RelativeLayout {
    private LayoutInflater mLayoutInflater;
    private ImageView mProgramPosterImageView;
//    private ImageView mProgramPlayImg;
    private com.star.ui.ImageView mProgramChannelIcon;
    private TextView mProgramName;
    private TextView mProgramStartDate;
    private TextView mProgramStartTime;
    private ChannelVO mChannel;
    private ChannelService mChannelService;

    public ViewProgramDetail(Context context) {
        this(context, null);
    }

    public ViewProgramDetail(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewProgramDetail(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context) {
    	mChannelService = new ChannelService(context);
        mLayoutInflater = LayoutInflater.from(context);
        View view = mLayoutInflater.inflate(R.layout.view_program_detail, this);
        mProgramPosterImageView = (ImageView) view.findViewById(R.id.iv_program_poster);
//        mProgramPlayImg = (ImageView) view.findViewById(R.id.iv_program_play_img);
        mProgramChannelIcon = (com.star.ui.ImageView) view.findViewById(R.id.iv_program_channel_icon);
        mProgramName = (TextView) view.findViewById(R.id.tv_program_name);
        mProgramStartDate = (TextView) view.findViewById(R.id.tv_program_stardate);
        mProgramStartTime = (TextView) view.findViewById(R.id.tv_program_startime);
    }
    
    public void setProgram(ProgramPPV program){
    	executeChannelInfoTask(program);
    	setProgramDetail(program);
    }

    public void executeChannelInfoTask(final ProgramPPV program) {
        new LoadingDataTask() {
            @Override
            public void onPreExecute() {
            }

            @Override
            public void onPostExecute() {
                if (mChannel == null)
                    return;
                try {
                	mProgramChannelIcon.setUrl(mChannel.getLogo().getResources().get(0).getUrl());
                	mProgramChannelIcon.setOnClickListener(mChannelIconClickListener);
                } catch (Exception e) {
                	mProgramChannelIcon.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void doInBackground() {
                mChannel = mChannelService.getChannelById(program.getChannelId());
            }
        }.execute();
    }

    private View.OnClickListener mChannelIconClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getContext(), BaseFragmentActivity.class);
            intent.putExtra("channel", mChannel);
            CommonUtil.startActivity(getContext(), intent);
        }
    };

    private void setProgramDetail(ProgramPPV program) {
        try {
            String url = null;
            for (int i = 0; i < program.getPpvContent().getPosterResourceList().size(); i++) {
                for (int a = 0; a < program.getPpvContent().getPosterResourceList().size(); a++) {
                    if (program.getPpvContent().getPosterResourceList().get(i).getSize() > program.getPpvContent().getPosterResourceList().get(a).getSize()) {
                        url = program.getPpvContent().getPosterResourceList().get(i).getResourceURL();
                        break;
                    }
                }
            }
            //频道图片
            mProgramPosterImageView.setUrl(url);
        } catch (Exception e) {
        }
        //设置program name
        mProgramName.setText(program.getName());
        long startDate = 0;
        String startime = "";
        for (int i = 0; i < program.getPpvContent().getProductList().size(); i++) {
            if (program.getPpvContent().getProductList().get(i).getRentleType().equals(RentleType.SINGLE)) {
                startDate = program.getPpvContent().getProductList().get(i).getEpgContentList().get(0).getStartDate();
                startime = startime + Constant.format.format(program.getPpvContent().getProductList().get(i).getEpgContentList().get(0).getStartDate()) + "/";
            }
        }
        //开始日期
        mProgramStartDate.setText(DateFormat.formatMonth(new Date(startDate)));
        //开始时间
        mProgramStartTime.setText(startime.substring(0, startime.length() - 1));
    }
}
