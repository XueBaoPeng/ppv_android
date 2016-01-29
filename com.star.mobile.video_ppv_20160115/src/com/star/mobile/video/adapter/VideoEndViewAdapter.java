package com.star.mobile.video.adapter;

import java.util.List;

import com.star.cms.model.Content;
import com.star.cms.model.Resource;
import com.star.cms.model.vo.VOD;
import com.star.mobile.video.R;
import com.star.ui.ImageView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 视频结束时的gridview的适配器
 * 
 * @author Lee
 * @version 1.0 2015.08.07
 *
 */
public class VideoEndViewAdapter extends BaseAdapter {
	private List<VOD> mRecommentsVideo;// 推荐视频
	private LayoutInflater mInflater;

	public VideoEndViewAdapter(Context context, List<VOD> mRecommentsVideo) {
		mInflater = LayoutInflater.from(context);
		this.mRecommentsVideo = mRecommentsVideo;
	}

	public void setRecommentVideo(List<VOD> mRecommentsVideo){
		this.mRecommentsVideo = mRecommentsVideo;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		if (mRecommentsVideo == null) {
			return 0;
		}
		return mRecommentsVideo.size();
	}

	@Override
	public Object getItem(int position) {
		return mRecommentsVideo.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		View view;
		if (convertView == null) {
			holder = new ViewHolder();
			view = mInflater.inflate(R.layout.view_video_end_prompt_item, parent, false);
			holder.mVideoEndPromptImageView = (com.star.ui.ImageView) view.findViewById(R.id.iv_video_end_recommend);
			holder.mVieoEndPromptName = (TextView) view.findViewById(R.id.tv_video_end_name);
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}

		if (mRecommentsVideo != null) {
			// 获得图片的url
			Content poster = mRecommentsVideo.get(position).getPoster();
			List<Resource> resources = poster.getResources();
			String url = resources.get(0).getUrl();
			holder.mVideoEndPromptImageView.setUrl(url);
			// 获得 视频的名字
			String name = mRecommentsVideo.get(position).getName();
			holder.mVieoEndPromptName.setText(name);
		}

		return view;
	}

	private class ViewHolder {
		com.star.ui.ImageView mVideoEndPromptImageView;
		TextView mVieoEndPromptName;
	}

}
