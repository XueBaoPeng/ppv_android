package com.star.mobile.video.me.mycoins.reward;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.star.cms.model.Award;
import com.star.cms.model.Resource;
import com.star.cms.model.vo.AwardVO;
import com.star.mobile.video.R;
import com.star.mobile.video.util.TextUtil;

public class RewardListAdapter extends BaseAdapter{

	private Context context;
	private List<AwardVO> awards;
	private OnClickListener listeren;
	private OnClickListener listeren_coupon;
	private int myCoins = 0;
	public RewardListAdapter(Context context,List<AwardVO> datas,int myCoins) {
		this.context = context;
		this.awards = datas;
		this.myCoins = myCoins;
	}
	
	public void updateDataAndRefreshUI(List<AwardVO> datas,int myCoins) {
		this.awards = datas;
		this.myCoins = myCoins;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		
		return awards.size();
	}

	@Override
	public Object getItem(int position) {
		
		return awards.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}
	
	public void setOnClick(OnClickListener listener,OnClickListener listener_coupon) {
		this.listeren = listener;
		this.listeren_coupon = listener_coupon;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder viewHolder;
		if(view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(context).inflate(R.layout.view_reward_coins_item, null);
			viewHolder.rewardIcon = (com.star.ui.ImageView) view.findViewById(R.id.iv_reward_item_icon);
			viewHolder.rewardNameOject = (TextView) view.findViewById(R.id.tv_reward_object_name);
			viewHolder.iv_shop= (ImageView) view.findViewById(R.id.iv_shop);
			viewHolder.rewardName = (TextView) view.findViewById(R.id.tv_reward_name);
			viewHolder.rewardPrice = (TextView) view.findViewById(R.id.tv_reward_price);
			viewHolder.tv_hint = (TextView) view.findViewById(R.id.tv_hint);
			viewHolder.ll_object = (LinearLayout) view.findViewById(R.id.ll_object);
			viewHolder.rl_coins_reward = (FrameLayout) view.findViewById(R.id.rl_coins_reward);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		AwardVO awardVo = awards.get(position);
		int awardType = awardVo.getType();
//		viewHolder.rewardIcon.setImageResource(R.drawable.no_picture_basic);
		if(awardType == Award.Type_Wallet) {//优惠卷
			viewHolder.rl_coins_reward.setBackgroundResource(R.drawable.coin_coupon_bg);
			viewHolder.rewardName.setVisibility(View.VISIBLE);
			viewHolder.ll_object.setVisibility(View.GONE);
			viewHolder.tv_hint.setVisibility(View.VISIBLE);
			viewHolder.rewardName.setText(awardVo.getName());
			viewHolder.rewardName.setTag(awardVo);
			view.setOnClickListener(listeren_coupon);
		} else if(awardType == Award.Type_Object) { // 实物
			viewHolder.rl_coins_reward.setBackgroundColor(Color.WHITE);
			viewHolder.rewardName.setVisibility(View.GONE);
			viewHolder.ll_object.setVisibility(View.VISIBLE);
			viewHolder.tv_hint.setVisibility(View.GONE);
			List<Resource> res = awardVo.getPoster().getResources();
			if(res.size() != 0) {
				String listImageUrl = res.get(1).getUrl();
				if(listImageUrl != null) {
					viewHolder.rewardIcon.setUrl(listImageUrl);
				} else {
					viewHolder.rewardIcon.setImageResource(R.drawable.no_picture_basic);
				}
			}
			viewHolder.rewardNameOject.setText(awardVo.getName());
			viewHolder.rewardNameOject.setTag(awardVo);
			view.setOnClickListener(listeren);
		}
		if(myCoins < awardVo.getPrice()){
			viewHolder.iv_shop.setImageResource(R.drawable.ic_shopping_cart_grey_24dp);
		}else{
			viewHolder.iv_shop.setImageResource(R.drawable.ic_shopping_cart_blue_24dp);
		}
		viewHolder.rewardPrice.setText(TextUtil.getInstance().addComma3(awardVo.getPrice()+""));
//		viewHolder.rewardId.setText(awardVo.getId()+"");
//		viewHolder.rewardDescription.setText(awardVo.getDescription());
		
		
		return view;
	}

	
	class ViewHolder {
		public LinearLayout ll_object;
		public FrameLayout rl_coins_reward;
		public TextView tv_hint;
		public com.star.ui.ImageView rewardIcon;
		public ImageView iv_shop;
		public TextView rewardName;
		public TextView rewardNameOject;
		public TextView rewardPrice;
	}
}
