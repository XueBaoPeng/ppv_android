package com.star.mobile.video.view;

import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.star.mobile.video.R;
import com.star.mobile.video.activity.AlertListActicity;
import com.star.mobile.video.adapter.MenuViewAdapter;
import com.star.mobile.video.chatroom.ChatRoomsFragment;
import com.star.mobile.video.discovery.DiscoveryFragment;
import com.star.mobile.video.fragment.ChannelGuideFragment;
import com.star.mobile.video.fragment.EpgDetailFragment;
import com.star.mobile.video.fragment.HomeFragment;
import com.star.mobile.video.fragment.OnAirFragment;
import com.star.mobile.video.fragment.VideoFragment;
import com.star.mobile.video.me.MeFragment;
import com.star.mobile.video.me.feedback.FeedbackActivity;
import com.star.mobile.video.model.FunctionType;
import com.star.mobile.video.model.MenuHandle;
import com.star.mobile.video.model.MenuItemRes;
import com.star.mobile.video.service.FunctionService;
import com.star.mobile.video.util.Constant;

public class MenuPage extends LinearLayout implements OnItemClickListener{

	private MenuViewAdapter meunAdapter;
	private List<MenuItemRes> barList;

	public MenuPage(Context context) {
		super(context);
//		LayoutInflater.from(context).inflate(R.layout.page_menu, this);
//		ListView lv_menu_list = (ListView) findViewById(R.id.lv_menu_list);
//		lv_menu_list.setOnItemClickListener(this);
//		
//		String[] bars = getResources().getStringArray(R.array.Bar_Menus);
//		barList = new ArrayList<MenuItemRes>();
//		MenuItemRes homeItemRes = new MenuItemRes(bars[0], R.drawable.menu_icon_home_fovus, R.drawable.menu_icon_home);
//		MenuItemRes videoItemRes = new MenuItemRes(bars[1], R.drawable.icon_bulling_video_focus, R.drawable.icon_bulling_video);
//		MenuItemRes onChnGuItemRes = new MenuItemRes(bars[2], R.drawable.menu_icon_channelguide_focus, R.drawable.menu_icon_channelguide);
//		MenuItemRes chatroomItemRes = new MenuItemRes(bars[3], R.drawable.menu_icon_chatroom_focus, R.drawable.menu_icon_chatroom);
//		MenuItemRes forumItemRes = new MenuItemRes(bars[4], R.drawable.icon_bulling_forum_focus, R.drawable.icon_bulling_forum);
//		MenuItemRes accountManager = new MenuItemRes(bars[5], R.drawable.icon_bulling_account_management_focus, R.drawable.icon_bulling_account_management);
//		MenuItemRes meItemRes = new MenuItemRes(bars[6], R.drawable.menu_icon_me_focus, R.drawable.menu_icon_me);
//		MenuItemRes alertItemRes = new MenuItemRes(bars[7], R.drawable.menu_icon_alert_focus, R.drawable.menu_icon_alert);
//		MenuItemRes feedbackItemRes = new MenuItemRes(bars[8], R.drawable.menu_icon_feedback_focus, R.drawable.menu_icon_feedback);
//		MenuItemRes setting = new MenuItemRes(bars[9], R.drawable.menu_icon_setting_focus, R.drawable.menu_icon_setting);
//		barList.add(homeItemRes);
//		barList.add(videoItemRes);
//		if(!FunctionService.doHideFuncation(FunctionType.SimpleVersion))
//			barList.add(onChnGuItemRes);
//		barList.add(chatroomItemRes);
//		barList.add(forumItemRes);
//		if(!FunctionService.doHideFuncation(FunctionType.SimpleVersion)){
//			barList.add(accountManager);
//			barList.add(meItemRes);
//			barList.add(alertItemRes);
//			barList.add(feedbackItemRes);
//		}
//		barList.add(setting);
//		
//		meunAdapter = new MenuViewAdapter(context,barList);
//		lv_menu_list.setAdapter(meunAdapter);
//		
////		setListViewHeight(lv_menu_list);
//		
//		setMenuBarWidth();
		
		setTagForFragment(context);
		
//		setTagForActivity(context);
//		
//		setLogoForFragment(context);
		
	}
	
	private void setListViewHeight(ListView listView) {
		if (meunAdapter == null) {
			return ;
		}
		int totalHeight = 0;
		int count = meunAdapter.getCount();
		for (int i = 0; i < count; i++) {
			View listItem = meunAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		if(totalHeight != 0){
			params.height = totalHeight;
			listView.setLayoutParams(params);
		}
	}

	private void setMenuBarWidth() {
		View itemView = LayoutInflater.from(getContext()).inflate(R.layout.view_menu_item_chngud, null);
		itemView.measure(0, 0);
		Constant.MENU_ITEM_WIDTH = itemView.getMeasuredWidth();
	}
	
	private void setTagForActivity(Context context) {
		//activity
		MenuHandle.setMenuItemClass(context.getString(R.string.activity_alertlist_title), AlertListActicity.class);
		MenuHandle.setMenuItemClass(context.getString(R.string.feedback), FeedbackActivity.class);
//		MenuHandle.setMenuItemClass(context.getString(R.string.setting), SettingActivity.class);
	}

	private void setLogoForFragment(Context context) {
		//logo
		Resources resource = context.getResources();
		MenuHandle.setFragmentLogo(resource.getString(R.string.fragment_tag_Home), resource.getDrawable(R.drawable.actionbar_home_logo));
		MenuHandle.setFragmentLogo(resource.getString(R.string.fragment_tag_video), resource.getDrawable(R.drawable.actionbar_button_video_white));
		MenuHandle.setFragmentLogo(resource.getString(R.string.fragment_tag_chatRooms), resource.getDrawable(R.drawable.icon_bulling_chat_rooms));
		MenuHandle.setFragmentLogo(resource.getString(R.string.fragment_tag_channelGuide), resource.getDrawable(R.drawable.icon_channel_guide));
		if(!FunctionService.doHideFuncation(FunctionType.SimpleVersion)){
			MenuHandle.setFragmentLogo(resource.getString(R.string.fragment_tag_Me), resource.getDrawable(R.drawable.actionbar_me_logo));
			MenuHandle.setFragmentLogo(resource.getString(R.string.fragment_tag_onAir), resource.getDrawable(R.drawable.icon_on_air));
			MenuHandle.setFragmentLogo(resource.getString(R.string.discobery_tag_fragment), resource.getDrawable(R.drawable.actionbar_icon_account_management));
		}
	}

	private void setTagForFragment(Context context) {
		//fragment
		MenuHandle.setMenuItemClass(context.getString(R.string.fragment_tag_Home), HomeFragment.class);
		MenuHandle.setMenuItemClass(context.getString(R.string.fragment_tag_video), VideoFragment.class);
		MenuHandle.setMenuItemClass(context.getString(R.string.fragment_tag_chatRooms), ChatRoomsFragment.class);
		MenuHandle.setMenuItemClass(context.getString(R.string.fragment_tag_channelGuide), ChannelGuideFragment.class);
		if(!FunctionService.doHideFuncation(FunctionType.SimpleVersion)){
			MenuHandle.setMenuItemClass(context.getString(R.string.discobery_tag_fragment), DiscoveryFragment.class);
			MenuHandle.setMenuItemClass(context.getString(R.string.fragment_tag_Me), MeFragment.class);
			MenuHandle.setMenuItemClass(context.getResources().getString(R.string.fragment_tag_onAir), OnAirFragment.class);
			MenuHandle.setMenuItemClass(context.getResources().getString(R.string.fragment_tag_epgDetail), EpgDetailFragment.class);
		}
		
		MenuHandle.setFragmentTag(HomeFragment.class.getName(), context.getString(R.string.fragment_tag_Home));
		MenuHandle.setFragmentTag(VideoFragment.class.getName(), context.getString(R.string.fragment_tag_video));
		MenuHandle.setFragmentTag(ChatRoomsFragment.class.getName(), context.getString(R.string.fragment_tag_chatRooms));
		MenuHandle.setFragmentTag(ChannelGuideFragment.class.getName(), context.getString(R.string.fragment_tag_channelGuide));
		if(!FunctionService.doHideFuncation(FunctionType.SimpleVersion)){
			MenuHandle.setFragmentTag(DiscoveryFragment.class.getName(), context.getString(R.string.discobery_tag_fragment));
			MenuHandle.setFragmentTag(MeFragment.class.getName(), context.getString(R.string.fragment_tag_Me));
			MenuHandle.setFragmentTag(OnAirFragment.class.getName(), context.getResources().getString(R.string.fragment_tag_onAir));
			MenuHandle.setFragmentTag(EpgDetailFragment.class.getName(), context.getResources().getString(R.string.fragment_tag_epgDetail));
		}
	}
	
	public void OnMenuBarSelect(String barTag) {
		int pos = -1;
		for(int i=0; i<barList.size(); i++){
			if(barList.get(i).getItemTitle().equals(barTag)){
				pos = i;
				break;
			}
		}
		if(meunAdapter!=null)
			meunAdapter.setOnClickPos(pos);
	}
	
	public void notifyMenubarStatusChange(){
		if(meunAdapter != null){
			meunAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		/*String itemname = barList.get(position).getItemTitle();
		if(itemname.equals(homeActivity.getCurrentFragmentTag())){
			homeActivity.closeSlidingMenu();
			return;
		}
		if(itemname.equals(getContext().getString(R.string.fragment_tag_channelGuide)) && homeActivity.getCurrentFragmentTag().equals(getContext().getString(R.string.activity_allchannels_title))){
			homeActivity.closeSlidingMenu();
			return;
		}
		
		
		if(itemname.equals(getResources().getString(R.string.fragment_tag_video))&&getResources().getString(R.string.tag_video_list).equals(homeActivity.getCurrentFragmentTag())){
			homeActivity.closeSlidingMenu();
			homeActivity.onBackPressed();
			return;
		}*/
		
		/*OnMenuBarSelect(itemname);
		
		if(itemname.equals(getResources().getString(R.string.fragment_tag_ccount_manager))) {
			if(SharedPreferencesUtil.getUserName(homeActivity) == null) {
				CommonUtil.pleaseLogin(homeActivity);
				return;
			}
		}
		if(itemname.equals(getResources().getString(R.string.activity_forum_title))){
			CommonUtil.skipBbs(homeActivity);
			closeMenuSlide();
			return;
		}
		
		Class<?> clazz = MenuHandle.getMenuItemClass(itemname);
		try {
			Object object = clazz.getConstructor().newInstance();
			if(object instanceof Activity){
				CommonUtil.startActivity(homeActivity, clazz);
				closeMenuSlide();
			}else if(object instanceof Fragment){
				homeActivity.setCurrentFragmentTag(itemname, false);
				homeActivity.setFragment((Fragment)object);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
	}

	private void closeMenuSlide() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
//				homeActivity.closeSlidingMenu();
			}
		}, 1000);
	}
	
}
