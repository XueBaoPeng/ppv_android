package com.star.mobile.video.liveandvideo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.star.cms.model.Category;
import com.star.cms.model.Package;
import com.star.cms.model.TVPlatformInfo;
import com.star.cms.model.enm.TVPlatForm;
import com.star.cms.model.vo.ChannelVO;
import com.star.mobile.video.R;
import com.star.mobile.video.activity.ChoosePlatformActivity;
import com.star.mobile.video.channel.ChannelControlView;
import com.star.mobile.video.channel.ChannelDetailView;
import com.star.mobile.video.channel.ChannelRateActivity;
import com.star.mobile.video.channel.ChatBottomInputView;
import com.star.mobile.video.channel.ChatCustomizeCallback;
import com.star.mobile.video.home.HomeActivity;
import com.star.mobile.video.home.tab.TabFragment;
import com.star.mobile.video.liveandvideo.ResizeLayout.OnResizeListener;
import com.star.mobile.video.service.CategoryService;
import com.star.mobile.video.service.ChannelService;
import com.star.mobile.video.service.PackageService;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.Constants;
import com.star.mobile.video.util.DensityUtil;
import com.star.mobile.video.util.ImageUtil;
import com.star.mobile.video.util.LoadingDataTask;
import com.star.mobile.video.util.ToastUtil;
import com.star.mobile.video.widget.FancyCoverFlow;
import com.star.ui.DragTopLayout;
import com.star.ui.DragTopLayout.PanelListener;
import com.star.ui.DragTopLayout.PanelState;
import com.star.ui.FlowLayout;
import com.star.util.Logger;
import com.star.util.app.GA;

import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Lee
 * @version 1.0
 * @param <T>
 * @date 2015/10/28 motify by lee 2015/12/18
 */
public class PlayFragment<T> extends TabFragment implements OnPageChangeListener, OnItemSelectedListener,
		AnimationListener, ChatCustomizeCallback, OnClickListener, OnItemClickListener, OnItemLongClickListener {
	private static final String TAG = "PlayFragment";
	private View mView;
	private FancyCoverFlow mFancyCoverFlow;
	private TitleCoverFlowAdapter mFancyCoverFlowAdapter;
	private DragTopLayout mChannelShowDrag;
	private GridView mFavoriteCollectGridView;
	private FavoriteCollectAdapter mFacoriteCollectAdapter;
	private ViewPager mHomeViewPager;
	private HomeViewPagerAdapter mHomeViewPagerAdapter;
	// private ImageView mFavoriteImageView;
	private ImageView mFavoriteSmallImageView;
	private ImageView mFavoriteBigImageView;
	private RelativeLayout mFavoriteSmallRL;
	/**
	 * 频道信息
	 */
	private TextView mChannelName;
	private TextView mChannelPackage;
	private TextView mChannelCategory;
	// 流失布局
	private FlowLayout mPlayFlowLayoutPackages;
	private FlowLayout mPlayFlowLayoutCategorys;
	private FlowLayout mPlayFlowLayoutChooseInfo;
	private RelativeLayout mViewFlowlayoutRL;
	// 展开、收缩频道的ImageView
	private ImageView mChannelExpandIV;
	private List<View> mViewDatas = new ArrayList<View>();
	private ChannelService mChannelService;
	private PackageService mPackageService;
	private CategoryService mCategoryService;
	// 获得的频道列表
	private List<ChannelVO> mTotalChannels = new ArrayList<ChannelVO>();
	private ChannelControlView channelControlView;
	private ChatBottomInputView chatBottomInputView;
	private com.star.ui.DragTopLayout dragLayout;
	private boolean isFirstEnter = true;
	// viewpager加载的view数量
	private int VIEW_COUNT = 3;
	private List<View> cachedViews = new ArrayList<View>();
	private int star = 0;
	private Thread thread;
//	private int position;
	private int recordPosition;
	private boolean isfav;
	private Package selectPkg = null;
	private Category selectCgy = null;
	private List<Category> mCategorys = new ArrayList<Category>();
	private List<Package> mPackages = new ArrayList<Package>();
	private List<ChannelVO> mChannels = new ArrayList<ChannelVO>();
	private Long mChannelId;
	private ChannelVO mCurrentChannel;
	// 选中的信息列表
	private List<TextView> mChooseInfos = new ArrayList<TextView>();
	private List<TextView> mPackageChooseInfos = new ArrayList<TextView>();
	private List<TextView> mCategroyChooseInfos = new ArrayList<TextView>();
	
	private RatingBar ratingChannel;
	private LinearLayout ll_rate;
	// 双击事件记录最近一次点击的ID
	private long lastClickId;
	/**
	 * 当前选中的itemid
	 */
	private long mSelectItemId;
	
	// 双击事件记录最近一次点击的时间
	private long lastClickTime;

	//GA对长按，双击，收藏的统计
	private String STATISTICS_CATEGORY = "Channel";
	private String FAVORITE_DOUBLE = "Favourite_double";
	private String FAVORITE_LONG = "Favourite_long";
	private String FAVORITE_ICON = "Favourite_icon";

	//平台提示dish decoder
	private RelativeLayout decoder_dish_left;
	private RelativeLayout decoder_dish_right;
	private ImageView  decoder_image;
	private ImageView  dish_image;
	private TextView  decoder_text;
	private TextView  dish_text;

	private TextView channel_dtt_number;
	private TextView channel_dth_number;
	private View dtt_layout;
	private View dth_layout;
	private TextView dtt_dish;

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				loadVideo();
				break;

			default:
				break;
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mView != null) {
			ViewGroup parent = (ViewGroup) mView.getParent();
			if (parent != null) {
				parent.removeView(mView);
			}
			return mView;
		}
		mView = inflater.inflate(R.layout.title_cover_flow, null);
		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					star++;
					if (star == 1) {
						Message msg = new Message();
						msg.what = 1;
						handler.sendMessage(msg);
					}
				}
			}
		});
		initView();
		initData();
		return mView;
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		ResizeLayout resizeLayout = (ResizeLayout) mView.findViewById(R.id.resizeLayout);
		mFancyCoverFlow = (FancyCoverFlow) mView.findViewById(R.id.fancy_cover_flow);
		mChannelShowDrag = (DragTopLayout) mView.findViewById(R.id.channel_show_view);
		mChannelShowDrag.setCollapseOffset(DensityUtil.dip2px(getActivity(), 95));
		mChannelShowDrag.openTopView(false);
		mChannelShowDrag.listener(new PanelListener() {

			@Override
			public void onSliding(float ratio) {
				initFlowLayout();
				if (android.os.Build.VERSION.SDK_INT < 11) {
					mPlayFlowLayoutChooseInfo.getBackground().setAlpha((int) (1 - ratio));
					mViewFlowlayoutRL.getBackground().setAlpha((int) (ratio));
				} else {
					mPlayFlowLayoutChooseInfo.setAlpha(1 - ratio);
					mViewFlowlayoutRL.setAlpha(ratio);
				}
			}

			@Override
			public void onRefresh() {
			}

			@Override
			public void onPanelStateChanged(PanelState panelState) {
				if (panelState == PanelState.COLLAPSED) {
					closeFlowLayout();
					setFlowLayoutChooseInfo();
					Animation an = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_reverse_rotate_anticlock);
					an.setFillAfter(true);
					mChannelExpandIV.startAnimation(an);
					// 没有选中的时候选中All这个选项
					if (mChooseInfos.size() == 0) {
						TextView childView = (TextView) mPlayFlowLayoutPackages.getChildAt(0);
						setFlowLayoutChooseTextView(childView);
						selectCgy = null;
						selectPkg = null;
						getChannelsAndUpdateUI();
					}
				} else if (panelState == PanelState.EXPANDED) {
					openFlowLayout();
					Animation an = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_reverse_rotate_clock);
					an.setFillAfter(true);
					mChannelExpandIV.startAnimation(an);
				}
			}
		});
		mFavoriteSmallRL = (RelativeLayout) mView.findViewById(R.id.favorit_small_rl);
		// mFavoriteImageView = (ImageView)
		// mView.findViewById(R.id.favority_imageview);
		mFavoriteSmallImageView = (ImageView) mView.findViewById(R.id.favority_small_imageview);
		mFavoriteSmallImageView.setVisibility(View.GONE);
		mFavoriteBigImageView = (ImageView) mView.findViewById(R.id.favority_big_imageview);
		mFavoriteBigImageView.setVisibility(View.GONE);
		mFavoriteCollectGridView = (GridView) mView.findViewById(R.id.gv_favorite_collect);
		mFavoriteCollectGridView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				mChannelShowDrag.setTouchMode(ImageUtil.isAdapterViewAttach(view));
			}
		});
		mHomeViewPager = (ViewPager) mView.findViewById(R.id.home_viewpager);
		dragLayout = (com.star.ui.DragTopLayout) mView.findViewById(R.id.dv_channel_detail);
		channelControlView = (ChannelControlView) mView.findViewById(R.id.cc_contro_view);
		chatBottomInputView = (ChatBottomInputView) mView.findViewById(R.id.cb_chat_view);
		channelControlView.setChatCustomizeCallback(this);
		mChannelName = (TextView) mView.findViewById(R.id.channel_name);
		channel_dtt_number= (TextView) mView.findViewById(R.id.channel_dtt_number);
		channel_dth_number= (TextView) mView.findViewById(R.id.channel_dth_number);
		dtt_layout = mView.findViewById(R.id.decoder_relativelayout);
		dth_layout = mView.findViewById(R.id.dish_relativelayout);
		mChannelPackage = (TextView) mView.findViewById(R.id.channel_package);
		mChannelCategory = (TextView) mView.findViewById(R.id.channel_category);

		mPlayFlowLayoutPackages = (FlowLayout) mView.findViewById(R.id.view_flowlayout_packages);
		mPlayFlowLayoutCategorys = (FlowLayout) mView.findViewById(R.id.view_flowlayout_categorys);
		mPlayFlowLayoutChooseInfo = (FlowLayout) mView.findViewById(R.id.view_flowlayout_choose_info);
		mChannelExpandIV = (ImageView) mView.findViewById(R.id.channel_expand_imageview);
		mViewFlowlayoutRL = (RelativeLayout) mView.findViewById(R.id.view_flowlayout_rl);
		mView.findViewById(R.id.ll_drag_container).setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
		ll_rate = (LinearLayout)mView.findViewById(R.id.ll_rate);
		ll_rate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),ChannelRateActivity.class);
				intent.putExtra("channel", mCurrentChannel);
				CommonUtil.startActivity(getActivity(), intent);
			}
		});
		ratingChannel = (RatingBar)mView.findViewById(R.id.rating_channel);
		resizeLayout.setOnResizeListener(new OnResizeListener() {
			@Override
			public void OnResize(int w, int h, int oldw, int oldh) {
				if (h > oldh) {
					if (mViewDatas != null && mViewDatas.size() > 0) {
						int position = mHomeViewPager.getCurrentItem();
						ChannelDetailView channelDetailView = (ChannelDetailView) mViewDatas.get(position % mViewDatas.size());
						channelDetailView.updateUiChat();
					}
				}
			}
		});
		decoder_dish_left= (RelativeLayout) mView.findViewById(R.id.decoder_layout_left);
		decoder_dish_right= (RelativeLayout) mView.findViewById(R.id.decoder_layout_right);
		decoder_image= (ImageView) mView.findViewById(R.id.image_question_left);
		dish_image= (ImageView) mView.findViewById(R.id.image_question_right);
		decoder_text= (TextView) mView.findViewById(R.id.tv_decoder);
		dish_text= (TextView) mView.findViewById(R.id.tv_dish);
		dtt_dish= (TextView) mView.findViewById(R.id.dth_dtt_text);
		//通过平台类型改变提示
		change_platform();
		//设置平台介绍详细信息
		show_platform();
	}

	private void show_platform() {
		decoder_image.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), ChoosePlatformActivity.class);
				intent.putExtra("platform_type", 0);
				CommonUtil.startActivity(getActivity(), intent);
			}
		});
		dish_image.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), ChoosePlatformActivity.class);
				intent.putExtra("platform_type", 1);
				CommonUtil.startActivity(getActivity(), intent);
			}
		});
	}

	/**
	 * 改变平台
	 */
	private void  change_platform(){

		decoder_dish_left.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(Constant.CURRENT_PLATFORM.equals(TVPlatForm.DTT)){
					return;
				}
				Constant.CURRENT_PLATFORM=TVPlatForm.DTT;
				clearAndRefreshData();
				decoder_dish_left.setBackground(getResources().getDrawable(R.drawable.decoder_dish_bg_left_press));
				decoder_dish_right.setBackground(getResources().getDrawable(R.drawable.decoder_dish_bg_right));
				decoder_image.setImageResource(R.drawable.ic_info_question_orange);
 				dish_image.setImageResource(R.drawable.ic_info_question_white);
				decoder_text.setTextColor(getResources().getColor(R.color.orange_color));
				dish_text.setTextColor(getResources().getColor(R.color.white));
			}
		});
		decoder_dish_right.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(Constant.CURRENT_PLATFORM.equals(TVPlatForm.DTH)){
					return;
				}
				Constant.CURRENT_PLATFORM=TVPlatForm.DTH;
				clearAndRefreshData();
				decoder_dish_left.setBackground(getResources().getDrawable(R.drawable.decoder_dish_bg_left));
				decoder_dish_right.setBackground(getResources().getDrawable(R.drawable.decoder_dish_bg_right_press));
 				decoder_image.setImageResource(R.drawable.ic_info_question_white);
 				dish_image.setImageResource(R.drawable.ic_info_question_orange);
				decoder_text.setTextColor(getResources().getColor(R.color.white));
				dish_text.setTextColor(getResources().getColor(R.color.orange_color));
			}
		});

	}
	/**
	 * 初始化数据
	 */
	private void initData() {
		mChannelService = new ChannelService(getActivity());
		mFancyCoverFlowAdapter = new TitleCoverFlowAdapter(getActivity(), mTotalChannels);
		mFancyCoverFlow.setAdapter(mFancyCoverFlowAdapter);

		setFancyCoverFlow();
		mFancyCoverFlow.setOnItemLongClickListener(this);
		mFancyCoverFlow.setOnItemClickListener(this);
		mFavoriteSmallRL.setOnClickListener(this);
		mFacoriteCollectAdapter = new FavoriteCollectAdapter(getActivity(), mChannels, R.layout.favorite_collect_item);
		mFavoriteCollectGridView.setAdapter(mFacoriteCollectAdapter);
		mFavoriteCollectGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				hideFavoriteCollectRL();
				// 点击gridview里的某一个item时，会跳转指定的频道
				skipToAssignChannel(position);
			}

		});
		mChannelExpandIV.setOnClickListener(this);
		mPackageService = new PackageService(getActivity());
		mCategoryService = new CategoryService(getActivity());
	}

	/**
	 * 情况并刷新下拉列表数据
	 */
	public void clearAndRefreshData() {
		mChooseInfos.clear();
		//清空packages和category的数据
		mPackageChooseInfos.clear();
		mCategroyChooseInfos.clear();
		isfav = false;
		selectPkg = null;
		selectCgy = null;
		loadingCategory();
		loadingPackage();
		getChannelsAndUpdateUI();
	}

	/**
	 * 点击gridview里的某一个item时，会跳转指定的频道
	 *
	 * @param position
	 */
	private void skipToAssignChannel(int position) {
		if (mChannels != null && mChannels.size() > 0) {
			ChannelVO channelVO = mChannels.get(position);
			if (channelVO != null) {
				if (mTotalChannels != null && mTotalChannels.size() > 0) {
					for (int j = 0; j < mTotalChannels.size(); j++) {
						ChannelVO totalChannelVO = mTotalChannels.get(j);
						if (totalChannelVO.getId().equals(channelVO.getId())) {
							mFancyCoverFlow.setSelection(j, true);
							break;
						}
					}
				}
			} else {
				// TODO channelVO=null
			}
		} else {
			mFancyCoverFlow.setSelection(position, true);
		}
	}

	@Override
	public void onStart() {
		setCurrentChannel();
		super.onStart();
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		return super.onContextItemSelected(item);
	}

	/**
	 * 设置频道信息
	 */
	private void setChannelInfo(ChannelVO channel) {
		if (channel != null) {
			mChannelId = channel.getId();
			mChannelName.setText(channel.getName());
			mChannelPackage.setText("");
			dtt_dish.setText("");
			try {
				dtt_layout.setVisibility(View.GONE);
				dth_layout.setVisibility(View.GONE);
				List<TVPlatformInfo> infos = channel.getOfAreaTVPlatforms().get(0).getPlatformInfos();
				String platform="";
				String pkg = "";
				for(TVPlatformInfo info : infos) {
					if(info.getOfPackage()!=null)
						pkg += "/"+info.getOfPackage().getName();
					if (TVPlatForm.DTT.equals(info.getTvPlatForm())) {
						channel_dtt_number.setText(info.getChannelNumber());
						dtt_layout.setVisibility(View.VISIBLE);
						platform+="/DTT";
					}else if(TVPlatForm.DTH.equals(info.getTvPlatForm())){
						channel_dth_number.setText(info.getChannelNumber());
						dth_layout.setVisibility(View.VISIBLE);
						platform+="/DTH";
					}
				}
				if (platform.startsWith("/")){
					platform=platform.substring(1);
				}
				if (pkg.startsWith("/")){
					pkg=pkg.substring(1);
				}
				mChannelPackage.setText(pkg);
				dtt_dish.setText(platform);
				Package channelPackage = channel.getOfPackage();
				if (channelPackage.getName() != null && !channelPackage.getName().isEmpty()) {
					mChannelPackage.setText(channelPackage.getName());
				}
			}catch (Exception e){
			}
			if(channel.getCommentTotalScore()>0){
				float channelScore=(float)channel.getCommentTotalScore()/channel.getCommentTotalCount();
				ratingChannel.setRating(channelScore);
			} else {
				ratingChannel.setRating(0f);
			}

			getCategorys(channel);
		}
	}

	/**
	 * 添加fragment view
	 */
	private void addFragmentView() {
		if (mTotalChannels.size() > 0) {
			for (int i = 0; i < VIEW_COUNT; i++) {
				ChannelDetailView view = new ChannelDetailView(getActivity());
				view.setBottomDrawer(getHomeBottomTab());
				view.setChannelControlView(channelControlView);
				view.setChannelDetailDrawer(dragLayout);
				view.setScrollListener(getDrawerListener());
				mViewDatas.add(view);
			}
		}
		mHomeViewPagerAdapter = new HomeViewPagerAdapter(mViewDatas, mTotalChannels);
		mHomeViewPager.setAdapter(mHomeViewPagerAdapter);
		mHomeViewPager.setOnPageChangeListener(this);
		mHomeViewPager.setOffscreenPageLimit(1);
	}

	/**
	 * 设置FancyCoverFlow参数
	 */
	@SuppressWarnings("deprecation")
	private void setFancyCoverFlow() {
		int imageViewSpaceParams = DensityUtil.dip2px(getActivity(), 10);// 把10dp转成像素值
		this.mFancyCoverFlow.setUnselectedAlpha(0.5f);
		this.mFancyCoverFlow.setUnselectedSaturation(1.0f);
		this.mFancyCoverFlow.setUnselectedScale(0.7f);
		this.mFancyCoverFlow.setSpacing(imageViewSpaceParams);
		this.mFancyCoverFlow.setMaxRotation(0);
		this.mFancyCoverFlow.setScaleDownGravity(0.8f);
		this.mFancyCoverFlow.setActionDistance(FancyCoverFlow.ACTION_DISTANCE_AUTO);
		this.mFancyCoverFlow.setOnItemSelectedListener(this);
	}

	/**
	 * 初始化频道
	 *
	 * @param chns
	 *            后台获得的数据集
	 */
	public void initChannels(List<ChannelVO> chns) {
		Constant.CURRENT_PLATFORM=TVPlatForm.DTT;
		mTotalChannels.clear();
		mTotalChannels.addAll(chns);
		setDatasForAdapter();
		addFragmentView();
		mChannelId = SharedPreferencesUtil.getCurrentChannel(getActivity());
		if (mChannelId != null && mChannelId != 0) {
			setCurrentChannel();
		}else{
			//默认跳转到德甲频道
			mChannelId = Constants.BUNDESLIGA_CHANNEL_ID;
			setCurrentChannel();
		}
		mCurrentChannel = mTotalChannels.get(recordPosition);
		setInitData(recordPosition);
	}

	/**
	 * 设置初始化数据
	 */
	private void setInitData(int position) {
		hideFavoriteCollectRL();
		if (mTotalChannels != null && mTotalChannels.size() > 0) {
			mFancyCoverFlowAdapter.setSelectPosition(position);
			mHomeViewPager.setCurrentItem(position);
			setChannelData(position);
			star = 0;
			if (thread.getState() == State.NEW) {
				thread.start();
			}
			//显示收藏
			showFavorite(position);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 显示收藏
	 */
	private void showFavorite(int position) {
		if (mCurrentChannel!=null&&mCurrentChannel.isFav()) {
			mFavoriteSmallImageView.setVisibility(View.VISIBLE);
		} else {
			mFavoriteSmallImageView.setVisibility(View.GONE);
		}
	}

	/**
	 * 给Adapter设置数据
	 */
	private void setDatasForAdapter() {
		mFancyCoverFlowAdapter.setCoverFlowData(mTotalChannels);
	}

	/**
	 * 获得Categorys
	 */
	private void getCategorys(final ChannelVO channel) {
		new LoadingDataTask() {
			private ChannelVO vo;

			@Override
			public void onPreExecute() {
			}

			@Override
			public void onPostExecute() {
				if (vo != null) {
					setCategroyInfo(vo);
				}
			}

			@Override
			public void doInBackground() {
				vo = mChannelService.getChannelById(channel.getId());
			}
		}.execute();
	}

	/**
	 * 设置category信息
	 *
	 * @param
	 */
	private void setCategroyInfo(ChannelVO channel) {
		if (channel.getCategories() != null) {
			if (channel.getCategories().size() > 0) {
				List<Category> categories = channel.getCategories();
				if (categories.get(0) != null) {
					Category category = categories.get(0);
					if (category.getName() != null && !category.getName().isEmpty()) {
						mChannelCategory.setText(category.getName());
					}else{
						mChannelCategory.setText("");
					}
				}
			}else{
				mChannelCategory.setText("");
			}
		}
	}

	/**
	 * 显示GridView，并伴随动画
	 */
	public void showTitleCoverFlowGridView() {
		mChannelShowDrag.setVisibility(View.VISIBLE);
		Animation upToDownAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_up_down);
		upToDownAnimation.setFillAfter(true);
		mFavoriteCollectGridView.startAnimation(upToDownAnimation);
		Animation alphaAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_alpha);
		alphaAnimation.setFillAfter(true);
		mChannelShowDrag.startAnimation(alphaAnimation);
		if (getHomeBottomTab().isOpen()) {
			getHomeBottomTab().close();
		}
		openFlowLayout();
		mChannelShowDrag.openTopView(false);
	}

	/**
	 * 隐藏GridView，并伴随动画
	 */
	public void hideTitleCoverFlowGridView() {
		Animation upToDownAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_down_up);
		upToDownAnimation.setFillAfter(true);
		upToDownAnimation.setAnimationListener(this);
		mFavoriteCollectGridView.startAnimation(upToDownAnimation);
		Animation alphaAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_no_alpha);
		alphaAnimation.setFillAfter(true);
		alphaAnimation.setAnimationListener(this);
		mChannelShowDrag.startAnimation(alphaAnimation);
		isShowBottomTab();
		mPlayFlowLayoutChooseInfo.setVisibility(View.INVISIBLE);
	}

	/**
	 * 是否显示bottomTab
	 */
	private void isShowBottomTab() {
		ChannelDetailView channelDetailView = (ChannelDetailView) mHomeViewPager.getChildAt(0);
		if (channelDetailView != null) {
			int select = channelDetailView.select();
			if (select != 3) {
				if (!getHomeBottomTab().isOpen()) {
					getHomeBottomTab().open();
				}
			}
		}
	}

	private void openFlowLayout() {
		mPlayFlowLayoutChooseInfo.setVisibility(View.INVISIBLE);
		mViewFlowlayoutRL.setVisibility(View.VISIBLE);
		mViewFlowlayoutRL.setAlpha(1);
	}

	private void closeFlowLayout() {
		mPlayFlowLayoutChooseInfo.setVisibility(View.VISIBLE);
		mViewFlowlayoutRL.setVisibility(View.INVISIBLE);
		mPlayFlowLayoutChooseInfo.setAlpha(1);
	}

	public void initFlowLayout() {
		mPlayFlowLayoutChooseInfo.setVisibility(View.VISIBLE);
		mViewFlowlayoutRL.setVisibility(View.VISIBLE);
	}

	/**
	 * 获得收藏类别的状态
	 *
	 * @return
	 */
	public int getFavoriteCollectStatu() {
		return mChannelShowDrag.getVisibility();
	}

	/**
	 * 隐藏coverflow圆角图片
	 */
	public void hidePlayCoverFlowCricleImage() {
		mFancyCoverFlowAdapter.hideCoverFlowCricleImage();
	}

	@Override
	public void onPageScrollStateChanged(int position) {

	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

	}

	/**
	 * 当前viewpager展示的view
	 */
	@Override
	public void onPageSelected(int position) {
		this.mFancyCoverFlow.setSelection(position);
	}



	/**
	 * title coverflow子item选中的时候
	 */
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		mCurrentChannel = mTotalChannels.get(position);
//		this.position = position;
		this.mSelectItemId = id;
		//显示收藏
		showFavorite(position);
		setInitData(position);
	}

	/**
	 * 设置channeldetail数据
	 *
	 * @param position
	 */
	private void setChannelData(int position) {
		if (mCurrentChannel != null) {
			setChannelInfo(mCurrentChannel);
			if (cachedViews != null && cachedViews.size() > 0) {
				for (View v : cachedViews) {
					ChannelDetailView channelDetailView = (ChannelDetailView) v;
					channelDetailView.onStop();
				}
				cachedViews.clear();
			}
			if (mViewDatas != null && mViewDatas.size() > 0) {
				cachedViews.add(mViewDatas.get(position % mViewDatas.size()));
				ChannelDetailView channelDetailView = (ChannelDetailView) mViewDatas.get(position % mViewDatas.size());
				channelDetailView.setChannelControlView(channelControlView);
				channelDetailView.setChannel(mCurrentChannel);
			}
		}
	}

	private void loadVideo() {
		if (mViewDatas != null && mViewDatas.size() > 0) {
			int position = mHomeViewPager.getCurrentItem();
			ChannelDetailView channelDetailView = (ChannelDetailView) mViewDatas.get(position % mViewDatas.size());
			channelDetailView.loadVideo(mCurrentChannel);
			resetDrawerView();
		}
	}

	/**
	 * 隐藏favoriteCollectRL
	 */
	public void hideFavoriteCollectRL() {
		if (mChannelShowDrag.getVisibility() == View.VISIBLE) {
			hideTitleCoverFlowGridView();
			controlHomeActionBar();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 设置当前的channel
	 *
	 */
	private void setCurrentChannel() {
		if (mTotalChannels != null && mTotalChannels.size() > 0) {
			for (int i = 0; i < mTotalChannels.size(); i++) {
				ChannelVO channelVO = mTotalChannels.get(i);
				if (channelVO != null) {
					if (channelVO.getId().equals(mChannelId)) {
						SharedPreferencesUtil.setCurrentChannel(mChannelId, getActivity());
						this.mFancyCoverFlow.setSelection(i, true);
						recordPosition = i;
						break;
					}
				}
			}
		}
	}

	public void setChannelId(Long channelId) {
		this.mChannelId = channelId;
	}

	public void setCurrentChannelLeft() {
		if (mTotalChannels != null && mTotalChannels.size() > 0) {
			if (recordPosition + 1 < mTotalChannels.size()) {
				this.mFancyCoverFlow.setSelection(recordPosition + 1);
			}
		}

	}

	public void setCurrentChannelRight() {
		if (mTotalChannels != null && mTotalChannels.size() > 0) {
			if (recordPosition - 1 >= 0) {
				this.mFancyCoverFlow.setSelection(recordPosition - 1);
			}
		}
	}

	public void openTopView() {
		dragLayout.openTopView(true);
	}

	private void controlHomeActionBar() {
		Activity activity = getActivity();
		if (activity instanceof HomeActivity) {
			((HomeActivity) activity).setActionBarMoreClick(false);
			((HomeActivity) activity).actionBarClockAnimation();
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	@Override
	public void onAnimationStart(Animation animation) {
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		mChannelShowDrag.setVisibility(View.GONE);
		mChannelShowDrag.clearAnimation();
		mFavoriteCollectGridView.clearAnimation();
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
	}

	@Override
	public void onStop() {
		// 获得选中图片的位置，在程序被隐藏时记录这个位置，下次打开的时候还在这个位置。
		saveCurrentChannelId();
		super.onStop();
	}

	/**
	 * 保存当前的频道
	 */
	private void saveCurrentChannelId() {
		if (mCurrentChannel !=null){
			Long currentChannelId = mCurrentChannel.getId();
			if (currentChannelId != null){
				SharedPreferencesUtil.setCurrentChannel(currentChannelId, getActivity());
			}
		}
	}

	@Override
	public void onChatBottom(boolean isShow) {
		if (isShow) {
			chatBottomInputView.setVisibility(View.VISIBLE);
			channelControlView.setChatBottom(chatBottomInputView.getmEtChat(), chatBottomInputView.getSendChat(),
					chatBottomInputView.getSendFace(), chatBottomInputView.getFaceContainer());
		} else {
			chatBottomInputView.setVisibility(View.GONE);
		}

	}

	/**
	 * 获得流失布局里的packages或categorys
	 */
	public void loadingCategory() {
		new LoadingDataTask() {
			@Override
			public void onPreExecute() {
			}

			@SuppressWarnings("unchecked")
			@Override
			public void onPostExecute() {
				if (mCategorys != null) {
					setCategorysData();
				}
			}

			@Override
			public void doInBackground() {
				mCategorys = mCategoryService.getCategorys();
			}
		}.execute();
	}

	/**
	 * 获得流失布局里的packages或categorys
	 */
	public void loadingPackage() {
		new LoadingDataTask() {
			List<Package> ps = null;
			@Override
			public void onPreExecute() {
				selectPkg = null;
			}

			@SuppressWarnings("unchecked")
			@Override
			public void onPostExecute() {
				mPackages.clear();
				if (ps != null) {
					mPackages.addAll(ps);
				}
				setPackagesData();
			}

			@Override
			public void doInBackground() {
				ps = mPackageService.getPackages(Constant.CURRENT_PLATFORM);
			}
		}.execute();
	}

	/**
	 * 获取GridView里的数据
	 */
	public void getChannelsAndUpdateUI() {
		new LoadingDataTask() {
			List<ChannelVO> chns;

			@Override
			public void onPreExecute() {
			}

			@Override
			public void onPostExecute() {
				if (chns != null) {
					mChannels.clear();
					mChannels.addAll(chns);
					mFacoriteCollectAdapter.setDatas(mChannels);
				}
			}

			@Override
			public void doInBackground() {
				chns = mChannelService.getChannels(selectCgy, isfav, selectPkg, Constant.CURRENT_PLATFORM);
			}
		}.execute();
	}

	/**
	 * 设置packges的数据
	 */
	private void setPackagesData() {
		LayoutInflater mInflater = LayoutInflater.from(getActivity());
		mPackages.add(0, null);
		mPackages.add(1, null);
		mPlayFlowLayoutPackages.removeAllViews();
		for (int i = 0; i < mPackages.size(); i++) {
			final TextView tv = (TextView) mInflater.inflate(R.layout.play_flowlayout_textview, mPlayFlowLayoutPackages,
					false);
			final Package p = mPackages.get(i);
			tv.setTag(p);
			if (i == 0) {
				tv.setText("All");
				setFlowLayoutChooseTextView(tv);
			} else if(i == 1){
				tv.setText("Favorite");
			} else {
				if (p != null) {
					tv.setText(p.getName());
				}
			}
			mPlayFlowLayoutPackages.addView(tv);
			tv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (tv.getText().equals("Favorite")) {
						isfav = true;
						selectPkg = null;
						selectCgy = null;
						//清空packages和category的选中状态
						resetChannelTextViewBG(mPlayFlowLayoutPackages);
						resetChannelTextViewBG(mPlayFlowLayoutCategorys);
						//清空packages和category的数据
						mPackageChooseInfos.clear();
						mCategroyChooseInfos.clear();
						//设置Favorite的选中状态
						setFlowLayoutChooseTextView((TextView) v);
						mPackageChooseInfos.add((TextView) v);
						getChannelsAndUpdateUI();
					} else {
						isfav = false;
						Package pk = (Package) v.getTag();
						resetChannelTextViewBG(mPlayFlowLayoutPackages);
						mPackageChooseInfos.clear();
						if (pk == null) {
							if (selectPkg != null || selectCgy != null) {
								if (selectCgy != null)
									resetChannelTextViewBG(mPlayFlowLayoutCategorys);
								selectPkg = null;
								selectCgy = null;
							}
							getChannelsAndUpdateUI();
							mCategroyChooseInfos.clear();
							setFlowLayoutChooseTextView((TextView) v);
						} else {
							if (pk.equals(selectPkg)) {
								selectPkg = null;
								setFlowLayoutTextView((TextView) v);
								if (mPlayFlowLayoutPackages.getChildAt(0) != null) {
									setFlowLayoutChooseTextView((TextView) mPlayFlowLayoutPackages.getChildAt(0));
								}
							} else {
								selectPkg = pk;
								setFlowLayoutChooseTextView((TextView) v);
								mPackageChooseInfos.add((TextView) v);
							}
							getChannelsAndUpdateUI();
						}
					}
				}
			});
		}
	}

	/**
	 * 设置category的数据
	 */
	private void setCategorysData() {
		LayoutInflater mInflater = LayoutInflater.from(getActivity());
		mPlayFlowLayoutCategorys.removeAllViews();
		for (int i = 0; i < mCategorys.size(); i++) {
			final TextView tv = (TextView) mInflater.inflate(R.layout.play_flowlayout_textview,
					mPlayFlowLayoutCategorys, false);
			final Category category = mCategorys.get(i);
			tv.setText(category.getName());
			tv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					resetChannelTextViewBG(mPlayFlowLayoutCategorys);

					TextView textView = (TextView) mPlayFlowLayoutPackages.getChildAt(0);
					if (textView != null) {
						textView.setBackgroundResource(0);
						textView.setTextColor(getResources().getColor(R.color.white));
					}
					mCategroyChooseInfos.clear();
					if (category.equals(selectCgy)) {
						selectCgy = null;
						tv.setBackgroundResource(0);
						tv.setTextColor(getResources().getColor(R.color.white));
						if (mPlayFlowLayoutPackages.getChildAt(0) != null) {
							setFlowLayoutChooseTextView((TextView) mPlayFlowLayoutPackages.getChildAt(0));
						}
					} else {
						selectCgy = category;
						setFlowLayoutChooseTextView(tv);

						mCategroyChooseInfos.add(tv);
					}
					getChannelsAndUpdateUI();
				}
			});
			mPlayFlowLayoutCategorys.addView(tv);
		}
	}

	/**
	 * 设置flowlayout里textview没有选中的颜色和背景色
	 *
	 * @param tv
	 */
	private void setFlowLayoutTextView(final TextView tv) {
		if (tv == null){
			return;
		}
		tv.setBackgroundResource(0);
		tv.setTextColor(getResources().getColor(R.color.white));
	}

	/**
	 * 设置flowLayout里textview选中的字体颜色和背景色
	 *
	 * @param tv
	 */
	private void setFlowLayoutChooseTextView(final TextView tv) {
		if (tv == null){
			return;
		}
		tv.setBackgroundResource(R.drawable.play_flowlayout_textview_bg);
		tv.setTextColor(getResources().getColor(R.color.orange_red));
	}

	/**
	 * 重新设置频道栏目的背景和文字颜色
	 */
	private void resetChannelTextViewBG(FlowLayout flowLayout) {
		int categorysChildCount = flowLayout.getChildCount();
		for (int j = 0; j < categorysChildCount; j++) {
			TextView textView = (TextView) flowLayout.getChildAt(j);
			textView.setBackgroundResource(0);
			textView.setTextColor(getResources().getColor(R.color.white));
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.channel_expand_imageview:
			mChannelShowDrag.toggleTopView(false);
			break;
		case R.id.favorit_small_rl:
			if(SharedPreferencesUtil.getUserName(getActivity()) != null){
				onClickFavArea();
			}else {
				CommonUtil.pleaseLogin(getActivity());
			}
			break;

		default:
			break;
		}
	}

	/**
	 * 设置选中的信息
	 */
	private void setFlowLayoutChooseInfo() {
		mChooseInfos.clear();
		mChooseInfos.addAll(mPackageChooseInfos);
		mChooseInfos.addAll(mCategroyChooseInfos);
		mPlayFlowLayoutChooseInfo.removeAllViews();
		LayoutInflater mInflater = LayoutInflater.from(getActivity());
		if (mChooseInfos != null && mChooseInfos.size() > 0) {
			for (int i = 0; i < mChooseInfos.size(); i++) {
				final TextView tv = (TextView) mInflater.inflate(R.layout.play_flowlayout_textview,
						mPlayFlowLayoutChooseInfo, false);
				TextView textView = mChooseInfos.get(i);
				tv.setText(textView.getText().toString());
				setFlowLayoutChooseTextView(tv);
				mPlayFlowLayoutChooseInfo.addView(tv);
			}
		} else {
			final TextView tv = (TextView) mInflater.inflate(R.layout.play_flowlayout_textview,
					mPlayFlowLayoutChooseInfo, false);
			tv.setText("All");
			setFlowLayoutChooseTextView(tv);
			mPlayFlowLayoutChooseInfo.addView(tv);
		}
	}

	/**
	 * 显示收藏小图的动画
	 */
	private void showFavoriteSmallIVAnim(final View view) {
		ObjectAnimator anim1 = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1.2f);
		ObjectAnimator anim2 = ObjectAnimator.ofFloat(view, "scaleY", 0f, 1.2f);
		ObjectAnimator anim3 = ObjectAnimator.ofFloat(view, "alpha", 0.0f, 1.0f);

		AnimatorSet animSet = new AnimatorSet();
		animSet.setDuration(400);
		animSet.setInterpolator(new DecelerateInterpolator());
		// 两个动画同时执行
		animSet.playTogether(anim3, anim1, anim2);
		animSet.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				super.onAnimationEnd(animation);
				mFavoriteSmallRL.setClickable(true);
				ObjectAnimator anim1 = ObjectAnimator.ofFloat(view, "scaleX", 1.2f, 1.0f);
				ObjectAnimator anim2 = ObjectAnimator.ofFloat(view, "scaleY", 1.2f, 1.0f);
				AnimatorSet animSet = new AnimatorSet();
				animSet.playTogether(anim1,anim2);
				animSet.setDuration(100);
				animSet.setInterpolator(new BounceInterpolator());
				animSet.start();
			}

			@Override
			public void onAnimationStart(Animator animation) {
				super.onAnimationStart(animation);
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				mFavoriteBigImageView.setVisibility(View.VISIBLE);
				showFavoriteBigIVAnim(mFavoriteBigImageView);
			}

		});
		animSet.start();
	}

	/**
	 * 显示收藏大图的动画
	 */
	private void showFavoriteBigIVAnim(final View view) {
		// TODO 显示完图片动画后，还有把数据保存到本地数据库
		ObjectAnimator anim1 = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1.0f);
		ObjectAnimator anim2 = ObjectAnimator.ofFloat(view, "scaleY", 0f, 1.0f);
		ObjectAnimator anim3 = ObjectAnimator.ofFloat(view, "alpha", 0.0f, 1.0f);

		AnimatorSet animSet = new AnimatorSet();
		animSet.setDuration(300);
		animSet.setInterpolator(new DecelerateInterpolator());
		// 两个动画同时执行
		animSet.playTogether(anim3, anim1, anim2);
		animSet.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				Logger.e("animation end start");
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				ObjectAnimator anim1 = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 2.0f);
				ObjectAnimator anim2 = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 2.0f);
				ObjectAnimator anim3 = ObjectAnimator.ofFloat(view, "alpha", 1.0f, 0.0f);
				AnimatorSet animSet = new AnimatorSet();
				animSet.setDuration(300);
				animSet.setInterpolator(new DecelerateInterpolator());
				// 两个动画同时执行
				animSet.playTogether(anim1, anim2);
				animSet.play(anim3).after(anim2);
				animSet.start();
			}

			@Override
			public void onAnimationStart(Animator animation) {
				super.onAnimationStart(animation);
			}

		});
		animSet.start();
	}

	/**
	 * 长按效果
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		GA.sendEvent(STATISTICS_CATEGORY, FAVORITE_LONG, mCurrentChannel.getName() == null? "":mCurrentChannel.getName(), 1);
		long itemId = ((FancyCoverFlow) parent).getAdapter().getItemId(position);
		if (mSelectItemId == itemId) {
			if(SharedPreferencesUtil.getUserName(getActivity()) != null){
				mFavoriteBigImageView.setVisibility(View.VISIBLE);
				if (mFavoriteSmallImageView.getVisibility() != View.VISIBLE) {
					// 显示大图的动画
					showFavoriteBigIVAnim(mFavoriteBigImageView);
				}
				onClickFavArea();
			}else {
				CommonUtil.pleaseLogin(getActivity());
			}
		}

		return false;
	}
	/**
	 * 实现双击的效果
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		long itemId = ((FancyCoverFlow) parent).getAdapter().getItemId(position);
		if (mSelectItemId == itemId) {
			if (itemId == lastClickId && (Math.abs(lastClickTime - System.currentTimeMillis()) < 1000)) {
				if(SharedPreferencesUtil.getUserName(getActivity()) != null){
					GA.sendEvent(STATISTICS_CATEGORY, FAVORITE_DOUBLE, mCurrentChannel.getName() == null? "":mCurrentChannel.getName(), 1);
					lastClickId = 0;
					lastClickTime = 0;
					mFavoriteBigImageView.setVisibility(View.VISIBLE);
					if (mFavoriteSmallImageView.getVisibility() != View.VISIBLE) {
						// 显示大图的动画
						showFavoriteBigIVAnim(mFavoriteBigImageView);
					}
					onClickFavArea();
				}else {
					CommonUtil.pleaseLogin(getActivity());
				}
			} else {
				lastClickId = itemId;
				lastClickTime = System.currentTimeMillis();
			}
		}
		
	}

	private void onClickFavArea() {
		new LoadingDataTask() {
			boolean favStatus;

			@Override
			public void onPreExecute() {
			}

			@Override
			public void onPostExecute() {
				if (favStatus) {
					// 根据状态修改收藏小图片的样子，显示或隐藏
					updateFavIconStatus();
				} else {
					rechangeFavStatus();
				}
			}

			private void rechangeFavStatus() {
				boolean isFav = mCurrentChannel.isFav();
				mCurrentChannel.setFav(!isFav);
			}

			@Override
			public void doInBackground() {
				rechangeFavStatus();
				favStatus = mChannelService.updateChannel(mCurrentChannel);
			}
		}.execute();
	}

	private void updateFavIconStatus() {
		GA.sendEvent(STATISTICS_CATEGORY, FAVORITE_ICON, mCurrentChannel.getName() == null? "":mCurrentChannel.getName(), 1);
		boolean isFav = mCurrentChannel.isFav();
		if (isFav) {
			mFavoriteSmallImageView.setVisibility(View.VISIBLE);
			mFavoriteSmallRL.setClickable(false);
			// 显示小图动画
			showFavoriteSmallIVAnim(mFavoriteSmallImageView);
		} else {
			// 小图片消失
			mFavoriteSmallImageView.setVisibility(View.GONE);
		}





	}

}
