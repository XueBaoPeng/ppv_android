package com.star.mobile.video.me.feedback;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.star.cms.model.Question;
import com.star.cms.model.dto.DoReportResult;
import com.star.mobile.video.R;
import com.star.mobile.video.activity.CurrentVersionAppDetailActivity;
import com.star.mobile.video.activity.IsLoginAlertDialogActivity;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.model.Answer;
import com.star.mobile.video.service.FeedbackService;
import com.star.mobile.video.service.ReportService;
import com.star.mobile.video.service.UserService;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.ApplicationUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.ExpressionUtil;
import com.star.mobile.video.util.PagerTaskUtil;
import com.star.mobile.video.util.ToastUtil;
import com.star.mobile.video.view.NoScrollGridView;
import com.star.util.loader.OnListResultListener;
import com.star.util.loader.OnResultListener;

public class UserReportActivity extends BaseActivity implements OnClickListener {

	private ReportService reportService;
	private LinearLayout report_list;
	private List<Answer> answerList = new ArrayList<Answer>();
	private List<Question> mQues = new ArrayList<Question>();
	private String regExpres = "s0{2}[1-4]";
	private Button bt_commit;
	private View mLoading;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_report);
		if(SharedPreferencesUtil.getUserName(UserReportActivity.this) == null) {
//			ToastUtil.centerShowToast(UserReportActivity.this, "Please login");
			Intent intent = new Intent(this,IsLoginAlertDialogActivity.class);
			intent.putExtra("isShowTitle", true);
			startActivity(intent);
			finish();
			return;
		}
		if(!FeedbackService.getInstance(UserReportActivity.this).isDoFourLayer()) {
			ToastUtil.centerShowToast(UserReportActivity.this, getString(R.string.user_report_for_current_version));
			finish();
			return;
		}
		((TextView) findViewById(R.id.tv_actionbar_title)).setText(getString(R.string.fast_user_report));
		report_list = (LinearLayout) findViewById(R.id.ll_report_list);
		findViewById(R.id.iv_actionbar_back).setOnClickListener(this);
		mLoading = findViewById(R.id.smartcard_loadingView);
		bt_commit = (Button) findViewById(R.id.bt_report_commit);
		bt_commit.setOnClickListener(this);
		TextView appVersion = (TextView) findViewById(R.id.tv_app_version);
		appVersion.setText( getString(R.string.app_name)+" V " + ApplicationUtil.getAppVerisonName(this));
		TextView appNewVersion = (TextView) findViewById(R.id.tv_app_newVersion);
		appNewVersion.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		appNewVersion.setOnClickListener(this);
		
		reportService = new ReportService(this);
		getQuestionTask();
	}

	private void getQuestionTask() {
		reportService.getQuestions(String.valueOf(ApplicationUtil.getAppVerison(UserReportActivity.this)), new OnListResultListener<Question>() {
			
			@Override
			public boolean onIntercept() {
				bt_commit.setVisibility(View.GONE);
				mLoading.setVisibility(View.VISIBLE);
				return false;
			}
			
			@Override
			public void onFailure(int errorCode, String msg) {
				bt_commit.setVisibility(View.VISIBLE);
				mLoading.setVisibility(View.GONE);
			}
			
			@Override
			public void onSuccess(List<Question> qs) {
				bt_commit.setVisibility(View.VISIBLE);
				mLoading.setVisibility(View.GONE);
				if(qs == null || qs.size() == 0) {
					
					CommonUtil.getInstance().showPromptDialog(UserReportActivity.this, null, getString(R.string.user_report_for_current_version_give_us_feedback), getString(R.string.go), null, new CommonUtil.PromptDialogClickListener() {
						
						@Override
						public void onConfirmClick() {
							finish();
							CommonUtil.startActivity(UserReportActivity.this,FeedbackActivity.class);
						}
						
						@Override
						public void onCancelClick() {
							
						}
					});
					return;
				}
				mQues.clear();
				mQues.addAll(qs);
				report_list.removeAllViews();
				for(int i=0; i<mQues.size(); i++){
					report_list.addView(getReportView(mQues.get(i)));
				}
			}
		});
	}
	
	public View getReportView(final Question question) {
		View view = LayoutInflater.from(this).inflate(R.layout.view_report_item, null);
		TextView tv_question = (TextView) view.findViewById(R.id.tv_report_question);
		NoScrollGridView listView = (NoScrollGridView) view.findViewById(R.id.lv_answer_list);
		tv_question.setText(question.getName()+":");
		final List<Answer> answers = packAnswer(question);
		final AnswerAdapter answerAdapter = new AnswerAdapter(UserReportActivity.this, answers);
		listView.setAdapter(answerAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(answers.get(position).getName().equals("None")){
					for(Answer a : answers){
						if(!a.getName().equals("None"))
							a.setSelect(false);
					}
				}else{
					for(Answer a : answers){
						if(a.getName().equals("None")){
							a.setSelect(false);
						}
					}
				}
				answerAdapter.setOnClickPos(position);
			}
		});
		return view;
	}
	
	private List<Answer> packAnswer(Question question) {
		List<Answer> answers = new ArrayList<Answer>();
		for(com.star.cms.model.Answer aw : question.getAnswers()){
			answers.add(new Answer(aw));
		}
		return answers;
	}

	private void commitAnswerTask(final List<Long> answerIds) {
		reportService.commitAnswer(answerIds, String.valueOf(ApplicationUtil.getAppVerison(UserReportActivity.this)), new OnResultListener<DoReportResult>() {
			
			@Override
			public void onSuccess(DoReportResult result) {
				CommonUtil.closeProgressDialog();
				if(result!=null) {
					if(result.isReport()) {
						answerList.clear();
//						recordFast();
//						Intent intent = new Intent(UserReportActivity.this,FourLayerService.class);
//						stopService(intent);
						SharedPreferencesUtil.setReportDone(UserReportActivity.this, SharedPreferencesUtil.getUserName(UserReportActivity.this), ApplicationUtil.getAppVerison(UserReportActivity.this));
						sendBroadcast(new Intent(FeedbackService.DO_FINISH_FOUR_LAYER));
						new UserService().updateCoins(UserReportActivity.this, result.getTaskResult().getCoins());
						CommonUtil.getInstance().showPromptDialog(UserReportActivity.this, null, getString(R.string.dotask_earncoins,result.getTaskResult().getTask().getName(),result.getTaskResult().getCoins()), getString(R.string.ok), null, new CommonUtil.PromptDialogClickListener() {
							
							@Override
							public void onConfirmClick() {
								finish();
								onBackPressed();
								PagerTaskUtil.removeFromTask(UserReportActivity.class);//在onBackPressed后执行
							}
							
							@Override
							public void onCancelClick() {
								
							}
						});
					} else if(result.getStatus() == DoReportResult.doReport) {
						ToastUtil.centerShowToast(UserReportActivity.this, getString(R.string.do_report));
					} else {
						ToastUtil.centerShowToast(UserReportActivity.this, getString(R.string.commit_report_error));
					}
				} else {
					ToastUtil.centerShowToast(UserReportActivity.this, getString(R.string.commit_report_error));
				}
			}
			
			@Override
			public boolean onIntercept() {
				CommonUtil.showProgressDialog(UserReportActivity.this);
				return false;
			}
			
			@Override
			public void onFailure(int errorCode, String msg) {
				CommonUtil.closeProgressDialog();
				ToastUtil.centerShowToast(UserReportActivity.this, getString(R.string.commit_report_error));
			}
		});
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_actionbar_back:
			super.onBackPressed();
			break;
		case R.id.bt_report_commit:
			if(getCommitQueIds().size()==mQues.size() && mQues.size()!=0){
				List<Long> answerIds = new ArrayList<Long>();
				for(Answer aw : answerList){
					answerIds.add(aw.getId());
				}
				commitAnswerTask(answerIds);
			} else {
				ToastUtil.centerShowToast(UserReportActivity.this, getString(R.string.answer_all_the_questions));
			}
			break;
		case R.id.tv_app_newVersion:
			CommonUtil.startActivity(this, CurrentVersionAppDetailActivity.class);
			break;
		default:
			break;
		}
	}

	private class AnswerAdapter extends BaseAdapter{

		private Context context;
		private List<Answer> answers;
		private int clickPos = -1;

		public AnswerAdapter(Context context, List<Answer> answers) {
			this.context = context;
			this.answers = answers;
		}
		
		public void setOnClickPos(int pos){
			this.clickPos = pos;
			notifyDataSetChanged();
		}
		
		@Override
		public int getCount() {
			return answers.size();
		}

		@Override
		public Object getItem(int position) {
			return answers.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = LayoutInflater.from(context).inflate(R.layout.view_report_answer_item, null);
			TextView tv_answer = (TextView) view.findViewById(R.id.tv_report_answer);
			ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_report_icon);
			ImageView iv_select = (ImageView) view.findViewById(R.id.iv_answer_select);
			Answer answer = answers.get(position);
			String answerStr = answer.getName();
			String express = ExpressionUtil.getExpressionString(context, answerStr, regExpres);
			if(express==null){
				tv_answer.setText(answerStr);
				iv_icon.setVisibility(View.GONE);
			}else{
				tv_answer.setText(answerStr.split(regExpres)[1]);
				int resId = ExpressionUtil.getExpressionResId(express);
				if(resId!=-1){
					iv_icon.setImageResource(resId);
					iv_icon.setVisibility(View.VISIBLE);
				}
			}
			if(position == clickPos){
				answer.setSelect(!answer.isSelect());
			}else if(answer.getQuestionID() == 1){
				answer.setSelect(false);
			}
			if(answer.isSelect()){
				if(answerList.indexOf(answer)==-1){
					answerList.add(answer);
					getCommitQueIds();
				}
				iv_select.setImageResource(R.drawable.sel);
			}else{
				if(answerList.indexOf(answer)!=-1){
					answerList.remove(answer);
					getCommitQueIds();
				}
				iv_select.setImageResource(R.drawable.no_sel);
			}
			return view;
		}
		
	}
	
	private List<Long> getCommitQueIds(){
		List<Long> ids = new ArrayList<Long>();
		for(Answer answer : answerList){
			if(ids.indexOf(answer.getQuestionID())==-1)
				ids.add(answer.getQuestionID());
		}
		if(ids.size()==mQues.size()){
			bt_commit.setBackgroundResource(R.drawable.orange_button_bg);
		}else{
			bt_commit.setBackgroundResource(R.drawable.need_more_coins_button);
		}
		return ids;
	}
}
