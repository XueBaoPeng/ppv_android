package com.star.mobile.video.liveandvideo;

import java.util.List;

import android.content.Context;

import com.star.cms.model.Content;
import com.star.cms.model.Resource;
import com.star.cms.model.vo.ChannelVO;
import com.star.mobile.video.R;
import com.star.mobile.video.util.adapterutils.CommonAdapter;
import com.star.mobile.video.util.adapterutils.ViewHolder;
/**
 * 
 * @author Lee
 * @version 1.0
 * @date 2015/10/29
 *
 */
public class FavoriteCollectAdapter extends CommonAdapter<ChannelVO>{

	public FavoriteCollectAdapter(Context context, List<ChannelVO> totalChannels, int layoutId) {
		super(context, totalChannels, layoutId);
	}

	
//	@Override
//	public void convert(ViewHolder holder, ChannelVO channelVO) {
//		// TODO Auto-generated method stub
//		if(channelVO!=null){
//			
//			Content logo=channelVO.getLogo();
//			if(logo!=null){
//				List<Resource> resources=logo.getResources();
//				if(resources!=null&& resources.size()>0){
//					String url=resources.get(0).getUrl();
//					if(url!=null){
//						holder.setImageView(R.id.favorite_collect_imageview, url);
//					}
//				}
//			}
//		}
//		
//	}

	public void convert(ViewHolder holder,ChannelVO channelVo){
		if(channelVo!=null){
			Content logo=channelVo.getLogo();
			if(logo!=null){
				List<Resource>resources=logo.getResources();
				if(resources!=null&&resources.size()>0){
					String url=resources.get(0).getUrl();
					if(url!=null){
//						holder.setFrescoImageView(R.id.favorite_collect_imageview, url);
						holder.setImageView(R.id.favorite_collect_imageview, url);
					}
				}
			}
		}
	}
	
	/*
	@Override
	public void convert(ViewHolder holder, ChannelVO channelVO) {
		// 获得图片的url
		if (channelVO != null) {
			Content logo = channelVO.getLogo();
			if (logo != null) {
				List<Resource> resources = logo.getResources();
				if (resources != null && resources.size() > 0) {
					String url = resources.get(0).getUrl();
					if (url != null) {
						holder.setImageView(R.id.favorite_collect_imageview, url);
					}
				}
			}
		}
	}
*/
}
