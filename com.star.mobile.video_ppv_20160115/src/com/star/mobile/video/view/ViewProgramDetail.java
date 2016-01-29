package com.star.mobile.video.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.star.mobile.video.R;
import com.star.ui.ImageView;


/**
 * Created by Lee on 2016/1/15.
 */
public class ViewProgramDetail extends RelativeLayout {
    private LayoutInflater mLayoutInflater;
    private ImageView mProgramPosterImageView;
//    private ImageView mProgramPlayImg;
    private ImageView mProgramChannelIcon;
    private TextView mProgramName;
    private TextView mProgramStartDate;
    private TextView mProgramStartTime;

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
        mLayoutInflater = LayoutInflater.from(context);
        View view = mLayoutInflater.inflate(R.layout.view_program_detail, this);
        mProgramPosterImageView = (ImageView) view.findViewById(R.id.iv_program_poster);
//        mProgramPlayImg = (ImageView) view.findViewById(R.id.iv_program_play_img);
        mProgramChannelIcon = (ImageView) view.findViewById(R.id.iv_program_channel_icon);
        mProgramName = (TextView) view.findViewById(R.id.tv_program_name);
        mProgramStartDate = (TextView) view.findViewById(R.id.tv_program_stardate);
        mProgramStartTime = (TextView) view.findViewById(R.id.tv_program_startime);
    }

    /**
     * 设置poster图片
     *
     * @param url
     */
    public void setProgramPosterImg(String url) {
        mProgramPosterImageView.setUrl(url);
    }

    public ImageView getProgramPosterImageView() {
        return mProgramPosterImageView;
    }

//    public ImageView getProgramPlayImg() {
//        return mProgramPlayImg;
//    }

    public ImageView getProgramChannelIcon() {
        return mProgramChannelIcon;
    }

    /**
     * 设置频道的图片
     *
     * @param url
     */
    public void setProgramChannelIcon(String url) {
        mProgramChannelIcon.setUrl(url);
    }

    public TextView getProgramStartDate() {
        return mProgramStartDate;
    }

    /**
     * program开始的日期
     *
     * @param programStratDate
     */
    public void setProgramStartDate(String programStratDate) {
        mProgramStartDate.setText(programStratDate);
    }

    public TextView getProgramName() {
        return mProgramName;
    }

    /**
     * 设置program的名字
     *
     * @param programName
     */
    public void setProgramName(String programName) {
        mProgramName.setText(programName);
    }

    public TextView getProgramStartTime() {
        return mProgramStartTime;
    }

    /**
     * program开始的时间
     *
     * @param programStartTime
     */
    public void setProgramStartTime(String programStartTime) {
        mProgramStartTime.setText(programStartTime);
    }


}
