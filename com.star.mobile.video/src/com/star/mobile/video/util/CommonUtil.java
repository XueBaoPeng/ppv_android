package com.star.mobile.video.util;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.support.v4.app.Fragment;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.star.cms.model.Area;
import com.star.cms.model.Award;
import com.star.cms.model.dto.RechargeResult;
import com.star.cms.model.enm.Type;
import com.star.cms.model.vo.ChannelVO;
import com.star.cms.model.vo.ExchangeVO;
import com.star.mobile.video.R;
import com.star.mobile.video.account.ChooseAreaActivity;
import com.star.mobile.video.activity.AccountConnectActivity;
import com.star.mobile.video.activity.BrowserActivity;
import com.star.mobile.video.activity.InvitationActivity;
import com.star.mobile.video.base.BaseFragmentActivity;
import com.star.mobile.video.dialog.PromptDialog;
import com.star.mobile.video.epg.EpgDetailActivity;
import com.star.mobile.video.fragment.AccountManagerFragment;
import com.star.mobile.video.model.MenuHandle;
import com.star.mobile.video.service.EggService;
import com.star.mobile.video.shared.SharedPreferencesUtil;

public class CommonUtil {

	protected static Dialog mDialog;
	protected static boolean shown;
	private static SharedPreferences mGuideByVersion;
	private static SharedPreferences mGuideByUser;
	/** 监听网络状态 */
	public static boolean NETWORK_CONNECT_STATUS = false;
	private String df="default";

	public static void startActivity(Activity activity, Class<?> activityClass) {
		Intent intent = new Intent(activity, activityClass);
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
	}

	public static void startActivityFromLeft(Activity activity, Class<?> activityClass) {
		Intent intent = new Intent(activity, activityClass);
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	}

	public static void startActivityFromLeft(Activity activity, Intent intent) {
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	}

	public static void startActivity(Context context, Intent intent) {
		if (context instanceof Activity) {
			Activity activity = (Activity) context;
			activity.startActivity(intent);
			activity.overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
		} else {
			context.startActivity(intent);
		}
	}

	public static void startActivityForResult(Activity activity, Intent intent, int requestCode) {
		activity.startActivityForResult(intent, requestCode);
		activity.overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
	}

	public static void finishActivity(Activity activity) {
		activity.finish();
		// activity.overridePendingTransition(R.anim.tran_pre_in,
		// R.anim.tran_pre_out);
	}

	/**
	 * 销毁activity 有动画
	 * 
	 * @param activity
	 */
	public static void finishActivityAnimation(Activity activity) {
		activity.finish();
		activity.overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	}

	// public static void finishActivityAndRemoveTask(Activity activity){
	// activity.finish();
	// activity.overridePendingTransition(R.anim.tran_pre_in,
	// R.anim.tran_pre_out);
	// PagerTaskUtil.removeFromTask(activity);
	// }

	public static void showProgressDialog(Context context, String title, String msg) {
		showProgressDialog(context, title, msg, true);
	}

	public static void showProgressDialog(Context context, String title, String msg, boolean backCancle) {
		showProgressDialog(context, title, msg, backCancle, true);
	}

	public static void showProgressDialog(Context context, String title, String msg, boolean backCancle,
			boolean cancelable) {
		View contentView = View.inflate(context, R.layout.mm_progress_dialog, null);
		TextView tv_msg = (TextView) contentView.findViewById(R.id.mm_progress_dialog_msg);
		TextView tv_title = (TextView) contentView.findViewById(R.id.mm_progress_dialog_title);
		if (title == null) {
			tv_title.setVisibility(View.GONE);
		} else {
			tv_title.setText(title);
			tv_title.setVisibility(View.VISIBLE);
		}
		if (msg == null) {
			tv_msg.setVisibility(View.GONE);
		} else {
			tv_msg.setText(msg);
			tv_msg.setVisibility(View.VISIBLE);
		}
		tv_msg.setText(msg);
		mDialog = new Dialog(context, R.style.TaskInfoDialog);
		mDialog.setContentView(contentView);
		if (cancelable) {
			if (backCancle) {
				mDialog.setCancelable(false);
				mDialog.setOnKeyListener(new OnKeyListener() {
					@Override
					public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
						if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
							closeProgressDialog();
						}
						return false;
					}
				});
			}
		} else {
			mDialog.setCancelable(false);
		}
		try {
			mDialog.show();
		} catch (Exception e) {
			Log.e("CommonUtil", "show dialog error!!!", e);
		}
	}

	public static void showProgressDialog(Context context) {
		showProgressDialog(context, null, null);
	}

	public static void showLocatingDialog(Context context) {
		AnimationDrawable anmiDrawable = (AnimationDrawable) context.getResources()
				.getDrawable(R.drawable.location_animation_list);
		View contentView = View.inflate(context, R.layout.dialog_location, null);
		ImageView iv_locating = (ImageView) contentView.findViewById(R.id.iv_locating);
		iv_locating.setBackgroundDrawable(anmiDrawable);
		anmiDrawable.start();
		mDialog = new Dialog(context, R.style.TaskInfoDialog);
		mDialog.setContentView(contentView);
		mDialog.setCancelable(true);
		try {
			mDialog.show();
		} catch (Exception e) {
			Log.e("CommonUtil", "show dialog error!!!", e);
		}
	}

	public static void showImageViewDialog(Context context, int resid) {
		ImageView contentView = new ImageView(context);
		contentView.setBackgroundResource(resid);
		contentView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mDialog = new Dialog(context, R.style.TaskInfoDialog);
		mDialog.setContentView(contentView);
		mDialog.setCancelable(true);
		try {
			mDialog.show();
		} catch (Exception e) {
			Log.e("CommonUtil", "show dialog error!!!", e);
		}
	}

	public static void showAddSmartCardInfoDialog(Context context) {
		View contentView = View.inflate(context, R.layout.dialog_addsmartcard_info, null);
		mDialog = new Dialog(context, R.style.TaskInfoDialog);
		mDialog.setContentView(contentView);
		mDialog.setCancelable(true);
		try {
			mDialog.show();
		} catch (Exception e) {
			Log.e("CommonUtil", "show dialog error!!!", e);
		}
	}

	public static void closeProgressDialog() {
		try {
			if (mDialog != null && mDialog.isShowing()) {
				mDialog.dismiss();
				mDialog = null;
			}
		} catch (Exception e) {
		}
	}

	public static void startActivity(Context context, Class<?> activityClass) {
		Intent intent = new Intent(context, activityClass);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	public static String getSign(Context context, String packageName) {
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> apps = pm.getInstalledPackages(PackageManager.GET_SIGNATURES);
		Iterator<PackageInfo> iter = apps.iterator();

		while (iter.hasNext()) {
			PackageInfo info = iter.next();
			String pName = info.packageName;
			// 按包名 取签名
			if (pName.equals(packageName)) {
				return info.signatures[0].toCharsString();

			}
		}
		return null;
	}

	public static void showHashKey(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo("com.star.mobile.video",
					PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.v("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		} catch (NameNotFoundException e) {

		} catch (NoSuchAlgorithmException e) {

		}
	}

	private static boolean rSuccess = false;

	public static Dialog showRechargeSuccessDialog(final Activity context, RechargeResult rr, double amount) {
		final Dialog dialog = new Dialog(context, R.style.TaskInfoDialog);
		String unit = SharedPreferencesUtil.getCurrencSymbol(context);
		View content = LayoutInflater.from(context).inflate(R.layout.dialog_rechage_success, null);
		TextView reBySc = (TextView) content.findViewById(R.id.tv_recharge_sc);
		TextView reByCo = (TextView) content.findViewById(R.id.tv_recharge_coupon);
		ImageView btnShare = (ImageView) content.findViewById(R.id.btn_shareEgg);
		Button button = (Button) content.findViewById(R.id.btn_OK);
		String textSC = null;
		String textCO = null;

		final double faceValue = rr.getByExchangeVO().getFaceValue();
		if (rr.getExchangeMoney() == null) {
			reByCo.setText(String.format(context.getString(R.string.recharge_notmatch),
					unit + rr.getByExchangeVO().getLessAmount()));
			textSC = String.format(context.getString(R.string.rechargeBy_SC_failCO),
					unit + sub(rr.getRechargeMoney(), amount), unit + rr.getRechargeMoney());
			SpannableStringBuilder style2 = setStyle(textSC, unit + sub(rr.getRechargeMoney(), amount),
					unit + rr.getRechargeMoney());
			reBySc.setText(style2);
			rSuccess = false;
		} else {
			textCO = String.format(context.getString(R.string.rechargeBy_CO), unit + faceValue,
					unit + rr.getExchangeMoney());
			SpannableStringBuilder style1 = setStyle(textCO, unit + faceValue, unit + rr.getExchangeMoney());
			reByCo.setText(style1);
			if (rr.getExchangeMoney() - amount - faceValue > 0.0) {
				textSC = String.format(context.getString(R.string.rechargeBy_SC),
						unit + sub(rr.getExchangeMoney(), amount, faceValue));
				SpannableStringBuilder style2 = setStyle(textSC, unit + sub(rr.getExchangeMoney(), amount, faceValue),
						null);
				reBySc.setText(style2);
			}
			rSuccess = true;
		}
		final Animation anim = startAnimation(btnShare);
		OnClickListener onclick = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.btn_OK:
				case R.id.btn_shareEgg:
					if (rSuccess)
						new EggService(context).shareFreeCouponExchange(context, ShareUtil.RECHARGE_SHARE);
					else
						new EggService(context).shareFreeCouponExchange(context, ShareUtil.DIRECTLY_SHARE);
					dialog.dismiss();
					break;
				}
				anim.cancel();
			}
		};
		btnShare.setOnClickListener(onclick);
		button.setOnClickListener(onclick);
		dialog.setContentView(content);
		try {
			dialog.show();
		} catch (Exception e) {
		}
		return dialog;
	}

	public static Animation startAnimation(ImageView view) {
		TranslateAnimation animUp = new TranslateAnimation(0, 0, 0, -10);
		animUp.setDuration(50);
		animUp.setRepeatMode(Animation.RESTART);
		animUp.setRepeatCount(Animation.INFINITE);
		animUp.setInterpolator(new DecelerateInterpolator());
		animUp.setStartOffset(2000);
		view.setAnimation(animUp);
		animUp.start();
		return animUp;
	}

	public static Dialog showRechargeSuccessDialog(final Activity context, double total, final double rechargeMoney,
			double pagaRC, final ExchangeVO exchange) {
		final Dialog dialog = new Dialog(context, R.style.TaskInfoDialog);
		String unit = SharedPreferencesUtil.getCurrencSymbol(context);
		View content = LayoutInflater.from(context).inflate(R.layout.dialog_rechage_success, null);
		TextView reBySc = (TextView) content.findViewById(R.id.tv_recharge_sc);
		TextView reByCo = (TextView) content.findViewById(R.id.tv_recharge_coupon);
		ImageView btnShare = (ImageView) content.findViewById(R.id.btn_shareEgg);
		Button button = (Button) content.findViewById(R.id.btn_OK);
		String textSC = null;
		String textCO = null;

		if (rechargeMoney > 0) {
			total += pagaRC;
			textSC = String.format(context.getString(R.string.rechargeBy_SC), unit + pagaRC);
			SpannableStringBuilder style2 = setStyle(textSC, unit + pagaRC, null);
			reBySc.setText(style2);
			total += rechargeMoney;
			textCO = String.format(context.getString(R.string.rechargeBy_CO), unit + rechargeMoney, unit + total);
			SpannableStringBuilder style1 = setStyle(textCO, unit + rechargeMoney, unit + total);
			reByCo.setText(style1);
			rSuccess = true;
		} else if (exchange != null) {
			total += pagaRC;
			textSC = String.format(context.getString(R.string.rechargeBy_SC_failCO), unit + pagaRC, unit + total);
			SpannableStringBuilder style2 = setStyle(textSC, unit + pagaRC, unit + total);
			reBySc.setText(style2);
			reByCo.setText(
					String.format(context.getString(R.string.recharge_notmatch), unit + exchange.getLessAmount()));
			rSuccess = false;
		}
		final Animation anim = startAnimation(btnShare);
		OnClickListener onclick = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.btn_OK:
				case R.id.btn_shareEgg:
					if (rSuccess)
						// EggAppearService.shareOneEgg(context,
						// StarApplication.mUser, rechargeMoney);
						new EggService(context).shareFreeCouponExchange(context, ShareUtil.RECHARGE_SHARE);
					else
						// EggAppearService.shareOneEgg(context,
						// StarApplication.mUser, 0.0);
						new EggService(context).shareFreeCouponExchange(context, ShareUtil.DIRECTLY_SHARE);
					dialog.dismiss();
					break;
				}
				anim.cancel();
			}
		};
		btnShare.setOnClickListener(onclick);
		button.setOnClickListener(onclick);
		dialog.setContentView(content);
		dialog.show();
		return dialog;
	}

	private static SpannableStringBuilder setStyle(String textCO, String faceValue, String totalValue) {
		SpannableStringBuilder style = new SpannableStringBuilder(textCO);
		if (faceValue != null) {
			int start = textCO.indexOf(faceValue);
			int end = start + faceValue.length();
			style.setSpan(new ForegroundColorSpan(Color.parseColor("#FF7E07")), start, end,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		if (totalValue != null) {
			int start_ = textCO.indexOf(totalValue);
			int end_ = start_ + totalValue.length();
			style.setSpan(new ForegroundColorSpan(Color.parseColor("#FF7E07")), start_, end_,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		return style;
	}

	public static Dialog showMyCouponsInfoDialog(final Context context, ExchangeVO exchange) {
		final Dialog dialog = new Dialog(context, R.style.TaskInfoDialog);
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_exchange_alert, null);
		TextView title = (TextView) view.findViewById(R.id.tv_coupons_title);
		TextView content = (TextView) view.findViewById(R.id.tv_coupons_content);
		com.star.ui.ImageView icon = (com.star.ui.ImageView) view.findViewById(R.id.iv_coupons_image);
		LinearLayout llPrice = (LinearLayout) view.findViewById(R.id.ll_ic_price);
		TextView tvSymbol = (TextView) view.findViewById(R.id.tv_symbol);
		TextView price = (TextView) view.findViewById(R.id.tv_coupon_price);
		if (exchange.getType() == Award.Type_Wallet) {
			llPrice.setVisibility(View.VISIBLE);
			tvSymbol.setText(SharedPreferencesUtil.getCurrencSymbol(context));
			price.setText("" + (int) exchange.getFaceValue());
			if (exchange.getTypeGet() == ExchangeVO.TYPE_NEW_CUSTOMER) {
				title.setText(context.getString(R.string.coupon_title_newCus));
				title.setTextColor(context.getResources().getColor(R.color.egg_yellow));
				icon.setImageResource(R.drawable.yellow_coupon_dec);
				content.setText(String.format(context.getString(R.string.coupon_content_newCus),
						DateFormat.format(exchange.getValidDate(), "yyyy-MM-dd")));
			} else if (exchange.getTypeGet() == ExchangeVO.TYPE_EXCHANGE
					|| exchange.getTypeGet() == ExchangeVO.CLUB_COUPON) {
				title.setText(context.getString(R.string.coupon_title_limit));
				title.setTextColor(context.getResources().getColor(R.color.egg_red));
				String text = String.format(context.getString(R.string.coupon_content_limit),
						SharedPreferencesUtil.getCurrencSymbol(context) + exchange.getLessAmount().intValue(),
						DateFormat.format(exchange.getValidDate(), "yyyy-MM-dd"));
				if (exchange.getTypeGet() == ExchangeVO.CLUB_COUPON) {
					text = String.format(context.getString(R.string.coupon_content_club),
							SharedPreferencesUtil.getCurrencSymbol(context) + exchange.getLessAmount().intValue());
				}
				icon.setImageResource(R.drawable.orange_coupon_dec);
				content.setText(text);
			} else if (exchange.getTypeGet() == ExchangeVO.FREE_COUPON) {
				title.setText(context.getString(R.string.coupon_title_free));
				title.setTextColor(context.getResources().getColor(R.color.egg_green));
				String text = String.format(context.getString(R.string.coupon_content_common),
						DateFormat.format(exchange.getValidDate(), "yyyy-MM-dd"));
				content.setText(text);
				icon.setImageResource(R.drawable.free_coupon_dec);
			} else {
				title.setText(context.getString(R.string.coupon_title_common));
				title.setTextColor(context.getResources().getColor(R.color.egg_blue));
				content.setText(String.format(context.getString(R.string.coupon_content_common),
						DateFormat.format(exchange.getValidDate(), "yyyy-MM-dd")));
				icon.setImageResource(R.drawable.blue_coupon_dec);
			}
		} else {
			try {
				icon.setUrl(exchange.getPoster().getResources().get(0).getUrl());
			} catch (Exception e) {
			}
		}
		dialog.setContentView(view);
		dialog.show();
		return dialog;
	}

	public static boolean matchStr(String regex, String str) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	public static double sub(double d1, double d2) {
		BigDecimal bd1 = new BigDecimal(Double.toString(d1));
		BigDecimal bd2 = new BigDecimal(Double.toString(d2));
		return bd1.subtract(bd2).doubleValue();
	}

	public static double sub(double d1, double d2, double d3) {
		double d4 = sub(d1, d2);
		return sub(d4, d3);
	}

	public static void saveUserInfo(Context context, String userName, String pwd, String token) {
		SharedPreferencesUtil.clearUserInfo(context);// 清空用户信息
		Map<String, String> params = new HashMap<String, String>();
		params.put("userName", userName);
		params.put("password", pwd);
		params.put("token", token);
		SharedPreferencesUtil.saveUserInfo(context, params);
		SharedPreferencesUtil.keepUserName(context, userName);
	}

	public static void pleaseLogin(final Context mContext) {
		pleaseLogin(false, mContext);
	}

	public static void pleaseLogin(final boolean goBack, final Context mContext) {
		final PromptDialog dialog = new PromptDialog(mContext);
		dialog.setMessage(mContext.getString(R.string.alert_login));
		dialog.setConfirmText(mContext.getString(R.string.login_btn));
		dialog.setCancelText(mContext.getString(R.string.later));
		dialog.setConfirmOnClick(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, ChooseAreaActivity.class);
				CommonUtil.startActivity((Activity) mContext, intent);
				dialog.dismiss();
			}
		});
		dialog.setCancelOnClick(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}


	/**
	 * 展示提示对话框
	 * @param context
	 * @param title 标题，不需要可传null
	 * @param message 提示内容，不需要可传null
	 * @param confirmContent 确认按钮文字，不需要可传null，传null的时候点击事件也不可操作
	 * @param cancelContent 取消按钮文字，不需要可传null，传null的时候点击事件也不可操作
	 * @param listener 确认、取消按钮的点击事件，不需要可传null
	 */
	public void showPromptDialog(final Context context, String title, final String message, String confirmContent,
			String cancelContent, PromptDialogClickListener listener) {
		setDialogClickListner(listener);
		// * @param 0 表示 Acitivty Dialog  
		promptDialog(context, title, message, confirmContent, cancelContent,0);
	}
	public void showPromptSystemDialog(final Context context, String title, final String message, String confirmContent,
			String cancelContent, PromptDialogClickListener listener) {
		setDialogClickListner(listener);
		//* @param 1 表示系统 Dialog  
		promptDialog(context, title, message, confirmContent, cancelContent,1);
	}

	/**
	 * 提示对话框
	 * @param context 上下文
	 * @param title 标题
	 * @param message 提示内容
	 * @param confirmContent 确认按钮文字
	 * @param cancelContent 取消按钮文字
	 */
	private void promptDialog(final Context context, String title, final String message, String confirmContent,
			String cancelContent,int type) {

		final PromptDialog dialog = new PromptDialog(context);
		if (title != null) {
			dialog.setTitle(title);
		}
		dialog.setMessage(message);
		if (confirmContent != null) {
			dialog.setConfirmText(confirmContent);
			dialog.setConfirmOnClick(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mDialogClickListner != null)
						mDialogClickListner.onConfirmClick();
					dialog.dismiss();
				}
			});
		}
		if (cancelContent != null) {
			dialog.setCancelText(cancelContent);
			dialog.setCancelOnClick(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mDialogClickListner != null)
						mDialogClickListner.onCancelClick();
					dialog.dismiss();
				}
			});
		}
		if(type ==1){
			dialog.getWindow().setType(
	                (WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));	
		}
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}

	public void showDoTaskDialog(Context context, String taskName, int coins, PromptDialogClickListener listener) {
		setDialogClickListner(listener);
		promptDialog(context, context.getString(R.string.congratulations),
				String.format(context.getString(R.string.dotask_earncoins), taskName, coins),
				context.getString(R.string.forum_confirm), null,0);
	}

	private PromptDialogClickListener mDialogClickListner;

	public interface PromptDialogClickListener {
		void onConfirmClick();

		void onCancelClick();
	}
	
	public void setDialogClickListner(PromptDialogClickListener dialogClickListner) {
		this.mDialogClickListner = dialogClickListner;
	}

	/**
	 * 单利模式
	 */
	private static CommonUtil instance = new CommonUtil();

	private CommonUtil() {
	}

	public static CommonUtil getInstance() {
		return instance;
	}
	
	/**
	 * 
	 * @param context
	 * @param target
	 *            要转的页面
	 * @param params
	 */

	public static void goActivityOrFargment(Context context, Class<?> target, Intent intent) {
		if (target == null)
			return;
		try {
			Object obj = target.getConstructor().newInstance();
			if (obj instanceof Activity) {
				CommonUtil.startActivity((Activity) context, intent);
			} else if (obj instanceof Fragment) {
				startFragmentActivity(context, MenuHandle.getFragmentTag(target.getName()), null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param context
	 * @param type
	 *            3:activity 2: html 0:channel 1:program
	 * @param page
	 *            如:com.star.mobile.video.LoginActivity?
	 * @param ownId
	 *            跳转 channel guid 和 epg
	 * @param htmlUrl
	 *            如： http://tenbre.me
	 * @param title
	 *            浏览器标题
	 */

	public static void goActivityOrFargment(Context context, Type type, String target, String htmlUrl, String title) {
		goActivityOrFargment(context, type, target, htmlUrl, title, false);
	}

	/**
	 * 
	 * @param context
	 * @param type
	 *            3:activity 2: html 0:channel 1:program
	 * @param page
	 *            如:com.star.mobile.video.LoginActivity?
	 * @param ownId
	 *            跳转 channel guid 和 epg
	 * @param htmlUrl
	 *            如： http://tenbre.me
	 * @param title
	 *            浏览器标题
	 * @param isAlertEpg
	 *            是否提示预约节目
	 */
	public static void goActivityOrFargment(Context context, Type type, String target, String htmlUrl, String title, boolean isAlertEpg) {
		try {
			String[] arr = target.split("\\?");
			Class<?> clazz = Class.forName(arr[0]);
			Intent intent = new Intent();
			if(arr.length>1){
				String[] params = arr[1].split("&");
				for (String param : params) {
					String[] value = param.split("=");
					if (value.length == 2) {
						intent.putExtra(value[0], value[1]);
					}
				}
			}
			if (type.equals(Type.Channel)) {
				type=Type.Page;
			} else if (type.equals(Type.Program)) {
				type=Type.Page;
			}
			if(type.equals(Type.Advertisement)){
				intent.setComponent(new ComponentName(context, clazz));
				intent.putExtra("loadUrl", htmlUrl);
				intent.putExtra("pageName", title);
			} else if (type.equals(Type.Page)) {
					Object object = clazz.getConstructor().newInstance();
					if (object instanceof Activity) {
						if (object instanceof AccountConnectActivity || object instanceof InvitationActivity) {
							if (SharedPreferencesUtil.getUserName(context) == null) {
								CommonUtil.pleaseLogin(context);
								return;
							}
						}
						intent.setComponent(new ComponentName(context, clazz));
					} else if (object instanceof Fragment) {
						if (object instanceof AccountManagerFragment) {
							if (SharedPreferencesUtil.getUserName(context) == null) {
								CommonUtil.pleaseLogin(context);
								return;
							}
						} 
						intent.setComponent(new ComponentName(context, BaseFragmentActivity.class.getName()));
						intent.putExtra("fragment",  MenuHandle.getFragmentTag(arr[0]));
					}
			}
			CommonUtil.startActivity(context, intent);
		} catch (Exception e) {
			Log.i("Recommend", "Parser page code error!", e);
		}
	}

	public static void showNetworkerror(final Activity context) {
		
		
		CommonUtil.getInstance().showPromptDialog(context, null, context.getString(R.string.error_network), context.getString(R.string.exit), null, new CommonUtil.PromptDialogClickListener() {
			
			@Override
			public void onConfirmClick() {
				context.finish();
			}
			
			@Override
			public void onCancelClick() {
				
			}
		});
	}

	/**
	 * 跳转到bbs
	 * 
	 * @param context
	 */
	public static void skipBbs(Activity activity) {
		Intent intent = new Intent(activity, BrowserActivity.class);
		intent.putExtra("loadUrl", activity.getString(R.string.bbs_url));
		intent.putExtra("isBbs", 1);
		CommonUtil.startActivity(activity, intent);

	}

	public static boolean match(String regex, String str) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	public static void startEpgActivity(Context context, Long epgId) {
		Intent intent = new Intent(context, EpgDetailActivity.class);
		if(epgId != null)
			intent.putExtra("programId", epgId);
		startActivity(context, intent);
	}

	public static void startFragmentActivity(Context context, String fragmentTag, Long id) {
		Intent intent = new Intent(context, BaseFragmentActivity.class);
		intent.putExtra("fragment", fragmentTag);
		if (id != null)
			intent.putExtra("id", id);
		startActivity(context, intent);
	}

	public static void startChannelActivity(Context context, ChannelVO channel) {
		Intent intent = new Intent(context, BaseFragmentActivity.class);
		if (channel != null){
			intent.putExtra("channel", channel);
			intent.putExtra("fragment", context.getString(R.string.fragment_tag_channelGuide));
		}
		startActivity(context, intent);
	}

	public static boolean show(Context context, String maskId, String maskType) {
		mGuideByVersion = SharedPreferencesUtil.getGuideSharePreferencesByAppVersion(context);
		mGuideByUser = SharedPreferencesUtil.getGuideSharePreferencesByUser(context);
		if (maskType.equals(Constant.GUIDE_BY_VERSION)) {
			shown = mGuideByVersion.getBoolean(maskId, false);
		} else if (maskType.equals(Constant.GUIDE_BY_USER)) {
			shown = mGuideByUser.getBoolean(maskId, false);
		}
		return shown;
	}

	public static void setShow(String maskId, String maskType) {
		if (maskType.equals(Constant.GUIDE_BY_VERSION)) {
			mGuideByVersion.edit().putBoolean(maskId, true).commit();
		} else if (maskType.equals(Constant.GUIDE_BY_USER)) {
			mGuideByUser.edit().putBoolean(maskId, true).commit();
		}
	}

	public static String getSelAreaNumber(Context context) {
		String areaCode = SharedPreferencesUtil.getAreaCode(context);
		String result = null;
		if (Area.NIGERIA_CODE.equals(areaCode)) {
			result = context.getString(R.string.nijeria_number);
		} else if (Area.TANZANIA_CODE.equals(areaCode)) {
			result = context.getString(R.string.tanzania_number);
		} else if (Area.KENYA_CODE.equals(areaCode)) {
			result = context.getString(R.string.kenya_area_number);
		} else if (Area.UGANDA_CODE.equals(areaCode)) {
			result = context.getString(R.string.uganda_number);
		} else if (Area.SOUTHAFRICA_CODE.equals(areaCode)) {
			result = context.getString(R.string.south_africa_number);
		} else if (Area.RWANDA_CODE.equals(areaCode)) {
			result = context.getString(R.string.rwanda_number);
		}
		return result;
	}
	
	 /**
     * 检测是否有emoji表情
     *
     * @param source
     * @return
     */
    public static boolean containsEmoji(String source) {
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (!isEmojiCharacter(codePoint)) { //如果不能匹配,则该字符是Emoji表情
                return true;
            }
        }
        return false;
    }
    
    /**
     * 判断是否是Emoji
     *
     * @param codePoint 比较的单个字符
     * @return
     */
    private static boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) ||
                (codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000)
                && (codePoint <= 0x10FFFF));
    }
    
    
}
