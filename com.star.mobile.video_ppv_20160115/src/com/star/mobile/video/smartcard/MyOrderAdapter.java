package com.star.mobile.video.smartcard;

import java.util.List;

import com.star.cms.model.BindCardCommand;
import com.star.cms.model.Command;
import com.star.cms.model.PpvCMD;
import com.star.cms.model.code.ChangePackageCode;
import com.star.cms.model.vo.SMSHistory;
import com.star.mobile.video.R;
import com.star.mobile.video.util.DateFormat;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 订单列表Adapter
 * 
 * @author Lee
 * @date 2016.01.05
 */
public class MyOrderAdapter extends BaseAdapter {
	private static final int TYPE_COUNT = 3;
	private static final int TYPE_RECEPT = 0;
	private static final int TYPE_FAILD = 1;
	private static final int TYPE_DONE = 2;
	private int mSelectPosition = -1;
	private Context mContext;
	private List<SMSHistory> myOrderList;
	private LayoutInflater mLayoutInflater;

	public MyOrderAdapter(Context context, List<SMSHistory> datas) {
		this.mContext = context;
		this.myOrderList = datas;
		mLayoutInflater = LayoutInflater.from(context);
	}

	public void setSelectPosition(int position) {
		this.mSelectPosition = position;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return myOrderList.size();
	}

	@Override
	public Object getItem(int position) {
		return myOrderList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		SMSHistory orderBean = myOrderList.get(position);
		if (orderBean != null) {
			int acceptStatus = orderBean.getAcceptStatus();
			int status = orderBean.getProgress();
			if (status == Command.Progress_inited) {
				return TYPE_RECEPT;
			} else if (status == Command.Progress_error ||status == Command.Progress_success) {
				if (acceptStatus == BindCardCommand.BIND_CARED_SUCCESS_RESULT || acceptStatus == ChangePackageCode.CHANGE_SUCCESS) {
					return TYPE_DONE;
				}else {
					return TYPE_FAILD;
				}
			} else if (status == Command.Progress_running) {
				return TYPE_RECEPT;
			}
		}
		return super.getItemViewType(position);
	}

	@Override
	public int getViewTypeCount() {
		return TYPE_COUNT;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ReceptViewHolder receptViewHolder = null;
		FailedtViewHolder failedViewHolder = null;
		DoneViewHolder doneViewHolder = null;
		SMSHistory myOrderBean = myOrderList.get(position);

		String orderStatus = "";
		int acceptStatus = myOrderBean.getAcceptStatus();
		int status = myOrderBean.getProgress();
		if (status == Command.Progress_inited) {
			orderStatus = mContext.getString(R.string.Recept);
		} else if (status == Command.Progress_error || status == Command.Progress_success) {
			if (acceptStatus == BindCardCommand.BIND_CARED_SUCCESS_RESULT || acceptStatus == ChangePackageCode.CHANGE_SUCCESS) {
				orderStatus = mContext.getString(R.string.Done);
			}else {
				orderStatus = mContext.getString(R.string.failed);
			}
		} else if (status == Command.Progress_running) {
			orderStatus = mContext.getString(R.string.Recept);
		}
		SpannableStringBuilder statusStyle = new SpannableStringBuilder(mContext.getString(R.string.Status) + orderStatus);
		statusStyle.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.gray_bg)), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		String cardNumber = myOrderBean.getSmartCardNo();
		SpannableStringBuilder cardNumberStyle = new SpannableStringBuilder(mContext.getString(R.string.card_number) + cardNumber);
		cardNumberStyle.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.gray_bg)), 0, 12, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		int type = getItemViewType(position);
		switch (type) {
		case TYPE_RECEPT:
			if (convertView == null) {
				receptViewHolder = new ReceptViewHolder();
				convertView = mLayoutInflater.inflate(R.layout.view_myorder_recept_item, parent, false);
				receptViewHolder.mReceptRelativeLayout = (RelativeLayout) convertView
						.findViewById(R.id.recept_relativelayout);
				receptViewHolder.mReceptDateTextview = (TextView) convertView.findViewById(R.id.recept_date_textview);
				receptViewHolder.mReceptStausTextview = (TextView) convertView
						.findViewById(R.id.recept_status_textview);
				receptViewHolder.mReceptTypeTextview = (TextView) convertView.findViewById(R.id.recept_type_textview);
				receptViewHolder.mReceptCardNumberTextview = (TextView) convertView
						.findViewById(R.id.recept_card_number_textview);
				convertView.setTag(receptViewHolder);
			} else {
				receptViewHolder = (ReceptViewHolder) convertView.getTag();
			}

			if (myOrderBean != null) {
				if (myOrderBean.getProgress() == Command.Progress_inited) {
					receptViewHolder.mReceptDateTextview
							.setText(DateFormat.format(myOrderBean.getCreateDate(), "yyyy-MM-dd HH:mm"));
				} else {
//					receptViewHolder.mReceptDateTextview
//							.setText(DateFormat.format(myOrderBean.getUpdateDate(), "yyyy-MM-dd HH:mm"));
				}
				receptViewHolder.mReceptStausTextview.setText(statusStyle);
				switch (myOrderBean.getType()) {
				case Command.BIND_CARD:
					receptViewHolder.mReceptTypeTextview.setText(mContext.getString(R.string.bingding_card));
					break;
				case Command.CHANGE_PACKAGE:
					receptViewHolder.mReceptTypeTextview.setText(mContext.getString(R.string.change_bouquet));
					break;
				case Command.RECARGE:
					receptViewHolder.mReceptTypeTextview.setText(mContext.getString(R.string.recharge));
					break;
				}
				receptViewHolder.mReceptCardNumberTextview.setText(cardNumberStyle);
			}

			if (mSelectPosition == position) {
				receptViewHolder.mReceptRelativeLayout
						.setBackgroundColor(mContext.getResources().getColor(R.color.gray_blue));
			} else {
				receptViewHolder.mReceptRelativeLayout
						.setBackgroundColor(mContext.getResources().getColor(R.color.white));
			}

			break;
		case TYPE_FAILD:
			if (convertView == null) {
				failedViewHolder = new FailedtViewHolder();
				convertView = mLayoutInflater.inflate(R.layout.view_myorder_failed_item, parent, false);
				failedViewHolder.mFailedRelativeLayout = (RelativeLayout) convertView
						.findViewById(R.id.failed_relativelayout);
				failedViewHolder.mFailedCardNumberTextview = (TextView) convertView
						.findViewById(R.id.failed_card_number_textview);
				failedViewHolder.mFailedDateTextview = (TextView) convertView.findViewById(R.id.failed_date_textview);
				failedViewHolder.mFailedStausTextview = (TextView) convertView
						.findViewById(R.id.failed_status_textview);
				failedViewHolder.mFailedTypeTextview = (TextView) convertView.findViewById(R.id.failed_type_textview);
				convertView.setTag(failedViewHolder);
			} else {
				failedViewHolder = (FailedtViewHolder) convertView.getTag();
			}
			if (myOrderBean != null) {
				if (myOrderBean.getProgress() == Command.Progress_inited) {
					failedViewHolder.mFailedDateTextview
							.setText(DateFormat.format(myOrderBean.getCreateDate(), "yyyy-MM-dd HH:mm"));
				} else {
					failedViewHolder.mFailedDateTextview
							.setText(DateFormat.format(myOrderBean.getUpdateDate(), "yyyy-MM-dd HH:mm"));
				}
				failedViewHolder.mFailedStausTextview.setText(statusStyle);
				switch (myOrderBean.getType()) {
				case Command.BIND_CARD:
					failedViewHolder.mFailedTypeTextview.setText(mContext.getString(R.string.bingding_card));
					break;
				case Command.CHANGE_PACKAGE:
					failedViewHolder.mFailedTypeTextview.setText(mContext.getString(R.string.change_bouquet));
					break;
				case Command.RECARGE:
					failedViewHolder.mFailedTypeTextview.setText(mContext.getString(R.string.recharge));
					break;
				}
				failedViewHolder.mFailedCardNumberTextview.setText(cardNumberStyle);
			}
			if (mSelectPosition == position) {
				failedViewHolder.mFailedRelativeLayout
						.setBackgroundColor(mContext.getResources().getColor(R.color.gray_red));
			} else {
				failedViewHolder.mFailedRelativeLayout
						.setBackgroundColor(mContext.getResources().getColor(R.color.white));
			}
			break;
		case TYPE_DONE:
			if (convertView == null) {
				doneViewHolder = new DoneViewHolder();
				convertView = mLayoutInflater.inflate(R.layout.view_myorder_done_item, parent, false);
				doneViewHolder.mDoneRelativeLayout = (RelativeLayout) convertView
						.findViewById(R.id.done_relativelayout);
				doneViewHolder.mDoneCardNumberTextview = (TextView) convertView
						.findViewById(R.id.done_card_number_textview);
				doneViewHolder.mDoneDateTextview = (TextView) convertView.findViewById(R.id.done_date_textview);
				doneViewHolder.mDoneStausTextview = (TextView) convertView.findViewById(R.id.done_status_textview);
				doneViewHolder.mDoneTypeTextview = (TextView) convertView.findViewById(R.id.done_type_textview);
				convertView.setTag(doneViewHolder);
			} else {
				doneViewHolder = (DoneViewHolder) convertView.getTag();
			}
			if (myOrderBean != null) {
				if (myOrderBean.getProgress() == Command.Progress_inited) {
					doneViewHolder.mDoneDateTextview
							.setText(DateFormat.format(myOrderBean.getCreateDate(), "yyyy-MM-dd HH:mm"));
				} else {
					doneViewHolder.mDoneDateTextview
							.setText(DateFormat.format(myOrderBean.getUpdateDate(), "yyyy-MM-dd HH:mm"));
				}

				doneViewHolder.mDoneStausTextview.setText(mContext.getString(R.string.Status) + orderStatus);
				switch (myOrderBean.getType()) {
				case Command.BIND_CARD:
					doneViewHolder.mDoneTypeTextview.setText(mContext.getString(R.string.bingding_card));
					break;
				case Command.CHANGE_PACKAGE:
					doneViewHolder.mDoneTypeTextview.setText(mContext.getString(R.string.change_bouquet));
					break;
				case Command.RECARGE:
					doneViewHolder.mDoneTypeTextview.setText(mContext.getString(R.string.recharge));
					break;
				}
				doneViewHolder.mDoneCardNumberTextview.setText(mContext.getString(R.string.card_number) + myOrderBean.getSmartCardNo());
			}
			if (mSelectPosition == position) {
				doneViewHolder.mDoneRelativeLayout
						.setBackgroundColor(mContext.getResources().getColor(R.color.onair_btn_focus));
			} else {
				doneViewHolder.mDoneRelativeLayout.setBackgroundColor(mContext.getResources().getColor(R.color.white));
			}
			break;

		default:
			break;
		}

		return convertView;
	}

	class ReceptViewHolder {
		RelativeLayout mReceptRelativeLayout;
		TextView mReceptDateTextview;
		TextView mReceptTypeTextview;
		TextView mReceptCardNumberTextview;
		TextView mReceptStausTextview;
	}

	class FailedtViewHolder {
		RelativeLayout mFailedRelativeLayout;
		TextView mFailedDateTextview;
		TextView mFailedTypeTextview;
		TextView mFailedCardNumberTextview;
		TextView mFailedStausTextview;
	}

	class DoneViewHolder {
		RelativeLayout mDoneRelativeLayout;
		TextView mDoneDateTextview;
		TextView mDoneTypeTextview;
		TextView mDoneCardNumberTextview;
		TextView mDoneStausTextview;
	}
}
