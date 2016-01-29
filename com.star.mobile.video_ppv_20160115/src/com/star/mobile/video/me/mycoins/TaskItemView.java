package com.star.mobile.video.me.mycoins;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.star.cms.model.Task;
import com.star.cms.model.vo.TaskVO;
import com.star.mobile.video.R;
import com.star.mobile.video.dialog.DoTaskClickListener;
import com.star.mobile.video.model.TaskCode;
import com.star.mobile.video.shared.SharedPreferencesUtil;

public class TaskItemView extends RelativeLayout{
	
	private ImageView taskLogo; 
	private TextView taskName;
	private TextView tvTaskDescription;//任务描述
	private TextView tvTaskCoin;//任务价格
	private ImageView ivCoinIcon;
	private RelativeLayout llTaskItem;
	private TextView tvMultiple;
	private Context context;
	private TextView tvRecargeMultiple;//充值积分翻倍描述
	
	public TaskItemView(Context context) {
		this(context,null);
	}
	
	public TaskItemView(Context context,AttributeSet attrs) {
		super(context,attrs);
		this.context = context;
		LayoutInflater.from(context).inflate(R.layout.view_tasks_item, this);
		llTaskItem = (RelativeLayout) findViewById(R.id.ll_view_task_item);
		taskLogo = (ImageView) findViewById(R.id.task_logo);
		taskName = (TextView) findViewById(R.id.tv_task_name);
		tvTaskDescription = (TextView) findViewById(R.id.tv_tasks_description);
		tvTaskCoin = (TextView) findViewById(R.id.tv_task_coin);
		ivCoinIcon = (ImageView) findViewById(R.id.iv_coin_icon);
		tvMultiple = (TextView) findViewById(R.id.tv_multiple);
		tvRecargeMultiple = (TextView) findViewById(R.id.tv_rearge_multiple);
	}
	
	public void setTask(TaskVO task){
		findViewById(R.id.iv_finished).setVisibility(View.GONE);
		taskName.setText(task.getName());
		tvTaskDescription.setText(task.getDescription().replace("[level]", task.getLevel()+""));
		if(task.getCode().equals(Task.TaskCode.Recharge)) {
			tvRecargeMultiple.setVisibility(View.VISIBLE);
			if(task.getCoins() > 0) {
				tvTaskCoin.setVisibility(View.GONE);
				tvMultiple.setText(task.getCoins()+"");
			} else {
				tvTaskCoin.setVisibility(View.VISIBLE);
				if(task.getTimes() == null || task.getTimes() <= 1) {
					tvTaskCoin.getPaint().setFlags(0);
					tvTaskCoin.setText("1"+SharedPreferencesUtil.getCurrencSymbol(context)+"=");
					tvRecargeMultiple.setText("1coin");
					tvMultiple.setVisibility(View.INVISIBLE);
				} else {
					tvTaskCoin.setText("1"+SharedPreferencesUtil.getCurrencSymbol(context)+"=");
					tvRecargeMultiple.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
					tvRecargeMultiple.setText("1coin");
					tvMultiple.setVisibility(View.VISIBLE);
					tvMultiple.setText(task.getTimes().intValue()+"coins");
				}
			}
			
		} else {
			tvRecargeMultiple.setVisibility(View.GONE);
			tvTaskCoin.setVisibility(View.VISIBLE);
			if(task.getTimes() == null || task.getTimes() <= 1) {
				tvTaskCoin.setText("+"+task.getPriceToday()+" coins");
				tvMultiple.setVisibility(View.INVISIBLE);
			} else {
				tvTaskCoin.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
				tvMultiple.setVisibility(View.VISIBLE);
				tvMultiple.setText(task.getTimes().intValue() * task.getPriceToday()+"");
				tvTaskCoin.setText(task.getPriceToday()+"");
			}
		}
		setTag(task);
		if(task.getTimesToday() < task.getTimesLimitToday() && task.getTimesLimitToday() > 0 && task.getCoins() <= 0) {
			setOnClickListener(new DoTaskClickListener((Activity)context));
			//未做的任务
			setTaskLogo(task.getCode(), true);
			setTaskNameTextColor(getContext().getResources().getColor(R.color.taks_blue));
			setTaskCoinTextColor(getContext().getResources().getColor(R.color.orange_color));
		}else{
			setOnClickListener(null);
			setTaskNameTextColor(getContext().getResources().getColor(R.color.dicobery_content_txt));
			setTaskCoinTextColor(getContext().getResources().getColor(R.color.dicobery_content_txt));
			findViewById(R.id.iv_finished).setVisibility(View.VISIBLE);
			setTaskLogo(task.getCode(), false);
		}
	}
	
	private void setTaskLogo(String taskCode, boolean cando){
		if(taskCode.equals(TaskCode.Register)) {
			//注册
			if(cando)
				setTaskLogo(R.drawable.ic_account_blue_24dp);
			else
				setTaskLogo(R.drawable.ic_account_grey_24dp);
		} else if(taskCode.equals(TaskCode.Book_program)) {
			//channel guide
			if(cando)
				setTaskLogo(R.drawable.ic_alarm_add_blue_24dp);
			else
				setTaskLogo(R.drawable.ic_alarm_add_grey_24dp);
		} else if(taskCode.equals(TaskCode.Go_four_report)) {
			//四格体验
			if(cando)
				setTaskLogo(R.drawable.ic_drafts_blue_24dp);
			else
				setTaskLogo(R.drawable.ic_drafts_grey_24dp);
		} else if(taskCode.equals(TaskCode.Bind_smartcard)) {
			//绑定智能卡
			if(cando)
				setTaskLogo(R.drawable.ic_add_circle_blue_24dp);
			else
				setTaskLogo(R.drawable.ic_add_circle_grey_24dp);
		} else if(taskCode.equals(TaskCode.Sign_in)){
			if(cando)
				setTaskLogo(R.drawable.icon_daily_blue_24dp);
			else
				setTaskLogo(R.drawable.icon_daily_grey_24dp);
		} else if(taskCode.equals(Task.TaskCode.Recharge)) {
			if(cando)
				setTaskLogo(R.drawable.ic_shopping_cart_blue_24dp);
			else
				setTaskLogo(R.drawable.ic_shopping_cart_grey_24dp);
		} else if(taskCode.equals(Task.TaskCode.Link_ThirdAccount)) {
			if(cando)
				setTaskLogo(R.drawable.ic_connect_blue_24dp);
			else
				setTaskLogo(R.drawable.ic_connect_grey_24dp);
		} else if(taskCode.equals(Task.TaskCode.RechargeWithPaga)) {
			if(cando)
				setTaskLogo(R.drawable.ic_paga_blue_24dp);
			else
				setTaskLogo(R.drawable.ic_paga_grey_24dp);
		}
	}
	
	public void setTaskLogo(int resId) {
		taskLogo.setImageResource(resId);
	}

	public void setTaskName(String text) {
		taskName.setText(text);
	}
	
	public void setTaskNameTextColor(int color) {
		taskName.setTextColor(color);
	}

	public void setTaskCoin(int coins) {
		 tvTaskCoin.setText(coins+"");
	}
	
	public String getTaskCoin() {
		return tvTaskCoin.getText().toString();
	}
	
	public void setTaskCoinTextColor(int color) {
		tvTaskCoin.setTextColor(color);
	}
	
	public void setTaskCoinTextFlag(int flag) {
		tvTaskCoin.getPaint().setFlags(flag);
	}
	
	public void setTaskDescriptionTextColor(int color) {
		tvTaskDescription.setTextColor(color);
	}
	
	/**
	 * 设置任务描述
	 * @param text
	 */
	public void setTaskDescription(String text) {
		tvTaskDescription.setText(text);
	}
	
	public void setBackgroundCoinIcon(int resid) {
		ivCoinIcon.setBackgroundResource(resid);
	}
	
	public void setTaskItemBackground(int resid) {
		llTaskItem.setBackgroundResource(resid);
	}
	
	public void setMultiple(int multiple) {
		tvMultiple.setText(multiple+"");
	}
	
	public void setMultipleVisibility(int visibility) {
		tvMultiple.setVisibility(visibility);
	}
	
	public void setMultipleTextColor(int color) {
		tvMultiple.setTextColor(color);
	}
}
