package com.star.mobile.video.me.notificaction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.star.mobile.video.R;

/**
 * @notify Lee Huicheng 2015/11/6 修改getview方法中的优化
 */
public class ReminderSettingAdapter extends BaseAdapter {

	private ReminderSettingItemData datas;
	private Context context;
	private int select;

	public ReminderSettingAdapter(Context context, ReminderSettingItemData datas, int select) {
		this.datas = datas;
		this.context = context;
		this.select = select;
	}

	public void updataSelTextColor(int select) {
		this.select = select;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return datas.getItemNames().length;
	}

	@Override
	public Object getItem(int position) {

		return datas.getItemNames()[position];
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();
			view = LayoutInflater.from(context).inflate(R.layout.reminder_item_view, null);
			holder.tvItemName = (TextView) view.findViewById(R.id.tv_item_name);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		if (datas != null) {
			holder.tvItemName.setText(datas.getItemNames()[position]);
			if (select == position) {
				holder.tvItemName.setTextColor(context.getResources().getColor(R.color.textcolor_orange));
			} else {
				holder.tvItemName.setTextColor(context.getResources().getColor(R.color.gray_bg));
			}
		}
		return view;
	}

	class ViewHolder {
		TextView tvItemName;
	}
}
