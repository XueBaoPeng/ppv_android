package com.star.mobile.video.dialog;

import com.star.cms.model.enm.Sex;
import com.star.mobile.video.R;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

public class SelectSexDialog extends Dialog implements android.view.View.OnClickListener {
	private TextView mDialogSpecified;
	private TextView mDialogMale;
	private TextView mDialogFemale;
	private String user_sex;
	private String tag;
	private int type;
	private SelectListener listener;
	private Context mContext;
	
	public SelectSexDialog(Context context,SelectListener listener,String tag) {
		super(context, R.style.TaskInfoDialog);
		this.mContext = context;
		this.listener = listener;
		this.tag=tag;
		setContentView(R.layout.selectsex_dialog);
		initView();
		initEvent();
	}

	private void initEvent() {
		mDialogFemale.setOnClickListener(this);
		mDialogMale.setOnClickListener(this);
		mDialogSpecified.setOnClickListener(this);
	}

	 
	private void initView() {
		mDialogFemale = (TextView) findViewById(R.id.select_Female);
		mDialogMale = (TextView) findViewById(R.id.select_Male);
		mDialogSpecified = (TextView) findViewById(R.id.select_specified);
		if(tag.equals(mContext.getString(R.string.sex_man))){
			type=Sex.MALE.getNum();
		}else if(tag.equals(mContext.getString(R.string.sex_woman))){
			type=Sex.WOMAN.getNum();
		}else{
			type=Sex.DEFAULT.getNum();
		}
		if(type==Sex.MALE.getNum()){
			mDialogMale.setTextColor(mContext.getResources().getColor(R.color.sex_color));
		}else if(type==Sex.WOMAN.getNum()){
			mDialogFemale.setTextColor(mContext.getResources().getColor(R.color.sex_color));
		}else if(type==Sex.DEFAULT.getNum()){
			mDialogSpecified.setTextColor(mContext.getResources().getColor(R.color.sex_color));
		}
		
	}

	public interface SelectListener {
		public void SelectResult(String sex);
		
		public void SelectSexType(int type);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.select_specified:
			user_sex = mDialogSpecified.getText().toString();
			this.listener.SelectResult(user_sex);
			this.listener.SelectSexType(Sex.DEFAULT.getNum());
			this.dismiss();
			break;
		case R.id.select_Male:
			user_sex = mDialogMale.getText().toString();
			this.listener.SelectResult(user_sex);
			this.listener.SelectSexType(Sex.MALE.getNum());
			this.dismiss();
			break;

		case R.id.select_Female:
			user_sex = mDialogFemale.getText().toString();
			this.listener.SelectResult(user_sex);
			this.listener.SelectSexType(Sex.WOMAN.getNum());
			this.dismiss();
			break;
		default:
			this.dismiss();
			break;
		}

	}
}
