package com.star.mobile.video.model;

import java.util.ArrayList;
import java.util.List;

import com.star.cms.model.Task;


public enum FunctionType {
	SmartCard(0),
	RechargeWithPaga(1),
	InviteFriends(2),
	FastReport(3),
	SimpleVersion(4),
	RechargeCard(5),
	Invitation(6),
	RegisterWithPhone(7),
	PPV(8);
	
	private int functionType;
	private FunctionType(int type) {
		this.functionType = type;
		switch (type) {
		case 0:
			taskCodes.add(Task.TaskCode.Bind_Smartcard);
			taskCodes.add(Task.TaskCode.Recharge);
			taskCodes.add(Task.TaskCode.RechargeWithPaga);
			break;
		case 1:
			taskCodes.add(Task.TaskCode.RechargeWithPaga);
			break;
		case 3:
			taskCodes.add(Task.TaskCode.Fast_Report);
			break;
		case 5:
			taskCodes.add(Task.TaskCode.Recharge);
			taskCodes.add(Task.TaskCode.RechargeWithPaga);
			break;
		}
	}

	public static FunctionType getFunctionType(int value) {
		FunctionType[] values = FunctionType.values();
		for (FunctionType type : values) {
			if (type.functionType == value) {
				return type;
			}
		}
		return null;
	}

	public int getNum() {
		return functionType;
	}
	
	private List<String> taskCodes = new ArrayList<String>();
	public List<String> getTaskCodes(){
		return taskCodes;
	}
}
