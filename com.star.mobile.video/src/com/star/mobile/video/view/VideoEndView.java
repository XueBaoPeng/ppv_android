package com.star.mobile.video.view;

import static com.star.mobile.video.util.Constants.BUNDESLIGA_CHANNEL_ID;
import static com.star.mobile.video.util.Constants.CATHY_CHANNEL_ID;
import static com.star.mobile.video.util.Constants.SERIEA_CHANNEL_ID;
import static com.star.mobile.video.util.Constants.SPORTS_CHANNEL_ID;
import static com.star.mobile.video.util.Constants.TENBRE_CHANNEL_ID;
import static com.star.mobile.video.util.Constants.TUNDE_KELANI_CHANNEL_ID;
import static com.star.mobile.video.util.Constants.YORUBA_CHANNEL_ID;

import java.util.ArrayList;
import java.util.List;

import org.libsdl.app.Player;

import com.star.cms.model.Package;
import com.star.cms.model.vo.ChannelVO;
import com.star.cms.model.vo.SmartCardInfoVO;
import com.star.cms.model.vo.VOD;
import com.star.mobile.video.R;
import com.star.mobile.video.account.ChooseAreaActivity;
import com.star.mobile.video.adapter.VideoEndViewAdapter;
import com.star.mobile.video.model.FunctionType;
import com.star.mobile.video.service.FunctionService;
import com.star.mobile.video.service.UserService;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.smartcard.SmartCardControlActivity;
import com.star.mobile.video.smartcard.SmartCardService;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.CommonUtil.PromptDialogClickListener;
import com.star.util.loader.OnListResultListener;
import com.star.mobile.video.util.DefaultLoadingTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * 视频播放完，弹出的推荐提示页面，还包括换包按钮
 * 
 * @author Lee
 * @version 1.0 2015.08.07
 */
public class VideoEndView extends RelativeLayout implements OnClickListener {
	private LayoutInflater mInflater;
	private GridView mVideoRecommentGridView;
	private RelativeLayout mChangeBouquetPromptRL;
	private TextView mVideoEndPromptTV;
	private TextView mVideoEndRecharge;
	private TextView mVideoEndUpdate;
	private Context mContext;
	private Player mActivity;
	private ChannelVO mChannel;
	private VideoEndViewAdapter mVideoEndViewAdapter;
	private List<VOD> mRecommentsVideo = new ArrayList<VOD>();// 推荐视频
	private List<SmartCardInfoVO> scInfo; // 所有卡号
	private SmartCardService mSmartCardService;
	public VideoEndView(Context context, ChannelVO channel, Activity activity) {
		this(context, null, channel, activity);
	}

	public VideoEndView(Context context, AttributeSet attrs, ChannelVO channel, Activity activity) {
		this(context, attrs, 0, channel, activity);
	}

	public VideoEndView(Context context, AttributeSet attrs, int defStyle, ChannelVO channel, Activity activity) {
		super(context, attrs, defStyle);
		mInflater = LayoutInflater.from(context);
		this.mContext = context;
		this.mChannel = channel;
		this.mActivity = (Player) activity;
		initView();
		initData();
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		mInflater.inflate(R.layout.view_video_end_prompt, this);
		mVideoRecommentGridView = (GridView) findViewById(R.id.gv_video_end_recomment);
		mChangeBouquetPromptRL = (RelativeLayout) findViewById(R.id.rl_change_bouquet_prompt);
		mVideoEndPromptTV = (TextView) findViewById(R.id.tv_video_end_prompt);
		mVideoEndRecharge = (TextView) findViewById(R.id.prompt_later);
		mVideoEndUpdate = (TextView) findViewById(R.id.prompt_ok);
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		mSmartCardService = new SmartCardService(mContext);
		getSmartCardNos();
		mVideoEndViewAdapter = new VideoEndViewAdapter(mContext, mRecommentsVideo);
		mVideoRecommentGridView.setAdapter(mVideoEndViewAdapter);
		mVideoRecommentGridView.setOnItemClickListener(new MyOnItemClickListener());
		// 需要获得当前视频结束时的频道
		Package channelPackage = mChannel.getOfPackage();
		String promptCotent = null;
		if (mChannel != null && channelPackage != null && isNoneBouquetChannel()) {
			promptCotent = String.format(mContext.getString(R.string.video_end_change_bouquet_prompt,
					mChannel.getName(), channelPackage.getName()));
		} else {
			promptCotent = String.format(mContext.getString(R.string.video_end_change_bouquet_none));
		}
		mVideoEndPromptTV.setText(promptCotent);
		mVideoEndRecharge.setOnClickListener(this);
		mVideoEndUpdate.setOnClickListener(this);
	}

	/**
	 * 点击列表弹出竖屏播放界面
	 * 
	 * @author Lee
	 *
	 */
	private class MyOnItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if (mRecommentsVideo != null && mRecommentsVideo.size() > 0) {
				transferHorizontalListView(position);
			}
		}

	}

	/**
	 * 点击item播放视频
	 * 
	 * @param position
	 */
	private void transferHorizontalListView(int position) {
		// 重新加载数据
		hideVideoEndView();
		VOD vod = mRecommentsVideo.get(position);
		mActivity.resetData(vod);
		mActivity.settingVideoPlay(position);
	}

	

	/**
	 * 没有换包的频道
	 * 
	 * @return
	 */
	private boolean isNoneBouquetChannel() {
		if(mChannel != null ){
			if(mChannel.getId() == TENBRE_CHANNEL_ID || mChannel.getId() == CATHY_CHANNEL_ID
				|| mChannel.getId() == BUNDESLIGA_CHANNEL_ID|| mChannel.getId() == SPORTS_CHANNEL_ID
				|| mChannel.getId() == TUNDE_KELANI_CHANNEL_ID || mChannel.getId() == SERIEA_CHANNEL_ID
				|| mChannel.getId() == YORUBA_CHANNEL_ID) {
					return false;			
				}else{
					return true;
				}
		}else{
			return false;
		}
	}

	/**
	 * 显示换包提示
	 */
	public void showChangeBouquetPromptRL() {
		if(!FunctionService.doHideFuncation(FunctionType.SimpleVersion)){
			mChangeBouquetPromptRL.setVisibility(View.VISIBLE);
		}
		mVideoRecommentGridView.setVisibility(View.VISIBLE);
	}

	/**
	 * 隐藏控件
	 */
	public void hideVideoEndView() {
		mChangeBouquetPromptRL.setVisibility(View.INVISIBLE);
		mVideoRecommentGridView.setVisibility(View.INVISIBLE);
	}

	/**
	 * 设置视频推荐的数据
	 * 
	 * @param recommentsVideo
	 */
	public void setRecommentVideo(List<VOD> recommentsVideo) {
		this.mRecommentsVideo = recommentsVideo;
		mVideoEndViewAdapter.setRecommentVideo(mRecommentsVideo);
	}

	@Override
	public void onClick(View v) {
		String userName = SharedPreferencesUtil.getUserName(mContext);
		switch (v.getId()) {
		case R.id.prompt_later:
		case R.id.prompt_ok:
			if (userName == null || "".equals(userName)) {
//				CommonUtil.pleaseLogin(true, getContext());
				CommonUtil.getInstance().showPromptDialog(mContext, null, mContext.getString(R.string.alert_login),
						mContext.getString(R.string.login_btn), mContext.getString(R.string.later), new PromptDialogClickListener() {
							
							@Override
							public void onConfirmClick() {
								Intent intent = new Intent (mContext, ChooseAreaActivity.class);
								CommonUtil.startActivity((Activity)mContext, intent);
							}
							
							@Override
							public void onCancelClick() {
								// TODO Auto-generated method stub
								
							}
						});
			} else {
				// 跳转到充值界面
				Intent chargeIntent = new Intent(mContext, SmartCardControlActivity.class);
				mContext.startActivity(chargeIntent);
			}
			break;

		default:
			break;
		}
	}

	private void getSmartCardNos() {
		mSmartCardService.getAllSmartCardInfos(new OnListResultListener<SmartCardInfoVO>() {
			
			@Override
			public boolean onIntercept() {
				return false;
			}
			
			@Override
			public void onFailure(int errorCode, String msg) {
			}
			
			@Override
			public void onSuccess(List<SmartCardInfoVO> value) {
				// TODO Auto-generated method stub
				scInfo = value;
			}
		});
	}

}
