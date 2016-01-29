package com.star.mobile.video.search;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.star.cms.model.vo.SearchVO;
import com.star.mobile.video.R;

public class SearchHeadView extends LinearLayout  {

	private TextView textView;
	private TextView tv_searchContent;
	
	public SearchHeadView(Context context) {
		this(context, null);
	}
	
	public SearchHeadView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	private void initView() {
		LayoutInflater.from(getContext()).inflate(R.layout.fragment_search_more_head, this);
		textView = (TextView) findViewById(R.id.check_more_type);
		tv_searchContent = (TextView) findViewById(R.id.tv_serachcontent);
	}
	
	public void setData(int searchType,String keys){
		tv_searchContent.setText(keys);
		if(searchType == SearchVO.CHANNEL_TYPE){
			textView.setText("CHANNEL");
		}else if(searchType == SearchVO.PROGRAM_TYPE){
			textView.setText("PROGRAM");
		}else if(searchType == SearchVO.VOD_TYPE){
			textView.setText("VIDEO");
		}else if(searchType == SearchVO.CHATROOM_TYPE){
			textView.setText("CHATROOM");
		}
	}
	
}
