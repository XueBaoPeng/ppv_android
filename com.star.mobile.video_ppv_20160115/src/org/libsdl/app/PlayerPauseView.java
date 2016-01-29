package org.libsdl.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.star.cms.model.Recommend;
import com.star.cms.model.Resource;
import com.star.cms.model.enm.Type;
import com.star.mobile.video.R;
import com.star.mobile.video.service.RecommendService;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.LoadingDataTask;

public class PlayerPauseView {

	private Player mPlayer;
	View popupView;
	private com.star.ui.ImageView pause_image;
	private ImageView cancel_image;
	
	private RecommendService remService;
	private List<Recommend> recommends = new ArrayList<Recommend>();
	private int recommendIndex;
	
	public PlayerPauseView(Player p){
		mPlayer = p;
	}
	void initPauseView() {
		new LoadingDataTask() {
			List<Recommend> rs;
			@Override
			public void onPreExecute() {
				remService = new RecommendService();
			}

			@Override
			public void onPostExecute() {
				if(rs==null || rs.size()==0)
					return;
				
				recommends.clear();
				for(int i=0;i<rs.size();i++)
				{
					if( rs.get(i).getType() == Type.Advertisement) 
						recommends.add(rs.get(i));
				}
			}

			@Override
			public void doInBackground() {
				rs = remService.getRecommends(true, mPlayer);
			}
		}.execute();
		
		popupView = mPlayer.getLayoutInflater().inflate(R.layout.view_player_pause, null);
		
		popupView.setVisibility(View.INVISIBLE);
		pause_image = (com.star.ui.ImageView) popupView.findViewById(R.id.player_pause_popup_image);
		cancel_image = (ImageView) popupView.findViewById(R.id.player_pause_popup_cancel);

		cancel_image.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				popupView.setVisibility(View.INVISIBLE);
			}
		});
	}

	private Drawable getRandomVideoAd() {
		int[] noByArea = {0,0,6,11,6,6,6,8};
		int areaId = (int)SharedPreferencesUtil.getAreaId(mPlayer);
		if(areaId >= noByArea.length) return null;
		Resources resources = mPlayer.getResources();
		StringBuffer sb = new StringBuffer("video_");
		sb.append(areaId);
		Random random = new Random();
		sb.append(random.nextInt(noByArea[areaId]));
		int resourceId = resources.getIdentifier(sb.toString(), "drawable",  mPlayer.getPackageName());

		return resources.getDrawable(resourceId);
	}
	void showPauseView() {
		if (mPlayer.isPortraitOrientation)
			return;
		try {
			Drawable d = getRandomVideoAd();
			Random random = new Random();
			boolean isAd = random.nextBoolean();
			if(( d != null && isAd ) || mPlayer.isDejia != null){
				pause_image.setImageDrawable(d);
				pause_image.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dismissPauseView();
					}
				});
			}else{
				pause_image.setImageResource(R.drawable.bundesliga_recommend);
			
//				if (recommends != null && recommends.size() > 0) {
//					recommendIndex = random.nextInt(recommends.size());
//					Recommend recommend = recommends.get(recommendIndex);
//					if (recommend != null) {
//						List<Resource> posters = recommend.getPosters();
//						if (posters != null && posters.size() > 0) {
//							Resource resource = posters.get(0);
//							if(resource != null){
//								pause_image.setUrl(resource.getUrl());
//								pause_image.setOnClickListener(new OnClickListener() {
//									@Override
//									public void onClick(View v) {
//				//						String url = "http://218.205.169.218:8581/bundesliga/bundesliga.php?areaId="
//				//						String url = "http://tenbre.me/bund/bundesliga.php?areaId="
//				//								+ SharedPreferencesUtil.getAreaId(Player.this);
//										String url = recommends.get(recommendIndex).getUrl();
//										CommonUtil.goActivityOrFargment(mPlayer, CommonUtil.ADVERTISEMENT, url, null, url, "");
//									}
//								});
//							}
//						}
//					}
//				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			pause_image.setImageResource(R.drawable.bundesliga_recommend);
		}
		popupView.setVisibility(View.VISIBLE);

	}
	
	void dismissPauseView() {
		if (popupView.getVisibility() == View.VISIBLE)
			popupView.setVisibility(View.INVISIBLE);
	}

}
