package com.star.mobile.video.view;

import java.io.Serializable;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.star.cms.model.SmartCardInfo;
import com.star.cms.model.vo.SmartCardInfoVO;
import com.star.mobile.video.R;
import com.star.mobile.video.dialog.DifferentTextColorConfirmDialog;
import com.star.mobile.video.service.UserService;
import com.star.mobile.video.smartcard.SmartCardActivity;
import com.star.mobile.video.smartcard.SmartCardControlActivity;
import com.star.mobile.video.smartcard.SmartCardService;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.DefaultLoadingTask;
import com.star.mobile.video.util.ToastUtil;
import com.star.util.loader.OnListResultListener;
import com.star.util.loader.OnResultListener;

public class SmartCardListView extends LinearLayout implements OnClickListener{

	private TextView tvTitle;
	private TextView tvOperation;
	private LinearLayout llOperation;
	private LinearLayout lvSmartCardlist;
	private LoadingProgressBar lpbLoadingSmartCard;
	private UserService userService;
	private List<SmartCardInfoVO> smartinfos;
	private Context context;
	private ImageView ivLoadingLine;
	private ImageView ivLoadingIcon;
	private boolean shownAddView;
	private boolean goTopup;
	private SmartCardService smartcardService;
	
	public SmartCardListView(Context context) {
		this(context, null);
	}
	
	public SmartCardListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		LayoutInflater.from(context).inflate(R.layout.view_smart_card_list, this);
		userService = new UserService();
		smartcardService = new SmartCardService(context);
		initView();
	}
	
	private void initView() {
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvOperation = (TextView) findViewById(R.id.tv_operation);
		llOperation = (LinearLayout) findViewById(R.id.ll_edit);
		lvSmartCardlist = (LinearLayout) findViewById(R.id.lv_smart_card_list);
		lpbLoadingSmartCard = (LoadingProgressBar) findViewById(R.id.lp_smartcard_loading);
		ivLoadingLine = (ImageView) findViewById(R.id.iv_loading_line);
		ivLoadingIcon = (ImageView) findViewById(R.id.iv_smart_loading_icon);
		
		addSmartcardView = (AddSmardCartView) findViewById(R.id.view_account_add);
		llOperation.setOnClickListener(this);
		ivLoadingIcon.setOnClickListener(this);
		
		lvSmartCardlist.setFocusable(true);
		lvSmartCardlist.setFocusableInTouchMode(true);
	}
	
	public void setTitle(String text) {
		tvTitle.setText(text);
	}
	
	public void setGoTopupActivity(boolean isGo){
		this.goTopup = isGo;
	}
	
	public void isShownAddCardView(boolean shownCardView){
		this.shownAddView = shownCardView;
	}
	
	public void getSmartCrad() {
		smartcardService.getAllSmartCardInfos(new OnListResultListener<SmartCardInfoVO>() {
			
			@Override
			public boolean onIntercept() {
				lvSmartCardlist.removeAllViews();
				addSmartcardView.setVisibility(View.GONE);
				lpbLoadingSmartCard.setVisibility(View.VISIBLE);
				return false;
			}
			
			@Override
			public void onFailure(int errorCode, String msg) {
				
			}
			
			@Override
			public void onSuccess(List<SmartCardInfoVO> value) {
				initSmartCard();
			}
		});
	}
	
	private void initSmartCard() {
		tvOperation.setText(context.getString(R.string.edit));
		lpbLoadingSmartCard.setVisibility(View.GONE);
		lvSmartCardlist.removeAllViews();
		if(smartinfos != null && smartinfos.size() > 0) {
			findViewById(R.id.ll_edit).setVisibility(View.VISIBLE);
			addSmartcardView.setVisibility(View.GONE);
			CardItemClick clickListener = new CardItemClick();
			for(SmartCardInfoVO vo : smartinfos){
				SmartCardItemView view = new SmartCardItemView(getContext());
				view.getDetailSmartCardInfo(vo);
				view.setOnClickListener(clickListener);
				view.setOptionBtnClick(SmartCardListView.this);
				view.setOptionBtnTag(vo);
				view.setTag(vo);
				lvSmartCardlist.addView(view);
				lvSmartCardlist.requestFocus();
			}
			if(goTopup){
				Intent i = new Intent();
				i.putExtra("smartinfos", (Serializable) smartinfos); 
				CommonUtil.startActivity((Activity)context, SmartCardControlActivity.class);
				goTopup = false;
			}
		} else {
			showAddSmartcardViewNoScroll();
		}
		if(smartinfos == null){
			ToastUtil.centerShowToast(context, getResources().getString(R.string.fail_to_get_the_smart_card_information));
		}
	}
	
	private void delUserSmardCartNoClick(final SmartCardInfo sc) {
		final DifferentTextColorConfirmDialog dialog = new DifferentTextColorConfirmDialog(context, false);
		dialog.show();
		dialog.setOneMessage(getResources().getString(R.string.no)+sc.getSmardCardNo());
		dialog.setOneMessageTextColor(getResources().getColor(R.color.orange));
		dialog.setTwoMessage(getResources().getString(R.string.delete_the_martcard));
		dialog.setLeftButtonText(getResources().getString(R.string.confirm));
		dialog.setRightButtonText(getResources().getString(R.string.cancel));
		dialog.setLeftButtonOnClick(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				delUserSmardCartNo(sc.getId());
			}
		});
		dialog.setRigthButtonOnClick(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				showOrHideOption();
			}
		});
	}
	
	private boolean editSmardCartStatus;
	private AddSmardCartView addSmartcardView;
	private void showOrHideOption() {
		if(!editSmardCartStatus) {
			showAddSmartcardView();
			int count = lvSmartCardlist.getChildCount();
			for(int i= 0;i < count;i++) {
				if(lvSmartCardlist.getChildAt(i) instanceof SmartCardItemView) {
					SmartCardItemView view = (SmartCardItemView)lvSmartCardlist.getChildAt(i);
					view.showOptionBtn();
				}
			}
			tvOperation.setText(context.getString(R.string.cancel));
			editSmardCartStatus = true;
		} else {
			hideAddSmartcardView();
			int count = lvSmartCardlist.getChildCount();
			for(int i= 0;i < count;i++) {
				if(lvSmartCardlist.getChildAt(i) instanceof SmartCardItemView) {
					SmartCardItemView view = (SmartCardItemView)lvSmartCardlist.getChildAt(i);
					view.hideOptionBtn();
				}
			}
			tvOperation.setText(context.getString(R.string.edit));
			editSmardCartStatus = false;
		}
	}
	
	private void showAddSmartcardViewNoScroll() {
		if(!shownAddView)
			return;
		findViewById(R.id.ll_edit).setVisibility(View.GONE);
		addSmartcardView.setVisibility(View.VISIBLE);
		addSmartcardView.setAddCartBtnClick(this);
		addSmartcardView.setIconAddCardRes(R.string.recharge_smartcard); 
	}

	private void showAddSmartcardView() {
		addSmartcardView.setVisibility(View.VISIBLE);
		addSmartcardView.showOptionBtn();
		addSmartcardView.setAddCartBtnClick(this);
		addSmartcardView.setIconAddCartBtnClick(this);
		addSmartcardView.setIconAddCardRes(R.string.add_smartcard); 
	}
	
	private void hideAddSmartcardView() {
		addSmartcardView.setVisibility(View.GONE);
		addSmartcardView.hideOptionBtn();
		addSmartcardView.setAddCartBtnClick(null);
		addSmartcardView.setIconAddCartBtnClick(null);
	}
	
	private void delUserSmardCartNo(final Long smartCardId) {
		smartcardService.delSmartCard(smartCardId, new OnResultListener<Boolean>() {

			@Override
			public boolean onIntercept() {
				CommonUtil.showProgressDialog(context, null, getResources().getString(R.string.loading));
				return false;
			}

			@Override
			public void onSuccess(Boolean value) {
				CommonUtil.closeProgressDialog();
				// 成功
				//成功
				userService.delCachedAllSmartCardNo();
				getSmartCrad();
				userService.saveExpectedStopSmartcard(getContext());
			}

			@Override
			public void onFailure(int errorCode, String msg) {
				CommonUtil.closeProgressDialog();
				// 失败
				ToastUtil.centerShowToast(context, getResources().getString(R.string.fail_to_delete_this_smart_card));
			}
		});
	}

	class CardItemClick implements OnClickListener{

		@Override
		public void onClick(View v) {
			Intent i = new Intent();
			SmartCardInfo sc = (SmartCardInfo) v.getTag();
			i.putExtra("card_id", sc.getSmardCardNo());
			i.putExtra("smartinfos", (Serializable) smartinfos); 
			i.setClass(getContext(), SmartCardControlActivity.class);
			CommonUtil.startActivity((Activity)context, i);
		}
	}
	
	private void goSmartCardActivity() {
		Intent it = new Intent();
		it.setClass(context, SmartCardActivity.class);
		CommonUtil.startActivity((Activity)context, it);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_account_add:
			goSmartCardActivity();
			break;
		case R.id.iv_add_card_btn:
		case R.id.view_account_add:
			showOrHideOption();
			goSmartCardActivity();
			break;
		case R.id.iv_option_btn:
			SmartCardInfo smardCartNo = (SmartCardInfo)v.getTag();
			delUserSmardCartNoClick(smardCartNo);
			break;
		case R.id.ll_edit:
			showOrHideOption();
			break;
		case R.id.iv_smart_loading_icon:
			ivLoadingIcon.setAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_rotate));
			getSmartCrad();
			break;
		default:
			break;
		}
	}
	
//	class CardNumAdapter extends BaseAdapter{
//
//		private List<SmartCardInfoVO> datas;
//		private Context context;
//		
//		public CardNumAdapter(Context context,List<SmartCardInfoVO> data) {
//			this.datas = data;
//			this.context = context;
//		}
//		
//		public void updataDataRefreshUi(List<SmartCardInfoVO> data) {
//			this.datas = data;
//			notifyDataSetChanged();
//		}
//		
//		@Override
//		public int getCount() {
//			
//			return datas.size();
//		}
//
//		@Override
//		public Object getItem(int position) {
//			
//			return datas.get(position);
//		}
//
//		@Override
//		public long getItemId(int position) {
//			
//			return position;
//		}
//
//		@Override
//		public View getView(int position, View view, ViewGroup parent) {
//			SmartCardItemView scView;
//			if(view == null) {
//				scView = new SmartCardItemView(context);
//				scView.setOptionBtnClick(SmartCardListView.this);
//				scView.setOnClickListener(new CardItemClick());
//			} else {
//				scView = (SmartCardItemView) view;
//			}
//			SmartCardInfoVO sc = datas.get(position);
//			scView.getDetailSmartCardInfo(sc);
//			return scView;
//		}
//	}
}