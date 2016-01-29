package com.star.mobile.video.view;

import java.util.TimerTask;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.star.mobile.video.R;
import com.star.mobile.video.model.MyTimer;

public class CheckCodeView extends LinearLayout implements OnClickListener {
	
	private Context mContext;
	
	private EditText etCode;//验证码输入框
	private Button btGetCode;//获取验证码按钮
	private TextView tvErrorMsg;//错误提示信息
	
	private MyTimer myTimer;
	private final int UPDATE_TIME = 101;
	private int timeCount = 60;
	
	private CheckCodeButtonOnClick btnOnClick;
	private CodeCallBack callBack;
	
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what == UPDATE_TIME) {
				if(timeCount <= 0) {
					stopTime();
				} else {
					setGetCodeButtonText(timeCount+"s");
					timeCount--;
				}
			}
		}
	};
	
	public CheckCodeView(Context context) {
		this(context, null);
	}

	public CheckCodeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		LayoutInflater.from(context).inflate(R.layout.check_code_view, this);
		etCode = (EditText) findViewById(R.id.et_check_num);
		btGetCode = (Button) findViewById(R.id.bt_ver_code);
		tvErrorMsg = (TextView) findViewById(R.id.tv_code_msg);
		etCode.addTextChangedListener(new CodeTextWatcher());
		btGetCode.setOnClickListener(this);
		setGetCodeButtonText(mContext.getString(R.string.get_code));
	}

	
	
	public void stopTime() {
		if(myTimer!= null && myTimer.innerTask != null) {
			myTimer.innerTask.cancel();
			btGetCode.setOnClickListener(CheckCodeView.this);
			btGetCode.setBackgroundResource(R.drawable.get_code_black_button);
		}
		timeCount = 0;
		setGetCodeButtonText(mContext.getString(R.string.get_code));
	}

	/**
	 * 计时
	 */
	public void time() {
		timeCount = 60;
		btGetCode.setOnClickListener(null);
		btGetCode.setBackgroundResource(R.drawable.get_code_orange_button);
		myTimer = new MyTimer();
		TimerTask timerTask = new TimerTask() {
			
			@Override
			public void run() {
				handler.sendEmptyMessage(UPDATE_TIME);
			}
		};
		myTimer.innerTask = timerTask;
		myTimer.schedule(timerTask, 0, 1000);
	}
	
	private void setGetCodeButtonText(String text) {
		btGetCode.setText(text);
	}
	
	public void setCheckCodeButtonOnClick(CheckCodeButtonOnClick click) {
		this.btnOnClick = click;
	}
	
	public void setCodeCallBack(CodeCallBack callBack) {
		this.callBack = callBack;
	}
	
	public String getCodeText() {
		return this.etCode.getEditableText().toString();
	}
	
	/**
	 * 清空文本框
	 */
	public void clearCodeEdittext() {
		etCode.getEditableText().clear();
	}
	
	private class CodeTextWatcher implements TextWatcher {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
				if(callBack != null) {
					callBack.Listener(getCodeText());
				}
		}

		@Override
		public void afterTextChanged(Editable s) {
			
		}
	}
	
	/**
	 * 设置验错误证码提示信息
	 * @param text
	 */
	public void setCodeErrorMsg(String text) {
		if(text == null || "".equals(text)) {
			tvErrorMsg.setVisibility(View.GONE);
		} else {
			tvErrorMsg.setText(text);
			tvErrorMsg.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * 设置验错误证码提示信息字体颜色
	 * @param color
	 */
	public void setCodeErrorTextColor(int color) {
		tvErrorMsg.setTextColor(color);
	}
	
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_ver_code:
			if(btnOnClick != null) {
				btnOnClick.onClick();
			}
			break;
		default:
			break;
		}
	}
	
	public void setCodeOnFocusChangeListener(OnFocusChangeListener l) {
		etCode.setOnFocusChangeListener(l);
	}
	
	/**
	 * 获取验证码 按钮事件
	 * @author zhangk
	 *
	 */
	public interface CheckCodeButtonOnClick {
		public void onClick();
	}
	
	
	/**
	 *  验证码 输入够四位回调
	 * @author zhangk
	 *
	 */
	public interface CodeCallBack {
		public void Listener(String codeText);
	}
	
	public void inputCodeSel() {
		etCode.setFocusable(true);
		etCode.setFocusableInTouchMode(true);
		etCode.requestFocus();
	}
}
