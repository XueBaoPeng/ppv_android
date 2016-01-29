package com.star.mobile.video.view;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;

import com.star.mobile.video.R;
import com.star.mobile.video.adapter.ViewPagerAdapter;
import com.star.mobile.video.util.ExpressionUtil;

public class FaceContainer extends LinearLayout {
	
	private int pageSize = 6;
	private int pageCount = 20;
	private int currentPage = 0;
	private int totalCount = 113;
	private int[] imageIds = new int[totalCount];
	private ImageView[] dots = new ImageView[pageSize];
	private LinearLayout dotLayout;
	private ViewPager faceTable;
	private Context mContext;
	
	public FaceContainer(Context context) {
		this(context, null);
	}
	
	public FaceContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        LayoutInflater.from(context).inflate(R.layout.view_chatface_layout, this);
		faceTable = (ViewPager) findViewById(R.id.vp_face_table);
		dotLayout = (LinearLayout) findViewById(R.id.ll_pager_group);
        initFacePage();
    }
	
	private void initFacePage() {
		List<View> lv = new ArrayList<View>();
		ImageView imageView;
		for (int i = 0; i < pageSize; ++i){
			lv.add(createGridView(i));
			imageView = new ImageView(mContext);
			imageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			dots[i] = imageView;
			if (i == 0) {
				dots[i].setBackgroundResource(R.drawable.circle_dot_chatroom_choose);
			} else {
				dots[i].setBackgroundResource(R.drawable.circle_dot_chatroom);
			}
			dotLayout.addView(dots[i]);
		}
		ViewPagerAdapter adapter = new ViewPagerAdapter(mContext, lv);
		faceTable.setAdapter(adapter);
		faceTable.setCurrentItem(currentPage);
		faceTable.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				currentPage = arg0;
				for (int i = 0; i < pageSize; i++) {
					if (i == arg0) {
						dots[i].setBackgroundResource(R.drawable.circle_dot_chatroom_choose);
					} else {
						dots[i].setBackgroundResource(R.drawable.circle_dot_chatroom);
					}
				}
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}
	
	private View createGridView(int index) {
		GridView gv = new GridView(mContext);
		gv.setNumColumns(7);
		gv.setSelector(new ColorDrawable(Color.TRANSPARENT));// 屏蔽GridView默认点击效果
		gv.setBackgroundColor(Color.TRANSPARENT);
		gv.setCacheColorHint(Color.TRANSPARENT);
		gv.setVerticalSpacing(30);
		gv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		gv.setGravity(Gravity.CENTER);
		List<Map<String,Object>> listItems = new ArrayList<Map<String,Object>>();
		for(int i=index*pageCount; i<(index+1)*pageCount && i<107; i++){
			try {
				if(i<10){
					Field field = R.drawable.class.getDeclaredField("f00" + i);
					int resourceId = Integer.parseInt(field.get(null).toString());
					imageIds[i] = resourceId;
				}else if(i<100){
					Field field = R.drawable.class.getDeclaredField("f0" + i);
					int resourceId = Integer.parseInt(field.get(null).toString());
					imageIds[i] = resourceId;
				}else{
					Field field = R.drawable.class.getDeclaredField("f" + i);
					int resourceId = Integer.parseInt(field.get(null).toString());
					imageIds[i] = resourceId;
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
	        Map<String,Object> listItem = new HashMap<String,Object>();
			listItem.put("image", imageIds[i]);
			listItems.add(listItem);
		}
		if((index+1)*pageCount<totalCount-1){
			imageIds[(index+1)*pageCount+1] = R.drawable.icon_face_delete;
			Map<String,Object> listItem = new HashMap<String,Object>();
			listItem.put("image", imageIds[(index+1)*pageCount+1]);
			listItems.add(listItem);
		}else{
			imageIds[totalCount-1] = R.drawable.icon_face_delete;
			Map<String,Object> listItem = new HashMap<String,Object>();
			listItem.put("image", imageIds[totalCount-1]);
			listItems.add(listItem);
		}
		SimpleAdapter simpleAdapter = new SimpleAdapter(mContext, listItems, R.layout.view_chat_face, new String[]{"image"}, new int[]{R.id.iv_face_icon});
		gv.setOnItemClickListener(onItemClickListener);
		gv.setAdapter(simpleAdapter);
		return gv;
	}
	
	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if(position == pageCount || (currentPage==5 && position==totalCount-1-currentPage*(pageCount+1))){
				int selection = etChat.getSelectionStart();
				String text = etChat.getText().toString();
				if (selection > 0) {
					if(selection < 4){
						etChat.getText().delete(selection - 1, selection);
					}else{
						String text2 = text.substring(selection - 4, selection);
						String expressStr = ExpressionUtil.getExpressionString(mContext, text2, "f0[0-9]{2}|f10[0-7]");
						if(expressStr!=null)
							etChat.getText().delete(selection - 4, selection);
						else
							etChat.getText().delete(selection - 1, selection);
					}
				}
				return;
			}
			position = currentPage*(pageCount+1)+position-currentPage;
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageIds[position % imageIds.length]);
			Matrix matrix = new Matrix();
			matrix.postScale(0.8f, 0.8f);
			Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0,
					bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			ImageSpan imageSpan = new ImageSpan(mContext, newBitmap);
			String str = null;
			if(position<10){
				str = "f00"+position;
			}else if(position<100){
				str = "f0"+position;
			}else{
				str = "f"+position;
			}
			SpannableString spannableString = new SpannableString(str);
			spannableString.setSpan(imageSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			etChat.append(spannableString);
		}
	};
	private EditText etChat;

	public void setEditText(EditText edittext){
		this.etChat = edittext;
	}

}
