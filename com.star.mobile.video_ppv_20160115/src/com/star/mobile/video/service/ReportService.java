package com.star.mobile.video.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.star.cms.model.Question;
import com.star.cms.model.dto.DoReportResult;
import com.star.mobile.video.base.AbstractService;
import com.star.mobile.video.util.Constant;
import com.star.util.loader.LoadMode;
import com.star.util.loader.OnListResultListener;
import com.star.util.loader.OnResultListener;

public class ReportService extends AbstractService{

	public ReportService(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public void getQuestions(String appVersion, OnListResultListener<Question> listener){
		String url = Constant.getReportQuestionUrl(appVersion);
		doGet(url, Question.class, LoadMode.CACHE_NET, listener);
	}
	
	public void commitAnswer(List<Long> answerIds, String appVersion, OnResultListener<DoReportResult> listener){
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("answerIDs", answerIds);
		params.put("appVersion", appVersion);
		doPost(Constant.getReportAnswerUrl(appVersion), DoReportResult.class, params, listener);
	}
}
