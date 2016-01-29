package com.star.mobile.video.liveandvideo;


import java.util.List;

import com.star.cms.model.Content;
import com.star.cms.model.Resource;
import com.star.cms.model.vo.ChannelVO;
import com.star.mobile.video.R;
import com.star.mobile.video.util.BitmapUtil;
import com.star.mobile.video.util.DensityUtil;
import com.star.mobile.video.widget.FancyCoverFlowAdapter;
import com.star.ui.ImageView.Finisher;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
/**
 * 频道logo展示Adapter
 * @author Lee
 * @version 1.0
 * @date 2015/10/25
 *
 */
public class TitleCoverFlowAdapter extends FancyCoverFlowAdapter{
	private LayoutInflater mInflater;
	private int mSelectPosition = -1;
	private List<ChannelVO> mTotalChannels;
	public TitleCoverFlowAdapter(Context context, List<ChannelVO> totalChannels) {
		mInflater = LayoutInflater.from(context);
		this.mTotalChannels = totalChannels;
	}

	public void setSelectPosition(int selectPosition) {
		this.mSelectPosition = selectPosition;
		notifyDataSetChanged();
	}

	public void setCoverFlowData(List<ChannelVO> totalChannels){
		this.mTotalChannels = totalChannels;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return mTotalChannels.size();
	}

	@Override
	public Object getItem(int position) {
		return mTotalChannels.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getCoverFlowItem(int position, View reusableView, final ViewGroup parent) {
		View view;
		final ViewHolder holder;
		if (reusableView != null) {
			view =  reusableView;
			holder = (ViewHolder) view.getTag();
			holder.mImageView.setImageResource(R.drawable.channel_detail_bg);
		} else {
			holder = new ViewHolder();
			view = mInflater.inflate(R.layout.cover_flow_item, parent,false);
			holder.mImageView = (com.star.ui.ImageView) view.findViewById(R.id.cover_flow_imageview);
			holder.mCricleImagebg = (ImageView) view.findViewById(R.id.cover_flow_iv_cricle_bg);
			view.setTag(holder);
		}
		
		if (mTotalChannels != null && mTotalChannels.size() > 0) {
			// 获得图片的url
			ChannelVO channelVO = mTotalChannels.get(position);
			if (channelVO != null) {
				Content logo = channelVO.getLogo();
				if (logo != null) {
					List<Resource> resources = logo.getResources();
					if (resources != null && resources.size() > 0) {
						String url = resources.get(0).getUrl();
						if (url != null) {
//							holder.mImageView.setFinisher(new Finisher() {
//								
//								@Override
//								public void run() {
//									//4dp的圆角
//									Bitmap bitmap = BitmapUtil.getRoundedCornerBitmap(holder.mImageView.getImage(),DensityUtil.dip2px(parent.getContext(), 4));
//									holder.mImageView.setImageBitmap(bitmap);
//								}
//							});
							holder.mImageView.setUrl(url);
						}
					}
				}
			}
		}
		
		if (mSelectPosition == position) {
			holder.mCricleImagebg.setVisibility(View.VISIBLE);
		} else {
			holder.mCricleImagebg.setVisibility(View.GONE);
		}
		return view;
	}

	class ViewHolder{
		com.star.ui.ImageView mImageView;
		ImageView mCricleImagebg;
	}
	/**
	 * 隐藏coverflow圆角图片
	 */
	public void hideCoverFlowCricleImage(){
		setSelectPosition(-1);
	}
	
}

