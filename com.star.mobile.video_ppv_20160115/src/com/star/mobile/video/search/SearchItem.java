package com.star.mobile.video.search;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.star.cms.model.Channel;
import com.star.cms.model.ChatRoom;
import com.star.cms.model.Program;
import com.star.cms.model.vo.VOD;
import com.star.mobile.video.R;
import com.star.mobile.video.util.DensityUtil;
import com.star.mobile.video.view.NoScrollListView;

public class SearchItem<T> extends LinearLayout {

	public SearchItem(Context context, List<T> searchData, String searchKey, OnClickListener checkMoreClickListener, boolean hasMore, boolean isBottom) {
		super(context);
		init(searchData, searchKey, checkMoreClickListener, hasMore, isBottom);
	}

	private void init(List<T> data, String searchKey, OnClickListener checkMoreClickListener, boolean hasMore, boolean isBottom) {
		if (data == null || data.size() == 0)
			return;
		LayoutInflater.from(getContext()).inflate(R.layout.view_search_onelevel_item, this);
		TextView tvSearchType = (TextView) findViewById(R.id.tv_search_type);
		NoScrollListView lvSearchGroup = (NoScrollListView) findViewById(R.id.lv_search_group);
		View tvCheckMore = findViewById(R.id.tv_check_more);
		View bottomLine = findViewById(R.id.iv_bottom_line);
		bottomLine.setVisibility(View.VISIBLE);
		T t = data.get(0);
		if (t instanceof Channel) {
			tvSearchType.setText("CHANNEL");
		} else if (t instanceof Program) {
			tvSearchType.setText("PROGRAM");
		} else if (t instanceof VOD) {
			tvSearchType.setText("VIDEO");
		} else if (t instanceof ChatRoom) {
			tvSearchType.setText("CHATROOM");
		}
		if(hasMore){
			tvCheckMore.setVisibility(View.VISIBLE);
		}else{
			tvCheckMore.setVisibility(View.GONE);
		}
		if(isBottom){
			bottomLine.setBackgroundColor(getResources().getColor(R.color.grey));
			LinearLayout.LayoutParams params = (LayoutParams) bottomLine.getLayoutParams();
			params.height = DensityUtil.dip2px(getContext(), 1);
			bottomLine.setLayoutParams(params);
		}
		if(data.size()>2){
			data.remove(data.size()-1);
		}
		lvSearchGroup.setAdapter(new SearchMoreAdapter<T>(getContext(), data, searchKey));
		tvCheckMore.setOnClickListener(checkMoreClickListener);
	}

}
