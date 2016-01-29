package com.star.mobile.video.channel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.libsdl.app.Player;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.star.cms.model.Content;
import com.star.cms.model.Resource;
import com.star.cms.model.vo.ChannelVO;
import com.star.cms.model.vo.VOD;
import com.star.mobile.video.R;
import com.star.mobile.video.home.tab.TabView;
import com.star.mobile.video.service.VideoService;
import com.star.mobile.video.util.DateFormat;
import com.star.mobile.video.util.DensityUtil;
import com.star.mobile.video.util.LoadingDataTask;
import com.star.mobile.video.view.ListView;
import com.star.mobile.video.view.ListView.LoadingListener;
import com.star.ui.ImageView;

public class VideoView extends TabView<ListView>  {

	private View mFragmentView;
	private View headerView;
	private View loadingView;
//	private long mCurrentChannelId ;
	private VideoService videoSerivce;
	private ListView lv_video_list;
	private VideoAdapter mVideoPlayAdapter;
	private List<VOD> mRecommentsVideo = new ArrayList<VOD>();// 推荐视频
	private int Offset = 0;
	private int COUNT = 6;
	
//	private SimpleDraweeView ivRecommendVideoIcon;
	private com.star.ui.ImageView ivRecommendVideoIcon;
	private TextView tvRecommendVideoName;
	private TextView tvRecommendVideoViews;
	private TextView tvRecommendVideoDuration;
	private final int WHAT_HEADER = 1;
	
	private ChannelVO mChannel;
	private Context mContext;
	private LinearLayout ivNoVideo;
	private View headerTwo;
	private boolean isLoadService;
//	/**
//	 * 创建新实例
//	 * 
//	 * @param index
//	 * @return
//	 */
//	public static  VideoFragment newInstance(int index) {
//		Bundle bundle = new Bundle();
//		bundle.putInt(FRAGMENT_INDEX, index);
//		VideoFragment fragment = new VideoFragment();
////		fragment.setArguments(bundle);
//		return fragment;
//	}

	public VideoView(Context context) {
		super(context);
		mContext=context;
		initView(context);
	}
	public VideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext=context;
		initView(context);
	}
	
	/*public  void setFrescoImageView(String url,SimpleDraweeView imageview){
		//通过fresco加载图片
		ProgressiveJpegConfig jpegConfig = new ProgressiveJpegConfig() {
            @Override
            public int getNextScanNumberToDecode(int scanNumber) {
                return scanNumber + 2;
            }

            @Override
            public QualityInfo getQualityInfo(int scanNumber) {
                boolean isGoodEnough = (scanNumber >= 5);
                return ImmutableQualityInfo.of(scanNumber, isGoodEnough, false);
            }
        };
        Uri uri=Uri.parse(url);
        ImagePipelineConfig.newBuilder(mContext).setProgressiveJpegConfig(jpegConfig).build();
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri).setProgressiveRenderingEnabled(true).build();
        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setTapToRetryEnabled(true)
                .setOldController(imageview.getController())//使用oldController可以节省不必要的内存分配
                .build();
        imageview.setController(draweeController);
	}*/
	public Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case WHAT_HEADER:
				if(mRecommentsVideo.size()>0){
					Content poster=mRecommentsVideo.get(0).getPoster();
					List<Resource> resposter=poster.getResources();
					String url = resposter.get(0).getUrl();
//					ivRecommendVideoIcon.setFinisher(new Finisher() {
//						@Override
//						public void run() {
//							//4dp的圆角
//							Bitmap bitmap = BitmapUtil.getRoundedCornerBitmap(ivRecommendVideoIcon.getImage(),DensityUtil.dip2px(mContext, 4));
//							ivRecommendVideoIcon.setImageBitmap(bitmap);
//						}
//					});
//					 setFrescoImageView(url, ivRecommendVideoIcon);
					ivRecommendVideoIcon.setUrl(url);
					VOD vod = mRecommentsVideo.get(0);
					String videoName = vod.getName();
					tvRecommendVideoName.setText(videoName);
					Content videoContent = mRecommentsVideo.get(0).getVideo();
					if (videoContent != null && videoContent.getSelCount() != null) {
						long watchCount = videoContent.getSelCount();
						tvRecommendVideoViews.setText(String.valueOf(watchCount));
					}
					
					if(videoContent.getResources()!=null&&videoContent.getResources().size()>0){
						//获得时长
						Long times = videoContent.getResources().get(0).getSize();
						if(times != null){
							tvRecommendVideoDuration.setText(DateFormat.formatTime(times));
						}
					}
				}
				break;
			}
		}
	};
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		if(mFragmentView == null) {
//			mFragmentView = inflater.inflate(R.layout.fragment_video_home, container, false);
//			headerGridView = (HeaderGridView) mFragmentView.findViewById(R.id.gridView);
//			mVideoLiveNoneMoreVideoTV = (TextView) mFragmentView.findViewById(R.id.video_live_none_more_video_textview);
//			headerView = inflater.inflate(R.layout.view_recommend_video_hader, null);
//			headerGridView.addHeaderView(headerView);
//			videoSerivce = new VideoService(getActivity());
//			mVideoPlayAdapter = new VideoAdapter(getActivity(), mRecommentsVideo);
//	        headerGridView.setAdapter(mVideoPlayAdapter);
//	        headerGridView.setOnItemClickListener(new ItemClickListener());
//			initView();
//			//获得索引值
////			Bundle bundle = getArguments();
////			if (bundle != null) {
////				mCurIndex = bundle.getInt(FRAGMENT_INDEX);
////			}
//			isPrepared = true;
//			lazyLoad();
//		}
//		
//		//因为共用一个Fragment视图，所以当前这个视图已被加载到Activity中，必须先清除后再加入Activity
//		ViewGroup parent = (ViewGroup)mFragmentView.getParent();
//		if(parent != null) {
//			parent.removeView(mFragmentView);
//		}
//		return mFragmentView;
//	}
	private void initView(final Context context) {
		mFragmentView =  LayoutInflater.from(context).inflate(R.layout.fragment_video_home,this);
		lv_video_list = (ListView) mFragmentView.findViewById(R.id.lv_video_list);
//		View headerOne = new View(getContext());
//		headerOne.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT,DensityUtil.dip2px(getContext(), 98)));
		headerTwo = new View(getContext());
		headerTwo.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT,DensityUtil.dip2px(getContext(), 48)));
//		headerGridView.addHeaderView(headerOne);
		lv_video_list.addHeaderView(headerTwo);
		headerView =  LayoutInflater.from(context).inflate(R.layout.view_recommend_video_hader, null);
		lv_video_list.addHeaderView(headerView);
		videoSerivce = new VideoService(context);
		mVideoPlayAdapter = new VideoAdapter(context, mRecommentsVideo);
		lv_video_list.setAdapter(mVideoPlayAdapter);
//		lv_video_list.setOnItemClickListener(new ItemClickListener());
		ivNoVideo = (LinearLayout) mFragmentView.findViewById(R.id.iv_no_video);
		ivRecommendVideoIcon = (ImageView) headerView.findViewById(R.id.iv_recommend_video_icon);
		tvRecommendVideoName = (TextView) headerView.findViewById(R.id.tv_recommend_video_name);
		tvRecommendVideoDuration = (TextView) headerView.findViewById(R.id.tv_video_duration);
		tvRecommendVideoViews = (TextView) headerView.findViewById(R.id.tv_video_views);
		loadingView = mFragmentView.findViewById(R.id.loadingView);
		ivRecommendVideoIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mRecommentsVideo.size()>0) {
					VOD vod = mRecommentsVideo.get(0);
					Content video=vod.getVideo();
					List<Resource> resvideo=video.getResources();
					Intent intent = new Intent(context,Player.class);
					intent.putExtra("videocontent", (Serializable)mRecommentsVideo);
					intent.putExtra("position", 0);
					intent.putExtra("channel", mChannel);
					intent.putExtra("filename",resvideo.get(0).getUrl() );
					intent.putExtra("epgname", vod.getName());
					context.startActivity(intent);
				}
			}
		});
		lv_video_list.setLoadingListener(new LoadingListener<VOD>() {
			
			@Override
			public List<VOD> loadingS(int offset, int requestCount) {
				isLoadService = true;
				return videoSerivce.getRecommendVideo(mChannel.getId(), offset, requestCount, mChannel.getType(),false);
			}

			@Override
			public void loadPost(List<VOD> datas) {
				loadingView.setVisibility(View.GONE);
				if(datas!=null && datas.size()>0){
					lv_video_list.setVisibility(View.VISIBLE);
					mRecommentsVideo.addAll(datas);
					if(isLoadService){
						lv_video_list.setRequestCount(6);
						mVideoPlayAdapter.setRecommentVideo(mRecommentsVideo);
					}else{
						mVideoPlayAdapter.clearRecommentVideo(mRecommentsVideo);
					}					
					handler.sendEmptyMessage(WHAT_HEADER);
					initScrollListener(lv_video_list);
				}else{
					if(mRecommentsVideo.size()<=0){
						if(isLoadService){
							ivNoVideo.setVisibility(View.VISIBLE);
							lv_video_list.setVisibility(View.GONE);	
						}else{
							loadingView.setVisibility(View.VISIBLE);			
						}
					}
				}
			}

			@Override
			public List<VOD> loadingL(int offset, int requestCount) {
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(!isLoadService){
					return videoSerivce.getRecommendVideo(mChannel.getId(), offset, requestCount, mChannel.getType(),true);	
				}else{
					return null;
				}
			}

			@Override
			public List<VOD> getFillList() {
				return mRecommentsVideo;
			}

			@Override
			public void onNoMoreData() {
			}
		});
	}
//	@Override
//	protected void lazyLoad() {
////		if (!isPrepared || !isVisible || mHasLoadedOnce) {
////			return;
////		}
////		headerGridView.setVisibility(View.GONE);
////		pullToRefresh.setVisibility(View.GONE);
////		loadingView.setVisibility(View.VISIBLE);
////		getChannelVideo(null);
//	}

//	private DrawerScrollListener<HeaderGridView> drawerListener;
//	private void getChannelVideo(final PullToRefreshLayout pullToRefreshLayout,final boolean isLocal) {
//		if (pullToRefreshLayout == null) {
//			COUNT= 7;
//		}else{
//			COUNT= 6;
//		}
//		new LoadingDataTask() {
//			private List<VOD> content;
//			@Override
//			public void onPreExecute() {
//			}
//			@Override
//			public void onPostExecute() {
////				mHasLoadedOnce= true;
//				loadingView.setVisibility(View.GONE);
//				if (content != null && content.size() > 0) {
//					pullToRefresh.setVisibility(View.VISIBLE);
//					headerGridView.setVisibility(View.VISIBLE);
//					if(content.size()<COUNT){
//						headerGridView.setCanPlull(false);
//					}
//					
//					if (pullToRefreshLayout != null) {
//						mRecommentsVideo.addAll(content);
//						mVideoPlayAdapter.setRecommentVideo(mRecommentsVideo);	
//					}else{
//						mRecommentsVideo.clear();
//						mRecommentsVideo.addAll(content);
//						mVideoPlayAdapter.clearRecommentVideo(mRecommentsVideo);
//					}
//					
//					handler.sendEmptyMessage(WHAT_HEADER);
//					showNoneMoreVideo(content);
//					if (pullToRefreshLayout != null) {
//						pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
//					}else{
//						headerGridView.setSelection(0);
//					}
//					initScrollListener(headerGridView);
//					if (!isLoadService) {
//						isLoadService = true;
//						headerGridView.setCanPlull(true);
//						getChannelVideo(null,false);
//					}
//				}else {
//					if (pullToRefreshLayout != null) {
//						pullToRefreshLayout.setLoadDoneText(getResources().getString(R.string.load_no_videos));
//						pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.NOMOREDATA);
//					}else{
//						if (!isLoadService) {
//							loadingView.setVisibility(View.VISIBLE);
//							isLoadService = true;
//							getChannelVideo(null,false);
//						}else{
//							ivNoVideo.setVisibility(View.VISIBLE);	
//						}
//					}
//				}
//				
//			}
//			
//			@Override
//			public void doInBackground() {
//				try {
//					Thread.sleep(300);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				content = videoSerivce.getRecommendVideo(mChannel.getId(), Offset, COUNT, mChannel.getType(),isLocal);
//			}
//		}.execute();	
//	}
	/**
	 * 显示没有morevideo
	 * 
	 * @param responseDatas
	 */
//	private void showNoneMoreVideo(List<VOD> responseDatas) {
//		if (responseDatas.size() == 0) {
//			mVideoLiveNoneMoreVideoTV.setVisibility(View.VISIBLE);
//		} else {
//			mVideoLiveNoneMoreVideoTV.setVisibility(View.GONE);
//		}
//	}
//	@Override
//	public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
//		
//	}
//
//	@Override
//	public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
//		// 加载数据
//		Offset += COUNT;
//		getChannelVideo(pullToRefreshLayout,false);
//	}
	public void clearVideo(){
		loadingView.setVisibility(View.VISIBLE);
		lv_video_list.setVisibility(View.GONE);
	}
	public void setChannel(ChannelVO mChannel){
		this.mChannel = mChannel;
		LoadingDataTask.cancelExistedTimers();
		loadingView.setVisibility(View.VISIBLE);
		lv_video_list.setVisibility(View.GONE);
//		pullToRefresh.setVisibility(View.GONE);
		Offset=0;
		isLoadService = false;
//		headerGridView.setCanPlull(true);
//		getChannelVideo(null,true);	
		if(mVideoPlayAdapter!=null){
			mVideoPlayAdapter.setmChannel(mChannel);
		}
		mRecommentsVideo.clear();
		lv_video_list.setRequestCount(7);
		lv_video_list.loadingData(true);
		initTabsStatus();
	}
}
