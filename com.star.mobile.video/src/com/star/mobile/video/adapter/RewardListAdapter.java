package com.star.mobile.video.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.star.cms.model.Award;
import com.star.cms.model.Resource;
import com.star.cms.model.vo.AwardVO;
import com.star.mobile.video.R;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.ui.ImageView;

public class RewardListAdapter extends BaseAdapter{

	private Context context;
	private List<AwardVO> awards;
	private OnClickListener listeren;
	
	public RewardListAdapter(Context context,List<AwardVO> datas) {
		this.context = context;
		this.awards = datas;
	}
	
	public void updateDataAndRefreshUI(List<AwardVO> datas) {
		this.awards = datas;
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
	
	public void setOnClick(OnClickListener listener) {
		this.listeren = listener;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder viewHolder;
		if(view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(context).inflate(R.layout.view_reward_item, null);
			viewHolder.rewardIcon = (ImageView) view.findViewById(R.id.iv_reward_item_icon);
			viewHolder.rewardName = (TextView) view.findViewById(R.id.tv_reward_name);
			viewHolder.rewardPrice = (TextView) view.findViewById(R.id.tv_reward_price);
			viewHolder.rewardFaceValue = (TextView) view.findViewById(R.id.tv_rw_price);
			viewHolder.rewardDescription = (TextView) view.findViewById(R.id.tv_description);
			viewHolder.rewardId = (TextView) view.findViewById(R.id.tv_award_id);
			viewHolder.rewardImageUrl = (TextView) view.findViewById(R.id.tv_imageurl);
			
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		AwardVO awardVo = awards.get(position);
		int awardType = awardVo.getType();
		viewHolder.rewardIcon.setImageResource(R.drawable.no_picture_basic);
		if(awardType == Award.Type_Wallet) {//优惠卷
			viewHolder.rewardFaceValue.setVisibility(View.VISIBLE);
			String symbol = SharedPreferencesUtil.getCurrencSymbol(context);
			viewHolder.rewardFaceValue.setText(symbol+(int)awardVo.getFaceValue()+"");
			int typeGet = awards.get(position).getTypeGet();
			if(typeGet == Award.FREE_COUPON) {
				viewHolder.rewardIcon.setImageResource(R.drawable.free_coupon_list);
			} else if(typeGet == Award.TYPE_COMMON) {
				viewHolder.rewardIcon.setImageResource(R.drawable.blue_coupon_list);
			} else if(typeGet == Award.TYPE_EXCHANGE) {
				viewHolder.rewardIcon.setImageResource(R.drawable.free_coupon_list);
			} else if(typeGet == Award.TYPE_NEW_CUSTOMER) {
				viewHolder.rewardIcon.setImageResource(R.drawable.yellow_coupon_list);
			}
			
		} else if(awardType == Award.Type_Object) { // 实物
			viewHolder.rewardFaceValue.setVisibility(View.GONE);
			List<Resource> res = awardVo.getPoster().getResources();
			if(res.size() != 0) {
				String listImageUrl = res.get(1).getUrl();
				String descriptionImageUrl = res.get(0).getUrl();
				if(listImageUrl != null) {
					viewHolder.rewardIcon.setUrl(listImageUrl);
				} else {
					viewHolder.rewardIcon.setImageResource(R.drawable.no_picture_basic);
				}
				if(descriptionImageUrl != null) {
					viewHolder.rewardImageUrl.setText(descriptionImageUrl);
				} else {
					viewHolder.rewardImageUrl.setText("");
				}
			}
		}
		viewHolder.rewardName.setText(awardVo.getName());
		viewHolder.rewardName.setTag(awardVo);
		viewHolder.rewardPrice.setText(awardVo.getPrice()+"");
		viewHolder.rewardId.setText(awardVo.getId()+"");
		viewHolder.rewardDescription.setText(awardVo.getDescription());
		
		view.setOnClickListener(listeren);
		return view;
	}

	
	class ViewHolder {
		public ImageView rewardIcon;
		public TextView rewardName;
		public TextView rewardPrice;
		public TextView rewardDescription;
		public TextView rewardId;
		public TextView rewardImageUrl;
		public TextView rewardFaceValue;//优惠面值
	}
}
