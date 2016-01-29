package com.star.mobile.video.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.star.cms.model.Content;
import com.star.cms.model.Resource;
import com.star.cms.model.vo.VOD;
import com.star.mobile.video.R;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.DensityUtil;

/**
 * channel guide视频可以滑动
 * 
 * @author Lee
 *
 */
public class VideoHorizontalListViewAdapter extends BaseAdapter {

	private List<VOD> mChannelVideoItemDomain;
	private LayoutInflater mLayoutInflater;
	private int itemWidth;

	public VideoHorizontalListViewAdapter(Context context, List<VOD> channelVideoItemDomain) {
		mLayoutInflater = LayoutInflater.from(context);
		this.itemWidth = (Constant.WINDOW_WIDTH - 3 * DensityUtil.dip2px(context, 6)
				- 2 * DensityUtil.dip2px(context, 10)) / 2;
	}

	public void setmChannelVideoItemDomain(List<VOD> mChannelVideoItemDomain) {
		this.mChannelVideoItemDomain = mChannelVideoItemDomain;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (mChannelVideoItemDomain == null) {
			return 0;
		}
		return mChannelVideoItemDomain.size();
	}

	@Override
	public Object getItem(int position) {
		return mChannelVideoItemDomain.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		View view = null;

		if (convertView == null) {
			holder = new ViewHolder();
			view = mLayoutInflater.inflate(R.layout.item_channel_guide_video_horizontal_listview, parent, false);
			holder.mVideoImageView = (com.star.ui.ImageView) view.findViewById(R.id.iv_video_imageview);
			holder.mWatchCountTextView = (TextView) view.findViewById(R.id.textview_watch_count);
			holder.mViewControlRL = (RelativeLayout) view.findViewById(R.id.show_control_rl);
			holder.mVideoImageView.setLayoutParams(new RelativeLayout.LayoutParams(itemWidth, itemWidth * 9 / 16));
			RelativeLayout.LayoutParams aprams = new RelativeLayout.LayoutParams(itemWidth,
					android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT);
			aprams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			holder.mViewControlRL.setLayoutParams(aprams);
			holder.vBlank = view.findViewById(R.id.v_blank);
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}
		if (position == 0) {
			holder.vBlank.setVisibility(View.GONE);
		} else {
			holder.vBlank.setVisibility(View.VISIBLE);
		}
		if (mChannelVideoItemDomain != null) {
			Content poster = mChannelVideoItemDomain.get(position).getPoster();
			List<Resource> resposter = poster.getResources();
			String url = resposter.get(0).getUrl();
			holder.mVideoImageView.setUrl(url);

			Content videoContent = mChannelVideoItemDomain.get(position).getVideo();
			Long watchCount = videoContent.getSelCount();
			holder.mWatchCountTextView.setText(watchCount==null?"0":String.valueOf(watchCount));
		}

		return view;
	}

	private class ViewHolder {
		private com.star.ui.ImageView mVideoImageView;
		private TextView mWatchCountTextView;
		private View vBlank;
		private RelativeLayout mViewControlRL;
	}

}
