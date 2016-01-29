package com.star.mobile.video.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.star.cms.model.Chart;
import com.star.cms.model.ChatRoom;
import com.star.mobile.video.R;
import com.star.mobile.video.activity.ChatActivity;
import com.star.mobile.video.model.LinkPkg;

public class ForwardDialog extends Dialog {

	private TextView okBtn;
	private EditText editText;

	public ForwardDialog(Context context) {
		this(context, 0);
	}

	public ForwardDialog(final Context context, int theme) {
		super(context, R.style.TaskInfoDialog);
		setOnKeyListener(new OnKeyListener() {
	        @Override
	        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
	            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
	            	((ChatActivity)context).onBackPressed();
	            }
	            return false;
	        }
	    });
	}
	
	public void showLinkDialog(final Context context, LinkPkg pkg, int type) {
		if(type!=Chart.TYPE_LINK)
			return;
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_forward_link, null);
		TextView title = (TextView) view.findViewById(R.id.tv_title);
		TextView content = (TextView) view.findViewById(R.id.tv_content);
		editText = (EditText) view.findViewById(R.id.et_leave_msg);
		com.star.ui.ImageView icon = (com.star.ui.ImageView) view.findViewById(R.id.iv_link_icon);
		TextView cancleBtn = (TextView) view.findViewById(R.id.cancel_btn);
		okBtn = (TextView) view.findViewById(R.id.ok_btn);
		
		if(pkg.getTitle()==null)
			title.setVisibility(View.GONE);
		else
			title.setText(pkg.getTitle());
		content.setText(pkg.getDescription()==null?pkg.getUrl():pkg.getDescription());
		try {
			icon.setUrl(pkg.getImgurl());
		} catch (Exception e) {
		}
		cancleBtn.setOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
				((ChatActivity)context).onBackPressed();
			}
		});
		setContentView(view);
		show();
	}
	
	public String getForwardEditText(){
		if(editText==null)
			return null;
		return editText.getText().toString().trim();
	}

	public void showTextDialog(final Context context, ChatRoom room, int type) {
		if(type!=Chart.TYPE_TEXT)
			return;
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_forward_text, null);
		TextView title = (TextView) view.findViewById(R.id.tv_title);
		TextView content = (TextView) view.findViewById(R.id.tv_content);
		com.star.ui.ImageView icon = (com.star.ui.ImageView) view.findViewById(R.id.iv_chatroom_icon);
		TextView cancleBtn = (TextView) view.findViewById(R.id.cancel_btn);
		okBtn = (TextView) view.findViewById(R.id.ok_btn);
		
		title.setText(R.string.forward_text);
		content.setText(room.getName());
		try {
			icon.setUrl(room.getLogo());
		} catch (Exception e) {
		}
		cancleBtn.setOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
				((ChatActivity)context).onBackPressed();
			}
		});
		setContentView(view);
		show();
	}
	
	public void setConfirmClickListener(android.view.View.OnClickListener listener){
		if(okBtn!=null)
			okBtn.setOnClickListener(listener);
	}

	@Override
	public void show() {
		try{
			super.show();
		}catch (Exception e) {
			Log.w("", "show dialog error!", e);
		}
	}
}
