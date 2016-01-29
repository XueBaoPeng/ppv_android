package com.star.mobile.video.channel;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.star.mobile.video.R;
import com.star.mobile.video.view.FaceContainer;

public class ChatBottomInputView extends RelativeLayout {

	private EditText mEtChat;
	private Context mContext;
	private ImageView sendChat;
	private ImageView sendFace;
	private FaceContainer faceContainer;
	private ImageView mImageViewSend;

	public ChatBottomInputView(Context context) {
		super(context);
		mContext = context;
		initView();
	}
	public ChatBottomInputView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initView();
	}

	private void initView() {
		LayoutInflater.from(mContext).inflate(R.layout.chat_room_bottom_input, this);
		setmEtChat((EditText) findViewById(R.id.video_live_chat_edittext));
		setSendChat((ImageView) findViewById(R.id.video_live_content_send_iv));
		setSendFace((ImageView) findViewById(R.id.video_live_chat_face_iv));
		setFaceContainer((FaceContainer) findViewById(R.id.faceContainer));
		getFaceContainer().setEditText(getmEtChat());
		mImageViewSend = (ImageView)findViewById(R.id.iv_image_send);
		setImageViewSend(mImageViewSend);
	}
	public void hideSoftKeyboard(Context context) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mEtChat.getWindowToken(), 0); // 强制隐藏键盘
	}
	public ImageView getSendChat() {
		return sendChat;
	}
	public void setSendChat(ImageView sendChat) {
		this.sendChat = sendChat;
	}
	public ImageView getSendFace() {
		return sendFace;
	}
	public void setSendFace(ImageView sendFace) {
		this.sendFace = sendFace;
	}
	public FaceContainer getFaceContainer() {
		return faceContainer;
	}
	public void setFaceContainer(FaceContainer faceContainer) {
		this.faceContainer = faceContainer;
	}
	public EditText getmEtChat() {
		return mEtChat;
	}
	public void setmEtChat(EditText mEtChat) {
		this.mEtChat = mEtChat;
	}
	public void setSendFaceGone(){
		if (sendFace != null) {
			sendFace.setVisibility(View.GONE);
		}
	}
	public ImageView getImageViewSend() {
		mImageViewSend.setVisibility(View.VISIBLE);
		return mImageViewSend;
	}
	public void setImageViewSend(ImageView mImageViewSend) {
		this.mImageViewSend = mImageViewSend;
	}
	
}
