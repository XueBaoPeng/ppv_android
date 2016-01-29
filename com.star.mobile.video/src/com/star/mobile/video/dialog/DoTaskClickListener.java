package com.star.mobile.video.dialog;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import com.star.cms.model.Task;
import com.star.cms.model.dto.DoTaskResult;
import com.star.cms.model.vo.TaskVO;
import com.star.mobile.video.R;
import com.star.mobile.video.account.ChooseAreaActivity;
import com.star.mobile.video.account.RegisterActivity;
import com.star.mobile.video.activity.AccountConnectActivity;
import com.star.mobile.video.me.feedback.UserReportActivity;
import com.star.mobile.video.me.mycoins.MyCoinsActivity;
import com.star.mobile.video.me.mycoins.TaskService;
import com.star.mobile.video.model.TaskCode;
import com.star.mobile.video.service.FeedbackService;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.smartcard.SmartCardActivity;
import com.star.mobile.video.smartcard.SmartCardControlActivity;
import com.star.mobile.video.util.ApplicationUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.CommonUtil.PromptDialogClickListener;
import com.star.mobile.video.util.ToastUtil;
import com.star.util.loader.OnResultListener;

public class DoTaskClickListener implements OnClickListener {

	private Activity context;
	private final int UPDATE_VIEW = 1000;
	private TaskService taskService;

	public DoTaskClickListener(Activity context){
		this.context = context;
		taskService = new TaskService(context);
	}
	
	@Override
	public void onClick(View v) {
		TaskVO tv = (TaskVO) v.getTag();
		if(tv.getCode().equals(TaskCode.Register)) {
			Intent intent = new Intent(context,RegisterActivity.class);
			intent.putExtra("isBackFinish", true);
			CommonUtil.startActivity(context, intent);
		} else if(tv.getCode().equals(TaskCode.Book_program)) {
			if(checkLongin()) {
				return;
			}
			CommonUtil.startFragmentActivity(context, context.getString(R.string.fragment_tag_channelGuide), 92L);
		} else if(tv.getCode().equals(TaskCode.Go_four_report)) {
			if(checkLongin()) {
				return;
			}
			if(!FeedbackService.getInstance(context).isDoFourLayer()) {
				ToastUtil.centerShowToast(context, context.getString(R.string.user_report_for_current_version));
				return;
			}
			CommonUtil.startActivity(context, UserReportActivity.class);
		} else if(tv.getCode().equals(TaskCode.Bind_smartcard)) {
			if(checkLongin()) {
				return;
			}
			Intent intent = new Intent(context, SmartCardActivity.class);
			intent.putExtra("showFrame", true);
			CommonUtil.startActivity(context, intent);
		} else if(tv.getCode().equals(TaskCode.Sign_in)) {
			if(!checkLongin()) {
				taskService.doTask(TaskCode.Sign_in,ApplicationUtil.getAppVerison(context), new OnResultListener<DoTaskResult>() {
					
					@Override
					public void onSuccess(DoTaskResult result) {
						CommonUtil.closeProgressDialog();
						if(result != null) {
							if(result.isDone()) {
								ToastUtil.centerShowToast(context, context.getString(R.string.sign_in_success));
								taskService.showTaskDialog(context, result, new PromptDialogClickListener() {
									@Override
									public void onConfirmClick() {
										if(context instanceof MyCoinsActivity){
											((MyCoinsActivity)context).updateUI();
										}
									}
									
									@Override
									public void onCancelClick() {
									}
								});
							} else {
								ToastUtil.centerShowToast(context, context.getString(R.string.sign_in_limit));
							}
						} else {
							ToastUtil.centerShowToast(context, context.getString(R.string.sign_in_failure));
						}
					}
					
					@Override
					public boolean onIntercept() {
						CommonUtil.showProgressDialog(context, null, context.getString(R.string.checking_in));
						return false;
					}
					
					@Override
					public void onFailure(int errorCode, String msg) {
						CommonUtil.closeProgressDialog();
						ToastUtil.centerShowToast(context, context.getString(R.string.sign_in_failure));
					}
				});
			}
		} else if(tv.getCode().equals(Task.TaskCode.Recharge)) {
			if(checkLongin()) {
				return;
			} else {
				CommonUtil.startActivity(context, new Intent(context, SmartCardControlActivity.class));
			}
		}else if(tv.getCode().equals(Task.TaskCode.Link_ThirdAccount)){
			if(checkLongin()) {
				return;
			} else {
				Intent intent = new Intent(context, AccountConnectActivity.class);
				intent.putExtra("goGetCoin", true);
				CommonUtil.startActivity(context, intent);
			}
		}else if(tv.getCode().equals(Task.TaskCode.RechargeWithPaga)){
			if(checkLongin()) {
				return;
			} else {
				CommonUtil.startActivity(context, new Intent(context, SmartCardControlActivity.class));
			}
		}
	}
	
	//判断是否登录
	private boolean checkLongin() {
		if(SharedPreferencesUtil.getUserName(context) == null) {
			CommonUtil.getInstance().showPromptDialog(context, null, context.getString(R.string.login_first), context.getString(R.string.ok), context.getString(R.string.dismiss_s), new PromptDialogClickListener() {
				
				@Override
				public void onConfirmClick() {
					Intent intent = new Intent(context,ChooseAreaActivity.class);
					context.startActivity(intent);
				}
				
				@Override
				public void onCancelClick() {
					
				}
			});
			return true;
		} else {
			return false;
		}
	}
}
