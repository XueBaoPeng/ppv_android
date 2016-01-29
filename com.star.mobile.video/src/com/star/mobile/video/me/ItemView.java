package com.star.mobile.video.me;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.star.mobile.video.R;
import com.star.mobile.video.activity.TellFriendActivity;
import com.star.mobile.video.model.MenuHandle;
import com.star.mobile.video.model.MenuItem;
import com.star.mobile.video.model.MenuItemRes;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.smartcard.SmartCardControlActivity;
import com.star.mobile.video.smartcard.SmartCardService;
import com.star.mobile.video.smartcard.SmartCardSharedPre;
import com.star.mobile.video.tenb.TenbActivity;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.view.NoScrollGridView;

public class ItemView extends LinearLayout {

	private TextView tvItemTitle;
	private TextView tvItemContent;
	private NoScrollGridView gvChildGroup;
	private ImageView ivItemIcon;
	private ImageView ivItemArrow;
	private ImageView ivDot;
	private SmartCardService smartCartService;
	private ImageView coin;//金币
	private RelativeLayout rlParentItemView;
	private Context mContext;
	private int postion;
	public ItemView(Context context, MenuItem<MeMenuItemRes> item) {
		super(context);
		this.mContext = context;
		init();
		setRes(item);
	}

	public ItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}

	private void init() {
		LayoutInflater.from(getContext()).inflate(R.layout.view_parent_item, this);
		tvItemTitle = (TextView) findViewById(R.id.tv_parent_title);
		ivItemIcon = (ImageView) findViewById(R.id.iv_parent_icon);
		ivItemArrow = (ImageView) findViewById(R.id.iv_parent_arrow);
		gvChildGroup = (NoScrollGridView) findViewById(R.id.gv_child_group);
		ivDot = (ImageView) findViewById(R.id.iv_parent_dot);
		rlParentItemView = (RelativeLayout) findViewById(R.id.rl_parent_item);
		smartCartService=new SmartCardService(mContext);
	}

	public void setRes(final MenuItem<MeMenuItemRes> item) {
		tvItemTitle.setText(item.getRes().getItemTitle());
		if (item.getRes().getItemTitle().equals(getContext().getString(R.string.setting))) {
			findViewById(R.id.iv_title_line).setVisibility(View.GONE);
		} else {
			findViewById(R.id.iv_title_line).setVisibility(View.VISIBLE);
		}
		if (item.getRes().isFoucs()) {
			ivDot.setVisibility(View.VISIBLE);
		} else {
			ivDot.setVisibility(View.INVISIBLE);
		}
		ivItemIcon.setImageResource(item.getRes().getUnfocusRes());
	 
	 
		//金币动画
		if(item.getRes().getItemTitle().equals(mContext.getString(R.string.fragment_tag_ccount_manager))){
		
			 getSmartCardInfo();
		}
		if (item.getItemRes() != null && item.getItemRes().size() > 0) {
			gvChildGroup.setAdapter(new SimpleAdapter(item.getItemRes()));
			gvChildGroup.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					MeMenuItemRes itemRes = item.getItemRes().get(position);
					if (mContext.getString(R.string.fragment_tag_ccount_manager).equals(itemRes.getItemTitle())) {
						if (SharedPreferencesUtil.getUserName(mContext) == null) {
							CommonUtil.pleaseLogin(mContext);
						} else {
							startActivityOrFragment(itemRes.getClazz());
						}
					} else {
						startActivityOrFragment(itemRes.getClazz());
					}
				}
			});
			ivItemArrow.setVisibility(View.INVISIBLE);
			// rlParentItemView.setLayoutParams(new
			// LayoutParams(LayoutParams.MATCH_PARENT, 70));
		} else {
			// rlParentItemView.setLayoutParams(new
			// LayoutParams(LayoutParams.MATCH_PARENT, 82));
			ivItemArrow.setVisibility(View.VISIBLE);
		}
		this.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivityOrFragment(item.getRes().getClazz());
			}
		});
	}
	/**
	 * 获得是否绑过智能卡
	 */
	private void getSmartCardInfo(){
		coin=(ImageView)findViewById(R.id.iv_coin);
		final SmartCardSharedPre sharedPre = new SmartCardSharedPre(getContext());
		final int status = sharedPre.getFirstBindStatus();
		 if(status==1){
				coin.setVisibility(View.VISIBLE);
				startAnimation(coin);
		 }else{
				coin.setVisibility(View.GONE);
		 }
		/*smartCartService.isBindSmartCard(new OnResultListener<Integer>() {
			
			@Override
			public void onSuccess(Integer value) {
				if(value!=null){
					 if(value==1){
							coin.setVisibility(View.VISIBLE);
							startAnimation(coin);
					 }else{
							coin.setVisibility(View.GONE);
					 }
				} 
			}
			@Override
			public boolean onIntercept() {
			 
				return false;
			}
			@Override
			public void onFailure(int errorCode, String msg) {
				
			}
		});*/
	}
	
	private void startAnimation(final ImageView imageview){
		
		int toY=(int) (rlParentItemView.getHeight()/2*1.5);
		
 		ObjectAnimator translation = ObjectAnimator.ofFloat(imageview, "translationY", imageview.getBottom(),imageview.getBottom()-toY);
		ObjectAnimator rotation = ObjectAnimator.ofFloat(imageview, "rotationY", 0,360);
		ObjectAnimator alpha1 = ObjectAnimator.ofFloat(imageview, "alpha", 0.2f,1.0f);
		ObjectAnimator alpha2 = ObjectAnimator.ofFloat(imageview, "alpha", 1.0f,0);
 		 translation.setDuration(1000);
 		 translation.setInterpolator(new DecelerateInterpolator());
 		 alpha1.setDuration(1000);
 		 rotation.setDuration(1500);
 		 alpha2.setDuration(500);
		AnimatorSet animSet1 = new AnimatorSet();
		//animSet1.setInterpolator(new DecelerateInterpolator());
		// 两个动画同时执行
 	 	animSet1.play(translation).with(alpha1);
 	 	animSet1.play(rotation).after(translation);
 	 	animSet1.play(alpha2).after(rotation);
 	 	animSet1.addListener(new AnimatorListenerAdapter() {
 	 		@Override
 	 		public void onAnimationEnd(Animator animation) {
 	 			super.onAnimationEnd(animation);
 	 			startAnimation(imageview);
 	 		}
		});
		animSet1.start();
		 
	}
	public class SimpleAdapter extends BaseAdapter {

		private List<MeMenuItemRes> items;

		public SimpleAdapter(List<MeMenuItemRes> items) {
			this.items = items;
		}

		@Override
		public int getCount() {
			return items.size();
		}

		@Override
		public Object getItem(int position) {
			return items.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			view = LayoutInflater.from(getContext()).inflate(R.layout.view_child_item, null);
			TextView tvTitle = (TextView) view.findViewById(R.id.tv_child_title);
			ImageView ivDot = (ImageView) view.findViewById(R.id.iv_child_dot);
			TextView tvContent = (TextView) view.findViewById(R.id.tv_content);
			
			
			MenuItemRes res = items.get(position);
			if (res != null) {
				if (res.isFoucs()) {
					ivDot.setVisibility(View.VISIBLE);
				} else {
					ivDot.setVisibility(View.INVISIBLE);
				}
				tvTitle.setText(res.getItemTitle());
				tvContent.setText(res.getItemContent());
							}
			return view;
		}
	}

	
	private void startActivityOrFragment(final Class<?> clazz) {
		if (clazz == null)
			return;
		try {
			Object obj = clazz.getConstructor().newInstance();
			if (obj instanceof TenbActivity || obj instanceof SmartCardControlActivity||obj instanceof TellFriendActivity) {
				if (SharedPreferencesUtil.getUserName(getContext()) == null) {
					CommonUtil.pleaseLogin(getContext());
					return;
				}
			}
			if (obj instanceof Activity) {
				CommonUtil.startActivity((Activity) getContext(), clazz);
			} else if (obj instanceof Fragment) {
				CommonUtil.startFragmentActivity(getContext(), MenuHandle.getFragmentTag(clazz.getName()), null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
