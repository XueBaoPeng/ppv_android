package com.star.mobile.video.view;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.star.cms.model.Bill;
import com.star.cms.model.Payment;
import com.star.mobile.video.R;
import com.star.mobile.video.account.AccountService;
import com.star.mobile.video.adapter.AccountBillAdapter;
import com.star.mobile.video.util.DateFormat;
import com.star.mobile.video.util.DateUtil;
import com.star.util.loader.OnListResultListener;

public class AccountBillListView extends LinearLayout implements View.OnClickListener {
	
	private boolean isPayment;
	private boolean isBill;
	private Context context;
	private String cardNo;
	private ListView lvContent;
	private RelativeLayout rlDate;
	private TextView tvSelectDate;
	private ImageView ivPull;
	private RelativeLayout rlNoDataView;
	private boolean selectDateWindowStatus = true;
	private List<String> dates;
	private View pbLoading;
	private AccountService accountService;

	public AccountBillListView(Context context,boolean isPayment,boolean isBill,String cardNo) {
		this(context, null);
		this.cardNo = cardNo;
		this.isBill = isBill;
		this.isPayment = isPayment;
		addItem();
	}
	
	public AccountBillListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		LayoutInflater.from(context).inflate(R.layout.view_account_bill_list, this);
		lvContent = (ListView) findViewById(R.id.lv_content);
		rlDate = (RelativeLayout) findViewById(R.id.rl_date);
		tvSelectDate = (TextView) findViewById(R.id.tv_sel_date);
		ivPull = (ImageView) findViewById(R.id.iv_pull);
		rlNoDataView = (RelativeLayout) findViewById(R.id.rl_no_data);
		pbLoading = (View) findViewById(R.id.pb_loading);
		rlDate.setOnClickListener(this);
		accountService = new AccountService(context);
		dates = DateUtil.getMonths(6, "yyyy-MM");
		tvSelectDate.setText(context.getString(R.string.this_month));
		tvSelectDate.setTag(dates.get(0));
	}
	
	private void addItem() {
		if(isPayment) {
			getPaymentList(cardNo,dates.get(0),false);
		} else if(isBill) {
			getBillList(cardNo,dates.get(0),false);
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_date:
			showRechargeConfirmSelectSmartCardPopuWindow();
			break;
		default:
			break;
		}
	}
	
	private void showRechargeConfirmSelectSmartCardPopuWindow() {
		rlDate.setVisibility(View.INVISIBLE);
		if(selectDateWindowStatus) {
			selectDateWindowStatus = false;
			ivPull.setBackgroundResource(R.drawable.pull_triangle_up_onecard);
		} else {
			ivPull.setBackgroundResource(R.drawable.pull_triangle);
		}
		View popView =  LayoutInflater.from(context).inflate(R.layout.view_account_bill_select, null);
		View footView = LayoutInflater.from(context).inflate(R.layout.view_account_select_footer, null);
		ListView lv_answer_list = (ListView) popView.findViewById(R.id.lv_answer_list);
		SelectDateAdapter scAdapter = new SelectDateAdapter(dates,tvSelectDate.getText().toString());
		final PopupWindow rechargeConfirmWindow = new PopupWindow(popView, rlDate.getWidth(), LayoutParams.WRAP_CONTENT, true);  
		rechargeConfirmWindow.setTouchable(true);
		rechargeConfirmWindow.setOutsideTouchable(true);
		rechargeConfirmWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
		lv_answer_list.addFooterView(footView);
		lv_answer_list.setAdapter(scAdapter);
		lv_answer_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
					long arg3) {
				if(arg2 >= dates.size()) {
					return;
				}
				TextView tv = (TextView) view.findViewById(R.id.tv_date);
				String selDate = (String)tv.getTag();
				if(selDate == null || "".equals(selDate)) {
					return;
				}
				if(arg2 == 0) {
					tvSelectDate.setText(context.getString(R.string.this_month));
				} else {
					tvSelectDate.setText(selDate);
				}
				
				if(isBill) {
					if(arg2 == 0) {
						getBillList(cardNo, selDate,false);
					} else {
						getBillList(cardNo, selDate,true);
					}
					
				} else if(isPayment) {
					if(arg2 == 0) {
						getPaymentList(cardNo, selDate,false);
					} else {
						getPaymentList(cardNo, selDate,true);
					}
				}
				rechargeConfirmWindow.dismiss();
			}
		});
		
		rechargeConfirmWindow.showAsDropDown(findViewById(R.id.rl_popuwindow_parent), 0, 0);
		rechargeConfirmWindow.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss() {
				ivPull.setBackgroundResource(R.drawable.pull_triangle);
				selectDateWindowStatus = true;
				rlDate.setVisibility(View.VISIBLE);
			}
		});
	}
	
	private void getBillList(final String cardNo,final String date,final boolean isFormLoal) {
		String d = date.replaceAll("-", "");
		final Date dateTime = DateFormat.stringToDate(d+"06", "yyyyMMdd",null);
		accountService.getBills(cardNo, dateTime.getTime(), new OnListResultListener<Bill>() {

			@Override
			public boolean onIntercept() {
				rlNoDataView.setVisibility(View.GONE);
				pbLoading.setVisibility(View.VISIBLE);
				return false;
			}

			@Override
			public void onFailure(int errorCode, String msg) {
				pbLoading.setVisibility(View.GONE);
				setIsNoData(false);
			}

			@Override
			public void onSuccess(List<Bill> datas) {
				pbLoading.setVisibility(View.GONE);
				if(datas != null) {
					if(datas.size() > 0) {
						AccountBillAdapter adapter = new AccountBillAdapter(context, datas, null, AccountBillListView.this.cardNo);
						adapter.notifyDataSetChanged();
						lvContent.setAdapter(adapter);
						setIsNoData(true);
					}
				}
			}
		});
	}
	
	private void getPaymentList(final String cardNo,String date,final boolean isFormLoal) {
		String time = date.replaceAll("-", "");
		final Date d = DateFormat.stringToDate(time+"05", "yyyyMMdd",null);
		accountService.getPayments(cardNo, d.getTime(), new OnListResultListener<Payment>() {

			@Override
			public boolean onIntercept() {
				rlNoDataView.setVisibility(View.GONE);
				pbLoading.setVisibility(View.VISIBLE);
				return false;
			}

			@Override
			public void onFailure(int errorCode, String msg) {
				pbLoading.setVisibility(View.GONE);
				setIsNoData(false);
			}

			@Override
			public void onSuccess(List<Payment> datas) {
				pbLoading.setVisibility(View.GONE);
				if(datas.size() > 0) {
					AccountBillAdapter adapter = new AccountBillAdapter(context, null, datas, AccountBillListView.this.cardNo);
					adapter.notifyDataSetChanged();
					lvContent.setAdapter(adapter);
					setIsNoData(true);
				} else {
					setIsNoData(false);
				}
			}
		});
	}
	
	private void setIsNoData(boolean isData) {
		if(isData) {
			lvContent.setVisibility(View.VISIBLE);
			rlNoDataView.setVisibility(View.GONE);
		} else {
			lvContent.setVisibility(View.GONE);
			rlNoDataView.setVisibility(View.VISIBLE);
		}
	}
	
	class SelectDateAdapter extends BaseAdapter {

		private List<String> dates;
		private String selDate;
		
		public SelectDateAdapter(List<String> dates,String selDate) {
			this.dates = dates;
			this.selDate = selDate;
		}
		public void notifyDataSetChanged(List<String> dates,String selDate) {
			this.dates = dates;
			this.selDate = selDate;
			notifyDataSetChanged();
		}
		@Override
		public int getCount() {
			
			return dates.size();
		}

		@Override
		public Object getItem(int position) {
			
			return dates.get(position);
		}

		@Override
		public long getItemId(int position) {
			
			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if(view == null) {
				viewHolder = new ViewHolder();
				view = LayoutInflater.from(context).inflate(R.layout.view_select_date_item, null);
				viewHolder.tvDate = (TextView) view.findViewById(R.id.tv_date);
				viewHolder.line = view.findViewById(R.id.v_line);
				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder)view.getTag();
			}
			String date = dates.get(position);
			if(position == 0) {
				date = context.getString(R.string.this_month);
			}
			if(date.equals(selDate)) {
				viewHolder.tvDate.setTextColor(context.getResources().getColor(R.color.taks_blue));
			} else {
				viewHolder.tvDate.setTextColor(context.getResources().getColor(R.color.choose_text));
			}
			if(position == 0) {
				viewHolder.tvDate.setText(context.getString(R.string.this_month));
				viewHolder.tvDate.setTag(date);
			} else {
				viewHolder.tvDate.setText(date);
				viewHolder.tvDate.setTag(date);
			}
			return view;
		}
		class ViewHolder {
			public TextView tvDate;
			public View line;
		}
	}
}
