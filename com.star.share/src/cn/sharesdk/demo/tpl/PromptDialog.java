package cn.sharesdk.demo.tpl;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PromptDialog extends Dialog {
	private TextView mDialogPromptTitle;
	private TextView mDialogPromptContent;
	private TextView mDialogPromptOK;
	private TextView mDialgoPromptLater;
	private View view;

	public PromptDialog(Context context) {
		this(context, 0);
	}

	public PromptDialog(Context context, int theme) {
		super(context, R.style.TaskInfoDialog);
		setContentView(R.layout.promp_dialog);
		initView();
	}

	private void initView() {
		mDialogPromptTitle = (TextView) findViewById(R.id.title);
		mDialogPromptContent = (TextView) findViewById(R.id.content);
		mDialogPromptOK = (TextView) findViewById(R.id.ok);
		view = (View) findViewById(R.id.title_down);
		mDialgoPromptLater = (TextView) findViewById(R.id.later);
		mDialgoPromptLater.setVisibility(View.GONE);
		mDialogPromptTitle.setVisibility(View.GONE);
		view.setVisibility(View.VISIBLE);
	}

	@SuppressLint("NewApi")
	public void setTitle(String str) {
		if (str == null && str.isEmpty()) {
			mDialogPromptTitle.setVisibility(View.GONE);
			view.setVisibility(View.VISIBLE);
		} else {
			mDialogPromptTitle.setVisibility(View.VISIBLE);
			view.setVisibility(View.GONE);
			mDialogPromptTitle.setText(str);
		}
	}

	public void setTitleTypeFace(Typeface typeFace) {
		mDialogPromptTitle.setTypeface(typeFace);
	}

	public void setMessage(String str) {
		mDialogPromptContent.setText(str);
	}

	public void setMessageTypeFace(Typeface typeFace) {
		mDialogPromptContent.setTypeface(typeFace);
	}

	public void setMessageTextColor(int color) {
		mDialogPromptContent.setTextColor(color);
	}

	public void setConfirmText(String text) {
		mDialogPromptOK.setText(text);
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("null")
	public void setCancelText(String text) {
		if (text == null && text.isEmpty()) {
			mDialgoPromptLater.setVisibility(View.GONE);
		} else {
			mDialgoPromptLater.setVisibility(View.VISIBLE);
			mDialgoPromptLater.setText(text);
		}
	}

	/**
	 * 确认按钮的点击事件
	 * 
	 * @param l
	 */
	public void setConfirmOnClick(android.view.View.OnClickListener l) {
		mDialogPromptOK.setOnClickListener(l);
	}

	/**
	 * 取消按钮的点击事件
	 * 
	 * @param l
	 */
	public void setCancelOnClick(android.view.View.OnClickListener l) {
		mDialgoPromptLater.setOnClickListener(l);
	}

	public void setConfirmTypeFace(Typeface typeFace) {
		mDialogPromptOK.setTypeface(typeFace);
	}

	public void setCancelTypeFace(Typeface typeFace) {
		mDialgoPromptLater.setTypeface(typeFace);
	}

	public void Confirm(int resId) {
		mDialogPromptOK.setBackgroundResource(resId);
	}

	public void setConfirmTextColor(int resId) {
		mDialogPromptOK.setTextColor(resId);
	}

	public void setCancelBackground(int resId) {
		mDialgoPromptLater.setBackgroundResource(resId);
	}

	public void setCancelTextColor(int resId) {
		mDialgoPromptLater.setTextColor(resId);
	}

	@Override
	public void show() {
		try {
			super.show();
		} catch (Exception e) {
			Log.w("", "show dialog error!", e);
		}
	}
}
 